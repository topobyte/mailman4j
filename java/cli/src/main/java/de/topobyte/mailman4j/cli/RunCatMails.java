// Copyright 2018 Sebastian Kuerten
//
// This file is part of mailman4j.
//
// mailman4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mailman4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mailman4j. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.mailman4j.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.topobyte.mailman4j.GzipUtil;
import de.topobyte.mailman4j.Mail;
import de.topobyte.mailman4j.MailCat;
import de.topobyte.mailman4j.MailComparatorByDate;
import de.topobyte.mailman4j.Mails;
import de.topobyte.mailman4j.MailsParser;
import de.topobyte.mailman4j.RawMail;
import de.topobyte.mailman4j.mirror.Config;
import de.topobyte.mailman4j.mirror.ConfigIO;
import de.topobyte.mailman4j.mirror.MirrorPaths;
import de.topobyte.mailman4j.mirror.TimeSpan;
import de.topobyte.mailman4j.mirror.TimeSpanBefore;
import de.topobyte.melon.paths.PathUtil;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunCatMails
{

	private static final String OPTION_DIR = "dir";
	private static final String OPTION_NO_TEXT = "no-text";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_DIR, true, true, "directory", "a mailman mirror directory");
			OptionHelper.addL(options, OPTION_NO_TEXT, false, false, "don't print mail text");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	private static boolean printText;

	private static Pattern patternFilenames = Pattern
			.compile("([0-9]{4,4})-([A-Za-z]+).txt.gz");
	private static String[] months = new String[] { "January", "February",
			"March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };
	private static Map<String, Integer> nameToMonth = new HashMap<>();
	static {
		for (int i = 0; i < months.length; i++) {
			nameToMonth.put(months[i], i + 1);
		}
	}

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argDir = line.getOptionValue(OPTION_DIR);
		Path pathDir = Paths.get(argDir);

		printText = !line.hasOption(OPTION_NO_TEXT);

		Path fileConfig = pathDir.resolve(MirrorPaths.FILENAME_CONFIG);
		Config config = ConfigIO.read(fileConfig);

		Path dirArchives = pathDir.resolve(MirrorPaths.DIRNAME_ARCHIVES);
		List<Path> files = PathUtil.list(dirArchives);

		List<Mail> mails = new ArrayList<>();
		for (Path file : files) {
			YearMonth fileDate = YearMonth.now();
			String filename = file.getFileName().toString();
			Matcher matcher = patternFilenames.matcher(filename);
			if (matcher.matches()) {
				String valYear = matcher.group(1);
				String valMonth = matcher.group(2);
				int year = Integer.parseInt(valYear);
				int month = nameToMonth.get(valMonth);
				fileDate = YearMonth.of(year, month);
			}

			String charset = config.getCharset();
			for (TimeSpan span : config.getTimeSpans()) {
				if (span instanceof TimeSpanBefore) {
					TimeSpanBefore before = (TimeSpanBefore) span;
					YearMonth date = YearMonth.of(before.getYear(),
							before.getMonth());
					if (fileDate.isBefore(date)) {
						charset = before.getCharset();
					}
				}
			}

			List<String> lines = GzipUtil.lines(file, charset);
			MailsParser parser = new MailsParser(lines);
			parser.parse();
			List<RawMail> raw = parser.getMails();
			mails.addAll(Mails.convert(raw));
		}

		Collections.sort(mails, new MailComparatorByDate());

		MailCat cat = new MailCat();
		cat.setPrintText(printText);

		for (Mail mail : mails) {
			cat.print(mail);
		}
	}

}

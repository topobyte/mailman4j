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
import java.time.LocalDate;
import java.time.Year;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mailman4j.DayPeriod;
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

	final static Logger logger = LoggerFactory.getLogger(RunCatMails.class);

	private static final String OPTION_DIR = "dir";
	private static final String OPTION_NO_TEXT = "no-text";
	private static final String OPTION_YEAR = "year";
	private static final String OPTION_MONTH = "month";
	private static final String OPTION_DAY = "day";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_DIR, true, true, "directory", "a mailman mirror directory");
			OptionHelper.addL(options, OPTION_NO_TEXT, false, false, "don't print mail text");
			OptionHelper.addL(options, OPTION_YEAR, true, false, "filter by year");
			OptionHelper.addL(options, OPTION_MONTH, true, false, "filter by month");
			OptionHelper.addL(options, OPTION_DAY, true, false, "filter by day of month");
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

		DayPeriod period = period(line);

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
				fileDate = yearMonth(matcher);
			}
			if (!period.intersects(fileDate)) {
				continue;
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
			if (period != null && !period.contains(mail.getDate())) {
				continue;
			}
			cat.print(mail);
		}
	}

	private static DayPeriod period(CommandLine line)
	{
		String argYear = line.getOptionValue(OPTION_YEAR);
		String argMonth = line.getOptionValue(OPTION_MONTH);
		String argDay = line.getOptionValue(OPTION_DAY);

		int year = -1;
		int month = -1;
		int day = -1;

		if (argYear != null) {
			year = Integer.parseInt(argYear);
		}
		if (argMonth != null) {
			month = Integer.parseInt(argMonth);
		}
		if (argDay != null) {
			day = Integer.parseInt(argDay);
		}

		DayPeriod period = null;

		if (argYear == null && argMonth == null && argDay == null) {
			period = null;
		} else if (argYear != null && argMonth != null && argDay != null) {
			LocalDate date = LocalDate.of(year, month, day);
			LocalDate nextDay = date.plusDays(1);
			period = new DayPeriod(date, nextDay);
			logger.debug(String.format("year, month and day: %s - %s", date,
					nextDay));
		} else if (argYear != null && argMonth != null) {
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate monthStart = yearMonth.atDay(1);
			LocalDate monthEnd = yearMonth.atEndOfMonth();
			LocalDate firstDayOfNextMonth = monthEnd.plusDays(1);
			period = new DayPeriod(monthStart, firstDayOfNextMonth);
			logger.debug(String.format("year and month: %s - %s", monthStart,
					firstDayOfNextMonth));
		} else if (argYear != null) {
			LocalDate start = Year.of(year).atMonth(1).atDay(1);
			LocalDate end = Year.of(year + 1).atMonth(1).atDay(1);
			period = new DayPeriod(start, end);
			logger.debug(String.format("year only: %s - %s", start, end));
		} else {
			throw new IllegalArgumentException(
					"Invalid combination of date specifiers");
		}

		return period;
	}

	private static YearMonth yearMonth(Matcher matcher)
	{
		String valYear = matcher.group(1);
		String valMonth = matcher.group(2);
		int year = Integer.parseInt(valYear);
		int month = nameToMonth.get(valMonth);
		YearMonth date = YearMonth.of(year, month);
		return date;
	}

}

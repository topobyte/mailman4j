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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.topobyte.mailman4j.GzipUtil;
import de.topobyte.mailman4j.Mail;
import de.topobyte.mailman4j.MailsParser;
import de.topobyte.mailman4j.mirror.MirrorPaths;
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

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argDir = line.getOptionValue(OPTION_DIR);
		Path pathDir = Paths.get(argDir);

		printText = !line.hasOption(OPTION_NO_TEXT);

		Path dirArchives = pathDir.resolve(MirrorPaths.DIRNAME_ARCHIVES);
		List<Path> files = PathUtil.list(dirArchives);

		List<Mail> mails = new ArrayList<>();
		for (Path file : files) {
			List<String> lines = GzipUtil.lines(file);
			MailsParser parser = new MailsParser(lines);
			parser.parse();
			mails.addAll(parser.getMails());
		}

		Collections.sort(mails, new Comparator<Mail>() {

			@Override
			public int compare(Mail o1, Mail o2)
			{
				return Long.compare(o1.getDate(), o2.getDate());
			}

		});

		for (Mail mail : mails) {
			print(mail);
		}
	}

	private static void print(Mail mail)
	{
		System.out.println(String.format("From: %s", mail.getFrom1()));
		System.out.println(String.format("Date: %s", mail.getDate()));
		System.out.println(String.format("Subject: %s", mail.getSubject()));

		if (printText) {
			for (String line : mail.getText()) {
				System.out.println(line);
			}
		}
		System.out.println();
	}

}

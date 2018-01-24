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

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mailman4j.DayPeriod;
import de.topobyte.mailman4j.GzipUtil;
import de.topobyte.mailman4j.Mail;
import de.topobyte.mailman4j.Mails;
import de.topobyte.mailman4j.MailsParser;
import de.topobyte.mailman4j.RawMail;
import de.topobyte.mailman4j.mirror.Config;
import de.topobyte.mailman4j.mirror.TimeSpan;
import de.topobyte.mailman4j.mirror.TimeSpanBefore;

public class Util
{

	final static Logger logger = LoggerFactory.getLogger(Util.class);

	private static String[] months = new String[] { "January", "February",
			"March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };
	private static Map<String, Integer> nameToMonth = new HashMap<>();
	static {
		for (int i = 0; i < months.length; i++) {
			nameToMonth.put(months[i], i + 1);
		}
	}

	private static Pattern patternFilenames = Pattern
			.compile("([0-9]{4,4})-([A-Za-z]+).txt.gz");

	static DayPeriod period(CommandLine line)
	{
		String argYear = line.getOptionValue(CommonOptions.OPTION_YEAR);
		String argMonth = line.getOptionValue(CommonOptions.OPTION_MONTH);
		String argDay = line.getOptionValue(CommonOptions.OPTION_DAY);

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

	static YearMonth yearMonth(Matcher matcher)
	{
		String valYear = matcher.group(1);
		String valMonth = matcher.group(2);
		int year = Integer.parseInt(valYear);
		int month = nameToMonth.get(valMonth);
		YearMonth date = YearMonth.of(year, month);
		return date;
	}

	static List<Mail> mails(List<Path> files, Config config, DayPeriod period)
			throws IOException
	{
		List<Mail> mails = new ArrayList<>();
		for (Path file : files) {
			YearMonth fileDate = YearMonth.now();
			String filename = file.getFileName().toString();
			Matcher matcher = patternFilenames.matcher(filename);
			if (matcher.matches()) {
				fileDate = Util.yearMonth(matcher);
			}
			if (period != null && !period.intersects(fileDate)) {
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
		return mails;
	}

}

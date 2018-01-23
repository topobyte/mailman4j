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

package de.topobyte.mailman4j;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dates
{

	final static Logger logger = LoggerFactory.getLogger(Dates.class);

	private static Pattern patternWithTimezoneAbbreviations = Pattern
			.compile("(.*) \\([A-Z]{3,4}\\)");

	public static long parseRfc1123(String date)
	{
		// Remove extra spaces sometimes used on days
		date = date.replaceAll(" +", " ");

		Matcher matcher = patternWithTimezoneAbbreviations.matcher(date);
		if (matcher.matches()) {
			date = matcher.group(1);
		}

		long timestamp = 0;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
			TemporalAccessor time = formatter.parse(date);
			Instant instant = Instant.from(time);
			timestamp = instant.toEpochMilli();
		} catch (DateTimeParseException e) {
			logger.error(String.format("Unable to parse date: '%s'", date));
		}

		return timestamp;
	}

	public static final DateTimeFormatter FROM_DATE;
	static {
		Map<Long, String> dayLookup = new HashMap<>();
		String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
		for (int i = 0; i < days.length; i++) {
			dayLookup.put((long) (i + 1), days[i]);
		}
		Map<Long, String> monthLookup = new HashMap<>();
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int i = 0; i < months.length; i++) {
			monthLookup.put((long) (i + 1), months[i]);
		}

		FROM_DATE = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.parseLenient().appendText(DAY_OF_WEEK, dayLookup)
				.appendLiteral(' ').appendText(MONTH_OF_YEAR, monthLookup)
				.appendLiteral(' ').optionalStart().appendLiteral(' ')
				.optionalEnd()
				.appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
				.appendLiteral(' ').appendValue(HOUR_OF_DAY, 2)
				.appendLiteral(':').appendValue(MINUTE_OF_HOUR, 2)
				.appendLiteral(':').appendValue(SECOND_OF_MINUTE, 2)
				.appendLiteral(' ').appendValue(YEAR, 4).toFormatter();
	}

	public static long parseFrom(String date)
	{
		long timestamp = 0;
		try {
			TemporalAccessor time = FROM_DATE.parse(date);
			LocalDateTime localDateTime = LocalDateTime.from(time);
			ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime,
					ZoneId.systemDefault());
			Instant instant = Instant.from(zonedDateTime);
			timestamp = instant.toEpochMilli();
		} catch (DateTimeParseException e) {
			logger.error(String.format("Unable to parse date: '%s'", date));
		}

		return timestamp;
	}

}

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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dates
{

	final static Logger logger = LoggerFactory.getLogger(Dates.class);

	public static long parse(String date)
	{
		Pattern patternWithTimezoneAbbreviations = Pattern
				.compile("(.*) \\([A-Z]{3,4}\\)");
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

}

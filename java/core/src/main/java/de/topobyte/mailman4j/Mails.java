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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mails
{

	final static Logger logger = LoggerFactory.getLogger(Mails.class);

	private static Pattern patternAddresses = Pattern
			.compile("(\\S*) at (\\S*)\\s*(.*)");

	public static List<Mail> convert(List<RawMail> rawMails)
	{
		List<Mail> mails = new ArrayList<>();

		for (RawMail raw : rawMails) {
			mails.add(convert(raw));
		}

		return mails;
	}

	public static Mail convert(RawMail raw)
	{
		String address = raw.getFrom1();

		Matcher matcher = patternAddresses.matcher(raw.getFrom1());
		if (!matcher.matches()) {
			logger.warn(String.format("Unmatched: '%s'", raw.getFrom1()));
		} else {
			String account = matcher.group(1);
			String server = matcher.group(2);
			String fromDate = matcher.group(3);
			logger.debug(
					String.format("%s@%s (%s)", account, server, fromDate));
			address = String.format("%s@%s", account, server);
		}

		long timestamp = Dates.parse(raw.getDate());

		return new Mail(address, timestamp, raw.getSubject(), raw.getText());
	}

}

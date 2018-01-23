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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MailCat
{

	private DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

	private boolean printText = true;

	public boolean isPrintText()
	{
		return printText;
	}

	public void setPrintText(boolean printText)
	{
		this.printText = printText;
	}

	public void print(Mail mail)
	{
		Instant instant = Instant.ofEpochMilli(mail.getDate());

		System.out.println(String.format("From: %s <%s>", mail.getName(),
				mail.getAddress()));
		System.out
				.println(String.format("Date: %s", formatter.format(instant)));
		System.out.println(String.format("Subject: %s", mail.getSubject()));

		if (printText) {
			for (String line : mail.getText()) {
				System.out.println(line);
			}
		}
		System.out.println();
	}

}

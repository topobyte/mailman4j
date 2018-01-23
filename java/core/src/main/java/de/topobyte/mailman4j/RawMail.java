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

import java.util.List;

public class RawMail
{

	private String from1;
	private String from2;
	private String date;
	private String subject;
	private List<String> text;

	public RawMail(String from1, String from2, String date, String subject,
			List<String> text)
	{
		this.from1 = from1;
		this.from2 = from2;
		this.date = date;
		this.subject = subject;
		this.text = text;
	}

	public String getFrom1()
	{
		return from1;
	}

	public String getFrom2()
	{
		return from2;
	}

	public String getDate()
	{
		return date;
	}

	public String getSubject()
	{
		return subject;
	}

	public List<String> getText()
	{
		return text;
	}

}

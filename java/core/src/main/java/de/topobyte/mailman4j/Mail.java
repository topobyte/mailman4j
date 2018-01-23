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

public class Mail
{

	private String address;
	private String name;
	private long date;
	private String subject;
	private List<String> text;

	public Mail(String address, String name, long date, String subject,
			List<String> text)
	{
		this.address = address;
		this.name = name;
		this.date = date;
		this.subject = subject;
		this.text = text;
	}

	public String getAddress()
	{
		return address;
	}

	public String getName()
	{
		return name;
	}

	public long getDate()
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

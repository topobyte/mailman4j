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

public class TestParseDates
{

	public static void main(String[] args)
	{
		String[] test = new String[] { "Sat, 19 Jun 2010 09:03:36 +0000",
				"Sat, 19 Jun 2010 09:03:36 +0000 (UTC)",
				"Mon, 28 Jun 2010 13:59:54 +0200 (CEST)",
				"Mon,  9 May 2011 12:41:51 +0200" };

		for (String date : test) {
			long timestamp = Dates.parse(date);
			System.out.println(timestamp);
		}
	}

}

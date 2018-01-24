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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;

public class DayPeriod
{

	private LocalDate start;
	private LocalDate end;

	private long startMillis;
	private long endMillis;

	/**
	 * Create a new period of days.
	 * 
	 * @param start
	 *            the start date, inclusive
	 * @param end
	 *            the end date, exclusive
	 */
	public DayPeriod(LocalDate start, LocalDate end)
	{
		this.start = start;
		this.end = end;
		startMillis = start.atStartOfDay(ZoneOffset.UTC).toInstant()
				.toEpochMilli();
		endMillis = end.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
	}

	public boolean contains(int year, int month, int day)
	{
		LocalDate date = LocalDate.of(year, month, day);
		return contains(date);
	}

	public boolean contains(LocalDate date)
	{
		return !date.isBefore(start) && date.isBefore(end);
	}

	public boolean contains(int year, int month)
	{
		YearMonth yearMonth = YearMonth.of(year, month);
		return contains(yearMonth);
	}

	public boolean contains(YearMonth yearMonth)
	{
		LocalDate monthStart = yearMonth.atDay(1);
		LocalDate monthEnd = yearMonth.atEndOfMonth();
		return !monthStart.isBefore(start) && monthEnd.isBefore(end);
	}

	public boolean intersects(int year, int month)
	{
		YearMonth yearMonth = YearMonth.of(year, month);
		return intersects(yearMonth);
	}

	public boolean intersects(YearMonth yearMonth)
	{
		LocalDate monthStart = yearMonth.atDay(1);
		LocalDate monthEnd = yearMonth.atEndOfMonth();
		return contains(monthStart) || contains(monthEnd)
				|| (monthStart.isBefore(start) && monthEnd.isAfter(end));
	}

	public boolean contains(long timestamp)
	{
		return timestamp >= startMillis && timestamp < endMillis;
	}

}

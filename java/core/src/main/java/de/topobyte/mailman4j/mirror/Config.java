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

package de.topobyte.mailman4j.mirror;

import java.util.ArrayList;
import java.util.List;

public class Config
{

	private String urlListInfo;
	private String urlListArchive;
	private String charset;
	private List<TimeSpan> timeSpans = new ArrayList<>();

	public String getUrlListInfo()
	{
		return urlListInfo;
	}

	public void setUrlListInfo(String urlListInfo)
	{
		this.urlListInfo = urlListInfo;
	}

	public String getUrlListArchive()
	{
		return urlListArchive;
	}

	public void setUrlListArchive(String ulrListArchive)
	{
		this.urlListArchive = ulrListArchive;
	}

	public String getCharset()
	{
		return charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}

	public void addTimeSpan(TimeSpan timeSpan)
	{
		timeSpans.add(timeSpan);
	}

	public List<TimeSpan> getTimeSpans()
	{
		return timeSpans;
	}

}

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArchiveOverviewParser
{

	public static List<String> parse(String text)
	{
		List<String> links = new ArrayList<>();

		Document doc = Jsoup.parse(text);

		Elements as = doc.getElementsByTag("a");
		for (Element element : as) {
			if (!element.hasAttr("href")) {
				continue;
			}
			String href = element.attr("href");
			if (!href.endsWith(".txt.gz")) {
				continue;
			}
			links.add(href);
		}

		return links;
	}

}

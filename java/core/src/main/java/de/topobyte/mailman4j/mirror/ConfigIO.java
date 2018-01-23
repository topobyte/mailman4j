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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigIO
{

	public static Config read(Path path)
			throws IOException, ParserConfigurationException, SAXException
	{
		InputStream input = Files.newInputStream(path);
		Config config = read(input);
		input.close();
		return config;
	}

	private static Pattern patternDate = Pattern
			.compile("([0-9]{4,4})-([0-9]{1,2})");

	public static Config read(InputStream input)
			throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(input);

		Config config = new Config();

		NodeList listInfos = doc.getElementsByTagName("list-info");
		if (listInfos.getLength() != 0) {
			Element listInfo = (Element) listInfos.item(0);
			String listInfoUrl = listInfo.getAttribute("url");
			config.setUrlListInfo(listInfoUrl);
		}

		NodeList listArchives = doc.getElementsByTagName("list-archive");
		if (listArchives.getLength() != 0) {
			Element listArchive = (Element) listArchives.item(0);

			String listArchiveUrl = listArchive.getAttribute("url");
			config.setUrlListArchive(listArchiveUrl);

			if (listArchive.hasAttribute("charset")) {
				String listArchiveCharset = listArchive.getAttribute("charset");
				config.setCharset(listArchiveCharset);
			}

			NodeList timeSpans = listArchive.getElementsByTagName("timespan");
			for (int i = 0; i < timeSpans.getLength(); i++) {
				Element timeSpan = (Element) timeSpans.item(i);
				String type = timeSpan.getAttribute("type");
				if (type.equals("before")) {
					String valDate = timeSpan.getAttribute("date");
					String charset = timeSpan.getAttribute("charset");
					Matcher matcher = patternDate.matcher(valDate);
					if (matcher.matches()) {
						String year = matcher.group(1);
						String month = matcher.group(2);
						TimeSpanBefore before = new TimeSpanBefore(
								Integer.parseInt(year), Integer.parseInt(month),
								charset);
						config.addTimeSpan(before);
					}
				}
			}
		}

		return config;
	}

}

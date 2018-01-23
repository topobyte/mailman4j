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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.topobyte.mailman4j.mirror.Config;
import de.topobyte.mailman4j.mirror.ConfigIO;

public class TestParseConfig
{

	@Test
	public void read()
			throws ParserConfigurationException, SAXException, IOException
	{
		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("config-test");
		Config config = ConfigIO.read(input);

		Assert.assertEquals(
				"https://lists.openstreetmap.de/mailman/listinfo/berlin",
				config.getUrlListInfo());
		Assert.assertEquals("https://lists.example.com/pipermail/foobar",
				config.getUrlListArchive());
	}

}

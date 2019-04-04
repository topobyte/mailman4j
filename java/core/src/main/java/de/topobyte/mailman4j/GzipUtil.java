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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

public class GzipUtil
{

	public static List<String> lines(Path file, String encoding)
			throws IOException
	{
		InputStream input = Files.newInputStream(file);
		return lines(input, encoding);
	}

	public static List<String> linesGzip(Path file, String encoding)
			throws IOException
	{
		InputStream input = Files.newInputStream(file);
		GZIPInputStream gzip = new GZIPInputStream(input);

		return lines(gzip, encoding);
	}

	private static List<String> lines(InputStream input, String encoding)
			throws IOException
	{
		String text = encoding == null
				? IOUtils.toString(input, StandardCharsets.UTF_8)
				: IOUtils.toString(input, encoding);
		input.close();

		String lines[] = text.split("\\r?\\n");
		return Arrays.asList(lines);
	}

}

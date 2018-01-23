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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mailman4j.http.DownloadException;
import de.topobyte.mailman4j.http.DownloadedText;
import de.topobyte.mailman4j.http.Util;

public class ArchiveDownloader
{

	final static Logger logger = LoggerFactory
			.getLogger(ArchiveDownloader.class);

	private Path dir;
	private String url;

	public ArchiveDownloader(Path directory, String url)
	{
		this.dir = directory;
		this.url = url;
	}

	public void execute() throws IOException, DownloadException
	{
		logger.info("Creating output directory...");
		Files.createDirectories(dir);

		logger.info("Downloading archives overview page...");
		DownloadedText text = Util.download(url, null);
		List<String> links = ArchiveOverviewParser.parse(text.getText());

		String base = url.endsWith("/") ? url : url + "/";

		logger.info("Downloading archives...");
		for (String link : links) {
			String full = base + link;
			logger.info(String.format("Processing link '%s'", link));
			Path file = dir.resolve(link);
			if (Files.exists(file)) {
				logger.info("Skipping existing file");
				continue;
			}
			logger.info(String.format("Downloading '%s'", full));
			Util.download(full, file, null);
		}
	}

}

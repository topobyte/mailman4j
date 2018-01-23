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

package de.topobyte.mailman4j.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util
{

	final static Logger logger = LoggerFactory.getLogger(Util.class);

	private static CloseableHttpClient httpclient = HttpClients.createDefault();

	public static void download(String url, Path out, DownloadOptions options)
			throws IOException, DownloadException
	{
		Path parent = out.getParent();
		Files.createDirectories(parent);

		logger.debug("output: " + out);
		logger.debug("url: " + url);

		HttpGet get = new HttpGet(url);

		setOptions(get, options);

		CloseableHttpResponse response = httpclient.execute(get);
		logger.debug("status: " + response.getStatusLine());

		if (response.getStatusLine().getStatusCode() != 200) {
			response.close();
			throw new DownloadException(
					response.getStatusLine().getStatusCode());
		}

		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();

		OutputStream os = Files.newOutputStream(out);
		IOUtils.copy(content, os);
		content.close();
		response.close();
		os.close();

		LocalDateTime time = null;
		Header[] headers = response.getHeaders("Last-Modified");
		if (headers.length != 0) {
			String lastMod = headers[0].getValue();
			lastMod = fixDate(lastMod);
			TemporalAccessor parse = DateTimeFormatter.RFC_1123_DATE_TIME
					.parse(lastMod);
			time = LocalDateTime.from(parse);
		}

		if (time != null) {
			Files.setLastModifiedTime(out,
					FileTime.from(time.toInstant(ZoneOffset.UTC)));
		}
	}

	public static DownloadedText download(String url, DownloadOptions options)
			throws IOException, DownloadException
	{
		logger.debug("url: " + url);

		HttpGet get = new HttpGet(url);

		setOptions(get, options);

		CloseableHttpResponse response = httpclient.execute(get);
		logger.debug("status: " + response.getStatusLine());

		if (response.getStatusLine().getStatusCode() != 200) {
			response.close();
			throw new DownloadException(
					response.getStatusLine().getStatusCode());
		}

		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copy(content, os);
		content.close();
		response.close();
		os.close();

		LocalDateTime time = null;
		Header[] headers = response.getHeaders("Last-Modified");
		if (headers.length != 0) {
			String lastMod = headers[0].getValue();
			lastMod = fixDate(lastMod);
			TemporalAccessor parse = DateTimeFormatter.RFC_1123_DATE_TIME
					.parse(lastMod);
			time = LocalDateTime.from(parse);
		}

		long modified = 0;
		if (time != null) {
			modified = time.toEpochSecond(ZoneOffset.UTC);
		}

		return new DownloadedText(new String(os.toByteArray()), modified);
	}

	private static void setOptions(HttpGet get, DownloadOptions options)
	{
		if (options == null) {
			return;
		}
		if (options.getUserAgent() != null) {
			get.addHeader("User-Agent", options.getUserAgent());
		}
	}

	private static String fixDate(String lastMod)
	{
		Pattern pattern = Pattern.compile("(.*)  ([0-9]*):(.*)");
		Matcher matcher = pattern.matcher(lastMod);
		if (matcher.matches()) {
			String fix = matcher.group(1) + " 0" + matcher.group(2) + ":"
					+ matcher.group(3);
			return fix;
		}
		return lastMod;
	}

}

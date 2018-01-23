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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.collections.util.ListUtil;

public class MailsParser
{

	final static Logger logger = LoggerFactory.getLogger(MailsParser.class);

	private List<String> lines;
	private List<Mail> mails;
	private boolean containsInvalid = false;

	public MailsParser(List<String> lines)
	{
		this.lines = lines;
	}

	public List<Mail> getMails()
	{
		return mails;
	}

	public boolean containsInvalid()
	{
		return containsInvalid;
	}

	public void parse()
	{
		mails = new ArrayList<>();

		List<Integer> possibleStarts = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("From ")) {
				possibleStarts.add(i);
			}
		}

		List<Integer> valid = new ArrayList<>();
		List<Integer> invalid = new ArrayList<>();

		for (int i : possibleStarts) {
			if (i + 3 >= lines.size()) {
				invalid.add(i);
				continue;
			}
			String line1 = lines.get(i + 1);
			String line2 = lines.get(i + 2);
			if (!line1.startsWith("From: ")) {
				invalid.add(i);
				continue;
			}
			if (!line2.startsWith("Date: ")) {
				invalid.add(i);
				continue;
			}
			valid.add(i);
		}

		if (!invalid.isEmpty()) {
			containsInvalid = true;
			logger.debug(String.format(
					"Lines starting with 'From ' that do not start a mail: %s",
					invalid));
			for (int i : invalid) {
				logger.debug(String.format("Line content: '%s'", lines.get(i)));
			}
		}

		assembleMails(valid);
	}

	private void assembleMails(List<Integer> indices)
	{
		for (int i = 0; i < indices.size() - 1; i++) {
			int index = indices.get(i);
			int nextIndex = indices.get(i + 1);
			assembleMail(index, nextIndex);
		}

		int lastIndex = ListUtil.last(indices);
		assembleMail(lastIndex, lines.size());
	}

	private void assembleMail(int from, int to)
	{
		List<String> relevantLines = new ArrayList<>();
		for (int i = from; i < to; i++) {
			relevantLines.add(lines.get(i));
		}

		// The following must be true since this is how we determined the
		// validity of each line index
		String from1 = relevantLines.get(0).substring(5);
		String from2 = relevantLines.get(1).substring(6);
		String date = relevantLines.get(2).substring(6);

		// Now make sense of the following lines. Parse headers lines until we
		// hit an empty line (or only whitespace). After that line, assume mail
		// content.
		String subject = null;

		int startText = -1;
		for (int i = 3; i < relevantLines.size(); i++) {
			String line = relevantLines.get(i);
			String trimmedLine = line.trim();
			if (trimmedLine.isEmpty()) {
				startText = i + 1;
				break;
			}
			if (line.startsWith("Subject: ") && subject == null) {
				subject = line.substring(9);
			}
		}

		List<String> text = new ArrayList<>();
		text.addAll(relevantLines.subList(startText, relevantLines.size()));

		long timestamp = Dates.parse(date);

		mails.add(new Mail(from1, from2, timestamp, subject, text));
	}

}

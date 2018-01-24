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

package de.topobyte.mailman4j.cli;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.topobyte.mailman4j.ArchiveDownloader;
import de.topobyte.mailman4j.mirror.Config;
import de.topobyte.mailman4j.mirror.ConfigIO;
import de.topobyte.mailman4j.mirror.MirrorPaths;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunFetchArchives
{

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, CommonOptions.OPTION_DIR, true, true, CommonOptions.ARG_DIR, CommonOptions.DESCRIPTION_DIR);
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argDir = line.getOptionValue(CommonOptions.OPTION_DIR);
		Path pathDir = Paths.get(argDir);

		Path fileConfig = pathDir.resolve(MirrorPaths.FILENAME_CONFIG);
		Config config = ConfigIO.read(fileConfig);

		Path dirArchives = pathDir.resolve(MirrorPaths.DIRNAME_ARCHIVES);

		ArchiveDownloader downloader = new ArchiveDownloader(dirArchives,
				config.getUrlListArchive());
		downloader.execute();
	}

}

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddressLister
{

	private Set<String> addresses = new HashSet<>();
	private Map<String, Integer> counter = new HashMap<>();

	public void address(String account, String server)
	{
		String address = String.format("%s@%s", account, server);
		boolean added = addresses.add(address);
		if (added) {
			counter.put(address, 1);
		} else {
			counter.put(address, counter.get(address) + 1);
		}
	}

	public void print()
	{
		List<String> list = new ArrayList<>(addresses);

		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2)
			{
				int c = Integer.compare(counter.get(o2), counter.get(o1));
				if (c != 0) {
					return c;
				}
				return o1.compareTo(o2);
			}

		});

		System.out.println("Number of distinct addresses: " + addresses.size());
		for (String address : list) {
			System.out.println(
					String.format("%s: %d", address, counter.get(address)));
		}
	}

}

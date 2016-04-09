/*
 * Copyright 2015-2016 Marco Stornelli <playappassistance@gmail.com>
 * 
 * This file is part of AirTask Desktop.
 *
 * AirTask Desktop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AirTask Desktop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AirTask Desktop.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.balda.airtask.ui;

import java.io.Serializable;
import java.util.regex.Pattern;

public class NotificationFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -415500125770755654L;
	public static final String ALL = "All";
	private String sender;
	private Pattern filter;

	public NotificationFilter(String s, String regex) {
		if (!ALL.equals(s))
			sender = s;
		filter = Pattern.compile(regex);
	}

	public boolean apply(String msg, String from) {
		if (sender != null && !from.equalsIgnoreCase(sender))
			return true;
		if (filter.matcher(msg).matches())
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (sender != null)
			return filter + " " + sender;
		else
			return filter.toString();
	}
}

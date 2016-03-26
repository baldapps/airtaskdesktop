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

package com.balda.airtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class Settings {

	public static final String TIMEOUT = "timeout";
	public static final String NAME = "name";
	public static final String DOWNLOADPATH = "downloadpath";
	public static final String ICONPATH = "iconpath";
	public static final String DEVICES = "devices";
	public static final String CLIPBOARD = "clipboard";

	public static final int DEF_TIMEOUT = 10000;
	public static final String DEF_NAME = "pc";
	public static final String DEF_DOWNLOADPATH = System.getProperty("user.home") + "/Downloads";
	public static final String DEF_ICONPATH = System.getProperty("user.dir") + "/airtask.png";
	public static final String DEF_CLIPBOARD = "#clip#";
	private Preferences prefs = Preferences.userNodeForPackage(getClass());

	private static Settings instance = new Settings();

	private Settings() {
	}

	public static Settings getInstance() {
		return instance;
	}

	public int getTimeout() {
		return prefs.getInt(TIMEOUT, DEF_TIMEOUT);
	}
	
	public String getName() {
		return prefs.get(NAME, DEF_NAME);
	}
	
	public String getDownloadPath() {
		return prefs.get(DOWNLOADPATH, DEF_DOWNLOADPATH);
	}
	
	public String getIconPath() {
		return prefs.get(ICONPATH, DEF_ICONPATH);
	}
	
	public String getClipboardPrefix() {
		return prefs.get(CLIPBOARD, DEF_CLIPBOARD);
	}
	
	public List<Device> getDevices() {
		String list = prefs.get(DEVICES, null);
		if (list == null)
			return Collections.emptyList();
		String[] tokens = list.split(";");
		ArrayList<Device> devices = new ArrayList<>();
		for (String d : tokens) {
			String[] devInfo = d.split("@");
			if (devInfo.length == 2) {
				devices.add(new Device(devInfo[0], devInfo[1]));
			}
		}
		return devices;
	}
	
	public void setTimeout(int timeout) {
		prefs.putInt(TIMEOUT, timeout);
	}

	public void setName(String name) {
		prefs.put(NAME, name);
	}

	public void setDownloadPath(String path) {
		prefs.put(DOWNLOADPATH, path);
	}
	
	public void setIconPath(String path) {
		prefs.put(ICONPATH, path);
	}
	
	public void setClipboardPrefix(String prefix) {
		prefs.put(CLIPBOARD, prefix);
	}
	
	public void setDevices(List<Device> list) {
		if (list.size() == 0) {
			prefs.remove(DEVICES);
			return;
		}
		StringBuilder b = new StringBuilder();
		for (Device d : list) {
			b.append(d.toString());
			b.append(";");
		}
		prefs.put(DEVICES, b.substring(0, b.length() - 1));
	}
}

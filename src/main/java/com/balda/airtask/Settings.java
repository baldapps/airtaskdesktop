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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import com.balda.airtask.ui.NotificationFilter;

public class Settings {

	public static final String TIMEOUT = "timeout";
	public static final String NAME = "name";
	public static final String DOWNLOADPATH = "downloadpath";
	public static final String ICONPATH = "iconpath";
	public static final String DEVICES = "devices";
	public static final String FILTERS = "filters";
	public static final String CLIPBOARD = "clipboard";
	public static final String SHOW_NOTIFICATIONS = "shownotificatios";
	public static final String SYNC_CLIPBOARD = "sync_clipboard";

	public static final int DEF_TIMEOUT = 10000;
	public static final String DEF_NAME = "pc";
	public static final String DEF_DOWNLOADPATH = System.getProperty("user.home") + "/Downloads";
	public static final String DEF_ICONPATH = System.getProperty("user.dir") + "/airtask.png";
	public static final String DEF_CLIPBOARD = "#clip#";
	public static final boolean DEF_SHOW = true;
	public static final boolean DEF_SYNC_CLIPBOARD = false;
	private Preferences prefs = Preferences.userNodeForPackage(getClass());
	private static final int MAX_BYTE_LEN = (Preferences.MAX_VALUE_LENGTH * 3) / 4;

	private static final Settings instance = new Settings();

	private Settings() {
	}

	public static Settings getInstance() {
		return instance;
	}

	public boolean syncClipboard() {
		return prefs.getBoolean(SYNC_CLIPBOARD, DEF_SYNC_CLIPBOARD);
	}

	public void setSyncClipboard(boolean sync) {
		prefs.putBoolean(SYNC_CLIPBOARD, sync);
	}

	public boolean showNotifications() {
		return prefs.getBoolean(SHOW_NOTIFICATIONS, DEF_SHOW);
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

	@SuppressWarnings("unchecked")
	public ArrayList<Device> getDevices() {
		ArrayList<Device> list = (ArrayList<Device>) getSerializable(DEVICES);
		if (list == null)
			return new ArrayList<>();
		return list;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<NotificationFilter> getFilters() {
		ArrayList<NotificationFilter> list = (ArrayList<NotificationFilter>) getSerializable(FILTERS);
		if (list == null)
			return new ArrayList<>();
		return list;
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

	public void setDevices(ArrayList<Device> list) {
		putSerializable(DEVICES, list);
	}

	public void setFilters(ArrayList<NotificationFilter> list) {
		putSerializable(FILTERS, list);
	}

	public void setShowNotifications(boolean show) {
		prefs.putBoolean(SHOW_NOTIFICATIONS, show);
	}

	public void addListener(PreferenceChangeListener pcl) {
		prefs.addPreferenceChangeListener(pcl);
	}

	public void removeListener(PreferenceChangeListener pcl) {
		prefs.removePreferenceChangeListener(pcl);
	}

	private void putSerializable(String key, Serializable s) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream objs;
		try {
			objs = new ObjectOutputStream(bos);
			objs.writeObject(s);
		} catch (IOException e) {
			return;
		}
		byte bytes[] = bos.toByteArray();

		int nChunks = (bytes.length + MAX_BYTE_LEN - 1) / MAX_BYTE_LEN;
		byte chunks[][] = new byte[nChunks][];
		for (int i = 0; i < nChunks; ++i) {
			int startByte = i * MAX_BYTE_LEN;
			int endByte = startByte + MAX_BYTE_LEN;
			if (endByte > bytes.length)
				endByte = bytes.length;
			int length = endByte - startByte;
			chunks[i] = new byte[length];
			System.arraycopy(bytes, startByte, chunks[i], 0, length);
		}

		for (int i = 0; i < chunks.length; ++i) {
			prefs.putByteArray(key + i, chunks[i]);
		}
		prefs.putInt(key + "len", nChunks);
	}

	private Serializable getSerializable(String key) {
		Serializable t = null;
		int length = 0;

		int nChunks = prefs.getInt(key + "len", 0);
		if (nChunks == 0)
			return null;
		byte chunks[][] = new byte[nChunks][];
		for (int i = 0; i < nChunks; i++) {
			chunks[i] = prefs.getByteArray(key + i, null);
			if (chunks[i] == null)
				return null;
			length += chunks[i].length;
		}
		byte bytes[] = new byte[length];
		int offset = 0;
		for (int i = 0; i < chunks.length; i++) {
			System.arraycopy(chunks[i], 0, bytes, offset, chunks[i].length);
			offset += chunks[i].length;
		}
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream objs = new ObjectInputStream(bis);
			t = (Serializable) objs.readObject();
		} catch (ClassCastException | IOException | ClassNotFoundException e) {
			t = null;
		}
		return t;
	}
}

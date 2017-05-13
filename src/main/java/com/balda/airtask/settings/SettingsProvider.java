/*
 * Copyright 2015-2017 Marco Stornelli <playappassistance@gmail.com>
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

package com.balda.airtask.settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class SettingsProvider {

	protected Preferences prefs = Preferences.userNodeForPackage(getClass());
	private static final int MAX_BYTE_LEN = (Preferences.MAX_VALUE_LENGTH * 3) / 4;

	protected SettingsProvider() {
	}

	public void addListener(PreferenceChangeListener pcl) {
		prefs.addPreferenceChangeListener(pcl);
	}

	public void removeListener(PreferenceChangeListener pcl) {
		prefs.removePreferenceChangeListener(pcl);
	}

	protected void putSerializable(String key, Serializable s) {
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

	protected Serializable getSerializable(String key) {
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

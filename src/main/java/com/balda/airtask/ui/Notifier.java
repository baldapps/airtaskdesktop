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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import com.balda.airtask.Settings;

public abstract class Notifier implements PreferenceChangeListener {

	private boolean show;

	public Notifier() {
		show = true;
		Settings.getInstance().addListener(this);
	}

	protected void toClipBoard(String msg) {
		StringSelection stringSelection = new StringSelection(msg);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	public void notify(String msg, String from) throws IOException {
		if (show)
			show(msg, from);
	}

	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(Settings.SHOW_NOTIFICATIONS)) {
			show = Settings.getInstance().showNotifications();
		}
	}

	protected abstract void show(String msg, String from) throws IOException;
}

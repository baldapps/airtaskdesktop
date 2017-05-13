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

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.balda.airtask.settings.Settings;

public class BuiltinNotifier extends Notifier {

	private Toaster toaster;
	private Icon icon;

	public BuiltinNotifier() {
		toaster = new Toaster();
		if (Settings.getInstance().getIconPath() != null) {
			try {
				icon = new ImageIcon(Settings.getInstance().getIconPath());
			} catch (Exception e) {
				icon = null;
			}
		} else
			icon = null;
		toaster.setDisplayTime(Settings.getInstance().getTimeout());
	}

	@Override
	public void show(String msg, String from) throws IOException {
		String origMsg;
		if (msg.startsWith(Settings.getInstance().getClipboardPrefix())) {
			origMsg = msg.substring(Settings.getInstance().getClipboardPrefix().length());
			toClipBoard(origMsg);
		} else {
			origMsg = msg;
		}

		if (icon == null) {
			toaster.showToaster(from, origMsg);
		} else {
			toaster.showToaster(icon, from, origMsg);
		}
	}
}

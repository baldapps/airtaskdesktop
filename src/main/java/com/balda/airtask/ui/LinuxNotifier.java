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

import com.balda.airtask.settings.Settings;

public class LinuxNotifier extends Notifier {

	@Override
	public void show(String msg, String from) throws IOException {
		String iconCmd = "";
		if (Settings.getInstance().getIconPath() != null) {
			iconCmd = " -i " + Settings.getInstance().getIconPath();
		}
		if (msg.startsWith(Settings.getInstance().getClipboardPrefix())) {
			String origMsg = msg.substring(Settings.getInstance().getClipboardPrefix().length());
			String command = "notify-send \"" + from + "\" \"To clipboard: " + origMsg + "\" -t "
					+ Settings.getInstance().getTimeout() + iconCmd;
			Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
			toClipBoard(origMsg);
		} else {
			String command = "notify-send \"" + from + "\" \"" + msg + "\" -t " + Settings.getInstance().getTimeout()
					+ iconCmd;
			Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
		}
	}

}

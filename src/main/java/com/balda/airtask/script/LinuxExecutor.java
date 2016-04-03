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

package com.balda.airtask.script;

import java.io.IOException;

public class LinuxExecutor extends ScriptExecutor {

	LinuxExecutor() {
	}

	@Override
	public void onFileReceived(String from, String path) {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", root + "/fileReceived", from, path);
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReceived(String from, String msg) {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", root + "/messageReceived", from, msg);
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

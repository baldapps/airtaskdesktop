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

import org.kohsuke.args4j.Option;

public class Parameters {
	@Option(name = "-m", usage = "message to send", metaVar = "MESSAGE", forbids = { "-f" })
	private String message;
	@Option(name = "-f", usage = "file to send", metaVar = "FILE", forbids = { "-m" })
	private String file;
	@Option(name = "-d", usage = "target device name", metaVar = "DEVICE")
	private String device;
	@Option(name = "-clip", usage = "send clipboard to device", forbids = { "-m", "-f" })
	private boolean clip;

	public String getMessage() {
		return message;
	}

	public String getFile() {
		return file;
	}

	public String getDevice() {
		return device;
	}

	public boolean sendClip() {
		return clip;
	}
}

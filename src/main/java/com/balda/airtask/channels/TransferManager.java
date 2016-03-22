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

package com.balda.airtask.channels;

import java.io.File;

import com.balda.airtask.AirTask;

public class TransferManager {

	private static TransferManager instance = new TransferManager();

	private TransferManager() {
	}

	public static TransferManager getInstance() {
		return instance;
	}

	public void sendFile(File f, String target) {
		TransferRequest req = new TransferRequest();
		req.setDestination(target);
		req.setFile(f.getAbsolutePath());
		req.setIp(AirTask.ipMap.get(target));
		new TransferClient(req).start();
	}
}

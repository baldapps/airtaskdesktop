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

public class ChannelManager {

	private ProbeReceiver pb;
	private RegistrationServer rs;
	private TcpMsgServer msgServer;
	private TransferServer ts;
	private static final ChannelManager instance = new ChannelManager();

	private ChannelManager() {
		pb = new ProbeReceiver();
		rs = new RegistrationServer();
		msgServer = new TcpMsgServer();
		ts = new TransferServer();
	}

	public static ChannelManager getInstance() {
		return instance;
	}

	public void start() {
		pb.start();
		rs.start();
		msgServer.start();
		ts.start();
	}

	public void stop() {
		pb.quit();
		rs.quit();
		msgServer.quit();
		ts.quit();
		try {
			pb.join();
			rs.join();
			msgServer.join();
			ts.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.balda.airtask.Settings;
import com.balda.airtask.json.Message;
import com.balda.airtask.script.ScriptFactory;
import com.balda.airtask.ui.NotifierFactory;
import com.google.gson.Gson;

public class TcpMsgServer extends Thread {

	private ServerSocket socket;
	private volatile boolean quit = false;
	public static final int PORT = 9876;

	TcpMsgServer() {
	}

	private void listen() throws Exception {
		Socket client = socket.accept();
		DataInputStream dis = new DataInputStream(client.getInputStream());
		String payload = dis.readUTF();
		Message m = new Gson().fromJson(payload, Message.class);
		if (m.getUserMessage() != null && m.getFromDevice() != null) {
			String from = m.getFromDevice();
			String msg = m.getUserMessage();
			String to = m.getTargetDevice();
			if (from.equals(Settings.getInstance().getName()))
				return;
			if (to != null && to.equalsIgnoreCase(Settings.getInstance().getName())) {
				ScriptFactory.getExecutor().onMessageReceived(from, msg);
				NotifierFactory.getNotifier().notify(msg, from);
			} else if (to == null) {
				ScriptFactory.getExecutor().onMessageReceived(from, msg);
				NotifierFactory.getNotifier().notify(msg, from);
			}
		}
	}

	@Override
	public void run() {
		try {
			socket = new ServerSocket(PORT);
			socket.setReuseAddress(true);
		} catch (Exception e) {
			return;
		}
		while (!quit) {
			try {
				listen();
			} catch (Exception ignored) {
			}
		}
	}

	public void quit() {
		quit = true;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

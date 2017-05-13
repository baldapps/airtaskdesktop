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
import java.util.ArrayList;

import com.balda.airtask.Device;
import com.balda.airtask.settings.Settings;

public class RegistrationServer extends Thread {

	public static final int REGISTRATION_PORT = 9878;
	private ServerSocket socket;
	private volatile boolean running;

	RegistrationServer() {
		try {
			socket = new ServerSocket(REGISTRATION_PORT);
			socket.setReuseAddress(true);
		} catch (IOException ignored) {
		}
		running = true;
	}

	@Override
	public void run() {
		if (socket == null)
			return;
		while (running) {
			try {
				final Socket clientSock = socket.accept();
				new ServerWorkerThread(clientSock).start();
			} catch (IOException ignored) {
			}
		}
	}

	private class ServerWorkerThread extends Thread {

		private Socket s;

		public ServerWorkerThread(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			saveDevice(s);
		}
	}

	private void saveDevice(Socket clientSock) {
		DataInputStream dis;
		try {
			dis = new DataInputStream(clientSock.getInputStream());
		} catch (IOException e) {
			try {
				clientSock.close();
			} catch (IOException ignored) {
			}
			return;
		}
		Device d = null;
		try {
			d = new Device(dis.readUTF(), dis.readUTF(), false);
		} catch (IOException e) {
			try {
				clientSock.close();
				dis.close();
			} catch (IOException ignored) {
			}
			return;
		}
		try {
			dis.close();
			clientSock.close();
		} catch (IOException ignored) {
		}

		ArrayList<Device> devices = Settings.getInstance().getDevices();
		if (devices.contains(d)) {
			devices.remove(d);
			devices.add(d);
		} else {
			devices.add(d);
		}
		Settings.getInstance().setDevices(devices);
	}

	public void quit() {
		running = false;
		try {
			if (socket != null)
				socket.close();
		} catch (IOException ignored) {
		}
	}
}

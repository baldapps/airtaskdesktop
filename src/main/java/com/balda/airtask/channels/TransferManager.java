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

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.balda.airtask.Device;
import com.balda.airtask.Settings;
import com.balda.airtask.json.Message;
import com.google.gson.Gson;

public class TransferManager {

	private Gson gson;
	private static TransferManager instance = new TransferManager();

	private TransferManager() {
		gson = new Gson();
	}

	public static TransferManager getInstance() {
		return instance;
	}

	public void sendFile(File f, Device target, boolean delete) {
		TransferRequest req = new TransferRequest();
		req.setDestination(target.getName().toLowerCase().trim());
		req.setFile(f.getAbsolutePath());
		req.setIp(target.getAddress());
		req.setDeleteOnExit(delete);
		new TransferClient(req).start();
	}

	public void sendMessage(String msg, Device device) throws IOException {
		Message m = new Message();
		m.setUserMessage(msg);
		m.setFromDevice(Settings.getInstance().getName());
		m.setTargetDevice(device.getName().toLowerCase().trim());
		Socket socket = new Socket();
		try {
			socket.setSoTimeout(30000);
			socket.connect(new InetSocketAddress(device.getAddress(), TcpMsgServer.PORT), 30000);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(gson.toJson(m));
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

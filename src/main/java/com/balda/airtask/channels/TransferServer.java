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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.balda.airtask.Settings;
import com.balda.airtask.script.ScriptFactory;
import com.balda.airtask.ui.NotifierFactory;

public class TransferServer extends Thread {
	public static final int TRANSFER_PORT = 9877;
	private ServerSocket socket;
	private volatile boolean running;

	public TransferServer() {
		try {
			socket = new ServerSocket(TRANSFER_PORT);
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
			saveFile(s);
		}
	}

	private void saveFile(Socket clientSock) {
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
		String sender;
		String fileName;
		long len;
		try {
			sender = dis.readUTF();
			fileName = dis.readUTF();
			len = dis.readLong();
		} catch (IOException e) {
			try {
				clientSock.close();
				dis.close();
			} catch (IOException ignored) {
			}
			return;
		}
		File folder = new File(Settings.getInstance().getDownloadPath());
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				try {
					dis.close();
				} catch (IOException ignored) {
				}
				return;
			}
		}

		File dest = new File(Settings.getInstance().getDownloadPath() + "/" + fileName);
		if (dest.exists()) {
			// noinspection ResultOfMethodCallIgnored
			dest.delete();
		}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(Settings.getInstance().getDownloadPath() + "/" + fileName);
		} catch (FileNotFoundException e) {
			try {
				dis.close();
			} catch (IOException ignored) {
			}
			return;
		}
		byte[] buffer = new byte[4096];
		int read;
		long remaining = len;
		try {
			while ((read = dis.read(buffer, 0, Math.min(4096, (int) remaining))) > 0) {
				remaining -= read;
				fos.write(buffer, 0, read);
			}
			NotifierFactory.getNotifier().show("File " + dest.getName() + " received", sender);
			ScriptFactory.getExecutor().onFileReceived(sender, dest.getName());
		} catch (IOException ignored) {
		} finally {
			try {
				fos.close();
				dis.close();
				clientSock.close();
			} catch (IOException ignored) {
			}
		}
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

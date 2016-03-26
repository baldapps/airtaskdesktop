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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.balda.airtask.Settings;

public class TransferClient extends Thread {

	private TransferRequest req;

	public TransferClient(TransferRequest r) {
		req = r;
	}

	@Override
	public void run() {
		byte[] tmp = new byte[4096];
		int n;
		Socket sock;
		try {
			sock = new Socket(req.getIp(), TransferServer.TRANSFER_PORT);
		} catch (IOException e) {
			return;
		}
		File myFile = new File(req.getFile());
		long fileSize = myFile.length();
		FileInputStream fis;
		try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			try {
				sock.close();
			} catch (IOException ignored) {
			}
			return;
		}
		DataInputStream bis = new DataInputStream(fis);
		OutputStream os;
		try {
			os = sock.getOutputStream();
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException ignored) {
			}
			try {
				sock.close();
			} catch (IOException ignored) {
			}
			return;
		}
		DataOutputStream dos = new DataOutputStream(os);
		try {
			dos.writeUTF(Settings.getInstance().getName());
			dos.writeUTF(myFile.getName());
			dos.writeLong(myFile.length());
			while (fileSize > 0 && (n = bis.read(tmp, 0, (int) Math.min(4096, fileSize))) != -1) {
				os.write(tmp, 0, n);
				fileSize -= n;
			}
		} catch (IOException ignored) {
		} finally {
			try {
				os.flush();
				sock.close();
				fis.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

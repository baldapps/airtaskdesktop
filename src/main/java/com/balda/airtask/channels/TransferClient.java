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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

import com.balda.airtask.Settings;

public class TransferClient extends Thread {

	private TransferRequest req;

	public TransferClient(TransferRequest r) {
		req = r;
	}

	private void showError(final String msg) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, msg);
			}
		});
	}

	@Override
	public void run() {
		byte[] tmp = new byte[4096];
		int n;
		File myFile = new File(req.getFile());
		Socket sock = new Socket();
		try {
			sock.setSoTimeout(5000);
		} catch (SocketException e) {
			if (req.isDeleteOnExit()) {
				myFile.delete();
			}
			try {
				sock.close();
			} catch (IOException ignored) {
			}
			showError(e.getMessage());
			return;
		}
		try {
			sock.connect(new InetSocketAddress(req.getIp(), TransferServer.TRANSFER_PORT), 5000);
		} catch (IOException e) {
			if (req.isDeleteOnExit()) {
				myFile.delete();
			}
			try {
				sock.close();
			} catch (IOException ignored) {
			}
			showError(e.getMessage());
			return;
		}
		long fileSize = myFile.length();
		FileInputStream fis;
		try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			try {
				sock.close();
			} catch (IOException ignored) {
			}
			if (req.isDeleteOnExit()) {
				myFile.delete();
			}
			showError(e.getMessage());
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
			if (req.isDeleteOnExit()) {
				myFile.delete();
			}
			showError(e.getMessage());
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
		} catch (IOException e) {
			showError(e.getMessage());
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
		if (req.isDeleteOnExit()) {
			myFile.delete();
		}
	}
}

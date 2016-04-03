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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.balda.airtask.channels.ProbeReceiver;
import com.balda.airtask.channels.TcpMsgServer;
import com.balda.airtask.channels.TransferManager;
import com.balda.airtask.channels.TransferServer;
import com.balda.airtask.ui.SendMessage;

public class AirTask {

	public static SendMessage form;

	private static void processClip(Parameters params) {
		List<Device> devices = Settings.getInstance().getDevices();
		if (params.getDevice() != null) {
			if (devices.contains(params.getDevice())) {
				ClipboardListener.getInstance().pasteClipboard(devices.get(devices.indexOf(params.getDevice())));
				System.exit(0);
			} else {
				System.err.println("Device not found");
				System.exit(1);
				return;
			}
		} else {
			if (devices.size() > 0 && devices.get(0).isDefault()) {
				ClipboardListener.getInstance().pasteClipboard(devices.get(devices.indexOf(params.getDevice())));
				System.exit(0);
			} else {
				System.err.println("Device not found");
				System.exit(1);
				return;
			}
		}
	}

	private static void processFile(Parameters params) {
		File file = new File(params.getFile());
		List<Device> devices = Settings.getInstance().getDevices();
		if (params.getDevice() != null) {
			if (devices.contains(params.getDevice())) {
				TransferManager.getInstance().sendFile(file, devices.get(devices.indexOf(params.getDevice())), false);
				System.exit(0);
			} else {
				System.err.println("Device not found");
				System.exit(1);
				return;
			}
		} else {
			if (devices.size() > 0 && devices.get(0).isDefault()) {
				TransferManager.getInstance().sendFile(file, devices.get(devices.indexOf(params.getDevice())), false);
				System.exit(0);
			} else {
				System.err.println("Device not found");
				System.exit(1);
				return;
			}
		}
	}

	private static void processMessage(Parameters params) {
		List<Device> devices = Settings.getInstance().getDevices();
		try {
			if (params.getDevice() != null) {
				if (devices.contains(params.getDevice())) {
					TransferManager.getInstance().sendMessage(params.getMessage(),
							devices.get(devices.indexOf(params.getDevice())));
					System.exit(0);
				} else {
					System.err.println("Device not found");
					System.exit(1);
					return;
				}
			} else {
				if (devices.size() > 0 && devices.get(0).isDefault()) {
					TransferManager.getInstance().sendMessage(params.getMessage(), devices.get(0));
					System.exit(0);
				} else {
					System.err.println("Device not found");
					System.exit(1);
					return;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
	}

	public static void main(String[] args) {
		Parameters params = new Parameters();
		CmdLineParser parser = new CmdLineParser(params);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.err.println();
			System.exit(1);
			return;
		}

		if (params.sendClip()) {
			processClip(params);
		} else if (params.getFile() != null) {
			processFile(params);
		} else if (params.getMessage() != null) {
			processMessage(params);
		}

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(SendMessage.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(SendMessage.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(SendMessage.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(SendMessage.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}

		/* Create the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				form = new SendMessage();
			}
		});

		ClipboardListener.getInstance().init();
		TcpMsgServer s;
		s = new TcpMsgServer();
		s.start();
		TransferServer serv = new TransferServer();
		serv.start();
		ProbeReceiver p = new ProbeReceiver();
		p.start();
	}
}

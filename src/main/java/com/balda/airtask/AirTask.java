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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.balda.airtask.ui.SendMessage;

public class AirTask {

	public static long timeout;
	public static String pcName;
	public static String icon;
	public static String clip;
	public static String downloadPath;
	public static SendMessage form;
	public static Map<String, String> ipMap;

	public static void init(String filename) throws FileNotFoundException, IOException {
		Properties configFile = new Properties();
		ipMap = new HashMap<>();
		configFile.load(new FileInputStream(filename));
		timeout = Long.parseLong(configFile.getProperty("TIMEOUT", "10000"));
		pcName = configFile.getProperty("DEVICENAME", "PC");
		icon = configFile.getProperty("ICON", null);
		clip = configFile.getProperty("CLIPBOARDCMD", "");
		downloadPath = configFile.getProperty("DOWNLOADPATH", "");
		if (downloadPath.endsWith("/")) {
			downloadPath = downloadPath.substring(0, downloadPath.length() - 1);
		}
		for (Enumeration<?> e = configFile.propertyNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			if (name.startsWith("DEVICE")) {
				String value = configFile.getProperty(name);
				String[] vals = value.split("@");
				if (vals.length != 2)
					continue;
				ipMap.put(vals[0].toLowerCase().trim(), vals[1].toLowerCase().trim());
			}
		}
	}

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("USAGE: java -jar airtask.jar CONFILE");
			return;
		}
		try {
			init(args[0]);
		} catch (IOException e) {
			System.out.println("error: " + e.getMessage());
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

		/* Create and display the form */

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				form = new SendMessage();
			}
		});

		TcpMsgServer s;
		s = new TcpMsgServer();
		s.start();
		TransferServer serv = new TransferServer();
		serv.start();
		ProbeReceiver p = new ProbeReceiver();
		p.start();
	}
}

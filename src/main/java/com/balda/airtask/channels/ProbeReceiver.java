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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;

import com.balda.airtask.settings.Settings;

public class ProbeReceiver extends Thread {
	private MulticastSocket socket;
	private volatile boolean shouldRestartSocketListen = true;
	public static final String PROBE_ADDR = "226.0.0.1";
	public static final int PROBE_PORT = 9879;
	public static final int REGISTRATION_PORT = 9878;

	ProbeReceiver() {
	}

	private boolean isSentByMe(DatagramPacket packet) throws SocketException {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				if (inetAddress.getHostAddress().equals(packet.getAddress().getHostAddress()))
					return true;
			}
		}
		return false;
	}

	private String getMacAddress(InetAddress ip) {
		String address = null;
		try {
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
			}
			address = sb.toString();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return address;
	}

	private void listen() throws Exception {
		byte[] buf = new byte[65535];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		if (isSentByMe(packet)) {
			return;
		}
		InetAddress sender = packet.getAddress();
		Random r = new Random();
		Thread.sleep(r.nextInt(10) * 1000);
		Socket s = new Socket(sender, REGISTRATION_PORT);
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		String mac = getMacAddress(s.getLocalAddress());
		try {
			dos.writeUTF(Settings.getInstance().getName());
			dos.writeUTF(s.getLocalAddress().getHostAddress());
			if (mac != null)
				dos.writeUTF(mac);
		} finally {
			dos.close();
			s.close();
		}
	}

	@Override
	public void run() {
		InetAddress multiCastIP;
		try {
			multiCastIP = InetAddress.getByName(PROBE_ADDR);
			if (socket == null || socket.isClosed()) {
				socket = new MulticastSocket(PROBE_PORT);
				socket.joinGroup(multiCastIP);
			}
		} catch (Exception e) {
			return;
		}
		while (shouldRestartSocketListen) {
			try {
				listen();
			} catch (Exception ignored) {
			}
		}
		if (!socket.isClosed())
			socket.close();
	}

	public void quit() {
		socket.close();
		shouldRestartSocketListen = false;
	}
}

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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ProbeClient {
	public static void send() throws IOException {
		DatagramSocket socket;
		socket = new DatagramSocket();
		InetAddress group;
		group = InetAddress.getByName(ProbeReceiver.PROBE_ADDR);
		byte[] buf = "probe".getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, ProbeReceiver.PROBE_PORT);
		socket.send(packet);
		socket.close();
	}
}

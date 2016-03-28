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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import com.balda.airtask.channels.TransferManager;

public class ClipboardListener implements ClipboardOwner, PreferenceChangeListener {

	private Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	private Device device;
	private boolean sync;
	private static final ClipboardListener instance = new ClipboardListener();

	private ClipboardListener() {
	}

	public static ClipboardListener getInstance() {
		return instance;
	}

	public void init() {
		Settings.getInstance().addListener(this);
		List<Device> devices = Settings.getInstance().getDevices();
		for (Device d : devices) {
			if (d.isDefault()) {
				device = d;
				break;
			}
		}
		sync = Settings.getInstance().syncClipboard();
		if (sync) {
			Transferable trans = sysClip.getContents(this);
			regainOwnership(trans);
		}
	}

	private void pasteClipboard(Transferable transferable, Device device) {
		if (transferable == null)
			return;
		try {
			String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
			if (data != null)
				TransferManager.getInstance().sendMessage(Settings.getInstance().getClipboardPrefix() + data, device);
		} catch (IOException | UnsupportedFlavorException e) {
		}
	}

	public void pasteClipboard(Device device) {
		Transferable transferable = sysClip.getContents(this);
		pasteClipboard(transferable, device);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(Settings.DEVICES)) {
			List<Device> devices = Settings.getInstance().getDevices();
			for (Device d : devices) {
				if (d.isDefault()) {
					device = d;
					break;
				}
			}
		} else if (evt.getKey().equals(Settings.SYNC_CLIPBOARD)) {
			sync = Settings.getInstance().syncClipboard();
			if (sync) {
				Transferable trans = sysClip.getContents(this);
				regainOwnership(trans);
			}
		}
	}

	void regainOwnership(Transferable t) {
		sysClip.setContents(t, this);
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		if (sync && device != null) {
			try {
				Transferable contents = arg0.getContents(this);
				pasteClipboard(arg1, device);
				regainOwnership(contents);
			} catch (Exception ignored) {
			}
		}
	}
}

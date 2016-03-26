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

package com.balda.airtask.ui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.balda.airtask.Device;
import com.balda.airtask.Settings;
import com.balda.airtask.channels.TcpMsgServer;
import com.balda.airtask.channels.TransferManager;
import com.balda.airtask.json.Message;
import com.google.gson.Gson;

public class SendMessage extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2860961241099827670L;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea messageArea;
	private javax.swing.JComboBox<Device> targetDeviceText;
	private Gson gson;

	/**
	 * Creates new form SendMessage
	 */
	public SendMessage() {
		initComponents();
		gson = new Gson();
	}

	public Image imageForTray(SystemTray theTray) {
		Image trayImage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("icon.png"));
		Dimension trayIconSize = theTray.getTrayIconSize();
		trayImage = trayImage.getScaledInstance(trayIconSize.width, trayIconSize.height, Image.SCALE_SMOOTH);
		return trayImage;
	}

	private void setupTray() {
		if (!SystemTray.isSupported()) {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			return;
		}
		final SystemTray tray = SystemTray.getSystemTray();
		final PopupMenu popup = new PopupMenu();
		final TrayIcon icon = new TrayIcon(imageForTray(tray), "AirTask", popup);

		MenuItem item1 = new MenuItem("OPEN");
		item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});
		MenuItem item2 = new MenuItem("EXIT");
		item2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tray.remove(icon);
				setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				dispose();
				System.exit(0);
			}
		});
		popup.add(item1);
		popup.add(item2);
		try {
			tray.add(icon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	private void initComponents() {

		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		targetDeviceText = new javax.swing.JComboBox<>();
		jScrollPane1 = new javax.swing.JScrollPane();
		messageArea = new javax.swing.JTextArea();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setupTray();

		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
		setIconImage(icon.getImage());
		setTitle("AirTask");

		jButton1.setText("Send");
		jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jButton1MouseClicked(evt);
			}
		});

		List<Device> devices = Settings.getInstance().getDevices();
		for (Device d : devices) {
			targetDeviceText.addItem(d);
		}
		
		messageArea.setColumns(20);
		messageArea.setRows(5);
		jScrollPane1.setViewportView(messageArea);

		jLabel1.setText("Target device");

		jLabel2.setText("Message");

		jButton2.setText("Send File");
		jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				sendFileMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(targetDeviceText)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(jButton2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jButton1))
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jLabel1).addComponent(jLabel2))
								.addGap(0, 0, Short.MAX_VALUE)))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addGap(4, 4, 4)
						.addComponent(targetDeviceText, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(22, 22, 22).addComponent(jLabel2)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButton1).addComponent(jButton2))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		pack();
	}

	private void sendFileMouseClicked(java.awt.event.MouseEvent evt) {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showDialog(jButton2, "Send");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			TransferManager.getInstance().sendFile(file, (Device) targetDeviceText.getSelectedItem());
		}
	}

	private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {
		String txt = messageArea.getText();
		Device device = (Device) targetDeviceText.getSelectedItem();
		Message m = new Message();
		m.setUserMessage(txt);
		m.setFromDevice(Settings.getInstance().getName());
		m.setTargetDevice(device.getName().toLowerCase().trim());
		Socket socket;
		try {
			socket = new Socket(device.getName().toLowerCase().trim(), TcpMsgServer.PORT);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Impossible to send the message: " + e.getMessage());
			return;
		}
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(gson.toJson(m));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Impossible to send the message: " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		messageArea.setText("");
	}
}

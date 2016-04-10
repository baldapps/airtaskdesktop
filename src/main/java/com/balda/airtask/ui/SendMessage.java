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
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.balda.airtask.Device;
import com.balda.airtask.Settings;
import com.balda.airtask.channels.TransferManager;

public class SendMessage extends javax.swing.JFrame implements PreferenceChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2860961241099827670L;
	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton3;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JScrollPane jScrollPane1;
	private JTextArea messageArea;
	private JComboBox<Device> targetDeviceText;
	private JMenuBar menuBar;
	private PriorityComboBoxModel comboModel;

	/**
	 * Creates new form SendMessage
	 */
	public SendMessage() {
		initComponents();
		Settings.getInstance().addListener(this);
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

		CheckboxMenuItem cb1 = new CheckboxMenuItem("Notifications");
		cb1.setState(Settings.getInstance().showNotifications());
		cb1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				Settings.getInstance().setShowNotifications(!Settings.getInstance().showNotifications());
			}
		});

		MenuItem item1 = new MenuItem("Open");
		item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
			}
		});
		MenuItem item2 = new MenuItem("Exit");
		item2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tray.remove(icon);
				setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				dispose();
				System.exit(0);
			}
		});
		popup.add(cb1);
		popup.addSeparator();
		popup.add(item1);
		popup.add(item2);
		try {
			tray.add(icon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	private void initComponents() {

		jButton1 = new JButton();
		jButton2 = new JButton();
		jButton3 = new JButton();
		targetDeviceText = new JComboBox<>();
		jScrollPane1 = new JScrollPane();
		messageArea = new JTextArea();
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		menuBar = new JMenuBar();

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setupTray();

		JMenu menu = new JMenu("Options");
		JMenuItem menuItem = new JMenuItem("Options");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OptionsDialog odiag = new OptionsDialog();
				odiag.setVisible(true);
			}
		});
		menu.setMnemonic(KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		menu.add(menuItem);
		menuBar.add(menu);
		setJMenuBar(menuBar);

		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
		setIconImage(icon.getImage());
		setTitle("AirTask");

		jButton1.setText("Send");
		jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				sendMessageMouseClicked(evt);
			}
		});

		List<Device> devices = Settings.getInstance().getDevices();
		comboModel = new PriorityComboBoxModel(devices);
		targetDeviceText.setModel(comboModel);

		messageArea.setColumns(20);
		messageArea.setRows(5);
		jScrollPane1.setViewportView(messageArea);

		jLabel1.setText("Target device");

		jLabel2.setText("Message");

		jButton2.setText("Send File");
		jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				sendFileMouseClicked(evt);
			}
		});

		jButton3.setText("Send clipboard");
		jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				sendClipboardMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(targetDeviceText)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup().addComponent(jButton3)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jButton2)
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
								.addComponent(jButton1).addComponent(jButton2).addComponent(jButton3))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		pack();
	}

	private void sendFileMouseClicked(MouseEvent evt) {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showDialog(jButton2, "Send");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			TransferManager.getInstance().sendFile(file, (Device) targetDeviceText.getSelectedItem(), false);
		}
	}

	private void sendClipboardMouseClicked(MouseEvent evt) {
		Device device = (Device) targetDeviceText.getSelectedItem();
		Transferable transferable;
		try {
			transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		} catch (IllegalStateException e) {
			JOptionPane.showMessageDialog(this, "Impossible to send the message: clipboard not available", "AirTask",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (transferable == null) {
			JOptionPane.showMessageDialog(this, "Clipboard is empty", "AirTask", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String lastErr = null;
		DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
		for (int count = 0; count < dataFlavors.length; count++) {
			if (DataFlavor.stringFlavor == dataFlavors[count]) {
				Object object;
				try {
					object = transferable.getTransferData(dataFlavors[count]);
				} catch (UnsupportedFlavorException | IOException e) {
					continue;
				}
				if (object instanceof String) {
					try {
						TransferManager.getInstance()
								.sendMessage(Settings.getInstance().getClipboardPrefix() + object.toString(), device);
					} catch (IOException e) {
						lastErr = e.getMessage();
					}
					break;
				}
			} else {
				Object object;
				try {
					object = transferable.getTransferData(dataFlavors[count]);
				} catch (UnsupportedFlavorException | IOException e) {
					continue;
				}
				if (object instanceof Image) {
					BufferedImage image = (BufferedImage) object;
					File f = new File(Settings.getInstance().getDownloadPath() + "/" + UUID.randomUUID() + ".png");
					try {
						ImageIO.write(image, "png", f);
					} catch (IOException e) {
						lastErr = e.getMessage();
						continue;
					}
					TransferManager.getInstance().sendFile(f, device, true);
					break;
				} else if (object instanceof List) {
					@SuppressWarnings("unchecked")
					List<File> selectedFileList = (List<File>) object;
					int size = selectedFileList.size();
					for (int index = 0; index < size; index++) {
						File file = selectedFileList.get(index);
						if (!file.isDirectory()) {
							TransferManager.getInstance().sendFile(file, device, false);
						} else {
							lastErr = "Directory transfer is not supported";
						}
					}
					break;
				}
			}
		}
		if (lastErr != null) {
			JOptionPane.showMessageDialog(this, "An error occured during transfer: " + lastErr, "AirTask",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void sendMessageMouseClicked(java.awt.event.MouseEvent evt) {
		String txt = messageArea.getText();
		Device device = (Device) targetDeviceText.getSelectedItem();
		boolean failed = false;
		try {
			TransferManager.getInstance().sendMessage(txt, device);
		} catch (IOException e) {
			failed = true;
			JOptionPane.showMessageDialog(this, "Impossible to send the message: " + e.getMessage(), "AirTask",
					JOptionPane.ERROR_MESSAGE);
		}
		if (!failed)
			messageArea.setText("");
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().startsWith(Settings.DEVICES)) {
			comboModel.removeAllElements();
			List<Device> devices = Settings.getInstance().getDevices();
			comboModel.addAll(devices);
		}
	}
}

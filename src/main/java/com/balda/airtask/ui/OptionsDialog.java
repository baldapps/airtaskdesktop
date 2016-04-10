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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import com.balda.airtask.Device;
import com.balda.airtask.Settings;
import com.balda.airtask.channels.ProbeClient;

public class OptionsDialog extends JDialog implements PreferenceChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7524032380144298523L;
	private final JPanel contentPanel = new JPanel();
	private JFormattedTextField timeoutField;
	private JTextField pcNameField;
	private JTextField iconField;
	private JTextField clipboardPrefixField;
	private JTextField downloadPathField;
	private JList<Device> deviceList;
	private JList<NotificationFilter> filterList;
	private JCheckBox chckbxNewCheckBox;
	private DefaultListModel<Device> listModel;
	private DefaultListModel<NotificationFilter> filterModel;

	private class DeviceRenderer extends JLabel implements ListCellRenderer<Device> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -951398425178262012L;

		@Override
		public Component getListCellRendererComponent(JList<? extends Device> list, Device d, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (d.isDefault())
				setText(d.getName() + "@" + d.getAddress() + " (default)");
			else
				setText(d.getName() + "@" + d.getAddress());
			if (isSelected) {
				setBackground(Color.blue);
				setForeground(Color.white);
			} else {
				setBackground(Color.white);
				setForeground(list.getForeground());
			}
			setOpaque(true);
			return this;
		}
	}

	/**
	 * Create the dialog.
	 */
	public OptionsDialog() {
		Settings.getInstance().addListener(this);
		setBounds(100, 100, 480, 590);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Options");
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
		setIconImage(icon.getImage());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Notification timeout");
		lblNewLabel.setBounds(24, 10, 249, 15);
		contentPanel.add(lblNewLabel);

		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(1000);
		formatter.setMaximum(60000);
		formatter.setAllowsInvalid(true);
		formatter.setCommitsOnValidEdit(false);
		timeoutField = new JFormattedTextField(formatter);
		timeoutField.setBounds(24, 25, 303, 25);
		contentPanel.add(timeoutField);
		timeoutField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Pc name");
		lblNewLabel_1.setBounds(24, 55, 70, 15);
		contentPanel.add(lblNewLabel_1);

		pcNameField = new JTextField();
		pcNameField.setBounds(24, 71, 303, 25);
		contentPanel.add(pcNameField);
		pcNameField.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Icon");
		lblNewLabel_2.setBounds(24, 100, 70, 15);
		contentPanel.add(lblNewLabel_2);

		iconField = new JTextField();
		iconField.setBounds(24, 115, 303, 25);
		contentPanel.add(iconField);
		iconField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Clipboard command prefix");
		lblNewLabel_3.setBounds(24, 145, 303, 15);
		contentPanel.add(lblNewLabel_3);

		clipboardPrefixField = new JTextField();
		clipboardPrefixField.setBounds(24, 160, 303, 25);
		contentPanel.add(clipboardPrefixField);
		clipboardPrefixField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Download path");
		lblNewLabel_4.setBounds(24, 190, 303, 15);
		contentPanel.add(lblNewLabel_4);

		downloadPathField = new JTextField();
		downloadPathField.setBounds(24, 205, 303, 25);
		contentPanel.add(downloadPathField);
		downloadPathField.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Devices");
		lblNewLabel_5.setBounds(24, 235, 70, 15);
		contentPanel.add(lblNewLabel_5);

		deviceList = new JList<>();
		deviceList.setCellRenderer(new DeviceRenderer());
		listModel = new DefaultListModel<>();
		deviceList.setModel(listModel);
		deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		deviceList.setLayoutOrientation(JList.VERTICAL_WRAP);
		deviceList.setBounds(24, 250, 303, 115);
		contentPanel.add(deviceList);

		JButton addDeviceBtn = new JButton("Add device");
		addDeviceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DeviceDialog dialog = new DeviceDialog();
				Device device = dialog.showDialog();
				if (device != null) {
					listModel.addElement(device);
				}
			}
		});
		addDeviceBtn.setFont(new Font("Dialog", Font.BOLD, 10));
		addDeviceBtn.setBounds(339, 250, 129, 25);
		contentPanel.add(addDeviceBtn);

		JButton removeDeviceBtn = new JButton("Remove device");
		removeDeviceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = deviceList.getSelectedIndex();
				listModel.remove(index);
			}
		});
		removeDeviceBtn.setFont(new Font("Dialog", Font.BOLD, 10));
		removeDeviceBtn.setBounds(339, 280, 129, 25);
		contentPanel.add(removeDeviceBtn);

		final JButton selectPathBtn = new JButton("Select path");
		selectPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(selectPathBtn, "OK");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					downloadPathField.setText(file.getAbsolutePath());
				}
			}
		});
		selectPathBtn.setBounds(339, 205, 129, 25);
		contentPanel.add(selectPathBtn);

		final JButton selectIconBtn = new JButton("Select icon");
		selectIconBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
				fc.setFileFilter(imageFilter);
				fc.setAcceptAllFileFilterUsed(false);
				int returnVal = fc.showDialog(selectIconBtn, "OK");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					iconField.setText(file.getAbsolutePath());
				}
			}
		});
		selectIconBtn.setBounds(339, 115, 129, 25);
		contentPanel.add(selectIconBtn);

		JButton btnNewButton = new JButton("Set as default");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setAsDefault(deviceList.getSelectedValue());
			}
		});
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 11));
		btnNewButton.setBounds(339, 309, 129, 25);
		contentPanel.add(btnNewButton);

		chckbxNewCheckBox = new JCheckBox("Sync clipboard with default device");
		chckbxNewCheckBox.setBounds(24, 373, 303, 23);
		contentPanel.add(chckbxNewCheckBox);

		filterList = new JList<>();
		filterModel = new DefaultListModel<>();
		filterList.setModel(filterModel);
		filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filterList.setLayoutOrientation(JList.VERTICAL_WRAP);
		filterList.setBounds(24, 420, 303, 85);
		contentPanel.add(filterList);

		JLabel lblFilters = new JLabel("Notification filters");
		lblFilters.setBounds(24, 404, 152, 15);
		contentPanel.add(lblFilters);

		JButton btnAddFilter = new JButton("Add filter");
		btnAddFilter.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAddFilter.setBounds(339, 420, 129, 25);
		btnAddFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilterDialog dialog = new FilterDialog();
				NotificationFilter filter = dialog.showDialog();
				if (filter != null) {
					filterModel.addElement(filter);
				}
			}
		});
		contentPanel.add(btnAddFilter);

		JButton btnRemoveFilter = new JButton("Remove filter");
		btnRemoveFilter.setFont(new Font("Dialog", Font.BOLD, 10));
		btnRemoveFilter.setBounds(339, 479, 129, 25);
		btnRemoveFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = filterList.getSelectedIndex();
				filterModel.remove(index);
			}
		});
		contentPanel.add(btnRemoveFilter);

		JButton probeButton = new JButton("Probe");
		probeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ProbeClient.send();
				} catch (IOException e1) {
					showError("Error during probe: " + e1.getMessage());
				}
			}
		});
		probeButton.setFont(new Font("Dialog", Font.BOLD, 11));
		probeButton.setBounds(339, 341, 129, 25);
		contentPanel.add(probeButton);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (onExit()) {
							setVisible(false);
							dispose();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						Settings.getInstance().removeListener(OptionsDialog.this);
						setVisible(false);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		init();
	}

	private void init() {
		Settings s = Settings.getInstance();

		chckbxNewCheckBox.setSelected(s.syncClipboard());
		timeoutField.setText(Integer.toString(s.getTimeout()));
		downloadPathField.setText(s.getDownloadPath());
		iconField.setText(s.getIconPath());
		clipboardPrefixField.setText(s.getClipboardPrefix());
		pcNameField.setText(s.getName());

		List<Device> list = s.getDevices();
		for (Device d : list) {
			listModel.addElement(d);
		}
		List<NotificationFilter> filters = s.getFilters();
		for (NotificationFilter f : filters) {
			filterModel.addElement(f);
		}
	}

	private void setAsDefault(Device d) {
		Settings s = Settings.getInstance();
		List<Device> devices = s.getDevices();
		listModel.clear();
		for (Device dev : devices) {
			if (dev.equals(d))
				dev.setDefault(true);
			else
				dev.setDefault(false);
			listModel.addElement(dev);
		}
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, "Timeout parameter must be a number between 1000 and 60000 milliseconds",
				getTitle(), JOptionPane.ERROR_MESSAGE);
	}

	private boolean onExit() {
		int time = 0;
		Settings s = Settings.getInstance();

		s.removeListener(this);
		try {
			time = Integer.parseInt(timeoutField.getText());
		} catch (NumberFormatException e) {
			showError("Timeout parameter must be a number between 1000 and 60000 milliseconds");
			return false;
		}

		File f = new File(downloadPathField.getText());
		if (!f.exists() || !f.canWrite()) {
			showError("Download path cannot be empty and it must be writeable");
			return false;
		}

		f = new File(iconField.getText());
		if (!f.exists() || !f.canRead()) {
			showError("Icon cannot be empty and it must be readable");
			return false;
		}

		int size = listModel.getSize();
		ArrayList<Device> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(listModel.getElementAt(i));
		}
		size = filterModel.getSize();
		ArrayList<NotificationFilter> filters = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			filters.add(filterModel.getElementAt(i));
		}
		s.setSyncClipboard(chckbxNewCheckBox.isSelected());
		s.setDevices(list);
		s.setFilters(filters);
		s.setClipboardPrefix(clipboardPrefixField.getText());
		s.setDownloadPath(downloadPathField.getText());
		s.setIconPath(iconField.getText());
		s.setName(pcNameField.getText());
		s.setTimeout(time);
		return true;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().startsWith(Settings.DEVICES)) {
			listModel.removeAllElements();
			List<Device> devices = Settings.getInstance().getDevices();
			for (Device d : devices) {
				listModel.addElement(d);
			}
		}
	}
}

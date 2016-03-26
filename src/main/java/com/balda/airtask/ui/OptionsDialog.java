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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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

public class OptionsDialog extends JDialog {

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
	private DefaultListModel<Device> listModel;
	
	private class DeviceRenderer extends JLabel implements ListCellRenderer<Device> {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -951398425178262012L;

		@Override
	    public Component getListCellRendererComponent(JList<? extends Device> list, Device d, int index,
	        boolean isSelected, boolean cellHasFocus) {
	        setText(d.getName()+"@"+d.getAddress());	         
	        return this;
	    }	     
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OptionsDialog dialog = new OptionsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public OptionsDialog() {
		setBounds(100, 100, 480, 360);
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
		timeoutField.setBounds(24, 25, 303, 19);
		contentPanel.add(timeoutField);
		timeoutField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Pc name");
		lblNewLabel_1.setBounds(24, 45, 70, 15);
		contentPanel.add(lblNewLabel_1);

		pcNameField = new JTextField();
		pcNameField.setBounds(24, 60, 303, 19);
		contentPanel.add(pcNameField);
		pcNameField.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Icon");
		lblNewLabel_2.setBounds(24, 80, 70, 15);
		contentPanel.add(lblNewLabel_2);

		iconField = new JTextField();
		iconField.setBounds(24, 95, 303, 19);
		contentPanel.add(iconField);
		iconField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Clipboard command prefix");
		lblNewLabel_3.setBounds(24, 115, 303, 15);
		contentPanel.add(lblNewLabel_3);

		clipboardPrefixField = new JTextField();
		clipboardPrefixField.setBounds(24, 130, 303, 19);
		contentPanel.add(clipboardPrefixField);
		clipboardPrefixField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Download path");
		lblNewLabel_4.setBounds(24, 150, 303, 15);
		contentPanel.add(lblNewLabel_4);

		downloadPathField = new JTextField();
		downloadPathField.setBounds(24, 165, 303, 19);
		contentPanel.add(downloadPathField);
		downloadPathField.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Devices");
		lblNewLabel_5.setBounds(24, 190, 70, 15);
		contentPanel.add(lblNewLabel_5);

		deviceList = new JList<>();
		deviceList.setCellRenderer(new DeviceRenderer());
		listModel = new DefaultListModel<>();
		deviceList.setModel(listModel);
		deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		deviceList.setLayoutOrientation(JList.VERTICAL_WRAP);
		deviceList.setBounds(24, 206, 303, 65);
		contentPanel.add(deviceList);

		JButton addDeviceBtn = new JButton("Add device");
		addDeviceBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeviceDialog dialog = new DeviceDialog();
				Device device = dialog.showDialog();
				if (device != null) {
					listModel.addElement(device);
				}
			}
		});
		addDeviceBtn.setFont(new Font("Dialog", Font.BOLD, 10));
		addDeviceBtn.setBounds(339, 206, 129, 25);
		contentPanel.add(addDeviceBtn);

		JButton removeDeviceBtn = new JButton("Remove device");
		removeDeviceBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = deviceList.getSelectedIndex();
				listModel.remove(index);
			}
		});
		removeDeviceBtn.setFont(new Font("Dialog", Font.BOLD, 10));
		removeDeviceBtn.setBounds(339, 246, 129, 25);
		contentPanel.add(removeDeviceBtn);

		final JButton selectPathBtn = new JButton("Select path");
		selectPathBtn.addActionListener(new ActionListener() {
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
		selectPathBtn.setBounds(339, 165, 129, 19);
		contentPanel.add(selectPathBtn);

		final JButton selectIconBtn = new JButton("Select icon");
		selectIconBtn.addActionListener(new ActionListener() {
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
		selectIconBtn.setBounds(339, 95, 129, 19);
		contentPanel.add(selectIconBtn);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
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
					public void actionPerformed(ActionEvent arg0) {
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
		
		timeoutField.setText(Integer.toString(s.getTimeout()));
		downloadPathField.setText(s.getDownloadPath());
		iconField.setText(s.getIconPath());
		clipboardPrefixField.setText(s.getClipboardPrefix());
		pcNameField.setText(s.getName());

		List<Device> list = s.getDevices();
		for (Device d : list) {
			listModel.addElement(d);
		}
	}

	private boolean onExit() {
		int time = 0;
		Settings s = Settings.getInstance();		
		
		try {
			time = Integer.parseInt(timeoutField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Timeout parameter must be a number between 1000 and 60000 milliseconds");
			return false;
		}

		File f = new File(downloadPathField.getText());
		if (!f.exists() || !f.canWrite()) {
			JOptionPane.showMessageDialog(this, "Download path cannot be empty and it must be writeable");
			return false;
		}

		f = new File(iconField.getText());
		if (!f.exists() || !f.canRead()) {
			JOptionPane.showMessageDialog(this, "Icon cannot be empty and it must be readable");
			return false;
		}

		int size = listModel.getSize();
		ArrayList<Device> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(listModel.getElementAt(i));
		}
		s.setDevices(list);
		s.setClipboardPrefix(clipboardPrefixField.getText());
		s.setDownloadPath(downloadPathField.getText());
		s.setIconPath(iconField.getText());
		s.setName(pcNameField.getText());
		s.setTimeout(time);
		return true;
	}
}

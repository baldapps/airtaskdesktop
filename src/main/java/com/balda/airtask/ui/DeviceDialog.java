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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.balda.airtask.Device;

public class DeviceDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5473142206344281065L;
	private final JPanel contentPanel = new JPanel();
	private JTextField addressField;
	private JTextField deviceNameField;
	private Device device;
	private static final Pattern PATTERN = Pattern
			.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static boolean validate(final String ip) {
		return PATTERN.matcher(ip).matches();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DeviceDialog dialog = new DeviceDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DeviceDialog() {
		setBounds(100, 100, 330, 130);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add device");
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
		setIconImage(icon.getImage());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Name");
			lblNewLabel.setBounds(12, 12, 70, 15);
			contentPanel.add(lblNewLabel);
		}
		{
			addressField = new JTextField();
			addressField.setBounds(105, 37, 208, 25);
			contentPanel.add(addressField);
			addressField.setColumns(10);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Address");
			lblNewLabel_1.setBounds(12, 39, 70, 15);
			contentPanel.add(lblNewLabel_1);
		}
		{
			deviceNameField = new JTextField();
			deviceNameField.setColumns(10);
			deviceNameField.setBounds(105, 10, 208, 25);
			contentPanel.add(deviceNameField);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (validate(addressField.getText())) {
							device = new Device(deviceNameField.getText(), addressField.getText());
							setVisible(false);
							dispose();
						} else {
							JOptionPane.showMessageDialog(DeviceDialog.this, "Invalid IP address");
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
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public Device showDialog() {
		setVisible(true);
		return device;
	}

}

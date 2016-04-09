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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.balda.airtask.Device;
import com.balda.airtask.Settings;

public class FilterDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5298846643457318226L;
	private final JPanel contentPanel = new JPanel();
	private JTextField regexField;
	private PriorityComboBoxModel comboModel;
	private JComboBox<Device> targetDeviceText;
	private NotificationFilter filter;

	/**
	 * Create the dialog.
	 */
	public FilterDialog() {
		setBounds(100, 100, 450, 150);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add filter");
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
		setIconImage(icon.getImage());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Device");
			lblNewLabel.setBounds(12, 12, 70, 15);
			contentPanel.add(lblNewLabel);
		}

		targetDeviceText = new JComboBox<>();
		List<Device> devices = Settings.getInstance().getDevices();
		ArrayList<Device> newDevices = new ArrayList<>(devices);
		newDevices.add(new Device(NotificationFilter.ALL, "", false));
		comboModel = new PriorityComboBoxModel(newDevices);
		targetDeviceText.setModel(comboModel);
		targetDeviceText.setBounds(155, 7, 279, 24);
		contentPanel.add(targetDeviceText);

		JLabel lblNewLabel_1 = new JLabel("Regular expression");
		lblNewLabel_1.setBounds(12, 46, 154, 15);
		contentPanel.add(lblNewLabel_1);

		regexField = new JTextField();
		regexField.setBounds(155, 43, 279, 25);
		contentPanel.add(regexField);
		regexField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Pattern.compile(regexField.getText());
						} catch (PatternSyntaxException exception) {
							JOptionPane.showMessageDialog(FilterDialog.this, "Invalid regular expression");
							return;
						}
						filter = new NotificationFilter(((Device) targetDeviceText.getSelectedItem()).getName(),
								regexField.getText());
						setVisible(false);
						dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	public NotificationFilter showDialog() {
		setVisible(true);
		return filter;
	}
}

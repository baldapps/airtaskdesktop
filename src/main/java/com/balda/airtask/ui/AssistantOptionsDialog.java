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
import java.text.NumberFormat;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.balda.airtask.settings.AssistantSettings;
import com.balda.airtask.settings.Settings;

public class AssistantOptionsDialog extends JDialog implements PreferenceChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7524032380144298523L;
	private final JPanel contentPanel = new JPanel();
	private JTextField clientId;
	private JTextField clientSecret;
	private JSlider slider;
	private JCheckBox chckbxAlwaysOn;

	/**
	 * Create the dialog.
	 */
	public AssistantOptionsDialog() {
		Settings.getInstance().addListener(this);
		setBounds(100, 100, 520, 281);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Assistant options");
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
		setIconImage(icon.getImage());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Client Id");
		lblNewLabel.setBounds(24, 10, 120, 15);
		contentPanel.add(lblNewLabel);

		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		clientId = new JTextField();
		clientId.setBounds(24, 25, 480, 30);
		contentPanel.add(clientId);
		clientId.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Client secret");
		lblNewLabel_1.setBounds(24, 64, 125, 15);
		contentPanel.add(lblNewLabel_1);

		clientSecret = new JTextField();
		clientSecret.setBounds(24, 79, 480, 30);
		contentPanel.add(clientSecret);
		clientSecret.setColumns(10);

		slider = new JSlider(0, 100);
		slider.setBounds(24, 138, 200, 16);
		contentPanel.add(slider);

		JLabel lblAssistantVolume = new JLabel("Assistant volume");
		lblAssistantVolume.setBounds(24, 121, 125, 15);
		contentPanel.add(lblAssistantVolume);

		chckbxAlwaysOn = new JCheckBox("Always on");
		chckbxAlwaysOn.setBounds(24, 162, 129, 23);
		contentPanel.add(chckbxAlwaysOn);
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
						Settings.getInstance().removeListener(AssistantOptionsDialog.this);
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
		AssistantSettings s = AssistantSettings.getInstance();
		s.addListener(this);
		clientId.setText(s.getClientId());
		clientSecret.setText(s.getClientSecret());
		slider.setValue(s.getVolume());
		chckbxAlwaysOn.setSelected(s.isAlwaysOn());
	}

	private boolean onExit() {
		AssistantSettings s = AssistantSettings.getInstance();
		s.removeListener(this);
		s.setClientId(clientId.getText());
		s.setClientSecret(clientSecret.getText());
		s.setVolume(slider.getValue());
		s.setAlwaysOn(chckbxAlwaysOn.isSelected());
		return true;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().startsWith(AssistantSettings.CLIENT_ID)) {
			clientId.setText(AssistantSettings.getInstance().getClientId());
		} else if (evt.getKey().startsWith(AssistantSettings.CLIENT_SECRET)) {
			clientSecret.setText(AssistantSettings.getInstance().getClientSecret());
		} else if (evt.getKey().startsWith(AssistantSettings.ASSISTANT_VOLUME)) {
			slider.setValue(AssistantSettings.getInstance().getVolume());
		} else if (evt.getKey().startsWith(AssistantSettings.ALWAYS_ON)) {
			chckbxAlwaysOn.setSelected(AssistantSettings.getInstance().isAlwaysOn());
		}
	}
}

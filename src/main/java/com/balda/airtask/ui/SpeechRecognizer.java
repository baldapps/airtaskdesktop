/*
 * Copyright 2015-2017 Marco Stornelli <playappassistance@gmail.com>
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.balda.airtask.assistant.api.AssistantManager;
import com.balda.airtask.assistant.api.VoiceTransaction;
import com.balda.airtask.assistant.api.VoiceTransactionListener;

public class SpeechRecognizer extends JFrame implements VoiceTransactionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5481918067116503338L;
	private JPanel contentPane;
	private VoiceTransaction currentTransaction;
	private JLabel micLabel;
	private JLabel talkStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				AssistantManager.getInstance().init();
				try {
					SpeechRecognizer frame = new SpeechRecognizer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SpeechRecognizer() {
		setResizable(false);
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 458, 282);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(236, 239, 241));
		contentPane.setBorder(new EmptyBorder(5, 85, 5, 85));
		setContentPane(contentPane);

		talkStatus = new JLabel("Talk now");
		talkStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
		talkStatus.setForeground(new Color(84, 110, 122));
		talkStatus.setHorizontalAlignment(SwingConstants.CENTER);

		micLabel = new JLabel("");
		micLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		micLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				micClicked();
			}
		});
		micLabel.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("micOff.png"))));

		JLabel lblNewLabel_1 = new JLabel("Google");
		lblNewLabel_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_1.setForeground(new Color(182, 186, 188));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(talkStatus);
		contentPane.add(micLabel);
		contentPane.add(lblNewLabel_1);
		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
	}

	private void micClicked() {
		if (currentTransaction == null || currentTransaction.isTerminated())
			startVoiceInteraction();
	}

	private void startVoiceInteraction() {
		try {
			currentTransaction = AssistantManager.getInstance().createTransaction(this);
			if (currentTransaction == null) {
				JOptionPane.showMessageDialog(this, "Authentication error", "AirTask", JOptionPane.ERROR_MESSAGE);
			} else
				currentTransaction.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error: " + e.getLocalizedMessage(), "AirTask",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	@Override
	public void onStart() {
		talkStatus.setText("Talk now");
		micLabel.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("micOn.png"))));
	}

	@Override
	public void onClose() {
		talkStatus.setText("Press mic to talk again");
		currentTransaction = null;
		micLabel.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("micOff.png"))));
	}

	@Override
	public void onRestart() {
		talkStatus.setText("Talk now");
		micLabel.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("micOn.png"))));
	}

	@Override
	public void onError(String error) {
		talkStatus.setText("Press mic to talk");
		micLabel.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("micOff.png"))));
		JOptionPane.showMessageDialog(this, "Error: " + error, "AirTask", JOptionPane.ERROR_MESSAGE);
		currentTransaction = null;
	}

	@Override
	public void onUserSpoken() {
		talkStatus.setText("Wait for reply...");
		micLabel.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("micOff.png"))));
	}

	@Override
	public void windowOpened(WindowEvent e) {
		startVoiceInteraction();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (currentTransaction != null)
			currentTransaction.stop();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}

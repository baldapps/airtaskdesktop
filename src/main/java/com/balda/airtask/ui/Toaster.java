/*
 * Copyright 2009 Daniele Piras
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
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class Toaster {
	// Width of the toster
	private int toasterWidth = 300;

	// Height of the toster
	private int toasterHeight = 128;

	// Step for the toaster
	private int step = 20;

	// Step time
	private int stepTime = 20;

	// Show time
	private long displayTime = 3000L;

	// Current number of toaster...
	private int currentNumberOfToaster = 0;

	// Max number of toasters for the sceen
	private final int maxToasterInSceen;

	// Font used to display message
	private Font font;

	// Color for border
	private Color borderColor;

	// Color for toaster
	private Color toasterColor;

	// Set message color
	private Color messageColor;

	// Set the margin
	int margin;

	boolean autoDismiss = true;

	// Flag that indicate if use alwaysOnTop or not.
	// method always on top start only SINCE JDK 5 !
	boolean useAlwaysOnTop = true;

	private final int screenHeight;
	private final int screenHeightY;
	private final int screenWidth;

	final ReentrantLock _lock = new ReentrantLock();
	final Condition _waitForDismiss;

	/**
	 * Constructor to initialized toaster component...
	 * 
	 * @author daniele piras
	 * 
	 */
	public Toaster() {
		// Set default font...
		font = new Font("Arial", Font.BOLD, 12);
		// Border color
		borderColor = new Color(245, 153, 15);
		toasterColor = Color.WHITE;
		messageColor = Color.BLACK;
		useAlwaysOnTop = true;
		// Verify AlwaysOnTop Flag...
		try {
			JFrame.class.getMethod("setAlwaysOnTop", new Class[] { Boolean.class });
		} catch (Exception e) {
			useAlwaysOnTop = false;
		}

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle screenRect = ge.getMaximumWindowBounds();

		screenHeight = (int) screenRect.height;
		screenHeightY = screenRect.y;
		screenWidth = (int) screenRect.width;
		maxToasterInSceen = screenHeight / toasterHeight;

		_waitForDismiss = _lock.newCondition();
	}

	/**
	 * 
	 * @param <T>
	 * @param toaster
	 */
	public final void dismiss() {

		_lock.lock();
		try {
			_waitForDismiss.signal();
		} finally {
			_lock.unlock();
		}

	}

	/**
	 * Class that rappresent a single toaster
	 * 
	 * @author daniele piras
	 * 
	 */
	class SingleToaster extends javax.swing.JFrame {
		private static final long serialVersionUID = 1L;

		// Label to store Icon
		private JLabel iconLabel = new JLabel();

		private JLabel fromLabel = new JLabel();

		// Text area for the message
		private JTextArea message = new JTextArea();

		/***
		 * Simple costructor that initialized components...
		 */
		public SingleToaster() {
			initComponents();
		}

		/***
		 * Function to initialized components
		 */
		private void initComponents() {
			setType(javax.swing.JFrame.Type.POPUP);

			setSize(toasterWidth, toasterHeight);

			message.setFont(getToasterMessageFont());

			JPanel externalPanel = new JPanel(new BorderLayout(1, 1));
			JPanel innerPanel = new JPanel(new BorderLayout(getMargin(), getMargin()));

			externalPanel.setBackground(getBorderColor());
			innerPanel.setBackground(getToasterColor());

			message.setBackground(getToasterColor());
			message.setMargin(new Insets(2, 2, 2, 2));
			message.setLineWrap(true);
			message.setWrapStyleWord(true);

			EtchedBorder etchedBorder = (EtchedBorder) BorderFactory.createEtchedBorder();

			externalPanel.setBorder(etchedBorder);
			externalPanel.add(innerPanel);

			message.setForeground(getMessageColor());

			fromLabel.setHorizontalAlignment(SwingConstants.CENTER);
			innerPanel.add(fromLabel, BorderLayout.PAGE_START);
			innerPanel.add(iconLabel, BorderLayout.WEST);
			innerPanel.add(message, BorderLayout.CENTER);

			getContentPane().add(externalPanel);
		}

	}

	/***
	 * Class that manage the animation
	 */
	public class AnimationThread extends Thread {
		javax.swing.JFrame toaster;
		private int startY;
		private int stopY;
		private int posX;

		public AnimationThread(javax.swing.JFrame toaster, int x, int fromY, int toY) {
			this.toaster = toaster;
			startY = fromY;
			stopY = toY;
			posX = x;
		}

		/**
		 * Animate vertically the toaster. The toaster could be moved from
		 * bottom to upper or to upper to bottom
		 * 
		 * @param posx
		 * @param fromy
		 * @param toy
		 * @throws InterruptedException
		 */
		public void animateVertically(int posx, int fromY, int toY) throws InterruptedException {

			toaster.setLocation(posx, fromY);
			if (toY < fromY) {
				for (int i = fromY; i > toY; i -= step) {
					toaster.setLocation(posx, i);
					Thread.sleep(stepTime);
				}
			} else {
				for (int i = fromY; i < toY; i += step) {
					toaster.setLocation(posx, i);
					Thread.sleep(stepTime);
				}
			}
			toaster.setLocation(posx, toY);
		}

		@Override
		public void run() {
			try {
				animateVertically(posX, startY, stopY);

				if (isAutoDismiss()) {

					if (getDisplayTime() > 0) {
						Thread.sleep(displayTime);
					}
				} else {

					_lock.lock();
					try {

						if (getDisplayTime() > 0) {
							_waitForDismiss.await(getDisplayTime(), TimeUnit.MILLISECONDS);
						} else {
							_waitForDismiss.await();
						}
					} finally {
						_lock.unlock();
					}
				}

				animateVertically(posX, stopY, startY);

				toaster.setVisible(false);
				toaster.dispose();

				synchronized (Toaster.this) {
					currentNumberOfToaster--;
				}

			} catch (Exception e) {

				System.out.println("error rendering toaster " + e.getMessage());
			}
		}

	}

	/**
	 * Show a toaster with the specified message and the associated icon.
	 */
	public void showToaster(Icon icon, String title, String msg) {
		SingleToaster singleToaster = new SingleToaster();
		if (icon != null) {
			singleToaster.iconLabel.setIcon(icon);
		}
		if (title != null)
			singleToaster.fromLabel.setText(title);
		singleToaster.message.setText(msg);
		animate(singleToaster);
	}

	/**
	 * Show a toaster with the specified message.
	 */
	public final void showToaster(String msg) {
		showToaster(null, null, msg);
	}

	/**
	 * Show a toaster with the specified message.
	 */
	public final void showToaster(String title, String msg) {
		showToaster(null, title, msg);
	}

	/**
	 * 
	 * @param <T>
	 * @param toaster
	 * @return
	 */
	protected <T extends javax.swing.JFrame> AnimationThread animate(T toaster) {
		boolean animateFromBottom = true;

		int startYPosition;
		int stopYPosition;

		if (screenHeightY > 0) {
			animateFromBottom = false; // Animate from top!
		}

		int posx = (int) screenWidth - toasterWidth - 1;

		toaster.setLocation(posx, screenHeight);
		toaster.setVisible(true);
		if (useAlwaysOnTop) {
			toaster.setAlwaysOnTop(true);
		}

		synchronized (this) {
			if (animateFromBottom) {
				startYPosition = screenHeight;
				stopYPosition = startYPosition - toasterHeight - 1;
				if (currentNumberOfToaster > 0) {
					stopYPosition = stopYPosition - (currentNumberOfToaster % maxToasterInSceen * toasterHeight);
				}
			} else {
				startYPosition = screenHeightY - toasterHeight;
				stopYPosition = screenHeightY;

				if (currentNumberOfToaster > 0) {
					stopYPosition = stopYPosition + (currentNumberOfToaster % maxToasterInSceen * toasterHeight);
				}
			}
			currentNumberOfToaster++;
		}

		final AnimationThread anim = new AnimationThread(toaster, posx, startYPosition, stopYPosition);
		anim.start();
		return anim;
	}

	public boolean isAutoDismiss() {
		return autoDismiss;
	}

	public void setAutoDismiss(boolean autoDismiss) {
		this.autoDismiss = autoDismiss;
	}

	/**
	 * @return Returns the font
	 */
	public Font getToasterMessageFont() {
		return font;
	}

	/**
	 * Set the font for the message
	 */
	public Toaster setToasterMessageFont(Font f) {
		font = f;
		return this;
	}

	/**
	 * @return Returns the borderColor.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor
	 *            The borderColor to set.
	 */
	public Toaster setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	/**
	 * @return Returns the displayTime.
	 */
	public long getDisplayTime() {
		return displayTime;
	}

	/**
	 * @param displayTime
	 *            The displayTime to set.
	 */
	public Toaster setDisplayTime(long displayTime) {
		this.displayTime = displayTime;
		return this;

	}

	/**
	 * @return Returns the margin.
	 */
	public int getMargin() {
		return margin;
	}

	/**
	 * @param margin
	 *            The margin to set.
	 */
	public Toaster setMargin(int margin) {
		this.margin = margin;
		return this;
	}

	/**
	 * @return Returns the messageColor.
	 */
	public Color getMessageColor() {
		return messageColor;
	}

	/**
	 * @param messageColor
	 *            The messageColor to set.
	 */
	public Toaster setMessageColor(Color messageColor) {
		this.messageColor = messageColor;
		return this;
	}

	/**
	 * @return Returns the step.
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step
	 *            The step to set.
	 */
	public Toaster setStep(int step) {
		this.step = step;
		return this;
	}

	/**
	 * @return Returns the stepTime in mills.
	 */
	public int getStepTime() {
		return stepTime;
	}

	/**
	 * @param stepTime
	 *            The stepTime to set.
	 */
	public Toaster setStepTime(int stepTime) {
		this.stepTime = stepTime;
		return this;
	}

	/**
	 * @return Returns the toasterColor.
	 */
	public Color getToasterColor() {
		return toasterColor;
	}

	/**
	 * @param toasterColor
	 *            The toasterColor to set.
	 */
	public Toaster setToasterColor(Color toasterColor) {
		this.toasterColor = toasterColor;
		return this;
	}

	/**
	 * @return Returns the toasterHeight.
	 */
	public int getToasterHeight() {
		return toasterHeight;
	}

	/**
	 * @param toasterHeight
	 *            The toasterHeight to set.
	 */
	public Toaster setToasterHeight(int toasterHeight) {
		this.toasterHeight = toasterHeight;
		return this;
	}

	/**
	 * @return Returns the toasterWidth.
	 */
	public int getToasterWidth() {
		return toasterWidth;
	}

	/**
	 * @param toasterWidth
	 *            The toasterWidth to set.
	 */
	public Toaster setToasterWidth(int toasterWidth) {
		this.toasterWidth = toasterWidth;
		return this;
	}
}
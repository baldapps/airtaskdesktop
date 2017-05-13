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

package com.balda.airtask.assistant.audio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingUtilities;

public class AudioRecorder extends AudioClient {

	private TargetDataLine microphone;
	private volatile boolean stopped = false;
	/**
	 * We use 70 dBFS as threshold here
	 */
	private static final double RMS_THRESHOLD = Math.pow(10, 70 / 20f);
	private static final long MAX_START_PAUSE = 5000; // 5.0s
	private static final long MAX_END_PAUSE = 3000; // 3.0s

	public AudioRecorder() {
	}

	public void getAudio(final AudioReceiver receiver) {
		// Reset the flag
		stopped = false;

		// Start a new thread to wait during listening
		Thread recorder = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final byte[] data = record();
					if (!stopped) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								receiver.onAudioReady(data);
							}
						});
					}
				} catch (final LineUnavailableException e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							receiver.onError(e.getLocalizedMessage());
						}
					});
				}
				stop();
			}
		});

		recorder.start();
	}

	private byte[] record() throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		// Checks if system supports the data line
		if (!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException();
		}

		microphone = (TargetDataLine) AudioSystem.getLine(info);
		microphone.open(format);
		microphone.start();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int numBytesRead;
		byte[] data = new byte[microphone.getBufferSize() / 5];
		short[] shorts = new short[data.length / 2];
		long startSilence = 0;
		boolean pause = false;
		long thresholdTime = MAX_START_PAUSE;

		// Begin audio capture.
		microphone.start();

		// Here, stopped is a global boolean set by another thread.
		while (!stopped) {
			// Read the next chunk of data from the TargetDataLine.
			numBytesRead = microphone.read(data, 0, data.length);
			ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

			// Save this chunk of data.
			byteArrayOutputStream.write(data, 0, numBytesRead);

			double rms = 0;
			for (int i = 0; i < shorts.length; i++) {
				rms += shorts[i] * shorts[i];
			}
			rms = Math.sqrt(rms / shorts.length);

			if (rms < RMS_THRESHOLD) {
				long now = System.currentTimeMillis();
				if (now - startSilence > thresholdTime && pause)
					break;
				if (!pause) {
					startSilence = now;
				}
				pause = true;
			} else {
				thresholdTime = MAX_END_PAUSE;
				pause = false;
			}
		}

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Stop the capture
	 */
	public void stop() {
		if (!stopped) {
			stopped = true;
			microphone.stop();
			microphone.close();
		}
	}
}

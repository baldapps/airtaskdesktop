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

package com.balda.airtask.assistant.api;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class HotwordDetector {

	private static final HotwordDetector instance = new HotwordDetector();
	private List<HotwordTrigger> listeners = new LinkedList<>();
	private LiveSpeechRecognizer recognizer;
	private boolean permanent;
	private volatile boolean running;

	private HotwordDetector() {
		Logger cmRootLogger = Logger.getLogger("default.config");
		cmRootLogger.setLevel(java.util.logging.Level.OFF);
		String conFile = System.getProperty("java.util.logging.config.file");
		if (conFile == null) {
			System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
		}

		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		configuration.setGrammarPath("resource:/com/balda/airtask/assistant/api/");
		configuration.setUseGrammar(true);
		configuration.setGrammarName("hotword");
		permanent = false;
		running = false;

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HotwordDetector getInstance() {
		return instance;
	}

	public void addListener(HotwordTrigger t) {
		if (t != null)
			listeners.add(t);
	}

	public void removeListener(HotwordTrigger t) {
		listeners.remove(t);
	}

	/**
	 * Start the service
	 * 
	 * @param p
	 *            True for always-on, false only on-demand
	 */
	public void start(boolean p) {
		if (!permanent)
			permanent = p;
		if (running)
			return;
		running = true;
		Thread t = new Thread() {
			@Override
			public void run() {
				recognizer.startRecognition(true);
				while (running) {
					SpeechResult result = recognizer.getResult();
					String text = result.getHypothesis();
					if (!text.isEmpty() && text.equalsIgnoreCase("ok google")) {
						for (final HotwordTrigger t : listeners) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									t.onHotwordSpoken();
								}
							});
						}
					}
				}
				recognizer.stopRecognition();
			}
		};
		t.start();
	}

	/**
	 * Stop the listening only if the service wasn't permanent
	 */
	public void stop() {
		if (!permanent) {
			running = false;
		}
	}

	/**
	 * Stop the service regardless if it was permanent or not
	 */
	public void kill() {
		running = false;
	}
}

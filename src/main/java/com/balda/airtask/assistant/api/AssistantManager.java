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

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import com.balda.airtask.assistant.audio.AudioPlayer;
import com.balda.airtask.assistant.audio.AudioRecorder;
import com.balda.airtask.assistant.authentication.AuthorizationHelper;
import com.balda.airtask.settings.AssistantSettings;
import com.balda.airtask.ui.SpeechRecognizer;
import com.balda.airtask.ui.WindowClosedListener;

public class AssistantManager implements PreferenceChangeListener, HotwordTrigger, WindowClosedListener {

	private AudioPlayer player;
	private AudioRecorder recorder;
	private AssistantClient client;
	private AuthorizationHelper helper;
	private boolean credChanged = false;
	public static final AssistantManager instance = new AssistantManager();
	private SpeechRecognizer speechRecognizer;

	private AssistantManager() {
		player = new AudioPlayer();
		recorder = new AudioRecorder();
		client = null;
	}

	public static AssistantManager getInstance() {
		return instance;
	}

	public void init() {
		AssistantSettings.getInstance().addListener(this);
		boolean authOk = false;
		try {
			helper = new AuthorizationHelper();
			authOk = helper.authorize();
			if (!authOk)
				authOk = helper.refreshIfNeeededd();
		} catch (Exception e) {
			authOk = false;
			return;
		}
		if (authOk && helper.getOAuthCredentials() != null)
			// Build the client (stub)
			client = new AssistantClient(helper.getOAuthCredentials());
		HotwordDetector.getInstance().addListener(this);
		if (AssistantSettings.getInstance().isAlwaysOn())
			HotwordDetector.getInstance().start();
	}

	public void shutdown() {
		HotwordDetector.getInstance().stop();
	}

	public void createSpeechReconizer() {
		if (speechRecognizer == null) {
			speechRecognizer = new SpeechRecognizer(this);
			speechRecognizer.setVisible(true);
		}
	}

	public VoiceTransaction createTransaction(VoiceTransactionListener l) throws Exception {
		if (helper == null)
			return null;
		boolean state = helper.refreshIfNeeededd();
		if (state) {
			if (credChanged || client == null) {
				client = new AssistantClient(helper.getOAuthCredentials());
			}
			return new VoiceTransaction(client, player, recorder, l);
		} else
			return null;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		switch (evt.getKey()) {
		case AssistantSettings.CLIENT_ID:
			credChanged = true;
			break;
		case AssistantSettings.CLIENT_SECRET:
			credChanged = true;
			break;
		case AssistantSettings.ALWAYS_ON:
			if (AssistantSettings.getInstance().isAlwaysOn())
				HotwordDetector.getInstance().start();
			else
				HotwordDetector.getInstance().stop();
		}
	}

	@Override
	public void onHotwordSpoken() {
		createSpeechReconizer();
	}

	@Override
	public void onClose() {
		speechRecognizer = null;
	}
}

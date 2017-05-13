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

import com.balda.airtask.assistant.audio.AudioPlayer;
import com.balda.airtask.assistant.audio.AudioRecorder;
import com.balda.airtask.assistant.authentication.AuthorizationHelper;

public class AssistantManager {

	private AudioPlayer player;
	private AudioRecorder recorder;
	private AssistantClient client;
	private AuthorizationHelper helper;
	public static final AssistantManager instance = new AssistantManager();

	private AssistantManager() {
		player = new AudioPlayer();
		recorder = new AudioRecorder();
		client = null;
	}

	public static AssistantManager getInstance() {
		return instance;
	}

	public void init() {
		try {
			helper = new AuthorizationHelper();
			helper.authorize();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// Build the client (stub)
		client = new AssistantClient(helper.getOAuthCredentials());
	}

	public VoiceTransaction createTransaction(VoiceTransactionListener l) throws Exception {
		if (helper == null)
			return null;
		boolean state = helper.refreshIfNeeededd();
		if (state)
			return new VoiceTransaction(client, player, recorder, l);
		else
			return null;
	}
}

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

import javax.sound.sampled.LineUnavailableException;

import com.balda.airtask.assistant.audio.AudioPlayer;
import com.balda.airtask.assistant.audio.AudioReceiver;
import com.balda.airtask.assistant.audio.AudioRecorder;

public class VoiceTransaction implements AudioReceiver {

	private AssistantClient client;
	private AudioPlayer player;
	private AudioRecorder recorder;

	enum TransactionState {
		IDLE, USER_INPUT, WAIT_FOR_REPLY, TERMINATED
	}

	private TransactionState state;
	private VoiceTransactionListener listener;

	VoiceTransaction(AssistantClient c, AudioPlayer p, AudioRecorder r, VoiceTransactionListener l) {
		client = c;
		player = p;
		recorder = r;
		state = TransactionState.IDLE;
		listener = l;
	}

	public void start() throws IllegalStateException {
		if (state != TransactionState.IDLE)
			throw new IllegalStateException("state is not idle");
		state = TransactionState.USER_INPUT;
		if (listener != null)
			listener.onStart();
		recorder.getAudio(this);
	}

	public void stop() {
		recorder.stop();
		state = TransactionState.TERMINATED;
	}

	@Override
	public void onAudioReady(byte[] audio) {
		switch (state) {
		default:
			break;
		case USER_INPUT:
			if (listener != null)
				listener.onUserSpoken();
			client.requestAssistant(this, audio);
			state = TransactionState.WAIT_FOR_REPLY;
			break;
		case WAIT_FOR_REPLY:
			if (audio.length > 0) {
				try {
					player.play(audio);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
			state = TransactionState.IDLE;
			break;
		}
	}

	void complete() {
		state = TransactionState.TERMINATED;
		if (listener != null)
			listener.onClose();
	}

	void restart() {
		state = TransactionState.USER_INPUT;
		if (listener != null)
			listener.onRestart();
		recorder.getAudio(this);
	}

	public boolean isTerminated() {
		return state == TransactionState.TERMINATED;
	}

	@Override
	public void onError(String error) {
		if (listener != null)
			listener.onError(error);
	}
}

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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;

import com.balda.airtask.script.ScriptFactory;
import com.balda.airtask.settings.AssistantSettings;
import com.google.api.client.auth.oauth2.Credential;
import com.google.assistant.embedded.v1alpha1.AudioInConfig;
import com.google.assistant.embedded.v1alpha1.AudioOutConfig;
import com.google.assistant.embedded.v1alpha1.ConverseConfig;
import com.google.assistant.embedded.v1alpha1.ConverseRequest;
import com.google.assistant.embedded.v1alpha1.ConverseResponse;
import com.google.assistant.embedded.v1alpha1.ConverseResult.MicrophoneMode;
import com.google.assistant.embedded.v1alpha1.ConverseState;
import com.google.assistant.embedded.v1alpha1.EmbeddedAssistantGrpc;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.protobuf.ByteString;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.StreamObserver;

public class AssistantClient implements StreamObserver<ConverseResponse> {

	private EmbeddedAssistantGrpc.EmbeddedAssistantStub embeddedAssistantStub;
	private ByteArrayOutputStream currentResponse = new ByteArrayOutputStream();
	// Endpoint for the assistant api (access with port 443)
	private static final String ASSISTANT_END_POINT = "embeddedassistant.googleapis.com";
	private static final int ASSISTANT_END_POINT_PORT = 443;
	// Audio sample rate, the encoding is Linear PCM (Linear 16)
	private static final int AUDIO_SMAPLE_RATE = 16000;
	// When we send audio, we split it in chunk, size of a chunk
	private static final int CHUNK_SIZE = 1024;
	// Only one request can be active until it completes
	private VoiceTransaction transaction;
	private MicrophoneMode currentMode = MicrophoneMode.CLOSE_MICROPHONE;

	/**
	 * Conversation state to continue a conversation if needed
	 *
	 * @see <a href=
	 *      "https://developers.google.com/assistant/sdk/reference/rpc/google.assistant.embedded.v1alpha1#google.assistant.embedded.v1alpha1.ConverseState">Google
	 *      documentation</a>
	 */
	private ByteString currentConversationState;

	public AssistantClient(Credential oAuthCredentials) {

		// Create a channel to the test service.
		ManagedChannel channel = ManagedChannelBuilder.forAddress(ASSISTANT_END_POINT, ASSISTANT_END_POINT_PORT)
				.build();

		// Create a stub with credential
		embeddedAssistantStub = EmbeddedAssistantGrpc.newStub(channel);

		updateCredentials(oAuthCredentials);
	}

	/**
	 * Get CallCredentials from OAuthCredentials
	 *
	 * @param oAuthCredentials
	 *            the credentials from the AuthenticationHelper
	 * @return the CallCredentials for the GRPC requests
	 */
	private CallCredentials getCallCredentials(Credential oAuthCredentials) {

		AccessToken accessToken = new AccessToken(oAuthCredentials.getAccessToken(),
				new Date(oAuthCredentials.getExpirationTimeMilliseconds()));

		OAuth2Credentials oAuth2Credentials = new OAuth2Credentials(accessToken);

		// Create an instance of {@link io.grpc.CallCredentials}
		return MoreCallCredentials.from(oAuth2Credentials);
	}

	/**
	 * Update the credentials used to request the api
	 *
	 * @param oAuthCredentials
	 *            the new credentials
	 */
	public void updateCredentials(Credential oAuthCredentials) {
		embeddedAssistantStub = embeddedAssistantStub.withCallCredentials(getCallCredentials(oAuthCredentials));
	}

	public void requestAssistant(VoiceTransaction t, byte[] request) throws IllegalArgumentException {
		if (transaction != null)
			throw new IllegalArgumentException();
		transaction = t;

		currentMode = MicrophoneMode.CLOSE_MICROPHONE;
		// Reset the byte array
		currentResponse = new ByteArrayOutputStream();

		// Send the config request
		StreamObserver<ConverseRequest> requester = embeddedAssistantStub.converse(this);
		requester.onNext(getConfigRequest());

		// Divide the audio request into chunks
		byte[][] chunks = divideArray(request, CHUNK_SIZE);

		// Send a request for each chunk
		for (byte[] chunk : chunks) {
			ByteString audioIn = ByteString.copyFrom(chunk);

			// Chunk of the request
			ConverseRequest converseRequest = ConverseRequest.newBuilder().setAudioIn(audioIn).build();

			requester.onNext(converseRequest);
		}

		// Mark the end of requests
		requester.onCompleted();
	}

	@Override
	public void onNext(ConverseResponse value) {
		try {
			if (value.getAudioOut() != null) {
				currentResponse.write(value.getAudioOut().getAudioData().toByteArray());
			}
			if (value.getResult() != null) {
				currentConversationState = value.getResult().getConversationState();
				if (value.getResult().getMicrophoneMode() != MicrophoneMode.MICROPHONE_MODE_UNSPECIFIED)
					currentMode = value.getResult().getMicrophoneMode();

				if (value.getResult().getSpokenRequestText() != null
						&& !value.getResult().getSpokenRequestText().isEmpty()) {
					ScriptFactory.getExecutor().onAssistantRequest(value.getResult().getSpokenRequestText());
				}

				if (value.getResult().getSpokenResponseText() != null
						&& !value.getResult().getSpokenResponseText().isEmpty()) {
					ScriptFactory.getExecutor().onAssistantReply(value.getResult().getSpokenResponseText());
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onError(Throwable t) {
		transaction.onError(t.getLocalizedMessage());
	}

	@Override
	public void onCompleted() {
		transaction.onAudioReady(currentResponse.toByteArray());
		if (currentMode == MicrophoneMode.DIALOG_FOLLOW_ON)
			transaction.restart();
		else {
			transaction.complete();
			transaction = null;
		}
	}

	/**
	 * Create the config message, this message must be send before the audio for
	 * each request
	 *
	 * @return the request to send
	 */
	private ConverseRequest getConfigRequest() {
		AudioInConfig audioInConfig = AudioInConfig.newBuilder().setEncoding(AudioInConfig.Encoding.LINEAR16)
				.setSampleRateHertz(AUDIO_SMAPLE_RATE).build();

		AudioOutConfig audioOutConfig = AudioOutConfig.newBuilder().setEncoding(AudioOutConfig.Encoding.LINEAR16)
				.setSampleRateHertz(AUDIO_SMAPLE_RATE).setVolumePercentage(AssistantSettings.getInstance().getVolume())
				.build();

		ConverseState converseState = null;
		if (currentConversationState != null) {
			converseState = ConverseState.newBuilder().setConversationState(currentConversationState).build();
		}

		ConverseConfig.Builder converseConfigBuilder = ConverseConfig.newBuilder().setAudioInConfig(audioInConfig)
				.setAudioOutConfig(audioOutConfig);

		if (converseState != null) {
			converseConfigBuilder.setConverseState(converseState);
		}

		return ConverseRequest.newBuilder().setConfig(converseConfigBuilder.build()).build();
	}

	/**
	 * Divide an array of byte in chunks of chunkSize bytes
	 *
	 * @param source
	 *            the source byte array
	 * @param chunkSize
	 *            the size of a chunk
	 * @return an array of chunks
	 * @see <a href=
	 *      "http://stackoverflow.com/questions/3405195/divide-array-into-smaller-parts">Divide
	 *      array into smaller parts</a>
	 */
	private byte[][] divideArray(byte[] source, int chunkSize) {
		byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunkSize)][chunkSize];

		int start = 0;

		for (int i = 0; i < ret.length; i++) {
			ret[i] = Arrays.copyOfRange(source, start, start + chunkSize);
			start += chunkSize;
		}

		return ret;
	}
}

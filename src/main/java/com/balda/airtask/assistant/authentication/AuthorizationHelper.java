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

package com.balda.airtask.assistant.authentication;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import com.balda.airtask.settings.AssistantSettings;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class AuthorizationHelper implements PreferenceChangeListener {

	/** Directory to store user credentials. */
	private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), "/.airtask/credentials");
	private static final String SCOPE = "https://www.googleapis.com/auth/assistant-sdk-prototype";

	private static final long MAX_DELAY_BEFORE_REFRESH = 300000; // 5m
	private String clientId;
	private String clientSecret;
	private boolean credChanged = false;

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to
	 * make it a single globally shared instance across your application.
	 */
	private static FileDataStoreFactory dataStoreFactory;

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private Credential credentials;

	public AuthorizationHelper() throws IOException, GeneralSecurityException {
		clientId = AssistantSettings.getInstance().getClientId();
		clientSecret = AssistantSettings.getInstance().getClientSecret();
		AssistantSettings.getInstance().addListener(this);
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	}

	/** Authorizes the installed application to access user's protected data. */
	public void authorize() throws Exception {
		credChanged = false;

		if (clientId.isEmpty() || clientSecret.isEmpty())
			return;

		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientId, clientSecret, Collections.singleton(SCOPE))
						.addRefreshListener(new DataStoreCredentialRefreshListener("user", dataStoreFactory))
						.setDataStoreFactory(dataStoreFactory).build();
		// authorize
		credentials = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	public Credential getOAuthCredentials() {
		return credentials;
	}

	/**
	 * Check if the token is expired
	 *
	 * @return true if the access token is ok, false otherwise
	 * @throws IOException
	 */
	public boolean refreshIfNeeededd() throws Exception {
		if (credChanged) {
			authorize();
			return false;
		}
		if (credentials == null)
			return false;
		if (credentials.getExpirationTimeMilliseconds() - System.currentTimeMillis() < MAX_DELAY_BEFORE_REFRESH) {
			return credentials.refreshToken();
		}
		return true;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		switch (evt.getKey()) {
		case AssistantSettings.CLIENT_ID:
			clientId = evt.getNewValue();
			credChanged = true;
			break;
		case AssistantSettings.CLIENT_SECRET:
			clientSecret = evt.getNewValue();
			credChanged = true;
			break;
		}
	}
}

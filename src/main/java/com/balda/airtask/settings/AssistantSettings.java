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

package com.balda.airtask.settings;

public class AssistantSettings extends SettingsProvider {

	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String CLIENT_AUTH_CODE = "client_auth_code";
	public static final String ASSISTANT_VOLUME = "assistant_volume";
	public static final String ALWAYS_ON = "always_on";

	public static final int DEF_ASSISTANT_VOLUME = 50;
	private static final AssistantSettings instance = new AssistantSettings();

	private AssistantSettings() {
	}

	public static AssistantSettings getInstance() {
		return instance;
	}

	public String getClientId() {
		return prefs.get(CLIENT_ID, "");
	}

	public String getClientSecret() {
		return prefs.get(CLIENT_SECRET, "");
	}

	public String getClientAuthCode() {
		return prefs.get(CLIENT_AUTH_CODE, "");
	}

	public int getVolume() {
		return prefs.getInt(ASSISTANT_VOLUME, DEF_ASSISTANT_VOLUME);
	}

	public boolean isAlwaysOn() {
		return prefs.getBoolean(ALWAYS_ON, false);
	}

	public void setClientId(String id) {
		prefs.put(CLIENT_ID, id);
	}

	public void setClientSecret(String secret) {
		prefs.put(CLIENT_SECRET, secret);
	}

	public void setClientAuthCode(String token) {
		prefs.put(CLIENT_AUTH_CODE, token);
	}

	public void setVolume(int v) {
		prefs.putInt(ASSISTANT_VOLUME, v);
	}

	public void setAlwaysOn(boolean s) {
		prefs.putBoolean(ALWAYS_ON, s);
	}
}

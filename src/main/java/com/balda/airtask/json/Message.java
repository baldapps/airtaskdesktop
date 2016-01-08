/*
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

package com.balda.airtask.json;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Message {

	@SerializedName("user_message")
	@Expose
	private String userMessage;
	@SerializedName("from_device")
	@Expose
	private String fromDevice;
	@SerializedName("target_device")
	@Expose
	private String targetDevice;

	/**
	 *
	 * @return The userMessage
	 */
	public String getUserMessage() {
		return userMessage;
	}

	/**
	 *
	 * @param userMessage
	 *            The user_message
	 */
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	/**
	 *
	 * @return The fromDevice
	 */
	public String getFromDevice() {
		return fromDevice;
	}

	/**
	 *
	 * @param fromDevice
	 *            The from_device
	 */
	public void setFromDevice(String fromDevice) {
		this.fromDevice = fromDevice;
	}

	/**
	 *
	 * @return The targetDevice
	 */
	public String getTargetDevice() {
		return targetDevice;
	}

	/**
	 *
	 * @param targetDevice
	 *            The target_device
	 */
	public void setTargetDevice(String targetDevice) {
		this.targetDevice = targetDevice;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (userMessage != null) {
			builder.append("userMessage ");
			builder.append(userMessage);
		}
		if (fromDevice != null) {
			builder.append(" fromDevice ");
			builder.append(fromDevice);
		}
		if (targetDevice != null) {
			builder.append(" targetDevice ");
			builder.append(targetDevice);
		}
		return builder.toString();
	}
}
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

import javax.sound.sampled.AudioFormat;

public abstract class AudioClient {
	protected AudioFormat format;
	public static final int SAMPLE_RATE = 16000;
	public static final int SAMPLE_RATE_BITS = 16;
	public static final int CHANNELS = 1;
	public static final boolean SIGNED = true;
	public static final boolean BIG_ENDIAN = false;

	public AudioClient() {
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_RATE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
	}
}

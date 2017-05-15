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

package com.balda.airtask.script;

import java.io.File;
import java.net.URLDecoder;
import java.security.CodeSource;

import com.balda.airtask.AirTask;

public abstract class ScriptExecutor {

	protected String root;

	ScriptExecutor() {
		try {
			root = getJarContainingFolder(AirTask.class);
		} catch (Exception e) {
			root = "";
		}
	}

	public static String getJarContainingFolder(Class<?> aclass) throws Exception {
		CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
		File jarFile;
		if (codeSource.getLocation() != null) {
			jarFile = new File(codeSource.getLocation().toURI());
		} else {
			String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
			String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
			jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
			jarFile = new File(jarFilePath);
		}
		return jarFile.getParentFile().getAbsolutePath();
	}

	public abstract void onFileReceived(String from, String path);

	public abstract void onMessageReceived(String from, String msg);

	public abstract void onAssistantRequest(String req, String reply);
}

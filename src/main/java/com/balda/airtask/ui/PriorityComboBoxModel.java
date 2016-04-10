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

package com.balda.airtask.ui;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import com.balda.airtask.Device;

class PriorityComboBoxModel extends DefaultComboBoxModel<Device> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2053746199826765385L;

	/*
	 * Create an empty model that will use the natural sort order of the item
	 */
	public PriorityComboBoxModel() {
		super();
	}

	/*
	 * Create a model with data and use the nature sort order of the items
	 */
	public PriorityComboBoxModel(List<Device> items) {
		super(items.toArray(new Device[items.size()]));
	}

	@Override
	public void removeAllElements() {
		super.removeAllElements();
		setSelectedItem(null);
	}

	@Override
	public void addElement(Device element) {
		insertElementAt(element, 0);
	}

	public void addAll(List<Device> elements) {
		if (elements == null)
			return;
		for (Device d : elements) {
			addElement(d);
		}
	}

	@Override
	public void insertElementAt(Device element, int index) {
		int size = getSize();

		// Determine where to insert element to keep model in sorted order
		for (index = 0; index < size; index++) {
			Device d = getElementAt(index);
			if (element.compareTo(d) > 0)
				break;
		}
		super.insertElementAt(element, index);
		if (element != null && (element.isDefault() || getSize() == 1)) {
			setSelectedItem(element);
		}
	}
}

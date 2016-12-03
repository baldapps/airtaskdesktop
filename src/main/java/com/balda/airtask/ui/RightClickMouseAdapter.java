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

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class RightClickMouseAdapter extends MouseAdapter {
	private Action cut;
	private Action copy;
	private Action paste;
	private Action undo;

	private String savedText = "";
	private RightClicItems lastItem;
	private JTextComponent textComponent;

	private JPopupMenu popup = new JPopupMenu();

	private enum RightClicItems {
		CUT, COPY, PASTE, UNDO
	};

	public RightClickMouseAdapter(JTextComponent cmp) {
		if (cmp == null)
			throw new IllegalArgumentException();

		textComponent = cmp;

		undo = new AbstractAction("Undo") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -476426320291310464L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				textComponent.setText(savedText);
				lastItem = RightClicItems.UNDO;
			}
		};

		popup.add(undo);
		popup.addSeparator();

		cut = new AbstractAction("Cut") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5729685809570007911L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				lastItem = RightClicItems.CUT;
				savedText = textComponent.getText();
				if (textComponent.getSelectedText() == null)
					textComponent.selectAll();
				textComponent.cut();
			}
		};

		popup.add(cut);

		copy = new AbstractAction("Copy") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6556502720745239798L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				lastItem = RightClicItems.COPY;
				if (textComponent.getSelectedText() == null)
					textComponent.selectAll();
				textComponent.copy();
			}
		};

		popup.add(copy);

		paste = new AbstractAction("Paste") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6363631200248243318L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				lastItem = RightClicItems.PASTE;
				savedText = textComponent.getText();
				textComponent.paste();
			}
		};

		popup.add(paste);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (e.getSource() != textComponent) {
				return;
			}
			textComponent.requestFocus();

			undo.setEnabled((lastItem == RightClicItems.CUT || lastItem == RightClicItems.PASTE));
			paste.setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
					.isDataFlavorSupported(DataFlavor.stringFlavor));
			cut.setEnabled(!(textComponent.getText() == null || textComponent.getText().equals("")));
			copy.setEnabled(!(textComponent.getText() == null || textComponent.getText().equals("")));

			int px = e.getX();
			if (px > 500) {
				px -= popup.getSize().width;
			}
			popup.show(e.getComponent(), px, e.getY() - popup.getSize().height);
		}
	}
}
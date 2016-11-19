/*
 * Copyright (C) 2016 Chan Chung Kwong <1m02math@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.chungkwong.sillytranslate.ui;
import java.awt.event.*;
import java.util.function.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ActionList<T> extends JList<T>{
	public ActionList(ListModel<T> model){
		super(model);
	}
	public void setAction(Consumer<T> consumer){
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"submit");
		getActionMap().put("submit",new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e){
				consumer.accept(getSelectedValue());
			}
		});
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==2)
					consumer.accept(getSelectedValue());
			}
		});

	}
}

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
package com.github.sillytranslate;
import com.github.sillytranslate.lex.*;
import com.github.sillytranslate.ui.*;
import java.awt.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DictionaryChooser extends JPanel{
	private final JFileChooser fileChooser=new JFileChooser();
	private final IntegratedDictionary dict=new IntegratedDictionary();
	public DictionaryChooser(){
		super(new CardLayout());
		DefaultListModel<NavigableDictionary> dicts=new DefaultListModel<>();
		dicts.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e){
				for(int i=e.getIndex0();i<=e.getIndex1();i++)
					dict.addDictionary(dicts.getElementAt(i),i);
			}
			@Override
			public void intervalRemoved(ListDataEvent e){
				for(int i=e.getIndex1();i>=e.getIndex0();i--)
					dict.removeDictionary(i);
			}
			@Override
			public void contentsChanged(ListDataEvent e){
				intervalRemoved(e);
				intervalAdded(e);
			}
		});
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		add(new EditableList(dicts,this::loadDictionary),"");
	}
	private NavigableDictionary loadDictionary(){
		try{
			fileChooser.showOpenDialog(this);
			return new StardictDictionary(fileChooser.getSelectedFile());
		}catch(IOException ex){
			Logger.getLogger(DictionaryViewer.class.getName()).log(Level.SEVERE,null,ex);
			return null;
		}
	}
	public IntegratedDictionary getDictionary(){
		return dict;
	}
}

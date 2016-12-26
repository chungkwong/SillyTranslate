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
package com.github.chungkwong.sillytranslate;
import com.github.chungkwong.sillytranslate.lex.*;
import com.github.chungkwong.sillytranslate.ui.*;
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
	private static final File BASE=new File(System.getProperty("user.home"),".SillyTranslate");
	private final JFileChooser fileChooser=new JFileChooser();
	private final IntegratedDictionary dict=new IntegratedDictionary();
	private final DefaultListModel<NavigableDictionary> dicts=new DefaultListModel<>();
	public DictionaryChooser(){
		super(new CardLayout());
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
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		add(new EditableList(dicts,this::loadDictionary),"");
	}
	private NavigableDictionary loadDictionary(){
		try{
			if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
				return loadDictionary(fileChooser.getSelectedFile());
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		throw new RuntimeException("No file is selected");
	}
	private NavigableDictionary loadDictionary(File file) throws IOException{
		if(file.isDirectory())
			return new StardictDictionary(file);
		else
			return new KeyValueDictionary(file);
	}
	public IntegratedDictionary getDictionary(){
		return dict;
	}
	public void fromPaths(String folders)throws IOException{
		dicts.clear();
		for(String path:folders.split(":"))
			if(!path.isEmpty()){
				File file=new File(path);
				if(file.isAbsolute())
					dicts.addElement(loadDictionary(file));
				else
					dicts.addElement(loadDictionary(new File(BASE,path)));
			}
	}
	public String toPaths(){
		StringBuilder buf=new StringBuilder();
		for(int i=0;i<dict.getNumberOfDictionary();i++){
			buf.append(dict.getDictionary(i).getSource()).append(':');
		}
		return buf.toString();
	}
}

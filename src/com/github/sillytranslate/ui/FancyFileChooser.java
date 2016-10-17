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
package com.github.sillytranslate.ui;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FancyFileChooser extends JTextField implements HintProvider{
	private final String SEPARATOR=System.getProperty("file.separator");
	public FancyFileChooser(){
		new AutoCompleteSupport(this,this);
	}
	public static void main(String[] args){
		JFrame f=new JFrame(java.util.ResourceBundle.getBundle("com/github/sillytranslate/Words").getString("DICTIONARY VIEWER"));
		f.add(new FancyFileChooser());
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	@Override
	public Hint[] getHints(Document doc,int pos){
		if(Thread.interrupted())
			return new Hint[0];
		try{
			String curr=doc.getText(0,doc.getLength());
			int lastSep=curr.lastIndexOf(SEPARATOR,pos);
			String prefix;
			File[] files;
			if(lastSep>=0){
				prefix=curr.substring(lastSep+SEPARATOR.length(),pos);
				files=new File(curr.substring(0,lastSep)).listFiles();
			}else{
				prefix=curr.substring(0,pos);
				files=File.listRoots();
			}
			ArrayList<Hint> choices=new ArrayList<>();
			int prefixLength=prefix.length();
			for(File f:files){
				if(Thread.interrupted())
					break;
				String name=f.getName();
				if(name.startsWith(prefix))
					if(f.isDirectory())
						choices.add(new SimpleHint(name,name.substring(prefixLength)+SEPARATOR,null,""));
					else
						choices.add(new SimpleHint(name,name.substring(prefixLength),null,""));
			}
			return choices.toArray(new Hint[0]);
		}catch(BadLocationException ex){
			Logger.getLogger(FancyFileChooser.class.getName()).log(Level.SEVERE,null,ex);
		}
		return new Hint[0];
	}
}

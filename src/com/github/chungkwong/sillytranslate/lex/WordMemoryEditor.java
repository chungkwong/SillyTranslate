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
package com.github.chungkwong.sillytranslate.lex;
import java.awt.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordMemoryEditor extends JPanel{
	private final WordMemory memory;
	private final JTextArea area=new JTextArea();
	private final JButton apply=new JButton(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("APPLY"));
	public WordMemoryEditor(WordMemory memory){
		super(new BorderLayout());
		this.memory=memory;
		updateText();
		add(area,BorderLayout.CENTER);
		apply.addActionListener((e)->{
			memory.clear();
			try{
				memory.readFromText(new BufferedReader(new StringReader(area.getText())));
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
			updateText();
		});
		add(apply,BorderLayout.SOUTH);
	}
	public void updateText(){
		try{
			StringWriter out=new StringWriter();
			memory.writeAsText(out);
			area.setText(out.getBuffer().toString());
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	public static void main(String[] args){
		JFrame f=new JFrame();
		WordMemory memory=WordMemory.getWordMemory("/home/kwong/.sillytranslatecache");
		f.add(new WordMemoryEditor(memory));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);
	}
}
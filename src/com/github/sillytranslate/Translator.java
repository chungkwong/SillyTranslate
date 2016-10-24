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
import java.awt.*;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Translator extends JPanel{

	public Translator(NavigableDictionary dict){
		setLayout(new BorderLayout());
		JTextArea from=new JTextArea();
		JButton ok=new JButton("Start");
		add(new JScrollPane(from),BorderLayout.CENTER);
		add(ok,BorderLayout.SOUTH);
		ok.addActionListener((e)->{
			removeAll();
			add(new LexEditor(new SimpleLex(new StringReader(from.getText())),(l)->{
				removeAll();
				WordTranslator translator=new WordTranslator(dict,l.iterator());
				add(translator,BorderLayout.CENTER);
				validate();
			}),BorderLayout.CENTER);
			validate();
		});

	}
	public static void main(String[] args) throws IOException {
		JFrame f=new JFrame("Translator");
		f.add(new Translator(new StardictDictionary(new File("/home/kwong/下载/stardict-lazyworm-ec-2.4.2"))));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}

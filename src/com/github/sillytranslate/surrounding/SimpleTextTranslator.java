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
package com.github.sillytranslate.surrounding;
import java.awt.*;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SimpleTextTranslator extends JPanel implements TextTranslator{
	private final JTextArea in=new JTextArea();
	private final JTextArea out=new JTextArea();
	private final JSplitPane pane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,new JScrollPane(in),new JScrollPane(out));
	private DocumentTranslatorEngine callback;
	public SimpleTextTranslator(){
		super(new BorderLayout());
		in.setEditable(false);
		add(pane,BorderLayout.CENTER);
		JButton ok=new JButton("OK");
		add(ok,BorderLayout.SOUTH);
		ok.addActionListener((e)->{
			callback.textTranslated(out.getText());
			out.setText("");
		});
		pane.setDividerLocation(0.5);
	}
	@Override
	public void translate(String text,DocumentTranslatorEngine callback){
		this.callback=callback;
		in.setText(text);
		pane.setDividerLocation(0.5);
		out.requestFocusInWindow();
	}
	public static void main(String[] args) throws FileNotFoundException{
		JFrame f=new JFrame("Translator");
		SimpleTextTranslator translator=new SimpleTextTranslator();
		PlainTextTranslator t=new PlainTextTranslator();
		t.setTextTranslator(translator);
		f.add(translator);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		t.start(new FileInputStream("/home/kwong/NetBeansProjects/JSchemeMin/README.md"),System.out);
	}
}
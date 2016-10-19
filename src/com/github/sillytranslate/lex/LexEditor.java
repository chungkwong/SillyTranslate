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
package com.github.sillytranslate.lex;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LexEditor extends JPanel{
	private final SimpleLex lex;
	private final JTextArea pane=new JTextArea();
	private final JButton ok=new JButton("OK");
	public LexEditor(SimpleLex lex,Consumer<List<String>> consumer){
		super(new BorderLayout());
		this.lex=lex;
		add(pane,BorderLayout.CENTER);
		ok.addActionListener((e)->{consumer.accept(Arrays.asList(pane.getText().split("\\n")));nextSentence();});
		add(ok,BorderLayout.SOUTH);
		nextSentence();
	}
	private static final Highlighter.HighlightPainter WORD_PAINTER=new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
	private static final Highlighter.HighlightPainter REMARK_PAINTER=new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
	private static final Highlighter.HighlightPainter MARK_PAINTER=new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
	public void nextSentence(){
		String next=lex.next();
		Lex.Type type=lex.tokenType();
		StringBuilder buf=new StringBuilder();
		while(type!=Lex.Type.END){
			pane.append(next+"\n");
			if(type==Lex.Type.WORD)
				try{
					pane.getHighlighter().addHighlight(pane.getText().length()-next.length()-1,pane.getText().length()-1,WORD_PAINTER);
				}catch(BadLocationException ex){
					Logger.getLogger(LexEditor.class.getName()).log(Level.SEVERE,null,ex);
				}
			//buf.append(next).append('\n');
			if(type==Lex.Type.MARK)
				break;
			next=lex.next();
			type=lex.tokenType();

		}
		//pane.setText(buf.toString());
		//if(buf.length()==0)
		//	ok.setEnabled(false);
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Lex editor");
		f.add(new LexEditor(new SimpleLex(new StringReader(JOptionPane.showInputDialog(""))),(o)->System.out.println(o)));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}

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
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LexEditor extends JPanel{
	private final Lex lex;
	private final JTextArea pane=new JTextArea();
	private final JButton ok=new JButton("OK");
	private final TreeMap<Integer,Token> tokens=new TreeMap<>();
	public LexEditor(Lex lex,Consumer<List<String>> consumer){
		super(new BorderLayout());
		this.lex=lex;
		pane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e){

			}
			@Override
			public void removeUpdate(DocumentEvent e){
				Document doc=e.getDocument();
				if(e.getLength()==1){
					System.out.println(tokens.get(e.getOffset()));
				}
			}
			@Override
			public void changedUpdate(DocumentEvent e){

			}
		});
		add(pane,BorderLayout.CENTER);
		ok.addActionListener((e)->{consumer.accept(Arrays.asList(pane.getText().split(" ")));});
		add(ok,BorderLayout.SOUTH);
		nextSentence();
	}
	private static HashMap<Token.Type,Highlighter.HighlightPainter> PAINTER=new HashMap<>();
	static{
		for(Token.Type type:Token.Type.values()){
			PAINTER.put(type,new DefaultHighlighter.DefaultHighlightPainter(type.getColor()));
		}
	}
	private void nextSentence(){
		try{
			Token next=lex.next();
			while(next!=null){
				int pos=pane.getText().length();
				pane.append(next.getText()+" ");
				Token.Type type=next.getType();
				tokens.put(pos,next);
				try{
					pane.getHighlighter().addHighlight(pos,pos+next.getText().length(),PAINTER.get(next.getType()));
				}catch(BadLocationException ex){
					Logger.getLogger(LexEditor.class.getName()).log(Level.SEVERE,null,ex);
				}
				//buf.append(next).append('\n');
				next=lex.next();
			}
			//pane.setText(buf.toString());
			//if(buf.length()==0)
			//	ok.setEnabled(false);
		}catch(IOException ex){
			Logger.getLogger(LexEditor.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Lex editor");
		f.add(new LexEditor(new SimpleLex(new StringReader(JOptionPane.showInputDialog(""))),(o)->System.out.println(o)));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}

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
		add(pane,BorderLayout.CENTER);
		ok.addActionListener((e)->{consumer.accept(Arrays.asList(pane.getText().split(" ")));});
		add(ok,BorderLayout.SOUTH);
		nextSentence();pane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e){
				insertRange(e.getOffset(),e.getLength());
			}
			@Override
			public void removeUpdate(DocumentEvent e){
				removeRange(e.getOffset(),e.getLength());
			}
			@Override
			public void changedUpdate(DocumentEvent e){
				//removeUpdate(e);
				//insertUpdate(e);
			}
		});
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
				tokens.put(pos,next);
				next=lex.next();
			}
			correctHighlight();
			//pane.setText(buf.toString());
			//if(buf.length()==0)
			//	ok.setEnabled(false);
		}catch(IOException ex){
			Logger.getLogger(LexEditor.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private void correctHighlight(){
		tokens.forEach((pos,val)->{
			try{
				pane.getHighlighter().addHighlight(pos,pos+val.getText().length(),PAINTER.get(val.getType()));
			}catch(BadLocationException ex){
				Logger.getLogger(LexEditor.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
	}
	private void insertRange(int begin,int size){
		Document doc=pane.getDocument();
		int end=begin+size;
		Integer pos=tokens.lastKey();
		while(pos!=null&&pos>begin){
			tokens.put(pos+size,tokens.remove(pos));
			pos=tokens.lowerKey(pos);
		}
		try{
			String raw=doc.getText(begin,size);
			int i=0;
			String left="";
			if(pos!=null&&pos<begin){
				if(!raw.isEmpty()){
					i=raw.indexOf(' ');
					if(i==-1)
						i=raw.length();
					left=tokens.get(pos).getText().substring(Math.min(begin-pos,tokens.get(pos).getText().length()));
					tokens.put(pos,new Token(Token.Type.WORD,tokens.get(pos).getText().substring(0,begin-pos)+raw.substring(0,i)));
				}
			}
			for(;i<raw.length();i++){
				if(raw.charAt(i)!=' '){
					int to=raw.indexOf(' ',i);
					if(to==-1)
						to=raw.length();
					tokens.put(i+begin,new Token(Token.Type.WORD,raw.substring(i,to)));
					i=to;
				}
			}
			if(!left.isEmpty()){
				if(raw.endsWith(" ")){
					tokens.put(end,new Token(Token.Type.WORD,left));
				}else{
					pos=tokens.lowerKey(end);
					tokens.put(pos,new Token(Token.Type.WORD,tokens.get(pos).getText()+left));
				}
			}
		}catch(BadLocationException ex){
			Logger.getLogger(LexEditor.class.getName()).log(Level.SEVERE,null,ex);
		}
		System.out.println(tokens);
		correctHighlight();
	}
	private void removeRange(int begin,int size){
		Document doc=pane.getDocument();
		int end=begin+size;
		Integer pos=tokens.floorKey(begin);
		if(pos!=null){
			Token token=tokens.get(pos);
			String text=token.getText().substring(0,begin-pos)+token.getText().substring(Math.min(end-pos,token.getText().length()));
			if(text.isEmpty())
				tokens.remove(pos);
			else
				tokens.put(pos,new Token(token.getType(),text));
			pos=tokens.higherKey(pos);
		}
		while(pos!=null&&pos+tokens.get(pos).getText().length()<=end){
			tokens.remove(pos);
			pos=tokens.higherKey(pos);
		}
		if(pos!=null&&end>=pos){
			Integer prev=tokens.lowerKey(pos);
			if(prev!=null){
				Token t=tokens.get(prev);
				tokens.put(prev,new Token(t.getType(),t.getText()+tokens.get(pos).getText().substring(end-pos)));
				tokens.remove(pos);
				pos=tokens.higherKey(pos);
			}
		}
		while(pos!=null){
			tokens.put(pos-size,tokens.remove(pos));
			pos=tokens.higherKey(pos);
		}
		correctHighlight();
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Lex editor");
		f.add(new LexEditor(new SimpleLex(new StringReader(JOptionPane.showInputDialog(""))),(o)->System.out.println(o)));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}

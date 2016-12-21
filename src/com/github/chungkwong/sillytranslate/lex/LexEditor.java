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
import com.github.chungkwong.sillytranslate.ui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LexEditor extends AbstractLexView implements ActionListener,DocumentListener{
	private final ActionTextArea pane;
	private final TreeMap<Integer,Token> tokens=new TreeMap<>();
	private final JPopupMenu menu=new JPopupMenu();
	private Consumer<List<Token>> consumer;
	public LexEditor(){
		setLayout(new BorderLayout());
		pane=new ActionTextArea((text)->commit());
		add(pane,BorderLayout.CENTER);
		pane.getDocument().addDocumentListener(this);
		for(Token.Type type:Token.Type.values()){
			JMenuItem item=new JMenuItem(type.toString());
			item.setActionCommand(type.name());
			item.addActionListener(this);
			menu.add(item);
		}
		pane.setComponentPopupMenu(menu);
	}
	public void commit(){
		pane.getDocument().removeDocumentListener(this);
		consumer.accept(tokens.values().stream().collect(Collectors.toList()));
	}
	@Override
	public void accept(Lex source,Consumer<List<Token>> callback){
		this.consumer=callback;
		pane.getDocument().removeDocumentListener(this);
		pane.setText("");
		tokens.clear();
		try{
			Token next=source.next();
			while(next!=null){
				int pos=pane.getText().length();
				pane.append(next.getText()+" ");
				tokens.put(pos,next);
				next=source.next();
			}
			correctHighlight();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		pane.getDocument().addDocumentListener(this);
		pane.requestFocusInWindow();
	}
	private static HashMap<Token.Type,Highlighter.HighlightPainter> PAINTER=new HashMap<>();
	static{
		for(Token.Type type:Token.Type.values()){
			PAINTER.put(type,new DefaultHighlighter.DefaultHighlightPainter(type.getColor()));
		}
	}
	private void correctHighlight(){
		pane.getHighlighter().removeAllHighlights();
		tokens.forEach((pos,val)->{
			try{
				pane.getHighlighter().addHighlight(pos,pos+val.getText().length(),PAINTER.get(val.getType()));
			}catch(BadLocationException ex){
				Logger.getGlobal().log(Level.FINEST,ex.getLocalizedMessage(),ex);
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
			if(pos!=null&&pos+tokens.get(pos).getText().length()>=begin){
				if(pos<begin){
					i=raw.indexOf(' ');
					if(i==-1)
						i=raw.length();
					left=tokens.get(pos).getText().substring(Math.min(begin-pos,tokens.get(pos).getText().length()));
					tokens.put(pos,new Token(Token.Type.WORD,tokens.get(pos).getText().substring(0,begin-pos)+raw.substring(0,i),""));
				}else{
					left=tokens.remove(pos).getText();
				}
			}
			for(;i<raw.length();i++){
				if(raw.charAt(i)!=' '){
					int to=raw.indexOf(' ',i);
					if(to==-1)
						to=raw.length();
					tokens.put(i+begin,new Token(Token.Type.WORD,raw.substring(i,to),""));
					i=to;
				}
			}
			if(!left.isEmpty()){
				pos=tokens.lowerKey(end);
				if(pos!=null&&pos+tokens.get(pos).getText().length()==end){
					tokens.put(pos,new Token(Token.Type.WORD,tokens.get(pos).getText()+left,""));
				}else{
					tokens.put(end,new Token(Token.Type.WORD,left,""));
				}
			}
		}catch(BadLocationException ex){
			Logger.getGlobal().log(Level.FINEST,ex.getLocalizedMessage(),ex);
		}
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
				tokens.put(pos,new Token(token.getType(),text,""));
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
				tokens.put(prev,new Token(t.getType(),t.getText()+tokens.get(pos).getText().substring(end-pos),""));
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
	/*public static void main(String[] args){
		JFrame f=new JFrame("Lex editor");
		f.add(new LexEditor(new SimpleLex(new StringReader(JOptionPane.showInputDialog(""))),(o)->System.out.println(o)));
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}*/
	@Override
	public void actionPerformed(ActionEvent e){
		Map.Entry<Integer,Token> entry=tokens.floorEntry(pane.getCaretPosition());
		if(entry!=null){
			int start=entry.getKey();
			String text=entry.getValue().getText();
			Token.Type type=Token.Type.valueOf(e.getActionCommand());
			tokens.put(start,new Token(type,text,""));
			correctHighlight();
		}
	}
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
}

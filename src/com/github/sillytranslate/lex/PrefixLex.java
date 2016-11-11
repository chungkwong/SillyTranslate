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
import com.github.sillytranslate.util.*;
import java.awt.*;
import java.io.*;
import java.util.logging.*;
import java.util.stream.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PrefixLex implements Lex{
	private CodePointReader in;
	private final NavigableDictionary dict;
	public PrefixLex(NavigableDictionary dict){
		this.dict=dict;
	}
	@Override
	public void setInput(String text){
		this.in=new CodePointReader(new StringReader(text));
	}
	@Override
	public Token next() throws IOException{
		StringBuilder buf=new StringBuilder();
		int c;
		String last=null;
		while((c=in.read())!=-1){
			buf.appendCodePoint(c);
			String curr=buf.toString();
			String geuss=dict.getCurrentWord(curr);
			if(geuss!=null&&geuss.startsWith(curr)){
				if(geuss.equals(curr))
					last=curr;
			}else{
				break;
			}
		}
		if(buf.length()==0)
			return null;
		else{
			Token.Type type=Token.Type.WORD;
			if(last==null){
				last=new String(new int[]{buf.codePointAt(0)},0,1);
				type=Token.Type.OTHER_MARK;
			}
			String curr=buf.toString().substring(last.length());
			in.unread(curr.codePoints().mapToObj((i)->i).collect(Collectors.toList()));
			return new Token(Token.Type.WORD,last);
		}
	}
	public static void main(String[] args) throws IOException{
		StardictDictionary zhDict=new StardictDictionary(new File("/home/kwong/下载/stardict-cedict-gb-2.4.2"));
		/*Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String sentence=in.next();
			PrefixLex lex=new PrefixLex(new StringReader(sentence),zhDict);
			String word;
			while((word=lex.next())!=null){
				System.out.println(word);
			}
		}*/
		JFrame f=new JFrame("只含几十行代码的劣质中文分词");
		JTextField in=new JTextField();
		JPanel out=new JPanel();
		in.addActionListener((e)->{
			out.removeAll();
			PrefixLex lex=new PrefixLex(zhDict);
			lex.setInput(in.getText());
			Token word;
			try{
				while((word=lex.next())!=null){
					JButton b=new JButton(word.getText());
					b.setToolTipText(word.getType().toString());
					out.add(b);
				}
				out.invalidate();
				f.revalidate();
			}catch(IOException ex){
				Logger.getLogger(PrefixLex.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		f.add(in,BorderLayout.NORTH);
		f.add(out,BorderLayout.CENTER);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
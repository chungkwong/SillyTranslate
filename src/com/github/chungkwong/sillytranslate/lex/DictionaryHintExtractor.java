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
import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DictionaryHintExtractor{
	public static Hint[] extractHint(String word,String prefix,NavigableDictionary dict){
		String entry=dict.getMeaning(word);
		if(entry==null)
			entry=dict.getMeaning(normalize(word));
		if(entry!=null)
			return split(entry,word,prefix);
		else
			return new Hint[0];
	}
	private static String normalize(String word){
		return word.toLowerCase();
	}
	private static Hint[] split(String text,String word,String prefix){
		ArrayList<Hint> hints=new ArrayList<>();
		int prefixLen=prefix.length();
		String type="";
		for(int i=0;i<text.length();i++){
			char c=text.charAt(i);
			if(c=='['){
				while(++i<text.length()&&text.charAt(i)!=']');
			}else if(Character.isWhitespace(c)){

			}else{
				int j=i;
				for(;j<text.length();j++){
					int d=text.charAt(j);
					if(d==','||d==';'||d=='\n'||d=='\r')
						break;
				}
				String token=text.substring(i,j);
				if(token.endsWith("."))
					type=token;
				else if(!token.isEmpty()&&token.startsWith(prefix))
					hints.add(new SimpleHint(token,token.substring(prefixLen),null,type));
				if(j>i)
					i=j-1;
			}
		}
		if(word.startsWith(prefix))
			hints.add(new SimpleHint(word,word.substring(prefixLen),null,""));
		return hints.toArray(new Hint[0]);
	}
	public static void main(String[] args) throws IOException{
		JFrame f=new JFrame("Test");
		StardictDictionary dict=new StardictDictionary(new File("/home/kwong/下载/stardict-lazyworm-ec-2.4.2"));
		JTextField input=new JTextField();
		f.add(input,BorderLayout.NORTH);
		JTextArea output=new JTextArea();
		f.add(output,BorderLayout.CENTER);
		input.addActionListener((e)->{
			output.setText(Arrays.stream(extractHint(input.getText(),"",dict)).map((o)->o.toString()).collect(Collectors.joining("\n")));
		});
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
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
import java.io.*;
import java.text.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class JavaLex implements Lex{
	private final BreakIterator iter;
	private final String text;
	private final Locale locale;
	public JavaLex(String text,Locale locale){
		iter=BreakIterator.getWordInstance(locale);
		iter.setText(text);
		this.text=text;
		this.locale=locale;
		iter.first();
	}
	private Token createToken(String word){
		Token.Type type=Token.Type.WORD;
		if(word.codePointCount(0,word.length())==1){
			ResourceBundle bundle=ResourceBundle.getBundle("com/github/sillytranslate/lex/LANGUAGE",locale);
			for(String key:bundle.keySet())
				if(bundle.getString(key).contains(word))
					type=Token.Type.valueOf(key);
		}
		return new Token(type,word);
	}
	@Override
	public Token next() throws IOException{
		int i=iter.current(),j=iter.next();
		if(j==BreakIterator.DONE)
			return null;
		String word=text.substring(i,j);
		if(word.trim().isEmpty())
			return next();
		return createToken(word);
	}
	public static void main(String[] args) throws IOException{
		Scanner in=new Scanner(System.in);
		while(in.hasNext()){
			JavaLex lex=new JavaLex(in.nextLine(),Locale.US);
			Token t;
			while((t=lex.next())!=null)
				System.out.println(t);
		}
	}
}
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
import com.github.chungkwong.sillytranslate.util.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SimpleLex implements Lex{
	private CodePointReader in;
	public SimpleLex(){
	}
	@Override
	public void setInput(String text){
		this.in=new CodePointReader(new StringReader(text));
	}
	@Override
	public Token next()throws IOException{
		StringBuilder buf=new StringBuilder();
		int c;
		while((c=in.read())!=-1&&Character.isWhitespace(c)){

		}
		while(c!=-1&&Character.isLetterOrDigit(c)){
			buf.appendCodePoint(c);
			c=in.read();
		}
		if(buf.length()==0){
			if(c==-1){
				return null;
			}else{
				String last=new String(new int[]{c},0,1);
				return new Token(Token.guessType(last,Locale.ENGLISH),last,"");
			}
		}else{
			in.unread(c);
			return new Token(Token.Type.WORD,buf.toString(),"");
		}
	}
	public static void main(String[] args)throws IOException{
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String line=in.nextLine();
			SimpleLex lex=new SimpleLex();
			lex.setInput(line);
			Token token=lex.next();
			while(token!=null){
				System.out.println(token);
				token=lex.next();
			}
		}
	}
}
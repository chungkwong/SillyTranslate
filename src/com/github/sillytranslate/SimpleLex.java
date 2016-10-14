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
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SimpleLex implements Lex{
	private final PushbackReader in;
	private Type type;
	public SimpleLex(Reader in){
		this.in=new PushbackReader(in,1);
	}
	@Override
	public Type tokenType(){
		return type;
	}
	@Override
	public String next(){
		StringBuilder buf=new StringBuilder();
		int c;
		try{
			while((c=in.read())!=-1&&Character.isWhitespace(c)){

			}
			while(c!=-1&&Character.isLetterOrDigit(c)){
				buf.appendCodePoint(c);
				c=in.read();
			}
			if(buf.length()==0){
				if(c=='('){
					int lv=1;
					while((c=in.read())!=-1){
						if(c=='('){
							++lv;
						}else if(c==')'){
							--lv;
							if(lv==0)
								break;
						}
						buf.appendCodePoint(c);
					}
					type=Type.REMARK;
				}else if(c=='\"'){
					while((c=in.read())!=-1){
						if(c=='\"'){
							break;
						}
						buf.appendCodePoint(c);
					}
					type=type.QUOTE;
				}else if(c==-1){
					type=Type.END;
				}else{
					buf.appendCodePoint(c);
					type=Type.MARK;
				}
			}else{
				in.unread(c);
				type=Type.WORD;
			}
		}catch(IOException ex){
			Logger.getLogger(SimpleLex.class.getName()).log(Level.SEVERE,null,ex);
		}
		return buf.toString();
	}
	public static void main(String[] args){
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String line=in.nextLine();
			SimpleLex lex=new SimpleLex(new StringReader(line));
			String token=lex.next();
			while(lex.tokenType()!=Lex.Type.END){
				System.out.println(token+":"+lex.tokenType());
				token=lex.next();
			}
		}
	}
}

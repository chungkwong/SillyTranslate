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
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TreeLex{
	private final CodePointReader in;
	public TreeLex(Reader in){
		this.in=new CodePointReader(in,1);
	}
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
				if(c==-1){
					return null;
				}else{
					buf.appendCodePoint(c);
				}
			}else{
				in.unread(c);
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
			TreeLex lex=new TreeLex(new StringReader(line));
			String token=lex.next();
			while(token!=null){
				System.out.println(token);
				token=lex.next();
			}
		}
	}
}

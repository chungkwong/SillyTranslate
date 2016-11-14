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
package com.github.chungkwong.json;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class JSONParser{
	private static final int EOFException=-1;
	public static JSONStuff parse(String in) throws IOException,SyntaxException{
		return parse(new StringReader(in));
	}
	public static JSONStuff parse(Reader in) throws IOException,SyntaxException{
		return nextValue(new PushbackReader(in));
	}
	private static JSONStuff nextValue(PushbackReader in) throws IOException,SyntaxException{
		int c=readNonwhitespace(in);
		switch(c){
			case '{':
				return nextObject(in);
			case '[':
				return nextArray(in);
			case '"':
				return nextString(in);
			case 'n':
				expect(in,'u');expect(in,'l');expect(in,'l');
				return JSONNull.INSTANCE;
			case 'f':
				expect(in,'a');expect(in,'l');expect(in,'s');expect(in,'e');
				return JSONBoolean.FALSE;
			case 't':
				expect(in,'r');expect(in,'u');expect(in,'e');
				return JSONBoolean.TRUE;
			default:
				in.unread(c);
				return nextNumber(in);
		}
	}
	private static JSONObject nextObject(PushbackReader in) throws SyntaxException,IOException{
		HashMap<JSONStuff,JSONStuff> members=new HashMap<>();
		int c=readNonwhitespace(in);
		if(c!='}'){
			in.unread(c);
			while(true){
				JSONStuff key=nextValue(in);
				expectNonwhitespace(in,':');
				JSONStuff value=nextValue(in);
				members.put(key,value);
				c=readNonwhitespace(in);
				if(c=='}')
					break;
				else if(c!=',')
					throw new SyntaxException("Expected ',' or '}'");
			}
		}
		return new JSONObject(members);
	}
	private static JSONArray nextArray(PushbackReader in) throws IOException, SyntaxException{
		List<JSONStuff> elements=new ArrayList<>();
		int c=readNonwhitespace(in);
		if(c!=']'){
			in.unread(c);
			while(true){
				elements.add(nextValue(in));
				c=readNonwhitespace(in);
				if(c==']')
					break;
				else if(c!=',')
					throw new SyntaxException("Expected ',' or ']'");
			}
		}
		return new JSONArray(elements);
	}
	private static JSONNumber nextNumber(PushbackReader in) throws IOException,SyntaxException{
		StringBuilder buf=new StringBuilder();
		int c=in.read();
		if(c=='-')
			c=appendAndNext(c,buf,in);
		while(Character.isDigit(c))
			c=appendAndNext(c,buf,in);
		if(c=='.'){
			c=appendAndNext(c,buf,in);
			while(Character.isDigit(c))
				c=appendAndNext(c,buf,in);
		}
		if(c=='e'||c=='E'){
			c=appendAndNext(c,buf,in);
			if(c=='-'||c=='+')
				c=appendAndNext(c,buf,in);
			while(Character.isDigit(c))
				c=appendAndNext(c,buf,in);
		}
		in.unread(c);
		try{
			return new JSONNumber(Long.valueOf(buf.toString()));
		}catch(NumberFormatException ex){
			return new JSONNumber(Double.valueOf(buf.toString()));
		}
	}
	private static int appendAndNext(int c,StringBuilder buf,Reader in) throws IOException{
		buf.appendCodePoint(c);
		return in.read();
	}
	private static JSONString nextString(PushbackReader in) throws IOException,SyntaxException{
		int c;
		StringBuilder buf=new StringBuilder();
		while((c=in.read())!='"'){
			if(c=='\\'){
				c=in.read();
				switch(c){
					case '"':buf.append('\"');break;
					case '\\':buf.append('\\');break;
					case '/':buf.append('/');break;
					case 'b':buf.append('\b');break;
					case 'f':buf.append('\f');break;
					case 'n':buf.append('\n');break;
					case 'r':buf.append('\r');break;
					case 't':buf.append('\t');break;
					case 'u':buf.appendCodePoint(nextFourHexDigit(in));break;
					default:throw new SyntaxException("Illegal escape");
				}
			}else{
				buf.appendCodePoint(c);
			}
		}
		return new JSONString(buf.toString());
	}
	private static int nextFourHexDigit(PushbackReader in) throws IOException{
		int val=0;
		for(int i=0;i<4;i++){
			val=val*16+Character.digit(in.read(),16);
		}
		return val;
	}
	private static int readNonwhitespace(PushbackReader in) throws IOException{
		int c;
		while(Character.isWhitespace(c=in.read())){
		}
		return c;
	}
	private static void expectNonwhitespace(PushbackReader in,int c) throws SyntaxException,IOException{
		if(readNonwhitespace(in)!=c){
			throw new SyntaxException("Expected "+(char)c);
		}
	}
	private static void expect(PushbackReader in,int c) throws SyntaxException,IOException{
		if(in.read()!=c){
			throw new SyntaxException("Expected "+(char)c);
		}
	}
}
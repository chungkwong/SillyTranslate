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
package com.github.chungkwong.sillytranslate.surrounding;
import com.github.chungkwong.sillytranslate.util.*;
import java.io.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class POTranslator implements DocumentTranslatorEngine{
	private TextTranslator translator;
	private Runnable callback;
	private CodePointReader in;
	private CodePointWriter out;
	@Override
	public void setTextTranslator(TextTranslator translator){
		this.translator=translator;
	}
	@Override
	public void setOnFinished(Runnable callback){
		this.callback=callback;
	}
	@Override
	public void start(InputStream in,OutputStream out){
		try{
			this.in=new CodePointReader(new InputStreamReader(in,"UTF-8"));
			this.out=new CodePointWriter(new OutputStreamWriter(out,"UTF-8"));
			textTranslated(null);
		}catch(UnsupportedEncodingException ex){
			Logger.getLogger(PropertiesTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void textTranslated(String text){
		try{
			if(text!=null)
				out.write(encode(text));
			int c=in.read();
			while(c!=-1){
				c=skipWhiteSpace(c);
				if(c==-1){
					break;
				}else if(c=='#'){
					while(c!='\n'&&c!='\r'&&c!=-1){
						out.writeCodepoint(c);
						c=in.read();
					}
				}else{
					String keyword=readKeyword(c);
					if(keyword.startsWith("msgstr")){
						String val=readValue();
						translator.translate(val,this);
						return;
					}else{
						c=skipValue();
					}
				}
			}
			in.close();
			out.flush();
			callback.run();
		}catch(IOException ex){
			Logger.getLogger(PropertiesTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private int skipWhiteSpace(int c) throws IOException{
		while(Character.isWhitespace(c)){
			out.writeCodepoint(c);
			c=in.read();
		}
		return c;
	}
	private String readKeyword(int c)throws IOException{
		StringBuilder buf=new StringBuilder();
		while(!Character.isWhitespace(c)){
			buf.appendCodePoint(c);
			out.writeCodepoint(c);
			c=in.read();
		}
		out.writeCodepoint(c);
		return buf.toString();
	}
	private int skipValue() throws IOException{
		int c=in.read();
		while((c=skipWhiteSpace(c))=='\"'){
			out.writeCodepoint(c);
			while((c=in.read())!='\"'){
				out.writeCodepoint(c);
				if(c=='\\'){
					c=in.read();
					out.writeCodepoint(c);
				}
			}
			out.writeCodepoint(c);
			c=in.read();
		}
		return c;
	}
	private String readValue() throws IOException{
		StringBuilder buf=new StringBuilder();
		int c=in.read();
		while((c=eatWhiteSpace(c))=='\"'){
			while((c=in.read())!='\"'){
				if(c=='\\'){
					c=in.read();
					switch(c){
						case 'n':buf.append('\n');break;
						case 'r':buf.append('\r');break;
						case 't':buf.append('\t');break;
						case 'f':buf.append('\f');break;
						case 'b':buf.append('\b');break;
						case '\\':buf.append('\\');break;
						case '\"':buf.append('\"');break;
						case '\'':buf.append('\'');break;
						default:buf.appendCodePoint(readEcapse(c));break;
					}
				}else{
					buf.appendCodePoint(c);
				}
			}
			c=in.read();
		}
		if(c!=-1)
			in.unread(c);
		return buf.toString();
	}
	private int eatWhiteSpace(int c) throws IOException{
		while(Character.isWhitespace(c)){
			c=in.read();
		}
		return c;
	}
	private int readEcapse(int c) throws IOException{
		if(c=='x'||c=='X'){
			return readInteger(in.read(),16);
		}else{
			return readInteger(c,8);
		}
	}
	private int readInteger(int c,int base) throws IOException{
		int number=0;
		int d;
		while((d=Character.digit(c,base))!=-1){
			number=number*base+d;
			c=in.read();
		}
		in.unread(c);
		return number;
	}
	private String encode(String text){
		return '\"'+text.replace("\\","\\\\").replace("\r","\\r\"\n\"").replace("\n","\\n\"\n\"")+"\"\n\n";
	}
	@Override
	public String toString(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("PO");
	}
}
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
public class GroffTranslator implements DocumentTranslatorEngine{
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
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	@Override
	public void textTranslated(String text){
		try{
			if(text!=null)
				out.write(encode(text));
			int c=in.read();
			while(c!=-1){
				if(c=='\n'){
					out.writeCodepoint(c);
					c=in.read();
				}else if(c=='.'){
					c=skipLine(c);
				}else{
					StringBuilder buf=new StringBuilder();
					while(c!='\n'&&c!=-1){
						if(c=='\\'){
							if(buf.length()>0){
								in.unread(c);
								translator.translate(buf.toString(),this);
								return;
							}else{
								c=processEscape(in.read());
							}
						}else{
							buf.appendCodePoint(c);
							c=in.read();
						}
					}
				}
			}
			in.close();
			out.flush();
			callback.run();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	private int skipLine(int c) throws IOException{
		while(c=='\n'){
			out.writeCodepoint(c);
			c=in.read();
		}
		return c;
	}
	private int processEscape(int c) throws IOException{
		out.writeCodepoint('\\');
		if(c=='*'){
			out.writeCodepoint(c);
			c=in.read();
		}
		out.writeCodepoint(c);
		if(c=='('){
			out.writeCodepoint(in.read());
			out.writeCodepoint(in.read());
		}else if(c=='['){
			while((c=in.read())!=']'&&c!=-1)
				out.writeCodepoint(c);
			out.writeCodepoint(']');
		}else{

		}
		c=in.read();
		if(c=='['){
			while((c=in.read())!=']'&&c!=-1)
				out.writeCodepoint(c);
			out.writeCodepoint(']');
			c=in.read();
		}else if(c=='\''){
			while((c=in.read())!='\''&&c!=-1)
				out.writeCodepoint(c);
			out.writeCodepoint('\'');
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
		return "Groff";
	}
}
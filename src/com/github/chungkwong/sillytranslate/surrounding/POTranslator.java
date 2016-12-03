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
			textTranslated("");
		}catch(UnsupportedEncodingException ex){
			Logger.getLogger(PropertiesTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void textTranslated(String text){
		try{
			out.write(encode(text));
			int c=in.read();
			while(c!=-1){
				c=skipWhiteSpace(c);
				if(c=='#'){
					while(c!='\n'&&c!='\r'){
						out.writeCodepoint(c);
						c=in.read();
					}
				}else{
					//TODO
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
	private String encode(String text){
		return text.replace("\\","\\\\").replace("\r","\\r").replace("\n","\\n");
	}
}

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
package com.github.sillytranslate.surrounding;
import com.github.sillytranslate.util.*;
import java.io.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PlainTextTranslator implements DocumentTranslatorEngine{
	private CodePointReader in;
	private CodePointWriter out;
	private TextTranslator translator;
	private Runnable callback;
	public PlainTextTranslator(){
	}
	@Override
	public void start(InputStream in,OutputStream out){
		try{
			this.in=new CodePointReader(new InputStreamReader(in,"UTF-8"));
			this.out=new CodePointWriter(new OutputStreamWriter(out,"UTF-8"));
			textTranslated("");
		}catch(IOException ex){
			Logger.getLogger(PlainTextTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void setOnFinished(Runnable callback){
		this.callback=callback;
	}
	@Override
	public void setTextTranslator(TextTranslator translator){
		this.translator=translator;
	}
	@Override
	public void textTranslated(String text){
		try{
			if(out==null)
				throw new IllegalStateException();
			out.write(text);
			int c;
			while((c=in.read())!=-1&&Character.isWhitespace(c)){
				out.writeCodepoint(c);
			}
			if(c!=-1){
				StringBuilder buf=new StringBuilder();
				do{
					buf.appendCodePoint(c);
					c=in.read();
					if(c=='\n'||c=='\r'){
						int d=in.read();
						if(d=='\n'||d=='\r'){
							in.unread(d);
							in.unread(c);
							break;
						}else
							in.unread(d);
					}
				}while(c!=-1);
				translator.translate(buf.toString(),this);
			}else{
				in.close();
				out.flush();
				callback.run();
			}
		}catch(IOException ex){
			Logger.getLogger(PlainTextTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public String toString(){
		return "Plain";
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String file="/home/kwong/NetBeansProjects/JSchemeMin/README.md";
		PlainTextTranslator t=new PlainTextTranslator();
		t.setTextTranslator((text,callback)->{
			callback.textTranslated("#"+text+"#");
		});
		t.start(new FileInputStream(file),System.out);
	}
}
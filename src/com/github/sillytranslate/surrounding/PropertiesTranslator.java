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
public class PropertiesTranslator implements DocumentTranslatorEngine{
	private CodePointReader in;
	private CodePointWriter out;
	private TextTranslator translator;
	private Runnable callback;
	private final StringBuilder buf=new StringBuilder();
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
				if(c=='!'||c=='#'){
					while(c!='\n'&&c!='\r'){
						out.writeCodepoint(c);
						c=in.read();
					}
				}else{
					while(c!='='&&c!=':'&&!Character.isWhitespace(c)&&c!=-1){
						out.writeCodepoint(c);
						if(c=='\\'){
							c=in.read();
							out.writeCodepoint(c);
							if(c=='\r'||c=='\n'){
								c=skipLineBreak(c);
							}else
								c=in.read();
						}else
							c=in.read();
					}
					if(c!='\r'&&c!='\n'&&c!=-1){
						out.writeCodepoint(c);
						c=in.read();
						while(c!='\r'&&c!='\n'&&c!=-1){
							if(c=='\\'){
								c=in.read();
								switch(c){
									case '\r':case '\n':c=skipLineBreakSilence(c);break;
									case 'f':buf.append('\f');c=in.read();break;
									case 'n':buf.append('\n');c=in.read();break;
									case 'r':buf.append('\r');c=in.read();break;
									case 't':buf.append('\t');c=in.read();break;
									case 'u':buf.append(getHex());c=in.read();break;
									default:buf.appendCodePoint(c);c=in.read();
								}
							}else{
								buf.appendCodePoint(c);
								c=in.read();
							}
						}
						in.unread(c);
						String val=buf.toString();
						buf.setLength(0);
						translator.translate(val,this);
						return;
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
	private int skipWhiteSpaceSilence(int c) throws IOException{
		while(Character.isWhitespace(c)){
			c=in.read();
		}
		return c;
	}
	private int skipLineBreak(int c) throws IOException{
		if(c=='\r'){
			c=in.read();
			if(c=='\n'){
				out.write("\r\n");
				c=in.read();
			}
		}else
			c=in.read();
		return skipWhiteSpace(c);
	}
	private int skipLineBreakSilence(int c) throws IOException{
		if(c=='\r'){
			c=in.read();
			if(c=='\n'){
				c=in.read();
			}
		}else
			c=in.read();
		return skipWhiteSpaceSilence(c);
	}
	private int getHex() throws IOException{
		return in.read()*0x1000+in.read()*0x100+in.read()*0x10+in.read();
	}
	private String encode(String text){
		return text.replace("\\","\\\\").replace("\r","\\r").replace("\n","\\n");
	}
	@Override
	public String toString(){
		return "Properties";
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String file="/home/kwong/NetBeansProjects/JGitGUI/src/com/chungkwong/jgitgui/text.properties";
		PropertiesTranslator t=new PropertiesTranslator();
		t.setTextTranslator((text,callback)->{
			callback.textTranslated("#"+text+"#");
		});
		t.start(new FileInputStream(file),System.out);
	}
}
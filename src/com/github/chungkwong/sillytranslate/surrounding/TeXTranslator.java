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
public class TeXTranslator implements DocumentTranslatorEngine{
	private static final String SPECIAL="&#$^_{}~";
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
			StringBuilder buf=new StringBuilder();
			boolean empty=true;
			while(c!=-1){
				if(c=='%'){
					if(empty)
						c=skipLine(c);
					else{
						in.unread(c);
						translator.translate(buf.toString(),this);
						return;
					}
				}else if(c=='\\'){
					if(empty){
						out.writeCodepoint(c);
						c=in.read();
						if((c>='a'&&c<='z')||(c>='A'&&c<='Z')){
							while((c>='a'&&c<='z')||(c>='A'&&c<='Z')){
								out.writeCodepoint(c);
								c=in.read();
							}
						}else{
							out.writeCodepoint(c);
							c=in.read();
						}
					}else{
						in.unread(c);
						translator.translate(buf.toString(),this);
						return;
					}
				}else if(Character.isWhitespace(c)){
					if(empty){
						out.writeCodepoint(c);
						c=in.read();
					}else{
						int line=0;
						while(Character.isWhitespace(c)){
							if(c=='\n'||c=='\r')
								++line;
							buf.appendCodePoint(c);
							c=in.read();
						}
						if(line>=2){
							in.unread(c);
							in.unread(c);
							translator.translate(buf.toString(),this);
							return;
						}
					}
				}else if(SPECIAL.indexOf(c)!=-1){
					if(empty){
						out.writeCodepoint(c);
						c=in.read();
					}else{
						in.unread(c);
						translator.translate(buf.toString(),this);
						return;
					}
				}else{
					empty=false;
					buf.appendCodePoint(c);
					c=in.read();
				}
			}
			if(!empty){
				translator.translate(buf.toString(),this);
				return;
			}
			in.close();
			out.flush();
			callback.run();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	private int skipLine(int c) throws IOException{
		while(c!='\n'&&c!='\r'&&c!=-1){
			out.writeCodepoint(c);
			c=in.read();
		}
		return c;
	}
	private String encode(String text){
		StringBuilder buf=new StringBuilder();
		for(int i=0;i<text.length();i++){
			char c=text.charAt(i);
			switch(c){
				case '$':buf.append("\\$");break;
				case '%':buf.append("\\%");break;
				case '#':buf.append("\\#");break;
				case '_':buf.append("\\_");break;
				case '&':buf.append("\\&");break;
				case '{':buf.append("$\\{$ ");break;
				case '}':buf.append("$\\}$ ");break;
				case '\\':buf.append("$\\backslash$ ");break;
				case '~':buf.append("\\~{}");break;
				case '^':buf.append("\\^{}");break;
				default:buf.append(c);break;
			}
		}
		return buf.toString();
	}
	@Override
	public String toString(){
		return "TeX";
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		String file="/home/kwong/projects/sysuthesis/main.tex";
		//String file="/home/kwong/NetBeansProjects/JSchemeMin/doc/first.tex";
		TeXTranslator t=new TeXTranslator();
		TextTranslatorStub stub=new TextTranslatorStub();
		t.setTextTranslator(stub);
		t.setOnFinished(()->{stub.close();});
		t.start(new FileInputStream(file),System.out);
	}
}
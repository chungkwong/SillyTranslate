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
import com.github.sillytranslate.*;
import com.github.sillytranslate.util.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PlainTextFormat implements DocumentFormat{
	private final CodePointReader in;
	private final Writer out;
	public PlainTextFormat(Reader in,Writer out){
		this.in=new CodePointReader(in,1);
		this.out=out;
	}
	@Override
	public String nextTextToBeTranslated() throws IOException{
		StringBuilder wbuf=new StringBuilder();
		int c;
		while((c=in.read())!=-1){
			if(Character.isWhitespace(c)){
				wbuf.appendCodePoint(c);
			}else{
				StringBuilder buf=new StringBuilder();
				do{
					buf.appendCodePoint(c);
					c=in.read();
					if(c=='\r'||c=='\n'){
						int d=in.read();
						if(d=='\r'||d=='\n'){
							wbuf.appendCodePoint(c).appendCodePoint(d);
							break;
						}else if(d!=-1){
							in.unread(d);
						}
					}
				}while(c!=-1);
				out.write(wbuf.toString());
				return buf.toString();
			}
		}
		out.write(wbuf.toString());
		return null;
	}
	@Override
	public void insertTranslation(String str) throws IOException{
		out.write(str);
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		Scanner in=new Scanner(System.in);
		OutputStreamWriter out=new OutputStreamWriter(System.out);
		PlainTextFormat t=new PlainTextFormat(new FileReader(in.nextLine()),out);
		String str;
		StardictDictionary d=new StardictDictionary(new File("/home/kwong/下载/stardict-kdic-computer-gb-2.4.2"));
		while((str=t.nextTextToBeTranslated())!=null){
			//System.out.println("Please translate"+str);
			//t.insertTranslation(in.nextLine());
			SimpleLex lex=new SimpleLex(new StringReader(str));
			String token=lex.next();
			while(lex.tokenType()!=Lex.Type.END){
				System.out.println(lex.tokenType()+":"+token+"-"+d.getMeaning(token));
				token=lex.next();
			}
		}
		//out.flush();
	}
}

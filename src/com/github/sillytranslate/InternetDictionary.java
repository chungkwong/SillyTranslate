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
import java.net.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class InternetDictionary implements NavigableDictionary{
	private final String queryPrefix,querySuffix;
	public InternetDictionary(String queryPrefix,String querySuffix){
		this.queryPrefix=queryPrefix;
		this.querySuffix=querySuffix;
	}

	@Override
	public String getMeaning(String word){
		try{
			URLConnection connection=new URL(queryPrefix+word+querySuffix).openConnection();
			String encoding=connection.getContentEncoding();
			if(encoding==null)
				encoding="UTF-8";
			InputStreamReader in=new InputStreamReader(connection.getInputStream(),encoding);
			StringBuilder buf=new StringBuilder();
			char[] cbuf=new char[1024];
			int size;
			while((size=in.read(cbuf,0,1024))!=-1)
				buf.append(cbuf,0,size);
			//return buf.toString();
			return removeHTMLTag(buf.toString());//.replaceAll("<[^>]*>","");
		}catch(MalformedURLException ex){
			Logger.getLogger(InternetDictionary.class.getName()).log(Level.SEVERE,null,ex);
		}catch(IOException ex){
			Logger.getLogger(InternetDictionary.class.getName()).log(Level.SEVERE,null,ex);
		}
		return null;
	}
	@Override
	public String getCurrentWord(String word){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getNextWord(String word){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	private static String removeHTMLTag(String html){
		StringBuilder buf=new StringBuilder();
		for(int i=0;i<html.length();i++){
			char c=html.charAt(i);
			if(c!='<'){
				buf.append(c);
			}else{
				boolean instr=false,inEsc=false;
				boolean isComment=i+3<html.length()&&html.charAt(i+1)=='!'&&html.charAt(i+2)=='-'&&html.charAt(i+3)=='-';
				int start=i+6;
				while(++i<html.length()){
					c=html.charAt(i);
					if(instr){
						if(c=='\"'&&!inEsc)
							instr=false;
						inEsc=c=='\\';
					}else if(c=='>'){
						if(!isComment||(i>start&&html.charAt(i-1)=='-'&&html.charAt(i-2)=='-'))
							break;
					}else if(c=='\"'){
						instr=true;
					}
				}
			}
		}
		return buf.toString();
	}
	public static void main(String[] args){
/*		Scanner in=new Scanner(System.in);
		while(in.hasNextLine())
			System.out.println(removeHTMLTag(in.nextLine()));*/
		System.out.println(new InternetDictionary("http://www.dict.org/bin/Dict?Form=Dict2&Database=*&Query=","").getMeaning("apple"));
	}
}

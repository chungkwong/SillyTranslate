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
package com.github.chungkwong.sillytranslate.sentence;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Sentences{
	public static String build(Stream<String> tokens,Locale locale){
		switch(locale.getLanguage()){
			case "en":
				return buildEnglish(tokens);
			default:
				return buildOther(tokens);
		}
	}
	private static String buildEnglish(Stream<String> tokens){
		String sentence=tokens.collect(Collectors.joining(" "));
		StringBuilder buf=new StringBuilder();
		if(!sentence.isEmpty()){
			buf.appendCodePoint(Character.toUpperCase(sentence.codePointAt(0)));
			Stack<Integer> quote=new Stack<>();
			for(int i=sentence.offsetByCodePoints(0,1);i<sentence.length();i=sentence.offsetByCodePoints(i,1)){
				int c=sentence.codePointAt(i);
				boolean rmLeft=c==','||c=='.'||c==';'||c=='?'||c=='!'||c==':'||c==')'||c=='/';
				boolean rmRight=c=='('||c=='/';
				if(c=='\''||c=='\"'){
					if(quote.isEmpty()||quote.peek().intValue()!=c){
						rmRight=true;
						quote.push(c);
					}else{
						rmLeft=true;
						quote.pop();
					}
				}
				if(rmLeft&&buf.charAt(buf.length()-1)==' ')
					buf.deleteCharAt(buf.length()-1);
				buf.appendCodePoint(c);
				if(rmRight){
					int j=sentence.offsetByCodePoints(i,1);
					if(j<sentence.length()&&sentence.charAt(j)==' '){
						i=j;
					}
				}
			}
		}
		return buf.toString();
	}
	private static String buildOther(Stream<String> tokens){
		return tokens.collect(Collectors.joining());
	}
	public static void main(String[] args){
		System.out.println(build(Arrays.stream(new String[]{"i","like","you"}),Locale.FRENCH));
		System.out.println(build(Arrays.stream(new String[]{"i","(","like","\"","good","\"",")",",","you","."}),Locale.ENGLISH));
		System.out.println(build(Arrays.stream(new String[]{"i","like","you"}),Locale.CHINA));
	}
}

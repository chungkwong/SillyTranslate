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
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Sentences{
	private static final HashMap<String,Function<Stream<String>,String>> DISPATCH=new HashMap<>();
	public static Function<Stream<String>,String> registerBuilder(String language,Function<Stream<String>,String> builder){
		return DISPATCH.put(language,builder);
	}
	public static String build(Stream<String> tokens,Locale locale){
		Function<Stream<String>,String> builder=DISPATCH.get(locale.getLanguage());
		if(builder!=null){
			return builder.apply(tokens);
		}else{
			return buildOther(tokens);
		}
	}
	private static String buildEnglish(Stream<String> tokens){
		String sentence=tokens.collect(Collectors.joining(" "));
		StringBuilder buf=new StringBuilder();
		if(!sentence.isEmpty()){
			Stack<Integer> quote=new Stack<>();
			int i=sentence.offsetByCodePoints(0,1);
			int c=sentence.codePointAt(0);
			buf.appendCodePoint(Character.toUpperCase(c));
			if(c=='\''||c=='\"'){
				quote.push(c);
				if(i<sentence.length()&&sentence.charAt(i)==' '){
					i=sentence.offsetByCodePoints(i,1);
				}
			}
			for(;i<sentence.length();i=sentence.offsetByCodePoints(i,1)){
				c=sentence.codePointAt(i);
				boolean rmLeft=c==','||c=='.'||c==';'||c=='?'||c=='!'||c==':'||c==')'||c=='/';
				boolean rmRight=c=='('||c=='/';
				if(c=='\"'||(c=='\''&&buf.charAt(buf.length()-1)==' ')){
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
	static{
		registerBuilder("en",Sentences::buildEnglish);
	}
	public static void main(String[] args){
		System.out.println(build(Arrays.stream(new String[]{"i","like","you"}),Locale.FRENCH));
		System.out.println(build(Arrays.stream(new String[]{"i","(","like","\"","good","\"",")",",","you","."}),Locale.US));
		System.out.println(build(Arrays.stream(new String[]{"i","(","like","\"","good","\"",")",",","you","."}),Locale.ENGLISH));
		System.out.println(build(Arrays.stream(new String[]{"i","like","you"}),Locale.CHINA));
	}
}

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
package com.github.chungkwong.sillytranslate.lex;
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class E2CNormalizers{
	private static final WordNormalizer[] NORMALIZERS=new WordNormalizer[]{
		createOwnNormalizer(),createPluralNormalizer(),createContinuingTenseNormalizer(),
		createPastTenseNormalizer(),createPastPerfectTenseNormalizer()
	};
	public static WordNormalizer[] getNormalizers(){
		return NORMALIZERS;
	}
	private static WordNormalizer createPastTenseNormalizer(){
		return new PropertyNormalizer("PAST","","了");
	}
	private static WordNormalizer createPastPerfectTenseNormalizer(){
		return new PropertyNormalizer("PERFECT","","过");
	}
	private static WordNormalizer createContinuingTenseNormalizer(){
		return new PropertyNormalizer("CONTINUING","正在","");
	}
	private static WordNormalizer createPluralNormalizer(){
		return new PropertyNormalizer("PLURAL","","");
	}
	private static WordNormalizer createOwnNormalizer(){
		return new PropertyNormalizer("OWN","","的");
	}
	private static class PropertyNormalizer implements WordNormalizer{
		private final Properties prop=new Properties();
		private final String prefix,suffix;
		private final String[] rules;
		public PropertyNormalizer(String name,String prefix,String suffix){
			this.prefix=prefix;
			this.suffix=suffix;
			try{
				prop.load(E2CNormalizers.class.getResourceAsStream("/com/github/chungkwong/sillytranslate/lex/"+name+"_en.properties"));
			}catch(IOException ex){
				Logger.getLogger(E2CNormalizers.class.getName()).log(Level.SEVERE,null,ex);
			}
			String rule=prop.getProperty("RULES","");
			ArrayList<String> term=new ArrayList<>();
			if(!rule.isEmpty())
				for(int i=0;;){
					int j=rule.indexOf(',',i)+1;
					if(j==0){
						term.add(rule.substring(i));
						break;
					}else{
						term.add(rule.substring(i,j-1));
						i=j;
					}
				}
			rules=term.toArray(new String[0]);
		}
		@Override
		public String toNormal(String word){
			if(prop.containsKey(word))
				return prop.getProperty(word);
			for(int i=0;i<rules.length;i+=2){
				if(word.endsWith(rules[i])){
					return word.substring(0,word.length()-rules[i].length())+rules[i+1];
				}
			}
			return word;
		}
		@Override
		public String toSpecial(String word){
			return prefix+word+suffix;
		}
	}
}
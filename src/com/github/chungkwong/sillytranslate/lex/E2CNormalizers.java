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

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class E2CNormalizers{
	private static final WordNormalizer[] NORMALIZERS=new WordNormalizer[]{
		new PluralNormalizer(),new PastTenseNormalizer(),new ContinuingTenseNormalizer()
	};
	public static WordNormalizer[] getNormalizers(){
		return NORMALIZERS;
	}
	private static class PastTenseNormalizer implements WordNormalizer{
		@Override
		public String toNormal(String word){
			return changeSuffix("ied","y","ed","");
		}
		@Override
		public String toSpecial(String word){
			return word+"了";
		}
	}
	private static class ContinuingTenseNormalizer implements WordNormalizer{
		@Override
		public String toNormal(String word){
			return changeSuffix(word,"ing","");
		}
		@Override
		public String toSpecial(String word){
			return "正在"+word;
		}
	}
	private static class PluralNormalizer implements WordNormalizer{
		@Override
		public String toNormal(String word){
			return changeSuffix(word,"ies","y","es","","s","");
		}
		@Override
		public String toSpecial(String word){
			return word;
		}
	}
	private static boolean isVowel(int c){
		return c=='e'||c=='a'||c=='o'||c=='i'||c=='u';
	}
	private static String changeSuffix(String word,String... replacement){
		for(int i=0;i<replacement.length;i+=2){
			if(word.endsWith(replacement[i])){
				return word.substring(0,word.length()-replacement[i].length())+replacement[i+1];
			}
		}
		return word;
	}
}
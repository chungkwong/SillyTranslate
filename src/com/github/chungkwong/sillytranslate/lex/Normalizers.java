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
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Normalizers{
	private static final HashMap<Locale,WordNormalizer[]> NORMALIZERS=new HashMap<>();
	public static WordNormalizer[] getNormalizers(Locale locale){
		WordNormalizer[] normalizers=NORMALIZERS.get(locale);
		if(normalizers==null){
			normalizers=loadNormalizers(locale);
			NORMALIZERS.put(locale,normalizers);
		}
		return normalizers;
	}
	private static WordNormalizer[] loadNormalizers(Locale locale){
		if(locale.getLanguage().equals("en")){
			return new WordNormalizer[]{
				new PropertyNormalizer("OWN","","的"),
				new PropertyNormalizer("CONTINUING","正在",""),
				new PropertyNormalizer("PLURAL","",""),
				new PropertyNormalizer("PAST","","了"),
				new PropertyNormalizer("PERFECT","","过")
			};
		}else{
			return new WordNormalizer[0];
		}
	}
}

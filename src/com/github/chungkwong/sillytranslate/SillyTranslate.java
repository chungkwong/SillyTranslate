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
package com.github.chungkwong.sillytranslate;
import com.github.chungkwong.sillytranslate.lex.*;
import java.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SillyTranslate{
	private static final Map<String,Function<Configure,Lex>> LEX_BUILDER=new LinkedHashMap<>();
	public static Lex buildLex(String name,Configure conf){
		return LEX_BUILDER.get(name).apply(conf);
	}
	public static Set<String> getLexBuilderNames(String name,Configure conf){
		return LEX_BUILDER.keySet();
	}
	public static void registerLexBuilder(String name,Function<Configure,Lex> builder){
		LEX_BUILDER.put(name,builder);
	}
	static{
		registerLexBuilder("JavaLex",(conf)->new JavaLex(conf.getInputLocale()));
		registerLexBuilder("SimpleLex",(conf)->new SimpleLex());
		registerLexBuilder("PrefixLex",(conf)->new PrefixLex(conf.getDictionary(),conf.getInputLocale()));
	}
}
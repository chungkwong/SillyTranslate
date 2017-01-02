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
import com.github.chungkwong.sillytranslate.surrounding.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SillyTranslate{
	private static final File BASE=new File(System.getProperty("user.home"),".SillyTranslate");
	private static final Map<String,Function<Configure,Lex>> LEX_BUILDER=new LinkedHashMap<>();
	private static final Map<String,DocumentTranslatorEngine> FORMATS=new LinkedHashMap<>();
	static void init(){

	}
	static void loadPlugIn(String[] clssses){
		if(clssses.length==0)
			return;
		ClassLoader loader=getPluginLoader();
		for(String cls:clssses){
			try{
				loader.loadClass(cls).getMethod("init").invoke(null);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		}
	}
	public static void bar(String str){
		System.out.println(":::"+str);
	}
	static ClassLoader getPluginLoader(){
		File[] jars=BASE.listFiles((dir,name)->name.endsWith(".jar"));
		URL[] urls=Arrays.stream(jars).map((jar)->{
			try{
				return jar.toURI().toURL();
			}catch(MalformedURLException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
				return null;
			}
		}).toArray(URL[]::new);
		return new URLClassLoader(urls,SillyTranslate.class.getClassLoader());
	}
	public static Lex buildLex(String name,Configure conf){
		return LEX_BUILDER.get(name).apply(conf);
	}
	public static Set<String> getLexBuilderNames(String name,Configure conf){
		return LEX_BUILDER.keySet();
	}
	public static void registerLexBuilder(String name,Function<Configure,Lex> builder){
		LEX_BUILDER.put(name,builder);
	}
	public static DocumentTranslatorEngine getDocumentTranslatorEngine(String name){
		return FORMATS.get(name);
	}
	public static Collection<DocumentTranslatorEngine> getDocumentTranslatorEngines(){
		return FORMATS.values();
	}
	public static void registerDocumentTranslator(String name,DocumentTranslatorEngine engine){
		FORMATS.put(name,engine);
	}
	private static DictionaryParser DICTIONARY_PARSER=new DefaultDictionaryParser();
	public static DictionaryParser getDictionaryParser(){
		return DICTIONARY_PARSER;
	}
	public static void setDictionaryParser(DictionaryParser parser){
		DICTIONARY_PARSER=parser;
	}
	static{
		registerLexBuilder("JavaLex",(conf)->new JavaLex(conf.getInputLocale()));
		registerLexBuilder("SimpleLex",(conf)->new SimpleLex());
		registerLexBuilder("PrefixLex",(conf)->new PrefixLex(conf.getDictionary(),conf.getInputLocale()));
		registerDocumentTranslator("plain",new PlainTextTranslator());
		registerDocumentTranslator("properties",new PropertiesTranslator());
		registerDocumentTranslator("po",new POTranslator());
		registerDocumentTranslator("xml",new XMLTranslator());
		registerDocumentTranslator("odf",new ODFTranslator());
		registerDocumentTranslator("ooxml",new OOXMLTranslator());
		registerDocumentTranslator("groff",new GroffTranslator());
		registerDocumentTranslator("tex",new TeXTranslator());
	}
}
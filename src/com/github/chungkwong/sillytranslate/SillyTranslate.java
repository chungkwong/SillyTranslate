/*
 * Copyright (C) 2016-2017 Chan Chung Kwong <1m02math@126.com>
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
import java.util.stream.*;
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
		if(clssses.length==0||(clssses.length==1&&clssses[0].isEmpty()))
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
	private static final HashMap<String,WordNormalizer[]> NORMALIZERS=new HashMap<>();
	public static WordNormalizer[] registerNormalizers(Locale from,Locale to,WordNormalizer[] normalizers){
		return NORMALIZERS.put(encodeDirection(from,to),normalizers);
	}
	public static WordNormalizer[] getNormalizers(Locale from,Locale to){
		return NORMALIZERS.getOrDefault(encodeDirection(from,to),new WordNormalizer[0]);
	}
	private static String encodeDirection(Locale in,Locale out){
		return in.getLanguage()+":"+out.getLanguage();
	}
	private static final HashMap<String,Function<Stream<String>,String>> SENTENCE_BUILDER=new HashMap<>();
	public static Function<Stream<String>,String> registerSentenceBuilder(String language,Function<Stream<String>,String> builder){
		return SENTENCE_BUILDER.put(language,builder);
	}
	public static String buildSentence(Stream<String> tokens,Locale locale){
		Function<Stream<String>,String> builder=SENTENCE_BUILDER.get(locale.getLanguage());
		if(builder!=null){
			return builder.apply(tokens);
		}else{
			return tokens.collect(Collectors.joining());
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
	private static final HashMap<String,Properties> LANGUAGE_LEX=new HashMap<>();
	private static final Properties DEFAULT_LANGUAGE_LEX=loadProperties("/com/github/chungkwong/sillytranslate/lex/LANGUAGE.properties");
	public static void registerLanguageLex(Locale locale,Properties prop){
		LANGUAGE_LEX.put(locale.toString(),prop);
	}
	public static Properties getLanguageLex(Locale locale){
		return LANGUAGE_LEX.getOrDefault(locale.toString(),LANGUAGE_LEX.getOrDefault(locale.getLanguage(),DEFAULT_LANGUAGE_LEX));
	}
	private static final HashMap<String,IntPredicate> LANGUAGE_CHARACTER=new HashMap<>();
	private static final IntPredicate DEFAULT_LANGUAGE_CHARACTER=(i)->true;
	public static void registerCharacterClassifier(Locale locale,IntPredicate pred){
		LANGUAGE_CHARACTER.put(locale.toString(),pred);
	}
	public static IntPredicate getCharacterClassifier(Locale locale){
		return LANGUAGE_CHARACTER.getOrDefault(locale.toString(),LANGUAGE_CHARACTER.getOrDefault(locale.getLanguage(),DEFAULT_LANGUAGE_CHARACTER));
	}
	private static Properties loadProperties(String path){
		Properties prop=new Properties();
		try{
			prop.load(SillyTranslate.class.getResourceAsStream(path));
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		return prop;
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
		registerSentenceBuilder("en",SillyTranslate::buildEnglish);
		registerLanguageLex(Locale.ENGLISH,loadProperties("/com/github/chungkwong/sillytranslate/lex/LANGUAGE_en.properties"));
		registerLanguageLex(Locale.CHINESE,loadProperties("/com/github/chungkwong/sillytranslate/lex/LANGUAGE_zh.properties"));
		registerCharacterClassifier(Locale.CHINESE,(i)->!Character.UnicodeBlock.of(i).equals(Character.UnicodeBlock.BASIC_LATIN));
	}
}
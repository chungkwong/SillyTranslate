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
import com.github.chungkwong.sillytranslate.sentence.*;
import com.github.chungkwong.sillytranslate.surrounding.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Registry{
	public static final List<DocumentTranslatorEngine> DOCUMENT_TYPE=new ArrayList<>();
	public static final List<SentenceTranslatorEngine> SENTENCE_TRANSLATOR_ENGINES=new ArrayList<>();
	public static List<Lex> TOKENIZERS=new ArrayList<>();
	static{
		DOCUMENT_TYPE.add(new PlainTextTranslator());
		DOCUMENT_TYPE.add(new PropertiesTranslator());
		DOCUMENT_TYPE.add(new XMLTranslator());
		DOCUMENT_TYPE.add(new ODFTranslator());
		DOCUMENT_TYPE.add(new OOXMLTranslator());
		DOCUMENT_TYPE.add(new POTranslator());
		TOKENIZERS.add(new SimpleLex());
	}
	public static void main(String[] args){
		Registry.class.getClassLoader();
	}
}
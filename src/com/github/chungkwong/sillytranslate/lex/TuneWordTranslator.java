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
import com.github.chungkwong.sillytranslate.ui.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TuneWordTranslator extends AbstractWordTranslator{
	private final NavigableDictionary dict;
	private final DictionaryHintExtractor hintExtractor;
	private final WordMemory memory;
	public TuneWordTranslator(NavigableDictionary dict,WordMemory memory,Locale locale){
		this.dict=dict;
		this.hintExtractor=new DictionaryHintExtractor(locale,false);
		this.memory=memory;
	}
	@Override
	public void accept(List<Token> source,Consumer<Iterator<Token>> callback){
		callback.accept(source.stream().map((t)->new Token(Token.Type.WORD,translate(t.getText()),""))
				.collect(Collectors.toList()).iterator());
	}
	public String translate(String word){
		Hint[] hints=hintExtractor.extractHint(word,"",dict,memory);
		if(hints.length>0)
			return hints[0].getInputText().substring(0,hints[0].getInputText().length()-1);
		else if(!word.isEmpty()&&Character.isUpperCase(word.codePointAt(0))){
			hints=hintExtractor.extractHint(toUsual(word),"",dict,memory);
			if(hints.length>0)
				return toFirst(hints[0].getInputText().substring(0,hints[0].getInputText().length()-1));
			else
				return word;
		}else
			return word;
	}
	private static String toUsual(String word){
		int second=word.offsetByCodePoints(0,1);
		return word.substring(0,second).toLowerCase()+word.substring(second);
	}
	private static String toFirst(String word){
		if(word.isEmpty())
			return "";
		else{
			int second=word.offsetByCodePoints(0,1);
			return word.substring(0,second).toUpperCase()+word.substring(second);
		}
	}
}
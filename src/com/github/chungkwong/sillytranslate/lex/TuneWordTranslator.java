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
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TuneWordTranslator extends AbstractWordTranslator{
	private final KeyValueDictionary dict;
	public TuneWordTranslator(KeyValueDictionary dict){
		this.dict=dict;
	}
	@Override
	public void accept(List<Token> source,Consumer<Iterator<Token>> callback){
		callback.accept(source.stream().map((t)->new Token(Token.Type.WORD,translate(t.getText()),""))
				.collect(Collectors.toList()).iterator());
	}
	public String translate(String word){
		if(dict.getCurrentWord(word).equals(word))
			return dict.getMeaning(word);
		else
			return word;
	}
}
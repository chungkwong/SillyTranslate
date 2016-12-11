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
import com.github.chungkwong.sillytranslate.lex.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class IntegratedSentenceTranslator implements SentenceTranslatorEngine{
	private final SentenceTranslatorEngine[] engines;
	public IntegratedSentenceTranslator(SentenceTranslatorEngine... engines){
		this.engines=engines;
	}
	@Override
	public List<String> getTranslation(List<Token> words){
		List<String> translations=new ArrayList<>();
		for(SentenceTranslatorEngine engine:engines)
			translations.addAll(engine.getTranslation(words));
		return translations;
	}
	@Override
	public String getName(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("INTEGRATED");
	}
}
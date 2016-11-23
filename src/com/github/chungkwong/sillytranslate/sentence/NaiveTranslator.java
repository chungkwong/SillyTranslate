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
import com.github.chungkwong.sillytranslate.util.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NaiveTranslator implements SentenceTranslatorEngine{
	private final int limit;
	public NaiveTranslator(int limit){
		this.limit=limit;
	}
	@Override
	public List<String> getTranslation(List<Token> words){
		List<String> list=new ArrayList<>();
		Permutator<Token> perm=new Permutator<>(words);
		List<Token> p=words;
		int i=0;
		while(p!=null&&++i<=limit){
			list.add(p.stream().map((t)->t.getText()).collect(Collectors.joining()));
			p=perm.nextPermutation();
		}
		return list;
	}
	@Override
	public String getName(){
		return "Brute force";
	}
}

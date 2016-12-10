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
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class KeyValueDictionary implements NavigableDictionary{
	private final String source;
	private final String name;
	private final TreeMap<String,String> dictionary=new TreeMap<>();
	public KeyValueDictionary(File file) throws FileNotFoundException{
		source=file.getAbsolutePath();
		name="{"+file.getName()+"}";
		Scanner in=new Scanner(file);
		in.useDelimiter("[=\\n\\r]+");
		while(in.hasNext()){
			dictionary.put(in.next(),in.next());
		}
	}
	@Override
	public String getMeaning(String word){
		return dictionary.getOrDefault(word,"");
	}
	@Override
	public String getCurrentWord(String word){
		return dictionary.ceilingKey(word);
	}
	@Override
	public String getNextWord(String word){
		return dictionary.higherKey(word);
	}
	@Override
	public String getSource(){
		return source;
	}
	@Override
	public String toString(){
		return name;
	}
}

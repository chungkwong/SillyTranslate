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
public class IntegratedDictionary implements NavigableDictionary{
	private final List<NavigableDictionary> dictionarys=new ArrayList<>();
	public IntegratedDictionary(){
	}
	public void addDictionary(NavigableDictionary dictionary){
		dictionarys.add(dictionary);
	}
	public void addDictionary(NavigableDictionary dictionary,int priority){
		dictionarys.add(priority,dictionary);
	}
	public NavigableDictionary getDictionary(int priority){
		return dictionarys.get(priority);
	}
	public void removeAllDictionary(){
		dictionarys.clear();
	}
	public void removeDictionary(int priority){
		dictionarys.remove(priority);
	}
	public int getNumberOfDictionary(){
		return dictionarys.size();
	}
	@Override
	public String getMeaning(String word){
		StringBuilder buf=new StringBuilder();
		for(NavigableDictionary dictionary:dictionarys){
			String curr=dictionary.getMeaning(word);
			if(curr!=null)
				buf.append(dictionary).append('\n').append(curr).append('\n').append('\n');
		}
		return buf.toString();
	}
	@Override
	public String getCurrentWord(String word){
		String best=null;
		for(NavigableDictionary dictionary:dictionarys){
			String curr=dictionary.getCurrentWord(word);
			if(curr!=null&&(best==null||curr.compareTo(best)<0))
				best=curr;
		}
		return best;
	}
	@Override
	public String getNextWord(String word){
		String best=null;
		for(NavigableDictionary dictionary:dictionarys){
			String curr=dictionary.getNextWord(word);
			if(curr!=null&&(best==null||curr.compareTo(best)<0))
				best=curr;
		}
		return best;
	}
	@Override
	public String getSource(){
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public String toString(){
		return dictionarys.toString();
	}
}

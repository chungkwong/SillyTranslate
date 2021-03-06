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

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Meaning{
	private final String text;
	private int count;
	private final String tag;
	public Meaning(String text,String tag,int count){
		this.text=text;
		this.count=count;
		this.tag=tag;
	}
	public String getText(){
		return text;
	}
	public String getTag(){
		return tag;
	}
	public int getCount(){
		return count;
	}
	public void used(int times){
		count+=times;
	}
	@Override
	public String toString(){
		return "[tag="+getTag()+",text="+getText()+",count="+getCount()+"]";
	}
}

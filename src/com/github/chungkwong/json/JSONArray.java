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
package com.github.chungkwong.json;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class JSONArray implements JSONStuff{
	private final List<JSONStuff> elements;
	public JSONArray(List<JSONStuff> elements){
		this.elements=elements;
	}
	@Override
	public String toString(){
		return elements.stream().map((o)->o.toString()).collect(Collectors.joining(",","[","]"));
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof JSONArray&&((JSONArray)obj).elements.equals(elements);
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=17*hash+Objects.hashCode(this.elements);
		return hash;
	}
	public List<JSONStuff> getElements(){
		return elements;
	}
}

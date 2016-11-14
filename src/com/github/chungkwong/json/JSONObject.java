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
public class JSONObject implements JSONStuff{
	private final Map<JSONStuff,JSONStuff> members;
	public JSONObject(Map<JSONStuff,JSONStuff> members){
		this.members=members;
	}
	@Override
	public String toString(){
		return members.entrySet().stream().map((o)->o.getKey()+":"+o.getValue()).collect(Collectors.joining(",","{","}"));
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof JSONObject&&((JSONObject)obj).members.equals(members);
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=19*hash+Objects.hashCode(this.members);
		return hash;
	}
	public Map<JSONStuff,JSONStuff> getMembers(){
		return members;
	}
}

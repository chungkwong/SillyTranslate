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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class JSONNumber implements JSONStuff{
	private final Number value;
	public JSONNumber(Number value){
		this.value=value;
	}
	@Override
	public String toString(){
		return Objects.toString(value);
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof JSONNumber&&((JSONNumber)obj).value.doubleValue()==value.doubleValue();
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=73*hash+Objects.hashCode(this.value.doubleValue());
		return hash;
	}
	public Number getValue(){
		return value;
	}
}

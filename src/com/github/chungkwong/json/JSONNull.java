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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class JSONNull implements JSONStuff{
	public static final JSONNull INSTANCE=new JSONNull();
	private JSONNull(){
	}
	@Override
	public String toString(){
		return "null";
	}
	@Override
	public boolean equals(Object obj){
		return INSTANCE==obj;
	}
	@Override
	public int hashCode(){
		int hash=7;
		return hash;
	}
}

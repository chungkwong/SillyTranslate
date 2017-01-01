/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
package com.github.chungkwong.sillytranslate.util;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OptionParser{
	private final HashMap<String,String> longOption=new HashMap<>();
	private final String shortOption;
	private final List<String> files=new ArrayList<>();
	public OptionParser(String[] args){
		StringBuilder s=new StringBuilder();
		boolean noopt=false;
		for(String param:args){
			if(noopt||!param.startsWith("-")){
				files.add(param);
			}else if(param.startsWith("--")){
				if(param.length()==2){
					noopt=true;
				}else{
					int i=param.indexOf('=');
					if(i==-1)
						this.longOption.put(param.substring(2),"");
					else
						this.longOption.put(param.substring(2,i),param.substring(i+1));
				}
			}else{
				s.append(param.substring(1));
			}
		}
		shortOption=s.toString();
	}
	public boolean hasShortOption(int c){
		return shortOption.indexOf(c)!=-1;
	}
	public String getLongOption(String key){
		return longOption.get(key);
	}
	public List<String> getFiles(){
		return files;
	}
}
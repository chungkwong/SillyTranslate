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
package com.github.chungkwong.sillytranslate.lex;
import com.github.chungkwong.sillytranslate.ui.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DefaultDictionaryParser implements DictionaryParser{
	@Override
	public void parse(String text,String word,String prefix,List<Hint> hints){
		int prefixLen=prefix.length();
		String type="";
		for(int i=0;i<text.length();i++){
			char c=text.charAt(i);
			if(c=='['){
				while(++i<text.length()&&text.charAt(i)!=']');
			}else if(c=='{'){
				while(++i<text.length()&&text.charAt(i)!='}');
			}else if(Character.isWhitespace(c)){

			}else{
				int j=i;
				for(;j<text.length();j++){
					int d=text.charAt(j);
					if(d==','||d==';'||d=='\n'||d=='\r')
						break;
				}
				String token=text.substring(i,j);
				if(token.endsWith(".")&&!token.endsWith("...")){
					type=token;
				}else if(!token.isEmpty()&&token.startsWith(prefix)){
					token+=":"+type;
					if(token.length()>prefixLen)
						hints.add(new SimpleHint(token,token.substring(prefixLen),null,""));
				}
				if(j>i)
					i=j-1;
			}
		}
	}

}

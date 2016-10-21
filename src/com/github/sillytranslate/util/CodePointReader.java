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
package com.github.sillytranslate.util;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodePointReader{
	private final Reader in;
	private final LinkedList<Integer> buffer;
	public CodePointReader(Reader in){
		this.in=in;
		this.buffer=new LinkedList<>();
	}
	public int read() throws IOException{
		if(buffer.isEmpty()){
			int c=in.read();
			if(Character.isHighSurrogate((char)c))
				return Character.toCodePoint((char)c,(char)in.read());
			else
				return c;
		}else
			return buffer.poll();
	}
	public void unread(int c) throws IOException{
		if(c!=-1){
			buffer.addFirst(c);
		}
	}
	public void unread(List<Integer> cs){
		buffer.addAll(0,cs);
	}
}

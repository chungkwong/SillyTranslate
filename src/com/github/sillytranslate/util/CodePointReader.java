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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodePointReader{
	private final PushbackReader in;
	public CodePointReader(Reader in,int size){
		this.in=new PushbackReader(in,size*2);
	}
	public int read() throws IOException{
		int c=in.read();
		if(Character.isHighSurrogate((char)c))
			return Character.toCodePoint((char)c,(char)in.read());
		else
			return c;
	}
	public void unread(int c) throws IOException{
		if(Character.isSupplementaryCodePoint(c)){
			in.unread(Character.lowSurrogate(c));
			in.unread(Character.highSurrogate(c));
		}else{
			in.unread(c);
		}
	}
}

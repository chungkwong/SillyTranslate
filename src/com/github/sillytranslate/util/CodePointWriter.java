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
public class CodePointWriter extends Writer{
	private final Writer src;
	public CodePointWriter(Writer src){
		this.src=src;
	}
	public void writeCodepoint(int c) throws IOException{
		if(Character.isSupplementaryCodePoint(c)){
			write(Character.highSurrogate(c));
			write(Character.lowSurrogate(c));
		}else
			write(c);
	}
	@Override
	public void write(char[] cbuf,int off,int len) throws IOException{
		src.write(cbuf,off,len);
	}
	@Override
	public void flush() throws IOException{
		src.flush();
	}
	@Override
	public void close() throws IOException{
		src.close();
	}
}

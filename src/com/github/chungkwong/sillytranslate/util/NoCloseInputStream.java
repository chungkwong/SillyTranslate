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
package com.github.chungkwong.sillytranslate.util;
import java.io.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */

public class NoCloseInputStream extends InputStream{
	private final InputStream back;
	public NoCloseInputStream(InputStream back){
		this.back=back;
	}
	@Override
	public int read() throws IOException{
		return back.read();
	}
	@Override
	public int read(byte[] b) throws IOException{
		return back.read(b);
	}
	@Override
	public int read(byte[] b,int off,int len) throws IOException{
		return back.read(b,off,len);
	}
	@Override
	public void close() throws IOException{

	}
	@Override
	public int available() throws IOException{
		return back.available();
	}
	@Override
	public synchronized void mark(int readlimit){
		back.mark(readlimit);
	}
	@Override
	public boolean markSupported(){
		return back.markSupported();
	}
	@Override
	public synchronized void reset() throws IOException{
		back.reset();
	}
	@Override
	public long skip(long n) throws IOException{
		return back.skip(n);
	}
}
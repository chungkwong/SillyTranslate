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
package com.github.sillytranslate.surrounding;
import com.github.sillytranslate.util.*;
import java.io.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class HTMLFormat implements DocumentFormat{
	private final CodePointReader in;
	private final Writer out;
	public HTMLFormat(Reader in,Writer out){
		this.in=new CodePointReader(in);
		this.out=out;
	}
	@Override
	public String nextTextToBeTranslated() throws IOException{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void insertTranslation(String str) throws IOException{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
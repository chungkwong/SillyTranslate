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
package com.github.chungkwong.sillytranslate.lex;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BypassLexView extends AbstractLexView{
	@Override
	public void accept(Lex source,Consumer<List<Token>> callback){
		Token t;
		ArrayList<Token> lst=new ArrayList<>();
		try{
			while((t=source.next())!=null)
				lst.add(t);
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		callback.accept(lst);
	}
}

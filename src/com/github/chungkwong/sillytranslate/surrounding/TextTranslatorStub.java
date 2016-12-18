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
package com.github.chungkwong.sillytranslate.surrounding;
import java.util.concurrent.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextTranslatorStub implements TextTranslator{
	private static final ExecutorService executor=Executors.newSingleThreadExecutor();
	@Override
	public void translate(String text,DocumentTranslatorEngine callback){
		executor.execute(()->callback.textTranslated("#"+text+"#"));
		//callback.textTranslated("#"+text+"#");
	}
	@Override
	public JComponent getUserInterface(){
		return new JLabel();
	}
	@Override
	public String getName(){
		return "Stub";
	}
	@Override
	public String getUsage(){
		return "";
	}
	public static void close(){
		executor.shutdown();
	}
}

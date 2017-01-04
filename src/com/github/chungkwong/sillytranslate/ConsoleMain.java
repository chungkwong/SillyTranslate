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
package com.github.chungkwong.sillytranslate;
import com.github.chungkwong.sillytranslate.surrounding.*;
import com.github.chungkwong.sillytranslate.util.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ConsoleMain{
	private static final ExecutorService executor=Executors.newSingleThreadExecutor();
	public ConsoleMain(InputStream in,OutputStream out,DocumentTranslatorEngine format,TextTranslator translator){
		format.setTextTranslator(translator);
		format.setOnFinished(()->{
			try{
				in.close();
				out.close();
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			}
		});
		format.start(in,out);
	}
	public static void main(String[] args)throws Exception{
		Logger.getGlobal().setLevel(Level.SEVERE);
		Logger.getGlobal().addHandler(new java.util.logging.ConsoleHandler());
		SillyTranslate.init();
		OptionParser options=new OptionParser(args);
		DocumentTranslatorEngine format=SillyTranslate.getDocumentTranslatorEngine(options.getLongOption("format"));
		TextTranslator translator=new ConsoleSimpleTextTranslator(new Scanner(System.in));
		String outfile=options.getLongOption("output");
		OutputStream out=outfile==null?System.out:new FileOutputStream(outfile);
		List<String> files=options.getFiles();
		if(files.isEmpty()){
			new ConsoleMain(System.in,out,format,translator);
		}else{
			for(String file:files){
				new ConsoleMain(new FileInputStream(file),out,format,translator);
			}
		}
	}
	static class ConsoleSimpleTextTranslator implements TextTranslator{
		private final Scanner in;
		public ConsoleSimpleTextTranslator(Scanner in){
			this.in=in;
		}
		@Override
		public void translate(String text,DocumentTranslatorEngine callback){
			System.err.println(java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("ENTER_TRANSLATION")+text);
			executor.submit(()->callback.textTranslated(in.nextLine()));
		}
		@Override
		public JComponent getUserInterface(){
			return null;
		}
		@Override
		public String getName(){
			return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("MANUAL");
		}
		@Override
		public String getUsage(){
			return "";
		}
	}
}

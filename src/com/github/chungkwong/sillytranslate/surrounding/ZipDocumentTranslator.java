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
import com.github.chungkwong.sillytranslate.util.*;
import java.io.*;
import java.util.logging.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class ZipDocumentTranslator implements DocumentTranslatorEngine{
	private TextTranslator translator;
	private Runnable callback;
	private ZipInputStream zipIn;
	private ZipOutputStream zipOut;
	private DocumentTranslatorEngine base;
	@Override
	public void setTextTranslator(TextTranslator translator){
		this.translator=translator;
	}
	@Override
	public void setOnFinished(Runnable callback){
		this.callback=callback;
	}
	@Override
	public void start(InputStream in,OutputStream out){
		zipIn=new ZipInputStream(in);
		zipOut=new ZipOutputStream(out);
		onFileFinished();
	}
	private void onFileFinished(){
		try{
			byte[] buf=new byte[4096];
			ZipEntry entry=zipIn.getNextEntry();
			while(entry!=null){
				zipOut.putNextEntry(new ZipEntry(entry.getName()));
				base=checkForBaseEngine(entry);
				if(base!=null){
					base.setOnFinished(()->onFileFinished());
					base.setTextTranslator(translator);
					base.start(new NoCloseInputStream(zipIn),zipOut);
					return;
				}
				int count;
				while((count=zipIn.read(buf))>=0){
					zipOut.write(buf,0,count);
				}
				entry=zipIn.getNextEntry();
			}
			zipIn.close();
			zipOut.finish();
			base=null;
			zipIn=null;
			zipOut=null;
			callback.run();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	@Override
	public void textTranslated(String text){
		base.textTranslated(text);
	}
	protected abstract DocumentTranslatorEngine checkForBaseEngine(ZipEntry entry);
}
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
import java.io.*;
import java.util.logging.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DocxTranslator implements DocumentTranslatorEngine{
	private TextTranslator translator;
	private Runnable callback;
	private ZipInputStream zipIn;
	private ZipOutputStream zipOut;
	private XMLTranslator xmlTranslator;
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
		try{
			zipIn=new ZipInputStream(in);
			zipOut=new ZipOutputStream(out);
			byte[] buf=new byte[4096];
			ZipEntry entry=zipIn.getNextEntry();
			while(entry!=null){
				System.out.println(entry);
				zipOut.putNextEntry(new ZipEntry(entry.getName()));
				if(entry.getName().endsWith("document.xml")){
					xmlTranslator=new XMLTranslator();
					xmlTranslator.setOnFinished(()->onXMLFinished());
					xmlTranslator.setTextTranslator(translator);
					xmlTranslator.start(new NoCloseInputStream(zipIn),zipOut);
					textTranslated("");
					return;
				}
				int count;
				while((count=zipIn.read(buf))>=0){
					zipOut.write(buf,0,count);
				}
				entry=zipIn.getNextEntry();
			}
		}catch(IOException ex){
			Logger.getLogger(DocxTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private void onXMLFinished(){
		try{
			byte[] buf=new byte[4096];
			ZipEntry entry=zipIn.getNextEntry();
			while(entry!=null){
				zipOut.putNextEntry(new ZipEntry(entry.getName()));
				int count;
				while((count=zipIn.read(buf))>=0){
					zipOut.write(buf,0,count);
				}
				entry=zipIn.getNextEntry();
			}
			//zipIn.close();
			zipOut.finish();
			callback.run();
		}catch(IOException ex){
			Logger.getLogger(DocxTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void textTranslated(String text){
		xmlTranslator.textTranslated(text);
	}
	public static void main(String[] args)throws Exception{
		DocxTranslator docxTranslator=new DocxTranslator();
		docxTranslator.setOnFinished(()->System.err.println("end"));
		docxTranslator.setTextTranslator(new TextTranslatorStub());
		docxTranslator.start(new FileInputStream("/home/kwong/下载/20161119044225181.docx"),
				new FileOutputStream("/home/kwong/下载/20161119044225181_clone.docx"));
	}
}
class NoCloseInputStream extends InputStream{
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
}
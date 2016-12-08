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
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StardictDictionary implements NavigableDictionary{
	private static final Charset UTF8=Charset.forName("UTF-8");
	private final CharsetDecoder UTF8_DECODER=UTF8.newDecoder();
	private final HashMap<String,String> info=new HashMap<>();
	private final TreeMap<String,Interval> dictionary=new TreeMap<>();
	private final String source;
	private byte[] pool;
	public StardictDictionary(File folder) throws IOException{
		this.source=folder.getAbsolutePath();
		File[] files=folder.listFiles();
		for(File file:files)
			if(file.getName().endsWith(".ifo")){
				initInfo(file);
				break;
			}
		for(File file:files)
			if(file.getName().endsWith(".dict")){
				pool=initDict(file);
				break;
			}
		for(File file:files)
			if(file.getName().endsWith(".idx")){
				initIndex(file);
				break;
			}
	}
	public TreeMap<String,Interval> getDictionary(){
		return dictionary;
	}
	private void initInfo(File file) throws IOException{
		Files.lines(file.toPath(),UTF8).forEach((line)->{
			int split=line.indexOf('=');
			if(split!=-1)
				info.put(line.substring(0,split),line.substring(split+1));
		});
	}
	private void initIndex(File file) throws IOException{
		try(DataInputStream in=new DataInputStream(new BufferedInputStream(new FileInputStream(file)))){
			ByteBuffer buf=java.nio.ByteBuffer.allocate(256);
			CharBuffer cbuf=java.nio.CharBuffer.allocate(256);
			while(true){
				byte b=in.readByte();
				if(b!=0){
					buf.put(b);
				}else{
					buf.limit(buf.position());
					buf.rewind();
					UTF8_DECODER.decode(buf,cbuf,true);
					cbuf.limit(cbuf.position());
					cbuf.rewind();
					dictionary.put(cbuf.toString(),new Interval(in.readInt(),in.readInt()));
					buf.clear();
					cbuf.clear();
					buf.limit(256);
				}
			}
		}catch(EOFException e){

		}
	}
	private byte[] initDict(File file) throws IOException{
		return Files.readAllBytes(file.toPath());
	}
	public String getMetaData(String key){
		return info.get(key);
	}
	@Override
	public String getMeaning(String word){
		Interval interval=dictionary.get(word);
		if(interval!=null)
			try{
				CharBuffer cbuf=UTF8_DECODER.decode(ByteBuffer.wrap(pool,interval.getOffset(),interval.getSize()));
				return cbuf.toString();
			}catch(CharacterCodingException ex){
				Logger.getGlobal().log(Level.FINEST,ex.getLocalizedMessage(),ex);
			}
		return "";
	}
	@Override
	public String getCurrentWord(String word){
		return dictionary.ceilingKey(word);
	}
	@Override
	public String getNextWord(String word){
		return dictionary.higherKey(word);
	}
	@Override
	public String toString(){
		return info.toString();
	}
	public String getSource(){
		return source;
	}
	public static void main(String[] args) throws IOException{
		StardictDictionary d=new StardictDictionary(new File("/home/kwong/projects/stardict-kdic-computer-gb-2.4.2"));
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine())
			System.out.println(d.getMeaning(in.nextLine()));
	}
}
class Interval{
	private final int offset,size;
	public Interval(int offset,int size){
		this.offset=offset;
		this.size=size;
	}
	public int getOffset(){
		return offset;
	}
	public int getSize(){
		return size;
	}
}

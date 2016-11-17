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
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordMemory{
	private final HashMap<String,List<Meaning>> map=new HashMap<>();
	private final File cache;
	public WordMemory(){
		this.cache=null;
	}
	public WordMemory(String path){
		this.cache=new File(path);
		try(DataInputStream sc=new DataInputStream(new BufferedInputStream(new FileInputStream(cache)))){
			cache.createNewFile();
			while(true){
				String word=sc.readUTF();
				List<Meaning> meanings=new ArrayList<>();
				while(sc.readBoolean()){
					meanings.add(new Meaning(sc.readUTF(),sc.readUTF(),sc.readInt()));
				}
				map.put(word,meanings);
			}
		}catch(IOException ex){
			Logger.getLogger(WordMemory.class.getName()).log(Level.SEVERE,null,ex);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(this::save));
	}
	public void useMeaning(String word,String meaning,String tag){
		List<Meaning> lst=map.get(word);
		if(lst==null){
			lst=new ArrayList<>();
			map.put(word,lst);
		}
		Optional<Meaning> entry=lst.stream().filter((m)->m.getText().equals(meaning)&&m.getTag().equals(tag)).findAny();
		if(entry.isPresent()){
			entry.get().used();
		}else{
			lst.add(new Meaning(meaning,tag,1));
		}
	}
	public List<Meaning> getMeanings(String word){
		return map.get(word);
	}
	public void save(){
		try(DataOutputStream to=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(cache)))){
			for(Map.Entry<String,List<Meaning>> entry:map.entrySet()){
				to.writeUTF(entry.getKey());
				for(Meaning m:entry.getValue()){
					to.writeBoolean(true);
					to.writeUTF(m.getText());
					to.writeUTF(m.getTag());
					to.writeInt(m.getCount());
				}
				to.writeBoolean(false);
			}
			to.flush();
		}catch(IOException ex){
			Logger.getLogger(WordMemory.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public String toString(){
		return map.toString();
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		WordMemory memory=new WordMemory("/home/kwong/useless.mem");
		memory.useMeaning("a","一","art");
		memory.useMeaning("a","一","art");
		memory.useMeaning("a","一","art");
		memory.useMeaning("a","个","ru");
		memory.useMeaning("a","a","al");
		memory.save();
		System.out.println(new WordMemory("/home/kwong/useless.mem"));
	}
}
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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordMemory{
	private final HashMap<String,List<Meaning>> map=new HashMap<>();
	public void registerMeanings(String word,List<Meaning> meanings){
		map.put(word,meanings);
	}
	public List<Meaning> getMeanings(String word){
		return map.get(word);
	}
	public static WordMemory loadWordMemory(InputStream in){
		WordMemory memory=new WordMemory();
		DataInputStream sc=new DataInputStream(in);
		try{
			while(true){
				String word=sc.readUTF();
				List<Meaning> meanings=new ArrayList<>();
				while(sc.readBoolean()){
					meanings.add(new Meaning(sc.readUTF(),sc.readUTF(),sc.readInt()));
				}
				memory.map.put(word,meanings);
			}
		}catch(IOException ex){

		}
		return memory;
	}
	public void saveTo(OutputStream out) throws UnsupportedEncodingException,IOException{
		DataOutputStream to=new DataOutputStream(out);
		for(Map.Entry<String,List<Meaning>> entry:map.entrySet()){
			to.writeUTF(entry.getKey());
			for(Meaning m:entry.getValue())
				if(m.getCount()>0){
					to.writeBoolean(true);
					to.writeUTF(m.getText());
					to.writeUTF(m.getTag());
					to.writeInt(m.getCount());
				}
			to.writeBoolean(false);
		}
		to.flush();
	}
	@Override
	public String toString(){
		return map.toString();
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		WordMemory memory=new WordMemory();
		memory.registerMeanings("k",Arrays.asList());
		memory.registerMeanings("a",Arrays.asList(new Meaning("一","art",3),new Meaning("个","ru",0),new Meaning("a","al",1)));
		memory.saveTo(new FileOutputStream("/home/kwong/useless.mem"));
		System.out.println(loadWordMemory(new FileInputStream("/home/kwong/useless.mem")));
	}
}
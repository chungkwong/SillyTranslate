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
package com.github.sillytranslate.lex;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordMemory{
	private final HashMap<String,TreeSet<Meaning>> map=new HashMap<>();

	public static WordMemory createWordMemory(InputStream in){
		WordMemory memory=new WordMemory();
		Scanner sc=new Scanner(in,"UTF-8");
		while(sc.hasNextLine()){
			String word=sc.next();
			TreeSet<Meaning> meanings=new TreeSet<>();
			while(sc.hasNext()){
				meanings.add(new Meaning(sc.next(),sc.nextInt(),sc.next()));
				if(sc.next().equals("e"))
					break;
			}
			memory.map.put(word,meanings);
		}
		return memory;
	}
	public void saveTo(OutputStream out) throws UnsupportedEncodingException,IOException{
		BufferedWriter to=new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
		for(Map.Entry<String,TreeSet<Meaning>> entry:map.entrySet()){
			to.write(entry.getKey());
			to.newLine();
			for(Meaning m:entry.getValue()){
				to.write(m.text);
				to.write("\t");
				to.write(m.text);
				to.write("\t");
				to.write(m.text);
				to.write("\t");
				to.write(c);
				to.newLine();
			}
		}
		to.flush();
	}
	static class Meaning implements Comparable<Meaning>{
		private String text;
		private int count;
		private String tag;
		public Meaning(String text,int count,String tag){
			this.text=text;
			this.count=count;
			this.tag=tag;
		}
		@Override
		public int compareTo(Meaning o){
			return Integer.compare(o.count,count);
		}
	}
}
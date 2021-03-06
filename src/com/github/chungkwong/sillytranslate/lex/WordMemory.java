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
import com.github.chungkwong.sillytranslate.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WordMemory{
	private static final HashMap<String,WordMemory> pool=new HashMap<>();
	private final TreeMap<String,Candidates> map=new TreeMap<>();
	private final File cache;
	public WordMemory(){
		this.cache=null;
	}
	private WordMemory(String path){
		cache=Configure.resolveFile(path);
		try(DataInputStream sc=new DataInputStream(new BufferedInputStream(new FileInputStream(cache)))){
			while(true){
				String word=sc.readUTF();
				boolean def=sc.readBoolean();
				List<Meaning> meanings=new ArrayList<>();
				while(sc.readBoolean()){
					meanings.add(new Meaning(sc.readUTF(),sc.readUTF(),sc.readInt()));
				}
				map.put(word,new Candidates(def,meanings));
			}
		}catch(EOFException ex){

		}catch(FileNotFoundException ex){
			try{
				cache.createNewFile();
			}catch(IOException ex1){
				Logger.getGlobal().log(Level.INFO,null,ex1);
			}
		}catch(IOException ex){
			Logger.getGlobal().log(Level.INFO,ex.getLocalizedMessage(),ex);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(this::save));
	}
	public static WordMemory getWordMemory(String path){
		if(pool.containsKey(path))
			return pool.get(path);
		else{
			WordMemory instance=new WordMemory(path);
			pool.put(path,instance);
			return instance;
		}
	}
	public void useMeaning(String word,String meaning,String tag,boolean def){
		useMeaning(word,meaning,tag,def,1);
	}
	public void useMeaning(String word,String meaning,String tag,boolean def,int count){
		Candidates cand=map.get(word);
		List<Meaning> lst;
		if(cand==null){
			lst=new ArrayList<>();
			map.put(word,new Candidates(def,lst));
		}else{
			lst=cand.getCandidates();
			cand.setDefault(def||cand.hasDefault());
		}
		Optional<Meaning> entry=lst.stream().filter((m)->m.getText().equals(meaning)&&m.getTag().equals(tag)).findAny();
		if(entry.isPresent()){
			entry.get().used(count);
			//May be low performance and can easily improved
			if(def){
				lst.remove(entry.get());
				lst.add(0,entry.get());
			}else if(cand.hasDefault()){
				Meaning frist=lst.remove(0);
				lst.sort((m,n)->n.getCount()-m.getCount());
				lst.add(0,frist);
			}else{
				lst.sort((m,n)->n.getCount()-m.getCount());
			}
		}else{
			lst.add(new Meaning(meaning,tag,count));
		}
	}
	public List<Meaning> getMeanings(String word){
		Candidates cand=map.get(word);
		return cand!=null?cand.getCandidates():null;
	}
	public Meaning getDefaultMeaning(String word){
		Candidates cand=map.get(word);
		return cand!=null&&cand.hasDefault()?cand.getCandidates().get(0):null;
	}
	public void clear(){
		map.clear();
	}
	public void save(){
		try(DataOutputStream to=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(cache)))){
			for(Map.Entry<String,Candidates> entry:map.entrySet()){
				to.writeUTF(entry.getKey());
				to.writeBoolean(entry.getValue().hasDefault());
				for(Meaning m:entry.getValue().getCandidates()){
					to.writeBoolean(true);
					to.writeUTF(m.getText());
					to.writeUTF(m.getTag());
					to.writeInt(m.getCount());
				}
				to.writeBoolean(false);
			}
			to.flush();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.INFO,ex.getLocalizedMessage(),ex);
		}
	}
	public void writeAsText(Writer out) throws IOException{
		out.write("Word\tTranslation\tTag\tCount\tDefault\n");
		for(Map.Entry<String,Candidates> entry:map.entrySet()){
			String word=entry.getKey();
			boolean def=entry.getValue().hasDefault();
			for(Meaning m:entry.getValue().getCandidates()){
				out.write(word);
				out.write('\t');
				out.write(m.getText());
				out.write('\t');
				out.write(m.getTag());
				out.write('\t');
				out.write(Integer.toString(m.getCount()));
				out.write('\t');
				if(def){
					def=false;
					out.write("default");
				}
				out.write('\n');
			}
		}
	}
	public void readFromText(BufferedReader in) throws IOException{
		in.readLine();
		String line=null;
		while((line=in.readLine())!=null){
			if(!line.isEmpty())
				addMeaning(line);
		}
	}
	private void addMeaning(String line){
		int begin=0;
		int end=line.indexOf('\t');
		String word=line.substring(begin,end);
		begin=end+1;
		end=line.indexOf('\t',begin);
		String translation=line.substring(begin,end);
		begin=end+1;
		end=line.indexOf('\t',begin);
		String tag=line.substring(begin,end);
		begin=end+1;
		end=line.indexOf('\t',begin);
		int count=Integer.parseInt(line.substring(begin,end));
		Meaning meaning=new Meaning(translation,tag,count);
		boolean def=end+1!=line.length();
		useMeaning(word,translation,tag,def,count);
	}
	void merge(WordMemory memory){
		memory.map.forEach((word,cand)->{
			cand.candidates.forEach((m)->useMeaning(word,m.getText(),m.getTag(),cand.hasDefault(),m.getCount()));
		});
	}
	void diff(WordMemory memory){
		Iterator<Map.Entry<String,Candidates>> iter=map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,Candidates> entry=iter.next();
			String word=entry.getKey();
			Candidates cand=entry.getValue();
			List<Meaning> thatMeanings=memory.getMeanings(word);
			if(thatMeanings!=null){
				List<Meaning> thisMeanings=cand.candidates;
				for(int i=thisMeanings.size()-1;i>=0;i--){
					Meaning thisMeaning=thisMeanings.get(i);
					for(int j=thatMeanings.size()-1;j>=0;j--){
						Meaning thatMeaning=thatMeanings.get(j);
						if(thisMeaning.getText().equals(thatMeaning.getText())&&thisMeaning.getTag().equals(thatMeaning.getTag())){
							thisMeaning.used(-thatMeaning.getCount());
							if(thatMeaning.getCount()<=0){
								thisMeanings.remove(i);
								if(i==0)
									cand.setDefault(false);
							}
							break;
						}
					}
				}
				if(thisMeanings.isEmpty()){
					iter.remove();
				}
			}
		};
	}
	String getCurrentWord(String word){
		return map.ceilingKey(word);
	}
	@Override
	public String toString(){
		return cache.getName();
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		File tmp=File.createTempFile("silly","cache");
		Files.copy(new File(args[0]).toPath(),tmp.toPath(),StandardCopyOption.REPLACE_EXISTING);
		WordMemory memory=WordMemory.getWordMemory(args[0]);
		memory.merge(WordMemory.getWordMemory(args[1]));
		memory.merge(WordMemory.getWordMemory(args[2]));
		memory.merge(WordMemory.getWordMemory(tmp.getAbsolutePath()));
	}
	private static class Candidates{
		private boolean def;
		private List<Meaning> candidates;
		public Candidates(boolean def,List<Meaning> candidates){
			this.def=def;
			this.candidates=candidates;
		}
		public void setDefault(boolean def){
			this.def=def;
		}
		public boolean hasDefault(){
			return def;
		}
		public List<Meaning> getCandidates(){
			return candidates;
		}
	}
}
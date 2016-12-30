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
package com.github.chungkwong.sillytranslate.sentence;
import com.github.chungkwong.sillytranslate.lex.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GprologSentenceTranslator implements SentenceTranslatorEngine{
	private final int limit;
	private final Locale locale;
	private String rules;
	public GprologSentenceTranslator(int limit,File src,Locale locale){
		this.limit=limit;
		this.locale=locale;
		try{
			rules=Files.readAllLines(src.toPath()).stream().collect(Collectors.joining("\n"));
		}catch(IOException ex){
			rules="";
			Logger.getLogger(RuleBasedSentenceTranslator.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public List<String> getTranslation(List<Token> words){
		String result;
		try{
			result=query(prepareQuery(words),prepareDatabase(words));
			System.out.println(result);
			if(!result.startsWith("[["))
				return Collections.emptyList();
			else
				return parseTranslation(result,words);
		}catch(IOException|InterruptedException ex){
			Logger.getGlobal().log(Level.INFO,ex.getLocalizedMessage(),ex);
			return Collections.emptyList();
		}
	}
	private List<String> parseTranslation(String result,List<Token> words) throws IOException{
		result=result.replace("["," [ ").replace("]"," ] ");
		Set<String> lst=new TreeSet<>();
		Scanner tokenizer=new Scanner(new StringReader(result));
		StringBuilder buf=new StringBuilder();
		int level=0;
		tokenizer.useDelimiter("[,\\s]+");
		while(tokenizer.hasNext()){
			if(tokenizer.hasNextInt()){
				int number=tokenizer.nextInt();
				if(level==3){
					buf.appendCodePoint(number);
				}else if(level==2){
					buf.append(words.get(number).getText());
				}
			}else{
				String s=tokenizer.next();
				if(s.equals("[")){
					++level;
				}else if(s.equals("]")){
					if(level==2){
						lst.add(buf.toString());
						buf.setLength(0);
					}
					--level;
				}
			}
		}
		return new ArrayList<>(lst);
	}
	private String prepareDatabase(List<Token> words){
		StringBuilder buf=new StringBuilder(rules);
		buf.append("\ndiscontiguous(text/2).\n");
		for(int i=0;i<words.size();i++){
			buf.append("discontiguous('").append(words.get(i).getTag()).append("'/1).\n");
			buf.append('\'').append(words.get(i).getTag()).append("'(").append(i).append(").\n");
			if(words.get(i).getType()!=Token.Type.FORMULA)
				buf.append("text(").append(i).append(",").append(quoteString(words.get(i).getText())).append(").\n");
		}
		return buf.toString();
	}
	private static String prepareQuery(List<Token> words){
		StringBuilder buf=new StringBuilder("findall(X,translate([");
		if(!words.isEmpty())
			buf.append(0);
		for(int i=1;i<words.size();i++)
			buf.append(',').append(i);
		buf.append("],X),L),write(L).\n");
		return buf.toString();
	}
	public static String query(String query,String data) throws IOException, InterruptedException{
		File dataFile=File.createTempFile("silly",".prolog");
		try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile),"UTF-8"))){
			out.append(data);
		}
		Process yap=Runtime.getRuntime().exec(new String[]{"gprolog"});
		BufferedReader in=new BufferedReader(new InputStreamReader(yap.getInputStream(),"UTF-8"));
		OutputStreamWriter out=new OutputStreamWriter(yap.getOutputStream(),"UTF-8");
		out.write("set_prolog_flag(unknown,fail).\n");
		out.write("consult('"+dataFile.getAbsolutePath()+"').\n");
		out.write(query+"\n");
		out.close();
		StringBuilder result=new StringBuilder();
		String line;
		while((line=in.readLine())!=null){
			System.out.println(line);
			if(line.startsWith("[["))
				result.append(line).append('\n');
		}
		in.close();
		//dataFile.delete();
		return result.toString();
	}
	@Override
	public String getName(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RULE BASED");
	}
	private static String quoteString(String str){
		return str.codePoints().mapToObj((i)->Integer.toString(i)).collect(Collectors.joining(",","[","]"));
	}
	public static void main(String[] args) throws IOException, InterruptedException{
		//System.out.println(query("hate(X),write(X).","translate([0,1,2],[0,2,1]).\ntranslate([0,1,2],[2,1,0]).\n"));
		GprologSentenceTranslator translator=new GprologSentenceTranslator(10,new File("src/com/github/chungkwong/sillytranslate/sentence/RULES2.prolog"),Locale.CHINESE);
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.isEmpty())
				break;
			String[] split=line.split(":");
			ArrayList<Token> list=new ArrayList<>();
			for(int i=0;i+1<split.length;i+=2)
				list.add(new Token(Token.Type.WORD,split[i],split[i+1]));
			System.out.println(translator.getTranslation(list));
		}
	}
}
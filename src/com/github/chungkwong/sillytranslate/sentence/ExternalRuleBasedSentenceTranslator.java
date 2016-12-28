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
public class ExternalRuleBasedSentenceTranslator implements SentenceTranslatorEngine{
	private static final String COMMAND="yap";
	private final int limit;
	private final Locale locale;
	private String rules;
	public ExternalRuleBasedSentenceTranslator(int limit,File src,Locale locale){
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
			System.out.print(result);
		}catch(IOException|InterruptedException ex){
			Logger.getGlobal().log(Level.INFO,ex.getLocalizedMessage(),ex);
			return Collections.emptyList();
		}
		if(!result.startsWith("[["))
			return Collections.emptyList();
		/*result=result.substring(2,result.lastIndexOf("]]"));
		return Arrays.stream(result.split("\\],\\[")).map(
				(s)->Sentences.build(Arrays.stream(s.split(",")).map((i)->extractWord(i,words)),locale)
		).distinct().collect(Collectors.toList());*/
		Collection<String> translations=new TreeSet<>();
		int end=result.lastIndexOf(']');
		for(int i=2;i<end;){
			int j=result.indexOf(']',i);
			translations.add(extractSentence(i,j,result,words));
			i=j+3;
		}
		return new ArrayList<>(translations);
	}
	private String extractSentence(int i,int j,String result,List<Token> words){
		ArrayList<String> parts=new ArrayList<>(words.size());
		while(i<j){
			int k=result.indexOf(',',i);
			if(k<0||k>j){
				parts.add(extractWord(result.substring(i,j),words));
				break;
			}else{
				parts.add(extractWord(result.substring(i,k),words));
				i=k+1;
			}
		}
		return Sentences.build(parts.stream(),locale);
	}
	private String extractWord(String i,List<Token> words){
		try{
			return words.get(Integer.parseInt(i)).getText();
		}catch(RuntimeException ex){
			return i;
		}
	}
	private String prepareDatabase(List<Token> words){
		StringBuilder buf=new StringBuilder(rules);
		for(int i=0;i<words.size();i++){
			buf.append('\'').append(words.get(i).getTag()).append("'(").append(i).append(").\n");
			if(words.get(i).getType()!=Token.Type.FORMULA)
				buf.append("text(").append(i).append(",\'").append(words.get(i).getText()).append("').\n");
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
	/*private String extractTranslation(Substitution subst,List<Token> words){
		List<Term> list=Lists.toJavaList(subst.findRoot(NEW));
		return Sentences.build(list.stream().map((i)->{
			Object val=((Constant)i).getValue();
			return val instanceof Number?words.get(((Number)val).intValue()).getText():val.toString();
		}),locale);
	}*/
	public static String query(String query,String data) throws IOException, InterruptedException{
		File dataFile=File.createTempFile("silly",".prolog");
		try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile),"UTF-8"))){
			out.append(data);
		}
		Process yap=Runtime.getRuntime().exec(new String[]{"yap","-l",dataFile.getAbsolutePath()});
		BufferedReader in=new BufferedReader(new InputStreamReader(yap.getInputStream(),"UTF-8"));
		OutputStreamWriter out=new OutputStreamWriter(yap.getOutputStream(),"UTF-8");
		out.write(query+"\n");
		out.close();
		StringBuilder result=new StringBuilder();
		String line;
		while((line=in.readLine())!=null){
			result.append(line).append('\n');
		}
		in.close();
		return result.toString();
	}
	@Override
	public String getName(){
		return java.util.ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/Words").getString("RULE BASED");
	}
	public static void main(String[] args) throws IOException, InterruptedException{
		//System.out.println(query("hate(X),write(X).","translate([0,1,2],[0,2,1]).\ntranslate([0,1,2],[2,1,0]).\n"));
		ExternalRuleBasedSentenceTranslator translator=new ExternalRuleBasedSentenceTranslator(10,new File("src/com/github/chungkwong/sillytranslate/sentence/RULES.prolog"),Locale.CHINESE);
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
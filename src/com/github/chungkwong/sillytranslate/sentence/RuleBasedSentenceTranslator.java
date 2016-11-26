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
import com.github.chungkwong.jprologmin.*;
import com.github.chungkwong.sillytranslate.lex.*;
import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RuleBasedSentenceTranslator implements SentenceTranslatorEngine{
	private final int limit;
	private static final Variable NEW=new Variable("NEW");
	private static final Constant<String> FAIL=new Constant<>("fail");
	public RuleBasedSentenceTranslator(int limit){
		this.limit=limit;
	}
	@Override
	public List<String> getTranslation(List<Token> words){
		Processor exec=new Processor(prepareQuery(words),prepareDatabase(words));
		Substitution subst;
		int i=0;
		Collection<String> translators=new TreeSet<>();
		while((subst=exec.getSubstitution())!=null){
			translators.add(extractTranslation(subst,words));
			if(++i>=limit)
				break;
			exec.reexecute();
		}
		return new ArrayList<>(translators);
	}
	private static Database prepareDatabase(List<Token> words){
		Database db=new Database(new InputStreamReader(RuleBasedSentenceTranslator.class.getResourceAsStream("RULES.prolog")));
		db.getFlag("undefined_predicate").setValue(FAIL);
		for(int i=0;i<words.size();i++){
			db.addPredication(new CompoundTerm(words.get(i).getTag(),new Constant(BigInteger.valueOf(i))));
			if(words.get(i).getType()!=Token.Type.FORMULA)
				db.addPredication(new CompoundTerm("text",new Constant(BigInteger.valueOf(i)),new Constant(words.get(i).getText())));
		}
		return db;
	}
	private static Predication prepareQuery(List<Token> words){
		List<Term> indices=new ArrayList<>();
		for(int i=0;i<words.size();i++)
			indices.add(new Constant(BigInteger.valueOf(i)));
		return new CompoundTerm("translate",Lists.asList(indices),NEW);
	}
	private static String extractTranslation(Substitution subst,List<Token> words){
		List<Term> list=Lists.toJavaList(subst.findRoot(NEW));
		return list.stream().map((i)->{
			Object val=((Constant)i).getValue();
			return val instanceof Number?words.get(((Number)val).intValue()).getText():val.toString();
		}).collect(Collectors.joining());
	}
	/**
	 * Each line look like 我:n.:恨:v.:你:n.
	 * @param args
	 */
	public static void main(String[] args){
		RuleBasedSentenceTranslator translator=new RuleBasedSentenceTranslator(10);
		Scanner in=new Scanner(System.in);
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.isEmpty())
				break;
			String[] split=line.split(":");
			ArrayList<Token> list=new ArrayList<>();
			for(int i=0;i+1<split.length;i+=2)
				list.add(new Token(Token.Type.WORD,split[i],split[i+1]));
			System.out.println(list);
			System.out.println(translator.getTranslation(list));
		}
	}
	@Override
	public String getName(){
		return "Rule based";
	}
}
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
	private static final int LIMIT=10;
	private static final Variable NEW=new Variable("NEW");
	private static final Variable OLD=new Variable("OLD");
	@Override
	public List<String> getTranslation(List<Token> words){
		Processor exec=new Processor(prepareQuery(words),prepareDatabase(words));
		Substitution subst;
		int i=0;
		ArrayList<String> translators=new ArrayList<>();
		while((subst=exec.getSubstitution())!=null){
			translators.add(extractTranslation(subst,words));
			if(++i>=LIMIT)
				break;
			exec.reexecute();
		}
		return translators;
	}
	private static Database prepareDatabase(List<Token> words){
		Database db=new Database(new InputStreamReader(RuleBasedSentenceTranslator.class.getResourceAsStream("RULES.prolog")));
		for(int i=0;i<words.size();i++)
			db.addPredication(new CompoundTerm(words.get(i).getTag().replace('.','_'),new Constant(BigInteger.valueOf(i))));
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
		return list.stream().map((i)->words.get(((Number)((Constant)i).getValue()).intValue())).
				map((t)->t.getText()).collect(Collectors.joining());
	}
	public static void main(String[] args){
		RuleBasedSentenceTranslator translator=new RuleBasedSentenceTranslator();
		System.out.println(translator.getTranslation(Arrays.asList(
				new Token(Token.Type.WORD,"hello",""),new Token(Token.Type.WORD,"world",""))));
	}
}
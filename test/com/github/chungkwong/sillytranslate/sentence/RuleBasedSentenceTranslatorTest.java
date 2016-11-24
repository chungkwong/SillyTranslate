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
import java.util.*;
import org.junit.*;

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RuleBasedSentenceTranslatorTest{
	public RuleBasedSentenceTranslatorTest(){
	}
	private static void assertTranslations(String in,String... out){
		RuleBasedSentenceTranslator translator=new RuleBasedSentenceTranslator(10);
		String[] split=in.split(":");
		ArrayList<Token> list=new ArrayList<>();
		for(int i=0;i+1<split.length;i+=2)
			list.add(new Token(Token.Type.WORD,split[i],split[i+1]));
		Assert.assertArrayEquals(translator.getTranslation(list).toArray(),out);
	}
	@Test
	public void testSentence(){
		assertTranslations("我:n.:恨:v.:你:n.","我恨你");
		assertTranslations("我:n.:和::他:n.:恨:v.:你:n.","我和他恨你");
		assertTranslations("我:n.:恨:v.:你:n.:和::他:n.","我恨你和他");
	}
	@Test
	public void testIncomplete(){
		assertTranslations("我:xyz.","我");
		assertTranslations("恨:v.:你:n.","恨你");
		assertTranslations("我:n.:和:conj.:他:n.","我和他");
		assertTranslations("书:n.:的:prep.:我:n.","我的书");
	}
}

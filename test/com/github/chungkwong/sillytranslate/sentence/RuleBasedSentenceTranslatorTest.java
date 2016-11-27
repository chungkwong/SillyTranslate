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
	@Test(timeout=5000)
	public void testSentence(){

	}
	@Test(timeout=5000)
	public void testClause(){
		assertTranslations("我:xyz.","我");
		assertTranslations("我:n.:恨:v.:你:n.","我恨你");
		assertTranslations("我:n.:和::他:n.:恨:v.:你:n.","我和他恨你");
		assertTranslations("我:n.:恨:v.:你:n.:和::他:n.","我恨你和他");
		assertTranslations("\":pun.:老公:n.:的:prep.:大众:n.:\":pun.:恨:v.","\"大众的老公\"恨");
	}
	@Test(timeout=5000)
	public void testNounPhrase(){
		assertTranslations("我:n.","我");
		assertTranslations("人道:n.:主义:n.","人道主义");
		assertTranslations("最高:n.:人民:n.:法院:n.","最高人民法院");
		assertTranslations("我:n.:和:conj.:他:n.","我和他");
		assertTranslations("我:n.:，::你:n.:和:conj.:他:n.","我、你和他","我，你和他");
		assertTranslations("我:n.:与:conj.:他:n.","我与他");
		assertTranslations("我:n.:及:conj.:他:n.","我及他");
		assertTranslations("我:n.:或:conj.:他:n.","我或他");
		assertTranslations("书:n.:的:prep.:我:n.","我的书");
		assertTranslations("法律:n.:顾问:n.:的:prep.:最高:n.:人民:n.:法院:n.","最高人民法院的法律顾问");
		assertTranslations("我的:pron.:人:n.","我的人");
		assertTranslations("那:art.:人:n.","那人");
		assertTranslations("一:art.:人:n.","一人");
		assertTranslations("十:num.:人:n.","十人");
		assertTranslations("好:adj.:人:n.","好人");
		assertTranslations("十:num.:佳:adj.:人:n.","十佳人");
		assertTranslations("大:adj.:好:adj.:人:n.","大好人");
		assertTranslations("可靠:adj.:，::及时:adj.:和:conj.:全面:adj.:服务:n.","可靠、及时和全面服务");
		assertTranslations("学校:n.:后:prep.:战乱:n.","后战乱的学校");
		assertTranslations("有名的:adj.:\"::老公:n.:的:prep.:大众:n.:\"","后战乱的学校");

	}
	@Test(timeout=5000)
	public void testVerbPhrase(){
		assertTranslations("恨:vbl.:你:n.","恨你");
		assertTranslations("恨:v.:你:n.","恨你");
		assertTranslations("恨:vt.:你:n.","恨你");
		assertTranslations("恨:vi.","恨");
		assertTranslations("玩:vi.:开心地:adv.","开心地玩");
		assertTranslations("看似:vi.:真:adv.:好:adj.","看似真好");
	}
	@Test(timeout=5000)
	public void testIncomplete(){
		assertTranslations("我:xyz.","我");
	}

}

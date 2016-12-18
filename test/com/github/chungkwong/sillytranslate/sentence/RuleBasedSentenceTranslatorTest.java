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
		RuleBasedSentenceTranslator translator=new RuleBasedSentenceTranslator(10,new File("src/com/github/chungkwong/sillytranslate/sentence/RULES.prolog"),Locale.CHINESE);
		String[] split=in.split(":");
		ArrayList<Token> list=new ArrayList<>();
		for(int i=0;i+1<split.length;i+=2)
			list.add(new Token(Token.Type.WORD,split[i],split[i+1]));
		Assert.assertArrayEquals(translator.getTranslation(list).toArray(),out);
	}
	@Test(timeout=8000)
	public void testSentence(){
		assertTranslations("因为:conj.:你:n.","因为你");
		assertTranslations("因为:conj.:你:n.:是:aux.:你:n.","因为你是你");
		assertTranslations("我:n.:是:aux.:我:n.:而:conj.:你:n.:是:aux.:你:n.","我是我而你是你");
		assertTranslations("我:n.:是:aux.:我:n.:，:punct.:你:n.:是:aux.:你:n.","我是我，你是你");
		assertTranslations("我:n.:是:aux.:我:n.:；:punct.:你:n.:是:aux.:你:n.","我是我；你是你");
	}
	@Test(timeout=8000)
	public void testClause(){
		assertTranslations("我:xyz.","我");
		assertTranslations("我:n.:恨:v.:你:n.","我恨你");
		assertTranslations("我:n.:和::他:n.:恨:v.:你:n.","我和他恨你");
		assertTranslations("我:n.:恨:v.:你:n.:和::他:n.","我恨你和他");
		assertTranslations("我:n.:跑:vi.:开心地:adv.:慢:adv.","我开心地慢跑");
		assertTranslations("我:n.:走:vi.:从:prep.:左:n.:到:prep.:右:n.","我从到右的左走","我从左到右走");
		assertTranslations("\":pun.:老公:n.:的:prep.:大众:n.:\":pun.:恨:v.","\"大众的老公\"恨");
		assertTranslations("数据库:n.:（:punct.:汇总:n.:的:prep.:事实:n.:）:punct.","数据库（事实的汇总）");
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
		assertTranslations("建筑:n.:从:prep.:东:n.:到:prep.:西:n.","从东到西的建筑","从到西的东的建筑","到西的从东的建筑");
		assertTranslations("有名的:adj.:\":punct.:老公:n.:的:prep.:大众:n.:\":punct.","有名的\"大众的老公\"");
	}
	@Test(timeout=10000)
	public void testWhClause(){
		assertTranslations("什么:n.::prep.:吃:vt.","吃的");
		assertTranslations("什么:n.::prep.:吃:v.","吃的");
		assertTranslations("什么时候:n.::prep.:吃:v.","吃的时候");
		assertTranslations("什么地方:n.::prep.:吃:v.","吃的地方");
		assertTranslations("如何:n.::prep.:吃:v.","吃的方法");
		assertTranslations("谁:n.::prep.:负责:v.","负责的人");
		assertTranslations("哪个:n.::prep.:选择:v.","选择的哪个");
		assertTranslations("为什么:n.::prep.:开始:v.","开始的原因");
		assertTranslations("什么:n.:你:pron.:看到:v.","什么你看到","你看到的");
		assertTranslations("车:n.:那:pron.:你:pron.:喜欢:v.","你喜欢的车","你的车喜欢","车那你喜欢");
		assertTranslations("口子:n.:什么时候:pron.:你:pron.:恨:v.:我:n.","你恨我的口子","你的口子恨我","口子什么时候你恨我");
		assertTranslations("什么:n.:你:pron.:看到:v.:是:aux.:什么:n.:你:pron.:得到:v.","你看到的是你得到的");
	}
	@Test(timeout=5000)
	public void testVerbPhrase(){
		assertTranslations("是:aux.:你:n.","是你");
		assertTranslations("恨:vbl.:你:n.","恨你");
		assertTranslations("恨:v.:你:n.","恨你");
		assertTranslations("恨:vt.:你:n.","恨你");
		assertTranslations("恨:vi.","恨");
		assertTranslations("玩:vi.:开心地:adv.","开心地玩");
		assertTranslations("看似:vi.:真:adv.:好:adj.","看似真好");
		assertTranslations("去:vi.:到:prep.:学校:n.","到学校去");
		assertTranslations("想:vi.::prep.:吃:vt.:饭:n.","想吃饭");
		assertTranslations("看:vt.:电影:n.:最近:adv.","最近看电影");
		assertTranslations("是:aux.:玩耍:vi.","玩耍");
		assertTranslations("是:aux.:杀死:vt.","被杀死");
		assertTranslations("是:aux.:杀死:vt.:他:n.","杀死他");
		assertTranslations("是:aux.:杀死:vt.:由:prep.:他:n.","被由他杀死");
		assertTranslations("是:aux.:快速地:adv.:杀死:vt.:由:prep.:他:n.","快速地被由他杀死");
		assertTranslations("是:aux.:杀死:vt.:快速地:adv.","被快速地杀死");
		assertTranslations("是:aux.:快速地:adv.:杀死:vt.","快速地被杀死");
	}
	@Test(timeout=5000)
	public void testAdjectivePhrase(){
		assertTranslations("好:adj.:人:n.","好人");
		assertTranslations("老:adj.:好:adj.:人:n.","老好人");
		assertTranslations("非常:adv.:好:adj.:人:n.","非常好人");
		assertTranslations("两个:num.:非常:adv.:好:adj.:人:n.","两个非常好人");
	}
	@Test(timeout=5000)
	public void testIncomplete(){
		assertTranslations("我:xyz.","我");
	}
}

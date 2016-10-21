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
package com.github.chungkwong.lex;

import com.github.sillytranslate.lex.*;
import java.io.*;
import java.util.*;
import org.junit.*;

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PrefixLexTest{
	@Test
	public void test() throws IOException{
		StardictDictionary zhDict=new StardictDictionary(new File("/home/kwong/下载/stardict-cedict-gb-2.4.2"));
		assertSplit(zhDict,"我是一个人","我","是","一个","人");
		assertSplit(zhDict,"我们爱北京天安门广场上的红旗。","我们","爱","北京","天安门广场","上","的","红旗","。");
		assertSplit(zhDict,"天安乐","天","安乐");
		assertSplit(zhDict,"I do.","I"," ","d","o",".");
	}
	private static void assertSplit(NavigableDictionary dict,String sentence,String... words) throws IOException{
		PrefixLex lex=new PrefixLex(new StringReader(sentence),dict);
		String word;
		ArrayList<String> result=new ArrayList<>();
		while((word=lex.next())!=null)
			result.add(word);
		Assert.assertArrayEquals(result.toArray(),words);
	}
}

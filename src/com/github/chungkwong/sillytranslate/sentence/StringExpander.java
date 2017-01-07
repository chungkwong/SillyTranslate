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
import com.github.chungkwong.sillytranslate.util.*;
import java.io.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StringExpander{
	public static void main(String[] args) throws IOException{
		args=new String[]{"/home/kwong/NetBeansProjects/SillyTranslate/src/com/github/chungkwong/sillytranslate/sentence/RULES.prolog",
			"/home/kwong/NetBeansProjects/SillyTranslate/src/com/github/chungkwong/sillytranslate/sentence/RULES3.prolog"};
		CodePointReader in=new CodePointReader(new InputStreamReader(new FileInputStream(args[0]),"UTF-8"));
		CodePointWriter out=new CodePointWriter(new OutputStreamWriter(new FileOutputStream(args[1]),"UTF-8"));
		expand(in,out);
		in.close();
		out.close();
	}
	private static void expand(CodePointReader in,CodePointWriter out) throws IOException{
		int c=in.read();
		while(c!=-1){
			if(c=='\"'){
				out.writeCodepoint('[');
				c=in.read();
				if(c!='\"'){
					if(c=='\\')
						c=in.read();
					out.write(Integer.toString(c));
					c=in.read();
					while(c!='\"'){
						if(c=='\\'){
							c=in.read();
						}
						out.writeCodepoint(',');
						out.write(Integer.toString(c));
						c=in.read();
					}
				}
				out.writeCodepoint(']');
				c=in.read();
			}else{
				out.writeCodepoint(c);
				c=in.read();
			}
		}
	}
}
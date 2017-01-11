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
import java.awt.*;
import java.util.*;
import static java.util.ResourceBundle.getBundle;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Token{
	public enum Type{
		WORD(getBundle("com/github/chungkwong/sillytranslate/Words").getString("WORD"),Color.LIGHT_GRAY),
		QUOTE_START(getBundle("com/github/chungkwong/sillytranslate/Words").getString("QUOTE START"),Color.GREEN),
		QUOTE_TOGGLE(getBundle("com/github/chungkwong/sillytranslate/Words").getString("QUOTE TOGGLE"),Color.GREEN),
		QUOTE_END(getBundle("com/github/chungkwong/sillytranslate/Words").getString("QUOTE END"),Color.GREEN),
		REMARK_START(getBundle("com/github/chungkwong/sillytranslate/Words").getString("REMARK START"),Color.GREEN),
		REMARK_END(getBundle("com/github/chungkwong/sillytranslate/Words").getString("REMARK END"),Color.GREEN),
		FULL_STOP(getBundle("com/github/chungkwong/sillytranslate/Words").getString("FULL STOP"),Color.GREEN),
		OTHER_MARK(getBundle("com/github/chungkwong/sillytranslate/Words").getString("OTHER"),Color.YELLOW),
		FORMULA(getBundle("com/github/chungkwong/sillytranslate/Words").getString("FORMULA"),Color.DARK_GRAY);
		private final String name;
		private final Color color;
		private Type(String name,Color color){
			this.name=name;
			this.color=new Color(color.getRed(),color.getGreen(),color.getBlue(),128);
		}
		public Color getColor(){
			return color;
		}
		@Override
		public String toString(){
			return name;
		}
	};
	private final Type type;
	private final String text;
	private final String tag;
	public Token(Type type,String text,String tag){
		this.type=type;
		this.text=text;
		this.tag=type!=Type.WORD&&tag.isEmpty()?type.name:tag;
	}
	public Type getType(){
		return type;
	}
	public String getText(){
		return text;
	}
	public String getTag(){
		return tag;
	}
	@Override
	public String toString(){
		return type+":"+text+"("+tag+")";
	}
	public static Type guessType(String text,Locale locale){
		Token.Type type=Token.Type.WORD;
		if(text.codePointCount(0,text.length())==1){
			Properties prop=SillyTranslate.getLanguageLex(locale);
			for(Object key:prop.keySet())
				if(prop.get(key).toString().contains(text))
					type=Token.Type.valueOf(key.toString());
		}
		return type;
	}
}

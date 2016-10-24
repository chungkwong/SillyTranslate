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
package com.github.sillytranslate.lex;
import java.awt.*;
import static java.util.ResourceBundle.getBundle;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Token{
	public enum Type{
		WORD(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("WORD"),Color.WHITE),
		QUOTE_START(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("QUOTE START"),Color.GREEN),
		QUOTE_TOGGLE(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("QUOTE TOGGLE"),Color.GREEN),
		QUOTE_END(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("QUOTE END"),Color.GREEN),
		REMARK_START(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("REMARK START"),Color.GREEN),
		REMARK_END(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("REMARK END"),Color.GREEN),
		FULL_STOP(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("FULL STOP"),Color.GREEN),
		OTHER_MARK(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("OTHER"),Color.YELLOW),
		FORMULA(getBundle("com/github/sillytranslate/lex/LOCALIZATION").getString("FORMULA"),Color.LIGHT_GRAY);
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
	public Token(Type type,String text){
		this.type=type;
		this.text=text;
	}
	public Type getType(){
		return type;
	}
	public String getText(){
		return text;
	}
	@Override
	public String toString(){
		return type+":"+text;
	}
}

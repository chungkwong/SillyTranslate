/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
import java.text.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NumberTranslator implements NavigableDictionary{
	private final Locale from,to;
	private final Translator[] translators=new Translator[]{
		this::translateFullDate,this::translateLongDate,this::translateMediumDate,this::translateShortDate,
		this::translateFullTime,this::translateLongTime,this::translateMediumTime,this::translateShortTime,
		this::translatePercent,this::translateCurrency,this::translateNumber
	};
	public NumberTranslator(Locale from,Locale to){
		this.from=from;
		this.to=to;
	}
	public String translateNumber(String text) throws ParseException{
		return translateFormat(text,NumberFormat.getNumberInstance(to),NumberFormat.getNumberInstance(from));
	}
	public String translatePercent(String text) throws ParseException{
		return translateFormat(text,NumberFormat.getPercentInstance(to),NumberFormat.getPercentInstance(from));
	}
	public String translateCurrency(String text) throws ParseException{
		return translateFormat(text,NumberFormat.getCurrencyInstance(to),NumberFormat.getCurrencyInstance(from));
	}
	public String translateShortDate(String text) throws ParseException{
		return translateFormat(text,DateFormat.getDateInstance(DateFormat.SHORT,to),DateFormat.getDateInstance(DateFormat.SHORT,from));
	}
	public String translateMediumDate(String text) throws ParseException{
		return translateFormat(text,DateFormat.getDateInstance(DateFormat.MEDIUM,to),DateFormat.getDateInstance(DateFormat.MEDIUM,from));
	}
	public String translateLongDate(String text) throws ParseException{
		return translateFormat(text,DateFormat.getDateInstance(DateFormat.LONG,to),DateFormat.getDateInstance(DateFormat.LONG,from));
	}
	public String translateFullDate(String text) throws ParseException{
		return translateFormat(text,DateFormat.getDateInstance(DateFormat.FULL,to),DateFormat.getDateInstance(DateFormat.FULL,from));
	}
	public String translateShortTime(String text) throws ParseException{
		return translateFormat(text,DateFormat.getTimeInstance(DateFormat.SHORT,to),DateFormat.getTimeInstance(DateFormat.SHORT,from));
	}
	public String translateMediumTime(String text) throws ParseException{
		return translateFormat(text,DateFormat.getTimeInstance(DateFormat.MEDIUM,to),DateFormat.getTimeInstance(DateFormat.MEDIUM,from));
	}
	public String translateLongTime(String text) throws ParseException{
		return translateFormat(text,DateFormat.getTimeInstance(DateFormat.LONG,to),DateFormat.getTimeInstance(DateFormat.LONG,from));
	}
	public String translateFullTime(String text) throws ParseException{
		return translateFormat(text,DateFormat.getTimeInstance(DateFormat.FULL,to),DateFormat.getTimeInstance(DateFormat.FULL,from));
	}
	public String translateFormat(String text,Format encode,Format decode) throws ParseException{
		ParsePosition pos=new ParsePosition(0);
		Object obj=decode.parseObject(text,pos);
		if(pos.getErrorIndex()!=-1||pos.getIndex()<text.length())
			throw new ParseException(text,0);
		return encode.format(obj);
	}
	public String translateProbe(String text){
		for(Translator translator:translators){
			try{
				return translator.translate(text);
			}catch(ParseException ex){

			}
		}
		return null;
	}
	public static void main(String[] args) throws ParseException{
		NumberTranslator translator=new NumberTranslator(Locale.PRC,Locale.US);
		System.out.println(translator.translateProbe("12.5%"));
		System.out.println(translator.translateProbe("￥34.70"));
		System.out.println(translator.translateProbe("52738174"));
		System.out.println(translator.translateProbe("1992-11-28"));
		System.out.println(translator.translateProbe("23:12:09"));
	}
	@Override
	public String getMeaning(String word){
		String translation=translateProbe(word);
		return translation==null?"":translation;
	}
	@Override
	public String getCurrentWord(String word){
		return translateProbe(word)==null?null:word;
	}
	@Override
	public String getNextWord(String word){
		return null;
	}
	@Override
	public String getSource(){
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public String toString(){
		return "{java.text}";
	}
	private static interface Translator{
		String translate(String text) throws ParseException;
	}
}

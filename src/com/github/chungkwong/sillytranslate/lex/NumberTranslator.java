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
		return NumberFormat.getNumberInstance(to).format(NumberFormat.getNumberInstance(from).parse(text));
	}
	public String translatePercent(String text) throws ParseException{
		return NumberFormat.getPercentInstance(to).format(NumberFormat.getPercentInstance(from).parse(text));
	}
	public String translateCurrency(String text) throws ParseException{
		return NumberFormat.getCurrencyInstance(to).format(NumberFormat.getCurrencyInstance(from).parse(text));
	}
	public String translateShortDate(String text) throws ParseException{
		return DateFormat.getDateInstance(DateFormat.SHORT,to).format(DateFormat.getDateInstance(DateFormat.SHORT,from).parse(text));
	}
	public String translateMediumDate(String text) throws ParseException{
		return DateFormat.getDateInstance(DateFormat.MEDIUM,to).format(DateFormat.getDateInstance(DateFormat.MEDIUM,from).parse(text));
	}
	public String translateLongDate(String text) throws ParseException{
		return DateFormat.getDateInstance(DateFormat.LONG,to).format(DateFormat.getDateInstance(DateFormat.LONG,from).parse(text));
	}
	public String translateFullDate(String text) throws ParseException{
		return DateFormat.getDateInstance(DateFormat.FULL,to).format(DateFormat.getDateInstance(DateFormat.FULL,from).parse(text));
	}
	public String translateShortTime(String text) throws ParseException{
		return DateFormat.getTimeInstance(DateFormat.SHORT,to).format(DateFormat.getTimeInstance(DateFormat.SHORT,from).parse(text));
	}
	public String translateMediumTime(String text) throws ParseException{
		return DateFormat.getTimeInstance(DateFormat.MEDIUM,to).format(DateFormat.getTimeInstance(DateFormat.MEDIUM,from).parse(text));
	}
	public String translateLongTime(String text) throws ParseException{
		return DateFormat.getTimeInstance(DateFormat.LONG,to).format(DateFormat.getTimeInstance(DateFormat.LONG,from).parse(text));
	}
	public String translateFullTime(String text) throws ParseException{
		return DateFormat.getTimeInstance(DateFormat.FULL,to).format(DateFormat.getTimeInstance(DateFormat.FULL,from).parse(text));
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
		return word;
	}
	@Override
	public String getNextWord(String word){
		return word;
	}
	@Override
	public String getSource(){
		return "Java";
	}
	private static interface Translator{
		String translate(String text) throws ParseException;
	}
}

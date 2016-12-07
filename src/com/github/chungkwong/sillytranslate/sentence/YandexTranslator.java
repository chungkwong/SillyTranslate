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
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class YandexTranslator extends CloudTranslator{
	private static final ResourceBundle BUNDLE=ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/sentence/yandex");
	private final String APP_KEY;
	private static final JSONParser PARSER=new JSONParser();
	public YandexTranslator(String APP_KEY){
		super(BUNDLE.getString("HOST"),BUNDLE.getString("SLOGAN"),BUNDLE.getString("HOME"));
		this.APP_KEY=APP_KEY;
	}
	@Override
	protected Map<String,String> getParams(String text,String from,String to){
		Map<String,String> params=new HashMap<String,String>();
		params.put("text",text);
		params.put("lang",to);
		params.put("key",APP_KEY);
		return params;
	}
	@Override
	protected String getLanguageCode(Locale locale){
		if(locale==null)
			return "auto";
		else
			return locale.getLanguage();
	}
	@Override
	protected String extractTranslation(String result){
		try{
			JSONObject object=(JSONObject)PARSER.parse(result);
			return ((JSONString)((JSONArray)object.getMembers().get(new JSONString("text"))).getElements().get(0)).getValue();
		}catch(IOException|SyntaxException|RuntimeException ex){
			System.err.println(result);
			Logger.getGlobal().log(Level.FINE,null,ex);
			return null;
		}
	}
}

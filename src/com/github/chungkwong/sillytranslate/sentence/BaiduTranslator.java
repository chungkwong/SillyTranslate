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
import java.security.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BaiduTranslator extends CloudTranslator{
	private static final ResourceBundle BUNDLE=ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/sentence/baidu");
	private final String APP_ID;
	private final String SECURITY_KEY;
	private static final JSONParser PARSER=new JSONParser();
	public BaiduTranslator(String APP_ID,String SECURITY_KEY){
		super(BUNDLE.getString("HOST"),BUNDLE.getString("SLOGAN"),BUNDLE.getString("HOME"));
		this.APP_ID=APP_ID;
		this.SECURITY_KEY=SECURITY_KEY;
	}
	@Override
	protected Map<String,String> getParams(String text,String from,String to){
		Map<String,String> params=new HashMap<String,String>();
		params.put("q",text);
		params.put("from",from);
		params.put("to",to);
		params.put("appid",APP_ID);
		String salt=String.valueOf(System.currentTimeMillis());
		params.put("salt",salt);
		String src=APP_ID+text+salt+SECURITY_KEY;
		params.put("sign",md5(src));
		return params;
	}
	@Override
	protected String getLanguageCode(Locale locale){
		if(locale==null)
			return "auto";
		else if(BUNDLE.containsKey(locale.toLanguageTag()))
			return BUNDLE.getString(locale.toLanguageTag());
		else if(BUNDLE.containsKey(locale.getLanguage()))
			return BUNDLE.getString(locale.getLanguage());
		else
			throw new RuntimeException("Unsupported Language");
	}
	@Override
	protected String extractTranslation(String result){
		try{
			JSONObject object=(JSONObject)PARSER.parse(result);
			object=(JSONObject)((JSONArray)object.getMembers().get(new JSONString("trans_result"))).getElements().get(0);
			return ((JSONString)object.getMembers().get(new JSONString("dst"))).getValue();
		}catch(IOException|SyntaxException|RuntimeException ex){
			Logger.getGlobal().log(Level.FINE,null,ex);
			return null;
		}
	}
	private static final char[] hexDigits={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	public static String md5(String input){
		if(input==null){
			return null;
		}
		try{
			MessageDigest messageDigest=MessageDigest.getInstance("MD5");
			byte[] inputByteArray=input.getBytes("UTF-8");
			messageDigest.update(inputByteArray);
			byte[] resultByteArray=messageDigest.digest();
			return byteArrayToHex(resultByteArray);
		}catch(NoSuchAlgorithmException|UnsupportedEncodingException e){
			return null;
		}
	}
	private static String byteArrayToHex(byte[] byteArray){
		char[] resultCharArray=new char[byteArray.length*2];
		int index=0;
		for(byte b:byteArray){
			resultCharArray[index++]=hexDigits[b>>>4&0xf];
			resultCharArray[index++]=hexDigits[b&0xf];
		}
		return new String(resultCharArray);
	}
}
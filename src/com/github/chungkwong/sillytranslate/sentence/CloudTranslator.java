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
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.logging.*;
import javax.net.ssl.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CloudTranslator{
	private static final String APP_ID=ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/sentence/secret").getString("BAIDU_APP_ID");
	private static final String SECURITY_KEY=ResourceBundle.getBundle("com/github/chungkwong/sillytranslate/sentence/secret").getString("BAIDU_APP_SECRET");
	public static void main(String[] args){
		TransApi api=new TransApi(APP_ID,SECURITY_KEY);
		String query="I hate you.";
		System.out.println(api.getTransResult(query,"auto","zh"));
	}
}
class MD5{
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
class HttpGet{
	protected static final int SOCKET_TIMEOUT=10000; // 10S
	protected static final String GET="GET";
	public static String get(String host,Map<String,String> params){
		try{
			SSLContext sslcontext=SSLContext.getInstance("TLS");
			sslcontext.init(null,new TrustManager[]{myX509TrustManager},null);
			String sendUrl=getUrlWithQueryString(host,params);
            // System.out.println("URL:" + sendUrl);
			URL uri=new URL(sendUrl);
			HttpURLConnection conn=(HttpURLConnection)uri.openConnection();
			if(conn instanceof HttpsURLConnection){
				((HttpsURLConnection)conn).setSSLSocketFactory(sslcontext.getSocketFactory());
			}
			conn.setConnectTimeout(SOCKET_TIMEOUT);
			conn.setRequestMethod(GET);
			int statusCode=conn.getResponseCode();
			if(statusCode!=HttpURLConnection.HTTP_OK){
				System.out.println("Http错误码："+statusCode);
			}
			InputStream is=conn.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			StringBuilder builder=new StringBuilder();
			String line=null;
			while((line=br.readLine())!=null){
				builder.append(line);
			}
			String text=builder.toString();
			close(br);
			close(is);
			conn.disconnect();
			return text;
		}catch(IOException|KeyManagementException|NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
	public static String getUrlWithQueryString(String url,Map<String,String> params){
		if(params==null){
			return url;
		}
		StringBuilder builder=new StringBuilder(url);
		if(url.contains("?")){
			builder.append("&");
		}else{
			builder.append("?");
		}
		int i=0;
		for(String key:params.keySet()){
			String value=params.get(key);
			if(value==null){
				continue;
			}
			if(i!=0){
				builder.append('&');
			}
			builder.append(key);
			builder.append('=');
			builder.append(encode(value));
			i++;
		}
		return builder.toString();
	}
	protected static void close(Closeable closeable){
		if(closeable!=null){
			try{
				closeable.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public static String encode(String input){
		if(input==null){
			return "";
		}
		try{
			return URLEncoder.encode(input,"UTF-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return input;
	}
	private static TrustManager myX509TrustManager=new X509TrustManager(){
		@Override
		public X509Certificate[] getAcceptedIssuers(){
			return null;
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException{
		}
		@Override
		public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException{
		}
	};
}
class TransApi{
	private static final String TRANS_API_HOST="http://api.fanyi.baidu.com/api/trans/vip/translate";
	private final String appid;
	private final String securityKey;
	private final JSONParser parser=new JSONParser();
	public TransApi(String appid,String securityKey){
		this.appid=appid;
		this.securityKey=securityKey;
	}
	public String getTransResult(String query,String from,String to){
		Map<String,String> params=buildParams(query,from,to);
		try{
			JSONObject result=(JSONObject)parser.parse(HttpGet.get(TRANS_API_HOST,params));
			result=(JSONObject)((JSONArray)result.getMembers().get(new JSONString("trans_result"))).getElements().get(0);
			return ((JSONString)result.getMembers().get(new JSONString("dst"))).getValue();
		}catch(IOException|SyntaxException|NullPointerException ex){
			Logger.getLogger(TransApi.class.getName()).log(Level.SEVERE,null,ex);
			return null;
		}
	}
	private Map<String,String> buildParams(String query,String from,String to){
		Map<String,String> params=new HashMap<String,String>();
		params.put("q",query);
		params.put("from",from);
		params.put("to",to);
		params.put("appid",appid);
		String salt=String.valueOf(System.currentTimeMillis());
		params.put("salt",salt);
		String src=appid+query+salt+securityKey;
		params.put("sign",MD5.md5(src));
		return params;
	}
}
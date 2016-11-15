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
public abstract class CloudTranslator{
	protected abstract Map<String,String> getParams(String text,String from,String to);
	protected abstract String getLanguageCode(Locale locale);
	protected abstract String extractTranslation(String result);
	private final String TRANS_API_HOST,SLOGAN,HOME;
	public CloudTranslator(String host,String slogan,String home){
		this.TRANS_API_HOST=host;
		this.SLOGAN=slogan;
		this.HOME=home;
	}
	public String translate(String text,Locale from,Locale to){
		Map<String,String> params=getParams(text,getLanguageCode(from),getLanguageCode(to));
		return extractTranslation(get(TRANS_API_HOST,params));
	}
	public String getHOME(){
		return HOME;
	}
	public String getSLOGAN(){
		return SLOGAN;
	}
	private String get(String host,Map<String,String> params){
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
			conn.setRequestMethod("GET");
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
	private static void close(Closeable closeable){
		if(closeable!=null){
			try{
				closeable.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	private static String getUrlWithQueryString(String url,Map<String,String> params){
		StringBuilder builder=new StringBuilder(url);
		if(url.contains("?")){
			builder.append("&");
		}else{
			builder.append("?");
		}
		boolean first=true;
		for(String key:params.keySet()){
			String value=params.get(key);
			if(value==null){
				continue;
			}
			if(first){
				first=false;
			}else{
				builder.append('&');
			}
			builder.append(key);
			builder.append('=');
			try{
				builder.append(URLEncoder.encode(value,"UTF-8"));
			}catch(UnsupportedEncodingException ex){
				Logger.getLogger(CloudTranslator.class.getName()).log(Level.SEVERE,null,ex);
			}
		}
		return builder.toString();
	}
	private static final int SOCKET_TIMEOUT=10000;
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
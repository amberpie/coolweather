package com.example.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {

	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){
			@Override
			public void run(){
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection= (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in= connection.getInputStream();
					/*
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					
					Log.d("HttpUtil","b3ok");
					
					while((line=reader.readLine())!=null){
						Log.d("HttpUtil","b4ok");
						response.append(line);
						Log.d("HttpUtil","b5ok");
					}
					*/
					//readLine()方法会因为读不到换行符而阻塞。所以用read()方法读取。
					byte[] b=new byte[1024];
					int x=in.read(b,0,b.length);
					String str=new String(b,0,x);
					str.replace(" ","");
					
					if(listener!=null){
						Log.d("HttpUtil",str);
						listener.onFinish(str);
					}
				}catch(Exception e){
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}

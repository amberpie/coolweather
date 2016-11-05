package com.example.coolweather.util;

import android.util.Log;

public class HttpCallbackListener {
	
	void onFinish(String response){
		;
	}
	
	void onError(Exception e){
		Log.d("HttpCallbackListener","error!!");
	}
}

package com.example.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coolweather.R;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private LinearLayout weatherInfoLayout;
	
	private TextView cityNameText ;
	private TextView publishText ;
	private TextView weatherDespText ;
	private TextView temp1Text ;
	private TextView temp2Text ;
	private TextView currentDateText ;
	private Button switchCity ;
	private Button refreshWeather;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherDespText=(TextView)findViewById(R.id.weather_desp);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		currentDateText=(TextView)findViewById(R.id.current_date);
		switchCity=(Button)findViewById(R.id.switch_city);
		refreshWeather=(Button)findViewById(R.id.refresh_weather);
		String countyCode=getIntent().getStringExtra("county_code");  //取出另一个Activity中传过来的数据
		if(!TextUtils.isEmpty(countyCode)){  //有县级代号就去查询天气
			publishText.setText("同步中…");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);  //
		}else{  //没有县级代号就直接显示不本地天气
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity",true);
			startActivity(intent);  
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中…");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code","");  //->Utility
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);  //
			}
			break;
		default:
			break;
		}
	}

	private void queryWeatherCode(String countyCode){  //查询县级代号对应的天气代号
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}
	
	//根据传入的地址和类型，向服务器查询天气代号或者天气信息
	private void queryFromServer(final String address, final String type){
		
		//Log.d("WeatherActivity",address);
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){
			//@Override
			public void onFinish(final String response){
				
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//从服务器返回的数据中解析出天气代号
						String[] array=response.split("\\|");
						if(array!=null && array.length==2){
							String weatherCode=array[1];
							
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){  //处理服务器返回的天气信息
					
					Utility.handleWeatherResponse(WeatherActivity.this,response);
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							
							showWeather();
						}
					});
				}
			}
			
			//@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						publishText.setTag("同步失败");
					}
				});
			}
		});
	}
	
	//从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	private void showWeather(){
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name",""));
		temp1Text.setText(prefs.getString("temp1",""));
		temp2Text.setText(prefs.getString("temp2",""));
		String weatherD=prefs.getString("weather_desp","");
		Log.d("WeatherActivityOOOOO",weatherD);
		weatherDespText.setText(prefs.getString("weather_desp",""));
		publishText.setText(prefs.getString("publish_time",""));
		currentDateText.setText(prefs.getString("current_date",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}

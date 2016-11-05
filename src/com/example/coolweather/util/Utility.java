package com.example.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.coolweather.model.CWDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

public class Utility {

	//返回数据的形式：代号|城市，代号|城市
	public synchronized static boolean handleProvincesResponse(CWDB cWDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(","); //按逗号分隔
			if(allProvinces!=null && allProvinces.length>0){
				for(String p:allProvinces){
					String[] array=p.split("\\|"); //按单竖线分隔
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					cWDB.saveProvince(province); //将解析出来的数据存储到Province表
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCitysResponse(CWDB cWDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(","); //按逗号分隔
			if(allCities!=null && allCities.length>0){
				for(String c:allCities){
					String[] array=c.split("\\|"); //按单竖线分隔
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					cWDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleCountiesResponse(CWDB cWDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(","); //按逗号分隔
			if(allCounties!=null && allCounties.length>0){
				for(String c:allCounties){
					String[] array=c.split("\\|"); //按单竖线分隔
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					cWDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	//解析服务器返回的JSON数据，病将解析出的数据存储到本地
	public static void handleWeatherResponse(Context context, String response){
		try{
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			Log.d("Utility",weatherDesp);
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	//将服务器返回的天气信息存储到sharedPreferences文件中
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		Log.d("Utility","a3ok");
		SimpleDateFormat sdf=new SimpleDateFormat("yyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weater_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}

}

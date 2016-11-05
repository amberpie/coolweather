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

	//�������ݵ���ʽ������|���У�����|����
	public synchronized static boolean handleProvincesResponse(CWDB cWDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(","); //�����ŷָ�
			if(allProvinces!=null && allProvinces.length>0){
				for(String p:allProvinces){
					String[] array=p.split("\\|"); //�������߷ָ�
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					cWDB.saveProvince(province); //���������������ݴ洢��Province��
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCitysResponse(CWDB cWDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(","); //�����ŷָ�
			if(allCities!=null && allCities.length>0){
				for(String c:allCities){
					String[] array=c.split("\\|"); //�������߷ָ�
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
			String[] allCounties=response.split(","); //�����ŷָ�
			if(allCounties!=null && allCounties.length>0){
				for(String c:allCounties){
					String[] array=c.split("\\|"); //�������߷ָ�
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
	
	//�������������ص�JSON���ݣ����������������ݴ洢������
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
	
	//�����������ص�������Ϣ�洢��sharedPreferences�ļ���
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		Log.d("Utility","a3ok");
		SimpleDateFormat sdf=new SimpleDateFormat("yyy��M��d��",Locale.CHINA);
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

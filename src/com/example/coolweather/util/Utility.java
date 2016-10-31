package com.example.coolweather.util;

import android.text.TextUtils;

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

}

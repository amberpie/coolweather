package com.example.coolweather.util;

import android.text.TextUtils;

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

}

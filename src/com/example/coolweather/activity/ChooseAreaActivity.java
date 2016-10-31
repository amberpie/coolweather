package com.example.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.model.CWDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity{
	
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog pDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;   //
	private CWDB cWDB;
	private List<String> dataList=new ArrayList<String>();   //

	private List<Province> provinceList;  //省列表
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;  //选中的省份
	private City selectedCity;
	private int currentLevel;  //选中的级别
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		cWDB=CWDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id){
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryCounties();
				}
			}
		});
		queryProvinces();
	}
	
	private void queryProvinces(){  //查询全国所有的省，有限从数据库查询，如果没有再去服务器查询
		provinceList=cWDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();  //刷新listView界面
			listView.setSelection(0); //listView第一项显示第0个数据（就是第一个数据
			titleText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	private void queryCities(){  //查询全国所有的市
		cityList=cWDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged(); 
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	private void queryCounties(){
		countyList=cWDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged(); 
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	private void queryFromServer(final String code, final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){  //这里进入子线程
			//@Override
			public void onFinish(String response){
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvincesResponse(cWDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCitysResponse(cWDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(cWDB, response, selectedCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable(){  //通过该方法回到主线程处理逻辑
						@Override
						public void run(){
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			//@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	private void showProgressDialog(){
		if(pDialog == null){
			pDialog=new ProgressDialog(this);
			pDialog.setMessage("正在加载…");
			pDialog.setCanceledOnTouchOutside(false);
		}
		pDialog.show();
	}
	
	private void closeProgressDialog(){
		if(pDialog != null){
			pDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}

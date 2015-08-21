package com.education.zhxy.heart.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.heart.adapter.HeartAdapter;
import com.education.zhxy.heart.data.bean.Teacher;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class HeartActivity extends BasicActivity implements OnItemClickListener{
	
	private static final String TAG = HeartActivity.class.getSimpleName();
	
	private TextView heartBackTextView;
	
	private HeartAdapter heartAdapter;
	
	private ListView heartListView;
	
	private HttpTask httpTask;
	
	// 自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.heartBackTextView:
				this.finish();
				break;
		}
	}
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.HEART_LIST, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.heart_main;
	}

	@Override
	public void initUI() {
		heartBackTextView = (TextView)findViewById(R.id.heartBackTextView);
		heartBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		heartAdapter = new HeartAdapter(getApplicationContext());
		heartListView = (ListView)findViewById(R.id.heartListView);
		heartListView.setAdapter(heartAdapter);
		heartListView.setOnItemClickListener(this);
		
		search();
	}

	@Override
	public void initData() {

	}
	
	@Override
	public void noNet(HttpTask task) {
		pd.dismiss();
		 ToastUtil.toast(getApplicationContext(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
		pd.dismiss();
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getApplicationContext(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
		}  	
	}
	
	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {
		pd.dismiss();
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getApplicationContext(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
		}  
	}
	
	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {
		Log.e(TAG, result.getData());
		pd.dismiss();
		if (result != null && !StringUtil.isEmpty(result.getData()) && StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if (commonResult.validate()) {
					JSONArray jsonArray = JSON.parseArray(commonResult.getData());
					if (jsonArray != null && jsonArray.size() > 0) {
						initTeacher(jsonArray);
					}
				} else {
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initTeacher(JSONArray jsonArray){
		List<Teacher> teacherList = new ArrayList<Teacher>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Teacher teacher = new Teacher();
			teacher.setUsid(jsonObject.getIntValue("usid"));
			teacher.setHId(jsonObject.getIntValue("HId"));
			teacher.setHName(jsonObject.getString("HName"));
			teacher.setHSex(jsonObject.getString("HSex"));
			teacher.setHAge(jsonObject.getIntValue("HAge"));
			teacher.setHCulture(jsonObject.getString("HCulture"));
			teacher.setHAptitude(jsonObject.getString("HAptitude"));
			teacher.setHCertificate(jsonObject.getString("HCertificate"));
			teacher.setHImage(jsonObject.getString("HImage"));
			teacherList.add(teacher);
		}
		
		if(teacherList != null && teacherList.size() > 0){
			heartAdapter.resetData(teacherList);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Teacher teacher = (Teacher)parent.getAdapter().getItem(position);
		Intent intent = new Intent(this,ChatActivity.class);
		intent.putExtra(Constants.TYPE, 1);
		intent.putExtra(Constants.ID, teacher.getUsid());
		intent.putExtra(Constants.NAMES, teacher.getHName());
		startActivity(intent);
	}

}

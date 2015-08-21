package com.education.zhxy.myinfo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.love.adapter.LoveAdapter;
import com.education.zhxy.love.data.bean.Students;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class MyAttentionActivity extends BasicActivity {
	
	private static final String TAG = MyAttentionActivity.class.getSimpleName();
	
	private HttpTask httpTask;

	private TextView myinfoMyAttentionBackTextView;
	
	private LoveAdapter loveAdapter;
	
	private ListView myinfoMyAttentionListView;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myinfoMyAttentionBackTextView:
				this.finish();
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_myattention;
	}

	@Override
	public void initUI() {
		myinfoMyAttentionBackTextView = (TextView)findViewById(R.id.myinfoMyAttentionBackTextView);
		myinfoMyAttentionBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		loveAdapter = new LoveAdapter(getApplicationContext());
		myinfoMyAttentionListView = (ListView)findViewById(R.id.myinfoMyAttentionListView);
		myinfoMyAttentionListView.setAdapter(loveAdapter);
		
		search();
	}
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_MYATTENTION, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
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
						initStudents(jsonArray);
					}else{
						loveAdapter.resetData(null,1);
					}
				} else {
					loveAdapter.resetData(null,1);
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initStudents(JSONArray jsonArray){
		List<Students> studentsList = new ArrayList<Students>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Students students = JSONObject.toJavaObject(jsonObject, Students.class);
			studentsList.add(students);
		}
		
		if(studentsList != null && studentsList.size() > 0){
			loveAdapter.resetData(studentsList,1);
		}
		
	}

}

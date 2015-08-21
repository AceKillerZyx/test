package com.education.zhxy.myschool.activity;

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
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myschool.adapter.TeacherDescriptionAdapter;
import com.education.zhxy.myschool.data.bean.TeacherDescription;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class TeacherDescriptionActivity extends BasicActivity {
	
	private static final String TAG = TeacherDescriptionActivity.class.getSimpleName();
	
	private HttpTask httpTask;
	
	private TextView teacherDescriptionBackTextView;
	
	private ListView teacherDescriptionListView;
	
	private TeacherDescriptionAdapter teacherDescriptionAdapter;
	
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.teacherDescriptionBackTextView:
				this.finish();
				break;
		}
	}
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.SCHOOLID, String.valueOf(SharedPreferencesUtil.getSchoolId(getApplicationContext())));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYSCHOOL_TEACHER_DESCRIPTION, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myschool_teacher_description;
	}

	@Override
	public void initUI() {
		teacherDescriptionBackTextView = (TextView)findViewById(R.id.teacherDescriptionBackTextView);
		teacherDescriptionBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		teacherDescriptionAdapter = new TeacherDescriptionAdapter(getApplicationContext());
		teacherDescriptionListView = (ListView)findViewById(R.id.teacherDescriptionListView);
		teacherDescriptionListView.setAdapter(teacherDescriptionAdapter);
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
						initTeacherDescription(jsonArray);
					}
				} else {
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initTeacherDescription(JSONArray jsonArray){
		List<TeacherDescription> teacherDescriptionList = new ArrayList<TeacherDescription>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			TeacherDescription teacherDescription = JSONObject.toJavaObject(jsonObject, TeacherDescription.class);
			teacherDescriptionList.add(teacherDescription);
		}
		
		if(teacherDescriptionList != null && teacherDescriptionList.size() > 0){
			teacherDescriptionAdapter.resetData(teacherDescriptionList);
		}
	}

}

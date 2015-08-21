package com.education.zhxy.myinfo.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class AboutUsActivity extends BasicActivity {

	private TextView myinfoAboutUsBackTextView;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myinfoAboutUsBackTextView:
			this.finish();
			break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_about_us;
	}

	@Override
	public void initUI() {
		myinfoAboutUsBackTextView = (TextView) findViewById(R.id.myinfoAboutUsBackTextView);
		myinfoAboutUsBackTextView.setOnClickListener(this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void initData() {
		
	}
	
	@Override
	public void noNet(HttpTask task) {
		 ToastUtil.toast(getApplicationContext(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
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

	}

}

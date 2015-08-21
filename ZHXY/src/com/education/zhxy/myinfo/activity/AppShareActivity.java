package com.education.zhxy.myinfo.activity;

import android.view.View;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;

public class AppShareActivity extends BasicActivity{
	private TextView myinfoAppShareTextView;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myinfoAppShareTextView:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void noNet(HttpTask task) {
		
	}

	@Override
	public void noData(HttpTask task, HttpResult result) {
		
	}

	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {
		
	}

	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {
		
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_appshare;
	}

	@Override
	public void initUI() {
		myinfoAppShareTextView=(TextView) findViewById(R.id.myinfoAppShareTextView);
		myinfoAppShareTextView.setOnClickListener(this);
	}

	@Override
	public void initData() {
		
	}

}

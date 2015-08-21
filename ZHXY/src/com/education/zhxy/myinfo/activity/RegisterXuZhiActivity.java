package com.education.zhxy.myinfo.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;

public class RegisterXuZhiActivity extends BasicActivity {
	private TextView registerXuZhiBackTextView;
	private Button registerXuzhiNextButton;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registerXuZhiBackTextView:
			this.finish();
			break;
		case R.id.registerXuzhiNextButton:
			Intent intent=new Intent(RegisterXuZhiActivity.this,RegisterActivity.class);
			startActivity(intent);
			this.finish();
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
		return R.layout.myinfo_register_xuzhi;
	}

	@Override
	public void initUI() {
		registerXuZhiBackTextView=(TextView) findViewById(R.id.registerXuZhiBackTextView);
		registerXuZhiBackTextView.setOnClickListener(this);
		registerXuzhiNextButton=(Button) findViewById(R.id.registerXuzhiNextButton);
		registerXuzhiNextButton.setOnClickListener(this);
	}

	@Override
	public void initData() {

	}

}

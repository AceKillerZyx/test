package com.education.zhxy.home.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.myclass.activity.MyClassActivity;
import com.education.zhxy.myinfo.activity.LoginActivity;
import com.education.zhxy.util.SharedPreferencesUtil;

/**
 * 欢迎页
 */
public class WelcomeActivity extends Activity {

	private boolean isFirstIn = false;
	private static final int TIME = 2000;
	private static final int GO_HOME = 1001;
	private static final int GO_GUIDE = 1002;
	private static final int GO_LOGIN = 1003;
	private static final int GO_MYCLASS = 1004;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GO_HOME:
					goHome();
					break;
				case GO_GUIDE:
					goGuide();
					break;
				case GO_LOGIN:
					//goLogin();
					goHome();
					break;
				case GO_MYCLASS:
					goMyClass();
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zhxy_welcome);
		UIinit();
	}

	public void UIinit() {
		isFirstIn = SharedPreferencesUtil.isFirstIn(getApplicationContext());
		if (!isFirstIn) {
			int usid = SharedPreferencesUtil.getUsid(getApplicationContext());
			if (usid == 0) {
				mHandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
			} else {
				// 进入页面
				if(SharedPreferencesUtil.getClassId(getApplicationContext()) == 0){
					mHandler.sendEmptyMessageDelayed(GO_MYCLASS, TIME);
				}else{
					mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
				}
			}
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME); 
		}
	}

	private void goHome() {
		startActivity(new Intent(WelcomeActivity.this, ZHXYActivity.class));
		finish();
	}

	private void goLogin() {
		startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
		finish();
	}

	private void goGuide() {
		startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
		finish();
	}

	private void goMyClass() {
		Intent intent = new Intent(WelcomeActivity.this,MyClassActivity.class);
		intent.putExtra(Constants.TYPE, 0);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
	}

}

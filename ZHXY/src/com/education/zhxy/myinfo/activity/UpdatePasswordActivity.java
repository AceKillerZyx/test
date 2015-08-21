package com.education.zhxy.myinfo.activity;

import java.util.HashMap;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class UpdatePasswordActivity extends BasicActivity {
	
	private static final String TAG = UpdatePasswordActivity.class.getSimpleName();
	
	private HttpTask httpTask;

	private TextView myinfoUpdatePasswordBackTextView,
			myinfoUpdatePasswordTextView;

	private EditText myinfoUpdatePasswordOldEditText,
			myinfoUpdatePasswordNewEditText, myinfoUpdatePasswordOKEditText;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myinfoUpdatePasswordBackTextView:
				this.finish();
				break;
			case R.id.myinfoUpdatePasswordTextView:
				if(validate()){
					updatePassword();
				}
				break;
		}
	}
	
	private boolean validate(){
		
		String oldPassword = myinfoUpdatePasswordOldEditText.getText().toString().trim();
		if(TextUtils.isEmpty(oldPassword)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_update_password_old_null_error);
			return false;
		}
		
		String password = myinfoUpdatePasswordNewEditText.getText().toString().trim();
		if(TextUtils.isEmpty(password)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_update_password_new_null_error);
			return false;
		}
		
		String okPassword = myinfoUpdatePasswordOKEditText.getText().toString().trim();
		if(TextUtils.isEmpty(okPassword)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_password_null_error);
			return false;
		}
		
		if(!password.equals(okPassword)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_password_error);
			return false;
		}
		
		return true;
	}
	
	private void updatePassword(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.PWD, myinfoUpdatePasswordOldEditText.getText().toString().trim());
		paramMap.put(Constants.NEWPWD, myinfoUpdatePasswordNewEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_UPDATE_PASSWORD, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}
	
	@Override
	public int initLayout() {
		return R.layout.myinfo_update_password;
	}

	@Override
	public void initUI() {
		myinfoUpdatePasswordBackTextView = (TextView)findViewById(R.id.myinfoUpdatePasswordBackTextView);
		myinfoUpdatePasswordBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		myinfoUpdatePasswordOldEditText = (EditText)findViewById(R.id.myinfoUpdatePasswordOldEditText);
		myinfoUpdatePasswordNewEditText = (EditText)findViewById(R.id.myinfoUpdatePasswordNewEditText);
		myinfoUpdatePasswordOKEditText = (EditText)findViewById(R.id.myinfoUpdatePasswordOKEditText);
		
		myinfoUpdatePasswordTextView = (TextView)findViewById(R.id.myinfoUpdatePasswordTextView);
		myinfoUpdatePasswordTextView.setOnClickListener(this);
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
					ToastUtil.toast(getApplicationContext(), R.string.myinfo_update_password_success);
					exit();
				}else{
					ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void exit(){
		Intent intent = new Intent();  
        intent.setAction(Constants.EXIT_LOGIN);  
        sendBroadcast(intent);  
        SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USID, 0);
		Intent intents = new Intent(this,LoginActivity.class);
		startActivity(intents);
		finish();
	}

}

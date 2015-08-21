package com.education.zhxy.myinfo.activity;

import java.util.HashMap;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class FeedbackActivity extends BasicActivity {
	
	private static final String TAG = FeedbackActivity.class.getSimpleName();
	
	private HttpTask httpTask;
	
	private Button myinfoFeedbackButton;
	
	private TextView myinfoFeedbackBackTextView;
	
	private EditText myinfoFeedbackEditText,myinfoFeedbackPhoneEditText;
	
	//自定义ProgressDialog
    private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myinfoFeedbackBackTextView:
				this.finish();
				break;
			case R.id.myinfoFeedbackButton:
				if(validate()){
					feedback();
				}
				break;
		}
	}
	
	private boolean validate(){
		String content = myinfoFeedbackEditText.getText().toString().trim();
		if(TextUtils.isEmpty(content)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_feedback_content_null_error);
			return false;
		}
		return true;
	}
	
	private void feedback(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.OPINIONCONETNT, myinfoFeedbackEditText.getText().toString().trim());
		paramMap.put(Constants.OPINIONTEL, myinfoFeedbackPhoneEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_FEEDBACK, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_feedback;
	}

	@Override
	public void initUI() {
		myinfoFeedbackBackTextView = (TextView)findViewById(R.id.myinfoFeedbackBackTextView);
		myinfoFeedbackBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		myinfoFeedbackEditText = (EditText)findViewById(R.id.myinfoFeedbackEditText);
		myinfoFeedbackPhoneEditText = (EditText)findViewById(R.id.myinfoFeedbackPhoneEditText);
		
		myinfoFeedbackButton = (Button)findViewById(R.id.myinfoFeedbackButton);
		myinfoFeedbackButton.setOnClickListener(this);
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
					ToastUtil.toast(getApplicationContext(), R.string.myinfo_feedback_success);
					myinfoFeedbackEditText.setText("");
					myinfoFeedbackPhoneEditText.setText("");
				}else{
					ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
				}
			}
		}
		
	}

}

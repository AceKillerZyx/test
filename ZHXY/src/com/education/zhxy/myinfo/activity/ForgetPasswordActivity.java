package com.education.zhxy.myinfo.activity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.home.activity.ZHXYActivity;
import com.education.zhxy.home.activity.ZHXYApplication;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myclass.activity.MyClassActivity;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class ForgetPasswordActivity extends BasicActivity {
	
	private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
	
	private int resultCode = 0; 
	
	private Button forgetButton;
	
	private HttpTask forgetHttpTask,loginHttpTask;
	
	private TextView forgetPasswordBackTextView, forgetSendCodeTextView;

	private EditText forgetPhoneEditText,forgetPasswordEditText,forgetOKPasswordEditText,forgetCodeEditText;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.forgetPasswordBackTextView:
				Intent intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
				this.finish();
				break;
			case R.id.forgetSendCodeTextView:
				sendCode();
				break;
			case R.id.forgetButton:
				if(validate()){
					forgetPassword();
				}
				break;
		}
	}
	
	private boolean validate(){
		String phone = forgetPhoneEditText.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_phone_null_error);
			return false;
		}
		
		String password = forgetPasswordEditText.getText().toString().trim();
		if(TextUtils.isEmpty(password)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_login_password_null_error);
			return false;
		}
		
		String okPassword = forgetOKPasswordEditText.getText().toString().trim();
		if(TextUtils.isEmpty(okPassword)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_password_null_error);
			return false;
		}
		
		if(!password.equals(okPassword)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_password_error);
			return false;
		}
		
		String code = forgetCodeEditText.getText().toString().trim();
		if(TextUtils.isEmpty(code)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_code_null_error);
			return false;
		}
		
		if(resultCode != SMSSDK.RESULT_COMPLETE){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_register_code_error);
			return false;
		}
		
		return true;
	}
	
	private void forgetPassword(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.TEL, forgetPhoneEditText.getText().toString().trim());
		paramMap.put(Constants.NEWPWD, forgetPasswordEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_FORGET_PASSWORD, false); // GET
		forgetHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		forgetHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void login(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.TEL, forgetPhoneEditText.getText().toString().trim());
		paramMap.put(Constants.PWD, forgetPasswordEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_LOGIN, false); // GET
		loginHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		loginHttpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_forget_password;
	}

	@Override
	public void initUI() {
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
				
		forgetPasswordBackTextView = (TextView) findViewById(R.id.forgetPasswordBackTextView);
		forgetPasswordBackTextView.setOnClickListener(this);
		
		forgetPhoneEditText = (EditText)findViewById(R.id.forgetPhoneEditText);
		forgetPasswordEditText = (EditText)findViewById(R.id.forgetPasswordEditText);
		forgetOKPasswordEditText = (EditText)findViewById(R.id.forgetOKPasswordEditText);
		forgetCodeEditText = (EditText)findViewById(R.id.forgetCodeEditText);
		forgetCodeEditText.addTextChangedListener(textWatcher);
		
		forgetSendCodeTextView = (TextView)findViewById(R.id.forgetSendCodeTextView);
		forgetSendCodeTextView.setOnClickListener(this);
		
		forgetButton = (Button)findViewById(R.id.forgetButton);
		forgetButton.setOnClickListener(this);
		
		// 初始化
		cn.smssdk.SMSSDK.initSDK(this, Constants.SMS_APPKEY, Constants.SMS_APPSECRET);
		EventHandler eh = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				resultCode = result;
			}
		};
		SMSSDK.registerEventHandler(eh);
	}
	
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
			String code = forgetCodeEditText.getText().toString().trim();
			if (!StringUtil.isEmpty(code)) {
				SMSSDK.submitVerificationCode("86", forgetPhoneEditText.getText().toString().trim(), code);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};

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
				if(task == forgetHttpTask){
					if (commonResult.validate()) {
						login();
					}else{
						ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
					}
				}
				
				if(task == loginHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if(jsonArray != null && jsonArray.size() > 0){
							JSONObject jsonObject = jsonArray.getJSONObject(0);
							SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USID, jsonObject.getIntValue(Constants.ID));
							SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USER_ROLE, jsonObject.getIntValue(Constants.ROLE));
							SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_HEADER_IMAGE, jsonObject.getString(Constants.IMAGE).replace("\\/", "/"));
							SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USNAME, forgetPhoneEditText.getText().toString().trim());
							loginEasemob();
						}
					}else{
						ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
					}
				}
			}
		}
	}
	
	private void loginEasemob(){
		// 调用sdk登陆方法登陆聊天服务器
		final String username = forgetPhoneEditText.getText().toString().trim();
		final String pwd = forgetPasswordEditText.getText().toString().trim();
		EMChatManager.getInstance().login(username, pwd, new EMCallBack() {
			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				ZHXYApplication.getInstance().setUserName(username);
				ZHXYApplication.getInstance().setPassword(pwd);
				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					// conversations in case we are auto login
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
				} catch (Exception e) {
					e.printStackTrace();
					//取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
                        public void run() {
                            ZHXYApplication.getInstance().logout(null);
                            ToastUtil.toast(getApplicationContext(), R.string.login_failure_failed);
                        }
                    });
					return;
				}
				//更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(ZHXYApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e(TAG, "update current user nick fail");
				}
				// 进入页面
				if(SharedPreferencesUtil.getClassId(getApplicationContext()) == 0){
					Intent intent = new Intent(ForgetPasswordActivity.this,MyClassActivity.class);
					intent.putExtra(Constants.TYPE, 0);
					startActivity(intent);
				}else{
					Intent intent = new Intent(ForgetPasswordActivity.this,ZHXYActivity.class);
					startActivity(intent);
				}
				ForgetPasswordActivity.this.finish();
			}

           

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ToastUtil.toast(getApplicationContext(), R.string.Login_failed);
					}
				});
				
			}
		});
	}
	
	@SuppressLint("HandlerLeak") 
	Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				forgetSendCodeTextView.setText(String.format(getString(R.string.myinfo_register_code_time), count));
				break;
			case 1:
				forgetSendCodeTextView.setText(R.string.myinfo_register_code_resend);
				forgetSendCodeTextView.setEnabled(true);
				break;
			}

		}
	};

	// 发送验证码按钮
	public void sendCode() {
		String mobiles = forgetPhoneEditText.getText().toString().trim();
		Pattern p = Pattern.compile("^1(3|5|7|8)\\d{9}$");
		Matcher m = p.matcher(mobiles);
		if (!TextUtils.isEmpty(mobiles) && mobiles.length() == 11 && m.find()) {
			SMSSDK.getVerificationCode("86", mobiles);
			forgetSendCodeTextView.setEnabled(false);
			timecount();
		} else {
			ToastUtil.toast(getApplicationContext(),R.string.myinfo_register_phone_error);
		}
	}

	int count = 0;
	boolean flag = true;

	public void timecount() {
		count = 60;
		flag = true;
		new Thread() {
			public void run() {
				try {
					while (flag) {
						sleep(1000);
						count--;
						timeHandler.sendEmptyMessage(0);
						if (count < 0) {
							count = 0;
							flag = false;
							timeHandler.sendEmptyMessage(1);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

}

package com.education.zhxy.myinfo.activity;

import java.util.HashMap;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.education.zhxy.easemob.db.UserDao;
import com.education.zhxy.easemob.domain.User;
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
import com.education.zhxy.util.UpdateManager;

public class LoginActivity extends BasicActivity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();
	
	private HttpTask httpTask;
	
	private UpdateManager updateManager;
	
	private RelativeLayout loginRelativeLayout;
	
	private EditText loginUserNameEditText,loginPasswordEditText;
	
	private TextView loginForgetPasswordTextView,loginRegisterTextView;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loginRelativeLayout:
				if(validate()){
					login();
				}
				break;
			case R.id.loginForgetPasswordTextView:
				Intent forgetPasswordIntent = new Intent(this,ForgetPasswordActivity.class);
				startActivity(forgetPasswordIntent);
				this.finish();
				break;
			case R.id.loginRegisterTextView:
				Intent registerIntent = new Intent(this,RegisterXuZhiActivity.class);
				startActivity(registerIntent);
				this.finish();
				break;
		}
	}
	
	private boolean validate(){
		String username = loginUserNameEditText.getText().toString().trim();
		if(TextUtils.isEmpty(username)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_login_username_null_error);
			return false;
		}
		
		String password = loginPasswordEditText.getText().toString().trim();
		if(TextUtils.isEmpty(password)){
			ToastUtil.toast(getApplicationContext(), R.string.myinfo_login_password_null_error);
			return false;
		}
		
		return true;
	}
	
	private void login(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.TEL, loginUserNameEditText.getText().toString().trim());
		paramMap.put(Constants.PWD, loginPasswordEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_LOGIN, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_login;
	}

	@Override
	public void initUI() {
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
		
		loginUserNameEditText = (EditText)findViewById(R.id.loginUserNameEditText);
		loginPasswordEditText = (EditText)findViewById(R.id.loginPasswordEditText);
		
		loginRelativeLayout = (RelativeLayout)findViewById(R.id.loginRelativeLayout);
		loginRelativeLayout.setOnClickListener(this);
		
		loginForgetPasswordTextView = (TextView)findViewById(R.id.loginForgetPasswordTextView);
		loginForgetPasswordTextView.setOnClickListener(this);
		loginRegisterTextView = (TextView)findViewById(R.id.loginRegisterTextView);
		loginRegisterTextView.setOnClickListener(this);
		
		updateManager = new UpdateManager(getApplicationContext(), 0);
		updateManager.checkUpdateInfo();
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
					if(jsonArray != null && jsonArray.size() > 0){
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USID, jsonObject.getIntValue(Constants.ID));
						SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USER_ROLE, jsonObject.getIntValue(Constants.ROLE));
						SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_HEADER_IMAGE, jsonObject.getString(Constants.IMAGE));
						SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USNAME, loginUserNameEditText.getText().toString().trim());
						loginEasemob(jsonObject.getString(Constants.USNAME));
					}
				}else{
					ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void loginEasemob(final String nick){
		// 调用sdk登陆方法登陆聊天服务器
		final String username = loginUserNameEditText.getText().toString().trim();
		final String pwd = loginPasswordEditText.getText().toString().trim();
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
				UserDao userdao = new UserDao(getApplicationContext());
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				userdao.saveContact(user);
				// 进入页面
				if(SharedPreferencesUtil.getClassId(getApplicationContext()) == 0){
					Intent intent = new Intent(LoginActivity.this,MyClassActivity.class);
					intent.putExtra(Constants.TYPE, 0);
					startActivity(intent);
				}else{
					Intent intent = new Intent(LoginActivity.this,ZHXYActivity.class);
					startActivity(intent);
				}
				LoginActivity.this.finish();
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

}

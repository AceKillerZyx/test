package com.education.zhxy.chat.activity;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.adapter.PersonAdapter;
import com.education.zhxy.chat.data.bean.ClassInfo;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.common.view.HorizontalListView;
import com.education.zhxy.easemob.controller.HXSDKHelper;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
public class ClassInfoActivity extends BasicActivity {
	
	private static final String TAG = ClassInfoActivity.class.getSimpleName();
	
	private HttpTask httpTask;
	
	private CheckBox chatClassInfoShieldCheckBox;
	
	private PersonAdapter personAdapter;
	
	private Button chatClassInfoExitButton;
	
	private HorizontalListView chatClassInfoHorizontalListView;
	
	private TextView chatClassInfoBackTextView, chatClassInfoNameTextView,
			chatClassInfoSloganTextView, chatClassInfoWillingTextView,
			chatClassInfoHistoryHomeworkTextView, chatClassInfoRecordTextView;

	private EMChatOptions chatOptions;
	
	//自定义ProgressDialog
    private CustomProgressDialog pd = null;
    
    private String groupid = "";

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chatClassInfoBackTextView:
				Intent intentChat = new Intent(this,ChatActivity.class);
				intentChat.putExtra("chatType",Constants.CHATTYPE_GROUP);
				intentChat.putExtra("groupId", groupid);
				startActivity(intentChat);
				this.finish();
				break;
			case R.id.chatClassInfoHistoryHomeworkTextView:
				Intent intent = new Intent(this,HistoryWorkActivity.class);
				startActivity(intent);
				break;
			case R.id.chatClassInfoRecordTextView:
				new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("确定删除聊天记录")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//删除所有会话记录(包括本地)
						EMChatManager.getInstance().deleteAllConversation();
					}
				}).create().show();
				break;
			case R.id.chatClassInfoExitButton:
				update();
				break;
		}
	}
	
	private void update() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.CLASSID, String.valueOf(0));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.CHAT_CLASS_INFO_UPDATE, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
		
	}
	
	@Override
	public int initLayout() {
		return R.layout.chat_class_info;
	}

	@Override
	public void initUI() {
		
		chatOptions = EMChatManager.getInstance().getChatOptions();
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
		
		chatClassInfoBackTextView = (TextView)findViewById(R.id.chatClassInfoBackTextView);
		chatClassInfoBackTextView.setOnClickListener(this);
		
		chatClassInfoNameTextView = (TextView)findViewById(R.id.chatClassInfoNameTextView);
		chatClassInfoNameTextView.setText(SharedPreferencesUtil.getClassName(getApplicationContext()));
		
		chatClassInfoSloganTextView = (TextView)findViewById(R.id.chatClassInfoSloganTextView);
		chatClassInfoWillingTextView = (TextView)findViewById(R.id.chatClassInfoWillingTextView);
		
		personAdapter = new PersonAdapter(getApplicationContext());
		chatClassInfoHorizontalListView = (HorizontalListView)findViewById(R.id.chatClassInfoHorizontalListView);
		chatClassInfoHorizontalListView.setAdapter(personAdapter);
		
		chatClassInfoHistoryHomeworkTextView = (TextView)findViewById(R.id.chatClassInfoHistoryHomeworkTextView);
		chatClassInfoHistoryHomeworkTextView.setOnClickListener(this);
		chatClassInfoRecordTextView = (TextView)findViewById(R.id.chatClassInfoRecordTextView);
		chatClassInfoRecordTextView.setOnClickListener(this);
		
		chatClassInfoExitButton = (Button)findViewById(R.id.chatClassInfoExitButton);
		chatClassInfoExitButton.setOnClickListener(this);
		
		chatClassInfoShieldCheckBox = (CheckBox)findViewById(R.id.chatClassInfoShieldCheckBox);
		
		Log.e(TAG, "status==="+HXSDKHelper.getInstance().getModel().getSettingMsgNotification());
		
		if(chatOptions.getNotificationEnable()){
			chatClassInfoShieldCheckBox.setSelected(true);
		}else{
			chatClassInfoShieldCheckBox.setSelected(false);
		}
		chatClassInfoShieldCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Log.e(TAG, "false");
					chatOptions.setNotificationEnable(false);
					chatClassInfoShieldCheckBox.setSelected(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					HXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
					/**
					* 屏蔽群消息后，就不能接收到此群的消息 （群创建者不能屏蔽群消息）（还是群里面的成员，但不再接收消息）  
					* @param groupId， 群id
					* @throws EasemobException
					*/
					try {
						EMGroupManager.getInstance().blockGroupMessage(groupid);
					} catch (EaseMobException e) {
						e.printStackTrace();
					}
				} else {
					Log.e(TAG, "true");
					chatOptions.setNotificationEnable(true);
					chatClassInfoShieldCheckBox.setSelected(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					HXSDKHelper.getInstance().getModel().setSettingMsgNotification(true);
					/**
					* 取消屏蔽群消息,就可以正常收到群的所有消息
					* @param groupId
					* @throws EaseMobException
					*/
					try {
						EMGroupManager.getInstance().unblockGroupMessage(groupid);
					} catch (EaseMobException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void initData() {
		ClassInfo classInfo = (ClassInfo)getIntent().getSerializableExtra(Constants.CLASSINFO);
		groupid = getIntent().getStringExtra("groupId");
		if(classInfo != null){
			initClassInfo(classInfo);
		}
	}
	
	private void initClassInfo(ClassInfo classInfo){
		chatClassInfoNameTextView.setText(classInfo.getClassName());
		chatClassInfoSloganTextView.setText(classInfo.getSlogan());
		chatClassInfoWillingTextView.setText(classInfo.getSendWord());
		if(classInfo.getList() != null && classInfo.getList().size() > 0){
			personAdapter.resetData(classInfo.getList());
		}
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
					ToastUtil.toast(getApplicationContext(), R.string.chat_class_exit_success);
					SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_CLASS_ID, 0);
					this.finish();
				} else {
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}

}

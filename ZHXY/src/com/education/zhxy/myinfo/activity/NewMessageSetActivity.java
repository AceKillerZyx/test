package com.education.zhxy.myinfo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.easemob.helper.ZHXYHXSDKHelper;
import com.education.zhxy.easemob.model.ZHXYHXSDKModel;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;

public class NewMessageSetActivity extends BasicActivity {
	private TextView myinfoNewMessageBackTextView,textview1,textview2;
	//声音 震动  免打扰
	private RelativeLayout rl_switch_notification,rl_switch_sound,rl_switch_vibrate;
	/**
	 * 打开新消息通知imageView
	 */
	private ImageView iv_switch_open_notification;
	/**
	 * 关闭新消息通知imageview
	 */
	private ImageView iv_switch_close_notification;
	/**
	 * 打开声音提示imageview
	 */
	private ImageView iv_switch_open_sound;
	/**
	 * 关闭声音提示imageview
	 */
	private ImageView iv_switch_close_sound;
	/**
	 * 打开消息震动提示
	 */
	private ImageView iv_switch_open_vibrate;
	/**
	 * 关闭消息震动提示
	 */
	private ImageView iv_switch_close_vibrate;
	private EMChatOptions chatOptions;
	ZHXYHXSDKModel model;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myinfoNewMessageBackTextView:
			this.finish();
			break;
		case R.id.rl_switch_notification:
			if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
				iv_switch_open_notification.setVisibility(View.INVISIBLE);
				iv_switch_close_notification.setVisibility(View.VISIBLE);
				rl_switch_sound.setVisibility(View.GONE);
				rl_switch_vibrate.setVisibility(View.GONE);
				textview1.setVisibility(View.GONE);
				textview2.setVisibility(View.GONE);
				chatOptions.setNotificationEnable(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);

				ZHXYHXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
			} else {
				iv_switch_open_notification.setVisibility(View.VISIBLE);
				iv_switch_close_notification.setVisibility(View.INVISIBLE);
				rl_switch_sound.setVisibility(View.VISIBLE);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				textview1.setVisibility(View.VISIBLE);
				textview2.setVisibility(View.VISIBLE);
				chatOptions.setNotificationEnable(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				ZHXYHXSDKHelper.getInstance().getModel().setSettingMsgNotification(true);
			}
			break;
		case R.id.rl_switch_sound:
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				ZHXYHXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				ZHXYHXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				ZHXYHXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				ZHXYHXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
			}
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
		return R.layout.myinfo_set;
	}

	@SuppressLint("CutPasteId")
	@Override
	public void initUI() {
		myinfoNewMessageBackTextView=(TextView) findViewById(R.id.myinfoNewMessageBackTextView);
		myinfoNewMessageBackTextView.setOnClickListener(this);
		
		
		rl_switch_notification=(RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_sound=(RelativeLayout) findViewById(R.id.rl_switch_sound);
		rl_switch_sound.setOnClickListener(this);
		rl_switch_vibrate=(RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		rl_switch_vibrate.setOnClickListener(this);
		
		iv_switch_open_notification = (ImageView) findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) findViewById(R.id.iv_switch_close_notification);
		iv_switch_open_sound = (ImageView) findViewById(R.id.iv_switch_open_sound);
		iv_switch_close_sound = (ImageView) findViewById(R.id.iv_switch_close_sound);
		iv_switch_open_vibrate = (ImageView) findViewById(R.id.iv_switch_open_vibrate);
		iv_switch_close_vibrate = (ImageView) findViewById(R.id.iv_switch_close_vibrate);
		
		textview1 = (TextView)findViewById(R.id.textView1);
		textview2 = (TextView)findViewById(R.id.textView2);
		
		chatOptions = EMChatManager.getInstance().getChatOptions();
		
		model = (ZHXYHXSDKModel) ZHXYHXSDKHelper.getInstance().getModel();
		
		// 震动和声音总开关，来消息时，是否允许此开关打开
		// the vibrate and sound notification are allowed or not?
		if (model.getSettingMsgNotification()) {
			iv_switch_open_notification.setVisibility(View.VISIBLE);
			iv_switch_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_notification.setVisibility(View.INVISIBLE);
			iv_switch_close_notification.setVisibility(View.VISIBLE);
		}
		
		// 是否打开声音
		// sound notification is switched on or not?
		if (model.getSettingMsgSound()) {
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		
		// 是否打开震动
		// vibrate notification is switched on or not?
		if (model.getSettingMsgVibrate()) {
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}
	
	}

	@Override
	public void initData() {
		
	}

}

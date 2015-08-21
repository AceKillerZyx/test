package com.education.zhxy.common.activity;

import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.education.zhxy.R;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.http.core.HttpListener;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.CommonUtils;

public abstract class BasicActivity extends Activity implements OnClickListener,HttpListener {
	private static final int notifiId = 11;
	
	protected NotificationManager notificationManager;
	private List<UserInfo> userInfoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		this.setContentView(this.initLayout());
		this.initUI();
		this.initData();
	}
	
	/**
	 * 设置布局
	 * @return
	 */
	public abstract int initLayout();
	
	/**
	 * 初始化UI
	 */
	public abstract void initUI();

	/**
	 * 初始化数据
	 */
	public abstract void initData();
	
	@Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        EMChatManager.getInstance().activityResumed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if(!EasyUtils.isAppRunningForeground(this)){
            return;
        }
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);
        
        String ticker = CommonUtils.getMessageDigest(message, this);
        String st = getResources().getString(R.string.expression);
        if(message.getType() == Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
        //设置状态栏提示
        if (userInfoList != null) {
			for (int i = 0; i < userInfoList.size(); i++) {
				System.out.println("通知："+userInfoList.get(i).getTel());
				if (userInfoList.get(i).getTel().equals(message.getFrom())) {
					String nick = userInfoList.get(i).getUsName();
					Log.e("from nick", nick);
					mBuilder.setTicker(nick+": " + ticker);
				}
			}
		}
        
        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }

    /**
     * 返回
     * @param view
     */
    public void back(View view) {
        finish();
    }
    
}

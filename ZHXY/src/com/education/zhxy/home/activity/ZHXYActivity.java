package com.education.zhxy.home.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.common.activity.ChatNotifcation;
import com.education.zhxy.fragment.MainFragment;
import com.education.zhxy.fragment.MyinfoFragment;
import com.education.zhxy.fragment.RobFragment;
import com.education.zhxy.util.CommonUtils;
import com.education.zhxy.util.SharedPreferencesUtil;


public class ZHXYActivity extends FragmentActivity implements OnClickListener,ChatNotifcation{
	
	private int i = 0;
	
	private boolean isReceiver = false;
	
	private static FragmentManager fragmentManager;
	
	private Fragment mainFragment,robFragment,myinfoFragment;
	
	private RadioButton mainRadioButton,robRadioButton,myinfoRadioButton;
	
	
	private NewMessageBroadcastReceiver receiver;
	
	 private static final int notifiId = 11;
	 protected NotificationManager notificationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zhxy_home);
		fragmentManager = getSupportFragmentManager(); //获取FragmentManager实例
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		init();  //初始化页面
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mainRadioButton:
				mainFragment = new MainFragment();  
                changeFrament(mainFragment, null, "mainFragment");  
                changeRadioButtonImage(v.getId());
				break;
			case R.id.robRadioButton:
				robFragment = new RobFragment();  
                changeFrament(robFragment, null, "robFragment");  
                changeRadioButtonImage(v.getId());
				break;
			case R.id.myinfoRadioButton:
				myinfoFragment = new MyinfoFragment();  
                changeFrament(myinfoFragment, null, "myinfoFragment");  
                changeRadioButtonImage(v.getId());
				break;
		}
	}
	
	// 初始化信息  
    public void init() {  
    	mainRadioButton = (RadioButton) findViewById(R.id.mainRadioButton);  
    	mainRadioButton.setOnClickListener(this);
    	robRadioButton = (RadioButton) findViewById(R.id.robRadioButton);  
    	robRadioButton.setOnClickListener(this);
    	myinfoRadioButton = (RadioButton) findViewById(R.id.myinfoRadioButton); 
    	myinfoRadioButton.setOnClickListener(this);
    	mainFragment = new MainFragment();  
        robFragment = new RobFragment();
        myinfoFragment = new MyinfoFragment();
        //初始化第一个页面
        changeFrament(mainFragment, null, "mainFragment");
        mainRadioButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.tab_main_pressed, 0, 0);
        mainRadioButton.setTextColor(getResources().getColor(R.color.common_title_bg));
        
     // 注册接收消息广播
     		receiver = new NewMessageBroadcastReceiver();
     		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
     		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
     		intentFilter.setPriority(5);
     		registerReceiver(receiver, intentFilter);

     		// 注册一个ack回执消息的BroadcastReceiver
     		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
     		ackMessageIntentFilter.setPriority(5);
     		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

     		//注册一个透传消息的BroadcastReceiver
     		IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
     		cmdMessageIntentFilter.setPriority(5);
     		registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
     		
     		
     		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
     		EMChat.getInstance().setAppInited();
    }
    
    // 切界面  
    public void changeFrament(Fragment fragment, Bundle bundle, String tag) {  
  
        for (int i = 0, count = fragmentManager.getBackStackEntryCount(); i < count; i++) {  
        	fragmentManager.popBackStack();
        }  
        FragmentTransaction fg = fragmentManager.beginTransaction();  
        fragment.setArguments(bundle);  
        fg.add(R.id.fragmentRoot, fragment, tag);  
        fg.addToBackStack(tag);  
        fg.commitAllowingStateLoss();  
  
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK) {  
            if (i == 0) {  
                Toast.makeText(this, "再点击一次将退出程序", Toast.LENGTH_SHORT).show();  
                i++;  
            } else { 
            	/*SharedPreferencesUtil.putBoolean(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_MUSIC, false);
            	stopService(new Intent(getApplicationContext(),MusicService.class));*/
                this.finish();
            }  
            return false;  
        }  
        return super.onKeyDown(keyCode, event); 
	}
	
	/*
	 * 更换RadioButton图片  
	 */
    public void changeRadioButtonImage(int btids) {  
        int[] imageh = { R.drawable.tab_main_normal, R.drawable.tab_rob_normal,R.drawable.tab_myinfo_normal };  
        int[] imagel = { R.drawable.tab_main_pressed, R.drawable.tab_rob_pressed, R.drawable.tab_myinfo_pressed };  
        int[] rabt = { R.id.mainRadioButton, R.id.robRadioButton,R.id.myinfoRadioButton };  
        switch (btids) {  
	        case R.id.mainRadioButton:  
	            changeImage(imageh, imagel, rabt, 0);  
	            break;  
	        case R.id.robRadioButton:  
	            changeImage(imageh, imagel, rabt, 1);  
	            break;  
	        case R.id.myinfoRadioButton:  
	            changeImage(imageh, imagel, rabt, 2);  
	            break;  
        }  
    }  
	
    /*
     * 改变图片
     */
	public void changeImage(int[] image1, int[] image2, int[] rabtid, int index) {  
        for (int i = 0; i < image1.length; i++) {  
            if (i != index) {  
                ((RadioButton) findViewById(rabtid[i])).setCompoundDrawablesWithIntrinsicBounds(0, image1[i],0, 0);  
                ((RadioButton) findViewById(rabtid[i])).setTextColor(getResources().getColor(R.color.main_bottom_gray));
            } else {  
                ((RadioButton) findViewById(rabtid[i])).setCompoundDrawablesWithIntrinsicBounds(0, image2[i],0, 0);  
                ((RadioButton) findViewById(rabtid[i])).setTextColor(getResources().getColor(R.color.common_title_bg));
            }  
        }  
    }
	
	protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {
        	/*SharedPreferencesUtil.putBoolean(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_MUSIC, false);
        	stopService(new Intent(getApplicationContext(),MusicService.class));*/
            finish();  
        }  
    };  
    
    @Override
	protected void onResume() {
    	super.onResume();
    	// 在当前的activity中注册广播  
        IntentFilter filter = new IntentFilter();  
        filter.addAction(Constants.EXIT_LOGIN);  
        isReceiver = true;
        this.registerReceiver(this.broadcastReceiver, filter);
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(isReceiver){
    		this.unregisterReceiver(this.broadcastReceiver);
    	}
    	// 注销广播接收者
    			try {
    				unregisterReceiver(receiver);
    			} catch (Exception e) {
    			}
    			try {
    				unregisterReceiver(ackMessageReceiver);
    			} catch (Exception e) {
    			}
    			try {
    				unregisterReceiver(cmdMessageReceiver);
    			} catch (Exception e) {
    			}
    }
    
    
    /**
	 * 透传消息BroadcastReceiver
	 */
	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {
		
		@SuppressWarnings("unused")
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			EMLog.d("收到消息", "收到透传消息");
			//获取cmd message对象
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = intent.getParcelableExtra("message");
			//获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
			String action = cmdMsgBody.action;//获取自定义action
			
			//获取扩展属性 此处省略
//			message.getStringAttribute("");
			EMLog.d("收到消息", String.format("透传消息：action:%s,message:%s", action,message.toString()));
//			String st9 = getResources().getString(R.string.receive_the_passthrough);
//			Toast.makeText(MainActivity.this, st9+action, Toast.LENGTH_SHORT).show();
		}
	};
    
    
	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					// 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
					if (ChatActivity.activityInstance != null) {
						if (msg.getChatType() == ChatType.Chat) {
							if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
								return;
						}
					}
					msg.isAcked = true;
				}
			}
		}
	};

	
	
    /**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				}
			}
			
			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();
			
			notifyNewMessage(message);  
			
			// 刷新bottom bar消息未读
			//updateUnreadLabel();
		}
	}
    
	 /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    public void notifyNewMessage(EMMessage message) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if(!EasyUtils.isAppRunningForeground(this)){
            return;
        }
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.zhxy_logo)
                .setContentTitle("新消息")
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);
        
        String ticker = CommonUtils.getMessageDigest(message, this);
        String st = getResources().getString(R.string.expression);
        if(message.getType() == Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
        //设置状态栏提示
        mBuilder.setTicker(message.getFrom()+": " + ticker);
        
        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(this, ChatActivity.class);
        //程序内 跳转 单聊
		if (message.getChatType() == ChatType.Chat) {
			intent.putExtra(Constants.TEL, message.getFrom());
			intent.putExtra(Constants.USNAME, message.getUserName());
	        Log.e("TAG", "收到的手机号 = " + message.getFrom());
	        Log.e("得到的用户名为：", message.getUserName());
		} 
		//程序内 跳转 群聊
		if (message.getChatType() == ChatType.GroupChat) {
			intent.putExtra(Constants.TEL, message.getTo());
			intent.putExtra("chatType", Constants.CHATTYPE_GROUP);
			intent.putExtra(Constants.CLASS_SHORT_NAME, SharedPreferencesUtil.getClassShortName(getApplicationContext()));
	        Log.e("TAG", "收到的群组id = " + message.getTo() +"传递的 班级名称 = " + SharedPreferencesUtil.getClassShortName(getApplicationContext()));
		}
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(notifiId, notification);
        
        Intent intent2 = new Intent();  
        intent2.setAction(Constants.AREA_UNREAD);
        intent2.putExtra(Constants.MSG_FROM, message.getFrom());  
        sendOrderedBroadcast(intent2, null);
    }
    
    
    
}

package com.education.zhxy.chat.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.GroupReomveListener;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.adapter.ExpressionAdapter;
import com.education.zhxy.chat.adapter.ExpressionPagerAdapter;
import com.education.zhxy.chat.adapter.MessageAdapter;
import com.education.zhxy.chat.adapter.PersonalAdapter;
import com.education.zhxy.chat.data.bean.ClassInfo;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.easemob.activity.BaiduMapActivity;
import com.education.zhxy.easemob.activity.ImageGridActivity;
import com.education.zhxy.easemob.activity.VoiceCallActivity;
import com.education.zhxy.easemob.listener.VoicePlayClickListener;
import com.education.zhxy.easemob.utils.SmileUtils;
import com.education.zhxy.easemob.view.ExpandGridView;
import com.education.zhxy.easemob.widget.PasteEditText;
import com.education.zhxy.home.activity.ZHXYApplication;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.CommonUtils;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

@SuppressLint("NewApi") @SuppressWarnings({"deprecation","rawtypes"})
public class ChatActivity extends BasicActivity  {
	
	private static final String TAG = ChatActivity.class.getSimpleName();
	
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;
	
	private HttpTask searchClassInfoHttpTask;
	
	public static final String COPY_IMAGE = "EASEMOBIMG";
	
	private ClassInfo classInfo;
	//个人列表
	private View commonView;

	private Dialog alertDialog;
	
	private ListView chatPersonalListView;
	
	private boolean isShow = false;
	
	private PersonalAdapter personalAdapter;
	
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	// private ViewPager expressionViewpager;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	@SuppressWarnings("unused")
	private ImageView locationImgview;
	private View more;
	@SuppressWarnings("unused")
	private int position;
	private ClipboardManager clipboard;
	private ViewPager expressionViewpager;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private int chatType;
	private EMConversation conversation;
	private NewMessageBroadcastReceiver receiver;
	public static ChatActivity activityInstance = null;
	// 给谁发送消息
	private String toChatUsername;
	private String groupName;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private File cameraFile;
	static int resendPos;

	private GroupListener groupListener;

	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private Button btnMore;
	public String playMsgId;
	private String groupShortName;
	
	private TextView chatBackTextView;
	
	private ImageView chatPersonalImageView,chatInfoImageView;
	
	private RelativeLayout chatTopRelativeLayout;
	
	private boolean isSingle = false ;
	
	
	private Intent intent;
	private String name;
	private UserInfo userInfo;
	// 自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@SuppressLint("HandlerLeak") 
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private EMGroup group;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chatBackTextView:
				if (isSingle) {	
					isSingle = false ;
					chatPersonalImageView.setVisibility(View.VISIBLE);
					chatInfoImageView.setVisibility(View.VISIBLE);
					unRegister();
//					chatType =  Constants.CHATTYPE_GROUP;
					chatBackTextView.setText(SharedPreferencesUtil.getClassShortName(getApplicationContext()));
					setUpView(classInfo.getList());
				} else {
					this.finish();
				}
				break;
			case R.id.chatInfoImageView:
				if(classInfo != null){
					Intent intent = new Intent(this,ClassInfoActivity.class);
					intent.putExtra(Constants.CLASSINFO, classInfo);
					intent.putExtra("groupId", toChatUsername);
					startActivity(intent);
					this.finish();
				}
				break;
			case R.id.chatPersonalImageView:
				if(!isShow){
					isShow = true;
					personal();
				}else{
					isShow = false;
					if(alertDialog != null && alertDialog.isShowing()){
						alertDialog.dismiss();
					}
				}
				break;
			case R.id.btn_send: // 点击发送按钮(发文字和表情)
				String s = mEditTextContent.getText().toString();
				sendText(s);
				break;
			case R.id.btn_take_picture: 
				selectPicFromCamera();// 点击照相图标
				break;
			case R.id.btn_picture:
				selectPicFromLocal(); // 点击图片图标
				break;
			case R.id.btn_location: // 位置
				startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
				break;
			case R.id.iv_emoticons_normal:// 点击显示表情框
				more.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.INVISIBLE);
				iv_emoticons_checked.setVisibility(View.VISIBLE);
				btnContainer.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.VISIBLE);
				hideKeyboard();
				break;
			case R.id.iv_emoticons_checked:// 点击隐藏表情框
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				btnContainer.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				break;
			case R.id.btn_video:
				// 点击摄像图标
				Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
				break;
			case R.id.btn_file:  // 点击文件图标
				selectFileFromLocal();
				break;
			case R.id.btn_voice_call://点击语音电话图标
				if (!EMChatManager.getInstance().isConnected())
					ToastUtil.toast(getApplicationContext(), R.string.not_connect_to_server);
				else
					startActivity(new Intent(ChatActivity.this, VoiceCallActivity.class).putExtra("username", toChatUsername)
					        .putExtra("isComingCall", false));
				break;
			/*case R.id.btn_video_call://视频通话
				if (!EMChatManager.getInstance().isConnected())
	                Toast.makeText(this, st1, 0).show();
	            else
	                startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername)
	                        .putExtra("isComingCall", false));
				break;*/
		}
	}
	
	private void searchClassInfo() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.CLASSID, String.valueOf(SharedPreferencesUtil.getClassId(getApplicationContext())));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.CHAT_CLASS_INFO, false); // GET
		searchClassInfoHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		searchClassInfoHttpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.chat_main;
	}

	@SuppressLint("InflateParams") @Override
	public void initUI() {
		pd = new CustomProgressDialog(this);
		chatTopRelativeLayout = (RelativeLayout)findViewById(R.id.chatTopRelativeLayout);
		intent=getIntent();
		chatBackTextView = (TextView)findViewById(R.id.chatBackTextView);
		chatBackTextView.setOnClickListener(this);
		
		
		//得到ChatListActivity界面传来的值获得手机号
		if ((UserInfo)intent.getSerializableExtra(Constants.CHAT_USERINFO) !=null) {
			userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.CHAT_USERINFO);
			toChatUsername = userInfo.getTel();
			chatBackTextView.setText((CharSequence) intent.getSerializableExtra(Constants.USNAME));
			Log.e("toChatUsername",toChatUsername);
		}else {
			toChatUsername = getIntent().getStringExtra(Constants.TEL);
			Log.e("程序内：单聊的名字是：",intent.getStringExtra(Constants.USNAME));
			chatBackTextView.setText(intent.getStringExtra(Constants.USNAME));
		}
		
		if (getIntent().getStringExtra("userId") != null) {
			toChatUsername = getIntent().getStringExtra("userId");
			chatType = Constants.CHATTYPE_SINGLE;
			Log.e("=================", "单聊 ：收到的tochatUsername = " + toChatUsername);
		}
		
		chatInfoImageView = (ImageView)findViewById(R.id.chatInfoImageView);
		chatInfoImageView.setOnClickListener(this);
		chatPersonalImageView = (ImageView)findViewById(R.id.chatPersonalImageView);
		chatPersonalImageView.setOnClickListener(this);
		
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		commonView = layoutInflater.inflate(R.layout.chat_personal, null);
		
		personalAdapter = new PersonalAdapter(getApplicationContext(),null);
		chatPersonalListView = (ListView)commonView.findViewById(R.id.chatPersonalListView);
		chatPersonalListView.setAdapter(personalAdapter);
//		chatPersonalListView.setOnItemClickListener(this);
		
		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (Button) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		more = findViewById(R.id.more);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	@SuppressLint("ClickableViewAccessibility") 
	private void setUpView(List<UserInfo> userInfoList) {
		activityInstance = this;
		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);
		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		System.out.println("chatType&&&&&&&&&&&&" + chatType);
		// 判断单聊还是群聊

		if (chatType == Constants.CHATTYPE_SINGLE) { // 单聊
//			toChatUsername = getIntent().getStringExtra("userId");
//			chatBackTextView.setText(toChatUsername);
			for(int i = 0;i<userInfoList.size() ; i++){
				if (userInfoList.get(i).getTel().equals(toChatUsername)) {
					chatBackTextView.setText(userInfoList.get(i).getUsName());
				}
			}
			findViewById(R.id.container_voice_call).setVisibility(View.VISIBLE);
			findViewById(R.id.container_video_call).setVisibility(View.GONE);
//			conversation = EMChatManager.getInstance().getConversation(toChatUsername,false);
		} else {
			// 群聊
			if (getIntent().getStringExtra("groupId") != null) {
			findViewById(R.id.container_voice_call).setVisibility(View.VISIBLE);
			findViewById(R.id.container_video_call).setVisibility(View.GONE);
			toChatUsername = getIntent().getStringExtra("groupId");
			Log.e("=================", "群聊 ：收到的tochatUsername = " + toChatUsername);
			groupName = getIntent().getStringExtra("groupName");
			Log.e(TAG, "groupName " + groupName);
			Log.e(TAG, "toChatUsername" + toChatUsername); 

			group = EMGroupManager.getInstance().getGroup(toChatUsername);
			groupShortName = SharedPreferencesUtil.getClassShortName(getApplicationContext());
			if(group != null){
				chatBackTextView.setText(groupShortName);
			}
			else
				chatBackTextView.setText(groupShortName);
			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername,true);
			}
		}
		conversation = EMChatManager.getInstance().getConversation(toChatUsername);
		// 把此会话的未读数置为0		
		conversation.resetUnreadMsgCount();
		
		// 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
		// 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
		final List<EMMessage> msgs = conversation.getAllMessages();
		System.out.println("List<EMMessage> msgs size:" + msgs.size());
		int msgCount = msgs != null ? msgs.size() : 0;
		if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
			String msgId = null;
			if (msgs != null && msgs.size() > 0) {
				msgId = msgs.get(0).getMsgId();
			}
			if (chatType == Constants.CHATTYPE_SINGLE) {
				conversation.loadMoreMsgFromDB(msgId, pagesize);
			} else {
				conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
			}
		}
		
		System.out.println("chatType*********" + chatType);
		System.out.println("userInfoList*********" + userInfoList);
		adapter = new MessageAdapter(this, toChatUsername, chatType,userInfoList);
		// 显示消息
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new ListScrollListener());
		int count = listView.getCount();
		if (count > 0) {
			listView.setSelection(count - 1);
		}

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				return false;
			}
		});
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

		// 注册一个消息送达的BroadcastReceiver
		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
		deliveryAckMessageIntentFilter.setPriority(5);
		registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);

		// 监听当前会话的群聊解散被T事件
		groupListener = new GroupListener();
		EMGroupManager.getInstance().addGroupChangeListener(groupListener);

		// show forward message if the message is not null
		String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
		if (forward_msg_id != null) {
			// 显示发送要转发的消息
			forwardMessage(forward_msg_id);
		}
		adapter.refresh();
	}
	
	/*
	 * 个人列表
	 */
	private void personal() {
		if(classInfo != null && classInfo.getList() != null && classInfo.getList().size() > 0){
			personalAdapter.resetData(classInfo.getList());
			if (alertDialog == null) {
				alertDialog = new Dialog(this, R.style.commonDialog);
				alertDialog.setContentView(commonView);
				Display d = getWindowManager().getDefaultDisplay();
				Rect rect = new Rect();  
		        Window window = getWindow();  
		        chatTopRelativeLayout.getWindowVisibleDisplayFrame(rect);  
		        // 标题栏跟状态栏的总体高度  
		        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();  
		        // 标题栏的高度：用上面的值减去状态栏的高度及为标题栏高度  
		        int titleBarHeight = contentViewTop + 100;  
				ToastUtil.setDialogLocation(this, alertDialog, d.getWidth()/2,d.getHeight() - titleBarHeight, 0, d.getHeight() - titleBarHeight);
			}
			if (alertDialog.isShowing()) {
				alertDialog.dismiss();
			} else {
				alertDialog.show();
			}
		}
	}
	
	@Override
	public void initData() {
		searchClassInfo();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
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
				if(task == searchClassInfoHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if(jsonArray != null && jsonArray.size() > 0){
							JSONObject jsonObject = jsonArray.getJSONObject(0);
							classInfo = JSONObject.toJavaObject(jsonObject, ClassInfo.class);
							setUpView(classInfo.getList());
						}
					}else{
						ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
					}
				}
			}
		}
	}
	
	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
				case RESULT_CODE_COPY: // 复制消息
					EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
					// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
					// ((TextMessageBody) copyMsg.getBody()).getMessage()));
					clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
					break;
				case RESULT_CODE_DELETE: // 删除消息
					EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
					conversation.removeMessage(deleteMsg.getMsgId());
					adapter.refresh();
					listView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
					break;
	
				case RESULT_CODE_FORWARD: // 分享消息
					Intent i = new Intent(ChatActivity.this,ShareActivity.class);
					startActivity(i);
					break;
					
			}
		}
		if (resultCode == RESULT_OK) { //清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频
				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !"".equals(locationAddress)) {
					more(more);
					sendLocationMsg(latitude, longitude, "", locationAddress);
					adapter.refresh();
				} else {
					ToastUtil.toast(getApplicationContext(), R.string.unable_to_get_loaction);
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
			        || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
			        || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				/*EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				addUserToBlacklist(deleteMsg.getFrom());*/
			} else if (conversation.getMsgCount() > 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				adapter.refresh();
			}
		}
	}
	
	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			ToastUtil.toast(getApplicationContext(), R.string.sd_card_does_not_exist);
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(), ZHXYApplication.getInstance().getUserName()
				+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {

		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == Constants.CHATTYPE_GROUP) {
				message.setChatType(ChatType.GroupChat);
			} else{
				message.setChatType(ChatType.Chat);
			}
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(toChatUsername);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			mEditTextContent.setText("");

			setResult(RESULT_OK);

		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == Constants.CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			message.setReceipt(toChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			conversation.addMessage(message);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == Constants.CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		conversation.addMessage(message);

		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
		// more(more);
	}

	/**
	 * 发送视频消息
	 */
	private void sendVideo(final String filePath, final String thumbPath, final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == Constants.CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			String to = toChatUsername;
			message.setReceipt(to);
			VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
			message.addBody(body);
			conversation.addMessage(message);
			listView.setAdapter(adapter);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * 发送位置信息
	 * 
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == Constants.CHATTYPE_GROUP)message.setChatType(ChatType.GroupChat);
		LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(toChatUsername);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);

	}

	/**
	 * 发送文件
	 * 
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			ToastUtil.toast(getApplicationContext(), R.string.File_does_not_exist);
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			ToastUtil.toast(getApplicationContext(), R.string.The_file_is_not_greater_than_10_m);
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == Constants.CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(toChatUsername);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
		EMMessage msg = null;
		msg = conversation.getMessage(resendPos);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;

		adapter.refresh();
		listView.setSelection(resendPos);
	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 点击清空聊天记录
	 * 
	 * @param view
	 */
	public void emptyHistory(View view) {
		String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
		startActivityForResult(
				new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5).putExtra("cancel", true),
				REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 点击进入群组详情
	 * 
	 * @param view
	 */
	public void toGroupDetails(View view) {
	   /* if(group == null){
	        Toast.makeText(getApplicationContext(), R.string.gorup_not_found, 0).show();
	        return;
	    }
		startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
				REQUEST_CODE_GROUP_DETAIL);*/
	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 消息广播接收者
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 记得把广播给终结掉
			abortBroadcast();

			String username = intent.getStringExtra("from");
			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgid);
			Log.i(TAG, "message chatType******++++++++======== " + message.getChatType());
			// 如果是群聊消息，获取到group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			}
			System.out.println("username===" + username);
			System.out.println("toChatUsername===" + toChatUsername);
			if (!username.trim().equals(toChatUsername.trim())) {
				// 消息不是发给当前会话，return
			    notifyNewMessage(message);
				return;
			}
			Log.i(TAG, "username==" + username + "==toChatUsername==" + toChatUsername);
			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// 通知adapter有新消息，更新ui
			adapter.refresh();
			Log.i(TAG, "listView.getCount()=== " + listView.getCount());
			listView.setSelection(listView.getCount() - 1);

		}
	}

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
					msg.isAcked = true;
				}
			}
			adapter.notifyDataSetChanged();

		}
	};

	/**
	 * 消息送达BroadcastReceiver
	 */
	private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
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
					msg.isDelivered = true;
				}
			}

			adapter.notifyDataSetChanged();
		}
	};
	private PowerManager.WakeLock wakeLock;

	/**
	 * 按住说话listener
	 * 
	 */
	@SuppressLint({ "Wakelock", "ClickableViewAccessibility" }) 
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}
	
	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {
						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.education.zhxy.easemob.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {
								int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											mEditTextContent.getEditableText().delete(i, selectionStart);
										else
											mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegister();
	}
	
	private void unRegister(){
		activityInstance = null;
		EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
		// 注销广播
		try {
			unregisterReceiver(receiver);
			receiver = null;
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
			ackMessageReceiver = null;
			unregisterReceiver(deliveryAckMessageReceiver);
			deliveryAckMessageReceiver = null;
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(group != null){
			Log.e(TAG, " OnResume 的groupShortName = " + groupShortName);
			chatBackTextView.setText(groupShortName);
//			adapter.refresh();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock != null && wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == Constants.CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						listView.setSelection(messages.size() - 1);
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
//		String username = intent.getStringExtra("groupId");
		if (!StringUtil.isEmpty(toChatUsername) && toChatUsername.equals(username))
			super.onNewIntent(intent);
		if (haveMoreData) {
			
		}
		else {
			finish();
			startActivity(intent);
		}

	}
	
	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		
	}

	/**
	 * 监测群组解散或者被T事件
	 * 
	 */
	class GroupListener extends GroupReomveListener {
		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (toChatUsername.equals(groupId)) {
						ToastUtil.toast(getApplicationContext(),R.string.you_are_group );
						/*if (GroupDetailsActivity.instance != null)
							GroupDetailsActivity.instance.finish();
						finish();*/
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			// 群组解散正好在此页面，提示群组被解散，并finish此页面
			runOnUiThread(new Runnable() {
				public void run() {
					if (toChatUsername.equals(groupId)) {
						ToastUtil.toast(getApplicationContext(),R.string.the_current_group );
						/*if (GroupDetailsActivity.instance != null)
							GroupDetailsActivity.instance.finish();*/
						finish();
					}
				}
			});
		}

	}

	public String getToChatUsername() {
		return toChatUsername;
	}
/*
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UserInfo userInfo = (UserInfo)arg0.getAdapter().getItem(arg2);
		Intent intent = new Intent(this,ChatSingleActivity.class);
		intent.putExtra("userId", userInfo.getUsTel());
		intent.putExtra("userName", userInfo.getUsName());
		intent.putExtra("chatType", Constants.CHATTYPE_SINGLE);
		startActivity(intent);
		this.finish();
		String name = SharedPreferencesUtil.getUsName(getApplicationContext());
		if(name.equals(userInfo.getTel())){
			ToastUtil.toast(getApplicationContext(), R.string.chat_self);
		}else{
			//是单聊页面
			isSingle = true;
			unRegister();
			toChatUsername = userInfo.getTel();
//			chatType =  Constants.CHATTYPE_SINGLE;
			chatPersonalImageView.setVisibility(View.GONE);
			chatInfoImageView.setVisibility(View.GONE);
			chatBackTextView.setText(userInfo.getUsName());
	//		searchClassInfo();
			setUpView(classInfo.getList());
			if(alertDialog != null && alertDialog.isShowing()){
				alertDialog.dismiss();
			}
		}
	}
	*/	
}
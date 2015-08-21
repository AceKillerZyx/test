package com.education.zhxy.chat.activity;

import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.adapter.PersonalAdapter;
import com.education.zhxy.chat.data.bean.ClassInfo;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myclass.activity.MyClassActivity;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.OnClickUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.education.zhxy.Constants;

public class ChatListActivity extends BasicActivity {
	private static final String TAG = ChatListActivity.class.getSimpleName();
	private TextView chatListBackTextView, chatGroupNameTextView;
	private ListView chatListView;
	private HttpTask searchClassInfoHttpTask;
	private UserInfo userInfo;
	private ClassInfo classInfo;
	private String toChatUsername;
	private UnReadBroadcastReceiver receiver;
	private PersonalAdapter adapter;
	//群组
	private String groupName = "";
	private String groupid = "";
	// 自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chatListBackTextView:
			finish();
			break;
		case R.id.chatGroupNameTextView:
			if (OnClickUtil.isFastClick()) {
		        return ;
		    } 
			System.out.println("是否有群？" + isHasGroup(groupName) +"|"+ groupName);
			if (SharedPreferencesUtil.getClassId(getApplicationContext()) == 0) {
				Intent myClassIntent = new Intent(getApplicationContext(),MyClassActivity.class);
				myClassIntent.putExtra(Constants.TYPE, 1);
				myClassIntent.putExtra("groupName", groupName);
				pd.dismiss();
				startActivity(myClassIntent);
				Log.e(TAG, "groupName:" + groupName);
			} else {
				// 没群 && 班主任s
				if ((!isHasGroup(groupName)) && SharedPreferencesUtil.getUserRole(getApplicationContext()) == 4) {
					// 建群
					createGroup();
					// 没群 && 不是班主任
				} else if (!isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getApplicationContext()) != 4) {
					ToastUtil.toast(getApplicationContext(), "你没有可加入的群！");
					pd.dismiss();
					// 有群 && 不是班主任 直接加入
				} else if (isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getApplicationContext()) != 4) {
					addContact();
					// 有群 && 是班主任  不重复创建 直接进入
				} else if (isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getApplicationContext()) == 4) {
					Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
					//Intent intent=new Intent(getActivity(),ChatListActivity.class);
					chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
					chatIntent.putExtra("groupId", groupid);
					chatIntent.putExtra("groupName",groupName);
					pd.dismiss();
					startActivity(chatIntent);
				}
			}
			break;

		}
	}
	//是否有群组
	private boolean isHasGroup(String groupName) {
		List<EMGroupInfo> grouplist;
		try {
			grouplist = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
			if (grouplist != null && grouplist.size() > 0) {
				for (int i = 0; i < grouplist.size(); i++) {
					EMGroupInfo emGroup = grouplist.get(i);
					System.out.println("groupnamelist" + emGroup.getGroupName());
					System.out.println("groupName" + groupName);
					if (emGroup.getGroupName().equals(groupName)) {
						groupid = emGroup.getGroupId();
						return true;
					}
				}
			}
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//创建群
	private void createGroup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String desc = groupName + "群聊";
				String[] members = new String[] {};
				try {
					// 创建公开群，此种方式创建的群，可以自由加入
					// EMGroupManager.getInstance().createPublicGroup(groupName,
					// desc, members, false);
					// 创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
					Log.e(TAG, "groupName:" + groupName);
					if (isHasGroup(groupName)) {
						Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
						//Intent intent =new Intent(getActivity(),ChatListActivity.class);
						chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
						chatIntent.putExtra("groupId", groupid);
						chatIntent.putExtra("groupName", groupName);
						startActivity(chatIntent);
					} else {
						EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, false);
						runOnUiThread(new Runnable() {
							public void run() {
								ToastUtil.toast(getApplicationContext(), "创建群成功！");
								try {
									List<EMGroupInfo> grouplist = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
									if (grouplist != null
											&& grouplist.size() > 0) {
										for (int i = 0; i < grouplist.size(); i++) {
											EMGroupInfo emGroup = grouplist.get(i);
											System.out.println("groupnamelist"+ emGroup.getGroupName());
											if (emGroup.getGroupName().equals(groupName)) {
												groupid = emGroup.getGroupId();
											}
										}
										Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
										//Intent intent=new Intent(getActivity(),ChatListActivity.class);
										chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
										chatIntent.putExtra("groupId", groupid);
										chatIntent.putExtra("groupName",groupName);
										startActivity(chatIntent);
									}
								} catch (final EaseMobException e) {
									runOnUiThread(new Runnable() {
										public void run() {
											ToastUtil.toast(getApplicationContext(),"获取群ID失败"+ e.getLocalizedMessage());
										}
									});
								}
							}
						});
					}
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							ToastUtil.toast(
									getApplicationContext(),
									R.string.Failed_to_create_groups
											+ e.getLocalizedMessage());
						}
					});
				}

			}
		}).start();
		pd.dismiss();
	}
	
	//添加联系人
	private void addContact() {
		try {
			String name = SharedPreferencesUtil.getUsName(getApplicationContext());
			List<EMGroupInfo> groupsList = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
			if (groupsList != null && groupsList.size() > 0) {
				for (int i = 0; i < groupsList.size(); i++) {
					EMGroupInfo emGroupInfo = groupsList.get(i);
					String groupname = emGroupInfo.getGroupName();
					if (groupname.equals(groupName)) {
						groupid = emGroupInfo.getGroupId();
					}
				}
				Log.e(TAG, "groupid:" + groupid);
				System.out.println("ishasContact "+ isHasContact(groupid, name));
				if (isHasContact(groupid, name)) {
					//Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
					Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
					intent.putExtra("chatType", Constants.CHATTYPE_GROUP);
					intent.putExtra("groupId", groupid);
					intent.putExtra("groupName", groupName);
					startActivity(intent);
				} else {
					new Thread(new Runnable() {
						public void run() {
							try {
								final EMGroup group = EMGroupManager.getInstance().getGroupFromServer(groupid);
								// 如果是membersOnly的群，需要申请加入，不能直接join
								/*
								 * if (group.isMembersOnly()) { EMGroupManager
								 * .getInstance() .applyJoinToGroup( groupid,
								 * getResources() .getString(
								 * R.string.Request_to_join)); } else {
								 */
								EMGroupManager.getInstance().joinGroup(groupid);
								// }
								runOnUiThread(new Runnable() {
									public void run() {
										if (group.isMembersOnly())
											ToastUtil.toast(getApplicationContext(),R.string.send_the_request_is);
										else
											ToastUtil.toast(getApplicationContext(),R.string.Join_the_group_chat);
										Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
										chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
										chatIntent.putExtra("groupId", groupid);
										chatIntent.putExtra("groupName",groupName);
										pd.dismiss();
										startActivity(chatIntent);
									}
								});
							} catch (final EaseMobException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										ToastUtil.toast(getApplicationContext(),R.string.Failed_to_join_the_group_chat
																+ e.getMessage());
									}
								});
							}
						}
					}).start();
				}
			}
		} catch (EaseMobException e1) {
			e1.printStackTrace();
		}
		pd.dismiss();
	}
	
	private boolean isHasContact(String groupid, String name) {
		EMGroup emGroup;
		try {
			Log.e(TAG, "groupid:" + groupid);
			emGroup = EMGroupManager.getInstance().getGroupFromServer(groupid);
			List<String> memberList = emGroup.getMembers();
			if (memberList != null && memberList.size() > 0) {
				for (int i = 0; i < memberList.size(); i++) {
					if (name.equals(memberList.get(i))) {
						System.out.println(name + "||" + memberList.get(i));
						return true;
					}
				}
			}
		} catch (EaseMobException e) {
			e.printStackTrace();
		}

		return false;
	}

	
	
	@Override
	public void initUI() {
		// 创建ProgressDialog
		pd = CustomProgressDialog.createDialog(getApplicationContext());
		chatListBackTextView = (TextView) findViewById(R.id.chatListBackTextView);
		chatListBackTextView.setOnClickListener(this);
		chatGroupNameTextView = (TextView) findViewById(R.id.chatGroupNameTextView);
		chatGroupNameTextView.setOnClickListener(this);
		chatGroupNameTextView.setText(SharedPreferencesUtil.getClassShortName(getApplicationContext()));
		chatListView = (ListView) findViewById(R.id.chatListView);
		groupName = SharedPreferencesUtil.getClassName(getApplicationContext());
		// 注册接收消息广播
		receiver = new UnReadBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.AREA_UNREAD);
		intentFilter.setPriority(5);
		this.registerReceiver(receiver, intentFilter);

		searchClassInfo();
		chatListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Intent intent = new Intent(ChatListActivity.this,ChatActivity.class);
				if (((UserInfo) adapter.getItem(position)).getTel().equals(toChatUsername)) {
					
				}
				Bundle mBundle = new Bundle();    
		        mBundle.putSerializable(Constants.CHAT_USERINFO,(UserInfo) adapter.getItem(position));  
		        mBundle.putSerializable(Constants.USNAME,((UserInfo) adapter.getItem(position)).getUsName());  
		        intent.putExtras(mBundle);    
				startActivityForResult(intent, 0);
			}
			
		});

	}

	private void searchClassInfo() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.CLASSID, String.valueOf(SharedPreferencesUtil.getClassId(getApplicationContext())));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.CHAT_CLASS_INFO,false); // GET
		searchClassInfoHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		searchClassInfoHttpTask.execute(httpParam);
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
		Log.e("返回结果", result.getData());
		if (result != null && !StringUtil.isEmpty(result.getData())
				&& StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if (task == searchClassInfoHttpTask) {
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							JSONObject jsonObject = jsonArray.getJSONObject(0);
							classInfo = JSONObject.toJavaObject(jsonObject,ClassInfo.class);
//							SharedPreferencesUtil.putString(SharedPreferencesUtil, fileName, key, value)
							adapter = new PersonalAdapter(getApplicationContext(),classInfo.getList());
							chatListView.setAdapter(adapter);
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
			}
		}
	}

	@Override
	public int initLayout() {
		return R.layout.chat_list;
	}


	@Override
	public void initData() {

	}

	private class UnReadBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(Constants.AREA_UNREAD)) {
				// 记得把广播给终结掉
				abortBroadcast();
				// 获取的手机号
				if (intent.getStringExtra(Constants.MSG_FROM) != null) {
					toChatUsername = intent.getStringExtra(Constants.MSG_FROM);
					Log.e(TAG, "广播收到的 tochatUsername = " + toChatUsername);
				}
			}
			// 刷新bottom bar消息未读
			//updateUnreadLabel();
		}
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			userInfo.setMsgsl("false");
		} else {
			userInfo.setMsgsl("true");
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

}

/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.education.zhxy.easemob.helper;

import java.util.Map;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.education.zhxy.Constants;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.easemob.controller.HXSDKHelper;
import com.education.zhxy.easemob.domain.User;
import com.education.zhxy.easemob.model.HXSDKModel;
import com.education.zhxy.easemob.model.ZHXYHXSDKModel;
import com.education.zhxy.easemob.receiver.CallReceiver;
import com.education.zhxy.home.activity.ZHXYActivity;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * @author easemob
 *
 */
public class ZHXYHXSDKHelper extends HXSDKHelper{

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    private CallReceiver callReceiver;
    
 // private List<UserInfo> userInfoList;
    
    @Override
    protected void initHXOptions(){
        super.initHXOptions();
        // you can also get EMChatOptions to set related SDK options
        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener(){
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
      return new OnMessageNotifyListener() {

         @Override
          public String onNewMessageNotify(EMMessage message) {
           /*   // 设置状态栏的消息提示，可以根据message的类型做相应提示
             String ticker = CommonUtils.getMessageDigest(message, appContext);
            String nick="";
              if(message.getType() == Type.TXT)
                  ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
              if (userInfoList != null) {
            	  for (int i = 0; i < userInfoList.size(); i++) {
        				System.out.println("通知："+userInfoList.get(i).getUsTel());
        				if (userInfoList.get(i).getUsTel().equals(message.getFrom())) {
        					nick = userInfoList.get(i).getUsName();
        					Log.e("from nick", nick);
        				}
        			}
              }
             */ 
//              return nick + ": " + ticker;
              return null;
         }

          @Override
          public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
              return null;
             // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
          }

          @Override
          public String onSetNotificationTitle(EMMessage message) {
              //修改标题,这里使用默认
              return null;
          }

          @Override
          public int onSetSmallIcon(EMMessage message) {
              //设置小图标
              return 0;
          }
      };
    }
    
    /**
     * 自定义通知栏监听事件
     * @return
     */
    @Override
    protected OnNotificationClickListener getNotificationClickListener(){
        return new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
            	Log.e("**************", "message = " + message);
                Intent intent = new Intent(appContext, ChatActivity.class);
                ChatType chatType = message.getChatType();
                Log.e("**************", "chatType = " + chatType);
                if (chatType == ChatType.Chat) { //单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", Constants.CHATTYPE_SINGLE);
                    Log.e("=================", "Intent 跳转到单聊界面  userId = " + message.getFrom() );
                } else { // 群聊信息 
                	// message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    intent.putExtra("chatType", Constants.CHATTYPE_GROUP);
                    Log.e("=================", "Intent 跳转到群聊界面  groupId = " + message.getTo()  );
                }
                return intent;
            }
        };
    }
    
    
    
    @Override
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, ZHXYActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
    }
    
    @Override
    protected void onCurrentAccountRemoved(){
    	Intent intent = new Intent(appContext, ZHXYActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
    
    
    @Override
    protected void initListener(){
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if(callReceiver == null)
            callReceiver = new CallReceiver();
        appContext.registerReceiver(callReceiver, callFilter);    
    }

    @Override
    protected HXSDKModel createModel() {
        return new ZHXYHXSDKModel(appContext);
    }
    
    /**
     * get demo HX SDK Model
     */
    public ZHXYHXSDKModel getModel(){
        return (ZHXYHXSDKModel) hxModel;
    }
    
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((ZHXYHXSDKModel) getModel()).getContactList();
        }
        
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }
    
    @Override
    public void logout(final EMCallBack callback){
        endCall();
        super.logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                setContactList(null);
                getModel().closeDB();
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                
            }

            @Override
            public void onProgress(int progress, String status) {
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
        });
    }
    
    void endCall(){
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

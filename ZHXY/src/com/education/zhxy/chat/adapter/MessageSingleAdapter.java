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
package com.education.zhxy.chat.adapter;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import com.easemob.util.LatLng;
import com.easemob.util.TextFormater;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.chat.activity.ChatSingleActivity;
import com.education.zhxy.chat.activity.LongContextMenu;
import com.education.zhxy.easemob.activity.BaiduMapActivity;
import com.education.zhxy.easemob.activity.ShowBigImage;
import com.education.zhxy.easemob.activity.ShowNormalFileActivity;
import com.education.zhxy.easemob.activity.ShowVideoActivity;
import com.education.zhxy.easemob.listener.VoicePlayClickListener;
import com.education.zhxy.easemob.task.LoadImageTask;
import com.education.zhxy.easemob.task.LoadVideoImageTask;
import com.education.zhxy.easemob.utils.ImageCache;
import com.education.zhxy.easemob.utils.ImageUtils;
import com.education.zhxy.easemob.utils.SmileUtils;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

@SuppressLint({ "InflateParams", "HandlerLeak", "CutPasteId" })
public class MessageSingleAdapter extends BaseAdapter {

	private final static String TAG = "msg";

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;
	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
	private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
	private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String username;
	private LayoutInflater inflater;
	private Activity activity;

	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;

	// reference to conversation object in chatsdk
	private EMConversation conversation;
	EMMessage[] messages = null;

	private Context context;

	private Map<String, Timer> timers = new Hashtable<String, Timer>();

	private List<UserInfo> userInfoList;

	private boolean sending = false;

	public MessageSingleAdapter(Context context, String username, int chatType,
			List<UserInfo> userInfoList) {
		this.username = username;
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.userInfoList = userInfoList;
		this.conversation = EMChatManager.getInstance().getConversation(
				username);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message message) {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(
					new EMMessage[conversation.getAllMessages().size()]);
			notifyDataSetChanged();
		}
	};

	/**
	 * 获取item数
	 */
	public int getCount() {
		return messages == null ? 0 : messages.length;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler
				.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}

	public EMMessage getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
		return 16;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = getItem(position);
		if (message == null) {
			return -1;
		}
		if (message.getType() == EMMessage.Type.TXT) {
			if (message.getBooleanAttribute(
					Constants.MESSAGE_ATTR_IS_VOICE_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL
						: MESSAGE_TYPE_SENT_VOICE_CALL;
			else if (message.getBooleanAttribute(
					Constants.MESSAGE_ATTR_IS_VIDEO_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL
						: MESSAGE_TYPE_SENT_VIDEO_CALL;
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT
					: MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE
					: MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION
					: MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE
					: MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO
					: MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE
					: MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {
		case LOCATION:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_location, null) : inflater
					.inflate(R.layout.row_sent_location, null);
		case IMAGE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_picture, null) : inflater
					.inflate(R.layout.row_sent_picture, null);

		case VOICE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_voice, null) : inflater
					.inflate(R.layout.row_sent_voice, null);
		case VIDEO:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_video, null) : inflater
					.inflate(R.layout.row_sent_video, null);
		case FILE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_file, null) : inflater
					.inflate(R.layout.row_sent_file, null);
		default:
			// 语音通话
			if (message.getBooleanAttribute(
					Constants.MESSAGE_ATTR_IS_VOICE_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.row_received_voice_call, null)
						: inflater.inflate(R.layout.row_sent_voice_call, null);
			// 视频通话
			else if (message.getBooleanAttribute(
					Constants.MESSAGE_ATTR_IS_VIDEO_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.row_received_video_call, null)
						: inflater.inflate(R.layout.row_sent_video_call, null);
			return message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.row_received_message, null) : inflater
					.inflate(R.layout.row_sent_message, null);
		}
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		final Viewholder mholder;
		if (convertView == null) {
			mholder = new Viewholder();
			convertView = createViewByMessage(message, position);
			Log.e(TAG, "messageType:" + message.getType());
			if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					mholder.iv = ((ImageView) convertView
							.findViewById(R.id.iv_sendPicture));
					mholder.iv_avatar = (ImageView) convertView
							.findViewById(R.id.iv_userhead);
					mholder.tv = (TextView) convertView
							.findViewById(R.id.percentage);
					mholder.pb = (ProgressBar) convertView
							.findViewById(R.id.progressBar);
					mholder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					mholder.tv_usernick = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

			} else if (message.getType() == EMMessage.Type.TXT) {
				try {
					mholder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					mholder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					mholder.iv_avatar = (ImageView) convertView
							.findViewById(R.id.iv_userhead);
					// 这里是文字内容
					mholder.tv = (TextView) convertView
							.findViewById(R.id.tv_chatcontent);
					mholder.tv_usernick = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

				// 语音通话及视频通话
				if (message.getBooleanAttribute(
						Constants.MESSAGE_ATTR_IS_VOICE_CALL, false)
						|| message.getBooleanAttribute(
								Constants.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
					mholder.iv = (ImageView) convertView
							.findViewById(R.id.iv_call_icon);
					mholder.tv = (TextView) convertView
							.findViewById(R.id.tv_chatcontent);
				}

			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					mholder.iv = ((ImageView) convertView
							.findViewById(R.id.iv_voice));
					mholder.iv_avatar = (ImageView) convertView
							.findViewById(R.id.iv_userhead);
					mholder.tv = (TextView) convertView
							.findViewById(R.id.tv_length);
					mholder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					mholder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					mholder.tv_usernick = (TextView) convertView
							.findViewById(R.id.tv_userid);
					mholder.iv_read_status = (ImageView) convertView
							.findViewById(R.id.iv_unread_voice);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.LOCATION) {
				try {
					mholder.iv_avatar = (ImageView) convertView
							.findViewById(R.id.iv_userhead);
					mholder.tv = (TextView) convertView
							.findViewById(R.id.tv_location);
					mholder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					mholder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					mholder.tv_usernick = (TextView) convertView
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.VIDEO) {
				try {
					mholder.iv = ((ImageView) convertView
							.findViewById(R.id.chatting_content_iv));
					mholder.iv_avatar = (ImageView) convertView
							.findViewById(R.id.iv_userhead);
					mholder.tv = (TextView) convertView
							.findViewById(R.id.percentage);
					mholder.pb = (ProgressBar) convertView
							.findViewById(R.id.progressBar);
					mholder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					mholder.size = (TextView) convertView
							.findViewById(R.id.chatting_size_iv);
					mholder.timeLength = (TextView) convertView
							.findViewById(R.id.chatting_length_iv);
					mholder.playBtn = (ImageView) convertView
							.findViewById(R.id.chatting_status_btn);
					mholder.container_status_btn = (LinearLayout) convertView
							.findViewById(R.id.container_status_btn);
					mholder.tv_usernick = (TextView) convertView
							.findViewById(R.id.tv_userid);

				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.FILE) {
				try {
					mholder.iv_avatar = (ImageView) convertView
							.findViewById(R.id.iv_userhead);
					mholder.tv_file_name = (TextView) convertView
							.findViewById(R.id.tv_file_name);
					mholder.tv_file_size = (TextView) convertView
							.findViewById(R.id.tv_file_size);
					mholder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					mholder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
					mholder.tv_file_download_state = (TextView) convertView
							.findViewById(R.id.tv_file_state);
					mholder.ll_container = (LinearLayout) convertView
							.findViewById(R.id.ll_file_container);
					// 这里是进度值
					mholder.tv = (TextView) convertView.findViewById(R.id.percentage);
				} catch (Exception e) {
				}
				try {
					mholder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

			}

			convertView.setTag(mholder);
		} else {
			mholder = (Viewholder) convertView.getTag();
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		// 单聊。群聊时，显示接收的消息的发送人的名称
		if ( message.direct == EMMessage.Direct.RECEIVE) {
			// demo里使用username代码nick
			String nick = "";
			String userhead = "";
			if (userInfoList != null) {
				for (int i = 0; i < userInfoList.size(); i++) {
					if (userInfoList.get(i).getTel()
							.equals(message.getFrom())) {
						nick = userInfoList.get(i).getUsName();
						userhead = userInfoList.get(i).getImage();
						Log.e("from nick", nick);
					}
				}
			}
			mholder.tv_usernick.setText(nick);
			if (!StringUtil.isEmpty(userhead)) {
				ImageLoader.getInstance().displayImage(
						ReleaseConfigure.ROOT_IMAGE + userhead,
						mholder.iv_avatar, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "Input/Output error";
									break;
								case DECODING_ERROR:
									message = "Image can't be decoded";
									break;
								case NETWORK_DENIED:
									message = "Downloads are denied";
									break;
								case OUT_OF_MEMORY:
									message = "Out Of Memory error";
									break;
								case UNKNOWN:
									message = "Unknown error";
									break;
								}
								Log.e(TAG, message);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
							}
						});
			}
		}
		// 单聊。群聊显示自己的名字
		if ( message.direct == EMMessage.Direct.SEND) {
			String nick = "";
			String userhead = "";
			sending = true;
			if (username.equals(message.getTo())) {
				if (userInfoList != null) {
					for (int i = 0; i < userInfoList.size(); i++) {
						if (userInfoList.get(i).getTel().equals(SharedPreferencesUtil.getUsName(context))) {
							nick = userInfoList.get(i).getUsName();
							userhead = userInfoList.get(i).getImage();
						}
					}
				}
				Log.e("to nick", nick + "");
			}
			if (!StringUtil.isEmpty(nick)) {
				mholder.tv_usernick.setText(nick);
			}
			if (!StringUtil.isEmpty(userhead)) {
				ImageLoader.getInstance().displayImage(
						ReleaseConfigure.ROOT_IMAGE + userhead,
						mholder.iv_avatar, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "Input/Output error";
									break;
								case DECODING_ERROR:
									message = "Image can't be decoded";
									break;
								case NETWORK_DENIED:
									message = "Downloads are denied";
									break;
								case OUT_OF_MEMORY:
									message = "Out Of Memory error";
									break;
								case UNKNOWN:
									message = "Unknown error";
									break;
								}
								Log.e(TAG, message);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
							}
						});
			}
		}
		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (message.direct == EMMessage.Direct.SEND
				&& chatType != ChatType.GroupChat) {
			mholder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			mholder.tv_delivered = (TextView) convertView
					.findViewById(R.id.tv_delivered);
			if (mholder.tv_ack != null) {
				if (message.isAcked) {
					if (mholder.tv_delivered != null) {
						mholder.tv_delivered.setVisibility(View.INVISIBLE);
					}
					mholder.tv_ack.setVisibility(View.VISIBLE);
				} else {
					mholder.tv_ack.setVisibility(View.INVISIBLE);

					// check and display msg delivered ack status
					if (mholder.tv_delivered != null) {
						if (message.isDelivered) {
							mholder.tv_delivered.setVisibility(View.VISIBLE);
						} else {
							mholder.tv_delivered.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		} else {
			// 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION)
					&& !message.isAcked && chatType != ChatType.GroupChat) {
				// 不是语音通话记录
				if (!message.getBooleanAttribute(
						Constants.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					try {
						EMChatManager.getInstance().ackMessageRead(
								message.getFrom(), message.getMsgId());
						// 发送已读回执
						message.isAcked = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}


		switch (message.getType()) {
		// 根据消息type显示item
		case IMAGE: // 图片
			handleImageMessage(message, mholder, position, convertView);
			break;
		case TXT: // 文本
			if (message.getBooleanAttribute(
					Constants.MESSAGE_ATTR_IS_VOICE_CALL, false)
					|| message.getBooleanAttribute(
							Constants.MESSAGE_ATTR_IS_VIDEO_CALL, false))
				// 音视频通话
				handleCallMessage(message, mholder, position);
			else
				handleTextMessage(message, mholder, position);
			break;
		case LOCATION: // 位置
			handleLocationMessage(message, mholder, position, convertView);
			break;
		case VOICE: // 语音
			handleVoiceMessage(message, mholder, position, convertView);
			break;
		case VIDEO: // 视频
			handleVideoMessage(message, mholder, position, convertView);
			break;
		case FILE: // 一般文件
			handleFileMessage(message, mholder, position, convertView);
			break;
		default:
			// not supported
		}

		/*
		 * if (message.direct == EMMessage.Direct.SEND) { View statusView =
		 * convertView.findViewById(R.id.msg_status); // 重发按钮点击事件
		 * statusView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * // 显示重发消息的自定义alertdialog Intent intent = new Intent(activity,
		 * AlertDialog.class); intent.putExtra("msg",
		 * activity.getString(R.string.confirm_resend));
		 * intent.putExtra("title", activity.getString(R.string.resend));
		 * intent.putExtra("cancel", true); intent.putExtra("position",
		 * position); if (message.getType() == EMMessage.Type.TXT)
		 * activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_TEXT); else if (message.getType() ==
		 * EMMessage.Type.VOICE) activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_VOICE); else if (message.getType() ==
		 * EMMessage.Type.IMAGE) activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_PICTURE); else if (message.getType() ==
		 * EMMessage.Type.LOCATION) activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_LOCATION); else if (message.getType() ==
		 * EMMessage.Type.FILE) activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_FILE); else if (message.getType() ==
		 * EMMessage.Type.VIDEO) activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_VIDEO);
		 * 
		 * } });
		 * 
		 * } else { final String st =
		 * context.getResources().getString(R.string.Into_the_blacklist); //
		 * 长按头像，移入黑名单 mholder.iv_avatar.setOnLongClickListener(new
		 * OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) { Intent intent = new
		 * Intent(activity, AlertDialog.class); intent.putExtra("msg", st);
		 * intent.putExtra("cancel", true); intent.putExtra("position",
		 * position); activity.startActivityForResult(intent,
		 * ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST); return true; } }); }
		 */

		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message
					.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			EMMessage prevMessage = getItem(position - 1);
			if (prevMessage != null
					&& DateUtils.isCloseEnough(message.getMsgTime(),
							prevMessage.getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message
						.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}


	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, Viewholder mholder,
			final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		Spannable span = SmileUtils
				.getSmiledText(context, txtBody.getMessage());
		// 设置内容
		mholder.tv.setText(span, BufferType.SPANNABLE);
		// 设置长按事件监听
		mholder.tv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						LongContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.TXT.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		ChatType chatType = message.getChatType();
		if (message.direct == EMMessage.Direct.SEND && sending || chatType != ChatType.GroupChat ) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				mholder.pb.setVisibility(View.GONE);
				mholder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				mholder.pb.setVisibility(View.GONE);
				mholder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				mholder.pb.setVisibility(View.VISIBLE);
				mholder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, mholder);
			}
		}
		
	}

	/**
	 * 音视频通话记录
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 */
	private void handleCallMessage(EMMessage message, Viewholder mholder,
			final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		mholder.tv.setText(txtBody.getMessage());

	}

	/**
	 * 图片消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message,
			final Viewholder mholder, final int position, View convertView) {
		mholder.pb.setTag(position);
		mholder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						LongContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.IMAGE.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				mholder.iv.setImageResource(R.drawable.chat_header_image);
				showDownloadImageProgress(message, mholder);
				// downloadImage(message, mholder);
			} else {
				// "!!!! not back receive, show image directly");
				mholder.pb.setVisibility(View.GONE);
				mholder.tv.setVisibility(View.GONE);
				mholder.iv.setImageResource(R.drawable.chat_header_image);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					String thumbnailPath = ImageUtils
							.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, mholder.iv, filePath,
							imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					mholder.iv, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					mholder.iv, filePath, IMAGE_DIR, message);
		}

		switch (message.status) {
		case SUCCESS:
			mholder.pb.setVisibility(View.GONE);
			mholder.tv.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			mholder.pb.setVisibility(View.GONE);
			mholder.tv.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			mholder.staus_iv.setVisibility(View.GONE);
			mholder.pb.setVisibility(View.VISIBLE);
			mholder.tv.setVisibility(View.VISIBLE);
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							mholder.pb.setVisibility(View.VISIBLE);
							mholder.tv.setVisibility(View.VISIBLE);
							mholder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								mholder.pb.setVisibility(View.GONE);
								mholder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								mholder.pb.setVisibility(View.GONE);
								mholder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								mholder.staus_iv.setVisibility(View.VISIBLE);
								ToastUtil.toast(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast));
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, mholder);
		}
	}

	/**
	 * 视频消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 * @param convertView
	 */
	private void handleVideoMessage(final EMMessage message,
			final Viewholder mholder, final int position, View convertView) {

		VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
		// final File image=new File(PathUtil.getInstance().getVideoPath(),
		// videoBody.getFileName());
		String localThumb = videoBody.getLocalThumb();

		mholder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(new Intent(activity,
						LongContextMenu.class).putExtra("position", position)
						.putExtra("type", EMMessage.Type.VIDEO.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (localThumb != null) {

			showVideoThumbView(localThumb, mholder.iv,
					videoBody.getThumbnailUrl(), message);
		}
		if (videoBody.getLength() > 0) {
			String time = DateUtils.toTimeBySecond(videoBody.getLength());
			mholder.timeLength.setText(time);
		}
		mholder.playBtn.setImageResource(R.drawable.video_download_btn_nor);

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (videoBody.getVideoFileLength() > 0) {
				String size = TextFormater.getDataSize(videoBody
						.getVideoFileLength());
				mholder.size.setText(size);
			}
		} else {
			if (videoBody.getLocalUrl() != null
					&& new File(videoBody.getLocalUrl()).exists()) {
				String size = TextFormater.getDataSize(new File(videoBody
						.getLocalUrl()).length());
				mholder.size.setText(size);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {

			// System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				// System.err.println("!!!! back receive");
				mholder.iv.setImageResource(R.drawable.chat_header_image);
				showDownloadImageProgress(message, mholder);

			} else {
				// System.err.println("!!!! not back receive, show image directly");
				mholder.iv.setImageResource(R.drawable.chat_header_image);
				if (localThumb != null) {
					showVideoThumbView(localThumb, mholder.iv,
							videoBody.getThumbnailUrl(), message);
				}

			}

			return;
		}
		mholder.pb.setTag(position);

		// until here ,deal with send video msg
		switch (message.status) {
		case SUCCESS:
			mholder.pb.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.GONE);
			mholder.tv.setVisibility(View.GONE);
			break;
		case FAIL:
			mholder.pb.setVisibility(View.GONE);
			mholder.tv.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mholder.pb.setVisibility(View.VISIBLE);
							mholder.tv.setVisibility(View.VISIBLE);
							mholder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								mholder.pb.setVisibility(View.GONE);
								mholder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								mholder.pb.setVisibility(View.GONE);
								mholder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								mholder.staus_iv.setVisibility(View.VISIBLE);
								ToastUtil.toast(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast));
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// sendMsgInBackground(message, mholder);
			sendPictureMessage(message, mholder);

		}

	}

	/**
	 * 语音消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message,
			final Viewholder mholder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		mholder.tv.setText(voiceBody.getLength() + "\"");
		mholder.iv.setOnClickListener(new VoicePlayClickListener(message,
				mholder.iv, mholder.iv_read_status, this, activity, username));
		mholder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						LongContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.VOICE.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		// 判断单聊还是群聊
		EMMessage msg = getItem(position);
		ChatType chatType = msg.getChatType();
		if (chatType == ChatType.GroupChat) {
			// 群聊
			if (((ChatActivity) activity).playMsgId != null
					&& ((ChatActivity) activity).playMsgId.equals(message
							.getMsgId()) && VoicePlayClickListener.isPlaying) {
				AnimationDrawable voiceAnimation;
				if (message.direct == EMMessage.Direct.RECEIVE) {
					mholder.iv.setImageResource(R.anim.voice_from_icon);
				} else {
					mholder.iv.setImageResource(R.anim.voice_to_icon);
				}
				voiceAnimation = (AnimationDrawable) mholder.iv.getDrawable();
				voiceAnimation.start();
			} else {
				if (message.direct == EMMessage.Direct.RECEIVE) {
					mholder.iv
							.setImageResource(R.drawable.chatfrom_voice_playing);
				} else {
					mholder.iv.setImageResource(R.drawable.chatto_voice_playing);
				}
			}
		} else {
			// 单聊
			if (((ChatSingleActivity) activity).playMsgId != null
					&& ((ChatSingleActivity) activity).playMsgId.equals(message
							.getMsgId()) && VoicePlayClickListener.isPlaying) {
				AnimationDrawable voiceAnimation;
				if (message.direct == EMMessage.Direct.RECEIVE) {
					mholder.iv.setImageResource(R.anim.voice_from_icon);
				} else {
					mholder.iv.setImageResource(R.anim.voice_to_icon);
				}
				voiceAnimation = (AnimationDrawable) mholder.iv.getDrawable();
				voiceAnimation.start();
			} else {
				if (message.direct == EMMessage.Direct.RECEIVE) {
					mholder.iv
							.setImageResource(R.drawable.chatfrom_voice_playing);
				} else {
					mholder.iv.setImageResource(R.drawable.chatto_voice_playing);
				}
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isListened()) {
				// 隐藏语音未听标志
				mholder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				mholder.iv_read_status.setVisibility(View.VISIBLE);
			}
			System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				mholder.pb.setVisibility(View.VISIBLE);
				System.err.println("!!!! back receive");
				((FileMessageBody) message.getBody())
						.setDownloadCallback(new EMCallBack() {

							@Override
							public void onSuccess() {
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										mholder.pb.setVisibility(View.INVISIBLE);
										notifyDataSetChanged();
									}
								});

							}

							@Override
							public void onProgress(int progress, String status) {
							}

							@Override
							public void onError(int code, String message) {
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										mholder.pb.setVisibility(View.INVISIBLE);
									}
								});

							}
						});

			} else {
				mholder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			mholder.pb.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			mholder.pb.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			mholder.pb.setVisibility(View.VISIBLE);
			mholder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, mholder);
		}
	}

	/**
	 * 文件消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 * @param convertView
	 */
	private void handleFileMessage(final EMMessage message,
			final Viewholder mholder, int position, View convertView) {
		final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message
				.getBody();
		final String filePath = fileMessageBody.getLocalUrl();
		mholder.tv_file_name.setText(fileMessageBody.getFileName());
		mholder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody
				.getFileSize()));
		mholder.ll_container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				File file = new File(filePath);
				if (file != null && file.exists()) {
					// 文件存在，直接打开
					FileUtils.openFile(file, (Activity) context);
				} else {
					// 下载
					context.startActivity(new Intent(context,
							ShowNormalFileActivity.class).putExtra("msgbody",
							fileMessageBody));
				}
				if (message.direct == EMMessage.Direct.RECEIVE
						&& !message.isAcked) {
					try {
						EMChatManager.getInstance().ackMessageRead(
								message.getFrom(), message.getMsgId());
						message.isAcked = true;
					} catch (EaseMobException e) {
						e.printStackTrace();
					}
				}
			}
		});
		String st1 = context.getResources().getString(R.string.Have_downloaded);
		String st2 = context.getResources()
				.getString(R.string.Did_not_download);
		if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
			System.err.println("it is receive msg");
			File file = new File(filePath);
			if (file != null && file.exists()) {
				mholder.tv_file_download_state.setText(st1);
			} else {
				mholder.tv_file_download_state.setText(st2);
			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			mholder.pb.setVisibility(View.INVISIBLE);
			mholder.tv.setVisibility(View.INVISIBLE);
			mholder.staus_iv.setVisibility(View.INVISIBLE);
			break;
		case FAIL:
			mholder.pb.setVisibility(View.INVISIBLE);
			mholder.tv.setVisibility(View.INVISIBLE);
			mholder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mholder.pb.setVisibility(View.VISIBLE);
							mholder.tv.setVisibility(View.VISIBLE);
							mholder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								mholder.pb.setVisibility(View.INVISIBLE);
								mholder.tv.setVisibility(View.INVISIBLE);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								mholder.pb.setVisibility(View.INVISIBLE);
								mholder.tv.setVisibility(View.INVISIBLE);
								mholder.staus_iv.setVisibility(View.VISIBLE);
								ToastUtil.toast(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast));
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// 发送消息
			sendMsgInBackground(message, mholder);
		}

	}

	/**
	 * 处理位置消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 * @param convertView
	 */
	private void handleLocationMessage(final EMMessage message,
			final Viewholder mholder, final int position, View convertView) {
		TextView locationView = ((TextView) convertView
				.findViewById(R.id.tv_location));
		LocationMessageBody locBody = (LocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());
		LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
		locationView.setOnClickListener(new MapClickListener(loc, locBody
				.getAddress()));
		locationView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity,
						LongContextMenu.class)).putExtra("position", position)
						.putExtra("type", EMMessage.Type.LOCATION.ordinal()),
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return false;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			return;
		}
		// deal with send message
		switch (message.status) {
		case SUCCESS:
			mholder.pb.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			mholder.pb.setVisibility(View.GONE);
			mholder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			mholder.pb.setVisibility(View.VISIBLE);
			break;
		default:
			sendMsgInBackground(message, mholder);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param mholder
	 * @param position
	 */
	public void sendMsgInBackground(final EMMessage message,
			final Viewholder mholder) {
		mholder.staus_iv.setVisibility(View.GONE);
		mholder.pb.setVisibility(View.VISIBLE);

		final long start = System.currentTimeMillis();
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				// umeng自定义事件，
				sendEvent2Umeng(message, start);

				updateSendedView(message, mholder);
			}

			@Override
			public void onError(int code, String error) {
				sendEvent2Umeng(message, start);

				updateSendedView(message, mholder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message,
			final Viewholder mholder) {
		System.err.println("!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();
		if (mholder.pb != null)
			mholder.pb.setVisibility(View.VISIBLE);
		if (mholder.tv != null)
			mholder.tv.setVisibility(View.VISIBLE);

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							mholder.pb.setVisibility(View.GONE);
							mholder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mholder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	/*
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message,
			final Viewholder mholder) {
		try {
			@SuppressWarnings("unused")
			String to = message.getTo();
			// before send, update ui
			mholder.staus_iv.setVisibility(View.GONE);
			mholder.pb.setVisibility(View.VISIBLE);
			mholder.tv.setVisibility(View.VISIBLE);
			mholder.tv.setText("0%");

			final long start = System.currentTimeMillis();
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "send image message successfully");
					sendEvent2Umeng(message, start);
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// send success
							mholder.pb.setVisibility(View.GONE);
							mholder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					sendEvent2Umeng(message, start);

					activity.runOnUiThread(new Runnable() {
						public void run() {
							mholder.pb.setVisibility(View.GONE);
							mholder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							mholder.staus_iv.setVisibility(View.VISIBLE);
							ToastUtil.toast(
									activity,
									activity.getString(R.string.send_fail)
											+ activity
													.getString(R.string.connect_failuer_toast));
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							mholder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param mholder
	 */
	private void updateSendedView(final EMMessage message,
			final Viewholder mholder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					mholder.tv.setVisibility(View.GONE);
				}
				System.out.println("message status : " + message.status);
				if (message.status == EMMessage.Status.SUCCESS) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// mholder.pb.setVisibility(View.INVISIBLE);
					// mholder.staus_iv.setVisibility(View.INVISIBLE);
					// } else {
					// mholder.pb.setVisibility(View.GONE);
					// mholder.staus_iv.setVisibility(View.GONE);
					// }

				} else if (message.status == EMMessage.Status.FAIL) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// mholder.pb.setVisibility(View.INVISIBLE);
					// } else {
					// mholder.pb.setVisibility(View.GONE);
					// }
					// mholder.staus_iv.setVisibility(View.VISIBLE);
					ToastUtil.toast(
							activity,
							activity.getString(R.string.send_fail)
									+ activity
											.getString(R.string.connect_failuer_toast));
				}

				notifyDataSetChanged();
			}
		});
	}

	/**
	 * load image into image view
	 * 
	 * @param thumbernailPath
	 * @param iv
	 * @param position
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath,
			final ImageView iv, final String localFullSizePath,
			String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					System.err.println("image view on click");
					Intent intent = new Intent(activity, ShowBigImage.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
						System.err
								.println("here need to check why download everytime");
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImageMessageBody body = (ImageMessageBody) message
								.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}
					if (message != null
							&& message.direct == EMMessage.Direct.RECEIVE
							&& !message.isAcked
							&& message.getChatType() != ChatType.GroupChat) {
						try {
							EMChatManager.getInstance().ackMessageRead(
									message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);
				}
			});
			return true;
		} else {
			new LoadImageTask().execute(thumbernailPath, localFullSizePath,
					remote, message.getChatType(), iv, activity, message);
			return true;
		}

	}

	/**
	 * 展示视频缩略图
	 * 
	 * @param localThumb
	 *            本地缩略图路径
	 * @param iv
	 * @param thumbnailUrl
	 *            远程缩略图路径
	 * @param message
	 */
	private void showVideoThumbView(String localThumb, ImageView iv,
			String thumbnailUrl, final EMMessage message) {
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(localThumb);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					VideoMessageBody videoBody = (VideoMessageBody) message
							.getBody();
					System.err.println("video view is on click");
					Intent intent = new Intent(activity,
							ShowVideoActivity.class);
					intent.putExtra("localpath", videoBody.getLocalUrl());
					intent.putExtra("secret", videoBody.getSecret());
					intent.putExtra("remotepath", videoBody.getRemoteUrl());
					if (message != null
							&& message.direct == EMMessage.Direct.RECEIVE
							&& !message.isAcked
							&& message.getChatType() != ChatType.GroupChat) {
						message.isAcked = true;
						try {
							EMChatManager.getInstance().ackMessageRead(
									message.getFrom(), message.getMsgId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);

				}
			});

		} else {
			new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv,
					activity, message, this);
		}

	}

	public static class Viewholder {
		ImageView iv;
		TextView tv;
		TextView tv_username;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView iv_avatar;
		TextView tv_usernick;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
	}

	/*
	 * 点击地图消息listener
	 */
	class MapClickListener implements View.OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;

		}

		@Override
		public void onClick(View v) {
			Intent intent;
			intent = new Intent(context, BaiduMapActivity.class);
			intent.putExtra("latitude", location.latitude);
			intent.putExtra("longitude", location.longitude);
			intent.putExtra("address", address);
			activity.startActivity(intent);
		}

	}

	/**
	 * umeng自定义事件统计
	 * 
	 * @param message
	 */
	private void sendEvent2Umeng(final EMMessage message, final long start) {
		activity.runOnUiThread(new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				long costTime = System.currentTimeMillis() - start;
				Map<String, String> params = new HashMap<String, String>();
				if (message.status == EMMessage.Status.SUCCESS)
					params.put("status", "success");
				else
					params.put("status", "failure");

				switch (message.getType()) {
				case TXT:
				case LOCATION:
					/*
					 * MobclickAgent.onEventValue(activity, "text_msg", params,
					 * (int) costTime); MobclickAgent.onEventDuration(activity,
					 * "text_msg", (int) costTime);
					 */
					break;
				case IMAGE:
					/*
					 * MobclickAgent.onEventValue(activity, "img_msg", params,
					 * (int) costTime); MobclickAgent.onEventDuration(activity,
					 * "img_msg", (int) costTime);
					 */
					break;
				case VOICE:
					/*
					 * MobclickAgent.onEventValue(activity, "voice_msg", params,
					 * (int) costTime); MobclickAgent.onEventDuration(activity,
					 * "voice_msg", (int) costTime);
					 */
					break;
				case VIDEO:
					/*
					 * MobclickAgent.onEventValue(activity, "video_msg", params,
					 * (int) costTime); MobclickAgent.onEventDuration(activity,
					 * "video_msg", (int) costTime);
					 */
					break;
				default:
					break;
				}

			}
		});
	}

}
package com.education.zhxy.chat.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.data.bean.ChatMsgEntity;
import com.education.zhxy.util.ArrayUtil;
import com.education.zhxy.util.FaceConversionUtil;
import com.education.zhxy.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ChatMsgAdapter extends BaseAdapter {

	private static final String TAG = ChatMsgAdapter.class.getSimpleName();

	private List<ChatMsgEntity> coll;
	
	private LayoutInflater mInflater;
	
	private Context context;
	
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
	public ChatMsgAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public void resetData(List<ChatMsgEntity> coll){
		this.coll = coll;
		this.notifyDataSetChanged();
	}

	public int getCount() {
		return ArrayUtil.isEmptyList(coll) ? 0 : coll.size();
	}

	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(coll) ? null : coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		ChatMsgEntity entity = coll.get(position);
		if (entity.getMsgType()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		return 2;
	}

	@SuppressLint("InflateParams") 
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(R.layout.chat_msg_item_left_layout, null);
			} else {
				convertView = mInflater.inflate(R.layout.chat_msg_item_right_layout, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.chatItemMesUserTextView = (TextView) convertView.findViewById(R.id.chatItemMesUserTextView);
			viewHolder.chatItemMesTimeTextView = (TextView) convertView.findViewById(R.id.chatItemMesTimeTextView);
			viewHolder.chatItemMesContentTextView = (TextView) convertView.findViewById(R.id.chatItemMesContentTextView);
			viewHolder.chatItemMesVoiceTimeTextView = (TextView) convertView.findViewById(R.id.chatItemMesVoiceTimeTextView);
			viewHolder.chatItemMesUserHeaderImageView = (ImageView)convertView.findViewById(R.id.chatItemMesUserHeaderImageView);
			viewHolder.chatItemMesImageView = (ImageView)convertView.findViewById(R.id.chatItemMesImageView);
			viewHolder.chatRelativeLayout = (RelativeLayout)convertView.findViewById(R.id.chatRelativeLayout);
			viewHolder.isComMsg = isComMsg;
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (!StringUtil.isEmpty(entity.getGcDate())) {
			viewHolder.chatItemMesTimeTextView.setText(entity.getGcDate().replace("T", " "));
		}
		if(!StringUtil.isEmpty(entity.getGcContent())){
			if(entity.getGcContent().contains(".jpg")){
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showImageForEmptyUri(R.drawable.heart_item_bg)
						.showImageOnFail(R.drawable.heart_item_bg)
						.resetViewBeforeLoading(true).cacheOnDisk(true)
						.imageScaleType(ImageScaleType.EXACTLY)
						.bitmapConfig(Bitmap.Config.RGB_565)
						.considerExifParams(true)
						.displayer(new FadeInBitmapDisplayer(300)).build();
				viewHolder.chatItemMesVoiceTimeTextView.setVisibility(View.INVISIBLE);
				viewHolder.chatItemMesImageView.setVisibility(View.VISIBLE);
				viewHolder.chatItemMesContentTextView.setVisibility(View.GONE);
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.chatItemMesImageView.getLayoutParams(); 
				linearParams.width = 100;
				linearParams.height = 150;
				viewHolder.chatItemMesImageView.setLayoutParams(linearParams);
				ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + entity.getGcContent(), viewHolder.chatItemMesImageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}
					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
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
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			}else if(entity.getGcContent().contains(".amr")){
				viewHolder.chatItemMesVoiceTimeTextView.setVisibility(View.VISIBLE);
				viewHolder.chatItemMesImageView.setVisibility(View.VISIBLE);
				viewHolder.chatItemMesContentTextView.setVisibility(View.GONE);
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.chatItemMesImageView.getLayoutParams(); 
				linearParams.width = 70;
				linearParams.height = 50;
				viewHolder.chatItemMesImageView.setLayoutParams(linearParams);
				viewHolder.chatItemMesImageView.setBackgroundResource(R.drawable.chat_voice_player_three);
				Log.e(TAG, "time" + entity.getGcContent().substring(0, entity.getGcContent().indexOf("**")));
				viewHolder.chatItemMesVoiceTimeTextView.setText(entity.getGcContent().substring(0, entity.getGcContent().indexOf("**")));
				viewHolder.chatRelativeLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(onChatItemClickListener != null){
							onChatItemClickListener.onChatItemClick(v, position);
						}
					}
				});
			}else{
				viewHolder.chatItemMesVoiceTimeTextView.setVisibility(View.INVISIBLE);
				viewHolder.chatItemMesImageView.setVisibility(View.GONE);
				viewHolder.chatItemMesContentTextView.setVisibility(View.VISIBLE);
				SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, entity.getGcContent());
				viewHolder.chatItemMesContentTextView.setText(spannableString);
			}
		}
		if(StringUtil.isEmpty(entity.getPostname())){
			viewHolder.chatItemMesUserTextView.setText(entity.getUsersname());
		}else{
			viewHolder.chatItemMesUserTextView.setText(entity.getUsersname() + "(" + entity.getPostname()+")");
		}
		viewHolder.chatRelativeLayout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(onChatItemLongClickListener != null){
					onChatItemLongClickListener.onChatItemLongClick(v, position);
				}
				return false;
			}
		});
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.chat_header_image)
				.showImageOnFail(R.drawable.chat_header_image)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + entity.getImages(), viewHolder.chatItemMesUserHeaderImageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
		
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
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
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			}
		});

		return convertView;
	}

	class ViewHolder {
		public TextView chatItemMesUserTextView, chatItemMesTimeTextView,
				chatItemMesContentTextView, chatItemMesVoiceTimeTextView;
		public ImageView chatItemMesUserHeaderImageView, chatItemMesImageView;
		public RelativeLayout chatRelativeLayout;
		public boolean isComMsg = true;
	}
	
	/**
     * 单击长按事件监听器
     */
    private OnChatItemLongClickListener onChatItemLongClickListener = null;
    private OnChatItemClickListener onChatItemClickListener = null;
    
    
    public void setOnChatItemLongClickListener(OnChatItemLongClickListener onChatItemLongClickListener){
    	this.onChatItemLongClickListener = onChatItemLongClickListener;
    }

    public interface OnChatItemLongClickListener {
        void onChatItemLongClick(View v, int position);
    }
    
    public void setOnChatItemClickListener(OnChatItemClickListener onChatItemClickListener){
    	this.onChatItemClickListener = onChatItemClickListener;
    }

    public interface OnChatItemClickListener {
        void onChatItemClick(View v, int position);
    }
}

package com.education.zhxy.chat.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.view.CircleImageView;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.ArrayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PersonAdapter extends BaseAdapter {

	private static final String TAG = PersonAdapter.class.getSimpleName();
	
	private Context context;
	
	private List<UserInfo> userInfoList;
	
	public PersonAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<UserInfo> userInfoList){
		this.userInfoList = userInfoList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(userInfoList) ? 0 : userInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(userInfoList) ? null : userInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(R.layout.chat_class_person_item, null);
			viewHolder.chatClassPersonItemCircleImageView = (CircleImageView)convertView.findViewById(R.id.chatClassPersonItemCircleImageView);
			
			viewHolder.chatClassPersonItemTextView = (TextView)convertView.findViewById(R.id.chatClassPersonItemTextView);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		UserInfo userInfo = userInfoList.get(position);
		if(userInfo != null){
			viewHolder.chatClassPersonItemTextView.setText(userInfo.getUsName());
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.common_header_image)
					.showImageOnFail(R.drawable.common_header_image)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + userInfo.getImage(), viewHolder.chatClassPersonItemCircleImageView, options, new SimpleImageLoadingListener() {
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
		}
		
		return convertView;
	}
	class ViewHolder {
		CircleImageView chatClassPersonItemCircleImageView;
		TextView chatClassPersonItemTextView;
	}

}

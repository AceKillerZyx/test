package com.education.zhxy.heart.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.heart.data.bean.Teacher;
import com.education.zhxy.util.ArrayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HeartAdapter extends BaseAdapter {
	
	private static final String TAG = HeartAdapter.class.getSimpleName();

	private Context context;
	
	private List<Teacher> teacherList;
	
	public HeartAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<Teacher> teacherList){
		this.teacherList = teacherList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(teacherList) ? 0 : teacherList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(teacherList) ? null : teacherList.get(position);
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
			convertView = (View) inflater.inflate(R.layout.heart_item, null);
			viewHolder.heartItemImageView = (ImageView)convertView.findViewById(R.id.heartItemImageView);
			
			viewHolder.heartItemNameTextView = (TextView)convertView.findViewById(R.id.heartItemNameTextView);
			viewHolder.heartItemSexTextView = (TextView)convertView.findViewById(R.id.heartItemSexTextView);
			viewHolder.heartItemAgeTextView = (TextView)convertView.findViewById(R.id.heartItemAgeTextView);
			viewHolder.heartItemEducationTextView = (TextView)convertView.findViewById(R.id.heartItemEducationTextView);
			viewHolder.heartItemQualificationsTextView = (TextView)convertView.findViewById(R.id.heartItemQualificationsTextView);
			viewHolder.heartItemCertificateNumberTextView = (TextView)convertView.findViewById(R.id.heartItemCertificateNumberTextView);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Teacher teacher = teacherList.get(position);
		if(teacher != null){
			viewHolder.heartItemNameTextView.setText(String.format(context.getResources().getString(R.string.love_name), teacher.getHName()));
			viewHolder.heartItemSexTextView.setText(String.format(context.getResources().getString(R.string.love_sex), teacher.getHSex()));
			viewHolder.heartItemAgeTextView.setText(String.format(context.getResources().getString(R.string.love_age), String.valueOf(teacher.getHAge())));
			viewHolder.heartItemEducationTextView.setText(String.format(context.getResources().getString(R.string.heart_education), teacher.getHCulture()));
			viewHolder.heartItemQualificationsTextView.setText(String.format(context.getResources().getString(R.string.heart_qualifications), teacher.getHAptitude()));
			viewHolder.heartItemCertificateNumberTextView.setText(String.format(context.getResources().getString(R.string.heart_certificate_number), teacher.getHCertificate()));
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.heart_item_bg)
					.showImageOnFail(R.drawable.heart_item_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + teacher.getHImage(), viewHolder.heartItemImageView, options, new SimpleImageLoadingListener() {
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
		ImageView heartItemImageView;
		TextView heartItemNameTextView,
				heartItemSexTextView, heartItemAgeTextView,
				heartItemEducationTextView, heartItemQualificationsTextView,
				heartItemCertificateNumberTextView;
	}

}

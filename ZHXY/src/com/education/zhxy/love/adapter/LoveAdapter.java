package com.education.zhxy.love.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.love.activity.LoveDetailActivity;
import com.education.zhxy.love.data.bean.Students;
import com.education.zhxy.util.ArrayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class LoveAdapter extends BaseAdapter {
	
	private static final String TAG = LoveAdapter.class.getSimpleName();

	private Context context;
	
	private int type = 0;
	
	private List<Students> studentsList;
	
	public LoveAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<Students> studentsList,int type){
		this.type = type;
		this.studentsList = studentsList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(studentsList) ? 0 : studentsList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(studentsList) ? null : studentsList.get(position);
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
			convertView = (View) inflater.inflate(R.layout.love_item, null);
			viewHolder.loveItemImageView = (ImageView)convertView.findViewById(R.id.loveItemImageView);
			
			viewHolder.loveItemNameTextView = (TextView)convertView.findViewById(R.id.loveItemNameTextView);
			viewHolder.loveItemContentTextView = (TextView)convertView.findViewById(R.id.loveItemContentTextView);
			
			viewHolder.loveItemButton = (Button)convertView.findViewById(R.id.loveItemButton);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Students students = studentsList.get(position);
		if(students != null){
			viewHolder.loveItemNameTextView.setText(students.getNames());
			viewHolder.loveItemContentTextView.setText(students.getPoorContent());
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.train_item_bg)
					.showImageOnFail(R.drawable.train_item_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + students.getImages(), viewHolder.loveItemImageView, options, new SimpleImageLoadingListener() {
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
			if(type == 0){
				viewHolder.loveItemButton.setVisibility(View.VISIBLE);
				viewHolder.loveItemButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,LoveDetailActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(Constants.STUDENTS, students);
						context.startActivity(intent);
					}
				});
			}else{
				viewHolder.loveItemButton.setVisibility(View.INVISIBLE);
			}
		}
		
		return convertView;
	}
	class ViewHolder {
		Button loveItemButton;
		ImageView loveItemImageView;
		TextView loveItemNameTextView, loveItemContentTextView;
	}

}

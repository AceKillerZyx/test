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
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.data.bean.Work;
import com.education.zhxy.util.ArrayUtil;
import com.education.zhxy.util.DateUtil;
import com.education.zhxy.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HistoryWorkAdapter extends BaseAdapter {

	private static final String TAG = HistoryWorkAdapter.class.getSimpleName();

	private Context context;

	private List<Work> workList;

	public HistoryWorkAdapter(Context context) {
		super();
		this.context = context;
	}

	public void resetData(List<Work> workList) {
		this.workList = workList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(workList) ? 0 : workList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(workList) ? null : workList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(
					R.layout.chat_history_work_item, null);
			viewHolder.chatHistoryWorkItemImageView = (ImageView) convertView
					.findViewById(R.id.chatHistoryWorkItemImageView);

			viewHolder.chatHistoryWorkItemWeekTextView = (TextView) convertView
					.findViewById(R.id.chatHistoryWorkItemWeekTextView);
			viewHolder.chatHistoryWorkItemTimeTextView = (TextView) convertView
					.findViewById(R.id.chatHistoryWorkItemTimeTextView);
			viewHolder.chatHistoryWorkItemInfoTextView = (TextView) convertView
					.findViewById(R.id.chatHistoryWorkItemInfoTextView);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Work work = workList.get(position);
		if (work != null) {
			viewHolder.chatHistoryWorkItemTimeTextView.setText("家庭作业" + work.getWkDate().substring(0, work.getWkDate().indexOf("T")));
			viewHolder.chatHistoryWorkItemWeekTextView.setText(DateUtil.getWeek(work.getWkDate()));
			viewHolder.chatHistoryWorkItemInfoTextView.setText(work.getWkContent());
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.study_item_bg)
					.showImageOnFail(R.drawable.study_item_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			if(!StringUtil.isEmpty(work.getWkImage())){
				ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + 
						work.getWkImage(),
						viewHolder.chatHistoryWorkItemImageView, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri, View view,
									FailReason failReason) {
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

		return convertView;
	}

	class ViewHolder {
		ImageView chatHistoryWorkItemImageView;
		TextView chatHistoryWorkItemWeekTextView,
				chatHistoryWorkItemTimeTextView,
				chatHistoryWorkItemInfoTextView;
	}

}

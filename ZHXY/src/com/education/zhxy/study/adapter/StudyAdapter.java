package com.education.zhxy.study.adapter;

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
import com.education.zhxy.study.data.bean.Book;
import com.education.zhxy.util.ArrayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

@SuppressLint("InflateParams") 
public class StudyAdapter extends BaseAdapter {
	
	private static final String TAG = StudyAdapter.class.getSimpleName();
	
	private Context context;
	
	private List<Book> bookList;
	
	public StudyAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<Book> bookList){
		this.bookList = bookList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(bookList) ? 0 : bookList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(bookList) ? null : bookList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(R.layout.study_item, null);
			viewHolder.studyItemImageView = (ImageView)convertView.findViewById(R.id.studyItemImageView);
			
			viewHolder.studyItemNameTextView = (TextView)convertView.findViewById(R.id.studyItemNameTextView);
			viewHolder.studyItemAuthorTextView = (TextView)convertView.findViewById(R.id.studyItemAuthorTextView);
			viewHolder.studyItemInfoTextView = (TextView)convertView.findViewById(R.id.studyItemInfoTextView);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Book book = bookList.get(position);
		if(book != null){
			viewHolder.studyItemNameTextView.setText(book.getBookName());
			viewHolder.studyItemAuthorTextView.setText(String.format(context.getString(R.string.study_author), book.getBookAutho()));
			viewHolder.studyItemInfoTextView.setText(book.getBookIntro());
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.study_item_bg)
					.showImageOnFail(R.drawable.study_item_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + book.getBookImages(), viewHolder.studyItemImageView, options, new SimpleImageLoadingListener() {
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
		ImageView studyItemImageView;
		TextView studyItemNameTextView, studyItemAuthorTextView,
				studyItemInfoTextView;
	}

}

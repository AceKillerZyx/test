package com.education.zhxy.chat.activity;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.data.bean.Work;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HomeWorkActivity extends BasicActivity {
	
	private static final String TAG = HomeWorkActivity.class.getSimpleName();
	
	private TextView chatHomeWorkBackTextView;
	
	private EditText chatHomeWorkEditText;
	
	private ImageView chatHomeWorkOneImageView, chatHomeWorkTwoImageView,
			chatHomeWorkThreeImageView, chatHomeWorkFourImageView,
			chatHomeWorkFiveImageView;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chatHomeWorkBackTextView:
				this.finish();
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.chat_home_work;
	}

	@Override
	public void initUI() {
		chatHomeWorkBackTextView = (TextView)findViewById(R.id.chatHomeWorkBackTextView);
		chatHomeWorkBackTextView.setOnClickListener(this);
		
		chatHomeWorkEditText = (EditText)findViewById(R.id.chatHomeWorkEditText);
		chatHomeWorkOneImageView = (ImageView)findViewById(R.id.chatHomeWorkOneImageView);
		chatHomeWorkTwoImageView = (ImageView)findViewById(R.id.chatHomeWorkTwoImageView);
		chatHomeWorkThreeImageView = (ImageView)findViewById(R.id.chatHomeWorkThreeImageView);
		chatHomeWorkFourImageView = (ImageView)findViewById(R.id.chatHomeWorkFourImageView);
		chatHomeWorkFiveImageView = (ImageView)findViewById(R.id.chatHomeWorkFiveImageView);
	}

	@Override
	public void initData() {
		Work work = (Work)getIntent().getSerializableExtra(Constants.WORK);
		if(work != null){
			initWork(work);
		}
	}
	
	private void initWork(Work work){
		chatHomeWorkBackTextView.setText("家庭作业" + work.getWkDate().substring(0, work.getWkDate().indexOf("T")));
		chatHomeWorkEditText.setText(work.getWkContent());
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.study_item_bg)
				.showImageOnFail(R.drawable.study_item_bg)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		if(!StringUtil.isEmpty(work.getWkImage())){
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + 
					work.getWkImage(),
					chatHomeWorkOneImageView, options,
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
		
		if(!StringUtil.isEmpty(work.getWkImage1())){
			ImageLoader.getInstance().displayImage(
					work.getWkImage1().replace("\\/", "/"),
					chatHomeWorkTwoImageView, options,
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

		if(!StringUtil.isEmpty(work.getWkImage2())){
			ImageLoader.getInstance().displayImage(
					work.getWkImage2().replace("\\/", "/"),
					chatHomeWorkThreeImageView, options,
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
		
		if(!StringUtil.isEmpty(work.getWkImage3())){
			ImageLoader.getInstance().displayImage(
					work.getWkImage3().replace("\\/", "/"),
					chatHomeWorkFourImageView, options,
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
		
		if(!StringUtil.isEmpty(work.getWkImage4())){
			ImageLoader.getInstance().displayImage(
					work.getWkImage4().replace("\\/", "/"),
					chatHomeWorkFiveImageView, options,
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
		
	}

}

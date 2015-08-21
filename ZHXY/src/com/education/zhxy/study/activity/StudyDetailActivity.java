package com.education.zhxy.study.activity;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.study.data.bean.Book;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class StudyDetailActivity extends BasicActivity {
	
	private static final String TAG = StudyDetailActivity.class.getSimpleName();
	
	private ImageView studyDetailImageView;
	
	private TextView studyDetailBackTextView, studyDetailNamerTextView,
			studyDetailAiuthorTextView, studyDetailPublishersTextView,
			studyDetailTimeTextView, studyDetailInforTextView;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.studyDetailBackTextView:
				this.finish();
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.study_detail;
	}

	@Override
	public void initUI() {
		studyDetailBackTextView = (TextView)findViewById(R.id.studyDetailBackTextView);
		studyDetailBackTextView.setOnClickListener(this);
		
		studyDetailImageView = (ImageView)findViewById(R.id.studyDetailImageView);
		studyDetailNamerTextView = (TextView)findViewById(R.id.studyDetailNamerTextView);
		studyDetailAiuthorTextView = (TextView)findViewById(R.id.studyDetailAiuthorTextView);
		studyDetailPublishersTextView = (TextView)findViewById(R.id.studyDetailPublishersTextView);
		studyDetailTimeTextView = (TextView)findViewById(R.id.studyDetailTimeTextView);
		studyDetailInforTextView = (TextView)findViewById(R.id.studyDetailInforTextView);
	}

	@Override
	public void initData() {
		Book book = (Book)getIntent().getSerializableExtra(Constants.BOOK);
		if(book != null){
			initBook(book);
		}
	}
	
	private void initBook(Book book){
		studyDetailNamerTextView.setText(book.getBookName());
		studyDetailAiuthorTextView.setText(book.getBookAutho());
		studyDetailPublishersTextView.setText(book.getBookConcern());
		if(!StringUtil.isEmpty(book.getBookDates())){
			studyDetailTimeTextView.setText(book.getBookDates().replace("T", " "));
		}
		studyDetailInforTextView.setText(book.getBookIntro());
		if(!StringUtil.isEmpty(book.getBookImages())){
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.study_item_bg)
					.showImageOnFail(R.drawable.study_item_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(
					ReleaseConfigure.ROOT_IMAGE + book.getBookImages(),
					studyDetailImageView, options,
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
		 ToastUtil.toast(getApplicationContext(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getApplicationContext(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
		}  	
	}
	
	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getApplicationContext(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getApplicationContext(), R.string.common_no_data);
		}  
	}
	
	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {

	}
}

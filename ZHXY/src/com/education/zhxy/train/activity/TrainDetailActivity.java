package com.education.zhxy.train.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.education.zhxy.location.activity.ZHXYLocationActivity;
import com.education.zhxy.train.data.bean.Train;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TrainDetailActivity extends BasicActivity {
	
	private static final String TAG = TrainDetailActivity.class.getSimpleName();
	
	private Train train;
	
	private ImageView trainDetailImaeView;

	private TextView trainDetailBackTextView, trainDetailSchoolInfoTextView,
			trainDetailSchoolAddressTextView, trainDetailSchoolPhoneTextView,
			trainDetailPhoneTextView, trainDetailMessageTextView,
			trainDetailLocationTextView;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.trainDetailBackTextView:
				this.finish();
				break;
			case R.id.trainDetailPhoneTextView:
				if(train != null && !StringUtil.isEmpty(train.getTsTel())){
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + train.getTsTel()));  
		            startActivity(intent);
				}
				break;
			case R.id.trainDetailMessageTextView:
				if(train != null && !StringUtil.isEmpty(train.getTsTel())){
					Uri uri = Uri.parse("smsto:"+train.getTsTel());            
					Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
					startActivity(it); 
				}
				break;
			case R.id.trainDetailLocationTextView:
				if(train != null){
					Intent intent = new Intent(this,ZHXYLocationActivity.class);
					intent.putExtra(Constants.LAT, train.getStuLatitude());
					intent.putExtra(Constants.LNG, train.getStuLongitude());
					startActivity(intent);
				}
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.train_detail;
	}

	@Override
	public void initUI() {
		trainDetailBackTextView = (TextView)findViewById(R.id.trainDetailBackTextView);
		trainDetailBackTextView.setOnClickListener(this);
		
		trainDetailImaeView = (ImageView)findViewById(R.id.trainDetailImaeView);
		trainDetailSchoolInfoTextView = (TextView)findViewById(R.id.trainDetailSchoolInfoTextView);
		trainDetailSchoolAddressTextView = (TextView)findViewById(R.id.trainDetailSchoolAddressTextView);
		trainDetailSchoolPhoneTextView = (TextView)findViewById(R.id.trainDetailSchoolPhoneTextView);
		trainDetailPhoneTextView = (TextView)findViewById(R.id.trainDetailPhoneTextView);
		trainDetailPhoneTextView.setOnClickListener(this);
		trainDetailMessageTextView = (TextView)findViewById(R.id.trainDetailMessageTextView);
		trainDetailMessageTextView.setOnClickListener(this);
		trainDetailLocationTextView = (TextView)findViewById(R.id.trainDetailLocationTextView);
		trainDetailLocationTextView.setOnClickListener(this);
	}

	@Override
	public void initData() {
		train = (Train)getIntent().getSerializableExtra(Constants.TRAIN);
		if(train != null){
			initTrain(train);
		}
	}
	
	private void initTrain(Train train){
		trainDetailSchoolInfoTextView.setText(train.getTsIntro());
		trainDetailSchoolAddressTextView.setText(train.getTsAdderss());
		trainDetailSchoolPhoneTextView.setText(train.getTsTel());
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.heart_item_bg)
				.showImageOnFail(R.drawable.heart_item_bg)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + train.getTsImages(), trainDetailImaeView, options, new SimpleImageLoadingListener() {
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

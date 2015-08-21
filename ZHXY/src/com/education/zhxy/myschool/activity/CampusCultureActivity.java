package com.education.zhxy.myschool.activity;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myschool.data.bean.CampusCulture;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CampusCultureActivity extends BasicActivity {
	
	private static final String TAG = CampusCultureActivity.class.getSimpleName();
	
	private HttpTask httpTask;
	
	private ImageView campusCultureOneImaeView, campusCultureTwoImaeView,
			campusCultureThreeImaeView, campusCultureFourImaeView,
			campusCultureFiveImaeView;
	
	private LinearLayout urlLinearLayout;
	
	private TextView campusCultureBackTextView, campusCultureTitleOneTextView,
			campusCultureContentOneTextView, campusCultureTitleTwoTextView,
			campusCultureContentTwoTextView, campusCultureTitleThreeTextView,
			campusCultureContentThreeTextView, campusCultureTitleFourTextView,
			campusCultureContentFourTextView, campusCultureTitleFiveTextView,
			campusCultureContentFiveTextView,campusCultureContentMoreTextView;
	
	private String url;
	private	CampusCulture campusCulture;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.campusCultureBackTextView:
				this.finish();
				break;
			case R.id.campusCultureContentMoreTextView:
				Intent intent=new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri uri=Uri.parse("http://"+campusCulture.getUrl());
				intent.setData(uri);
				startActivity(intent);
		}
	}
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.SCHOOLID, String.valueOf(SharedPreferencesUtil.getSchoolId(getApplicationContext())));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYSCHOOL_CAMPUS_CULTURE, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myschool_campus_culture;
	}

	@Override
	public void initUI() {
		campusCultureBackTextView = (TextView)findViewById(R.id.campusCultureBackTextView);
		campusCultureBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		campusCultureOneImaeView = (ImageView)findViewById(R.id.campusCultureOneImaeView);
		campusCultureTwoImaeView = (ImageView)findViewById(R.id.campusCultureTwoImaeView);
		campusCultureThreeImaeView = (ImageView)findViewById(R.id.campusCultureThreeImaeView);
		campusCultureFourImaeView = (ImageView)findViewById(R.id.campusCultureFourImaeView);
		campusCultureFiveImaeView = (ImageView)findViewById(R.id.campusCultureFiveImaeView);
		
		campusCultureTitleOneTextView = (TextView)findViewById(R.id.campusCultureTitleOneTextView);
		campusCultureContentOneTextView = (TextView)findViewById(R.id.campusCultureContentOneTextView);
		campusCultureTitleTwoTextView = (TextView)findViewById(R.id.campusCultureTitleTwoTextView);
		campusCultureContentTwoTextView = (TextView)findViewById(R.id.campusCultureContentTwoTextView);
		campusCultureTitleThreeTextView = (TextView)findViewById(R.id.campusCultureTitleThreeTextView);
		campusCultureContentThreeTextView = (TextView)findViewById(R.id.campusCultureContentThreeTextView);
		campusCultureTitleFourTextView = (TextView)findViewById(R.id.campusCultureTitleFourTextView);
		campusCultureContentFourTextView = (TextView)findViewById(R.id.campusCultureContentFourTextView);
		campusCultureTitleFiveTextView = (TextView)findViewById(R.id.campusCultureTitleFiveTextView);
		campusCultureContentFiveTextView = (TextView)findViewById(R.id.campusCultureContentFiveTextView);
		
		
		//查看更多
		urlLinearLayout=(LinearLayout) findViewById(R.id.urlLinearLayout);
		campusCultureContentMoreTextView=(TextView) findViewById(R.id.campusCultureContentMoreTextView);
		campusCultureContentMoreTextView.setOnClickListener(this);
		
		search();
		
	}

	@Override
	public void initData() {

	}
	
	@Override
	public void noNet(HttpTask task) {
		pd.dismiss();
		 ToastUtil.toast(getApplicationContext(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
		pd.dismiss();
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
		pd.dismiss();
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
		Log.e(TAG, result.getData());
		pd.dismiss();
		if (result != null && !StringUtil.isEmpty(result.getData()) && StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if (commonResult.validate()) {
					JSONArray jsonArray = JSON.parseArray(commonResult.getData());
					if (jsonArray != null && jsonArray.size() > 0) {
						initCampusCulture(jsonArray);
					}
				} else {
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initCampusCulture(JSONArray jsonArray){
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		campusCulture = JSONObject.toJavaObject(jsonObject, CampusCulture.class);
		if (campusCulture.getUrl()==null) {
			urlLinearLayout.setVisibility(View.GONE);
		}else {
			campusCultureContentMoreTextView.setText(campusCulture.getUrl());
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.common_viewpager_bg)
				.showImageOnFail(R.drawable.common_viewpager_bg)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		if(!StringUtil.isEmpty(campusCulture.getCsynopsisimage())){
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE +campusCulture.getCsynopsisimage(), campusCultureOneImaeView,
					options, new SimpleImageLoadingListener() {
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
			campusCultureTitleOneTextView.setText(campusCulture.getCsynopsis());
			campusCultureContentOneTextView.setText(campusCulture.getCsynopsistext());
		}else{
			campusCultureOneImaeView.setVisibility(View.GONE);
			campusCultureTitleOneTextView.setVisibility(View.GONE);
			campusCultureContentOneTextView.setVisibility(View.GONE);
		}
		
		if(!StringUtil.isEmpty(campusCulture.getCmottoimage())){
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE +campusCulture.getCmottoimage(), campusCultureTwoImaeView,
					options, new SimpleImageLoadingListener() {
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
			campusCultureTitleTwoTextView.setText(campusCulture.getCmotto());
			campusCultureContentTwoTextView.setText(campusCulture.getCmottotext());
		}else{
			campusCultureTwoImaeView.setVisibility(View.GONE);
			campusCultureTitleTwoTextView.setVisibility(View.GONE);
			campusCultureContentTwoTextView.setVisibility(View.GONE);
		}
		
		if(!StringUtil.isEmpty(campusCulture.getCbadgeimage())){
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE +campusCulture.getCbadgeimage(), campusCultureThreeImaeView,
					options, new SimpleImageLoadingListener() {
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
			campusCultureTitleThreeTextView.setText(campusCulture.getCbadge());
			campusCultureContentThreeTextView.setText(campusCulture.getCbadgetext());
		}else{
			campusCultureThreeImaeView.setVisibility(View.GONE);
			campusCultureTitleThreeTextView.setVisibility(View.GONE);
			campusCultureContentThreeTextView.setVisibility(View.GONE);
		}
		
		if(!StringUtil.isEmpty(campusCulture.getCthoughtimage())){
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE +campusCulture.getCthoughtimage(), campusCultureFourImaeView,
					options, new SimpleImageLoadingListener() {
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
			campusCultureTitleFourTextView.setText(campusCulture.getCthought());
			campusCultureContentThreeTextView.setText(campusCulture.getCthoughttext());
		}else{
			campusCultureFourImaeView.setVisibility(View.GONE);
			campusCultureTitleFourTextView.setVisibility(View.GONE);
			campusCultureContentFourTextView.setVisibility(View.GONE);
		}
		
		if(!StringUtil.isEmpty(campusCulture.getCtext1image())){
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE +campusCulture.getCtext1image(), campusCultureFiveImaeView,
					options, new SimpleImageLoadingListener() {
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
			campusCultureTitleFiveTextView.setText(campusCulture.getCtext1());
			campusCultureContentFiveTextView.setText(campusCulture.getCctext1s());
		}else{
			campusCultureFiveImaeView.setVisibility(View.GONE);
			campusCultureTitleFiveTextView.setVisibility(View.GONE);
			campusCultureContentFiveTextView.setVisibility(View.GONE);
		}
	}

}

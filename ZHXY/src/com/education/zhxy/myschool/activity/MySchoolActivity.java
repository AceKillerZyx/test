package com.education.zhxy.myschool.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.education.zhxy.myschool.data.bean.MySchoolAd;
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

@SuppressLint("HandlerLeak") 
public class MySchoolActivity extends BasicActivity {
	
	private static final String TAG = MySchoolActivity.class.getSimpleName();
	
	private HttpTask mySchoolAdHttpTask;
	
	private ImageView[] imageViews = null;

	private ImageView imageView = null;

	private AtomicInteger what = new AtomicInteger(0);

	private boolean isContinue = true;

	private ViewPager mySchoolViewPager;

	private ViewGroup mySchoolLinearLayout;

	private ArrayList<View> imagePageViews = null;

	private TextView mySchoolBackTextView,mySchoolWriteOffTextView;
	
	private RelativeLayout mySchoolCampusCultureRelativeLayout,
			mySchoolTeacherDescriptionRelativeLayout,
			mySchoolInformedRelativeLayout;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mySchoolBackTextView:
				this.finish();
				break;
			case R.id.mySchoolWriteOffTextView:
				SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_CLASS_ID, 0);
				SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_SCHOOL_ID, 0);
				this.finish();
				break;
			case R.id.mySchoolCampusCultureRelativeLayout:
				Intent campusCultureIntent = new Intent(this,CampusCultureActivity.class);
				startActivity(campusCultureIntent);
				break;
			case R.id.mySchoolTeacherDescriptionRelativeLayout:
				Intent teacherDescriptionIntent = new Intent(this,TeacherDescriptionActivity.class);
				startActivity(teacherDescriptionIntent);
				break;
			case R.id.mySchoolInformedRelativeLayout:
				Intent informedIntent = new Intent(this,InformedActivity.class);
				startActivity(informedIntent);
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.myschool_main;
	}

	@Override
	public void initUI() {
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
				
		mySchoolBackTextView = (TextView)findViewById(R.id.mySchoolBackTextView);
		mySchoolBackTextView.setOnClickListener(this);
		
		mySchoolWriteOffTextView = (TextView)findViewById(R.id.mySchoolWriteOffTextView);
		mySchoolWriteOffTextView.setOnClickListener(this);
		
		mySchoolViewPager = (ViewPager) findViewById(R.id.mySchoolViewPager);
		mySchoolLinearLayout = (ViewGroup)findViewById(R.id.mySchoolLinearLayout);
		
		mySchoolCampusCultureRelativeLayout = (RelativeLayout)findViewById(R.id.mySchoolCampusCultureRelativeLayout);
		mySchoolCampusCultureRelativeLayout.setOnClickListener(this);
		mySchoolTeacherDescriptionRelativeLayout = (RelativeLayout)findViewById(R.id.mySchoolTeacherDescriptionRelativeLayout);
		mySchoolTeacherDescriptionRelativeLayout.setOnClickListener(this);
		mySchoolInformedRelativeLayout = (RelativeLayout)findViewById(R.id.mySchoolInformedRelativeLayout);
		mySchoolInformedRelativeLayout.setOnClickListener(this);
		
		mySchoolAd();
	}
	
	private void mySchoolAd() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.SCHOOLID, String.valueOf(SharedPreferencesUtil.getSchoolId(getApplicationContext())));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYSCHOOL_AD, false); // GET
		mySchoolAdHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		mySchoolAdHttpTask.execute(httpParam);
		pd.show();
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
				if (task == mySchoolAdHttpTask) {
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initMySchool(jsonArray);
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
			}
		}
	}
	
	/**
	 * 初始化图片
	 */
	private void initMySchool(JSONArray jsonArray) {
		List<String> stringList = new ArrayList<String>();
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		MySchoolAd mySchoolAd = JSONObject.toJavaObject(jsonObject, MySchoolAd.class);
		if(!StringUtil.isEmpty(mySchoolAd.getSimage())){
			stringList.add(mySchoolAd.getSimage());
		}
		
		if(!StringUtil.isEmpty(mySchoolAd.getSimage1())){
			stringList.add(mySchoolAd.getSimage1());
		}
		
		if(!StringUtil.isEmpty(mySchoolAd.getSimage2())){
			stringList.add(mySchoolAd.getSimage2());
		}
		
		if(!StringUtil.isEmpty(mySchoolAd.getSimage3())){
			stringList.add(mySchoolAd.getSimage3());
		}
		
		if(!StringUtil.isEmpty(mySchoolAd.getSimage4())){
			stringList.add(mySchoolAd.getSimage4());
		}
		
		if(!StringUtil.isEmpty(mySchoolAd.getSimage5())){
			stringList.add(mySchoolAd.getSimage5());
		}
		if(stringList != null && stringList.size() > 0){
			initViewPager(stringList);
		}
	}
	
	/**
	 * 初始化图片
	 */
	private void initViewPager(List<String> stringList) {
		imagePageViews = new ArrayList<View>();
		Log.e(TAG, "size:" + stringList.size());
		for(int i = 0;i < stringList.size();i++){
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.common_viewpager_bg)
					.showImageOnFail(R.drawable.common_viewpager_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + stringList.get(i), imageView, options, new SimpleImageLoadingListener() {
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
				imagePageViews.add(imageView);
		}
		//对imageviews进行填充  
        imageViews = new ImageView[imagePageViews.size()]; 
        //小图标  
        for (int i = 0; i < imagePageViews.size(); i++) { 
            imageView = new ImageView(getApplicationContext()); 
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(12, 12);
            lp.setMargins(5, 2, 5, 5);
            imageView.setLayoutParams(lp); 
            imageViews[i] = imageView; 
            if (i == 0) { 
                imageViews[i].setBackgroundResource(R.drawable.common_dot_focused); 
            } else { 
                imageViews[i].setBackgroundResource(R.drawable.common_dot_normal); 
            } 
            mySchoolLinearLayout.addView(imageViews[i]); 
        } 
        
        mySchoolViewPager.setAdapter(new AdvAdapter(imagePageViews)); 
        mySchoolViewPager.setOnPageChangeListener(new GuidePageChangeListener()); 
        mySchoolViewPager.setOnTouchListener(new OnTouchListener() { 
             
            @SuppressLint("ClickableViewAccessibility") @Override 
            public boolean onTouch(View v, MotionEvent event) { 
                switch (event.getAction()) { 
                case MotionEvent.ACTION_DOWN: 
                case MotionEvent.ACTION_MOVE: 
                    isContinue = false; 
                    break; 
                case MotionEvent.ACTION_UP: 
                    isContinue = true; 
                    break; 
                default: 
                    isContinue = true; 
                    break; 
                } 
                return false; 
            } 
        }); 
        new Thread(new Runnable() { 
 
            @Override 
            public void run() { 
                while (true) { 
                    if (isContinue) { 
                        viewHandler.sendEmptyMessage(what.get()); 
                        whatOption(); 
                    } 
                } 
            } 
 
        }).start(); 
    }
	private void whatOption() {
		what.incrementAndGet();
		if (what.get() > imageViews.length - 1) {
			what.getAndAdd(-4);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
	}

	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mySchoolViewPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};

	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			what.getAndSet(arg0);
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.common_dot_focused);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.common_dot_normal);
				}
			}

		}

	}

	private final class AdvAdapter extends PagerAdapter {
		private List<View> views = null;

		public AdvAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

}

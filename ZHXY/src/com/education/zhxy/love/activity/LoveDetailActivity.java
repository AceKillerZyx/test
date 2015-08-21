package com.education.zhxy.love.activity;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.education.zhxy.love.data.bean.Students;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class LoveDetailActivity extends BasicActivity{
	
	private static final String TAG = LoveDetailActivity.class.getSimpleName();
	
	private HttpTask httpTask,confirmAttentionHttpTask,cancelAttentionHttpTask,searchISAttentionHttpTask;
	
	private int poorid = 0;
	
	private int attid = 0;
	
	private boolean isAttention = false;
	
	private ImageView loveDetailImaeView,loveDetailAttentionImageView;
	
	private Button loveDetailDonateButton,loveDetailDonateOkButton;
	
	private RelativeLayout loveDetailAttentionRelativeLayout;
	
	private EditText loveDetailNameEditText, loveDetailPhoneEditText,
			loveDetailPriceEditText, loveDetailMessageEditText;
	
	private LinearLayout loveDetailContentLinearLayout,loveDetailContentTwoLinearLayout,loveDetailButtonLinearLayout;
	
	private TextView loveDetailBackTextView, loveDetailNameTextView,
			loveDetailSexTextView, loveDetailAgeTextView,
			loveDetailEthnicTextView, loveDetailPovertyTextView,
			loveDetailAddressTextView, loveDetailContentTextView,loveDetailAttentionTextView;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loveDetailBackTextView:
				this.finish();
				break;
			case R.id.loveDetailAttentionRelativeLayout:
				if(isAttention){
					cancelAttention();
				}else{
					confirmAttention();
				}
				break;
			case R.id.loveDetailDonateButton:
				loveDetailNameEditText.setText("");
				loveDetailPhoneEditText.setText("");
				loveDetailPriceEditText.setText("");
				loveDetailMessageEditText.setText("");
				loveDetailContentLinearLayout.setVisibility(View.GONE);
				loveDetailContentTwoLinearLayout.setVisibility(View.VISIBLE);
				loveDetailButtonLinearLayout.setVisibility(View.GONE);
				loveDetailDonateOkButton.setVisibility(View.VISIBLE);
				break;
			case R.id.loveDetailDonateOkButton:
				if(validate()){
					donate();
				}
				break;
		}
	}
	
	private boolean validate(){
		String name = loveDetailNameEditText.getText().toString().trim();
		if(TextUtils.isEmpty(name)){
			ToastUtil.toast(getApplicationContext(), R.string.love_detail_name_null_error);
			return false;
		}
		
		String phone = loveDetailPhoneEditText.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastUtil.toast(getApplicationContext(), R.string.love_detail_phone_null_error);
			return false;
		}
		
		String price = loveDetailPriceEditText.getText().toString().trim();
		if(TextUtils.isEmpty(price)){
			ToastUtil.toast(getApplicationContext(), R.string.love_detail_price_null_error);
			return false;
		}
		return true;
	}
	
	private void donate() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.POORID, String.valueOf(poorid));
		paramMap.put(Constants.DNAMES,loveDetailNameEditText.getText().toString().trim());
		paramMap.put(Constants.DTEL, loveDetailPhoneEditText.getText().toString().trim());
		paramMap.put(Constants.DMONEY, loveDetailPriceEditText.getText().toString().trim());
		paramMap.put(Constants.DCONTENT, loveDetailMessageEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.LOVE_DONATE, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}
	
	private void searchISAttention(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.POORID, String.valueOf(poorid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.LOVE_ATTENTION, false); // GET
		searchISAttentionHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		searchISAttentionHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void confirmAttention() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.POORID, String.valueOf(poorid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.LOVE_CONFIRM_ATTENTION, false); // GET
		confirmAttentionHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		confirmAttentionHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void cancelAttention() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.ATTID, String.valueOf(attid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.LOVE_CANCEL_ATTENTION, false); // GET
		cancelAttentionHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		cancelAttentionHttpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.love_detail;
	}

	@Override
	public void initUI() {
		loveDetailBackTextView = (TextView)findViewById(R.id.loveDetailBackTextView);
		loveDetailBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		loveDetailImaeView = (ImageView)findViewById(R.id.loveDetailImaeView);
		
		loveDetailContentLinearLayout = (LinearLayout)findViewById(R.id.loveDetailContentLinearLayout);
		loveDetailNameTextView = (TextView)findViewById(R.id.loveDetailNameTextView);
		loveDetailSexTextView = (TextView)findViewById(R.id.loveDetailSexTextView);
		loveDetailAgeTextView = (TextView)findViewById(R.id.loveDetailAgeTextView);
		loveDetailEthnicTextView = (TextView)findViewById(R.id.loveDetailEthnicTextView);
		loveDetailPovertyTextView = (TextView)findViewById(R.id.loveDetailPovertyTextView);
		loveDetailAddressTextView = (TextView)findViewById(R.id.loveDetailAddressTextView);
		loveDetailContentTextView = (TextView)findViewById(R.id.loveDetailContentTextView);
		
		loveDetailContentTwoLinearLayout = (LinearLayout)findViewById(R.id.loveDetailContentTwoLinearLayout);
		loveDetailNameEditText = (EditText)findViewById(R.id.loveDetailNameEditText);
		loveDetailPhoneEditText = (EditText)findViewById(R.id.loveDetailPhoneEditText);
		loveDetailPriceEditText = (EditText)findViewById(R.id.loveDetailPriceEditText);
		loveDetailMessageEditText = (EditText)findViewById(R.id.loveDetailMessageEditText);
		
		loveDetailButtonLinearLayout = (LinearLayout)findViewById(R.id.loveDetailButtonLinearLayout);
		loveDetailAttentionRelativeLayout = (RelativeLayout)findViewById(R.id.loveDetailAttentionRelativeLayout);
		loveDetailAttentionRelativeLayout.setOnClickListener(this);
		loveDetailAttentionImageView = (ImageView)findViewById(R.id.loveDetailAttentionImageView);
		loveDetailAttentionTextView = (TextView)findViewById(R.id.loveDetailAttentionTextView);
		loveDetailDonateButton = (Button)findViewById(R.id.loveDetailDonateButton);
		loveDetailDonateButton.setOnClickListener(this);
		loveDetailDonateOkButton = (Button)findViewById(R.id.loveDetailDonateOkButton);
		loveDetailDonateOkButton.setOnClickListener(this);
		
		loveDetailContentLinearLayout.setVisibility(View.VISIBLE);
		loveDetailContentTwoLinearLayout.setVisibility(View.GONE);
		loveDetailButtonLinearLayout.setVisibility(View.VISIBLE);
		loveDetailDonateOkButton.setVisibility(View.GONE);
	}

	@Override
	public void initData() {
		Students students = (Students)getIntent().getSerializableExtra(Constants.STUDENTS);
		if(students != null){
			initStudents(students);
		}
	}
	
	private void initStudents(Students students){
		poorid = students.getId();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.common_viewpager_bg)
				.showImageOnFail(R.drawable.common_viewpager_bg)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + 
				students.getImages(),
				loveDetailImaeView, options,
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
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
					}
				});
		loveDetailNameTextView.setText(String.format(getResources().getString(R.string.love_name), students.getNames()));
		loveDetailSexTextView.setText(String.format(getResources().getString(R.string.love_sex), students.getSex()));
		loveDetailAgeTextView.setText(String.format(getResources().getString(R.string.love_age), String.valueOf(students.getAge())));
		loveDetailEthnicTextView.setText(String.format(getResources().getString(R.string.love_ethnic), students.getPoor()));
		loveDetailPovertyTextView.setText(String.format(getResources().getString(R.string.love_poverty), students.getPoor()));
		loveDetailAddressTextView.setText(String.format(getResources().getString(R.string.love_address), students.getAdderss()));
		loveDetailContentTextView.setText(students.getPoorContent());
		searchISAttention();
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {
		Log.e(TAG, result.getData());
		pd.dismiss();
		if (result != null && !StringUtil.isEmpty(result.getData()) && StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if(task == httpTask){
					if (commonResult.validate()) {
						ToastUtil.toast(getApplicationContext(), R.string.love_donate_success);
						loveDetailContentLinearLayout.setVisibility(View.VISIBLE);
						loveDetailContentTwoLinearLayout.setVisibility(View.GONE);
						loveDetailButtonLinearLayout.setVisibility(View.VISIBLE);
						loveDetailDonateOkButton.setVisibility(View.GONE);
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == confirmAttentionHttpTask){
					if (commonResult.validate()) {
						JSONObject jsonObject = JSON.parseObject(commonResult.getData());
						if(jsonObject != null && jsonObject.size() > 0){
							attid = jsonObject.getIntValue(Constants.ID);
						}
						isAttention = true;
						loveDetailAttentionTextView.setText(R.string.love_detail_attentioned);
						loveDetailAttentionImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.love_attention_checked));
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == cancelAttentionHttpTask){
					if (commonResult.validate()) {
						isAttention = false;
						loveDetailAttentionTextView.setText(R.string.love_detail_attention);
						loveDetailAttentionImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.love_attention_normal));
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == searchISAttentionHttpTask){
					if (commonResult.validate()) {
						JSONObject jsonObject = JSON.parseObject(commonResult.getData());
						if(jsonObject != null && jsonObject.size() > 0){
							attid = jsonObject.getIntValue(Constants.ID);
						}
						isAttention = true;
						loveDetailAttentionTextView.setText(R.string.love_detail_attentioned);
						loveDetailAttentionImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.love_attention_checked));
					}
				}
			}
		}
	}
}

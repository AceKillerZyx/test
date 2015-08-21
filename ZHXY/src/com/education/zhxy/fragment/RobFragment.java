package com.education.zhxy.fragment;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.common.view.RushBuyCountDownTimerView;
import com.education.zhxy.fragment.data.bean.Rob;
import com.education.zhxy.http.core.HttpListener;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.DateUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class RobFragment extends Fragment implements OnClickListener,HttpListener{
	
	private static final String TAG = RobFragment.class.getSimpleName();
	
	private boolean isRobClick  = false;
	
	private boolean isAddressOperate  = false;
	//boolean isFirst=true;
	
	private Rob rob;
	
	private View commonView;

	private Dialog alertDialog;
	
	private ImageView robImageView;
	
	private HttpTask searchHttpTask,robHttpTask,searchInfoHttpTask,updateHttpTask,saveHttpTask,numHttpTask;
	
	private TextView robNumberTextView,robNameTextView,robContentTextView,robTimeTextView;
	
	private RushBuyCountDownTimerView robRushBuyCountDownTimerView;
	
	private Button robButton,robAddressEditButton,robAddressConfirmButton;
	
	private EditText robAddressNameEditText,robAddressPhoneEditText,robAddressAddressEditText;
	
	//自定义ProgressDialog
	Map<String, Boolean> map=new HashMap<String, Boolean>();
	
	private CustomProgressDialog pd = null;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.rob_fragment, container, false);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.robButton:
				Log.e(TAG, "抢？"+isRobClick);
				if(isRobClick){
					
					numSearch();
				}else{
					if(getActivity() != null && !getActivity().isFinishing()){
						ToastUtil.toast(getActivity(), R.string.rob_not_time);
					}
					search();
				}
				break;
			case R.id.robAddressEditButton:
				if(!isAddressOperate){
					isAddressOperate = true;
					robAddressEditButton.setText(R.string.rob_address_cancel);
					robAddressConfirmButton.setText(R.string.rob_address_save);
					robAddressAddressEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.common_rectangle_bg));
					robAddressAddressEditText.setEnabled(true);
				}else{
					isAddressOperate = false;
					robAddressEditButton.setText(R.string.rob_address_edit);
					robAddressConfirmButton.setText(R.string.rob_address_confirm);
					robAddressAddressEditText.setBackgroundColor(color.transparent);
					robAddressAddressEditText.setEnabled(false);
				}
				break;
			case R.id.robAddressConfirmButton:
				if(validate()){
					if(isAddressOperate){
						robAddressAddressEditText.setBackgroundColor(color.transparent);
						robAddressAddressEditText.setEnabled(false);
						isAddressOperate = false;
						robAddressEditButton.setText(R.string.rob_address_edit);
						robAddressConfirmButton.setText(R.string.rob_address_confirm);
						update();
					}else{
						save();
					}
				}
				break;
		}
	}
	
	private boolean validate(){
		String address = robAddressAddressEditText.getText().toString().trim();
		if(TextUtils.isEmpty(address)){
			ToastUtil.toast(getActivity(), R.string.rob_address_null_error);
			return false;
		}
		return true;
	}
	
	@SuppressLint("InflateParams")@Override	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(getActivity());
				
		robImageView = (ImageView)getView().findViewById(R.id.robImageView);
		robNumberTextView = (TextView)getView().findViewById(R.id.robNumberTextView);
		robNameTextView = (TextView)getView().findViewById(R.id.robNameTextView);
		robContentTextView = (TextView)getView().findViewById(R.id.robContentTextView);
		robRushBuyCountDownTimerView = (RushBuyCountDownTimerView)getView().findViewById(R.id.robRushBuyCountDownTimerView);
		robTimeTextView = (TextView)getView().findViewById(R.id.robTimeTextView);
		robButton = (Button)getView().findViewById(R.id.robButton);
		robButton.setOnClickListener(this);
		isRobClick  = false;
		robButton.setBackgroundColor(getResources().getColor(R.color.gray));
		
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		commonView = layoutInflater.inflate(R.layout.rob_address_dialog, null);
		
		robAddressNameEditText = (EditText)commonView.findViewById(R.id.robAddressNameEditText);
		robAddressPhoneEditText = (EditText)commonView.findViewById(R.id.robAddressPhoneEditText);
		robAddressAddressEditText = (EditText)commonView.findViewById(R.id.robAddressAddressEditText);
		
		robAddressEditButton = (Button)commonView.findViewById(R.id.robAddressEditButton);
		robAddressEditButton.setOnClickListener(this);
		
		robAddressConfirmButton = (Button)commonView.findViewById(R.id.robAddressConfirmButton);
		robAddressConfirmButton.setOnClickListener(this);
		
		search();
		
		IntentFilter filter = new IntentFilter();  
        filter.addAction(Constants.UPDATE_ROB_UI);  
        getActivity().registerReceiver(this.broadcastReceiver, filter);
        
        
        
	}
	
	protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {
        	isRobClick  = true;
        	robButton.setClickable(true);
			robButton.setBackgroundResource(R.drawable.myinfo_exit_login_button_selector);
        }  
    };  
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.ROB_LIST, false); // GET
		searchHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		searchHttpTask.execute(httpParam);
		
		//if (isFirst==true) {
			pd.show();
			//isFirst=false;
		//}
		
		/*if (map.get("isJiaZai") ==null || !map.get("isJiaZai") || map.get("isJiaZai")) {
			pd.show();
			map.put("isJiaZai", true);
		}*/
	}
	
	private void searchInfo() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_ACCOUNT_MANAGER_INFO, false); // GET
		searchInfoHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		searchInfoHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void update() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		paramMap.put(Constants.USADDERSS, robAddressAddressEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_ACCOUNT_MANAGER_UPDATE, false); // GET
		updateHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		updateHttpTask.execute(httpParam);
		pd.show();
	}
	
	//数量更新
	private void numUpdate(){
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		paramMap.put(Constants.ROBID, String.valueOf(rob.getId()));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.ROB_UPDATE, false); // GET
		robHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		robHttpTask.execute(httpParam);
		pd.show();
	}
	
	//数量查询
	private void numSearch() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		paramMap.put(Constants.ROBID, String.valueOf(rob.getId()));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.ROB_NUM, false); // GET
		numHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		numHttpTask.execute(httpParam);
		pd.show();
	}

	private void save() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		paramMap.put(Constants.ROBID, String.valueOf(rob.getId()));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.ROB_ADD, false); // GET
		saveHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		saveHttpTask.execute(httpParam);
		pd.show();
		if(alertDialog != null && alertDialog.isShowing()){
			alertDialog.dismiss();
		}
	}

	@Override
	public void noNet(HttpTask task) {
		pd.dismiss();
		ToastUtil.toast(getActivity(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
		pd.dismiss();
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getActivity(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getActivity(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getActivity(), R.string.common_no_data);
		}  	
	}
	
	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {
		pd.dismiss();
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getActivity(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getActivity(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getActivity(), R.string.common_no_data);
		}  
	}

	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {
		Log.e(TAG, result.getData());
		pd.dismiss();
		if (result != null && !StringUtil.isEmpty(result.getData()) && StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if (task == searchHttpTask) {
					if (commonResult.validate() ) {
						JSONObject jsonObject = JSON.parseObject(commonResult.getData());
						if(jsonObject != null && jsonObject.size() > 0){
							initRob(jsonObject);
						}
					} else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),commonResult.getErrMsg());
						}
					}
				}
				
				//数量查询
				if (task == numHttpTask) {
					if (commonResult.validate() ) {
						JSONObject jsonObject = JSONObject.parseObject(commonResult.getData());
						if(jsonObject != null && jsonObject.size() > 0){
							initRob(jsonObject);
							numUpdate();
						}
					}else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),commonResult.getErrMsg());
						}
					}
				}
				
				if (task == robHttpTask) {
					if (commonResult.validate()) {
						if(getActivity() != null && !getActivity().isFinishing()){
							ToastUtil.toast(getActivity(), R.string.rob_success);
						}
						isRobClick  = false;
						robButton.setClickable(false);
						robButton.setBackgroundColor(getResources().getColor(R.color.gray));
						searchInfo();
					} else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),commonResult.getErrMsg());
						}
					}
				}
				
				if (task == searchInfoHttpTask) {
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if(jsonArray != null && jsonArray.size() > 0){
							initUserinfo(jsonArray);
						}
					} else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),commonResult.getErrMsg());
						}
					}
				}
				
				if (task == saveHttpTask) {
					if (commonResult.validate()) {
						if(getActivity() != null && !getActivity().isFinishing()){
							ToastUtil.toast(getActivity(), R.string.rob_takeout);
						}
					} else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),commonResult.getErrMsg());
						}
					}
				}
			}
		}
	}
	
	private void initUserinfo(JSONArray jsonArray){
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		UserInfo userinfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);
		if(userinfo != null){
			robAddressNameEditText.setText(userinfo.getUsName());
			robAddressPhoneEditText.setText(userinfo.getTel());
			robAddressAddressEditText.setText(userinfo.getUsAdderss());
		}
		robAddress();
	}
	
	private void initRob(JSONObject jsonObject){
		rob = JSONObject.toJavaObject(jsonObject, Rob.class);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.common_viewpager_bg)
				.showImageOnFail(R.drawable.common_viewpager_bg)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + rob.getImages(),robImageView, options, new SimpleImageLoadingListener() {
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
		robNumberTextView.setText(rob.getNumber() + "个");
		robNameTextView.setText(rob.getText1());
		robContentTextView.setText(rob.getText2().replace("\\n", "\n"));
		String date = rob.getStartRobdate();
		try {
			String lag = DateUtil.getDatePoor(DateUtil.parse(date.replace("T", " ")), DateUtil.parse(DateUtil.getToday()));
			int day = Integer.parseInt(lag.substring(0, lag.lastIndexOf("*")));
			int hours = Integer.parseInt(lag.substring(lag.lastIndexOf("*") + 1, lag.lastIndexOf("-")));
			int minute = Integer.parseInt(lag.substring(lag.lastIndexOf("-") + 1, lag.lastIndexOf("&")));
			int second = Integer.parseInt(lag.substring(lag.lastIndexOf("&")+1, lag.length()));
			Log.e(TAG, "天" + day + "--时" + hours + "--分" + minute + "--秒" + second);
			robRushBuyCountDownTimerView.setTime(hours, minute, second);
			robRushBuyCountDownTimerView.start();
			if(day == 0){
				robTimeTextView.setVisibility(View.GONE);
			}else{
				robTimeTextView.setVisibility(View.VISIBLE);
				robTimeTextView.setText(day + "天");
			}
			if(day <= 0 && hours <= 0 && minute <= 0 && second <= 0 && rob.getNumber() > 0){
				isRobClick  = true;
				robButton.setBackgroundResource(R.drawable.myinfo_exit_login_button_selector);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 地址
	 */
	@SuppressWarnings("deprecation")
	private void robAddress() {
		robAddressNameEditText.setBackgroundColor(color.transparent);
		robAddressPhoneEditText.setBackgroundColor(color.transparent);
		robAddressAddressEditText.setBackgroundColor(color.transparent);
		robAddressNameEditText.setEnabled(false);
		robAddressPhoneEditText.setEnabled(false);
		robAddressAddressEditText.setEnabled(false);
		if (alertDialog == null) {
			alertDialog = new Dialog(getActivity(), R.style.commonDialog);
			alertDialog.setContentView(commonView);
			Display d = getActivity().getWindowManager().getDefaultDisplay();
			ToastUtil.setDialogLocation(getActivity(), alertDialog, d.getWidth() - 60,d.getHeight() / 2 , 30, d.getHeight() / 3);
		}

		if (alertDialog.isShowing()) {
			alertDialog.dismiss();
		} else {
			alertDialog.show();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(this.broadcastReceiver);
	}

}

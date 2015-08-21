package com.education.zhxy.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.view.CircleImageView;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.easemob.controller.HXSDKHelper;
import com.education.zhxy.myinfo.activity.AboutUsActivity;
import com.education.zhxy.myinfo.activity.AccountManagerActivity;
import com.education.zhxy.myinfo.activity.AppShareActivity;
import com.education.zhxy.myinfo.activity.FeedbackActivity;
import com.education.zhxy.myinfo.activity.InstructionsActivity;
import com.education.zhxy.myinfo.activity.LoginActivity;
import com.education.zhxy.myinfo.activity.MyAttentionActivity;
import com.education.zhxy.myinfo.activity.MyPackage;
import com.education.zhxy.myinfo.activity.NewMessageSetActivity;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.UpdateManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyinfoFragment extends Fragment implements OnClickListener{
	
	private static final String TAG = MyinfoFragment.class.getSimpleName();
	
	private CircleImageView myinfoHeaderCircleImageView;
	
	private Button myinfoExitLoginButton;
	
	private UpdateManager updateManager;
	
	private EMChatOptions chatOptions;
	
	private TextView myinfoAccountManagerTextView, myinfoMyAttentionTextView,
			myinfoSystemUpgradeTextView, myinfoAboutUsTextView,
			myinfoiInstructionsTextView, myinfoFeedbackTextView,myinfoSetTextView,myinfoMyPackageTextView,myinfoShareTextView;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myinfoAccountManagerTextView:
				Intent accountManagerIntent = new Intent(getActivity(),AccountManagerActivity.class);
				startActivityForResult(accountManagerIntent, 0);
				break;
			case R.id.myinfoMyAttentionTextView:
				Intent myAttentionIntent = new Intent(getActivity(),MyAttentionActivity.class);
				startActivity(myAttentionIntent);
				break;
			case R.id.myinfoSystemUpgradeTextView:
				updateManager.checkUpdateInfo();
				break;
			case R.id.myinfoAboutUsTextView:
				Intent aboutUsIntent = new Intent(getActivity(),AboutUsActivity.class);
				startActivity(aboutUsIntent);
				break;
			case R.id.myinfoExitLoginButton:
				pd.show();
				exitLogin();
				break;
			case R.id.myinfoiInstructionsTextView:
				Intent instructionsIntent = new Intent(getActivity(),InstructionsActivity.class);
				startActivity(instructionsIntent);
				break;
			case R.id.myinfoFeedbackTextView:
				Intent feedbackIntent = new Intent(getActivity(),FeedbackActivity.class);
				startActivity(feedbackIntent);
				break;
			case R.id.myinfoSetTextView:
				Intent intent=new Intent(getActivity(),NewMessageSetActivity.class);
				startActivity(intent);
				break;
			case R.id.myinfoMyPackageTextView:
				Intent inent=new  Intent(getActivity(),MyPackage.class);
				startActivity(inent);
				break;
			case R.id.myinfoShareTextView:
				Intent appShareIntent=new Intent(getActivity(),AppShareActivity.class);
				startActivity(appShareIntent);
				break;
		}
	}
	
	private void exitLogin(){
		new Thread(){
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				chatOptions.setNotificationEnable(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
				Intent intent = new Intent();  
		        intent.setAction(Constants.EXIT_LOGIN);  
		        getActivity().sendBroadcast(intent);  
		        SharedPreferencesUtil.putInt(getActivity(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_USID, 0);
				Intent intents = new Intent(getActivity(),LoginActivity.class);
				pd.dismiss();
				getActivity().startActivity(intents);
				getActivity().finish();
			};
		}.start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.myinfo_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		chatOptions = EMChatManager.getInstance().getChatOptions();
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(getActivity());
		myinfoHeaderCircleImageView = (CircleImageView)getView().findViewById(R.id.myinfoHeaderCircleImageView);
		initHeaderImage();
		myinfoAccountManagerTextView = (TextView)getView().findViewById(R.id.myinfoAccountManagerTextView);
		myinfoAccountManagerTextView.setOnClickListener(this);
		myinfoMyAttentionTextView = (TextView)getView().findViewById(R.id.myinfoMyAttentionTextView);
		myinfoMyAttentionTextView.setOnClickListener(this);
		myinfoSystemUpgradeTextView = (TextView)getView().findViewById(R.id.myinfoSystemUpgradeTextView);
		myinfoSystemUpgradeTextView.setOnClickListener(this);
		myinfoAboutUsTextView = (TextView)getView().findViewById(R.id.myinfoAboutUsTextView);
		myinfoAboutUsTextView.setOnClickListener(this);
		myinfoiInstructionsTextView = (TextView)getView().findViewById(R.id.myinfoiInstructionsTextView);
		myinfoiInstructionsTextView.setOnClickListener(this);
		myinfoFeedbackTextView = (TextView)getView().findViewById(R.id.myinfoFeedbackTextView);
		myinfoFeedbackTextView.setOnClickListener(this);
		myinfoSetTextView=(TextView) getView().findViewById(R.id.myinfoSetTextView);
		myinfoSetTextView.setOnClickListener(this);
		myinfoMyPackageTextView=(TextView) getView().findViewById(R.id.myinfoMyPackageTextView);
		myinfoMyPackageTextView.setOnClickListener(this);
		myinfoShareTextView=(TextView) getView().findViewById(R.id.myinfoShareTextView);
		myinfoShareTextView.setOnClickListener(this);
		
		myinfoExitLoginButton = (Button)getView().findViewById(R.id.myinfoExitLoginButton);
		myinfoExitLoginButton.setOnClickListener(this);
		
		updateManager = new UpdateManager(getActivity(), 1);
	}
	
	private void initHeaderImage(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.common_header_image)
				.showImageOnFail(R.drawable.common_header_image)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + SharedPreferencesUtil.getHeaderImage(getActivity()),myinfoHeaderCircleImageView, options, new SimpleImageLoadingListener() {
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
	
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == getActivity().RESULT_OK){
			initHeaderImage();
		}
	}

}

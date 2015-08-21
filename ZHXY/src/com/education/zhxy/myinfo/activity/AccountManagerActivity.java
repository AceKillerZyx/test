package com.education.zhxy.myinfo.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CircleImageView;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myinfo.data.bean.UserInfo;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.ImageUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StorageUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.education.zhxy.util.UploadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AccountManagerActivity extends BasicActivity {
	
	private static final String TAG = AccountManagerActivity.class.getSimpleName();
	
	private File imgFile;
	
	private String picPath;
	
	private Intent intent;
	
	private String myinfoAccountManagerHeaderPath;
	
	private static final int TAKE_PICTURE = 0;

	private static final int CHOOSE_PICTURE = 1;
	
	private View commonView;

	private Dialog alertDialog;
	
	private Button commonCancelButton;
	
	private int jobId = 0;
	
//	private ArrayAdapter<Job> jobArrayAdapter;
	
//	private List<Job> jobList = null;
	
	private Spinner myinfoAccountManagerSexSpinner;
	
	private HttpTask searchHttpTask,updateHttpTask;
	
	private CircleImageView myinfoAccountManagerHeaderCircleImageView;
	
	private TextView myinfoAccountManagerBackTextView,
			myinfoAccountManagerFinishTextView,
			myinfoAccountManagerUpdatePasswordTextView,
			commonPhotographTextView, commonAlbumsTextView,
			myinfoAccountManagerJobTextView;
	
	private EditText myinfoAccountManagerNameEditText,myinfoAccountManagerAgeEditText,
			myinfoAccountManagerInstructionEditText,
			myinfoAccountManagerLoveEditText,
			myinfoAccountManagerClassEditText,
			myinfoAccountManagerAddressEditText;
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myinfoAccountManagerBackTextView:
				setResult(RESULT_OK, intent);
				this.finish();
				break;
			case R.id.myinfoAccountManagerHeaderCircleImageView:
				choisePicture();
				break;
			case R.id.commonPhotographTextView:
				takePicture();
				if(alertDialog != null && alertDialog.isShowing()){
					alertDialog.dismiss();
				}
				break;
			case R.id.commonAlbumsTextView:
				fromPhotoAlbum();
				if(alertDialog != null && alertDialog.isShowing()){
					alertDialog.dismiss();
				}
				break;
			case R.id.commonCancelButton:
				if(alertDialog != null && alertDialog.isShowing()){
					alertDialog.dismiss();
				}
				break;
			case R.id.myinfoAccountManagerFinishTextView:
				update();
				break;
			case R.id.myinfoAccountManagerUpdatePasswordTextView:
				Intent intent = new Intent(this,UpdatePasswordActivity.class);
				startActivity(intent);
				this.finish();
				break;
		}
	}
	
	/*private void job() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_ACCOUNT_MANAGER_JOB, false); // GET
		jobHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		jobHttpTask.execute(httpParam);
	}*/
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_ACCOUNT_MANAGER_INFO, false); // GET
		searchHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		searchHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void update() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.POID, String.valueOf(jobId));
		paramMap.put(Constants.IMAGE, myinfoAccountManagerHeaderPath);
		Log.e(TAG, "sex:" + myinfoAccountManagerSexSpinner.getSelectedItem().toString());
		paramMap.put(Constants.SEX, myinfoAccountManagerSexSpinner.getSelectedItem().toString());
		paramMap.put(Constants.AGE, myinfoAccountManagerAgeEditText.getText().toString().trim());
		paramMap.put(Constants.USCONSTELLATION, myinfoAccountManagerInstructionEditText.getText().toString().trim());
		paramMap.put(Constants.USPERSONALNOTE, myinfoAccountManagerLoveEditText.getText().toString().trim());
		paramMap.put(Constants.USADDERSS, myinfoAccountManagerAddressEditText.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYINFO_ACCOUNT_MANAGER_UPDATE, false); // GET
		updateHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		updateHttpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myinfo_account_manager;
	}

	@SuppressLint("InflateParams") @Override
	public void initUI() {
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
				
		myinfoAccountManagerBackTextView = (TextView)findViewById(R.id.myinfoAccountManagerBackTextView);
		myinfoAccountManagerBackTextView.setOnClickListener(this);
		
		myinfoAccountManagerFinishTextView = (TextView)findViewById(R.id.myinfoAccountManagerFinishTextView);
		myinfoAccountManagerFinishTextView.setOnClickListener(this);
		
		myinfoAccountManagerHeaderCircleImageView = (CircleImageView)findViewById(R.id.myinfoAccountManagerHeaderCircleImageView);
		myinfoAccountManagerHeaderCircleImageView.setOnClickListener(this);
		
		myinfoAccountManagerNameEditText = (EditText)findViewById(R.id.myinfoAccountManagerNameEditText);
		// 建立数据源
		String[] sexItems = getResources().getStringArray(R.array.myinfo_account_manager_sexs);
		// 建立Adapter并且绑定数据源
		ArrayAdapter<String> sexAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.common_spinner_selected, sexItems);
		sexAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
		myinfoAccountManagerSexSpinner = (Spinner)findViewById(R.id.myinfoAccountManagerSexSpinner);
		myinfoAccountManagerSexSpinner.setAdapter(sexAdapter);
		myinfoAccountManagerAgeEditText = (EditText)findViewById(R.id.myinfoAccountManagerAgeEditText);
		myinfoAccountManagerJobTextView = (TextView)findViewById(R.id.myinfoAccountManagerJobTextView);
		myinfoAccountManagerInstructionEditText = (EditText)findViewById(R.id.myinfoAccountManagerInstructionEditText);
		myinfoAccountManagerLoveEditText = (EditText)findViewById(R.id.myinfoAccountManagerLoveEditText);
		myinfoAccountManagerClassEditText = (EditText)findViewById(R.id.myinfoAccountManagerClassEditText);
		myinfoAccountManagerAddressEditText = (EditText)findViewById(R.id.myinfoAccountManagerAddressEditText);
		
		myinfoAccountManagerUpdatePasswordTextView = (TextView)findViewById(R.id.myinfoAccountManagerUpdatePasswordTextView);
		myinfoAccountManagerUpdatePasswordTextView.setOnClickListener(this);
		
		
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		commonView = layoutInflater.inflate(R.layout.common_image_dialog, null);

		commonPhotographTextView = (TextView) commonView.findViewById(R.id.commonPhotographTextView);
		commonPhotographTextView.setOnClickListener(this);

		commonAlbumsTextView = (TextView) commonView.findViewById(R.id.commonAlbumsTextView);
		commonAlbumsTextView.setOnClickListener(this);

		commonCancelButton = (Button) commonView.findViewById(R.id.commonCancelButton);
		commonCancelButton.setOnClickListener(this);
//		job();
	}

	@Override
	public void initData() {
		intent = getIntent();
		search();
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
				/*if(task == jobHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if(jsonArray != null && jsonArray.size() > 0){
							initJob(jsonArray);
						}
					}else{
						ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
					}
				}*/
				if(task == searchHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if(jsonArray != null && jsonArray.size() > 0){
							initUserinfo(jsonArray);
						}
					}else{
						ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
					}
				}
				
				if(task == updateHttpTask){
					if (commonResult.validate()) {
						ToastUtil.toast(getApplicationContext(), R.string.myinfo_account_manager_update_success);
						SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_HEADER_IMAGE,myinfoAccountManagerHeaderPath);
						search();
					}else{
						ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
					}
				}
			}
		}
	}
	
	/*private void initJob(JSONArray jsonArray){
		jobList = new ArrayList<Job>();
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			Job job = JSONObject.toJavaObject(jsonObject, Job.class);
			jobList.add(job);
		if(jobList != null && jobList.size() > 0){
			jobId = jobList.get(0).getId();
			jobArrayAdapter = new ArrayAdapter<Job>(getApplicationContext(), R.layout.common_spinner_selected,jobList);
			jobArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
		}
			search();
	}*/
	
	private void initUserinfo(JSONArray jsonArray){
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		UserInfo userinfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);
		if(!StringUtil.isEmpty(userinfo.getImage())){
			myinfoAccountManagerHeaderPath = userinfo.getImage();
			SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_HEADER_IMAGE, myinfoAccountManagerHeaderPath);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.common_header_image)
					.showImageOnFail(R.drawable.common_header_image)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + myinfoAccountManagerHeaderPath, myinfoAccountManagerHeaderCircleImageView, options, new SimpleImageLoadingListener() {
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
		myinfoAccountManagerNameEditText.setText(userinfo.getUsName());
		myinfoAccountManagerAgeEditText.setText(userinfo.getUsAge());
		myinfoAccountManagerJobTextView.setText(userinfo.getRolename());
		myinfoAccountManagerInstructionEditText.setText(userinfo.getUsConstellation());
		myinfoAccountManagerLoveEditText.setText(userinfo.getUsPersonalNote());
		myinfoAccountManagerClassEditText.setText(userinfo.getClassname());
		myinfoAccountManagerAddressEditText.setText(userinfo.getUsAdderss());
		if(!StringUtil.isEmpty(userinfo.getUsSex()) && "男".equals(userinfo.getUsSex())){
			myinfoAccountManagerSexSpinner.setSelection(0);
		}else{
			myinfoAccountManagerSexSpinner.setSelection(1);
		}
		
	}
	
	/*
	 * 选择相片
	 */
	@SuppressWarnings("deprecation")
	private void choisePicture() {
		if (alertDialog == null) {
			alertDialog = new Dialog(this, R.style.commonDialog);
			alertDialog.setContentView(commonView);
			Display d = getWindowManager().getDefaultDisplay();
			ToastUtil.setDialogLocation(this, alertDialog, d.getWidth() - 60,d.getHeight() / 2 - 100, 30, d.getHeight() / 3);
		}

		if (alertDialog.isShowing()) {
			alertDialog.dismiss();
		} else {
			alertDialog.show();
		}
	}

	/*
	 * 拍照
	 */
	private void takePicture() {
		String name = "img_" + System.currentTimeMillis();
		imgFile = new File(StorageUtil.getTempDirectory(getApplicationContext()), name);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
		startActivityForResult(intent, TAKE_PICTURE);
	}

	/*
	 * 从相册获取
	 */
	private void fromPhotoAlbum() {
		Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
		openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
            	case CHOOSE_PICTURE:
            		ContentResolver resolver = getContentResolver();
            		//照片的原始资源地址
            		 Uri originalUri = data.getData();
					try {
						//使用ContentProvider通过URI获取原始图片
	            		 Bitmap smallBitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
	            		 initImageView(smallBitmap);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
            		break;
            	case TAKE_PICTURE:
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
		                options.inTempStorage = new byte[1024 * 1024 * 2];
		                options.inSampleSize = 2;
		                Bitmap smallBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
		                initImageView(smallBitmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            	break;
            }
		}
	}
	
	private void initImageView(Bitmap bitmap){
		picPath = null;
		if (bitmap != null) {
			Bitmap photo = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/5, bitmap.getHeight()/5);
    		//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
			bitmap.recycle();
            //将处理过的图片显示在界面上，并保存到本地
			myinfoAccountManagerHeaderCircleImageView.setImageBitmap(photo);
			String name = String.valueOf(System.currentTimeMillis()) + ".jpg";
			ImageUtil.savePhotoToSDCard(photo,ImageUtil.UPLOAD_ADD_BOOK, name);
			if(picPath == null){
				picPath = ImageUtil.UPLOAD_ADD_BOOK + name;
			}
			new Thread() {
				public void run() {
					Looper.prepare();
					try {
						String resultValue = UploadUtil.doUPloadFile(getApplicationContext(),picPath, ReleaseConfigure.COMMON_UPDATE_IMAGE);
						if(resultValue.equals("error")){
							ToastUtil.toast(getApplicationContext(), getResources().getString(R.string.common_albums));
						}else{
							Log.e(TAG, "resultValue"+resultValue);
							CommonResult commonResult = JSON.parseObject(resultValue, CommonResult.class);
							if(commonResult != null && commonResult.validate()){
								JSONArray jsonArray = JSON.parseArray(commonResult.getData());
								if(jsonArray != null && jsonArray.size() > 0){
									JSONObject jsonObject = jsonArray.getJSONObject(0);
									myinfoAccountManagerHeaderPath = jsonObject.getString(Constants.PATH);
									Log.e(TAG, "myinfoAccountManagerHeaderPath:" + myinfoAccountManagerHeaderPath);
								}
							}else{
								ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, e.getMessage());
					}
				}
			}.start();
		}
	}

}

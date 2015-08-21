package com.education.zhxy.chat.activity;

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
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
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
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.ImageUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StorageUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.education.zhxy.util.UploadUtil;

public class UploadWorkActivity extends BasicActivity {
	
	private static final String TAG = UploadWorkActivity.class.getSimpleName();
	
	private int type = 0;
	
	private HttpTask httpTask;
	
	private File imgFile;
	
	private String picPath;
	
	private String path;
	
	private Handler handler;
	
	private Intent intent;
	
	private static final int TAKE_PICTURE = 0;

	private static final int CHOOSE_PICTURE = 1;
	
	private View commonView;

	private Dialog alertDialog;
	
	private Button chatUploadWorkButton,commonCancelButton;
	
	private EditText chatUploadWorkEditText;

	private TextView chatUploadWorkBackTextView,commonPhotographTextView,commonAlbumsTextView;
	
	private String oneImagePath, twoImagePath, threeImagePath, fourImagePath,
			fiveImagePath;
	
	private ImageView chatUploadWorkOneImageView, chatUploadWorkTwoImageView,
			chatUploadWorkThreeImageView, chatUploadWorkFourImageView,
			chatUploadWorkFiveImageView;
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chatUploadWorkBackTextView:
				setResult(RESULT_OK, intent);
				this.finish();
				break;
			case R.id.chatUploadWorkOneImageView:
				type = 0;
				choisePicture();
				break;
			case R.id.chatUploadWorkTwoImageView:
				type = 1;
				choisePicture();
				break;
			case R.id.chatUploadWorkThreeImageView:
				type = 2;
				choisePicture();
				break;
			case R.id.chatUploadWorkFourImageView:
				type = 3;
				choisePicture();
				break;
			case R.id.chatUploadWorkFiveImageView:
				type = 4;
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
			case R.id.chatUploadWorkButton:
				if(validate()){
					uploadWork();
				}
				break;
		}
	}
	
	private boolean validate(){
		String content = chatUploadWorkEditText.getText().toString().trim();
		if(TextUtils.isEmpty(content)){
			ToastUtil.toast(getApplicationContext(), R.string.chat_upload_work_null_error);
			return false;
		}
		return true;
	}
	
	private void uploadWork() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.CLASSESID, String.valueOf(SharedPreferencesUtil.getClassId(getApplicationContext())));
		paramMap.put(Constants.WKCONTENT, chatUploadWorkEditText.getText().toString().trim());
		if(!StringUtil.isEmpty(oneImagePath)){
			paramMap.put(Constants.WKIMAGE, oneImagePath);
		}
		
		if(!StringUtil.isEmpty(twoImagePath)){
			paramMap.put(Constants.WKIMAGE1, twoImagePath);
		}
		
		if(!StringUtil.isEmpty(threeImagePath)){
			paramMap.put(Constants.WKIMAGE2, threeImagePath);
		}
		
		if(!StringUtil.isEmpty(fourImagePath)){
			paramMap.put(Constants.WKIMAGE3, fourImagePath);
		}
		
		if(!StringUtil.isEmpty(fiveImagePath)){
			paramMap.put(Constants.WKIMAGE4, fiveImagePath);
		}
		HttpParam httpParam = new HttpParam(ReleaseConfigure.CHAT_UPLOAD_WORK, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
	}

	@Override
	public int initLayout() {
		return R.layout.chat_upload_work;
	}

	@SuppressLint("InflateParams") @Override
	public void initUI() {
		chatUploadWorkBackTextView = (TextView)findViewById(R.id.chatUploadWorkBackTextView);
		chatUploadWorkBackTextView.setOnClickListener(this);
		
		chatUploadWorkEditText = (EditText)findViewById(R.id.chatUploadWorkEditText);
		
		chatUploadWorkOneImageView = (ImageView)findViewById(R.id.chatUploadWorkOneImageView);
		chatUploadWorkOneImageView.setOnClickListener(this);
		chatUploadWorkTwoImageView = (ImageView)findViewById(R.id.chatUploadWorkTwoImageView);
		chatUploadWorkTwoImageView.setOnClickListener(this);
		chatUploadWorkThreeImageView = (ImageView)findViewById(R.id.chatUploadWorkThreeImageView);
		chatUploadWorkThreeImageView.setOnClickListener(this);
		chatUploadWorkFourImageView = (ImageView)findViewById(R.id.chatUploadWorkFourImageView);
		chatUploadWorkFourImageView.setOnClickListener(this);
		chatUploadWorkFiveImageView = (ImageView)findViewById(R.id.chatUploadWorkFiveImageView);
		chatUploadWorkFiveImageView.setOnClickListener(this);
		
		chatUploadWorkTwoImageView.setVisibility(View.GONE);
		chatUploadWorkThreeImageView.setVisibility(View.GONE);
		chatUploadWorkFourImageView.setVisibility(View.GONE);
		chatUploadWorkFiveImageView.setVisibility(View.GONE);
		
		chatUploadWorkButton = (Button)findViewById(R.id.chatUploadWorkButton);
		chatUploadWorkButton.setOnClickListener(this);
		
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		commonView = layoutInflater.inflate(R.layout.common_image_dialog, null);

		commonPhotographTextView = (TextView) commonView.findViewById(R.id.commonPhotographTextView);
		commonPhotographTextView.setOnClickListener(this);

		commonAlbumsTextView = (TextView) commonView.findViewById(R.id.commonAlbumsTextView);
		commonAlbumsTextView.setOnClickListener(this);

		commonCancelButton = (Button) commonView.findViewById(R.id.commonCancelButton);
		commonCancelButton.setOnClickListener(this);
		
		handler = new Handler();
	}

	@Override
	public void initData() {
		intent = getIntent();
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
		Log.e(TAG, result.getData());
		if (result != null && !StringUtil.isEmpty(result.getData()) && StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if (commonResult.validate()) {
					setResult(RESULT_OK, intent);
					this.finish();
				}else{
					ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
				}
			}
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
			ToastUtil.setDialogLocation(this, alertDialog, d.getWidth() - 60,d.getHeight() / 2 - 150, 30, d.getHeight() / 3);
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
		if (bitmap != null) {
			picPath = null;
			Bitmap photo = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/5, bitmap.getHeight()/5);
    		//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
			bitmap.recycle();
            //将处理过的图片显示在界面上，并保存到本地
			switch (type) {
				case 0:
					chatUploadWorkOneImageView.setImageBitmap(photo);
					break;
				case 1:
					chatUploadWorkTwoImageView.setImageBitmap(photo);
					break;
				case 2:
					chatUploadWorkThreeImageView.setImageBitmap(photo);
					break;
				case 3:
					chatUploadWorkFourImageView.setImageBitmap(photo);
					break;
				case 4:
					chatUploadWorkFiveImageView.setImageBitmap(photo);
					break;
			}
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
									path = jsonObject.getString(Constants.PATH);
									Log.e(TAG, "path:" + path);
									if(!StringUtil.isEmpty(path)){
										handler.post(runnableUi);
									}
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
	
	// 构建Runnable对象，在runnable中更新界面
	Runnable runnableUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			switch (type) {
				case 0:
					oneImagePath = path;
					Log.e(TAG, "oneImagePath:" + oneImagePath);
					chatUploadWorkTwoImageView.setVisibility(View.VISIBLE);
					break;
				case 1:
					twoImagePath = path;
					Log.e(TAG, "twoImagePath:" + twoImagePath);
					chatUploadWorkThreeImageView.setVisibility(View.VISIBLE);
					break;
				case 2:
					threeImagePath = path;
					Log.e(TAG, "threeImagePath:" + threeImagePath);
					chatUploadWorkFourImageView.setVisibility(View.VISIBLE);
					break;
				case 3:
					fourImagePath = path;
					Log.e(TAG, "fourImagePath:" + fourImagePath);
					chatUploadWorkFiveImageView.setVisibility(View.VISIBLE);
					break;
				case 4:
					fiveImagePath = path;
					Log.e(TAG, "fiveImagePath:" + fiveImagePath);
					break;
			}
		}

	};

}

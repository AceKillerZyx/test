package com.education.zhxy.study.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.ImageUtil;
import com.education.zhxy.util.StorageUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.education.zhxy.util.UploadUtil;

public class AddBookActivity extends BasicActivity implements OnItemSelectedListener{
	
	private static final String TAG = AddBookActivity.class.getSimpleName();
	
	private File imgFile;
	
	private String picPath;
	
	private String studyAddBookPath;
	
	private static final int TAKE_PICTURE = 0;

	private static final int CHOOSE_PICTURE = 1;
	
	private View commonView;

	private Dialog alertDialog;
	
	private Intent intent;
	
	private HttpTask httpTask;
	
	private ImageView studyAddBookImageView;
	
	private Button studyAddBookButton,commonCancelButton;
	
	private LinearLayout studyAddBookGreadLinearLayout;

	private Spinner studyAddBookTypeSpinner, studyAddBookStageSpinner,
			studyAddBookGreadSpinner;

	private TextView studyAddBookBackTextView, commonPhotographTextView,
			commonAlbumsTextView, studyAddBookTimeTextView;
	
	private EditText studyAddBookNameEditText, studyAddBookAuthorEditText,
			studyAddBookPublishersEditText, studyAddBookInfoEditText;
	
	//日期选择
	
	private static final int DATE_DIALOG_ID = 1;  
	  
    private static final int SHOW_DATAPICK = 0;  
  
    private int mYear;  
  
    private int mMonth;  
  
    private int mDay;
    
    //自定义ProgressDialog
  	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.studyAddBookBackTextView:
				setResult(RESULT_OK, intent);
				this.finish();
				break;
			case R.id.studyAddBookImageView:
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
			case R.id.studyAddBookButton:
				
				if(validate()){
					addBook();
				}
				break;
			case R.id.studyAddBookTimeTextView:
				Message msg = new Message(); 
				msg.what = SHOW_DATAPICK;  
				saleHandler.sendMessage(msg); 
				break;
		}
	}
	
	private boolean validate(){
		
		if(StringUtil.isEmpty(studyAddBookPath)){
			ToastUtil.toast(getApplicationContext(), R.string.study_add_book_image_null_error);
			return false;
		}
		
		String name = studyAddBookNameEditText.getText().toString().trim();
		if(TextUtils.isEmpty(name)){
			ToastUtil.toast(getApplicationContext(), R.string.study_add_book_name_null_error);
			return false;
		}
		
		String author = studyAddBookAuthorEditText.getText().toString().trim();
		if(TextUtils.isEmpty(author)){
			ToastUtil.toast(getApplicationContext(), R.string.study_add_book_author_null_error);
			return false;
		}
		
		String publishers = studyAddBookPublishersEditText.getText().toString().trim();
		if(TextUtils.isEmpty(publishers)){
			ToastUtil.toast(getApplicationContext(), R.string.study_add_book_publishers_null_error);
			return false;
		}
		
		String info = studyAddBookInfoEditText.getText().toString().trim();
		if(TextUtils.isEmpty(info)){
			ToastUtil.toast(getApplicationContext(), R.string.study_add_book_info_null_error);
			return false;
		}
		return true;
	}

	@Override
	public int initLayout() {
		return R.layout.study_add_book;
	}

	@SuppressLint("InflateParams") @Override
	public void initUI() {
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
				
				
		studyAddBookBackTextView = (TextView)findViewById(R.id.studyAddBookBackTextView);
		studyAddBookBackTextView.setOnClickListener(this);
		
		studyAddBookImageView = (ImageView)findViewById(R.id.studyAddBookImageView);
		studyAddBookImageView.setOnClickListener(this);
		
		String[] typeItems = getResources().getStringArray(R.array.study_add_book_types);
		ArrayAdapter<String> typeAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.study_spinner_selected, typeItems);
		typeAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
		studyAddBookTypeSpinner = (Spinner)findViewById(R.id.studyAddBookTypeSpinner);
		studyAddBookTypeSpinner.setAdapter(typeAdapter);
		
		String[] stageItems = getResources().getStringArray(R.array.study_add_book_stages);
		ArrayAdapter<String> stageAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.study_spinner_selected, stageItems);
		stageAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
		studyAddBookStageSpinner = (Spinner)findViewById(R.id.studyAddBookStageSpinner);
		studyAddBookStageSpinner.setAdapter(stageAdapter);
		studyAddBookStageSpinner.setOnItemSelectedListener(this);
		
		studyAddBookGreadLinearLayout = (LinearLayout)findViewById(R.id.studyAddBookGreadLinearLayout);
		studyAddBookGreadLinearLayout.setVisibility(View.GONE);
		
		studyAddBookGreadSpinner = (Spinner)findViewById(R.id.studyAddBookGreadSpinner);
		
		studyAddBookNameEditText = (EditText)findViewById(R.id.studyAddBookNameEditText);
		studyAddBookAuthorEditText = (EditText)findViewById(R.id.studyAddBookAuthorEditText);
		studyAddBookTimeTextView = (TextView)findViewById(R.id.studyAddBookTimeTextView);
		studyAddBookTimeTextView.setOnClickListener(this);
		studyAddBookPublishersEditText = (EditText)findViewById(R.id.studyAddBookPublishersEditText);
		studyAddBookInfoEditText = (EditText)findViewById(R.id.studyAddBookInfoEditText);
		
		
		studyAddBookButton = (Button)findViewById(R.id.studyAddBookButton);
		studyAddBookButton.setOnClickListener(this);
		
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		commonView = layoutInflater.inflate(R.layout.common_image_dialog, null);

		commonPhotographTextView = (TextView) commonView.findViewById(R.id.commonPhotographTextView);
		commonPhotographTextView.setOnClickListener(this);

		commonAlbumsTextView = (TextView) commonView.findViewById(R.id.commonAlbumsTextView);
		commonAlbumsTextView.setOnClickListener(this);

		commonCancelButton = (Button) commonView.findViewById(R.id.commonCancelButton);
		commonCancelButton.setOnClickListener(this);
	}
	
	private void addBook() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.BOOKIMAGES,studyAddBookPath);
		paramMap.put(Constants.BOOKNAME,studyAddBookNameEditText.getText().toString().trim());
		paramMap.put(Constants.BOOKAUTHO,studyAddBookAuthorEditText.getText().toString().trim());
		paramMap.put(Constants.BOOKTYPE,String.valueOf(studyAddBookTypeSpinner.getSelectedItemPosition() + 1));
		paramMap.put(Constants.BOOKCLASSTYPEID,String.valueOf(studyAddBookStageSpinner.getSelectedItemPosition() + 1));
		int gread = 0;
		switch (studyAddBookStageSpinner.getSelectedItemPosition()) {
			case 0:
				gread = 0;
				break;
			case 1:
				gread = studyAddBookGreadSpinner.getSelectedItemPosition() + 1;
				break;
			case 2:
				gread = studyAddBookGreadSpinner.getSelectedItemPosition() + 7;
				break;
			case 3:
				gread = studyAddBookGreadSpinner.getSelectedItemPosition() + 10;
				break;
		}
		Log.e(TAG, "gread:" + gread);
		paramMap.put(Constants.BOOKGREAD,String.valueOf(gread));
		paramMap.put(Constants.BOOKINTRO,studyAddBookInfoEditText.getText().toString().trim());
		paramMap.put(Constants.BOOKCONCERN,studyAddBookPublishersEditText.getText().toString().trim());
		paramMap.put(Constants.BOOKDATES,studyAddBookTimeTextView.getText().toString().trim());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.STUDY_ADD_BOOK, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public void initData() {
		intent = getIntent();
		setDateTime();
	}
	
	private void setDateTime() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		updateDisplay();
	}

	/**
	 * 
	 * 更新日期
	 */

	private void updateDisplay() {
		studyAddBookTimeTextView.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-").append(
				(mDay < 10) ? "0" + mDay : mDay));

	}
	
	/** 
	 
     * 日期控件的事件 
 
     */  
  
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	
	@Override 
	protected Dialog onCreateDialog(int id) {
		if(id == DATE_DIALOG_ID){
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);
		}
		return null;
	};
	
	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if(id == DATE_DIALOG_ID){
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);  
		}
	}
	
	/**
	 * 处理日期控件的Handler
	 */
	@SuppressLint("HandlerLeak") 
	Handler saleHandler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == SHOW_DATAPICK){
				showDialog(DATE_DIALOG_ID);
			}
		}
	};
	
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
					ToastUtil.toast(getApplicationContext(), R.string.study_add_book_success);
					setResult(RESULT_OK, intent);
					this.finish();
				} else {
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
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
		picPath = null;
		if (bitmap != null) {
			Bitmap photo = ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()/5, bitmap.getHeight()/5);
    		//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
			bitmap.recycle();
            //将处理过的图片显示在界面上，并保存到本地
			studyAddBookImageView.setImageBitmap(photo);
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
									studyAddBookPath = jsonObject.getString(Constants.PATH);
									Log.e(TAG, "studyAddBookPath:" + studyAddBookPath);
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
		if(position == 0){
			studyAddBookGreadLinearLayout.setVisibility(View.GONE);
		}else{
			studyAddBookGreadLinearLayout.setVisibility(View.VISIBLE);
			String[] greadItems = null;
			switch (position) {
				case 1:
					greadItems =  getResources().getStringArray(R.array.study_add_book_gread_srimarys);
					break;
				case 2:
					greadItems =  getResources().getStringArray(R.array.study_add_book_gread_hunior_middles);
					break;
				case 3:
					greadItems =  getResources().getStringArray(R.array.study_add_book_gread_senior_middles);
					break;
			}
			ArrayAdapter<String> gradeAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.study_spinner_selected, greadItems);
			gradeAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
			studyAddBookGreadSpinner.setAdapter(gradeAdapter);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

}

package com.education.zhxy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.http.core.HttpListener;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
@SuppressLint({ "SdCardPath", "HandlerLeak", "InflateParams" }) @SuppressWarnings("unused")
public class UpdateManager implements HttpListener{
	private static final String TAG = UpdateManager.class.getSimpleName();
	
	private int type = 0;
	
	private Context mContext;
	
	private String updateMsg = "有最新的软件包哦，亲快下载吧~"; // 提示语

	private String apkUrl;  //返回的安装包url
	
	private int versionCode;  // 版本号
	
	private Dialog noticeDialog;
	
	private Dialog downloadDialog;
	
	private Dialog zuixinDialog;
	
	private ProgressDialog dialog;

	private static final String savePath = "/sdcard/updatedemo/";  //下载包安装路径

	private static final String saveFileName = savePath + "UpdateDemoRelease.apk";

	private ProgressBar mProgress;  //进度条与通知ui刷新的handler和msg常量

	private static final int DOWN_UPDATE = 1;
	
	private static final int DOWN_OVER = 2;
	
	private int progress;
	
	private Thread downLoadThread;
	
	private boolean interceptFlag = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context,int type) {
		this.mContext = context;
		this.type = type;
	}

	//外部接口让主Activity调用
	public void checkUpdateInfo() {
		jiazai();
	}

	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.proBarUpdate);
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}
	private void showzuixin(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage("您当前版本是最新版本，无需更新！");
		builder.setPositiveButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		zuixinDialog=builder.create();
		zuixinDialog.show();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		public void run() {
			try {
				URL url = new URL(ReleaseConfigure.BASICURL_DOWNAPK + apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					//更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						//下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);//点击取消就停止下载.
				Log.e(TAG, "mdownApkRunnable");
				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/*
	 *下载apk
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
		Log.e(TAG, "downloadApk");
	}

	/*
	 *  安装apk
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}

	private int getVersionCode(Context mcontext) {
		//获取软件版本号，对应AndroidManifest.xml下android:versionCode
		try {
			versionCode = mcontext.getPackageManager().getPackageInfo(mcontext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	private void jiazai() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(mContext);
		HttpParam httpParam = new HttpParam(ReleaseConfigure.ZHXY_APK_UPDATE, false); // get
		HttpTask httpTask = new HttpTask(mContext,this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
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
		if(result != null && result.getData() != null && StringUtil.isGoodJson(result.getData())){
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			versionCode = getVersionCode(mContext);
			if (commonResult.validate()) {
				JSONArray jsonArray = JSON.parseArray(commonResult.getData());
				if(jsonArray != null && jsonArray.size() > 0){
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					apkUrl = jsonObject.getString(Constants.ANDROIDURL);
					int versionnumber = jsonObject.getIntValue(Constants.VERSIONNUMBER);
					Log.e(TAG, "need to update apkUrl:" + apkUrl + "--versionnumber:" + versionnumber);
					if(versionnumber > versionCode){
						showNoticeDialog();
					}else{
						if(type == 1){
							showzuixin();
						}
					}
				}
			} else {
				ToastUtil.toast(mContext, commonResult.getErrMsg());
			}
		}
	}

}

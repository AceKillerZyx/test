package com.education.zhxy.easemob.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easemob.chat.EMChatConfig;
import com.easemob.chat.FileMessageBody;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.FileUtils;
import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;

public class ShowNormalFileActivity extends BasicActivity {
	private ProgressBar progressBar;
	private File file;

	@Override
	public void onClick(View arg0) {
		
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
		
	}

	@Override
	public int initLayout() {
		return R.layout.activity_show_file;
	}

	@Override
	public void initUI() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		final FileMessageBody messageBody = getIntent().getParcelableExtra("msgbody");
		file = new File(messageBody.getLocalUrl());
		//set head map
		final Map<String, String> maps = new HashMap<String, String>();
		if (!TextUtils.isEmpty(messageBody.getSecret())) {
			maps.put("share-secret", messageBody.getSecret());
		}
		//下载文件
		new Thread(new Runnable() {
			public void run() {
				HttpFileManager fileManager = new HttpFileManager(ShowNormalFileActivity.this, EMChatConfig.getInstance().getStorageUrl());
				fileManager.downloadFile(messageBody.getRemoteUrl(), messageBody.getLocalUrl(), maps,
						new CloudOperationCallback() {
					
							@Override
							public void onSuccess(String result) {
								runOnUiThread(new Runnable() {
									public void run() {
										FileUtils.openFile(file, ShowNormalFileActivity.this);
										finish();
									}
								});
							}
							
							@Override
							public void onProgress(final int progress) {
								runOnUiThread(new Runnable() {
									public void run() {
										progressBar.setProgress(progress);
									}
								});
							}
							
							@Override
							public void onError(final String msg) {
								runOnUiThread(new Runnable() {
									public void run() {
										if(file != null && file.exists()&&file.isFile())
											file.delete();
										String str4 = getResources().getString(R.string.Failed_to_download_file);
										Toast.makeText(ShowNormalFileActivity.this, str4+msg, Toast.LENGTH_SHORT).show();
										finish();
									}
								});
							}
						});

			}
		}).start();
	}

	@Override
	public void initData() {
		
	}
}

package com.education.zhxy.chat.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.adapter.HistoryWorkAdapter;
import com.education.zhxy.chat.data.bean.Work;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class HistoryWorkActivity extends BasicActivity implements OnItemClickListener{
	
	private static final String TAG = HistoryWorkActivity.class.getSimpleName();
	
	private TextView chatHistoryWorkBackTextView;
	
	private HistoryWorkAdapter historyWorkAdapter;
	
	private ListView chatHistoryWorkListView;
	
	private HttpTask httpTask;
	
	//自定义ProgressDialog
    private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chatHistoryWorkBackTextView:
				this.finish();
				break;
		}
	}
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.CLASSESID, String.valueOf(SharedPreferencesUtil.getClassId(getApplicationContext())));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.CHAT_HISTORY_WORK, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.chat_history_work;
	}

	@Override
	public void initUI() {
		chatHistoryWorkBackTextView = (TextView)findViewById(R.id.chatHistoryWorkBackTextView);
		chatHistoryWorkBackTextView.setOnClickListener(this);
		
		pd = new CustomProgressDialog(this);
		
		historyWorkAdapter = new HistoryWorkAdapter(getApplicationContext());
		chatHistoryWorkListView = (ListView)findViewById(R.id.chatHistoryWorkListView);
		chatHistoryWorkListView.setAdapter(historyWorkAdapter);
		chatHistoryWorkListView.setOnItemClickListener(this);
		
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
					if(jsonArray != null && jsonArray.size() > 0){
						initWork(jsonArray);
					}
				}else{
					ToastUtil.toast(getApplicationContext(), commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initWork(JSONArray jsonArray){
		List<Work> workList = new ArrayList<Work>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Work work = JSONObject.toJavaObject(jsonObject, Work.class);
			workList.add(work);
		}
		if(workList != null && workList.size() > 0){
			historyWorkAdapter.resetData(workList);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Intent intent = new Intent(this,HomeWorkActivity.class);
		intent.putExtra(Constants.WORK, (Work)parent.getAdapter().getItem(position));
		startActivity(intent);
	}

}

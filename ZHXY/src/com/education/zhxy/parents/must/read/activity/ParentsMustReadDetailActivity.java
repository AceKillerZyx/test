package com.education.zhxy.parents.must.read.activity;

import android.view.View;
import android.widget.TextView;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.parents.must.read.data.bean.ParentsRead;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class ParentsMustReadDetailActivity extends BasicActivity {
	
	private TextView parentsMustReadDetailBackTextView,
			parentsMustReadDetailNameTextView,
			parentsMustReadDetailContentTextView;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.parentsMustReadDetailBackTextView:
				this.finish();
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.parents_must_read_detail;
	}

	@Override
	public void initUI() {
		parentsMustReadDetailBackTextView = (TextView)findViewById(R.id.parentsMustReadDetailBackTextView);
		parentsMustReadDetailBackTextView.setOnClickListener(this);
		parentsMustReadDetailNameTextView = (TextView)findViewById(R.id.parentsMustReadDetailNameTextView);
		parentsMustReadDetailContentTextView = (TextView)findViewById(R.id.parentsMustReadDetailContentTextView);
	}

	@Override
	public void initData() {
		ParentsRead parentsRead = (ParentsRead)getIntent().getSerializableExtra(Constants.PARENTSREAD);
		if(parentsRead != null){
			parentsMustReadDetailNameTextView.setText(parentsRead.getNames());
			parentsMustReadDetailContentTextView.setText(parentsRead.getContent());
		}
	}
	
	@Override
	public void noNet(HttpTask task) {
		 ToastUtil.toast(getApplicationContext(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
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

	}
}

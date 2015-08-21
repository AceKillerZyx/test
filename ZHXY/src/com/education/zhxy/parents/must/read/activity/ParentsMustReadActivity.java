package com.education.zhxy.parents.must.read.activity;

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
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.parents.must.read.adapter.ParentsMustReadAdapter;
import com.education.zhxy.parents.must.read.data.bean.ParentsRead;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class ParentsMustReadActivity extends BasicActivity implements OnItemClickListener{
	
	private static final String TAG = ParentsMustReadActivity.class.getSimpleName();
	
	private int type = 1;
	
	private int classtype = 1;
	
	private HttpTask httpTask;
	
	private ParentsMustReadAdapter parentsMustReadAdapter;
	
	private ListView parentsMustReadListView;
	
	private View parentsMustReadOneView, parentsMustReadTwoView,
			parentsMustReadThreeView;
	
	private TextView parentsMustReadBackTextView,
			parentsMustReadEducateTextView, parentsMustReadStudyTextView,
			parentsMustReadNurseryTextView, parentsMustReadPrimaryTextView,
			parentsMustReadHuniorMiddleTextView,
			parentsMustReadSeniorMiddleTextView;
	
	private ArrayList<TextView> textViewList = new ArrayList<TextView>();
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.parentsMustReadBackTextView:
				this.finish();
				break;
			case R.id.parentsMustReadEducateTextView:
				type = 1;
				classtype = 1;
				setType(0);
				parentsMustReadNurseryTextView.setVisibility(View.VISIBLE);
				parentsMustReadEducateTextView.setSelected(true);
				parentsMustReadStudyTextView.setSelected(false);
				parentsMustReadEducateTextView.setTextColor(getResources().getColor(R.color.common_bg));
				parentsMustReadStudyTextView.setTextColor(getResources().getColor(R.color.common_text));
				search();
				break;
			case R.id.parentsMustReadStudyTextView:
				type = 2;
				classtype = 2;
				setType(1);
				parentsMustReadNurseryTextView.setVisibility(View.GONE);
				parentsMustReadEducateTextView.setSelected(false);
				parentsMustReadStudyTextView.setSelected(true);
				parentsMustReadEducateTextView.setTextColor(getResources().getColor(R.color.common_text));
				parentsMustReadStudyTextView.setTextColor(getResources().getColor(R.color.common_bg));
				search();
				break;
			case R.id.parentsMustReadNurseryTextView:
				classtype = 1;
				parentsMustReadOneView.setVisibility(View.GONE);
				parentsMustReadTwoView.setVisibility(View.VISIBLE);
				parentsMustReadThreeView.setVisibility(View.VISIBLE);
				setType(0);
				break;
			case R.id.parentsMustReadPrimaryTextView:
				parentsMustReadOneView.setVisibility(View.GONE);
				parentsMustReadTwoView.setVisibility(View.GONE);
				parentsMustReadThreeView.setVisibility(View.VISIBLE);
				classtype = 2;
				setType(1);
				break;
			case R.id.parentsMustReadHuniorMiddleTextView:
				classtype = 3;
				setType(2);
				parentsMustReadOneView.setVisibility(View.VISIBLE);
				parentsMustReadTwoView.setVisibility(View.GONE);
				parentsMustReadThreeView.setVisibility(View.GONE);
				break;
			case R.id.parentsMustReadSeniorMiddleTextView:
				classtype = 4;
				setType(3);
				parentsMustReadOneView.setVisibility(View.VISIBLE);
				parentsMustReadTwoView.setVisibility(View.VISIBLE);
				parentsMustReadThreeView.setVisibility(View.GONE);
				break;
		}
	}
	
	private void search() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.TYPEID, String.valueOf(type));
		paramMap.put(Constants.CLASSTYPEID, String.valueOf(classtype));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.PARENTS_MUST_READ_LIST, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.parents_must_read_main;
	}

	@Override
	public void initUI() {
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
		
		parentsMustReadBackTextView = (TextView)findViewById(R.id.parentsMustReadBackTextView);
		parentsMustReadBackTextView.setOnClickListener(this);
		parentsMustReadEducateTextView = (TextView)findViewById(R.id.parentsMustReadEducateTextView);
		parentsMustReadEducateTextView.setOnClickListener(this); 
		parentsMustReadStudyTextView = (TextView)findViewById(R.id.parentsMustReadStudyTextView);
		parentsMustReadStudyTextView.setOnClickListener(this); 
		parentsMustReadNurseryTextView = (TextView)findViewById(R.id.parentsMustReadNurseryTextView);
		parentsMustReadNurseryTextView.setOnClickListener(this); 
		textViewList.add(parentsMustReadNurseryTextView);
		parentsMustReadPrimaryTextView = (TextView)findViewById(R.id.parentsMustReadPrimaryTextView);
		parentsMustReadPrimaryTextView.setOnClickListener(this); 
		textViewList.add(parentsMustReadPrimaryTextView);
		parentsMustReadHuniorMiddleTextView = (TextView)findViewById(R.id.parentsMustReadHuniorMiddleTextView);
		parentsMustReadHuniorMiddleTextView.setOnClickListener(this); 
		textViewList.add(parentsMustReadHuniorMiddleTextView);
		parentsMustReadSeniorMiddleTextView = (TextView)findViewById(R.id.parentsMustReadSeniorMiddleTextView);
		parentsMustReadSeniorMiddleTextView.setOnClickListener(this);
		textViewList.add(parentsMustReadSeniorMiddleTextView);
		
		parentsMustReadAdapter = new ParentsMustReadAdapter(getApplicationContext());
		parentsMustReadListView = (ListView)findViewById(R.id.parentsMustReadListView);
		parentsMustReadListView.setAdapter(parentsMustReadAdapter);
		parentsMustReadListView.setOnItemClickListener(this);
		
		parentsMustReadOneView = (View)findViewById(R.id.parentsMustReadOneView);
		parentsMustReadTwoView = (View)findViewById(R.id.parentsMustReadTwoView);
		parentsMustReadThreeView = (View)findViewById(R.id.parentsMustReadThreeView);
		
		parentsMustReadEducateTextView.setSelected(true);
		parentsMustReadStudyTextView.setSelected(false);
		parentsMustReadEducateTextView.setTextColor(getResources().getColor(R.color.common_bg));
		parentsMustReadStudyTextView.setTextColor(getResources().getColor(R.color.common_text));
		
		parentsMustReadNurseryTextView.setSelected(true);
		parentsMustReadOneView.setVisibility(View.GONE);
		parentsMustReadTwoView.setVisibility(View.GONE);
		parentsMustReadThreeView.setVisibility(View.VISIBLE);
		search();
	}
	
	private void setType(int position) {
        int size = textViewList.size();
        for (int i = 0; i < size; i++) {
            textViewList.get(i).setSelected(i == position);
            if(textViewList.get(i).isSelected()){
            	textViewList.get(i).setTextColor(getResources().getColor(R.color.common_title_bg));
            }else{
            	textViewList.get(i).setTextColor(getResources().getColor(R.color.common_text));
            }
        }
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
					if (jsonArray != null && jsonArray.size() > 0) {
						initParentsMustRead(jsonArray);
					}else{
						parentsMustReadAdapter.resetData(null);
					}
				} else {
					parentsMustReadAdapter.resetData(null);
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initParentsMustRead(JSONArray jsonArray){
		List<ParentsRead> parentsReadList = new ArrayList<ParentsRead>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			ParentsRead parentsRead = JSONObject.toJavaObject(jsonObject, ParentsRead.class);
			parentsReadList.add(parentsRead);
		}
		
		if(parentsReadList != null && parentsReadList.size() > 0){
			parentsMustReadAdapter.resetData(parentsReadList);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this,ParentsMustReadDetailActivity.class);
		intent.putExtra(Constants.PARENTSREAD, (ParentsRead)arg0.getAdapter().getItem(arg2));
		startActivity(intent);
	}
}

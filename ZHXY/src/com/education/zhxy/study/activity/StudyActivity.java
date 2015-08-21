package com.education.zhxy.study.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
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
import com.education.zhxy.study.adapter.StudyAdapter;
import com.education.zhxy.study.data.bean.Book;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class StudyActivity extends BasicActivity implements OnItemClickListener{
	
	private static final String TAG = StudyActivity.class.getSimpleName();
	
	private int type = 1;
	
	private int classtype = 2;
	
	private HttpTask httpTask;
	
	private ListView studyListView;
	
	private StudyAdapter studyAdapter;
	
	private ImageView studyAddImageView;

	private TextView studyBackTextView, studySupplementaryTextView,
			studyExtracurricularTextView, studyNurseryTextView,
			studyPrimaryTextView, studyHuniorMiddleTextView,
			studySeniorMiddleTextView;
	
	private View studyOneView,studyTwoView,studyThreeView;
	
	private ArrayList<TextView> textViewList = new ArrayList<TextView>();
	
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.studyBackTextView:
				this.finish();
				break;
			case R.id.studyAddImageView:
				Intent intent = new Intent(this,AddBookActivity.class);
				startActivity(intent);
				break;
			case R.id.studySupplementaryTextView:
				type = 1;
				classtype = 2;
				setType(1);
				studyNurseryTextView.setVisibility(View.GONE);
				studySupplementaryTextView.setSelected(true);
				studyExtracurricularTextView.setSelected(false);
				studySupplementaryTextView.setTextColor(getResources().getColor(R.color.common_bg));
				studyExtracurricularTextView.setTextColor(getResources().getColor(R.color.common_text));
				studyList();
				break;
			case R.id.studyExtracurricularTextView:
				type = 2;
				classtype = 1;
				setType(0);
				studyNurseryTextView.setVisibility(View.VISIBLE);
				studySupplementaryTextView.setSelected(false);
				studyExtracurricularTextView.setSelected(true);
				studySupplementaryTextView.setTextColor(getResources().getColor(R.color.common_text));
				studyExtracurricularTextView.setTextColor(getResources().getColor(R.color.common_bg));
				studyList();
				break;
			case R.id.studyNurseryTextView:
				classtype = 1;
				setType(0);
				studyOneView.setVisibility(View.GONE);
				studyTwoView.setVisibility(View.VISIBLE);
				studyThreeView.setVisibility(View.VISIBLE);
				break;
			case R.id.studyPrimaryTextView:
				classtype = 2;
				setType(1);
				studyOneView.setVisibility(View.GONE);
				studyTwoView.setVisibility(View.GONE);
				studyThreeView.setVisibility(View.VISIBLE);
				break;
			case R.id.studyHuniorMiddleTextView:
				classtype = 3;
				setType(2);
				studyOneView.setVisibility(View.VISIBLE);
				studyTwoView.setVisibility(View.GONE);
				studyThreeView.setVisibility(View.GONE);
				break;
			case R.id.studySeniorMiddleTextView:
				classtype = 4;
				setType(3);
				studyOneView.setVisibility(View.VISIBLE);
				studyTwoView.setVisibility(View.VISIBLE);
				studyThreeView.setVisibility(View.GONE);
				break;
		}
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
        studyList();
    }

	@Override
	public int initLayout() {
		return R.layout.study_main;
	}

	@Override
	public void initUI() {
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
				
		studyBackTextView = (TextView)findViewById(R.id.studyBackTextView);
		studyBackTextView.setOnClickListener(this);
		
		studyAddImageView = (ImageView)findViewById(R.id.studyAddImageView);
		studyAddImageView.setOnClickListener(this);
		
		studySupplementaryTextView = (TextView)findViewById(R.id.studySupplementaryTextView);
		studySupplementaryTextView.setOnClickListener(this);
		studyExtracurricularTextView = (TextView)findViewById(R.id.studyExtracurricularTextView);
		studyExtracurricularTextView.setOnClickListener(this); 
		studyNurseryTextView = (TextView)findViewById(R.id.studyNurseryTextView);
		studyNurseryTextView.setOnClickListener(this);
		textViewList.add(studyNurseryTextView);
		studyPrimaryTextView = (TextView)findViewById(R.id.studyPrimaryTextView);
		studyPrimaryTextView.setOnClickListener(this);
		textViewList.add(studyPrimaryTextView);
		studyHuniorMiddleTextView = (TextView)findViewById(R.id.studyHuniorMiddleTextView);
		studyHuniorMiddleTextView.setOnClickListener(this);
		textViewList.add(studyHuniorMiddleTextView);
		studySeniorMiddleTextView = (TextView)findViewById(R.id.studySeniorMiddleTextView);
		studySeniorMiddleTextView.setOnClickListener(this);
		textViewList.add(studySeniorMiddleTextView);
		
		studyAdapter = new StudyAdapter(getApplicationContext());
		studyListView = (ListView)findViewById(R.id.studyListView);
		studyListView.setAdapter(studyAdapter);
		studyListView.setOnItemClickListener(this);
		
		studySupplementaryTextView.setSelected(true);
		studyExtracurricularTextView.setSelected(false);
		studySupplementaryTextView.setTextColor(getResources().getColor(R.color.common_bg));
		studyExtracurricularTextView.setTextColor(getResources().getColor(R.color.common_text));
		
		studyOneView = (View)findViewById(R.id.studyOneView);
		studyTwoView = (View)findViewById(R.id.studyTwoView);
		studyThreeView = (View)findViewById(R.id.studyThreeView);
		
		studyNurseryTextView.setVisibility(View.GONE);
		studyOneView.setVisibility(View.GONE);
		studyTwoView.setVisibility(View.GONE);
		studyThreeView.setVisibility(View.VISIBLE);
		studyPrimaryTextView.setSelected(true);
		
		studyList();
	}
	
	private void studyList() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.BOOKTYPE, String.valueOf(type));
		paramMap.put(Constants.BOOKCLASSTYPEID, String.valueOf(classtype));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.STUDY_LIST, false); // GET
		httpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		httpTask.execute(httpParam);
		pd.show();
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
						initStudy(jsonArray);
					}else{
						studyAdapter.resetData(null);
					}
				} else {
					studyAdapter.resetData(null);
					ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
				}
			}
		}
	}
	
	private void initStudy(JSONArray jsonArray){
		List<Book> bookList = new ArrayList<Book>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Book book = JSONObject.toJavaObject(jsonObject, Book.class);
			bookList.add(book);
		}
		if(bookList != null && bookList.size() > 0){
			studyAdapter.resetData(bookList);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this,StudyDetailActivity.class);
		intent.putExtra(Constants.BOOK, (Book)arg0.getAdapter().getItem(arg2));
		startActivity(intent);
	}

}

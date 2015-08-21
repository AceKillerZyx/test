package com.education.zhxy.myclass.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.home.activity.ZHXYActivity;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.myclass.data.bean.Classes;
import com.education.zhxy.myclass.data.bean.Grade;
import com.education.zhxy.myclass.data.bean.Prefectures;
import com.education.zhxy.myclass.data.bean.School;
import com.education.zhxy.myschool.activity.MySchoolActivity;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;

public class MyClassActivity extends BasicActivity{
	
	private static final String TAG = MyClassActivity.class.getSimpleName();
	
	private int type = 0;
	
	private Intent intent;
	
	private Button myclassChoiseButton;
	
	private List<Prefectures> prefecturesList;
	
	private List<School> schoolList;
	
	private List<Grade> gradeList;
	
	private List<Classes> classesList;
	
	private ArrayAdapter<Prefectures> prefecturesArrayAdapter;
	
	private ArrayAdapter<School> schoolArrayAdapter;
	
	private ArrayAdapter<Grade> gradeArrayAdapter;
	
	private ArrayAdapter<Classes> classesArrayAdapter;
	
	private int countid = 0;
	
	private int schoolid =0;
	
	private int gradeid =0;
	
	private int classid =0;
	
	private String prefecturesName,schoolName,gradeName,className;
	
	private HttpTask prefecturesHttpTask, schoolHttpTask, gradeHttpTask,
			classHttpTask, saveHttpTask;
	
	private Spinner myclassPrefecturesSpinner, myclassSchoolSpinner,
			myclassGradeSpinner, myclassClassSpinner;
	
//	private EditText myClassGradeEditText,myClassClassEditText;
	
	
	private TextView myClassBackTextView;
	
	private RelativeLayout myclassPrefecturesRelativeLayout,
			myclassSchoolRelativeLayout, myclassGradeRelativeLayout,
			myclassClassRelativeLayout;
	
	private String groupName = "";
	
	private String groupid = "";
	//自定义ProgressDialog
	private CustomProgressDialog pd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myClassBackTextView:
				this.finish();
				break;
			case R.id.myclassPrefecturesRelativeLayout:
				countid = 0;
				searchPrefectures();
				break;
			case R.id.myclassSchoolRelativeLayout:
				schoolid =0;
				searchSchool();
				break;
			case R.id.myclassGradeRelativeLayout:
				gradeid =0;
				searchGrade();
				break;
			case R.id.myclassClassRelativeLayout:
				classid =0;
				searchClass();
				break;
			case R.id.myclassChoiseButton:
				if(type != 2){
					save();
				}else{
					SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_SCHOOL_ID, schoolid);
					Intent mySchoolIntent = new Intent(this,MySchoolActivity.class);
					startActivity(mySchoolIntent);
					this.finish();
				}
				break;
		}
	}
	
	private void searchPrefectures() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		String city = SharedPreferencesUtil.getCity(getApplicationContext());
		if(StringUtil.isEmpty(city)){
			city = SharedPreferencesUtil.getLocationCity(getApplicationContext());
		}
		
		if(StringUtil.lastEqual(city, '市')){
			city = city.substring(0, city.length() - 1);
		}
		Log.e(TAG, "city:" + city);
		paramMap.put(Constants.CITY, city);
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYCLASS_PREFECTURES, false); // GET
		prefecturesHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		prefecturesHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void searchSchool() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.COUNTID, String.valueOf(countid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYCLASS_SCHOOL, false); // GET
		schoolHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		schoolHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void searchGrade() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.SCHOOLID, String.valueOf(schoolid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYCLASS_GRADE, false); // GET
		gradeHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		gradeHttpTask.execute(httpParam);
		pd.show();
	}
	private void searchClass() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.GRADEID, String.valueOf(gradeid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYCLASS_CLASS, false); // GET
		classHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		classHttpTask.execute(httpParam);
		pd.show();
	}
	
	private void save() {
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getApplicationContext());
		paramMap.put(Constants.CLASSID, String.valueOf(classid));
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MYCLASS_SAVE, false); // GET
		saveHttpTask = new HttpTask(getApplicationContext(), this);
		httpParam.setParams(paramMap);
		saveHttpTask.execute(httpParam);
		pd.show();
	}

	@Override
	public int initLayout() {
		return R.layout.myclass_main;
	}

	@SuppressLint("InflateParams") @Override
	public void initUI() {
		
		//创建ProgressDialog
		pd = CustomProgressDialog.createDialog(this);
				
		myClassBackTextView = (TextView)findViewById(R.id.myClassBackTextView);
		myClassBackTextView.setOnClickListener(this);
		
		myclassPrefecturesRelativeLayout = (RelativeLayout)findViewById(R.id.myclassPrefecturesRelativeLayout);
		myclassPrefecturesRelativeLayout.setOnClickListener(this);
		myclassSchoolRelativeLayout = (RelativeLayout)findViewById(R.id.myclassSchoolRelativeLayout);
		myclassSchoolRelativeLayout.setOnClickListener(this);
		myclassGradeRelativeLayout = (RelativeLayout)findViewById(R.id.myclassGradeRelativeLayout);
		myclassGradeRelativeLayout.setOnClickListener(this);
		myclassClassRelativeLayout = (RelativeLayout)findViewById(R.id.myclassClassRelativeLayout);
		myclassClassRelativeLayout.setOnClickListener(this);
		
		myclassPrefecturesSpinner = (Spinner)findViewById(R.id.myclassPrefecturesSpinner);
		myclassSchoolSpinner = (Spinner)findViewById(R.id.myclassSchoolSpinner);
		myclassGradeSpinner = (Spinner)findViewById(R.id.myclassGradeSpinner);
		myclassClassSpinner = (Spinner)findViewById(R.id.myclassClassSpinner);
		
		
		/*myClassGradeEditText=(EditText) findViewById(R.id.myClassGradeEditeText);
		myClassClassEditText=(EditText) findViewById(R.id.myClassClassEditeText);*/
		
		
		myclassChoiseButton = (Button)findViewById(R.id.myclassChoiseButton);
		myclassChoiseButton.setOnClickListener(this);
	}
	
	@Override
	public void initData() {
		intent = getIntent();
		type = intent.getIntExtra(Constants.TYPE, 0);
		Drawable drawable= getResources().getDrawable(R.drawable.common_title_back_selector);
		/// 这一步必须要做,否则不会显示.  
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		if(type == 0){
			myclassGradeRelativeLayout.setVisibility(View.VISIBLE);
			myclassClassRelativeLayout.setVisibility(View.VISIBLE);
			myClassBackTextView.setText(R.string.main_myclass);
		}else if(type == 1){
			myclassGradeRelativeLayout.setVisibility(View.VISIBLE);
			myclassClassRelativeLayout.setVisibility(View.VISIBLE);
			myClassBackTextView.setCompoundDrawables(drawable, null, null, null);
			myClassBackTextView.setText(R.string.main_myclass);
		}else{
			myclassGradeRelativeLayout.setVisibility(View.GONE);
			myclassClassRelativeLayout.setVisibility(View.GONE);
			myClassBackTextView.setCompoundDrawables(drawable, null, null, null);
			myClassBackTextView.setText(R.string.main_myschool);
		}
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
				if(task == prefecturesHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initPrefectures(jsonArray);
						}else{
							ToastUtil.toast(getApplicationContext(), R.string.common_not_data);
							countid = 0;
							prefecturesArrayAdapter = new ArrayAdapter<Prefectures>(getApplicationContext(), R.layout.common_spinner_selected,new ArrayList<Prefectures>());
							prefecturesArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
							myclassPrefecturesSpinner.setAdapter(prefecturesArrayAdapter);
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == schoolHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initSchool(jsonArray);
						}else{
							ToastUtil.toast(getApplicationContext(), R.string.common_not_data);
							schoolid = 0;
							schoolArrayAdapter = new ArrayAdapter<School>(getApplicationContext(), R.layout.common_spinner_selected,new ArrayList<School>());
							schoolArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
							myclassSchoolSpinner.setAdapter(schoolArrayAdapter);
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == gradeHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initGrade(jsonArray);
						}else{
							ToastUtil.toast(getApplicationContext(), R.string.common_not_data);
							gradeid = 0;
							gradeArrayAdapter = new ArrayAdapter<Grade>(getApplicationContext(), R.layout.common_spinner_selected,new ArrayList<Grade>());
							gradeArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
							myclassGradeSpinner.setAdapter(gradeArrayAdapter);
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == classHttpTask){
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initClass(jsonArray);
						}else{
							ToastUtil.toast(getApplicationContext(), R.string.common_not_data);
							classid = 0;
							classesArrayAdapter = new ArrayAdapter<Classes>(getApplicationContext(), R.layout.common_spinner_selected,new ArrayList<Classes>());
							classesArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
							myclassClassSpinner.setAdapter(classesArrayAdapter);
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
				if(task == saveHttpTask){
					if (commonResult.validate()) {
						SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_CLASS_ID, classid);
						SharedPreferencesUtil.putInt(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_SCHOOL_ID, schoolid);
						String city = SharedPreferencesUtil.getCity(getApplicationContext());
						if (StringUtil.isEmpty(city)) {
							city = SharedPreferencesUtil.getLocationCity(getApplicationContext());
						}

						if (!StringUtil.isEmpty(city) && StringUtil.lastEqual(city, '市')) {
							city = city.substring(0, city.length() - 1);
						}
						String name = city + prefecturesName + schoolName + gradeName+className;
						String shortname = gradeName+className;
						Log.e(TAG, "name:" + name);
						SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_CLASS_SHORT_NAME, shortname);
						SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_CLASS_NAME,name);
						switch (type) {
							case 0: //主页
								Intent zhxyIntent = new Intent(this,ZHXYActivity.class);
								startActivity(zhxyIntent);
								this.finish();
								break;
							case 1:  //聊天
								groupName = SharedPreferencesUtil.getClassName(getApplicationContext());
									Log.e(TAG, "groupName1:" + groupName);
									// 没群 && 班主任
									if ((!isHasGroup(groupName)) && SharedPreferencesUtil.getUserRole(getApplicationContext()) == 4) {
										// 建群
										createGroup();
										// 没群 && 不是班主任
									} else if (!isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getApplicationContext()) != 4) {
										ToastUtil.toast(getApplicationContext(), "你没有可加入的群！");
										// 有群 && 不是班主任 直接加入
									} else if (isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getApplicationContext()) != 4) {
										addContact();
										// 有群 && 是班主任  不重复创建 直接进入
									} else if (isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getApplicationContext()) == 4) {
										Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
										chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
										chatIntent.putExtra("groupId", groupid);
										chatIntent.putExtra("groupName",groupName);
										startActivity(chatIntent);
									}
								break;
							case 2: //我的学校
								Intent mySchoolIntent = new Intent(this,MySchoolActivity.class);
								startActivity(mySchoolIntent);
								this.finish();
								break;
						}
					} else {
						ToastUtil.toast(getApplicationContext(),commonResult.getErrMsg());
					}
				}
				
			}
		}
	}
	
	private void createGroup() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String desc = groupName + "群聊";
				String[] members = new String[] {};
				try {
					// 创建公开群，此种方式创建的群，可以自由加入
					// EMGroupManager.getInstance().createPublicGroup(groupName,
					// desc, members, false);
					// 创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
					Log.e(TAG, "groupName:" + groupName);
					if (isHasGroup(groupName)) {
						Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
						chatIntent.putExtra("chatType", Constants.CHATTYPE_GROUP);
						startActivity(chatIntent);
					} else {
						//创建公开群，不需要审核
						EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, false);
						runOnUiThread(new Runnable() {
							public void run() {
								ToastUtil.toast(getApplicationContext(), "创建群成功！");
								List<EMGroup> grouplist = EMGroupManager.getInstance().getAllGroups();
								if(grouplist != null && grouplist.size() > 0){
									for(int i = 0;i < grouplist.size();i++){
										EMGroup emGroup = grouplist.get(i);
										if(emGroup.getGroupName().equals(groupName)){
											groupid = emGroup.getGroupId();
										}
									}
								}
								Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
								chatIntent.putExtra("chatType", Constants.CHATTYPE_GROUP);
								chatIntent.putExtra("groupId", groupid);
								chatIntent.putExtra("groupName", groupName);
								startActivity(chatIntent);
							}
						});
					}
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							ToastUtil.toast(getApplicationContext(),R.string.Failed_to_create_groups + e.getLocalizedMessage());
						}
					});
				}

			}
		}).start();
	}
	
	private boolean isHasGroup(String groupName){
		List<EMGroup> grouplist = EMGroupManager.getInstance().getAllGroups();
		if(grouplist != null && grouplist.size() > 0){
			for(int i = 0;i < grouplist.size();i++){
				EMGroup emGroup = grouplist.get(i);
				if(emGroup.getGroupName().equals(groupName)){
					groupid = emGroup.getGroupId();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	private void addContact() {
		try {
			String name = SharedPreferencesUtil.getUsName(getApplicationContext());
			List<EMGroupInfo> groupsList = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
			if(groupsList != null && groupsList.size() > 0){
				for(int i = 0;i < groupsList.size();i++){
					EMGroupInfo emGroupInfo = groupsList.get(i);
					String groupname = emGroupInfo.getGroupName();
					if(groupname.equals(groupName)){
						groupid = emGroupInfo.getGroupId();
					}
				}
				if(isHasContact(groupid, name)){
					Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
					chatIntent.putExtra("chatType", Constants.CHATTYPE_GROUP);
					chatIntent.putExtra("groupId", groupid);
					chatIntent.putExtra("groupName", groupName);
					startActivity(chatIntent);
				}else{
					new Thread(new Runnable() {
						public void run() {
							try {
								final EMGroup group = EMGroupManager.getInstance().getGroupFromServer(groupid);
								//如果是membersOnly的群，需要申请加入，不能直接join
								/*if(group.isMembersOnly()){
									EMGroupManager.getInstance().applyJoinToGroup(groupid, getResources().getString(R.string.Request_to_join));
								}else{*/
									EMGroupManager.getInstance().joinGroup(groupid);
//								}
								runOnUiThread(new Runnable() {
									public void run() {
										if(group.isMembersOnly())
											ToastUtil.toast(getApplicationContext(), R.string.send_the_request_is);
										else
											ToastUtil.toast(getApplicationContext(), R.string.Join_the_group_chat);
											Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
											chatIntent.putExtra("chatType", Constants.CHATTYPE_GROUP);
											chatIntent.putExtra("groupId", groupid);
											chatIntent.putExtra("groupName", groupName);
											startActivity(chatIntent);
									}
								});
							} catch (final EaseMobException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										ToastUtil.toast(getApplicationContext(), R.string.Failed_to_join_the_group_chat +e.getMessage());
									}
								});
							}
						}
					}).start();
				}
			}
		} catch (EaseMobException e1) {
			e1.printStackTrace();
		}
	}
	
	private boolean isHasContact(String groupid,String name){
		EMGroup emGroup= EMGroupManager.getInstance().getGroup(groupid);
		List<String> memberList =  emGroup.getMembers();
		if(memberList != null && memberList.size() > 0){
			for(int i = 0;i < memberList.size();i++){
				if(name.equals(memberList.get(i))){
					return true;
				}
			}
		}
		return false;
	}
	
	private void initPrefectures(JSONArray jsonArray){
		prefecturesList = new ArrayList<Prefectures>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Prefectures prefectures = JSONObject.toJavaObject(jsonObject, Prefectures.class);
			prefecturesList.add(prefectures);
		}
		if(prefecturesList != null && prefecturesList.size() > 0){
			countid = prefecturesList.get(0).getId();
			prefecturesName = prefecturesList.get(0).getName();
			prefecturesArrayAdapter = new ArrayAdapter<Prefectures>(getApplicationContext(), R.layout.common_spinner_selected,prefecturesList);
			prefecturesArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
			myclassPrefecturesSpinner.setAdapter(prefecturesArrayAdapter);
			myclassPrefecturesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
					countid = ((Prefectures)(myclassPrefecturesSpinner.getSelectedItem())).getId();
					prefecturesName = ((Prefectures)(myclassPrefecturesSpinner.getSelectedItem())).getName();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
		}
	}
	
	private void initSchool(JSONArray jsonArray){
		schoolList = new ArrayList<School>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			School school = JSONObject.toJavaObject(jsonObject, School.class);
			schoolList.add(school);
		}
		if(schoolList != null && schoolList.size() > 0){
			schoolid = schoolList.get(0).getId();
			schoolName = schoolList.get(0).getStuNames();
			schoolArrayAdapter = new ArrayAdapter<School>(getApplicationContext(), R.layout.common_spinner_selected,schoolList);
			schoolArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
			myclassSchoolSpinner.setAdapter(schoolArrayAdapter);
			myclassSchoolSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
					schoolid = ((School)(myclassSchoolSpinner.getSelectedItem())).getId();
					schoolName = ((School)(myclassSchoolSpinner.getSelectedItem())).getStuNames();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
		}
	}
	
	private void initGrade(JSONArray jsonArray){
		gradeList = new ArrayList<Grade>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Grade grade = JSONObject.toJavaObject(jsonObject, Grade.class);
			gradeList.add(grade);
		}
		if(gradeList != null && gradeList.size() > 0){
			gradeid = gradeList.get(0).getId();
			gradeName = gradeList.get(0).getGradeName();
			gradeArrayAdapter = new ArrayAdapter<Grade>(getApplicationContext(), R.layout.common_spinner_selected,gradeList);
			gradeArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
			myclassGradeSpinner.setAdapter(gradeArrayAdapter);
			myclassGradeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
					gradeid = ((Grade)(myclassGradeSpinner.getSelectedItem())).getId();
					gradeName = ((Grade)(myclassGradeSpinner.getSelectedItem())).getGradeName();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
		}
	}
	
	private void initClass(JSONArray jsonArray){
		classesList = new ArrayList<Classes>();
		for(int i = 0;i < jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Classes classes = JSONObject.toJavaObject(jsonObject, Classes.class);
			classesList.add(classes);
		}
		if(classesList != null && classesList.size() > 0){
			classid = classesList.get(0).getId();
			className = classesList.get(0).getClassName();
			classesArrayAdapter = new ArrayAdapter<Classes>(getApplicationContext(), R.layout.common_spinner_selected,classesList);
			classesArrayAdapter.setDropDownViewResource(R.layout.common_spinner_dropdown);
			myclassClassSpinner.setAdapter(classesArrayAdapter);
			myclassClassSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
					classid = ((Classes)(myclassClassSpinner.getSelectedItem())).getId();
					className = ((Classes)(myclassClassSpinner.getSelectedItem())).getClassName();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
		}
	}

}

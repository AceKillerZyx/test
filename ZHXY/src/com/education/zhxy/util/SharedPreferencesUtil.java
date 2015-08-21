package com.education.zhxy.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences帮助类
 * 
 */
public class SharedPreferencesUtil {

	public static final String APP_INFO_FILE_NAME = "app_info"; // app相关信息文件

	public static final String APP_INFO_IS_FIRST_IN = "is_first_in"; // app是否第一次进入
	
	public static final String USER_INFO_FILE_NAME = "user_info"; // 用户相关信息文件
	
	public static final String USER_INFO_KEY_USID = "usid"; // 用户ID

	public static final String USER_INFO_KEY_USNAME = "us_name"; // 用户名（手机号）
	
	public static final String USER_INFO_KEY_USER_ROLE = "user_role"; // 用户角色
	
	public static final String USER_INFO_KEY_HEADER_IMAGE = "header_image"; // 用户头像
	
	public static final String USER_INFO_KEY_LOCATION_CITY= "location_city"; //定位城市
	
	public static final String USER_INFO_KEY_LOCATION_ADDRESS= "location_address"; //定位地址
	
	public static final String USER_INFO_KEY_CITY= "city"; //城市
	
	public static final String USER_INFO_KEY_CLASS_ID= "classid"; //班级ID
	
	
	public static final String USER_INFO_KEY_CHAT_ID2= "classid"; //私聊ID2
	
	public static final String USER_INFO_KEY_SCHOOL_ID= "schoolid"; //学校ID
	
	public static final String USER_INFO_KEY_CLASS_NAME= "className"; //班级名称
	
	public static final String USER_INFO_KEY_CLASS_SHORT_NAME = "class_short_name"; //班级短名称
	
	public static final String USER_INFO_KEY_MUSIC= "music"; //音乐播放控制
	
	// 判断是否第一次进入应用
	public static boolean isFirstIn(Context context) {
		return getBooleanValue(context, APP_INFO_FILE_NAME,APP_INFO_IS_FIRST_IN);
	}
	
	// 控制音乐播放
	public static boolean isStartMusic(Context context) {
		return getBooleanValue(context, USER_INFO_FILE_NAME,USER_INFO_KEY_MUSIC);
	}

	// 获取用户ID
	public static int getUsid(Context context) {
		return getIntValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_USID);
	}
	
	// 获取用户名称
	public static String getUsName(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_USNAME);
	}
	
	// 获取用户角色
	public static int getUserRole(Context context) {
		return getIntValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_USER_ROLE);
	}
	
	// 获取用户头像
	public static String getHeaderImage(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_HEADER_IMAGE);
	}

	// 获取定位城市
	public static String getLocationCity(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_LOCATION_CITY);
	}
	
	// 获取定位地址
	public static String getLocationAddress(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_LOCATION_ADDRESS);
	}
	
	// 获取城市
	public static String getCity(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_CITY);
	}
	
	// 获取学校ID
	public static int getSchoolId(Context context) {
		return getIntValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_SCHOOL_ID);
	}
	
	// 获取班级ID
	public static int getClassId(Context context) {
		return getIntValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_CLASS_ID);
	}
	// 获取私聊ID2
	public static int getUsid2(Context context) {
		return getIntValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_CLASS_ID);
	}
	
	// 获取班级名称
	public static String getClassName(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_CLASS_NAME);
	}
	
	// 获取班级显示名称
	public static String getClassShortName(Context context) {
		return getStringValue(context, USER_INFO_FILE_NAME, USER_INFO_KEY_CLASS_SHORT_NAME);
	}
	
	public static void putString(Context context, String fileName, String key,String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void putInt(Context context, String fileName, String key,
			int value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void putFloat(Context context, String fileName, String key,
			float value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				fileName, Context.MODE_PRIVATE).edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public static void putLong(Context context, String fileName, String key,long value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void putBoolean(Context context, String fileName, String key,
			boolean value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				fileName, Context.MODE_PRIVATE).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static String getStringValue(Context context, String fileName,
			String key) {
		return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
				.getString(key, StringUtil.EMPTY);
	}

	public static int getIntValue(Context context, String fileName, String key) {
		return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
				.getInt(key, 0);
	}

	public static float getFloatValue(Context context, String fileName,
			String key) {
		return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
				.getFloat(key, 0);
	}
	
	public static long getLongValue(Context context, String fileName,String key) {
		return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getLong(key, 0);
	}

	public static boolean getBooleanValue(Context context, String fileName,String key) {
		return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
				.getBoolean(key, true);
	}

	public static void emptyFile(Context context, String fileName) {
		context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().commit();
	}

}
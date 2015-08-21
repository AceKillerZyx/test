package com.education.zhxy.util;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.education.zhxy.Constants;

public class CommonDataUtil {
	
	private static HashMap<String, String> commonParams = new HashMap<String, String>();
	
	/**
     * 获取请求公共参数
     * @param context
     * @return
     */
    public static HashMap<String, String> getCommonParams(Context context) {
        if (null != context) {
            commonParams.put(Constants.USID,String.valueOf(SharedPreferencesUtil.getUsid(context)));   //用户ID
            Log.e("usid:", String.valueOf(SharedPreferencesUtil.getUsid(context)));
        }
        return commonParams;
    }
}

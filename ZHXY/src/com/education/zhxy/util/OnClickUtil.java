package com.education.zhxy.util;

public class OnClickUtil {
	    private static long lastClickTime;
	    public synchronized static boolean isFastClick() {
	        long time = System.currentTimeMillis();   
	        if ( time - lastClickTime < 3000) {   
	            return true;   
	        }
	        lastClickTime = time;   
	        return false;   
	    }
	}

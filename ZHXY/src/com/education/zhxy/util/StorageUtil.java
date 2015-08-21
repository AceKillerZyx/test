package com.education.zhxy.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class StorageUtil {
    
    private static final String ANDROID = "Android";
	
	private static final String DATA = "data";
	
	public static final String DIR_TMP = "tmp";
	
	public static File getTempDirectory(Context context) {
		return getDirectory(context, DIR_TMP);
	}
	
	public static File getDirectory(Context context, String sub) {
		File targetFileDir = null, rootFileDir = null;
		if (hasSDCard()) {
			rootFileDir = new File(new File(Environment.getExternalStorageDirectory(), ANDROID), DATA);
		} else {
			rootFileDir = context.getFilesDir();
		}
		
		targetFileDir = new File(new File(rootFileDir, context.getPackageName()), sub);
		if (! targetFileDir.exists() && ! targetFileDir.mkdirs()) {
			return null;
		} else {
			return targetFileDir;
		}
	}
	
	public static boolean hasSDCard() {
	    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

}

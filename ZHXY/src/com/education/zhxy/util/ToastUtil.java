package com.education.zhxy.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ToastUtil {
	public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
    
    public static void toast(Context context, int text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 设置dialog显示位置
     * @param activity
     * @param alertDialog
     * @return
     */
    public static void setDialogLocation(Activity activity,Dialog alertDialog,int width,int height,int x,int y){
    	Window dialogWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width; // 宽度
        lp.height = height;
        lp.x = x;
        lp.y = y;
        dialogWindow.setGravity(Gravity.TOP|Gravity.RIGHT);
        dialogWindow.setAttributes(lp);
    }
}

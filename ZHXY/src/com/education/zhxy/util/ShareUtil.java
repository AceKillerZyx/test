package com.education.zhxy.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;


public class ShareUtil {
    
    /**
     * 分享至Sina
     * @param context
     * @param title
     * @param content
     * @param webUrl
     * @param bitmap
     */
    public static void shareToSina(Activity activity, String title, String content, String webUrl, Bitmap bitmap) {
    	// 创建微博分享接口实例
    	IWeiboShareAPI weiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, Constants.SINA_CONSUMER_KEY);
        if (weiboShareAPI.isWeiboAppInstalled()) {
            weiboShareAPI.registerApp();
            if (weiboShareAPI.isWeiboAppSupportAPI() && weiboShareAPI.getWeiboAppSupportAPI() >= 10351) {
                TextObject textObject = new TextObject();
                textObject.text = title + content + webUrl;
                ImageObject imageObject = new ImageObject();

                if (null == bitmap) {
                    bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.zhxy_logo);
                }

                imageObject.setImageObject(bitmap);
                //初始化微博的分享消息
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.mediaObject = imageObject;
                weiboMessage.textObject = textObject;
                //初始化从第三方到微博的消息请求
                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                // 用transaction唯一标识一个请求
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMessage;
                //发送请求消息到微博，唤起微博分享界面
                weiboShareAPI.sendRequest(activity,request);
                activity.finish();
            } else {
                ToastUtil.toast(activity, R.string.chat_sina_sdk_error); // 客户端不支持sdk
            }
        } else {
            ToastUtil.toast(activity, R.string.chat_no_sina_app); // 未安装客户端
        }
    }

}

package com.education.zhxy.chat.activity;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.common.activity.BasicActivity;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.util.ShareUtil;
import com.education.zhxy.util.ToastUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareActivity extends BasicActivity implements IWXAPIEventHandler{
	
	private static final String TITLE = "智慧校园";
	
	private static final String DESCRIPTION = "爸妈提高孩子学习的神器！";

	private static final String URL = "http://115.28.148.135:8080/zhxy/apk/ZHXY.apk";
	
	private IWXAPI api;
	
	private Button chatShareWXButton, chatShareWXFrinedButton,
			chatShareSinaButton, chatShareCancelButton;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.chatShareCancelButton:
				this.finish();
				break;
			case R.id.chatShareWXButton:
				shareToWeixin(0);//朋友圈
				break;
			case R.id.chatShareWXFrinedButton:
				shareToWeixin(1);//朋友
				break;
			case R.id.chatShareSinaButton:
				ShareUtil.shareToSina(this, TITLE,DESCRIPTION, URL, null);
				break;
		}
	}

	@Override
	public int initLayout() {
		return R.layout.chat_share_dialog;
	}

	@SuppressWarnings("static-access")
	@Override
	public void initUI() {
		Window window = getWindow();  
        WindowManager.LayoutParams layoutParams = window.getAttributes();  
//        Display d = getWindowManager().getDefaultDisplay();
        //设置窗口的大小及透明度  
        layoutParams.width = LayoutParams.MATCH_PARENT;  
        layoutParams.height = layoutParams.WRAP_CONTENT;  
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);  
        
        chatShareWXButton = (Button)findViewById(R.id.chatShareWXButton);
        chatShareWXButton.setOnClickListener(this);
        
        chatShareWXFrinedButton = (Button)findViewById(R.id.chatShareWXFrinedButton);
        chatShareWXFrinedButton.setOnClickListener(this);
        
        chatShareSinaButton = (Button)findViewById(R.id.chatShareSinaButton);
        chatShareSinaButton.setOnClickListener(this);
		
        chatShareCancelButton = (Button)findViewById(R.id.chatShareCancelButton);
        chatShareCancelButton.setOnClickListener(this);
	}

	@Override
	public void initData() {
		api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID, true);
        api.registerApp(Constants.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
	}
	
	@Override
	public void noNet(HttpTask task) {

	}

	@Override
	public void noData(HttpTask task, HttpResult result) {

	}

	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {

	}

	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {

	}
	
	private void shareToWeixin(int type) {
		WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = URL;  // 点击跳转的地址。
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = TITLE;  // 链接标题
        msg.description = DESCRIPTION;  // 链接内容
        msg.thumbData = bmpToByteArray(getDefaultBitmap(getResources()));

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		if(type == 0){
        	req.scene = SendMessageToWX.Req.WXSceneTimeline; //分享到朋友圈
        }else{
        	req.scene = SendMessageToWX.Req.WXSceneSession; //分享给好友
        }
		// 调用api接口发送数据到微信
		api.sendReq(req);
		this.finish();
    }
	
	private byte[] bmpToByteArray(Bitmap bmp) {
        byte[] result = null;
        if (null != bmp) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bmp.compress(CompressFormat.JPEG, 5, output);
            result = output.toByteArray();
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
	
	private Bitmap getDefaultBitmap(Resources res) {
        return BitmapFactory.decodeResource(res, R.drawable.zhxy_logo);
    }
    
    private String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	setIntent(intent);
        api.handleIntent(intent, this);
    }

	@Override
	public void onReq(BaseReq arg0) {
		
	}

	@Override
	public void onResp(BaseResp arg0) {
		int result = R.string.chat_share_success;
        switch (arg0.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.chat_share_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.chat_share_failed;
                break;
            default:
                break;
        }
        ToastUtil.toast(getApplicationContext(), result);
	}

}

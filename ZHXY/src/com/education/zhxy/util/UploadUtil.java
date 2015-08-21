package com.education.zhxy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 
 * 上传工具类
 * @author
 * 支持上传文件和参数
 */
public class UploadUtil {
	
	private static final String TAG = UploadUtil.class.getSimpleName();
	
	/**
	 * 上传图片
	 * @param context,picPath,url
	 */
	public static String doUPloadFile(Context context,String picPath,String url) throws Exception {
		String result = "";
    	// 需要上传的文件
        File file = new File(picPath);
        FileInputStream fis = new FileInputStream(file);
        HttpClient httpclient = createClient();
 
        MultipartEntity me = new MultipartEntity();        //需要下载第三方jar包(httpmime.jar)
        me.addPart("file", new InputStreamBody(fis, file.getName()));
 
        Log.e(TAG, "url"+url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(me);
        HttpResponse httpResponse = httpclient.execute(httpPost);
 
        int code = httpResponse.getStatusLine().getStatusCode();
        Log.e(TAG,"http status code:" + code);
        if (code == HttpStatus.SC_OK) {
            result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
            // 上传的结果，可以使json，或者是返回上传后文件的的url
            Log.e(TAG,"result = " + result);
            Log.e(TAG,"上传成功");
        }else{
        	result = "error";
        	Log.e(TAG,"上传失败");
        }
        return result;
    }
    
	/**
	 * 创建HttpClient
	 */
    public static HttpClient createClient(){
    	HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.DEFAULT_CONTENT_CHARSET);
        params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, true);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);
 
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
 
        return new DefaultHttpClient(conMgr, params);
    }
    
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return InputStream 
     * @throws Exception 
     */  
    public static InputStream getImageStream(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return conn.getInputStream();  
        }  
        return null;  
    }  
    
    /** 
     * 从网络上下载图片 
     */  
    public static Bitmap downloadPicture(Context context,String url) {
    	Bitmap bitmap = null;
    	HttpClient httpclient = createClient();  
        HttpGet httpget = new HttpGet(url);  
        try {  
            HttpResponse resp = httpclient.execute(httpget);  
            if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {  
                HttpEntity entity = resp.getEntity();  
                InputStream in  = entity.getContent();  
                bitmap = BitmapFactory.decodeStream(in);
            }else{
            	bitmap = null;
            }
        } catch (Exception e) {  
            e.printStackTrace();  
            bitmap = null;
        } finally {  
            httpclient.getConnectionManager().shutdown();  
        }  
        return bitmap;
    }  
  
}  
	

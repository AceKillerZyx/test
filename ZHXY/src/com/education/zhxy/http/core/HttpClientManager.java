package com.education.zhxy.http.core;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.Uri;

public class HttpClientManager {
	private static final int TIMEOUT = 10000;

    private static final int TIMEOUT_SOCKET = 15000;

    public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

	public static HttpContext clientContext = null;

	public static HttpContext getClientContext(Context context) {
		clientContext = new BasicHttpContext();
		return clientContext;
	}

	public static HttpClient getNewInstance(Context mContext) {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        ConnManagerParams.setTimeout(params, 5000); //设置连接管理器的超时
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);//设置连接超时
        HttpConnectionParams.setSoTimeout(params, TIMEOUT_SOCKET);//设置请求超时

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        //HttpClient newInstance = new DefaultHttpClient(conMgr, params);
        DefaultHttpClient newInstance = new DefaultHttpClient(conMgr, params);
        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(3,false);
        newInstance.setHttpRequestRetryHandler(retryHandler);
        
//        switch (NetUtil.checkNetType(mContext)) {
//            case NetUtil.CTWAP: {
//                //通过代理解决中国移动联通GPRS中wap无法访问的问题
//                HttpHost proxy = new HttpHost("10.0.0.200", 80, "http");
//                newInstance.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//            }
//                break;
//            case NetUtil.CMWAP: {
//                //通过代理解决中国移动联通GPRS中wap无法访问的问题
//                HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
//                newInstance.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//            }
//                break;
//        }
        return newInstance;
    }
}

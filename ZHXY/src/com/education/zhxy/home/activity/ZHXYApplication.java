package com.education.zhxy.home.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.easemob.EMCallBack;
import com.education.zhxy.Constants;
import com.education.zhxy.easemob.domain.User;
import com.education.zhxy.easemob.helper.ZHXYHXSDKHelper;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressLint("HandlerLeak") 
/*
 * 如果您的工程中实现了Application的继承类，那么，您需要将父类改为com.baidu.frontia.FrontiaApplication。
 * 如果您没有实现Application的继承类，那么，请在AndroidManifest.xml的Application标签中增加属性： 
 * <application android:name="com.baidu.frontia.FrontiaApplication"
 * 。。。
 */
public class ZHXYApplication extends FrontiaApplication {
	private static final String TAG = ZHXYApplication.class.getSimpleName();
	// 百度定位
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";
	private int frequence = 1000 * 60 * 60 * 24; // 定位自动刷新平率
	private Double lat, lon;
	private String address,address_province, address_city, address_code;
	public static final int FLAG = 1;
	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	
	
	//环信
	public static Context applicationContext;
	private static ZHXYApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static ZHXYHXSDKHelper hxSDKHelper = new ZHXYHXSDKHelper();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FLAG:
				Log.e(TAG, "定位省份：" + address_province + "--" + "城市：" + address_city + "--" + "城市代码：" + address_code + "--地址：" + address);
				SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_LOCATION_CITY, address_city);
				SharedPreferencesUtil.putString(getApplicationContext(), SharedPreferencesUtil.USER_INFO_FILE_NAME, SharedPreferencesUtil.USER_INFO_KEY_LOCATION_ADDRESS, address);
				break;
			}
		}
	};

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		if (Constants.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}

		super.onCreate();
		initImageLoader(getApplicationContext());
		
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();
		mLocationClient.start();
		
		applicationContext = this;
        instance = this;

        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         * 
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         * for example:
         * 例子：
         * 
         * public class DemoHXSDKHelper extends HXSDKHelper
         * 
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
        hxSDKHelper.onInit(applicationContext);
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	public class MyLocationListener implements BDLocationListener {
		
		public void onReceiveLocation(BDLocation location) {
			lat = location.getLatitude();
			lon = location.getLongitude();
			Log.e("定位结果坐标：", lat + "|" + lon);
			baiduAutoLocation();
		}
	}
	
	public void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setCoorType(tempcoor);
		option.setScanSpan(frequence);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	public void baiduAutoLocation(){
		// 省份-城市-城市代码
		new AsyncTask<String, Void, Void>() {
			StringBuilder builder = new StringBuilder(1000000);
			@Override
			protected Void doInBackground(String... params) {
				try {
					URL url = new URL(params[0]);
					URLConnection connection = url.openConnection();
					InputStream is = connection.getInputStream();
					InputStreamReader isr = new InputStreamReader(is,"utf-8");
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						builder.append(line);
					}
					String data = builder.toString().substring(builder.toString().indexOf("(") + 1, builder.toString().length() - 1);
					Log.e(TAG, "builder string" + data);
					JSONObject jo = JSON.parseObject(data);
					JSONObject result = jo.getJSONObject("result");
					address_code = result.getString("cityCode");
					address = result.getString("formatted_address");
					JSONObject addressComponent = result.getJSONObject("addressComponent");
					address_province = addressComponent.getString("province");
					address_city = addressComponent.getString("city");
					Log.e("省份：", address_province);
					Log.e("城市：", address_city);
					Log.e("城市代码：", address_code);
					Log.e("地址：", address);
					if (jo != null) {
						Message msg = new Message();
						msg.what = FLAG;
						mHandler.sendMessage(msg);
					} 
				}catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute("http://api.map.baidu.com/geocoder/v2/?ak=S3oZa4bjxxCXr58LYeAQZDpm&callback=renderReverse&location="+ lat + ","+ lon + "&output=json&pois=0");
	}
	
	public static ZHXYApplication getInstance() {
		return instance;
	}
 
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
	    return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
	    hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(emCallBack);
	}
	
}

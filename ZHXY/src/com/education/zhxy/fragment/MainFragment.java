package com.education.zhxy.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.chat.activity.ChatActivity;
import com.education.zhxy.chat.activity.ChatListActivity;
import com.education.zhxy.common.activity.ChatNotifcation;
import com.education.zhxy.common.activity.DetailsActivity;
import com.education.zhxy.common.data.bean.CityModel;
import com.education.zhxy.common.data.bean.CommonResult;
import com.education.zhxy.common.view.CustomProgressDialog;
import com.education.zhxy.common.view.MyLetterListView;
import com.education.zhxy.common.view.MyLetterListView.OnTouchingLetterChangedListener;
import com.education.zhxy.fragment.adapter.HotNewsAdapter;
import com.education.zhxy.fragment.data.bean.MainAd;
import com.education.zhxy.fragment.data.bean.MainDay;
import com.education.zhxy.fragment.data.bean.MainNews;
import com.education.zhxy.http.core.HttpListener;
import com.education.zhxy.http.core.HttpParam;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;
import com.education.zhxy.love.activity.LoveActivity;
import com.education.zhxy.myclass.activity.MyClassActivity;
import com.education.zhxy.myschool.activity.MySchoolActivity;
import com.education.zhxy.parents.must.read.activity.ParentsMustReadActivity;
import com.education.zhxy.study.activity.StudyActivity;
import com.education.zhxy.train.activity.TrainActivity;
import com.education.zhxy.util.CommonDataUtil;
import com.education.zhxy.util.DBManager;
import com.education.zhxy.util.OnClickUtil;
import com.education.zhxy.util.SharedPreferencesUtil;
import com.education.zhxy.util.StringUtil;
import com.education.zhxy.util.ToastUtil;
import com.education.zhxy.util.UpdateManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

@SuppressLint("HandlerLeak")
public class MainFragment<NewMessageBroadcastReceiver> extends Fragment implements HttpListener,
		OnClickListener ,ChatNotifcation{

	private static final String TAG = MainFragment.class.getSimpleName();

	private HttpTask mainAdHttpTask, mainNewsHttpTask, mainDayHttpTask;

	private ImageView[] imageViews = null;

	private ImageView imageView = null;
	
	/*
	 * 标记红点用到的
	 */
	private ImageView mainMyClassRedImageView;//班级沟通 红点
	private ImageView mainMySchoolRedImageView;//我的学校 红点
	private boolean isReceiver = false;
	private NewMessageBroadcastReceiver receiver;
	private	int unreadMsgCountTotal;
	
	private UpdateManager updateManager;

	//private FixedSpeedScroller mScroller;// 滚动速度

	private AtomicInteger what = new AtomicInteger(0);

	private boolean isContinue = true;

	private ViewPager mainViewPager;

	private LinearLayout mainLinearLayout;// 圆点容器

	private ArrayList<View> imagePageViews = null;

	//private LinearLayout newsLinearLayout;

	private DisplayImageOptions options;


	private TextView mainLocationTextView, mainMySchoolTextView,
			mainMyclassTextView, mainLoveTextView, mainTrainTextView,
			/* mainHeartTextView */mainParentsMustReadTextView,
			mainStudyTextView, mainEnglishTextView, mainChinaTextView;

	private View commonView;

	private Dialog alertDialog;

	private BaseAdapter adapter;
	private ListView mCityLit;
	private TextView overlay, mainCityLocationTextView;
	private MyLetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private SQLiteDatabase database;
	private ArrayList<CityModel> mCityNames;
	private EditText et;
	private LinearLayout mainCityLocationLinearLayout;

	private RelativeLayout mainRelativeLayout;

	// 音乐
	/*
	 * private ImageView mainMusicImageView; private Intent musicIntent;
	 */

	private String groupName = "";

	private String groupid = "";

	// 自定义ProgressDialog
	private CustomProgressDialog pd = null;
	
		//搞个数组来做listview的数据
		//private String []  array={"第一条新闻","第二条新闻","第三条新闻","第四条新闻","第五条新闻"};
	    // ArrayList<HashMap<String,Object>> listItem=new ArrayList<HashMap<String,Object>>();
	
		/*
		 * 热点新闻
		 * 开个线程来控制Listview自动的改编数据
		 */
	 	 private ListView hotNewsListView;
		 List<MainNews> newsList;
		 HotNewsAdapter hotNewsAdapter;
		 private TextView hotNewsNullTextView;
	    
		private int index = 0;
		Animation a=null;
		Runnable r = new Runnable(){
			@Override
			public void run() {
				
					if (index >= newsList.size()) {
						index = 0;
					}
					if(index < newsList.size()){
						hotNewsListView.setSelection(index);
						hotNewsListView.startAnimation(a);
						index++;
						new Handler().postDelayed(r, 3000);
					}
			}
		};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.main_fragment, container, false);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 创建ProgressDialog
		pd = CustomProgressDialog.createDialog(getActivity());
		
		updateManager = new UpdateManager(getActivity(), 0);
		updateManager.checkUpdateInfo();

		mainRelativeLayout = (RelativeLayout) getView().findViewById(
				R.id.mainRelativeLayout);

		mainLocationTextView = (TextView) getView().findViewById(
				R.id.mainLocationTextView);
		mainLocationTextView.setOnClickListener(this);
		
		/*ArrayList<HashMap<String,Object>> listItem=new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < array.length; i++) {
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("hotnew",array[i]);
			listItem.add(map);
		}*/
		newsList=new ArrayList<MainNews>();
		hotNewsListView=(ListView) getView().findViewById(R.id.hotNewsListView);
		//SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(), listItem, R.layout.hot_news_item, new String[]{"hotnew"},new int[]{R.id.hotNewsItemTextVIew});
		hotNewsAdapter=new HotNewsAdapter(getActivity(), newsList);
		Log.e("newList的值为：",""+ newsList.size());
		hotNewsListView.setAdapter(hotNewsAdapter);
		//条目单击后执行的操作
		hotNewsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent=new Intent(getActivity(),DetailsActivity.class);
				intent.putExtra(Constants.MAIN_NEWS, newsList.get(position));
				startActivity(intent);
			}
		});
		//暂无新闻控件
		hotNewsNullTextView=(TextView) getView().findViewById(R.id.hotNewsNullTextView);
		/*
		 * musicIntent = new Intent(getActivity(),MusicService.class);
		 * mainMusicImageView =
		 * (ImageView)getView().findViewById(R.id.mainMusicImageView);
		 * mainMusicImageView.setOnClickListener(this);
		 * if(!SharedPreferencesUtil.isStartMusic(getActivity())){
		 * getActivity().startService(musicIntent);
		 * mainMusicImageView.setBackgroundResource
		 * (R.drawable.main_title_music_start); }else{
		 * getActivity().stopService(musicIntent);
		 * mainMusicImageView.setBackgroundResource
		 * (R.drawable.main_title_music_stop); }
		 */
		a=AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
		new Handler().postDelayed(r, 3000);
		/*
		 *  注册接收消息广播
		 *  标记红点用到的
		 */
				receiver = new NewMessageBroadcastReceiver();
				IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
				// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
				intentFilter.setPriority(5);
				getActivity().registerReceiver(receiver, intentFilter);
		
		
		if (StringUtil.isEmpty(SharedPreferencesUtil.getCity(getActivity()))) {
			if (StringUtil.isEmpty(SharedPreferencesUtil
					.getLocationCity(getActivity()))) {
				mainLocationTextView.setText(R.string.main_choise_city);
			} else {
				mainLocationTextView.setText(SharedPreferencesUtil
						.getLocationCity(getActivity()));
			}
		} else {
			mainLocationTextView.setText(SharedPreferencesUtil
					.getCity(getActivity()));
		}

		if (!mainLocationTextView.getText().toString().trim()
				.endsWith(getString(R.string.main_choise_city))) {
			SharedPreferencesUtil.putString(getActivity(),
					SharedPreferencesUtil.USER_INFO_FILE_NAME,
					SharedPreferencesUtil.USER_INFO_KEY_CITY,
					mainLocationTextView.getText().toString().trim());
		}
		mainViewPager = (ViewPager) getView().findViewById(R.id.mainViewPager);

		/*newsAutoScrollView = (AutoScrollView) getView().findViewById(
				R.id.newsAutoScrollView);
		newsLinearLayout = (LinearLayout) getView().findViewById(
				R.id.newsLinearLayout);*/
		mainLinearLayout = (LinearLayout) getView().findViewById(
				R.id.mainLinearLayout);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.common_viewpager_bg)
				.showImageOnFail(R.drawable.common_viewpager_bg)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		mainMySchoolTextView = (TextView) getView().findViewById(
				R.id.mainMySchoolTextView);
		mainMySchoolTextView.setOnClickListener(this);
		mainMyclassTextView = (TextView) getView().findViewById(
				R.id.mainMyclassTextView);
		mainMyclassTextView.setOnClickListener(this);
		mainLoveTextView = (TextView) getView().findViewById(
				R.id.mainLoveTextView);
		mainLoveTextView.setOnClickListener(this);
		mainTrainTextView = (TextView) getView().findViewById(
				R.id.mainTrainTextView);
		mainTrainTextView.setOnClickListener(this);
		/*
		 * mainHeartTextView =
		 * (TextView)getView().findViewById(R.id.mainHeartTextView);
		 * mainHeartTextView.setOnClickListener(this);
		 */
		mainParentsMustReadTextView = (TextView) getView().findViewById(
				R.id.mainParentsMustReadTextView);
		mainParentsMustReadTextView.setOnClickListener(this);
		mainStudyTextView = (TextView) getView().findViewById(
				R.id.mainStudyTextView);
		mainStudyTextView.setOnClickListener(this);
		mainEnglishTextView = (TextView) getView().findViewById(
				R.id.mainEnglishTextView);
		mainChinaTextView = (TextView) getView().findViewById(
				R.id.mainChinaTextView);

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		commonView = layoutInflater.inflate(R.layout.main_city_dialog, null);
		mainCityLocationLinearLayout = (LinearLayout) commonView
				.findViewById(R.id.mainCityLocationLinearLayout);
		mainCityLocationLinearLayout.setOnClickListener(this);
		mainCityLocationTextView = (TextView) commonView
				.findViewById(R.id.mainCityLocationTextView);
		et = (EditText) commonView.findViewById(R.id.et);
		et.addTextChangedListener(textWatcher);
		overlay = (TextView) commonView.findViewById(R.id.overlay);
		mCityLit = (ListView) commonView.findViewById(R.id.city_list);
		letterListView = (MyLetterListView) commonView
				.findViewById(R.id.cityLetterListView);
		DBManager dbManager = new DBManager(getActivity());
		dbManager.openDateBase();
		dbManager.closeDatabase();
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
		mCityNames = getCityNames();
		// database.close();
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		// initOverlay();
		setAdapter(mCityNames);
		mCityLit.setOnItemClickListener(new CityListOnItemClick());

		groupName = SharedPreferencesUtil.getClassName(getActivity());
		
		//红点空间初始化
		mainMyClassRedImageView=(ImageView) getView().findViewById(R.id.mainMyClassRedImageView);
		mainMySchoolRedImageView=(ImageView) getView().findViewById(R.id.mainMySchoolRedImageView);
		
		mainAd();
		mainNews();
		mainDay();
	}

	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			String searchInfo = et.getText().toString().trim();
			if (!StringUtil.isEmpty(searchInfo)) {
				mCityNames.clear();
				mCityNames = getSelectCityNames(searchInfo);
				setAdapter(mCityNames);
			} else {
				mCityNames = getCityNames();
				setAdapter(mCityNames);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};

	@SuppressWarnings("deprecation")
	private void choiseCity() {
		if (StringUtil.isEmpty(SharedPreferencesUtil.getLocationCity(getActivity()))) {
			mainCityLocationTextView.setText(R.string.main_locationing);
		} else {
			mainCityLocationTextView.setText(SharedPreferencesUtil.getLocationCity(getActivity()));
		}
		if (alertDialog == null) {
			alertDialog = new Dialog(getActivity(), R.style.commonDialog);
			alertDialog.setContentView(commonView);
			Display d = getActivity().getWindowManager().getDefaultDisplay();
			Rect rect = new Rect();
			Window window = getActivity().getWindow();
			mainRelativeLayout.getWindowVisibleDisplayFrame(rect);
			// 标题栏跟状态栏的总体高度
			int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT)
					.getTop();
			// 标题栏的高度：用上面的值减去状态栏的高度及为标题栏高度
			int titleBarHeight = contentViewTop + 75;
			ToastUtil.setDialogLocation(getActivity(), alertDialog,
					d.getWidth(), d.getHeight() - titleBarHeight, 0,
					titleBarHeight);
		}

		if (alertDialog.isShowing()) {
			alertDialog.dismiss();
		} else {
			alertDialog.show();
		}
	}

	/*@Override
	public void onStart() {
		if (!newsAutoScrollView.isScrolled()) {
			newsAutoScrollView.requestFocus();
			newsAutoScrollView.setScrolled(true);
		}
		super.onStart();
	}

	@Override
	public void onStop() {
		if (newsAutoScrollView.isScrolled()) {
			newsAutoScrollView.setScrolled(false);
		}
		super.onStop();
	}*/

	/**
	 * 初始化图片
	 */
	private void initViewPager(final JSONArray jsonArray) {
		imagePageViews = new ArrayList<View>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			final MainAd mainAd = JSONObject.toJavaObject(jsonObject,
					MainAd.class);
			if (getActivity() != null && !getActivity().isFinishing()) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse(mainAd.getAdvHtp());
						intent.setData(content_url);
						startActivity(intent);
					}
				});
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				ImageLoader.getInstance().displayImage(
						ReleaseConfigure.ROOT_IMAGE + mainAd.getAdvImage(),
						imageView, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "Input/Output error";
									break;
								case DECODING_ERROR:
									message = "Image can't be decoded";
									break;
								case NETWORK_DENIED:
									message = "Downloads are denied";
									break;
								case OUT_OF_MEMORY:
									message = "Out Of Memory error";
									break;
								case UNKNOWN:
									message = "Unknown error";
									break;
								}
								Log.e(TAG, message);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
							}
						});
				imagePageViews.add(imageView);
			}
		}
		removeView();
		// 对imageviews进行填充
		imageViews = new ImageView[imagePageViews.size()];
		// 小图标
		for (int i = 0; i < imagePageViews.size(); i++) {
			imageView = new ImageView(getActivity());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(12, 12);
			lp.setMargins(5, 2, 5, 5);
			imageView.setLayoutParams(lp);
			imageViews[i] = imageView;
			if (i == 0) {
				imageViews[i]
						.setBackgroundResource(R.drawable.common_dot_focused);
			} else {
				imageViews[i]
						.setBackgroundResource(R.drawable.common_dot_normal);
			}
			mainLinearLayout.addView(imageViews[i]);
		}

		mainViewPager.setAdapter(new AdvAdapter(imagePageViews));
		/*try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new FixedSpeedScroller(mainViewPager.getContext(),new AccelerateInterpolator(0.5f));
			mField.set(mainViewPager, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		mainViewPager.setOnPageChangeListener(new GuidePageChangeListener());
		mainViewPager.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isContinue = false;
				case MotionEvent.ACTION_MOVE:
					isContinue = false;
					break;
				case MotionEvent.ACTION_UP:
					isContinue = true;
					break;
				}
				return false;
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						whatOption();
						viewHandler.sendEmptyMessage(what.get());
					}
				}
			}

		}).start();
	}

	private void whatOption() {
		what.incrementAndGet();
		if (what.get() > imageViews.length - 1) {
			what.getAndAdd(-4);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			isContinue = false;
		}
	}

	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mainViewPager.setCurrentItem(msg.what);
		}

	};

	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			what.getAndSet(arg0);
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0]
						.setBackgroundResource(R.drawable.common_dot_focused);
				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.common_dot_normal);
				}
			}

		}

	}

	private final class AdvAdapter extends PagerAdapter {
		private List<View> views = null;

		public AdvAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

	private void mainAd() {
		Log.e("导航图","---");
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		String city = SharedPreferencesUtil.getCity(getActivity());
		if (StringUtil.isEmpty(city)) {
			city = SharedPreferencesUtil.getLocationCity(getActivity());
		}
		if (!StringUtil.isEmpty(city) && StringUtil.lastEqual(city, '市')) {
			city = city.substring(0, city.length() - 1);
		}
		Log.e(TAG, "city:" + city);
		paramMap.put("type", "5");
		paramMap.put(Constants.CITY, city);	
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MAIN_NEWS, false); // GET
		mainAdHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		mainAdHttpTask.execute(httpParam);
		pd.show();
	}

	private void mainNews() {
		Log.e("新闻","--");
		HashMap<String, String> paramMap = CommonDataUtil.getCommonParams(getActivity());
		paramMap.put("type", "1");
		paramMap.put("city", "");
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MAIN_NEWS, false); // GET
		mainNewsHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		mainNewsHttpTask.execute(httpParam);
		pd.show();
	}

	private void mainDay() {
		Log.e("每日一句", "--");
		HashMap<String, String> paramMap = CommonDataUtil
				.getCommonParams(getActivity());
		HttpParam httpParam = new HttpParam(ReleaseConfigure.MAIN_DAY, false); // GET
		mainDayHttpTask = new HttpTask(getActivity(), this);
		httpParam.setParams(paramMap);
		mainDayHttpTask.execute(httpParam);
//		pd.show();
	}

	@Override
	public void noNet(HttpTask task) {
		 ToastUtil.toast(getActivity(), R.string.common_no_net);
	}
	
	@Override
	public void noData(HttpTask task, HttpResult result) {
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getActivity(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getActivity(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getActivity(), R.string.common_no_data);
		}  	
	}
	
	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {
		if (!StringUtil.isEmpty(result.getData())) {
    		if (StringUtil.isEmpty(result.getErrorMsg())) {
        		ToastUtil.toast(getActivity(), R.string.common_no_data);
    		}else{
    			ToastUtil.toast(getActivity(), result.getErrorMsg());
    		}
		}else {
			ToastUtil.toast(getActivity(), R.string.common_no_data);
		}  
	}

	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {
		Log.e(TAG, result.getData());
		pd.dismiss();
		if (result != null && !StringUtil.isEmpty(result.getData())&& StringUtil.isGoodJson(result.getData())) {
			CommonResult commonResult = JSON.parseObject(result.getData(),CommonResult.class);
			if (null != commonResult) {
				if (task == mainAdHttpTask) {
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initViewPager(jsonArray);
						} else {
							mainViewPager.setAdapter(null);
						}
					} else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),
									commonResult.getErrMsg());
							mainViewPager.setAdapter(null);
						}
					}
				}

				if (task == mainNewsHttpTask) {
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							hotNewsListView.setVisibility(View.VISIBLE);
							hotNewsNullTextView.setVisibility(View.GONE);
							initNews(jsonArray);
						}else {
							hotNewsListView.setVisibility(View.GONE);
							hotNewsNullTextView.setVisibility(View.VISIBLE);
							
						}
					} else {
						if (getActivity() != null && !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),commonResult.getErrMsg());
						}
					}
				}

				if (task == mainDayHttpTask) {
					if (commonResult.validate()) {
						JSONArray jsonArray = JSON.parseArray(commonResult
								.getData());
						if (jsonArray != null && jsonArray.size() > 0) {
							initDay(jsonArray);
						}
					} else {
						if (getActivity() != null
								&& !getActivity().isFinishing()) {
							ToastUtil.toast(getActivity(),
									commonResult.getErrMsg());
						}
					}
				}
			}
		}
	}

	private void initDay(JSONArray jsonArray) {
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		MainDay mainDay = JSONObject.toJavaObject(jsonObject, MainDay.class);
		mainEnglishTextView.setText(mainDay.getEnglish());
		mainChinaTextView.setText(mainDay.getTranslate());
	}

	private void initNews(JSONArray jsonArray) {
		if (getActivity() != null && !getActivity().isFinishing()) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				MainNews mainNews = JSONObject.toJavaObject(jsonObject,MainNews.class);
				Log.e("地址为:", mainNews.getUrl());
				newsList.add(mainNews);
			}
		}
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainMySchoolTextView:
			pd.show();
			if (OnClickUtil.isFastClick()) {
		        return ;
		    }
			if (SharedPreferencesUtil.getSchoolId(getActivity()) == 0) {
				Intent myClassIntent = new Intent(getActivity(),MyClassActivity.class);
				myClassIntent.putExtra(Constants.TYPE, 2);
				pd.dismiss();
				startActivity(myClassIntent);
			} else {
				Intent mySchoolIntent = new Intent(getActivity(),MySchoolActivity.class);
				pd.dismiss();
				startActivity(mySchoolIntent);
			}
			break;
		case R.id.mainMyclassTextView:
			Intent intent=new Intent(getActivity(),ChatListActivity.class);
			mainMyClassRedImageView.setVisibility(View.INVISIBLE);
			unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
			unreadMsgCountTotal=-1;
			Log.e("单击之后unreadMsgCountTotal的值为:",""+unreadMsgCountTotal);
			startActivity(intent);
			/*pd.dismiss();
			if (OnClickUtil.isFastClick()) {
		        return ;
		    } 
			System.out.println("是否有群？" + isHasGroup(groupName) +"|"+ groupName);
			if (SharedPreferencesUtil.getClassId(getActivity()) == 0) {
				Intent myClassIntent = new Intent(getActivity(),MyClassActivity.class);
				myClassIntent.putExtra(Constants.TYPE, 1);
				myClassIntent.putExtra("groupName", groupName);
				pd.dismiss();
				startActivity(myClassIntent);
				Log.e(TAG, "groupName:" + groupName);
			} else {
				// 没群 && 班主任s
				if ((!isHasGroup(groupName)) && SharedPreferencesUtil.getUserRole(getActivity()) == 4) {
					// 建群
					createGroup();
					// 没群 && 不是班主任
				} else if (!isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getActivity()) != 4) {
					ToastUtil.toast(getActivity(), "你没有可加入的群！");
					pd.dismiss();
					// 有群 && 不是班主任 直接加入
				} else if (isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getActivity()) != 4) {
					addContact();
					// 有群 && 是班主任  不重复创建 直接进入
				} else if (isHasGroup(groupName) && SharedPreferencesUtil.getUserRole(getActivity()) == 4) {
					Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
					//Intent intent=new Intent(getActivity(),ChatListActivity.class);
					chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
					chatIntent.putExtra("groupId", groupid);
					chatIntent.putExtra("groupName",groupName);
					pd.dismiss();
					startActivity(chatIntent);
				}
			}*/
			break;
		case R.id.mainLoveTextView:
			pd.show();
			if (OnClickUtil.isFastClick()) {
		        return ;
		    }
			Intent loveIntent = new Intent(getActivity(), LoveActivity.class);
			pd.dismiss();
			startActivity(loveIntent);
			break;
		case R.id.mainTrainTextView:
			pd.show();
			if (OnClickUtil.isFastClick()) {
		        return ;
		    }
			Intent trainIntent = new Intent(getActivity(), TrainActivity.class);
			pd.dismiss();
			startActivity(trainIntent);
			break;
		/*
		 * case R.id.mainHeartTextView: Intent heartIntent = new
		 * Intent(getActivity(),HeartActivity.class);
		 * startActivity(heartIntent); break;
		 */
		case R.id.mainParentsMustReadTextView:
			pd.show();
			if (OnClickUtil.isFastClick()) {
		        return ;
		    }
			Intent parentsMustReadIntent = new Intent(getActivity(),ParentsMustReadActivity.class);
			pd.dismiss();
			startActivity(parentsMustReadIntent);
			break;
		case R.id.mainStudyTextView:
			pd.show();
			if (OnClickUtil.isFastClick()) {
		        return ;
		    }
			Intent myStudyIntent = new Intent(getActivity(),StudyActivity.class);
			pd.dismiss();
			startActivity(myStudyIntent);
			break;
		case R.id.mainLocationTextView:
			choiseCity();
			break;
		/*
		 * case R.id.mainMusicImageView:
		 * if(!SharedPreferencesUtil.isStartMusic(getActivity())){
		 * mainMusicImageView
		 * .setBackgroundResource(R.drawable.main_title_music_start);
		 * SharedPreferencesUtil.putBoolean(getActivity(),
		 * SharedPreferencesUtil.USER_INFO_FILE_NAME,
		 * SharedPreferencesUtil.USER_INFO_KEY_MUSIC, true);
		 * getActivity().startService(musicIntent); } else{
		 * mainMusicImageView.setBackgroundResource
		 * (R.drawable.main_title_music_stop);
		 * SharedPreferencesUtil.putBoolean(getActivity(),
		 * SharedPreferencesUtil.USER_INFO_FILE_NAME,
		 * SharedPreferencesUtil.USER_INFO_KEY_MUSIC, false);
		 * getActivity().stopService(musicIntent); } break;
		 */
		case R.id.mainCityLocationLinearLayout:
			String location = mainCityLocationTextView.getText().toString()
					.trim();
			if (!getString(R.string.main_locationing).equals(location)) {
				mainLocationTextView
						.setText(mainCityLocationTextView.getText());
				SharedPreferencesUtil.putString(getActivity(),
						SharedPreferencesUtil.USER_INFO_FILE_NAME,
						SharedPreferencesUtil.USER_INFO_KEY_CITY,
						mainLocationTextView.getText().toString().trim());
				mainAd();
			} else {
				mainLocationTextView.setText(R.string.main_choise_city);
			}
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			break;
		}
	}

	private ArrayList<CityModel> getSelectCityNames(String con) {
		ArrayList<CityModel> names = new ArrayList<CityModel>();
		// 判断查询的内容是不是汉字
		Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher m = p_str.matcher(con);
		String sqlString = null;
		if (m.find() && m.group(0).equals(con)) {
			sqlString = "SELECT * FROM T_city WHERE AllNameSort LIKE " + "\""
					+ con + "%" + "\"" + " ORDER BY CityName";
		} else {
			sqlString = "SELECT * FROM T_city WHERE NameSort LIKE " + "\""
					+ con + "%" + "\"" + " ORDER BY CityName";
		}
		Cursor cursor = database.rawQuery(sqlString, null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityModel cityModel = new CityModel();
			cityModel.setCityName(cursor.getString(cursor
					.getColumnIndex("AllNameSort")));
			cityModel.setNameSort(cursor.getString(cursor
					.getColumnIndex("CityName")));
			names.add(cityModel);
		}
		cursor.close();
		return names;
	}

	/**
	 * 从数据库获取城市数据
	 * 
	 * @return
	 */
	private ArrayList<CityModel> getCityNames() {
		ArrayList<CityModel> names = new ArrayList<CityModel>();
		Cursor cursor = database.rawQuery(
				"SELECT * FROM T_city ORDER BY CityName", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityModel cityModel = new CityModel();
			cityModel.setCityName(cursor.getString(cursor
					.getColumnIndex("AllNameSort")));
			cityModel.setNameSort(cursor.getString(cursor
					.getColumnIndex("CityName")));
			names.add(cityModel);
		}
		cursor.close();
		return names;
	}

	/**
	 * 城市列表点击事件
	 * 
	 * @author sy
	 * 
	 */
	class CityListOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			CityModel cityModel = (CityModel) mCityLit.getAdapter()
					.getItem(pos);
			mainLocationTextView.setText(cityModel.getCityName());
			if (getActivity() != null && !getActivity().isFinishing()) {
				SharedPreferencesUtil.putString(getActivity(),
						SharedPreferencesUtil.USER_INFO_FILE_NAME,
						SharedPreferencesUtil.USER_INFO_KEY_CITY,
						mainLocationTextView.getText().toString().trim());
				ToastUtil.toast(getActivity(),
						"您选择的城市为：" + cityModel.getCityName());
				mainAd();
			}
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
		}

	}

	/**
	 * 为ListView设置适配器
	 * 
	 * @param list
	 */
	private void setAdapter(List<CityModel> list) {
		if (list != null) {
			adapter = new ListAdapter(getActivity(), list);
			mCityLit.setAdapter(adapter);
		}

	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CityModel> list;

		public ListAdapter(Context context, List<CityModel> list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getNameSort();
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1)
						.getNameSort() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getNameSort();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.main_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(list.get(position).getCityName());
			String currentStr = list.get(position).getNameSort();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.getNameSort() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	@SuppressLint({ "InflateParams", "InlinedApi" })
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		overlay = (TextView) inflater.inflate(R.layout.main_city_list_item,
				null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mCityLit.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(isReceiver){
    		getActivity().unregisterReceiver(this.broadcastReceiver);
    	}
    	// 注销广播接收者
		try {
			getActivity().unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		try {
			getActivity().unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}
		try {
			getActivity().unregisterReceiver(cmdMessageReceiver);
		} catch (Exception e) {
		}
	}

	private void removeView() {
		int count = mainLinearLayout.getChildCount();
		if (count != 0) {
			for (int i = 0; i < count; i++) {
				mainLinearLayout.removeView(imageViews[i]);
			}
		}
	}

	private void createGroup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String desc = groupName + "群聊";
				String[] members = new String[] {};
				try {
					// 创建公开群，此种方式创建的群，可以自由加入
					// EMGroupManager.getInstance().createPublicGroup(groupName,
					// desc, members, false);
					// 创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
					Log.e(TAG, "groupName:" + groupName);
					if (isHasGroup(groupName)) {
						Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
						//Intent intent =new Intent(getActivity(),ChatListActivity.class);
						chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
						chatIntent.putExtra("groupId", groupid);
						chatIntent.putExtra("groupName", groupName);
						startActivity(chatIntent);
					} else {
						EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, false);
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								ToastUtil.toast(getActivity(), "创建群成功！");
								try {
									List<EMGroupInfo> grouplist = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
									if (grouplist != null
											&& grouplist.size() > 0) {
										for (int i = 0; i < grouplist.size(); i++) {
											EMGroupInfo emGroup = grouplist.get(i);
											System.out.println("groupnamelist"+ emGroup.getGroupName());
											if (emGroup.getGroupName().equals(groupName)) {
												groupid = emGroup.getGroupId();
											}
										}
										Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
										//Intent intent=new Intent(getActivity(),ChatListActivity.class);
										chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
										chatIntent.putExtra("groupId", groupid);
										chatIntent.putExtra("groupName",groupName);
										startActivity(chatIntent);
									}
								} catch (final EaseMobException e) {
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											ToastUtil.toast(getActivity(),"获取群ID失败"+ e.getLocalizedMessage());
										}
									});
								}
							}
						});
					}
				} catch (final EaseMobException e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							ToastUtil.toast(
									getActivity(),
									R.string.Failed_to_create_groups
											+ e.getLocalizedMessage());
						}
					});
				}

			}
		}).start();
		pd.dismiss();
	}

	private boolean isHasGroup(String groupName) {
		List<EMGroupInfo> grouplist;
		try {
			grouplist = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
			if (grouplist != null && grouplist.size() > 0) {
				for (int i = 0; i < grouplist.size(); i++) {
					EMGroupInfo emGroup = grouplist.get(i);
					System.out.println("groupnamelist" + emGroup.getGroupName());
					System.out.println("groupName" + groupName);
					if (emGroup.getGroupName().equals(groupName)) {
						groupid = emGroup.getGroupId();
						return true;
					}
				}
			}
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	private void addContact() {
		try {
			String name = SharedPreferencesUtil.getUsName(getActivity());
			List<EMGroupInfo> groupsList = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
			if (groupsList != null && groupsList.size() > 0) {
				for (int i = 0; i < groupsList.size(); i++) {
					EMGroupInfo emGroupInfo = groupsList.get(i);
					String groupname = emGroupInfo.getGroupName();
					if (groupname.equals(groupName)) {
						groupid = emGroupInfo.getGroupId();
					}
				}
				Log.e(TAG, "groupid:" + groupid);
				System.out.println("ishasContact "+ isHasContact(groupid, name));
				if (isHasContact(groupid, name)) {
					//Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
					Intent intent=new Intent(getActivity(),ChatListActivity.class);
					intent.putExtra("chatType", Constants.CHATTYPE_GROUP);
					intent.putExtra("groupId", groupid);
					intent.putExtra("groupName", groupName);
					startActivity(intent); 
					
				} else {
					new Thread(new Runnable() {
						public void run() {
							try {
								final EMGroup group = EMGroupManager.getInstance().getGroupFromServer(groupid);
								// 如果是membersOnly的群，需要申请加入，不能直接join
								/*
								 * if (group.isMembersOnly()) { EMGroupManager
								 * .getInstance() .applyJoinToGroup( groupid,
								 * getResources() .getString(
								 * R.string.Request_to_join)); } else {
								 */
								EMGroupManager.getInstance().joinGroup(groupid);
								// }
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										if (group.isMembersOnly())
											ToastUtil.toast(getActivity(),R.string.send_the_request_is);
										else
											ToastUtil.toast(getActivity(),R.string.Join_the_group_chat);
										Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
										chatIntent.putExtra("chatType",Constants.CHATTYPE_GROUP);
										chatIntent.putExtra("groupId", groupid);
										chatIntent.putExtra("groupName",groupName);
										pd.dismiss();
										startActivity(chatIntent);
									}
								});
							} catch (final EaseMobException e) {
								e.printStackTrace();
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										ToastUtil.toast(getActivity(),R.string.Failed_to_join_the_group_chat
																+ e.getMessage());
									}
								});
							}
						}
					}).start();
				}
			}
		} catch (EaseMobException e1) {
			e1.printStackTrace();
		}
		pd.dismiss();
	}

	private boolean isHasContact(String groupid, String name) {
		EMGroup emGroup;
		try {
			Log.e(TAG, "groupid:" + groupid);
			emGroup = EMGroupManager.getInstance().getGroupFromServer(groupid);
			List<String> memberList = emGroup.getMembers();
			if (memberList != null && memberList.size() > 0) {
				for (int i = 0; i < memberList.size(); i++) {
					if (name.equals(memberList.get(i))) {
						System.out.println(name + "||" + memberList.get(i));
						return true;
					}
				}
			}
		} catch (EaseMobException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	BroadcastReceiver broadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			getActivity().finish();
		}
		
		
	};
	
	@Override
	public void onResume() {
		super.onResume();
		// 在当前的activity中注册广播  
        IntentFilter filter = new IntentFilter();  
        filter.addAction(Constants.EXIT_LOGIN);  
        isReceiver = true;
        getActivity().registerReceiver(this.broadcastReceiver, filter);
        //刷新
		updateUnreadLabel();
		Log.e("重新开始:",""+ unreadMsgCountTotal);
		EMChatManager.getInstance().activityResumed();
		
	}
	
	
	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				}
			}
			
			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();
			
			notifyNewMessage(message);  

			// 刷新bottom bar消息未读
			updateUnreadLabel();
		}
	}
	
	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					// 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
					if (ChatActivity.activityInstance != null) {
						if (msg.getChatType() == ChatType.Chat) {
							if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
								return;
						}
					}
					msg.isAcked = true;
				}
			}
		}
	};
	/**
	 * 透传消息BroadcastReceiver
	 */
	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {
		
		@SuppressWarnings("unused")
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			EMLog.d(TAG, "收到透传消息");
			//获取cmd message对象
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = intent.getParcelableExtra("message");
			//获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
			String action = cmdMsgBody.action;//获取自定义action
			
			//获取扩展属性 此处省略
//			message.getStringAttribute("");
			EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
//			String st9 = getResources().getString(R.string.receive_the_passthrough);
//			Toast.makeText(MainActivity.this, st9+action, Toast.LENGTH_SHORT).show();
		}
	};
	
	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			mainMyClassRedImageView.setVisibility(View.VISIBLE);
		} else {
			mainMyClassRedImageView.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 获取未读消息数	
	 * 
	 */
	public int getUnreadMsgCountTotal() {
		unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	@Override
	public void notifyNewMessage(EMMessage message) {
		
	}

	
	
}

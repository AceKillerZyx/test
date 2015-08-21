package com.education.zhxy.home.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.myinfo.activity.LoginActivity;
import com.education.zhxy.util.SharedPreferencesUtil;

public class GuideActivity extends Activity implements OnPageChangeListener {
	private ViewPager vp;
	private PageAdapter adapter;
	private List<View> views;
	private ImageView[] dots;
	private int[] ids = { R.id.iv1, R.id.iv2, R.id.iv3};
	private TextView start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zhxy_guide);
		initViews();
		initDots();
	}

	private void initDots() {
		dots = new ImageView[views.size()];
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) findViewById(ids[i]);
		}
	}

	@SuppressLint("InflateParams") 
	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.zhxy_guide_one, null));
		views.add(inflater.inflate(R.layout.zhxy_guide_two, null));
		views.add(inflater.inflate(R.layout.zhxy_guide_three, null));
		adapter = new PageAdapter(views, this);
		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(adapter);
		start = (TextView) views.get(2).findViewById(R.id.start);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//设置已经引导
				SharedPreferencesUtil.putBoolean(getApplicationContext(), SharedPreferencesUtil.APP_INFO_FILE_NAME, SharedPreferencesUtil.APP_INFO_IS_FIRST_IN, false);
				int usid = SharedPreferencesUtil.getUsid(getApplicationContext());
				if(usid == 0){
					startActivity(new Intent(GuideActivity.this, LoginActivity.class));
				}else{
					startActivity(new Intent(GuideActivity.this, ZHXYActivity.class));
				}
				finish();
			}
		});
		vp.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < ids.length; i++) {
			if (i == arg0)
				dots[i].setImageResource(R.drawable.common_dot_focused);
			else {
				dots[i].setImageResource(R.drawable.common_dot_normal);
			}
		}
	}

	public class PageAdapter extends PagerAdapter {

		private List<View> views;
		@SuppressWarnings("unused")
		private Context context;

		public PageAdapter(List<View> views, Context context) {
			this.views = views;
			this.context = context;

		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}
	}
}

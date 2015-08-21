package com.education.zhxy.common.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.education.zhxy.fragment.MainFragment;

public class FixedSpeedScroller extends Scroller {

	@SuppressWarnings("unused")
	private Context context;
	private int mDuration = 2000;// 间隔时长

	public FixedSpeedScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
		this.context = context;
	}

	public FixedSpeedScroller(Context context) {
		super(context);
		this.context = context;
	}

	public void setDuration(MainFragment mainFragment, int time) {
		try {
			Field field = ViewPager.class.getDeclaredField("mScroller");
			field.setAccessible(true);
			this.setmDuration(time);
			field.set(mainFragment, this);
		} catch (Exception e) {

		}
	}

	public int getmDuration() {
		return mDuration;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}

	public void setDuration(Context mainFragment, int time) {
		this.context = mainFragment;
		this.mDuration = time;
	}

	/**
	 * 默认间隔
	 * 
	 */
	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy);
	}

	/**
	 * 自定义间隔
	 * 
	 */
	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

}

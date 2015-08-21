package com.education.zhxy.chat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class FacePagerAdapter extends PagerAdapter {

	List<View> mData = new ArrayList<View>();

	public FacePagerAdapter(List<View> data) {
		this.mData = data;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mData.get(position));
		return mData.get(position);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(mData.get(position));
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}

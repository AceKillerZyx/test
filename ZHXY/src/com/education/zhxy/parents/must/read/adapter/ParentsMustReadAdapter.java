package com.education.zhxy.parents.must.read.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.TextSize;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.parents.must.read.data.bean.ParentsRead;
import com.education.zhxy.util.ArrayUtil;

public class ParentsMustReadAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<ParentsRead> parentsReadList;
	
	public ParentsMustReadAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<ParentsRead> parentsReadList){
		this.parentsReadList = parentsReadList;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(parentsReadList) ? 0 : parentsReadList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(parentsReadList) ? null : parentsReadList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setTextAppearance(context, R.style.commonText);
		textView.setPadding(20, 20, 20, 20);
		textView.setTextColor(context.getResources().getColor(R.color.common_title_bg));
		textView.setTextSize(14);
		ParentsRead parentsRead = parentsReadList.get(position);
		if(parentsRead != null){
			textView.setText(parentsRead.getNames());
		}
		return textView;
	}

}

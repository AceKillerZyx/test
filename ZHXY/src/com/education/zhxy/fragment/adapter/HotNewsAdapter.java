package com.education.zhxy.fragment.adapter;

import java.util.List;

import com.education.zhxy.R;
import com.education.zhxy.fragment.data.bean.MainNews;
import com.education.zhxy.util.ArrayUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HotNewsAdapter extends BaseAdapter{
	
	List<MainNews> newsList;
	Context context;
	public HotNewsAdapter(Context context, List<MainNews> newsList){
		super();
		this.newsList = newsList;
		this.context = context;
	}
	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(newsList) ? 0:newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(newsList)? null: newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(R.layout.hot_news_item, null);
			viewHolder.hotNewsItemTextView=(TextView) convertView.findViewById(R.id.hotNewsItemTextVIew);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MainNews mainNews=newsList.get(position);
		if (mainNews!=null) {
			viewHolder.hotNewsItemTextView.setText(mainNews.getTitle());
		}
		
		return convertView;
	}
	
	class ViewHolder {
		  TextView hotNewsItemTextView;
	  }
	
}

   

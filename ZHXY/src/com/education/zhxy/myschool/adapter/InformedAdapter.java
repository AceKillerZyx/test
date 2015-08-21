package com.education.zhxy.myschool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.myschool.data.bean.Informed;
import com.education.zhxy.util.ArrayUtil;

@SuppressLint("InflateParams") 
public class InformedAdapter  extends BaseAdapter {
	
	private Context context;
	
	private List<Informed> informedList;
	
	public InformedAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<Informed> informedList){
		this.informedList = informedList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(informedList) ? 0 : informedList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(informedList) ? null : informedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Informed informed = informedList.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(R.layout.myschool_informed_item, null);
			
			viewHolder.mySchoolInformedItemTitleTextView = (TextView)convertView.findViewById(R.id.mySchoolInformedItemTitleTextView);
			viewHolder.mySchoolInformedItemTimeTextView = (TextView)convertView.findViewById(R.id.mySchoolInformedItemTimeTextView);
			viewHolder.mySchoolInformedItemContentTextView = (TextView)convertView.findViewById(R.id.mySchoolInformedItemContentTextView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(informed != null){
			viewHolder.mySchoolInformedItemTitleTextView.setText("最新通知");
			viewHolder.mySchoolInformedItemTimeTextView.setText(informed.getNdate().replace("T", " "));
			viewHolder.mySchoolInformedItemContentTextView.setText(informed.getNcontent());
		}
		
		return convertView;
	}
	class ViewHolder {
		TextView mySchoolInformedItemTitleTextView,
				mySchoolInformedItemTimeTextView,
				mySchoolInformedItemContentTextView;
	}

}

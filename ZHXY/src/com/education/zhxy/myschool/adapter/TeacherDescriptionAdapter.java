package com.education.zhxy.myschool.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.zhxy.R;
import com.education.zhxy.ReleaseConfigure;
import com.education.zhxy.myschool.data.bean.TeacherDescription;
import com.education.zhxy.util.ArrayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TeacherDescriptionAdapter extends BaseAdapter {
	
	private static final String TAG = TeacherDescriptionAdapter.class.getSimpleName();

	private Context context;
	
	private List<TeacherDescription> teacherdescriptionList;
	
	public TeacherDescriptionAdapter(Context context){
		super();
		this.context = context;
	}
	
	public void resetData(List<TeacherDescription> teacherdescriptionList){
		this.teacherdescriptionList = teacherdescriptionList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ArrayUtil.isEmptyList(teacherdescriptionList) ? 0 : teacherdescriptionList.size();
	}

	@Override
	public Object getItem(int position) {
		return ArrayUtil.isEmptyList(teacherdescriptionList) ? null : teacherdescriptionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(R.layout.myschool_teacher_description_item, null);
			viewHolder.myschoolteacherItemImageView = (ImageView)convertView.findViewById(R.id.myschoolteacherItemImageView);
			viewHolder.myschoolteacherItemTnamesTextView = (TextView)convertView.findViewById(R.id.myschoolteacherItemTnamesTextView);
			viewHolder.myschoolteacherItemUserNameTextView = (TextView)convertView.findViewById(R.id.myschoolteacherItemUserNameTextView);
			viewHolder.myschoolteacherItemAgeTextView = (TextView)convertView.findViewById(R.id.myschoolteacherItemAgeTextView);
			viewHolder.myschoolteacherItemGradeNameTextView = (TextView)convertView.findViewById(R.id.myschoolteacherItemGradeNameTextView);
			viewHolder.myschoolteacherItemEducationTextView = (TextView)convertView.findViewById(R.id.myschoolteacherItemEducationTextView);
			viewHolder.hmyschoolteacherItemQualificationsTextView = (TextView)convertView.findViewById(R.id.hmyschoolteacherItemQualificationsTextView);
			viewHolder.myschoolteacherItemCertificateNumberTextView = (TextView)convertView.findViewById(R.id.myschoolteacherItemCertificateNumberTextView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		TeacherDescription teacherDescription = teacherdescriptionList.get(position);
		if(teacherDescription != null){
			viewHolder.myschoolteacherItemTnamesTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_tnames), teacherDescription.getTnames()));
			viewHolder.myschoolteacherItemUserNameTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_username), teacherDescription.getUsersname()));
			viewHolder.myschoolteacherItemGradeNameTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_gradename),teacherDescription.getGreadname()));
			viewHolder.myschoolteacherItemAgeTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_age),teacherDescription.getUserage()));
			viewHolder.myschoolteacherItemEducationTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_education), teacherDescription.getTculture()));
			viewHolder.hmyschoolteacherItemQualificationsTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_qualifications), teacherDescription.getTcertificate()));
			viewHolder.myschoolteacherItemCertificateNumberTextView.setText(String.format(context.getResources().getString(R.string.myschool_teacher_certificate_number), teacherDescription.getTworkingtime()));
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.heart_item_bg)
					.showImageOnFail(R.drawable.heart_item_bg)
					.resetViewBeforeLoading(true).cacheOnDisk(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300)).build();
			ImageLoader.getInstance().displayImage(ReleaseConfigure.ROOT_IMAGE + teacherDescription.getUsimage(), viewHolder.myschoolteacherItemImageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
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
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				}
			});
		}
		
		return convertView;
	}
	class ViewHolder {
		ImageView myschoolteacherItemImageView;
		TextView 
		myschoolteacherItemTnamesTextView,
		myschoolteacherItemUserNameTextView, 
		myschoolteacherItemGradeNameTextView,
		myschoolteacherItemAgeTextView, 
		myschoolteacherItemEducationTextView,
		hmyschoolteacherItemQualificationsTextView,
		myschoolteacherItemCertificateNumberTextView;
	}

}

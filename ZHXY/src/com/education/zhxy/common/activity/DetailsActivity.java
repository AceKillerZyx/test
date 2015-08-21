package com.education.zhxy.common.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.zhxy.Constants;
import com.education.zhxy.R;
import com.education.zhxy.fragment.data.bean.MainNews;
import com.education.zhxy.http.core.HttpResult;
import com.education.zhxy.http.core.HttpTask;

public class DetailsActivity extends BasicActivity{
	private TextView detailsBackTextView,DetailsTitleOneTextView,DetailsContentOneTextView,detailsMoreTextView
	,DetailsTitleTwoTextView
	,DetailsTitleThreeTextView
	,DetailsTitleFourTextView
	,DetailsTitleFiveTextView
	,DetailsContentTwoTextView
	,DetailsContentThreeTextView
	,DetailsContentFourTextView
	,DetailsContentFiveTextView;	
	
	private ImageView DetailsOneImaeView,DetailsTwoImaeView,DetailsThreeImaeView,DetailsFourImaeView,DetailsFiveImaeView;
	
	private LinearLayout detailsLinearLayout2,detailsLinearLayout3,detailsLinearLayout4,detailsLinearLayout5,detailsMoreLinearLayout;
	
	private MainNews mainNews;	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.detailsBackTextView:
			this.finish();
			break;
		case R.id.detailsMoreTextView:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://"+mainNews.getUrl());
			intent.setData(content_url);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void noNet(HttpTask task) {
		
	}

	@Override
	public void noData(HttpTask task, HttpResult result) {
		
	}

	@Override
	public void onLoadFailed(HttpTask task, HttpResult result) {
		
	}

	@Override
	public void onLoadFinish(HttpTask task, HttpResult result) {
		
	}

	@Override
	public int initLayout() {
		return R.layout.details;
	}

	@Override
	public void initUI() {
		
		mainNews = (MainNews)getIntent().getSerializableExtra(Constants.MAIN_NEWS);
		
		detailsLinearLayout2=(LinearLayout) findViewById(R.id.detailsLinearLayout2);
		detailsLinearLayout3=(LinearLayout) findViewById(R.id.detailsLinearLayout3);
		detailsLinearLayout4=(LinearLayout) findViewById(R.id.detailsLinearLayout4);
		detailsLinearLayout5=(LinearLayout) findViewById(R.id.detailsLinearLayout5);
		detailsMoreLinearLayout=(LinearLayout) findViewById(R.id.detailsMoreLinearLayout);
		
		//返回按钮
		detailsBackTextView=(TextView) findViewById(R.id.detailsBackTextView);
		detailsBackTextView.setOnClickListener(this);
		
		//第一个标题
		DetailsTitleOneTextView=(TextView) findViewById(R.id.DetailsTitleOneTextView);
		DetailsTitleOneTextView.setText(mainNews.getTitle());
		
		//第二个标题
		DetailsTitleTwoTextView=(TextView) findViewById(R.id.DetailsTitleTwoTextView);
		if (mainNews.getTitle2()==null) {
			detailsLinearLayout2.setVisibility(View.GONE);
		}else {
			DetailsTitleTwoTextView.setText(mainNews.getTitle2());
		}
		
		//第三个标题
		DetailsTitleThreeTextView=(TextView) findViewById(R.id.DetailsTitleThreeTextView);
		if (mainNews.getTitle3()==null) {
			detailsLinearLayout3.setVisibility(View.GONE);
			
			
			
		}else {
			DetailsTitleThreeTextView.setText(mainNews.getTitle3());
		}
		
		//第四个标题
		DetailsTitleFourTextView=(TextView) findViewById(R.id.DetailsTitleFourTextView);
		if (mainNews.getTitle4()==null) {
			detailsLinearLayout4.setVisibility(View.GONE);
		}else {
			DetailsTitleFourTextView.setText(mainNews.getTitle4());
		}
		
		//第五个标题
		DetailsTitleFiveTextView=(TextView) findViewById(R.id.DetailsTitleFiveTextView);
		if (mainNews.getTitle5()==null) {
			detailsLinearLayout5.setVisibility(View.GONE);
		}else {
			DetailsTitleFiveTextView.setText(mainNews.getTitle5());
		}
		
		
		//第一个内容
		DetailsContentOneTextView=(TextView) findViewById(R.id.DetailsContentOneTextView);
		DetailsContentOneTextView.setText(mainNews.getContent());
		//第二个内容
		DetailsContentTwoTextView=(TextView) findViewById(R.id.DetailsContentTwoTextView);
		DetailsContentTwoTextView.setText(mainNews.getContent2());
		//第三个内容
		DetailsContentThreeTextView=(TextView) findViewById(R.id.DetailsContentThreeTextView);
		DetailsContentThreeTextView.setText(mainNews.getContent3());
		//第四个内容
		DetailsContentFourTextView=(TextView) findViewById(R.id.DetailsContentFourTextView);
		DetailsContentFourTextView.setText(mainNews.getContent4());
		//第五个内容
		DetailsContentFiveTextView=(TextView) findViewById(R.id.DetailsContentFiveTextView);
		DetailsContentFiveTextView.setText(mainNews.getContent5());
		
		//第一个图片
		DetailsOneImaeView=(ImageView) findViewById(R.id.DetailsOneImaeView);
		if (mainNews.getImg()==null) {
			DetailsOneImaeView.setVisibility(View.GONE);
		}else {
			DetailsOneImaeView.setImageResource(R.drawable.zhxy_guide_one);
		}
		
		//第二个图片
		DetailsTwoImaeView=(ImageView) findViewById(R.id.DetailsTwoImaeView);
		if (mainNews.getImg2()==null) {
			DetailsTwoImaeView.setVisibility(View.GONE);
		}else {
			DetailsTwoImaeView.setImageResource(R.drawable.zhxy_guide_one);
		}
		
		//第三个图片
		DetailsThreeImaeView=(ImageView) findViewById(R.id.DetailsThreeImaeView);
		if (mainNews.getImg3()==null) {
			DetailsThreeImaeView.setVisibility(View.GONE);
		}else {
			DetailsThreeImaeView.setImageResource(R.drawable.zhxy_guide_one);
		}
		
		//第四个图片
		DetailsFourImaeView=(ImageView) findViewById(R.id.DetailsFourImaeView);
		if (mainNews.getImg4()==null) {
			DetailsFourImaeView.setVisibility(View.GONE);
		}else {
			DetailsFourImaeView.setImageResource(R.drawable.zhxy_guide_one);
		}
		
		
		//第五个图片
		DetailsFiveImaeView=(ImageView) findViewById(R.id.DetailsFiveImaeView);
		if (mainNews.getImg5()==null) {
			DetailsFiveImaeView.setVisibility(View.GONE);
		}else {
			DetailsFiveImaeView.setImageResource(R.drawable.zhxy_guide_one);
		}
		
		detailsMoreTextView=(TextView) findViewById(R.id.detailsMoreTextView);
		detailsMoreTextView.setOnClickListener(this);
		if (mainNews==null) {
			detailsMoreLinearLayout.setVisibility(View.GONE);
		}else {
			detailsMoreTextView.setText(mainNews.getUrl());	
		}
		
	}

	@Override
	public void initData() {
		
	}

}

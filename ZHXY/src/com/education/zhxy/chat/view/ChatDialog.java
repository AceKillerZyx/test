package com.education.zhxy.chat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.education.zhxy.R;

/**
 * @文件名称: MyDialog.java
 * @功能描述: 自定义dialog
 * @版本信息: Copyright (c)2014
 * @开发人员: vincent
 * @版本日志: 1.0
 * @创建时间: 2014年3月18日 下午1:45:38
 */
public class ChatDialog extends Dialog implements OnClickListener {
	
	private TextView chatDialogCopyTextView,chatDialogShareTextView,chatDialogDeleteTextView;
	
	private IDialogOnclickInterface dialogOnclickInterface;
	
	private Context context;

	public ChatDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_dialog);

		chatDialogCopyTextView = (TextView) findViewById(R.id.chatDialogCopyTextView);
		chatDialogShareTextView = (TextView) findViewById(R.id.chatDialogShareTextView);
		chatDialogDeleteTextView = (TextView) findViewById(R.id.chatDialogDeleteTextView);
		chatDialogCopyTextView.setOnClickListener(this);
		chatDialogShareTextView.setOnClickListener(this);
		chatDialogDeleteTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		dialogOnclickInterface = (IDialogOnclickInterface) context;
		switch (v.getId()) {
			case R.id.chatDialogCopyTextView:
				dialogOnclickInterface.copyOnclick();
				break;
			case R.id.chatDialogShareTextView:
				dialogOnclickInterface.shareOnclick();
				break;
			case R.id.chatDialogDeleteTextView:
				dialogOnclickInterface.deleteOnclick();
				break;
		}
	}

	public interface IDialogOnclickInterface {
		void copyOnclick();
		void shareOnclick();
		void deleteOnclick();
	}
}

package com.hwnag.listenbook.myView;

import com.hwang.listenbook.R;

import android.R.anim;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AlertDialog {
	
	Context context;
	//android.app.AlertDialog ad;
	android.app.Dialog ad;
	TextView titleView;
	TextView messageView,errorTv;
	
	LinearLayout buttonLayout;
	public AlertDialog(final Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.alertdialog, null);
		//ad=new android.app.AlertDialog.Builder(context).create();
		ad =new android.app.Dialog(context);
		//ad.setView(view);
		ad.setContentView(view);
		ad.show();
		//关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		//window.setContentView(view);
		window.setBackgroundDrawable(new ColorDrawable(0));
	
		titleView=(TextView)view.findViewById(R.id.title);
		messageView=(TextView)view.findViewById(R.id.message);
		buttonLayout=(LinearLayout)view.findViewById(R.id.buttonLayout);
	}
	public void setTitle(int resId)
	{
		titleView.setText(resId);
	}
	public void setTitle(String title) {
		titleView.setText(title);
	}
	public void setMessage(int resId) {
		messageView.setText(resId);
	}
 
	public void setMessage(String message)
	{
		messageView.setText(message);
	}
	
	
	
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,final OnClickListener listener)
	{
		Button button=new Button(context);
		LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		params.weight=1;
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.dialog_left);
		button.setText(text);
		button.setTextColor(context.getResources().getColor(R.color.title_bg));
		button.setTextSize(18);
		button.setOnClickListener(listener);
		buttonLayout.addView(button);
	}
 
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text,final OnClickListener listener)
	{
		Button button=new Button(context);
		LayoutParams params=new LayoutParams(0, LayoutParams.MATCH_PARENT);
		params.weight=1;
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.dialog_right);
		button.setText(text);
		button.setTextColor(context.getResources().getColor(R.color.title_bg));
		button.setTextSize(18);
		button.setOnClickListener(listener);
		if(buttonLayout.getChildCount()>0)
		{
			params.setMargins(1, 0, 0, 0);
			button.setLayoutParams(params);
			buttonLayout.addView(button, 1);
		}else{
			button.setLayoutParams(params);
			buttonLayout.addView(button);
		}
 
	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
 
}
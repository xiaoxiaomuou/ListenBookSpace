package com.hwang.listenbook;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;

import net.tsz.afinal.FinalActivity;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.hwang.listenbook.util.ActivityStackControlUtil;

/**
 * @description Activity的基类
 *
 * @author zhanghao
 * @createDate 2014-12-28
 * @version 1.0
 */
public class BaseActivity extends FinalActivity implements OnClickListener ,OnTouchListener{

	protected int mScreenWith;
	
	protected int mBar;

	private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		ActivityStackControlUtil.add(this);
		mScreenWith = getWindowManager().getDefaultDisplay().getWidth();
//		Class<?> c = null;
//		Object obj = null;
//		Field field = null;
//		int x = 0;
//		try {
//			c = Class.forName("com.android.internal.R$dimen");
//			obj = c.newInstance();
//			field = c.getField("status_bar_height");
//			x = Integer.parseInt(field.get(obj).toString());
//			mBar = getResources().getDimensionPixelSize(x);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		HttpClient httpClient = new HttpClient();
//		httpClient.tenderResources(null, 3,"", "", "", "", "", 1, 10, Contant.ZHAOBIAO);

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		ActivityStackControlUtil.remove(this);
	}

	protected void findViewById() {

	}

	protected void enterActivity(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
	}

	protected void exitActivity() {
		finish();
		overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void show(ProgressDialog dialog, String msg) {
		try {
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage(msg);
			if (!dialog.isShowing()) {
				dialog.show();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	
	public  void cancle(ProgressDialog dialog) {
		if (dialog != null ) {
			try {
				dialog.dismiss();	
				dialog = null;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * 联网失败
	 */
	public void connectFail(){
		Toast.makeText(getApplicationContext(), getString(R.string.http_error), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * go pdf
	 * @param context
	 * @param path
	 */
	public void toPdf(Context context,String path){
	Uri uri = Uri.parse(path);
	Intent intent = new Intent(context,MuPDFActivity.class);
	intent.setAction(Intent.ACTION_VIEW);
	intent.setData(uri);
	context.startActivity(intent);
	}
	
	/**
	 * 显示提示信息
	 * 
	 * @param info 提示信息内容
	 */
	public void showToast(String info) {
		try {
			Toast.makeText(this, info, Toast.LENGTH_SHORT).show();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

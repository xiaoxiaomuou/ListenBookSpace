package com.hwang.listenbook;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StartActivity extends BaseActivity{
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	boolean isFirstIn = false;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private static final long SPLASH_DELAY_MILLIS = 1000;
	Runnable mStart = null;
    
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				passInto();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_start_layout);
		
		init();
	}
    
	private void init() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
//		if (!isFirstIn) {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//		} else {
//			
//			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
//		}

	}
	
	private void goGuide() {
//		Intent intent = new Intent(StartActivity.this, GuideActivity.class);
//		StartActivity.this.startActivity(intent);
//		StartActivity.this.finish();
	}
	
	private void passInto() {
		mStart = new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage(202);
				msg.sendToTarget();
			}
		};
		handler.postDelayed(mStart, 500);
	}

	Handler handler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {

			super.dispatchMessage(msg);
		}

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 202:
//				if(Tool.getStringShared(getApplicationContext(), Contant.USERNAME).length()==0){
//					startActivity(new Intent(StartActivity.this,
//							LoginActivity.class));
//					finish();
//					overridePendingTransition(R.anim.right_in, R.anim.left_out);
//				}else{
					
					startActivity(new Intent(StartActivity.this,
							MainActivity.class));
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
//					} 
				
				break;
			}
		}
	};
}

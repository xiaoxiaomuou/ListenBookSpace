package com.hwnag.listenbook.myView;

import java.util.Timer;
import java.util.TimerTask;

import com.hwang.listenbook.R;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.Tool;

import android.R.anim;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class VoiceAlertDialog {
	
	Context context;
	//android.app.AlertDialog ad;
	android.app.Dialog ad;
	TextView titleView;
	SeekBar voiceBar;
	int voice = -1;
	private AudioManager audioManager;
	private boolean isOper =false;
	//private Handler cancelHandler;
	Handler cancelHandler = new Handler() {  
        public void handleMessage(Message msg) {  
            if (msg.what == 1) {  
                if(ad!=null && ad.isShowing()){
                	if (isOper) {
						
					}else {
						ad.dismiss();
						timer.cancel();
					}
                }
            }  
            super.handleMessage(msg);  
        };  
    };  
    Timer timer = new Timer();  
    TimerTask task = new TimerTask() {  
  
        @Override  
        public void run() {  
            // 需要做的事:发送消息  
            Message message = new Message();  
            message.what = 1;  
            cancelHandler.sendMessage(message);  
        }  
    };  
	
	LinearLayout buttonLayout;
	public VoiceAlertDialog(final Context context,AudioManager audioManager) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.audioManager = audioManager;
//		this.cancelHandler = cancelHandler;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.voice_dialog, null);
		//ad=new android.app.AlertDialog.Builder(context).create();
		ad =new android.app.Dialog(context);
		//ad.setView(view);
		ad.setContentView(view);
		 timer.schedule(task, 2500, 2500); // 1s后执行task,经过1s再次执行  
		ad.show();
		//关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		//window.setContentView(view);
		window.setBackgroundDrawable(new ColorDrawable(0));
	
		titleView=(TextView)view.findViewById(R.id.title);
		voiceBar=(SeekBar)view.findViewById(R.id.voice_bar);
//		buttonLayout=(LinearLayout)view.findViewById(R.id.buttonLayout);
		voiceBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
	}
	public void setTitle(int resId)
	{
		titleView.setText(resId);
	}
	public void setTitle(String title) {
		titleView.setText(title);
	}
	public void setMaxVoice(int voice) {
		voiceBar.setMax(voice);
	}
 
	public void setCurrentVoice(int voice)
	{
		voiceBar.setProgress(voice);
	}
	
	public int getCurrentVoice(){
		return voice;
	}
	
	public boolean isShowing(){
		return ad.isShowing();
	}
	
	public boolean isOper(){
		return isOper;
	}
	
	
	/* 拖放进度监听 ，别忘了Service里面还有个进度条刷新*/  
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {  
	  
	    @Override  
	    public void onProgressChanged(SeekBar seekBar, int progress,  
	            boolean fromUser) {  
	        /*假设改变源于用户拖动*/  
	       if (fromUser) {
	    	  isOper =true;
	    	   voice=progress;
	    	   audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voice, 0);
	       }  
	    }  
	  
	    @Override  
	    public void onStartTrackingTouch(SeekBar seekBar) {  
	       // PlayerService.mMediaPlayer.pause(); // 开始拖动进度条时，音乐暂停播放 
	    	
	    }  
	  
	    @Override  
	    public void onStopTrackingTouch(SeekBar seekBar) {  
	       // PlayerService.mMediaPlayer.start(); // 停止拖动进度条时，音乐开始播放  
	    	isOper =false;
	    }  
	} 
	
//	/**
//	 * 设置按钮
//	 * @param text
//	 * @param listener
//	 */
//	public void setPositiveButton(String text,final View.OnClickListener listener)
//	{
//		Button button=new Button(context);
//		LinearLayout.LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
//		params.weight=1;
//		button.setLayoutParams(params);
//		button.setBackgroundResource(R.drawable.dialog_left);
//		button.setText(text);
//		button.setTextColor(context.getResources().getColor(R.color.title_bg));
//		button.setTextSize(18);
//		button.setOnClickListener(listener);
//		buttonLayout.addView(button);
//	}
// 
//	/**
//	 * 设置按钮
//	 * @param text
//	 * @param listener
//	 */
//	public void setNegativeButton(String text,final View.OnClickListener listener)
//	{
//		Button button=new Button(context);
//		LinearLayout.LayoutParams params=new LayoutParams(0, LayoutParams.MATCH_PARENT);
//		params.weight=1;
//		button.setLayoutParams(params);
//		button.setBackgroundResource(R.drawable.dialog_right);
//		button.setText(text);
//		button.setTextColor(context.getResources().getColor(R.color.title_bg));
//		button.setTextSize(18);
//		button.setOnClickListener(listener);
//		if(buttonLayout.getChildCount()>0)
//		{
//			params.setMargins(1, 0, 0, 0);
//			button.setLayoutParams(params);
//			buttonLayout.addView(button, 1);
//		}else{
//			button.setLayoutParams(params);
//			buttonLayout.addView(button);
//		}
// 
//	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
 
}
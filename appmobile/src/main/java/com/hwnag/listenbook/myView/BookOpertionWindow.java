package com.hwnag.listenbook.myView;

import com.hwang.listenbook.R;
import com.hwang.listenbook.util.Constants;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class BookOpertionWindow extends PopupWindow{
	
	private View mMenuView = null;
	
	private Button mReadBtn = null;
	
	private Button mCollectBtn = null;
	
	private Button mCopyBtn = null;
	
	private Button mdeleteBtn = null;
	
	private Button mErCodeBtn = null;
	
	private Button mInfoBtn = null;
	
	private Button mCancel = null;
	
	public BookOpertionWindow(Activity context,OnClickListener itemsOnClick,int type) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.book_operation_window, null);
		
		mReadBtn = (Button)mMenuView.findViewById(R.id.btn_read);
		mCollectBtn = (Button)mMenuView.findViewById(R.id.btn_collect);
		mCopyBtn = (Button)mMenuView.findViewById(R.id.btn_copy);
		mdeleteBtn = (Button)mMenuView.findViewById(R.id.btn_delete);
		mErCodeBtn = (Button)mMenuView.findViewById(R.id.btn_ercode);
		mInfoBtn = (Button)mMenuView.findViewById(R.id.btn_info);
		mCancel = (Button)mMenuView.findViewById(R.id.btn_cancel);
		if (Constants.IS_SERVER) {
			mErCodeBtn.setVisibility(View.VISIBLE);
			mCollectBtn.setVisibility(View.GONE);
			if(type==1){//书籍
				mCopyBtn.setVisibility(View.GONE);
				mCollectBtn.setText(context.getString(R.string.btn_collect));
				
			} else if(type == 2) {//收cang
				mCopyBtn.setVisibility(View.VISIBLE);
				mCollectBtn.setText(context.getString(R.string.btn_uncollect));
			} else {
				mCopyBtn.setVisibility(View.VISIBLE);
				mCollectBtn.setText(context.getString(R.string.btn_collect));
			}
		} else {
			mErCodeBtn.setVisibility(View.GONE);
			mCopyBtn.setVisibility(View.GONE);
			mCollectBtn.setVisibility(View.VISIBLE);
			if(type==1){//书籍
				
				mCollectBtn.setText(context.getString(R.string.btn_collect));
				
			} else if(type == 2) {
				mCollectBtn.setText(context.getString(R.string.btn_uncollect));
			} else {
				mCollectBtn.setText(context.getString(R.string.btn_collect));
			}
		}
		
		
		mReadBtn.setOnClickListener(itemsOnClick);
		mCollectBtn.setOnClickListener(itemsOnClick);
		mCopyBtn.setOnClickListener(itemsOnClick);
		mdeleteBtn.setOnClickListener(itemsOnClick);
		mErCodeBtn.setOnClickListener(itemsOnClick);
		mInfoBtn.setOnClickListener(itemsOnClick);
		
		mCancel.setOnClickListener(itemsOnClick);
		
		//设置SelectPicPopupWindow的View
				this.setContentView(mMenuView);
				//设置SelectPicPopupWindow弹出窗体的宽
				this.setWidth(LayoutParams.FILL_PARENT);
				//设置SelectPicPopupWindow弹出窗体的高
				this.setHeight(LayoutParams.WRAP_CONTENT);
				//设置SelectPicPopupWindow弹出窗体可点击
				this.setFocusable(true);
				//设置SelectPicPopupWindow弹出窗体动画效果
				this.setAnimationStyle(R.style.AnimBottom);
				//实例化一个ColorDrawable颜色为半透明
				ColorDrawable dw = new ColorDrawable(0xb0000000);
				//设置SelectPicPopupWindow弹出窗体的背景
				this.setBackgroundDrawable(dw);
				//mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
				/*mMenuView.setOnTouchListener(new OnTouchListener() {
					
					public boolean onTouch(View v, MotionEvent event) {
						
						int height = mMenuView.findViewById(R.id.pop_layout).getTop();
						int y=(int) event.getY();
						if(event.getAction()==MotionEvent.ACTION_UP){
							if(y<height){
								dismiss();
							}
						}				
						return true;
					}
				});*/
		
	}
}

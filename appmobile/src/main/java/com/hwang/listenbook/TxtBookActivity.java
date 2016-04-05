package com.hwang.listenbook;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.BookPageFactory;
import com.hwang.listenbook.util.MyPageWidget;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * txt阅读
 * @author wanghao
 *
 */
@SuppressLint("WrongCall")
public class TxtBookActivity extends BaseActivity{

	private static final String TAG = "TxtBookActivity";
	@ViewInject(id=R.id.top_back) ImageView mBack;
	@ViewInject(id=R.id.top_title) TextView titleTv;
	@ViewInject(id=R.id.top_layout) View topView;
	@ViewInject(id=R.id.MyFlipper) ViewFlipper mMyViewFlipper;
	
	public MyPageWidget mPageWidget;
	public TxtBookActivity myebook = this;
	public static Bitmap mCurPageBitmap, mNextPageBitmap;
	public static Canvas mCurPageCanvas, mNextPageCanvas;
	public BookPageFactory pagefactory;
	public static int mHeight,mWidth;
	public static String filepath;
	public static String scale;
	public static String memory = "0.0";
	private int index = 0;
	private ListenBook  book;
	private ArrayList<ListenBookFileBean> fileBeans ;
	private FinalDb fDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_txt_book);
		fDb = FinalDb.create(this);
		if (this.getIntent() != null
				&& this.getIntent().getDataString() != null) {
			filepath = Uri.decode(this.getIntent().getDataString());
			filepath = filepath
					.substring(filepath.indexOf('/') + 2);
			
		}else {
			book = (ListenBook) getIntent().getSerializableExtra("book");
			index = getIntent().getIntExtra("index", 0);
			//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
			fileBeans = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(
					ListenBookFileBean.class, "mainCode='"+book.getCode()+"'");
//			fileBeans = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(
//					ListenBookFileBean.class, "mainCode="+book.getCode()+"");
			
			filepath = fileBeans.get(index).getSrc();
		}
		
		initView();
	}

	private void initView() {
		mBack.setVisibility(View.VISIBLE);
		mBack.setImageResource(R.drawable.back);
		if (book!=null) {
			titleTv.setText(book.getName());
		} else {
			titleTv.setText(Tool.getFileName(filepath));
		}
		mBack.setOnClickListener(this);
		mWidth = Tool.getwindowWidth(myebook);
		mHeight = Tool.getwindowHeight(myebook)-70;
		mPageWidget = new MyPageWidget(this);
		mMyViewFlipper.addView(mPageWidget);
		Log.d("pvebook", "hrer is width==>" + mWidth);
		Log.d("pvebook", "hrer is height==>" + mHeight);
		mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
		mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		pagefactory = new BookPageFactory(mWidth, mHeight);
		
		
		pagefactory.setBgBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.bg));
		if (fileBeans!=null && fileBeans.size()!=0) {
			if (fileBeans.get(index).getCode().equals(book.getCurrentCode())) {
				memory=android.provider.Settings.System.getString(
						getContentResolver(), book.getCode());
			}
			
		}
		openBook();

//		}

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub

				boolean ret = false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
					 if (Math.abs(e.getX()-mWidth/2)>mWidth/4 
							 || Math.abs(e.getY()-mHeight/2)> mWidth/4) {
						 mPageWidget.abortAnimation(); 
							
							mPageWidget.calcCornerXY(e.getX(), e.getY());
							pagefactory.onDraw(mCurPageCanvas);
							Log.d("hwang", "this is onDraw=====4444");
							if (mPageWidget.DragToRight()) {								
								try {
									pagefactory.prePage();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								if (pagefactory.isfirstPage()){
									mPageWidget.clearAnimation();
									goPrevBook();
									return false;}
								pagefactory.onDraw(mNextPageCanvas);
								
							} else {
								
								pagefactory.onDraw(mCurPageCanvas);
								Log.d("hwang", "this is onDraw=====55555");
								try {
									pagefactory.nextPage();
									
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}	
								if (pagefactory.islastPage()){
									mPageWidget.clearAnimation();
									goNextBook();
									return false;
									}
								pagefactory.onDraw(mNextPageCanvas);							
							}
							mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
							
					} else {
						showView();
					}
						
					}

					ret = mPageWidget.doTouchEvent(e);
					return ret;
				}
				return false;
			}

		});
	}

	protected void showView() {
		if (topView.isShown()) {
			topView.setVisibility(View.GONE);
		} else {
			topView.setVisibility(View.VISIBLE);
		}
	}

	protected void goPrevBook() {
		// TODO Auto-generated method stub
		if(fileBeans!=null && index>0){
			index-=1;
			filepath = fileBeans.get(index).getSrc();
			openBook();
		}
	}

	private void openBook() {
		if(filepath!=null){			
			try {
				pagefactory.openbook(filepath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if ( memory==null) {
			
			pagefactory.onDraw(mCurPageCanvas);
			Log.d("hwang", "this is onDraw=====2222");
		} else {
			
			pagefactory.gotoPage(Float.parseFloat(memory));
			pagefactory.onDraw(mCurPageCanvas);
			Log.d("hwang", "this is onDraw=====33333");
		}
		if (book!=null) {
			book.setCurrentCode(fileBeans.get(index).getCode());
		}
		
	}

	protected void goNextBook() {
		if(fileBeans!=null && index<fileBeans.size()-1){
			index+=1;
			filepath = fileBeans.get(index).getSrc();
			openBook();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			finish();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	
	@Override
	public void onDestroy() {
		if(mCurPageBitmap != null && !mCurPageBitmap.isRecycled())
		{
			//mCurPageBitmap.recycle();
			mCurPageBitmap = null;
		}
		if(mNextPageBitmap != null && !mNextPageBitmap.isRecycled())
		{
			//mNextPageBitmap.recycle();
			mNextPageBitmap = null;
		}
		if(pagefactory.m_book_bg != null && !pagefactory.m_book_bg.isRecycled())
		{
			//pagefactory.m_book_bg.recycle();
			pagefactory.m_book_bg = null;
		}
		System.gc();
		super.onDestroy();
		//System.exit(0);
	}
	
	@Override
	public void finish() {
		if (book!=null) {
			book.setCurrentCode(fileBeans.get(index).getCode());
			fDb.update(book);
			android.provider.Settings.System.putString(
					getContentResolver(),book.getCode(), BookPageFactory.strPercent.replace("%", ""));
		}
		
		super.finish();
	}
}

package com.hwang.listenbook;


import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.bean.ListenBookFileManager;
import com.hwang.listenbook.bean.ListenBookManager;
import com.hwang.listenbook.service.HttpService;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;
import com.hwnag.listenbook.myView.AlertDialog;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends TabActivity implements
OnCheckedChangeListener,OnClickListener {

	private static final String TAG = "MainActivity";
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	int currentView = 0;
	private static int maxTabIndex = 4;
	private RadioGroup mainTab;
	private TabHost tabhost;
	private Intent mHomepage, mMessage, mClassify, mDownload;

	private RadioButton mRadioButton1;
	private RadioButton mRadioButton2;
	private RadioButton mRadioButton3;
	private RadioButton mRadioButton4;
	
	private ImageView erCodeIv;
	private TextView titleTv;
	private ImageView rightIv;
	private View titleView;
	private Receiver mReceiver;
	
	private FinalDb fDb;
	private PopupWindow mMoreOpreationWindow;
	
	private int total=0;
	private int page = 1;
	
	private int mDialgoWidth = 0;
	
	private Dialog mLoadingDialog;
	private Dialog mLoadingImageDialog;
	private TextView tipTextView;
	private TextView tipTextView1;
	App app;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 LogcatHelper.getInstance(this).start(); 
		setContentView(R.layout.activity_main);
		app = (App) this.getApplication();
		mReceiver = new Receiver();
	     IntentFilter intentFilter = new IntentFilter(Constants.INTENT_ACTION);
	     registerReceiver(mReceiver, intentFilter);
	     MyLog.d(TAG, "here is extSdCard==>" + Tool.getExtSDCardPath().size());
	     MyLog.d(TAG, "here is path==>" + Environment.getExternalStorageDirectory().getAbsolutePath());
	     File files = new File(Constants.IMAGE_PATH);
	     MyLog.d(TAG, "here is path==>" + files.getPath());
	     
	     File file = new File(Constants.SD_PATH);
	     	if (!file.exists() && Constants.IS_SERVER) {
				showToast("没有外置卡，请插入外置存储卡");
			}else {
			File file1 = new File(Constants.IMAGE_PATH);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			 File file2 = new File(Constants.FILE_PATH);
				if (!file2.exists()) {
					file2.mkdirs();
				}
		}

		
		initView();
		getData();
	}
	
	private void initView() {
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.get_data_progressbar, null);// 得到加载view
		tipTextView = (TextView) v.findViewById(R.id.tip_tv);// 提示文字
		tipTextView.setText(getString(R.string.image_loading,0,0));// 设置加载信息
		tipTextView1 = (TextView) v.findViewById(R.id.tip_tv_1);
		tipTextView1.setVisibility(View.VISIBLE);
		mLoadingImageDialog = new Dialog(this, R.style.MyDialogStyle);// 创建自定义样式dialog
		mLoadingImageDialog.setCancelable(true);// 不可以用“返回键”取消
		mLoadingImageDialog.setContentView(v);// 设置布局

		mRadioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		mRadioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		mRadioButton3 = (RadioButton) findViewById(R.id.radio_button3);
		mRadioButton4 = (RadioButton) findViewById(R.id.radio_button4);
		
		erCodeIv = (ImageView) findViewById(R.id.top_back);
		if (Constants.IS_SERVER) {
			erCodeIv.setVisibility(View.INVISIBLE);
			mRadioButton1.setText("全部");
		} else {
			erCodeIv.setVisibility(View.VISIBLE);
			mRadioButton1.setText("收藏");
		}
		
		erCodeIv.setImageResource(R.drawable.er_code);
		titleTv = (TextView) findViewById(R.id.top_title);
		titleTv.setText(getString(R.string.app_name));
		rightIv = (ImageView) findViewById(R.id.top_right);
		rightIv.setVisibility(View.VISIBLE);
		rightIv.setImageResource(R.drawable.more_oper);
		titleView = (View) findViewById(R.id.top_layout);
		mLoadingDialog = Tool.createLoadingDialog(MainActivity.this, getString(R.string.data_loading));
		
		mainTab = (RadioGroup) findViewById(R.id.main_tab);
		mainTab.setOnCheckedChangeListener(this);
		tabhost = getTabHost();

		mHomepage = new Intent(this, MainCollectActivity.class);
		tabhost.addTab(tabhost.newTabSpec("mHomepage").setIndicator("", null)
				.setContent(mHomepage));

		mMessage = new Intent(this, MainBookActivity.class);
		tabhost.addTab(tabhost.newTabSpec("mMessage").setIndicator("", null)
				.setContent(mMessage));
		mClassify = new Intent(this, MainListenActivity.class);
		mClassify.putExtra("type", "fenlei");
		tabhost.addTab(tabhost.newTabSpec("mClassify").setIndicator("", null)
				.setContent(mClassify));
		mDownload = new Intent(this, MainDownLoadActivity.class);
		mDownload.putExtra("type", "gouwuche");
		tabhost.addTab(tabhost.newTabSpec("mDownload").setIndicator("", null)
				.setContent(mDownload));
		
		mainTab.getChildAt(0).setSelected(true);
		
		erCodeIv.setOnClickListener(this);
		fDb = FinalDb.create(MainActivity.this,true);
		rightIv.setOnClickListener(this);
		
		View view = getLayoutInflater().inflate(R.layout.business_detail_operation_window,
				null, false);
		mMoreOpreationWindow = new PopupWindow(view,
				//android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				 Tool.getwindowWidth(getApplicationContext())/3,
				 LayoutParams.WRAP_CONTENT, true);// 创建PopupWindow实例
		
		Button button1 = (Button) view.findViewById(R.id.btn_about_us);
		Button button2 = (Button) view.findViewById(R.id.btn_update_res);
		Button button3 = (Button) view.findViewById(R.id.btn_get_app);
//		Button button4 = (Button) view.findViewById(R.id.btn_seat_set);
//		Button button5 = (Button) view.findViewById(R.id.btn_fault_report);
//		Button button6 = (Button) view.findViewById(R.id.btn_password_reset);
//		Button button7 = (Button) view.findViewById(R.id.btn_recharge);
		View service_layout = (View) view.findViewById(R.id.service_layout);
		if (Constants.IS_SERVER) {
			service_layout.setVisibility(View.VISIBLE);
		} else {
			service_layout.setVisibility(View.GONE);
		}
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
//		button4.setOnClickListener(this);
//		button5.setOnClickListener(this);
//		button6.setOnClickListener(this);
//		button7.setOnClickListener(this);
		
		
		mMoreOpreationWindow.setBackgroundDrawable(new BitmapDrawable());
		mMoreOpreationWindow.setOutsideTouchable(true);
		mDialgoWidth = mMoreOpreationWindow.getWidth();
		
		
	}
	private void getData() {

		Intent intent = new Intent(MainActivity.this, HttpService.class);
		startService(intent);
		ArrayList<ListenBook> downLoadBooks =(ArrayList<ListenBook>)fDb.findAllByWhere(ListenBook.class, " isdownload='0'");
		for (int i = 0; i < downLoadBooks.size(); i++) {
			MyLog.d(TAG, "the unLoad book ===>" + downLoadBooks.get(i).getCode());
		}
		ArrayList<ListenBookFileBean> bookFileBeans =(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, "isDownload='0'");
		for (int i = 0; i < bookFileBeans.size(); i++) {
			MyLog.d(TAG, "the unLoad book file ===>" + bookFileBeans.get(i).getSrc());
		}

		/**2015-11-28 新加*/
//		Tool.copyDBToSDcrad();
	}
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button1:
			this.tabhost.setCurrentTab(0);
			currentView = 0;
			break;
		case R.id.radio_button2:
			this.tabhost.setCurrentTab(1);
			currentView = 1;
			break;
		case R.id.radio_button3:
			this.tabhost.setCurrentTab(2);
			currentView = 2;
			break;
		case R.id.radio_button4:
			this.tabhost.setCurrentTab(3);
			currentView = 3;
			Intent intent = new Intent();
			sendBroadcast(intent);
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			Intent intent4 = new Intent();  
			 intent4.setClass(MainActivity.this, MipcaActivityCapture.class);  
			 intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			 startActivity(intent4);
			break;

		case R.id.top_right:
			mMoreOpreationWindow.showAtLocation(v, Gravity.NO_GRAVITY,
					Tool.getwindowWidth(getApplicationContext())- mDialgoWidth-v.getWidth()/4, 
					titleView.getHeight() + Tool.getStatusBarHeight(getApplicationContext()) - 2);
			break;
		case R.id.btn_about_us:
			Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
			startActivity(intent);
			mMoreOpreationWindow.dismiss();
			break;
		case R.id.btn_update_res:
			if (!app.isDownLoading()) {
				 File file = new File("/mnt/extsd");
			     	if (!file.exists() && Constants.IS_SERVER) {
						showToast("没有外置卡，请插入外置存储卡");
					}else {
					mLoadingDialog.show();
					getUpdateData(Constants.WEN_ZI_TYPE);
					mMoreOpreationWindow.dismiss();
				
					MainActivity.this.tabhost.setCurrentTab(3);
					currentView = 3;
					mRadioButton4.setChecked(true);
					}
			} else {
				showToast("正在下载无法更新");
			}
			break;
		case R.id.btn_get_app:
			Uri uri = Uri.parse(Constants.GET_APP_URL);    
			Intent it = new Intent(Intent.ACTION_VIEW, uri);    
			startActivity(it);
			break;
		default:
			break;
		}
	}

    private void getUpdateData(final int type){
    	if (!Tool.getNetworkState(MainActivity.this)) {
    		if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
				mLoadingDialog.cancel();
			}
			return;
		}
    	
    	FinalHttp fh = new FinalHttp();
    	AjaxParams params = new AjaxParams();
    	params.put("page", page+"");
    	params.put("pagesize", Constants.PAGE_SIZE+"");
    	params.put("type", type+"");
    	fh.get(Constants.GET_BOOK_INFO_LIST_URL, params, new AjaxCallBack<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				MyLog.d(TAG, "here is error msg==>" + strMsg + errorNo);
				showToast(getString(R.string.http_error));
				getUpdateData(type);
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				MyLog.d(TAG, t.toString());
				ListenBookManager manager = new ListenBookManager();
				manager = manager.praseJSONObject(t.toString());
				MyLog.d(TAG, "here is status ===>" + manager.getBaseBean().getCode());
				if (manager.getBaseBean().getCode()==1) {
					if ( manager.getListenBooks().size()!=0) {
						boolean isStop = false;
						for (int i = 0; i < manager.getListenBooks().size(); i++) {
							MyLog.d(TAG, manager
									.getListenBooks()
									.get(i).
									getName()+"");
							
							if (fDb.findById(manager.getListenBooks().get(i).getCode(), ListenBook.class)==null) {
								fDb.save(manager.getListenBooks().get(i));
							} else {
								isStop = true;
								break;
							}
						}
						if (!isStop) {
							if (manager.getCurrent_total()+manager.getSkip_total()!=manager.getTotal()) {
								page+=1;
								getUpdateData(type);
							} else {
								if (type==Constants.WEN_ZI_TYPE) {
									page =1;
									getUpdateData(Constants.YIN_PIN_TYPE);
								} else {
									getBookFileInfo();
								}
								
							}
							
						} else {
							if (type==Constants.WEN_ZI_TYPE) {
								page =1;
								getUpdateData(Constants.YIN_PIN_TYPE);
							} else {
								getBookFileInfo();
							}
						}
					} else {
						getBookFileInfo();
					}
				} else {
					showToast(manager.getBaseBean().getMessage());
					if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
						mLoadingDialog.cancel();
					}
				}
				
				
				super.onSuccess(t);
			}
    		
    	});
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
	
	/**
	 * 获取书籍文件信息
	 */
	private void getBookFileInfo() {
		ArrayList<ListenBook> books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class, " isdownload='0'");
		int index = 0;
		page =1;
		if (books.size()!=0) {
			getInfo(index, books);
		} else {
			if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
				mLoadingDialog.cancel();
			}
			showToast("没有书籍可供更新");
			
		}
		
		
	}
	
	
	private void getInfo(final int index, final ArrayList<ListenBook> books) {
		if (!Tool.getNetworkState(MainActivity.this)) {
    		if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
				mLoadingDialog.cancel();
			}
			return;
		}
    	final ListenBook book = books.get(index);
    	FinalHttp fh = new FinalHttp();
    	AjaxParams params = new AjaxParams();
    	if (book.getBookType().equals("2")) {
    		params.put("type", book.getBookType()+"");
        	params.put("mainid", book.getCode());
		}
    	if (book.getBookType().equals("3") || book.getBookType().equals("5")) {
    		params.put("booktype", book.getBookType()+"");
        	params.put("bookid", book.getCode());
		}
    	params.put("page", page+"");
    	params.put("pagesize", Constants.PAGE_SIZE+"");
    	MyLog.d(TAG, "type==>" + book.getBookType()+"mainid==>" + book.getCode());
    	fh.get(Constants.GET_BOOK_LIST_URL, params, new AjaxCallBack<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				MyLog.d(TAG, "here is error msg==>" + strMsg + errorNo);
				showToast(getString(R.string.http_error));
				getFileInfoComplate();
				//getInfo(index, books);
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				MyLog.d(TAG, t.toString());
				ListenBookFileManager manager = new ListenBookFileManager();
				manager = manager.praseJSONObject(t.toString(),book.getCode());
				MyLog.d(TAG, "here is status ===>" + manager.getBaseBean().getCode());
				if (manager.getBaseBean().getCode()==1) {
					if ( manager.getFileBeans().size()!=0) {
						
						for (int i = 0; i < manager.getFileBeans().size(); i++) {
							MyLog.d(TAG, manager.getFileBeans().get(i).getTitle()+"");
							
							if (fDb.findById(manager.getFileBeans().get(i).getCode(), ListenBookFileBean.class)==null) {
								fDb.save(manager.getFileBeans().get(i));
							} 
						}
						
							if (manager.getCurrent_total()+manager.getSkip_total()!=manager.getTotal()) {//
								page+=1;
								getInfo(index, books);
							} else {
								if (book.getBookType().equals("5")) {
									//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
									ArrayList<ListenBookFileBean> files= (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, " mainCode='"+book.getCode()+"'");
//									ArrayList<ListenBookFileBean> files= (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, " mainCode="+book.getCode()+"");
									boolean isDown =false;
									for (int i = 0; i < files.size(); i++) {
										if (files.get(i).isDownload()==false) {
											isDown =false;
											break;
										} else {
											isDown =true;
										}
									}
									book.setIsdownload(isDown);
									fDb.update(book);
								}
								
								page =1;
								if (index < books.size()-1) {
									getInfo(index+1, books);
								} else {
									getFileInfoComplate();
								}
								
							}
							
						} else {
							page =1;
							if (index < books.size()-1) {
								getInfo(index+1, books);
							} else {
								getFileInfoComplate();
							}
						}
					} else {
						MyLog.d(TAG, "here is filed book==>" + book.getName() +"  " +manager.getBaseBean().getMessage());
					showToast(manager.getBaseBean().getMessage());
					if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
						mLoadingDialog.cancel();
					}
				}
				
				
				super.onSuccess(t);
			}
    		
    	});
	
	}
	private  void getFileInfoComplate() {
		if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
			mLoadingDialog.cancel();
		}
		getNeedDownLoadNum();
	}
	
	private void getNeedDownLoadNum() {
		int downloadNum = 0;
		int downloadFileNum = 0;
		ArrayList<ListenBook> books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class, " isdownload='0'");
		downloadNum = books.size();
		
		downloadFileNum = fDb.findAllByWhere(ListenBookFileBean.class, " isdownload='0'").size();
		if(downloadNum!=0){
			tipTextView.setText( getString(R.string.image_loading,downloadNum,0));
			
			createDownLoadDialog(getString(R.string.download_dialog_msg,downloadNum,downloadFileNum));
		} else {
			showToast("没有可以更新的资源");
		}
	}
	
	private void createDownLoadDialog(String msg) {
		final AlertDialog ad = new AlertDialog(MainActivity.this);
		ad.setTitle(getString(R.string.notice));
		ad.setMessage(msg);
		ad.setPositiveButton(getString(R.string.yes), new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ad.dismiss();
				goDownload();

			}
		});

		ad.setNegativeButton(getString(R.string.no), new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ad.dismiss();
			}
		});
		
	}
	
	
	protected void goDownload() {
		mLoadingImageDialog.show();
		Intent intent = new Intent(MainActivity.this, DownLoadService.class);
		startService(intent);
	}


	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String string = intent.getExtras().getString(Constants.INTENT_RECEIVER);
			MyLog.d(TAG, "here is Receiver inReceiver main");
			
			if (string.equals(Constants.SHOU_CANG)) {
				MainActivity.this.tabhost.setCurrentTab(0);
				currentView = 0;
				mRadioButton1.setChecked(true);
			}
			
			if (string.equals(Constants.SHU_JIA)) {
				MainActivity.this.tabhost.setCurrentTab(1);
				currentView = 1;
				mRadioButton2.setChecked(true);
			}
			
			if (string.equals(Constants.TING_SHU)) {
				MainActivity.this.tabhost.setCurrentTab(2);
				currentView = 2;
				mRadioButton3.setChecked(true);
			}
			
			if (string.equals(Constants.XIA_ZAI)) {
				MainActivity.this.tabhost.setCurrentTab(3);
				currentView = 3;
				mRadioButton4.setChecked(true);
			}
			
			if (string.equals(Constants.COVER_DOWN)) {
				int total = intent.getIntExtra("total", 0);
				int loading = intent.getIntExtra("loading", 0);
				tipTextView.setText( getString(R.string.image_loading,total,loading));
				if (total==loading) {
					if(mLoadingImageDialog!=null && mLoadingImageDialog.isShowing()){
						mLoadingImageDialog.dismiss();
					}
					
				}
			}
			
			if (string.equals(Constants.FILE_XIA_ZAI)) {
				MyLog.d(TAG, "*********file ===>");
				int total = intent.getIntExtra("total", 0);
				int loading = intent.getIntExtra("loading", 0);
				tipTextView.setText( getString(R.string.file_loading,total,loading));
				
			}
			
			if (string.equals(Constants.LOADING)) {
				String name = intent.getStringExtra("name");
				String loading = intent.getStringExtra("loading");
				tipTextView1.setText( getString(R.string.loading_detail,name,loading));
				
			}
			
			if (string.equals(Constants.XIA_ZAI_FAILURE)) {
				
				if(mLoadingImageDialog!=null && mLoadingImageDialog.isShowing()){
					mLoadingImageDialog.dismiss();
				}
				if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				
			}
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}

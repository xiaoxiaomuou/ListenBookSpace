package com.hwang.listenbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.util.ArrayList;

public class MainDownLoadActivity extends BaseActivity{

	private static final String TAG = "MainDownLoadActivity";
	
	@ViewInject(id=R.id.listview) ListView mListView;
	@ViewInject(id=R.id.null_tv) TextView nullTv;
	private FinalDb fDb;
	private ArrayList<ListenBookFileBean> bookFiles = new ArrayList<ListenBookFileBean>();
	private ArrayList<ListenBook> books = new ArrayList<ListenBook>();
	private DownLoadAdapter adapter;
	private String code = "";
	private int bookCurrent =0;
	private int fileCurrent =0;
	private Receiver mReceiver;
	private float downX;  //点下时候获取的x坐标
	private float upX;   //手指离开时候的x坐标
	private int candelete=-1;
	App app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_download);
		fDb = FinalDb.create(MainDownLoadActivity.this);
		mReceiver = new Receiver();
	     IntentFilter intentFilter = new IntentFilter(Constants.PHONE_INTENT_ACTION);
	     registerReceiver(mReceiver, intentFilter);
	     app = (App) this.getApplication();
	}
	
	@Override
	protected void onResume() {
		initView();
		if (Constants.IS_SERVER && bookFiles.size()!=0) {
			if (!app.isDownLoading()) {
				Intent intent3 = new Intent(MainDownLoadActivity.this, DownLoadService.class);
//				Intent intent3 = new Intent();
//				intent3.setAction("com.tingshu.PlayerService");
//				intent3.setPackage(getLocalClassName());
				startService(intent3);
			}
		}
		
		super.onResume();
	}

	private void initView() {
		
		getData();
		
	}

	private void getData() {
		books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class, " isdownload='0'");
		bookFiles = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, " isdownload='0'");
		if (books.size()!=0) {
			nullTv.setVisibility(View.GONE);
		} else {
			nullTv.setVisibility(View.VISIBLE);
		}
		adapter = new DownLoadAdapter(this);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	class DownLoadAdapter extends BaseAdapter{

		private Context context; // 运行上下文

		private LayoutInflater listContainer; // 视图容器
		
		public class ListItemView { // 自定义控件集合

			private ImageView cover;
			private TextView nameTv;
			private TextView downNumberTv;
			private TextView descTv;
			private TextView statusTv;
			private TextView deleteTv;
			private ProgressBar bar;

		}

		public DownLoadAdapter(Context context) {
			this.context = context;
			listContainer = LayoutInflater.from(context); // 创建视图容
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return books.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return books.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ListItemView listItemView = new ListItemView();
			if (convertView == null) {
				convertView = listContainer.inflate(R.layout.book_download_item, null);
				listItemView.cover = (ImageView) convertView.findViewById(R.id.cover_iv);
				listItemView.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
				listItemView.downNumberTv = (TextView) convertView.findViewById(R.id.down_number_tv);
				listItemView.descTv = (TextView) convertView.findViewById(R.id.desc_tv);
				listItemView.statusTv = (TextView) convertView.findViewById(R.id.status_tv);
				listItemView.bar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
				listItemView.deleteTv = (TextView) convertView.findViewById(R.id.delete_tv);
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}
			MyLog.d("bookAdapter", books.get(position).getName()+"");
//			Drawable drawable = Tool.getBitmapByWidth(vector.get(position).getCover(),
//					 Tool.getwindowWidth(context)/3-20, 
//					 (Tool.getwindowWidth(context)/3-20)*4/3, 0);
			//listItemView.cover.setImageDrawable(drawable);
			listItemView.cover.setImageBitmap(Tool.getImageThumbnail(books.get(position).getCover(),
					Tool.getwindowWidth(context)/3-20,(Tool.getwindowWidth(context)/3-20)*4/3));
			
			listItemView.nameTv.setText(books.get(position).getName());
			
			listItemView.downNumberTv.setText(books.get(position).getDownNumber() + "人下载");
//			listItemView.SizeTv.setText(getFileSize(books.get(position).get()));
			listItemView.descTv.setText(books.get(position).getDescription());
			if (!books.get(position).isIsdownload()) {
				if (code.equals(books.get(position).getCode())) {
					Drawable rightImg = getResources().getDrawable(R.drawable.downloading);
					// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
					rightImg.setBounds(0, 0, rightImg.getMinimumWidth(), rightImg.getMinimumHeight());
					listItemView.statusTv.setCompoundDrawables(null, rightImg, null, null);
					listItemView.statusTv.setText(getString(R.string.downloading));
					listItemView.bar.setMax(100);
					listItemView.bar.setProgress(bookCurrent);
					listItemView.bar.setSecondaryProgress(fileCurrent);
				} else {
					Drawable rightImg = getResources().getDrawable(R.drawable.wait_download);
					// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
					rightImg.setBounds(0, 0, rightImg.getMinimumWidth(), rightImg.getMinimumHeight());
					listItemView.statusTv.setCompoundDrawables(null, rightImg, null, null);
					listItemView.statusTv.setText(getString(R.string.wait_downloading));
					listItemView.bar.setMax(100);
					listItemView.bar.setProgress(0);
					listItemView.bar.setSecondaryProgress(0);
				}
			} else {
				Drawable rightImg = getResources().getDrawable(R.drawable.wait_download);
				// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
				rightImg.setBounds(0, 0, rightImg.getMinimumWidth(), rightImg.getMinimumHeight());
				listItemView.statusTv.setCompoundDrawables(null, rightImg, null, null);
				listItemView.statusTv.setText(getString(R.string.download_fail));
				listItemView.bar.setMax(100);
				listItemView.bar.setProgress(0);
				listItemView.bar.setSecondaryProgress(0);
				
			}
			if (candelete == position) {
				listItemView.deleteTv.setVisibility(View.VISIBLE);
				listItemView.statusTv.setVisibility(View.GONE);
			} else {
				listItemView.deleteTv.setVisibility(View.GONE);
				listItemView.statusTv.setVisibility(View.VISIBLE);
			}
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (!app.isDownLoading) {
						candelete = position;
						notifyDataSetChanged();
					} else {
						showToast("下载中无法进行操作");
					}
					
				}
			});
			
			listItemView.deleteTv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					deleteBook(position);
					candelete =-1;
				}
			});
			return convertView;
		}

		
			/**
			 * 删除书籍
			 * @param index
			 */
			private void deleteBook(int index) {
				//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
				ArrayList<ListenBookFileBean> files =
						(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
								"mainCode='"+ books.get(index).getCode()+"'");
//				ArrayList<ListenBookFileBean> files =
//						(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
//								"mainCode="+ books.get(index).getCode()+"");
				for (int i = 0; i < files.size(); i++) {
					File file = new File(files.get(i).getSrc());
					if (file.exists()) {
						file.delete();
						
					}
					
					fDb.delete(files.get(i));
				}
				if (!books.get(index).getCover().contains("http")) {
					File file2 = new File(books.get(index).getCover());
					if (file2.exists()) {
						file2.delete();
						
					}
				}
				fDb.delete(books.get(index));
				getData();
			}
		
		
	}

	public CharSequence getFileSize(String fileSize) {
		if(TextUtils.isEmpty(fileSize)){
			return "";
		} else {
			return fileSize+"MB";
		}
		
	}
	
	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String string = intent.getExtras().getString(Constants.INTENT_RECEIVER);
			
			
			if (string.equals(Constants.LOADING)) {
				code = intent.getStringExtra("code");
				bookCurrent = intent.getIntExtra("current", 0);
				fileCurrent = intent.getIntExtra("fileCurrent", 0);
				adapter.notifyDataSetChanged();
			}
			
			if (string.equals(Constants.PHONE_DOWNLOAD)) {
				MyLog.d(TAG, "here is download receiver******************* ");
				getData();
				candelete =-1;
			}
				
			
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
}

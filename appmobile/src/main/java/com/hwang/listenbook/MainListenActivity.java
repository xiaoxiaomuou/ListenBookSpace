package com.hwang.listenbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.hwang.listenbook.adapter.BookGridAdapter;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.BookOper;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;
import com.hwnag.listenbook.myView.BookOpertionWindow;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.util.ArrayList;

public class MainListenActivity extends BaseActivity{

	private static final String TAG = "MainListenActivity";
	
	@ViewInject(id=R.id.gridview) GridView mGridView;
	@ViewInject(id=R.id.null_tv) TextView nullTv;
	private FinalDb fDb;
	private BookGridAdapter adapter;
	private ArrayList<ListenBook> books = new ArrayList<ListenBook>();
	 private BookOpertionWindow mMenuWindow ;
	 private int selectIndex = -1;
	 private Context context = this;
	private  Dialog infoDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_listen);
		initView();
		
	}
	
	@Override
	protected void onResume() {
		getData();
		super.onResume();
	}

	private void initView() {
		// TODO Auto-generated method stub
		fDb = FinalDb.create(MainListenActivity.this,true);
		nullTv.setVisibility(View.VISIBLE);
		mGridView.setVerticalScrollBarEnabled(false);
//		mGridView.setVisibility(View.GONE);
		mMenuWindow = new BookOpertionWindow(MainListenActivity.this, MainListenActivity.this,3);
		// 显示窗口
		mMenuWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				
			}
		});
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				openFileList(arg2);
				
			}

		});
		
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectIndex = arg2;
				mMenuWindow.showAtLocation(
						MainListenActivity.this.findViewById(R.id.main),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				return true;
			}
		} );
	

	}

	private void getData() {
		// TODO Auto-generated method stub
		//books = (ArrayList<ListenBook>) fDb.findAll(ListenBook.class);
		books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class, 
				" bookType='" + Constants.YIN_PIN_TYPE +"' and isdownload = '1'");
		MyLog.d(TAG, "this is listen book list size===>" + books.size());
		if(books.size()!=0){
			nullTv.setVisibility(View.GONE);
		} else {
			nullTv.setVisibility(View.VISIBLE);
		}
		adapter = new BookGridAdapter(MainListenActivity.this, books);
		mGridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			cancelMenuWindow();
			break;

		case R.id.btn_read:
			openFileList(selectIndex);
			cancelMenuWindow();
			break;
		case R.id.btn_collect:
			collectBook(selectIndex);
			cancelMenuWindow();
			break;
		case R.id.btn_delete:
			deleteBook(selectIndex);
			cancelMenuWindow();
			break;
		case R.id.btn_ercode:
			Intent intent = new Intent(MainListenActivity.this, ErCodeActivity.class);
			intent.putExtra("book", books.get(selectIndex));
			startActivity(intent);
			break;
		case R.id.btn_info:
			cancelMenuWindow();
			infoDialog = Tool.createBookInfoDialog(context, books.get(selectIndex), MainListenActivity.this);
			infoDialog.show();
			
			break;
			
		case R.id.btn_copy:
			
			
		case R.id.clickbtn:
			if (infoDialog!=null && infoDialog.isShowing()) {
				infoDialog.dismiss();
				BookOper.openBook(books.get(selectIndex),context, fDb);
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	private void openFileList(int arg2) {
		ListenBook book = books.get(arg2);
		
		Intent intent = new Intent(MainListenActivity.this,FileListActivity.class);
		intent.putExtra("book", book);
		startActivity(intent);
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
//		ArrayList<ListenBookFileBean> files =
//				(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
//						"mainCode="+ books.get(index).getCode()+"");
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
		books.remove(books.get(index));
		adapter.notifyDataSetChanged();
	}

	/**
	 * 收藏书籍
	 * @param index
	 */
	private void collectBook(int index) {
		ListenBook book = books.get(index);
		if (!book.isCollect()) {
			book.setCollect(true);
			fDb.update(book);
			books.get(index).setCollect(true);
			showToast("收藏成功！");
		} else {
			showToast("已收藏书籍无法收藏！");
		}
		
	}

	private void cancelMenuWindow() {
		if (mMenuWindow!=null && mMenuWindow.isShowing()) {
			mMenuWindow.dismiss();
		}
	}


}

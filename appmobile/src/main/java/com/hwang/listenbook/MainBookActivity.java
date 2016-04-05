package com.hwang.listenbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.hwang.listenbook.adapter.ReadBookGridAdapter;
import com.hwang.listenbook.bean.BookListBean;
import com.hwang.listenbook.util.BookOper;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwnag.listenbook.myView.BookOpertionWindow;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;

public class MainBookActivity extends BaseActivity{

	private static final String TAG = "MainBookActivity";
	@ViewInject(id=R.id.gridview) GridView mGridView;
	@ViewInject(id=R.id.null_tv) TextView nullTv;
	private FinalDb fDb;
	private ReadBookGridAdapter adapter;
//	private ArrayList<ListenBook> books = new ArrayList<>();
	private ArrayList<BookListBean> books = new ArrayList<>();
	 private BookOpertionWindow mMenuWindow ;
	 private int selectIndex = -1;
	 private Context context = this;
	 private  Dialog infoDialog;
	 private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int info = msg.what;
				if (info == 1) {
					getData();
				}
			}
		};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_listen);
		
		initView();
		
	}
	
	@Override
	protected void onResume() {
		handler.sendEmptyMessage(1);
		super.onResume();
	}

	private void initView() {
		// TODO Auto-generated method stub
		fDb = FinalDb.create(this,true);
		mGridView.setVerticalScrollBarEnabled(false);

		mMenuWindow = new BookOpertionWindow(MainBookActivity.this, MainBookActivity.this,1);
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
//				BookOper.openBook(books.get(arg2),context, fDb);
				BookOper.openReadBook(books.get(arg2), context, fDb);
			}


		});
		
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   int arg2, long arg3) {
				if (!Constants.IS_SERVER) {
					selectIndex = arg2;
					mMenuWindow.showAtLocation(
							MainBookActivity.this.findViewById(R.id.main),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				}

				return true;
			}
		});
	}

	private void getData() {
		// TODO Auto-generated method stub
//		books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class,
//				"( bookType='" + Constants.PDF_TYPE +"' or bookType='" + Constants.TEXT_TYPE +"') and isdownload = '1'","sort");
		books = (ArrayList<BookListBean>) fDb.findAllByWhere(BookListBean.class,"coverDownloaded=1 and book in (SELECT book FROM com_hwang_listenbook_bean_BookBean where fileDownloaded=1)");

		MyLog.e(TAG, "this is listen book list size===>" + books.size());
		if(books.size()!=0){
			nullTv.setVisibility(View.GONE);
		} else {
			nullTv.setVisibility(View.VISIBLE);
		}
//		adapter = new BookGridAdapter(this, books);
		adapter = new ReadBookGridAdapter(this, books);
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
			BookOper.openReadBook(books.get(selectIndex), context, fDb);
			cancelMenuWindow();
			break;
		/*case R.id.btn_collect:
			collectBook(selectIndex);
			cancelMenuWindow();
			break;
		case R.id.btn_delete:
			deleteBook(selectIndex);
			cancelMenuWindow();
			break;*/
		case R.id.btn_ercode:
			Intent intent = new Intent(MainBookActivity.this, ErCodeActivity.class);
			intent.putExtra("book", books.get(selectIndex));
			startActivity(intent);
			break;
		/*case R.id.btn_info:
			cancelMenuWindow();
			infoDialog = Tool.createBookInfoDialog(context, books.get(selectIndex), MainBookActivity.this);
			infoDialog.show();
			
			break;*/
			
		case R.id.clickbtn:
			if (infoDialog!=null && infoDialog.isShowing()) {
				infoDialog.dismiss();
				BookOper.openReadBook(books.get(selectIndex), context, fDb);
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	/**
	 * 删除书籍
	 */
/*	private void deleteBook(int index) {
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
	}*/

	/**
	 * 收藏书籍
	 */
/*	private void collectBook(int index) {
		ListenBook book = books.get(index);
		if (!book.isCollect()) {
			book.setCollect(true);
			fDb.update(book);
			books.get(index).setCollect(true);
			showToast("收藏成功！");
		} else {
			showToast("已收藏书籍无法收藏！");
		}
		
	}*/

	private void cancelMenuWindow() {
		if (mMenuWindow!=null && mMenuWindow.isShowing()) {
			mMenuWindow.dismiss();
		}
	}

}

package com.hwang.listenbook.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.hwang.listenbook.FileListActivity;
import com.hwang.listenbook.bean.BookBean;
import com.hwang.listenbook.bean.BookListBean;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;

public class BookOper {

	private static final String TAG ="BookOper";
	/**
	 * 打开书籍
	 * @param book 参数
	 */
	public static void openBook(ListenBook book,Context context, FinalDb fDb) {
		
		MyLog.d(TAG, "here is book click==>" + book.getName() + " type===>" +book.getBookType());
		if (book.getBookType().equals(Constants.PDF_TYPE+"")){
			Uri uri ;
			//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
			ArrayList<ListenBookFileBean> beans = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class," mainCode='"+ book.getCode()+"'");
//			ArrayList<ListenBookFileBean> beans = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class," mainCode="+ book.getCode()+"");
			MyLog.d(TAG, "file list size==>" +beans.size());
			if (beans.size()!=0) {
				MyLog.d(TAG, "here is book file==>" + beans.get(0).getSrc());
				uri = Uri.parse(beans.get(0).getSrc());
				
				Intent intent = new Intent(context, MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.putExtra("name", book.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setData(uri);
				context.startActivity(intent);
				
			} else {
				showToast("文件未下载！",context);
			}
		} else if(book.getBookType().equals(Constants.YIN_PIN_TYPE+"")){
			Intent intent = new Intent(context,FileListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("book", book);
			context.startActivity(intent);
		} else {//打开txt文件
			Intent intent = new Intent(context,FileListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("book", book);
			context.startActivity(intent);
		}
	}
	
	/**
	 * 显示提示信息
	 * 
	 * @param info 提示信息内容
	 */
	public static void showToast(String info,Context context) {
		try {
			Toast.makeText(context, info, Toast.LENGTH_SHORT).show();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void openReadBook(BookListBean book,Context context, FinalDb fDb) {
		ArrayList<BookBean> beans = (ArrayList<BookBean>) fDb.findAllByWhere(BookBean.class," book='"+ book.getBook()+"'");
		if (beans.size()>0) {
			BookBean bean = beans.get(0);
			int viewedNums = bean.getViewedNumber();
			bean.setViewedNumber(++viewedNums);
//			bean.setFileDownloaded(true);
//			bean.setFile(bean.getFile());
			fDb.update(bean);
//			Uri uri = Uri.parse(Constants.FILE_BOOK_PATH +bean.getBook()+"/"+ bean.getFile());
			Uri uri = Uri.parse(Constants.FILE_BOOK_PATH +bean.getBook()+"/"+ Tool.getFileName(bean.getFileNative()));
			Intent intent = new Intent(context, MuPDFActivity.class);
//			Intent intent = new Intent(context, BookPdfDetailActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtra("name", book.getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setData(uri);
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "文件未下载！" , Toast.LENGTH_SHORT).show();
		}
	}
}

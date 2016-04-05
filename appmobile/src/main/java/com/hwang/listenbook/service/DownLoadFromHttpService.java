package com.hwang.listenbook.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.hwang.listenbook.App;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.ArrayList;

public class DownLoadFromHttpService extends Service{

	private static final String TAG = "DownLoadFromHttpService";
	private ArrayList<ListenBookFileBean> bookFileBeans;
	private ArrayList<ListenBook> downLoadBooks ;
	private int index = 0;
	private int fileIndex =0;
	private Context context =this;
	private FinalDb fDb;
	private String mainCode;
	private boolean isLoading = false;
	App app;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		app = (App) this.getApplication();;
		fDb = FinalDb.create(getApplicationContext());
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		
		downLoadBooks =(ArrayList<ListenBook>)fDb.findAllByWhere(ListenBook.class, " isdownload='0'");
		if (downLoadBooks.size()!=0 ) {
//			if (!isLoading) {
				startDownLoadBook();
//			}
			
		}else {
			Toast.makeText(getApplicationContext(), "无资源可供下载", Toast.LENGTH_SHORT).show();
		}
		

		super.onStart(intent, startId);
	}
	
	private void startDownLoadBook() {
		app.setDownLoading(true);
		MyLog.d(TAG, "book doooooooooooooooooooownload!");
		Intent intent = new Intent();
		intent.setAction(Constants.PHONE_INTENT_ACTION);
		intent.putExtra(Constants.INTENT_RECEIVER, Constants.PHONE_DOWNLOAD);//更改购物车下标显示数量
		sendBroadcast(intent);
		
		if (downLoadBooks.size()!=0) {
			index=0;
			goDownloadBook();
		}
		
	}

	private void goDownloadBook() {
		ListenBook book= downLoadBooks.get(index);
		//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
		bookFileBeans =(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, "mainCode='"+ book.getCode() +"' and isDownload='0'");
//		bookFileBeans =(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, "mainCode="+ book.getCode() +" and isDownload='0'");
		fileIndex =0;
		goFileDownload();
	}
	
	private void goFileDownload() {
		// TODO Auto-generated method stub
		if (!Tool.getNetworkState(context)) {
			return;
		}
		final FinalHttp fHttp =new FinalHttp();
		final ListenBookFileBean bookBean = bookFileBeans.get(fileIndex);
		final String path = bookBean.getSrc();
		if (path!=null && path.contains("http://")) {
			
			fHttp.download(path,
					Constants.FILE_PATH + Tool.getFileName(path), false, new AjaxCallBack<File>(){

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							
							if (errorNo==416) {
								bookBean.setSrc(Constants.FILE_PATH + Tool.getFileName(path));
								bookBean.setDownload(true);
								fDb.update(bookBean);
							} else {
								bookBean.setSuccess(false);
								fDb.update(bookBean);
							}
							bookFileBeans.remove(bookBean);
							Intent intent = new Intent();
							intent.setAction(Constants.PHONE_INTENT_ACTION);
							intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADING);//更改购物车下标显示数量
							intent.putExtra("current", fileIndex*100/bookFileBeans.size());
							intent.putExtra("fileCurrent", 0);
							intent.putExtra("code", bookFileBeans.get(fileIndex).getMainCode());
							context.sendBroadcast(intent);
							if(fileIndex < bookFileBeans.size()-1){
								fileIndex +=1;
								goFileDownload();
							} else {
								if (index < downLoadBooks.size()-1) {
									index +=1;
									goDownloadBook();
								} else {
									finishDownLoad();
									app.setDownLoading(false);
								}
							}
							MyLog.d(TAG, "failure  ==>"+strMsg+errorNo);
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							String msg = current+"/"+ count;
							MyLog.d(TAG,"the file is ==>" + msg);
							int cur=Integer.valueOf(current*100/count+"");
							Intent intent = new Intent();
							intent.setAction(Constants.PHONE_INTENT_ACTION);
							intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADING);
							intent.putExtra("fileCurrent", cur);
							intent.putExtra("current", fileIndex*100/bookFileBeans.size());
							intent.putExtra("code", bookFileBeans.get(fileIndex).getMainCode());
							context.sendBroadcast(intent);
							super.onLoading(count, current);
						}

						@Override
						public void onSuccess(File t) {
							MyLog.d(TAG, "success  ==>"+Constants.FILE_PATH + Tool.getFileName(path));
							bookBean.setSrc(Constants.FILE_PATH + Tool.getFileName(path));
							bookBean.setDownload(true);
							fDb.update(bookBean);
							
							Intent intent = new Intent();
							intent.setAction(Constants.PHONE_INTENT_ACTION);
							intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADING);//更改购物车下标显示数量
							intent.putExtra("current", (fileIndex+1)*100/bookFileBeans.size());
							intent.putExtra("fileCurrent", 0);
							intent.putExtra("code", bookFileBeans.get(fileIndex).getMainCode());
							sendBroadcast(intent);
							
							if(fileIndex < bookFileBeans.size()-1){
								fileIndex +=1;
								goFileDownload();
							} else {
								if (index < downLoadBooks.size()-1) {
									doChangeState();
									index +=1;
									goDownloadBook();
								} else {
									finishDownLoad();
									app.setDownLoading(false);
								}
							}
							
							super.onSuccess(t);
						}

						
	
					});
			} else {
				bookBean.setDownload(true);
				bookFileBeans.remove(bookBean);
				fDb.update(bookBean);
				if(fileIndex < bookFileBeans.size()-1){
					index +=1;
					goFileDownload();
				} else {
					finishDownLoad();
					app.setDownLoading(false);
				}
			}
	}

	private void finishDownLoad() {
		for (int i = 0; i < downLoadBooks.size(); i++) {
			ListenBook book = downLoadBooks.get(i);
			//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
			ArrayList<ListenBookFileBean> files= (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, " mainCode='"+book.getCode()+"'");
//			ArrayList<ListenBookFileBean> files= (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, " mainCode="+book.getCode()+"");
			boolean isDown =false;
			for (int j = 0; j < files.size(); j++) {
				if (files.get(j).isDownload()==false) {
					isDown =false;
					break;
				} else {
					isDown =true;
				}
			}
			book.setIsdownload(isDown);
			fDb.update(book);
		}
			Intent intent = new Intent();
			intent.setAction(Constants.PHONE_INTENT_ACTION);
			intent.putExtra(Constants.INTENT_RECEIVER, Constants.PHONE_DOWNLOAD);
			sendBroadcast(intent);
		
		
	}

	private void doChangeState() {
		
		boolean isDown =false;
		boolean isSuccess = true;
		for (int j = 0; j < bookFileBeans.size(); j++) {
			if (bookFileBeans.get(j).isDownload()==false) {
				isDown =false;
				break;
			} else {
				isDown =true;
			}
			if (bookFileBeans.get(j).isSuccess()==false) {
				isSuccess =false;
				break;
			} else {
				isSuccess =true;
			}
		}
		downLoadBooks.get(index).setIsdownload(isDown);
		downLoadBooks.get(index).setSuccess(isSuccess);
		fDb.update(downLoadBooks.get(index));
		Intent intent = new Intent();
		intent.setAction(Constants.PHONE_INTENT_ACTION);
		intent.putExtra(Constants.INTENT_RECEIVER, Constants.PHONE_DOWNLOAD);
		sendBroadcast(intent);
	}

}

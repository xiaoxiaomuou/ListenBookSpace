package com.hwang.listenbook;

import java.io.File;
import java.util.ArrayList;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.renderscript.Element;

/**
 * 服务器更新下载
 * @author wanghao
 *
 */
public class DownLoadService extends Service{

	private static final String TAG = "DownLoadService";
	private FinalDb fDb;
	private ArrayList<ListenBook> downLoadBooks;
	private ArrayList<ListenBookFileBean> bookFileBeans;
	private int index = 0;
	private int fileIndex =0;
	private Context context =this;
	private FinalHttp fHttp;
	App app;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		app =(App) this.getApplication();
		fHttp =new FinalHttp();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		index = 0;
		fDb = FinalDb.create(getApplicationContext());
		downLoadBooks =(ArrayList<ListenBook>)fDb.findAllByWhere(ListenBook.class, " isdownload='0'");
		
		if (downLoadBooks.size()!=0) {
			goDownload();
			app.setDownLoading(true);
		}
		

		super.onStart(intent, startId);
	}

	protected void goDownload() {
		if (!Tool.getNetworkState(DownLoadService.this)) {
			return;
		}
		
//		final FinalHttp fHttp =new FinalHttp();
		final ListenBook book = downLoadBooks.get(index);
		final String path = downLoadBooks.get(index).getCover();
		MyLog.d(TAG, "the book cover===>" +path);
		if (path.contains("http://")) {
			
			fHttp.download(path,
					Constants.IMAGE_PATH + Tool.getFileName(path), false, new AjaxCallBack<File>(){

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							// TODO Auto-generated method stub
							MyLog.d(TAG, "failure  ==>"+strMsg+errorNo);
							if (errorNo==416) {
								book.setCover(Constants.IMAGE_PATH + Tool.getFileName(path));
								fDb.update(book);
								Intent intent = new Intent();
								intent.setAction(Constants.INTENT_ACTION);
								intent.putExtra(Constants.INTENT_RECEIVER, Constants.COVER_DOWN);//更改购物车下标显示数量
								intent.putExtra("loading", index+1);
								intent.putExtra("total", downLoadBooks.size());
								sendBroadcast(intent);
								
								if(index < downLoadBooks.size()-1){
									index +=1;
									goDownload();
								} else {
									startDownLoadBook();
								}
							} else {
								finishDownLoad();
							}
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							String msg = current+"/"+ count;
							MyLog.d(TAG,"the file is ==>" + msg);
							Intent intent = new Intent();
							intent.setAction(Constants.INTENT_ACTION);
							intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADING);
							intent.putExtra("loading", current*100/count+"%");
							intent.putExtra("name", index+1+"");
							sendBroadcast(intent);
							super.onLoading(count, current);
						}

						@Override
						public void onSuccess(File t) {
							MyLog.d(TAG, "success  ==>"+Constants.IMAGE_PATH + Tool.getFileName(path));
							book.setCover(Constants.IMAGE_PATH + Tool.getFileName(path));
							fDb.update(book);
							
							Intent intent = new Intent();
							intent.setAction(Constants.INTENT_ACTION);
							intent.putExtra(Constants.INTENT_RECEIVER, Constants.COVER_DOWN);//更改购物车下标显示数量
							intent.putExtra("loading", index+1);
							intent.putExtra("total", downLoadBooks.size());
							sendBroadcast(intent);
							if(index < downLoadBooks.size()-1){
								index +=1;
								goDownload();
							} else {
								startDownLoadBook();
							}
							super.onSuccess(t);
						}
	
					});
			} else {
				if(index < downLoadBooks.size()-1){
					index +=1;
					goDownload();
				} else {
					startDownLoadBook();
				}
			}
	}

		private void startDownLoadBook() {
			Intent intent1 = new Intent();
			intent1.setAction(Constants.INTENT_ACTION);
			intent1.putExtra(Constants.INTENT_RECEIVER, Constants.XIA_ZAI_FAILURE);//更改购物车下标显示数量
			sendBroadcast(intent1);
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
			doGetMoreSize();
			ListenBook book= downLoadBooks.get(index);
			//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
			bookFileBeans =(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, "mainCode='"+ book.getCode() +"' and isDownload='0'");
//			bookFileBeans =(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, "mainCode="+ book.getCode() +" and isDownload='0'");
			fileIndex =0;
			goFileDownload();
		}

		private void goFileDownload() {
			MyLog.d(TAG, "herers is file download=======%%%%%%");
			if (!Tool.getNetworkState(DownLoadService.this)) {
				return;
			}
			
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
									doChangeState();
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
									doChangeState();
									if (index < downLoadBooks.size()-1) {
										
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
//				ArrayList<ListenBookFileBean> files= (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, " mainCode="+book.getCode()+"");
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
				sendBroadcast(intent);
				Intent intent1 = new Intent();
				intent1.setAction(Constants.INTENT_ACTION);
				intent1.putExtra(Constants.INTENT_RECEIVER, Constants.XIA_ZAI_FAILURE);//更改购物车下标显示数量
				sendBroadcast(intent1);
			
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
		
		@Override
		public void onDestroy() {
			
			super.onDestroy();
		}
		/**
		 * 获取更多空间
		 */
		private void doGetMoreSize(){
			File file = new File("/mnt/extsd");
			
			if (Tool.getAvailableExternalMemorySize(file)*10
					/Tool.getTotalExternalMemorySize(file)>9) {
				ArrayList<ListenBook> listenBooks =
						(ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class, 
								"ORDER BY 'addTime' ASC");
				deleteBook(listenBooks.get(0));
				doGetMoreSize();
			}
		}
		
		/**
		 * 删除书籍
		 * @param index
		 */
		private void deleteBook(ListenBook book) {
			//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
			ArrayList<ListenBookFileBean> files =
					(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
							"mainCode='"+ book.getCode()+"'");
//			ArrayList<ListenBookFileBean> files =
//					(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
//							"mainCode="+ book.getCode()+"");
			for (int i = 0; i < files.size(); i++) {
				File file = new File(files.get(i).getSrc());
				if (file.exists()) {
					file.delete();
					
				}
				
				fDb.delete(files.get(i));
			}
			if (!book.getCover().contains("http")) {
				File file2 = new File(book.getCover());
				if (file2.exists()) {
					file2.delete();
					
				}
			}
			fDb.delete(book);
			
		}
}

	

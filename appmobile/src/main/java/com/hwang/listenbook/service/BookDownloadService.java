package com.hwang.listenbook.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hwang.listenbook.bean.BookBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.ArrayList;

/**
 * <b>Copyright:</b>  Copyright © 2011-2014 www.aiweimob.com. All Rights Reserved<br/>
 * <b>Description:</b>  <br/>
 * <b>author:</b>  zcf <br/>
 * <b>data:</b>  2016/3/28 20:40<br/>
 *
 * @version :  V1.0 <br/>
 */
public class BookDownloadService extends Service {
    private Context context = this;
    private static final String TAG = "BookDownloadService";
    private FinalHttp finalHttp;
    private FinalDb fDb;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        finalHttp = new FinalHttp();
        fDb = FinalDb.create(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyLog.e(TAG,"onStartCommand");
        String bookIdStr = intent.getStringExtra(Constants.BOOK_ID);
        startDownload(bookIdStr);
        /**
         * 如果你实现onStartCommand()来安排异步工作或者在另一个线程中工作,
         * 那么你可能需要使用START_FLAG_REDELIVERY来 让系统重新发送一个intent。
         * 这样如果你的服务在处理它的时候被Kill掉, Intent不会丢失
         */
        return Service.START_FLAG_REDELIVERY;
    }

    private void startDownload(String bookId) {
        if (!Tool.getNetworkState(context)) {
            return;
        }
        ArrayList<BookBean> bookBeans = (ArrayList<BookBean>) fDb.findAllByWhere(BookBean.class,
                "book='" + bookId + "'");
        final BookBean bean = bookBeans.get(0);
        String urlPath = bean.getFileNative();
        urlPath = urlPath.replaceAll("/readOnline/","");

        String filePath = Constants.FILE_BOOK_PATH + bean.getBook();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String fileName = Tool.getFileName(bean.getFileNative());

        final String nativeFileUrl = filePath + "/" + fileName;
        MyLog.e(TAG, "url = " + urlPath + "\nnaUrl =" + nativeFileUrl);

        finalHttp.download(urlPath, nativeFileUrl, new AjaxCallBack<File>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Intent intent = new Intent();
                intent.setAction(Constants.INTENT_ACTION);
                intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADED);
                intent.putExtra(Constants.LOADED_RESULT_KEY,Constants.LOADED_ERROR_KEY);
                sendBroadcast(intent);
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                MyLog.e(TAG, "current=" + current + "|count=" + count);
                Intent intent = new Intent();
                intent.setAction(Constants.INTENT_ACTION);
                intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADING);
                intent.putExtra(Constants.DEFS_LOADING_CURRENT_KEY,current);
                intent.putExtra(Constants.DEFS_LOADING_COUNT_KEY,count);
                sendBroadcast(intent);
            }

            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                bean.setFile(fileName);
                bean.setFileDownloaded(true);
                fDb.save(bean);
                Intent intent = new Intent();
                intent.setAction(Constants.INTENT_ACTION);
                intent.putExtra(Constants.INTENT_RECEIVER, Constants.LOADED);
                intent.putExtra(Constants.LOADED_RESULT_KEY, Constants.LOADED_SUCCESS_KEY);
                sendBroadcast(intent);
            }
        });
    }
}

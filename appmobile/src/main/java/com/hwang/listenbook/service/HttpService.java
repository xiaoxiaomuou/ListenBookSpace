package com.hwang.listenbook.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.BookToJson;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.NanoHTTPD;

import net.tsz.afinal.FinalDb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HttpService extends Service{

	NanoHTTPD httpServer;
	private FinalDb db;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		db = FinalDb.create(HttpService.this);
		try {
			httpServer = new NanoHTTPD(8080,new File(Constants.SD_PATH),HttpService.this);
//			httpServer = new NanoHTTPD(8080,Environment.getExternalStorageDirectory(),HttpService.this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	public  String getBookInfo(String code){
		ListenBook book = db.findById(code, ListenBook.class);
		//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
		ArrayList<ListenBookFileBean> beans = (ArrayList<ListenBookFileBean>)db.findAllByWhere(ListenBookFileBean.class, "mainCode='"+code+"'");
//		ArrayList<ListenBookFileBean> beans = (ArrayList<ListenBookFileBean>)db.findAllByWhere(ListenBookFileBean.class, "mainCode="+code+"");

		return BookToJson.bookToJSON(book,beans);
	}
}

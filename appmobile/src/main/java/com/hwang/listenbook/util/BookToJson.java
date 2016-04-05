package com.hwang.listenbook.util;

import java.security.PublicKey;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;

public class BookToJson {

	public static String bookToJSON(ListenBook book,ArrayList<ListenBookFileBean> beans){
		JSONObject object = new JSONObject();
		try {
			if (book==null) {
				JSONObject statusObject = new JSONObject();
				
					statusObject.put("code", 0);
					statusObject.put("name", "FAILURE");
					statusObject.put("message", "没有该书籍信息");
				object.put("status", statusObject);
			} else {
				JSONObject statusObject = new JSONObject();
				
				statusObject.put("code", 1);
				statusObject.put("name", "SUCCESS");
				statusObject.put("message", "");
				
			object.put("status", statusObject);
				JSONObject dataObject = new JSONObject();
				dataObject.put("id", book.getCode());
				dataObject.put("name", book.getName());
				dataObject.put("author", book.getAuthor());
				dataObject.put("cover", getUrl(book.getCover()));
				dataObject.put("category", book.getCategory());
				dataObject.put("production", book.getProduction());
				dataObject.put("description", book.getDescription());
				dataObject.put("addtime", book.getAddTime());
				dataObject.put("booktype", book.getBookType());
				
				object.put("datas", dataObject);
				object.put("files", getJSONArrayByCode(beans));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyLog.d("JSON", ""+object.toString());
		return object.toString();
	}

	private static JSONArray getJSONArrayByCode(ArrayList<ListenBookFileBean> beans) {
		JSONArray array = new JSONArray();
		try {
			for (int i = 0; i < beans.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("id", beans.get(i).getCode());
				object.put("title", beans.get(i).getTitle());
				object.put("filesize", beans.get(i).getFileSize());
				object.put("sort", beans.get(i).getSort());
				object.put("src", getUrl(beans.get(i).getSrc()));
				object.put("addtime", beans.get(i).getDate());
				array.put(i, object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return array;
	}

	private static String getUrl(String url) {
		String targetUrl ="";
		if (!TextUtils.isEmpty(url)) {
			targetUrl =Constants.DOWNLOAD_URL + url.substring(url.indexOf(Constants.STORE_PATH));
		}
		
		return targetUrl;
	}
}

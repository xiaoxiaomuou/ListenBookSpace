package com.hwang.listenbook.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookFromAndroid {

	private BaseBean baseBean;
	private ListenBook listenBook;
	private ArrayList<ListenBookFileBean> bookFiles;
	public BaseBean getBaseBean() {
		return baseBean;
	}
	public void setBaseBean(BaseBean baseBean) {
		this.baseBean = baseBean;
	}
	public ListenBook getListenBook() {
		return listenBook;
	}
	public void setListenBook(ListenBook listenBook) {
		this.listenBook = listenBook;
	}
	public ArrayList<ListenBookFileBean> getBookFiles() {
		return bookFiles;
	}
	public void setBookFiles(ArrayList<ListenBookFileBean> bookFiles) {
		this.bookFiles = bookFiles;
	}
	
	public BookFromAndroid praseJSONObject(String string){
		BookFromAndroid bfa = new BookFromAndroid();
		try {
			JSONObject object = new JSONObject(string);
			if (object.has("status")) {
				BaseBean bean = new BaseBean();
				bean = bean.praseJSONObject(object.getString("status"));
				bfa.setBaseBean(bean);
			}
			
			
			if (object.has("datas")) {
				JSONObject object2= object.getJSONObject("datas");
			
					ListenBook book = new ListenBook();
					book=book.praseListenBookAndroid(object2);
					bfa.setListenBook(book);
			}
			if (object.has("files")) {
				JSONArray array = object.getJSONArray("files");
				ArrayList<ListenBookFileBean> bookFiles = new ArrayList<ListenBookFileBean>();
				for (int i = 0; i < array.length(); i++) {
					ListenBookFileBean book = new ListenBookFileBean();
					book = book.praseJSONObject(array.getJSONObject(i),bfa.getListenBook().getCode());
					bookFiles.add(book);
				}
				bfa.setBookFiles(bookFiles);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bfa = null;
		}
		return bfa;
	}
}

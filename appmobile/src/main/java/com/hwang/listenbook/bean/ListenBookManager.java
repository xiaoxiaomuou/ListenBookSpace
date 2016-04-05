package com.hwang.listenbook.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListenBookManager {

	private BaseBean baseBean;
	private int total;
	private int current_total;
	private int skip_total;
	private ArrayList<ListenBook> listenBooks = new ArrayList<ListenBook>();
	public BaseBean getBaseBean() {
		return baseBean;
	}
	public void setBaseBean(BaseBean baseBean) {
		this.baseBean = baseBean;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCurrent_total() {
		return current_total;
	}
	public void setCurrent_total(int current_total) {
		this.current_total = current_total;
	}
	public int getSkip_total() {
		return skip_total;
	}
	public void setSkip_total(int skip_total) {
		this.skip_total = skip_total;
	}
	public ArrayList<ListenBook> getListenBooks() {
		return listenBooks;
	}
	public void setListenBooks(ArrayList<ListenBook> listenBooks) {
		this.listenBooks = listenBooks;
	}
	
	public ListenBookManager praseJSONObject(String string){
		ListenBookManager manager = new ListenBookManager();
		try {
			JSONObject object = new JSONObject(string);
			if (object.has("status")) {
				BaseBean bean = new BaseBean();
				bean = bean.praseJSONObject(object.getString("status"));
				manager.setBaseBean(bean);
			}
			if (object.has("total")) {
				JSONObject obj = object.getJSONObject("total");
				if (obj.has("total")) {
					manager.setTotal(obj.getInt("total"));
				}
				if (obj.has("current_total")) {
					manager.setCurrent_total(obj.getInt("current_total"));
				}
				if (obj.has("skip_total")) {
					manager.setSkip_total(obj.getInt("skip_total"));
				}
			}
			
			if (object.has("datas")) {
				JSONArray array = object.getJSONArray("datas");
				ArrayList<ListenBook> books = new ArrayList<ListenBook>();
				for (int i = 0; i < array.length(); i++) {
					ListenBook book = new ListenBook();
					book=book.praseListenBook(array.getJSONObject(i));
					books.add(book);
				}
				manager.setListenBooks(books);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			manager = null;
		}
		return manager;
	}
}

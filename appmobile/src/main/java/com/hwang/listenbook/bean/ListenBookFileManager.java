package com.hwang.listenbook.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListenBookFileManager {

	private BaseBean baseBean;
	private int total;
	private int current_total;
	private int skip_total;
	private ArrayList<ListenBookFileBean> fileBeans = new ArrayList<ListenBookFileBean>();
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
	public ArrayList<ListenBookFileBean> getFileBeans() {
		return fileBeans;
	}
	public void setFileBeans(ArrayList<ListenBookFileBean> fileBeans) {
		this.fileBeans = fileBeans;
	}
	
	public ListenBookFileManager praseJSONObject(String string,String mainCode){
		ListenBookFileManager manager = new ListenBookFileManager();
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
				ArrayList<ListenBookFileBean> bookFiles = new ArrayList<ListenBookFileBean>();
				for (int i = 0; i < array.length(); i++) {
					ListenBookFileBean book = new ListenBookFileBean();
					book = book.praseJSONObject(array.getJSONObject(i),mainCode);
					bookFiles.add(book);
				}
				manager.setFileBeans(bookFiles);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			manager = null;
		}
		return manager;
	}
	
}

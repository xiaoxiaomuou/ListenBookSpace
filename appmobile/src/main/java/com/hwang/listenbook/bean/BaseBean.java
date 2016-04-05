package com.hwang.listenbook.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 基础返回解析类
 * @author wanghao
 *
 */
public class BaseBean {

	private int code;
	private String name;
	private String message;
	
	
	
	public int getCode() {
		return code;
	}



	public void setCode(int code) {
		this.code = code;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public BaseBean praseJSONObject(String str){
		BaseBean baseBean = new BaseBean();
		try {
			JSONObject object = new JSONObject(str);
			if (object.has("code")) {
				baseBean.setCode(object.getInt("code"));
			}
			if (object.has("name")) {
				baseBean.setName(object.getString("name"));
			}
			if (object.has("message")) {
				baseBean.setMessage(object.getString("message"));
			}
		} catch (JSONException e) {
			baseBean =null;
			e.printStackTrace();
		}
		return baseBean;
	}
}

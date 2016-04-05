package com.hwang.listenbook.bean;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hwang.listenbook.util.MyLog;

import net.tsz.afinal.annotation.sqlite.Id;

/**
 * 听书抽象类
 * @author wanghao
 *
 */
public class ListenBook implements Serializable{
	@Id(column="code")
	private String code;//唯一标识
	private String name;
	private String cover;
	private String category;
	private String production;
	private String description;
	private String bookType;
	private String author;
	private String addTime;
	private String publishdate;
	private String readCode;
	private int readPage;
	private int downNumber;
	private String isbn;
	private long sort;
	private boolean isCollect =false;
	private boolean isdownload = false;
	private boolean isSuccess =true;
	private int current;//当前播放记录点
	private String currentCode;//当前播放记录子文件id
	
	
	
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public String getCurrentCode() {
		return currentCode;
	}
	public void setCurrentCode(String currentCode) {
		this.currentCode = currentCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getProduction() {
		return production;
	}
	public void setProduction(String production) {
		this.production = production;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isCollect() {
		return isCollect;
	}
	public void setCollect(boolean isCollect) {
		this.isCollect = isCollect;
	}
	
	public String getBookType() {
		return bookType;
	}
	public void setBookType(String bookType) {
		this.bookType = bookType;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public ListenBook praseListenBook(JSONObject object){
		ListenBook book = new ListenBook();
		try {
			if (object.has("id")) {
				book.setCode(object.getString("id"));
			}
			if (object.has("name")) {
				book.setName(object.getString("name"));
			}
			if (object.has("cover")) {
				book.setCover(object.getString("cover"));
			}
			if (object.has("publishdate")) {
				book.setPublishdate(object.getString("publishdate"));
			}
			if (object.has("sort")) {
				
				book.setSort(object.getLong("sort"));
			}
			
			if (object.has("isbn")) {
				book.setIsbn(object.getString("isbn"));
			}
			if (object.has("author")) {
				book.setAuthor(object.getString("author"));
			}
			if (object.has("cateogry")) {
				JSONArray array = object.getJSONArray("cateogry");
				
				String category = "";
				if (array.length()!=0) {
					for (int i = 0; i < array.length(); i++) {
						if (i!=array.length()-1) {
							category += array.getString(i) + ",";
						} else {
							category += array.getString(i);
						}
						
					}
				} 
				MyLog.d("ListenBook", "category is ==>" + category);
				book.setCategory(category);
			}
			if (object.has("category")) {
				JSONArray array = object.getJSONArray("category");
				
				String category = "";
				if (array.length()!=0) {
					for (int i = 0; i < array.length(); i++) {
						if (i!=array.length()-1) {
							category += array.getString(i) + ",";
						} else {
							category += array.getString(i);
						}
						
					}
				} 
				MyLog.d("ListenBook", "category is ==>" + category);
				book.setCategory(category);
			}
			
			if (object.has("production")) {
				book.setProduction(object.getString("production"));
			} else if (object.has("publisher")) {
				book.setProduction(object.getString("publisher"));
			}
			if (object.has("description")) {
				book.setDescription(object.getString("description"));
			} else if(object.has("summary")){
				book.setDescription(object.getString("summary"));
			}
			if (object.has("booktype")) {
				book.setBookType(object.getString("booktype"));
				MyLog.d("prase", "the book type ===>" + object.getString("booktype"));
				MyLog.d("prase", "the book name ===>" + book.getName());
			} else {
				book.setBookType("2");
			}
			if (object.has("addtime")) {
				book.setAddTime(object.getString("addtime"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			book = null;
		}
		return book;
	}
	public boolean isIsdownload() {
		return isdownload;
	}
	public void setIsdownload(boolean isdownload) {
		this.isdownload = isdownload;
	}
	public String getPublishdate() {
		return publishdate;
	}
	public void setPublishdate(String publishdate) {
		this.publishdate = publishdate;
	}
	public int getDownNumber() {
		return downNumber;
	}
	public void setDownNumber(int downNumber) {
		this.downNumber = downNumber;
	}
	public String getReadCode() {
		return readCode;
	}
	public void setReadCode(String readCode) {
		this.readCode = readCode;
	}
	public int getReadPage() {
		return readPage;
	}
	public void setReadPage(int readPage) {
		this.readPage = readPage;
	}
	public long getSort() {
		return sort;
	}
	public void setSort(long sort) {
		this.sort = sort;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	
	public ListenBook praseListenBookAndroid(JSONObject object){
		ListenBook book = new ListenBook();
		try {
			if (object.has("id")) {
				book.setCode(object.getString("id"));
			}
			if (object.has("name")) {
				book.setName(object.getString("name"));
			}
			if (object.has("cover")) {
				book.setCover(object.getString("cover"));
			}
			if (object.has("publishdate")) {
				book.setPublishdate(object.getString("publishdate"));
			}
			if (object.has("sort")) {
				
				book.setSort(object.getLong("sort"));
			}
			
			if (object.has("isbn")) {
				book.setIsbn(object.getString("isbn"));
			}
			if (object.has("author")) {
				book.setAuthor(object.getString("author"));
			}
			if (object.has("category")) {
				 
				MyLog.d("ListenBook", "category is ==>" + object.getString("category"));
				book.setCategory(object.getString("category"));
			}
			
			if (object.has("production")) {
				book.setProduction(object.getString("production"));
			} else if (object.has("publisher")) {
				book.setProduction(object.getString("publisher"));
			}
			if (object.has("description")) {
				book.setDescription(object.getString("description"));
			} else if(object.has("summary")){
				book.setDescription(object.getString("summary"));
			}
			if (object.has("booktype")) {
				book.setBookType(object.getString("booktype"));
				MyLog.d("prase", "the book type ===>" + object.getString("booktype"));
				MyLog.d("prase", "the book name ===>" + book.getName());
			} else {
				book.setBookType("2");
			}
			if (object.has("addtime")) {
				book.setAddTime(object.getString("addtime"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			book = null;
		}
		return book;
	}
	
}

package com.hwang.listenbook.bean;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;

import org.json.JSONObject;

import com.hwang.listenbook.util.Constants;

import net.tsz.afinal.annotation.sqlite.Id;

/**
 * 听书文件抽象类
 * @author wanghao
 *
 */
public class ListenBookFileBean implements Serializable{

	@Id(column="code")
	private String code;//唯一标识
	private String mainCode;
	private String title;
	private String src;
	private int sort;
	private String date;
	private String fileSize;
	private String name;
	private String cover;
	private int downNumber;
	private boolean isDownload = false;
	private boolean isSuccess = true;
	
	
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	
	public ListenBookFileBean praseJSONObject(JSONObject object,String mainCode){
		ListenBookFileBean bean = new ListenBookFileBean();
		bean.setMainCode(mainCode);
		try {
			if (object.has("id")) {
				bean.setCode(object.getString("id"));
			}
			if (object.has("title")) {
				bean.setTitle(object.getString("title"));
			}
			if (object.has("src")) {
				bean.setSrc(object.getString("src"));
			}
			if (object.has("sort")) {
				bean.setSort(object.getInt("sort"));
			}
			if (object.has("content")) {
				
				String path = Constants.FILE_PATH + "/"+mainCode+"_"+sort+".txt";
				if(writeToTxt(object.getString("content"), path)){
					bean.setSrc(path);
					bean.setDownload(true);
				} else {
					bean.setDownload(false);
				}
			}
			if (object.has("addtime")) {
				bean.setDate(object.getString("addtime"));
			}
			if (object.has("filesize")) {
				bean.setFileSize(object.getString("filesize"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			bean = null;
		}
		
		return bean;
	}
	public String getMainCode() {
		return mainCode;
	}
	public void setMainCode(String mainCode) {
		this.mainCode = mainCode;
	}
	public boolean isDownload() {
		return isDownload;
	}
	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
	
	public boolean writeToTxt(String str,String path){
		boolean isSuccess =false;
        try {
            FileWriter fw = new FileWriter(path);
            fw.flush();
            fw.write(str);
            fw.close();
            isSuccess =true;
        } catch (Exception e) {
            e.printStackTrace();
            File file = new File(path);
            if (file.exists()) {
				file.delete();
			}
            isSuccess =false;
        }
        
        return isSuccess;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDownNumber() {
		return downNumber;
	}
	public void setDownNumber(int downNumber) {
		this.downNumber = downNumber;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}

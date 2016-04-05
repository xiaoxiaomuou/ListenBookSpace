package com.hwang.listenbook.bean;

import android.content.Context;

import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.annotation.sqlite.Id;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * <b>Copyright:</b>  Copyright © 2011-2014 www.aiweimob.com. All Rights Reserved<br/>
 * <b>Description:</b> 图书详情表 <br/>
 * <b>author:</b>  zcf <br/>
 * <b>data:</b>  2015/12/17 9:35<br/>
 *
 * @version :  V1.0 <br/>
 */
public class BookBean implements Serializable {

    @Id(column="id")
    private int id;	//主键
    private String book;//图书唯一标识(id)
    private String title;//图书名称(title)。特别说明，这里不使用name，是因为这里的值不是name，name在表BookListBean中
    private String file;//图书本地地址(fileurl)
    private String fileNative;//图书本地服务器端源地址(fileurl)
    private String fileUrl;//图书远程服务器源地址(fileurl)
    private String encryptType;//加密类型(encrypttype)
    private String userKey;//用户密码(userkey)
    private int sort ;//排序
    private int current;////当前播放记录点(这个是为音频书籍保留的)
    private String currentCode;////当前播放记录子文件id(同上，为音频书籍保留的)
    private boolean fileDownloaded = false; //书籍是否下载了
    private int downNumber =0;	//从本地大屏扫描下载的数量
    private int viewedNumber=0;	//当前图书被阅读的次数
    private boolean regular = true;   //当前图书是否正常
    private String rootDirectory; //存储时的根目录

    public BookBean parseJSONObject (JSONObject object,Context context) {
        BookBean bb = new BookBean();
        try {

            if (object.has("id")) {
                bb.setBook(object.getString("id"));
            }

            if (object.has("title")) {
                bb.setTitle(object.getString("title"));
//                bb.setRootDirectory(Tool.setStoragePath4Db(context));
            }

            if (object.has("fileurl")) {
                bb.setFileUrl(object.getString("fileurl"));
            }

            if (object.has("sort")) {
                Object sort = object.get("sort");
                bb.setSort(Tool.jsonInt2RealInt(sort));
            }

            if (object.has("fileNative")) {
                bb.setFileNative(object.getString("fileNative"));
            }

            if (object.has("encrypttype")) {
                bb.setEncryptType(object.getString("encrypttype"));
            }

            if (object.has("userkey")) {
                bb.setUserKey(object.getString("userkey"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            bb = null;
        }

        return bb;
    }

    public String getFileNative() {
        return fileNative;
    }

    public void setFileNative(String fileNative) {
        this.fileNative = fileNative;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

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

    public int getDownNumber() {
        return downNumber;
    }

    public void setDownNumber(int downNumber) {
        this.downNumber = downNumber;
    }

    public String getFile() {
        MyLog.e("getFile","返回的路径="+file);
        return file;
    }

    public void setFile(String file) {
        MyLog.e("setFile","设置路径="+file);
        this.file = file;
    }

    public boolean isFileDownloaded() {
        return fileDownloaded;
    }

    public void setFileDownloaded(boolean fileDownloaded) {
        this.fileDownloaded = fileDownloaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRegular() {
        return regular;
    }

    public void setRegular(boolean regular) {
        this.regular = regular;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViewedNumber() {
        return viewedNumber;
    }

    public void setViewedNumber(int viewedNumber) {
        this.viewedNumber = viewedNumber;
    }

    public String getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(String encryptType) {
        this.encryptType = encryptType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}

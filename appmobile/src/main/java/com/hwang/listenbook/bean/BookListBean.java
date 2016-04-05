package com.hwang.listenbook.bean;

import net.tsz.afinal.annotation.sqlite.Id;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <b>Copyright:</b>  Copyright © 2011-2014 www.aiweimob.com. All Rights Reserved<br/>
 * <b>Description:</b> 书籍简简介表 <br/>
 * <b>author:</b>  zcf <br/>
 * <b>data:</b>  2015/12/16 16:23<br/>
 * <b>version:</b>  V1.0 <br/>
 */
public class BookListBean implements Serializable {
    @Id(column="id")
    private int id; //主键
    private String book;//图书唯一标识(id)
    private String name;//图书标题(name)
    private String coverUrl;//图书封面源地址(cover)
    private String coverNativeUrl;//图书封面本地服务器源地址(cover)
    private String cover;//图书封面源地址(cover)
    private String publisher;//出版社(publisher)
    private String description;//简介(summary)
    private String bookType;//书籍类型(booktype)
    private String author;//作者(author)
    private String addTime;//添加时间(addtime)
    private String publishDate;//出版时间(publishdate)
    private String isbn;//isbn号(isbn)
    private int sort;//排序
    private boolean coverDownloaded = false;    //封面是否下载了
	private int clcCategory;	//中图法类别(clcCategory)
	private int comCategory;	//常用类别(comCategory)
    private ArrayList<BookBean> bookBeans;
    private boolean encrypted = false;//是否加密(isencrypt)
    private String rootDirectory; //存储时的根目录
//    private String quanPin4Name; //书籍标题的汉语全拼(自定义字段)
    private String pinyin;//拼音字段(预设 2016-3-2)
    private String wenXuanUrl;  //文轩云图线上订购链接

    public String getCoverNativeUrl() {
        return coverNativeUrl;
    }

    public void setCoverNativeUrl(String coverNativeUrl) {
        this.coverNativeUrl = coverNativeUrl;
    }

    public String getWenXuanUrl() {
        return wenXuanUrl;
    }

    public void setWenXuanUrl(String wenXuanUrl) {
        this.wenXuanUrl = wenXuanUrl;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public int getClcCategory() {
        return clcCategory;
    }

    public void setClcCategory(int clcCategory) {
        this.clcCategory = clcCategory;
    }

    public int getComCategory() {
        return comCategory;
    }

    public void setComCategory(int comCategory) {
        this.comCategory = comCategory;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public boolean isCoverDownloaded() {
        return coverDownloaded;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setCoverDownloaded(boolean coverDownloaded) {
        this.coverDownloaded = coverDownloaded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public ArrayList<BookBean> getBookBeans() {
        return bookBeans;
    }

    public void setBookBeans(ArrayList<BookBean> bookBeans) {
        this.bookBeans = bookBeans;
    }

}

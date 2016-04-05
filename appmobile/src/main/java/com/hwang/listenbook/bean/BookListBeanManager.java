package com.hwang.listenbook.bean;

import android.content.Context;

import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>Copyright:</b>  Copyright © 2011-2014 www.aiweimob.com. All Rights Reserved<br/>
 * <b>Description:</b> 书籍列表管理类 <br/>
 * <b>author:</b>  zcf <br/>
 * <b>data:</b>  2015/12/17 10:18<br/>
 *
 * @version :  V1.0 <br/>
 */
public class BookListBeanManager {
    private BaseBean baseBean;
    private BookListBean bookListBean;

    public BookListBeanManager parseJSONString (String data,FinalDb fDb,Context context) {
        BookListBeanManager blbm = new BookListBeanManager();

        try {
            JSONObject objectRoot = new JSONObject(data);

            if (objectRoot.has("status")) {
                BaseBean baseBean = new BaseBean();
                baseBean = baseBean.praseJSONObject(objectRoot.getString("status"));
                blbm.setBaseBean(baseBean);
            }

            if (objectRoot.has("data")) {
                BookListBean blb = new BookListBean();
                JSONObject object = new JSONObject(objectRoot.getString("data"));

                if (object.has("id")) {
                    blb.setBook(object.getString("id"));
//                    blb.setRootDirectory(Tool.setStoragePath4Db(context));
                }

                if (object.has("name")) {
                    String nameStr = Tool.jsonString2RealString(object.getString("name"));
                    blb.setName(nameStr);
                }

                if (object.has("description")){
                    blb.setDescription(object.getString("description"));
                }

                if (object.has("author")) {
                    blb.setAuthor(object.getString("author"));
                }

                if (object.has("publisher")) {
                    blb.setPublisher(object.getString("publisher"));
                }

                if (object.has("summary")) {
                    blb.setDescription(object.getString("summary"));
                }

                if (object.has("addtime")) {
                    blb.setAddTime(object.getString("addtime"));
                }

                if (object.has("publishdate")) {
                    blb.setPublishDate(object.getString("publishdate"));
                }

                if (object.has("booktype")) {
                    blb.setBookType(object.getString("booktype"));
                }

                if (object.has("coverUrl")) {
                    blb.setCoverUrl(object.getString("coverUrl"));
                }

                if (object.has("coverNative")) {
                    blb.setCoverNativeUrl(object.getString("coverNative"));
                }

                if (object.has("isbn")) {
                    blb.setIsbn(object.getString("isbn"));
                }

                if (object.has("sort")) {
                    blb.setSort(Tool.jsonInt2RealInt(object.get("sort")));
                }

                if (object.has("clccategory")) {
                    blb.setClcCategory(Tool.jsonInt2RealInt(object.get("clccategory")));
                }
                if (object.has("comcategory")) {
                    blb.setComCategory(Tool.jsonInt2RealInt(object.get("comcategory")));
                }

                if (object.has("isencrypt")) {
                    blb.setEncrypted(Tool.jsonBoolean2RealBoolean(object.get("isencrypt")));
                }
                
                
//                bookListBean = bookListBean.parseJSONObject(objectRoot.getString("data"));
                blbm.setBookListBean(blb);
            }

            if (objectRoot.has("files")) {
                JSONArray filesArr = objectRoot.getJSONArray("files");
//                    ArrayList<BookBean> bbs = new ArrayList<>();

                for (int i = 0 ,j=filesArr.length(); i < j; i++) {
                    BookBean bookBean = new BookBean();
                    bookBean = bookBean.parseJSONObject(filesArr.getJSONObject(i),context);
//                        if (fDb.findAllByWhere(BookBean.class,
//                                "sort='"+filesArr.getJSONObject(i).getInt("sort")+"'").size()==0)
                    fDb.save(bookBean);
//                        bbs.add(bookBean);

                }
//                    blb.setBookBeans(bbs);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            blbm = null;
        }

        return blbm;
    }

    public BaseBean getBaseBean() {
        return baseBean;
    }

    public void setBaseBean(BaseBean baseBean) {
        this.baseBean = baseBean;
    }

    public BookListBean getBookListBean() {
        return bookListBean;
    }

    public void setBookListBean(BookListBean bookListBean) {
        this.bookListBean = bookListBean;
    }
}

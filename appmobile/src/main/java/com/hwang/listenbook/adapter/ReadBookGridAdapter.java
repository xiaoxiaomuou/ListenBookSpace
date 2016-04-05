package com.hwang.listenbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hwang.listenbook.R;
import com.hwang.listenbook.bean.BookListBean;
import com.hwang.listenbook.util.BitmapUtils;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import java.util.ArrayList;

/**
 * <b>Copyright:</b>  Copyright © 2011-2014 www.aiweimob.com. All Rights Reserved<br/>
 * <b>Description:</b>  <br/>
 * <b>author:</b>  zcf <br/>
 * <b>data:</b>  2016/3/29 1:09<br/>
 *
 * @version :  V1.0 <br/>
 */
public class ReadBookGridAdapter extends BaseAdapter {
    private Context context; // 运行上下文

    private LayoutInflater listContainer; // 视图容器

    private ArrayList<BookListBean> vector;

    public class ListItemView { // 自定义控件集合
        private ImageView cover;
    }

    public ReadBookGridAdapter(Context context, ArrayList<BookListBean> vector) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容
        this.vector = vector;
    }

    @Override
    public int getCount() {
        return vector.size();
    }

    @Override
    public Object getItem(int position) {
        return vector.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = new ListItemView();
        if (convertView == null) {
            convertView = listContainer.inflate(R.layout.book_grid_item, null);
            listItemView.cover = (ImageView) convertView.findViewById(R.id.cover_iv);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        BookListBean bean = vector.get(position);
        MyLog.e("getView","position="+position);
        listItemView.cover.setImageBitmap(BitmapUtils.decodeSampledBitmapFromFd(Constants.FILE_BOOK_PATH + bean.getBook() + "/" + bean.getCover(),
                Tool.getwindowWidth(context) / 3 - 20, (Tool.getwindowWidth(context) / 3 - 20) * 4 / 3));
        return convertView;
    }
}

package com.hwang.listenbook.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwang.listenbook.R;
import com.hwang.listenbook.ShowBookDetailActivity;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import java.util.ArrayList;

public class BookGridAdapter extends BaseAdapter {

	private Context context; // 运行上下文

	private LayoutInflater listContainer; // 视图容器

	private ArrayList<ListenBook> vector;

	public class ListItemView { // 自定义控件集合

		private ImageView cover;
		private TextView nameTv;

	}

	public BookGridAdapter(Context context, ArrayList<ListenBook> vector) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容
		this.vector = vector;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return vector.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return vector.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = new ListItemView();
		if (convertView == null) {
			convertView = listContainer.inflate(R.layout.book_grid_item, null);
			listItemView.cover = (ImageView) convertView.findViewById(R.id.cover_iv);
			listItemView.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		MyLog.d("bookAdapter", vector.get(position).getName());
		listItemView.cover.setImageBitmap(Tool.getImageThumbnail(vector.get(position).getCover(),
				Tool.getwindowWidth(context)/3-20,(Tool.getwindowWidth(context)/3-20)*4/3));
		 if (Constants.IS_SERVER) {
			 listItemView.nameTv.setVisibility(View.VISIBLE);
		} else {
			listItemView.nameTv.setVisibility(View.GONE);
		}
		
		 listItemView.nameTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ShowBookDetailActivity.class);
				intent.putExtra("book", vector.get(position));
				context.startActivity(intent);
			}
		});
		 
		return convertView;
	}

}
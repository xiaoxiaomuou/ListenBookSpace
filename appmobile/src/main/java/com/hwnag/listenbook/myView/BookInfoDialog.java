package com.hwnag.listenbook.myView;

import net.tsz.afinal.FinalDb;

import com.hwang.listenbook.R;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.util.Constants;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookInfoDialog extends Dialog{
	 //定义回调事件，用于dialog的点击事件
    public interface OnCustomDialogListener{
             public void back();
     }
    Context context;
	private ListenBook book;
	private OnCustomDialogListener customDialogListener;
	public BookInfoDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	public BookInfoDialog(Context context,ListenBook book,
			OnCustomDialogListener customDialogListener){
		
		super(context);
		this.context = context;
		this.book = book;
		this.customDialogListener = customDialogListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 setContentView(R.layout.book_info_dialog);
         //设置标题
         TextView nameTv = (TextView)findViewById(R.id.name_tv);
         TextView authorTv = (TextView)findViewById(R.id.author_tv);
         TextView isbnTv = (TextView)findViewById(R.id.isbn_tv);
         TextView publishTv = (TextView)findViewById(R.id.publish_tv);
         TextView updateTimeTv = (TextView)findViewById(R.id.update_time_tv);
         TextView categoryTv = (TextView)findViewById(R.id.category_tv);
         TextView descTv = (TextView)findViewById(R.id.description_tv);
         if (book.getBookType().equals(Constants.YIN_PIN_TYPE+"")) {
 			isbnTv.setVisibility(View.GONE);
 			authorTv.setVisibility(View.GONE);
 			
 		}
         nameTv.setText(context.getString(R.string.book_info_name,book.getName()));
         authorTv.setText(context.getString(R.string.book_info_author,getString(book.getAuthor())));
         isbnTv.setText(context.getString(R.string.book_info_isbn,getString(book.getIsbn())));
         publishTv.setText(context.getString(R.string.book_info_publish,getString(book.getProduction())));
         updateTimeTv.setText(context.getString(R.string.book_info_update,getTime(book.getAddTime())));
         categoryTv.setText(context.getString(R.string.book_info_category,getString(book.getCategory())));
         descTv.setText(context.getString(R.string.book_info_desc,getString(book.getDescription())));
         Button clickBtn = (Button) findViewById(R.id.clickbtn);
         clickBtn.setOnClickListener(clickListener);
         
		super.onCreate(savedInstanceState);
	}

	private String getString(String string) {
		if (TextUtils.isEmpty(string)|| string.equals("null")) {
			return "未知";
		}
		return string;
	}


	private CharSequence getTime(String string) {
		if (!TextUtils.isEmpty(string) && !string.equals("null")) {
			String time = string.substring(0, string.indexOf("T"));
			return time;
		} else {
			return "未知";
		}
		
		
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
                customDialogListener.back();
            BookInfoDialog.this.dismiss();
        }
};

}

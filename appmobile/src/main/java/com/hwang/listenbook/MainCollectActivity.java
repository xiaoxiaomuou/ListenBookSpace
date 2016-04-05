package com.hwang.listenbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.hwang.listenbook.adapter.BookGridAdapter;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.BookOper;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;
import com.hwnag.listenbook.myView.BookOpertionWindow;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.util.ArrayList;

public class MainCollectActivity extends BaseActivity{

	public static final String TAG = "MainCollectActivity";

	@ViewInject(id=R.id.gridview) GridView mListView;
	@ViewInject(id=R.id.null_tv) TextView nullTv;
	private FinalDb fDb;
	private BookGridAdapter adapter;
	private ArrayList<ListenBook> books = new ArrayList<ListenBook>();
	private BookOpertionWindow mMenuWindow ;
	private int selectIndex = -1;
	private Context context = this;
	private  Dialog infoDialog;
	private int gravity=0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int info = msg.what;
			if (info == 1) {
				getData();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_collect);
		
		initView();
	}
	
	@Override
	protected void onResume() {
		handler.sendEmptyMessage(1);
		super.onResume();
	}

	private void initView() {
		if (Constants.IS_SERVER) {
			nullTv.setText("还没有书籍，赶快更新资源吧");
		}
		mListView.setVerticalScrollBarEnabled(false);
		fDb = FinalDb.create(this,true);
		mMenuWindow = new BookOpertionWindow(MainCollectActivity.this, MainCollectActivity.this,2);
		// 显示窗口
		mMenuWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				BookOper.openBook(books.get(arg2),context, fDb);
			}

			
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!Constants.IS_SERVER) {
					selectIndex = arg2;
					mMenuWindow.showAtLocation(
							MainCollectActivity.this.findViewById(R.id.main),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				}
				
				return true;
			}
		} );
		
	}
	
	private void getData() {
		// TODO Auto-generated method stub
		//books = (ArrayList<ListenBook>) fDb.findAll(ListenBook.class);
		if (Constants.IS_SERVER) {
			books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class,"isDownload='1'","sort");
		} else {
			books = (ArrayList<ListenBook>) fDb.findAllByWhere(ListenBook.class, "isCollect='1'");
		}
	
		MyLog.d(TAG, "this is listen book list size===>" + books.size());
		if(books.size()!=0){
			nullTv.setVisibility(View.GONE);
		} else {
			nullTv.setVisibility(View.VISIBLE);
		}
		adapter = new BookGridAdapter(this, books);
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			cancelMenuWindow();
			break;

		case R.id.btn_read:
			BookOper.openBook(books.get(selectIndex),context, fDb);
			cancelMenuWindow();
			break;
		case R.id.btn_collect:
			collectBook(selectIndex);
			cancelMenuWindow();
			break;
		case R.id.btn_delete:
			deleteBook(selectIndex);
			cancelMenuWindow();
			break;
		case R.id.btn_ercode:
			Intent intent = new Intent(MainCollectActivity.this, ErCodeActivity.class);
			intent.putExtra("book", books.get(selectIndex));
			startActivity(intent);
			break;
		case R.id.btn_info:
			cancelMenuWindow();
			infoDialog = Tool.createBookInfoDialog(context, books.get(selectIndex), MainCollectActivity.this);
			infoDialog.show();
			
			break;
			
		case R.id.clickbtn:
			if (infoDialog!=null && infoDialog.isShowing()) {
				infoDialog.dismiss();
				BookOper.openBook(books.get(selectIndex),context, fDb);
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	/**
	 * 删除书籍
	 * @param index
	 */
	private void deleteBook(int index) {
		//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
		ArrayList<ListenBookFileBean> files =
				(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
						"mainCode='"+ books.get(index).getCode()+"'");
//		ArrayList<ListenBookFileBean> files =
//				(ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
//						"mainCode="+ books.get(index).getCode()+"");
		for (int i = 0; i < files.size(); i++) {
			File file = new File(files.get(i).getSrc());
			if (file.exists()) {
				file.delete();
				
			}
			
			fDb.delete(files.get(i));
		}
		if (!books.get(index).getCover().contains("http")) {
			File file2 = new File(books.get(index).getCover());
			if (file2.exists()) {
				file2.delete();
				
			}
		}
		fDb.delete(books.get(index));
		books.remove(books.get(index));
		adapter.notifyDataSetChanged();
	}

	/**
	 * 收藏书籍
	 * @param index
	 */
	private void collectBook(int index) {
		ListenBook book = books.get(index);
		if (book.isCollect()) {
			book.setCollect(false);
			fDb.update(book);
			books.remove(book);
			showToast("取消收藏收藏成功！");
		} else {
			showToast("已取消收藏书籍无法取消收藏！");
		}
		
	}

	private void cancelMenuWindow() {
		if (mMenuWindow!=null && mMenuWindow.isShowing()) {
			mMenuWindow.dismiss();
		}
	}

//	class CollectAdapter extends BaseAdapter{
//
//		private Context context; // 运行上下文
//
//		private LayoutInflater listContainer; // 视图容器
//
//		private ArrayList<ListenBook> vector;
//
//		public class ListItemView { // 自定义控件集合
//
//			private ImageView cover;
//			private TextView nameTv;
//			private TextView sizeTv;
//			private Button stateIv;
//
//		}
//
//		public CollectAdapter(Context context, ArrayList<ListenBook> vector) {
//			this.context = context;
//			listContainer = LayoutInflater.from(context); // 创建视图容
//			this.vector = vector;
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return vector.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			// TODO Auto-generated method stub
//			return vector.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			// TODO Auto-generated method stub
//			return arg0;
//		}
//
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			ListItemView listItemView = new ListItemView();
//			if (convertView == null) {
//				convertView = listContainer.inflate(R.layout.collect_list_item, null);
//				listItemView.cover = (ImageView) convertView.findViewById(R.id.book_cover_iv);
//				listItemView.stateIv = (Button) convertView.findViewById(R.id.state_iv);
//				listItemView.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
//				listItemView.sizeTv = (TextView) convertView.findViewById(R.id.size_tv);
//				convertView.setTag(listItemView);
//			} else {
//				listItemView = (ListItemView) convertView.getTag();
//			}
//			MyLog.d("bookAdapter", vector.get(position).getName());
//
//			listItemView.cover.setImageBitmap(Tool.getImageThumbnail(vector.get(position).getCover(),
//					Tool.dip2px(getApplicationContext(), 50),Tool.dip2px(getApplicationContext(), 70)));
//			
//				 listItemView.nameTv.setText(vector.get(position).getName());
//				 listItemView.sizeTv.setText(vector.get(position).getCategory());
//				 listItemView.stateIv.setOnTouchListener(new OnTouchListener() {
//					
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						if (Constants.IS_SERVER) {
//							Intent intent = new Intent(context, ShowBookDetailActivity.class);
//							intent.putExtra("book", vector.get(position));
//							context.startActivity(intent);
//						} else {
//						selectIndex = position;
//						mMenuWindow.showAtLocation(
//								MainCollectActivity.this.findViewById(R.id.main),
//								Tool.getGravity(Constants.IS_SERVER) | Gravity.CENTER_HORIZONTAL, 0, 0);
//						}
//						return false;
//					}
//				});
//			convertView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					BookOper.openBook(books.get(position),context, fDb);
//				}
//			});
//			 
//			return convertView;
//		}
//
//		
//	}
}

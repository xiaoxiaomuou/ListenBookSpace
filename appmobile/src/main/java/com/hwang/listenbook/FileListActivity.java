package com.hwang.listenbook;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.service.PlayerService;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;

public class FileListActivity extends BaseActivity{

	public static final String TAG = "FileListActivity";

//	@ViewInject(id=R.id.top_back) ImageView mBack;
//	@ViewInject(id=R.id.top_title) TextView titleTv;
//	@ViewInject(id=R.id.listview) ListView mListView;
	
	private ImageView mBack;
	private TextView titleTv;
	private ListView mListView;
	
	private ListenBook book ;
	private ArrayList<ListenBookFileBean> fileBeans ;
	private FinalDb fdDb ;
	private FileAdapter adapter;
	private String currentPlay="";
	private App app;
//	private Receiver mReceiver;	//2015-11-5 13:32:08
    private Dialog mLoadingDialog;
	private boolean isFirstClick;	//是否是第一次打开页面
	private int totalMusicNum;    //该专辑共有多少首歌曲
	public int index = 0;    //歌曲id，默认是第一首
	private String code;	//专辑id

	private Receiver mReceiver;	//自定义广播接收器，用来更新“播放中”的状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);
		mLoadingDialog = Tool.createLoadingDialog(FileListActivity.this, getString(R.string.data_loading));
		
		app = (App) this.getApplication();

		initData();
		initView();
		if (!Tool.isServiceRunning(FileListActivity.this, "PlayerService")) {
			Intent intent3 = new Intent(FileListActivity.this, PlayerService.class);
			startService(intent3);
		}

		mReceiver = new Receiver();
		IntentFilter intentFilter = new IntentFilter(Constants.PLAY_INTENT_ACTION);
		registerReceiver(mReceiver, intentFilter);
	}

	private void initData(){

		fdDb = FinalDb.create(FileListActivity.this);
		book = (ListenBook) getIntent().getSerializableExtra("book");
		currentPlay =book.getCurrentCode();
		code = book.getCode();
		fileBeans = (ArrayList<ListenBookFileBean>) fdDb.findAllByWhere(ListenBookFileBean.class, "mainCode ='" +code+ "'","sort");
		totalMusicNum = fileBeans.size();
		Tool.setIntShared(this, "totalMusicNumActFileList", totalMusicNum);
	}
	
	private void initView() {
		isFirstClick = true;
		mBack = (ImageView)findViewById(R.id.top_back);
		titleTv = (TextView) findViewById(R.id.top_title);
		mListView = (ListView) findViewById(R.id.listview);

		mBack.setImageResource(R.drawable.back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		titleTv.setText(book.getName());
		adapter = new FileAdapter(FileListActivity.this, fileBeans);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				mLoadingDialog.show();

				if (book.getBookType().equals(Constants.YIN_PIN_TYPE + "")) {
					MyLog.e(TAG, "listviewclick = " + arg2);
					Intent intent = new Intent(FileListActivity.this, PlayerActivity.class);
					intent.putExtra("book", book);
					intent.putExtra("files", fileBeans);
					intent.putExtra("index", arg2);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					enterActivity(intent);
					sendAudioBroadcast(arg2);
					updataStateTv(arg2);
					currentPlay = fileBeans.get(arg2).getCode();
//					adapter.notifyDataSetChanged();	//在这里更新，则列表页一直都会是“暂停”状态
					MyLog.d(TAG, "hrere is end");
					if (isFirstClick) {
						isFirstClick = false;
						app.setPlaying(true);
						/**
						 * @作用 第一次点击的时候，记录当前的mainCode。这样在下面的广播中
						 * 就可以根据mainCode对照，避免在所有专辑的播放列表中均显示“播放中”
						 */
						Tool.setStringShared(FileListActivity.this,"mainCodeActFileList",code);
					}
				} else if (book.getBookType().equals(Constants.TEXT_TYPE + "")) {
					Intent intent = new Intent(FileListActivity.this, TxtBookActivity.class);
					intent.putExtra("book", book);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("index", arg2);

					enterActivity(intent);
				}

			}

		});
		
	}

	/**
	 * 更改选中item的状态
	 * @param position 选中item的id
	 */
	private void updataStateTv(int position){
//        adapter.notifyDataSetChanged();     //先清除之前的状态
		MyLog.e(TAG,"updataStateTv : "+position);
		adapter.setSelectItem(position);
		adapter.notifyDataSetChanged();   //这里为什么要刷新数据？为了显示“播放中”这几个字
	}

	/**
	 * @作用 发送广播，播放指定的音频
	 * @param arg 指定的音频
	 */
	private void sendAudioBroadcast(int arg) {
		Intent intent2 = new Intent();
		intent2.setAction(Constants.PLAY_INTENT_ACTION);
		intent2.putExtra(Constants.INTENT_RECEIVER, Constants.READY_PLAY);//更改购物车下标显示数量
		intent2.putExtra("code", code);
		intent2.putExtra("index", arg);
		sendBroadcast(intent2);
	}
	
	@Override
	protected void onResume() {
		int positionInt = Tool.getIntShared(this, "musicPositionBackActPlayer");
		if (!isFirstClick) {
			currentPlay = fileBeans.get(positionInt).getCode();
			adapter.setSelectItem(positionInt);
		}
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	@Override
	protected void onPause() {
		MyLog.d(TAG, "hrere is pause");
		if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			exitActivity();
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	class FileAdapter extends BaseAdapter{

		private Context context; // 运行上下文

		private LayoutInflater listContainer; // 视图容器

		private ArrayList<ListenBookFileBean> vector;

		public class ListItemView { // 自定义控件集合

			private ImageView cover;
			private TextView nameTv;
			private TextView sizeTv;
			private TextView stateIv;

		}

		public FileAdapter(Context context, ArrayList<ListenBookFileBean> vector) {
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
				convertView = listContainer.inflate(R.layout.file_list_item, null);
				listItemView.cover = (ImageView) convertView.findViewById(R.id.book_cover_iv);
				listItemView.stateIv = (TextView) convertView.findViewById(R.id.state_iv);
				listItemView.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
				listItemView.sizeTv = (TextView) convertView.findViewById(R.id.size_tv);
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}
//			MyLog.d("bookAdapter", vector.get(position).getTitle());
			MyLog.e("getView","position = "+position);

			listItemView.cover.setImageBitmap(Tool.getImageThumbnail(book.getCover(),
					Tool.dip2px(getApplicationContext(), 50),Tool.dip2px(getApplicationContext(), 70)));
			 if (!TextUtils.isEmpty(vector.get(position).getTitle())) {
				 listItemView.nameTv.setText(vector.get(position).getTitle());
			} else {
				listItemView.nameTv.setText(book.getName()+vector.get(position).getSort());
			}

			if (position==selectItem){
				if (app.isPlaying()) {
					listItemView.stateIv.setText("播放中");
				} else {
					listItemView.stateIv.setText("暂停");
				}
			}else{
				listItemView.stateIv.setText("");
			}

			listItemView.sizeTv.setText("大小:" + vector.get(position).getFileSize() + "MB");
			
			return convertView;
		}

		public  void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}
		private int  selectItem=-1;
	}
	
	@Override
	public void onDestroy() {
		if (!app.isPlaying()) {
			if (Tool.isServiceRunning(FileListActivity.this, "PlayerService")) {
				Intent intent = new Intent(this, PlayerService.class);
				stopService(intent);
			}
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String string = intent.getExtras().getString(Constants.INTENT_RECEIVER);
			String mainCode = Tool.getStringShared(FileListActivity.this,"mainCodeActFileList");

			if (!TextUtils.isEmpty(string)&&mainCode.equals(code)){

				if (string.equals(Constants.READY_PLAY)) {
					index = intent.getIntExtra("index", 0); //更新歌曲标识
					updataStateTv(index);
				}

				if (string.equals(Constants.PAUSE)) {
					index = intent.getIntExtra("index", 0); //更新歌曲标识
					updataStateTv(index);
				}

				if (string.equals(Constants.START_PLAY)) {
					index = intent.getIntExtra("index", 0); //更新歌曲标识
					updataStateTv(index);
				}

			}

		}
	}

}

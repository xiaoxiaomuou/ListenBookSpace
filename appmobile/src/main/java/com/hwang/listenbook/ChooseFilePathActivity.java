package com.hwang.listenbook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;
import com.hwnag.listenbook.myView.AlertDialog;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择路径
 * @author wanghao
 *
 */
@SuppressLint("NewApi")
public class ChooseFilePathActivity extends BaseActivity {

	private static final String TAG = "ChooseFilePathActivity";
	@ViewInject(id=R.id.list) ListView lvFiles;
	@ViewInject(id=R.id.top_back) ImageView mBack;
	@ViewInject(id=R.id.top_title) TextView titleTv;
	@ViewInject(id=R.id.copy_tv) TextView topRightTv;
	@ViewInject(id=R.id.path_tv) TextView pathTv;
	@ViewInject(id=R.id.return_tv) TextView returnTv;
	ArrayList<ListenBookFileBean> fileBeans;
	File mCurrentDir;
	File[] mFileList;
	String root;
	int foldernum = 0;
	private ListenBook book;
	private FinalDb fDb;
	private Dialog mLoadingDialog;
	private TextView tipTextView;
	private int copyIndex =0 ;
	private File usbFile;
	private Receiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_file_path);
		usbFile = new File(Constants.USB_PATH);
		receiver = new Receiver();
		IntentFilter intentFilter = new IntentFilter("com.hwang.listenbook.usb");
	     registerReceiver(receiver, intentFilter);
		root = Tool.getStringShared(this, "usbPath");
		book = (ListenBook) getIntent().getSerializableExtra("book");
//		root =Environment.getExternalStorageDirectory().getPath();
		mCurrentDir = usbFile;
		fDb = FinalDb.create(this);
		initView();
	}

	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.get_data_progressbar, null);// 得到加载view
		tipTextView = (TextView) v.findViewById(R.id.tip_tv);// 提示文字
		//tipTextView.setText(getString(R.string.copy_loading,0,0));// 设置加载信息
		
		mLoadingDialog = new Dialog(this, R.style.MyDialogStyle);// 创建自定义样式dialog
		mLoadingDialog.setCancelable(false);// 不可以用“返回键”取消
		mLoadingDialog.setContentView(v);// 设置布局

		//注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
		fileBeans = (ArrayList<ListenBookFileBean>)
				fDb.findAllByWhere(ListenBookFileBean.class, "mainCode='"+book.getCode()+"'");
//		fileBeans = (ArrayList<ListenBookFileBean>)
//				fDb.findAllByWhere(ListenBookFileBean.class, "mainCode="+book.getCode()+"");
		tipTextView.setText(getString(R.string.copy_loading,fileBeans.size(),copyIndex));
		
		mBack.setImageResource(R.drawable.back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		titleTv.setText("复制到SD卡");
		
		topRightTv.setOnClickListener(this);
		returnTv.setOnClickListener(this);
		pathTv.setText(root);
		mFileList = mCurrentDir.listFiles(new bookFileFilter());
		inflateListView(mFileList);
		
		lvFiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				
				
					File[] tem = mFileList[position]
							.listFiles(new bookFileFilter());
					mCurrentDir = mFileList[position];
					mFileList = tem;
					inflateListView(mFileList);
					pathTv.setText(mCurrentDir.getPath());
				
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			finish();
			break;

		case R.id.copy_tv:
			if (isSizeTooBig()) {
				createDownLoadDialog("SD卡存储空间不足");
				return;
			}
			mLoadingDialog.show();
			new Thread(runnable).start();
			
			break;
		case R.id.return_tv:
			FolderReturn();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 文件空间大小对比
	 * @return
	 */
	private boolean isSizeTooBig() {
		long sdSize = Tool.getAvailableExternalMemorySize(usbFile);
		long fileSize = 0;
		for (int i = 0; i < fileBeans.size(); i++) {
			File file = new File(fileBeans.get(i).getSrc());
			try {
				fileSize += getFileSize(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (fileSize<sdSize) {
			return false;
		} else {
			return true;
		}
	}

	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			doCopy();
		}
	};
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if (msg.what==1) {
				tipTextView.setText(getString(R.string.copy_loading,fileBeans.size(),copyIndex));
				if (copyIndex==fileBeans.size()) {
					if (mLoadingDialog!=null && mLoadingDialog.isShowing() ) {
						mLoadingDialog.dismiss();
					}
					copyIndex =0;
					showToast("复制完成！");
					mFileList = mCurrentDir.listFiles(new bookFileFilter());
					inflateListView(mFileList);
				}
			} else if(msg.what==2){
				if (mLoadingDialog!=null && mLoadingDialog.isShowing() ) {
					mLoadingDialog.dismiss();
				}
				createDownLoadDialog("该文件夹已存在，请选择其他路径下载");
			}
			super.handleMessage(msg);
		}
		
	};
	
	private void doCopy() {
		
		File file = new File(pathTv.getText().toString()+"/"+book.getName());
		if (!file.exists()) {
			file.mkdirs();
		} else {
			handler.sendEmptyMessage(2);
			
			return;
		}
		
		for (int i = 0; i < fileBeans.size(); i++) {
			copyIndex +=1;
			
			File file2 = new File(fileBeans.get(i).getSrc());
			File file3= new File(file.getPath()+"/" + book.getName()+
					getNumber(fileBeans.get(i).getSort())+getFileType(fileBeans.get(i).getSrc()));
			if (file2.exists()) {
				if (file3.exists()) {
					file3.delete();
				}
				copyFileTo(file2,file3);
			}
			handler.sendEmptyMessage(1);
		}
		
	}

	private String getFileType(String title) {
		String type = title.substring(title.lastIndexOf("."));
		return type;
	}

	private String getNumber(int sort) {
		
		DecimalFormat df = new DecimalFormat("0000");
		String string = df.format(sort);
		return string;
	}
	
	private void createDownLoadDialog(String msg) {
		final AlertDialog ad = new AlertDialog(ChooseFilePathActivity.this);
		ad.setTitle(getString(R.string.notice));
		ad.setMessage(msg);
		ad.setPositiveButton(getString(R.string.yes), new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ad.dismiss();

			}
		});

		ad.setNegativeButton(getString(R.string.no), new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ad.dismiss();
			}
		});
		
	}

	/**
	 * 文件过滤
	 * @author wanghao
	 *
	 */
	public class bookFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			// TODO Auto-generated method stub
			String filename = pathname.getAbsolutePath().toLowerCase();
			if (pathname.isDirectory() && (!pathname.isHidden())) {
				if (filename.equals(root + "/lost.dir"))
					return false;
				else {
					foldernum++;
					return true;
				}
			} else
				return false;
		}

	}
	
	/**
	 * 
	 * 
	 * @param files
	 */
	private void inflateListView(File[] files) {
		if (files == null)
			return;
		FileSort(files);

		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		Map<String, Object> listItemFirst = new HashMap<String, Object>();
		for (int i = 0; i < files.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			if (files[i].isDirectory()) {
				// ������ļ��о���ʾ��ͼƬΪ�ļ��е�ͼƬ
				listItem.put("modify", "");
				listItem.put("logo", R.drawable.folder);
			}
			listItem.put("filename", files[i].getName()); // ���һ���ļ����
			listItems.add(listItem);
		}
		// ����һ��SimpleAdapter
		SimpleAdapter adapter = new SimpleAdapter(ChooseFilePathActivity.this,
				listItems, R.layout.list, new String[] { "filename",
						"modify", "logo" }, new int[] { R.id.filename, R.id.modify, R.id.logo });
		// �����ݼ�
		lvFiles.setAdapter(adapter);
		pathTv.setText(mCurrentDir.getAbsolutePath());
		
	}
	
	public void FileSort(File[] file) {// �����ļ�����ǰ���ļ��ں�
		long beg = System.currentTimeMillis();
		for (int i = 0; i < file.length - 1; i++) {
			boolean flag = false;// �����ı�־�����Ƚ�һ��û�н�������ʾ�������
			for (int j = 0; j < file.length - 1; j++) {
				if (file[j].isFile() && file[j + 1].isDirectory()) {
					File tempFile = file[j];
					file[j] = file[j + 1];
					file[j + 1] = tempFile;
					flag = true;
				}
			}
			if (!flag)
				break;
		}
		long end = System.currentTimeMillis() - beg;

	}
	
	public void FolderReturn() {
		try {
			if (!pathTv.getText().toString().equals(Constants.USB_PATH)) {
				// ��ȡ��һ��Ŀ¼
				mCurrentDir = mCurrentDir.getParentFile();
				foldernum = 0;
				// �г���ǰĿ¼�µ������ļ�
				mFileList = mCurrentDir.listFiles(new bookFileFilter());
				// �ٴθ���ListView
				inflateListView(mFileList);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private boolean copyFileTo(File srcFile, File destFile){
		if(srcFile.isDirectory() || destFile.isDirectory())
			return false;
		FileInputStream fis;
		FileOutputStream fos;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			int readLen = 0;
			byte[] buf = new byte[1024];
			while((readLen = fis.read(buf)) != -1){
				fos.write(buf, 0, readLen);
			}
			fos.flush();
			fos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	class Receiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
		
				finish();
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	 private static long getFileSize(File file) throws Exception {
		  long size = 0;
		  if (file.exists()) {
		   FileInputStream fis = null;
		   fis = new FileInputStream(file);
		   size = fis.available();
		  } else {
		  // file.createNewFile();
		   MyLog.d("获取文件大小", "文件不存在!");
		  }
		  return size;
		 }
}

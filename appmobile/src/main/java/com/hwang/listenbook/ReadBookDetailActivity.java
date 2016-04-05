package com.hwang.listenbook;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hwang.listenbook.bean.BookBean;
import com.hwang.listenbook.bean.BookListBean;
import com.hwang.listenbook.bean.BookListBeanManager;
import com.hwang.listenbook.service.BookDownloadService;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.ArrayList;

/**
 * <b>Copyright:</b>  Copyright © 2011-2014 www.aiweimob.com. All Rights Reserved<br/>
 * <b>Description:</b>  <br/>
 * <b>author:</b>  zcf <br/>
 * <b>data:</b>  2016/3/28 15:01<br/>
 *
 * @version :  V1.0 <br/>
 */
public class ReadBookDetailActivity extends BaseActivity {
    private static final String TAG = "ReadBookDetailActivity";
    private Uri uri;
    private Dialog mLoadingDialog;

    private ImageView mBack;
    private TextView titleTv;
    private ImageView coverIv;
    private TextView nameTv;
    private TextView authorTv;
    private TextView publishTv;
    private TextView categoryTv;
    private TextView isbnTv;
    private TextView descTv;
    private Button clickBtn;//下载按钮
    private ProgressBar progressBar;//进度条

    private FinalDb fDb;

    private int width;
    private String bookId;

    private static final int BEGIN_DOWNLOAD_FILE = 1;//开始下载内容
    private static final int LOADING = 2;//正在下载
    private static final int LOADED_SUCCESS = 3;//下载成功
    private static final int HAVE_DOWNLOADED = 4;//已经下载过了
    private DownloadFileReceiver downloadReceiver;
    private static final String CURRENT_KEY = "currentKey";
    private static final String COUNT_KEY = "countKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_detail);

        uri = Uri.parse(getIntent().getStringExtra("url"));
        mLoadingDialog = Tool.createLoadingDialog(this, getString(R.string.data_loading));

        downloadReceiver = new DownloadFileReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.INTENT_ACTION);
        registerReceiver(downloadReceiver, intentFilter);

        initView();

        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
        Intent intent = new Intent(ReadBookDetailActivity.this, BookDownloadService.class);
        stopService(intent);
        MyLog.e(TAG,"onDestroy");
    }

    private void initView() {
        // TODO Auto-generated method stub
        mBack = (ImageView) findViewById(R.id.top_back);
        titleTv = (TextView) findViewById(R.id.top_title);
        clickBtn = (Button) findViewById(R.id.clickbtn);
        coverIv = (ImageView) findViewById(R.id.cover_iv);
        nameTv = (TextView) findViewById(R.id.name_tv);
        authorTv = (TextView) findViewById(R.id.author_tv);
        publishTv = (TextView) findViewById(R.id.publish_tv);
        categoryTv = (TextView) findViewById(R.id.category_tv);
        isbnTv = (TextView) findViewById(R.id.isbn_tv);
        descTv = (TextView) findViewById(R.id.desc_tv);
        progressBar = (ProgressBar) findViewById(R.id.activity_book_detail_pb);

        width = Tool.getwindowWidth(getApplicationContext()) / 3;

        mBack.setImageResource(R.drawable.back);
        mBack.setVisibility(View.VISIBLE);
        titleTv.setText("书籍详情");
        fDb = FinalDb.create(this);

        mBack.setOnClickListener(this);
        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(bookId)) {
                    ArrayList<BookBean> bookBeans = (ArrayList<BookBean>) fDb.findAllByWhere(BookBean.class,
                            "book='" + bookId + "'");
                    MyLog.e(TAG, "size=" + bookBeans.size());
                    if (bookBeans.size() > 0) {
                        if (bookBeans.get(0).isFileDownloaded()) {
                            Toast.makeText(ReadBookDetailActivity.this, "书籍已下载", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(ReadBookDetailActivity.this, BookDownloadService.class);
                            intent.putExtra(Constants.BOOK_ID, bookBeans.get(0).getBook());
                            startService(intent);
                            Message msg = new Message();
                            msg.what = BEGIN_DOWNLOAD_FILE;
                            myHandler.sendMessage(msg);
//                            exitActivity();
                        }
                    }
                }
            }
        });

    }

    private void getData() {
        if (!Tool.getNetworkState(ReadBookDetailActivity.this)) {
            return;
        }
        mLoadingDialog.show();
        FinalHttp fh = new FinalHttp();
        fh.addHeader("content-type", "application/json");
        MyLog.e(TAG, "the uri===>" + uri.toString());
        fh.get(uri.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                BookListBeanManager manager = new BookListBeanManager();
                manager = manager.parseJSONString(o.toString(), fDb, ReadBookDetailActivity.this);
                int code = manager.getBaseBean().getCode();
                if (code == 1) {
                    bookId = manager.getBookListBean().getBook();
                    ArrayList<BookListBean> bookListBeans = (ArrayList<BookListBean>) fDb.findAllByWhere(BookListBean.class,
                            "book='" + bookId + "'");
                    if (bookListBeans.size() == 0) {
                        fDb.save(manager.getBookListBean());
                    } else {
                        Message msg = new Message();
                        msg.what = HAVE_DOWNLOADED;
                        myHandler.sendMessage(msg);
                    }

                    ArrayList<BookListBean> bookListBeans2 = (ArrayList<BookListBean>) fDb.findAllByWhere(BookListBean.class,
                            "book='" + bookId + "'");
                    if (bookListBeans2.size() > 0)
                        setData(bookListBeans2.get(0));
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    private void setData(BookListBean bookListBean) {
        nameTv.setText(bookListBean.getName());
        authorTv.setText(getString(R.string.book_info_author, getRealString(bookListBean.getAuthor())));
        isbnTv.setText(getString(R.string.book_info_isbn, getRealString(bookListBean.getIsbn())));
        publishTv.setText(getString(R.string.book_info_publish, getRealString(bookListBean.getPublisher())));
        categoryTv.setText(getString(R.string.book_info_category, "综合"));
        descTv.setText(getString(R.string.book_info_desc, getRealString(bookListBean.getDescription())));
        getImage(bookListBean);

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private String getRealString(String string) {
        if (TextUtils.isEmpty(string) || string.equals("null")) {
            return "暂无数据";
        }
        return string;
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

    private void getImage(final BookListBean bookListBean) {
        FinalHttp fHttp = new FinalHttp();
        String urlPath = bookListBean.getCoverNativeUrl();
        urlPath = urlPath.replaceAll("/readOnline/", "");

        String filePath = Constants.FILE_BOOK_PATH + bookListBean.getBook();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String coverName = Tool.getFileName(bookListBean.getCoverUrl());

        final String nativeCoverUrl = filePath + "/" + coverName;
        MyLog.e(TAG, "url = " + urlPath + "\tnaUrl =" + nativeCoverUrl);
        fHttp.download(urlPath, nativeCoverUrl, false, new AjaxCallBack<File>() {
            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                MyLog.e(TAG, "current=" + current + "|count=" + count);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);
                MyLog.e(TAG, "onSuccess");
                bookListBean.setCover(coverName);
                bookListBean.setCoverDownloaded(true);
                coverIv.setImageBitmap(Tool.getImageThumbnail(nativeCoverUrl
                        , width, width * 4 / 3));
                fDb.save(bookListBean);
            }
        });
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEGIN_DOWNLOAD_FILE:
                    clickBtn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case LOADING:
                    Bundle bundle = msg.getData();
                    long count = bundle.getLong(COUNT_KEY);
                    long current = bundle.getLong(CURRENT_KEY);
                    int progressInt;
                    if (count > 0) {
                        progressInt = (int) (100 * current / count);
                    } else {
                        progressInt = 10;
                    }
                    MyLog.e(TAG, "loading - - - progress=" + progressInt);
                    progressBar.setProgress(progressInt);
                    break;
                case LOADED_SUCCESS:
                    Toast.makeText(ReadBookDetailActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    exitActivity();
                    break;
                case HAVE_DOWNLOADED:
                    clickBtn.setText("已经下载");
                    clickBtn.setClickable(false);
                    clickBtn.setTextColor(getResources().getColor(R.color.red));
                    break;
            }
        }
    };

    private class DownloadFileReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String string = intent.getExtras().getString(Constants.INTENT_RECEIVER);
            Message msg = new Message();
            if (string.equals(Constants.LOADING)) {
                long count = intent.getLongExtra(Constants.DEFS_LOADING_COUNT_KEY, 0);
                long current = intent.getLongExtra(Constants.DEFS_LOADING_CURRENT_KEY, 0);
                Bundle bundle = new Bundle();
                bundle.putLong(CURRENT_KEY, current);
                bundle.putLong(COUNT_KEY, count);
                msg.setData(bundle);
                msg.what = LOADING;
            }

            if (string.equals(Constants.LOADED)) {
                String result = intent.getStringExtra(Constants.LOADED_RESULT_KEY);
                if (result.equals(Constants.LOADED_SUCCESS_KEY)) {
                    msg.what = LOADED_SUCCESS;
                }
            }

            myHandler.sendMessage(msg);
        }
    }
}

package com.hwang.listenbook;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwang.listenbook.bean.BookFromAndroid;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.ArrayList;

public class BookDetailActivity extends BaseActivity {

    private static final String TAG = "BookDetailActivity";
    @ViewInject(id=R.id.top_back) ImageView mBack;
    @ViewInject(id=R.id.top_title) TextView titleTv;
    @ViewInject(id=R.id.cover_iv) ImageView coverIv;
    @ViewInject(id=R.id.name_tv) TextView nameTv;
    @ViewInject(id=R.id.author_tv) TextView authorTv;
    @ViewInject(id=R.id.publish_tv) TextView publishTv;
    @ViewInject(id=R.id.category_tv) TextView categoryTv;
    @ViewInject(id=R.id.isbn_tv) TextView isbnTv;
    @ViewInject(id=R.id.desc_tv) TextView descTv;
    @ViewInject(id=R.id.clickbtn) Button clickBtn;

    private Uri url;
    private Dialog mLoadingDialog;
    private ListenBook book ;
    private ArrayList<ListenBookFileBean> bookFileBeans =
            new ArrayList<>();

    private FinalDb fDb;
    private FinalBitmap fBitmap;
    private  int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        url = Uri.parse(getIntent().getStringExtra("url"));
        mLoadingDialog = Tool.createLoadingDialog(BookDetailActivity.this,
                getString(R.string.data_loading));
        fBitmap = FinalBitmap.create(BookDetailActivity.this);
        initView();
        getData();

    }

    private void getData() {
        if (!Tool.getNetworkState(BookDetailActivity.this)) {

            return;
        }
        mLoadingDialog.show();
        FinalHttp fh = new FinalHttp();
        fh.addHeader("content-type", "application/json");
        MyLog.d(TAG, "the uri===>" + url);
//		fh.post(url.toString(),null, null, "multipart/form-data", new AjaxCallBack<Object>(){
        fh.post(url.toString(),new AjaxCallBack<Object>(){

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                MyLog.d(TAG, "here is error msg==>" + strMsg + errorNo);
                showToast(getString(R.string.http_error));
                if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.cancel();
                }
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(Object t) {
                MyLog.d(TAG, t.toString());
                if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.cancel();
                }
                BookFromAndroid manager = new BookFromAndroid();
                manager = manager.praseJSONObject(t.toString());
                MyLog.d(TAG, "here is status ===>" + manager.getBaseBean().getCode());
                if (manager.getBaseBean().getCode()==1) {
                    book = manager.getListenBook();
                    bookFileBeans = manager.getBookFiles();
                    setData();
                } else {
                    showToast(manager.getBaseBean().getMessage());
                    if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.cancel();
                    }
                }


                super.onSuccess(t);
            }



        });
    }


    private void initView() {
        // TODO Auto-generated method stub
        mBack.setImageResource(R.drawable.back);
        mBack.setVisibility(View.VISIBLE);
        titleTv.setText("书籍详情");
        fDb = FinalDb.create(BookDetailActivity.this);

        mBack.setOnClickListener(this);
        clickBtn.setOnClickListener(this);

    }

    private void setData() {
        if (book.getBookType().equals(Constants.YIN_PIN_TYPE+"")) {
            isbnTv.setVisibility(View.GONE);
            authorTv.setVisibility(View.GONE);
        }
        nameTv.setText(book.getName());
        authorTv.setText(getString(R.string.book_info_author,getRealString(book.getAuthor())));
        isbnTv.setText(getString(R.string.book_info_isbn,getRealString(book.getIsbn())));
        publishTv.setText(getString(R.string.book_info_publish,getRealString(book.getProduction())));
        categoryTv.setText(getString(R.string.book_info_category,getRealString(book.getCategory())));
        descTv.setText(getString(R.string.book_info_desc,getRealString(book.getDescription())));

        width = Tool.getwindowWidth(getApplicationContext())/3;
        //  fBitmap.display(coverIv, book.getCover(),width , width*4/3);

        if (fDb.findById( book.getCode(),ListenBook.class) != null) {
            if (fDb.findById( book.getCode(),ListenBook.class).isIsdownload()) {
                clickBtn.setText("已经下载");
                clickBtn.setClickable(false);
                clickBtn.setTextColor(getResources().getColor(R.color.red));
            } else {
                clickBtn.setText("已经放入下载列表");
                clickBtn.setClickable(true);
                clickBtn.setTextColor(getResources().getColor(R.color.red));
            }
            book = fDb.findById( book.getCode(),ListenBook.class);
            File file = new File(book.getCover());
            if (!file.exists()) {
                getImage();
            } else {
                coverIv.setImageBitmap(Tool.getImageThumbnail(book.getCover()
                        ,width , width*4/3));
            }

        } else {
            getImage();
            clickBtn.setText(getString(R.string.btn_download));
            clickBtn.setClickable(true);
            clickBtn.setTextColor(getResources().getColor(R.color.green));
        }
    }

    private void getImage() {

        FinalHttp fHttp = new FinalHttp();
        fHttp.download(book.getCover(),
                Constants.IMAGE_PATH + Tool.getFileName(book.getCover()), false, new AjaxCallBack<File>(){

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        // TODO Auto-generated method stub
                        MyLog.d(TAG, "failure  ==>"+strMsg+errorNo);
                        if (errorNo!=416) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.book_cover_default);
                            Tool.saveBitmap(book,bitmap);
                            book.setCover(Constants.IMAGE_PATH+Tool.getFileName(book.getCover()));
                        }else {
                            book.setCover(Constants.IMAGE_PATH+Tool.getFileName(book.getCover()));
                        }
                        super.onFailure(t, errorNo, strMsg);
                    }



                    @Override
                    public void onSuccess(File t) {
                        MyLog.d(TAG, "success  ==>"+Constants.IMAGE_PATH + Tool.getFileName(book.getCover()));
                        book.setCover(Constants.IMAGE_PATH + Tool.getFileName(book.getCover()));
                        coverIv.setImageBitmap(Tool.getImageThumbnail(book.getCover()
                                ,width , width*4/3));


                        super.onSuccess(t);
                    }

                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_back:
                exitActivity();
                break;

            case R.id.clickbtn:
                if (book==null) {
                    showToast("没有联接热点，请连接热点");
                    return;
                }
                if ( book.getCover().contains("http")) {
                    showToast("正在下载封面，请稍后再试");
                    return;
                }
                if (fDb.findById( book.getCode(),ListenBook.class) == null){

                    fDb.save(book);
                    for (int i = 0; i < bookFileBeans.size(); i++) {
                        bookFileBeans.get(i).setCover(book.getCover());
                        bookFileBeans.get(i).setName(book.getName());
                        bookFileBeans.get(i).setDownNumber(book.getDownNumber());
                        fDb.save(bookFileBeans.get(i));
                    }
                }
                Intent intent = new Intent();
                intent.setAction(Constants.INTENT_ACTION);
                intent.putExtra(Constants.INTENT_RECEIVER, Constants.XIA_ZAI);
                sendBroadcast(intent);
//			Intent intent1 = new Intent(BookDetailActivity.this, DownLoadFromHttpService.class);
//			startService(intent1);
                Intent itnent1 = new Intent();
                itnent1.setAction("com.tingshu.DownLoadFromHttpService");
                itnent1.setPackage(getPackageName());
                startService(itnent1);
                exitActivity();
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    private void saveCover() {
        Bitmap bitmap = fBitmap.getBitmapFromCache(book.getCover());
        if(bitmap!=null ){

        }else {
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.book_cover_default);
        }
        Tool.saveBitmap(book,bitmap);
        book.setCover(Constants.IMAGE_PATH+Tool.getFileName(book.getCover()));
    }



    private String getRealString(String string) {
        if (TextUtils.isEmpty(string)|| string.equals("null")) {
            return "暂无数据";
        }
        return string;
    }


    private CharSequence getTime(String string) {
        if (!TextUtils.isEmpty(string) && !string.equals("null")) {
            String time = string.substring(0, string.indexOf("T"));
            return time;
        } else {
            return "暂无数据";
        }


    }
}

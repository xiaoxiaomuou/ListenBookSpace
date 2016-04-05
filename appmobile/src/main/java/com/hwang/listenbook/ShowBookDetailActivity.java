package com.hwang.listenbook;

import java.io.File;
import java.util.Hashtable;

import net.tsz.afinal.annotation.view.ViewInject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowBookDetailActivity extends BaseActivity{

	private static final String TAG = "ShowBookDetailActivity";

	@ViewInject(id=R.id.top_back) ImageView back;
	@ViewInject(id=R.id.cover_iv) ImageView coverIv;
	@ViewInject(id=R.id.name_tv) TextView nameTv;
	@ViewInject(id=R.id.author_tv) TextView authorTv;
	@ViewInject(id=R.id.isbn_tv) TextView isbnTv;
	@ViewInject(id=R.id.publish_tv) TextView publishTv;
	@ViewInject(id=R.id.publish_time) TextView publishTimeTv;
	@ViewInject(id=R.id.category_tv) TextView categoryTv;
	@ViewInject(id=R.id.desc_tv) TextView descTv;
	@ViewInject(id=R.id.ercode_iv) ImageView erCodeIv;
	@ViewInject(id=R.id.download_btn) ImageView downloadBtn;
	private ListenBook book;
	private File usbFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_book_detail);
		book = (ListenBook) getIntent().getSerializableExtra("book");
		initView();
	}

	private void initView() {
		back.setVisibility(View.VISIBLE);
		back.setImageResource(R.drawable.back);
		back.setOnClickListener(this);
		downloadBtn.setOnClickListener(this);
		if (book.getBookType().equals(Constants.YIN_PIN_TYPE+"")) {
			isbnTv.setVisibility(View.GONE);
			authorTv.setVisibility(View.GONE);
			publishTimeTv.setVisibility(View.GONE);
		}
		createImage(Constants.DOWNLOAD_URL + book.getCode(), erCodeIv);
		 nameTv.setText(getString(R.string.book_info_name,book.getName()));
         authorTv.setText(getString(R.string.book_info_author,getRealString(book.getAuthor())));
         isbnTv.setText(getString(R.string.book_info_isbn,getRealString(book.getIsbn())));
         publishTv.setText(getString(R.string.book_info_publish,getRealString(book.getProduction())));
         publishTimeTv.setText(getString(R.string.book_info_update,getTime(book.getPublishdate())));
         categoryTv.setText(getString(R.string.book_info_category,getRealString(book.getCategory())));
         descTv.setText(getString(R.string.book_info_desc,getRealString(book.getDescription())));
	    
	    int width = Tool.getwindowWidth(getApplicationContext())/3;
	    coverIv.setImageBitmap(Tool.getImageThumbnail(book.getCover(),
	    		width,width*4/3));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.download_btn:
			MyLog.d(TAG, "here is book type--->" +book.getBookType());
//			Intent intent = new Intent(ShowBookDetailActivity.this, ChooseFilePathActivity.class);
//			intent.putExtra("book", book);
//			startActivity(intent);
			usbFile = new File(Constants.USB_PATH);
			if (book.getBookType().equals(Constants.YIN_PIN_TYPE+"")) {
				//if (TextUtils.isEmpty(Tool.getStringShared(this, "usbPath"))) {
				if (!usbFile.exists()) {
				
					showToast("请插入要复制到的sd卡");
				} else {
					Intent intent = new Intent(ShowBookDetailActivity.this, ChooseFilePathActivity.class);
					intent.putExtra("book", book);
					startActivity(intent);
				}
			} else {
				showToast("非音频类书籍无法复制到SD卡");
			}
			break;

		default:
			break;
		}
		super.onClick(v);
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
	
	// 生成QR图
    private void createImage(String url,ImageView qr_image) {
        try {
            // 需要引入core包
        	int QR_WIDTH = Tool.getwindowWidth(getApplicationContext())*2/3;
        	int QR_HEIGHT = QR_WIDTH;
            QRCodeWriter writer = new QRCodeWriter();
 
            String text = url;
 
            MyLog.d(TAG, "生成的文本：" + text);
            if (text == null || "".equals(text) || text.length() < 1) {
                return;
            }
 
            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
                    QR_WIDTH, QR_HEIGHT);
 
            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());
 
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
 
                }
            }
 
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
 
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            qr_image.setImageBitmap(bitmap);
 
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}

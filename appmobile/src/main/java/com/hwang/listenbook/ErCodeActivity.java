package com.hwang.listenbook;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.annotation.view.ViewInject;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ErCodeActivity extends BaseActivity{

	private static final String TAG = "ErCodeActivity";
	@ViewInject(id=R.id.top_back) ImageView back;
	@ViewInject(id=R.id.top_title) TextView titleTv;
	@ViewInject(id=R.id.name_tv) TextView nameTv;
	@ViewInject(id=R.id.ercode_iv) ImageView erCodeIv;

	private ListenBook book;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ercode);
		book = (ListenBook) getIntent().getSerializableExtra("book");
		iniView();
	}

	private void iniView() {
		// TODO Auto-generated method stub
		back.setVisibility(View.VISIBLE);
		back.setImageResource(R.drawable.back);
		back.setOnClickListener(this);
		titleTv.setText("扫码下载");
		
		nameTv.setText(book.getName());
		createImage(Constants.DOWNLOAD_URL + book.getCode(), erCodeIv);
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

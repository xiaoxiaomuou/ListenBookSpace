package com.hwang.listenbook.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.net.URL;


//处理类
public class ImageHelper {
  /**
   * 转换图片成圆形
   * @param bitmap 传入Bitmap对象
   * @return
   */
  public static Bitmap toRoundBitmap(Bitmap bitmap) {
          int width = bitmap.getWidth();
          int height = bitmap.getHeight();
          float roundPx;
          float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
          if (width <= height) {
                  roundPx = width / 2;
                  top = 0;
                  bottom = width;
                  left = 0;
                  right = width;
                  height = width;
                  dst_left = 0;
                  dst_top = 0;
                  dst_right = width;
                  dst_bottom = width;
          } else {
                  roundPx = height / 2;
                  float clip = (width - height) / 2;
                  left = clip;
                  right = width - clip;
                  top = 0;
                  bottom = height;
                  width = height;
                  dst_left = 0;
                  dst_top = 0;
                  dst_right = height;
                  dst_bottom = height;
          }
          
          Bitmap output = Bitmap. createBitmap(width,
                          height, Config. ARGB_8888);
          Canvas canvas = new Canvas(output);
          
          final int color = 0xff424242;
          final Paint paint = new Paint();
          final Rect src = new Rect((int)left, ( int)top, (int)right, (int)bottom);
          final Rect dst = new Rect((int)dst_left, ( int)dst_top, (int)dst_right, (int)dst_bottom);
          final RectF rectF = new RectF(dst);

          paint.setAntiAlias( true);
          
          canvas.drawARGB(0, 0, 0, 0);
          paint.setColor(color);
          canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

          paint.setXfermode( new PorterDuffXfermode(Mode.SRC_IN));
          canvas.drawBitmap(bitmap, src, dst, paint);
          return output;
  }
 
  /**
   * @description 图片转成圆角
   * @author   liyangkun
   * @createDate 2014-4-9
   * @param bitmap
   * @return
   */
  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) { 
	     
      Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),  
          bitmap.getHeight(), Config.ARGB_8888);
      //得到画布
      Canvas canvas = new Canvas(output); 
   
      
     //将画布的四角圆化
      final int color = Color.RED;  
      final Paint paint = new Paint();  
      //得到与图像相同大小的区域  由构造的四个值决定区域的位置以及大小
      final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
      final RectF rectF = new RectF(rect);  
      //值越大角度越明显
      final float roundPx = 5;  
     
      paint.setAntiAlias(true);  
      canvas.drawARGB(0, 0, 0, 0);  
      paint.setColor(color);  
      //drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
      canvas.drawRoundRect(rectF, roundPx,roundPx, paint);  
     
      paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
      canvas.drawBitmap(bitmap, rect, rect, paint);  
     
      return output;  
    } 
  
//  // 下载图片转为bitmap
//  public static Bitmap getHttpBitmap(String url) {
//    URL u = null;
//    Bitmap bmp = null;
//     try {
//        u = new URL(url);
//
//    } catch (Exception e) {
//        // TODO: handle exception
//    }
//     try {
//        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//        conn.setConnectTimeout(0);
//        conn.setDoInput( true);
//        conn.connect();
//        InputStream ins = conn.getInputStream();
//        bmp = BitmapFactory. decodeStream(ins);
//        ins.close();
//    } catch (Exception e) {
//        // TODO: handle exception
//    }
//     return bmp;
//  }
}


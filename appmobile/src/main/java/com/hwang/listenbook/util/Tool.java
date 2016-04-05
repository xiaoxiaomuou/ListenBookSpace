package com.hwang.listenbook.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hwang.listenbook.R;
import com.hwang.listenbook.bean.ListenBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.zhiyou.chejiatong.image.ImageHelper;

@SuppressLint("SimpleDateFormat")
public class Tool {

    private static final String TAG = "Tool";


    /**
     * 判断当前网络状�?是否可用2
     *
     * @param context 上下文
     * @return boolean (true为网络在连接)
     */
    public static boolean getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isAvailable()) {
            return true;
        }
        Toast.makeText(context, "网络未连接，请打开网络", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean getWifiState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        } else {
            Toast.makeText(context, "wifi未连接，请打开wifi", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
     *
     * @param context dpValue
     * @return int
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率�?px(像素) 的单�?转成�?dp
     *
     * @param context pxValue
     * @return int (返回dp单位�?
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */

    public static int getwindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();

        return width;

    }

    /**
     * 获取屏幕宽度
     */

    public static int getwindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int height = wm.getDefaultDisplay().getHeight();

        return height;

    }

    /**
     * 是否是手机号
     *
     * @param mobiles
     * @return boolean(true为手机号)
     * ^((13[0-9])|(15[^4,\\D])|(170)|(18[0,5-9])|(14[5,7]))\\d{8}$
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                //.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
                .compile("^((13[0-9])|(15[0-9])|(17[0,6-8])|(18[0-9])|(14[5,7]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 是否是时间 hh:mm
     *
     * @param time
     * @return
     */
    public static boolean isTime(String time) {
        Pattern p = Pattern
                //.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
                .compile("([01][0-9]|2[0-3]):[0-5][0-9]");
        Matcher m = p.matcher(time);
        return m.matches();
    }

    /**
     * @param json
     * @return
     * @description string转Json对象
     * @author liyangkun
     * @createDate 2013-10-15
     */
    public static JSONObject parseFromJson(String json) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(json))
            return null;

        try {
            JSONObject object = new JSONObject(json);
            return object;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // if (Constants.DEBUG) {
            // Log.d(TAG, "CarWizardGPSManager parseFromJson 错误： " + e.getMessage());
            // }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param json
     * @return
     * @description string转Json对象
     * @author liyangkun
     * @createDate 2013-10-15
     */
    public static JSONArray parseFromJsonArray(String json) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(json))
            return null;

        try {
            JSONArray array = new JSONArray(json);
            return array;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // if (Constants.DEBUG) {
            // Log.d(TAG, "CarWizardGPSManager parseFromJson 错误： " + e.getMessage());
            // }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * string字符串获取
     **/
    public static String getStringShared(Context context, String str) {
        String result = "";
        SharedPreferences sp = context.getSharedPreferences("App_Info",
                context.MODE_WORLD_READABLE);
        result = sp.getString(str, "");
        return result;
    }

    /**
     * string字符串保存
     **/
    public static void setStringShared(Context context, String str,
                                       String result) {
        SharedPreferences sp = context.getSharedPreferences("App_Info",
                context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(str, result);
        edit.commit();
    }

    public static void setBooleanShared(Context context, String str,
                                        boolean result) {
        SharedPreferences sp = context.getSharedPreferences("App_Info",
                context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(str, result);
        edit.commit();
    }

    public static boolean getBooleanShared(Context context, String str) {
        boolean result = true;
        SharedPreferences sp = context.getSharedPreferences("App_Info",
                context.MODE_WORLD_READABLE);
        result = sp.getBoolean(str, true);
        return result;
    }

    /**
     * float字符串获取
     **/
    public static float getFloatShared(Context context, String str) {
        float result = 0;
        SharedPreferences sp = context.getSharedPreferences("App_Info",
                context.MODE_WORLD_READABLE);
        result = sp.getFloat(str, 0);
        return result;
    }

    /**
     * float字符串保存
     **/
    public static void setFloatShared(Context context, String str,
                                      float result) {
        SharedPreferences sp = context.getSharedPreferences("App_Info",
                context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putFloat(str, result);
        edit.commit();
    }

    /**
     * 分享功能
     *
     * @param context       上下文
     * @param activityTitle Activity的名字
     * @param msgTitle      消息标题
     * @param msgText       消息内容
     * @param imgPath       图片路径，不分享图片则传null
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    public static int getIntShared(Context context, String str) {
        int result;
        SharedPreferences sp = context.getSharedPreferences("App_Info", 0);
        result = sp.getInt(str, 0);
        return result;
    }

    /**
     * @param context 上下文
     * @param str     key值
     * @param result  value值
     * @作用 设置Int型的SharedPreference
     */
    public static void setIntShared(Context context, String str,
                                    int result) {
        SharedPreferences sp = context.getSharedPreferences("App_Info", 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(str, result);
        edit.commit();
    }


    /**
     * 根据宽度从本地图片路径获取该图片的缩略图
     *
     * @param localImagePath 本地图片的路径
     * @param width          缩略图的宽
     * @param addedScaling   额外可以加的缩放比例
     * @return bitmap 指定宽高的缩略图
     */
    public static Drawable getRoundBitmapByWidth(String localImagePath, int width,
                                                 int height, int addedScaling) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }
        File file = new File(localImagePath);
        if (!file.exists()) {
            return null;
        }
        Bitmap temBitmap = null;

        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();
            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;
            // 加载获取图片的宽高
            BitmapFactory.decodeFile(localImagePath, outOptions);
            int width_x = outOptions.outWidth;
            int widthRatio = (int) Math.ceil(width_x / width);
            outOptions.inSampleSize = widthRatio;
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);

        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
        Drawable drawable = new BitmapDrawable(temBitmap);
        return drawable;
    }

    @SuppressLint("NewApi")
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);

        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 根据宽度从本地图片路径获取该图片的缩略图
     *
     * @param localImagePath 本地图片的路径
     * @param width          缩略图的宽
     * @param addedScaling   额外可以加的缩放比例
     * @return bitmap 指定宽高的缩略图
     */
    public static Drawable getBitmapByWidth(String localImagePath, int width,
                                            int height, int addedScaling) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }
        File file = new File(localImagePath);
        if (!file.exists()) {
            return null;
        }
        Bitmap temBitmap = null;

        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();
            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;
            // 加载获取图片的宽高
            BitmapFactory.decodeFile(localImagePath, outOptions);
            int width_x = outOptions.outWidth;
            int widthRatio = (int) Math.ceil(width_x / width);
            outOptions.inSampleSize = widthRatio;
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
            //temBitmap = ImageHelper.toRoundBitmap(temBitmap);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
        Drawable drawable = new BitmapDrawable(temBitmap);
        return drawable;
    }


    /**
     * 处理图片,不变形处理
     *
     * @param bm           所要转换的bitmap
     * @param newWidth新的宽
     * @param newHeight新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImgHold(Bitmap bm, int newWidth, int newheight) {
        if (bm == null) {
            return bm;
        }
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newheight) / height;
        Matrix matrix = new Matrix();
        if (scaleWidth >= scaleHeight) {
            matrix.postScale(scaleWidth, scaleWidth);
        } else {
            matrix.postScale(scaleHeight, scaleHeight);
        }
        Bitmap newbm = null;
        try {
            newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            // TODO: handle exception
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return newbm;
    }

    /**
     * 判断是否有sd卡
     *
     * @return hasSD
     */
    public static boolean checkHasSD() {
        boolean hasSD = true;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (!externalStorageDirectory.exists()) {
                hasSD = false;
            }
        } else {
            hasSD = false;
        }
        return hasSD;
    }

    /**
     * 时间转换
     *
     * @param date
     * @param format “xxxx-xx-xx xx:xx”
     * @return
     */
    public static String formatDate(long date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        try {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    /**
     * 将现在的时间转换为分钟
     *
     * @param time
     * @return
     */
    public static int minuteTime(String time) {
        MyLog.d(TAG, "here is end time ==>" + time);
        int minute = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3));
        return minute;
    }

    /**
     * 获取view的宽度
     *
     * @param view
     * @return
     */
    public static int getViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    /**
     * 时间转换 HH:mm
     *
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * 是否有提示音
     *
     * @param startTime 响铃时间开始
     * @param endTime   响铃时间结束
     * @param nowTime   当前时间
     * @return
     */
    public static boolean isRing(String startTime, String endTime, String nowTime) {
        boolean result = false;
        int start = minuteTime(startTime);
        int end = minuteTime(endTime);
        int now = minuteTime(nowTime);
        MyLog.d("PushNoRing", "the start  end  now is===>" + start + " , " + end + " , " + now);

        if (now > start && now < end) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    /**
     * 判断是否有sdcard
     *
     * @return
     */
    public static boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }


    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            x = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e) {
            // TODO: handle exception

        }
        return x;
    }


    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.get_data_progressbar, null);// 得到加载view
        TextView tipTextView = (TextView) v.findViewById(R.id.tip_tv);// 提示文字
        TextView tipTextView1 = (TextView) v.findViewById(R.id.tip_tv_1);// 提示文字
        tipTextView1.setVisibility(View.GONE);
        tipTextView.setText(msg);// 设置加载信息
        Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(v);// 设置布局
        return loadingDialog;

    }


    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createBookInfoDialog(Context context, ListenBook book, OnClickListener listener) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.book_info_dialog, null);// 得到加载view
        TextView nameTv = (TextView) v.findViewById(R.id.name_tv);
        TextView authorTv = (TextView) v.findViewById(R.id.author_tv);
        TextView isbnTv = (TextView) v.findViewById(R.id.isbn_tv);
        TextView publishTv = (TextView) v.findViewById(R.id.publish_tv);
        TextView updateTimeTv = (TextView) v.findViewById(R.id.update_time_tv);
        TextView categoryTv = (TextView) v.findViewById(R.id.category_tv);
        TextView descTv = (TextView) v.findViewById(R.id.description_tv);

        nameTv.setText(context.getString(R.string.book_info_name, Tool.getString(book.getName())));
        authorTv.setText(context.getString(R.string.book_info_author, Tool.getString(book.getAuthor())));
        isbnTv.setText(context.getString(R.string.book_info_isbn, Tool.getString(book.getIsbn())));
        publishTv.setText(context.getString(R.string.book_info_publish, Tool.getString(book.getProduction())));
        updateTimeTv.setText(context.getString(R.string.book_info_update, Tool.getTime(book.getAddTime())));
        categoryTv.setText(context.getString(R.string.book_info_category, Tool.getString(book.getCategory())));
        descTv.setText(context.getString(R.string.book_info_desc, Tool.getString(book.getDescription())));
        Button clickBtn = (Button) v.findViewById(R.id.clickbtn);
        clickBtn.setOnClickListener(listener);
        Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(v);// 设置布局
        return loadingDialog;

    }

    public static String getString(String string) {
        if (TextUtils.isEmpty(string) || string.equals("null")) {
            return "未知";
        }
        return string;
    }


    public static CharSequence getTime(String string) {
        if (!TextUtils.isEmpty(string) && !string.equals("null")) {
            String time = string.substring(0, string.indexOf("T"));
            return time;
        } else {
            return "未知";
        }


    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog showInfoDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.get_data_progressbar, null);// 得到加载view
        TextView tipTextView = (TextView) v.findViewById(R.id.tip_tv);// 提示文字
        tipTextView.setText(msg);// 设置加载信息
        Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(v);// 设置布局
        return loadingDialog;

    }

    public static String getFileName(String path) {
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        return fileName;
    }

    /**
     * 获取指定文件大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    /**
     * 高斯模糊
     *
     * @param bitmap
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        try {
            RenderScript rs = RenderScript.create(context);

            //Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

            //Set the radius of the blur
            blurScript.setRadius(25.f);

            //Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);

            //recycle the original bitmap
            bitmap.recycle();

            //After finishing everything, we destroy the Renderscript.
            rs.destroy();
        } catch (NoClassDefFoundError e) {

        }
        //Instantiate a new Renderscript


        return outBitmap;


    }


    //时间显示函数,我们获得音乐信息的是以毫秒为单位的，把把转换成我们熟悉的00:00格式
    public static String ShowTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean saveBitmap(ListenBook book, Bitmap bm) {
        MyLog.e(TAG, "保存图片");
        boolean isSuccess = false;
        File f = new File(Constants.IMAGE_PATH + Tool.getFileName(book.getCover()));
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            MyLog.e(TAG, "已经保存");
            return !isSuccess;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            return isSuccess;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return isSuccess;
        }

    }


    public static int getGravity(boolean isServer) {
        int gravity = 0;
        if (isServer) {
            gravity = Gravity.CENTER;
        } else {
            gravity = Gravity.BOTTOM;
        }

        return gravity;
    }

    public static List<String> getExtSDCardPath() {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public static long getAvailableExternalMemorySize(File path) {
        if (path.exists()) {
//	            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public static long getTotalExternalMemorySize(File path) {
        if (path.exists()) {
            //  File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static String FormatFileSize(long fileS) {// ת���ļ���С
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取当前系统SDK版本号
     */
    public static int getSystemVersion() {
        /*获取当前系统的android版本号*/
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    public static void copyDBToSDcrad() {
        String DATABASE_NAME = "afinal.db";

        String oldPath = "data/data/com.hwang.listenbook/databases/" + DATABASE_NAME;
        String newPath = Constants.BASE_PATH + DATABASE_NAME;

        copyFile(oldPath, newPath);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    private static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * @param string 获取的json字符串中的字符串值
     * @return 正常情况下返回值本身，否则返回字符串"empty"
     */
    public static String jsonString2RealString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "empty";
        } else {
            return string;
        }
    }

    /**
     * @param object 获取的json字符串中的值
     * @return 正常情况下返回值的int型结果，否则返回0
     */
    public static int jsonInt2RealInt(Object object) {
        try {
            return Integer.valueOf(object.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param object 获取的json字符串中的值
     * @return 正常情况下返回值的int型结果，否则返回false
     */
    public static boolean jsonBoolean2RealBoolean(Object object) {
        try {
            return Boolean.valueOf(object.toString());
        } catch (Exception e) {
            return false;
        }
    }
}

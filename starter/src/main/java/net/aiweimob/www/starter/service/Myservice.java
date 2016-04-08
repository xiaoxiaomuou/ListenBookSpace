package net.aiweimob.www.starter.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import net.aiweimob.www.starter.utils.MyConstace;
import net.aiweimob.www.starter.utils.PrefUtils;
import net.aiweimob.www.starter.view.DesktopLayout;

/**
 * Created by Administrator on 2016/4/8.
 */
public class Myservice extends Service{

    /**
     * 包的管理器 PackageManager;
     */
    PackageManager pm ;
    private SharedPreferences sp;

    private Myservice act;

    /**
     * 高优先级的窗体控件
     */
    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    private DesktopLayout mDesktopLayout;

    @Override
    public void onCreate() {
        super.onCreate();

        act = this;

        pm = act.getPackageManager();//获得了包管理器

        sp = getSharedPreferences("config", MODE_PRIVATE);
        createWindowManager();
        createDesktopLayout();

        showDesk();
    }

    /**
     * 悬浮窗是否显示着
     */
    private Boolean isShow;

    /**
     * 是否是拖动，
     * 如果按下，移动，距离小于10个象素，我们认为是点击的动作，按点击的逻辑处理
     * 如果超过10个像素，我们认为发生了拖动，按拖动的逻辑处理
     */
    private boolean isDrop;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 创建悬浮窗体
     */
    private void createDesktopLayout() {

        mDesktopLayout = DesktopLayout.getInstance(this);


        /**
         * 窗体的点击事件
         */
        mDesktopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrop){
                    Log.i("mDesktopLayout", "onclick不执行");
                    return;
                }
                Log.i("mDesktopLayout", "onclick执行了");
                restartMyapp();
            }
        });

        // 读取保存的数据，初始化位置
        mLayoutParams.x= sp.getInt("params_x", 0);
        mLayoutParams.y= sp.getInt("params_y", 0);

        mDesktopLayout.setOnTouchListener(new View.OnTouchListener() {

            /**
             * down 事件的X坐标
             */
            private int downX;
            /**
             * 上一个事件中的X坐标
             */
            private int lastX;
            /**
             * 上一个事件中的Y坐标
             */
            private int lastY;

            @Override
            /**
             * mDesktopLayout 时，不断调用此方法
             * 如果消费了事件，必须返回true
             */
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();

                        isDrop = false; // 按下时，肯定没有拖动

                        break;
                    case MotionEvent.ACTION_UP:

                        // 抬起手指时， 保存控件当前的位置
                        sp.edit().putInt("params_x", mLayoutParams.x).commit();
                        sp.edit().putInt("params_y", mLayoutParams.y).commit();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("ACTION_MOVE========");
                        // 二：移动手指时，记录移动时的坐标点moveX,moveY求得手指在屏幕移动的距离

                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - lastX; // 此处应该求的的是二个相领事件之间的距离
                        int disY = moveY - lastY;

                        // 移动距离超过15象素，认为发生了拖动
                        if (Math.abs(disX) > 15 || Math.abs(disY) > 15) {
                            Log.i("Move", disX + "--大于15了--");
                            isDrop = true;
                        }

                        // 三: 让控件同样移动disX,和disY的距离

                        mLayoutParams.x += disX;
                        Log.i("Paramsx", mLayoutParams.x + "");
                        mLayoutParams.y += disY;

                        mWindowManager.updateViewLayout(mDesktopLayout, mLayoutParams); // 更新view在屏幕的位置
                        Log.i("update", mLayoutParams.x + "----" + mLayoutParams.y);

                        // 四，让downX downY 改变为当前的moveX,moveY,以便于获得二个相领事件之间的距离
                        lastX = moveX;
                        lastY = moveY;

                        break;
                }
                return false;
            }
        });
    }

    /**
     * 设置WindowManager
     */
    private void createWindowManager() {
        // 取得系统窗体
        mWindowManager = (WindowManager) getApplicationContext()
                .getSystemService(WINDOW_SERVICE);

        // 窗体的布局样式
        mLayoutParams = new WindowManager.LayoutParams();

/*        //透明效果
        mLayoutParams.format = PixelFormat.RGBA_8888; */

//        mLayoutParams.alpha = 0.8f;

        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        // 设置显示的模式
        mLayoutParams.format = PixelFormat.RGBA_8888;

        // 设置对齐的方法
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        // 设置窗体宽度和高度
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    /**
     * 显示DesktopLayout
     */
    private void showDesk() {

        isShow = PrefUtils.getBoolean(act, MyConstace.key_is_show, false);
        Log.i("isShow","isShow的值是"+isShow);

        if(!isShow){
            mWindowManager.addView(mDesktopLayout, mLayoutParams);
            PrefUtils.setBoolean(act,MyConstace.key_is_show,true);
        }

    }
    /**
     * 重启我的应用
     */
    private void restartMyapp() {
        //closeDesk();
//        startAPP("net.aiweimob.www.starter");//com.tencent.qqmusic
//        startAPP("com.hwang.listenbook");//com.tencent.qqmusic

        Intent intent = pm.getLaunchIntentForPackage("net.aiweimob.www.starter");
        Log.i("startAPP","准备启动");
        startActivity(intent);


    }

    private void closeDesk() {
        Log.i("closeDesk","进来关闭"+mDesktopLayout+"窗体了");
        mWindowManager.removeView(mDesktopLayout);

    }


}

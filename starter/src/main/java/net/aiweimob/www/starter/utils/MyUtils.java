package net.aiweimob.www.starter.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/4/5.
 */
public class MyUtils {
    /**
     * 显示toast
     * @param ctx
     * @param msg
     */
    public static void showToast(final Activity ctx,final String msg){

        if("main".equals(Thread.currentThread().getName())){
            // 如果是主线程，直接弹toast
            Toast.makeText(ctx, msg,Toast.LENGTH_SHORT).show();
        }else{
            // 如果是子线程，
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

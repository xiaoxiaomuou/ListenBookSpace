package net.aiweimob.www.starter.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/4/5.
 */
public class AppInfoBean {
    /**
     * 应用名称
     */
    public String appName;

    /**
     * 应用的包名
     */
    public String packageName;


    /**
     * 图标
     */
    public Drawable appIcon;

    /**
     * 是否是SD卡中的
     */
    public boolean isInSd;

    /**
     * 是否是系统应用
     */
    public boolean isSys;

    /**
     * APK文件的路径
     */
    public String apkPath;

    /**
     * 判断当前条目是否被选中
     */
    public boolean isSelect ;
}

package net.aiweimob.www.starter.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import net.aiweimob.www.starter.bean.AppInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/5.
 */
public class AppUtils {
    /**
     *  获得所有的应用程序信息
     * @param ctx
     * @return
     */
    public static List<AppInfoBean> getAllAppInfo(Context ctx) {
        List<AppInfoBean> appList = new ArrayList<AppInfoBean>();

        PackageManager pm = ctx.getPackageManager();

        List<PackageInfo> installedPackages = pm.getInstalledPackages(0); // 获得所有的安装包的信息
        for (PackageInfo packageInfo : installedPackages) {
            AppInfoBean appInfo = new AppInfoBean();
            // 设置包名
            appInfo.packageName = packageInfo.packageName;

            appList.add(appInfo);// 将bean 添加至集合

            ApplicationInfo applicationInfo = packageInfo.applicationInfo;

            String appName = applicationInfo.loadLabel(pm).toString();
            appInfo.appName = appName;

            Drawable appIcon = applicationInfo.loadIcon(pm);
            appInfo.appIcon = appIcon;

//			applicationInfo.dataDir ; // /data/data目录
            String apkPath = applicationInfo.sourceDir; // apk 文件的路径
            System.out.println(appName+" : "+apkPath);

            appInfo.apkPath = apkPath;

            File apkFile = new File(apkPath);

            if(apkPath.startsWith("/data")){ // 用户应用
                appInfo.isSys = false;
                System.out.println("根据 路径 值判断，当前应用是用户应用");
            }else{
                System.out.println("根据 路径 值判断，当前应用是系统应用");
                appInfo.isSys = true;
            }

			/*
			 * flags 是应用的信息标记值
			 * ApplicationInfo.FLAG_SYSTEM 是否是系统应用的标记位
			 * 二者按位相与，如果不等于0，说明匹配成功，当前应就拥有，标记位所表示的属性
			 */
            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) !=0){
                System.out.println("根据 flag 值判断，当前应用是系统应用");
            }else{
                System.out.println("根据 flag 值判断，当前应用是用户应用");
            }


            // 匹配成功，说明是在外部存储中
            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                appInfo.isInSd = true;
            }else{
                appInfo.isInSd = false;
            }

        }

        SystemClock.sleep(1000); // 模拟耗时的操作

        return appList;
    }
}

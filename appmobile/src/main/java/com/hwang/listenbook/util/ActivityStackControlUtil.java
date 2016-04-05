package com.hwang.listenbook.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityStackControlUtil {

	private static List<Activity> activityList = new ArrayList<Activity>();

	public static void finish(Activity activity) {
		try {
			activityList.remove(activity);
			activity.finish();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static void remove(Activity activity) {
		try {
			activityList.remove(activity);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static void add(Activity activity) {
		try {
			for(int i=0;i<activityList.size();i++){
				if(activityList.get(i).getLocalClassName().equals(activity.getLocalClassName())){
					activityList.remove(i);
					activity.finish();
				}
			}
			activityList.add(activity);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void finishProgram() {
		try {
			for (Activity activity : activityList) {
				activity.finish();
			}
			activityList.clear();
			android.os.Process.killProcess(android.os.Process.myPid()); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		ActivityStackControlUtil.finishProgram();
	}

}
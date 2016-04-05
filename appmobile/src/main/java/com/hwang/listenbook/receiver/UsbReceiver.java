package com.hwang.listenbook.receiver;

import com.hwang.listenbook.ChooseFilePathActivity;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.Tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UsbReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String path = intent.getDataString();
		path = path.substring(11) + "/"; // path=file:///mnt/usb
//		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
//		|| intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
//			Tool.setStringShared(context, "usbPath", path);
//		} else {
//			Tool.setStringShared(context, "usbPath", "");
//		Toast.makeText(context, "SD卡已插入", Toast.LENGTH_SHORT).show();
//		}
		
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
		|| intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
			Tool.setStringShared(context, "usbPath", path);
			Toast.makeText(context, "SD卡已插入", Toast.LENGTH_SHORT).show();
		} else {
			if (path.equals(Constants.USB_PATH)) {
				Intent intent2 = new Intent();
				intent2.setAction("com.hwang.listenbook.usb");
				context.sendBroadcast(intent2);
			}
			
		}
		
	}

	
}

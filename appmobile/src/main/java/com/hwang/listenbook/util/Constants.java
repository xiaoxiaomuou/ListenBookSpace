package com.hwang.listenbook.util;

import android.os.Environment;

public class Constants {
	
	public static final boolean IS_SERVER = false;
	
	public static final String USB_PATH = "/mnt/usbhost0";
	
	public static final String STORE_PATH = "listenBook/";
	
	public static final String SD_PATH = "/mnt/extsd";
	
	public static final String BASE_PATH = (IS_SERVER?SD_PATH:Environment.getExternalStorageDirectory().getAbsolutePath()) + "/" + STORE_PATH;

//	public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + STORE_PATH;
	
	public static final String IMAGE_PATH = BASE_PATH + "cover/";
	public static final String FILE_PATH = BASE_PATH + "file/";
	public static final String FILE_BOOK_PATH = FILE_PATH + "book/";

	/** 自定义广播 */
	public static final String INTENT_ACTION = "com.hwang.change";
	
	/** 自定义广播 */
	public static final String PHONE_INTENT_ACTION = "com.hwang.change.phone";

//	public static final String BOOK_DOWNLOAD_ACTION = "com.hwang.change.BookDownloadService";

	/** 自定义广播 */
	public static final String PLAY_INTENT_ACTION = "com.hwang.change.play";
	
    /** 自定义接收广播字段 */
    public static final String INTENT_RECEIVER = "receiverString";
    
    /** 收藏*/
    public static final String SHOU_CANG = "shoucang";
    
    /** 书架*/
    public static final String SHU_JIA = "shujia";
    
	/** 听书*/
    public static final String TING_SHU = "tingshu";
    
    /** 下载*/
    public static final String XIA_ZAI = "xiazai";
    
    public static final String FILE_XIA_ZAI = "filexiazai";
    
    public static final String XIA_ZAI_FAILURE = "xiazaifailure";
    
    public static final String PHONE_DOWNLOAD = "phonedownload";
    
    public static final String LOADING ="loading";

    public static final String LOADED ="loaded";

	public static final String DEFS_LOADING_COUNT_KEY = "count";//单本下载时，资源总长度键名(备注:允许重用此值)
	public static final String DEFS_LOADING_CURRENT_KEY = "current";//单本下载时，资源已下载长度键名(备注:允许重用此值)
	public static final String LOADED_RESULT_KEY = "loadedResult";//下载完成后，得到的结果，成功或失败(备注:允许重用此值)
	public static final String LOADED_ERROR_KEY = "loadedError";//下载失败(备注:允许重用此值)
	public static final String LOADED_SUCCESS_KEY = "loadedSuccess";//下载成功(备注:允许重用此值)

    public static final String COVER_DOWN = "coverdown";
	
	public static final boolean ISLOG = true;
	
	public static final String PAUSE = "pause";
	
	public static final String PLAY_MAG ="play";
	
	public static final String PLAY_NEXT = "play_next";
	
	public static final String READY_PLAY = "readyplay";
	
	public static final String START_PLAY = "start_play";
	
	public static final String MUSIC_COUNT = "musiccount";//音乐总时长
	
	public static final String MUSIC_CURRENT = "musiccurrent";//当前时长
	
	public static final String MUSIC_PLAY_NEXT = "nextmusic";//自动播放下一曲

	public static final int PAGE_SIZE = 20;
	
	public static final int YIN_PIN_TYPE = 2;
	
	public static final int WEN_ZI_TYPE = 1;
	
	public static final int PDF_TYPE = 3;
	
	public static final int TEXT_TYPE = 5;
	
	public static final String DOWNLOAD_URL = "http://192.168.43.1:8080/";
//	/**基础接口*/
//	public static final String BASE_URL = "http://read.aiweimob.com";
//	
	/**展示基础接口*/
	public static final String BASE_URL = "http://222.143.28.175:8080";
	/**获取书籍信息列表*/
	public static final String GET_BOOK_INFO_LIST_URL = BASE_URL + "/api/MainContent";
	
	/**获取书籍列表*/
	public static final String GET_BOOK_LIST_URL = BASE_URL + "/api/content";
	
	public static final String GET_APP_URL = "http://readc.aiweimob.com/downdaping.html";
	public static final String BOOK_ID = "bookId";
	public static final String BOOK_FILE_URL = "bookFileUrl";

}
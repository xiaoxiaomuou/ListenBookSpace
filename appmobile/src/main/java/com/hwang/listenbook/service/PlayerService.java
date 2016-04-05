package com.hwang.listenbook.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.hwang.listenbook.App;
import com.hwang.listenbook.R;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.MyLog;
import com.hwang.listenbook.util.Tool;

import net.tsz.afinal.FinalDb;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PlayerService extends Service implements Runnable,
        OnCompletionListener {

    private static final String TAG = "PlayerService";
    /* 定于一个多媒体对象 */
    public MediaPlayer mMediaPlayer = null;

    private int index;

    private FinalDb fDb;
    //    private int currentPosition = 0;// 设置默认进度条当前位置
    private int total = 0;//
    private ListenBook book;

    App app;

    String code = "";

    Receiver mReceiver;

    private ArrayList<ListenBookFileBean> listenBeans = new ArrayList<>();

    private int totalMusicNum;    //该专辑共有多少首歌曲

    /**
     * Notification管理
     */
    public NotificationManager mNotificationManager;
    /**
     * 通知栏按钮广播
     */
    public ButtonBroadcastReceiver bReceiver;
    /**
     * 通知栏按钮点击事件对应的ACTION
     */
    public final static String ACTION_BUTTON = "com.listenbook.intent.action.MusicButtonClickTwo";
    private RemoteViews mRemoteViews;   //写成全局变量，是为了用handler更新
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    /**
     * 上一首 按钮点击 ID
     */
    public final static int BUTTON_PREV_ID = 1;
    /**
     * 播放/暂停 按钮点击 ID
     */
    public final static int BUTTON_PALY_ID = 2;
    public final static String SONG_NAME = "SONG_NAME";
    /**
     * 下一首 按钮点击 ID
     */
    public final static int BUTTON_NEXT_ID = 3;
    /**
     * 关闭notification
     */
    public final static int BUTTON_CANCEL_ID = 4;
    private int notifyId = 100;
    private static final int UPDATE_NOTIFICATION_POPPIC = 11;   //更新播放、暂停图标
//    private static final int UPDATE_NOTIFICATION_POPPRE = 12;   //播放上一首
    private static final int UPDATE_NOTIFICATION_POPNEX = 13;   //播放下一首
//    private static final int HIDE_NOTIFICATION = 12;   //隐藏通知栏
    private MyHandler myHandler;

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fDb = FinalDb.create(this);
        app = (App) this.getApplication();
        mReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter(Constants.PLAY_INTENT_ACTION);
        registerReceiver(mReceiver, intentFilter);
        mMediaPlayer = new MediaPlayer();

        myHandler = new MyHandler(PlayerService.this);
        initMusicBtnReceiver();
//        MyLog.d(TAG, "hrere is oncreate service");
            /* 监听播放是否完成 */
//	        mMediaPlayer.setOnCompletionListener(this);  
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        destoryMusic();
        app.isPlaying = false;

        unregisterReceiver(bReceiver);
    }

    private void destoryMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @SuppressWarnings("static-access")
    public void playMusic() {
//        MyLog.d(TAG, "@@@@@@hrere is the path===>" + listenBeans.get(index).getSrc());

        mMediaPlayer.reset();
        try {

            mMediaPlayer.setDataSource(listenBeans.get(index).getSrc());
//				mMediaPlayer.reset();
            mMediaPlayer.prepare();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listenBeans.get(index).getCode().equals(book.getCurrentCode())) {
            mMediaPlayer.seekTo(book.getCurrent());
        }
        mMediaPlayer.start();
        app.setPlaying(true);
        new Thread(this).start();
        mMediaPlayer.setOnCompletionListener(this);
//			Tool.setStringShared(getApplicationContext(), "currentplay", listenBeans.get(index).getCode());
        app.setCurrentPlay(listenBeans.get(index).getCode());

    }

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);
    }

    // 刷新进度条
    @Override
    public void run() {
        total = mMediaPlayer.getDuration();//
        int currentPosition = 0;
        while (mMediaPlayer != null && currentPosition < total) {
            try {
                Thread.sleep(1000);
                if (mMediaPlayer != null) {
                    currentPosition = mMediaPlayer.getCurrentPosition();
                    Intent intent2 = new Intent();
                    intent2.setAction(Constants.PLAY_INTENT_ACTION);
                    intent2.putExtra(Constants.INTENT_RECEIVER, Constants.MUSIC_CURRENT);//更改购物车下标显示数量
                    intent2.putExtra("current", mMediaPlayer.getCurrentPosition());
                    intent2.putExtra("count", mMediaPlayer.getDuration());

                    sendBroadcast(intent2);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //   PlayerActivity.seekBar.setProgress(currentPosition);
        }

    }

    public void onCompletion(MediaPlayer mp) {
            /* 播放完当前歌曲，自动播放下一首 */
        //这个方法，当遇到error(-38,0)时，就会被执行，造成歌曲播放的混乱
        //但是这个方法是用来自动播放下一首歌曲的，判定方法需要更改一下（2015-11-5 14:02:34）
//	    	MyLog.e(TAG, "hrere is onCompletion,total="+total+",mp-getCurrentPosition ="+mp.getCurrentPosition()+",mp-getAudioSessionId="+mp.getAudioSessionId());
        int currentPosition = mp.getCurrentPosition();
        if (currentPosition == 0) {
            if (index < totalMusicNum - 1) {
                index += 1;
                autoPlayNext(index);
            } else {
                if (app.isPlaying()) {
//                    mMediaPlayer.pause();
                    index = 0;
                    autoPlayNext(index);
                } else {
                    mMediaPlayer.stop();
                }
            }
        } else if (currentPosition > 0) {
            int reminder = total / currentPosition;
            if (reminder < 2) {    //是同一首歌，播放完了
                if (index < totalMusicNum - 1) {
                    index += 1;
                    autoPlayNext(index);
                } else {
                    if (app.isPlaying()) {
//                        mMediaPlayer.pause();
                        index = 0;
                        autoPlayNext(index);
                    } else {
                        mMediaPlayer.stop();
                    }
                }
            }
        }
    }

    private void autoPlayNext(int position) {
        playMusic();
        Intent intent2 = new Intent();
        intent2.setAction(Constants.PLAY_INTENT_ACTION);
        intent2.putExtra(Constants.INTENT_RECEIVER, Constants.MUSIC_PLAY_NEXT);
        intent2.putExtra("index", position);
        sendBroadcast(intent2);

        myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPNEX);
//        showButtonNotify(listenBeans.get(position).getTitle()); //2015-11-17 通知栏显示
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String string = intent.getExtras().getString(Constants.INTENT_RECEIVER);
//				MyLog.e(TAG, "here is Receiver play---------"+string);

            if (string.equals(Constants.READY_PLAY)) {
                if (code.equals(intent.getStringExtra("code"))) {
                    if (index == intent.getIntExtra("index", 0)) {

                    } else {
                        index = intent.getIntExtra("index", 0);
                        getData();
                        playMusic();
                        book.setCurrentCode(listenBeans.get(index).getCode());
                        fDb.update(book);
                    }
                } else {
                    code = intent.getStringExtra("code");
                    index = intent.getIntExtra("index", 0);
                    getData();
                    playMusic();
                    book.setCurrentCode(listenBeans.get(index).getCode());
                    fDb.update(book);
                }

                /**
                 * @说明 为了实现FileListActivity中播放列表与当前播放的数据一致
                 * @反馈 实现不了 2015年11月17日，故注释掉
                 */
//                Tool.setIntShared(context, "musicPositionBackActPlayer", index);
            }

            if (string.equals(Constants.PLAY_MAG)) {
                int msec = intent.getIntExtra("current", 0);
                if (mMediaPlayer != null) {
                    mMediaPlayer.seekTo(msec);
//						if (!mMediaPlayer.isPlaying()) {
//							mMediaPlayer.start();
//						}
                }
            }

            if (string.equals(Constants.START_PLAY)) {
//                showButtonNotify(listenBeans.get(index).getTitle());
                /**
                 * 不能在这里调用showButtonNotify方法。凡是在这里调用此方法
                 * 均会出现需要关闭两次的情况
                 */
                MyLog.e(TAG,"start_play - - - - - - - - mBuilder : "+mBuilder);
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    app.setPlaying(true);
                    myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPPIC);
                }
            }

            if (string.equals(Constants.PAUSE)) {
//                showButtonNotify(listenBeans.get(index).getTitle());
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    app.setPlaying(false);
//                    myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPPIC);
//						book.setCurrent(currentPosition);
//						fDb.update(book);
                }

            }
        }
    }

    public void getData() {    //获取歌曲信息
        book = fDb.findById(code, ListenBook.class);

        //注释这句，是因为mainCode的类型改变了(2015-11-5)(只用改服务器端，手机端不改 2015-11-5 11:59:38)
        listenBeans = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class,
                "mainCode='" + code + "'", "sort");
        totalMusicNum = listenBeans.size();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            app.setPlaying(false);
        }

        myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPNEX);
//        showButtonNotify(listenBeans.get(index).getTitle());
    }

    private void updateMusic(int index) {
        playMusic();
        Intent intent2 = new Intent();
        intent2.setAction(Constants.PLAY_INTENT_ACTION);
        intent2.putExtra(Constants.INTENT_RECEIVER, Constants.READY_PLAY);//更改购物车下标显示数量
        intent2.putExtra("code", book.getCode());
        intent2.putExtra("index", index);
        sendBroadcast(intent2);
    }

    /**
     * 播放上一首
     */
    private void playPrevious() {
        if (index == 0) {
            index = totalMusicNum - 1;
            updateMusic(index);
        } else if (index > 0) {
            updateMusic(--index);
        }
    }

    private void nfcPlayOrPause() {
        Intent intent2 = new Intent();
        intent2.setAction(Constants.PLAY_INTENT_ACTION);
        intent2.putExtra("index", index);
        if (app.isPlaying) {
            intent2.putExtra(Constants.INTENT_RECEIVER, Constants.PAUSE);//zanting
            app.setPlaying(false);
        } else {
            intent2.putExtra(Constants.INTENT_RECEIVER, Constants.START_PLAY);//bofang
            app.setPlaying(true);
        }
        sendBroadcast(intent2);    //发送播放/暂停广播
    }

    private void playNext() {
        if (index == (totalMusicNum - 1)) {
            index = 0;
            updateMusic(index);
        } else if (index < (totalMusicNum - 1)) {
            updateMusic(++index);
        }
    }

    /**
     * @作用 停止音频。在notification的cancel方法调用后调用，意即关闭notification时停止播放音频
     */
    private void stopMusic() {
        Intent intent2 = new Intent();
        intent2.setAction(Constants.PLAY_INTENT_ACTION);
        if (app.isPlaying) {
            intent2.putExtra(Constants.INTENT_RECEIVER, Constants.PAUSE);//暂停
            intent2.putExtra("index", index);
            app.setPlaying(false);
            sendBroadcast(intent2);    //发送播放/暂停广播
        }
    }

    /**
     * 带按钮的通知栏点击广播接收
     */
    public void initMusicBtnReceiver() {
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public void showButtonNotify(String nameStr) {
//        mNotificationManager.cancelAll();
        mBuilder = new NotificationCompat.Builder(this);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_music);
        mRemoteViews.setImageViewResource(R.id.nfc_iv_song_icon, R.drawable.ic_launcher);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.nfc_tv_song_singer, book.getName());
        MyLog.e(TAG, "显示 = " + nameStr);
        mRemoteViews.setTextViewText(R.id.nfc_tv_song_name, nameStr);
        //如果版本号低于（3.0），那么不显示按钮
        if (Tool.getSystemVersion() <= 9) {
            mRemoteViews.setViewVisibility(R.id.nfc_ll_button, View.GONE);
        }

        MyLog.e(TAG, "app.isPlaying = " + app.isPlaying);
        if (app.isPlaying) {
            mRemoteViews.setImageViewResource(R.id.nfc_ibtn_play, R.drawable.nfc_btn_pause_white);
        } else {
            mRemoteViews.setImageViewResource(R.id.nfc_ibtn_play, R.drawable.nfc_btn_play_white);
        }

        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
        /* 上一首按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.nfc_ibtn_prev, intent_prev);
        /* 播放/暂停  按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        buttonIntent.putExtra(SONG_NAME, nameStr);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.nfc_ibtn_play, intent_paly);
        /* 下一首 按钮  */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.nfc_ibtn_next, intent_next);
        //取消按钮
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_CANCEL_ID);
        PendingIntent intent_cancel = PendingIntent.getBroadcast(this, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.nfc_iv_cancel, intent_cancel);

        mBuilder.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(nameStr)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);
        notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notify.contentView = mRemoteViews;
        if (Tool.getSystemVersion() >= 16) {
            notify.priority = Notification.PRIORITY_DEFAULT;    // 设置该通知优先级
        }

//        if (isNfcShow==1)
//        mNotificationManager.notify(notifyId, notify);
    }

    private Notification notify;
    private NotificationCompat.Builder mBuilder;
    private int isNfcShow = 0;

    /**
     * @param flags 属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     *                 点击去除： Notification.FLAG_AUTO_CANCEL
     * @说明 获取默认的pendingIntent, 为了防止2.3及以下版本报错
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 广播监听按钮点击时间
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String intentReceiver = intent.getExtras().getString(Constants.INTENT_RECEIVER);
            MyLog.e(TAG,"通知栏----------------------  "+intentReceiver);
            String action = intent.getAction();
            if (action.equals(ACTION_BUTTON)) {
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_PREV_ID:    //播放上一首
                        playPrevious();
                        myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPNEX);
//                        showButtonNotify(listenBeans.get(index).getTitle());
//                        myHandler.sendEmptyMessage(SET_MUSIC_NAME);
                        break;
                    case BUTTON_PALY_ID:    //播放/暂停
                        nfcPlayOrPause();
//                        showButtonNotify(intent.getStringExtra(SONG_NAME));    //更新歌曲名称
                        myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPPIC);
                        break;
                    case BUTTON_NEXT_ID:    //播放下一首
                        playNext();
//                        showButtonNotify(listenBeans.get(index).getTitle());
                        myHandler.sendEmptyMessage(UPDATE_NOTIFICATION_POPNEX);
                        break;
                    case BUTTON_CANCEL_ID:
                        mNotificationManager.cancel(notifyId);
//                        isNfcShow = 0;
                        stopMusic();
                        break;
                    default:
                        break;
                }
            }
        }
    }


    static class MyHandler extends Handler {
        WeakReference<PlayerService> mActivity;

        MyHandler(PlayerService activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerService theActivity = mActivity.get();
            switch (msg.what) {
                case UPDATE_NOTIFICATION_POPPIC:
                    MyLog.e(TAG,"- - - - - - - - - - - - -app.isplaying : "+theActivity.app.isPlaying);
                    if (theActivity.app.isPlaying) {
                        theActivity.mRemoteViews.setImageViewResource(R.id.nfc_ibtn_play, R.drawable.nfc_btn_pause_white);
                    } else {
                        theActivity.mRemoteViews.setImageViewResource(R.id.nfc_ibtn_play, R.drawable.nfc_btn_play_white);
                    }
                    theActivity.mNotificationManager.notify(theActivity.notifyId, theActivity.notify);   //通知一下，否则不会更新
                    break;
                case UPDATE_NOTIFICATION_POPNEX:
                    theActivity.showButtonNotify(theActivity.listenBeans.get(theActivity.index).getTitle());
                    theActivity.mNotificationManager.notify(theActivity.notifyId, theActivity.notify);   //通知一下，否则不会更新
                    break;
               /* case HIDE_NOTIFICATION:
                    theActivity.mRemoteViews.setViewVisibility(R.id.nfc_rl,View.GONE);
                    break;*/
                default:
                    break;
            }
        }
    }

}

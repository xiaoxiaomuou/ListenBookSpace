package com.hwang.listenbook;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hwang.listenbook.R.id;
import com.hwang.listenbook.bean.ListenBook;
import com.hwang.listenbook.bean.ListenBookFileBean;
import com.hwang.listenbook.util.Constants;
import com.hwang.listenbook.util.Tool;
import com.hwnag.listenbook.myView.VoiceAlertDialog;

import net.tsz.afinal.FinalDb;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PlayerActivity extends BaseActivity {

    private static final String TAG = "PlayerActivity";

    private View mainView;
    private ImageView coverIv;
    private SeekBar seekBar;
    private TextView nameTv;
    private TextView currentTv;
    private TextView totalTv;
    private ImageView backIv;
    private ImageView operIv;
    private ImageView collectIv;
    //    private ImageView serverCoverIv;
    private View coverLayout;
    //	private View serverCoverLayout;
    private ImageView voiceBtn;
    private ImageView backAct;

    private ListenBook book;
    public ArrayList<ListenBookFileBean> bookFileBeans;
    public int index;
//    private Context context;
    private FinalDb fDb;
//    private Thread cancelThread;
    App app;
    private Receiver mReceiver;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    private AudioManager mAudioManager;

    private static final int NAME_SET_INITVIEW = 11; //实例化视图
    private static final int SET_MUSIC_NAME = 12;    //设置要播放的音乐的名字
    private static final int SET_MUSIC_PLAY = 13;    //设置音频播放
    private static final int SET_MUSIC_PAUSE = 14;  //设置音频暂停播放

    private MyHandler myHandler;

    private int totalMusicNum;


    static class MyHandler extends Handler {
        WeakReference<PlayerActivity> mActivity;

        MyHandler(PlayerActivity mainActivityServer) {
            mActivity = new WeakReference<>(mainActivityServer);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PlayerActivity theActivity = mActivity.get();
            String nameTvStr = theActivity.bookFileBeans.get(theActivity.index).getTitle();
            switch (msg.what) {
                case NAME_SET_INITVIEW:
                    theActivity.initView();
                    if (TextUtils.isEmpty(theActivity.bookFileBeans.get(theActivity.index).getTitle())) {
                        theActivity.nameTv.setText(theActivity.book.getName() + "-" + theActivity.index);
                    } else {
                        theActivity.nameTv.setText(nameTvStr);
                    }
                    break;

                case SET_MUSIC_NAME:
                    theActivity.nameTv.setText(nameTvStr);
                    break;

                case SET_MUSIC_PLAY:
                    theActivity.app.setPlaying(true);
                    theActivity.operIv.setImageResource(R.drawable.paus_btn);
                    break;

                case SET_MUSIC_PAUSE:
                    theActivity.app.setPlaying(false);
                    theActivity.operIv.setImageResource(R.drawable.play_btn);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        app = (App) this.getApplication();
//        context = this;

        book = (ListenBook) getIntent().getSerializableExtra("book");
        index = getIntent().getIntExtra("index", 0);

        myHandler = new MyHandler(this);
        new Thread(new Runnable() {
            public void run() {
                getData();
            }
        }).start();
        mReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter(Constants.PLAY_INTENT_ACTION);
        registerReceiver(mReceiver, intentFilter);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        totalMusicNum = Tool.getIntShared(this, "totalMusicNumActFileList");
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getData() {
        fDb = FinalDb.create(this);
//		bookFileBeans = (ArrayList<ListenBookFileBean>) fDb.findAllByWhere(ListenBookFileBean.class, "mainCode ='" + book.getCode() + "'","sort");
        bookFileBeans = (ArrayList<ListenBookFileBean>) getIntent().getSerializableExtra("files");

        myHandler.sendEmptyMessage(NAME_SET_INITVIEW);
    }

    @SuppressLint("NewApi")
    private void initView() {
        mainView = (View) findViewById(id.main);
        coverIv = (ImageView) findViewById(id.cover_iv);
        seekBar = (SeekBar) findViewById(id.seek_bar);
        nameTv = (TextView) findViewById(id.act_player_tv_name);
        currentTv = (TextView) findViewById(id.current_time_tv);
        totalTv = (TextView) findViewById(id.total_time_tv);
        backIv = (ImageView) findViewById(id.back_iv);
        operIv = (ImageView) findViewById(id.oper_iv);
        collectIv = (ImageView) findViewById(id.collect_iv);
//        serverCoverIv = (ImageView) findViewById(id.server_cover);
        coverLayout = (View) findViewById(id.cover_layout);
//		serverCoverLayout = (View) findViewById(id.server_cover_layout);
        voiceBtn = (ImageView) findViewById(id.voice_btn);
        backAct = (ImageView) findViewById(id.act_player_iv_back);

        coverLayout.setVisibility(View.VISIBLE);

        coverIv.setImageBitmap(Tool.getImageThumbnail(book.getCover(),
                Tool.dip2px(this, 120), Tool.dip2px(this, 160)));

        backAct.setOnClickListener(this);
        backIv.setOnClickListener(this);
        operIv.setOnClickListener(this);
        collectIv.setOnClickListener(this);
        voiceBtn.setOnClickListener(this);

        /*if (!book.isCollect()) {
            collectIv.setImageResource(R.drawable.play_collect_normal);
        } else {
            collectIv.setImageResource(R.drawable.play_collect_pressed);
        }*/
        if (app.isPlaying()) {
            operIv.setImageResource(R.drawable.paus_btn);
        } else {
            operIv.setImageResource(R.drawable.play_btn);
        }
		 /* 播放进度监听 */
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
    }

    private void updateMusic(int index) {
        Intent intent2 = new Intent();
        intent2.setAction(Constants.PLAY_INTENT_ACTION);
        /**
         * @说明 加上这个判断是避免在点击播放下一首的时候，歌曲是暂停状态
         */
        if (!app.isPlaying) {
            intent2.putExtra(Constants.INTENT_RECEIVER, Constants.START_PLAY);//发送播放广播
            myHandler.sendEmptyMessage(SET_MUSIC_PLAY);     //更改播放/暂停图标为暂停标识
        }

        intent2.putExtra(Constants.INTENT_RECEIVER, Constants.READY_PLAY);//更改购物车下标显示数量
        intent2.putExtra("code", book.getCode());
        intent2.putExtra("index", index);
//        MyLog.e(TAG, "book.code = " + book.getCode() + "|---------|index = " + index);

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
//        MyLog.e(TAG,"播放//////////////////////暂停");
        Intent intent2 = new Intent();
        intent2.setAction(Constants.PLAY_INTENT_ACTION);
        if (app.isPlaying) {
            intent2.putExtra(Constants.INTENT_RECEIVER, Constants.PAUSE);//zanting
            app.setPlaying(false);
        } else {
            intent2.putExtra(Constants.INTENT_RECEIVER, Constants.START_PLAY);//bofang
            app.setPlaying(true);
        }
        sendBroadcast(intent2);    //发送播放/暂停广播
    }

    /**
     * @作用 点击播放下一首歌曲
     */
    private void playNext() {
//        MyLog.e(TAG,"播放下一一一一一一首");
        if (index == (totalMusicNum - 1)) {
            index = 0;
            updateMusic(index);
        } else if (index < (totalMusicNum - 1)) {
            updateMusic(++index);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.act_player_iv_back: //返回
                exitActivity();
                break;

            case id.back_iv:    //播放上一首
                playPrevious();
                break;

            case id.oper_iv:    //播放/暂停
                nfcPlayOrPause();
                break;

            case id.collect_iv:     //播放下一首
                playNext();
                break;
            case id.voice_btn:
                createVoiceDialog();
                break;
            default:
                break;
        }
        super.onClick(v);
    }


    /* 拖放进度监听 ，别忘了Service里面还有个进度条刷新*/
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
	        /*假设改变源于用户拖动*/
            if (fromUser) {
                Intent intent2 = new Intent();
                intent2.setAction(Constants.PLAY_INTENT_ACTION);
                intent2.putExtra(Constants.INTENT_RECEIVER, Constants.PLAY_MAG);//更改购物车下标显示数量
                intent2.putExtra("current", progress);
                sendBroadcast(intent2);

            }
            totalTv.setText(Tool.ShowTime(seekBar.getMax()));
            currentTv.setText(Tool.ShowTime(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // PlayerService.mMediaPlayer.pause(); // 开始拖动进度条时，音乐暂停播放
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // PlayerService.mMediaPlayer.start(); // 停止拖动进度条时，音乐开始播放
        }
    }


    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String string = intent.getExtras().getString(Constants.INTENT_RECEIVER);

//            MyLog.e(TAG, "onReceive = " + string);

            if (string.equals(Constants.MUSIC_COUNT)) {
                seekBar.setMax(intent.getIntExtra("count", 0));
                totalTv.setText(Tool.ShowTime(seekBar.getMax()));
            }

            if (string.equals(Constants.MUSIC_CURRENT)) {
                int msec = intent.getIntExtra("current", 0);
                seekBar.setProgress(msec);    //报错空指针 2015-11-5 13:46:01
                currentTv.setText(Tool.ShowTime(msec));
                seekBar.setMax(intent.getIntExtra("count", 0));
                totalTv.setText(Tool.ShowTime(seekBar.getMax()));
            }

            if (string.equals(Constants.START_PLAY) ) {
                myHandler.sendEmptyMessage(SET_MUSIC_PLAY);
            }

            if (string.equals(Constants.PAUSE)) {
                myHandler.sendEmptyMessage(SET_MUSIC_PAUSE);
            }

            if (string.equals(Constants.READY_PLAY)) {
                index = intent.getIntExtra("index", 0); //更新歌曲标识
                myHandler.sendEmptyMessage(SET_MUSIC_NAME);
                myHandler.sendEmptyMessage(SET_MUSIC_PLAY); //更改播放按钮图标
            }

            if (string.equals(Constants.MUSIC_PLAY_NEXT)) {
                index = intent.getIntExtra("index", 0);
                if (TextUtils.isEmpty(bookFileBeans.get(index).getTitle())) {
                    nameTv.setText(book.getName() + "-" + index);
                } else {
                    nameTv.setText(bookFileBeans.get(index).getTitle());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注释 去掉“播放中”的状态
        Tool.setIntShared(this, "musicPositionBackActPlayer", index);
//        MyLog.e(TAG, "onPause---------index = " + index);
    }

    private void createVoiceDialog() {
        final VoiceAlertDialog ad = new VoiceAlertDialog(PlayerActivity.this, mAudioManager);
        final int currentVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        ad.setTitle(getString(R.string.voice));
        ad.setMaxVoice(mMaxVolume);
        ad.setCurrentVoice(currentVoice);
    }

}

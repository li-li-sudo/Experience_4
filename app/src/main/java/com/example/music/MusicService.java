package com.example.music;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;

import android.os.Bundle;
import android.os.IBinder;

import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private static final String TAG = "JalLog::MusicService";
    private MediaPlayer mPlayer;
    private Timer timer;
    private Music mMusic;
    private List<Music> mMusicList;
    private int mPosition;
    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicList = MusicList.getMusicList(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPosition = intent.getExtras().getInt("position");
        playIndex(mPosition);
        return super.onStartCommand(intent, flags, startId);
    }
   //Binder跨进餐通信
    public class MyBinder extends Binder {
        //判断歌曲是否正在播放
        public boolean isPlaying(){
            return mPlayer.isPlaying();
        }
        //判断现在是否没有歌曲播放
        public boolean isNullOfPlayer(){
            return mPlayer == null;
        }
        //播放与暂停
        public void play_pause() {
            //如果歌曲在播放，点击则暂停
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                Log.i(TAG, "Play stop");
            //歌曲没有播放，点击开始播放
            } else {
                mPlayer.start();
                Log.i(TAG, "Play start");
            }
        }
        //上一曲
        public void pre(){
            mPosition = (mPosition - 1 + mMusicList.size()) % mMusicList.size();
            playIndex(mPosition);
        }
        //下一曲
        public void next(){
            mPosition = (mPosition + 1) % mMusicList.size();
            playIndex(mPosition);
        }
        //播放
       public void start(){
            mPlayer.start();
       }
        //暂停
        public void stop(){
            mPlayer.pause();
        }
        //当前歌曲位置
        public int getPosition(){
            return mPosition;
        }
        //Returns the length of the mMusic in milliseconds

        //Return the name of the mMusic
        //歌名
        public String getName(){
            return mMusic.getName();
        }

        //Set the progress of mMusic playback in milliseconds
       //歌曲播放位置
        public void seekTo(int mesc){
            mPlayer.seekTo(mesc);
        }
    }

    private void playIndex(int position) {

        if (mPlayer==null){
            mPlayer = new MediaPlayer();
        }
        if (mMusic == mMusicList.get(position)){
            return;// continue play this mMusic.
        }

        mPlayer.reset();//重置音乐播放器
        mMusic = mMusicList.get(position);//获取位置
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//指定流媒体类型
        addTimer();//添加计时器
        try {
            mPlayer.setDataSource(mMusic.getUrl());
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();//播放音乐
    }
    //添加计时器用于设置音乐播放器中的播放进度条
    private void addTimer() {
        if(timer==null){
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    if(mPlayer==null)return;
                    int duration=mPlayer.getDuration();//获取歌曲总时长
                    int currentPosition=mPlayer.getCurrentPosition();//获取播放进度
                    Message msg=DetailActivity.handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至消息对象中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    msg.setData(bundle);
                    //将消息发送到主线程的消息队列
                    DetailActivity.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒执行一次
            timer.schedule(task,5,500);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "MusicService :: onBind()");
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MusicService :: onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "MusicService :: onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "MusicService :: onUnbind()");
        super.onRebind(intent);
    }

}
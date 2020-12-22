package com.example.music;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "JalLog::DetailActivity";
    private Button BtnPre, BtnPlayPause, BtnNext,back;
    private static SeekBar seekBar;     //进度条
    private MyConnection mConnection;
    private static TextView title,artist;
    private static List<Music> mMusicList;
    private static MusicService.MyBinder mBinder;
    /*建立连接*/
    class MyConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mBinder = (MusicService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
        }
    }
    /*配置界面*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "DetailActivity :: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        bindView();
        bindMusicService();
    }

    /*建立服务*/
    private void bindMusicService() {
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        intent.putExtras(getIntent().getExtras());
        startService(intent);
        mConnection = new MyConnection();
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        Log.i(TAG, "mBinder:"+ mBinder);
    }
    /*界面显示及进度条点击处理*/
    private void bindView() {
        mMusicList=MusicList.getMusicList(this);
        BtnPre=findViewById(R.id.btn_pre);
        BtnPlayPause=findViewById(R.id.btn_play_pause);
        BtnNext=findViewById(R.id.btn_next);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title);
        artist=findViewById(R.id.artist);

        seekBar=findViewById(R.id.seekBar);
        Bundle bundle = getIntent().getExtras();
        String curtitle = mMusicList.get(bundle.getInt("position")).getTitle();
        String curartist=mMusicList.get(bundle.getInt("position")).getSinger();
        title.setText(curtitle);
        artist.setText(curartist);
        back.setOnClickListener(this);
        BtnPre.setOnClickListener(this);
        BtnPlayPause.setOnClickListener(this);
        BtnNext.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条改变时，会调用此方法
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            //滑动条开始滑动时调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            //滑动条停止滑动时调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //根据拖动的进度改变音乐播放进度
                int progress=seekBar.getProgress();//获取seekBar的进度
                    mBinder.seekTo(progress);//改变播放进度
            }
        });

    }
    @SuppressLint("HandlerLeak")
    public static Handler handler=new Handler(){//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            int duration=bundle.getInt("duration"); //获取歌曲总时长
            int currentPosition=bundle.getInt("currentPosition");//获取播放进度

            if(currentPosition < duration){
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
            }
            else{
                nextsong();
            }
        }
    };
    public static void nextsong(){
        mBinder.next();
        int position1=mBinder.getPosition();
        String nexttitle=mMusicList.get(position1).getTitle();
        String nextartist=mMusicList.get(position1).getSinger();
        title.setText(nexttitle);
        artist.setText(nextartist);
    }
    /*按钮点击处理*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*上一曲*/
            case R.id.btn_pre:
                mBinder.pre();
                int position=mBinder.getPosition();
                String pretitle=mMusicList.get(position).getTitle();
                String preartist=mMusicList.get(position).getSinger();
                title.setText(pretitle);
                artist.setText(preartist);
                break;
                /*暂停/开始*/
            case R.id.btn_play_pause:
                mBinder.play_pause();
                if(mBinder.isPlaying()){
                    BtnPlayPause.setBackgroundResource(R.drawable.play);
                }
                else
                    BtnPlayPause.setBackgroundResource(R.drawable.pause);
                break;
                /*下一曲*/
            case R.id.btn_next:
                nextsong();
                break;
                /*返回*/
            case R.id.back:
                Intent intent1=new Intent(PlayActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
                default:
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "DetailActivity :: onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "DetailActivity :: onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "DetailActivity :: onStop()");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "DetailActivity :: onPause()");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "DetailActivity :: onRestart()");
        super.onRestart();
    }
    @Override
    protected void onResume() {
        Log.i(TAG, "DetailActivity :: onResume()");
        super.onResume();
    }
}
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

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "JalLog::DetailActivity";
    private Button BtnPre, BtnPlayPause, BtnNext,back;
    private static SeekBar seekBar;
    private MyConnection mConnection;
    private TextView title,artist;
    private List<Music> mMusicList;
    private MusicService.MyBinder mBinder;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "DetailActivity :: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        bindView();
        bindMusicService();
    }


    private void bindMusicService() {
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        intent.putExtras(getIntent().getExtras());
        startService(intent);
        mConnection = new MyConnection();
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        Log.i(TAG, "mBinder:"+ mBinder);
    }
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
                if (progress==seekBar.getMax()) {//当滑动条到末端
                    BtnPlayPause.setBackgroundResource(R.drawable.pause);
                }
                else if(mBinder.isPlaying()){
                    BtnPlayPause.setBackgroundResource(R.drawable.play);
                }
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
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            //歌曲总时长
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+"";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+"";
            }
            //tv_total.setText(strMinute+":"+strSecond);
            //歌曲当前播放时长
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+" ";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+" ";
            }
        //    tv_progress.setText(strMinute+":"+strSecond);
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pre:
                mBinder.pre();
                int position=mBinder.getPosition();
                String pretitle=mMusicList.get(position).getTitle();
                String preartist=mMusicList.get(position).getSinger();
                title.setText(pretitle);
                artist.setText(preartist);
                break;
            case R.id.btn_play_pause:
                mBinder.play_pause();
                if(mBinder.isPlaying()){
                    BtnPlayPause.setBackgroundResource(R.drawable.play);
                }
                else
                    BtnPlayPause.setBackgroundResource(R.drawable.pause);
                break;
            case R.id.btn_next:
                mBinder.next();
                int position1=mBinder.getPosition();
                String nexttitle=mMusicList.get(position1).getTitle();
                String nextartist=mMusicList.get(position1).getSinger();
                title.setText(nexttitle);
                artist.setText(nextartist);
                break;
            case R.id.back:
                Intent intent1=new Intent(DetailActivity.this,MainActivity.class);
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
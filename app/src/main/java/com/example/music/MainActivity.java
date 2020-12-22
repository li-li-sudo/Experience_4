package com.example.music;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "JalLog::MainActivity";
    private MyConnection mConnection;   //服务器连接
    private MusicService.MyBinder mBinder;  //跨进程通信服务
    private List<Music> mMusicList;     //音乐列表
    private ListView mListView;     //显示音乐
    private Context mContext;
    private int mPpositionOfPlaying;//正在播放的位置
    /*连接服务*/
    class MyConnection implements ServiceConnection {
        private static final String TAG = "LogMyConnection";
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MusicService.MyBinder) service;
            showListView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
    /*配置页面*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        requestPermission();
    }

    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }else {
            initView();
        }
    }
    /*获取音乐，并展示*/
    private void initView() {
        mListView = findViewById(R.id.listView);
        mMusicList = MusicList.getMusicList(this);
        if (mBinder == null){//mBinder为null，则bindService尚未完成内容
            bindMusicService();
        }else{
            showListView();
        }
    }
    /*展示音乐列表*/
    private void showListView() {
        /*判断当前是否有音乐正在播放，有则获取正在播放的音乐在列表中的位置*/
        if ( mBinder==null || mBinder.isNullOfPlayer()){
            mPpositionOfPlaying = -1;
        } else {
            mPpositionOfPlaying = mBinder.getPosition();//获取当前播放的位置
        }
        MusicAdapter adapter = new MusicAdapter(this, mMusicList, mPpositionOfPlaying);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
    }
    /*建立服务器连接*/
    private void bindMusicService() {
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        mConnection = new MyConnection();
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    /*获取读文件权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED){
                            String s = permissions[i];
                            Toast.makeText(this,s+getResources().getString(R.string.rejectPermission),Toast.LENGTH_SHORT).show();
                        }else{
                            initView();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    /*销毁活动*/
    @Override
    protected void onDestroy() {
        Log.i(TAG, "MainActivity :: onDestroy()");
        super.onDestroy();
    }
    @Override
    protected void onStart() {
        Log.i(TAG, "MainActivity :: onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "MainActivity :: onStop()");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "MainActivity :: onPause()");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "MainActivity :: onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "MainActivity :: onResume()");
        super.onResume();
        initView();
    }
}
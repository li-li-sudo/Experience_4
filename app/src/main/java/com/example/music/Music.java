package com.example.music;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

//将对象转换为可以传输的二进制流(二进制序列)的过程,
//通过序列化,转化为可以在网络传输或者保存到本地的流(序列),从而进行传输数据 。
public  class Music implements Parcelable {
    private String title; //名称
    private String singer;//歌手
    private String album;//专辑
    private String url;//地址
    private long size;//大小
    private long time;//时长
    private String name;//歌名

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Music(){

    };


    protected Music(Parcel in) {
        title = in.readString();
        singer = in.readString();
        album = in.readString();
        url = in.readString();
        size = in.readLong();
        time = in.readLong();
        name = in.readString();
    }

    //反序列化
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    //序列化
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(singer);
        dest.writeString(album);
        dest.writeString(url);
        dest.writeLong(size);
        dest.writeLong(time);
        dest.writeString(name);
    }




    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", url='" + url + '\'' +
                ", size=" + size +
                ", time=" + time +
                ", name='" + name + '\'' +
                '}';
    }
//判断是否一致
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return size == music.size &&
                time == music.time &&
                title .equals( music.title)&&
                album.equals(music.album)&&
                url.equals(music.url)&&
                name.equals(music.name);
    }

}
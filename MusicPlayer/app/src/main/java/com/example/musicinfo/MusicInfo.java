package com.example.musicinfo;

import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MusicInfo {
    private File file; // 对应的音乐文件
    private String absPath; // 音乐的绝对路径
    private String musicName; // 音乐文件名
    private String musicDate; // 音乐的修改日期
    private double musicSize; // 音乐文件大小
    private String title; // 音乐的标题
    private String artist; // 音乐人
    private String album; // 专辑名
    private String duration; // 音乐的时长
    private String musicFormat; // 音乐文件格式
    private byte[] pic; // 专辑图片对应的字节数组
    /* 获取音乐元数据信息，包括标题、音乐人、专辑名等 */
    private static MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    public MusicInfo(File file) {
        this.file = file;
        this.absPath  = file.getAbsolutePath();
        String illegal = "Illegal music file format!";
        // 先要验证文件的存在性和合法性
        if (!file.exists() || file.isDirectory() || !absPath.contains("."))
            throw new IllegalArgumentException(illegal);
        if (!this.absPath.toLowerCase().endsWith(".mp3") &&
                !this.absPath.toLowerCase().endsWith(".m4a"))
            throw new IllegalArgumentException(illegal);
        this.musicFormat = absPath.toLowerCase().substring(absPath.lastIndexOf('.') + 1);
        this.musicName = file.getName();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(file.lastModified());
        this.musicDate = dateFormat.format(date); // 将修改日期格式转换为可读的日期格式
        this.musicSize = (double)file.length() / 1024 /1024; // 单位从字节转换为MB
        try {
            mmr.setDataSource(absPath);
            this.title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            this.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            this.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            this.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            this.pic = mmr.getEmbeddedPicture();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.duration != null) { // 由于从mmr中获取的播放时长单位为毫秒，所以需要将其格式转换为时间
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            Date time = new Date(Long.parseLong(duration));
            this.duration = timeFormat.format(time);
        }
    }

    public File getFile() {
        return file;
    }

    public String getAbsPath() {
        return absPath;
    }

    private String getMusicName() {
        return musicName;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getMusicFormat() {
        return musicFormat;
    }

    public String getMusicDate() {
        return musicDate;
    }

    public double getMusicSize() {
        return musicSize;
    }

    public byte[] getPic() {
        return pic;
    }

    @NonNull
    @Override
    public String toString() {
        return getMusicName();
    }
}

package com.artharyoung.contentprovider.db;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by syamiadmin on 2016/7/19.
 */
public class Utils {

    public static final String DATA_TABLE_NAME = "VideoPlayHistory";
    public static final int VERSION = 1;
    //Video数据表的定义
    public static final String TABLE_VIDEO_NAME = "Video";//数据表名
    public static final String Video_id = "_id";
    public static final String Video_title="video_title";
    public static final String Video_album="video_album";
    public static final String Video_artist="video_artist";
    public static final String Video_displayName="video_displayName";
    public static final String Video_mimeType="video_mimeType";
    public static final String Video_path="video_path";
    public static final String Video_size="video_size";
    public static final String Video_duration="video_duration";
    public static final String Video_playDuration="video_playDuration";
    public static final String Video_createdDate="video_createdDate";

    //获取当前时间的方法
    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
}

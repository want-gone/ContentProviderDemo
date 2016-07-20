package com.artharyoung.contentprovider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by syamiadmin on 2016/7/18.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        /**CursorFactory设置为null,使用默认值**/
        super(context, Utils.DATA_TABLE_NAME, null, Utils.VERSION);
    }

    /**
     * 初次使用数据库时初始化数据库表
     * integer primary key autoincrement
     * 使用自增长字段作为主键
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
    // 创建Video数据表
        String table_video = "create table if not exists " + Utils.TABLE_VIDEO_NAME + " ( "
                + Utils.Video_id + " integer primary key autoincrement,"
                + Utils.Video_title + " text not null, "
                + Utils.Video_album + " text, "
                + Utils.Video_artist + " text,"
                + Utils.Video_displayName + " text, "
                + Utils.Video_mimeType + " text, "
                + Utils.Video_path + " text, "
                + Utils.Video_size + " integer, "
                + Utils.Video_duration + " integer, "
                + Utils.Video_playDuration + " integer,"
                + Utils.Video_createdDate + " timestamp default (datetime('now', 'localtime')));";
        Log.d(TAG, "onCreate() called with: " + "table_video = [" + table_video + "]");
        db.execSQL(table_video);
        //开启WAL模式，提高并发。缺点是由于读的时候会比较日记确保数据的完整性，性能相对下降
//        db.enableWriteAheadLogging();
    }

    /**如果VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade**/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //升级时增加一列，onCreate建表时需要更改代码添加，这样旧表的数据会保留
//        db.execSQL("alter table " + Common.TABLE_VIDEO_NAME + " add "
//                + Common.Video_duration + "integer");

        //完全删除已存数据重新建表
        db.execSQL("drop table if exists " + Utils.TABLE_VIDEO_NAME);
        onCreate(db);
    }
}

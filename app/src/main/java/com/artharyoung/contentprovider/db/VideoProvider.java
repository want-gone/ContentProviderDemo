package com.artharyoung.contentprovider.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class VideoProvider extends ContentProvider {
    private static final String TAG = "VideoProvider";
    private static final UriMatcher matcher;
    private DBHelper helper;
    private SQLiteDatabase db;

    /**
     * com.your-company.VideoProvider 授权信息
     */
    private static final String AUTHORITY = "com.studyjams.mdvideo.VideoProvider";
    private static final int VIDEO_ALL = 0;
    private static final int VIDEO_ONE = 1;

    /**
     * MIME类型vnd.android.cursor.item/content-type
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + Utils.TABLE_VIDEO_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + Utils.TABLE_VIDEO_NAME;

    static {
        /**常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码**/
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        /**如果match方法匹配路径，返回匹配码为1**/
        matcher.addURI(AUTHORITY, Utils.TABLE_VIDEO_NAME, VIDEO_ALL);   //匹配记录集合
        /**如果match方法匹配路径，返回匹配码为2**/
        matcher.addURI(AUTHORITY, Utils.TABLE_VIDEO_NAME + "/#", VIDEO_ONE); //匹配单条记录
    }

    /**
     * 操作使用的uri
     */
    public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/" + Utils.TABLE_VIDEO_NAME);

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType: " + uri);
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                return CONTENT_TYPE;
            case VIDEO_ONE:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete: " + uri);
        db = helper.getWritableDatabase();
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                //doesn't need any code in my provider.
                break;
            case VIDEO_ONE:
                //删除某一条数据
                long _id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
        }
        return db.delete(Utils.TABLE_VIDEO_NAME, selection, selectionArgs);
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert: " + uri);
        if (matcher.match(uri) != VIDEO_ALL) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = helper.getWritableDatabase();
        return ContentUris.withAppendedId(uri, db.insert(Utils.TABLE_VIDEO_NAME, null, values));
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: " + uri);
        db = helper.getReadableDatabase();
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                //doesn't need any code in my provider.
                break;
            case VIDEO_ONE:
                long _id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return db.query(Utils.TABLE_VIDEO_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update: " + uri);
        db = helper.getWritableDatabase();
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                //doesn't need any code in my provider.
                break;
            case VIDEO_ONE:
                long _id = ContentUris.parseId(uri);
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return db.update(Utils.TABLE_VIDEO_NAME, values, selection, selectionArgs);
    }
}

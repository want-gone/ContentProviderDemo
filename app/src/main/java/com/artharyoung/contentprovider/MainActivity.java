package com.artharyoung.contentprovider;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.artharyoung.contentprovider.ProRecyclerView.RecyclerViewItemClickListener;
import com.artharyoung.contentprovider.db.Utils;
import com.artharyoung.contentprovider.db.VideoProvider;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements RecyclerViewItemClickListener.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "MainActivity";
    /**Loader的编号**/
    private static final int LOADER_NUM = 1;
    /**查询的唯一编号**/
    private static final int Query_NUM = 2;

    /**Loader管理器**/
    private LoaderManager mLoaderManager;

    private Context mContext = null;
    private LocalVideoCursorAdapter mLocalVideoCursorAdapter;

    /**异步数据库操作**/
    private SyncHandler mSyncHandler;

    private VideoObserver mVideoObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        init();
        initData();
    }

    private void init(){
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,true);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mLocalVideoCursorAdapter = new LocalVideoCursorAdapter(this);
        mRecyclerView.setAdapter(mLocalVideoCursorAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(mContext, this));
    }

    private void initData(){
        mLoaderManager = getSupportLoaderManager();
        mLoaderManager.initLoader(LOADER_NUM, null, this);
        mSyncHandler = new SyncHandler(getContentResolver());
        mVideoObserver = new VideoObserver(new MyHandler(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册数据库变化监听
        getContentResolver().registerContentObserver(VideoProvider.URI, true, mVideoObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消数据库变化监听
        getContentResolver().unregisterContentObserver(mVideoObserver);
    }

    public void insert(View v){
        Log.d(TAG, "insert: ");
        ContentValues values = new ContentValues();
        values.put(Utils.Video_title, "测试");
        values.put(Utils.Video_album, "111111");
        values.put(Utils.Video_artist, "222222");
        values.put(Utils.Video_displayName, "33333");
        values.put(Utils.Video_mimeType, "44444");
        values.put(Utils.Video_path, R.drawable.empty_photo);
        values.put(Utils.Video_size, 232);
        values.put(Utils.Video_duration, 23);
        values.put(Utils.Video_playDuration, 1213);
        values.put(Utils.Video_createdDate, Utils.getTime());

        /**
         * token:一个令牌，主要用来标识查询,保证唯一即可.需要跟onXXXComplete方法传入的一致。
         * （当然你也可以不一致，同样在数据库的操作结束后会调用对应的onXXXComplete方法 ）
         * cookie:你想传给onXXXComplete方法使用的一个对象。(没有的话传递null即可)
         * Uri :uri（进行查询的通用资源标志符）:
         * ContentValues: 数据存储对象
         */
        for(int i = 0;i<100;i++) {
            mSyncHandler.startInsert(Query_NUM, null, VideoProvider.URI, values);
        }
    }

    public void delete(View v){
        Log.d(TAG, "delete: ");

        /**
         * 删除_id为的记录
         * 这个id指的是数据库的自增id,那么在这里，若UI上的索引与id没有关联起来，
         * 那么以id来删除某个数据是没有意义的
         * 这一部分在adapter里用了一个listView来关联
         */
//        Uri delUri = ContentUris.withAppendedId(VideoProvider.URI, mLocalVideoCursorAdapter.getItemData(0).getId());
//        mSyncHandler.startDelete(Query_NUM,null,delUri, null, null);

        //删除所有记录
//        mSyncHandler.startDelete(Query_NUM,null,VideoProvider.URI,null,null);

        /**
         *  使用SQL语句删除
         *  这个删除中id是数据表的id,这个条件是不一定存在的
         *  可以通过adapter获取界面UI对应的索引，再获取数据表的id
         *  但获取的数据表的id并不一定是连续的（阿西吧）
         *  只能用for循环单个删除。
         *  所以在数据处理上，排序算法在实际的使用中还是很有用的，如果读出的数据是排序过连续的
         *  我就可以直接利用SQL的删除语法一次性删除了
         *  我要去好好复习一下了（这一条跟项目并没有什么关联）
         */

        mSyncHandler.startDelete(Query_NUM,null,VideoProvider.URI,"_id>10",null);
    }

    public void update(View v){
        Log.d(TAG, "update: ");
        ContentValues values = new ContentValues();
        values.put(Utils.Video_title, "测试");
        values.put(Utils.Video_album, "111111");
        values.put(Utils.Video_artist, "222222");
        values.put(Utils.Video_displayName, "33333");
        values.put(Utils.Video_mimeType, "44444");
        values.put(Utils.Video_path, R.drawable.empty_photo);
        values.put(Utils.Video_size, 1024*1024*10);
        values.put(Utils.Video_duration, 42423);
        values.put(Utils.Video_playDuration, 14213);
        values.put(Utils.Video_createdDate, Utils.getTime());

        /**将界面上第一个item更新，从adapter里获取id**/
        Uri updateUri = ContentUris.withAppendedId(VideoProvider.URI, mLocalVideoCursorAdapter.getItemData(0).getId());
        mSyncHandler.startUpdate(Query_NUM, null, updateUri, values, null, null);
    }

    public void query(View v) {
        Log.d(TAG, "query: ");
        /**
         * token:一个令牌，主要用来标识查询,保证唯一即可.需要跟onXXXComplete方法传入的一致。
         * （当然你也可以不一致，同样在数据库的操作结束后会调用对应的onXXXComplete方法 ）
         * cookie:你想传给onXXXComplete方法使用的一个对象。(没有的话传递null即可)
         * Uri :uri（进行查询的通用资源标志符）:
         * projection: 查询的列
         * selection:  限制条件
         * selectionArgs: 查询参数
         * orderBy: 排序条件
         */
        mSyncHandler.startQuery(Query_NUM, null, VideoProvider.URI, null, null, null, Utils.Video_createdDate);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(mContext,mLocalVideoCursorAdapter.getItemData(position).getCreatedDate() + "列表索引号：" + position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_NUM:
                return new CursorLoader(
                        mContext,
                        VideoProvider.URI,
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case LOADER_NUM:
                mLocalVideoCursorAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch(loader.getId()) {
            case LOADER_NUM:
                mLocalVideoCursorAdapter.swapCursor(data);

                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }

    }

    private static class SyncHandler extends AsyncQueryHandler {
        private  ContentResolver mContentResolver;
        public SyncHandler(ContentResolver cr) {
            super(cr);
            mContentResolver = cr;
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            Log.d(TAG, "onDeleteComplete: ");
            mContentResolver.notifyChange(VideoProvider.URI, null);
            /**
             * 在这里直接调用LoaderManager的重载也是可以有相同效果的
             * 但是LoaderManager只能在Activity和Fragment里实现
             * 而AsyncQueryHandler的使用并不限于这两者，
             * 所以更新数据的机制变了一下
             * 设置一个contentProvider的观察者（监听）
             * 然后在监听回调里让LoaderManager重新加载数据
             */
//            mLoaderManager.restartLoader(LOADER_NUM,null,MainActivity.this);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            Log.d(TAG, "onInsertComplete: ");
//            mLoaderManager.restartLoader(LOADER_NUM,null,MainActivity.this);
            mContentResolver.notifyChange(VideoProvider.URI, null);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            Log.d(TAG, "onQueryComplete: ");
//            mLoaderManager.restartLoader(LOADER_NUM,null,MainActivity.this);
            mContentResolver.notifyChange(VideoProvider.URI, null);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            super.onUpdateComplete(token, cookie, result);
            Log.d(TAG, "onUpdateComplete: ");
//            mLoaderManager.restartLoader(LOADER_NUM,null,MainActivity.this);
            mContentResolver.notifyChange(VideoProvider.URI, null);
        }

    }

    public class VideoObserver extends ContentObserver {
        public VideoObserver(Handler handler) {
            super(handler);
        }
        public void onChange(boolean selfChange) {
            //此处可以进行相应的业务处理
            mLoaderManager.restartLoader(LOADER_NUM,null,MainActivity.this);
        }
    }

    /**
     * 一个Activity中Handler该有的样子
     * 通过弱引用防止因持有activity的上下文导致可能的内存泄漏
     */
    private static class MyHandler extends Handler {
            private WeakReference<Activity> activityWeakReference;

            public MyHandler(Activity activity) {
                activityWeakReference = new WeakReference<Activity>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                Activity activity = activityWeakReference.get();
                if (activity != null) {
                    Log.d(TAG, "handleMessage: " + msg.what);
                }
            }
        }
}

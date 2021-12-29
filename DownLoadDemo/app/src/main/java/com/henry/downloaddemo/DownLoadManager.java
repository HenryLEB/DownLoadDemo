package com.henry.downloaddemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownLoadManager {
    private volatile static DownLoadManager manager;
    //    public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/";//文件下载保存路径
    public static String FILE_PATH = "";
    private DownLoadDbHelper helper;//数据库帮助类
    private SQLiteDatabase db;
    private Context mContext;
    private ProgressListener listener;//进度回调监听
    private Map<String, DownLoadFileInfo> map = new HashMap<>();//保存正在下载的任务信息

    private DownLoadManager(Context context, DownLoadDbHelper dbHelper, ProgressListener progressListener) {
        mContext = context;
        helper = dbHelper;
        db = helper.getReadableDatabase();
        listener = progressListener;
        FILE_PATH = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
    }

    static class SingleTon {
        private volatile static DownLoadManager instance;

        private static DownLoadManager getDownLoadManagerInstance(Context context, DownLoadDbHelper dbHelper, ProgressListener progressListener) {
            if (instance != null)
                return instance;
            else synchronized (DownLoadManager.class) {
                if (instance != null)
                    return instance;
                else
                    instance = new DownLoadManager(context, dbHelper, progressListener);
            }
            return instance;
        }
    }

    public static DownLoadManager getInstance(Context context, DownLoadDbHelper dbHelper, ProgressListener progressListener) {
        return SingleTon.getDownLoadManagerInstance(context, dbHelper, progressListener);
    }

    /**
     * 开始下载任务
     */
    public void start(String url) {
        db = helper.getReadableDatabase();
        DownLoadFileInfo info = helper.queryData(db, url);
        map.put(url, info);
        new DownLoadTask(map.get(url), helper, listener).start();
    }

    /**
     * 停止下载任务
     */
    public void stop(String url) {
        map.get(url).setStop(true);
    }

    /**
     * 重新下载任务
     */
    public void restart(String url) {
        stop(url);
        try {
            File file = new File(FILE_PATH, map.get(url).getFileName());
            if (file.exists())
                file.delete();
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db = helper.getWritableDatabase();
        helper.resetData(db, url);
        start(url);
    }

    /**
     * 获取当前任务状态
     */
    public boolean getCurrentState(String url) {
        return map.get(url).isDownLoading();
    }


    /**
     * 添加下载任务
     *
     * @param info 下载文件信息
     */
    public void addTask(DownLoadFileInfo info) {
        //判断数据库是否已经存在这条下载信息
        if (!helper.isExist(db, info)) {
            helper.insertData(db, info);
            map.put(info.getUrl(), info);
        } else {
            //从数据库获取最新的下载信息
            DownLoadFileInfo fileInfo = helper.queryData(db, info.getUrl());
            if (!map.containsKey(info.getUrl())) {
                map.put(info.getUrl(), fileInfo);
            }
        }
    }


}

package com.henry.downloaddemo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.mbms.FileInfo;

import androidx.annotation.Nullable;

import java.util.Locale;

public class DownLoadDbHelper extends SQLiteOpenHelper {
    public static String TABLE_NAME = "file";//表名


    public DownLoadDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table file(fileName varchar,url varchar,length integer,finished integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入一条下载信息
     */
    public void insertData(SQLiteDatabase db, DownLoadFileInfo info) {
        String sql = String.format(Locale.CHINA, "insert into '%s' values ('%s', '%s', %d, %d)",
                TABLE_NAME, info.getFileName(), info.getUrl(), info.getLength(), info.getFinished());
        db.execSQL(sql);
    }

    /**
     * 是否已经插入这条数据
     */
    public boolean isExist(SQLiteDatabase db, DownLoadFileInfo info) {
        String sql = String.format(Locale.CHINA, "select * from '%s' where url=?", TABLE_NAME);
        Cursor cursor = db.rawQuery(sql, new String[]{info.getUrl()});
        boolean exist = cursor.moveToNext();
        cursor.close();
        return exist;
    }


    /**
     * 查询已经存在的一条信息
     */
    @SuppressLint("Range")
    public DownLoadFileInfo queryData(SQLiteDatabase db, String url) {
        String sql = String.format(Locale.CHINA, "select * from '%s' where url=?", TABLE_NAME);
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        DownLoadFileInfo downLoadFileInfo = new DownLoadFileInfo();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                int length = cursor.getInt(cursor.getColumnIndex("length"));
                int finished = cursor.getInt(cursor.getColumnIndex("finished"));
                downLoadFileInfo.setFileName(fileName);
                downLoadFileInfo.setUrl(url);
                downLoadFileInfo.setLength(length);
                downLoadFileInfo.setFinished(finished);
            }
            cursor.close();
        }
        return downLoadFileInfo;
    }


    /**
     * 恢复一条下载信息
     */
    public void resetData(SQLiteDatabase db, String url) {
        String sql = String.format(Locale.CHINA, "update '%s' set length=0,finished=0 where url='%s'", TABLE_NAME, url);
        db.execSQL(sql);
    }


    /**
     * 更新下载信息
     */
    public void updateData(SQLiteDatabase db, DownLoadFileInfo info) {
        String sql = String.format(Locale.CHINA, "update '%s' set fileName='%s',url='%s',length=%d,finished=%d where url='%s'",
                TABLE_NAME, info.getFileName(), info.getUrl(), info.getLength(), info.getFinished(), info.getUrl());
        db.execSQL(sql);
    }

}

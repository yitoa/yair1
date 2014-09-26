
package com.ya.yair.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yteair.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        // CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 设备的mac地址存储到这个表中(在同一个局域网中不止一台设备)
        db.execSQL("CREATE TABLE IF NOT EXISTS sbmac" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, mac VARCHAR(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("ALTER TABLE sbmac ADD COLUMN other STRING");
    }

}

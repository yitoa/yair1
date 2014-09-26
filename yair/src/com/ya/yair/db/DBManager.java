
package com.ya.yair.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     * 
     * @param persons
     */
    public void add(String mac) {
        db.beginTransaction(); // 开始事务
        try {
            db.execSQL("INSERT INTO sbmac VALUES (null, ?)", new Object[] {
                mac
            });
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
        }
    }

    /**
     * update person's age
     * 
     * @param person
     */
    /*
     * public void updateAge(Person person) { ContentValues cv = new
     * ContentValues(); cv.put("age", person.age); db.update("person", cv,
     * "name = ?", new String[]{person.name}); }
     */

    /**
     * delete old person
     * 
     * @param person
     */
    // public void deleteOldPerson(Person person) {
    // db.delete("person", "age >= ?", new
    // String[]{String.valueOf(person.age)});
    // }

    /**
     * query all persons, return list
     * 
     * @return List<Person>
     */
    public List<String> query() {
        ArrayList<String> macs = new ArrayList<String>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            String mac = c.getString(c.getColumnIndex("mac"));
            macs.add(mac);
        }
        c.close();
        return macs;
    }

    /**
     * 查找数据库里面所有的mac设备
     * 
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM sbmac", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }

}

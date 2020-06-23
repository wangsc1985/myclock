package com.wang17.myclock.database.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "myclock.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库后，对数据库的操作
        try {
            db.execSQL("create table if not exists setting("
                    + "name TEXT PRIMARY KEY,"
                    + "value TEXT,"
                    + "level INT NOT NULL DEFAULT 100,"
                    + "updateTime LONG NOT NULL DEFAULT 1,"
                    + "syncTime LONG NOT NULL DEFAULT 1,"
                    + "status INT NOT NULL DEFAULT 1)");
            db.execSQL("create table if not exists markDay("
                    + "id TEXT PRIMARY KEY,"
                    + "dateTime LONG,"
                    + "item TEXT NOT NULL DEFAULT '',"
                    + "summary TEXT NOT NULL DEFAULT '',"
                    + "updateTime LONG NOT NULL DEFAULT 1,"
                    + "syncTime LONG NOT NULL DEFAULT 1,"
                    + "status INT NOT NULL DEFAULT 1)");
            db.execSQL("create table if not exists runLog("
                    + "id TEXT PRIMARY KEY,"
                    + "runTime LONG,"
                    + "tag TEXT,"
                    + "item TEXT,"
                    + "message TEXT,"
                    + "updateTime LONG NOT NULL DEFAULT 1,"
                    + "syncTime LONG NOT NULL DEFAULT 1,"
                    + "status INT NOT NULL DEFAULT 1)");

            db.execSQL("create table if not exists location("
                    + "Id TEXT PRIMARY KEY,"
                    + "UserId TEXT,"
                    + "LocationType INT,"
                    + "Longitude REAL,"
                    + "Latitude REAL,"
                    + "Accuracy REAL,"
                    + "Provider TEXT,"
                    + "Speed REAL,"
                    + "Bearing REAL,"
                    + "Satellites INT,"
                    + "Country TEXT,"
                    + "Province TEXT,"
                    + "City TEXT,"
                    + "CityCode TEXT,"
                    + "District TEXT,"
                    + "AdCode TEXT,"
                    + "Address TEXT,"
                    + "PoiName TEXT,"
                    + "Time LONG,"
                    + "Summary TEXT,"
                    + "updateTime LONG NOT NULL DEFAULT 1,"
                    + "syncTime LONG NOT NULL DEFAULT 1,"
                    + "status INT NOT NULL DEFAULT 1)");
            db.execSQL("create table if not exists dayItem("
                    + "id TEXT PRIMARY KEY,"
                    + "name TEXT,"
                    + "summary TEXT,"
                    + "targetInHour INT,"
                    + "updateTime LONG NOT NULL DEFAULT 1,"
                    + "syncTime LONG NOT NULL DEFAULT 1,"
                    + "status INT NOT NULL DEFAULT 1)");
            db.execSQL("insert into dayItem values('00000000-0000-0000-0000-000000000000','持戒记录','',48,1,1,1)");
            db.execSQL("create table if not exists bankToDo("
                    + "id TEXT PRIMARY KEY,"
                    + "dateTime LONG,"
                    + "bankName TEXT,"
                    + "cardNumber TEXT,"
                    + "money REAL,"
                    + "updateTime LONG NOT NULL DEFAULT 1,"
                    + "syncTime LONG NOT NULL DEFAULT 1,"
                    + "status INT NOT NULL DEFAULT 1)");
        } catch (SQLException e) {
            Log.e("wangsc", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更改数据库版本的操作
        try {
            switch (oldVersion) {
            }
        } catch (SQLException e) {
            Log.e("wangsc", e.getMessage());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 每次成功打开数据库后首先被执行
    }


}

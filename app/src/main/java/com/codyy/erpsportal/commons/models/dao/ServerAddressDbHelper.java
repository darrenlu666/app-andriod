package com.codyy.erpsportal.commons.models.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gujiajia on 2016/8/2.
 */
public class ServerAddressDbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "server_address";

    private final static int DB_VERSION = 1;

    private final static String CREATE_TABLE_SERVER_ADDRESS = "CREATE TABLE "
            + ServerAddressDao.TN_SERVER_ADDRESS + "("
            + ServerAddressDao.COLUMN_SERVER_ADDRESS + " TEXT, "
            + ServerAddressDao.COLUMN_CREATE_TIME + " LONG, "
            + ServerAddressDao.COLUMN_UPDATE_TIME + " LONG)";

    private static ServerAddressDbHelper sInstance;

    private ServerAddressDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized ServerAddressDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ServerAddressDbHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SERVER_ADDRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}

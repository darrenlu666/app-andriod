package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiajia on 2016/8/2.
 */
public class ServerAddressDao {

    /**
     * 表名
     */
    public final static String TN_SERVER_ADDRESS = "server_address";

    public final static String COLUMN_SERVER_ADDRESS = "serverAddress";

    public final static String COLUMN_CREATE_TIME = "createTime";

    public final static String COLUMN_UPDATE_TIME = "updateTime";

    public static void saveServerAddress(Context context, String serverAddress) {
        SQLiteDatabase db = ServerAddressDbHelper.getInstance(context).getReadableDatabase();
        boolean insert;
        Cursor cursor = db.query(TN_SERVER_ADDRESS
                ,new String[]{COLUMN_SERVER_ADDRESS, COLUMN_CREATE_TIME, COLUMN_UPDATE_TIME}
                ,COLUMN_SERVER_ADDRESS + "=?"
                ,new String[]{serverAddress}, null, null
                , COLUMN_UPDATE_TIME + " desc");
        insert = !cursor.moveToNext();
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UPDATE_TIME, System.currentTimeMillis());
        if (insert) {
            contentValues.put(COLUMN_SERVER_ADDRESS, serverAddress);
            contentValues.put(COLUMN_CREATE_TIME, System.currentTimeMillis());
            db.insert(TN_SERVER_ADDRESS, null, contentValues);
        } else {
            db.update(TN_SERVER_ADDRESS
                    , contentValues
                    , COLUMN_SERVER_ADDRESS + "=?"
                    , new String[]{serverAddress});
        }
        db.close();
    }

    public static List<String> findServerAddresses(Context context) {
        SQLiteDatabase db = ServerAddressDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("select serverAddress from server_address order by updateTime", null);
        if (cursor.getCount() == 0) return null;
        List<String> serverAddresses = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()) {
            serverAddresses.add(cursor.getString(0));
        }
        cursor.close();
        return serverAddresses;
    }
}

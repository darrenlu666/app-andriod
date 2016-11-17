package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoMyFriendAdapter;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-2-24.
 */
public class WeiBoAtDao {
    private DbHelper mDbHelper;

    public WeiBoAtDao(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    public void insertAtData(String userId, WeiBoSearchPeople searchPeople) {
        SQLiteDatabase databa = mDbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        long time = System.currentTimeMillis();
        contentValues.put("name", searchPeople.getRealName());
        contentValues.put("id", searchPeople.getBaseUserId());
        contentValues.put("userId", userId);
        contentValues.put("headpic", searchPeople.getHeadPic());
        contentValues.put("createTime", time);
        if (ishave(userId, searchPeople.getBaseUserId(), databa)) {
            databa.update(DbHelper.WEIBO_AT_HISTORY, contentValues, "id=? and userId=?", new String[]{searchPeople.getBaseUserId(), userId});
        } else {
            databa.insert(DbHelper.WEIBO_AT_HISTORY, null, contentValues);
        }
        databa.close();
    }

    public boolean ishave(String userId, String id, SQLiteDatabase databa) {
        Cursor c = databa.rawQuery("select * from " + DbHelper.WEIBO_AT_HISTORY + " where id=? and userId=?", new String[]{id, userId});
        return c.moveToFirst();
    }

    public ArrayList<WeiBoSearchPeople> getAtHistory(String userId) {
        SQLiteDatabase databa = mDbHelper.getReadableDatabase();
        Cursor c = databa.rawQuery("select * from " + DbHelper.WEIBO_AT_HISTORY + " where userId=? ORDER BY createTime DESC", new String[]{userId});
        ArrayList<WeiBoSearchPeople> atHistories = new ArrayList<>();
        while (c.moveToNext()) {
            WeiBoSearchPeople searchPeople = new WeiBoSearchPeople();
            searchPeople.setRealName(c.getString(c.getColumnIndex("name")));
            searchPeople.setBaseUserId(c.getString(c.getColumnIndex("id")));
            searchPeople.setHeadPic(c.getString(c.getColumnIndex("headpic")));
            searchPeople.setmHolderType(WeiBoMyFriendAdapter.TYPE_MY_FRIEND);
            if (atHistories.size() < 5) {
                atHistories.add(searchPeople);
            } else {
                deleteHistory(userId, c.getString(c.getColumnIndex("id")), databa);
            }
        }
        if (atHistories.size() > 0) {
            WeiBoSearchPeople weiBoSearchPeople = new WeiBoSearchPeople();
            weiBoSearchPeople.setmHolderType(WeiBoMyFriendAdapter.TYPE_DIVIDE_RECENT);
            atHistories.add(0, weiBoSearchPeople);
        }
        return atHistories;
    }

    /**
     * 删除搜索数据
     *
     * @param str
     */
    public void deleteHistory(String userId, String str, SQLiteDatabase databa) {
        databa.delete(DbHelper.WEIBO_AT_HISTORY, "id = ? and userId=?", new String[]{str, userId});
    }
}

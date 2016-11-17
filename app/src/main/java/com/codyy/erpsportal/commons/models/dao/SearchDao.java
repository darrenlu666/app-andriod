package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.entities.SearchBase;
import com.codyy.erpsportal.commons.models.entities.SearchHistory;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/28.
 */
public class SearchDao {
    private DbHelper mDbHelper;

    public SearchDao(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    /**
     * 获取搜索历史
     *
     * @return
     */
    public ArrayList<SearchBase> getSearchHistory(String id) {
        ArrayList<SearchBase> searchHistories = new ArrayList<>();
        if (TextUtils.isEmpty(id)) {
            return searchHistories;
        }
        SQLiteDatabase databa = mDbHelper.getReadableDatabase();
        Cursor c = databa.rawQuery("select * from " + DbHelper.SEARCH_HISTORY_TABLE + " where id = ? ORDER BY createTime DESC", new String[]{id});
        while (c.moveToNext()) {
            if (searchHistories.size() >= 11) {
                break;
            }
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setmType(SearchBase.HISTORY_CONT);
            searchHistory.setCont(c.getString(c.getColumnIndex("searchcont")));
            searchHistory.setType(c.getString(c.getColumnIndex("searchtype")));
            searchHistories.add(searchHistory);
        }
        SearchBase searchBase = new SearchBase();
        searchBase.setmType(SearchBase.HISTORY_TITLE);
        if (searchHistories.size() > 0) {
            searchBase.setmTitle("搜索历史");
        } else {
            searchBase.setmTitle("没有搜索记录");
        }
        searchHistories.add(0, searchBase);
        if (searchHistories.size() > 1) {
            SearchBase delete = new SearchBase();
            delete.setmTitle("清空搜索记录");
            delete.setmType(SearchBase.HISTORY_DELETE);
            searchHistories.add(delete);
        }
        c.close();
        databa.close();
        return searchHistories;
    }

    public boolean ishave(String str, SQLiteDatabase databa, String id) {
        boolean flag = false;
        Cursor c = databa.rawQuery("select * from " + DbHelper.SEARCH_HISTORY_TABLE + " where searchcont=? and id=?", new String[]{str, id});
        flag = c.moveToFirst();
        c.close();
        return flag;
    }

    /**
     * 插入搜索历史
     *
     * @param cont
     * @param type
     */
    public void writeHistory(String cont, String type, String id) {
        SQLiteDatabase databa = mDbHelper.getWritableDatabase();
        if (!ishave(cont, databa, id)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("searchcont", cont);
            contentValues.put("searchtype", type);
            contentValues.put("id", id);
            long time = System.currentTimeMillis();
            contentValues.put("createTime", time);
            databa.insert(DbHelper.SEARCH_HISTORY_TABLE, null, contentValues);
        } else {
            ContentValues contentValues = new ContentValues();
            long time = System.currentTimeMillis();
            contentValues.put("createTime", time);
            contentValues.put("searchtype", type);
            databa.update(DbHelper.SEARCH_HISTORY_TABLE, contentValues, "searchcont = ? and id = ?", new String[]{cont, id});
        }
        databa.close();
    }

    /**
     * 删除搜索数据
     *
     * @param str
     */
    public void deleteHistory(String str, String id) {
        SQLiteDatabase databa = mDbHelper.getWritableDatabase();
        if (str != null) {
            databa.delete(DbHelper.SEARCH_HISTORY_TABLE, "searchcont = ? and id = ?", new String[]{str, id});
        } else {
            databa.delete(DbHelper.SEARCH_HISTORY_TABLE, "id = ?", new String[]{id});
        }
        databa.close();
    }
}

package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.models.entities.CacheItem;
import com.codyy.erpsportal.resource.models.entities.Audio;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存数据入口
 * Created by caixingming on 2015/5/19.
 */
public class CacheDao {

    public static final String TABLE = "cache";

    public static final String ID = "id";//资源id 唯一（objectId)
    public static final String USERNAME = "userName";
    public static final String NAME = "name";
    public static final String THUMB_PATH = "thumbPath";
    public static final String SIZE = "size";
    public static final String CREATE_TIME = "createTime";
    public static final String DURATION = "duration";//时长
    public static final String BASE_USER_ID = "baseUserId";//用户基本id{#用于区分用户信息}
    /**
     *  增加下载状态    0：待下载 1：暂停
     */
    public static  final String STATE = "state";//下载状态  0：待下载 1：暂停
    public static  final String PROGRESS = "progress";//下载进度 对暂停之后的有用
    public static  final String TYPE = "type";//资源类型：0：resource 1:evaluation评课议课 2：视频会议
    public static  final String URL = "url";//资源下载地址

    public static final String SUFFIX = "suffix";

    public static final String CREATE_TABLE ="CREATE TABLE " + TABLE + " ("
                            + ID +" TEXT ,"
                            + USERNAME +" TEXT,"
                            + NAME +" TEXT,"
                            + THUMB_PATH +" TEXT,"
                            + SIZE+" LONG,"
                            + CREATE_TIME +" TEXT,"
                            + DURATION +" Integer,"
                            + STATE +" TEXT, "
                            + PROGRESS +" TEXT, "
                            + URL +" TEXT, "
                            + BASE_USER_ID+" TEXT, "
                            + TYPE +" TEXT, "
                            + SUFFIX + " TEXT "
                            +  ");";

    private DbHelper mDbHelper;

    public CacheDao(Context c){
        mDbHelper = DbHelper.getInstance(c);
    }

    /**
     * 向表中添加一条数据
     * download_state 默认0:待下载
     * @return
     */
    public void insert(CacheItem cacheItem) {
        // 这个 key如果师是否已经存在了，不存在就强势插入
        boolean isExist = isExist(cacheItem.getId() , cacheItem.getBaseUserId());
        if (!isExist) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues newValues = new ContentValues();
            newValues.put(ID, cacheItem.getId());
            newValues.put(NAME, cacheItem.getName());
            newValues.put(THUMB_PATH, cacheItem.getThumbPath());
            newValues.put(CREATE_TIME, LocalDateTime.now().toString());
            newValues.put(SIZE, cacheItem.getSize());
            newValues.put(STATE, cacheItem.getState());
            newValues.put(TYPE, cacheItem.getType());
            newValues.put(URL, cacheItem.getDownloadUrl());
            newValues.put(BASE_USER_ID, cacheItem.getBaseUserId());
            newValues.put(SUFFIX, cacheItem.getSuffix());
            db.insert(TABLE, null, newValues);
            db.close();
        }
    }

    /**
     * 是否存在某个用户下载的文件
     * @param resId
     * @param baseUserId
     * @return
     */
    public boolean isExist(String resId ,String baseUserId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "select * from " + TABLE
                + " where baseUserId='" + baseUserId
                + "' and id='" + resId + "'" ;
        Cursor cursor = db.rawQuery(sql, null);

        boolean isExist = false;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        db.close();
        return isExist;
    }

    /**
     * 下载服务专用
     * 更新缓存的状态
     * @param cacheItem
     */
    public void update(CacheItem cacheItem){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(ID, cacheItem.getId());
        newValues.put(NAME, cacheItem.getName());
        newValues.put(THUMB_PATH, cacheItem.getThumbPath());
        newValues.put(SIZE, cacheItem.getSize());
        newValues.put(CREATE_TIME, cacheItem.getCreateTime());
        newValues.put(STATE, cacheItem.getState());
        newValues.put(PROGRESS,cacheItem.getProgress());
        newValues.put(URL, cacheItem.getDownloadUrl());
        newValues.put(BASE_USER_ID, cacheItem.getBaseUserId());
        newValues.put(SUFFIX, cacheItem.getSuffix());
        db.update(TABLE,newValues, "id=? AND baseUserId=?",
                new String[]{cacheItem.getId(), cacheItem.getBaseUserId()});
        db.close();
    }

    public void updateState(CacheItem cacheItem) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(STATE, cacheItem.getState());
        db.update(TABLE,newValues, "id=? AND baseUserId=?",
                new String[]{cacheItem.getId(), cacheItem.getBaseUserId()});
        db.close();
    }

    /**
     * 下载服务专用
     * 更新缓存的状态
     * @param cacheItem
     */
    public void updateSize(CacheItem cacheItem){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(ID, cacheItem.getId());
        newValues.put(SIZE, cacheItem.getSize());
        db.update(TABLE,newValues, ID +" = '"+cacheItem.getId()+"'",null);
        db.close();
    }

    /**
     * 下载服务专用
     * 更新缓存的size
     */
    public void updateResourceSize(int size, String resId, String userId){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(ID, resId);
        newValues.put(SIZE, (long)size);
        db.update(TABLE,newValues, "id=? AND baseUserId=?",
                new String[]{resId, userId});
        db.close();
    }

    /**
     * 取出数据库的所有数据
     *
     * @return
     */
    public List<CacheItem> getAllData(String baseUserId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql;
        Cursor cursor;
        List<CacheItem> cacheItems = new ArrayList<>();
        sql = "select * from " + TABLE +" where "+BASE_USER_ID+" = ? ORDER BY "
                + CREATE_TIME + " DESC" ;
        cursor = db.rawQuery(sql, new String[]{baseUserId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CacheItem cacheItem = mapToCacheItem(cursor);
                cacheItems.add(cacheItem);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        db.close();
        return cacheItems;
    }

    /**
     * 取出数据库的所有数据
     *
     * @return
     */
    public List<CacheItem> getDownloadingData(String baseUserId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql;
        Cursor cursor;
        List<CacheItem> cacheItems = new ArrayList<>();
        sql = "SELECT * FROM " + TABLE +" WHERE "+BASE_USER_ID+" = ? AND state = 0 ORDER BY "
                + CREATE_TIME + " DESC" ;
        cursor = db.rawQuery(sql, new String[]{baseUserId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CacheItem cacheItem = mapToCacheItem(cursor);
                cacheItems.add(cacheItem);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        db.close();
        return cacheItems;
    }

    /**
     * 取出数据库的所有数据
     *
     * @return
     */
    public List<Audio> findAudioCaches(String baseUserId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        List<Audio> audios = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE +" WHERE "+BASE_USER_ID+" = ? AND "
                + TYPE + " = ? ORDER BY " + CREATE_TIME + " DESC" ;
        Cursor cursor = db.rawQuery(sql, new String[]{baseUserId, CacheItem.DOWNLOAD_TYPE_AUDIO});
        while (cursor.moveToNext()) {
            Audio audio = new Audio();
            audio.setId(cursor.getString((cursor.getColumnIndex(ID))));
            audio.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            String suffix = cursor.getString(cursor.getColumnIndex(SUFFIX));
            audio.setStreamUrl("file://" + FileDownloadService.getCachedFile(baseUserId, audio.getId() + suffix));
            audios.add( audio);
        }
        cursor.close();
        db.close();
        return audios;
    }

    @NonNull
    private CacheItem mapToCacheItem(Cursor cursor) {
        CacheItem cacheItem = new CacheItem();
        cacheItem.setId(cursor.getString(cursor.getColumnIndex(ID)));
        cacheItem.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        cacheItem.setThumbPath(cursor.getString(cursor.getColumnIndex(THUMB_PATH)));
        cacheItem.setCreateTime(cursor.getString(cursor.getColumnIndex(CREATE_TIME)));
        String state = cursor.getString(cursor.getColumnIndex(STATE));
        cacheItem.setState(state);
        cacheItem.setProgress(cursor.getInt(cursor.getColumnIndex(PROGRESS)));
        cacheItem.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        cacheItem.setSize(cursor.getLong(cursor.getColumnIndex(SIZE)));
        cacheItem.setDownloadUrl(cursor.getString(cursor.getColumnIndex(URL)));
        cacheItem.setBaseUserId(cursor.getString(cursor.getColumnIndex(BASE_USER_ID)));
        cacheItem.setSuffix(cursor.getString(cursor.getColumnIndex(SUFFIX)));
        return cacheItem;
    }

    /**
     * 删除对应resId的资源
     * @param resId
     */
    public boolean deleteOneData(String resId ,String baseUserId){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int result = db.delete(TABLE, ID +" = ? AND "+BASE_USER_ID +" = ? ",new String[]{resId,baseUserId});
        db.close();
        if(result>0)
            return  true;
        else
            return false;
    }

    public boolean batchDelete(List<String> resIds, String baseUserId) {
        if (resIds == null || resIds.size() == 0) return false;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        StringBuilder whereClauseSb = new StringBuilder();
        whereClauseSb.append(BASE_USER_ID).append(" = '").append(baseUserId).append("' AND ")
                .append(ID).append(" IN (");
        for (String id: resIds) {
            whereClauseSb.append('\'').append(id).append("\',");
        }
        whereClauseSb.replace(whereClauseSb.length()-1, whereClauseSb.length(), ")");
//        StringBuilder sb = new StringBuilder();
//        for (String id: resIds) {
//            sb.append('\'').append(id).append("\',");
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        int result = db.delete(TABLE, BASE_USER_ID +" = ? AND " + ID + " IN (?)",new String[]{baseUserId, sb.toString()});
        int result = db.delete(TABLE, whereClauseSb.toString(), null);
        db.close();
        return result > 0;
    }

    /**
     * 删除所有的数据
     */
    public void deleteAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }
}

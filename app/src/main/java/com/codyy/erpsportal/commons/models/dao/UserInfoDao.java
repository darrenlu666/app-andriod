package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * Created by gujiajia on 2016/7/30.
 */
public class UserInfoDao {

    public synchronized static UserInfo find(Context context) {
        SQLiteDatabase database = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DbHelper.USER_TABLE_NAME, null);
        UserInfo userInfo = new UserInfo();
        try {
            if (cursor.moveToNext()) {
                userInfo.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                userInfo.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                userInfo.setBaseUserId(cursor.getString(cursor.getColumnIndex("baseUserId")));
                userInfo.setPosition(cursor.getString(cursor.getColumnIndex("position")));
                userInfo.setContactPhone(cursor.getString(cursor.getColumnIndex("contactPhone")));
                userInfo.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                userInfo.setBaseAreaId(cursor.getString(cursor.getColumnIndex("baseAreaId")));
                userInfo.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
                userInfo.setAreaCode(cursor.getString(cursor.getColumnIndex("areaCode")));
                userInfo.setIsAdmin(cursor.getInt(cursor.getColumnIndex("isAdmin")) == 1);
                userInfo.setRealName(cursor.getString(cursor.getColumnIndex("realName")));
                userInfo.setSchoolId(cursor.getString(cursor.getColumnIndex("schoolId")));
                userInfo.setSchoolName(cursor.getString(cursor.getColumnIndex("schoolName")));
                userInfo.setHeadPic(cursor.getString(cursor.getColumnIndex("headPic")));
                userInfo.setUserType(cursor.getString(cursor.getColumnIndex("userType")));
                userInfo.setSubjectNames(cursor.getString(cursor.getColumnIndex("subjectNames")));
                userInfo.setClassLevelNames(cursor.getString(cursor.getColumnIndex("classLevelNames")));
                userInfo.setEvaFlag(cursor.getString(cursor.getColumnIndex("evaFlag")));
                userInfo.setPermGrant(cursor.getString(cursor.getColumnIndex("permGrant")));
                userInfo.setVideoConferenceFlag(cursor.getString(cursor.getColumnIndex("videoConferenceFlag")));
                userInfo.setGroupPreparationFlag(cursor.getString(cursor.getColumnIndex("groupPreparationFlag")));
                userInfo.setInteractiveListenFlag(cursor.getString(cursor.getColumnIndex("interactiveListenFlag")));
                userInfo.setNetTeachingFlag(cursor.getString(cursor.getColumnIndex("netTeachingFlag")));
                userInfo.setClasslevelName(cursor.getString(cursor.getColumnIndex("classlevelName")));
                userInfo.setBaseClassId(cursor.getString(cursor.getColumnIndex("baseClassId")));
                userInfo.setBaseClassName(cursor.getString(cursor.getColumnIndex("baseClassName")));
                userInfo.setValidateCode(cursor.getString(cursor.getColumnIndex("validateCode")));
                userInfo.setServerAddress(cursor.getString(cursor.getColumnIndex("serverAddress")));
                userInfo.setParentId(cursor.getString(cursor.getColumnIndex("parentId")));
                userInfo.setGroupCategoryConfig(cursor.getString(cursor.getColumnIndex("groupCategoryConfig")));
            } else {
                return null;
            }
        } finally {
            cursor.close();
            database.close();
        }
        return userInfo;
    }

    public synchronized static void save(Context context, UserInfo userInfo) {
        SQLiteDatabase database = DbHelper.getInstance(context).getWritableDatabase();
        boolean needUpdate = false;
        Cursor cursor = database.rawQuery("select * from " + DbHelper.USER_TABLE_NAME, null);
        if (cursor.getCount() > 0) {
            needUpdate = true;
        }
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userName", userInfo.getUserName());
        contentValues.put("baseUserId", userInfo.getBaseUserId());
        contentValues.put("uuid", userInfo.getUuid());
        contentValues.put("contactPhone", userInfo.getContactPhone());
        contentValues.put("position", userInfo.getPosition());
        contentValues.put("userType", userInfo.getUserType());
        contentValues.put("remark", userInfo.getRemark());
        contentValues.put("baseAreaId", userInfo.getBaseAreaId());
        contentValues.put("areaName", userInfo.getAreaName());
        contentValues.put("areaCode", userInfo.getAreaCode());
        contentValues.put("levelName", userInfo.getLevelName());
        contentValues.put("isAdmin", userInfo.isAdmin() ? 1 : 0);
        contentValues.put("realName", userInfo.getRealName());
        contentValues.put("schoolId", userInfo.getSchoolId());
        contentValues.put("schoolName", userInfo.getSchoolName());
        contentValues.put("headPic", userInfo.getHeadPic());
        contentValues.put("subjectNames", userInfo.getSubjectNames());
        contentValues.put("classLevelNames", userInfo.getClassLevelNames());
        contentValues.put("evaFlag", userInfo.getEvaFlag());
        contentValues.put("permGrant", userInfo.getPermGrant());
        contentValues.put("classlevelName", userInfo.getClasslevelName());
        contentValues.put("baseClassId", userInfo.getBaseClassId());
        contentValues.put("baseClassName", userInfo.getBaseClassName());
        contentValues.put("interactiveListenFlag", userInfo.getInteractiveListenFlag());
        contentValues.put("groupPreparationFlag", userInfo.getGroupPreparationFlag());
        contentValues.put("videoConferenceFlag", userInfo.getVideoConferenceFlag());
        contentValues.put("netTeachingFlag", userInfo.getNetTeachingFlag());
        contentValues.put("validateCode", userInfo.getValidateCode());
        contentValues.put("serverAddress", userInfo.getServerAddress());
        contentValues.put("parentId" , userInfo.getParentId());
        contentValues.put("groupCategoryConfig" , userInfo.getGroupCategoryConfig());
        if (needUpdate) {
            database.update(DbHelper.USER_TABLE_NAME, contentValues, null, null);
        } else {
            database.insert(DbHelper.USER_TABLE_NAME, null, contentValues);
        }
        database.close();
    }

    public synchronized static int delete(Context context) {
        SQLiteDatabase database = DbHelper.getInstance(context).getWritableDatabase();
        int result = database.delete(DbHelper.USER_TABLE_NAME, null, null);
        database.close();
        return result;
    }
}

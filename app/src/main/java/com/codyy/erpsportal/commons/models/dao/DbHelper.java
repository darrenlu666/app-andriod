package com.codyy.erpsportal.commons.models.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gujiajia on 2015/4/11.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 11;

    private static final String DATABASE_NAME = "database_classroom";

    public static final String SEARCH_HISTORY_TABLE = "search_history";
    public static final String WEIBO_AT_HISTORY = "weibo_at_history";
    public static final String USER_TABLE_NAME = "user";
    private static final String USER_TABLE_CREATE =
            "CREATE TABLE " + USER_TABLE_NAME + " (" +
                    "userName TEXT," +
                    "baseUserId TEXT," +
                    "uuid TEXT," +
                    "contactPhone TEXT," +
                    "position TEXT," +
                    "userType TEXT," +
                    "remark TEXT," +
                    "baseAreaId TEXT," +
                    "areaName TEXT," +
                    "areaCode TEXT," +
                    "levelName TEXT," +
                    "isAdmin INTEGER," +
                    "realName TEXT," +
                    "schoolId TEXT," +
                    "schoolName TEXT," +
                    "headPic TEXT," +
                    "subjectNames TEXT," +
                    "classLevelNames TEXT," +
                    "baseClassId TEXT," +
                    "classlevelName TEXT," +
                    "baseClassName TEXT," +
                    "evaFlag TEXT," +
                    "permGrant TEXT," +
                    "videoConferenceFlag TEXT," +
                    "groupPreparationFlag TEXT," +
                    "interactiveListenFlag TEXT," +
                    "netTeachingFlag TEXT," +
                    "validateCode TEXT," +
                    "serverAddress TEXT," +
                    "parentId TEXT," +
                    "groupCategoryConfig TEXT);";
    /**
     * 搜索历史表
     */
    private static final String SEARCH_HISTORY = "CREATE TABLE " + SEARCH_HISTORY_TABLE +
            "(" +
            "id text," +
            "searchcont text," +
            "searchtype text," +
            "createTime long" +
            ");";

    private static final String CREATE_WEIBO_AT_HISTORY = "CREATE TABLE " + WEIBO_AT_HISTORY +
            "(" +
            "name text," +
            "id text," +
            "userId text," +
            "headpic text," +
            "createTime long" +
            ");";

    private static DbHelper sInstance;

    public static synchronized DbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(SEARCH_HISTORY);
        db.execSQL(CREATE_WEIBO_AT_HISTORY);
        db.execSQL(CacheDao.CREATE_TABLE);
        db.execSQL(TaskAnswerDao.TASK_ITEM_ANSWER_CREATE);
        db.execSQL(TaskAnswerDao.TASK_ITEM_PIC_CREATE);
        db.execSQL(TaskReadDao.TASK_ITEM_READ_CREATE);
        db.execSQL(TaskAnswerDao.TASK_START_LOG_TABLE_CREATE);
        db.execSQL(TaskReadDao.TASK_ITEM_READ_AUDIO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        boolean userTableAltered = false;
        if (oldVersion <= 1) {
//            String sql = "alter table " + USER_TABLE_NAME + " add column baseClassId TEXT,classlevelName TEXT,baseClassName TEXT;";
//            db.execSQL(sql);
            db.execSQL("alter table " + USER_TABLE_NAME + " add column baseClassId TEXT;");
            db.execSQL("alter table " + USER_TABLE_NAME + " add column classlevelName TEXT;");
            db.execSQL("alter table " + USER_TABLE_NAME + " add column baseClassName TEXT;");
            userTableAltered = true;
        }
        if (oldVersion <= 2) {
            db.execSQL("alter table " + SEARCH_HISTORY_TABLE + " add column createTime long;");
            db.execSQL("delete  from " + SEARCH_HISTORY_TABLE);
        }

        if (oldVersion <= 3) {
            db.execSQL("alter table " + USER_TABLE_NAME + " add column validateCode TEXT;");
            userTableAltered = true;
        }

        if (oldVersion <= 4) {
            db.execSQL("alter table " + USER_TABLE_NAME + " add column serverAddress TEXT;");
            userTableAltered = true;
        }

        if (oldVersion <= 5) {
            db.execSQL(CREATE_WEIBO_AT_HISTORY);
        }
        if (oldVersion == 6 && newVersion >= 7) {
            db.execSQL("alter table " + WEIBO_AT_HISTORY + " add column headpic TEXT;");
            db.execSQL("DELETE FROM " + WEIBO_AT_HISTORY);
        }
        if (oldVersion == 7 && newVersion >= 8) {
            db.execSQL("alter table " + WEIBO_AT_HISTORY + " add column userId TEXT;");
            db.execSQL("DELETE FROM " + WEIBO_AT_HISTORY);
        }
        if (oldVersion == 8 && newVersion >= 9) {
            db.execSQL("alter table " + TaskAnswerDao.TASK_ITEM_ANSWER_TABLE + " add column resourceLocalPath TEXT;");
            db.execSQL("alter table " + TaskAnswerDao.TASK_ITEM_ANSWER_TABLE + " add column resourceType TEXT;");
        }

        if (oldVersion < 9) {
            db.execSQL("alter table " + TaskAnswerDao.TASK_ITEM_PIC_TABLE + " add column resourceLocalPath TEXT;");
            db.execSQL("alter table " + CacheDao.TABLE + " add column " + CacheDao.BASE_USER_ID + " TEXT;");
        }

        if (oldVersion < 10) {
            db.execSQL("alter table " + USER_TABLE_NAME + " add column parentId TEXT;");
            userTableAltered = true;
        }

        if (oldVersion < 11) {
            db.execSQL("alter table " + USER_TABLE_NAME + " add column netTeachingFlag TEXT;");
            userTableAltered = true;
        }

        if (userTableAltered) {
            db.execSQL("delete from " + USER_TABLE_NAME);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= 2) {
            db.execSQL("DROP TABLE " + USER_TABLE_NAME);
            db.execSQL("DROP TABLE " + SEARCH_HISTORY_TABLE);
            db.execSQL(USER_TABLE_CREATE);
            db.execSQL(SEARCH_HISTORY);
        }
    }
}

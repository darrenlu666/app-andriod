package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储习题批阅信息
 * Created by ldh on 2016/1/13.
 */
public class TaskReadDao {

    public static final String TASK_ITEM_READ_TABLE = "task_item_read";
    public static final String TASK_ITEM_READ_AUDIO_TABLE = "task_item_read_audio";
    public static final String TASK_ITEM_TYPE_EXAM = "typeExam";//测试模块
    public static final String TASK_ITEM_TYPE_WORK = "typeWork";//作业模块
    private static final String ID = "id";
    private static final String TASK_ITEM_TYPE = "taskItemType";
    private static final String STUDENT_ID = "studentId";
    private static final String TASK_ID = "taskId";
    private static final String TASK_ITEM_ID = "taskItemId";
    private static final String TASK_ITEM_READ_SCORE = "taskItemReadScore";
    private static final String TASK_ITEM_READ_COMMENT = "taskItemReadComment";
    private static final String TASK_ITEM_READ_TIME = "taskItemReadTime";

    private static final String TASK_ITEM_READ_AUDIO_URL = "taskItemReadAudioUrl";
    private static final String TASK_ITEM_READ_AUDIO_LENGTH = "taskItemReadAudioLength";
    private static final String TASK_ITEM_READ_AUDIO_NAME = "taskItemReadAudioName";
    private static final String TASK_ITEM_READ_AUDIO_MESSAGE_ID = "taskItemReadAudioMessageId";
    private static final String TASK_ITEM_READ_IS_TOTAL_COMMENT = "IsTotalComment";

    private static TaskReadDao mTaskReadDao = null;
    private DbHelper mDbHelper = null;
    private SQLiteDatabase mDatabase = null;

    public static final String TASK_ITEM_READ_CREATE = "CREATE TABLE " + TASK_ITEM_READ_TABLE + "(" +
            TASK_ITEM_TYPE + " TEXT," +
            STUDENT_ID + " TEXT," +
            TASK_ID + " TEXT," +
            TASK_ITEM_ID + " TEXT UNIQUE," +
            TASK_ITEM_READ_SCORE + " TEXT," +
            TASK_ITEM_READ_COMMENT + " TEXT," +
            TASK_ITEM_READ_TIME + " INTEGER, " +
            "PRIMARY KEY (" + STUDENT_ID + "," + TASK_ITEM_ID + ") )";

    public static final String TASK_ITEM_READ_AUDIO_CREATE = "CREATE TABLE " + TASK_ITEM_READ_AUDIO_TABLE + "(" +
            STUDENT_ID + " TEXT," +
            TASK_ID + " TEXT," +
            TASK_ITEM_ID + " TEXT," +
            TASK_ITEM_READ_AUDIO_URL + " TEXT UNIQUE," +
            TASK_ITEM_READ_AUDIO_LENGTH + " TEXT," +
            TASK_ITEM_READ_AUDIO_NAME + " TEXT, " +
            TASK_ITEM_READ_AUDIO_MESSAGE_ID + " TEXT, " +
            TASK_ITEM_READ_IS_TOTAL_COMMENT + " INTEGER" + ")";

    public TaskReadDao(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    public static TaskReadDao newInstance(Context context) {
        if (mTaskReadDao == null) {
            synchronized (TaskReadDao.class) {
                mTaskReadDao = new TaskReadDao(context);
            }
        }
        return mTaskReadDao;
    }

    /**
     * 插入一条批阅记录
     *
     * @param type
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param score
     * @param comment
     */
    public void insert(String type, String studentId, String taskId, String taskItemId, String score, String comment) {
        mDatabase = mDbHelper.getWritableDatabase();
        String insertSql = "replace into " + TASK_ITEM_READ_TABLE + "(" +
                TASK_ITEM_TYPE + "," +
                STUDENT_ID + "," +
                TASK_ID + "," +
                TASK_ITEM_ID + "," +
                TASK_ITEM_READ_SCORE + "," +
                TASK_ITEM_READ_COMMENT + ") values ('" +
                type + "','" +
                studentId + "','" +
                taskId + "','" +
                taskItemId + "','" +
                score + "','" +
                comment + "')";
        mDatabase.execSQL(insertSql);
        mDatabase.close();
    }

    /**
     * 上传成功后，删除某学生某套作业/测试的本地批阅记录
     *
     * @param studentId
     * @param taskId
     */
    public void delete(String studentId, String taskId) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.delete(TASK_ITEM_READ_TABLE, STUDENT_ID + " = ? and " + TASK_ID + " = ?", new String[]{studentId, taskId});
        mDatabase.close();
    }

    /**
     * 修改某学生某道题的批阅记录
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param score
     * @param comment
     * @param time
     */
    public void update(String studentId, String taskId, String taskItemId, String score, String comment, int time) {
        mDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_ITEM_READ_SCORE, score);
        contentValues.put(TASK_ITEM_READ_COMMENT, comment);
        contentValues.put(TASK_ITEM_READ_TIME, time);
        mDatabase.update(TASK_ITEM_READ_TABLE, contentValues, STUDENT_ID +
                " = ? & " + "TASK_ID = ? & " + "TASK_ITEM_ID = ?", new String[]{studentId, taskId, taskItemId});
        mDatabase.close();
    }

    /**
     * 获取某学生某套作业的批阅记录
     *
     * @param studentId
     * @param taskId
     * @return
     */
    public List<TaskItemReadInfo> query(String studentId, String taskId) {
        List<TaskItemReadInfo> list = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        String sqlStr = "select * from " + TASK_ITEM_READ_TABLE + " where " + STUDENT_ID + " = '" + studentId + "' and " + TASK_ID + " = '" + taskId + "'";
        Cursor c = mDatabase.rawQuery(sqlStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                TaskItemReadInfo taskItemReadInfo = new TaskItemReadInfo();
                taskItemReadInfo.setTaskItemType(c.getString(c.getColumnIndex(TASK_ITEM_TYPE)));
                taskItemReadInfo.setStudentId(c.getString(c.getColumnIndex(STUDENT_ID)));
                taskItemReadInfo.setTaskId(c.getString(c.getColumnIndex(TASK_ID)));
                taskItemReadInfo.setTaskItemId(c.getString(c.getColumnIndex(TASK_ITEM_ID)));
                taskItemReadInfo.setTaskItemReadScore(c.getString(c.getColumnIndex(TASK_ITEM_READ_SCORE)));
                taskItemReadInfo.setTaskItemReadComment(c.getString(c.getColumnIndex(TASK_ITEM_READ_COMMENT)));
                taskItemReadInfo.setTaskItemReadTime(c.getInt(c.getColumnIndex(TASK_ITEM_READ_TIME)));
                list.add(taskItemReadInfo);
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return list;
    }

    /**
     * 测试-老师-按学生批阅-只有问答题、计算题需要打分判断
     *
     * @param studentId
     * @param taskId
     * @return
     */
    public List<TaskItemReadInfo> queryExam(String studentId, String taskId) {
        List<TaskItemReadInfo> list = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        String sqlStr = "select * from " + TASK_ITEM_READ_TABLE + " where " + STUDENT_ID + " = '" + studentId + "' and " + TASK_ID + " = '" + taskId + "'";// + "' and " + TASK_ITEM_TYPE + "='ASK_ANSWER' or " + TASK_ITEM_TYPE + "='COMPUTING'"
        Cursor c = mDatabase.rawQuery(sqlStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                TaskItemReadInfo taskItemReadInfo = new TaskItemReadInfo();
                taskItemReadInfo.setTaskItemType(c.getString(c.getColumnIndex(TASK_ITEM_TYPE)));
                taskItemReadInfo.setStudentId(c.getString(c.getColumnIndex(STUDENT_ID)));
                taskItemReadInfo.setTaskId(c.getString(c.getColumnIndex(TASK_ID)));
                taskItemReadInfo.setTaskItemId(c.getString(c.getColumnIndex(TASK_ITEM_ID)));
                taskItemReadInfo.setTaskItemReadScore(c.getString(c.getColumnIndex(TASK_ITEM_READ_SCORE)));
                taskItemReadInfo.setTaskItemReadComment(c.getString(c.getColumnIndex(TASK_ITEM_READ_COMMENT)));
                taskItemReadInfo.setTaskItemReadTime(c.getInt(c.getColumnIndex(TASK_ITEM_READ_TIME)));
                list.add(taskItemReadInfo);
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return list;
    }

    /**
     * 插入音频评论
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param audioUrl
     * @param audioName
     * @param messageId
     * @param audioLength
     */
    public void insertCommentAudio(String studentId, String taskId, String taskItemId, String audioUrl, String audioLength, String audioName, String messageId, int isTotalComment) {
        mDatabase = mDbHelper.getWritableDatabase();
        String insertSql = "replace into " + TASK_ITEM_READ_AUDIO_TABLE + "(" +
                STUDENT_ID + "," +
                TASK_ID + "," +
                TASK_ITEM_ID + "," +
                TASK_ITEM_READ_AUDIO_URL + "," +
                TASK_ITEM_READ_AUDIO_LENGTH + "," +
                TASK_ITEM_READ_AUDIO_NAME + "," +
                TASK_ITEM_READ_AUDIO_MESSAGE_ID + "," +
                TASK_ITEM_READ_IS_TOTAL_COMMENT + ") values ('" +
                studentId + "','" +
                taskId + "','" +
                taskItemId + "','" +
                audioUrl + "','" +
                audioLength + "','" +
                audioName + "','" +
                messageId + "','" +
                isTotalComment + "')";
        mDatabase.execSQL(insertSql);
        mDatabase.close();
    }

    /**
     * 删除某条语音评论
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param audioUrl
     */
    public void deleteCommentAudio(String studentId, String taskId, String taskItemId, String audioUrl, int isTotalComment) {
        mDatabase = mDbHelper.getWritableDatabase();
        if (isTotalComment == 0) {
            mDatabase.delete(TASK_ITEM_READ_AUDIO_TABLE, STUDENT_ID + " = ? and " + TASK_ID + " = ? and " + TASK_ITEM_ID + " = ? and " + TASK_ITEM_READ_AUDIO_URL + " = ?",
                    new String[]{studentId, taskId, taskItemId, audioUrl});
        } else {
            mDatabase.delete(TASK_ITEM_READ_AUDIO_TABLE, STUDENT_ID + " = ? and " + TASK_ID + " = ? and " + TASK_ITEM_READ_AUDIO_URL + " = ?",
                    new String[]{studentId, taskId, audioUrl});
        }
        mDatabase.close();
    }

    /**
     * 查询某道题的音频评论
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param isTotalComment
     * @return
     */
    public List<TaskItemReadAudioInfo> queryCommentAudio(String studentId, String taskId, String taskItemId, int isTotalComment) {
        List<TaskItemReadAudioInfo> list = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        String sqlStr;
        if (isTotalComment == 0) {
            sqlStr = "select * from " + TASK_ITEM_READ_AUDIO_TABLE + " where " + STUDENT_ID + " = '" + studentId + "' and " + TASK_ID + " = '" + taskId +
                    "' and " + TASK_ITEM_ID + "='" + taskItemId + "'";
        } else {
            sqlStr = "select * from " + TASK_ITEM_READ_AUDIO_TABLE + " where " + STUDENT_ID + " = '" + studentId + "' and " + TASK_ID + " = '" + taskId +
                    "' and " + TASK_ITEM_READ_IS_TOTAL_COMMENT + " = '" + isTotalComment + "'";
        }

        Cursor c = mDatabase.rawQuery(sqlStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                TaskItemReadAudioInfo taskItemReadAudioInfo = new TaskItemReadAudioInfo();
                taskItemReadAudioInfo.setStudentId(c.getString(c.getColumnIndex(STUDENT_ID)));
                taskItemReadAudioInfo.setTaskId(c.getString(c.getColumnIndex(TASK_ID)));
                taskItemReadAudioInfo.setTaskItemId(c.getString(c.getColumnIndex(TASK_ITEM_ID)));
                taskItemReadAudioInfo.setAudioLength(c.getString(c.getColumnIndex(TASK_ITEM_READ_AUDIO_LENGTH)));
                taskItemReadAudioInfo.setAudioUrl(c.getString(c.getColumnIndex(TASK_ITEM_READ_AUDIO_URL)));
                taskItemReadAudioInfo.setAudioName(c.getString(c.getColumnIndex(TASK_ITEM_READ_AUDIO_NAME)));
                taskItemReadAudioInfo.setAudioMessageId(c.getString(c.getColumnIndex(TASK_ITEM_READ_AUDIO_MESSAGE_ID)));
                list.add(taskItemReadAudioInfo);
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return list;
    }


    public class TaskItemReadAudioInfo extends BaseReadInfo {
        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public String getAudioLength() {
            return audioLength;
        }

        public void setAudioLength(String audioLength) {
            this.audioLength = audioLength;
        }

        public String getAudioName() {
            return audioName;
        }

        public void setAudioName(String audioName) {
            this.audioName = audioName;
        }

        private String audioUrl;
        private String audioLength;
        private String audioName;
        private String audioMessageId;

        public String getAudioMessageId() {
            return audioMessageId;
        }

        public void setAudioMessageId(String audioMessageId) {
            this.audioMessageId = audioMessageId;
        }
    }

    /**
     * 批阅实体类
     */
    public class TaskItemReadInfo extends BaseReadInfo {
        public String getTaskItemReadScore() {
            return taskItemReadScore;
        }

        public void setTaskItemReadScore(String taskItemReadScore) {
            this.taskItemReadScore = taskItemReadScore;
        }

        public String getTaskItemReadComment() {
            return taskItemReadComment;
        }

        public void setTaskItemReadComment(String taskItemReadComment) {
            this.taskItemReadComment = taskItemReadComment;
        }

        public String getTaskItemReadOverallCommnet() {
            return taskItemReadOverallCommnet;
        }

        public void setTaskItemReadOverallCommnet(String taskItemReadOverallCommnet) {
            this.taskItemReadOverallCommnet = taskItemReadOverallCommnet;
        }

        private String taskItemReadScore;
        private String taskItemReadComment;
        private String taskItemReadOverallCommnet;

    }

    public class BaseReadInfo {
        private String taskItemType;
        private String studentId;
        private String taskId;
        private String taskItemId;
        private int taskItemReadTime;

        public String getTaskItemType() {
            return taskItemType;
        }

        public void setTaskItemType(String taskItemType) {
            this.taskItemType = taskItemType;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTaskItemId() {
            return taskItemId;
        }

        public void setTaskItemId(String taskItemId) {
            this.taskItemId = taskItemId;
        }

        public int getTaskItemReadTime() {
            return taskItemReadTime;
        }

        public void setTaskItemReadTime(int taskItemReadTime) {
            this.taskItemReadTime = taskItemReadTime;
        }
    }


}

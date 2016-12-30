package com.codyy.erpsportal.commons.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.homework.models.entities.task.TaskAnswer;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.erpsportal.homework.models.entities.task.AnswerTimeLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业、测试模块答案本地存储
 * Created by ldh on 2015/12/29.
 */
public class TaskAnswerDao {
    /**
     * 答题时间记录表
     */
    public static final String TASK_START_LOG_TABLE = "task_start_log";
    /**
     * 习题部分--习题主表
     */
    public static final String TASK_ITEM_ANSWER_TABLE = "task_item_answer";

    /**
     * 习题部分--图片表
     */
    public static final String TASK_ITEM_PIC_TABLE = "task_item_pic";

    private static final String ID = "id";
    public static final String ANSWER_STUDENT_ID = "studentId";
    public static final String ANSWER_TASK_ID = "taskId";
    public static final String ANSWER_TASK_ITEM_ID = "taskItemId";
    public static final String ANSWER_TASK_ITEM_TYPE = "taskItemType";
    public static final String ANSWER_STUDENT_ANSWER = "studentAnswer";
    public static final String ANSWER_STUDENT_TEXT_ANSWER = "studentTextAnswer";
    public static final String ANSWER_RESOURCE_NAME = "resourceName";
    public static final String ANSWER_RESOURCE_ID = "resourceId";
    public static final String ANSWER_RESOURCE_LOCAL_PATH = "resourceLocalPath";
    public static final String ANSWER_RESOURCE_TYPE = "resourceType";
    public static final String ANSWER_TIME = "time";

    public static final String PIC_IMAGE_NAME = "picImageName";
    public static final String PIC_IMAGE_URL = "picImageUrl";

    /**
     * 预估实际时间
     */
    public static final String TASK_ESTIMATE_END_TIME = "estimateEndTime";

    /**
     * 实际结束时间
     */
    public static final String TASK_REAL_END_TIME = "realEndTime";

    public static final String TASK_EXAM_RESULT_ID = "examResultId";

    /**
     * 答题时间记录表创建
     */
    public static final String TASK_START_LOG_TABLE_CREATE = "CREATE TABLE " + TASK_START_LOG_TABLE + "(" +
            ANSWER_STUDENT_ID + " TEXT," +
            ANSWER_TASK_ID + " TEXT," +
            TASK_ESTIMATE_END_TIME + " LONG," +
            TASK_REAL_END_TIME + " LONG," +
            TASK_EXAM_RESULT_ID + " TEXT)";


    /**
     * 习题答案主表
     */
    public static final String TASK_ITEM_ANSWER_CREATE = "CREATE TABLE  " + TASK_ITEM_ANSWER_TABLE +
            "(" +
            ANSWER_STUDENT_ID + " TEXT," +
            ANSWER_TASK_ID + " TEXT," +
            ANSWER_TASK_ITEM_ID + " TEXT PRIMARY KEY UNIQUE," +
            ANSWER_TASK_ITEM_TYPE + " TEXT," +
            ANSWER_STUDENT_ANSWER + " TEXT," +
            ANSWER_STUDENT_TEXT_ANSWER + " TEXT," +
            ANSWER_RESOURCE_NAME + " TEXT," +
            ANSWER_RESOURCE_ID + " TEXT," +
            ANSWER_RESOURCE_LOCAL_PATH + " TEXT," +
            ANSWER_RESOURCE_TYPE + " TEXT," +
            ANSWER_TIME + " LONG )";


    /**
     * 习题答案-图片表
     */
    public static final String TASK_ITEM_PIC_CREATE = "CREATE TABLE  " + TASK_ITEM_PIC_TABLE +
            "(" +
            ANSWER_TASK_ID + " TEXT," +//缺少taskid column
            ANSWER_STUDENT_ID + " TEXT," +
            ANSWER_TASK_ITEM_ID + " TEXT," +
            ANSWER_TASK_ITEM_TYPE + " TEXT," +
            ANSWER_RESOURCE_LOCAL_PATH + " TEXT," +
            PIC_IMAGE_NAME + " TEXT," +
            PIC_IMAGE_URL + " TEXT)";

    private DbHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private volatile static TaskAnswerDao mTaskAnswerDao;

    /**
     * added by eachann
     * date:2016/01/12
     *
     * @param context Context
     * @return TaskAnswerDao
     */
    public static TaskAnswerDao getInstance(Context context) {
        if (mTaskAnswerDao == null) {
            synchronized (TaskAnswerDao.class) {
                if (mTaskAnswerDao == null) {
                    mTaskAnswerDao = new TaskAnswerDao(context);
                }
            }
        }
        return mTaskAnswerDao;
    }

    private TaskAnswerDao(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    /**
     * 保存答案
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param taskItemType
     * @param studentAnswer
     * @param studentTextAnswer
     * @param resourceName      音视频名称
     * @param resourceId        音视频地址
     */
    public void insert(String studentId, String taskId, String taskItemId, String taskItemType, String studentAnswer, String studentTextAnswer,
                       String resourceName, String resourceId, String resourceLocalPath, String resourceType) {
        mDatabase = mDbHelper.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ANSWER_STUDENT_ID,studentId);
//        contentValues.put(ANSWER_TASK_ID, taskAnswer.getTaskId());
//        contentValues.put(ANSWER_TASK_ITEM_ID, taskAnswer.getTaskItemId());
//        contentValues.put(ANSWER_TASK_ITEM_TYPE, taskAnswer.getTaskItemType());
//        contentValues.put(ANSWER_STUDENT_ANSWER, taskAnswer.getStudentAnswer());
//        contentValues.put(ANSWER_STUDENT_TEXT_ANSWER, taskAnswer.getStudentTextAnswer());
//        contentValues.put(ANSWER_RESOURCE_NAME, taskAnswer.getName());
//        contentValues.put(ANSWER_RESOURCE_ID,taskAnswer.getResourceId());
//        contentValues.put(ANSWER_TIME,System.currentTimeMillis());
//        mDatabase.insert(TASK_ITEM_ANSWER_TABLE, null, contentValues);
        String insertSql = "replace into " + TASK_ITEM_ANSWER_TABLE + "(" +
                ANSWER_STUDENT_ID + "," +
                ANSWER_TASK_ID + "," +
                ANSWER_TASK_ITEM_ID + "," +
                ANSWER_TASK_ITEM_TYPE + "," +
                ANSWER_STUDENT_ANSWER + "," +
                ANSWER_STUDENT_TEXT_ANSWER + "," +
                ANSWER_RESOURCE_NAME + "," +
                ANSWER_RESOURCE_ID + "," +
                ANSWER_RESOURCE_LOCAL_PATH + "," +
                ANSWER_RESOURCE_TYPE + "," +
                ANSWER_TIME + ") values ('" +
                studentId + "','" +
                taskId + "','" +
                taskItemId + "','" +
                taskItemType + "','" +
                switchSingleQuotes(studentAnswer) + "','" +
                switchSingleQuotes(studentTextAnswer) + "','" +
                resourceName + "','" +
                resourceId + "','" +
                resourceLocalPath + "','" +
                resourceType + "','" +
                System.currentTimeMillis() + "')";
        mDatabase.execSQL(insertSql);
        mDatabase.close();
    }

    private String switchSingleQuotes(String str) {
        if (str.contains("'"))
            return str.replace("'", "''");
        else return str;
    }

    /**
     * 添加图片信息
     *
     * @param taskItemId   题目id
     * @param taskItemType 题目类型
     * @param imageName    图片名称
     * @param imageUrl     图片地址
     * @return 返回题目id作为外键
     */
    public String insertPic(String studentId, String taskId, String taskItemId, String taskItemType, String imageName, String imageUrl, String imageLocalPath) {
        mDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ANSWER_STUDENT_ID, studentId);
        contentValues.put(ANSWER_TASK_ID, taskId);
        contentValues.put(ANSWER_TASK_ITEM_ID, taskItemId);
        contentValues.put(ANSWER_TASK_ITEM_TYPE, taskItemType);
        contentValues.put(PIC_IMAGE_NAME, imageName);
        contentValues.put(PIC_IMAGE_URL, imageUrl);
        contentValues.put(ANSWER_RESOURCE_LOCAL_PATH, imageLocalPath);
        mDatabase.insert(TASK_ITEM_PIC_TABLE, null, contentValues);
        mDatabase.close();
        return taskItemId;
    }

    /**
     * 增加某学生某套题的答题结束时间记录
     *
     * @param studentId
     * @param taskId
     * @param estimateEndTime 预计结束时间
     * @param realEndTime     实际结束时间
     */
    public void insertAnswerTimeLog(String studentId, String taskId, long estimateEndTime, long realEndTime, String examResultId) {
        if (queryAnswerTimeLog(studentId, taskId) != null)
            return;
        mDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ANSWER_STUDENT_ID, studentId);
        contentValues.put(ANSWER_TASK_ID, taskId);
        contentValues.put(TASK_ESTIMATE_END_TIME, estimateEndTime);
        contentValues.put(TASK_REAL_END_TIME, realEndTime);
        contentValues.put(TASK_EXAM_RESULT_ID, examResultId);
        mDatabase.insert(TASK_START_LOG_TABLE, null, contentValues);
        mDatabase.close();
    }

    /**
     * 查询某学生某套题的答题结束时间记录
     *
     * @param studentId
     * @param taskId
     * @return
     */
    public AnswerTimeLog queryAnswerTimeLog(String studentId, String taskId) {
        mDatabase = mDbHelper.getWritableDatabase();
        String selectStr = "select * from " + TASK_START_LOG_TABLE +
                " where " + ANSWER_STUDENT_ID + " = '" + studentId + "' and " + ANSWER_TASK_ID + " = '" + taskId + "'";
        AnswerTimeLog answerTimeLog = null;
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor c = mDatabase.rawQuery(selectStr, null);
        if (c != null && c.moveToFirst()) {
            answerTimeLog = new AnswerTimeLog();
            answerTimeLog.setTaskId(c.getString(c.getColumnIndex(ANSWER_TASK_ID)));
            answerTimeLog.setStudentId(c.getString(c.getColumnIndex(ANSWER_STUDENT_ID)));
            answerTimeLog.setEstimateEndTime(c.getLong(c.getColumnIndex(TASK_ESTIMATE_END_TIME)));
            answerTimeLog.setRealEndTime(c.getLong(c.getColumnIndex(TASK_REAL_END_TIME)));
            answerTimeLog.setExamResultId(c.getString(c.getColumnIndex(TASK_EXAM_RESULT_ID)));
        }
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDatabase.close();
        return answerTimeLog;
    }

    /**
     * 查询某学生某套题的答题结束时间记录
     *
     * @return
     */
    public List<AnswerTimeLog> queryAnswerTimeLogList() {
        mDatabase = mDbHelper.getWritableDatabase();
        String selectStr = "select * from " + TASK_START_LOG_TABLE;
        List<AnswerTimeLog> list = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor c = mDatabase.rawQuery(selectStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                AnswerTimeLog answerTimeLog = new AnswerTimeLog();
                answerTimeLog.setTaskId(c.getString(c.getColumnIndex(ANSWER_TASK_ID)));
                answerTimeLog.setStudentId(c.getString(c.getColumnIndex(ANSWER_STUDENT_ID)));
                answerTimeLog.setEstimateEndTime(c.getLong(c.getColumnIndex(TASK_ESTIMATE_END_TIME)));
                answerTimeLog.setRealEndTime(c.getLong(c.getColumnIndex(TASK_REAL_END_TIME)));
                answerTimeLog.setExamResultId(c.getString(c.getColumnIndex(TASK_EXAM_RESULT_ID)));
                list.add(answerTimeLog);
            } while (c.moveToNext());
        }
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDatabase.close();
        return list;
    }

    /**
     * 删除某学生某套题的答题结束时间记录
     *
     * @param studentId
     * @param taskId
     */
    public void deleteAnswerTimeLog(String studentId, String taskId) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.delete(TASK_START_LOG_TABLE, ANSWER_STUDENT_ID + " =? and " + ANSWER_TASK_ID + " = ?", new String[]{studentId, taskId});
        mDatabase.close();
    }

    /**
     * 上传成功后清空记录
     * 删除该学生整套测试/作业的所有答案记录
     */
    public void deleteTaskInfo(String studentId, String taskId) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.delete(TASK_ITEM_ANSWER_TABLE, ANSWER_STUDENT_ID + " =? and " + ANSWER_TASK_ID + " = ?", new String[]{studentId, taskId});
        mDatabase.close();
    }

    /**
     * 删除某学生、某个作业/测试、某个题的图片信息
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     */
    public void deletePicInfo(String studentId, String taskId, String taskItemId, String taskItemType) {
        mDatabase = mDbHelper.getWritableDatabase();
        if (!taskItemType.equals(TaskFragment.TYPE_TEXT)) {
            mDatabase.delete(TASK_ITEM_PIC_TABLE, ANSWER_STUDENT_ID + " = ? and " + ANSWER_TASK_ID + " = ? and " + ANSWER_TASK_ITEM_ID + " = ? and " + ANSWER_TASK_ITEM_TYPE + " = ?", new String[]{studentId, taskId, taskItemId, taskItemType});
        } else {
            mDatabase.delete(TASK_ITEM_PIC_TABLE, ANSWER_STUDENT_ID + " = ? and " + ANSWER_TASK_ID + " = ? and " + ANSWER_TASK_ITEM_TYPE + " = ?", new String[]{studentId, taskId, taskItemType});
        }
        mDatabase.close();
    }

    public void deleteOnePicInfo(String studentId, String taskId, String taskItemId, String taskItemType, String message) {
        mDatabase = mDbHelper.getWritableDatabase();
        if (!taskItemType.equals(TaskFragment.TYPE_TEXT)) {
            mDatabase.delete(TASK_ITEM_PIC_TABLE, ANSWER_STUDENT_ID + " = ? and " + ANSWER_TASK_ID + " = ? and " + ANSWER_TASK_ITEM_ID + " = ? and " + ANSWER_TASK_ITEM_TYPE + " = ? and " + ANSWER_RESOURCE_LOCAL_PATH + " = ? ",
                    new String[]{studentId, taskId, taskItemId, taskItemType, message});
        } else {
            mDatabase.delete(TASK_ITEM_PIC_TABLE, ANSWER_STUDENT_ID + " = ? and " + ANSWER_TASK_ID + " = ? and " + ANSWER_TASK_ITEM_TYPE + " = ? and " + ANSWER_RESOURCE_LOCAL_PATH + " = ? ",
                    new String[]{studentId, taskId, taskItemType, message});
        }
        mDatabase.close();
    }

    /**
     * 删除该学生该套试题的所有图片
     *
     * @param studentId
     * @param taskId
     */
    public void deleteAllPicInfo(String studentId, String taskId) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.delete(TASK_ITEM_PIC_TABLE, ANSWER_STUDENT_ID + " = ? and " + ANSWER_TASK_ID + " = ? ", new String[]{studentId, taskId});
        mDatabase.close();
    }

    /**
     * 删除该学生单个题目的答题记录
     * @param uuid
     * @param taskItemId
     */
/*    public void deleteItemInfo(String uuid,String taskItemId){
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.delete(TASK_ITEM_ANSWER_TABLE, ANSWER_STUDENT_ID + "=? &" + ANSWER_TASK_ITEM_ID + " = ?", new String[]{uuid, taskItemId});
        mDatabase.close();
    }*/


    /**
     * 查询该学生该套题目的答案集合
     *
     * @param studentId 学生id
     * @param taskId    作业/测试 id
     * @return
     */
    public List<TaskAnswer> query(String studentId, String taskId) {
        String selectStr = "select * from " + TASK_ITEM_ANSWER_TABLE + " where " +
                ANSWER_STUDENT_ID + " = '" +
                studentId + "' and " + ANSWER_TASK_ID + " = '" + taskId + "'";
        List<TaskAnswer> lists = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor c = mDatabase.rawQuery(selectStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                TaskAnswer taskAnswer = new TaskAnswer();
                taskAnswer.setTaskId(c.getString(c.getColumnIndex(ANSWER_TASK_ID)));
                taskAnswer.setTaskItemId(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_ID)));
                taskAnswer.setTaskItemType(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_TYPE)));
                taskAnswer.setStudentAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_ANSWER)));
                taskAnswer.setStudentTextAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_TEXT_ANSWER)));
                taskAnswer.setResourceName(c.getString(c.getColumnIndex(ANSWER_RESOURCE_NAME)));
                taskAnswer.setResourceId(c.getString(c.getColumnIndex(ANSWER_RESOURCE_ID)));
                taskAnswer.setTime(c.getLong(c.getColumnIndex(ANSWER_TIME)));
                taskAnswer.setResourceLocalPath(c.getString(c.getColumnIndex(ANSWER_RESOURCE_LOCAL_PATH)));
                taskAnswer.setResourceType(c.getString(c.getColumnIndex(ANSWER_RESOURCE_TYPE)));
                lists.add(taskAnswer);
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return lists;
    }

    /**
     * 查询单个习题的信息
     *
     * @param studentId
     * @param taskId
     * @param taskItemId
     * @param taskItemType
     * @return
     */
    public TaskAnswer queryItemInfo(String studentId, String taskId, String taskItemId, String taskItemType) {
        String selectStr;
        TaskAnswer taskAnswer = null;
        if (taskItemType.equals(TaskFragment.TYPE_TEXT)) {
            selectStr = "select * from " + TASK_ITEM_ANSWER_TABLE + " where " + ANSWER_STUDENT_ID + " = '" +
                    studentId + "' and " + ANSWER_TASK_ITEM_ID + " = '" + taskId + "' and " + ANSWER_TASK_ITEM_TYPE + " = '" + taskItemType + "'";
        } else {
            selectStr = "select * from " + TASK_ITEM_ANSWER_TABLE + " where " + ANSWER_STUDENT_ID + " = '" +
                    studentId + "' and " + ANSWER_TASK_ID + " = '" + taskId + "' and " + ANSWER_TASK_ITEM_ID + " = '" + taskItemId + "'";
        }
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor c = mDatabase.rawQuery(selectStr, null);
        if (c != null && c.moveToFirst()) {
            taskAnswer = new TaskAnswer();
            taskAnswer.setTaskId(c.getString(c.getColumnIndex(ANSWER_TASK_ID)));
            taskAnswer.setTaskItemId(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_ID)));
            taskAnswer.setTaskItemType(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_TYPE)));
            taskAnswer.setStudentAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_ANSWER)));
            taskAnswer.setStudentTextAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_TEXT_ANSWER)));
            taskAnswer.setResourceName(c.getString(c.getColumnIndex(ANSWER_RESOURCE_NAME)));
            taskAnswer.setResourceId(c.getString(c.getColumnIndex(ANSWER_RESOURCE_ID)));
            taskAnswer.setTime(c.getLong(c.getColumnIndex(ANSWER_TIME)));
            taskAnswer.setResourceLocalPath(c.getString(c.getColumnIndex(ANSWER_RESOURCE_LOCAL_PATH)));
            taskAnswer.setResourceType(c.getString(c.getColumnIndex(ANSWER_RESOURCE_TYPE)));
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return taskAnswer;
    }

    public List<TaskAnswer> queryExam(String studentId, String taskId) {
        String selectStr = "select * from " + TASK_ITEM_ANSWER_TABLE + " where " +
                ANSWER_STUDENT_ID + " = '" +
                studentId + "' and " + ANSWER_TASK_ID + " = '" + taskId + "'";
        List<TaskAnswer> lists = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor c = mDatabase.rawQuery(selectStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                TaskAnswer taskAnswer = new TaskAnswer();
                taskAnswer.setTaskId(c.getString(c.getColumnIndex(ANSWER_TASK_ID)));
                taskAnswer.setTaskItemId(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_ID)));
                taskAnswer.setTaskItemType(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_TYPE)));
                taskAnswer.setStudentAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_ANSWER)));
                taskAnswer.setStudentTextAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_TEXT_ANSWER)));
                taskAnswer.setResourceName(c.getString(c.getColumnIndex(ANSWER_RESOURCE_NAME)));
                taskAnswer.setResourceId(c.getString(c.getColumnIndex(ANSWER_RESOURCE_ID)));
                taskAnswer.setResourceLocalPath(c.getString(c.getColumnIndex(ANSWER_RESOURCE_LOCAL_PATH)));
                taskAnswer.setResourceType(c.getString(c.getColumnIndex(ANSWER_RESOURCE_TYPE)));
                taskAnswer.setTime(c.getLong(c.getColumnIndex(ANSWER_TIME)));
                lists.add(taskAnswer);
                 /*if (!TextUtils.isEmpty(c.getString(c.getColumnIndex(ANSWER_STUDENT_ANSWER)))){
                    List<TaskPicInfo> picInfos = queryPicInfo(studentId, taskId, c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_ID)), c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_TYPE)));
                    if (picInfos.size() > 0) {
                        TaskAnswer taskAnswer = new TaskAnswer();
                        taskAnswer.setTaskId(c.getString(c.getColumnIndex(ANSWER_TASK_ID)));
                        taskAnswer.setTaskItemId(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_ID)));
                        taskAnswer.setTaskItemType(c.getString(c.getColumnIndex(ANSWER_TASK_ITEM_TYPE)));
                        taskAnswer.setStudentAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_ANSWER)));
                        taskAnswer.setStudentTextAnswer(c.getString(c.getColumnIndex(ANSWER_STUDENT_TEXT_ANSWER)));
                        taskAnswer.setResourceName(c.getString(c.getColumnIndex(ANSWER_RESOURCE_NAME)));
                        taskAnswer.setResourceId(c.getString(c.getColumnIndex(ANSWER_RESOURCE_ID)));
                        taskAnswer.setResourceLocalPath(c.getString(c.getColumnIndex(ANSWER_RESOURCE_LOCAL_PATH)));
                        taskAnswer.setResourceType(c.getString(c.getColumnIndex(ANSWER_RESOURCE_TYPE)));
                        taskAnswer.setTime(c.getLong(c.getColumnIndex(ANSWER_TIME)));
                        lists.add(taskAnswer);
                    }

                }*/
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return lists;
    }

    /**
     * 获取该学生该题目的图片信息
     *
     * @param studentId    学生id
     * @param taskId       作业/测试id
     * @param taskItemId   题id
     * @param taskItemType 题型
     * @return
     */
    public List<TaskPicInfo> queryPicInfo(String studentId, String taskId, String taskItemId, String taskItemType) {
        String selectStr;
        if (taskItemType.equals(TaskFragment.TYPE_TEXT)) {
            selectStr = "select * from " + TASK_ITEM_PIC_TABLE +
                    " where " + ANSWER_STUDENT_ID + " = '" + studentId + "' and " + ANSWER_TASK_ID + " = '" + taskId + "' and " + ANSWER_TASK_ITEM_TYPE + " = '" + taskItemType + "'";
        } else {
            selectStr = "select * from " + TASK_ITEM_PIC_TABLE +
                    " where " + ANSWER_STUDENT_ID + " = '" + studentId + "' and " + ANSWER_TASK_ITEM_ID + " = '" + taskItemId + "' and " + ANSWER_TASK_ITEM_TYPE + " = '" + taskItemType + "'";
        }
        List<TaskPicInfo> picInfoList = new ArrayList<>();
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor c = mDatabase.rawQuery(selectStr, null);
        if (c != null && c.moveToFirst()) {
            do {
                TaskPicInfo picInfo = new TaskPicInfo();
                picInfo.setTaskItemId(taskItemId);
                picInfo.setTaskItemType(taskItemType);
                picInfo.setImageName(c.getString(c.getColumnIndex(PIC_IMAGE_NAME)));
                picInfo.setImageUrl(c.getString(c.getColumnIndex(PIC_IMAGE_URL)));
                picInfo.setImageLocalPath(c.getString(c.getColumnIndex(ANSWER_RESOURCE_LOCAL_PATH)));
                picInfoList.add(picInfo);
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        mDatabase.close();
        return picInfoList;
    }

   /* //将填空题答案处理成一个字符串，用“∴”隔开
    private String appendBlankString(List<TaskAnswer.BlankAnswerListEntity> list) {
        StringBuffer blankAnswerBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size()) {
                blankAnswerBuffer.append(list.get(i).getBlankAnswer() + "∴");
            } else if (i == list.size() - 1) {
                blankAnswerBuffer.append(list.get(i).getBlankAnswer());
            }
        }
        return blankAnswerBuffer.toString();
    }

    //将图片ID处理成一个字符串，用“∴”隔开
    private String appendPicIdString(List<TaskAnswer.PicListEntity> list) {
        StringBuffer blankAnswerBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size()) {
                blankAnswerBuffer.append(list.get(i).getPicId() + "∴");
            } else if (i == list.size() - 1) {
                blankAnswerBuffer.append(list.get(i).getPicId());
            }
        }
        return blankAnswerBuffer.toString();
    }

    //分解填空题答案字符串
    private List<TaskAnswer.BlankAnswerListEntity> splitBlankArray(String blankStr) {
        List<TaskAnswer.BlankAnswerListEntity> list = new ArrayList<>();
        String[] strArray = blankStr.split("∴");
        for (int i = 0; i < strArray.length; i++) {
            TaskAnswer.BlankAnswerListEntity entity = new TaskAnswer.BlankAnswerListEntity();
            entity.setBlankAnswer(strArray[i]);
            list.add(entity);
        }
        return list;
    }

    //分解图片id字符串
    private List<TaskAnswer.PicListEntity> splitPicArray(String picIdStr) {
        List<TaskAnswer.PicListEntity> list = new ArrayList<>();
        String[] strArray = picIdStr.split("∴");
        for (int i = 0; i < strArray.length; i++) {
            TaskAnswer.PicListEntity entity = new TaskAnswer.PicListEntity();
            entity.setPicId(strArray[i]);
            list.add(entity);
        }
        return list;
    }*/

}

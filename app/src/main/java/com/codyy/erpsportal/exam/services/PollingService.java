package com.codyy.erpsportal.exam.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.artifex.mupdfdemo.AsyncTask;
import com.codyy.MediaFileUtils;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskAnswerDao;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.exam.utils.PollingUtils;
import com.codyy.erpsportal.homework.models.entities.task.AnswerTimeLog;
import com.codyy.erpsportal.homework.models.entities.task.TaskAnswer;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eachann on 2016/3/11.
 */
public class PollingService extends Service {
    public static final String ACTION = "com.codyy.erpsportal.services.exam.polling.PollingService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //new ScanFileAsyncTask(this).execute(0);
    }

    class ScanFileAsyncTask extends AsyncTask<Integer, Integer, String> {
        private Context mContext;

        public ScanFileAsyncTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(Integer... params) {
            Cog.i(PollingService.class.getSimpleName(), "执行媒体库扫描更新");
            /*更新图片媒体库,移除无用图片*/
            String str[] = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA};
            Cursor imageCursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, str,
                    MediaStore.Images.Media.SIZE + "<? and " + MediaStore.Images.Media.SIZE + ">0 and " + MediaStore.Images.Media.DISPLAY_NAME + " like '%.jpg'  or '%.jpeg' or '%.bmp' or '%.png'",
                    new String[]{"5242881"}, MediaStore.Images.Media.DATE_ADDED + " ASC");
            if (imageCursor != null && imageCursor.getCount() > 0) {

                while (imageCursor.moveToNext()) {
                    MediaFileUtils.scanFile(mContext, imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
                imageCursor.close();
            }
            /*更新视频媒体库,移除无用视频*/
            String videoStr[] = {MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.DURATION};
            Cursor videoCursor = mContext.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoStr, MediaStore.Video.Media.SIZE + "<? and " + MediaStore.Video.Media.SIZE + ">0 and " + MediaStore.Video.Media.DISPLAY_NAME + " like '%.mp4'",
                    new String[]{"524288001"}, MediaStore.Video.Media.DATE_ADDED + " DESC");
            if (videoCursor != null && videoCursor.getCount() > 0) {
                while (videoCursor.moveToNext()) {
                    MediaFileUtils.scanFile(mContext, videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                }
                videoCursor.close();
            }
            /*更新音频媒体库,移除无用音频*/
            String aduioStr[] = {MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DURATION};
            Cursor aduioCursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, aduioStr, MediaStore.Audio.Media.SIZE + "<? and " + MediaStore.Audio.Media.SIZE + ">0 and " + MediaStore.Audio.Media.DISPLAY_NAME + " like '%.mp3'",
                    new String[]{"104857601"}, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            if (aduioCursor != null && aduioCursor.getCount() > 0) {
                while (aduioCursor.moveToNext()) {
                    MediaFileUtils.scanFile(mContext, aduioCursor.getString(aduioCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                }
                aduioCursor.close();
            }
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestSubmitAnswer();
        PollingUtils.startPollingService(EApplication.instance(), 5, PollingService.class, PollingService.ACTION);
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestSubmitAnswer() {
        final TaskAnswerDao dao = TaskAnswerDao.getInstance(this);
        List<AnswerTimeLog> list = dao.queryAnswerTimeLogList();
        if (list.size() > 0) {
            for (final AnswerTimeLog log : list) {
                if (System.currentTimeMillis() > log.getEstimateEndTime() + 10000) {//延迟10秒钟 发送考试记录
                    final List<TaskAnswer> answerList = dao.queryExam(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), log.getTaskId());
                    Map<String, String> map = new HashMap<>();
                    map.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
                    map.put("examTaskId", log.getTaskId());
                    map.put("examResultId", log.getExamResultId());
                    JSONArray array = new JSONArray();
                    for (TaskAnswer answer : answerList) {
                        Map<String, String> data = new HashMap<>();
                        data.put("examQuestionId", answer.getTaskItemId());
                        data.put("answer", TextUtils.isEmpty(answer.getStudentAnswer()) ? "" : StringUtils.replaceHtml2(answer.getStudentAnswer()));
                        data.put("answerPath", TextUtils.isEmpty(answer.getResourceId()) ? "" : answer.getResourceId());
                        List<TaskPicInfo> picInfos = dao.queryPicInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), answer.getTaskId(), answer.getTaskItemId(), answer.getTaskItemType());
                        if (picInfos != null && picInfos.size() > 0) {
                            JSONArray pics = new JSONArray();
                            for (TaskPicInfo picInfo : picInfos) {
                                pics.put(picInfo.getImageUrl());
                            }
                            data.put("imagePath", pics.toString());
                        }
                        JSONObject object = new JSONObject(data);
                        array.put(object);
                    }
                    map.put("answerInfo", array.toString());
                    RequestSender sender = new RequestSender(this);
                    sender.sendRequest(new RequestSender.RequestData(URLConfig.STUDENT_SUBMIT_EXAM, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if ("success".equals(response.optString("result"))) {
                                dao.deleteAnswerTimeLog(log.getStudentId(), log.getTaskId());
                                dao.deleteTaskInfo(log.getStudentId(), log.getTaskId());
                                dao.deleteAllPicInfo(log.getStudentId(), log.getTaskId());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(Throwable error) {

                        }
                    }));

                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.models.entities.MeetingShow;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.onlinemeetings.widgets.BGABadgeTextView;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频会议工具类
 * Created by poe on 15-8-3.
 */
public class UiOnlineMeetingUtils {

    private static final String TAG = UiOnlineMeetingUtils.class.getSimpleName();
    public static final String DOC_TYPE_PICTURE =   "online.meeting.picture";
    public static final String DOC_TYPE_PDF =   "online.meeting.pdf";
    public static SparseArray<String> mImages = new SparseArray<>();
    static {
        mImages.append(0,"jpg");
        mImages.append(1,"png");
        mImages.append(2,"gif");
        mImages.append(3,"tiff");
    }

    /**
     * 获取系统状态栏的高度
     * @param context
     * @return
     */
    public static int getSystemStatusBarHeight(Activity context){
        Rect rectangle = new Rect();
        Window window = context.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        return statusBarHeight;
    }
    /**
     * 计时器开始计时
     * @param meetingBase
     * @param chronometer
     */
    public static void startCount(MeetingBase meetingBase , Chronometer chronometer) {
        if(meetingBase != null && !TextUtils.isEmpty(meetingBase.getBaseBeginTime())){
            try {
                String beginTime = meetingBase.getBaseBeginTime();
                long now = System.currentTimeMillis();
                long begin = DateUtil.stringToLong(beginTime, DateUtil.DEF_FORMAT);
                long passTime = (now - begin);
                chronometer.setBase(SystemClock.elapsedRealtime() - passTime);
                chronometer.setVisibility(View.VISIBLE);
                chronometer.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取视频播放stream
     * @param meetingBase
     * @param resID
     * @return
     */
    public static String  getStream(MeetingBase meetingBase , String resID){
        if(meetingBase == null || resID == null) return null;

        return  meetingBase.getBaseDMS().getDmsCode() + "_" + meetingBase.getBaseMeetID() + "_u_" + resID;
    }


    /**
     * 获取共享桌面视频播放stream
     * @param meetingBase
     * @param userId 桌面共享人的ｉｄ
     * var shareDeskStreamName = mid + "_" + userId + "_" + "desk",
     * Var shareVideoStreamName = mid + "_" + userId + "_" + "video";
     * @return
     */
    public static String  getDeskStream(MeetingBase meetingBase , String userId){
        if(meetingBase == null || userId == null) return null;

        return  meetingBase.getBaseMeetID() + "_" + userId+"_desk";
    }

    /**
     * 获取共享视频播放stream
     * @param meetingBase
     * @param userId　共享视频的人的ｉｄ .
     * var shareDeskStreamName = mid + "_" + userId + "_" + "desk",
     * Var shareVideoStreamName = mid + "_" + userId + "_" + "video";
     * @return
     */
    public static String  getShareVideoStream(MeetingBase meetingBase , String userId){
        if(meetingBase == null || userId == null) return null;

        return  meetingBase.getBaseMeetID() + "_" + userId+"_video";
    }

    /**
     * 获取在线用户的完整数据 .
     * @param onlineUserID
     * @param userList
     * @return
     */
    public static OnlineUserInfo getOnlineUserByID(String onlineUserID,List<OnlineUserInfo> userList){
        if(userList == null || userList.size() == 0) return  null;

        OnlineUserInfo result = null;
        for(OnlineUserInfo user: userList){
            if(user.getId().equals(onlineUserID)){
                result  =   user;
                //来到此处的必然都是在线的发言人
                user.setIsOnline(true);
                break;
            }
        }

        return  result;
    }

    /**
     * 删除制定resID的文档资源
     * @param resID
     * @param list
     */
    public static void remove(String resID,List<MeetingShow> list){
        if(null!= list && list.size()>0){
            for(int i=0;i<list.size();i++){
                if(resID.equals(list.get(i).getShowResID())){
                    list.remove(i);
                    break;
                }
            }
        }
    }
    /**
     * 判断资源是否已存在 .
     * @param resID
     * @return
     */
    public static int getResourceIndex(String resID,List<MeetingShow> list){
        int result = -1;
        if(null!= list && list.size()>0){

            for(int i=0;i<list.size();i++){
                if(resID.equals(list.get(i).getShowResID())){
                    result  =   i;
                    break;
                }
            }
        }
        return  result;
    }

    /**
     * 创建文件缓存目录 .
     * @return
     */
    public static  String createDir(){
        File file = new File(EApplication.instance().getExternalCacheDir().getAbsolutePath() + "/" + com.codyy.erpsportal.commons.utils.Constants.FOLDER_DOC_CACHE);
        if(file.exists()){
            if(!file.isDirectory()){
                file.delete();
                file.mkdirs();
            }
        }
        else {
            file.mkdirs();
        }
        return  file.getAbsolutePath();
    }

    /**
     * 检查缓存中是否存在某文件
     * @param ResourceName
     * @return
     */
    public static boolean hasLoadCompleted(String ResourceName){
        //bmp转换为jpg.
        if(!TextUtils.isEmpty(ResourceName)&&ResourceName.endsWith(".bmp")){
             ResourceName = ResourceName.substring(0,ResourceName.indexOf(".bmp"))+".jpg";
        }
        File file = new File(createDir() +"/" + ResourceName);
        return file.exists();
    }

    /**
     *  检测文件类型 目前图片支持 {jpg,png,gif,tiff}
     * @param ResourceName
     * @return DOC_TYPE_PICTURE/DOC_TYPE_PDF
     */
    public static String CheckResourceType(String ResourceName){
        String suffix = ResourceName.substring(ResourceName.lastIndexOf(".")+1).toLowerCase();

        for(int i=0 ;i< mImages.size();i++){
            if(mImages.get(i).equals(suffix)){
                return  DOC_TYPE_PICTURE;
            }
        }
        return  DOC_TYPE_PDF;
    }

    /**
     * 根据不同的类型返回不同的数据请求url
     * {集体备课,互动听课,视频会议}
     * @param type 传递的类型 mType
     * @return 对应type的url请求方法名
     */
    public static String getUrlForMeeting(String type){
        String url = "unknown";

        switch (type){
            case Constants.TYPE_PREPARE_LESSON://集体备课
                url=URLConfig.GET_PREPARATION_VIDEO_LIST;
                break;
            case Constants.TYPE_INTERACT_LESSON://互动听课
                url=URLConfig.GET_LECTURE_VIDEO_LIST;
                break;
            case Constants.TYPE_ONLINE_MEETING://集体备课
                url = URLConfig.GET_ONLINE_MEETING_VIDEOS;
                break;
        }
        return  url;
    }
    /**
     * 根据不同的类型返回key值 用于request 请求
     * {集体备课,视频会议,互动听课}
     * @param type
     * @return
     */
    public static String getKeyByMeetingType(String type){

        String key = "unknown";

        switch (type){
            case Constants.TYPE_PREPARE_LESSON://集体备课
                key =   "preparationId";
                break;
            case Constants.TYPE_INTERACT_LESSON://互动听课
                key =   "lectureId";
                break;
            case Constants.TYPE_ONLINE_MEETING://视频会议
                key =   "remoteId";
                break;
        }
        return  key;
    }



    /**
     * 制作TabWidget
     * @param mContext
     * @param title
     * @param drawableId
     * @return
     */
    public static View makeTabIndicator(Context mContext,String title, int drawableId) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator_online, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_tab_indicator);
        imageView.setBackgroundResource(drawableId);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_tab_indicator);
        titleTv.setText(title);
        BGABadgeTextView notice =(BGABadgeTextView) view.findViewById(R.id.tv_tab_notice);
        notice.setVisibility(View.GONE);
        return view;
    }

    /**
     * 獲取Drawble (資源文文件)
     * @param resourceId
     * @return
     */
    public static Drawable loadDrawable(@DrawableRes int resourceId){
        Drawable icon = ContextCompat.getDrawable(EApplication.instance(), resourceId);
        return  icon;
    }

    /**
     * 只要参数为 uuid 和 mid的数据加载...
     * 获取视频 基本数据/用户/演示信息/
     * @param act  activity object .
     * @param userID uuid
     * @param meetingID mid
     * @param url  方法地址
     * @param callback
     */
    public static void loadMeetingData(Activity act , String userID , String meetingID ,String url , final ICallback callback) {

        Map<String, String> params = new HashMap<>();
        params.put("uuid", userID);
        params.put("mid", meetingID);

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(act);
        requestSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    if (null != callback) callback.onSuccess(response);
                } else {
                    //toast error messages .
                    if (!TextUtils.isEmpty(response.optString("message"))) {
                        ToastUtil.showToast(EApplication.instance(), response.optString("message"));
                    }
                    if (null != callback) callback.onFailure(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (null != callback) callback.onNetError();
            }
        }));
    }

    /**
     * 获取视频 基本数据
     * @param act  activity object .
     * @param userID uuid
     * @param meetingID mid
     * @param callback
     */
    public static void loadMeetingBaseData(final FragmentManager fragmentManager, Activity act , String userID , String meetingID ,String role , final ICallback callback){
        act.getFragmentManager();
        Map<String, String> params = new HashMap<>();
        params.put("uuid", userID);
        params.put("mid", meetingID);
        params.put("role", role);// 1:发言人(一般不传) 2:参会者(非发言人) 3:观摩者
        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(act);
        requestSender.sendRequest(new RequestSender.RequestData( URLConfig.GET_ONLINE_MEETING_RESOURCE, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    if (null != callback) callback.onSuccess(response);
                } else {
                    //toast error messages .
                    if(!TextUtils.isEmpty(response.optString("message"))){
                        //ToastUtil.showToast(EApplication.instance(), response.optString("message"));

                        TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.UNSTART_STATUS_TIP);
                        fragment.show(fragmentManager, "showtips");
                    }
                    if (null != callback) callback.onFailure(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (null != callback) callback.onNetError();
            }
        }));
    }

    /**
     * 获取视频 基本数据
     * @param act  activity object .
     * @param userID uuid
     * @param meetingID mid
     * @param meet_model  0:视频模式  1:演示模式
     * @param callback
     * @deprecated 已废弃　，移动端无权限执行切换模式
     */
    public static void setMeetingModel(Activity act , String userID , String meetingID ,String meet_model , final ICallback callback){
        Cog.i(TAG,"setMeetingModel :"+meet_model);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", userID);
        params.put("mid", meetingID);
        params.put("meet_modle", meet_model);

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(act);
        requestSender.sendRequest(new RequestSender.RequestData( URLConfig.SET_ONLINE_MEETING_MODEL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    if (null != callback) callback.onSuccess(response);
                } else {
                    //toast error messages .
                    if(!TextUtils.isEmpty(response.optString("message"))){
                        ToastUtil.showToast(EApplication.instance(), response.optString("message"));
                    }
                    if (null != callback) callback.onFailure(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast( R.string.net_error, Toast.LENGTH_SHORT);
                if (null != callback) callback.onNetError();
            }
        }));
    }


    /**
     * 获取视频共享/桌面共享数据
     * @param act
     * @param userID
     * @param meetingID
     * @param url
     * @param callback
     */
    public static void getShareDetail(Activity act , String userID , String meetingID ,String url , final ICallback callback){

        Map<String, String> params = new HashMap<>();
        params.put("uuid", userID);
        params.put("mid", meetingID);

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(act);
        requestSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    if (null != callback) callback.onSuccess(response);
                } else {
                    //toast error messages .
                    if(!TextUtils.isEmpty(response.optString("message"))){
                        ToastUtil.showToast(EApplication.instance(), response.optString("message"));
                    }
                    if (null != callback) callback.onFailure(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast( R.string.net_error, Toast.LENGTH_SHORT);
                if (null != callback) callback.onNetError();
            }
        }));
    }

    /**
     * 本地对文档的一些列操作 增/删/改 操作
     * @param act
     * @param userID
     * @param meetingID
     * @param resID
     * @param url
     * @param callback
     */
    public static void setDocument(Activity act , String userID , String meetingID ,String resID ,String index ,String url , final ICallback callback){
        Cog.i(TAG,"setDocument :: "+url);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", userID);
        params.put("mid", meetingID);
        params.put("resid",resID);
        if(null != index){
            params.put("index",index);
        }

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(act);
        requestSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    if (null != callback) callback.onSuccess(response);
                } else {
                    //toast error messages .
                    if(!TextUtils.isEmpty(response.optString("message"))){
                        ToastUtil.showToast(EApplication.instance(), response.optString("message"));
                    }
                    if (null != callback) callback.onFailure(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast( R.string.net_error, Toast.LENGTH_SHORT);
                if (null != callback) callback.onNetError();
            }
        }));
    }

    /**
     * 本地对文档的一些列操作 增/删/改 操作
     * @param act
     * @param userID
     * @param meetingID
     * @param resID
     * @param callback
     */
    public static void openDocument(Activity act , String userID , String meetingID ,String resID , final ICallback callback){
        Cog.i(TAG,"openDocument~ ");
        Map<String, String> params = new HashMap<>();
        params.put("uuid", userID);
        params.put("mid", meetingID);
        params.put("docId",resID);

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(act);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.POST_OPEN_ONLINE_MEETING_DOCUMENT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    if (null != callback) callback.onSuccess(response);
                } else {
                    //toast error messages .
                    if(!TextUtils.isEmpty(response.optString("message"))){
                        ToastUtil.showToast(EApplication.instance(), response.optString("message"));
                    }
                    if (null != callback) callback.onFailure(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(  R.string.net_error, Toast.LENGTH_SHORT);
                if (null != callback) callback.onNetError();
            }
        }));
    }


    /**
     * 文档下载地址/演示内容地址
     * @param uuid
     * @param resID
     * @return
     */
    @NonNull
    public static String getResourceURL(String uuid ,String resID) {
        return URLConfig.GET_VIDEOMEETING_DOCUMENT_CONTENT +"?uuid="+uuid+"&docId="+resID;
    }

    public interface  ICallback{

        /**
         * 返回结果正常
         * @param response
         */
        void onSuccess(JSONObject response);

        /**
         * 返回错误
         * @param response
         */
        void onFailure(JSONObject response);

        /**
         * net error
         */
        void onNetError();

    }
}

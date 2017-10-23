package com.codyy.erpsportal.classroom.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 专递课堂详情类
 * Created by ldh on 2016/6/30.
 */
public class ClassRoomDetail extends BaseClassRoomDetail implements Parcelable{
    private String mainUrl;

    private String stream;

    private String urlType;

    private DMS external;

    private DMS internal;

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }


    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    /**
     * 请求成功返回实体类的内容，错误则返回错误码
     *
     * @param response
     * @return
     */
    public static void parseResult(JSONObject response , final ISocketTestCallBack callBack) {
        final ClassRoomDetail classRoomDetail = new ClassRoomDetail();
        List<ReceiveInfoEntity> receiveInfoEntityList = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("receiveInfoList");
        for (int i = 0; i < jsonArray.length(); i++) {
            ReceiveInfoEntity receiveInfoEntity = new ReceiveInfoEntity();
            JSONObject object = jsonArray.optJSONObject(i);
            receiveInfoEntity.setReceiveName(object.isNull("receiveName") ? "" : object.optString("receiveName"));
            receiveInfoEntity.setReceiveUrl(object.isNull("url") ? "" : object.optString("url"));
            receiveInfoEntity.setStream(object.isNull("stream") ? "" : object.optString("stream"));
            receiveInfoEntityList.add(receiveInfoEntity);
        }
        classRoomDetail.setReceiveInfoList(receiveInfoEntityList);
        classRoomDetail.setArea(response.isNull("area") ? "" : response.optString("area"));
        classRoomDetail.setClassPeriod(response.isNull("classPeriod") ? "" : response.optString("classPeriod"));
        classRoomDetail.setClassTime(response.isNull("classTime") ? "" : response.optString("classTime"));
        classRoomDetail.setGrade(response.isNull("grade") ? "" : response.optString("grade"));
        classRoomDetail.setUrlType(response.isNull("urlType") ? "" : response.optString("urlType"));
        classRoomDetail.setMainUrl(response.optString("url"));

        classRoomDetail.setStream(response.isNull("stream") ? "" : response.optString("stream"));
        classRoomDetail.setSchoolName(response.isNull("schoolName") ? "" : response.optString("schoolName"));
        classRoomDetail.setTeacher(response.isNull("teacher") ? "" : response.optString("teacher"));
        classRoomDetail.setSubject(response.isNull("subject") ? "" : response.optString("subject"));

        //17-10-11 根据网络情况返回不通的dmc
        if(null != response.optJSONArray("internal")){
            final JSONObject internal = response.optJSONArray("internal").optJSONObject(0);
            final JSONObject external = response.optJSONArray("external").optJSONObject(0);

            DMS indms = new DMS();
            indms.setSocketUrl(internal.optString("socketUrl"));
            indms.setRtmpUrl(internal.optString("rtmpUrl"));

            DMS exdms = new DMS();
            exdms.setSocketUrl(external.optString("socketUrl"));
            exdms.setRtmpUrl(external.optString("rtmpUrl"));

            classRoomDetail.setExternal(exdms);
            classRoomDetail.setInternal(indms);

            //1. 判断内网的服务器是否可用
                String socketUrl = internal.optString("socketUrl");
                Log.i("socket","test socket start !!!------------------"+socketUrl);

                AsyncHttpGet get = new AsyncHttpGet("http://"+socketUrl);
                get.setTimeout(500);

                AsyncHttpClient.getDefaultInstance().websocket(get, "http", new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        Log.i("socket","test socket end !!!-----------------error: "+ex);
                        if (ex != null) {
                            ex.printStackTrace();
                            classRoomDetail.setMainUrl("rtmp://"+external.optString("rtmpUrl")+"/dms");
                            callBack.onComplete(classRoomDetail);
                            return;
                        }
                        classRoomDetail.setMainUrl("rtmp://"+internal.optString("rtmpUrl")+"/dms");
                        callBack.onComplete(classRoomDetail);

                        String ping ="{method: \"ping\", data: \"1111111111111\"}";
                        webSocket.send(ping);

                        webSocket.setStringCallback(new WebSocket.StringCallback() {
                            public void onStringAvailable(String s) {
                                System.out.println("I got a string: " + s);
                            }
                        });
                    }
                });
            }else{
                callBack.onComplete(classRoomDetail);
            }
    }

    public DMS getExternal() {
        return external;
    }

    public void setExternal(DMS external) {
        this.external = external;
    }

    public DMS getInternal() {
        return internal;
    }

    public void setInternal(DMS internal) {
        this.internal = internal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mainUrl);
        dest.writeString(this.stream);
        dest.writeString(this.urlType);
    }

    public static final Parcelable.Creator<ClassRoomDetail> CREATOR = new Parcelable.Creator<ClassRoomDetail>() {
        public ClassRoomDetail createFromParcel(Parcel source) {
            return new ClassRoomDetail(source);
        }

        public ClassRoomDetail[] newArray(int size) {
            return new ClassRoomDetail[size];
        }
    };

    public ClassRoomDetail(Parcel in){
        this.mainUrl = in.readString();
        this.stream = in.readString();
        this.urlType = in.readString();
    }

    public ClassRoomDetail(){

    }


    public interface ISocketTestCallBack{
        /**
         * 测速完返回详情.
         * @return
         */
        void onComplete(ClassRoomDetail data);
    }
}

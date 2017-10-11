package com.codyy.erpsportal.classroom.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;

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
    public static ClassRoomDetail parseResult(JSONObject response) {
        ClassRoomDetail classRoomDetail = new ClassRoomDetail();
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

        String directURL = response.optString("url");
        //17-10-11 根据网络情况返回不通的dmc
        JSONObject internal = response.optJSONArray("internal").optJSONObject(0);
        JSONObject external = response.optJSONArray("external").optJSONObject(0);
        //判断网络类型
        if(NetworkUtils.isNetWorkTypeWifi(EApplication.instance())){//wifi . 优先内网dms方便演示.
            if(null != internal && !TextUtils.isEmpty(internal.optString("rtmpUrl"))){
                directURL = "rtmp://"+internal.optString("rtmpUrl")+"/dms";
            }else if(null != external && !TextUtils.isEmpty(external.optString("rtmpUrl"))){
                directURL = "rtmp://"+external.optString("rtmpUrl")+"/dms";
            }
        }else{//4G . 有限外网dms
            if(null != external && !TextUtils.isEmpty(external.optString("rtmpUrl"))){
                directURL = "rtmp://"+external.optString("rtmpUrl")+"/dms";
            }else if(null != internal && !TextUtils.isEmpty(internal.optString("rtmpUrl"))){
                directURL = "rtmp://"+internal.optString("rtmpUrl")+"/dms";
            }
        }
        classRoomDetail.setMainUrl(!TextUtils.isEmpty(directURL) ? "" : directURL);

        classRoomDetail.setStream(response.isNull("stream") ? "" : response.optString("stream"));
        classRoomDetail.setSchoolName(response.isNull("schoolName") ? "" : response.optString("schoolName"));
        classRoomDetail.setTeacher(response.isNull("teacher") ? "" : response.optString("teacher"));
        classRoomDetail.setSubject(response.isNull("subject") ? "" : response.optString("subject"));

        return classRoomDetail;
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
}

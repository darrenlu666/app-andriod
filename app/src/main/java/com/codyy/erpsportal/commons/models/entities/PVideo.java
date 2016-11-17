package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.Cog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by caixingming on 2015/5/8.
 *  往期录播的视频单元类
 */
public class PVideo implements Parcelable{

    private String videoId;

    private String videoPath;//视频相对路径

    private String videoName;//视频名称

    public PVideo() {}

    public PVideo(String videoId, String videoPath, String videoName) {
        this.videoId = videoId;
        this.videoPath = videoPath;
        this.videoName = videoName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public static ArrayList<PVideo> parseList(JSONObject response){
        ArrayList<PVideo> mList = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("list");
        if(null!=jsonArray && jsonArray.length()>0){
            Cog.i("Json", "jsonArray length ==>>" + jsonArray.length() + "::" + jsonArray);
            for(int i=0;i < jsonArray.length();i++){
                JSONObject object = jsonArray.optJSONObject(i);
                mList.add( parse(object));
            }
        }
        return  mList;
    }

    /**
     * 课堂巡视列表
     * @param response
     * @return
     */
    public static ArrayList<PVideo> parseTourList(JSONObject response){
        ArrayList<PVideo> videos = new ArrayList<>();
        JSONArray jsonArray    = response.optJSONArray("watchPath");

        if(null != jsonArray && jsonArray.length() > 0){
            Cog.i("Json", "jsonArray length ==>>" + jsonArray.length() + "::" + jsonArray);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject object = jsonArray.optJSONObject(i);
                PVideo pv = new PVideo();
                pv.setVideoName(StringUtils.replaceHtml(object.optString("schoolName")));
                pv.setVideoPath(object.optString("receivePath"));

                if(TextUtils.isEmpty(pv.getVideoPath())){
                    pv.setVideoPath(object.optString("mainPath"));
                }

                if("main".equals(object.optString("roomType"))){
                    videos.add(0, pv);
                }else{
                    videos.add(pv);
                }
            }
        }
        return  videos;
    }


    /**
     * 直播课堂 接收教室
     * @param response
     * @return
     */
    public static ArrayList<PVideo> parseAccessList(JSONObject response){
        ArrayList<PVideo> list = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("watchPath");
        if(null!=jsonArray && jsonArray.length()>0){
            Cog.i("Json", "jsonArray length ==>>" + jsonArray.length() + "::" + jsonArray);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject object = jsonArray.optJSONObject(i);
                if(object.optString("roomType").equals("receive")) {
                    PVideo pv = new PVideo();
                    pv.setVideoName(object.optString("schoolName"));
                    pv.setVideoPath(object.optString("receivePath"));
                    if(TextUtils.isEmpty(pv.getVideoPath())){
                        pv.setVideoPath(object.optString("mainPath"));
                    }
                    list.add(pv);
                }
            }
        }
        return  list;
    }

    private static PVideo parse(JSONObject jsonObject){
        PVideo  pv = new PVideo();
        pv.setVideoId( jsonObject.optString("videoId"));
        pv.setVideoPath( jsonObject.optString("path"));
        pv.setVideoName( jsonObject.optString("name"));
        return pv;
    }

    public static  final Creator<PVideo> CREATOR  = new Creator<PVideo>(){

        @Override
        public PVideo createFromParcel(Parcel source) {
            PVideo  pv = new PVideo();
            pv.videoId  =   source.readString();
            pv.videoPath    =   source.readString();
            pv.videoName    =   source.readString();
            return pv;
        }

        @Override
        public PVideo[] newArray(int size) {
            return new PVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoId);
        dest.writeString(videoPath);
        dest.writeString(videoName);
    }
}

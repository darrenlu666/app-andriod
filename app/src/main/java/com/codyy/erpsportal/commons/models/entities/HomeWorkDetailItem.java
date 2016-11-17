package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/17.
 */
public class HomeWorkDetailItem implements Parcelable{
    /**
     * imgUrl : http://10.1.210.102:8080/MobileInterface/classWorkImages/86fff43217684d85b8bff0c99a11a07d_2540829376094614810360f88a207227_efb930b1-c7e7-4ad2-b3cb-15eb980197b3.review.gif
     * classWorkId : c77676e7f0e0490a82a945fc1c27522d
     * status : 已阅
     */
    private String imgUrl;
    private String classWorkId;
    private String status;

    private String bigImageUrl;

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setClassWorkId(String classWorkId) {
        this.classWorkId = classWorkId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getClassWorkId() {
        return classWorkId;
    }

    public String getStatus() {
        return status;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    /**
     * 返回作业详情item
     *
     * @param array
     * @return
     */
    public static ArrayList<HomeWorkDetailItem> getHomeWorkDetailItem(JSONArray array) {
        ArrayList<HomeWorkDetailItem> homeWorkDetailItems = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            HomeWorkDetailItem item = new HomeWorkDetailItem();
            JSONObject object = array.optJSONObject(i);
            item.setClassWorkId(object.optString("baseClassWorkId"));
            item.setStatus(object.optString("status"));
            StringBuilder builder = new StringBuilder(object.optString("picUrl"));
            item.setBigImageUrl(builder.toString());
            item.setImgUrl(builder.insert(builder.lastIndexOf("."), ".small").toString());
            homeWorkDetailItems.add(item);
        }
        return homeWorkDetailItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgUrl);
        dest.writeString(this.classWorkId);
        dest.writeString(this.status);
        dest.writeString(this.bigImageUrl);
    }

    public HomeWorkDetailItem() {
    }

    protected HomeWorkDetailItem(Parcel in) {
        this.imgUrl = in.readString();
        this.classWorkId = in.readString();
        this.status = in.readString();
        this.bigImageUrl = in.readString();
    }

    public static final Creator<HomeWorkDetailItem> CREATOR = new Creator<HomeWorkDetailItem>() {
        public HomeWorkDetailItem createFromParcel(Parcel source) {
            return new HomeWorkDetailItem(source);
        }

        public HomeWorkDetailItem[] newArray(int size) {
            return new HomeWorkDetailItem[size];
        }
    };
}

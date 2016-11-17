package com.codyy.erpsportal.statistics.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * 区域，学校，直属校
 * Created by gujiajia on 2016/8/7.
 */
public class AreaInfo implements Parcelable{
    public final static String TYPE_AREA = "AREA";

    public final static String TYPE_SCHOOL = "SCH";

    /**
     * 直属校
     */
    public final static String TYPE_DSCH = "DSCH";

    public final static String TYPE_CLASSROOM = "CLASSROOM";

    private String id;

    private String name;

    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSchool() {
        return TYPE_SCHOOL.equals(type);
    }

    public boolean isArea() {
        return TYPE_AREA.equals(type);
    }

    public boolean isDirectSchool() {
        return TYPE_DSCH.equals(type);
    }

    public AreaInfo() { }

    public AreaInfo(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public AreaInfo(UserInfo userInfo) {
        if (!userInfo.isArea()) {
            type = TYPE_SCHOOL;
            id = userInfo.getSchoolId();
        } else {
            type = TYPE_AREA;
            id = userInfo.getBaseAreaId();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
    }

    protected AreaInfo(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
    }

    public static final Creator<AreaInfo> CREATOR = new Creator<AreaInfo>() {
        @Override
        public AreaInfo createFromParcel(Parcel source) {
            return new AreaInfo(source);
        }

        @Override
        public AreaInfo[] newArray(int size) {
            return new AreaInfo[size];
        }
    };

    public static AreaInfo create(String areaId, String schoolId) {
        if (!TextUtils.isEmpty(schoolId)) {
            AreaInfo areaInfo = new AreaInfo();
            areaInfo.setType(TYPE_SCHOOL);
            areaInfo.setId(schoolId);
            return areaInfo;
        } else if (!TextUtils.isEmpty(areaId)) {
            AreaInfo areaInfo = new AreaInfo();
            areaInfo.setType(TYPE_AREA);
            areaInfo.setId(areaId);
            return areaInfo;
        } else {
            return null;
        }
    }
}

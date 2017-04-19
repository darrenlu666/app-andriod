package com.codyy.erpsportal.statistics.models.entities;

import android.os.Parcel;

/**
 * 过滤数据实体
 * Created by gujiajia on 2016/8/14.
 */
public class StatFilterCarrier implements android.os.Parcelable {

    /**
     * 按什么过滤
     */
    private int filterBy;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 学期id
     */
    private String termId;

    /**
     * 学科id，-1为全部学期
     */
    private String subjectId;

    @StatFilterBy
    public int getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(int filterBy) {
        this.filterBy = filterBy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.filterBy);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.termId);
        dest.writeString(this.subjectId);
    }

    public StatFilterCarrier() {
    }

    protected StatFilterCarrier(Parcel in) {
        this.filterBy = in.readInt();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.termId = in.readString();
        this.subjectId = in.readString();
    }

    public static final Creator<StatFilterCarrier> CREATOR = new Creator<StatFilterCarrier>() {
        @Override
        public StatFilterCarrier createFromParcel(Parcel source) {
            return new StatFilterCarrier(source);
        }

        @Override
        public StatFilterCarrier[] newArray(int size) {
            return new StatFilterCarrier[size];
        }
    };
}

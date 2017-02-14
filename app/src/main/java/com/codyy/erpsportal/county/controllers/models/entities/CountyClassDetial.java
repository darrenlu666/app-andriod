package com.codyy.erpsportal.county.controllers.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.widgets.TimeTable.TimeTableView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-8-12.
 */
public class CountyClassDetial implements Parcelable {

    /**
     * dataList : [{"classLevelName":"一年级","classSque":8,"clsScheduleDetailId":"106941d0d9e24b2794f4d9be64aaca67","clsScheduleId":"6895fd337a7f4b2ca8e6e5ee60480c35","courseName":"语文","daySque":7,"status":"INIT"}]
     * result : success
     * weekClass : {"weekPlanNum":0,"weekRealityNum":0,"weekScheNum":5}
     * termClass : {"benefitStuNum":322,"maxTermPlanNum":5,"planScheduleNum":0,"realityScheduleNum":0}
     * weekDateList : [{"date":"8月8日","holiday":false,"weekDay":1},{"date":"8月9日","holiday":false,"weekDay":2},{"date":"8月10日","holiday":false,"weekDay":3},{"date":"8月11日","holiday":false,"weekDay":4},{"date":"8月12日","holiday":false,"weekDay":5},{"date":"8月13日","holiday":false,"weekDay":6},{"date":"8月14日","holiday":false,"weekDay":7}]
     */

    private String result;
    /**
     * weekPlanNum : 0
     * weekRealityNum : 0
     * weekScheNum : 5
     */

    private WeekClassBean weekClass;
    /**
     * benefitStuNum : 322
     * maxTermPlanNum : 5
     * planScheduleNum : 0
     * realityScheduleNum : 0
     */

    private TermClassBean termClass;
    /**
     * classLevelName : 一年级
     * classSque : 8
     * clsScheduleDetailId : 106941d0d9e24b2794f4d9be64aaca67
     * clsScheduleId : 6895fd337a7f4b2ca8e6e5ee60480c35
     * courseName : 语文
     * daySque : 7
     * status : INIT
     */

    private List<DataListBean> dataList;
    /**
     * date : 8月8日
     * holiday : false
     * weekDay : 1
     */

    private List<WeekDateListBean> weekDateList;
    private List<TimeTableView2.Holiday> holidays;
    private List<TimeTableView2.TimeTable> timeTables;

    public List<TimeTableView2.TimeTable> getTimeTables() {
        return timeTables;
    }

    public void setTimeTables(List<TimeTableView2.TimeTable> timeTables) {
        this.timeTables = timeTables;
    }

    public List<TimeTableView2.Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<TimeTableView2.Holiday> holidays) {
        this.holidays = holidays;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public WeekClassBean getWeekClass() {
        return weekClass;
    }

    public void setWeekClass(WeekClassBean weekClass) {
        this.weekClass = weekClass;
    }

    public TermClassBean getTermClass() {
        return termClass;
    }

    public void setTermClass(TermClassBean termClass) {
        this.termClass = termClass;
    }

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public List<WeekDateListBean> getWeekDateList() {
        return weekDateList;
    }

    public void setWeekDateList(List<WeekDateListBean> weekDateList) {
        this.weekDateList = weekDateList;
    }

    public static class WeekClassBean implements Parcelable {
        private int weekPlanNum;
        private int weekRealityNum;
        private int weekScheNum;

        public int getWeekPlanNum() {
            return weekPlanNum;
        }

        public void setWeekPlanNum(int weekPlanNum) {
            this.weekPlanNum = weekPlanNum;
        }

        public int getWeekRealityNum() {
            return weekRealityNum;
        }

        public void setWeekRealityNum(int weekRealityNum) {
            this.weekRealityNum = weekRealityNum;
        }

        public int getWeekScheNum() {
            return weekScheNum;
        }

        public void setWeekScheNum(int weekScheNum) {
            this.weekScheNum = weekScheNum;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.weekPlanNum);
            dest.writeInt(this.weekRealityNum);
            dest.writeInt(this.weekScheNum);
        }

        public WeekClassBean() {
        }

        protected WeekClassBean(Parcel in) {
            this.weekPlanNum = in.readInt();
            this.weekRealityNum = in.readInt();
            this.weekScheNum = in.readInt();
        }

        public static final Creator<WeekClassBean> CREATOR = new Creator<WeekClassBean>() {
            @Override
            public WeekClassBean createFromParcel(Parcel source) {
                return new WeekClassBean(source);
            }

            @Override
            public WeekClassBean[] newArray(int size) {
                return new WeekClassBean[size];
            }
        };
    }

    public static class TermClassBean implements Parcelable {
        private int benefitStuNum;
        private int maxTermPlanNum;
        private int planScheduleNum;
        private int realityScheduleNum;

        public int getBenefitStuNum() {
            return benefitStuNum;
        }

        public void setBenefitStuNum(int benefitStuNum) {
            this.benefitStuNum = benefitStuNum;
        }

        public int getMaxTermPlanNum() {
            return maxTermPlanNum;
        }

        public void setMaxTermPlanNum(int maxTermPlanNum) {
            this.maxTermPlanNum = maxTermPlanNum;
        }

        public int getPlanScheduleNum() {
            return planScheduleNum;
        }

        public void setPlanScheduleNum(int planScheduleNum) {
            this.planScheduleNum = planScheduleNum;
        }

        public int getRealityScheduleNum() {
            return realityScheduleNum;
        }

        public void setRealityScheduleNum(int realityScheduleNum) {
            this.realityScheduleNum = realityScheduleNum;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.benefitStuNum);
            dest.writeInt(this.maxTermPlanNum);
            dest.writeInt(this.planScheduleNum);
            dest.writeInt(this.realityScheduleNum);
        }

        public TermClassBean() {
        }

        protected TermClassBean(Parcel in) {
            this.benefitStuNum = in.readInt();
            this.maxTermPlanNum = in.readInt();
            this.planScheduleNum = in.readInt();
            this.realityScheduleNum = in.readInt();
        }

        public static final Creator<TermClassBean> CREATOR = new Creator<TermClassBean>() {
            @Override
            public TermClassBean createFromParcel(Parcel source) {
                return new TermClassBean(source);
            }

            @Override
            public TermClassBean[] newArray(int size) {
                return new TermClassBean[size];
            }
        };
    }

    public static class DataListBean implements Parcelable {
        private String classLevelName;
        private int classSque;
        private String clsScheduleDetailId;
        private String clsScheduleId;
        private String courseName;
        private int daySque;
        private String status;
        private boolean has;
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isHas() {
            return has;
        }

        public void setHas(boolean has) {
            this.has = has;
        }

        public String getClassLevelName() {
            return classLevelName;
        }

        public void setClassLevelName(String classLevelName) {
            this.classLevelName = classLevelName;
        }

        public int getClassSque() {
            return classSque;
        }

        public void setClassSque(int classSque) {
            this.classSque = classSque;
        }

        public String getClsScheduleDetailId() {
            return clsScheduleDetailId;
        }

        public void setClsScheduleDetailId(String clsScheduleDetailId) {
            this.clsScheduleDetailId = clsScheduleDetailId;
        }

        public String getClsScheduleId() {
            return clsScheduleId;
        }

        public void setClsScheduleId(String clsScheduleId) {
            this.clsScheduleId = clsScheduleId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public int getDaySque() {
            return daySque;
        }

        public void setDaySque(int daySque) {
            this.daySque = daySque;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.classLevelName);
            dest.writeInt(this.classSque);
            dest.writeString(this.clsScheduleDetailId);
            dest.writeString(this.clsScheduleId);
            dest.writeString(this.courseName);
            dest.writeInt(this.daySque);
            dest.writeString(this.status);
            dest.writeByte(this.has ? (byte) 1 : (byte) 0);
            dest.writeInt(this.count);
        }

        public DataListBean() {
        }

        protected DataListBean(Parcel in) {
            this.classLevelName = in.readString();
            this.classSque = in.readInt();
            this.clsScheduleDetailId = in.readString();
            this.clsScheduleId = in.readString();
            this.courseName = in.readString();
            this.daySque = in.readInt();
            this.status = in.readString();
            this.has = in.readByte() != 0;
            this.count = in.readInt();
        }

        public static final Creator<DataListBean> CREATOR = new Creator<DataListBean>() {
            @Override
            public DataListBean createFromParcel(Parcel source) {
                return new DataListBean(source);
            }

            @Override
            public DataListBean[] newArray(int size) {
                return new DataListBean[size];
            }
        };
    }

    public static class WeekDateListBean implements Parcelable {
        private String date;
        private boolean holiday;
        private int weekDay;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public boolean isHoliday() {
            return holiday;
        }

        public void setHoliday(boolean holiday) {
            this.holiday = holiday;
        }

        public int getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(int weekDay) {
            this.weekDay = weekDay;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.date);
            dest.writeByte(this.holiday ? (byte) 1 : (byte) 0);
            dest.writeInt(this.weekDay);
        }

        public WeekDateListBean() {
        }

        protected WeekDateListBean(Parcel in) {
            this.date = in.readString();
            this.holiday = in.readByte() != 0;
            this.weekDay = in.readInt();
        }

        public static final Creator<WeekDateListBean> CREATOR = new Creator<WeekDateListBean>() {
            @Override
            public WeekDateListBean createFromParcel(Parcel source) {
                return new WeekDateListBean(source);
            }

            @Override
            public WeekDateListBean[] newArray(int size) {
                return new WeekDateListBean[size];
            }
        };
    }

    public CountyClassDetial() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeParcelable(this.weekClass, flags);
        dest.writeParcelable(this.termClass, flags);
        dest.writeTypedList(this.dataList);
        dest.writeTypedList(this.weekDateList);
        dest.writeTypedList(this.holidays);
        dest.writeTypedList(this.timeTables);
    }

    protected CountyClassDetial(Parcel in) {
        this.result = in.readString();
        this.weekClass = in.readParcelable(WeekClassBean.class.getClassLoader());
        this.termClass = in.readParcelable(TermClassBean.class.getClassLoader());
        this.dataList = in.createTypedArrayList(DataListBean.CREATOR);
        this.weekDateList = in.createTypedArrayList(WeekDateListBean.CREATOR);
        this.holidays = in.createTypedArrayList(TimeTableView2.Holiday.CREATOR);
        this.timeTables = in.createTypedArrayList(TimeTableView2.TimeTable.CREATOR);
    }

    public static final Creator<CountyClassDetial> CREATOR = new Creator<CountyClassDetial>() {
        @Override
        public CountyClassDetial createFromParcel(Parcel source) {
            return new CountyClassDetial(source);
        }

        @Override
        public CountyClassDetial[] newArray(int size) {
            return new CountyClassDetial[size];
        }
    };
}

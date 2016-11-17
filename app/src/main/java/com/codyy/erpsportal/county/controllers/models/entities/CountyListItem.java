package com.codyy.erpsportal.county.controllers.models.entities;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.codyy.erpsportal.county.controllers.activities.CountyItemDetailActivity;
import com.codyy.erpsportal.county.controllers.fragments.ContyListFragment;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;

/**
 * Created by kmdai on 16-6-6.
 */
public class CountyListItem extends RefreshEntity implements Parcelable {
    /**
     * 标题
     */
    public final static int ITEM_TYPE_TITLE = REFRESH_TYPE_LASTVIEW + 1;
    /**
     * 标题内容item
     */
    public final static int ITEM_TYPE_COUNT = ITEM_TYPE_TITLE + 1;

    /**
     * areaName : 园区
     * clsClassroomId : e0a248bab93345afa8cc0afacf728830
     * clsSchoolId : 300ef1ecf09d4dcd9db27d33baba9ea3
     * mainClassRoomName : 主讲教室1
     * mainScheduleList : [{"actualTimes":0,"baseUserId":"5ae60bab4e8d44ec9e36857f60127813","classlevel":"一年级","classlevelId":"31efba2782214d0eb5736055aae04da0","classroomId":null,"planTimes":2,"receiveClassRoomList":null,"result":null,"roomName":null,"rowCount":1,"scheduleDetailId":null,"scheduleId":"5e3a182e83f8464c9d01922b6fe14c8e","schoolName":null,"status":null,"subject":"数学","subjectId":"382c748435cf4600a3871f9b4bab84cf","surveillance":null,"teacherName":"deshan","weekTimes":1}]
     * mainSelfScheduleList : null
     * rowCount : 1
     * schoolName : 阿泽小学
     * selfRowCount : 1
     */

    private String areaName;
    private String clsClassroomId;
    private String clsSchoolId;
    private String mainClassRoomName;
    private int rowCount;
    private String schoolName;
    private int selfRowCount;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public String getMainClassRoomName() {
        return mainClassRoomName;
    }

    public void setMainClassRoomName(String mainClassRoomName) {
        this.mainClassRoomName = mainClassRoomName;
    }


    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getSelfRowCount() {
        return selfRowCount;
    }

    public void setSelfRowCount(int selfRowCount) {
        this.selfRowCount = selfRowCount;
    }

    public static class ScheduleItem extends RefreshEntity {


        /**
         * baseUserId : 348415a468f34697acde4496a850006d
         * classSeq : 2
         * actualTimes : 0
         * classlevel : 二年级
         * classlevelId : c6bf499ea16e4673a0ec83d856cfea71
         * classroomId :
         * daySeq : 7
         * rowCount : 1
         * scheduleDate : 1470499200000
         * scheduleDetailId : 4652aaa317e34583bb7e3f1cc452b8e9
         * scheduleId : 4e2c8ae28fc8411fb089ea890f71ca4c
         * strScheduleDate : 2016-08-07
         * subject : 语文
         * subjectId : 6a3387092fad4540b05114e2a45dc80b
         * planTimes : 2
         * weekTimes : 1
         * teacherName : 吴一桐
         */

        private String baseUserId;
        private int classSeq;
        private int actualTimes;
        private String classlevel;
        private String classlevelId;
        private String classroomId;
        private int daySeq;
        private int rowCount;
        private long scheduleDate;
        private String scheduleDetailId;
        private String scheduleId;
        private String strScheduleDate;
        private String subject;
        private String subjectId;
        private int planTimes;
        private int weekTimes;
        private String teacherName;
        private int contyType;
        private String dayStr;
        private String classStr;

        public String getDayStr() {
            return dayStr;
        }

        public void setDayStr(String dayStr) {
            this.dayStr = dayStr;
        }

        public String getClassStr() {
            return classStr;
        }

        public void setClassStr(String classStr) {
            this.classStr = classStr;
        }

        public int getContyType() {
            return contyType;
        }

        public void setContyType(int contyType) {
            this.contyType = contyType;
        }

        public String getBaseUserId() {
            return baseUserId;
        }

        public void setBaseUserId(String baseUserId) {
            this.baseUserId = baseUserId;
        }

        public int getClassSeq() {
            return classSeq;
        }

        public void setClassSeq(int classSeq) {
            this.classSeq = classSeq;
        }

        public int getActualTimes() {
            return actualTimes;
        }

        public void setActualTimes(int actualTimes) {
            this.actualTimes = actualTimes;
        }

        public String getClasslevel() {
            return classlevel;
        }

        public void setClasslevel(String classlevel) {
            this.classlevel = classlevel;
        }

        public String getClasslevelId() {
            return classlevelId;
        }

        public void setClasslevelId(String classlevelId) {
            this.classlevelId = classlevelId;
        }

        public String getClassroomId() {
            return classroomId;
        }

        public void setClassroomId(String classroomId) {
            this.classroomId = classroomId;
        }

        public int getDaySeq() {
            return daySeq;
        }

        public void setDaySeq(int daySeq) {
            this.daySeq = daySeq;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public long getScheduleDate() {
            return scheduleDate;
        }

        public void setScheduleDate(long scheduleDate) {
            this.scheduleDate = scheduleDate;
        }

        public String getScheduleDetailId() {
            return scheduleDetailId;
        }

        public void setScheduleDetailId(String scheduleDetailId) {
            this.scheduleDetailId = scheduleDetailId;
        }

        public String getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
        }

        public String getStrScheduleDate() {
            return strScheduleDate;
        }

        public void setStrScheduleDate(String strScheduleDate) {
            this.strScheduleDate = strScheduleDate;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public int getPlanTimes() {
            return planTimes;
        }

        public void setPlanTimes(int planTimes) {
            this.planTimes = planTimes;
        }

        public int getWeekTimes() {
            return weekTimes;
        }

        public void setWeekTimes(int weekTimes) {
            this.weekTimes = weekTimes;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public ScheduleItem() {
        }

        /**
         * item点击
         *
         * @param view
         * @param item
         */
        public void onClick(View view, ScheduleItem item) {
            Intent intent = new Intent(view.getContext(), CountyItemDetailActivity.class);
            intent.putExtra(CountyItemDetailActivity.EXTRA_DATA, item);
            intent.putExtra(ContyListFragment.EXTRA_TYPE, item.getContyType());
            view.getContext().startActivity(intent);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.baseUserId);
            dest.writeInt(this.classSeq);
            dest.writeInt(this.actualTimes);
            dest.writeString(this.classlevel);
            dest.writeString(this.classlevelId);
            dest.writeString(this.classroomId);
            dest.writeInt(this.daySeq);
            dest.writeInt(this.rowCount);
            dest.writeLong(this.scheduleDate);
            dest.writeString(this.scheduleDetailId);
            dest.writeString(this.scheduleId);
            dest.writeString(this.strScheduleDate);
            dest.writeString(this.subject);
            dest.writeString(this.subjectId);
            dest.writeInt(this.planTimes);
            dest.writeInt(this.weekTimes);
            dest.writeString(this.teacherName);
            dest.writeInt(this.contyType);
        }

        protected ScheduleItem(Parcel in) {
            super(in);
            this.baseUserId = in.readString();
            this.classSeq = in.readInt();
            this.actualTimes = in.readInt();
            this.classlevel = in.readString();
            this.classlevelId = in.readString();
            this.classroomId = in.readString();
            this.daySeq = in.readInt();
            this.rowCount = in.readInt();
            this.scheduleDate = in.readLong();
            this.scheduleDetailId = in.readString();
            this.scheduleId = in.readString();
            this.strScheduleDate = in.readString();
            this.subject = in.readString();
            this.subjectId = in.readString();
            this.planTimes = in.readInt();
            this.weekTimes = in.readInt();
            this.teacherName = in.readString();
            this.contyType = in.readInt();
        }

        public static final Creator<ScheduleItem> CREATOR = new Creator<ScheduleItem>() {
            @Override
            public ScheduleItem createFromParcel(Parcel source) {
                return new ScheduleItem(source);
            }

            @Override
            public ScheduleItem[] newArray(int size) {
                return new ScheduleItem[size];
            }
        };
    }

    public CountyListItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.areaName);
        dest.writeString(this.clsClassroomId);
        dest.writeString(this.clsSchoolId);
        dest.writeString(this.mainClassRoomName);
        dest.writeInt(this.rowCount);
        dest.writeString(this.schoolName);
        dest.writeInt(this.selfRowCount);
    }

    protected CountyListItem(Parcel in) {
        super(in);
        this.areaName = in.readString();
        this.clsClassroomId = in.readString();
        this.clsSchoolId = in.readString();
        this.mainClassRoomName = in.readString();
        this.rowCount = in.readInt();
        this.schoolName = in.readString();
        this.selfRowCount = in.readInt();
    }

    public static final Creator<CountyListItem> CREATOR = new Creator<CountyListItem>() {
        @Override
        public CountyListItem createFromParcel(Parcel source) {
            return new CountyListItem(source);
        }

        @Override
        public CountyListItem[] newArray(int size) {
            return new CountyListItem[size];
        }
    };

}

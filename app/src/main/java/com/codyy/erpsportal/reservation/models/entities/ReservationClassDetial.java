package com.codyy.erpsportal.reservation.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.widgets.TimeTable.TimeTableView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-8-8.
 */
public class ReservationClassDetial implements Parcelable {

    /**
     * result : success
     * holidayList : [{"dateOfWeek":"08-08","daySeq":1,"holidayFlag":false,"holidayName":null,"strDate":"08-08"},{"dateOfWeek":"08-09","daySeq":2,"holidayFlag":false,"holidayName":null,"strDate":"08-09"},{"dateOfWeek":"08-10","daySeq":3,"holidayFlag":false,"holidayName":null,"strDate":"08-10"},{"dateOfWeek":"08-11","daySeq":4,"holidayFlag":false,"holidayName":null,"strDate":"08-11"},{"dateOfWeek":"08-12","daySeq":5,"holidayFlag":false,"holidayName":null,"strDate":"08-12"},{"dateOfWeek":"08-13","daySeq":6,"holidayFlag":false,"holidayName":null,"strDate":"08-13"},{"dateOfWeek":"08-14","daySeq":7,"holidayFlag":false,"holidayName":null,"strDate":"08-14"}]
     * scheduleList : [{"classSeq":1,"roomType":null,"status":"INIT","subjectName":"英语","beginTime":1470585600000,"liveAppointmentId":"60c78deb6dac4c0797edde9aaa4390f2"}]
     * roomList : [{"classroomMode":"1","clsClassroomId":"dfaab96e7b14465da63b17cc0b1a1a50","clsSchoolId":"aa261c29b72d49f786e15b496baeab37","handDirectedSwitch":"N","interactiveClassroomFlag":"N","roomName":"主讲教室1","roomType":"MASTER","schoolName":"王刚学校"}]
     */

    private String result;
    /**
     * dateOfWeek : 08-08
     * daySeq : 1
     * holidayFlag : false
     * holidayName : null
     * strDate : 08-08
     */

    private List<HolidayListBean> holidayList;
    /**
     * classSeq : 1
     * roomType : null
     * status : INIT
     * subjectName : 英语
     * beginTime : 1470585600000
     * liveAppointmentId : 60c78deb6dac4c0797edde9aaa4390f2
     */

    private List<ScheduleListBean> scheduleList;
    /**
     * classroomMode : 1
     * clsClassroomId : dfaab96e7b14465da63b17cc0b1a1a50
     * clsSchoolId : aa261c29b72d49f786e15b496baeab37
     * handDirectedSwitch : N
     * interactiveClassroomFlag : N
     * roomName : 主讲教室1
     * roomType : MASTER
     * schoolName : 王刚学校
     */

    private List<RoomListBean> roomList;
    private List<TimeTableView2.TimeTable> mTimeTableContents;
    private List<TimeTableView2.Holiday> weekList;

    public List<TimeTableView2.Holiday> getWeekList() {
        return weekList;
    }

    public void setWeekList(List<TimeTableView2.Holiday> weekList) {
        this.weekList = weekList;
    }

    public List<TimeTableView2.TimeTable> getTimeTableContents() {
        return mTimeTableContents;
    }

    public void setTimeTableContents(List<TimeTableView2.TimeTable> tableContents) {
        this.mTimeTableContents = tableContents;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<HolidayListBean> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<HolidayListBean> holidayList) {
        this.holidayList = holidayList;
    }

    public List<ScheduleListBean> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<ScheduleListBean> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<RoomListBean> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<RoomListBean> roomList) {
        this.roomList = roomList;
    }

    public static class HolidayListBean {
        private long dateOfWeek;
        private int daySeq;
        private boolean holidayFlag;
        private String holidayName;
        private String strDate;

        public long getDateOfWeek() {
            return dateOfWeek;
        }

        public void setDateOfWeek(long dateOfWeek) {
            this.dateOfWeek = dateOfWeek;
        }

        public int getDaySeq() {
            return daySeq;
        }

        public void setDaySeq(int daySeq) {
            this.daySeq = daySeq;
        }

        public boolean isHolidayFlag() {
            return holidayFlag;
        }

        public void setHolidayFlag(boolean holidayFlag) {
            this.holidayFlag = holidayFlag;
        }

        public String getHolidayName() {
            return holidayName;
        }

        public void setHolidayName(String holidayName) {
            this.holidayName = holidayName;
        }

        public String getStrDate() {
            return strDate;
        }

        public void setStrDate(String strDate) {
            this.strDate = strDate;
        }
    }

    public static class ScheduleListBean implements Parcelable {
        private int classSeq;
        private int daySeq;
        private String roomType;
        private String status;
        private String subjectName;
        private long beginTime;
        private String liveAppointmentId;

        public int getClassSeq() {
            return classSeq;
        }

        public int getDaySeq() {
            return daySeq;
        }

        public void setDaySeq(int daySeq) {
            this.daySeq = daySeq;
        }

        public void setClassSeq(int classSeq) {
            this.classSeq = classSeq;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public long getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(long beginTime) {
            this.beginTime = beginTime;
        }

        public String getLiveAppointmentId() {
            return liveAppointmentId;
        }

        public void setLiveAppointmentId(String liveAppointmentId) {
            this.liveAppointmentId = liveAppointmentId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.classSeq);
            dest.writeInt(this.daySeq);
            dest.writeString(this.roomType);
            dest.writeString(this.status);
            dest.writeString(this.subjectName);
            dest.writeLong(this.beginTime);
            dest.writeString(this.liveAppointmentId);
        }

        public ScheduleListBean() {
        }

        protected ScheduleListBean(Parcel in) {
            this.classSeq = in.readInt();
            this.daySeq = in.readInt();
            this.roomType = in.readString();
            this.status = in.readString();
            this.subjectName = in.readString();
            this.beginTime = in.readLong();
            this.liveAppointmentId = in.readString();
        }

        public static final Parcelable.Creator<ScheduleListBean> CREATOR = new Parcelable.Creator<ScheduleListBean>() {
            @Override
            public ScheduleListBean createFromParcel(Parcel source) {
                return new ScheduleListBean(source);
            }

            @Override
            public ScheduleListBean[] newArray(int size) {
                return new ScheduleListBean[size];
            }
        };
    }

    public static class RoomListBean {
        private String classroomMode;
        private String clsClassroomId;
        private String clsSchoolId;
        private String handDirectedSwitch;
        private String interactiveClassroomFlag;
        private String roomName;
        private String roomType;
        private String schoolName;

        public String getClassroomMode() {
            return classroomMode;
        }

        public void setClassroomMode(String classroomMode) {
            this.classroomMode = classroomMode;
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

        public String getHandDirectedSwitch() {
            return handDirectedSwitch;
        }

        public void setHandDirectedSwitch(String handDirectedSwitch) {
            this.handDirectedSwitch = handDirectedSwitch;
        }

        public String getInteractiveClassroomFlag() {
            return interactiveClassroomFlag;
        }

        public void setInteractiveClassroomFlag(String interactiveClassroomFlag) {
            this.interactiveClassroomFlag = interactiveClassroomFlag;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeList(this.holidayList);
        dest.writeTypedList(this.scheduleList);
        dest.writeList(this.roomList);
        dest.writeList(this.mTimeTableContents);
        dest.writeList(this.weekList);
    }

    public ReservationClassDetial() {
    }

    protected ReservationClassDetial(Parcel in) {
        this.result = in.readString();
        this.holidayList = new ArrayList<HolidayListBean>();
        in.readList(this.holidayList, HolidayListBean.class.getClassLoader());
        this.scheduleList = in.createTypedArrayList(ScheduleListBean.CREATOR);
        this.roomList = new ArrayList<RoomListBean>();
        in.readList(this.roomList, RoomListBean.class.getClassLoader());
        this.mTimeTableContents = new ArrayList<TimeTableView2.TimeTable>();
        in.readList(this.mTimeTableContents, TimeTableView2.TimeTable.class.getClassLoader());
        this.weekList = new ArrayList<TimeTableView2.Holiday>();
        in.readList(this.weekList, TimeTableView2.Holiday.class.getClassLoader());
    }

    public static final Parcelable.Creator<ReservationClassDetial> CREATOR = new Parcelable.Creator<ReservationClassDetial>() {
        @Override
        public ReservationClassDetial createFromParcel(Parcel source) {
            return new ReservationClassDetial(source);
        }

        @Override
        public ReservationClassDetial[] newArray(int size) {
            return new ReservationClassDetial[size];
        }
    };
}

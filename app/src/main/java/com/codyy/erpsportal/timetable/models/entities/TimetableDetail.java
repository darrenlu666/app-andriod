package com.codyy.erpsportal.timetable.models.entities;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-8-3.
 */
public class TimetableDetail {
    private int weekCount;
    private boolean result;
    private int currentWeek;
    private String currentDate;
    private int code;
    private int afternoonCount;
    private int morningCount;

    public String getCurrentDate() {
        return currentDate;
    }

    public int getAfternoonCount() {
        return afternoonCount;
    }

    public void setAfternoonCount(int afternoonCount) {
        this.afternoonCount = afternoonCount;
    }

    public int getMorningCount() {
        return morningCount;
    }

    public void setMorningCount(int morningCount) {
        this.morningCount = morningCount;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    private List<HolidayListBean> holidayList;


    private List<ScheduleListBean> scheduleList;

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public static class HolidayListBean {
        private boolean holidayFlag;
        private int daySeq;
        private String holidayName;
        private String strDate;

        public boolean isHolidayFlag() {
            return holidayFlag;
        }

        public void setHolidayFlag(boolean holidayFlag) {
            this.holidayFlag = holidayFlag;
        }

        public int getDaySeq() {
            return daySeq;
        }

        public void setDaySeq(int daySeq) {
            this.daySeq = daySeq;
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
        private String mainClassroomName;
        private String status;
        private String classIds;
        private String mainSchoolName;
        private String classNames;
        private String classLevelId;
        private String teacherPhone;
        private int daySeq;
        private String teacherName;
        private String relatedClass;
        private String subjectName;
        private String classLevelName;
        private String subjectId;
        private int classSeq;
        private String teacherId;
        private String detailId;
        private String courseStartTime;
        private String courseEndTime;
        private boolean isAdd;

        public void onClick(View view) {
            System.out.println("-----------onClick");
        }

        public boolean isAdd() {
            return isAdd;
        }

        public void setAdd(boolean add) {
            isAdd = add;
        }

        public String getCourseStartTime() {
            return courseStartTime;
        }

        public void setCourseStartTime(String courseStartTime) {
            this.courseStartTime = courseStartTime;
        }

        public String getCourseEndTime() {
            return courseEndTime;
        }

        public void setCourseEndTime(String courseEndTime) {
            this.courseEndTime = courseEndTime;
        }

        public void onPhoneClick(View view, String phone) {
            if (phone != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        }

        /**
         * classlevelId : 69f64de14ce04b7eb096351709cf7567
         * classlevelName : 九年级
         * className : 一班
         * baseClassId : 6961445b37304403a964bca7b90bdbec
         */

        private List<ListenClassListBean> listenClassList;
        /**
         * phoneNum : null
         * baseUserId : 04373a919e284ca4a10ab24d91a264bf
         * realname : wgschooltea3
         */

        private List<DirectorListBean> directorList;
        /**
         * clsSchoolId : aa261c29b72d49f786e15b496baeab37
         * schoolName : 王刚学校
         * studentNum : 100
         * teacherPhone : null
         * classroomId : dfaab96e7b14465da63b17cc0b1a1a50
         * teacherName : wgschooltea1
         * teacherId : aa516e6ca3164e2d83e5085643bcdf98
         * classroomName : 主讲教室1
         */

        private List<ReceiveListBean> receiveList;

        public String getMainClassroomName() {
            return mainClassroomName;
        }

        public void setMainClassroomName(String mainClassroomName) {
            this.mainClassroomName = mainClassroomName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getClassIds() {
            return classIds;
        }

        public void setClassIds(String classIds) {
            this.classIds = classIds;
        }

        public String getMainSchoolName() {
            return mainSchoolName;
        }

        public void setMainSchoolName(String mainSchoolName) {
            this.mainSchoolName = mainSchoolName;
        }

        public String getClassNames() {
            return classNames;
        }

        public void setClassNames(String classNames) {
            this.classNames = classNames;
        }

        public String getClassLevelId() {
            return classLevelId;
        }

        public void setClassLevelId(String classLevelId) {
            this.classLevelId = classLevelId;
        }

        public String getTeacherPhone() {
            return teacherPhone;
        }

        public void setTeacherPhone(String teacherPhone) {
            this.teacherPhone = teacherPhone;
        }

        public int getDaySeq() {
            return daySeq;
        }

        public void setDaySeq(int daySeq) {
            this.daySeq = daySeq;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getRelatedClass() {
            return relatedClass;
        }

        public void setRelatedClass(String relatedClass) {
            this.relatedClass = relatedClass;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getClassLevelName() {
            return classLevelName;
        }

        public void setClassLevelName(String classLevelName) {
            this.classLevelName = classLevelName;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public int getClassSeq() {
            return classSeq;
        }

        public void setClassSeq(int classSeq) {
            this.classSeq = classSeq;
        }

        public String getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }

        public String getDetailId() {
            return detailId;
        }

        public void setDetailId(String detailId) {
            this.detailId = detailId;
        }

        public List<ListenClassListBean> getListenClassList() {
            return listenClassList;
        }

        public void setListenClassList(List<ListenClassListBean> listenClassList) {
            this.listenClassList = listenClassList;
        }

        public List<DirectorListBean> getDirectorList() {
            return directorList;
        }

        public void setDirectorList(List<DirectorListBean> directorList) {
            this.directorList = directorList;
        }

        public List<ReceiveListBean> getReceiveList() {
            return receiveList;
        }

        public void setReceiveList(List<ReceiveListBean> receiveList) {
            this.receiveList = receiveList;
        }

        public static class ListenClassListBean implements Parcelable {
            private String classlevelId;
            private String classlevelName;
            private String className;
            private String baseClassId;

            public String getClasslevelId() {
                return classlevelId;
            }

            public void setClasslevelId(String classlevelId) {
                this.classlevelId = classlevelId;
            }

            public String getClasslevelName() {
                return classlevelName;
            }

            public void setClasslevelName(String classlevelName) {
                this.classlevelName = classlevelName;
            }

            public String getClassName() {
                return className;
            }

            public void setClassName(String className) {
                this.className = className;
            }

            public String getBaseClassId() {
                return baseClassId;
            }

            public void setBaseClassId(String baseClassId) {
                this.baseClassId = baseClassId;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.classlevelId);
                dest.writeString(this.classlevelName);
                dest.writeString(this.className);
                dest.writeString(this.baseClassId);
            }

            public ListenClassListBean() {
            }

            protected ListenClassListBean(Parcel in) {
                this.classlevelId = in.readString();
                this.classlevelName = in.readString();
                this.className = in.readString();
                this.baseClassId = in.readString();
            }

            public static final Creator<ListenClassListBean> CREATOR = new Creator<ListenClassListBean>() {
                @Override
                public ListenClassListBean createFromParcel(Parcel source) {
                    return new ListenClassListBean(source);
                }

                @Override
                public ListenClassListBean[] newArray(int size) {
                    return new ListenClassListBean[size];
                }
            };
        }

        public static class DirectorListBean implements Parcelable {
            private String phoneNum;
            private String baseUserId;
            private String realname;

            public String getPhoneNum() {
                return phoneNum;
            }

            public void setPhoneNum(String phoneNum) {
                this.phoneNum = phoneNum;
            }

            public String getBaseUserId() {
                return baseUserId;
            }

            public void setBaseUserId(String baseUserId) {
                this.baseUserId = baseUserId;
            }

            public String getRealname() {
                return realname;
            }

            public void setRealname(String realname) {
                this.realname = realname;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.phoneNum);
                dest.writeString(this.baseUserId);
                dest.writeString(this.realname);
            }

            public DirectorListBean() {
            }

            protected DirectorListBean(Parcel in) {
                this.phoneNum = in.readString();
                this.baseUserId = in.readString();
                this.realname = in.readString();
            }

            public static final Creator<DirectorListBean> CREATOR = new Creator<DirectorListBean>() {
                @Override
                public DirectorListBean createFromParcel(Parcel source) {
                    return new DirectorListBean(source);
                }

                @Override
                public DirectorListBean[] newArray(int size) {
                    return new DirectorListBean[size];
                }
            };
        }

        public static class ReceiveListBean implements Parcelable {
            private String clsSchoolId;
            private String schoolName;
            private int studentNum;
            private String teacherPhone;
            private String classroomId;
            private String teacherName;
            private String teacherId;
            private String classroomName;

            public String getClsSchoolId() {
                return clsSchoolId;
            }

            public void setClsSchoolId(String clsSchoolId) {
                this.clsSchoolId = clsSchoolId;
            }

            public String getSchoolName() {
                return schoolName;
            }

            public void setSchoolName(String schoolName) {
                this.schoolName = schoolName;
            }

            public int getStudentNum() {
                return studentNum;
            }

            public void setStudentNum(int studentNum) {
                this.studentNum = studentNum;
            }

            public String getTeacherPhone() {
                return teacherPhone;
            }

            public void setTeacherPhone(String teacherPhone) {
                this.teacherPhone = teacherPhone;
            }

            public String getClassroomId() {
                return classroomId;
            }

            public void setClassroomId(String classroomId) {
                this.classroomId = classroomId;
            }

            public String getTeacherName() {
                return teacherName;
            }

            public void setTeacherName(String teacherName) {
                this.teacherName = teacherName;
            }

            public String getTeacherId() {
                return teacherId;
            }

            public void setTeacherId(String teacherId) {
                this.teacherId = teacherId;
            }

            public String getClassroomName() {
                return classroomName;
            }

            public void setClassroomName(String classroomName) {
                this.classroomName = classroomName;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.clsSchoolId);
                dest.writeString(this.schoolName);
                dest.writeInt(this.studentNum);
                dest.writeString(this.teacherPhone);
                dest.writeString(this.classroomId);
                dest.writeString(this.teacherName);
                dest.writeString(this.teacherId);
                dest.writeString(this.classroomName);
            }

            public ReceiveListBean() {
            }

            protected ReceiveListBean(Parcel in) {
                this.clsSchoolId = in.readString();
                this.schoolName = in.readString();
                this.studentNum = in.readInt();
                this.teacherPhone = in.readString();
                this.classroomId = in.readString();
                this.teacherName = in.readString();
                this.teacherId = in.readString();
                this.classroomName = in.readString();
            }

            public static final Creator<ReceiveListBean> CREATOR = new Creator<ReceiveListBean>() {
                @Override
                public ReceiveListBean createFromParcel(Parcel source) {
                    return new ReceiveListBean(source);
                }

                @Override
                public ReceiveListBean[] newArray(int size) {
                    return new ReceiveListBean[size];
                }
            };
        }

        public ScheduleListBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mainClassroomName);
            dest.writeString(this.status);
            dest.writeString(this.classIds);
            dest.writeString(this.mainSchoolName);
            dest.writeString(this.classNames);
            dest.writeString(this.classLevelId);
            dest.writeString(this.teacherPhone);
            dest.writeInt(this.daySeq);
            dest.writeString(this.teacherName);
            dest.writeString(this.relatedClass);
            dest.writeString(this.subjectName);
            dest.writeString(this.classLevelName);
            dest.writeString(this.subjectId);
            dest.writeInt(this.classSeq);
            dest.writeString(this.teacherId);
            dest.writeString(this.detailId);
            dest.writeString(this.courseStartTime);
            dest.writeString(this.courseEndTime);
            dest.writeByte(this.isAdd ? (byte) 1 : (byte) 0);
            dest.writeTypedList(this.listenClassList);
            dest.writeTypedList(this.directorList);
            dest.writeTypedList(this.receiveList);
        }

        protected ScheduleListBean(Parcel in) {
            this.mainClassroomName = in.readString();
            this.status = in.readString();
            this.classIds = in.readString();
            this.mainSchoolName = in.readString();
            this.classNames = in.readString();
            this.classLevelId = in.readString();
            this.teacherPhone = in.readString();
            this.daySeq = in.readInt();
            this.teacherName = in.readString();
            this.relatedClass = in.readString();
            this.subjectName = in.readString();
            this.classLevelName = in.readString();
            this.subjectId = in.readString();
            this.classSeq = in.readInt();
            this.teacherId = in.readString();
            this.detailId = in.readString();
            this.courseStartTime = in.readString();
            this.courseEndTime = in.readString();
            this.isAdd = in.readByte() != 0;
            this.listenClassList = in.createTypedArrayList(ListenClassListBean.CREATOR);
            this.directorList = in.createTypedArrayList(DirectorListBean.CREATOR);
            this.receiveList = in.createTypedArrayList(ReceiveListBean.CREATOR);
        }

        public static final Creator<ScheduleListBean> CREATOR = new Creator<ScheduleListBean>() {
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
}

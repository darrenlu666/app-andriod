package com.codyy.erpsportal.reservation.models.entities;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.List;

/**
 * Created by kmdai on 16-8-9.
 */
public class ReservationDetial implements Parcelable {

    /**
     * result : success
     * list : {"beginTime":1470672000000,"classlevelName":"王刚学校一年级","classroomName":"主讲教室5","contact":"18299999999","directorList":[{"directorPhone":"13844445555","directorUserName":"王刚"}],"lessonClass":[{"baseClasslevelName":"王刚学校一年级","lessonClassNames":"二班,一班"}],"lessonClassNames":"王刚学校一年级二班,一班","realBeginTime":1470797177872,"realEndTime":1470797177872,"receiveList":[{"classroomName":"主讲教室2","contact":"110","helpUserName":"wgschooltea1","lessonClassNames":"王刚学校一年级二班,一班","schoolName":"王刚学校","status":"END","stuNum":57}],"schoolName":"王刚学校","speakUserName":"wJwTea","status":"END","subjectName":"王刚学校数学","totalMinite":286}
     */

    private String result;
    /**
     * beginTime : 1470672000000
     * classlevelName : 王刚学校一年级
     * classroomName : 主讲教室5
     * contact : 18299999999
     * directorList : [{"directorPhone":"13844445555","directorUserName":"王刚"}]
     * lessonClass : [{"baseClasslevelName":"王刚学校一年级","lessonClassNames":"二班,一班"}]
     * lessonClassNames : 王刚学校一年级二班,一班
     * realBeginTime : 1470797177872
     * realEndTime : 1470797177872
     * receiveList : [{"classroomName":"主讲教室2","contact":"110","helpUserName":"wgschooltea1","lessonClassNames":"王刚学校一年级二班,一班","schoolName":"王刚学校","status":"END","stuNum":57}]
     * schoolName : 王刚学校
     * speakUserName : wJwTea
     * status : END
     * subjectName : 王刚学校数学
     * totalMinite : 286
     */

    private ListBean list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public static class ListBean implements Parcelable {
        private String strBeginTime;
        private String classlevelName;
        private String classroomName;
        private String contact;
        private String lessonClassNames;
        private String schoolName;
        private String speakUserName;
        private String status;
        private String subjectName;

        public String getStrBeginTime() {
            return strBeginTime;
        }

        public void setStrBeginTime(String strBeginTime) {
            this.strBeginTime = strBeginTime;
        }

        /**
         * directorPhone : 13844445555
         * directorUserName : 王刚
         */

        private List<DirectorListBean> directorList;
        /**
         * baseClasslevelName : 王刚学校一年级
         * lessonClassNames : 二班,一班
         */

        private List<LessonClassBean> lessonClass;
        /**
         * classroomName : 主讲教室2
         * contact : 110
         * helpUserName : wgschooltea1
         * lessonClassNames : 王刚学校一年级二班,一班
         * schoolName : 王刚学校
         * status : END
         * stuNum : 57
         */

        private List<ReceiveListBean> receiveList;


        public String getClasslevelName() {
            return classlevelName;
        }

        public void setClasslevelName(String classlevelName) {
            this.classlevelName = classlevelName;
        }

        public String getClassroomName() {
            return classroomName;
        }

        public void setClassroomName(String classroomName) {
            this.classroomName = classroomName;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getLessonClassNames() {
            return lessonClassNames;
        }

        public void setLessonClassNames(String lessonClassNames) {
            this.lessonClassNames = lessonClassNames;
        }


        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSpeakUserName() {
            return speakUserName;
        }

        public void setSpeakUserName(String speakUserName) {
            this.speakUserName = speakUserName;
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


        public List<DirectorListBean> getDirectorList() {
            return directorList;
        }

        public void setDirectorList(List<DirectorListBean> directorList) {
            this.directorList = directorList;
        }

        public List<LessonClassBean> getLessonClass() {
            return lessonClass;
        }

        public void setLessonClass(List<LessonClassBean> lessonClass) {
            this.lessonClass = lessonClass;
        }

        public List<ReceiveListBean> getReceiveList() {
            return receiveList;
        }

        public void setReceiveList(List<ReceiveListBean> receiveList) {
            this.receiveList = receiveList;
        }

        public static class DirectorListBean implements Parcelable {
            private String directorPhone;
            private String directorUserName;

            public String getDirectorPhone() {
                return directorPhone;
            }

            public void setDirectorPhone(String directorPhone) {
                this.directorPhone = directorPhone;
            }

            public String getDirectorUserName() {
                return directorUserName;
            }

            public void setDirectorUserName(String directorUserName) {
                this.directorUserName = directorUserName;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.directorPhone);
                dest.writeString(this.directorUserName);
            }

            public DirectorListBean() {
            }

            protected DirectorListBean(Parcel in) {
                this.directorPhone = in.readString();
                this.directorUserName = in.readString();
            }

            public static final Parcelable.Creator<DirectorListBean> CREATOR = new Parcelable.Creator<DirectorListBean>() {
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

        public static class LessonClassBean implements Parcelable {
            private String baseClasslevelName;
            private String lessonClassNames;

            public String getBaseClasslevelName() {
                return baseClasslevelName;
            }

            public void setBaseClasslevelName(String baseClasslevelName) {
                this.baseClasslevelName = baseClasslevelName;
            }

            public String getLessonClassNames() {
                return lessonClassNames;
            }

            public void setLessonClassNames(String lessonClassNames) {
                this.lessonClassNames = lessonClassNames;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.baseClasslevelName);
                dest.writeString(this.lessonClassNames);
            }

            public LessonClassBean() {
            }

            protected LessonClassBean(Parcel in) {
                this.baseClasslevelName = in.readString();
                this.lessonClassNames = in.readString();
            }

            public static final Parcelable.Creator<LessonClassBean> CREATOR = new Parcelable.Creator<LessonClassBean>() {
                @Override
                public LessonClassBean createFromParcel(Parcel source) {
                    return new LessonClassBean(source);
                }

                @Override
                public LessonClassBean[] newArray(int size) {
                    return new LessonClassBean[size];
                }
            };
        }

        public static class ReceiveListBean implements Parcelable {
            private String classroomName;
            private String contact;
            private String helpUserName;
            private String lessonClassNames;
            private String schoolName;
            private String status;
            private int stuNum;

            public String getClassroomName() {
                return classroomName;
            }

            public void setClassroomName(String classroomName) {
                this.classroomName = classroomName;
            }

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getHelpUserName() {
                return helpUserName;
            }

            public void setHelpUserName(String helpUserName) {
                this.helpUserName = helpUserName;
            }

            public String getLessonClassNames() {
                return lessonClassNames;
            }

            public void setLessonClassNames(String lessonClassNames) {
                this.lessonClassNames = lessonClassNames;
            }

            public String getSchoolName() {
                return schoolName;
            }

            public void setSchoolName(String schoolName) {
                this.schoolName = schoolName;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getStuNum() {
                return stuNum;
            }

            public void setStuNum(int stuNum) {
                this.stuNum = stuNum;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.classroomName);
                dest.writeString(this.contact);
                dest.writeString(this.helpUserName);
                dest.writeString(this.lessonClassNames);
                dest.writeString(this.schoolName);
                dest.writeString(this.status);
                dest.writeInt(this.stuNum);
            }

            public ReceiveListBean() {
            }

            protected ReceiveListBean(Parcel in) {
                this.classroomName = in.readString();
                this.contact = in.readString();
                this.helpUserName = in.readString();
                this.lessonClassNames = in.readString();
                this.schoolName = in.readString();
                this.status = in.readString();
                this.stuNum = in.readInt();
            }

            public static final Parcelable.Creator<ReceiveListBean> CREATOR = new Parcelable.Creator<ReceiveListBean>() {
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

        public ListBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.strBeginTime);
            dest.writeString(this.classlevelName);
            dest.writeString(this.classroomName);
            dest.writeString(this.contact);
            dest.writeString(this.lessonClassNames);
            dest.writeString(this.schoolName);
            dest.writeString(this.speakUserName);
            dest.writeString(this.status);
            dest.writeString(this.subjectName);
            dest.writeTypedList(this.directorList);
            dest.writeTypedList(this.lessonClass);
            dest.writeTypedList(this.receiveList);
        }

        protected ListBean(Parcel in) {
            this.strBeginTime = in.readString();
            this.classlevelName = in.readString();
            this.classroomName = in.readString();
            this.contact = in.readString();
            this.lessonClassNames = in.readString();
            this.schoolName = in.readString();
            this.speakUserName = in.readString();
            this.status = in.readString();
            this.subjectName = in.readString();
            this.directorList = in.createTypedArrayList(DirectorListBean.CREATOR);
            this.lessonClass = in.createTypedArrayList(LessonClassBean.CREATOR);
            this.receiveList = in.createTypedArrayList(ReceiveListBean.CREATOR);
        }

        public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
            @Override
            public ListBean createFromParcel(Parcel source) {
                return new ListBean(source);
            }

            @Override
            public ListBean[] newArray(int size) {
                return new ListBean[size];
            }
        };

        public void onPhoneClick(View view, String phone) {
            if (phone != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeParcelable(this.list, flags);
    }

    public ReservationDetial() {
    }

    protected ReservationDetial(Parcel in) {
        this.result = in.readString();
        this.list = in.readParcelable(ListBean.class.getClassLoader());
    }

    public static final Creator<ReservationDetial> CREATOR = new Creator<ReservationDetial>() {
        @Override
        public ReservationDetial createFromParcel(Parcel source) {
            return new ReservationDetial(source);
        }

        @Override
        public ReservationDetial[] newArray(int size) {
            return new ReservationDetial[size];
        }
    };
}

package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/10.
 */
public class PrepareLessonsDetailEntity implements Parcelable {


    /**
     * result : success
     * receive : ["s","s","s","s","s"]
     * data : {"sponsorDate":"2015-07-29 09:40:07","endDate":"2015-07-31 09:39:00","myScore":0,"recClassroomId":"sss","sponsorName":"宇宙","description":"dfgsdfg","scoreType":"ssss","fileFlag":"ssss","title":"尽快缓解客流","endDateTime":1438306740000,"averageScore":0,"beginDate":"2015-08-13 09:00:57","startDateTime":1438220340000,"sponsorDateTime":1438134007011,"preparationId":"41d9cfecc98b46788bf59ea2a76cb9fa","beginDateTime":1439427657667,"classLevelName":"sss","startDate":"2015-07-30 09:39:00","status":"PROGRESS","subjectName":"ssss","recUserId":"sssss"}
     * masterSchool : [{"masterteacher":"宇宙","masterclassroom":"sss","participator":"","schoolName":"中国江苏苏州苏州园区宇宙小学"}]
     * participator : ["s","s","s","s","s"]
     * permission : {"hasPermission":false}
     * alternateachievement : ["s","s","s","s","s"]
     * interrelatedDoc : ["868559356.png","BUG阿萨德_1532_Carter, Angela - Burning Your Boats (Short Story Collection)(1).doc","27123456.iididididid.gif","捕获.PNG"]
     */
    private String result;
    private List<String> receive;
    private DataEntity data;
    private MasterSchoolEntity masterSchool;
    private List<ParticipatorItem> participator;
    private List<ReceiveSchoolItem> receiveSchoolItems;
    private PermissionEntity permission;
    private List<String> alternateachievement;
    private List<String> interrelatedDoc;
    private List<MeetMember> meetMembers;
    /**
     * 0：主讲人 1：发言人 2：参会者 3：观摩者 5:其他
     */
    private String userType;
    private boolean hasVideo;

    public List<MeetMember> getMeetMembers() {
        return meetMembers;
    }

    public void setMeetMembers(List<MeetMember> meetMembers) {
        this.meetMembers = meetMembers;
    }

    public boolean getHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean b) {
        this.hasVideo = b;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<ReceiveSchoolItem> getReceiveSchoolItems() {
        return receiveSchoolItems;
    }

    public void setReceiveSchoolItems(List<ReceiveSchoolItem> receiveSchoolItems) {
        this.receiveSchoolItems = receiveSchoolItems;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setReceive(List<String> receive) {
        this.receive = receive;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setMasterSchool(MasterSchoolEntity masterSchool) {
        this.masterSchool = masterSchool;
    }

    public void setParticipator(List<ParticipatorItem> participator) {
        this.participator = participator;
    }

    public void setPermission(PermissionEntity permission) {
        this.permission = permission;
    }

    public void setAlternateachievement(List<String> alternateachievement) {
        this.alternateachievement = alternateachievement;
    }

    public void setInterrelatedDoc(List<String> interrelatedDoc) {
        this.interrelatedDoc = interrelatedDoc;
    }

    public String getResult() {
        return result;
    }

    public List<String> getReceive() {
        return receive;
    }

    public DataEntity getData() {
        return data;
    }

    public MasterSchoolEntity getMasterSchool() {
        return masterSchool;
    }

    public List<ParticipatorItem> getParticipator() {
        return participator;
    }

    public PermissionEntity getPermission() {
        return permission;
    }

    public List<String> getAlternateachievement() {
        return alternateachievement;
    }

    public List<String> getInterrelatedDoc() {
        return interrelatedDoc;
    }

    public static class DataEntity implements Parcelable {

        /**
         * sponsorDate : 2015-07-29 09:40:07
         * endDate : 2015-07-31 09:39:00
         * myScore : 0
         * recClassroomId : sss
         * sponsorName : 宇宙
         * description : dfgsdfg
         * scoreType : ssss
         * fileFlag : ssss
         * title : 尽快缓解客流
         * endDateTime : 1438306740000
         * averageScore : 0
         * beginDate : 2015-08-13 09:00:57
         * startDateTime : 1438220340000
         * sponsorDateTime : 1438134007011
         * preparationId : 41d9cfecc98b46788bf59ea2a76cb9fa
         * beginDateTime : 1439427657667
         * classLevelName : sss
         * startDate : 2015-07-30 09:39:00
         * status : PROGRESS
         * subjectName : ssss
         * recUserId : sssss
         */
        private String sponsorDate;
        private String endDate;
        private int myScore;
        private String recClassroomId;
        private String sponsorName;
        private String description;
        private String fileFlag;
        private String title;
        private long endDateTime;
        private int averageScore;
        private String beginDate;
        private long startDateTime;
        private long sponsorDateTime;
        private String preparationId;
        private long beginDateTime;
        private String classLevelName;
        private String startDate;
        private String status;
        private String subjectName;
        private String recUserId;
        private String finishDate;
        private long duration;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void setSponsorDate(String sponsorDate) {
            this.sponsorDate = sponsorDate;
        }

        public String getFinishDate() {
            return finishDate;
        }

        public void setFinishDate(String finishDate) {
            this.finishDate = finishDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public void setMyScore(int myScore) {
            this.myScore = myScore;
        }

        public void setRecClassroomId(String recClassroomId) {
            this.recClassroomId = recClassroomId;
        }

        public void setSponsorName(String sponsorName) {
            this.sponsorName = sponsorName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setFileFlag(String fileFlag) {
            this.fileFlag = fileFlag;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setEndDateTime(long endDateTime) {
            this.endDateTime = endDateTime;
        }

        public void setAverageScore(int averageScore) {
            this.averageScore = averageScore;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public void setStartDateTime(long startDateTime) {
            this.startDateTime = startDateTime;
        }

        public void setSponsorDateTime(long sponsorDateTime) {
            this.sponsorDateTime = sponsorDateTime;
        }

        public void setPreparationId(String preparationId) {
            this.preparationId = preparationId;
        }

        public void setBeginDateTime(long beginDateTime) {
            this.beginDateTime = beginDateTime;
        }

        public void setClassLevelName(String classLevelName) {
            this.classLevelName = classLevelName;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public void setRecUserId(String recUserId) {
            this.recUserId = recUserId;
        }

        public String getSponsorDate() {
            return sponsorDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public int getMyScore() {
            return myScore;
        }

        public String getRecClassroomId() {
            return recClassroomId;
        }

        public String getSponsorName() {
            return sponsorName;
        }

        public String getDescription() {
            return description;
        }

        public String getFileFlag() {
            return fileFlag;
        }

        public String getTitle() {
            return title;
        }

        public long getEndDateTime() {
            return endDateTime;
        }

        public int getAverageScore() {
            return averageScore;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public long getStartDateTime() {
            return startDateTime;
        }

        public long getSponsorDateTime() {
            return sponsorDateTime;
        }

        public String getPreparationId() {
            return preparationId;
        }

        public long getBeginDateTime() {
            return beginDateTime;
        }

        public String getClassLevelName() {
            return classLevelName;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getStatus() {
            return status;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public String getRecUserId() {
            return recUserId;
        }

        public DataEntity() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.sponsorDate);
            dest.writeString(this.endDate);
            dest.writeInt(this.myScore);
            dest.writeString(this.recClassroomId);
            dest.writeString(this.sponsorName);
            dest.writeString(this.description);
            dest.writeString(this.fileFlag);
            dest.writeString(this.title);
            dest.writeLong(this.endDateTime);
            dest.writeInt(this.averageScore);
            dest.writeString(this.beginDate);
            dest.writeLong(this.startDateTime);
            dest.writeLong(this.sponsorDateTime);
            dest.writeString(this.preparationId);
            dest.writeLong(this.beginDateTime);
            dest.writeString(this.classLevelName);
            dest.writeString(this.startDate);
            dest.writeString(this.status);
            dest.writeString(this.subjectName);
            dest.writeString(this.recUserId);
            dest.writeString(this.finishDate);
            dest.writeLong(this.duration);
        }

        protected DataEntity(Parcel in) {
            this.sponsorDate = in.readString();
            this.endDate = in.readString();
            this.myScore = in.readInt();
            this.recClassroomId = in.readString();
            this.sponsorName = in.readString();
            this.description = in.readString();
            this.fileFlag = in.readString();
            this.title = in.readString();
            this.endDateTime = in.readLong();
            this.averageScore = in.readInt();
            this.beginDate = in.readString();
            this.startDateTime = in.readLong();
            this.sponsorDateTime = in.readLong();
            this.preparationId = in.readString();
            this.beginDateTime = in.readLong();
            this.classLevelName = in.readString();
            this.startDate = in.readString();
            this.status = in.readString();
            this.subjectName = in.readString();
            this.recUserId = in.readString();
            this.finishDate = in.readString();
            this.duration = in.readLong();
        }

        public static final Creator<DataEntity> CREATOR = new Creator<DataEntity>() {
            @Override
            public DataEntity createFromParcel(Parcel source) {
                return new DataEntity(source);
            }

            @Override
            public DataEntity[] newArray(int size) {
                return new DataEntity[size];
            }
        };
    }

    /**
     * 主讲学校
     */
    public static class MasterSchoolEntity extends BaseTitleItemBar implements Parcelable {
        /**
         * schoolName : 中国江苏苏州苏州园区宇宙小学
         * masterclassroom : 备课室1
         * masterteacher : 王老师
         * participator : 张三、李四、王五、赵六
         */
        private String masterteacher;
        private String masterclassroom;
        private String participator;
        private String schoolName;

        private boolean selfSchool;

        public boolean isSelfSchool() {
            return selfSchool;
        }

        public void setSelfSchool(boolean selfSchool) {
            this.selfSchool = selfSchool;
        }

        public void setMasterteacher(String masterteacher) {
            this.masterteacher = masterteacher;
        }

        public void setMasterclassroom(String masterclassroom) {
            this.masterclassroom = masterclassroom;
        }

        public void setParticipator(String participator) {
            this.participator = participator;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getMasterteacher() {
            return masterteacher;
        }

        public String getMasterclassroom() {
            return masterclassroom;
        }

        public String getParticipator() {
            return participator;
        }

        public String getSchoolName() {
            return schoolName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.masterteacher);
            dest.writeString(this.masterclassroom);
            dest.writeString(this.participator);
            dest.writeString(this.schoolName);
            dest.writeByte(selfSchool ? (byte) 1 : (byte) 0);
        }

        public MasterSchoolEntity() {
        }

        protected MasterSchoolEntity(Parcel in) {
            this.masterteacher = in.readString();
            this.masterclassroom = in.readString();
            this.participator = in.readString();
            this.schoolName = in.readString();
            this.selfSchool = in.readByte() != 0;
        }

        public static final Creator<MasterSchoolEntity> CREATOR = new Creator<MasterSchoolEntity>() {
            @Override
            public MasterSchoolEntity createFromParcel(Parcel source) {
                return new MasterSchoolEntity(source);
            }

            @Override
            public MasterSchoolEntity[] newArray(int size) {
                return new MasterSchoolEntity[size];
            }
        };
    }

    /**
     * 参与机构/接收学校
     */
    public static class ParticipatorItem extends BaseTitleItemBar implements Parcelable {

        /**
         * classroom : 备课室1
         * participator : monitor1
         * schoolName : 中国江苏苏州金鸡湖/%小学
         */
        private String classroom;
        private String participator;
        private String schoolName;
        private boolean selfSchool;

        public boolean isSelfSchool() {
            return selfSchool;
        }

        public void setSelfSchool(boolean selfSchool) {
            this.selfSchool = selfSchool;
        }

        public void setClassroom(String classroom) {
            this.classroom = classroom;
        }

        public void setParticipator(String participator) {
            this.participator = participator;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getClassroom() {
            return classroom;
        }

        public String getParticipator() {
            return participator;
        }

        public String getSchoolName() {
            return schoolName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.classroom);
            dest.writeString(this.participator);
            dest.writeString(this.schoolName);
            dest.writeByte(selfSchool ? (byte) 1 : (byte) 0);
        }

        public ParticipatorItem() {
        }

        protected ParticipatorItem(Parcel in) {
            this.classroom = in.readString();
            this.participator = in.readString();
            this.schoolName = in.readString();
            this.selfSchool = in.readByte() != 0;
        }

        public static final Creator<ParticipatorItem> CREATOR = new Creator<ParticipatorItem>() {
            @Override
            public ParticipatorItem createFromParcel(Parcel source) {
                return new ParticipatorItem(source);
            }

            @Override
            public ParticipatorItem[] newArray(int size) {
                return new ParticipatorItem[size];
            }
        };
    }

    /**
     * 接收学校
     */
    public static class ReceiveSchoolItem extends BaseTitleItemBar implements Parcelable {

        /**
         * classroom :
         * participator : monitor1
         * schoolName : 中国江苏苏州金鸡湖/%小学
         */
        private String receiveteacher;
        private String receiveclassroom;
        private String schoolName;

        public String getReceiveclassroom() {
            return receiveclassroom;
        }

        public void setReceiveclassroom(String receiveclassroom) {
            this.receiveclassroom = receiveclassroom;
        }

        public String getReceiveteacher() {
            return receiveteacher;
        }

        public void setReceiveteacher(String receiveteacher) {
            this.receiveteacher = receiveteacher;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.receiveteacher);
            dest.writeString(this.receiveclassroom);
            dest.writeString(this.schoolName);
        }

        public ReceiveSchoolItem() {
        }

        protected ReceiveSchoolItem(Parcel in) {
            this.receiveteacher = in.readString();
            this.receiveclassroom = in.readString();
            this.schoolName = in.readString();
        }

        public static final Creator<ReceiveSchoolItem> CREATOR = new Creator<ReceiveSchoolItem>() {
            @Override
            public ReceiveSchoolItem createFromParcel(Parcel source) {
                return new ReceiveSchoolItem(source);
            }

            @Override
            public ReceiveSchoolItem[] newArray(int size) {
                return new ReceiveSchoolItem[size];
            }
        };
    }

    public static class PermissionEntity implements Parcelable {
        /**
         * hasPermission : false
         */
        private boolean hasPermission;
        private String className;
        private String schoolName;

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public void setHasPermission(boolean hasPermission) {
            this.hasPermission = hasPermission;
        }

        public boolean isHasPermission() {
            return hasPermission;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(hasPermission ? (byte) 1 : (byte) 0);
            dest.writeString(this.className);
            dest.writeString(this.schoolName);
        }

        public PermissionEntity() {
        }

        protected PermissionEntity(Parcel in) {
            this.hasPermission = in.readByte() != 0;
            this.className = in.readString();
            this.schoolName = in.readString();
        }

        public static final Creator<PermissionEntity> CREATOR = new Creator<PermissionEntity>() {
            @Override
            public PermissionEntity createFromParcel(Parcel source) {
                return new PermissionEntity(source);
            }

            @Override
            public PermissionEntity[] newArray(int size) {
                return new PermissionEntity[size];
            }
        };
    }


    public static PrepareLessonsDetailEntity parseJson(JSONObject jsonObject, String type) {
        PrepareLessonsDetailEntity prepareLessonsDetailEntity = new PrepareLessonsDetailEntity();
        JSONArray participator = null;
        List<ParticipatorItem> participatorList = new ArrayList<>();
        if (type.equals(Constants.TYPE_PREPARE_LESSON)) {
            participator = jsonObject.optJSONArray("participator");
            //参会者
            for (int i = 0; i < participator.length(); i++) {
                JSONObject data = participator.optJSONObject(i);
                ParticipatorItem participatorItem = new ParticipatorItem();
                participatorItem.setClassroom(getStr(data.optString("classroom")));
                participatorItem.setParticipator(getStr(data.optString("participator")));
                participatorItem.setSchoolName(getStr(data.optString("schoolName")));
                participatorItem.setSelfSchool(data.optBoolean("selfSchool"));
                participatorList.add(participatorItem);
            }
        } else {
            participator = jsonObject.optJSONArray("listenSchool");
            for (int i = 0; i < participator.length(); i++) {
                JSONObject data = participator.optJSONObject(i);
                ParticipatorItem participatorItem = new ParticipatorItem();
                participatorItem.setClassroom(getStr(data.optString("listenClassroom")));
                participatorItem.setParticipator(getStr(data.optString("listenTeacher")));
                participatorItem.setSchoolName(getStr(data.optString("listenSchoolName")));
                participatorItem.setSelfSchool(data.optBoolean("selfSchool"));
                participatorList.add(participatorItem);
            }
            List<ReceiveSchoolItem> receiveSchoolItems = new ArrayList<>();
            JSONArray receiveSchool = jsonObject.optJSONArray("receiveSchool");
            for (int i = 0; i < receiveSchool.length(); i++) {
                JSONObject data = receiveSchool.optJSONObject(i);
                ReceiveSchoolItem receiveSchoolItem = new ReceiveSchoolItem();
                receiveSchoolItem.setReceiveclassroom(getStr(data.optString("receiveclassroom")));
                receiveSchoolItem.setReceiveteacher(getStr(data.optString("receiveteacher")));
                receiveSchoolItem.setSchoolName(getStr(data.optString("schoolName")));
                receiveSchoolItems.add(receiveSchoolItem);
            }
            prepareLessonsDetailEntity.setReceiveSchoolItems(receiveSchoolItems);
        }


        prepareLessonsDetailEntity.setParticipator(participatorList);
        prepareLessonsDetailEntity.setUserType(getStr(jsonObject.optString("userType")));
        prepareLessonsDetailEntity.setHasVideo(jsonObject.optBoolean("hasVideo"));
        //相关文档
        JSONArray interrelatedDoc = jsonObject.optJSONArray("interrelatedDoc");
        if(null != interrelatedDoc){
            List<String> interrelatedDocList = new ArrayList<>();
            for (int i = 0; i < interrelatedDoc.length(); i++) {
                interrelatedDocList.add(interrelatedDoc.optString(i));
            }
            prepareLessonsDetailEntity.setInterrelatedDoc(interrelatedDocList);
        }
        //备课成果
        JSONArray alternateachievement = jsonObject.optJSONArray("alternateachievement");
        if(null != alternateachievement){
            List<String> alternateachievementList = new ArrayList<>();
            for (int i = 0; i < alternateachievement.length(); i++) {
                alternateachievementList.add(alternateachievement.optString(i));
            }
            prepareLessonsDetailEntity.setAlternateachievement(alternateachievementList);
        }
        JSONArray meetArray = jsonObject.optJSONArray("meetMember");
        if(null != meetArray){
            ArrayList<MeetMember> meetMembers = new ArrayList<>();
            if (meetArray != null) {
                for (int i = 0; i < meetArray.length(); i++) {
                    JSONObject object = meetArray.optJSONObject(i);
                    MeetMember meetMember = new MeetMember();
                    meetMember.setMemberType(object.optString("memberType"));
                    meetMember.setBaseUserId(object.optString("baseUserId"));
                    meetMember.setRealName(object.optString("realName"));
                    meetMembers.add(meetMember);

                }
            }
            prepareLessonsDetailEntity.setMeetMembers(meetMembers);
        }

        JSONObject data = jsonObject.optJSONObject("data");
        DataEntity dataEntity = new DataEntity();
        dataEntity.setStartDate(data.optString(getStr("startDate")));
        dataEntity.setBeginDateTime(data.optLong("beginDateTime", 0));
        dataEntity.setAverageScore(data.optInt("averageScore", 0));
        dataEntity.setStartDateTime(data.optLong("startDateTime", 0));
        dataEntity.setStatus(data.optString("status"));
        dataEntity.setFileFlag(data.optString("fileFlag"));
        dataEntity.setClassLevelName(getStr(data.optString("classLevelName")));
        dataEntity.setRecClassroomId(data.optString("recClassroomId"));
        dataEntity.setPreparationId(data.optString("preparationId"));
        dataEntity.setEndDate(getStr(data.optString("endDate")));
        dataEntity.setMyScore(data.optInt("myScore", 0));
        dataEntity.setRecUserId(data.optString("recUserId"));
        dataEntity.setFinishDate(data.optString("finishDate"));
        dataEntity.setBeginDate(getStr(data.optString("beginDate")));
        dataEntity.setSponsorDate(data.optString("sponsorDate"));
        dataEntity.setEndDateTime(data.optLong("endDateTime", 0));
        dataEntity.setTitle(getStr(data.optString("title")));
        dataEntity.setSubjectName(getStr(data.optString("subjectName")));
        dataEntity.setSponsorDateTime(data.optLong("sponsorDateTime", 0));
        dataEntity.setDescription(getStr(data.optString("description")));
        dataEntity.setSponsorName(getStr(data.optString("sponsorName")));
        dataEntity.setDuration(data.optLong("duration", 0));
        prepareLessonsDetailEntity.setData(dataEntity);

        JSONObject permission = jsonObject.optJSONObject("permission");
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setHasPermission(permission.optBoolean("hasPermission"));
        permissionEntity.setSchoolName(getStr(permission.optString("schoolName")));
        permissionEntity.setClassName(getStr(permission.optString("className")));
        prepareLessonsDetailEntity.setPermission(permissionEntity);

        JSONObject masterSchool = jsonObject.optJSONObject("masterSchool");
        MasterSchoolEntity masterSchoolEntity = new MasterSchoolEntity();
        masterSchoolEntity.setMasterclassroom(getStr(masterSchool.optString("masterclassroom")));
        masterSchoolEntity.setMasterteacher(getStr(masterSchool.optString("masterteacher")));
        masterSchoolEntity.setParticipator(getStr(masterSchool.optString("participator")));
        masterSchoolEntity.setSchoolName(getStr(masterSchool.optString("schoolName")));
        masterSchoolEntity.setSelfSchool(masterSchool.optBoolean("selfSchool"));
        prepareLessonsDetailEntity.setMasterSchool(masterSchoolEntity);

        return prepareLessonsDetailEntity;
    }

    /**
     * meetMember: [
     * {
     * baseUserId: null,
     * memberType: null,
     * realName: "国家学校"
     * },
     * {
     * baseUserId: null,
     * memberType: null,
     * realName: "国家老师"
     * }
     * ]
     */
    public static class MeetMember implements Parcelable {

        /**
         * baseUserId : null
         * memberType : null
         * realName : 国家学校
         */

        private String baseUserId;
        private String memberType;
        private String realName;

        public String getBaseUserId() {
            return baseUserId;
        }

        public void setBaseUserId(String baseUserId) {
            this.baseUserId = baseUserId;
        }

        public String getMemberType() {
            return memberType;
        }

        public void setMemberType(String memberType) {
            this.memberType = memberType;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public MeetMember() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.baseUserId);
            dest.writeString(this.memberType);
            dest.writeString(this.realName);
        }

        protected MeetMember(Parcel in) {
            this.baseUserId = in.readString();
            this.memberType = in.readString();
            this.realName = in.readString();
        }

        public static final Creator<MeetMember> CREATOR = new Creator<MeetMember>() {
            @Override
            public MeetMember createFromParcel(Parcel source) {
                return new MeetMember(source);
            }

            @Override
            public MeetMember[] newArray(int size) {
                return new MeetMember[size];
            }
        };
    }

    public static String getStr(String str) {
        if ("null".equals(str)) {
            return "无";
        }
        return str;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeStringList(this.receive);
        dest.writeParcelable(this.data, flags);
        dest.writeParcelable(this.masterSchool, flags);
        dest.writeList(this.participator);
        dest.writeList(this.receiveSchoolItems);
        dest.writeParcelable(this.permission, flags);
        dest.writeStringList(this.alternateachievement);
        dest.writeStringList(this.interrelatedDoc);
        dest.writeList(this.meetMembers);
        dest.writeString(this.userType);
        dest.writeByte(hasVideo ? (byte) 1 : (byte) 0);
    }

    public PrepareLessonsDetailEntity() {
    }

    protected PrepareLessonsDetailEntity(Parcel in) {
        this.result = in.readString();
        this.receive = in.createStringArrayList();
        this.data = in.readParcelable(DataEntity.class.getClassLoader());
        this.masterSchool = in.readParcelable(MasterSchoolEntity.class.getClassLoader());
        this.participator = new ArrayList<ParticipatorItem>();
        in.readList(this.participator, ParticipatorItem.class.getClassLoader());
        this.receiveSchoolItems = new ArrayList<ReceiveSchoolItem>();
        in.readList(this.receiveSchoolItems, ReceiveSchoolItem.class.getClassLoader());
        this.permission = in.readParcelable(PermissionEntity.class.getClassLoader());
        this.alternateachievement = in.createStringArrayList();
        this.interrelatedDoc = in.createStringArrayList();
        this.meetMembers = new ArrayList<MeetMember>();
        in.readList(this.meetMembers, MeetMember.class.getClassLoader());
        this.userType = in.readString();
        this.hasVideo = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PrepareLessonsDetailEntity> CREATOR = new Parcelable.Creator<PrepareLessonsDetailEntity>() {
        @Override
        public PrepareLessonsDetailEntity createFromParcel(Parcel source) {
            return new PrepareLessonsDetailEntity(source);
        }

        @Override
        public PrepareLessonsDetailEntity[] newArray(int size) {
            return new PrepareLessonsDetailEntity[size];
        }
    };
}

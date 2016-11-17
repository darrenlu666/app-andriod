package com.codyy.erpsportal.onlinemeetings.models.entities;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldh on 2015/9/7.
 */
public class VideoMeetingDetailEntity {

    /**
     * result : success
     * participator : [{"classroom":"接收教室1","participator":"gugu,顾伟,顾伟","schoolName":"中国江苏苏州园区顾伟学校"}]
     * meetVideoList : []
     * interrelatedDoc : [{"createTime":1440831808833,"guestName":null,"uploadUserId":"5a5f7766dfa94cc9b25c85810ff3f2e4","belongToType":"RELATED","clsClassroomId":null,"documentPath":"463a0f38-118c-494b-aaba-376da99238d5.jpg","meetingId":"4e28c81aefe547af8f1c4d0da8d71bd5","meetDocumentId":"38220a9c00fe44c49b94963e3cfb1caa","documentName":"技术中心办公座位图.jpg","locked":"N","guestFlag":"N","pageNum":0}]
     * hasVideo : false
     * alternateachievement : [{"createTime":1441508397457,"guestName":null,"uploadUserId":"5a5f7766dfa94cc9b25c85810ff3f2e4","belongToType":"RESULT","clsClassroomId":null,"documentPath":"11c27b9d-be19-4f29-90cc-dc8a5745dab1.pdf","meetingId":"4e28c81aefe547af8f1c4d0da8d71bd5","meetDocumentId":"afcd7ea568ec4fa7a48c01c40d52ad52","documentName":"项目开发流程.pdf","locked":"Y","guestFlag":"N","pageNum":0}]
     * data : {"fileFlag":null,"startDate":"2015-09-06 10:49:09","beginDateTime":1440836160000,"averageScore":0,"startDateTime":1441507749798,"status":"END","class":"com.codyy.oc.mobile.view.PrepareMeetDetail","endDate":"2015-09-05 16:16:00","myScore":0,"beginDate":"2015-08-29 16:16:00","endDateTime":1441440960000,"sponsorDate":"2015-08-29 15:03:28","scoreType":null,"id":"4e28c81aefe547af8f1c4d0da8d71bd5","mainSpeakerUserId":"5a5f7766dfa94cc9b25c85810ff3f2e4","title":"mobile_meet_0829","subjectName":null,"recUserId":null,"sponsorDateTime":1440831808827,"classLevelName":null,"sponsorName":"语文老师002","description":"mobile_meet_0829mobile_meet_0829mobile_meet_0829mobile_meet_0829mobile_meet_0829mobile_meet_0829","recClassroomId":null,"sponsorId":"5a5f7766dfa94cc9b25c85810ff3f2e4"}
     data : {"fileFlag":"Y","startDate":"2015-08-19 14:36:00","averageScore":0,"startDateTime":1439966160000,"status":"INIT","preparationId":"1c88edd54f71462398382b870a8459b8","endDate":"2015-08-20 15:34:00","myScore":0,"sponsorDate":"2015-08-03 15:35:28","endDateTime":1440056040000,"id":"1c88edd54f71462398382b870a8459b8","title":"adfasdf","sponsorDateTime":1438587328164,"sponsorName":"习大大&lt;","description":"asdfasdf","recClassroomId":"2540829376094614810360f88a207227"}
     * permission : {"hasPermission":false}
     * masterSchool : {"participator":"语文老师003,qjhadmin,语文老师001","schoolName":"中国江苏苏州园区青剑湖小学","masterteacher":"语文老师002","masterclassroom":"主讲教室1"}
     * userType : 2
     */
    private String result;
    private String userType;
    private java.util.List<ParticipatorEntity> participator;
    private java.util.List<meetVideoEntity> meetVideoList;
    private java.util.List<InterrelatedDocEntity> interrelatedDoc;
    private boolean hasVideo;
    private java.util.List<AlternateachievementEntity> alternateachievement;
    private DataEntity data;
    private PermissionEntity permission;
    private MasterSchoolEntity masterSchool;

    public void setHasVideo(boolean hasVideo){
        this.hasVideo = hasVideo;
    }

    public boolean getHasVideo(){
        return this.hasVideo;
    }

    public void setMeetVideoList(List<meetVideoEntity> meetVideoList){
        this.meetVideoList = meetVideoList;
    }


    public void setResult(String result) {
        this.result = result;
    }
    public void setUserType(String userType){
        this.userType = userType;
    }

    public void setParticipator(List<ParticipatorEntity> participator) {
        this.participator = participator;
    }

    public void setInterrelatedDoc(List<InterrelatedDocEntity> interrelatedDoc) {
        this.interrelatedDoc = interrelatedDoc;
    }

    public void setAlternateachievement(List<AlternateachievementEntity> alternateachievement) {
        this.alternateachievement = alternateachievement;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setPermission(PermissionEntity permission) {
        this.permission = permission;
    }

    public void setMasterSchool(MasterSchoolEntity masterSchool) {
        this.masterSchool = masterSchool;
    }

    public String getResult() {
        return result;
    }

    public String getUserType(){
        return userType;
    }

    public List<ParticipatorEntity> getParticipator() {
        return participator;
    }

    public List<InterrelatedDocEntity> getInterrelatedDoc() {
        return interrelatedDoc;
    }

    public List<AlternateachievementEntity> getAlternateachievement() {
        return alternateachievement;
    }

    public DataEntity getData() {
        return data;
    }

    public PermissionEntity getPermission() {
        return permission;
    }

    public MasterSchoolEntity getMasterSchool() {
        return masterSchool;
    }

    public static class ParticipatorEntity extends BaseTitleItemBar {
        /**
         * classroom : 接收教室1
         * participator : gugu,顾伟,顾伟
         * schoolName : 中国江苏苏州园区顾伟学校
         */
        private String classroom;
        private String participator;
        private String schoolName;
        private boolean selfSchool;
//        private String teacherList;
//        private String studentList;

//        public String getStudentList() {
//            return studentList;
//        }
//
//        public void setStudentList(String studentList) {
//            this.studentList = studentList;
//        }

//        public String getTeacherList() {
//            return teacherList;
//        }
//
//        public void setTeacherList(String teacherList) {
//            this.teacherList = teacherList;
//        }

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
    }

    public static class meetVideoEntity{
//        meetVideoId: "19c77d282eea4a14a4e59a29c561c56a",
//        meetingId: "5421224247a047cba9b281b90ec58557",
//        videoName: "5421224247a047cba9b281b90ec58557",
//        thumbPath: "thumbPicDefault.jpg",
//        fileStatus: "FILED",
//        localFilePath: "E:\5421224247a047cba9b281b90ec58557.flv",
//        filePath: "/data/video/a/dc/02/32/a0801f9a-e582-4cea-b895-7fb0243ff914.flv",
//        resolutionHeight: null,
//        resolutionWidth: null,
//        duration: null,
//        transFlag: "TRANS_SUCCESS",
//        storeServer: "a",
//        uploadUserId: "5a5f7766dfa94cc9b25c85810ff3f2e4",
//        createTime: 1441522531793,
//        uploadUserName: "语文老师002",
//        strDuration: "",
//        strCreateTime: "2015-09-06 14:55:31",
//        strFileStatus: "已归档"

        private String meetVideoId;
        private String meetingId;
        private String videoName;
        private String thumbPath;
        private String fileStatus;
        private String localFilePath;
        private String filePath;
        private long resolutionHeight;
        private long resolutionWidth;
        private long duration;
        private String transFlag;
        private String storeServer;
        private String uploadUserId;
        private String createTime;
        private String uploadUserName;
        private String strDuration;
        private String strCreateTime;
        private String strFileStatus;

        public String getLocalFilePath() {
            return localFilePath;
        }

        public void setLocalFilePath(String localFilePath) {
            this.localFilePath = localFilePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public long getResolutionHeight() {
            return resolutionHeight;
        }

        public void setResolutionHeight(long resolutionHeight) {
            this.resolutionHeight = resolutionHeight;
        }

        public long getResolutionWidth() {
            return resolutionWidth;
        }

        public void setResolutionWidth(long resolutionWidth) {
            this.resolutionWidth = resolutionWidth;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getTransFlag() {
            return transFlag;
        }

        public void setTransFlag(String transFlag) {
            this.transFlag = transFlag;
        }

        public String getStoreServer() {
            return storeServer;
        }

        public void setStoreServer(String storeServer) {
            this.storeServer = storeServer;
        }

        public String getUploadUserId() {
            return uploadUserId;
        }

        public void setUploadUserId(String uploadUserId) {
            this.uploadUserId = uploadUserId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUploadUserName() {
            return uploadUserName;
        }

        public void setUploadUserName(String uploadUserName) {
            this.uploadUserName = uploadUserName;
        }

        public String getStrDuration() {
            return strDuration;
        }

        public void setStrDuration(String strDuration) {
            this.strDuration = strDuration;
        }

        public String getStrCreateTime() {
            return strCreateTime;
        }

        public void setStrCreateTime(String strCreateTime) {
            this.strCreateTime = strCreateTime;
        }

        public String getStrFileStatus() {
            return strFileStatus;
        }

        public void setStrFileStatus(String strFileStatus) {
            this.strFileStatus = strFileStatus;
        }

        public String getFileStatus() {
            return fileStatus;
        }

        public void setFileStatus(String fileStatus) {
            this.fileStatus = fileStatus;
        }

        public String getThumbPath() {
            return thumbPath;
        }

        public void setThumbPath(String thumbPath) {
            this.thumbPath = thumbPath;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getMeetVideoId() {
            return meetVideoId;
        }

        public void setMeetVideoId(String meetVideoId) {
            this.meetVideoId = meetVideoId;
        }
    }

    public static class InterrelatedDocEntity {
        /**
         * createTime : 1440831808833
         * guestName : null
         * uploadUserId : 5a5f7766dfa94cc9b25c85810ff3f2e4
         * belongToType : RELATED
         * clsClassroomId : null
         * documentPath : 463a0f38-118c-494b-aaba-376da99238d5.jpg
         * meetingId : 4e28c81aefe547af8f1c4d0da8d71bd5
         * meetDocumentId : 38220a9c00fe44c49b94963e3cfb1caa
         * documentName : 技术中心办公座位图.jpg
         * locked : N
         * guestFlag : N
         * pageNum : 0
         */
        private long createTime;
        private String guestName;
        private String uploadUserId;
        private String belongToType;
        private String clsClassroomId;
        private String documentPath;
        private String meetingId;
        private String meetDocumentId;
        private String documentName;
        private String locked;
        private String guestFlag;
        private int pageNum;

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setGuestName(String guestName) {
            this.guestName = guestName;
        }

        public void setUploadUserId(String uploadUserId) {
            this.uploadUserId = uploadUserId;
        }

        public void setBelongToType(String belongToType) {
            this.belongToType = belongToType;
        }

        public void setClsClassroomId(String clsClassroomId) {
            this.clsClassroomId = clsClassroomId;
        }

        public void setDocumentPath(String documentPath) {
            this.documentPath = documentPath;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public void setMeetDocumentId(String meetDocumentId) {
            this.meetDocumentId = meetDocumentId;
        }

        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }

        public void setGuestFlag(String guestFlag) {
            this.guestFlag = guestFlag;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public long getCreateTime() {
            return createTime;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getUploadUserId() {
            return uploadUserId;
        }

        public String getBelongToType() {
            return belongToType;
        }

        public String getClsClassroomId() {
            return clsClassroomId;
        }

        public String getDocumentPath() {
            return documentPath;
        }

        public String getMeetingId() {
            return meetingId;
        }

        public String getMeetDocumentId() {
            return meetDocumentId;
        }

        public String getDocumentName() {
            return documentName;
        }

        public String getLocked() {
            return locked;
        }

        public String getGuestFlag() {
            return guestFlag;
        }

        public int getPageNum() {
            return pageNum;
        }
    }

    public static class AlternateachievementEntity {
        /**
         * createTime : 1441508397457
         * guestName : null
         * uploadUserId : 5a5f7766dfa94cc9b25c85810ff3f2e4
         * belongToType : RESULT
         * clsClassroomId : null
         * documentPath : 11c27b9d-be19-4f29-90cc-dc8a5745dab1.pdf
         * meetingId : 4e28c81aefe547af8f1c4d0da8d71bd5
         * meetDocumentId : afcd7ea568ec4fa7a48c01c40d52ad52
         * documentName : 项目开发流程.pdf
         * locked : Y
         * guestFlag : N
         * pageNum : 0
         */
        private long createTime;
        private String guestName;
        private String uploadUserId;
        private String belongToType;
        private String clsClassroomId;
        private String documentPath;
        private String meetingId;
        private String meetDocumentId;
        private String documentName;
        private String locked;
        private String guestFlag;
        private int pageNum;

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setGuestName(String guestName) {
            this.guestName = guestName;
        }

        public void setUploadUserId(String uploadUserId) {
            this.uploadUserId = uploadUserId;
        }

        public void setBelongToType(String belongToType) {
            this.belongToType = belongToType;
        }

        public void setClsClassroomId(String clsClassroomId) {
            this.clsClassroomId = clsClassroomId;
        }

        public void setDocumentPath(String documentPath) {
            this.documentPath = documentPath;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public void setMeetDocumentId(String meetDocumentId) {
            this.meetDocumentId = meetDocumentId;
        }

        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }

        public void setGuestFlag(String guestFlag) {
            this.guestFlag = guestFlag;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public long getCreateTime() {
            return createTime;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getUploadUserId() {
            return uploadUserId;
        }

        public String getBelongToType() {
            return belongToType;
        }

        public String getClsClassroomId() {
            return clsClassroomId;
        }

        public String getDocumentPath() {
            return documentPath;
        }

        public String getMeetingId() {
            return meetingId;
        }

        public String getMeetDocumentId() {
            return meetDocumentId;
        }

        public String getDocumentName() {
            return documentName;
        }

        public String getLocked() {
            return locked;
        }

        public String getGuestFlag() {
            return guestFlag;
        }

        public int getPageNum() {
            return pageNum;
        }
    }

    public static class DataEntity{
        //data : {"fileFlag":"Y","startDate":"2015-08-19 14:36:00","averageScore":0,"startDateTime":1439966160000,"status":"INIT","preparationId":"1c88edd54f71462398382b870a8459b8","endDate":"2015-08-20 15:34:00","myScore":0,"sponsorDate":"2015-08-03 15:35:28","endDateTime":1440056040000,"id":"1c88edd54f71462398382b870a8459b8","title":"adfasdf","sponsorDateTime":1438587328164,"sponsorName":"习大大&lt;","description":"asdfasdf","recClassroomId":"2540829376094614810360f88a207227"}
        private String fileFlag;
        private String startDate;
        private int averageScore;
        private long startDateTime;
        private String status;
        private String preparationId;
        private String endDate;
        private int myScore;
        private String sponsorDate;
        private long endDateTime;
        private String id;
        private String title;
        private long sponsorDateTime;
        private String sponsorName;
        private String description;
        private String recClassroomId;

        public String getBeginDateTime() {
            return beginDateTime;
        }

        public void setBeginDateTime(String beginDateTime) {
            this.beginDateTime = beginDateTime;
        }

        private String beginDateTime;

        public void setFileFlag(String fileFlag) {
            this.fileFlag = fileFlag;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public void setAverageScore(int averageScore) {
            this.averageScore = averageScore;
        }

        public void setStartDateTime(long startDateTime) {
            this.startDateTime = startDateTime;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setPreparationId(String preparationId) {
            this.preparationId = preparationId;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public void setMyScore(int myScore) {
            this.myScore = myScore;
        }

        public void setSponsorDate(String sponsorDate) {
            this.sponsorDate = sponsorDate;
        }

        public void setEndDateTime(long endDateTime) {
            this.endDateTime = endDateTime;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSponsorDateTime(long sponsorDateTime) {
            this.sponsorDateTime = sponsorDateTime;
        }

        public void setSponsorName(String sponsorName) {
            this.sponsorName = sponsorName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setRecClassroomId(String recClassroomId) {
            this.recClassroomId = recClassroomId;
        }

        public String getFileFlag() {
            return fileFlag;
        }

        public String getStartDate() {
            return startDate;
        }

        public int getAverageScore() {
            return averageScore;
        }

        public long getStartDateTime() {
            return startDateTime;
        }

        public String getStatus() {
            return status;
        }

        public String getPreparationId() {
            return preparationId;
        }

        public String getEndDate() {
            return endDate;
        }

        public int getMyScore() {
            return myScore;
        }

        public String getSponsorDate() {
            return sponsorDate;
        }

        public long getEndDateTime() {
            return endDateTime;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public long getSponsorDateTime() {
            return sponsorDateTime;
        }

        public String getSponsorName() {
            return sponsorName;
        }

        public String getDescription() {
            return description;
        }

        public String getRecClassroomId() {
            return recClassroomId;
        }
    }

    public static class PermissionEntity extends BaseTitleItemBar{
        /**
         * hasPermission : true
         * className : 主讲教室2
         */
        private boolean hasPermission;
        private String className;
        private String schoolName;
        private String teachName;

        public String getTeachName() {
            return teachName;
        }

        public void setTeachName(String teachName) {
            this.teachName = teachName;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public void setHasPermission(boolean hasPermission) {
            this.hasPermission = hasPermission;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public boolean isHasPermission() {
            return hasPermission;
        }

        public String getClassName() {
            return className;
        }
    }

    public static class MasterSchoolEntity {
        /**
         * participator :
         * schoolName : 中国
         * masterteacher : 12
         * masterclassroom : null
         */
        private String participator;
        private String schoolName;
        private String teacherlist;
        private String masterteacher;
        private boolean selfSchool;
        private String studentList;
        private String masterclassroom;

        public String getTeacherlist() {
            return teacherlist;
        }

        public void setTeacherlist(String teacherlist) {
            this.teacherlist = teacherlist;
        }

        public String getStudentList() {
            return studentList;
        }

        public void setStudentList(String studentList) {
            this.studentList = studentList;
        }

        public boolean isSelfSchool() {
            return selfSchool;
        }

        public void setSelfSchool(boolean selfSchool) {
            this.selfSchool = selfSchool;
        }

        public void setParticipator(String participator) {
            this.participator = participator;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public void setMasterteacher(String masterteacher) {
            this.masterteacher = masterteacher;
        }

        public void setMasterclassroom(String masterclassroom) {
            this.masterclassroom = masterclassroom;
        }

        public String getParticipator() {
            return participator;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public String getMasterteacher() {
            return masterteacher;
        }

        public String getMasterclassroom() {
            return masterclassroom;
        }
    }

    public static VideoMeetingDetailEntity parseJsonObject(JSONObject jsonObject){
        VideoMeetingDetailEntity videoMeetingDetailEntity = new VideoMeetingDetailEntity();
        videoMeetingDetailEntity.setUserType(jsonObject.optString("userType"));
        videoMeetingDetailEntity.setHasVideo(jsonObject.optBoolean("hasVideo"));
        JSONArray meetVideoArray = jsonObject.optJSONArray("meetVideoList");
        List<meetVideoEntity> meetVideoEntities = new ArrayList<>();
        for(int i = 0;i<meetVideoArray.length();i++){
            meetVideoEntity meetVideoEntity = new meetVideoEntity();
            JSONObject object = meetVideoArray.optJSONObject(i);
            meetVideoEntity.setMeetVideoId(object.optString("meetVideoId"));
            meetVideoEntity.setMeetingId(object.optString("meetingId"));
            meetVideoEntity.setVideoName(object.optString("videoName"));
            meetVideoEntity.setThumbPath(object.optString("thumbPath"));
            meetVideoEntity.setFileStatus(object.optString("fileStatus"));
            meetVideoEntity.setLocalFilePath(object.optString("localFilePath"));
            meetVideoEntity.setFilePath(object.optString("filePath"));
            meetVideoEntity.setResolutionHeight(object.optLong("resolutionHeight"));
            meetVideoEntity.setResolutionWidth(object.optLong("resolutionWidth"));
            meetVideoEntity.setDuration(object.optLong("duration"));
            meetVideoEntity.setTransFlag(object.optString("transFlag"));
            meetVideoEntity.setStoreServer(object.optString("storeServer"));
            meetVideoEntity.setUploadUserId(object.optString("uploadUserId"));
            meetVideoEntity.setCreateTime(DateUtil.getDateStr(object.optLong("createTime"),"yyyy-MM-dd HH:mm:ss"));
            meetVideoEntity.setUploadUserName(object.optString("uploadUserName"));
            meetVideoEntity.setStrDuration(object.optString("strDuration"));
            meetVideoEntity.setStrCreateTime(object.optString("strCreateTime"));
            meetVideoEntity.setStrFileStatus(object.optString("strFileStatus"));

            meetVideoEntities.add(meetVideoEntity);
        }
        videoMeetingDetailEntity.setMeetVideoList(meetVideoEntities);


        JSONObject data =  jsonObject.optJSONObject("data");
        DataEntity dataEntity = new DataEntity();
        dataEntity.setSponsorDate(data.optString("sponsorDate"));
        dataEntity.setSponsorName(data.optString("sponsorName"));
        dataEntity.setDescription(data.optString("description"));
        dataEntity.setTitle(data.optString("title"));
        dataEntity.setAverageScore(data.optInt("averageScore"));
        dataEntity.setStartDate(data.optString("startDate"));//开始时间
        dataEntity.setEndDate(data.optString("endDate"));//预约结束时间
        dataEntity.setStatus(data.optString("status"));
        dataEntity.setBeginDateTime(data.optString("beginDate"));//预约开始时间
        videoMeetingDetailEntity.setData(dataEntity);

        JSONObject masterSchool = jsonObject.optJSONObject("masterSchool");
        MasterSchoolEntity masterSchoolEntity = new MasterSchoolEntity();
        masterSchoolEntity.setMasterclassroom(StringUtils.filterNullString(masterSchool.optString("masterclassroom")));
        masterSchoolEntity.setMasterteacher(StringUtils.filterNullString(masterSchool.optString("masterteacher")));
        masterSchoolEntity.setParticipator(StringUtils.filterNullString(masterSchool.optString("participator")));
        masterSchoolEntity.setSchoolName(masterSchool.optString("schoolName"));
        masterSchoolEntity.setSelfSchool(masterSchool.optBoolean("selfSchool"));
        masterSchoolEntity.setStudentList(masterSchool.optString("studentList"));
        masterSchoolEntity.setTeacherlist(masterSchool.optString("teacherList"));
        videoMeetingDetailEntity.setMasterSchool(masterSchoolEntity);

        JSONObject permission = jsonObject.optJSONObject("permission");
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setClassName(permission.optString("className"));
        permissionEntity.setHasPermission(permission.optBoolean("hasPermission"));
        permissionEntity.setSchoolName(permission.optString("schoolName"));
        videoMeetingDetailEntity.setPermission(permissionEntity);

        List<ParticipatorEntity> ParticipatorEntityList = new ArrayList<>();
        JSONArray participator = jsonObject.optJSONArray("participator");
        for (int i = 0; i< participator.length();i++){
            ParticipatorEntity participatorEntity = new ParticipatorEntity();
            JSONObject object = participator.optJSONObject(i);
            participatorEntity.setSchoolName(object.optString("schoolName"));
            participatorEntity.setParticipator(object.optString("participator"));
            participatorEntity.setClassroom(object.optString("classroom"));
            participatorEntity.setSelfSchool(object.optBoolean("selfSchool"));
//            participatorEntity.setStudentList(object.optString("studentList"));
//            participatorEntity.setTeacherList(object.optString("teacherList"));
            ParticipatorEntityList.add(participatorEntity);
        }
        videoMeetingDetailEntity.setParticipator(ParticipatorEntityList);


        List<AlternateachievementEntity> alternateachievementList = new ArrayList<>();
        JSONArray alternateachievement = jsonObject.optJSONArray("alternateachievement");
        for(int i = 0;i<alternateachievement.length();i++){
            AlternateachievementEntity alternateachievementEntity = new AlternateachievementEntity();
            JSONObject object = alternateachievement.optJSONObject(i);
            alternateachievementEntity.setMeetDocumentId(object.optString("meetDocumentId"));
            alternateachievementEntity.setMeetingId(object.optString("meetingId"));
            alternateachievementEntity.setBelongToType(object.optString("belongToType"));
            alternateachievementEntity.setDocumentPath(object.optString("documentPath"));
            alternateachievementEntity.setUploadUserId(object.optString("uploadUserId"));
            alternateachievementEntity.setCreateTime(object.optLong("createTime"));
            alternateachievementEntity.setLocked(object.optString("locked"));
            alternateachievementEntity.setGuestFlag(object.optString("guestFlag"));
            alternateachievementEntity.setGuestName(object.optString("guestName"));
            alternateachievementEntity.setPageNum(object.optInt("pageNum"));
            alternateachievementEntity.setClsClassroomId(object.optString("clsClassroomId"));
            alternateachievementEntity.setDocumentName(object.optString("documentName"));

            alternateachievementList.add(alternateachievementEntity);
        }
        videoMeetingDetailEntity.setAlternateachievement(alternateachievementList);

        List<InterrelatedDocEntity> interrelatedDocList = new ArrayList<>();
        JSONArray interrelatedDoc = jsonObject.optJSONArray("interrelatedDoc");
        for(int i = 0; i<interrelatedDoc.length();i++){
            JSONObject object = interrelatedDoc.optJSONObject(i);
            InterrelatedDocEntity interrelatedDocEntity = new InterrelatedDocEntity();
            interrelatedDocEntity.setMeetDocumentId(object.optString("meetDocumentId"));
            interrelatedDocEntity.setMeetingId(object.optString("meetingId"));
            interrelatedDocEntity.setBelongToType(object.optString("belongToType"));
            interrelatedDocEntity.setDocumentPath(object.optString("documentPath"));
            interrelatedDocEntity.setUploadUserId(object.optString("uploadUserId"));
            interrelatedDocEntity.setCreateTime(object.optLong("createTime"));
            interrelatedDocEntity.setLocked(object.optString("locked"));
            interrelatedDocEntity.setGuestFlag(object.optString("guestFlag"));
            interrelatedDocEntity.setGuestName(object.optString("guestName"));
            interrelatedDocEntity.setPageNum(object.optInt("pageNum"));
            interrelatedDocEntity.setClsClassroomId(object.optString("clsClassroomId"));
            interrelatedDocEntity.setDocumentName(object.optString("documentName"));

            interrelatedDocList.add(interrelatedDocEntity);
        }
        videoMeetingDetailEntity.setInterrelatedDoc(interrelatedDocList);

        return videoMeetingDetailEntity;
    }


}

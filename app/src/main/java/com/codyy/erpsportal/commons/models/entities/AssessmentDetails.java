package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/4/20.
 */
public class AssessmentDetails extends BaseTitleItemBar implements Parcelable {
    private String isAttend;
    private String masterTeacherName;
    private String startDate;
    private String evaluationId;
    private double averageScore;
    private String status;
    private String realEndTime;
    private String scheduleSchoolName;
    private String endDate;
    private double myScore;
    private String sponsorDate;
    private String scoreType;
    private String title;
    private String subjectName;
    private String classLevelName;
    private String evaType;
    private String description;
    private String sponsorName;
    private String scheduleDate;
    private int classSeq;
    private int weekSeq;
    private String serverAddress;
    private String realBeginTime;
    private List<SchoolTeacher> schoolTeacherList;
    private ArrayList<VideoId> videoIds;
    private boolean scoreVisible;
    private String scheduleDetailId;

    public String getScheduleDetailId() {
        return scheduleDetailId;
    }

    public void setScheduleDetailId(String scheduleDetailId) {
        this.scheduleDetailId = scheduleDetailId;
    }

    public boolean isScoreVisible() {
        return scoreVisible;
    }

    public void setScoreVisible(boolean scoreVisible) {
        this.scoreVisible = scoreVisible;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public List<SchoolTeacher> getSchoolTeacherList() {
        return schoolTeacherList;
    }

    public void setSchoolTeacherList(List<SchoolTeacher> schoolTeacherList) {
        this.schoolTeacherList = schoolTeacherList;
    }

    public String getIsAttend() {
        return isAttend;
    }

    public void setIsAttend(String isAttend) {
        this.isAttend = isAttend;
    }

    public String getMasterTeacherName() {
        return masterTeacherName;
    }

    public void setMasterTeacherName(String masterTeacherName) {
        this.masterTeacherName = masterTeacherName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = getString(startDate);
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = getString(evaluationId);
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = getString(status);
    }

    public String getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(String realEndTime) {
        this.realEndTime = getString(realEndTime);
    }

    public String getScheduleSchoolName() {
        return scheduleSchoolName;
    }

    public void setScheduleSchoolName(String scheduleSchoolName) {
        this.scheduleSchoolName = getString(scheduleSchoolName);
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = getString(endDate);
    }

    public double getMyScore() {
        return myScore;
    }

    public void setMyScore(double myScore) {
        this.myScore = myScore;
    }

    public String getSponsorDate() {
        return sponsorDate;
    }

    public void setSponsorDate(String sponsorDate) {
        this.sponsorDate = getString(sponsorDate);
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = getString(scoreType);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = getString(title);
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = getString(subjectName);
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = getString(classLevelName);
    }

    public String getEvaType() {
        return evaType;
    }

    public void setEvaType(String evaType) {
        this.evaType = getString(evaType);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = getString(description);
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = getString(sponsorName);
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = getString(scheduleDate);
    }

    public int getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public int getWeekSeq() {
        return weekSeq;
    }

    public void setWeekSeq(int weekSeq) {
        this.weekSeq = weekSeq;
    }

    public String getRealBeginTime() {
        return realBeginTime;
    }

    public void setRealBeginTime(String realBeginTime) {
        this.realBeginTime = getString(realBeginTime);
    }

    public ArrayList<VideoId> getVideoIds() {
        return videoIds;
    }

    public void setVideoIds(ArrayList videoIds) {
        this.videoIds = videoIds;
    }

    private String getString(String a) {
        if ("null".equals(a)) {
            return "无";
        }
        return a;
    }

    /**
     * 获取评课详情
     *
     * @param object
     * @param assessmentDetails
     */
    public static void getAssessmentDetail(JSONObject object, AssessmentDetails assessmentDetails) {
        if ("success".equals(object.optString("result"))) {
            assessmentDetails.setIsAttend(object.optString("isAttend"));
            JSONObject jsonObject = object.optJSONObject("evluationDetail");
            assessmentDetails.setMasterTeacherName(jsonObject.optString("masterTeacherName"));
            assessmentDetails.setStartDate(jsonObject.optString("startDate"));
            assessmentDetails.setEvaluationId(jsonObject.optString("evaluationId"));
            assessmentDetails.setAverageScore(jsonObject.optDouble("averageScore"));
            assessmentDetails.setStatus(jsonObject.optString("status"));
            assessmentDetails.setRealEndTime(jsonObject.optString("realEndTime"));
            assessmentDetails.setScheduleSchoolName(jsonObject.optString("scheduleSchoolName"));
            assessmentDetails.setEndDate(jsonObject.optString("endDate"));
            assessmentDetails.setMyScore(jsonObject.optDouble("myScore", -1));
            assessmentDetails.setSponsorDate(jsonObject.optString("sponsorDate"));
            assessmentDetails.setScoreType(jsonObject.optString("scoreType"));
            assessmentDetails.setTitle(jsonObject.optString("title"));
            assessmentDetails.setSubjectName(jsonObject.optString("subjectName"));
            assessmentDetails.setClassLevelName(jsonObject.optString("classLevelName"));
            assessmentDetails.setEvaType(jsonObject.optString("evaType"));
            assessmentDetails.setDescription(jsonObject.optString("description"));
            assessmentDetails.setSponsorName(jsonObject.optString("sponsorName"));
            assessmentDetails.setScheduleDate(jsonObject.optString("scheduleDate"));
            assessmentDetails.setClassSeq(jsonObject.optInt("classSeq"));
            assessmentDetails.setWeekSeq(jsonObject.optInt("weekSeq"));
            assessmentDetails.setRealBeginTime(jsonObject.optString("realBeginTime"));
            assessmentDetails.setServerAddress(jsonObject.optString("serverAddress"));
            assessmentDetails.setScoreVisible(jsonObject.optBoolean("scoreVisible"));
            assessmentDetails.setScheduleDetailId(jsonObject.optString("scheduleDetailId"));
            JSONArray jsonArray = object.optJSONArray("schoolTeachers");
            List<SchoolTeacher> schoolTeachers = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                SchoolTeacher schoolTeacher = new SchoolTeacher();
                schoolTeacher.setTeachers(jsonObject1.optString("teachers"));
                schoolTeacher.setInvitedStatus(jsonObject1.optString("invitedStatus"));
                schoolTeacher.setIsSelf(jsonObject1.optString("isSelf"));
                schoolTeacher.setSchoolName(jsonObject1.optString("schoolName"));
                schoolTeachers.add(schoolTeacher);
            }
            JSONArray jsonArray1 = object.optJSONArray("videoIds");
            Gson gson = new Gson();
            ArrayList<VideoId> strings = new ArrayList<>();
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject object1 = jsonArray1.optJSONObject(i);
                VideoId videoId = gson.fromJson(object1.toString(), VideoId.class);
                strings.add(videoId);
            }
            assessmentDetails.setVideoIds(strings);
            assessmentDetails.setSchoolTeacherList(schoolTeachers);
        }
    }


    public AssessmentDetails() {
    }

    public static class VideoId implements Parcelable {

        /**
         * id : 53a16af378a24873b31f6af2e7291490
         * filePath : http://10.5.225.32:8080/ResourceServer/res/view/mobile/viewVideo/8d5686a143c94f4885230beaeed654e7/53a16af378a24873b31f6af2e7291490/normal.html
         */

        private String id;
        private String filePath;
        private String downloadUrl;

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public VideoId() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.filePath);
            dest.writeString(this.downloadUrl);
        }

        protected VideoId(Parcel in) {
            this.id = in.readString();
            this.filePath = in.readString();
            this.downloadUrl = in.readString();
        }

        public static final Creator<VideoId> CREATOR = new Creator<VideoId>() {
            @Override
            public VideoId createFromParcel(Parcel source) {
                return new VideoId(source);
            }

            @Override
            public VideoId[] newArray(int size) {
                return new VideoId[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.isAttend);
        dest.writeString(this.masterTeacherName);
        dest.writeString(this.startDate);
        dest.writeString(this.evaluationId);
        dest.writeDouble(this.averageScore);
        dest.writeString(this.status);
        dest.writeString(this.realEndTime);
        dest.writeString(this.scheduleSchoolName);
        dest.writeString(this.endDate);
        dest.writeDouble(this.myScore);
        dest.writeString(this.sponsorDate);
        dest.writeString(this.scoreType);
        dest.writeString(this.title);
        dest.writeString(this.subjectName);
        dest.writeString(this.classLevelName);
        dest.writeString(this.evaType);
        dest.writeString(this.description);
        dest.writeString(this.sponsorName);
        dest.writeString(this.scheduleDate);
        dest.writeInt(this.classSeq);
        dest.writeInt(this.weekSeq);
        dest.writeString(this.serverAddress);
        dest.writeString(this.realBeginTime);
        dest.writeTypedList(schoolTeacherList);
        dest.writeTypedList(videoIds);
        dest.writeByte(scoreVisible ? (byte) 1 : (byte) 0);
        dest.writeString(this.scheduleDetailId);
    }

    protected AssessmentDetails(Parcel in) {
        this.isAttend = in.readString();
        this.masterTeacherName = in.readString();
        this.startDate = in.readString();
        this.evaluationId = in.readString();
        this.averageScore = in.readDouble();
        this.status = in.readString();
        this.realEndTime = in.readString();
        this.scheduleSchoolName = in.readString();
        this.endDate = in.readString();
        this.myScore = in.readDouble();
        this.sponsorDate = in.readString();
        this.scoreType = in.readString();
        this.title = in.readString();
        this.subjectName = in.readString();
        this.classLevelName = in.readString();
        this.evaType = in.readString();
        this.description = in.readString();
        this.sponsorName = in.readString();
        this.scheduleDate = in.readString();
        this.classSeq = in.readInt();
        this.weekSeq = in.readInt();
        this.serverAddress = in.readString();
        this.realBeginTime = in.readString();
        this.schoolTeacherList = in.createTypedArrayList(SchoolTeacher.CREATOR);
        this.videoIds = in.createTypedArrayList(VideoId.CREATOR);
        this.scoreVisible = in.readByte() != 0;
        this.scheduleDetailId = in.readString();
    }

    public static final Creator<AssessmentDetails> CREATOR = new Creator<AssessmentDetails>() {
        @Override
        public AssessmentDetails createFromParcel(Parcel source) {
            return new AssessmentDetails(source);
        }

        @Override
        public AssessmentDetails[] newArray(int size) {
            return new AssessmentDetails[size];
        }
    };
}

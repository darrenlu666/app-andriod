package com.codyy.erpsportal.resource.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源详情
 * Created by gujiajia on 2015/4/17.
 */
public class ResourceDetails implements Parcelable {

    private String id;

    private String description;

    private String userName;

    private String resourceName;

    private String thumbPath;

    private String downloadUrl;

    private String playUrl;

    private String semesterName;

    private String classLevelName;

    private String versionName;

    private String subjectName;

    private String volumeName;

    private String chapterName;

    private String sectionName;

    private long size;

    private String createTime;

    private String evaluateAvg;

    private String viewCount;

    private String favoriteCount;

    private String downloadCount;

    /**
     * 分享者
     */
    private String sharer;

    /**
     * 分享次数
     */
    private int sharedTimes;

    private int duration;

    private String durationStr;

    private String knowledgeNames;

    private String[] knowledgeNameArr;

    private List<VideoClarity> videoClarities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationStr() {
        return durationStr;
    }

    public void setDurationStr(String durationStr) {
        this.durationStr = durationStr;
    }

    public String getKnowledgeNames() {
        return knowledgeNames;
    }

    public void setKnowledgeNames(String knowledgeNames) {
        this.knowledgeNames = knowledgeNames;
    }

    public String[] getKnowledgeNameArr() {
        return knowledgeNameArr;
    }

    public void setKnowledgeNameArr(String[] knowledgeNameArr) {
        this.knowledgeNameArr = knowledgeNameArr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEvaluateAvg() {
        return evaluateAvg;
    }

    public void setEvaluateAvg(String evaluateAvg) {
        this.evaluateAvg = evaluateAvg;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getSharer() {
        return sharer;
    }

    public void setSharer(String sharer) {
        this.sharer = sharer;
    }

    public int getSharedTimes() {
        return sharedTimes;
    }

    public void setSharedTimes(int sharedTimes) {
        this.sharedTimes = sharedTimes;
    }

    public List<VideoClarity> getVideoClarities() {
        return videoClarities;
    }

    public void setVideoClarities(List<VideoClarity> videoClarities) {
        this.videoClarities = videoClarities;
    }

    public ResourceDetails() {
    }

    public static ResourceDetails parseJson(JSONObject jsonObject) {
        ResourceDetails resourceDetails = new ResourceDetails();
        resourceDetails.setDescription(optStringOrNull(jsonObject, "brief"));
        resourceDetails.setChapterName(optString(jsonObject, "chapterName"));
        resourceDetails.setId(jsonObject.optString("resourceId"));
        long createTime = jsonObject.optLong("createTime");
        resourceDetails.setCreateTime(DateTimeFormat.forPattern("yyyy-MM-dd").print(createTime));
        resourceDetails.setClassLevelName(optString(jsonObject, "clslevelName"));
        resourceDetails.setUserName(jsonObject.optString("createUserName"));
        resourceDetails.setDownloadCount(jsonObject.optString("downloadCnt", "未知"));
        resourceDetails.setResourceName(jsonObject.optString("resourceName"));
        resourceDetails.setThumbPath(jsonObject.optString("thumbPathUrl"));
        resourceDetails.setDownloadUrl(jsonObject.optString("downloadUrl"));
        resourceDetails.setSemesterName(optString(jsonObject, "semesterName"));
        resourceDetails.setSubjectName(optString(jsonObject, "subjectName"));
        resourceDetails.setVolumeName(optString(jsonObject, "volumnName"));
        resourceDetails.setSectionName(optString(jsonObject, "sectionName"));
        resourceDetails.setVersionName(optString(jsonObject, "versionName"));
        resourceDetails.setSize(jsonObject.optLong("size"));
        resourceDetails.setDuration(jsonObject.optInt("duration"));
        resourceDetails.setDurationStr(jsonObject.optString("durationStr"));
        resourceDetails.setEvaluateAvg(jsonObject.optString("ratingAvg"));
        resourceDetails.setViewCount(jsonObject.optString("viewCnt"));
        resourceDetails.setPlayUrl(optString(jsonObject, "playUrl"));
        resourceDetails.setFavoriteCount(jsonObject.optString("favoriteCnt", "未知"));
        JSONArray knowledges = jsonObject.optJSONArray("knowledges");
        if (knowledges != null){
            String[] knowledgeArr = new String[knowledges.length()];
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<knowledges.length(); i++) {
                String knowledge = knowledges.optString(i);
                if (i != 0)
                    sb.append("、");
                sb.append(knowledge);
                knowledgeArr[i] = knowledge;
            }
            resourceDetails.setKnowledgeNames(sb.toString());
            resourceDetails.setKnowledgeNameArr(knowledgeArr);
        }
        resourceDetails.setSharer(jsonObject.optString("shareName"));
        resourceDetails.setSharedTimes(jsonObject.optInt("shareNum"));
        JSONArray clarityArr = jsonObject.optJSONArray("resVideos");
        if (clarityArr != null && clarityArr.length() > 0) {
            List<VideoClarity> videoClarities = new ArrayList<>(clarityArr.length());
            for (int i=0; i < clarityArr.length(); i++) {
                JSONObject clarityJsonObj = clarityArr.optJSONObject(i);
                VideoClarity videoClarity = new VideoClarity();
                videoClarity.setDefinition(clarityJsonObj.optString("definition"));
                videoClarity.setDownloadUrl(clarityJsonObj.optString("downloadUrl"));
                videoClarity.setPlayUrl(clarityJsonObj.optString("playUrl"));
                String definitionName = "HIGH".equals(videoClarity.getDefinition())?
                        "高清" : "普清";
                videoClarity.setDefinitionName(definitionName);
                videoClarities.add(videoClarity);
            }
            resourceDetails.setVideoClarities(videoClarities);
        }
        return resourceDetails;
    }

    private static String optStringOrNull(JSONObject jsonObject, String key) {
        return jsonObject.isNull(key)? null: jsonObject.optString(key, null);
    }

    private static String optString(JSONObject jsonObject, String key) {
        if (jsonObject.isNull(key)) return null;
        return jsonObject.optString(key, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.userName);
        dest.writeString(this.resourceName);
        dest.writeString(this.thumbPath);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.playUrl);
        dest.writeString(this.semesterName);
        dest.writeString(this.classLevelName);
        dest.writeString(this.versionName);
        dest.writeString(this.subjectName);
        dest.writeString(this.volumeName);
        dest.writeString(this.chapterName);
        dest.writeString(this.sectionName);
        dest.writeLong(this.size);
        dest.writeString(this.createTime);
        dest.writeString(this.evaluateAvg);
        dest.writeString(this.viewCount);
        dest.writeString(this.favoriteCount);
        dest.writeString(this.downloadCount);
        dest.writeInt(this.duration);
        dest.writeString(this.durationStr);
        dest.writeString(this.knowledgeNames);
    }

    protected ResourceDetails(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
        this.userName = in.readString();
        this.resourceName = in.readString();
        this.thumbPath = in.readString();
        this.downloadUrl = in.readString();
        this.playUrl = in.readString();
        this.semesterName = in.readString();
        this.classLevelName = in.readString();
        this.versionName = in.readString();
        this.subjectName = in.readString();
        this.volumeName = in.readString();
        this.chapterName = in.readString();
        this.sectionName = in.readString();
        this.size = in.readLong();
        this.createTime = in.readString();
        this.evaluateAvg = in.readString();
        this.viewCount = in.readString();
        this.favoriteCount = in.readString();
        this.downloadCount = in.readString();
        this.duration = in.readInt();
        this.durationStr = in.readString();
        this.knowledgeNames = in.readString();
    }

    public static final Creator<ResourceDetails> CREATOR = new Creator<ResourceDetails>() {
        public ResourceDetails createFromParcel(Parcel source) {
            return new ResourceDetails(source);
        }

        public ResourceDetails[] newArray(int size) {
            return new ResourceDetails[size];
        }
    };
}

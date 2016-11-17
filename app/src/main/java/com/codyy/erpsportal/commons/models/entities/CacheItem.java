package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.resource.models.entities.ResourceDetails;

/**
 * 缓存项
 * Created by kmdai on 2015/4/11.
 */
public class CacheItem implements Parcelable {

    public static final String DOWNLOAD_TYPE_VIDEO = "0"; //资源详情
    public static final String DOWNLOAD_TYPE_AUDIO = "1";
    public static final String DOWNLOAD_TYPE_IMAGE = "2";

    public static final String STATE_DOWNLOADING = "0";

    public static final String STATE_PAUSE = "1";

    public static final String STATE_ERROR = "2";

    public static final String STATE_COMPLETE = "3";

    private String id;

    private String name;

    private String thumbPath;

    private String resSize;

    private long size;

    private String createTime;

    private String speed;//100kb/s
    private boolean isCheck = false;
    private int progress;//进度

    private volatile String state;

    private String type;//资源类型：0：resource 1:评课议课
    private String downloadUrl ;//下载地址
    private String baseUserId;//记录用户信息，区分缓存账号
    private String suffix;//文件后缀

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getResSize() {
        return resSize;
    }

    public void setResSize(String resSize) {
        this.resSize = resSize;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSpeed() {
        return speed;
    }

   public CacheItem(){ }

   public CacheItem(ResourceDetails resourceDetails){
        init(resourceDetails);
   }
   public void setSpeed(String speed) {
       this.speed = speed;
   }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 0:resource
     * 1:evaluation
     * 下载时拼接URL使用...
     * @param type
     *  public static final String DOWNLOAD_TYPE_RESOURCE = "0"; //资源详情
     *  public static final String DOWNLOAD_TYPE_EVALUATE = "1" ;//评课议课
    *   public static final String DOWNLOAD_TYPE_MEETING  = "2" ;//视频会议
     */
    public void setType(String type) {
        this.type = type;
    }

    public void init(ResourceDetails resourceDetails ){
        this.speed = "0kb/s";
        this.progress = 0;
        this.state = STATE_DOWNLOADING;
        this.type = "0";//default:0 resource type

        this.setId(resourceDetails.getId());
        this.setName(resourceDetails.getResourceName());
        this.setThumbPath(resourceDetails.getThumbPath());
        this.setSize(resourceDetails.getSize());
        this.setCreateTime(resourceDetails.getCreateTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.thumbPath);
        dest.writeString(this.resSize);
        dest.writeLong(this.size);
        dest.writeString(this.createTime);
        dest.writeString(this.speed);
        dest.writeByte(isCheck ? (byte) 1 : (byte) 0);
        dest.writeInt(this.progress);
        dest.writeString(state);
        dest.writeString(this.type);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.baseUserId);
        dest.writeString(this.suffix);
    }

    protected CacheItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.thumbPath = in.readString();
        this.resSize = in.readString();
        this.size = in.readLong();
        this.createTime = in.readString();
        this.isCheck = in.readByte() != 0;
        this.progress = in.readInt();
        this.state = in.readString();
        this.type = in.readString();
        this.downloadUrl = in.readString();
        this.baseUserId = in.readString();
        this.suffix = in.readString();
    }

    public static final Creator<CacheItem> CREATOR = new Creator<CacheItem>() {
        public CacheItem createFromParcel(Parcel source) {
            return new CacheItem(source);
        }

        public CacheItem[] newArray(int size) {
            return new CacheItem[size];
        }
    };

    public boolean isDownloading() {
        return STATE_DOWNLOADING.equals(state);
    }
}

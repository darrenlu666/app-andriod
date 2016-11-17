package com.codyy.erpsportal.weibo.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.entities.RefreshEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 15-12-23.
 */
public class WeiBoListInfo extends RefreshEntity {
    /**
     * 原始微博
     */
    public final static int TYPE_ORIGINAL = 0x001;
    /**
     * 转发
     */
    public final static int TYPE_TURN = 0x002;
    /**
     * miblogId : f1b9c4945b84443a9076fd20c90b1fd7
     * originalFlag : Y
     * originalBlogId : null
     * blogContent : 生活不乏精彩，只是有时候我们的眼睛盯着乌云不放。
     * baseUserId : 3813dba196ce4c819f9a095de9b00db1
     * userType : TEACHER
     * createTime : 1451543081557
     * agreeCount : 1
     * locked : N
     * commentCount : 0
     * publishTime : 1451543081557
     * videoName : null
     * videoPath : null
     * videoStoreServer : null
     * audioName : null
     * audioPath : null
     * audioStoreServer : null
     * realName : 方老师
     * headPic : cdf50e40-27e7-4d8b-887b-85ba1d72a7e5.jpg
     * imageList : [{"miblogImageId":"12486b556bed4464802b85597e4c4817","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"d283fa99-b86a-4c6b-94eb-c50b41610363.jpg","createTime":123456789,"sort":0},{"miblogImageId":"eaa8e70f552347fd9928e2182db376dd","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"ac88d2be-e33c-4fc3-91ea-84988a269a48.jpg","createTime":123456789,"sort":1},{"miblogImageId":"21b76e8a599d4d2e89cb5d15c58e8f66","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"59098acb-b1bf-4842-bbfc-961ea4b4725c.jpg","createTime":123456789,"sort":2},{"miblogImageId":"fc0040effa084dfa9e989305c4334e29","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"6e96a09f-1edc-49fe-bca8-e96e8183037a.jpg","createTime":123456789,"sort":3},{"miblogImageId":"51122243ddd84d89a28018f72e7e758c","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"7139d469-0ffa-4fc3-8132-b1d5ff62087a.jpg","createTime":123456789,"sort":4},{"miblogImageId":"cd59f36043d047ec992f0791a8339b31","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"a5f81aa6-8d83-4152-aa80-483c76dc43d4.jpg","createTime":123456789,"sort":5},{"miblogImageId":"1749ba2c2fb242bca2ffe4fbc5910b2a","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"fec9f978-31c2-431f-8e05-2c51b6025666.jpg","createTime":123456789,"sort":6},{"miblogImageId":"dde72828c2a5406ba7c725cf36b5c1d0","miblogId":"f1b9c4945b84443a9076fd20c90b1fd7","image":"507ff645-bfeb-4dac-ab41-a1e3a32a25e2.jpg","createTime":123456789,"sort":7}]
     * originalMiblogBlog : null
     */
    private String miblogId;
    private String originalFlag;
    private String originalBlogId;
    private String blogContent;
    private String baseUserId;
    private String userType;
    private long createTime;
    private String groupMiblogId;
    private int agreeCount;
    private String locked;
    private int commentCount;
    private long publishTime;
    private String videoName;
    private String videoPath;
    private String videoStoreServer;
    private String audioName;
    private String audioPath;
    private String audioStoreServer;
    private String realName;
    private String headPic;
    private String isfollow;
    private String transFlag;
    private WeiBoListInfo originalMiblogBlog;
    /**
     * miblogImageId : 12486b556bed4464802b85597e4c4817
     * miblogId : f1b9c4945b84443a9076fd20c90b1fd7
     * image : d283fa99-b86a-4c6b-94eb-c50b41610363.jpg
     * createTime : 123456789
     * sort : 0
     */

    private ArrayList<ImageListEntity> imageList;

    private String delFlag;
    private String agreeFlag;
    private String originalDelFlag;
    /**
     * 是否有截屏
     */
    private String printScreeFlag;
    private String thumb;
    private String isTimeFlag;

    public String getIsfollow() {
        return isfollow;
    }

    public void setIsfollow(String isfollow) {
        this.isfollow = isfollow;
    }

    public String getGroupMiblogId() {
        return groupMiblogId;
    }

    public void setGroupMiblogId(String groupMiblogId) {
        this.groupMiblogId = groupMiblogId;
    }

    public String getIsTimeFlag() {
        return isTimeFlag;
    }

    public void setIsTimeFlag(String isTimeFlag) {
        this.isTimeFlag = isTimeFlag;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPrintScreeFlag() {
        return printScreeFlag;
    }

    public void setPrintScreeFlag(String printScreeFlag) {
        this.printScreeFlag = printScreeFlag;
    }

    public String getOriginalDelFlag() {
        return originalDelFlag;
    }

    public void setOriginalDelFlag(String originalDelFlag) {
        this.originalDelFlag = originalDelFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getAgreeFlag() {
        return agreeFlag;
    }

    public void setAgreeFlag(String agreeFlag) {
        this.agreeFlag = agreeFlag;
    }

    public void setMiblogId(String miblogId) {
        this.miblogId = miblogId;
    }

    public void setOriginalFlag(String originalFlag) {
        this.originalFlag = originalFlag;
    }

    public void setOriginalBlogId(String originalBlogId) {
        this.originalBlogId = originalBlogId;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setVideoStoreServer(String videoStoreServer) {
        this.videoStoreServer = videoStoreServer;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void setAudioStoreServer(String audioStoreServer) {
        this.audioStoreServer = audioStoreServer;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public void setOriginalMiblogBlog(WeiBoListInfo originalMiblogBlog) {
        this.originalMiblogBlog = originalMiblogBlog;
    }

    public void setImageList(ArrayList<ImageListEntity> imageList) {
        this.imageList = imageList;
    }

    public String getMiblogId() {
        return miblogId;
    }

    public String getOriginalFlag() {
        return originalFlag;
    }

    public String getOriginalBlogId() {
        return originalBlogId;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public String getUserType() {
        return userType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public String getLocked() {
        return locked;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getVideoStoreServer() {
        return videoStoreServer;
    }

    public String getAudioName() {
        return audioName;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public String getAudioStoreServer() {
        return audioStoreServer;
    }

    public String getRealName() {
        return realName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public WeiBoListInfo getOriginalMiblogBlog() {
        return originalMiblogBlog;
    }

    public ArrayList<ImageListEntity> getImageList() {
        return imageList;
    }

    public String getTransFlag() {
        return transFlag;
    }

    public void setTransFlag(String transFlag) {
        this.transFlag = transFlag;
    }

    public static class ImageListEntity implements android.os.Parcelable {
        private String miblogImageId;
        private String miblogId;
        private String image;
        private long createTime;
        private int sort;

        public void setMiblogImageId(String miblogImageId) {
            this.miblogImageId = miblogImageId;
        }

        public void setMiblogId(String miblogId) {
            this.miblogId = miblogId;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getMiblogImageId() {
            return miblogImageId;
        }

        public String getMiblogId() {
            return miblogId;
        }

        public String getImage() {
            return image;
        }

        public long getCreateTime() {
            return createTime;
        }

        public int getSort() {
            return sort;
        }

        public static ImageListEntity getImage(JSONObject object) {
            ImageListEntity imageListEntity = new ImageListEntity();
            if (object != null) {
                imageListEntity.setCreateTime(object.optLong("createTime"));
                imageListEntity.setMiblogId(object.optString("miblogId"));
                imageListEntity.setMiblogImageId(object.optString("miblogImageId"));
                imageListEntity.setSort(object.optInt("sort"));
                imageListEntity.setImage(object.optString("image"));
            }
            return imageListEntity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.miblogImageId);
            dest.writeString(this.miblogId);
            dest.writeString(this.image);
            dest.writeLong(this.createTime);
            dest.writeInt(this.sort);
        }

        public ImageListEntity() {
        }

        protected ImageListEntity(Parcel in) {
            this.miblogImageId = in.readString();
            this.miblogId = in.readString();
            this.image = in.readString();
            this.createTime = in.readLong();
            this.sort = in.readInt();
        }

        public static final Creator<ImageListEntity> CREATOR = new Creator<ImageListEntity>() {
            public ImageListEntity createFromParcel(Parcel source) {
                return new ImageListEntity(source);
            }

            public ImageListEntity[] newArray(int size) {
                return new ImageListEntity[size];
            }
        };
    }

    public static WeiBoListInfo getWeiBo(JSONObject weiboInfo, boolean originalFlag) {
        if (weiboInfo != null) {
            WeiBoListInfo weiBoListInfo = new WeiBoListInfo();
            weiBoListInfo.setmHolderType(WeiBoListInfo.TYPE_ORIGINAL);
            weiBoListInfo.setGroupMiblogId(weiboInfo.optString("groupMiblogId"));
            weiBoListInfo.setMiblogId(weiboInfo.optString("miblogId"));
            weiBoListInfo.setOriginalFlag(weiboInfo.optString("originalFlag"));
            weiBoListInfo.setOriginalBlogId(weiboInfo.optString("originalBlogId"));
            weiBoListInfo.setBlogContent(weiboInfo.optString("blogContent"));
            weiBoListInfo.setBaseUserId(weiboInfo.optString("baseUserId"));
            weiBoListInfo.setUserType(weiboInfo.optString("userType"));
            weiBoListInfo.setCreateTime(weiboInfo.optLong("createTime"));
            weiBoListInfo.setAgreeCount(weiboInfo.optInt("agreeCount"));
            weiBoListInfo.setLocked(weiboInfo.optString("locked"));
            weiBoListInfo.setCommentCount(weiboInfo.optInt("commentCount"));
            weiBoListInfo.setPublishTime(weiboInfo.optLong("publishTime"));
            weiBoListInfo.setVideoName(weiboInfo.optString("videoName"));
            weiBoListInfo.setVideoPath(weiboInfo.optString("videoPath"));
            weiBoListInfo.setVideoStoreServer(weiboInfo.optString("videoStoreServer"));
            weiBoListInfo.setAudioName(weiboInfo.optString("audioName"));
            weiBoListInfo.setAudioPath(weiboInfo.optString("audioPath"));
            weiBoListInfo.setAudioStoreServer(weiboInfo.optString("audioStoreServer"));
            weiBoListInfo.setRealName(weiboInfo.optString("realName"));
            weiBoListInfo.setHeadPic(weiboInfo.optString("headPic"));
            weiBoListInfo.setIsfollow(weiboInfo.optString("isfollow"));
            weiBoListInfo.setTransFlag(weiboInfo.optString("transFlag"));
            JSONArray imglist = weiboInfo.optJSONArray("imageList");
            if (imglist != null && imglist.length() > 0) {
                ArrayList<ImageListEntity> imageListEntities = new ArrayList<>();
                for (int j = 0; j < imglist.length(); j++) {
                    imageListEntities.add(ImageListEntity.getImage(imglist.optJSONObject(j)));
                }
                weiBoListInfo.setImageList(imageListEntities);
            } else {
                weiBoListInfo.setImageList(null);
            }
            if ("N".equals(weiBoListInfo.getOriginalFlag())) {
                weiBoListInfo.setmHolderType(WeiBoListInfo.TYPE_TURN);
                JSONObject object = weiboInfo.optJSONObject("originalMiblogBlog");
                weiBoListInfo.setOriginalMiblogBlog(getWeiBo(object, true));
            }
            if (originalFlag) {
                weiBoListInfo.setBlogContent(addAtClick(weiBoListInfo.getBaseUserId(), weiBoListInfo.getRealName()) + weiBoListInfo.getBlogContent());
            }
            weiBoListInfo.setDelFlag(weiboInfo.optString("delFlag"));
            weiBoListInfo.setAgreeFlag(weiboInfo.optString("agreeFlag"));
            weiBoListInfo.setOriginalDelFlag(weiboInfo.optString("originalDelFlag"));
            weiBoListInfo.setPrintScreeFlag(weiboInfo.optString("printScreeFlag"));
            weiBoListInfo.setThumb(weiboInfo.optString("thumb"));
            weiBoListInfo.setIsTimeFlag(weiboInfo.optString("isTimeFlag"));
            return weiBoListInfo;
        } else {
            return null;
        }
    }

    public static List<WeiBoListInfo> getWeiBoList(JSONObject object) {
        ArrayList<WeiBoListInfo> infos = new ArrayList<>();
        if ("success".equals(object.optString("result"))) {
            JSONArray array = object.optJSONArray("weiboList");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject weiboInfo = array.optJSONObject(i);
                    infos.add(getWeiBo(weiboInfo, false));
                }
            }
        }
        return infos;
    }

    private static String addAtClick(String id, String name) {
        //<a class='blue' href='http://10.1.80.22:8080/UserCenter/toUserIndex/f28ccdd615d9427d83656d2ef373a224.html' target='_blank'>@阔地管家 </a>
        StringBuilder stringBuilder = new StringBuilder("<a class='blue' href='http://10.1.80.22:8080/UserCenter/toUserIndex/");
        stringBuilder.append(id);
        stringBuilder.append(".html' target='_blank'>@");
        stringBuilder.append(name);
        stringBuilder.append(":</a>");
        return stringBuilder.toString();
    }

    public WeiBoListInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.miblogId);
        dest.writeString(this.originalFlag);
        dest.writeString(this.originalBlogId);
        dest.writeString(this.blogContent);
        dest.writeString(this.baseUserId);
        dest.writeString(this.userType);
        dest.writeLong(this.createTime);
        dest.writeString(this.groupMiblogId);
        dest.writeInt(this.agreeCount);
        dest.writeString(this.locked);
        dest.writeInt(this.commentCount);
        dest.writeLong(this.publishTime);
        dest.writeString(this.videoName);
        dest.writeString(this.videoPath);
        dest.writeString(this.videoStoreServer);
        dest.writeString(this.audioName);
        dest.writeString(this.audioPath);
        dest.writeString(this.audioStoreServer);
        dest.writeString(this.realName);
        dest.writeString(this.headPic);
        dest.writeString(this.isfollow);
        dest.writeString(this.transFlag);
        dest.writeParcelable(this.originalMiblogBlog, flags);
        dest.writeTypedList(this.imageList);
        dest.writeString(this.delFlag);
        dest.writeString(this.agreeFlag);
        dest.writeString(this.originalDelFlag);
        dest.writeString(this.printScreeFlag);
        dest.writeString(this.thumb);
        dest.writeString(this.isTimeFlag);
    }

    protected WeiBoListInfo(Parcel in) {
        super(in);
        this.miblogId = in.readString();
        this.originalFlag = in.readString();
        this.originalBlogId = in.readString();
        this.blogContent = in.readString();
        this.baseUserId = in.readString();
        this.userType = in.readString();
        this.createTime = in.readLong();
        this.groupMiblogId = in.readString();
        this.agreeCount = in.readInt();
        this.locked = in.readString();
        this.commentCount = in.readInt();
        this.publishTime = in.readLong();
        this.videoName = in.readString();
        this.videoPath = in.readString();
        this.videoStoreServer = in.readString();
        this.audioName = in.readString();
        this.audioPath = in.readString();
        this.audioStoreServer = in.readString();
        this.realName = in.readString();
        this.headPic = in.readString();
        this.isfollow = in.readString();
        this.transFlag = in.readString();
        this.originalMiblogBlog = in.readParcelable(WeiBoListInfo.class.getClassLoader());
        this.imageList = in.createTypedArrayList(ImageListEntity.CREATOR);
        this.delFlag = in.readString();
        this.agreeFlag = in.readString();
        this.originalDelFlag = in.readString();
        this.printScreeFlag = in.readString();
        this.thumb = in.readString();
        this.isTimeFlag = in.readString();
    }

    public static final Creator<WeiBoListInfo> CREATOR = new Creator<WeiBoListInfo>() {
        @Override
        public WeiBoListInfo createFromParcel(Parcel source) {
            return new WeiBoListInfo(source);
        }

        @Override
        public WeiBoListInfo[] newArray(int size) {
            return new WeiBoListInfo[size];
        }
    };
}

package com.codyy.erpsportal.resource.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 音频资源
 * Created by gujiajia on 2016/6/14.
 */
public class Audio implements android.os.Parcelable {

    private String id;

    private String name;

    private int playCount;

    private int downloadCount;

    private long duration;

    private String streamUrl;

    private boolean playing;

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

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public static JsonParser<Audio> sParser = new JsonParser<Audio>() {
        @Override
        public Audio parse(JSONObject jsonObject) {
            Audio audio = new Audio();
            audio.setId(jsonObject.optString("resourceId"));
            audio.setName(jsonObject.optString("resourceName"));
//            audio.setStreamUrl("file://" + Environment.getExternalStorageDirectory() + "/IfHeDoesntLoveYou.mp3");
            audio.setStreamUrl(jsonObject.optString("playUrl"));
            audio.setDownloadCount(jsonObject.optInt("downloadCnt"));
            audio.setPlayCount(jsonObject.optInt("viewCnt"));
            audio.setDuration(jsonObject.optLong("duration"));
            return audio;
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Audio) {
            if (((Audio)o).getId().equals(getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.playCount);
        dest.writeInt(this.downloadCount);
        dest.writeLong(this.duration);
        dest.writeString(this.streamUrl);
        dest.writeByte(this.playing ? (byte) 1 : (byte) 0);
    }

    public Audio() {
    }

    protected Audio(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.playCount = in.readInt();
        this.downloadCount = in.readInt();
        this.duration = in.readLong();
        this.streamUrl = in.readString();
        this.playing = in.readByte() != 0;
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel source) {
            return new Audio(source);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };
}

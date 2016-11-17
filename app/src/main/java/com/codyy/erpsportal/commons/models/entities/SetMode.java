package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2015/7/3.
 * 课堂模式
 * 业务处理：
 * Mode=0 视频模式
 * Mode=1 演示模式
 * Mode=2 互动模式
 * a.设置上课模式，Mode == 2时，提示“主讲教师正在使用【互动模式】授课<br>在退出该模式之前，手动导播功能暂时无法使用”，导播模式页面显示切换自动导播，并像主讲教室发送设置自动导播COCO
 * b.当点击机位标题切换时，需要切换机位，判断当前Mode == 2，提示“主讲教师正在使用【互动模式】授课<br>在退出该模式之前，手动导播功能暂时无法使用”，不能改为未自动模式
 */
public class SetMode implements Parcelable {
    int mode;

    @Override
    public String toString() {
        return "SetMode{" +
                "mode=" + mode +
                '}';
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mode);
    }

    public SetMode() {
    }

    protected SetMode(Parcel in) {
        this.mode = in.readInt();
    }

    public static final Creator<SetMode> CREATOR = new Creator<SetMode>() {
        public SetMode createFromParcel(Parcel source) {
            return new SetMode(source);
        }

        public SetMode[] newArray(int size) {
            return new SetMode[size];
        }
    };
}

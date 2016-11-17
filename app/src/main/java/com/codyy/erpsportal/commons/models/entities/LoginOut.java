package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2015/7/3.
 * 下线
 * 业务处理：
 * 判断from == MAIN_SPEAK(主讲教室uid)//即主讲教室关闭
 * 弹框提示（"提示", '与远程课堂终端失去连接，请耐心等待。'）
 * 判断from == UID//自己
 * 弹框提示（"提示", '帐号已于别处登录，点击【确定】退出远程导播。'）点击确定关闭页面
 */
public class LoginOut implements Parcelable {
    String from;
    String to;

    @Override
    public String toString() {
        return "LoginOut{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.from);
        dest.writeString(this.to);
    }

    public LoginOut() {
    }

    protected LoginOut(Parcel in) {
        this.from = in.readString();
        this.to = in.readString();
    }

    public static final Creator<LoginOut> CREATOR = new Creator<LoginOut>() {
        public LoginOut createFromParcel(Parcel source) {
            return new LoginOut(source);
        }

        public LoginOut[] newArray(int size) {
            return new LoginOut[size];
        }
    };
}

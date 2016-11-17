package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;

/**
 * 直属校选择项
 * Created by gujiajia on 2016/3/7.
 */
public class DirectSchoolsChoice extends Choice {

    public DirectSchoolsChoice(String id, String title) {
        super(id, title);
    }

    public DirectSchoolsChoice(String title) {
        super(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DirectSchoolsChoice() {
    }

    protected DirectSchoolsChoice(Parcel in) {
        super(in);
    }

    public static final Creator<DirectSchoolsChoice> CREATOR = new Creator<DirectSchoolsChoice>() {
        public DirectSchoolsChoice createFromParcel(Parcel source) {
            return new DirectSchoolsChoice(source);
        }

        public DirectSchoolsChoice[] newArray(int size) {
            return new DirectSchoolsChoice[size];
        }
    };
}

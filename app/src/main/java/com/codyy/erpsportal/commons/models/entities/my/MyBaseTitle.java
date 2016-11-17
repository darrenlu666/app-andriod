package com.codyy.erpsportal.commons.models.entities.my;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

/**
 * 我的-班级空间-标题
 * Created by poe on 16-3-1.
 */
public class MyBaseTitle extends BaseTitleItemBar implements Parcelable{

    private String baseMenuId ;
    private String menuName;

    public MyBaseTitle() {

    }
    public MyBaseTitle(String baseMenuId, String menuName) {
        this.baseMenuId = baseMenuId;
        this.menuName = menuName;
    }

    public MyBaseTitle(String baseTitle, int baseViewHoldType, String baseMenuId, String menuName) {
        super(baseTitle, baseViewHoldType);
        this.baseMenuId = baseMenuId;
        this.menuName = menuName;
    }

    protected MyBaseTitle(Parcel in) {
        baseMenuId = in.readString();
        menuName = in.readString();
    }

    public static final Creator<MyBaseTitle> CREATOR = new Creator<MyBaseTitle>() {
        @Override
        public MyBaseTitle createFromParcel(Parcel in) {
            return new MyBaseTitle(in);
        }

        @Override
        public MyBaseTitle[] newArray(int size) {
            return new MyBaseTitle[size];
        }
    };

    public String getBaseMenuId() {
        return baseMenuId;
    }

    public void setBaseMenuId(String baseMenuId) {
        this.baseMenuId = baseMenuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(baseMenuId);
        dest.writeString(menuName);
    }

}

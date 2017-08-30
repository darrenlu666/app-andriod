package com.codyy.erpsportal.commons.models.entities.my;

import android.os.Parcel;
import android.os.Parcelable;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

/**
 * 班级基本信息
 * Created by kmdai on 2015/9/1.
 */
public class ClassCont extends BaseTitleItemBar implements Parcelable{
    private String baseClassId;
    private String baseClassName;
    @SerializedName(value = "classLevelName",alternate = {"classlevelName"})
    private String classLevelName ;//新增的年纪字段 #三年级
    private String classPic;
    private String schoolName;//学校名称

    public ClassCont() {
    }

    public ClassCont(String baseClassId, String baseClassName, String classLevelName) {
        this.baseClassId = baseClassId;
        this.baseClassName = baseClassName;
        this.classLevelName = classLevelName;
    }

    protected ClassCont(Parcel in) {
        baseClassId = in.readString();
        baseClassName = in.readString();
        classLevelName = in.readString();
        classPic    =   in.readString();
        schoolName  =   in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(baseClassId);
        dest.writeString(baseClassName);
        dest.writeString(classLevelName);
        dest.writeString(classPic);
        dest.writeString(schoolName);
    }

    public static final Creator<ClassCont> CREATOR = new Creator<ClassCont>() {
        @Override
        public ClassCont createFromParcel(Parcel in) {
            return new ClassCont(in);
        }

        @Override
        public ClassCont[] newArray(int size) {
            return new ClassCont[size];
        }
    };

    public String getBaseClassId() {
        return baseClassId;
    }

    public void setBaseClassId(String baseClassId) {
        this.baseClassId = baseClassId;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public void setBaseClassName(String baseClassName) {
        this.baseClassName = baseClassName;
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public String getClassPic() {
        return classPic;
    }

    public void setClassPic(String classPic) {
        this.classPic = classPic;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}

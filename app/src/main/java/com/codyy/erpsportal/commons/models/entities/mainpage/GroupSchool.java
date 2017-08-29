package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 集团校
 * Created by poe on 17-8-22.
 */

public class GroupSchool extends BaseTitleItemBar{

    /**
     * clsSchoolId : 测试内容8ho8
     * clsSchoolName : 测试内容3t1w
     * coverPic : 测试内容1111
     * schoolTypeName : 测试内容2jb5
     */

    private String clsSchoolId;
    private String clsSchoolName;
    private String coverPic;
    private String schoolTypeName;

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public String getClsSchoolName() {
        return clsSchoolName;
    }

    public void setClsSchoolName(String clsSchoolName) {
        this.clsSchoolName = clsSchoolName;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public String getSchoolTypeName() {
        return schoolTypeName;
    }

    public void setSchoolTypeName(String schoolTypeName) {
        this.schoolTypeName = schoolTypeName;
    }
}

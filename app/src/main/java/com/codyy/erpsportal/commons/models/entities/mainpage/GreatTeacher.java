package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 名师推荐
 * Created by poe on 17-8-8.
 */

public class GreatTeacher extends BaseTitleItemBar{

    /**
     * realName :
     * subjectName :
     * classlevelName :
     * headPic :
     * baseUserId :
     */

    private String realName;
    private String subjectName;
    private String classlevelName;
    private String headPic;
    private String baseUserId;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }
}

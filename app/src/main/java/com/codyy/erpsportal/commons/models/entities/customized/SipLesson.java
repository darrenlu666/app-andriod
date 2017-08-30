package com.codyy.erpsportal.commons.models.entities.customized;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 同步课堂entity(sip)
 * Created by poe on 24/07/17.
 */

public class SipLesson extends BaseTitleItemBar{

    /**
     * classlevelName : 苏州一年级ssxxx
     * id : b93c225317ba45c9ab57aaafd08e8622
     * schoolName : 琦琦学校
     * subjectName : 苏州语文
     * teacherName : 王　琦
     * thumb : http://10.1.70.15:8080/ResourceServer/images/8598d1316d6747b8948100c5661c65b8_1c994ed663414d49b6b210116cbc1518.jpg
     */

    private String classlevelName;
    private String id;
    private String schoolName;
    private String subjectName;
    private String teacherName;
    private String thumb;

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}

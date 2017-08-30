package com.codyy.erpsportal.groups.models.entities;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

import java.io.Serializable;

/**
 * 圈组空间-集体备课
 * Created by poe on 16-2-2.
 */
public class GroupPrepareLesson extends BaseTitleItemBar implements Serializable {
    private String meetingId;
    private String meetingTitle;
    private String meetingDescription ;
    private String realName ;
    private String createUserId ;
    private String headPic;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getMeetingDescription() {
        return meetingDescription;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }
}

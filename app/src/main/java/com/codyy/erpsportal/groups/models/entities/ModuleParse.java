package com.codyy.erpsportal.groups.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 圈组空间解析数据集合
 * {@link ModulePrepareLesson }
 * {@link ModulePersonPrepare}
 * {@link ModuleBlog}
 * Created by poe on 16-2-1.
 */
public class ModuleParse implements Serializable {
    @SerializedName("groupPreparationLessonViews")
    private ModulePrepareLesson prepareLesson;
    @SerializedName("groupLessonPlans")
    private ModulePersonPrepare personPrepare;
    @SerializedName("blogViews")
    private ModuleBlog blog;
    private ModuleResource resourceList ;

    public ModulePrepareLesson getPrepareLesson() {
        return prepareLesson;
    }

    public void setPrepareLesson(ModulePrepareLesson prepareLesson) {
        this.prepareLesson = prepareLesson;
    }

    public ModulePersonPrepare getPersonPrepare() {
        return personPrepare;
    }

    public void setPersonPrepare(ModulePersonPrepare personPrepare) {
        this.personPrepare = personPrepare;
    }

    public ModuleBlog getBlog() {
        return blog;
    }

    public void setBlog(ModuleBlog blog) {
        this.blog = blog;
    }

    public ModuleResource getResourceList() {
        return resourceList;
    }

    public void setResourceList(ModuleResource resourceList) {
        this.resourceList = resourceList;
    }
}

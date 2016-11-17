package com.codyy.erpsportal.groups.models.entities;

/**
 * 圈组空间-模块基础类
 * Created by poe on 16-2-2.
 */
public class ModuleBase {
    private String groupModuleId;//"GROUP_PREPARATION_TEACH","GROUP_BLOG_TEACH","GROUP_PERPRELESSON_TEACH"
    private String moduleName;//"集体备课",
    private String moduleType;//"TEACH",“INTEREST”
    private String moduleMenu;//"GROUP_BLOG"，"GROUP_PREPARATION","GROUP_PERPRELESSON"

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getGroupModuleId() {
        return groupModuleId;
    }

    public void setGroupModuleId(String groupModuleId) {
        this.groupModuleId = groupModuleId;
    }

    public String getModuleMenu() {
        return moduleMenu;
    }

    public void setModuleMenu(String moduleMenu) {
        this.moduleMenu = moduleMenu;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}

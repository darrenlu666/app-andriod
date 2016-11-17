package com.codyy.erpsportal.commons.models.entities;

import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.controllers.viewholders.ApplicationViewHold;
import com.codyy.erpsportal.commons.models.Jumpable;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poe on 15-7-20.
 * an application in function fragment .
 */
public class AppInfo extends BaseTitleItemBar implements Cloneable{
    /**
     *
     */
    public static final String CATEGORY_SINGLE_MODEL = "single";
    public static final String CATEGORY_CHILD_MODEL =  "child";

    @StringDef(value = {CATEGORY_SINGLE_MODEL,CATEGORY_CHILD_MODEL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CATEGORY{}

    /**
     * resource icon id
     */
    private int icon;
    /**
     * 菜单id "onlineclass.id",
     */
    private String menuID;//
    /**
     * name
     */
    private String appName;//
    /**
     * 分类 single or children click
     */
    private String category;
    /**
     * web端传递的图片
     */
    private String headPic ;
    /**
     *
     * 用户权限
     */
    private List<AppPriority> roles;
    /**
     * 点击后执行跳转
     */
    private Jumpable jumpable;

    private List<AppInfo> childGroups;

    private int startPos;//动画开始的位置

    private int targetPos;//动画结束的位置

    public AppInfo() {
    }

    public AppInfo(int icon, String menuID, String appName, List<AppPriority> roles, Jumpable jumpable,@CATEGORY String category) {
        this.icon = icon;
        this.menuID = menuID;
        this.appName = appName;
        this.roles = roles;
        this.jumpable = jumpable;
        this.category   =   category;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<AppPriority> getRole() {
        return roles;
    }

    public void setRole(List<AppPriority> role) {
        this.roles = role;
    }

    public Jumpable getJumpable() {
        return jumpable;
    }

    public void setJumpable(Jumpable jumpable) {
        this.jumpable = jumpable;
    }

    public List<AppInfo> getChildGroups() {
        return childGroups;
    }

    @CATEGORY
    public String getCategory() {
        return category;
    }

    public void setCategory(@CATEGORY String category) {
        this.category = category;
    }

    public void setChildGroups(List<AppInfo> childGroups) {
        this.childGroups = childGroups;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getTargetPos() {
        return targetPos;
    }

    public void setTargetPos(int targetPos) {
        this.targetPos = targetPos;
    }

    public static List<AppInfo> parseData(JSONArray jsonArray , String userType){
        if(null == jsonArray) return  null;
        List<AppInfo> list = new ArrayList<>();
        if(jsonArray.length()>0){
            for(int i=0 ; i< jsonArray.length() ; i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    AppInfo app = parseOneData(jsonObject);
                    app.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_SINGLE);
                    if(AppConfig.getMenuIndex(app.getMenuID())>= 0){
                        list.add(app);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }

        //mock data start -----------
//        list.add(new AppInfo(R.drawable.ic_function_06,"internet.lesson.id","网络授课",AppConfig.instance().getPriorities("internet.lesson.id"),null,AppInfo.CATEGORY_SINGLE_MODEL));
        //mock data end --------------
        //init add jump
        AppConfig.instance().updateData(list);
        list = AppConfig.instance().getFilterAppInfo(userType);

        return  list;
    }


    private static AppInfo parseOneData(JSONObject jsonObject) {

        if (null == jsonObject) return null;

        AppInfo appInfo = new AppInfo();
        appInfo.setMenuID(jsonObject.optString("baseMenuId"));
        appInfo.setAppName(jsonObject.optString("menuName"));
        String image = jsonObject.optString("imagePath");
        if(null != image && !TextUtils.isEmpty(image)){
            if(!image.contains("http")){
                image = URLConfig.IMAGE_URL+image;
            }
        }
        appInfo.setHeadPic(image);
        //角色分配
        appInfo.setRole(AppConfig.instance().getPriorities(appInfo.getMenuID()));
        return appInfo;
    }

    @Override
    public AppInfo clone() throws CloneNotSupportedException {

        return (AppInfo)super.clone();
    }
}

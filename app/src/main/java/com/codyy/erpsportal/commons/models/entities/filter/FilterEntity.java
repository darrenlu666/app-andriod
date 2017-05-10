package com.codyy.erpsportal.commons.models.entities.filter;

import android.text.TextUtils;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupManagerFilterFragment;
import com.codyy.erpsportal.commons.utils.Cog;
import java.util.HashMap;
import java.util.List;

/**
 * 单个筛选元数据
 * Created by poe on 16-1-25.
 */
public class FilterEntity {
    public static final String TAG = "FilterEntity";
    public static final int LEVEL_AREA = 0x00;//地区id
    public static final int LEVEL_DIRECT = 0x01;//直属校
    public static final int LEVEL_CLASS_SEMESTER = 0x002;//固定的筛选条件-学段
    public static final int LEVEL_SCHOOL = 0x03;//固定-学校
    public static final int LEVEL_CLASS_TEAM = 0x004;//固定的筛选条件-组别
    public static final int LEVEL_CLASS_LEVEL = 0x005;//固定的筛选条件-年级
    public static final int LEVEL_CLASS_SUBJECT = 0x006;//固定的筛选条件-学科
    public static final int LEVEL_CLASS_CATEGORY = 0x007;//固定的筛选条件-分类
    public static final int LEVEL_CLASS_STATE = 0x008;//固定的筛选条件-状态
    public static final int LEVEL_CLASS_END = 0x009;//固定的筛选条件-结束位

    private String id;//当前的id 获取数据用
    private String selectedId;//选中的id 最终筛选结果
    private String uuid ;//用户的uuid 获取数据用
    private String cacheId;//缓存的区域id
    private String cacheSchoolId;//如有需要可以缓存一个ｓｃｈｏｏｌＩｄ　．
    private String cacheAreaId;//缓存当前用户的id ,变态产品需要取当前用户区域的id ．
    private String name;//当前选中的名称 -default：全部
    private int level;//等级 暂时分 省/市/县/直属校/学校/年纪/学科
    private String levelName ;
    private String url ;
    private HashMap<String, String> params ;
    private FilterEntity parent ;//父类节点
    private List<FilterEntity> children; //孩子节点集合 1 to N .
    private boolean isCheck;//是否选中
    private boolean hasDirect ;//是否有直属校 -仅地区有效

    private void init(){
        // init
        params = new HashMap<>();
        params.put("uuid", uuid);
        switch (getLevel()){
            case FilterEntity.LEVEL_AREA://1.地区
            case FilterEntity.LEVEL_DIRECT://2.直属校
                if(!TextUtils.isEmpty(getId()))
                params.put("areaId", getId());
                break;
            case FilterEntity.LEVEL_CLASS_SEMESTER://3.学段
                //no more args .
                //选中的区域id
                if(!TextUtils.isEmpty(getCacheId())){
//                    params.put("areaId", getCacheId()); 级联选项
                    params.put("areaId", getCacheAreaId());//直接取当前用户的区域id ．
                }
                //父类分段的id .
//                if(!TextUtils.isEmpty(getCacheId())){
//                    params.put("schoolId",getCacheId());
//                }
                break;
            case FilterEntity.LEVEL_SCHOOL://4.学校
                //选中的区域id
                if(!TextUtils.isEmpty(getCacheId())){
                    params.put("areaId", getCacheId());
                }
                //父类分段的id .
                if(!TextUtils.isEmpty(getId())){
                    params.put("semesterId",getId());
                }
                break;
            case FilterEntity.LEVEL_CLASS_LEVEL://5. 年级
                if(!TextUtils.isEmpty(getCacheId())) {
                    params.put("areaId", getCacheId());
                }
                /** 此处不可去除，学校，老师，等获取本校的年纪 **/
                if(!TextUtils.isEmpty(getCacheSchoolId())){
                    params.put("schoolId",getCacheSchoolId());
                }
                break;
            case FilterEntity.LEVEL_CLASS_SUBJECT://6.普通筛选   学科.
                if(!TextUtils.isEmpty(getCacheId())) {
                    params.put("areaId", getCacheId());
                }
                if(!TextUtils.isEmpty(getCacheSchoolId())){
                    params.put("schoolId",getCacheSchoolId());
                }
                break;
            case FilterEntity.LEVEL_CLASS_CATEGORY://7. 分类
                // no more args .
                break;
        }
    }

    /**
     * 带第二个id为学校初始化使用》
     * @param uuid
     * @param id
     * @param name
     * @param level
     * @param levelName
     * @param url
     * @param isCheck
     * @param hasDirect
     */
    public FilterEntity(String uuid ,String id,String cacheId,String cacheSchoolId, String name, int level, String levelName, String url, boolean isCheck, boolean hasDirect) {
        this.uuid = uuid;
        this.id = id;
        this.cacheId = cacheId;
        this.name = name;
        this.level = level;
        this.levelName = levelName;
        this.url = url;
        this.isCheck = isCheck;
        this.hasDirect = hasDirect;
        this.cacheSchoolId = cacheSchoolId;
        init();
    }

    public String getCacheAreaId() {
        return cacheAreaId;
    }

    public void setCacheAreaId(String cacheAreaId) {
        this.cacheAreaId = cacheAreaId;
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        Cog.i(TAG ,"["+getName()+"]"+"leveName:"+getLevelName()+" set cache id : " +cacheId);
        this.cacheId = cacheId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isHasDirect() {
        return hasDirect;
    }

    public void setHasDirect(boolean hasDirect) {
        this.hasDirect = hasDirect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        Cog.i(TAG ,"["+getName()+"]"+"leveName:"+getLevelName()+" set id : " +id);
        this.id = id;
    }

    public String getCacheSchoolId() {
        return cacheSchoolId;
    }

    public void setCacheSchoolId(String cacheSchoolId) {
        this.cacheSchoolId = cacheSchoolId;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public FilterEntity getParent() {
        return parent;
    }

    public void setParent(FilterEntity parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getUrl() {
        //如果是直属校 ，url 为获取学校
        //如果是
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getParams() {
//        if(null == params &&  getLevel() > 0 ){
            init();
//        }
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public List<FilterEntity> getChildren() {
        return children;
    }

    public void setChildren(List<FilterEntity> children) {
        this.children = children;
    }


    /**
     * 根据LEVEL返回对应的 API .
     * @param levelName
     * @return
     */
    public static String getURL(String levelName) {
        if(TextUtils.isEmpty(levelName)) return "";
        String url = "" ;
        switch (levelName){
            case GroupFilterFragment.STR_PROVINCE:
            case GroupFilterFragment.STR_AREA:
            case GroupFilterFragment.STR_CITY:
                url = URLConfig.GET_AREA;
                break;
            case GroupFilterFragment.STR_SCHOOL:
                url = URLConfig.GET_DIRECT_SCHOOL;
                break;
            case GroupFilterFragment.STR_SEMESTER:
                url = URLConfig.GET_SEMESTER_LIST;
                break;
            case GroupFilterFragment.STR_TEAM://分组动态获取(userInfo.getTeam()) .
                url = "";
                break;
            case GroupFilterFragment.STR_LEVEL:
                url = URLConfig.ALL_CLASS_LEVEL;
                break;
            case GroupFilterFragment.STR_SUBJECT:
                url = URLConfig.ALL_SUBJECTS_LIST ;
                break;
            case GroupFilterFragment.STR_CATEGORY://分类-兴趣组
                url = URLConfig.GET_GROUP_CATEGORY_LIST ;
                break;
            default:
                url = URLConfig.GET_AREA ;
                break;
        }
        return url;
    }

    /**
     * 获取-筛选-左侧子节点-url API. 降一级给数据 .
     * @param levelName
     * @return
     */
    public static String getChildURL(String levelName , String filterType) {
        if(TextUtils.isEmpty(levelName)) return "";
        String url = "" ;
        switch (levelName){
            case GroupFilterFragment.STR_PROVINCE://省
            case GroupFilterFragment.STR_CITY://市
            case GroupFilterFragment.STR_AREA://县
                url = URLConfig.GET_AREA;
                break;
            case GroupFilterFragment.STR_SEMESTER://学段
                url = URLConfig.GET_DIRECT_SCHOOL;
                break;
            case GroupFilterFragment.STR_SCHOOL://学校
                if(GroupFilterFragment.TYPE_FILTER_TEACH.equals(filterType)){
                    url = URLConfig.ALL_CLASS_LEVEL;
                }else{
                    url = URLConfig.GET_GROUP_CATEGORY_LIST;
                }
            break;
            case GroupManagerFilterFragment.STR_TEAM://组别
                if(GroupManagerFilterFragment.TYPE_FILTER_GROUP_AREA.equals(filterType)){//辖区内
                    url = URLConfig.ALL_CLASS_LEVEL;
                }else{//校内
                    url = URLConfig.GET_GROUP_CATEGORY_LIST;
                }
                break;
            case GroupFilterFragment.STR_LEVEL://年纪
                url = URLConfig.ALL_SUBJECTS_LIST;
                break;
            case GroupFilterFragment.STR_SUBJECT://学科
                url = "";
                break;
            default://分类等...end !
                url = URLConfig.GET_AREA ;
                break;
        }
        return url;
    }

    /**
     * 应用-圈组筛选专用 ，中间固定选项 组别
     * @param levelName 级别名称
     * @param itemName item name (like 教研组/兴趣组)
     * @return url
     */
    public static String getChildURL2(String levelName , String itemName) {
        if(TextUtils.isEmpty(levelName)) return "";
        String url = "" ;
        switch (levelName){
            case GroupFilterFragment.STR_PROVINCE://省
            case GroupFilterFragment.STR_CITY://市
            case GroupFilterFragment.STR_AREA://县
                url = URLConfig.GET_AREA;
                break;
            case GroupFilterFragment.STR_SEMESTER://学段
                url = URLConfig.GET_DIRECT_SCHOOL;
                break;
            case GroupFilterFragment.STR_SCHOOL://学校
                url = URLConfig.GET_GROUP_CATEGORY_LIST;
                break;
            case GroupManagerFilterFragment.STR_TEAM://组别
                if(GroupManagerFilterFragment.STR_TEAM_TEACH.equals(itemName)){
                    //教研组
                    url = URLConfig.ALL_CLASS_LEVEL;
                }else{
                    //兴趣组
                    url = URLConfig.GET_GROUP_CATEGORY_LIST;
                }
                break;
            case GroupFilterFragment.STR_LEVEL://年纪
                url = URLConfig.ALL_SUBJECTS_LIST;
                break;
            case GroupFilterFragment.STR_SUBJECT://学科
                url = "" ;
                break;
            default://分类等...end !
                url = URLConfig.GET_AREA ;
                break;
        }
        return url;
    }

    /**
     * 获取-筛选-右侧-Level
     * @param levelName
     * @return
     */
    public static int getLevel(String levelName) {
        int level =  FilterEntity.LEVEL_AREA ;
        switch (levelName){
            case GroupFilterFragment.STR_PROVINCE:
            case GroupFilterFragment.STR_AREA:
            case GroupFilterFragment.STR_CITY:
                level = FilterEntity.LEVEL_AREA ;
                break;
            case GroupFilterFragment.STR_SCHOOL:
                level = FilterEntity.LEVEL_SCHOOL ;
                break;
            case GroupFilterFragment.STR_SEMESTER:
                level = FilterEntity.LEVEL_CLASS_SEMESTER;
                break;
            case GroupFilterFragment.STR_LEVEL:
                level = FilterEntity.LEVEL_CLASS_LEVEL ;
                break;
            case GroupFilterFragment.STR_SUBJECT:
                level = FilterEntity.LEVEL_CLASS_SUBJECT;
                break;
            case GroupFilterFragment.STR_CATEGORY:
                level = FilterEntity.LEVEL_CLASS_CATEGORY;
                break;
        }
        return level;
    }

    /**
     * 应用-圈组-筛选
     * 获取-筛选-左侧子节点的Level
     * @param levelName
     * @param arg2 参数2 item Name
     * @return
     */
    public static int getChildLevel(String levelName , String arg2) {
        int level =  FilterEntity.LEVEL_AREA ;
        switch (levelName){
            case GroupManagerFilterFragment.STR_PROVINCE:
            case GroupManagerFilterFragment.STR_AREA:
            case GroupManagerFilterFragment.STR_CITY:
                level = FilterEntity.LEVEL_AREA ;
                break;
            case GroupManagerFilterFragment.STR_SEMESTER:
                level = FilterEntity.LEVEL_SCHOOL;
                break;
            case GroupFilterFragment.STR_SCHOOL:
                level = FilterEntity.LEVEL_CLASS_TEAM ;
                break;
            case GroupManagerFilterFragment.STR_TEAM:
                if(arg2!=null && arg2.equals(GroupManagerFilterFragment.STR_TEAM_TEACH)){
                    //教研组-下一级“年级”
                    level = FilterEntity.LEVEL_CLASS_LEVEL ;
                }else {
                    //兴趣组-下一级"分类"
                    level = FilterEntity.LEVEL_CLASS_CATEGORY ;
                }
                break;
            case GroupManagerFilterFragment.STR_LEVEL:
                level = FilterEntity.LEVEL_CLASS_SUBJECT;
                break;
            case GroupManagerFilterFragment.STR_SUBJECT:
                level = FilterEntity.LEVEL_CLASS_END;
                break;
            case GroupManagerFilterFragment.STR_CATEGORY:
                level = FilterEntity.LEVEL_CLASS_END;
                break;
            case GroupManagerFilterFragment.STR_STATE://分类
                level = FilterEntity.LEVEL_CLASS_END;
                break;
        }
        return level;
    }
}

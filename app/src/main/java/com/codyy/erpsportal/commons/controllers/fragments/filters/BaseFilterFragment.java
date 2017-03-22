package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.filter.AreaItem;
import com.codyy.erpsportal.commons.models.entities.filter.AreaList;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.groups.controllers.viewholders.FilterChoiceViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.FilterConditionViewHolder;
import com.codyy.url.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *  公共的筛选接口
 * ------------
 * |全部 |北京  |
 * |item1|王府井|
 * |item2|学段  |
 * |item3|学校  |
 * |直属校|分类  |
 * -------------
 * Created by poe on 16-4-25.
 */
public abstract class BaseFilterFragment extends Fragment implements GroupFilterInterface{
    private static final String TAG = "BaseFilterFragment";
    /**应用-圈组－(辖区)内筛选**/
    public static final String TYPE_FILTER_GROUP_AREA = "filter.type.area";
    /**应用－圈组－(校)内筛选 **/
    public static final String TYPE_FILTER_GROUP_SCHOOL = "filter.type.school";
    /** 门户-教研组 **/
    public static final String TYPE_FILTER_TEACH = "filter.type.teach";
    /**门户-兴趣组 **/
    public static final String TYPE_FILTER_INTEREST = "filter.type.interest" ;
    /**应用 - 网络授课 **/
    public static final String TYPE_FILTER_NET_TEACH = "filter.type.net.teach";
    /**应用 - 网络授课-区域内授课 **/
    public static final String TYPE_FILTER_NET_TEACH_AREA_MANAGER = "filter.type.net.teach.area";
    /**实时轮巡 - 区域管理 **/
    public static final String TYPE_FILTER_LIVE_WATCH_AREA = "filter.type.watch.area";
    /**实时轮训　－　学校平台 **/
    public static final String TYPE_FILTER_LIVE_WATCH_SCHOOL = "filter.type.watch.school";
    /** **/
    public static final String EXTRA_TYPE = "com.extra.type";
    public static final int INIT = 1;
    public static final int CLICK_LEFT = 2;
    public static final int CLICK_RIGHT = 3;
    public static final String STR_PROVINCE = "省";
    public static final String STR_CITY = "市";
    public static final String STR_AREA = "县";
    public static final String STR_SEMESTER = "学段";
    public static final String STR_SCHOOL = "学校";
    public static final String STR_SCHOOL_DIRECT = "直属校";
    public static final String STR_CATEGORY = "分类" ;
    public static final String STR_TEAM = "组别";//INTEREST/TEACH
    public static final String STR_STATE = "状态";//(CLOSED/WAIT/APPROVED/REJECT) or (INIT/PROGRESS/CLOSED)
    public static final String STR_TEAM_TEACH = "教研组";
    public static final String STR_TEAM_INTEREST = "兴趣组";
    public static final String STR_LEVEL = "年级";
    public static final String STR_SUBJECT = "学科";
    public static final String STR_ALL = "全部";
    public static final String STR_STATE_PENDING = "待处理";

    @Bind(R.id.rcl_condition)RecyclerView mConditionRecyclerView;
    @Bind(R.id.rcl_choice) RecyclerView mChoiceRecyclerView;
    public String mFilterType = TYPE_FILTER_GROUP_AREA ;//筛选的类型 default:辖区内
    public List<FilterEntity> mBottom = new ArrayList<>();
    public BaseRecyclerAdapter<FilterEntity,FilterConditionViewHolder> mConditionAdapter;
    /**
     * 左侧筛选listview适配器
     */
    public BaseRecyclerAdapter<FilterEntity, FilterChoiceViewHolder> mChoiceAdapter;
    /**
     * 用户信息
     */
    public  UserInfo mUserInfo;
    /**
     * 左侧筛选listview适配器的数据源
     */
    public AreaList mAreaList = null;
    /**
     * 右侧筛选listview适配器的数据源-
     */
    public static LinkedList<FilterEntity> mData = new LinkedList<>();
    /**
     * 保存右侧点击了哪一项
     */
    public int mRightClickPosition = 0;
    /**
     * 左侧菜单点位置
     */
    public int mLeftClickPosition = 0 ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        if(null != getArguments()){
            mFilterType     =   getArguments().getString(EXTRA_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_filter, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 左侧点击item
     * @param position position
     * @param fe FilterEntity
     */
    public abstract void onLeftItemClick(int position , FilterEntity fe);

    /**
     * 右侧点击item
     * @param position position
     * @param fe FilterEntity
     */
    public abstract void onRightItemClick(int position , FilterEntity fe);

    /**
     * 添加底部固定选项（or 固定+不定选项)
     */
    public abstract void addBottom();

    @Override
    public void initView() {
        Cog.e(TAG, "Filter Fragment initView()");
        mConditionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mConditionRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        setAdapter();
    }

    private void setAdapter() {
        mConditionAdapter   =   new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<FilterConditionViewHolder>() {
            @Override
            public FilterConditionViewHolder createViewHolder(ViewGroup parent, int viewType) {
               return  new FilterConditionViewHolder(LayoutInflater.from(parent.getContext()).
                       inflate(R.layout.item_live_right_filter, parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mConditionAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterEntity>() {
            @Override
            public void onItemClicked(View v, int position, FilterEntity data) {
                onRightSelected(position , data );
            }
        });
        mConditionRecyclerView.setAdapter(mConditionAdapter);
        mChoiceAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<FilterChoiceViewHolder>() {
            @Override
            public FilterChoiceViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return  new FilterChoiceViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_live_left_filter, parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mChoiceAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterEntity>() {
            @Override
            public void onItemClicked(View v, int position, FilterEntity data) {
                onLeftSelected(position , data);
            }
        });
        mChoiceRecyclerView.setAdapter(mChoiceAdapter);
    }

    @Override
    public void initData() {
        //add base data .
        addBottom();
        //load more ....
        if(mFilterType.equals(TYPE_FILTER_NET_TEACH)||TYPE_FILTER_LIVE_WATCH_SCHOOL.equals(mFilterType)){
            //do nothing !
        }else{
            if (UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())) {
                //区域管理员-级别
                FilterEntity filterEntity = new FilterEntity(mUserInfo.getUuid(),mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),mUserInfo.getBaseAreaId(),STR_ALL,FilterEntity.LEVEL_AREA,STR_ALL, URLConfig.GET_AREA,false,false);
                loadData(filterEntity, INIT, INIT);
            }else{
                // TODO: 16-8-9 校级管理员需要动态获取分段+学校 .
                FilterEntity filterEntity = new FilterEntity(mUserInfo.getUuid(),mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),mUserInfo.getBaseAreaId(),STR_ALL,FilterEntity.LEVEL_CLASS_SEMESTER,STR_ALL,FilterEntity.getURL(STR_AREA),false,false);
                mConditionAdapter.setData(mData);
                //校管理员-级别
                updateNextCondition(filterEntity);
            }
        }
    }

    @Override
    public void onLeftSelected(int position, FilterEntity fe) {
        onLeftItemClick(position , fe);
    }

    @Override
    public void onRightSelected(int position, FilterEntity fe) {
        onRightItemClick(position , fe);
    }

    @Override
    public void updateNextCondition(FilterEntity fe) {
        int nextPos = mRightClickPosition+1;
        if(nextPos >= mData.size()){
            return;
        }
        Cog.i(TAG," updateNextCondition rightClickPos:" +mRightClickPosition+" nextPos:"+ nextPos );
        switch (fe.getLevel()){
            case FilterEntity.LEVEL_AREA://区域选择了 All/直属校 对应更新学段和学校的cacheId .
            case FilterEntity.LEVEL_CLASS_SEMESTER://学段 (点击了直属校 or 县（区）最后一级)
                for(int i = 0 ; i < mData.size() ;i++){
                    Cog.i(TAG,mData.get(i).getLevelName()+" update Next condition :: set cacheId :"+fe.getId());
                    //v5.3.0需求，年纪学科不在级联
                    if(mData.get(i).getLevel()==FilterEntity.LEVEL_CLASS_LEVEL || mData.get(i).getLevel() == FilterEntity.LEVEL_CLASS_SUBJECT){

                    }else{
                        mData.get(i).setCacheId(fe.getId());
                    }
                }
                break;
            case FilterEntity.LEVEL_SCHOOL://学校
                //学校id为schoolId，parent中记录areaId .
                mData.get(nextPos).setId(fe.getId());
                mData.get(nextPos).setCacheId(fe.getCacheId());
                break;
            case FilterEntity.LEVEL_CLASS_TEAM://分组
                // TODO: 16-3-17 如何传递学校的id到年纪（只有点了教研组） （点兴趣组-增加“分类”）
                mData.get(nextPos).setId(fe.getId());
                break;
            case FilterEntity.LEVEL_CLASS_LEVEL://年级
            case FilterEntity.LEVEL_CLASS_SUBJECT://学科
            case FilterEntity.LEVEL_CLASS_CATEGORY://分类
            case FilterEntity.LEVEL_CLASS_STATE://状态
                mData.get(nextPos).setId(fe.getId());
                break;
            default:
                mData.get(mRightClickPosition).setId(fe.getId());
                break;
        }
    }

    @Override
    public void updateChildren(FilterEntity filterEntity) {
        Cog.i(TAG,"updateChildren:id 【"+filterEntity.getId()+"】 : 【"+filterEntity.getLevelName() +filterEntity.getName()+"】"+" cacheID 【"+ filterEntity.getCacheId()+"】");
        List<AreaItem> list = mAreaList.getAreaItemlist();
        if(list != null && list.size() >0 ){
            List<FilterEntity> feList = new ArrayList<>();
            //ALL 包含在数据中
            for(AreaItem ai : list){
                int level =  FilterEntity.getChildLevel(mAreaList.getLevelName(),ai.getAreaName());
                String url = FilterEntity.getChildURL2(mAreaList.getLevelName(),ai.getAreaName());
                //网络授课的筛选需要对学校做特殊处理 - 1.学校的左侧筛选条件获取中国的年级. 2.地区的直属校直接获取学校 .
                if(TYPE_FILTER_NET_TEACH_AREA_MANAGER.equals(mFilterType)){
                    if(GroupFilterFragment.STR_SCHOOL.equals(mAreaList.getLevelName())){
                        level = FilterEntity.LEVEL_CLASS_LEVEL;
                        url = URLConfig.ALL_CLASS_LEVEL;
                    }
                }
                FilterEntity filter = new FilterEntity(mUserInfo.getUuid(),ai.getAreaId(),filterEntity.getCacheId(),filterEntity.getCacheSchoolId(),ai.getAreaName(),level,mAreaList.getLevelName(),url,ai.getAreaName().equals(filterEntity.getName()),false);


                switch (filterEntity.getLevel()){
                    case FilterEntity.LEVEL_AREA:
                        //全部 ｏｒ　直属校
                        if(STR_ALL.equals(ai.getAreaName()) || STR_SCHOOL_DIRECT.equals(ai.getAreaName())){
                            //只有Area地方才需要id，固定筛选""代表全部
                            filter.setId(filterEntity.getId());
                            //直属校　直接获取对应区域的ｉｄ　. －－－＞获取学段
                            filter.setCacheId(filterEntity.getId());
                        }else{
                            //普通地区选项不传递ｉｄ，使用门户ｉｄ传递－－>
                            filter.setCacheId(mUserInfo.getBaseAreaId());
                        }
                        break;
                    case FilterEntity.LEVEL_CLASS_SEMESTER://== 直属校 or 县（区） 缓存下区县的id，暂时无用 地区id在refreshNextPeriod方法中已经设置.
                    case FilterEntity.LEVEL_SCHOOL://学校：错位了 将一级 == semester . 学校学要cacheId（areaId) .
                    case FilterEntity.LEVEL_CLASS_TEAM://组别：同上 == school .
                        filter.setCacheId(filterEntity.getCacheId());
                        break;
                    case FilterEntity.LEVEL_CLASS_LEVEL://年级：同上 点击教研组
                    case FilterEntity.LEVEL_CLASS_SUBJECT://同上 == level .
//                        filter.setCacheId(filterEntity.getCacheId());
//                        filter.setCacheSchoolId(filterEntity.getCacheSchoolId());
                        break;
                    case FilterEntity.LEVEL_CLASS_CATEGORY://点击了分类 ，筛选的结尾
                    case FilterEntity.LEVEL_CLASS_STATE://点击了结尾
                        filter.setCacheId(filterEntity.getCacheId());
                        break;
                }
                feList.add(filter);
            }
            //直属校 id 为父类的id
            if(mAreaList.getHasDirect()!=null && mAreaList.getHasDirect().equals("Y")){
                Cog.i(TAG,"update Children() Direct School add new ()~ ~! cached id :"+filterEntity.getCacheId());
                //判断是否有直属校
                filterEntity.setHasDirect(true);
                // TODO: 16-9-27 单独处理　无分组信息的情况
                String id = filterEntity.getId();
                //直属校根据当前选中状态．
                String cacheId = filterEntity.getId();
                String area_name = STR_SEMESTER ;
                if(mFilterType.equals(TYPE_FILTER_NET_TEACH_AREA_MANAGER)){
                    area_name = STR_SCHOOL ;
                    id = "";//此处置空　为了获取直属校学校数据不传递无效的ＳｅｍｅｓｔｅｒＩｄ．
                    Cog.i(TAG,"直属校：id"+id +" cacheId :" + filterEntity.getCacheId() +" level:"+filterEntity.getLevelName());
                }

                FilterEntity fe = new FilterEntity(mUserInfo.getUuid(),id,cacheId,filterEntity.getCacheSchoolId(),STR_SCHOOL_DIRECT,FilterEntity.getLevel(area_name),area_name,FilterEntity.getURL(area_name),STR_SCHOOL_DIRECT.equals(filterEntity.getName()),true);
                fe.setCacheAreaId(mUserInfo.getBaseAreaId());
                feList.add(fe);
            }
            filterEntity.setChildren(feList);
            mChoiceAdapter.setData(feList);
        }
    }

    @Override
    public void refreshOrigin(FilterEntity fe) {
        //添加第一级目录
        if (TextUtils.isEmpty(mAreaList.getLevelName().trim())) {
            updateNextCondition(fe);
            return;
        }
        //非固定选区
        if(mUserInfo.getUserType().equals("AREA_USR")){//区域管理者-级别
            FilterEntity filterEntity = new FilterEntity(mUserInfo.getUuid(),mUserInfo.getBaseAreaId(),mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.getLevel(mAreaList.getLevelName()),mAreaList.getLevelName(),FilterEntity.getURL(mAreaList.getLevelName()),true,mAreaList.getHasDirect().equals("Y"));
            filterEntity.setSelectedId(mUserInfo.getBaseAreaId());
            //新增数据.
            updateChildren(filterEntity);
            mData.add(0, filterEntity);
            mRightClickPosition =   0;
            mConditionAdapter.setData(mData);
            updateNextCondition(filterEntity);
        }
    }

    @Override
    public void refreshLeft(FilterEntity fe) {
        if(TextUtils.isEmpty(mAreaList.getLevelName())){//地区选择结束位置
            //分段 or 学校　
            if(mData.size()>=mBottom.size()){//人工+1 完成学段的获取 .
                if((mData.size()- (mBottom.size()+mRightClickPosition+1))>0){//防止下面还有多余的地区机构夹在当前位置和固定筛选区域内
                    Cog.i(TAG,"Delete 多余的区市县机构！@@##￥@！！");
                    clearRightData();
                    addBottom();
                }
                //clear 后需要重新设置id .
                updateNextCondition(fe);
                if(mFilterType.equals(TYPE_FILTER_NET_TEACH_AREA_MANAGER) || TYPE_FILTER_LIVE_WATCH_AREA.equals(mFilterType)){
                    loadData(mData.get(mData.size()-mBottom.size()),CLICK_LEFT,AreaList.TYPE_PARSE_SCHOOL);
                }else{
                    loadData(mData.get(mData.size()-mBottom.size()),CLICK_LEFT,AreaList.TYPE_PARSE_SEMESTER);
                }
            }
            //更新数据
            updateChildren(mData.get(mRightClickPosition));
        }else if(STR_SCHOOL_DIRECT.equals(fe.getName())) {//直属校
            Cog.i(TAG,"refreshNewCondition -------------------直属校  set id : "+fe.getId() +" LEVEL["+fe.getLevel()+fe.getLevelName()+"]");
            clearRightData();
            addBottom();
            updateNextCondition(fe);
            //无论新增的学校 or 已存在 向下跳一级
            if(mRightClickPosition<(mData.size()-1)){
                mRightClickPosition++;
            }
            if(mData.get(mRightClickPosition).getLevel()>=fe.getLevel()){
                mData.get(mRightClickPosition).setId(fe.getId());
            }
            //更新数据
            updateChildren(mData.get(mRightClickPosition));
        }else{
            Cog.i(TAG," >< fe id :"+fe.getId()+" <>Level : "+fe.getLevel()+ " ["+fe.getLevelName()+"]");
            if (FilterEntity.LEVEL_AREA == fe.getLevel()) {//地区动态添加数据
                clearRightData();
                addBottom();
                addItem(fe.getId(),fe.getCacheId(),fe.getCacheSchoolId(),STR_ALL,FilterEntity.getLevel(mAreaList.getLevelName()),mAreaList.getLevelName(),FilterEntity.getURL(mAreaList.getLevelName()), mAreaList.getHasDirect() != null && mAreaList.getHasDirect().equals("Y"),fe);
                updateNextCondition(fe);
                //无论新增的学校 or 已存在 向下跳一级
                if(mRightClickPosition<(mData.size()-1)){
                    mRightClickPosition++;
                }
            }else {//固定的数据请求
                //无论新增的学校 or 已存在 向下跳一级
                if(mRightClickPosition<(mData.size()-1)){
                    mRightClickPosition++;
                }
                Cog.i(TAG,"now level :"+mData.get(mRightClickPosition).getLevel() + " fe.Level:"+fe.getLevel());
                switch (fe.getLevel()){
                    case FilterEntity.LEVEL_CLASS_SEMESTER://学段 (点击了直属校 or 县（区）最后一级)
                        //在学段实体记录下上面的areaId , 提供下面的学校使用 .
                        if(mData.get(mRightClickPosition).getLevel()>=fe.getLevel()){
                            mData.get(mRightClickPosition).setId(fe.getId());
                        }
                        break;
                    case FilterEntity.LEVEL_SCHOOL://学校
                        //学校id为schoolId，parent中记录areaId .
                        if(mData.get(mRightClickPosition).getLevel()>=fe.getLevel()){
                            mData.get(mRightClickPosition).setId(fe.getId());
                            mData.get(mRightClickPosition).setCacheId(fe.getCacheId());
                        }
                        break;
                    case FilterEntity.LEVEL_CLASS_TEAM://组别
                        if(mData.get(mRightClickPosition).getLevel()>=fe.getLevel()){
                            mData.get(mRightClickPosition).setId(fe.getId());
//                            mData.get(mRightClickPosition).setCacheSchoolId(fe.getId());
                        }
                        break;
                    case FilterEntity.LEVEL_CLASS_LEVEL://年级
                    case FilterEntity.LEVEL_CLASS_SUBJECT://学科
                    case FilterEntity.LEVEL_CLASS_CATEGORY://分类
                    case FilterEntity.LEVEL_CLASS_STATE://状态
                        if(mData.get(mRightClickPosition).getLevel()>=fe.getLevel()){
                            mData.get(mRightClickPosition).setId(fe.getId());
//                            mData.get(mRightClickPosition).setCacheSchoolId(fe.getCacheSchoolId());
                        }
                        break;
                }
                //更新数据
                updateChildren(mData.get(mRightClickPosition));
            }
        }
        updateItemBackground();
    }

    @Override
    public void refreshRight(FilterEntity fe) {
        //到最后一级了
        updateChildren(mData.get(mRightClickPosition));
        mChoiceAdapter.setData(mData.get(mRightClickPosition).getChildren());
    }

    /**
     * 清除clickposition之后的所有数据
     */
    public void clearRightData() {
        //清楚多余的item
        int count = mData.size();
        //All 则清空下面的非固定选项
        for (int j = 0; j < (count - (mRightClickPosition+1) ); j++) {
            mData.remove(mRightClickPosition + 1);
        }
    }

    /**
     * 获取对应级别的位置
     * @param levelName levelName
     * @return position .
     */
    public int getConditionPosition(String levelName) {
        int pos = -1;
        if(null != levelName && null != mData && mData.size()>0){
            for(int i=0 ; i< mData.size() ;i ++){
                if(levelName.equals(mData.get(i).getLevelName())){
                    pos = i;
                    break;
                }
            }
        }
        return  pos;
    }

    public int getConditionPosition(FilterEntity fe , List<FilterEntity> data) {
        int pos = -1;
        if(null != fe && null != data && data.size()>0){
            for(int i=0 ; i< data.size() ;i ++){
                if(fe.equals(data.get(i))){
                    pos = i;
                    break;
                }
            }
        }
        return  pos;
    }

    /**
     * 判断是否添加了 item
     * @param levelName levelName
     * @return true ：已经存在 false: 不存在
     */
    public boolean hasLevel(String levelName){
        boolean result = false ;
        for(int i=0 ; i < mData.size() ; i++){
            if(mData.get(i).getLevelName().equals(levelName)){
                result =true ;
                break;
            }
        }
        return result;
    }

    /**
     * 选中listview时Item背景变化
     */
    public void updateItemBackground() {
        if(mRightClickPosition < mData.size()){
            for(int i = 0; i< mData.size() ; i++){
                if(i == mRightClickPosition){
                    mData.get(i).setCheck(true);
                }else{
                    mData.get(i).setCheck(false);
                }
            }
        }
        mConditionAdapter.notifyDataSetChanged();
    }

    public void updateChoiceItemBackground(List<FilterEntity> list) {
        if(null == list) return;
        //update the choice item .
        for (int i = 0 ; i< list.size() ;i ++) {
            if(mLeftClickPosition == i){
                list.get(i).setCheck(true);
            }else{
                list.get(i).setCheck(false);
            }
        }
        mChoiceAdapter.notifyDataSetChanged();
    }
    /**
     * 加载数据
     * @param filterEntity filterEntity
     * @param flag INIT/CLICK_LEFT/CLICK_RIGHT
     * @param parseType 1:地区 2：学校 3.年级 4.学科 5.学段
     */
    public void loadData(final FilterEntity filterEntity, final int flag, final int parseType) {
        Cog.e(TAG, "Filter Fragment loadData()" + " flag:" + flag + " parseType:" + parseType);
        Cog.i(TAG,"loadData:id 【"+filterEntity.getId()+"】 : 【"+filterEntity.getLevelName() +filterEntity.getName()+"】"+" cacheID 【"+ filterEntity.getCacheId()+"】");
        WebApi webApi = RsGenerator.create(WebApi.class);
        //请求中禁止点击事件
        mChoiceRecyclerView.setEnabled(false);
        Cog.e(TAG, "loadData:url=" + filterEntity.getUrl() + "?" + filterEntity.getParams());
        webApi.post4Json(filterEntity.getUrl(), filterEntity.getParams())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onResponse response:" + response);
                        try {
                            //根据item的value来初始化左侧列表
                            if (flag == INIT) {
                                mAreaList = AreaList.parse(response, parseType, STR_ALL);
                            } else {
                                mAreaList = AreaList.parse(response, parseType, mData.get(mRightClickPosition).getLevelName());//java.lang.IndexOutOfBoundsException
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (mAreaList == null) {
                            UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                            //刷新左侧数据
                            if (flag == CLICK_RIGHT) {
                                mAreaList = new AreaList();
                                refreshRight(filterEntity);
                            }
                            return;
                        } else {
                            if (flag == INIT) {
                                refreshOrigin(filterEntity);
                            } else if (flag == CLICK_LEFT) {
                                refreshLeft(filterEntity);
                            } else {
                                refreshRight(filterEntity);
                            }
                        }
                        if (null != mChoiceRecyclerView)
                            mChoiceRecyclerView.setEnabled(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error:" + error);
                        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                        if(null != mChoiceRecyclerView)
                        mChoiceRecyclerView.setEnabled(true);
                    }
                });
    }

    /**
     * 添加一个item
     */
    public void addItem( String id,String cacheId ,String cacheSchoolId,String name,int level ,String levelName ,String url , boolean hasDirect , FilterEntity parent) {
        FilterEntity filter = new FilterEntity(mUserInfo.getUuid(),id,cacheId,cacheSchoolId,name,level,levelName,url,true,hasDirect);
        filter.setParent(parent);
        updateChildren(filter);
        mData.get(mRightClickPosition).setCheck(false);
        //增加一个区域item
//        if((mRightClickPosition+1)<mData.size()){
            mData.add(mRightClickPosition + 1, filter);
//        }
        mConditionAdapter.notifyDataSetChanged();
    }

    @Override
    public Bundle getFilterData() {
        Bundle bundle = new Bundle();
        //是否有直属校
        Boolean hasDirect = false;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getName().equals(STR_SCHOOL_DIRECT)) {
                hasDirect = true;
            }
        }
        bundle.putBoolean("hasDirect", hasDirect);
        if ("AREA_USR".equals(mUserInfo.getUserType())) {
            int lastAreaPosition = getConditionPosition(STR_SEMESTER);
            //区域id
            if(lastAreaPosition>0){
                if(!TextUtils.isEmpty(mData.get(lastAreaPosition-1).getSelectedId())){
                    bundle.putString("areaId", mData.get(getConditionPosition(STR_SEMESTER)-1).getSelectedId());
                }else if((lastAreaPosition-2>=0)&&!TextUtils.isEmpty(mData.get(lastAreaPosition-2).getSelectedId())){
                    bundle.putString("areaId", mData.get(getConditionPosition(STR_SEMESTER)-2).getSelectedId());
                }
            }else{
                bundle.putString("areaId", mUserInfo.getBaseAreaId());
            }
        }else{
            bundle.putString("areaId", mUserInfo.getBaseAreaId());
        }
        //学段
        if(getConditionPosition(STR_SEMESTER)>=0){
            bundle.putString("semester",mData.get(getConditionPosition(STR_SEMESTER)).getSelectedId());
        }
        //学校
        if(getConditionPosition(STR_SCHOOL)>=0){
            FilterEntity feSchool = mData.get(getConditionPosition(STR_SCHOOL));
            //过滤ALL选项
            bundle.putString("directSchoolId",STR_ALL.equals(feSchool.getName())?"":feSchool.getSelectedId());//直属校id
        }
        //组别
        if(getConditionPosition(STR_TEAM)>=0){
            FilterEntity team = mData.get(getConditionPosition(STR_TEAM));
            if(team != null ){
                switch (team.getName()){
                    case STR_TEAM_INTEREST:
                        bundle.putString("groupType","INTEREST");
                        break;
                    case STR_TEAM_TEACH:
                        bundle.putString("groupType","TEACH");
                        break;
                    default:
                        bundle.putString("groupType","");
                        break;
                }
            }
        }else{
            if(getConditionPosition(STR_LEVEL)>=0){
                bundle.putString("groupType","TEACH");
            }else if(getConditionPosition(STR_CATEGORY)>=0){
                bundle.putString("groupType","INTEREST");
            }else{
                bundle.putString("groupType","");
            }
        }
        //年级
        if(getConditionPosition(STR_LEVEL)>=0){
            bundle.putString("class", mData.get(getConditionPosition(STR_LEVEL)).getSelectedId());//年级
        }
        //学科
        if(getConditionPosition(STR_SUBJECT)>=0){
            bundle.putString("subject",  mData.get(getConditionPosition(STR_SUBJECT)).getSelectedId());//学科
        }
        //分类
        if(getConditionPosition(STR_CATEGORY)>=0){
            bundle.putString("category",  mData.get(getConditionPosition(STR_CATEGORY)).getSelectedId());//分类
        }
        //状态
        if(getConditionPosition(STR_STATE)>= 0){
            bundle.putString("state", mData.get(getConditionPosition(STR_STATE)).getSelectedId());
        }
        return bundle;
    }

    @Override
    public void onDestroy() {
        if (mData != null) {
            mData.clear();
        }
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    protected String getCacheSchoolId() {
        String cacheSchoolId = mUserInfo.getSchoolId();
        if(mData.size()>0){
            FilterEntity cache =  mData.get(mData.size()-1);
            if(!TextUtils.isEmpty(cache.getCacheSchoolId())){
                cacheSchoolId = cache.getCacheSchoolId();
            }
        }
        return cacheSchoolId;
    }

    protected String getCacheAreaId() {
        String cacheId = mUserInfo.getBaseAreaId();
        if(mData.size()>0){//获取顶级ｉｔｅｍ的区域ｉｄ.
            cacheId = mData.get(mData.size()-1).getCacheId();
        }
        return cacheId;
    }

    /**
     * 获取年纪信息
     * ｛＃v5.3.0 注释掉的为级联代码，目前与ｗｅｂ保持一致性，不需要级联｝
     * @return FilterEntity
     */
    protected FilterEntity getClassLevel(){
        /*String cacheId = getCacheAreaId();
        String cacheSchoolId = getCacheSchoolId();
        FilterEntity fe = new FilterEntity(mUserInfo.getUuid(),"",cacheId,cacheSchoolId,STR_ALL,FilterEntity.LEVEL_CLASS_LEVEL,STR_LEVEL,FilterEntity.getURL(STR_LEVEL),false,false);*/
        FilterEntity fe = new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_LEVEL,STR_LEVEL,FilterEntity.getURL(STR_LEVEL),false,false);
        return fe;
    }

    /**
     * 获取班级信息
     * ｛＃v5.3.0 注释掉的为级联代码，目前与ｗｅｂ保持一致性，不需要级联｝
     * @return FilterEntity
     */
    protected FilterEntity getSubject(){
       /* String cacheId = getCacheAreaId();
        String cacheSchoolId = getCacheSchoolId();
        FilterEntity fe =new FilterEntity(mUserInfo.getUuid(),"",cacheId,cacheSchoolId,STR_ALL,FilterEntity.LEVEL_CLASS_SUBJECT,STR_SUBJECT,FilterEntity.getURL(STR_SUBJECT),false,false);*/
        FilterEntity fe =new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_SUBJECT,STR_SUBJECT,FilterEntity.getURL(STR_SUBJECT),false,false);
        return fe;
    }

}

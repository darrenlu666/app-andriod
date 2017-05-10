package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.SimpleTextViewHolder;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.groups.models.entities.Category;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import butterknife.Bind;

/**
 * 单个recyclerView博文分类列表筛选
 * 自带http数据获取
 * 选中提供回调{@link BaseRecyclerAdapter.OnItemClickListener}
 * 只要实现此接口的Activity都可以回调
 * Created by poe on 2016/1/25 .
 */
public  class CategoryFilterFragment extends BaseHttpFragment {
    private static final String TAG = "SimpleListFilterFragment";
    /**
     * 个人博文
     */
    public static final String CATEGORY_TYPE_PERSON = "type.blog.person";
    /**
     * 门户博文
     */
    public static final String CATEGORY_TYPE_DOOR = "type.blog.door";
    /**
     * 圈组博文N
     */
    public static final String CATEGORY_TYPE_GROUP = "type.blog.group";


    private static String EXTRA_FILTER_TYPE = "type";
    private static String EXTRA_FILTER_GROUP_ID = "groupId";
    private static String EXTRA_VISIT_USER_ID = "visit.userId";

    @Bind(R.id.rcl_condition) RecyclerView mConditionRecyclerView;
    private BaseRecyclerAdapter.OnItemClickListener mOnItemClickListener;//点击事件
    private BaseRecyclerAdapter<FilterEntity,SimpleTextViewHolder> mConditionAdapter;
    /**
     * 右侧筛选listview适配器的数据源-
     */
    private static LinkedList<FilterEntity> mData = new LinkedList<>();
    /**
     * 保存右侧点击了哪一项
     */
    private int mRightClickPosition = 0;
    private String mType = CATEGORY_TYPE_PERSON ;
    private String mVisitedUserId = "";
    private String mGroupId = "";

    public static CategoryFilterFragment create(String type){
        CategoryFilterFragment filter = new CategoryFilterFragment();
        Bundle bd = new Bundle();
        bd.putString(EXTRA_FILTER_TYPE ,type);
        bd.putString(EXTRA_VISIT_USER_ID , "");
        bd.putString(EXTRA_FILTER_GROUP_ID , "");
        filter.setArguments(bd);
        return filter;
    }

    /**
     *
     * @param type
     * @param visitedUserId 个人博文专用－访客模式
     * @param groupID　　　圈组博文－专用
     * @return
     */
    public static CategoryFilterFragment create(String type , String visitedUserId ,String groupID){
        CategoryFilterFragment filter = new CategoryFilterFragment();
        Bundle bd = new Bundle();
        bd.putString(EXTRA_FILTER_TYPE ,type);
        bd.putString(EXTRA_VISIT_USER_ID , visitedUserId);
        bd.putString(EXTRA_FILTER_GROUP_ID , groupID);
        filter.setArguments(bd);
        return filter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //链接到父类Activity的OnItemClick
        mOnItemClickListener = (BaseRecyclerAdapter.OnItemClickListener) getActivity();
        if(null != getArguments()){
            mType = getArguments().getString(EXTRA_FILTER_TYPE);
            mVisitedUserId  =   getArguments().getString(EXTRA_VISIT_USER_ID);
            mGroupId    =   getArguments().getString(EXTRA_FILTER_GROUP_ID);
        }
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_category_filter;
    }

    @Override
    public String obtainAPI() {
        if(CATEGORY_TYPE_GROUP.equals(mType)){//圈组博文分类
            return URLConfig.GET_HOME_GROUP_BLOG_POST_CATEGORY_INFO;
        }else if(CATEGORY_TYPE_DOOR.equals(mType)) {//首页博文分类
            return URLConfig.GET_HOME_BLOG_POST_CATEGORY_INFO;
        }else{//个人博文分类
            return URLConfig.GET_BLOG_CATEGORY_LIST;
        }
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String,String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if(CATEGORY_TYPE_GROUP.equals(mType)){
            params.put("groupId", mGroupId);
//            params.put("visitedUserId", TextUtils.isEmpty(mVisitedUserId)?mUserInfo.getBaseUserId():mVisitedUserId);
        }else{
            params.put("schoolId", TextUtils.isEmpty(mUserInfo.getSchoolId())?"":mUserInfo.getSchoolId());
            params.put("baseAreaId", mUserInfo.getBaseAreaId());
            params.put("visitedUserId", TextUtils.isEmpty(mVisitedUserId)?mUserInfo.getBaseUserId():mVisitedUserId);
        }

        return params;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
        if(null == mConditionRecyclerView ) return;
        if(isRefreshing) mData.clear();
        if (response.has("list")) {
            mData.clear();
            JSONArray areas = response.optJSONArray("list");
            for (int i = 0; i < areas.length(); i++) {
                JSONObject areasItem = (JSONObject) areas.opt(i);
                Category category = new Gson().fromJson(areasItem.toString(),Category.class);
                FilterEntity fe = new FilterEntity(mUserInfo.getUuid(),category.getGroupCategoryId(),mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),category.getCategoryName(),FilterEntity.LEVEL_AREA,GroupFilterFragment.STR_AREA,"",false,false);
                mData.add(fe);
            }
            mConditionAdapter.setData(mData);
        }
    }

    @Override
    public void onFailure(Throwable error) {
        //do nothing ...
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        requestData(true);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        Cog.e(TAG, "Filter Fragment initView()");
        mConditionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mConditionRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        setAdapter();
    }

    private void setAdapter() {
        mConditionAdapter   =   new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<SimpleTextViewHolder>() {
            @Override
            public SimpleTextViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new SimpleTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_filter_simple_text, parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mConditionAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterEntity>() {
            @Override
            public void onItemClicked(View v, int position, FilterEntity data) {
                if(null != mOnItemClickListener){
                    try {
                        mOnItemClickListener.onItemClicked(v,position,data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //update click position .
                mRightClickPosition =   getConditionPosition(data,mData);
                updateItemBackground();
            }
        });
        mConditionRecyclerView.setAdapter(mConditionAdapter);
    }

    /**
     * 选中listView时Item背景变化
     */
    void updateItemBackground() {
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

    private int getConditionPosition(FilterEntity fe , List<FilterEntity> data) {
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

    @Override
    public void onDestroy() {
        if (mData != null) mData.clear();
        super.onDestroy();
    }
}



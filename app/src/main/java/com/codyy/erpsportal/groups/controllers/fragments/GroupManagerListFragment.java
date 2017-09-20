package com.codyy.erpsportal.groups.controllers.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.interfaces.SimpleRecyclerDelegate;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.groups.controllers.activities.GroupSpaceActivity;
import com.codyy.erpsportal.groups.controllers.activities.GroupSpaceDetailManagerActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupManagerViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.groups.models.entities.GroupTeaching;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * 应用-圈组管理-列表
 * Created by poe on 16-1-20.
 */
public class GroupManagerListFragment extends SimpleRecyclerFragment<Group> {
    public final static String TAG = "GroupManagerListFragment";
    public final static String ARG_LIST_TYPE = "com.list.type";//类型参数
    public final static String TYPE_MY_LIST = "MY_LIST";//我的圈组
    public final static String TYPE_MANAGER_AREA = "AREA_MANAGE";//辖区内圈组
    public final static String TYPE_MANAGER_SCHOOL = "SCHOOL_MANAGE";//校内圈组
    public final static int REQUEST_CODE_GROUP_MANAGER = 0x210 ;
    /**
     * 此类型fragment {@link GroupManagerViewHolder#ITEM_TYPE_GROUP_MANAGER}
     * defalult : 我的圈组
     */
    private int mViewHolderType = GroupManagerViewHolder.ITEM_TYPE_GROUP_MY;
    private String mBaseAreaId;//地区id
    private String mSchoolId;//学校id
    private String mSemesterId;//学段id
    private String mGroupType;//组别(默认全部)（TEACH/INTEREST)
    private String mGradeId;//年级
    private String mSubjectId;//学科
    private String mCategoryId ;//分类id
    private String mState;//String	状态(默认全部) (CLOSED/WAIT/APPROVED/REJECT)
    private String mType = "MY_LIST";//圈组类型type	String	类型（辖区内AREA_MANAGE 校内SCHOOL_MANAGE 我的MY_LIST）
    private String mDirectFlag = "nodirect";//DIRECTLY  or NODIRECT

    public static GroupManagerListFragment newInstance(String type){
        GroupManagerListFragment fragment = new GroupManagerListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TYPE, type);
        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            mType   = getArguments().getString(ARG_LIST_TYPE);
            if(null != mType){
                if(mType.equals(TYPE_MANAGER_AREA)){
                    mViewHolderType = GroupManagerViewHolder.ITEM_TYPE_GROUP_MANAGER;
                }
                if(mType.equals(TYPE_MANAGER_SCHOOL)){
                    mViewHolderType = GroupManagerViewHolder.ITEM_TYPE_GROUP_SCHOOL;
                }
                if(mType.equals(TYPE_MY_LIST)){
                    mViewHolderType = GroupManagerViewHolder.ITEM_TYPE_GROUP_MY;
                }
            }
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mBaseAreaId = mUserInfo.getBaseAreaId();
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GROUP_MANAGER && resultCode == Activity.RESULT_OK){
            refresh();
        }
    }

    public void refresh(Bundle bd){
        if(null != bd){
            mBaseAreaId = bd.getString("areaId");
            boolean hasDirectory = bd.getBoolean("hasDirect");
            mDirectFlag = hasDirectory?"directly":"nodirect";
            mSemesterId   =   bd.getString("semester");
            mSchoolId   =   bd.getString("directSchoolId");
            mGroupType  =   bd.getString("groupType");
            mGradeId      =   bd.getString("class");
            mSubjectId    =   bd.getString("subject");
            mCategoryId   =   bd.getString("category");
            mState         =    bd.getString("state");
            refresh();
        }
    }

    @Override
    public SimpleRecyclerDelegate<Group> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<Group>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_GROUP_MANAGER_LIST;
            }

            /**
             * directFlag	boolean	直属校（默认false，非直属)
             * @return
             */
            @Override
            public HashMap<String, String> getParams(boolean isRefreshing) {
                HashMap hashMap = new HashMap();
                if(null != mUserInfo) hashMap.put("uuid" , mUserInfo.getUuid());
                if(null != mBaseAreaId) hashMap.put("areaId",mBaseAreaId);
                if(null != mSchoolId) hashMap.put("schoolId",mSchoolId);
                if(null != mSemesterId) hashMap.put("semesterId",mSemesterId);
                if(null != mGroupType) hashMap.put("groupType",mGroupType);
                if(null != mGradeId) hashMap.put("gradeId",mGradeId);
                if(null != mSubjectId) hashMap.put("subjectId",mSubjectId);
                if(null != mCategoryId) hashMap.put("categoryId",mCategoryId);
                if(null != mState) hashMap.put("state",mState);
                if(null != mType) hashMap.put("type", mType);
                if(null != mDirectFlag) hashMap.put("directFlag",mDirectFlag);
                hashMap.put("start",mDataList.size()+"");
                hashMap.put("end",(mDataList.size()+sPageCount-1)+"");
                return hashMap;
            }

            @Override
            public void parseData(JSONObject response,boolean isRefreshing) {
                GroupTeaching groupTeaching = new Gson().fromJson(response.toString(),GroupTeaching.class);
                if(null != groupTeaching && null != groupTeaching.getTotal()) {
                    mTotal  =   Integer.parseInt(groupTeaching.getTotal());
                    if (groupTeaching.getDataList() != null && groupTeaching.getDataList().size() > 0) {
                        for (Group group : groupTeaching.getDataList()) {
                            group.setBaseViewHoldType(mViewHolderType);
                            mDataList.add(group);
                        }
                    }
                }
            }

            @Override
            public BaseRecyclerViewHolder<Group> getViewHolder(ViewGroup parent, int viewType) {
                return new GroupManagerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_group_manager,parent,false));
            }

            @Override
            public void OnItemClicked(View v, int position, Group data) {
                switch (data.getBaseViewHoldType()){
                    case GroupManagerViewHolder.ITEM_TYPE_GROUP_MANAGER://辖区内
                    case GroupManagerViewHolder.ITEM_TYPE_GROUP_SCHOOL://校内的
                        GroupSpaceDetailManagerActivity.start(GroupManagerListFragment.this,data.getGroupId(),REQUEST_CODE_GROUP_MANAGER);
                        break;
                    case GroupManagerViewHolder.ITEM_TYPE_GROUP_MY://我的
                        if(data.getJoinStatus().equals("APPROVED")){
                            if(!"Y".equals(data.getClosedFlag())){
                                if(!"REJECT".equals(data.getApproveStatus())){
                                    GroupSpaceActivity.start(getActivity(), Titles.sWorkspaceGroup,data.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_GROUP);
                                }
                            }else{
                                //审核中 或者 拒绝 只要不是圈主就可以继续
                                if(!"CREATOR".equals(data.getUserType())){
                                    switch (data.getJoinStatus()){
                                        case "APPROVED"://加入的圈组没有 “通过”状态
                                        case "WAIT":
                                            GroupSpaceActivity.start(getActivity(), Titles.sWorkspaceGroup,data.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_GROUP);
                                            break;
                                        case "REJECT":
                                            //do nothing ...
                                            break;
                                        default:
                                            GroupSpaceActivity.start(getActivity(), Titles.sWorkspaceGroup,data.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_GROUP);
                                            break;
                                    }
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public int getTotal() {
                return mTotal;
            }
        };
    }
}

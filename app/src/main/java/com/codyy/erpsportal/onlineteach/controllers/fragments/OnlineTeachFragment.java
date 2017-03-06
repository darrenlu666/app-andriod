package com.codyy.erpsportal.onlineteach.controllers.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerFragment;
import com.codyy.erpsportal.onlineteach.models.entities.NetTeach;
import com.codyy.erpsportal.onlineteach.models.entities.NetTeachParse;
import com.codyy.erpsportal.onlineteach.controllers.activities.OnlineTeachDetailActivity;
import com.codyy.erpsportal.onlineteach.controllers.viewholders.NetTeachManagerViewHolder;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * 应用-网络授课列表
 * Created by poe on 16-6-21.
 */
public class OnlineTeachFragment extends SimpleRecyclerFragment<NetTeach> {
    public final static String TAG = "GroupManagerListFragment";
    public final static String ARG_LIST_TYPE = "com.list.type";
    public final static String TYPE_MY_CREATE = "MY_CREATE";//我创建的
    public final static String TYPE_MANAGER_AREA = "AREA_MANAGE";//区内课程管理
    public final static String TYPE_MANAGER_SCHOOL = "SCHOOL_MANAGE";//本校课程管理
    public final static String TYPE_MY = "MY_LIST";//我的课程
    public final static int REQUEST_CODE_GROUP_MANAGER = 0x210 ;
    private int mViewHolderType = NetTeachManagerViewHolder.ITEM_TYPE_MY;

    private String mBaseAreaId;//地区id
    private String mSchoolId;//学校id 　．
    private String mGradeId;//年级
    private String mSubjectId;//学科
    private String mStatus;//String	未开始INIT，进行中PROGRESS，已结束END
    private String mType = TYPE_MY;//圈组类型type	String	类型（辖区内AREA_MANAGE 校内SCHOOL_MANAGE 我的MY_LIST）

    public static OnlineTeachFragment newInstance(String type){
        OnlineTeachFragment fragment = new OnlineTeachFragment();
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
                    mViewHolderType = NetTeachManagerViewHolder.ITEM_TYPE_MANAGER;
                }
                if(mType.equals(TYPE_MANAGER_SCHOOL)){
                    mViewHolderType = NetTeachManagerViewHolder.ITEM_TYPE_SCHOOL;
                }
                if(mType.equals(TYPE_MY_CREATE)){
                    mViewHolderType = NetTeachManagerViewHolder.ITEM_TYPE_MY_CREATE;
                }
                if(mType.equals(TYPE_MY)){
                    mViewHolderType = NetTeachManagerViewHolder.ITEM_TYPE_MY;
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
            mBaseAreaId   =   bd.getString("areaId");
            mGradeId      =   bd.getString("class");
            mSubjectId    =   bd.getString("subject");
            mStatus       =   bd.getString("state");
            mSchoolId     =   bd.getString("directSchoolId");
            refresh();
        }
    }

    @Override
    public SimpleRecyclerDelegate<NetTeach> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<NetTeach>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_MY_CREATE_TEACH_LIST;
            }

            /**
             * directFlag	boolean	直属校（默认false，非直属)
             * @return
             */
            @Override
            public HashMap<String, String> getParams() {
                HashMap hashMap = new HashMap();
                if(null != mUserInfo) hashMap.put("uuid" , mUserInfo.getUuid());
                if(null != mBaseAreaId) hashMap.put("areaId",mBaseAreaId);
                if(!TextUtils.isEmpty(mSchoolId)) hashMap.put("schoolId",mSchoolId);
                if(null != mGradeId) hashMap.put("gradeId",mGradeId);
                if(null != mSubjectId) hashMap.put("subjectId",mSubjectId);
                if(null != mStatus) hashMap.put("status",mStatus);
                if(null != mType) hashMap.put("type", mType);

                hashMap.put("start",mDataList.size()+"");
                hashMap.put("end",(mDataList.size()+sPageCount-1)+"");
                return hashMap;
            }

            @Override
            public void parseData(JSONObject response) {
                NetTeachParse parse = new Gson().fromJson(response.toString() , NetTeachParse.class);
                if(null != parse) {
                    mTotal  =   parse.getTotal();
                    if (parse.getData() != null && parse.getData().size() > 0) {
                        for (NetTeach group : parse.getData()) {
                            group.setBaseViewHoldType(mViewHolderType);
                            mDataList.add(group);
                        }
                    }
                }
            }

            @Override
            public BaseRecyclerViewHolder<NetTeach> getViewHolder(ViewGroup parent) {
                return new NetTeachManagerViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_net_teach));
            }

            @Override
            public void OnItemClicked(View v, int position, NetTeach data) {
                OnlineTeachDetailActivity.start(getActivity() , data.getLessonId());
            }

            @Override
            public int getTotal() {
                return mTotal;
            }

        };
    }
}

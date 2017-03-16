package com.codyy.erpsportal.classroom.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.Watcher;
import com.codyy.erpsportal.classroom.models.WatcherParse;
import com.codyy.erpsportal.classroom.viewholder.PeopleTreeViewHolder;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.groups.controllers.activities.BlogPostDetailActivity;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerFragment;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * v5.3.3 customized classroom need show the amount of all watch people .
 * Created by poe on 17-3-13.
 */
public class PeopleTreeFragment extends SimpleRecyclerFragment<Watcher> {
    private final static String TAG = "PeopleTreeFragment";
    private static final String ARG_CLASS_ID = "class.id";

    private String mClassId;//class id .
    private String mUpdateTime;//the last one request return updateTime .


    public static PeopleTreeFragment newInstance(UserInfo userInfo, String scheduleDetailId) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.USER_INFO, userInfo);
        args.putString(ARG_CLASS_ID, scheduleDetailId);
        PeopleTreeFragment fragment = new PeopleTreeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            mClassId  = getArguments().getString(ARG_CLASS_ID);
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        //to get the people datasoiurce .
        initData();
    }

    @Override
    public SimpleRecyclerDelegate<Watcher> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<Watcher>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_CUSTOMER_LIVING_WATCHER_LIST;
            }

            @Override
            public HashMap<String, String> getParams(boolean isRefreshing) {
                HashMap<String, String> param = new HashMap<>();
                if(null != mClassId) param.put("clsScheduleDetailId",mClassId);
                if(null != mUserInfo){
                    param.put("uuid",mUserInfo.getUuid());
                    if(mUserInfo.isParent() && mUserInfo.getSelectedChild()!=null){
                        param.put("studentId",mUserInfo.getSelectedChild().getStudentId());
                    }
                }
                param.put("count",String.valueOf(sPageCount));
                if(null != mUpdateTime && !isRefreshing) param.put("updateTime",mUpdateTime);
                return param;
            }

            @Override
            public void parseData(JSONObject response,boolean isRefreshing) {
                WatcherParse parse = new Gson().fromJson(response.toString(),WatcherParse.class);
                if(null != parse){
                    //do something .
                    mTotal  =   parse.getTotal();
                    if (parse.getData() != null && parse.getData().size() > 0) {
                        for (Watcher group : parse.getData()) {
                            group.setBaseViewHoldType(0);
                            mDataList.add(group);
                        }
                    }
                    // record the newest update time .(tips : order updateTime desc .)
                    if(mDataList!= null && mDataList.size()>0){
                        mUpdateTime = mDataList.get(0).getUpdateTime();
                    }
                }
            }

            @Override
            public BaseRecyclerViewHolder<Watcher> getViewHolder(ViewGroup parent,int viewType) {
                return new PeopleTreeViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(), R.layout.item_custom_living_people_tree));
            }

            @Override
            public void OnItemClicked(View v, int position, Watcher data) {
                //go tho person intro .
                /*显示观看过此直播视频的总人数（不统计来自授课平台用户，隔30秒局部刷新一次数据），
                点击标签显示用户列表。列表显示用户头像、姓名、所属区域、学校名称、
                用户身份类型（管理员、教师、学生、家长）、年级班级。
                点击头像进入个人访客页面，点击管理员头像无响应。
                列表数据规则：
                1、按照用户进入观看的时间倒序排列；
                2、省市县校用户身份类型为管理员；
                3、所属区域显示示例：xx市（市级管理员）、xx县（教师），即所属区域的最后一个节点；
                4、省市县管理员用户，不显示学校名称、年级班级；
                5、学校管理员用户，不显示年级班级；
                6、教师用户不显示年级班级；
                7、家长用户所属区县、学校、年级班级显示绑定的第一个小孩属性*/
                switch (data.getUserType()){
                    case  UserInfo.USER_TYPE_AREA_USER:
                    case UserInfo.USER_TYPE_SCHOOL_USER:
                        //do nothing .
                        break;
                    case UserInfo.USER_TYPE_TEACHER:
                    case UserInfo.USER_TYPE_STUDENT:
                    case UserInfo.USER_TYPE_PARENT:
                        if(data.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                            MainActivity.start(getActivity() , mUserInfo , 2);
                        }else{//2.访客
                            PublicUserActivity.start(getActivity() , data.getBaseUserId());
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

package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.ClassMemberActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.FunctionParentAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.ApplicationChildViewHold;
import com.codyy.erpsportal.commons.controllers.viewholders.ApplicationViewHold;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.utils.UserFragmentUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.RecyclerTabLayoutSimple;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleVerticalDivider;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AppInfo;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UpdatePortalEvent;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.my.TeacherClassParse;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 应用页（first level ）
 * Created by gujiajia on 2015/7/15.
 * modified by poe on 2015/11/25 .
 */
public class FunctionFragment extends BaseHttpFragment {
    private static final String TAG = "FunctionFragment";
    public static final int ITEM_COUNT_CHILD = 4;//二级目录列数

    @Bind(R.id.recycler_tab_layout)RecyclerTabLayoutSimple mRecyclerTabLayout;//孩子列表
    @Bind(R.id.rcv_frag_function) RecyclerView mRecyclerView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;

//    private StudentParse mStudentParse;
    protected List<AppInfo> mData;
    private FunctionParentAdapter mParentAdapter ;
    private List<Student> mStudents = new ArrayList<>();//家长名下孩子集合 .
    private List<ClassCont> mClassList = new ArrayList<>();//老师下面的班级.
    //new adapter .
    private BaseRecyclerAdapter<AppInfo , BaseRecyclerViewHolder<AppInfo>> mAdapter ;
    private int mLastExpandPos = -1 ;
    public static int sCurrentPosition = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_fuction;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_PARENT_CHILDREN;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
            data.put("userId",mUserInfo.getBaseUserId());
        }
        return data;
    }

    @Override
    public void onSuccess(JSONObject response) {
        Cog.d(TAG,response != null ?response.toString():" null ");
        if(null == mRecyclerView ) return;
        List<Student> mStudentList = Student.parseData(response);
        //mStudentParse = new Gson().fromJson(response.toString(), StudentParse.class);
        if (null != mStudentList) {
            mUserInfo.setChildList(mStudentList);
            if (mStudentList.size() > 0) {
                //if first get the result . set default first student as the selected student by parent .
                if (mUserInfo.getSelectedChild() == null) {
                    mUserInfo.setSelectedChild(mStudentList.get(0));
                }
                mStudents = mStudentList;
            }
            mParentAdapter = new FunctionParentAdapter(mStudents,mRecyclerTabLayout, new RecyclerTabLayoutSimple.OnItemClickListener() {
                @Override
                public void OnClick(int position) {
                    mRecyclerTabLayout.setCurrentItem(position,true);
                    mParentAdapter.notifyDataSetChanged();
                    Student student = mStudents.get(position);
                    Cog.d(TAG, "onChildClick student mUserInfo.getSelectedChild()=", mUserInfo.getSelectedChild());
                    if (!mUserInfo.getSelectedChild().equals(student)) {
                        Cog.d(TAG, "onNewChildSelected schoolId=", student.getSchoolId());
                        mUserInfo.setSelectedChild(student);
                        EventBus.getDefault().post(new UpdatePortalEvent());
                        loadData();
                    }
                }

                @Override
                public int getDataSize() {
                    return mStudents.size();
                }
            });

            mRecyclerTabLayout.setUpWithAdapter(mParentAdapter);
            loadData();
        }
    }

    @Override
    public void onFailure(VolleyError error) {
        if(null == mRecyclerView ) return;
        if(mEmptyView.isLoading()){
            mEmptyView.setLoading(false);
        }
        loadData();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean newLogin = false;
        if(null != mUserInfo){
            if(!UserInfoKeeper.obtainUserInfo().getUuid().equals(mUserInfo.getUuid())){
                mUserInfo    =  UserInfoKeeper.getInstance().getUserInfo();
                newLogin    =   true ;
            }
        } else {
            mUserInfo = UserInfoKeeper.obtainUserInfo();
        }
        mClassList = mUserInfo.getClassList();
        //if load data failed , try it again .
        if(mData == null || mData.size() == 0){
            newLogin = true;
        }

        if(newLogin){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(true);
            //注册配置表
            ConfigBus.register(mModuleConfigListener);
            ConfigBus.getInstance().setLoadingHandler(new ConfigBus.LoadingHandler() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onError() {
                    if(null == mRecyclerView ) return;
                    mEmptyView.setLoading(false);
                    requestData();
                }
            });
        }
        //load data .
        if(UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())){
            mRecyclerTabLayout.setVisibility(View.VISIBLE);
            requestData();
        }else{
            mRecyclerTabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        initView();
    }

    private void initView() {
        mRecyclerTabLayout.setPositionThreshold(0.5f);
        mRecyclerTabLayout.setIndicatorHeight(10);
        mRecyclerTabLayout.setIndicatorModel(RecyclerTabLayoutSimple.MODE_INDICATOR_BOTTOM);
        mRecyclerTabLayout.setMinimumHeight((int) (getResources().getDisplayMetrics().density * 200));
        mRecyclerTabLayout.setAutoSelectionMode(true);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData();
                if(UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())){
                    mRecyclerTabLayout.setVisibility(View.VISIBLE);
                    requestData();
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(position <mData.size() && mAdapter.getItemViewType(position) == ApplicationViewHold.ITEM_TYPE_CHILD){
                    return  3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //set item animation .
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //set divider
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_vertical_line);
        mRecyclerView.addItemDecoration(new SimpleVerticalDivider(divider));
        //set adapter .http://test.codyy.cn/mobile/ImageServlet/default/img/workspace/new_hdtk.png
        mAdapter    =   new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<AppInfo>>() {
            @Override
            public BaseRecyclerViewHolder<AppInfo> createViewHolder(final ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType){
                    case ApplicationViewHold.ITEM_TYPE_SINGLE://单独点击
                    case ApplicationViewHold.ITEM_TYPE_MULTI://show children view with animation .
                    case ApplicationViewHold.ITEM_TYPE_SINGLE_EMPTY://
                        viewHolder  =   new ApplicationViewHold(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_child,parent,false));
                        break;
                    case ApplicationViewHold.ITEM_TYPE_CHILD://children views mode .
                        viewHolder  =   new ApplicationChildViewHold(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_child_container,parent,false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<AppInfo>() {
            @Override
            public void onItemClicked(View v, int position, AppInfo data) {
                switch (mAdapter.getItemViewType(position)){
                    case ApplicationViewHold.ITEM_TYPE_SINGLE://单独点击
                        if("class.member.id".equals(data.getMenuID())){//班级成员
                            mRecyclerView.setEnabled(false);
                            if(UserInfo.USER_TYPE_STUDENT.equals(mUserInfo.getUserType())){//学生
                                if(null !=mClassList && mClassList.size() == 0){
                                    mClassList.add(new ClassCont(mUserInfo.getBaseClassId() ,mUserInfo.getBaseClassName(),mUserInfo.getClasslevelName()));
                                }
                                ClassMemberActivity.start(getActivity(), mUserInfo, mUserInfo.getBaseClassId(), mUserInfo.getBaseClassName(),mClassList,ClassMemberActivity.TYPE_FROM_APPLICATION);
                            }else if (UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())) {//家长
                                //家长
                                if (mUserInfo.getSelectedChild() != null) {
                                    ClassMemberActivity.start(getActivity(), mUserInfo, mUserInfo.getSelectedChild().getClassId(), mUserInfo.getSelectedChild().getClassName(), UserFragmentUtils.constructClassListInfo(mStudents),ClassMemberActivity.TYPE_FROM_APPLICATION);
                                }
                            } else if(UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())){//老师
                                mClassList  =   mUserInfo.getClassList();
                                if(mClassList == null || mClassList.size()==0){
                                    // TODO: 16-3-20 获取班级信息.... && 弹出选择框 ! .
                                    loadClassData();
                                }else{
                                    showSelectClassDialog();
                                }
                            }
                            if("group.id".equals(data.getMenuID())) { //圈组
                                switch (mUserInfo.getUserType()) {
                                    case UserInfo.USER_TYPE_AREA_USER:
                                    case UserInfo.USER_TYPE_SCHOOL_USER:
                                    case UserInfo.TEAM_TYPE_TEACH:
                                        AppConfig.jumpToActivity(data.getJumpable(), getActivity());
                                        break;
                                    case UserInfo.USER_TYPE_STUDENT:
                                    case UserInfo.USER_TYPE_PARENT:
                                        if (mUserInfo.isHasTeam(UserInfo.TEAM_TYPE_INTEREST)) {
                                            AppConfig.jumpToActivity(data.getJumpable(), getActivity());
                                        } else {
                                            ToastUtil.showToast("权限不够！");
                                        }
                                        break;
                                }
                            }
                        }else{
                            AppConfig.jumpToActivity(data.getJumpable(),getActivity());
                        }
                        break;
                    case ApplicationViewHold.ITEM_TYPE_SINGLE_EMPTY:
                        // TODO: 16-8-27 do nothing ... with any click event .
                        break;
                    case ApplicationViewHold.ITEM_TYPE_MULTI://show children view with animation .
                            if(data.getChildGroups()!=null && data.getChildGroups().size()>0){
                                Cog.i(TAG," last pos: "+mLastExpandPos +" current : "+position);
                                //判断是否已经展开
                                int deletePos = removeChildItem();
                                if(deletePos >0 ){
                                    mAdapter.setData(mData);
                                }
                                //计算是否是同一个位置
                                if(mLastExpandPos == position){
                                    //collapse 。
                                    mLastExpandPos      =   -1;
                                    sCurrentPosition    =   -1;
                                }else{//expand .
                                    try {
                                        int lastClick = mLastExpandPos ;
                                        int pos = position+(3-position%3);
                                        //重新计算位置...
                                        if(deletePos < pos){
                                            if(position>0 && deletePos>0){
                                                position = position - 1;
                                            }
                                            pos = position+(3-position%3);
                                        }else if(deletePos == pos ){
                                            Cog.i(TAG," deletePos pos: "+deletePos +"expand pos : "+pos);
                                        }
                                        //记录上次展开的位置
                                        mLastExpandPos = position;
                                        //记录本次展开的位置
                                        sCurrentPosition    =   position ;
                                        AppInfo newApp = data.clone();
                                        newApp.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_CHILD);
                                         if(Math.abs(lastClick-position)<3){
                                            newApp.setStartPos(lastClick%3);
                                        }else{//从0开始
                                            newApp.setStartPos(-1);
                                        }
                                        newApp.setTargetPos(position%3);
                                        if(pos > mData.size()){
                                            for(int i = 0 ;i < (pos-mData.size());i++){
                                                AppInfo appInfo  = new AppInfo();
                                                appInfo.setBaseViewHoldType(ApplicationViewHold.ITEM_TYPE_SINGLE_EMPTY);
                                                mData.add(appInfo);
                                            }
                                        }
                                        mData.add(pos,newApp);
                                        mAdapter.setData(mData);
                                        mAdapter.notifyItemInserted(pos);
                                    } catch (CloneNotSupportedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        break;
                    case ApplicationViewHold.ITEM_TYPE_CHILD://children views mode .
                        AppConfig.jumpToActivity(data.getJumpable(), getActivity());
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 清楚已经打开的子Items
     */
    private int removeChildItem() {
        int result = -1;
        if(null != mData && mData.size() >0){
            for(int i = 0 ; i <mData.size() ; i++){
                if(ApplicationViewHold.ITEM_TYPE_CHILD == mData.get(i).getBaseViewHoldType()){
                    mData.remove(i);
                    result = i ;
                    break;
                }
            }
        }
        return  result ;
    }

    private ConfigBus.OnModuleConfigListener mModuleConfigListener = new ConfigBus.OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            //反注册防止/退出登录后引起无线死循环
            ConfigBus.unregister(mModuleConfigListener);
            AppConfig.instance().updateConfigTitles();
            requestData();
        }
    };

    //set the application datas
    private void loadData() {
        Cog.e(TAG,"loadData() ~~");
        if(null == mUserInfo || null == getActivity()){
            Cog.e(TAG,"Line 187 getActivity() is NULL ~~~!!!");
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo!=null?mUserInfo.getUuid():"");
        final String userType = mUserInfo!=null?mUserInfo.getUserType(): UserInfo.USER_TYPE_PARENT;
        if(null != mUserInfo && null != mUserInfo.getSelectedChild() ){
            params.put("studentId",mUserInfo.getSelectedChild().getStudentId());
        }
        requestData(URLConfig.URL_GET_APPS, params, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if(null == mRecyclerView ) return;
                if ("success".equals(response.optString("result"))) {
                    mData = AppInfo.parseData(response.optJSONArray("useList"),userType);
                    if(mData!=null && mData.size() >0 ){
                        mAdapter.setData(mData);
                    }
                }

                if(mData!=null&&mData.size()<=0){
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setLoading(false);
                }else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                if(null == mRecyclerView ) return;
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if(mData == null ||mData.size()<=0){
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setLoading(false);
                }else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 获取老师名下的班级信息
     * {"result":"success","teacherClassList":[]}
     */
    private void loadClassData() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("userId",mUserInfo.getBaseUserId());
        }
        requestData(URLConfig.GET_TEACHER_CLASS_LIST, data, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                if(null == mRecyclerView ) return;
                mRecyclerView.setEnabled(true);
                TeacherClassParse teacherClassParse = new Gson().fromJson(response.toString(),TeacherClassParse.class);
                if(null != teacherClassParse && teacherClassParse.getDataList()!=null && teacherClassParse.getDataList().size()>0){
                    mClassList = teacherClassParse.getDataList();
                    mUserInfo.setClassList(teacherClassParse.getDataList());
                    //弹出选择框
                    showSelectClassDialog();
                }else{
                    ToastUtil.showToast("暂无班级信息！");
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                if(null == mRecyclerView ) return;
                mRecyclerView.setEnabled(true);
                LogUtils.log(error);
                ToastUtil.showToast(error.getMessage());
            }
        });
    }

    /**
     * 弹出选择班级的选择框.
     */
    private void showSelectClassDialog() {
        if(mClassList!=null && mClassList.size()>0){
            ClassMemberActivity.start(getActivity(), mUserInfo, mClassList.get(0).getBaseClassId(), mClassList.get(0).getBaseClassName(),mClassList,ClassMemberActivity.TYPE_FROM_APPLICATION);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sCurrentPosition = -1;
    }
}

package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.groups.controllers.viewholders.SimpleTextViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.commons.models.entities.my.MyBaseTitle;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个recyclerView列表筛选
 * 选中提供回调{@link BaseRecyclerAdapter.OnItemClickListener}
 * 只要实现此接口的Activity都可以回调
 * Created by poe on 2016/1/25 .
 */
public  class SimpleListFilterFragment extends Fragment {
    private static final String TAG = "SimpleListFilterFragment";
    public static final String EXTRA_FILTER_CONDITION = "com.group.filter.selected";//选中的筛选条件
    public static final String EXTRA_FILTER_TITLES = "com.group.filter.titles";//选项
    public static final String STR_ALL = "全部";
    @Bind(R.id.rcl_condition) RecyclerView mConditionRecyclerView;
    @Bind(R.id.rcl_choice) RecyclerView mChoiceRecyclerView;
    private BaseRecyclerAdapter.OnItemClickListener mOnItemClickListener;//点击事件
    private List<FilterEntity> mBottom = new ArrayList<>();
    private List<MyBaseTitle> mTitles = new ArrayList<>();
    private BaseRecyclerAdapter<FilterEntity,SimpleTextViewHolder> mConditionAdapter;
    private  UserInfo mUserInfo;
    /**
     * 右侧筛选listview适配器的数据源-
     */
    private static LinkedList<FilterEntity> mData = new LinkedList<>();
    /**
     * 保存右侧点击了哪一项
     */
    private int mRightClickPosition = 0;

    /**
     * 实例化
     * @param titles
     * @return
     */
    public static SimpleListFilterFragment newInstance(ArrayList<MyBaseTitle> titles){

        SimpleListFilterFragment fragment = new SimpleListFilterFragment();
        Bundle bd = new Bundle();
        bd.putParcelableArrayList(SimpleListFilterFragment.EXTRA_FILTER_TITLES, titles);
        fragment.setArguments(bd);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mOnItemClickListener = (BaseRecyclerAdapter.OnItemClickListener) getActivity();
        if(null != getArguments()){
            mTitles =   getArguments().getParcelableArrayList(EXTRA_FILTER_TITLES);
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
     * 初始化布局
     */
    private void initView() {
        Cog.e(TAG, "Filter Fragment initView()");
        mConditionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChoiceRecyclerView.setVisibility(View.GONE);
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mConditionRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        setAdapter();
    }

    private void setAdapter() {
        mConditionAdapter   =   new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<SimpleTextViewHolder>() {
            @Override
            public SimpleTextViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new SimpleTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_simple_text, null));
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
                        mOnItemClickListener.onItemClicked(v ,position , data);
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

    private void initData() {
        //add base data .
        addBottom();
        if(mData.size() > 0){
            mConditionAdapter.setData(mData);
        }
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

    /**
     * 添加一个item
     */
    public void addItem( String id, String value,int level ,String levelName ,String url , boolean hasDirect , FilterEntity parent) {
        FilterEntity filter = new FilterEntity(mUserInfo.getUuid(),id,mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),value,level,levelName,url,true,hasDirect);
        filter.setParent(parent);
        mData.get(mRightClickPosition).setCheck(false);
        //增加一个区域item
        if((mRightClickPosition+1)<mData.size()){
            mData.add(mRightClickPosition + 1, filter);
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
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    /**
     * 添加底部的固定筛选条件
     */
    private void addBottom() {
        mBottom.clear();
        for(int i= 0 ;i <mTitles.size() ; i ++){
            mBottom.add(new FilterEntity(mUserInfo.getUuid(),mTitles.get(i).getBaseMenuId(),mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),mTitles.get(i).getMenuName(),FilterEntity.LEVEL_CLASS_SEMESTER,STR_ALL,FilterEntity.getURL(STR_ALL),false,false));
        }
        //add bottom .
        mData.addAll(mBottom);
    }

    /**
     * 返回筛选条件
     */
    public Bundle getFilterData() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FILTER_CONDITION,mData.get(mRightClickPosition).getName());//选中的数据
        return bundle;
    }
}



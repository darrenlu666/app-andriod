package com.codyy.tpmp.filterlibrary.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.tpmp.filterlibrary.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.entities.FilterCell;
import com.codyy.tpmp.filterlibrary.entities.FilterConstants;
import com.codyy.tpmp.filterlibrary.entities.FilterModule;
import com.codyy.tpmp.filterlibrary.entities.FilterUser;
import com.codyy.tpmp.filterlibrary.entities.interfaces.DataBuilder;
import com.codyy.tpmp.filterlibrary.entities.interfaces.FilterModuleInterface;
import com.codyy.tpmp.filterlibrary.interfaces.HttpGetInterface;
import com.codyy.tpmp.filterlibrary.utils.FilterUtil;
import com.codyy.tpmp.filterlibrary.viewholders.FilterChoiceViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.FilterConditionViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import java.util.LinkedList;
import java.util.List;

/**
 * 公共的筛选实现
 * 1. 实现可自由配置 -> ex.
 * 2. 高度解耦合
 * {@link com.codyy.tpmp.filterlibrary.entities.RemoteFilterBuilder}
 * {@link com.codyy.tpmp.filterlibrary.entities.SimpleFilterBuilder}
 * ex: 实现一个筛选->[省·学校·年级·学科·状态·类别]
 * int[] filterLevels = new int[]{
 * FilterConstants.LEVEL_AREA       //省
 * , FilterConstants.LEVEL_SCHOOL     //学校
 * , FilterConstants.LEVEL_CLASS_LEVEL//年纪
 * , FilterConstants.LEVEL_CLASS_SUBJECT//学科
 * , FilterConstants.LEVEL_CLASS_STATE //状态
 * , FilterConstants.LEVEL_LESSON_CATEGORY//类别
 * };
 * <p>
 * ------------
 * |全部 |北京  |
 * |item1|学校 |
 * |item2|年级 |
 * |item3|学科 |
 * |直属校|状态 |
 * -------------
 * Created by poe on 16-4-25.
 */
public class CommentFilterFragment extends Fragment {
    private static final String TAG = "CommentFilterFragment";
    RecyclerView mConditionRecyclerView;
    RecyclerView mChoiceRecyclerView;
    /**
     * 右侧筛选listview适配器
     */
    public BaseRecyclerAdapter<FilterModule, FilterConditionViewHolder> mConditionAdapter;
    /**
     * 左侧子筛选listview适配器
     */
    public BaseRecyclerAdapter<FilterCell, FilterChoiceViewHolder> mChoiceAdapter;
    /**
     * 筛选数据源-
     */
    public LinkedList<FilterModule> mData = new LinkedList<>();
    /**
     * 保存右侧点击了哪一项
     */
    public int mRightClickPosition = 0;
    /**
     * 左侧菜单点位置
     */
    public FilterUser mFilterUser;

    public HttpGetInterface mRequestSender;
    /**
     * Filter实现插拔的参数集合.
     * ex:int[] filterLevels = new int[]{
     * FilterConstants.LEVEL_AREA       //省
     * , FilterConstants.LEVEL_SCHOOL     //学校
     * , FilterConstants.LEVEL_CLASS_LEVEL//年级
     * , FilterConstants.LEVEL_CLASS_SUBJECT//学科
     * , FilterConstants.LEVEL_CLASS_STATE //状态
     * , FilterConstants.LEVEL_LESSON_CATEGORY//类别
     * };
     */
    public int[] mFilterLevels = new int[]{
              FilterConstants.LEVEL_AREA       //省
            , FilterConstants.LEVEL_SCHOOL     //学校
            , FilterConstants.LEVEL_CLASS_LEVEL//年级
            , FilterConstants.LEVEL_CLASS_SUBJECT//学科
    };

    /**
     * @param uuid
     * @param userType
     * @param baseAreaId
     * @param schoolId
     * @return
     */
    public static CommentFilterFragment newInstance(
            String uuid
            , String userType
            , String baseAreaId
            , String schoolId
            , int[] filers
    ) {
        CommentFilterFragment filterFragment = new CommentFilterFragment();
        Bundle bd = new Bundle();
        bd.putString(FilterUser.EXTRA_UUID, uuid);
        bd.putString(FilterUser.EXTRA_USER_TYPE, userType);
        bd.putString(FilterUser.EXTRA_BASE_AREA_ID, baseAreaId);
        bd.putString(FilterUser.EXTRA_SCHOOL_ID, schoolId);
        bd.putIntArray(FilterUser.EXTRA_FILTER_DATA, filers);
        filterFragment.setArguments(bd);
        return filterFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            //get user info .
            String uuid = getArguments().getString(FilterUser.EXTRA_UUID);
            String userType = getArguments().getString(FilterUser.EXTRA_USER_TYPE);
            String baseAreaId = getArguments().getString(FilterUser.EXTRA_BASE_AREA_ID);
            String schoolId = getArguments().getString(FilterUser.EXTRA_SCHOOL_ID);
            mFilterLevels = getArguments().getIntArray(FilterUser.EXTRA_FILTER_DATA);
            mFilterUser = new FilterUser(uuid, userType, baseAreaId, schoolId);
        }
        if(getActivity() instanceof  HttpGetInterface){
            mRequestSender = (HttpGetInterface) getActivity();
        }else{
            throw new RuntimeException("you should implements the net request interface HttpGetInterface!!!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_filter, container, false);
        mConditionRecyclerView = (RecyclerView) view.findViewById(R.id.rcl_condition);
        mChoiceRecyclerView = (RecyclerView) view.findViewById(R.id.rcl_choice);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"onViewCreated!");
        initView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated!"+mData.size());
        initData();
    }

    public void initView() {
        Log.e(TAG, "Filter Fragment initView()");
        mConditionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Drawable divider = FilterUtil.loadDrawable(getContext(), R.drawable.divider_filter);
        mConditionRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        setAdapter();
    }

    private void setAdapter() {
        mConditionAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<FilterConditionViewHolder>() {
            @Override
            public FilterConditionViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new FilterConditionViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_filter_right, parent, false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mConditionAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterModule>() {
            @Override
            public void onItemClicked(View v, int position, FilterModule data) {
                onRightSelected(position, data);
            }
        });
        mConditionRecyclerView.setAdapter(mConditionAdapter);
        mChoiceAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<FilterChoiceViewHolder>() {
            @Override
            public FilterChoiceViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new FilterChoiceViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_filter_left, parent, false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mChoiceAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterCell>() {
            @Override
            public void onItemClicked(View v, int position, FilterCell data) {
                Log.i(TAG, " onLeftSelected " + position + " right: " + mRightClickPosition);
                mChoiceAdapter.setEnable(false);
                onLeftSelected(position, data);
            }
        });
        mChoiceRecyclerView.setAdapter(mChoiceAdapter);
    }

    /**
     * 构建一个筛选结构
     */
    // 03/05/17 自由插拔任意多个筛选选项
    public void initData() {
        Log.i(TAG,"initData()!");
        for (int level : mFilterLevels) {
            FilterModule filterModule = null;
            switch (level) {
                case FilterConstants.LEVEL_AREA://remote
                case FilterConstants.LEVEL_SCHOOL://remote
                case FilterConstants.LEVEL_CLASS_LEVEL://remote
                case FilterConstants.LEVEL_CLASS_SUBJECT://remote
                case FilterConstants.LEVEL_CLASS_CATEGORY://remote
                case FilterConstants.LEVEL_CLASS_SEMESTER://remote
                    filterModule = FilterModule.constructRemoteModule(level, mFilterUser, mRequestSender);
                    break;
                case FilterConstants.LEVEL_CLASS_TEAM://组别
                case FilterConstants.LEVEL_CLASS_STATE://状态
                case FilterConstants.LEVEL_LESSON_CATEGORY://类别（系列课/单节课）
                case FilterConstants.LEVEL_MANAGER_STATE://状态
                    filterModule = FilterModule.constructSimpleModule(level, mFilterUser);
                    break;
            }

            //link next .
            if (mData.size() > 0) {
                FilterModule lastModule = mData.get(mData.size() - 1);
                lastModule.setNext(filterModule);
                filterModule.setLast(lastModule);
            }

            //add module .
            mData.add(filterModule);
        }
        mConditionAdapter.setData(mData);
        mConditionAdapter.notifyDataSetChanged();
        //load data # init()
        setSelected(0, true);
    }

    /**
     * 更新选中情况.
     *
     * @param position
     */
    public void setSelected(int position, boolean init) {
        Log.i(TAG, "#setSelected () :: " + position + " tag: " + init);
        if (position < 0 || position >= mData.size())
            throw new IllegalArgumentException("position is out of the mData index !");

        if (position != mRightClickPosition || init) {
            mData.get(mRightClickPosition).setSelected(false);
            mRightClickPosition = position;
            mData.get(mRightClickPosition).setSelected(true);
            mData.get(position).onItemClick(new DataBuilder.BuildListener<List<FilterCell>>() {
                @Override
                public void onSuccess(String levelName, List<FilterCell> result) {
                    synchronized (mData) {
                        FilterModule currentMode = mData.get(mRightClickPosition);
                        //判断是否数据对齐
                        if (null != result && result.size() > 0 && result.get(0).getParent() == currentMode.getData()) {
                            mChoiceAdapter.setData(result);
                            mChoiceAdapter.setEnable(true);
                            if (!TextUtils.isEmpty(levelName)) {//更新level 名称
                                mData.get(mRightClickPosition).getData().setLevelName(levelName);
                                mConditionAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.e(TAG, "#setSelected() :: Async Data Request result failed not teh request item here ~~!! BaseFilter");
//                            mChoiceAdapter.setEnable(true);
                        }
                    }
                }

                @Override
                public void onError(Throwable t) {
                    mChoiceAdapter.setEnable(true);
                }
            });

            mConditionAdapter.notifyDataSetChanged();
        }
    }

    public void onLeftSelected(int position, final FilterCell fe) {
        Log.i(TAG, " onLeftSelected " + position + " right: " + mRightClickPosition +" id:"+fe.getId());
        FilterModule currentModule = mData.get(mRightClickPosition);

        if (FilterModuleInterface.Mode.COMPLEX == currentModule.getType()) {//复杂布局-区域/组别
            if (FilterConstants.LEVEL_AREA == currentModule.getData().getLevel()) {
                //update children state of current module .
                executeLeftAreaClick(position, fe, currentModule);
            } else if (FilterConstants.LEVEL_CLASS_TEAM == currentModule.getData().getLevel()) {//组别
                // 05/05/17 组别的扩展.
                executeLeftTeamClick(position,fe);
            } else {
                // TODO: 05/05/17 扩展其他的处理情况.
                Log.e(TAG, "未知的筛选节点，暂未处理" + fe.getLevelName());
            }
        } else {
            //update children state of current module .
            currentModule.onChildrenClick(position, fe, null);
            //jump to next item .
            jumpToNextItem();
            mChoiceRecyclerView.setEnabled(true);
            mChoiceAdapter.setEnable(true);
        }
    }

    /**
     * 执行左侧的组别点击.
     * @param position
     * @param fe
     */
    private void executeLeftTeamClick(int position,FilterCell fe) {
        Log.i(TAG,"executeLeftTeamClick  "+position+" name: "+fe.getName());
        FilterModule currentModule = mData.get(mRightClickPosition);

        switch (fe.getName()) {
            case FilterConstants.STR_TEAM_INTEREST://兴趣组
                clearSubTeamModule();
                //add category .
                addSimpleModule(FilterConstants.LEVEL_CLASS_CATEGORY);
                break;
            case FilterConstants.STR_TEAM_TEACH://教研组
                clearSubTeamModule();
                //add level , subject .
                //倒序插入
                addSimpleModule(FilterConstants.LEVEL_CLASS_SUBJECT);
                addSimpleModule(FilterConstants.LEVEL_CLASS_LEVEL);
                break;
            case FilterConstants.STR_ALL://"全部"
                clearSubTeamModule();
                break;
        }

        //update children state of current module .
        currentModule.onChildrenClick(position, fe, null);
        //jump to next item .
        jumpToNextItem();
        mChoiceRecyclerView.setEnabled(true);
        mChoiceAdapter.setEnable(true);
    }

    /**
     * 处理左侧的区域点击事件.
     * @param position
     * @param fe
     * @param currentModule
     */
    private void executeLeftAreaClick(int position, FilterCell fe, FilterModule currentModule) {
        //update the next module param . {if (type == COMPLEX)}
        //  04/05/17 ~~更换区域需要更新余下的filterParam.areaId~~
        if(!hasAreaModel(currentModule.getSelectedId())) {
            currentModule.setSelectedId(fe.getId());
            updateNextCondition(currentModule);
        }
        //跳转.
        currentModule.onChildrenClick(position, fe, new DataBuilder.BuildListener<List<FilterCell>>() {

            @Override
            public void onSuccess(String levelName, List<FilterCell> result) {
                synchronized (mData) {
                    FilterModule nowModule = mData.get(mRightClickPosition);
                    //处理区域的点击
                    if (TextUtils.isEmpty(levelName)) {//已经到区县末端
                        clearAreaModule();
                        //跳转到下一个item
                        jumpToNextItem();
                    } else if (FilterConstants.STR_SCHOOL_DIRECT.equals(levelName)) {//直属校
                        // 全部/直属校

                        clearAreaModule();
                        mChoiceAdapter.setEnable(true);
                        mChoiceAdapter.notifyDataSetChanged();
                        mConditionAdapter.notifyDataSetChanged();
                        jumpToNextItem();
                    }else if(FilterConstants.STR_ALL.equals(levelName)){//全部.
                        // 全部/直属校
                        clearAreaModule();
                        mChoiceAdapter.setEnable(true);
                        mChoiceAdapter.notifyDataSetChanged();
                        mConditionAdapter.notifyDataSetChanged();
                    } else {//正常区域item->获取下级数据.

                        if(!hasAreaModel(nowModule.getSelectedId())){
                            //  04/05/17 新增节点
                            addNewAreaModel(levelName, result, nowModule);
                        }else{
                            jumpToNextItem();
                        }

                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                //更新.
                mChoiceAdapter.notifyDataSetChanged();
                mConditionAdapter.notifyDataSetChanged();
                mChoiceAdapter.setEnable(true);
            }
        });

    }

    /**
     * 添加非区域类item.
     *
     * @param level
     */
    private void addSimpleModule(int level) {
        FilterModule categoryModule = FilterModule.constructRemoteModule(
                level
                , mFilterUser
                , mRequestSender);
        FilterModule currentModule = mData.get(mRightClickPosition);
        FilterModule tempModule = currentModule.getNext();
        currentModule.setNext(categoryModule);
        categoryModule.setNext(tempModule);
        //添加数据
        int newPosition = mRightClickPosition + 1;
        if (newPosition < mData.size()) {
            mData.add(newPosition, categoryModule);
        } else {
            mData.add(categoryModule);
        }
    }

    /**
     * 清除组别的子模块
     */
    private void clearSubTeamModule() {
        int position = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (i > mRightClickPosition) {
                int level = mData.get(i).getData().getLevel();
                if (FilterConstants.LEVEL_CLASS_LEVEL == level
                        || FilterConstants.LEVEL_CLASS_SUBJECT == level
                        || FilterConstants.LEVEL_CLASS_CATEGORY == level) {
                    position = i;
                    break;
                }
            }
        }
        if (position > 0) {
            mData.remove(position);
            clearSubTeamModule();
        } else {
            return;
        }
    }

    /**
     * 1.向数据结构中间插入一条FilterModule
     * 2.跳转到这条数据上面
     *
     * @param levelName
     * @param result
     * @param currentModule
     */
    private void addNewAreaModel(String levelName, List<FilterCell> result, FilterModule currentModule) {
        Log.i(TAG, " addNewAreaModel () " + "levelName: " + levelName);
        // 03/05/17 新建一个Module  .
        FilterModule areaModule = FilterModule.constructRemoteModule(FilterConstants.getLevel(levelName), mFilterUser, mRequestSender);
        //set the id as last one select area id .
        areaModule.getData().setId(currentModule.getSelectedId());

        areaModule.getData().setLevelName(levelName);
        areaModule.getFilterParam().setAreaId(currentModule.getSelectedId());

        //处理孩子数据.
        if (result != null && result.size() > 0) {
            for (FilterCell fe : result) {
                fe.setParent(areaModule.getData());
            }
            areaModule.setChildrenData(result);
            areaModule.getFilterParam().setUpdate(false);
        }
        // 03/05/17 连接进入整个链表中.
        FilterModule temp = currentModule.getNext();
        currentModule.setNext(areaModule);
        areaModule.setLast(currentModule);
        areaModule.setNext(temp);
        // 03/05/17 更新数据.
        int newPosition = mRightClickPosition + 1;
        if (newPosition < mData.size()) {
            mData.add(newPosition, areaModule);
            setSelected(newPosition, false);
        } else {
            mData.add(areaModule);
            setSelected(newPosition, false);
        }
    }

    private void jumpToNextItem() {
        Log.i(TAG, "jumpToNextItem()----------------------------->---------->--------->");
        mChoiceAdapter.setEnable(false);
        int newPosition = mRightClickPosition + 1;
        if (newPosition < mData.size()) {
            setSelected(newPosition, false);
        } else {
            mChoiceAdapter.notifyDataSetChanged();
            mConditionAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 清除多余的area Module .
     * 当前点击位置之后的区域Module删除 .
     */
    private void clearAreaModule() {
        int position = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (i > mRightClickPosition && mData.get(i).getData().getLevel() == FilterConstants.LEVEL_AREA) {
                position = i;
                break;
            }
        }
        if (position > 0) {
            mData.remove(position);
            clearAreaModule();
        } else {
            return;
        }
    }

    //    @Override
    public void updateNextCondition(FilterModule currentModule) {
        updateNextConditionReverse(currentModule.getSelectedId(), currentModule);
        mConditionAdapter.notifyDataSetChanged();
    }

    /**
     * 递归更新孩子节点.
     *
     * @param areaId
     * @param currentModule
     */
    private void updateNextConditionReverse(String areaId, FilterModule currentModule) {
        Log.i(TAG," updateNextConditionReverse(String areaId, FilterModule currentModule) : current : "+mRightClickPosition+" select id :"+areaId);
        FilterModule next = currentModule.getNext();
        if (null != next) {
            next.getFilterParam().setAreaId(areaId);
            //  04/05/17 更换区域需要更新next.name=’全部‘
            next.getData().setName(FilterConstants.STR_ALL);
            next.clearSelect();
            updateNextConditionReverse(areaId, next);
        } else {
            return;
        }
    }

    //    @Override
    public void onRightSelected(int position, FilterModule fe) {
        setSelected(position, false);
    }

    /**
     * 判断是否已经有了相同的area item .
     * 减少重复数据new.
     */
    public boolean hasAreaModel(String areaId) {
        Log.i(TAG, "clearRightData start~ ");
        //清多余的item
        if (mRightClickPosition < mData.size() - 1) {
            FilterModule next = mData.get((mRightClickPosition + 1));
            if (next.getData().getId().equals(areaId)) {
                //do nothing ..
                return true;
            }
        }

        return false;
    }

    /**
     * 获取对应级别的位置
     *
     * @param levelName levelName
     * @return position .
     */
    public int getConditionPosition(String levelName) {
        int pos = -1;
        if (null != levelName && null != mData && mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                if (levelName.equals(mData.get(i).getData().getLevelName())) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }

    /**
     * 获取筛选的条件
     *
     * @return
     */
    public Bundle getFilterData() {
        Bundle bundle = new Bundle();
        //是否有直属校
        Boolean hasDirect = false;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getData().getName().equals(FilterConstants.STR_SCHOOL_DIRECT)) {
                hasDirect = true;
            }
        }
        bundle.putBoolean("hasDirect", hasDirect);
        if ("AREA_USR".equals(mFilterUser.getUserType())) {
            String chooseAreaId = mFilterUser.getBaseAreaId();
            if (null == mData || mData.size() > 0) {
                chooseAreaId = mData.get(mData.size() - 1).getFilterParam().getAreaId();
            }
            bundle.putString("areaId", chooseAreaId);
        } else {
            bundle.putString("areaId", mFilterUser.getBaseAreaId());
        }
        //学段
        if (getConditionPosition(FilterConstants.STR_SEMESTER) >= 0) {
            bundle.putString("semester", mData.get(getConditionPosition(FilterConstants.STR_SEMESTER)).getSelectedId());
        }
        //学校
        if (getConditionPosition(FilterConstants.STR_SCHOOL) >= 0) {
            FilterCell feSchool = mData.get(getConditionPosition(FilterConstants.STR_SCHOOL)).getData();
            //过滤ALL选项
            bundle.putString("directSchoolId", FilterConstants.STR_ALL.equals(feSchool.getName()) ? "" :  mData.get(getConditionPosition(FilterConstants.STR_SCHOOL)).getSelectedId());//直属校id
        }
        //组别
        if (getConditionPosition(FilterConstants.STR_TEAM) >= 0) {
            FilterCell team = mData.get(getConditionPosition(FilterConstants.STR_TEAM)).getData();
            if (team != null) {
                switch (team.getName()) {
                    case FilterConstants.STR_TEAM_INTEREST:
                        bundle.putString("groupType", "INTEREST");
                        break;
                    case FilterConstants.STR_TEAM_TEACH:
                        bundle.putString("groupType", "TEACH");
                        break;
                    default:
                        bundle.putString("groupType", "");
                        break;
                }
            }
        } else {
            if (getConditionPosition(FilterConstants.STR_LEVEL) >= 0) {
                bundle.putString("groupType", "TEACH");
            } else if (getConditionPosition(FilterConstants.STR_CATEGORY) >= 0) {
                bundle.putString("groupType", "INTEREST");
            } else {
                bundle.putString("groupType", "");
            }
        }
        //年级
        if (getConditionPosition(FilterConstants.STR_LEVEL) >= 0) {
            bundle.putString("class", mData.get(getConditionPosition(FilterConstants.STR_LEVEL)).getSelectedId());//年级
        }
        //学科
        if (getConditionPosition(FilterConstants.STR_SUBJECT) >= 0) {
            bundle.putString("subject", mData.get(getConditionPosition(FilterConstants.STR_SUBJECT)).getSelectedId());//学科
        }
        //分类
        if (getConditionPosition(FilterConstants.STR_CATEGORY) >= 0) {
            bundle.putString("category", mData.get(getConditionPosition(FilterConstants.STR_CATEGORY)).getSelectedId());//分类
        }
        //状态
        if (getConditionPosition(FilterConstants.STR_STATE) >= 0) {
            bundle.putString("state", mData.get(getConditionPosition(FilterConstants.STR_STATE)).getSelectedId());
        }
        return bundle;
    }

    @Override
    public void onDestroy() {
        if (mData != null) {
            mData.clear();
        }
        super.onDestroy();
    }
}

package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.filter.AreaItem;
import com.codyy.erpsportal.commons.models.entities.filter.AreaList;
import com.codyy.erpsportal.commons.models.entities.filter.LiveFilterRightItem;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.groups.controllers.viewholders.ChoiceViewHolder;
import com.codyy.url.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 直播筛选
 * Created by yangxinwu on 2015/6/11.
 * modified by poe 2016/1/21
 */
public class LiveFilterFragment extends Fragment {
    private static final String TAG = "LiveFilterFragment";
    private static final int INIT = 1;
    private static final int CLICK_LEFT = 2;
    private static final int CLICK_RIGHT = 3;
    public static final String STR_PROVINCE = "省";
    public static final String STR_CITY = "市";
    public static final String STR_AREA = "县";
    public static final String STR_SCHOOL = "学校";
    public static final String STR_SCHOOL_DIRECT = "直属校";
    public static final String STR_LEVEL = "年级";
    public static final String STR_SUBJECT = "学科";
    public static final String STR_ALL = "全部";

    /**
     * 右侧筛选项目内容
     */
    private ListView mRightLv;
    /**
     * 左侧筛选项目内容
     */
    private ListView mLeftLv;

    /**
     * 右侧筛选listview适配器
     */
    private RightAdapter mFirstAdapter;

    /**
     * 左侧筛选listview适配器
     */
    private ObjectsAdapter<AreaItem, ChoiceViewHolder> mChoiceAdapter;

    private static UserInfo mUserInfo;

    /**
     * 左侧筛选listview适配器的数据源
     */
    private AreaList mAreaList = null;

    /**
     * 右侧筛选listview适配器的数据源-
     */
    private static LinkedList<LiveFilterRightItem> data = new LinkedList<>();

    /**
     * 保存上一级筛选值
     */
    private String tempAreaName;
    /**
     * 保存上一级筛选值ID
     */
    private String tempAreaId;
    /**
     * 保存上一级筛选值的级别 比如“省,市，学科”等
     */
    private String tempLevelName;

    /**
     * 保存上一级筛选值的级别ID,用于查询直属校
     */
    private String schoolId;
    /**
     * 保存右侧listview点击了哪一项
     */
    private int clickRightPosition = 0;

    private boolean isTeacher = false;//判断是老师则没有区域和学校选择的过程

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.e(TAG, "Filter Fragment onCreate()");
        mUserInfo = getActivity().getIntent().getParcelableExtra("user_info");
        if (null != mUserInfo) {//只要不是机构的
            if (!"AREA_USR".equals(mUserInfo.getUserType())) {
                isTeacher = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Cog.e(TAG, "Filter Fragment onCreateView()");
        View view = inflater.inflate(R.layout.fragment_resource_filter, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView(View view) {
        Cog.e(TAG, "Filter Fragment initView()");
        mRightLv = (ListView) view.findViewById(R.id.first);
        mLeftLv = (ListView) view.findViewById(R.id.second);

        //筛选列
        mRightLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickRightPosition = position;

                TextView tv = (TextView) view.findViewById(R.id.title);
                String title = tv.getText().toString();
                //固定的选项
                if (title.equals(STR_LEVEL) || title.equals(STR_SUBJECT)) {
                    if (title.equals(STR_LEVEL)) {
                        loadData(URLConfig.ALL_CLASS_LEVEL, "", CLICK_RIGHT, 3);
                    } else if (title.equals(STR_SUBJECT)) {
                        loadData(URLConfig.ALL_SUBJECTS_LIST, "", CLICK_RIGHT, 4);
                    }
                } else {
                    //不固定选项
                    if (position == 0) {//地区
                        loadData(URLConfig.GET_AREA, mUserInfo.getBaseAreaId(), CLICK_RIGHT, 1);
                    } else {

                        //学校
                        if (STR_SCHOOL.equals(title)) {//学校的属性取决于其上一级地区是 具体Area或者直属（上一级AREA）
                            loadData(URLConfig.GET_DIRECT_SCHOOL, data.get(position - 1).getId(), CLICK_RIGHT, 2);
                            return;
                        }

                        loadData(URLConfig.GET_AREA, data.get(position - 1).getId(), CLICK_RIGHT, 1);
                    }
                }
            }
        });

        /**
         * 筛选子选项
         */
        mLeftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view.findViewById(R.id.title);
                tempAreaName = tv.getText().toString();
                tempAreaId = mAreaList.getAreaItemlist().get(position).getAreaId();

                //确保选中的变色
                for (AreaItem ai : mAreaList.getAreaItemlist()) {
                    ai.setSelectArea(tempAreaName);
                }
                mChoiceAdapter.notifyDataSetChanged();

                //All
                if (position == 0 && mLeftLv.getCount() > 1 &&
                        !STR_SCHOOL.equals(tempLevelName) && !STR_LEVEL.equals(tempLevelName) && !STR_SUBJECT.equals(tempLevelName)) {
                    data.get(clickRightPosition).setValue(STR_ALL);

                    if (clickRightPosition > 0) {
                        data.get(clickRightPosition).setId(data.get(clickRightPosition - 1).getId());
                    } else {
                        data.get(0).setId(mUserInfo.getBaseAreaId());
                    }
                    clearRightData();
                    addItem(STR_ALL, "", STR_LEVEL);
                    addItem(STR_ALL, "", STR_SUBJECT);
                }

                //不是最后一个
                if (!STR_SCHOOL_DIRECT.equals(tempAreaName)) {

                    tempLevelName = mAreaList.getLevelName();
                    //选择分区
                    if (!"".equals(tempAreaId) && !STR_LEVEL.equals(tempLevelName) && !STR_SUBJECT.equals(tempLevelName) && !STR_SCHOOL_DIRECT.equals(tempLevelName)) {
                        schoolId = tempAreaId;
                        loadData(URLConfig.GET_AREA, tempAreaId, CLICK_LEFT, 1);

                        //选择学校
                    } else if (!"".equals(tempAreaId) && !STR_LEVEL.equals(tempLevelName) && !STR_SUBJECT.equals(tempLevelName) && STR_SCHOOL_DIRECT.equals(tempLevelName)) {
                        Cog.e(TAG, "点击了学校：" + tempAreaName);
                    }

                } else {
                    //直属校 效果等同于选择ALL的刷新id效果
                    tempLevelName = STR_SCHOOL_DIRECT;
                    //1.第一个选项并且是国家级的直属校
                    if (clickRightPosition == 0) {
                        tempAreaId = mUserInfo.getBaseAreaId();
                        //省必须持有全国的id
                        data.get(0).setId(mUserInfo.getBaseAreaId());
                        loadData(URLConfig.GET_DIRECT_SCHOOL, mUserInfo.getBaseAreaId(), CLICK_LEFT, 2);
                    } else {
                        tempAreaId = data.get(clickRightPosition - 1).getId();
                        //直属校的id默认指向上一个区域的id
                        schoolId = mAreaList.getAreaItemlist().get(position).getAreaId();
                        data.get(clickRightPosition).setId(data.get(clickRightPosition - 1).getId());
                        //2.省市县下直属校
                        clearRightData();
                        addItem(STR_ALL, "", STR_LEVEL);
                        addItem(STR_ALL, "", STR_SUBJECT);

                        loadData(URLConfig.GET_DIRECT_SCHOOL, data.get(clickRightPosition - 1).getId(), CLICK_LEFT, 2);
                    }

                }

                if (mLeftLv.getCount() > 1 && STR_SCHOOL_DIRECT.equals(tempLevelName) && !STR_SCHOOL_DIRECT.equals(tempAreaName)) {
                    //小学单选项：三亚小学
                    updateRightList();
                    return;
                }

                if (STR_LEVEL.equals(tempLevelName)) {
                    int count = data.size();
                    data.get(count - 2).setValue(tempAreaName);
                    data.get(count - 2).setId(tempAreaId);
                    mFirstAdapter.notifyDataSetChanged();
                }
                if (STR_SUBJECT.equals(tempLevelName)) {
                    int count = data.size();
                    data.get(count - 1).setValue(tempAreaName);
                    data.get(count - 1).setId(tempAreaId);
                    mFirstAdapter.notifyDataSetChanged();
                }
            }
        });

        mFirstAdapter = new RightAdapter(getActivity(), data);
        mChoiceAdapter = new ObjectsAdapter<>(
                getActivity(), ChoiceViewHolder.class);
        mRightLv.setAdapter(mFirstAdapter);
        mLeftLv.setAdapter(mChoiceAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Cog.e(TAG, "onViewCreated ~");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cog.e(TAG, "onActivityCreated ~");
        mUserInfo   = UserInfoKeeper.getInstance().getUserInfo();
        if(null != mUserInfo){
            schoolId = mUserInfo.getSchoolId();//()getmUserInfo.getBaseAreaId();
        }else{
            LogUtils.log("用户登录信息失效！LiveFilterFragment : line#302");
        }

        if(mUserInfo.getUserType().equals("STUDENT")||mUserInfo.getUserType().equals("PARENT")){

        }else {
            LiveFilterRightItem liveFilterRightItem = new LiveFilterRightItem(STR_ALL, STR_LEVEL, STR_LEVEL, "");
            data.add(liveFilterRightItem);
        }

        LiveFilterRightItem liveFilterRightItem2 = new LiveFilterRightItem(STR_ALL, STR_SUBJECT, STR_SUBJECT, "");
        data.add(liveFilterRightItem2);

        //如果不是老师则加载数据
        if (!isTeacher) {
            loadData(URLConfig.GET_AREA, mUserInfo.getBaseAreaId(), INIT, 1);
        }
    }

    /**
     * 加载数据
     */
    private void loadData(String url, String AreaId, final int flag, final int parseType) {
        Cog.e(TAG, "Filter Fragment loadData()" + " flag:" + false + " parseType:" + parseType);
        WebApi webApi = RsGenerator.create(WebApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (parseType != 3 && parseType != 4) {
            params.put("areaId", AreaId);
        }

        if(parseType == 3 && tempLevelName!= null && tempLevelName.equals("乡镇")){
            params.put("schoolId",mUserInfo.getSchoolId());//tempAreaId
        }

        if(parseType == 3 && tempLevelName!= null && !tempLevelName.equals("乡镇")){
            params.put("schoolId",tempAreaId);//tempAreaId
        }
        if(parseType == 4 && tempAreaId != null){
            params.put("classlevelId",tempAreaId);
        }

        //请求中禁止点击事件
        mLeftLv.setEnabled(false);
        Cog.e(TAG, "loadData:url=" + url + "?" + params);
        webApi.post4Json(url, params)
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
                                mAreaList = AreaList.parse(response, parseType, data.get(clickRightPosition).getValue());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (mAreaList == null) {
                            UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                            return;
                        } else {
                            if (flag == INIT) {
                                updateUIOfInit();
                            } else if (flag == CLICK_LEFT) {
                                updateUIofClickLeft();
                            } else {
                                updateUIofClickRight();
                            }
                        }

                        mLeftLv.setEnabled(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error:" + error);
                        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                        mLeftLv.setEnabled(true);
                    }
                });
    }


    /**
     * 初次加载数据
     */
    public void updateUIOfInit() {
        //添加第一级目录
        if (TextUtils.isEmpty(mAreaList.getLevelName().trim())) {
            return;
        }
        //非固定选区
        if(mUserInfo.getUserType().equals("AREA_USR")){
            LiveFilterRightItem liveFilterRightItem = new LiveFilterRightItem(STR_ALL, mAreaList.getLevelName(), mAreaList.getLevelName(), mUserInfo.getBaseAreaId());
            data.add(0, liveFilterRightItem);
            mFirstAdapter.notifyDataSetChanged();
        }

//        hasSchool();
//        mLeftLv.setVisibility(View.GONE);
    }

    /**
     * 点击左侧listview中的item项进行的更新
     */
    public void updateUIofClickLeft() {
        mChoiceAdapter.clearData();
        updateRightList();
        hasSchool();
    }

    /**
     * 点击右侧listview中的item项进行的更新
     */
    public void updateUIofClickRight() {
        updateItemBackground(clickRightPosition);
        mFirstAdapter.notifyDataSetChanged();
        hasSchool();
    }

    /**
     * 新增区县、学校等数据更新
     */
    private void updateRightList() {

        //更新当前选择的condition 最后选中选项+1
        data.get(clickRightPosition).setValue(tempAreaName);

        if (tempAreaName.equals(STR_ALL)) {

            //学校清空学校id
            if (data.get(clickRightPosition).getLevelName().equals(STR_SCHOOL_DIRECT)) {
                data.get(clickRightPosition).setSecondId("");
            } else {
                data.get(clickRightPosition).setId(tempAreaId);
            }
            //全部 ，清空
            clearRightData();
            addItem(STR_ALL, "", STR_LEVEL);
            addItem(STR_ALL, "", STR_SUBJECT);
            updateItemBackground(clickRightPosition);
        } else if (tempAreaName.equals(STR_SCHOOL_DIRECT)) {
            data.get(clickRightPosition).setId(tempAreaId);
            //防止 市下的值变为 “直属校 市”
            //直属校
            if (!data.get(clickRightPosition + 1).getLevelName().equals(STR_SCHOOL_DIRECT)) {
                clearRightData();
                LiveFilterRightItem lri = new LiveFilterRightItem(STR_ALL, STR_SCHOOL_DIRECT, STR_SCHOOL, "");
                data.add(clickRightPosition + 1, lri);
                addItem(STR_ALL, "", STR_LEVEL);
                addItem(STR_ALL, "", STR_SUBJECT);

            }

            //无论新增的学校 or 已存在 向下跳一级
            clickRightPosition++;
            updateItemBackground(clickRightPosition);
        } else {//左边点击中间 or 直接点击 右边listview

            if (data.get(clickRightPosition).getLevelName().equals(STR_SCHOOL_DIRECT)) {
                //排除学校节点、不用更新学校的schoolid 然并卵，获取学校id的时候有用
                data.get(clickRightPosition).setId(tempAreaId);
                data.get(clickRightPosition).setSecondId(tempAreaId);
            } else if (!data.get(clickRightPosition + 1).getValue().equals(tempAreaName)) {//三亚市
                data.get(clickRightPosition).setId(tempAreaId);
                //分区或者 年级、学科
                if (!TextUtils.isEmpty(mAreaList.getLevelName())) {//过滤区县，直接挂学校了
                    clearRightData();
                    LiveFilterRightItem liveFilterRightItem = new LiveFilterRightItem(STR_ALL, mAreaList.getLevelName(), mAreaList.getLevelName(), data.get(clickRightPosition).getId());
                    data.add(clickRightPosition + 1, liveFilterRightItem);
                    addItem(STR_ALL, "", STR_LEVEL);
                    addItem(STR_ALL, "", STR_SUBJECT);
                    clickRightPosition++;
                    updateItemBackground(clickRightPosition);
                }
            }
        }


        //保证右边的item选中状态
        for (AreaItem ai : mAreaList.getAreaItemlist()) {
            ai.setSelectArea(data.get(clickRightPosition).getValue());
        }

        mFirstAdapter.notifyDataSetChanged();
    }

    /**
     * 清楚clickposition和 年级之间的items
     */
    private void clearRightData() {

        //click position clear the all data after...
        if (data.size() > 2) {
            //清楚多余的item
            int count = data.size();
            //All 则清空下面的非固定选项
            for (int j = 0; j < (count - clickRightPosition - 1); j++) {
                data.remove(clickRightPosition + 1);
            }
        }
    }

    /**
     * 如果有直属校进行的更新
     * 增加直属校
     */
    private void hasSchool() {
        mChoiceAdapter.clearData();
        if ("Y".equals(mAreaList.getHasDirect())) {
            List<AreaItem> areaItemlist = mAreaList.getAreaItemlist();
            if (areaItemlist.size() > 1) {
                //增加直属校/selectArea注意为当前选中的值 右侧点击时，如果是左侧自动加载呢？同理一样
                AreaItem areaItem = new AreaItem(STR_SCHOOL_DIRECT, data.get(clickRightPosition).getId(), data.get(clickRightPosition).getValue());
                areaItemlist.add(areaItem);
                mChoiceAdapter.setData(areaItemlist);
                mChoiceAdapter.notifyDataSetChanged();
            } else {
                //到学校根节点了 应该直接显示学校
                Cog.e(TAG, "区县下的选择....发送一次学校请求~");
                tempLevelName = STR_SCHOOL_DIRECT;
                loadData(URLConfig.GET_DIRECT_SCHOOL, schoolId, CLICK_LEFT, 2);
            }
        } else {
            mChoiceAdapter.setData(mAreaList.getAreaItemlist());
            mChoiceAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选中listview时Item背景变化
     */
    private void updateItemBackground(int clickRightPosition) {
        mFirstAdapter.setSelectedPosition(clickRightPosition);
        mFirstAdapter.setClickRightHead(-1);
    }

    /**
     * 添加一个item
     */
    private void addItem(String value, String id, String levelName) {
        LiveFilterRightItem liveFilterRightItem = new LiveFilterRightItem(value, levelName, levelName, id);
        data.add(liveFilterRightItem);
        mFirstAdapter.notifyDataSetChanged();
    }

    public class RightAdapter extends BaseAdapter {
        private int selectedPosition = -1;// 选中的位置
        private int clickRightHead = -1;// 选中的位置
        private LinkedList<LiveFilterRightItem> data;
        private LayoutInflater listContainer;

        class ViewHolder { // 自定义控件集合
            RelativeLayout filterItemView;
            TextView title;
            TextView content;
        }

        public RightAdapter(Context context, LinkedList<LiveFilterRightItem> data) {
            this.data = data;
            this.listContainer = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public void setClickRightHead(int position) {
            clickRightHead = position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                // 获取list_item布局文件的视图
                convertView = listContainer.inflate(R.layout.item_live_right_filter, null);
                viewHolder = new ViewHolder();
                viewHolder.filterItemView = (RelativeLayout) convertView.findViewById(R.id.filter_item_view);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (clickRightHead == -1) {
                if (selectedPosition == position) {
                    viewHolder.content.setTextColor(parent.getContext().getResources().getColor(R.color.green));
                } else {
                    viewHolder.content.setTextColor(Color.BLACK);
                }
            } else {
                viewHolder.content.setTextColor(Color.BLACK);
            }

            LiveFilterRightItem liveFilterRightItem = data.get(position);
            viewHolder.title.setText(liveFilterRightItem.getAlignName());
            viewHolder.title.setTag(liveFilterRightItem.getId());
            viewHolder.content.setText(liveFilterRightItem.getValue());

            return convertView;
        }
    }

    /**
     * 返回筛选条件
     */
    public Bundle getLiveFilterData() {
        Boolean hasDirect = false;
        String directSchoolId = "";

        //查找学校id
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getLevelName().equals(STR_SCHOOL_DIRECT)) {
                directSchoolId = data.get(i).getSecondId();
                break;
            }
        }

        //查找是否有直属校
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getValue().equals(STR_SCHOOL_DIRECT)) {
                hasDirect = true;
                break;
            }
        }

        Bundle bundle = new Bundle();
        for(int i=0;i<data.size();i++){
            if(data.get(i).getLevelName().equals(STR_SUBJECT)){
                bundle.putString("subject", data.get(i).getId());//学科
            }
            if(data.get(i).getLevelName().equals(STR_LEVEL)){
                bundle.putString("class", data.get(i).getId());//年级
            }
        }

        bundle.putString("directSchoolId", directSchoolId);//直属校Id
        bundle.putBoolean("hasDirect", hasDirect);//是否有直属校
        if (data.size() > 3) {
            bundle.putString("areaId", data.get(data.size() - 4).getId());
        } else {
            if (data.size() == 3) {
                if (hasDirect) {
                    bundle.putString("areaId", mUserInfo.getBaseAreaId());
                } else {
                    bundle.putString("areaId", data.get(data.size() - 3).getId());
                }
            } else {
                bundle.putString("areaId", mUserInfo.getBaseAreaId());
            }
        }
        return bundle;
    }

    @Override
    public void onDestroy() {
        if (data != null) data.clear();
        super.onDestroy();
    }

}



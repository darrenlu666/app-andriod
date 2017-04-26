package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.FilterParamsProvider;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.entities.AreaFilterItem;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.ChoicesOption;
import com.codyy.erpsportal.commons.models.entities.DirectSchoolsChoice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsedListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.utils.AreasFetcher;
import com.codyy.erpsportal.repairs.utils.AreasFetcher.OnAreasFetchedListener;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 过滤
 */
public class BaseFilterFragment extends Fragment implements FilterParamsProvider {

    private static final String TAG = "BaseFilterFragment";

    public static final String ARG_AREA_INFO = "ARG_AREA_INFO";

    public static final String ARG_AREA_ID = "ARG_AREA_ID";

    public static final String ARG_INIT = "ARG_INIT";

    /**
     * 右侧筛选项目内容列表
     */
    private ListView mOptionsLv;

    /**
     * 左侧筛选项目内容列表
     */
    private ListView mChoiceLv;

    /**
     * 右侧项目列表当前被选中项
     */
//    private int mCurrentPosition;

    private ObjectsAdapter<FilterItem, FilterItemViewHolder> mOptionsAdapter;

    private ObjectsAdapter<Choice, ChoiceViewHolder> mChoiceAdapter;

    private View mView;

    /**
     * 用来获取年级学科的地区信息
     */
    private AreaInfo mAreaInfo;

    /**
     * 用来获取地区的地区id
     */
    private String mAreaId;

    private AreaFilterItem mInitFilterItem;

    /**
     *
     * @param areaInfo 获取学科与年级参数
     * @param areaId 地区筛选根级，无需地区筛选的传null
     * @return
     */
    public static BaseFilterFragment newInstance(AreaInfo areaInfo, String areaId) {
        BaseFilterFragment fragment = new BaseFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_AREA_INFO, areaInfo);
        if (!TextUtils.isEmpty(areaId)) {
            bundle.putString(ARG_AREA_ID, areaId);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static BaseFilterFragment newInstance(UserInfo userInfo, String areaId) {
        AreaInfo areaInfo = new AreaInfo();
        if (userInfo.isArea()) {
            areaInfo.setType(AreaInfo.TYPE_AREA);
            areaInfo.setId(userInfo.getBaseAreaId());
        } else {
            areaInfo.setType(AreaInfo.TYPE_SCHOOL);
            areaInfo.setId(userInfo.getSchoolId());
        }
        return newInstance(areaInfo, areaId);
    }

    public static BaseFilterFragment newInstance(UserInfo userInfo) {
        if (userInfo.isArea()) {
            return newInstance(userInfo, userInfo.getBaseAreaId());
        } else {
            return newInstance(userInfo, null);
        }
    }

    public BaseFilterFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAreaInfo = getArguments().getParcelable(ARG_AREA_INFO);
            mAreaId = getArguments().getString(ARG_AREA_ID);
            mInitFilterItem = getArguments().getParcelable(ARG_INIT);
        }
    }

    private int mLastOptionPos = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_resource_filter, container, false);
            mOptionsLv = (ListView) mView.findViewById(R.id.first);
            mOptionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final FilterItem item = mOptionsAdapter.getItem(position);
                    if (item instanceof ChoicesOption) {
                        ChoicesOption choicesOption = (ChoicesOption) item;
                        updateChoices(choicesOption, choicesOption.getChoices());
                        return;
                    }
                    WebApi webApi = RsGenerator.create(WebApi.class);
                    Map<String, String> params = new HashMap<>();
                    if ("classLevelId".equals(item.getParamName()) || "subjectId".equals(item.getParamName())) {
                        if (mAreaInfo != null) {
                            if (mAreaInfo.isSchool()) {
                                if (!TextUtils.isEmpty(mAreaInfo.getId())) {
                                    params.put("schoolId", mAreaInfo.getId());
                                }
                            } else {
                                if (!TextUtils.isEmpty(mAreaInfo.getId())) {
                                    params.put("areaId", mAreaInfo.getId());
                                }
                            }
                        }
                    } else {
                        List<Pair<FilterItem, String>> preconditions = item.getPreconditions();
                        if (preconditions != null && preconditions.size() > 0) {
                            for (Pair<FilterItem, String> pair : preconditions) {
                                FilterItem preItem = pair.first;
                                Choice choice = preItem.getChoice();
                                if (choice == null || choice.isAll()) {
                                    uncheckItem(position);
                                    shortlyToast("请先选择" + preItem.getTypeName());
                                    return;
                                }
                                params.put(pair.second, choice.getId());
                            }
                        }
                    }
                    mLastOptionPos = position;
                    Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Cog.d(TAG, "onErrorResponse error:" + throwable);
                            uncheckItem(position);
                            UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                        }
                    };
                    Cog.d(TAG, "sendRequest=", item.getUrl(), params);
                    if (item.getResponseType() == FilterItem.ARRAY) {
                        webApi.post4Ja(item.getUrl(), params)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<JSONArray>() {
                                    @Override
                                    public void accept(JSONArray response) throws Exception {
                                        Cog.d(TAG, "onResponse JSONArray response:" + response);
                                        handleJsonItems(response, item);
                                    }
                                }, errorConsumer);
                    } else {
                        webApi.post4Json(item.getUrl(), params)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<JSONObject>() {
                                    @Override
                                    public void accept(JSONObject response) throws Exception {
                                        Cog.d(TAG, "onResponse JSONObject response:" + response);
                                        if ("success".equals(response.optString("result"))) {
                                            handleJsonItems(response.optJSONArray("list"), item);
                                        }
                                    }
                                }, errorConsumer);
                    }

                }
            });

            mChoiceLv = (ListView) mView.findViewById(R.id.second);
            mChoiceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Choice choice = mChoiceAdapter.getItem(position);
                    int currentPosition = mOptionsLv.getCheckedItemPosition();
                    FilterItem filterItem = mOptionsAdapter.getItem(currentPosition);
                    boolean hasChanged = filterItem.setChoice(choice);
                    int nextPosition = currentPosition + 1;

                    if (filterItem instanceof AreaFilterItem) {
                        AreaFilterItem areaFilterItem = (AreaFilterItem) filterItem;
                        if (hasChanged) {//如果地区选择有变化，要先清除所有下级地区或学校选项
                            FilterItem nextFilterItem = mOptionsAdapter.getItem(nextPosition);
                            while (nextFilterItem instanceof AreaFilterItem) {
                                mOptionsAdapter.removeItem(nextPosition);
                                if (nextPosition >= mOptionsAdapter.getCount()) break;
                                nextFilterItem = mOptionsAdapter.getItem(nextPosition);
                            }
                            boolean newAdded = false;
                            if (!choice.isAll() && !areaFilterItem.isSchool()) {
                                AreaFilterItem newAreaFilterItem;
                                if (choice instanceof DirectSchoolsChoice) {//直属校被点
                                    if (onDirectSchoolClick()) return;
                                    newAreaFilterItem = fetchSchools(choice.getId());
                                } else {
                                    newAreaFilterItem = fetchAreaFilterItem(choice.getId());
                                }
                                mOptionsAdapter.addItem(nextPosition, newAreaFilterItem);
                                newAdded = true;
                            }
                            mOptionsAdapter.notifyDataSetChanged();
                            if (newAdded) {//如果有新加项，切到新项
                                mOptionsLv.performItemClick(null, nextPosition, 0);
                            }
                        }
                        return;
                    }

                    if (hasChanged) {
                        clearInfluencingItems(filterItem);
                    }
                    mOptionsAdapter.notifyDataSetChanged();
                    //选全部不自动跳下个选择项
                    if (position != 0 && nextPosition < mOptionsAdapter.getCount()) {
                        mOptionsLv.performItemClick(null, nextPosition, 0);
                    }
                }
            });

            mOptionsAdapter = new ObjectsAdapter<>(
                    getActivity(), FilterItemViewHolder.class);
            mChoiceAdapter = new ObjectsAdapter<>(
                    getActivity(), ChoiceViewHolder.class);
            mOptionsLv.setAdapter(mOptionsAdapter);
            mChoiceLv.setAdapter(mChoiceAdapter);
            initOptionItems();
        }
        return mView;
    }

    /**
     * 直属校被点击
     * @return true 已被处理
     */
    protected boolean onDirectSchoolClick() {
        return false;
    }

    private void handleJsonItems(JSONArray jsonArray, FilterItem item) {
        List<Choice> choices = item.getChoiceParser().parseArray(jsonArray);
        if (choices == null) {
            choices = new ArrayList<>(1);
        }
        choices.add(0, new Choice(Choice.ALL, "全部"));
        updateChoices(item, choices);
    }

    /**
     * 更新左边地区选项
     * @param item 右侧选中的过滤项
     * @param choices 设置到左边的选项们
     */
    private void updateChoices(FilterItem item, List<Choice> choices) {
        mChoiceAdapter.setData(choices);
        mChoiceAdapter.notifyDataSetChanged();
        if (item.getChoice() == null) {
            mChoiceLv.setItemChecked(0, true);
        } else {
            int idx = choices.indexOf(item.getChoice());
            mChoiceLv.setItemChecked(idx, true);
        }
    }

    /**
     * 取消选中
     * @param position 取消选中的位置
     */
    private void uncheckItem(int position) {
        mOptionsLv.setItemChecked(position, false);
        if (mLastOptionPos != -1) {
            mOptionsLv.setItemChecked(mLastOptionPos, true);
        } else {
            mChoiceAdapter.clearData();
            mChoiceAdapter.notifyDataSetChanged();
        }
    }

    private void shortlyToast(String msg) {
        UIUtils.toast(getActivity(), msg, Toast.LENGTH_SHORT);
    }

    /**
     * 清除start位置之后的已选项，包括start
     *
     * @param start 开始位置
     */
    private void clearItemsAfter(int start) {
        for (int i = start; i < mOptionsAdapter.getCount(); i++) {
            mOptionsAdapter.getItem(i).clearChoice();
        }
    }

    /**
     * 清除影响项选择
     * @param filterItem 筛选项
     */
    private void clearInfluencingItems(FilterItem filterItem) {
        Set<FilterItem> filterItemSet = filterItem.getInfluencingItems();
        if (filterItemSet != null && filterItemSet.size() > 0) {
            for (FilterItem item : filterItem.getInfluencingItems()) {
                item.clearChoice();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始加载数据
     */
    protected void initOptionItems() {
        List<FilterItem> items = new ArrayList<>();
        if (mInitFilterItem != null) {
            items.add(mInitFilterItem);
        } else if (mAreaId != null) {
            AreaFilterItem areaFilterItem = fetchAreaFilterItem(mAreaId);
            items.add(areaFilterItem);
        }

        appendExtraOptions(items);
        mOptionsAdapter.addData(items);
    }

    /**
     * 如果有学校信息，用getClasslevelsBySchoolId这个接口请求数据
     * @return url地址
     */
    private String obtainClassLevelUrl() {
        if (mAreaInfo != null) {
            if (mAreaInfo.isSchool()) {
                if (!TextUtils.isEmpty(mAreaInfo.getId())) {
                    return URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID;
                }
            }
        }
        return URLConfig.ALL_CLASS_LEVEL;
    }

    /**
     * 子类复写可以追加筛选项
     * @param items 筛选项列表
     */
    protected void appendExtraOptions(List<FilterItem> items) {
        //如有areaInfo添加年级学科项
        if (mAreaInfo != null) {
            FilterItem classLevelFilterItem = new FilterItem("年级", "classLevelId", obtainClassLevelUrl(), FilterItem.OBJECT);
            FilterItem subjectFilterItem = new FilterItem("学科", "subjectId", URLConfig.ALL_SUBJECTS_LIST, FilterItem.OBJECT);
            subjectFilterItem.addPrecondition(classLevelFilterItem, "classlevelId");
            items.add(classLevelFilterItem);
            items.add(subjectFilterItem);
        }
    }

    /**
     * 抓取下级地区
     * @param areaId 当前地区id
     * @return 地区筛选项
     */
    @NonNull
    private AreaFilterItem fetchAreaFilterItem(final String areaId) {
        AreasFetcher areasFetcher = new AreasFetcher();
        areasFetcher.setOnAreasFetchedListener(new OnAreasFetchedListener() {
            @Override
            public void onNoAreaFetched(AreaFilterItem areaFilterItem) {
                if (onLowestAreaClick()) {
                    int index = mOptionsAdapter.indexOf(areaFilterItem);
                    mOptionsAdapter.removeItem(index);
                    mOptionsAdapter.notifyDataSetChanged();
                    int prevIndex = index - 1;
                    if (prevIndex >=0 && prevIndex < mOptionsAdapter.getCount()) {
                        FilterItem item = mOptionsAdapter.getItem(prevIndex);
                        if (item instanceof  ChoicesOption) {
                            updateChoices(item, ((ChoicesOption) item).getChoices());
                            mOptionsLv.setItemChecked(prevIndex, true);
                        }
                    }
                    return;
                }
                fetchSchools(areaFilterItem, areaId);
            }

            @Override
            public void onAreasFetched(AreaFilterItem areaFilterItem) {
                mOptionsAdapter.notifyDataSetChanged();
                tryUpdateChoices(areaFilterItem);
            }
        });
        return areasFetcher.fetchAreaFilterItem(areaId);
    }

    /**
     * 最下级地区被点击
     * @return true 点击事件被处理了
     */
    protected boolean onLowestAreaClick() {
        return false;
    }

    /**
     * 抓取学校数据
     * @param areaId 地区id
     * @return 包含学校的地区筛选项
     */
    private AreaFilterItem fetchSchools(String areaId) {
        final AreaFilterItem areaFilterItem = new AreaFilterItem();
        areaFilterItem.setUrl(URLConfig.GET_AREA);
        fetchSchools(areaFilterItem, areaId);
        return areaFilterItem;
    }

    /**
     * 抓取学校数据，并加入地区筛选项
     * @param areaFilterItem 地区筛选项
     * @param areaId 地区id
     */
    private void fetchSchools(final AreaFilterItem areaFilterItem, String areaId) {
        Map<String, String> params = new HashMap<>();
        params.put("areaId", areaId);
        WebApi webApi = RsGenerator.create(WebApi.class);
        Cog.d(TAG, "fetchSchools url=", URLConfig.GET_DIRECT_SCHOOL, params);
        webApi.post4Json(URLConfig.GET_DIRECT_SCHOOL, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "fetchSchools response=", response);
                        if ("success".equals(response.optString("result"))) {
                            JSONArray jsonArray = response.optJSONArray("schools");
                            final List<Choice> choiceList = new ArrayList<>(1 + jsonArray.length());
                            choiceList.add(0, new Choice(Choice.ALL, "全部"));
                            AreaFilterItem.SCHOOL_CHOICE_PARSER.parseArray(jsonArray, new OnParsedListener<Choice>() {
                                @Override
                                public void handleParsedObj(Choice obj) {
                                    choiceList.add(obj);
                                }
                            });
                            areaFilterItem.setTypeName("校");
                            areaFilterItem.setParamName("schoolId");
                            areaFilterItem.setSchool(true);
                            areaFilterItem.setChoices(choiceList);
                            mOptionsAdapter.notifyDataSetChanged();
                            tryUpdateChoices(areaFilterItem);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "fetchSchools error=", error);
                    }
                });
    }

    /**
     * 右侧筛选项对应的选项数据更新时，如果同时正好被选中，需要更新左侧选项们
     * @param areaFilterItem 右侧筛选项
     */
    private void tryUpdateChoices(AreaFilterItem areaFilterItem) {
        int currentPos = mOptionsLv.getCheckedItemPosition();
        //判断当前右侧地区选择项是否就是选中项
        if (areaFilterItem == mOptionsAdapter.getItem(currentPos)) {
            updateChoices(areaFilterItem, areaFilterItem.getChoices());
        }
    }

    /**
     * 获取筛选数据
     *
     * @return 筛选数据列表
     */
    public List<FilterItem> getFilterItems() {
        return mOptionsAdapter.getItems();
    }

    @Override
    public Map<String, String> acquireFilterParams() {
        return FilterItem.obtainParams(getFilterItems());
    }

    /**
     * 右侧选择项过滤项组持者
     */
    public static class FilterItemViewHolder extends AbsViewHolder<FilterItem> {

        private TextView title;

        private TextView content;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_filter_option;
        }

        @Override
        public void mapFromView(View view) {
            this.title = (TextView) view.findViewById(R.id.title);
            this.content = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public void setDataToView(FilterItem data, Context context) {
            title.setText(data.getTypeName());
            Choice choice = data.getChoice();
            if (choice == null) {
                content.setText(R.string.all);
            } else {
                content.setText(choice.getTitle());
            }
        }
    }

    /**
     * 左侧选项过滤项组持者
     */
    public static class ChoiceViewHolder extends AbsViewHolder<Choice> {

        private TextView title;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_filter_choice;
        }

        @Override
        public void mapFromView(View view) {
            title = (TextView) view;
        }

        @Override
        public void setDataToView(Choice data, Context context) {
            title.setText(data.getTitle());
        }
    }
}

package com.codyy.erpsportal.resource.controllers.fragments;

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
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
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
public class ParentsResourcesFilterFragment extends Fragment implements FilterParamsProvider {

    private static final String TAG = "ParentsResourcesFilterFragment";

    /**
     * 右侧筛选项目内容
     */
    private ListView mOptionsLv;

    /**
     * 左侧帅选项目内容
     */
    private ListView mChoiceLv;

    /**
     * 右侧项目列表当前被选中项
     */
//    private int mCurrentPosition;

    private ObjectsAdapter<FilterItem, FilterItemViewHolder> mOptionsAdapter;

    private ObjectsAdapter<Choice, ChoiceViewHolder> mChoiceAdapter;

    private View mView;

    private UserInfo mUserInfo;

    private String mSchoolId;

    public static ParentsResourcesFilterFragment newInstance(UserInfo userInfo, String schoolId){
        ParentsResourcesFilterFragment fragment = new ParentsResourcesFilterFragment();
        Bundle args = new Bundle();
        args.putParcelable(Extra.USER_INFO, userInfo);
        args.putString(Extra.SCHOOL_ID, schoolId);
        fragment.setArguments(args);
        return fragment;
    }

    public ParentsResourcesFilterFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
            mSchoolId = getArguments().getString(Extra.SCHOOL_ID);
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
                            if (pair.second.startsWith("p")) {
                                params.put(pair.second, choice.getPlaceId());
                            } else {
                                params.put(pair.second, choice.getId());
                            }
                        }
                    }

                    mLastOptionPos = position;
                    Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable error) throws Exception {
                            Cog.d(TAG, "onErrorResponse error:" + error);
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
//        mChoiceLv.setEmptyView(mSecondEmptyView);
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
                            while(nextFilterItem instanceof AreaFilterItem) {
                                mOptionsAdapter.removeItem(nextPosition);
                                if (nextPosition >= mOptionsAdapter.getCount()) break;
                                nextFilterItem = mOptionsAdapter.getItem(nextPosition);
                            }
                            boolean newAdded = false;
                            if (!choice.isAll() && !areaFilterItem.isSchool()) {
                                AreaFilterItem newAreaFilterItem;
                                if (choice instanceof DirectSchoolsChoice) {
                                    newAreaFilterItem = fetchSchools(choice.getId());
                                } else {
                                    newAreaFilterItem = fetchAreaFilterItem(choice.getId());
                                }
                                mOptionsAdapter.addItem(nextPosition, newAreaFilterItem);
                                newAdded = true;
                            }
                            mOptionsAdapter.notifyDataSetChanged();
                            if (newAdded) {
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

    private void handleJsonItems(JSONArray jsonArray, FilterItem item) {
        List<Choice> choices = item.getChoiceParser().parseArray(jsonArray);
        if (choices == null) {
            choices = new ArrayList<>(1);
        }
        choices.add(0, new Choice(Choice.ALL, "全部"));
        updateChoices(item, choices);
    }

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
     * @param start
     */
    private void clearItemsAfter(int start) {
        for (int i = start; i < mOptionsAdapter.getCount(); i++) {
            mOptionsAdapter.getItem(i).clearChoice();
        }
    }

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

    private void initOptionItems() {
        List<FilterItem> items = new ArrayList<>();

//        if(mAreaId != null) {
//            AreaFilterItem areaFilterItem = fetchAreaFilterItem( mAreaId);
//            items.add(areaFilterItem);
//        }

        String allClassLevelUrl;
        String allSubjectUrl;
        if (!TextUtils.isEmpty(mSchoolId)) {
            allClassLevelUrl = URLConfig.ALL_CLASS_LEVEL_BY_SCHOOL_ID+ "?schoolId=" + mSchoolId;
        } else {
            return;
        }
        FilterItem classLevelFilterItem = new FilterItem("年级", "baseClasslevelId", allClassLevelUrl);
        FilterItem subjectFilterItem = new FilterItem("学科", "baseSubjectId", URLConfig.ALL_SUBJECTS_BY_CLASS_ID);
        FilterItem versionFilterItem = new FilterItem("版本", "baseVersionId", URLConfig.VERSIONS);
        FilterItem volumeFilterItem = new FilterItem("分册", "baseVolumeId", URLConfig.VOLUMES);

        subjectFilterItem.addPrecondition(classLevelFilterItem, "pClasslevelId");

        versionFilterItem.addPrecondition(classLevelFilterItem, "pClasslevelId");
        versionFilterItem.addPrecondition(subjectFilterItem, "pSubjectId");

        volumeFilterItem.addPrecondition(classLevelFilterItem, "classLevelId");
        volumeFilterItem.addPrecondition(subjectFilterItem, "subjectId");
        volumeFilterItem.addPrecondition(versionFilterItem, "versionId");
        items.add(classLevelFilterItem);
        items.add(subjectFilterItem);
        items.add(versionFilterItem);
        items.add(volumeFilterItem);
        mOptionsAdapter.addData(items);
    }

    @NonNull
    private AreaFilterItem fetchAreaFilterItem(final String areaId) {
        final Map<String, String> params = new HashMap<>();
        final AreaFilterItem areaFilterItem = new AreaFilterItem();
        areaFilterItem.setUrl(URLConfig.GET_AREA);
        params.put("uuid", mUserInfo.getUuid());
        params.put("areaId", areaId);
        WebApi webApi = RsGenerator.create(WebApi.class);
        webApi.post4Json(areaFilterItem.getUrl(), params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "fetchAreaFilterItem response=", response);
                        if ("success".equals(response.optString("result"))) {
                            String levelName = response.optString("levelName");
                            areaFilterItem.setTypeName(levelName);
                            boolean hasDirect = "Y".equals(response.optString("hasDirect"));
                            JSONArray jsonArray = response.optJSONArray("areas");
                            if (jsonArray.length() == 0) {
                                fetchSchools(areaFilterItem, params);
                                return;
                            }
                            final List<Choice> choiceList = new ArrayList<>(1 + jsonArray.length() + (hasDirect?1:0));
                            choiceList.add(0, new Choice(Choice.ALL, "全部"));
                            AreaFilterItem.AREA_CHOICE_PARSER.parseArray(jsonArray, new OnParsedListener<Choice>() {
                                @Override
                                public void handleParsedObj(Choice obj) {
                                    choiceList.add(obj);
                                }
                            });
                            if (hasDirect) {
                                choiceList.add(new DirectSchoolsChoice(areaId, "直属校"));
                            }
                            areaFilterItem.setParamName("baseAreaId");
                            areaFilterItem.setChoices(choiceList);
                            mOptionsAdapter.notifyDataSetChanged();
                            tryUpdateChoices(areaFilterItem);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "initOptionItems error=", error);
                    }
                });
        return areaFilterItem;
    }

    private AreaFilterItem fetchSchools(String areaId) {
        final Map<String, String> params = new HashMap<>();
        final AreaFilterItem areaFilterItem = new AreaFilterItem();
        areaFilterItem.setUrl(URLConfig.GET_AREA);
        params.put("uuid", mUserInfo.getUuid());
        params.put("areaId", areaId);
        fetchSchools(areaFilterItem, params);
        return areaFilterItem;
    }

    private void fetchSchools(final AreaFilterItem areaFilterItem, Map<String, String> params){
        WebApi webApi = RsGenerator.create(WebApi.class);
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

    private void tryUpdateChoices(AreaFilterItem areaFilterItem) {
        int currentPos = mOptionsLv.getCheckedItemPosition();
        if (areaFilterItem == mOptionsAdapter.getItem(currentPos)) {
            updateChoices(areaFilterItem, areaFilterItem.getChoices());
        }
    }

    /**
     * 获取筛选数据
     *
     * @return
     */
    public List<FilterItem> getResourceFilterItems() {
        return mOptionsAdapter.getItems();
    }

    @Override
    public Map<String, String> acquireFilterParams() {
        return FilterItem.obtainParams(getResourceFilterItems());
    }

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

    public static class FilterPrecondition {
        private String uuid;
        private String areaId;
        private String schoolId;
    }
}

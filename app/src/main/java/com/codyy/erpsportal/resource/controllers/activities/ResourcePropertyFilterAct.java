package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.resource.controllers.fragments.ChoiceDialog;
import com.codyy.erpsportal.resource.controllers.fragments.ChoiceDialog.OnChosenListener;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 属性筛选
 * Created by gujiajia on 2016/7/6
 */
public class ResourcePropertyFilterAct extends AppCompatActivity implements OnChosenListener, OnCancelListener {

    private static final String TAG = "ResourcePropertyFilterAct";

    public final static String EXTRA_SEMESTER = "com.codyy.erpsportal.EXTRA_SEMESTER";

    public final static String EXTRA_TYPE = "com.codyy.erpsportal.EXTRA_TYPE";

    public final static String EXTRA_PROPERTY_FILTER = "com.codyy.erpsportal.EXTRA_PROPERTY_FILTER";

    /**
     * 是否是学生
     */
    public final static String EXTRA_STUDENT = "com.codyy.erpsportal.EXTRA_STUDENT";

    public static final int REQUEST_FILTER = 1;

    /**
     * “不限”选择
     */
    private Choice mNoLimitChoice = new Choice(Choice.ALL, "不限");

    private Choice mAllChoice = new Choice(Choice.ALL, "全部");

    private AreaInfo mAreaInfo;

    @Bind(R.id.list_view)
    ListView mOptionsLv;

    /**
     * 是否是学生
     */
    private boolean mIsStudent;

    /**
     * 版本过滤项（学生单项时要隐藏的）
     */
    private FilterItem mVersionFilterItem;

    private ObjectsAdapter<FilterItem, ResourceFilterItemViewHolder> mOptionsAdapter;

    private ChoiceDialog mChoiceDialog;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_property_filter);
        ButterKnife.bind(this);
        mRequestQueue = RequestManager.getRequestQueue();
        mOptionsAdapter = new ObjectsAdapter<>(
                this, ResourceFilterItemViewHolder.class);
        mOptionsLv.setAdapter(mOptionsAdapter);
        initAttributes();
        addItems();
    }

    private void initAttributes() {
        mIsStudent = getIntent().getBooleanExtra(EXTRA_STUDENT, false);
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
    }

    private void addItems() {
        List<FilterItem> items = new ArrayList<>();
        Choice semester = getIntent().getParcelableExtra(EXTRA_SEMESTER);
        addItem(items, "学段", "baseSemesterId", URLConfig.GET_SEMESTER_LIST,
                semester != null? semester: mAllChoice);
        addItem(items, "年级", "baseClasslevelId", URLConfig.CLASS_LEVELS);
        addItem(items, "学科", "baseSubjectId", URLConfig.SUBJECTS);
        mVersionFilterItem = new FilterItem("版本", "baseVersionId", URLConfig.VERSIONS);
        mVersionFilterItem.setChoice(mAllChoice);
        if (!mIsStudent) {
            items.add(mVersionFilterItem);//?classlevelId=d3fa9752e3894f7687b3c4899d9cf500&subjectId=d3fa9752e3894f7687b3c4899d9cf600"
        }
        addItem(items, "分册", "baseVolumeId", URLConfig.VOLUMES);//?versionId=d3fa9752e3894f7687b3c4899d9cf900
        addItem(items, "章", "chapterId", URLConfig.CHAPTERS);//?volumeId=d3fa9752e3894f7687b3c4899d9d1000
        addItem(items, "节", "sectionId", URLConfig.SECTIONS);//?chapterId=d3fa9752e3894f7687b3c4899d9d1001
        addItem(items, "分类", "categoryId", URLConfig.CATEGORIES);
        mOptionsAdapter.addData(items);
    }

    private void addItem(List<FilterItem> items, String title, String paramName, String url, Choice choice) {
        FilterItem item = new FilterItem(title, paramName, url);
        item.setChoice(choice);
        items.add(item);
    }

    private void addItem(List<FilterItem> items, String title, String paramName, String url) {
        addItem(items, title, paramName, url, mAllChoice);
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(final int position) {
        FilterItem item = mOptionsAdapter.getItem(position);
        final String paramName = item.getParamName();
        Map<String, String> params = new HashMap<>();
        switch (paramName) {
            case "baseSemesterId": //点击学段行
                if (mAreaInfo.isArea()) {
                    params.put("areaId", mAreaInfo.getId());
                } else if (mAreaInfo.isSchool()) {
                    params.put("schoolId", mAreaInfo.getId());
                }
                break;
            case "baseClasslevelId":
                FilterItem semesterItem = obtainFilterItem("baseSemesterId");
                if (semesterItem == null) return;
                Choice semester = semesterItem.getChoice();
                if (semester == null || semester.getPlaceId() == null) {
                    uncheckItem(position);
                    shortlyToast("请先选择学段！");
                    return;
                }
                params.put("pSemesterId", semester.getPlaceId());
                break;
            case "baseSubjectId": {//选择学科
                FilterItem classLevelItem = obtainFilterItem("baseClasslevelId");
                if (classLevelItem == null) return;
                Choice classLevel = classLevelItem.getChoice();
                if (classLevel == null || classLevel.getPlaceId() == null) {
                    uncheckItem(position);
                    shortlyToast("请先选择年级！");
                    return;
                }
                params.put("pClasslevelId", classLevel.getPlaceId());
                break;
            }
            case "baseVersionId": { //点击版本
                if (mIsStudent && mTempChoices != null) {
                    showDialog(mTempChoices);
                    return;
                }
                Map<String, FilterItem> items = obtainFilterItems("baseClasslevelId", "baseSubjectId");
                FilterItem classLevelItem = items.get("baseClasslevelId");
                FilterItem subjectItem = items.get("baseSubjectId");
                if (classLevelItem == null || subjectItem == null) return;
                Choice classLevel = classLevelItem.getChoice();
                Choice subject = subjectItem.getChoice();
                if (classLevel == null || classLevel.getPlaceId() == null) {
                    uncheckItem(position);
                    shortlyToast("请先选择年级！");
                    return;
                }
                if (subject == null || subject.getPlaceId() == null) {
                    uncheckItem(position);
                    shortlyToast("请先选择学科！");
                    return;
                }
                params.put("pClasslevelId", classLevel.getPlaceId());
                params.put("pSubjectId", subject.getPlaceId());
                break;
            }
            case "baseVolumeId": { //点击分册
                Map<String, FilterItem> items = obtainFilterItems("baseClasslevelId", "baseSubjectId");
                FilterItem classLevelItem = items.get("baseClasslevelId");
                FilterItem subjectItem = items.get("baseSubjectId");

                Choice classLevel = classLevelItem.getChoice();
                Choice subject = subjectItem.getChoice();
                Choice version = mVersionFilterItem.getChoice();
                if (!mOptionsAdapter.getItems().contains(mVersionFilterItem)) {//版本隐藏了，提示要提示学科
                    if (subject == null || subject.isNothing()) {
                        uncheckItem(position);
                        shortlyToast("请先选择学科！");
                        return;
                    }
                    if (version == null || version.isNothing()) {//当前学科下没有版本，无需请求分册了，直接给个“全部”选项
                        List<Choice> choices = new ArrayList<>(1);
                        choices.add(mAllChoice);
                        showDialog(choices);
                        return;
                    }
                }
                if (version == null || version.isNothing()) {
                    uncheckItem(position);
                    shortlyToast("请先选择版本！");
                    return;
                }

                params.put("classLevelId", classLevel.getId());
                params.put("subjectId", subject.getId());
                params.put("versionId", version.getId());
                break;
            }
            case "chapterId":  //点击章
                FilterItem volumeItem = obtainFilterItem("baseVolumeId");
                if (volumeItem == null) return;
                Choice volume = volumeItem.getChoice();
                if (volume == null || volume.isNothing()) {
                    uncheckItem(position);
                    shortlyToast("请先选择分册！");
                    return;
                }
                params.put("volumnId", volume.getId());
                break;
            case "sectionId":  //点击节
                FilterItem chapterItem = obtainFilterItem("chapterId");
                if (chapterItem == null) return;
                Choice chapter = chapterItem.getChoice();
                if (chapter == null || chapter.isNothing()) {
                    uncheckItem(position);
                    shortlyToast("请先选择章！");
                    return;
                }
                params.put("chapterId", chapter.getId());
                break;
            case "subCategoryId":
                FilterItem categoryItem = obtainFilterItem("categoryId");
                if (categoryItem == null) return;
                Choice category = categoryItem.getChoice();
                params.put("parentId", category.getId());
                break;
        }
        Cog.d(TAG, "url=" + item.getUrl() + params);
        mRequestQueue.add(new NormalPostRequest(item.getUrl(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse response:" + response);
                if ("success".equals(response.optString("result"))) {
                    List<Choice> choices = new Choice.BaseChoiceParser()
                            .parseArray(response.optJSONArray("list"));
                    if (choices == null) {
                        choices = new ArrayList<>(1);
                    }
                    if (paramName.equals("subCategoryId")) {
                        choices.add(0, mNoLimitChoice);
                    } else {
                        choices.add(0, mAllChoice);
                    }

                    showDialog(choices);
                } else {
                    uncheckItem(position);
                    UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onErrorResponse error:" + error);
                uncheckItem(position);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }));
    }

    private Map<String,FilterItem> obtainFilterItems(String... paramName) {
        Map<String, FilterItem> filterItems = new HashMap<>(paramName.length);
        //创建过滤列表
        List<String> paramNameList = new ArrayList<>(Arrays.asList(paramName));
        for (int i=0; i < mOptionsAdapter.getCount(); i++) {
            FilterItem filterItem = mOptionsAdapter.getItem(i);
            int hitIndex = paramNameList.indexOf(filterItem.getParamName());
            if (hitIndex >= 0) {
                filterItems.put(paramNameList.remove(hitIndex), filterItem);
                if (paramNameList.size() == 0) break;//所有项都已搜到，跳出搜索
            }
        }
        return filterItems;
    }

    private FilterItem obtainFilterItem(@NonNull String paramName) {
        for (int i=0; i < mOptionsAdapter.getCount(); i++) {
            FilterItem filterItem = mOptionsAdapter.getItem(i);
            if (paramName.equals(filterItem.getParamName())) {
                return filterItem;
            }
        }
        return null;
    }

    private int positionOf(String paramName) {
        for (int i=0; i < mOptionsAdapter.getCount(); i++) {
            FilterItem filterItem = mOptionsAdapter.getItem(i);
            if (paramName.equals(filterItem.getParamName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 显示选择的弹出窗
     */
    private void showDialog(List<Choice> choices) {
        if (mChoiceDialog == null) {
            mChoiceDialog = ChoiceDialog.newInstance(choices);
            mChoiceDialog.setOnCancelListener(this);
        }
        if (mChoiceDialog.getDialog() == null || !mChoiceDialog.getDialog().isShowing()) {
            mChoiceDialog.changeData(choices);
            mChoiceDialog.show(getSupportFragmentManager(), "choiceDialog");
        } else {
            mChoiceDialog.changeDataImmediately(choices);
        }
    }

    private void shortlyToast(String msg) {
        UIUtils.toast(this, msg, Toast.LENGTH_SHORT);
    }


    @OnClick(R.id.btn_confirm)
    public void onConfirmClick() {
        ArrayList<FilterItem> filterItems = new ArrayList<>(mOptionsAdapter.getItems());
        if (!filterItems.contains(mVersionFilterItem)) {
            filterItems.add(3, mVersionFilterItem);
        }
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_PROPERTY_FILTER,
                filterItems);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onChosen(Choice choice) {
        int position = mOptionsLv.getCheckedItemPosition();
        FilterItem item = mOptionsAdapter.getItem(position);
        Choice orgChoice = item.getChoice();
        if (choice.equals(orgChoice)) { //选择与原来没有变化
            mChoiceDialog.dismiss();
            uncheckItem(position);
            return;
        }
        item.setChoice(choice);
        if (position < positionOf("sectionId")) {
            clearItemsBeforeType(position + 1);
        } else if ("categoryId".equals(item.getParamName())) {//正在在选择第一级分类
            if (!choice.isNothing()) {//选择一个分类
                FilterItem childCategoryItem = mOptionsAdapter.getItem(position + 1);
                if (childCategoryItem == null) {
                    childCategoryItem = new FilterItem("", "subCategoryId", URLConfig.SON_CATEGORIES);
                    childCategoryItem.setChoice(mNoLimitChoice);
                    mOptionsAdapter.addData(childCategoryItem);
                } else {
                    childCategoryItem.setChoice(mNoLimitChoice);
                }
            } else {//选择不限分类
                clearChildCategoryOption();
            }
        }
        if("baseSubjectId".equals(item.getParamName()) && mIsStudent
                && !item.getChoice().isNothing()) {
            autoFetchVersion(item);
        }

        mOptionsAdapter.notifyDataSetChanged();
        mChoiceDialog.dismiss();
        uncheckItem(position);
    }

    /**
     * 清除start位置之后至类型的已选项，包括start
     * @param start 起始位置
     */
    private void clearItemsBeforeType(int start) {
        List<FilterItem> items = mOptionsAdapter.getItems();
        for (int i = start; i < items.size(); i++) {
            FilterItem filterItem = items.get(i);
            filterItem.setChoice(mAllChoice);
            if ("sectionId".equals(filterItem.getParamName())) break;
        }
        //清除的选项包含版本，如果版本是隐藏状态，上面的循环处理不到版本项，此处需设一下
        if (start <= 3) {
            mVersionFilterItem.setChoice(mAllChoice);
        }
    }

    /**
     * 清除子分类项
     */
    private void clearChildCategoryOption() {
        FilterItem lastFilterItem = mOptionsAdapter.getItem(mOptionsAdapter.getCount() - 1);
        if (lastFilterItem != null && "subCategoryId".equals(lastFilterItem.getParamName())) {
            mOptionsAdapter.removeItem(mOptionsAdapter.getCount() - 1);
        }
    }

    private List<Choice> mTempChoices;

    /**
     * 自动获取版本
     */
    private void autoFetchVersion(FilterItem subjectItem) {
        Map<String, String> params = new HashMap<>();
        FilterItem classLevelItem = obtainFilterItem("baseClasslevelId");
        if (classLevelItem == null || subjectItem == null) return;
        Choice classLevel = classLevelItem.getChoice();
        Choice subject = subjectItem.getChoice();
        params.put("pClasslevelId", classLevel.getPlaceId());
        params.put("pSubjectId", subject.getPlaceId());
        Cog.d(TAG, "autoFetchVersion ", mVersionFilterItem.getUrl(), params);
        mRequestQueue.add(new NormalPostRequest(mVersionFilterItem.getUrl(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse response:" + response);
                if ("success".equals(response.optString("result"))) {
                    List<Choice> choices = new Choice.BaseChoiceParser()
                            .parseArray(response.optJSONArray("list"));
                    if (choices == null || choices.size() == 0) {
                        mVersionFilterItem.setChoice(mAllChoice);
                        hideVersionFilterItem();
                    } else if (choices.size() == 1) {
                        mVersionFilterItem.setChoice(choices.get(0));
                        hideVersionFilterItem();
                    } else {
                        choices.add(0, mAllChoice);
                        mTempChoices = choices;
                        //大于一个版本时得显示版本选项
                        showVersionFilterItem();
                    }
                    //showDialog(choices);
                } else {
                    UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onErrorResponse error:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }));
    }

    private void hideVersionFilterItem() {
        List<FilterItem> itemList = mOptionsAdapter.getItems();
        itemList.remove(mVersionFilterItem);
        mOptionsAdapter.notifyDataSetChanged();
    }

    private void showVersionFilterItem() {
        List<FilterItem> itemList = mOptionsAdapter.getItems();
        //第三项不是版本项则添加
        FilterItem item = itemList.get(3);
        if (!"baseSubjectId".equals(item.getParamName())) {
            itemList.add(3, mVersionFilterItem);
        }
        mOptionsAdapter.notifyDataSetChanged();
    }

    private void uncheckItem(int position) {
        mOptionsLv.setItemChecked(position, false);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        int checkedPosition = mOptionsLv.getCheckedItemPosition();
        if (checkedPosition != ListView.INVALID_POSITION) {
            mOptionsLv.setItemChecked(checkedPosition, false);
        }
    }

    public static class ResourceFilterItemViewHolder extends AbsViewHolder<FilterItem> {

        private TextView title;

        private TextView content;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_resource_filter;
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
                content.setText(R.string.no_filter);
            } else {
                content.setText(choice.getTitle());
            }
        }
    }

    public static void startForResult(Activity activity, AreaInfo areaInfo, boolean isStudent){
        startForResult(activity, areaInfo, null, isStudent);
    }

    public static void startForResult(Activity activity, AreaInfo areaInfo, Choice semester, boolean isStudent) {
        Intent intent = new Intent( activity, ResourcePropertyFilterAct.class);
        if (semester != null) {
            intent.putExtra(EXTRA_SEMESTER, semester);
        }
        if (isStudent) {
            intent.putExtra(EXTRA_STUDENT, true);
        }
        intent.putExtra( Extra.AREA_INFO, areaInfo);
        activity.startActivityForResult(intent, REQUEST_FILTER);
        UIUtils.addEnterAnim(activity);
    }

    public static void startForResult(Activity activity, AreaInfo areaInfo, Choice semester) {
        startForResult(activity, areaInfo, semester, false);
    }

}

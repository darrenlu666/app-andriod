package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.resource.controllers.fragments.ChoiceDialog;
import com.codyy.erpsportal.resource.controllers.fragments.ChoiceDialog.OnChosenListener;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 属性筛选
 * Created by gujiajia
 */
public class ResourcePropertyFilterAct extends AppCompatActivity implements OnChosenListener, OnCancelListener {

    private static final String TAG = "ResourcePropertyFilterAct";

    public final static String EXTRA_SEMESTER = "com.codyy.erpsportal.EXTRA_SEMESTER";
    public final static String EXTRA_CLASS_LEVEL = "com.codyy.erpsportal.EXTRA_CLASS_LEVEL";
    public final static String EXTRA_SUBJECT = "com.codyy.erpsportal.EXTRA_SUBJECT";
    public final static String EXTRA_VERSION = "com.codyy.erpsportal.EXTRA_VERSION";
    public final static String EXTRA_VOLUME = "com.codyy.erpsportal.EXTRA_VOLUME";
    public final static String EXTRA_CHAPTER = "com.codyy.erpsportal.EXTRA_CHAPTER";
    public final static String EXTRA_SECTION = "com.codyy.erpsportal.EXTRA_SECTION";
    public final static String EXTRA_TYPE = "com.codyy.erpsportal.EXTRA_TYPE";
    public final static String EXTRA_PARENT_CATEGORY = "com.codyy.erpsportal.EXTRA_PARENT_CATEGORY";
    public final static String EXTRA_CHILD_CATEGORY = "com.codyy.erpsportal.EXTRA_CHILD_CATEGORY";

    public final static String EXTRA_PROPERTY_FILTER = "com.codyy.erpsportal.EXTRA_PROPERTY_FILTER";

    /**
     * 学段
     */
    private static final int OPTION_SEMESTER = 0;

    /**
     * 年级
     */
    private static final int OPTION_CLASS_LEVEL = 1;

    /**
     * 学科
     */
    private static final int OPTION_SUBJECT = 2;

    /**
     * 版本
     */
    private static final int OPTION_VERSION = 3;

    /**
     * 分册
     */
    private static final int OPTION_VOLUME = 4;

    /**
     * 章
     */
    private static final int OPTION_CHAPTER = 5;

    /**
     * 节
     */
    private static final int OPTION_SECTION = 6;

    private static final int OPTION_PARENT_CATEGORY = 7;

    private static final int OPTION_CHILD_CATEGORY = 8;

    public static final int REQUEST_FILTER = 1;

    /**
     * “不限”选择
     */
    private Choice mNoLimitChoice = new Choice(Choice.ALL, "不限");

    private Choice mAllChoice = new Choice(Choice.ALL, "全部");

    private AreaInfo mAreaInfo;

    @Bind(R.id.list_view)
    ListView mOptionsLv;

    private ObjectsAdapter<FilterItem, ResourceFilterItemViewHolder> mOptionsAdapter;

    private ChoiceDialog mChoiceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_property_filter);
        ButterKnife.bind(this);
        mOptionsAdapter = new ObjectsAdapter<>(
                this, ResourceFilterItemViewHolder.class);
        mOptionsLv.setAdapter(mOptionsAdapter);
        initAttributes();
        addItems();
    }

    private void initAttributes() {
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
    }

    private void addItems() {
        List<FilterItem> items = new ArrayList<>();
        Choice semester = getIntent().getParcelableExtra(EXTRA_SEMESTER);
        addItem(items, "学段", "baseSemesterId", URLConfig.GET_SEMESTER_LIST,
                semester != null? semester: mAllChoice);
        addItem(items, "年级", "baseClasslevelId", URLConfig.CLASS_LEVELS);
        addItem(items, "学科", "baseSubjectId", URLConfig.SUBJECTS);
        addItem(items, "版本", "baseVersionId", URLConfig.VERSIONS);//?classlevelId=d3fa9752e3894f7687b3c4899d9cf500&subjectId=d3fa9752e3894f7687b3c4899d9cf600"
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
        RequestQueue requestQueue = RequestManager.getRequestQueue();
        Map<String, String> params = new HashMap<>();
        if (position == OPTION_SEMESTER) {
            if (mAreaInfo.isArea()) {
                params.put("areaId", mAreaInfo.getId());
            } else if (mAreaInfo.isSchool()) {
                params.put("schoolId", mAreaInfo.getId());
            }
        } else if (position == OPTION_CLASS_LEVEL) {
            FilterItem semesterItem = mOptionsAdapter.getItem(OPTION_SEMESTER);
            Choice semester = semesterItem.getChoice();
            if (semester == null || semester.getPlaceId() == null) {
                uncheckItem(position);
                shortlyToast("请先选择学段！");
                return;
            }
            params.put("pSemesterId", semester.getPlaceId());
        } else if (position == OPTION_SUBJECT) {
            FilterItem classLevelItem = mOptionsAdapter.getItem(OPTION_CLASS_LEVEL);
            Choice classLevel = classLevelItem.getChoice();
            if (classLevel == null || classLevel.getPlaceId() == null) {
                uncheckItem(position);
                shortlyToast("请先选择年级！");
                return;
            }
            params.put("pClasslevelId", classLevel.getPlaceId());
        } else if (position == OPTION_VERSION) { //点击版本
            FilterItem classLevelItem = mOptionsAdapter.getItem(OPTION_CLASS_LEVEL);
            FilterItem subjectItem = mOptionsAdapter.getItem(OPTION_SUBJECT);
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
        } else if (position == OPTION_VOLUME) { //点击分册
            FilterItem classLevelItem = mOptionsAdapter.getItem(OPTION_CLASS_LEVEL);
            FilterItem subjectItem = mOptionsAdapter.getItem(OPTION_SUBJECT);
            FilterItem versionItem = mOptionsAdapter.getItem(OPTION_VERSION);

            Choice classLevel = classLevelItem.getChoice();
            Choice subject = subjectItem.getChoice();
            Choice version = versionItem.getChoice();
            if (version == null || version.isNothing()) {
                uncheckItem(position);
                shortlyToast("请先选择版本！");
                return;
            }

            params.put("classLevelId", classLevel.getId());
            params.put("subjectId", subject.getId());
            params.put("versionId", version.getId());
        } else if (position == OPTION_CHAPTER) { //点击章
            FilterItem volumeItem = mOptionsAdapter.getItem(OPTION_VOLUME);
            Choice volume = volumeItem.getChoice();
            if (volume == null || volume.isNothing()) {
                uncheckItem(position);
                shortlyToast("请先选择分册！");
                return;
            }
            params.put("volumnId", volume.getId());
        } else if (position == OPTION_SECTION) { //点击节
            FilterItem chapterItem = mOptionsAdapter.getItem(OPTION_CHAPTER);
            Choice chapter = chapterItem.getChoice();
            if (chapter == null || chapter.isNothing()) {
                uncheckItem(position);
                shortlyToast("请先选择章！");
                return;
            }
            params.put("chapterId", chapter.getId());
        } else if (position == OPTION_CHILD_CATEGORY) {
            FilterItem categoryItem = mOptionsAdapter.getItem(OPTION_PARENT_CATEGORY);
            Choice category = categoryItem.getChoice();
            params.put("parentId", category.getId());
        }
        Cog.d(TAG, "url=" + item.getUrl() + params);
        requestQueue.add(new NormalPostRequest(item.getUrl(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse response:" + response);
                if ("success".equals(response.optString("result"))) {
                    List<Choice> choices = new Choice.BaseChoiceParser()
                            .parseArray(response.optJSONArray("list"));
                    if (choices == null) {
                        choices = new ArrayList<>(1);
                    }
                    if (position == OPTION_CHILD_CATEGORY) {
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
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_PROPERTY_FILTER,
                (ArrayList<? extends Parcelable>)mOptionsAdapter.getItems());
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
        if (position < OPTION_SECTION) {
//            clearChildCategoryOption();
            clearItemsBeforeType(position + 1);
        } else if (position == OPTION_PARENT_CATEGORY) {//正在在选择第一级分类
            if (!choice.isNothing()) {//选择一个分类
                FilterItem childCategoryItem = mOptionsAdapter.getItem(OPTION_CHILD_CATEGORY);
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

        mOptionsAdapter.notifyDataSetChanged();
        mChoiceDialog.dismiss();
        uncheckItem(position);
    }

    /**
     * 清除start位置之后至类型的已选项，包括start
     * @param start 起始位置
     */
    private void clearItemsBeforeType(int start) {
        for (int i = start; i < OPTION_PARENT_CATEGORY; i++) {
            mOptionsAdapter.getItem(i).setChoice(mAllChoice);
        }
    }

    /**
     * 清除子分类项
     */
    private void clearChildCategoryOption() {
        if (mOptionsAdapter.getCount() == OPTION_CHILD_CATEGORY + 1) {
            mOptionsAdapter.removeItem( OPTION_CHILD_CATEGORY);
        }
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

    public static void startForResult(Activity activity, AreaInfo areaInfo){
        Intent intent = new Intent( activity, ResourcePropertyFilterAct.class);
        intent.putExtra( Extra.AREA_INFO, areaInfo);
        activity.startActivityForResult(intent, REQUEST_FILTER);
        UIUtils.addEnterAnim(activity);
    }

    public static void startForResult(Activity activity, AreaInfo areaInfo, Choice semester) {
        Intent intent = new Intent( activity, ResourcePropertyFilterAct.class);
        intent.putExtra( EXTRA_SEMESTER, semester);
        intent.putExtra( Extra.AREA_INFO, areaInfo);
        activity.startActivityForResult(intent, REQUEST_FILTER);
        UIUtils.addEnterAnim(activity);
    }
}

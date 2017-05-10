package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.resource.controllers.fragments.ChoiceDialog;
import com.codyy.erpsportal.resource.controllers.fragments.ChoiceDialog.OnChosenListener;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 属性筛选
 * Created by gujiajia on 2016/7/6
 */
public class ResourceKnowledgeFilterAct extends AppCompatActivity implements OnChosenListener, OnCancelListener {

    private static final String TAG = "ResourcePropertyFilterAct";

    public final static String EXTRA_SEMESTER = "com.codyy.erpsportal.EXTRA_SEMESTER";
    public final static String EXTRA_TYPE = "com.codyy.erpsportal.EXTRA_TYPE";

    public final static String EXTRA_KNOWLEDGE_FILTER = "com.codyy.erpsportal.EXTRA_KNOWLEDGE_FILTER";

    /**
     * 学段
     */
    private static final int OPTION_SEMESTER = 0;

    /**
     * 学科
     */
    private static final int OPTION_SUBJECT = 1;

    /**
     * 根知识点
     */
    private final static int OPTION_ROOT_KNOWLEDGE = 2;

    public static final int REQUEST_FILTER = 2;

    /**
     * “不限”选择
     */
    private Choice mNoLimitChoice = new Choice(Choice.ALL, "不限");

    private Choice mAllChoice = new Choice(Choice.ALL, "全部");

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.list_view)
    ListView mOptionsLv;

    private AreaInfo mAreaInfo;

    private Choice mSemesterChoice;

    private ObjectsAdapter<FilterItem, ResourceFilterItemViewHolder> mOptionsAdapter;
    private ChoiceDialog mChoiceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_property_filter);
        ButterKnife.bind(this);
        initAttributes();
        mTitleBar.setTitle(R.string.filter_by_knowledge_point);
        mOptionsAdapter = new ObjectsAdapter<>(
                this, ResourceFilterItemViewHolder.class);
        mOptionsLv.setAdapter(mOptionsAdapter);
        addItems();
    }

    private void initAttributes() {
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
        mSemesterChoice = getIntent().getParcelableExtra(EXTRA_SEMESTER);
    }

    private void addItems() {
        List<FilterItem> items = new ArrayList<>();
        addItem(items, "学段", "baseSemesterId", URLConfig.GET_SEMESTER_LIST,
                mSemesterChoice!=null? mSemesterChoice: mAllChoice);
        addItem(items, "学科", "baseSubjectId", URLConfig.ALL_SUBJECTS_LIST);
        addItem(items, "知识点", "knowledge1", URLConfig.ROOT_KNOWLEDGE);//?classlevelId=d3fa9752e3894f7687b3c4899d9cf500&subjectId=d3fa9752e3894f7687b3c4899d9cf600"
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
        WebApi webApi = RsGenerator.create(WebApi.class);
        Map<String, String> params = new HashMap<>();
        boolean noLimit = false;
        switch (item.getParamName()) {
            case "baseSemesterId":
            case "baseSubjectId":
                if (mAreaInfo.isArea()) {
                    params.put("areaId", mAreaInfo.getId());
                } else if (mAreaInfo.isSchool()) {
                    params.put("schoolId", mAreaInfo.getId());
                }
                break;
            case "knowledge1":
                FilterItem semesterItem = mOptionsAdapter.getItem(OPTION_SEMESTER);
                FilterItem subjectItem = mOptionsAdapter.getItem(OPTION_SUBJECT);
                Choice semester = semesterItem.getChoice();
                Choice subject = subjectItem.getChoice();
                if (semester == null || semester.isNothing()) {
                    shortlyToast("请先选择学段！");
                    uncheckItem(position);
                    return;
                }
                if (subject == null || subject.isNothing()) {
                    shortlyToast("请先选择学科！");
                    uncheckItem(position);
                    return;
                }
                params.put("semesterId", semester.getId());
                params.put("subjectId", subject.getId());
                break;
            case "knowledge2":
            case "knowledge3":
            case "knowledge4":
            case "knowledge5":
            case "knowledge6":
                noLimit = true;//下级知识点需要显示不限
                FilterItem knowledgeItem = mOptionsAdapter.getItem(position - 1);
                Choice knowledge = knowledgeItem.getChoice();
                params.put("parentId", knowledge.getId());
                break;
            case "subCategoryId":
                noLimit = true;//子分类需要显示不限
                FilterItem categoryItem = mOptionsAdapter.getItem(position - 1);
                Choice category = categoryItem.getChoice();
                params.put("parentId", category.getId());
                break;
            default:
                break;
        }
        final boolean needShowNoLimit = noLimit;
        Cog.d(TAG, "url=" + item.getUrl() + params);
        webApi.post4Json(item.getUrl(), params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onResponse response:" + response);
                        if ("success".equals(response.optString("result"))) {
                            List<Choice> choices = new Choice.BaseChoiceParser()
                                    .parseArray(response.optJSONArray("list"));
                            if (choices == null) {
                                choices = new ArrayList<>(1);
                            }
                            choices.add(0, needShowNoLimit ? mNoLimitChoice : mAllChoice);
                            showDialog(choices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "onErrorResponse error:" + error);
                        uncheckItem(position);
                        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                    }
                });
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
        intent.putParcelableArrayListExtra(EXTRA_KNOWLEDGE_FILTER, (ArrayList<? extends Parcelable>) mOptionsAdapter.getItems());
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
            return;
        }
        item.setChoice(choice);
        if (!choice.isNothing()) {
            switch (item.getParamName()) {
                case "baseSemesterId":
                case "baseSubjectId":
                    clearAllKnowledgeChoices();
                    break;
                case "knowledge1":
                    fitOutKnowledge(position + 1, "knowledge2");//下一级设成
                    clearChildKnowledgeOptions(position + 2);
                    break;
                case "knowledge2":
                    fitOutKnowledge(position + 1, "knowledge3");
                    clearChildKnowledgeOptions(position + 2);
                    break;
                case "knowledge3":
                    fitOutKnowledge(position + 1, "knowledge4");
                    clearChildKnowledgeOptions(position + 2);
                    break;
                case "knowledge4":
                    fitOutKnowledge(position + 1, "knowledge5");
                    clearChildKnowledgeOptions(position + 2);
                    break;
                case "knowledge5":
                    fitOutKnowledge(position + 1, "knowledge6");
                    break;
                case "categoryId":
                    fitOutItem(position + 1, "subCategoryId", URLConfig.SON_CATEGORIES);
                    break;
                default:
                    break;
            }
        } else {
            switch (item.getParamName()) {
                case "baseSemesterId":
                case "baseSubjectId":
                    clearAllKnowledgeChoices();
                    break;
                case "knowledge1":
                case "knowledge2":
                case "knowledge3":
                case "knowledge4":
                case "knowledge5":
                    clearChildKnowledgeOptions(position + 1);
                    break;
                case "subCategoryId":
                    clearChildCategoryOption(position + 1);
                    break;
            }
        }
        mOptionsAdapter.notifyDataSetChanged();
        mChoiceDialog.dismiss();
        uncheckItem(position);
    }

    /**
     * 学段或学科变化，得清了所有已选知识点
     */
    private void clearAllKnowledgeChoices() {
        FilterItem item = mOptionsAdapter.getItem(2);
        item.setChoice(mAllChoice);
        clearChildKnowledgeOptions(3);
    }

    private void clearChildCategoryOption(int position) {
        FilterItem childCategoryItem = mOptionsAdapter.getItem(position);
        if (childCategoryItem != null && "subCategoryId".equals(childCategoryItem.getParamName())) {
            mOptionsAdapter.removeItem( position);
        }
    }

    private void clearChildKnowledgeOptions(int start) {
        for (;;) {
            FilterItem item = mOptionsAdapter.getItem(start);
            if (!"categoryId".equals(item.getParamName())) {//删除类型以上的知识点选择项
                mOptionsAdapter.removeItem(start);
            } else {
                break;
            }
        }
    }

    /**
     * 在指定位置检测是否有对应参数名的知识点选择项，如无则添加，如有则将其设为“不限”
     * @param position 位置
     * @param paramName 参数名称
     */
    private void fitOutKnowledge(int position, @NonNull String paramName) {
        fitOutItem(position, paramName, URLConfig.KNOWLEDGE);
    }

    /**
     * 在指定位置检测是否有对应参数名的选择项，如无则添加，如有则将其设为“不限”
     * @param position 位置
     * @param paramName 参数名称
     * @param url 请求地址
     */
    private void fitOutItem(int position, @NonNull String paramName, String url) {
        FilterItem knowledgeItem = mOptionsAdapter.getItem(position);
        if (knowledgeItem == null || !paramName.equals(knowledgeItem.getParamName())) {
            mOptionsAdapter.addItem(position, new FilterItem("", paramName, url));
        } else {
            knowledgeItem.setChoice( mNoLimitChoice);
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
        Intent intent = new Intent( activity, ResourceKnowledgeFilterAct.class);
        intent.putExtra(Extra.AREA_INFO, areaInfo);
        activity.startActivityForResult(intent, REQUEST_FILTER);
        UIUtils.addEnterAnim(activity);
    }

    public static void startForResult(Activity activity, AreaInfo areaInfo, Choice semester) {
        Intent intent = new Intent( activity, ResourceKnowledgeFilterAct.class);
        intent.putExtra(EXTRA_SEMESTER , semester);
        intent.putExtra(Extra.AREA_INFO, areaInfo);
        activity.startActivityForResult(intent, REQUEST_FILTER);
        UIUtils.addEnterAnim(activity);
    }
}

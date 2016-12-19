package com.codyy.erpsportal.exam.controllers.activities.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ItemIndexListRecyBaseAdapter;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.TimePickerDialog;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 老师-布置作业
 * Created by eachann on 2016/1/21.
 */
public class TeacherArrangeActivity extends ToolbarActivity {
    private static final String TAG = TeacherArrangeActivity.class.getSimpleName();
    public static final String DIALOG_TITLE = "TITLE";
    private static final int REQUEST_CODE = 100;
    private static final int RESULT_CODE = 200;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    @Bind(R.id.tv_arrange_start_time)
    TextView mArrangeStartTime;
    @Bind(R.id.tv_arrange_end_time)
    TextView mArrangeEndTime;
    @Bind(R.id.tv_is_selected)
    TextView mSelectTitle;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;


    @OnClick(R.id.tv_arrange_start_time)
    public void onClickArrangeStartTime(final View view) {
        TimePickerDialog dialog = new TimePickerDialog();
        dialog.setOnConfirmListener(new TimePickerDialog.OnClickTimePicker() {
            @Override
            public void onConfirmClickListener(String time) {
                if (mArrangeEndTime.getText() != null && !TextUtils.isEmpty(mArrangeEndTime.getText())) {
                    Date end = DateUtil.stringToDate(mArrangeEndTime.getText().toString(), DateUtil.DEF_FORMAT);
                    Date start = DateUtil.stringToDate(time, DateUtil.DEF_FORMAT);
                    if (start.after(end)) {
                        Snackbar.make(view, getString(R.string.exam_arrange_start_time_error_msg), Snackbar.LENGTH_SHORT).show();
                        mArrangeStartTime.setText(null);
                    } else {
                        mArrangeStartTime.setText(time);
                    }

                } else {
                    mArrangeStartTime.setText(time);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_TITLE, getString(R.string.exam_arrange_start_time_title));
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), TimePickerDialog.TAG);
    }

    @OnClick(R.id.tv_arrange_end_time)
    public void onClickArrangeEndTime(final View view) {
        TimePickerDialog dialog = new TimePickerDialog();
        dialog.setOnConfirmListener(new TimePickerDialog.OnClickTimePicker() {
            @Override
            public void onConfirmClickListener(String time) {
                if (mArrangeStartTime.getText() != null && !TextUtils.isEmpty(mArrangeStartTime.getText())) {
                    Date start = DateUtil.stringToDate(mArrangeStartTime.getText().toString(), DateUtil.DEF_FORMAT);
                    Date end = DateUtil.stringToDate(time, DateUtil.DEF_FORMAT);
                    if (end.before(start)) {
                        Snackbar.make(view, getString(R.string.exam_arrange_end_time_error_msg), Snackbar.LENGTH_SHORT).show();
                        mArrangeEndTime.setText(null);
                    } else {
                        mArrangeEndTime.setText(time);
                    }

                } else {
                    mArrangeEndTime.setText(time);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_TITLE, getString(R.string.exam_arrange_end_time_title));
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), TimePickerDialog.TAG);

    }

    //原来：跳转到@Deprecated TeacherArrangeListActivity，现改为：跳转到TeacherArrangeTreeActivity
    @OnClick(R.id.tv_class_list_head)
    public void onClickSelectPerson(View view) {
        Intent intent = new Intent(this, TeacherArrangeTreeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
        UIUtils.addEnterAnim(this);
    }

    private ItemListRecyclerAdapter mAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE && data != null) {
            ArrayList<TreeItem> mTreeItems = data.getParcelableArrayListExtra(EXTRA_DATA);
            if (mTreeItems.size() > 0) {
                mTvTaskTitle.setVisibility(View.VISIBLE);
                mSelectTitle.setVisibility(View.VISIBLE);
                mAdapter.setList(mTreeItems);
                for (int i = 0; i < mTreeItems.size(); i++) {
                    String childNames = mTreeItems.get(i).getChildNames();
                    try {
                        JSONArray jsonArray = new JSONArray(childNames);
                        for (int j = 0; j < jsonArray.length(); j++) {
                            mStuIdsArr.put(jsonArray.optJSONObject(j).optString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                mTvTaskTitle.setVisibility(View.GONE);
                mSelectTitle.setVisibility(View.INVISIBLE);
                mAdapter.setList(new ArrayList<TreeItem>());
            }
            /*if (mTreeItems.size() > 1) {
                mTvTaskTitle.setVisibility(View.VISIBLE);
                mSelectTitle.setVisibility(View.VISIBLE);
                List<TreeItemList> mItemLists = new ArrayList<>();
                for (int i = 0; i < mTreeItems.size(); i++) {
                    if (mTreeItems.get(i).getType() == 2) {
                        TreeItemList treeItemList = new TreeItemList();
                        treeItemList.setClasslevelId(mTreeItems.get(i).getClasslevelId());
                        treeItemList.setClasslevelName(mTreeItems.get(i).getClasslevelName());
                        treeItemList.setSelected(mTreeItems.get(i).isSelected());
                        treeItemList.setType(mTreeItems.get(i).getType());
                        mItemLists.add(treeItemList);
                    }
                    if (mTreeItems.get(i).getType() == 1 && mTreeItems.get(i).isSelected()) {
                        TreeItemList treeItemList = new TreeItemList();
                        treeItemList.setClasslevelId(mTreeItems.get(i).getClasslevelId());
                        treeItemList.setClasslevelName(mTreeItems.get(i).getClasslevelName());
                        treeItemList.setSelected(mTreeItems.get(i).isSelected());
                        treeItemList.setType(mTreeItems.get(i).getType());
                        List<TreeItem> treeItems = new ArrayList<>();
                        for (int j = i + 1; j < mTreeItems.size(); j++) {
                            if (mTreeItems.get(j).getType() == 1 && mTreeItems.get(j).isSelected()) {
                                i = j - 1;
                                break;
                            } else {
                                treeItems.add(mTreeItems.get(j));
                                mStuIds.add(mTreeItems.get(j).getClasslevelId());
                            }
                        }
                        treeItemList.setList(treeItems);
                        mItemLists.add(treeItemList);
                    }
                    mAdapter.setList(mItemLists);
                }

            } else {
                mTvTaskTitle.setVisibility(View.GONE);
                mSelectTitle.setVisibility(View.INVISIBLE);
                mAdapter.setList(new ArrayList<TreeItemList>());
            }*/
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_arrange;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        setViewAnim(false, mTitle);
        mTitle.setText(getString(R.string.exam_arrange));
        mAdapter = new ItemListRecyclerAdapter(this, new ArrayList<TreeItem>());
        mRecyclerView.setAdapter(mAdapter);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public static void setResultActivity(List<TreeItem> list, Activity activity) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_DATA, (ArrayList<? extends Parcelable>) list);
        activity.setResult(RESULT_CODE, intent);
    }

    public static void startActivity(Context from, String examId) {
        Intent intent = new Intent(from, TeacherArrangeActivity.class);
        intent.putExtra(EXTRA_EXAM_ID, examId);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }

    public class ItemListRecyclerAdapter extends ItemIndexListRecyBaseAdapter<TreeItem> {

        public ItemListRecyclerAdapter(Context context, List<TreeItem> list) {
            super(context, list);
        }

        /**
         * 获取布局文件
         *
         * @param viewType view类型
         * @return id
         */
        @Override
        protected int getLayoutId(int viewType) {
            return viewType == TYPE_TITLE ? R.layout.item_work_item_index_title : R.layout.item_exam_gridlayout;
        }

        /**
         * 创建viewholder
         *
         * @param view
         * @param viewType
         * @return
         */
        @Override
        protected RecyclerView.ViewHolder createViewHolder(View view, int viewType) {
            return viewType == TYPE_TITLE ? new ItemTitleViewHolder(view) : new ItemIndexViewHolder(view);
        }

        /**
         * 获取布局类型
         *
         * @param position
         * @return
         */
        @Override
        public int getItemType(int position) {
            return mList.get(position).getType() == TYPE_CONTENT ? TYPE_CONTENT : TYPE_TITLE;
        }
    }

    public static class ItemTitleViewHolder extends RecyclerViewHolder<TreeItem> {
        private TextView textView;


        public ItemTitleViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            textView = (TextView) view.findViewById(R.id.tv_title_item_list);
        }

        @Override
        public void setDataToView(TreeItem data) {
            textView.setText(data.getParentName() + data.getClasslevelName());
        }
    }

    public static class ItemIndexViewHolder extends RecyclerViewHolder<TreeItem> {
        private TextView mTitle;
        private GridLayout glLayout;
        private Context context;

        public ItemIndexViewHolder(View view) {
            super(view);
            context = view.getContext();
        }

        @Override
        public void mapFromView(View view) {
            mTitle = (TextView) view.findViewById(R.id.tv_class);
            glLayout = (GridLayout) view.findViewById(R.id.gl_layout);
        }

        @Override
        public void setDataToView(final TreeItem data) {
            mTitle.setText(data.getParentName() + data.getClasslevelName());
            JSONArray childrenArr = null;
            try {
                childrenArr = new JSONArray(data.getChildNames());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (childrenArr != null) {
                for (int i = 0; i < childrenArr.length(); i++) {
                    JSONObject object = childrenArr.optJSONObject(i);
                    GridLayout.LayoutParams layout = new GridLayout.LayoutParams(GridLayout.spec(i / 4), GridLayout.spec(i % 4));
                    layout.width = UIUtils.dip2px(context, 80);
                    layout.setMargins(UIUtils.dip2px(context, 4), UIUtils.dip2px(context, 4), UIUtils.dip2px(context, 4), UIUtils.dip2px(context, 4));
                    Button button = new Button(context);
                    button.setSingleLine();
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        button.setElevation(UIUtils.dip2px(context, 2));//添加阴影
                    }*/
                    button.setText(object.isNull("name") ? "" : object.optString("name"));
                    button.setBackgroundResource(R.color.main_green);
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                    button.setTextColor(context.getResources().getColor(R.color.white));
                    glLayout.addView(button, layout);
                }
            }
        }
    }

    private TextView mTvTaskTitle;
    private JSONArray mStuIdsArr = new JSONArray();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        mTvTaskTitle = (TextView) linearLayout.findViewById(R.id.task_title);
        mTvTaskTitle.setText(getString(R.string.exam_arrange_select_person_confirm));
        mTvTaskTitle.setVisibility(View.GONE);
        mTvTaskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mArrangeStartTime.getText())) {
                    Snackbar.make(v, getString(R.string.exam_arrange_start_time_empty), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mArrangeEndTime.getText())) {
                    Snackbar.make(v, getString(R.string.exam_arrange_end_time_empty), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (mStuIdsArr == null || mStuIdsArr.length() == 0) {
                    Snackbar.make(v, getString(R.string.exam_arrange_select_person_toast), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                finishCreateClassTask(mArrangeStartTime.getText().toString(), mArrangeEndTime.getText().toString(), mStuIdsArr.toString());
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void finishCreateClassTask(String startTime, String endTime, String classes) {
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        map.put("examId", getIntent().getStringExtra(EXTRA_EXAM_ID));
        map.put("examStartTime", startTime);
        map.put("examEndTime", endTime);
        map.put("studentIds", classes);
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.POST_FINISH_CREATE_TASK, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.i(TAG, response.toString());
                if ("success".equals(response.optString("result"))) {
                    ToastUtil.showToast(TeacherArrangeActivity.this, "布置完成");
                    finish();
                } else {
                    ToastUtil.showToast(TeacherArrangeActivity.this, "布置失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(TeacherArrangeActivity.this, "网络错误");
            }
        }));
    }
}

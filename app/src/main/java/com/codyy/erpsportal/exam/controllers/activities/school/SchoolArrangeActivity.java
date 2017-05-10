package com.codyy.erpsportal.exam.controllers.activities.school;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ItemIndexListRecyBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.TimePickerDialog;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.erpsportal.statistics.widgets.OrderLayout;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 布置作业
 * Created by eachann on 2016/1/21.
 */
public class SchoolArrangeActivity extends ToolbarActivity {
    private static final String TAG = SchoolArrangeActivity.class.getSimpleName();
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
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_is_selected)
    TextView mSelectTitle;


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

    @OnClick(R.id.tv_class_list_head)
    public void onClickSelectPerson(View view) {
        SchoolArrangeTreeActivity.startActivity(this, getIntent().getStringExtra(EXTRA_EXAM_ID), mTreeItemList);
    }

    private ItemListRecyclerAdapter mAdapter;
    private ArrayList<TreeItem> mTreeItemList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE && data != null) {
            mTreeItemList = data.getParcelableArrayListExtra(EXTRA_DATA);
            if (mTreeItemList.size() > 0) {
                mTvTaskTitle.setVisibility(View.VISIBLE);
                mSelectTitle.setVisibility(View.VISIBLE);
                mAdapter.setList(mTreeItemList);
            } else {
                mTvTaskTitle.setVisibility(View.GONE);
                mSelectTitle.setVisibility(View.INVISIBLE);
                mAdapter.setList(new ArrayList<TreeItem>());
            }
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
        //mAdapter = new ItemListVariableAdapter(this, new ArrayList<TreeItem>());
        mRecyclerView.setAdapter(mAdapter);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mTreeItemList.get(position).getType() == 1 ? 1 : gridLayoutManager.getSpanCount();
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public static void setResultActivity(List<TreeItem> list, Activity activity) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_DATA, (ArrayList<? extends Parcelable>) list);
        activity.setResult(RESULT_CODE, intent);
    }

    public static void startActivity(Context from, String examId) {
        Intent intent = new Intent(from, SchoolArrangeActivity.class);
        intent.putExtra(EXTRA_EXAM_ID, examId);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }

    public class ItemListVariableAdapter extends ItemIndexListRecyBaseAdapter<TreeItem> {

        private List<TreeItem> mList = null;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() != TYPE_TITLE) {
                ItemIndexNewViewHolder holder1 = (ItemIndexNewViewHolder) holder;
                Button button = new Button(mContext);
                button.setSingleLine();
                button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f);
                button.setLayoutParams(new ViewGroup.LayoutParams(OrderLayout.LayoutParams.WRAP_CONTENT, OrderLayout.LayoutParams.WRAP_CONTENT));
                button.setText(mList.get(position).getClasslevelName());
                holder1.mAttributesOl.addView(button);
            }
        }

        public ItemListVariableAdapter(Context context, List<TreeItem> list) {
            super(context, list);
            mList = list;
        }

        @Override
        public void setList(List<TreeItem> list) {
            mList = list;
            notifyDataSetChanged();
        }

        public List<TreeItem> getTreeItem() {
            List<TreeItem> items = new ArrayList<>();
            for (TreeItem item : mList) {
                if (item.getType() != 2) {
                    items.add(item);
                }
            }
            return items;
        }

        /**
         * 获取布局文件
         *
         * @param viewType view类型
         * @return id
         */
        @Override
        protected int getLayoutId(int viewType) {
            return viewType == TYPE_TITLE ? R.layout.item_work_item_index_title : R.layout.item_selected_arrange_stu;
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
            return viewType == TYPE_TITLE ? new ItemTitleViewHolder(view) : new ItemIndexNewViewHolder(view);
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

    public static class ItemIndexNewViewHolder extends RecyclerView.ViewHolder {
        public OrderLayout mAttributesOl;

        public ItemIndexNewViewHolder(View itemView) {
            super(itemView);
            mAttributesOl = (OrderLayout) itemView.findViewById(R.id.ol_attributes);
        }
    }


    public class ItemListRecyclerAdapter extends ItemIndexListRecyBaseAdapter<TreeItem> {

        public ItemListRecyclerAdapter(Context context, List<TreeItem> list) {
            super(context, list);
        }

        public List<TreeItem> getTreeItem() {
            List<TreeItem> items = new ArrayList<>();
            for (TreeItem item : mList) {
                if (item.getType() != 2) {
                    items.add(item);
                }
            }
            return items;
        }

        /**
         * 获取布局文件
         *
         * @param viewType view类型
         * @return id
         */
        @Override
        protected int getLayoutId(int viewType) {
            return viewType == TYPE_TITLE ? R.layout.item_work_item_index_title : R.layout.toolbar_task_button;
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
            textView.setText(data.getClasslevelName());
        }
    }

    public static class ItemIndexViewHolder extends RecyclerViewHolder<TreeItem> {
        private TextView button;
        private Context context;

        public ItemIndexViewHolder(View view) {
            super(view);
            context = view.getContext();
        }

        @Override
        public void mapFromView(View view) {
            button = (TextView) view.findViewById(R.id.task_title);
        }

        @Override
        public void setDataToView(final TreeItem data) {
            button.setSingleLine();
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(UIUtils.dip2px(context, 90), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = UIUtils.dip2px(context, 10f);
            layoutParams.bottomMargin = UIUtils.dip2px(context, 8f);
            layoutParams.gravity = Gravity.CENTER;
            int padding = UIUtils.dip2px(context, 8f);
            button.setPadding(padding, padding, padding, padding);
            button.setLayoutParams(layoutParams);
            button.setText(String.valueOf(data.getClasslevelName()));
        }
    }

    private TextView mTvTaskTitle;

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
                if (mAdapter.getTreeItem() == null || mAdapter.getTreeItem().size() == 0) {
                    Snackbar.make(v, getString(R.string.exam_arrange_select_person_toast), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                JSONArray jsonArray = new JSONArray();
                for (TreeItem item : mAdapter.getTreeItem()) {
                    jsonArray.put(item.getClasslevelId());
                }
                finishCreateClassTask(mArrangeStartTime.getText().toString(), mArrangeEndTime.getText().toString(), jsonArray.toString());

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
        map.put("classIds", classes);
        Cog.i(TAG, map.toString());
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.POST_CREATE_CLASS_TASK, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    ToastUtil.showToast("布置完成");
                    finish();
                } else {
                    ToastUtil.showToast("布置失败");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                ToastUtil.showToast("网络错误");
            }
        }));
    }
}

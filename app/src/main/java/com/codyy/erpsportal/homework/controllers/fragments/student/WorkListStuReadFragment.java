package com.codyy.erpsportal.homework.controllers.fragments.student;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.homework.controllers.activities.WorkItemDetailActivity;
import com.codyy.erpsportal.homework.controllers.activities.WorkStatisticListActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.homework.models.entities.student.WorkListStuReadClass;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class WorkListStuReadFragment extends LoadMoreFragment<WorkListStuReadClass, WorkListStuReadFragment.StuReadViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    public static final String TAG = WorkListStuReadFragment.class.getSimpleName();
    private static final String STATUS_READ_WAITING = "END";//待批阅
    private static final String STATUS_READ = "CHECKED";//已批阅

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public WorkListStuReadFragment() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String action) {
        if ((WorkItemDetailActivity.class.getSimpleName() + " read N").equals(action)) {
            loadData(true);
        }
    }

    @Override
    protected ViewHolderCreator<StuReadViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<StuReadViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_work_list_read;
            }

            @Override
            protected StuReadViewHolder doCreate(View view) {
                return new StuReadViewHolder(view, getActivity());
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_STU_READ_LIST;
    }

    @Override
    protected List<WorkListStuReadClass> getList(JSONObject response) {
        return WorkListStuReadClass.parseResponse(response);
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("baseUserId", UserInfoKeeper.obtainUserInfo().getBaseUserId());
    }

    /**
     * 确认过滤时调用
     *
     * @param params 过滤参数，用于附加到url请求参数列表，实现过滤。
     */
    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        addParam("state", TextUtils.isEmpty(params.get("stuSelfState")) ? "" : params.get("stuSelfState"));
        loadData(true);
    }

    public static class StuReadViewHolder extends RecyclerViewHolder<WorkListStuReadClass> {
        private SimpleDraweeView subjectIcon;
        private TextView workNameTv;
        private TextView workItemCountTv;
        private TextView workAssignPercentTv;
        private TextView workAssignTimeTv;
        private ImageView workStatusIv;
        private TextView mReadBrowserTv;
        private TextView mReadWorkTv;
        private View divisionView;
        private View container;
        private Context mContext;

        public StuReadViewHolder(View view, FragmentActivity context) {
            super(view);
            this.mContext = context;
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            subjectIcon = (SimpleDraweeView) container.findViewById(R.id.imgOfLessonItem);
            workNameTv = (TextView) container.findViewById(R.id.tv_title);
            workItemCountTv = (TextView) container.findViewById(R.id.tv_total_count);
            workAssignPercentTv = (TextView) container.findViewById(R.id.tv_percent);
            workAssignTimeTv = (TextView) container.findViewById(R.id.tv_assign_time);
            workStatusIv = (ImageView) container.findViewById(R.id.iv_status);
            mReadWorkTv = (TextView) container.findViewById(R.id.tv_read_worklist);
            mReadBrowserTv = (TextView) container.findViewById(R.id.tv_read_browser_work);
            divisionView = container.findViewById(R.id.line_division);
        }

        @Override
        public void setDataToView(final WorkListStuReadClass data) {
            subjectIcon.setImageURI(Uri.parse(URLConfig.IMAGE_URL + data.getSubjectPic()));
            workNameTv.setText(data.getWorkName());
            workItemCountTv.setText(data.getWorkTotal());
            workAssignPercentTv.setText(WorkUtils.switchStr(data.getReadFinishedCount(), mContext.getResources().getColor(R.color.work_statistic_circle_color_2)));
            workAssignTimeTv.setText(data.geworkAssignTime());
            switch (data.getWorkState()) {
                case STATUS_READ_WAITING:
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.to_read));
                    mReadWorkTv.setVisibility(View.VISIBLE);
                    divisionView.setVisibility(View.VISIBLE);
                    break;
                case STATUS_READ:
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.read));
                    mReadWorkTv.setVisibility(View.GONE);
                    divisionView.setVisibility(View.GONE);
                    break;
            }
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkItemDetailActivity.startActivity(mContext, data.getWorkId(), WorkItemDetailFragment.STATUS_STU_VIEW, null, data.getReadOverType(), data.getWorkName());
                }
            });
            String readNum = data.getReadFinishedCount().substring(0, data.getReadFinishedCount().indexOf("/"));
            if (Integer.valueOf(readNum) == 0) {
                mReadWorkTv.setVisibility(View.GONE);
                divisionView.setVisibility(View.GONE);
            }

            //点击“批阅”
            mReadWorkTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String readNum = data.getReadFinishedCount().substring(0, data.getReadFinishedCount().indexOf("/"));
                    if (Integer.valueOf(readNum) > 0) {
                        WorkStatisticListActivity.startActivity(mContext, data.getWorkName(), data.getWorkId(), null, WorkStatisticListActivity.FROM_STU_READ, data.getReadOverType());
                    } else {
                        ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.work_stu_list_no_person));
                    }
                }
            });

            //点击“查看批阅”
            mReadBrowserTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String readNum = data.getReadFinishedCount().substring(0, data.getReadFinishedCount().indexOf("/"));
                    if (Integer.valueOf(readNum) > 0) {
                        WorkStatisticListActivity.startActivity(mContext, data.getWorkName(), data.getWorkId(), null, WorkStatisticListActivity.FROM_STU_READ_BROWSER, data.getReadOverType());
                    } else {
                        ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.work_stu_list_no_person));
                    }
                }
            });
        }
    }
}

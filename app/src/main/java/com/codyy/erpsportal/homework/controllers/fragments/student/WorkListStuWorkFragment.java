package com.codyy.erpsportal.homework.controllers.fragments.student;

import android.content.Context;
import android.content.Intent;
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
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.homework.models.entities.student.WorkListStuWorkClass;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class WorkListStuWorkFragment extends LoadMoreFragment<WorkListStuWorkClass, WorkListStuWorkFragment.StuWorkViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    public static final String TAG = WorkListStuWorkFragment.class.getSimpleName();
    private static final String STATUS_CHECKED = "CHECKED";//已完成
    private static final String STATUS_END = "END";//待批阅
    private static final String STATUS_PROGRESS = "PROGRESS";//未完成

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public WorkListStuWorkFragment() {
    }

    @Override
    protected ViewHolderCreator<StuWorkViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<StuWorkViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_work_list_student;
            }

            @Override
            protected StuWorkViewHolder doCreate(View view) {
                return new StuWorkViewHolder(view, getActivity());
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_STU_HOMEWORK_LIST;
    }

    @Override
    protected List<WorkListStuWorkClass> getList(JSONObject response) {
        Cog.d(TAG, response);
        return WorkListStuWorkClass.parseResponse(response);
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
        addParam("state", TextUtils.isEmpty(params.get("stuState")) ? "" : params.get("stuState"));
        loadData(true);
    }

    public void onEventMainThread(String action) {
        if ((WorkItemDetailActivity.class.getSimpleName() + " do").equals(action)) {
            ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_answer_submit_success));
            loadData(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static class StuWorkViewHolder extends RecyclerViewHolder<WorkListStuWorkClass> {
        private SimpleDraweeView subjectIcon;
        private TextView workNameTv;
        private TextView workItemFinishCountTv;
        private TextView workAccurcyTv;
        private TextView workAssignTimeTv;
        private ImageView workStatusIv;
        private View container;
        private Context mContext;
        private ImageView mIvInteractiveComment;

        public StuWorkViewHolder(View view, FragmentActivity context) {
            super(view);
            this.mContext = context;
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            subjectIcon = (SimpleDraweeView) container.findViewById(R.id.imgOfLessonItem);
            workNameTv = (TextView) container.findViewById(R.id.tv_title);
            workItemFinishCountTv = (TextView) container.findViewById(R.id.tv_item_finished_count);
            workAccurcyTv = (TextView) container.findViewById(R.id.tv_work_accuracy);
            workAssignTimeTv = (TextView) container.findViewById(R.id.tv_assign_time);
            workStatusIv = (ImageView) container.findViewById(R.id.iv_status);
            mIvInteractiveComment = (ImageView) container.findViewById(R.id.iv_interactive_comment);
        }


        @Override
        public void setDataToView(final WorkListStuWorkClass data) {
            subjectIcon.setImageURI(Uri.parse(URLConfig.IMAGE_URL + data.getSubjectPic()));
            workNameTv.setText(data.getWorkName());
            workItemFinishCountTv.setText(WorkUtils.switchStr(data.getItemFinishedCount(), mContext.getResources().getColor(R.color.work_statistic_circle_color_2)));
            workAccurcyTv.setText(WorkUtils.roundFloat(data.getWorkAccuracy()));
            workAssignTimeTv.setText(data.geworkAssignTime());
            switch (data.getWorkState()) {
                case STATUS_PROGRESS://未完成
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_unfinished));
                    break;
                case STATUS_CHECKED://已完成
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_finished));
                    break;
                case STATUS_END://待批阅
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.to_read));
                    break;
            }

            if (WorkUtils.READ_TYPE_TEACHER.equals(data.getReadOverType())) {
                mIvInteractiveComment.setVisibility(View.GONE);
            } else {
                mIvInteractiveComment.setVisibility(View.VISIBLE);
            }
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getHaveAttachment() && data.getWorkState().equals(STATUS_PROGRESS)) {//拥有附件题，并状态为未完成时，不可进入习题页面
                        ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.work_tip_have_attachment));
                    } else {
                        Intent intent = new Intent(mContext, WorkItemDetailActivity.class);
                        intent.putExtra(WorkItemDetailActivity.EXTRA_WORK_ID, data.getWorkId());
                        intent.putExtra(WorkItemDetailActivity.EXTRA_CURRENT_STATUS, getStatus(data.getWorkState()));
                        intent.putExtra(WorkItemDetailActivity.EXTRA_STUDENT_ID, UserInfoKeeper.getInstance().getUserInfo().getBaseUserId());
                        intent.putExtra(WorkItemDetailActivity.EXTRA_IS_TEACHER_READ, data.getReadOverType());
                        intent.putExtra(WorkItemDetailActivity.EXTRA_TITLE, UserInfoKeeper.getInstance().getUserInfo().getRealName());
                        WorkItemDetailActivity.startActivity(mContext, data.getWorkId(), getStatus(data.getWorkState()),
                                UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), data.getReadOverType(),data.getWorkName());
                    }
                }
            });
        }
    }

    /**
     * 转换状态
     *
     * @param status
     * @return
     */
    private static String getStatus(String status) {
        switch (status) {
            case STATUS_PROGRESS:
                return WorkItemDetailFragment.STATUS_STU_PROGRESS;
            case STATUS_END:
                return WorkItemDetailFragment.STATUS_STU_END;
            case STATUS_CHECKED:
                return WorkItemDetailFragment.STATUS_STU_CHECKED;
            default:
                return null;
        }
    }
}

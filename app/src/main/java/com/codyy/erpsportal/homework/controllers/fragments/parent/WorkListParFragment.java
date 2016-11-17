package com.codyy.erpsportal.homework.controllers.fragments.parent;

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
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.homework.models.entities.parent.WorkListParentClass;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 家长作业中心
 * Created by ldh on 2016/1/27.
 */
public class WorkListParFragment extends LoadMoreFragment<WorkListParentClass, WorkListParFragment.ParWorkViewHolder> implements TabsWithFilterActivity.OnFilterObserver {
    public static final String TAG = WorkListParFragment.class.getSimpleName();
    private static final String STATUS_CHECKED = "CHECKED";//已完成
    private static final String STATUS_END = "END";//待批阅
    private static final String STATUS_PROGRESS = "PROGRESS";//未完成

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WorkListParFragment() {
    }

    @Override
    protected ViewHolderCreator<ParWorkViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ParWorkViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_work_list_student;
            }

            @Override
            protected ParWorkViewHolder doCreate(View view) {
                return new ParWorkViewHolder(view,getActivity());
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_PAR_HOMEWORK_LIST;
    }

    @Override
    protected List<WorkListParentClass> getList(JSONObject response) {
        Cog.d(TAG, response);
        return WorkListParentClass.parseResponse(response);
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("studentId", UserInfoKeeper.obtainUserInfo().getSelectedChild().getStudentId());//studentId待修改
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

    public static class ParWorkViewHolder extends RecyclerViewHolder<WorkListParentClass> {
        private SimpleDraweeView subjectIcon;
        private TextView workNameTv;
        private TextView workItemFinishCountTv;
        private TextView workAccurcyTv;
        private TextView workAssignTimeTv;
        private ImageView workStatusIv;
        private View container;
        private Context mContext;
        private ImageView mIvInteractiveComment;

        public ParWorkViewHolder(View view, FragmentActivity context) {
            super(view);
            this.mContext = context;
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            subjectIcon = (SimpleDraweeView)container.findViewById(R.id.imgOfLessonItem);
            workNameTv = (TextView) container.findViewById(R.id.tv_title);
            workItemFinishCountTv = (TextView) container.findViewById(R.id.tv_item_finished_count);
            workAccurcyTv = (TextView) container.findViewById(R.id.tv_work_accuracy);
            workAssignTimeTv = (TextView) container.findViewById(R.id.tv_assign_time);
            workStatusIv = (ImageView) container.findViewById(R.id.iv_status);
            mIvInteractiveComment = (ImageView)container.findViewById(R.id.iv_interactive_comment);
        }

        @Override
        public void setDataToView(final WorkListParentClass data) {
            subjectIcon.setImageURI(Uri.parse(URLConfig.IMAGE_URL + data.getSubjectPic()));
            workNameTv.setText(data.getWorkName());
            workItemFinishCountTv.setText(WorkUtils.switchStr(data.getItemFinishedCount(), mContext.getResources().getColor(R.color.work_statistic_circle_color_2)));
            workAccurcyTv.setText(data.getWorkAccuracy());
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
            if(WorkUtils.READ_TYPE_TEACHER.equals(data.getReadOverType())){
                mIvInteractiveComment.setVisibility(View.GONE);
            }else{
                mIvInteractiveComment.setVisibility(View.VISIBLE);
            }
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkItemDetailActivity.startActivity(mContext, data.getWorkId(), getStatus(data.getWorkState()),
                            UserInfoKeeper.obtainUserInfo().getSelectedChild().getStudentId(),data.getReadOverType(),data.getWorkName());
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
                return WorkItemDetailFragment.STATUS_PAR_PROGRESS;
            case STATUS_END:
                return WorkItemDetailFragment.STATUS_PAR_END;
            case STATUS_CHECKED:
                return WorkItemDetailFragment.STATUS_PAR_CHECKED;
            default:
                return null;
        }
    }
}

package com.codyy.erpsportal.homework.controllers.fragments.teacher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import com.codyy.erpsportal.homework.controllers.activities.WorkItemDetailActivity;
import com.codyy.erpsportal.homework.controllers.activities.WorkStatisticDetailActivity;
import com.codyy.erpsportal.homework.controllers.activities.WorkStuReadActivity;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.models.entities.teacher.WorkListTeacherClass;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class WorkListTeacherFragment extends LoadMoreFragment<WorkListTeacherClass, WorkListTeacherFragment.TeacherViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    public static final String TAG = WorkListTeacherFragment.class.getSimpleName();
    private static final String STATUS_ASSIGN_WAITING = "INIT";//待布置
    private static final String STATUS_READ_WAITING = "PROGRESS";//待批阅
    private static final String STATUS_END = "END";//已批阅

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WorkListTeacherFragment() {

    }

    @Override
    protected AbsVhrCreator<TeacherViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<TeacherViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_work_list;
            }

            @Override
            protected TeacherViewHolder doCreate(View view) {
                return new TeacherViewHolder(view, getActivity());
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_HOMEWORK_LIST;
    }

    @Override
    protected List<WorkListTeacherClass> getList(JSONObject response) {
        return WorkListTeacherClass.parseTeacherWorkList(response);
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        addParam("userId", UserInfoKeeper.obtainUserInfo().getBaseUserId());
    }

    /**
     * 确认过滤时调用
     *
     * @param params 过滤参数，用于附加到url请求参数列表，实现过滤。
     */
    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        addParam("state", TextUtils.isEmpty(params.get("teaState")) ? "" : params.get("teaState"));
        loadData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(true);
    }

    public class TeacherViewHolder extends RecyclerViewHolder<WorkListTeacherClass> {
        private SimpleDraweeView subjectIcon;
        private TextView workNameTv;
        private TextView workItemCountTv;
        private TextView workAssignPercentTv;
        private TextView workAssignTimeTv;
        private ImageView workStatusIv;
        private TextView workReadTv;
        private View divisionView;
        private TextView workStatisticTv;
        private TextView workArrangeTv;
        private View container;
        private Context mContext;
        private LinearLayout mReadStatisticLinearLayout;//批阅统计行布局
        private LinearLayout mArrangeLinearLayout;//布置行布局

        private ImageView mIvInteractiveComment;

        public TeacherViewHolder(View view, FragmentActivity context) {
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
            workReadTv = (TextView) container.findViewById(R.id.tv_read_worklist);
            divisionView = container.findViewById(R.id.line_division);
            workStatisticTv = (TextView) container.findViewById(R.id.tv_statistic_work);
            workArrangeTv = (TextView) container.findViewById(R.id.tv_arrange_work);
            mReadStatisticLinearLayout = (LinearLayout) container.findViewById(R.id.ll_work_action);
            mArrangeLinearLayout = (LinearLayout) container.findViewById(R.id.ll_work_arrange);
            mIvInteractiveComment = (ImageView) container.findViewById(R.id.iv_interactive_comment);
        }

        @Override
        public void setDataToView(final WorkListTeacherClass data) {
            subjectIcon.setImageURI(Uri.parse(URLConfig.IMAGE_URL + data.getSubjectPic()));
            workNameTv.setText(data.getWorkName());
            workItemCountTv.setText(data.getWorkTotal());
            workAssignPercentTv.setText(WorkUtils.switchStr(data.getWorkFinishedCount(), mContext.getResources().getColor(R.color.work_statistic_circle_color_2)));
            workAssignTimeTv.setText(data.geworkAssignTime());
            switch (data.getWorkState()) {
                case STATUS_ASSIGN_WAITING:
                    mReadStatisticLinearLayout.setVisibility(View.GONE);
                    mArrangeLinearLayout.setVisibility(View.VISIBLE);
                    workReadTv.setVisibility(View.VISIBLE);
                    divisionView.setVisibility(View.VISIBLE);
                    workAssignTimeTv.setVisibility(View.GONE);
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.read_waiting));
                    break;
                case STATUS_READ_WAITING:
                    mReadStatisticLinearLayout.setVisibility(View.VISIBLE);
                    mArrangeLinearLayout.setVisibility(View.GONE);
                    workReadTv.setVisibility(View.VISIBLE);
                    divisionView.setVisibility(View.VISIBLE);
                    workAssignTimeTv.setVisibility(View.VISIBLE);
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.to_read));
                    break;
                case STATUS_END:
                    mReadStatisticLinearLayout.setVisibility(View.VISIBLE);
                    mArrangeLinearLayout.setVisibility(View.GONE);
                    workReadTv.setVisibility(View.GONE);
                    divisionView.setVisibility(View.GONE);
                    workAssignTimeTv.setVisibility(View.VISIBLE);
                    workStatusIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.read));
                    break;
            }
            String readNum = data.getWorkFinishedCount().substring(0, data.getWorkFinishedCount().indexOf("/"));
            if (Integer.valueOf(readNum) > 0) {
                /*workReadTv.setVisibility(View.VISIBLE);
                divisionView.setVisibility(View.VISIBLE);*/
            } else {
                workReadTv.setVisibility(View.GONE);
                divisionView.setVisibility(View.GONE);
            }
            if (WorkUtils.READ_TYPE_TEACHER.equals(data.getReadOverType())) {
                mIvInteractiveComment.setVisibility(View.GONE);
            } else {
                mIvInteractiveComment.setVisibility(View.VISIBLE);
                workReadTv.setVisibility(View.GONE);
                divisionView.setVisibility(View.GONE);
            }
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkItemDetailActivity.startActivity(mContext, data.getWorkId(), WorkItemDetailFragment.STATUS_TEACHER_VIEW, null, data.getReadOverType(), data.getWorkName());
                }
            });
            //点击“批阅”
            workReadTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String readNum = data.getWorkFinishedCount().substring(0, data.getWorkFinishedCount().indexOf("/"));
                    if (Integer.valueOf(readNum) > 0) {
                        if (WorkUtils.READ_TYPE_TEACHER.equals(data.getReadOverType())) {
                            WorkStuReadActivity.startActivity(mContext, data.getWorkId(), WorkStuReadActivity.TYPE_FROM_WORK, data.getReadOverType());
                        } else if (WorkUtils.READ_TYPE_STU_EACH_OTHER.equals(data.getReadOverType())) {
                            ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_stu_list_read_by_stu));
                        } else if (WorkUtils.READ_TYPE_STU_SPECIFIED.equals(data.getReadOverType())) {
                            ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_stu_list_read_by_stu_specific));
                        }
                    } else {
                        ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_stu_list_no_person));
                    }
                }
            });

            //点击“统计”
            workStatisticTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkStatisticDetailActivity.startActivity(mContext, data.getWorkId(), data.getReadOverType());
                }
            });


            //点击“布置”
            workArrangeTv.setOnClickListener(new View.OnClickListener() {
                MyDialog mArrangeDialog;

                @Override
                public void onClick(View v) {
                    mArrangeDialog = MyDialog.newInstance(mContext.getResources().getString(R.string.arrange_work_dialog),
                            MyDialog.DIALOG_STYLE_TYPE_0, new MyDialog.OnclickListener() {
                                @Override
                                public void leftClick(MyDialog myDialog) {
                                    mArrangeDialog.dismiss();
                                }

                                @Override
                                public void rightClick(MyDialog myDialog) {
                                    RequestSender requestSender = new RequestSender(getActivity());
                                    requestSender.sendRequest(new RequestSender.RequestData(URLConfig.ARRANGE_WORK + "?uuid=" + UserInfoKeeper.obtainUserInfo().getUuid() + "&workId=" + data.getWorkId(), null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            if ("success".equals(response.optString("result")) && mEmptyTv != null) {
                                                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.arrange_work_success));
                                                loadData(true);
                                            } else {
                                                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.arrange_work_failure));
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(Throwable error) {
                                            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.arrange_work_failure));
                                        }
                                    }));
                                    mArrangeDialog.dismiss();
                                }

                                @Override
                                public void dismiss() {

                                }
                            });
                    mArrangeDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "arrangework");
                }
            });
        }
    }
}

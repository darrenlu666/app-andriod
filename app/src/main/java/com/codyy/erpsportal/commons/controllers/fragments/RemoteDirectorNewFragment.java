package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.RemoteDirectorActivity;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ClassRoomItem;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程导播列表fragment
 * Created by ldh on 2016/8/1.
 */
public class RemoteDirectorNewFragment extends LoadMoreFragment<ClassRoomItem, RemoteDirectorNewFragment.RemoteDirectorHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private UserInfo mUserInfo;
    private String mClassType;//课程类型（专递课堂 直录播课堂）

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassType = getArguments().getString(ClassRoomContants.FROM_WHERE_MODEL);
    }

    @Override
    protected AbsVhrCreator<RemoteDirectorHolder> newViewHolderCreator() {
        return new AbsVhrCreator<RemoteDirectorHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_remote_director;
            }

            @Override
            protected RemoteDirectorHolder doCreate(View view) {
                return new RemoteDirectorHolder(view, getContext());
            }
        };
    }

    @Override
    protected String getUrl() {
        if (mClassType.equals(ClassRoomContants.FROM_WHERE_LINE)) {
            return URLConfig.GET_NEW_ONLINE_DIRECTOR_LIST;
        } else {
            return URLConfig.GET_NEW_SCHOOL_DIRECTOR_LIST;
        }
    }

    @Override
    protected void addParams(Map<String, String> params) {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (mUserInfo != null) {
            params.put("uuid", mUserInfo.getUuid());
        }
    }

    @Override
    protected List<ClassRoomItem> getList(JSONObject response) {
        return ClassRoomItem.parseList(response);
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("classlevelId", TextUtils.isEmpty(params.get("classLevelId")) ? "" : params.get("classLevelId"));
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        loadData(true);
    }

    public class RemoteDirectorHolder extends RecyclerViewHolder<ClassRoomItem> {
        SimpleDraweeView headerImage;
        TextView mTvHolderOne;
        TextView mTvHolderTwo;
        TextView mTvHolderThr;
        TextView mTvTipIn;
        TextView mTvUnstart;
        RelativeLayout mRlTime;
        TextView mTvHour;
        TextView mTvMinite;
        RelativeLayout mRlContainer;
        Context mContext;

        private static final String STATUS_INIT = "INIT";
        private static final String STATUS_PROGRESS = "PROGRESS";

        public RemoteDirectorHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
        }

        @Override
        public void mapFromView(View view) {
            mRlContainer = (RelativeLayout) view.findViewById(R.id.rl_container);
            headerImage = (SimpleDraweeView) view.findViewById(R.id.iv_head);
            mTvHolderOne = (TextView) view.findViewById(R.id.tv_holder_1);
            mTvHolderTwo = (TextView) view.findViewById(R.id.tv_holder_2);
            mTvHolderThr = (TextView) view.findViewById(R.id.tv_holder_3);
            mTvTipIn = (TextView) view.findViewById(R.id.tv_in_remote_living_class);
            mTvUnstart = (TextView) view.findViewById(R.id.tv_school_unstart);
            mRlTime = (RelativeLayout) view.findViewById(R.id.rl_start_time);
            mTvHour = (TextView) view.findViewById(R.id.tv_hour);
            mTvMinite = (TextView) view.findViewById(R.id.tv_minite);
        }

        @Override
        public void setDataToView(final ClassRoomItem data) {
            String[] numArr = getResources().getStringArray(R.array.numbers);
            mTvHolderOne.setText(data.getTeacher());
            mTvHolderTwo.setText(data.getGrade() + "/" + data.getSubject() + "/第" + numArr[Integer.valueOf(data.getSetsuji())] + "节");
            mTvHolderThr.setText(data.getClassRoom());
            ImageFetcher.getInstance(mContext).fetchImage(headerImage, StringUtils.replace(data.getSubjectPic()));
            if (mClassType.equals(ClassRoomContants.FROM_WHERE_LINE)) {
                if (data.getStatus().equals(STATUS_PROGRESS)) {
                    mTvTipIn.setVisibility(View.VISIBLE);
                    mRlTime.setVisibility(View.GONE);
                } else {
                    mTvTipIn.setVisibility(View.GONE);
                    mRlTime.setVisibility(View.VISIBLE);
                    mTvHour.setText(getTimeFromLong(data.getStartTime(), "hour"));
                    mTvMinite.setText(getTimeFromLong(data.getStartTime(), ""));
                }
            } else {
                if (data.getStatus().equals(STATUS_PROGRESS)) {
                    mTvTipIn.setVisibility(View.VISIBLE);
                    mTvUnstart.setVisibility(View.GONE);
                } else {
                    mTvTipIn.setVisibility(View.GONE);
                    mTvUnstart.setVisibility(View.VISIBLE);
                }
            }
            mRlContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                    fetchRemoteDirectorConfig(data.getMeetingId(),
                            data.getSubject(),
                            data.getTeacher(),
                            "PROGRESS".equals(data.getStatus()));
                }
            });
        }

        private String getTimeFromLong(long time, String type) {
            String formatTime = DateUtil.getDateStr(time, DateUtil.DEF_FORMAT);
            String time1 = formatTime.split(" ")[1];
            String returnTime;
            if (type.equals("hour")) {
                returnTime = time1.split(":")[0];
            } else {
                returnTime = time1.split(":")[1];
            }
            return returnTime;
        }
    }

    private RequestSender mRequestSender;

    /**
     * 获取远程导播配置
     *
     * @param mid 视频会话id
     */
    private void fetchRemoteDirectorConfig(String mid, final String subject, final String teacher, final boolean isProgressing) {
        mRequestSender = new RequestSender(getContext());
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        param.put("mid", mid);
        if (!mClassType.equals(ClassRoomContants.FROM_WHERE_LINE)) {
            param.put("meetType", "LIVE");
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.REMOTE_DIRECTOR_CONFIG, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String result = jsonObject.optString("result");
                if ("success".equals(result)) {
                    RemoteDirectorConfig config = RemoteDirectorConfig.parse(jsonObject.optJSONObject("data"));
                    dismissDialog();
                    if (TextUtils.isEmpty(config.getPmsRemoteUrl())) {
                        UIUtils.toast(getContext(), "无法获取直播服务地址！", Toast.LENGTH_SHORT);
                    } else {
                        RemoteDirectorActivity.start(getActivity(), config, subject, teacher, isProgressing);
                    }
                } else if ("error".equals(result)) {
                    dismissDialog();
                    UIUtils.toast(getContext(), "抱歉，无法连接远程导播！", Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(Throwable volleyError) {
                dismissDialog();
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }));
    }

    private AppCompatDialog mLoadingDialog;

    private void showDialog() {
        createDialog();
        mLoadingDialog.show();
    }

    private void dismissDialog() {
        mLoadingDialog.dismiss();
    }

    private void createDialog() {
        if (mLoadingDialog == null) {
            Cog.d("createDialog", "createDialog");
            mLoadingDialog = new AppCompatDialog(getContext(), R.style.NoTitleDialogStyle);
            mLoadingDialog.setTitle("请稍等…");
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.remote_loading_layout, null);
            mLoadingDialog.setContentView(view);
            ProgressBar loadingProgressBar = (ProgressBar) view.findViewById(R.id.loadingProgressBar);
            Drawable drawable = getResources().getDrawable(R.drawable.progress_loading);
            loadingProgressBar.setIndeterminateDrawable(drawable);
        }

    }

}

package com.codyy.erpsportal.statistics.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.statistics.controllers.activities.CoursesProportionTableActivity;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.erpsportal.statistics.models.entities.CoursesProportion;
import com.codyy.erpsportal.statistics.widgets.ProportionBar;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 按月统计碎片
 */
public class MonthlyStatFragment extends Fragment implements OnItemClickListener {

    private final static String TAG = "MonthlyStatFragment";

    public MonthlyStatFragment() { }

    private View mRootView;

    /**
     * 地区范围列表，左侧的列表
     */
    private ListView mScopesLv;

    private ObjectsAdapter<AreaInfo, AreaViewHolder> mScopesAdapter;

    private int mCurrCheckedPos = 0;

    private RequestSender mRequestSender;

    private UserInfo mUserInfo;

    private int mType;

    private LoadingDialog mLoadingDialog;

    /**
     * 地区统计的数据列表，右侧的列表
     */
    private ListView mMonthlyProportionLv;

    private ObjectsAdapter<CoursesProportion, CoursesProportionViewHolder> mMonthlyProportionAdapter;

    public static MonthlyStatFragment newInstance(UserInfo userInfo, int type) {
        MonthlyStatFragment monthlyStatFragment = new MonthlyStatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extra.USER_INFO, userInfo);
        bundle.putInt(CoursesProportionTableActivity.EXTRA_TYPE, type);
        monthlyStatFragment.setArguments(bundle);
        return monthlyStatFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(this);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
            mType = getArguments().getInt(CoursesProportionTableActivity.EXTRA_TYPE);
        }
        mLoadingDialog = LoadingDialog.newInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_monthly_stat, container, false);
            mScopesLv = (ListView) mRootView.findViewById(R.id.lv_statistical_scope);
            mScopesLv.setOnItemClickListener(this);
            mMonthlyProportionLv = (ListView) mRootView.findViewById(R.id.lv_statistical_proportion);
            mScopesAdapter = new ObjectsAdapter<>(getContext(), AreaViewHolder.class);
            mScopesLv.setAdapter(mScopesAdapter);

            mMonthlyProportionAdapter = new ObjectsAdapter<>(getContext(), CoursesProportionViewHolder.class);
            mMonthlyProportionLv.setAdapter(mMonthlyProportionAdapter);
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        List<CoursesProportion> proportionList = new ArrayList<>();
//        proportionList.add(new CoursesProportion("2015年1月", 100));
//        proportionList.add(new CoursesProportion("2月", 70));
//        proportionList.add(new CoursesProportion("3月", 60));
//        proportionList.add(new CoursesProportion("4月", 80));
//        proportionList.add(new CoursesProportion("5月", 90));
//        proportionList.add(new CoursesProportion("6月", 10));
//        proportionList.add(new CoursesProportion("7月", 50));
//        proportionList.add(new CoursesProportion("8月", 80));
//        proportionList.add(new CoursesProportion("9月", 90));
//        proportionList.add(new CoursesProportion("10月", 60.00f));
//        proportionList.add(new CoursesProportion("11月", 80));
//        proportionList.add(new CoursesProportion("12月", 90));
//        proportionList.add(new CoursesProportion("2016年1月", 100));
//        proportionList.add(new CoursesProportion("2月", 98.76f));
//        proportionList.add(new CoursesProportion("3月", 43));
//        proportionList.add(new CoursesProportion("4月", 12));
//        proportionList.add(new CoursesProportion("5月", 55));
//        proportionList.add(new CoursesProportion("6月", 34.8f));
//        mMonthlyProportionAdapter.addData(proportionList);
    }

    /**
     * 设置左侧地区列表
     * @param areaInfoList
     */
    public void setScopes(List<AreaInfo> areaInfoList) {
        mScopesAdapter.setData(areaInfoList);
        mScopesAdapter.notifyDataSetChanged();
        mScopesLv.setItemChecked(0, true);
        AreaInfo areaInfo = mScopesAdapter.getItem(0);
        loadProportionData(areaInfo);
    }

    private void loadProportionData(AreaInfo areaInfo) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (areaInfo.isSchool()) {
            params.put("type", "SCHOOL");
        } else if (areaInfo.isArea()) {
            params.put("type", "AREA");
        } else {
            params.put("type", "CLASSROOM");
        }
        params.put("id", areaInfo.getId());
        if (mType == CoursesProportionTableActivity.TYPE_PROPORTION_MAIN) {
            params.put("statisticType", "MASTER");
        } else if (mType == CoursesProportionTableActivity.TYPE_PROPORTION_MAIN_INVITED) {
            params.put("statisticType", "INVITE");
        } else {
            params.put("statisticType", "RECEIVE");
        }
        mLoadingDialog.show(getFragmentManager(), "loading_dialog_monthly_stat");
        RequestData requestData = new RequestData(URLConfig.COURSES_PROPORTION_STAT_MONTH, params
                , new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "setScopes response=", response);
                if (mRootView == null) return;
                mLoadingDialog.dismiss();
                if (response.optBoolean("result")) {
                    JSONArray dataJa = response.optJSONArray("data");
                    if (dataJa != null) {
                        List<CoursesProportion> coursesProportionList = CoursesProportion.PARSER.parseArray(dataJa);
                        mMonthlyProportionAdapter.setData(coursesProportionList);
                        mMonthlyProportionAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "setScopes error=", error);
                mLoadingDialog.dismiss();
            }
        });
        requestData.setTimeout(60000);
        mRequestSender.sendGetRequest( requestData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Cog.d(TAG, "onDestroy called");
        mRequestSender.stop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCurrCheckedPos != position) {
            mCurrCheckedPos = position;
            AreaInfo scope = mScopesAdapter.getItem(position);
            loadProportionData(scope);
        }
    }

    public static class AreaViewHolder extends AbsViewHolder<AreaInfo> {

        private TextView mScopeTv;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_statistical_scope;
        }

        @Override
        public void mapFromView(View view) {
            mScopeTv = (TextView) view.findViewById(R.id.tv_scope_name);
        }

        @Override
        public void setDataToView(AreaInfo data, Context context) {
            mScopeTv.setText(data.getName());
        }
    }

    public static class CoursesProportionViewHolder extends AbsViewHolder<CoursesProportion> {

        private TextView dateTv;

        private ProportionBar proportionBar;

        private FrameLayout verticalLineFl;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_courses_proportion;
        }

        @Override
        public void mapFromView(View view) {
            dateTv = (TextView) view.findViewById(R.id.tv_date);
            proportionBar = (ProportionBar) view.findViewById(R.id.pb_courses);
            verticalLineFl = (FrameLayout) view.findViewById(R.id.fl_vertical_line);
        }

        @Override
        public void setDataToView(CoursesProportion data, Context context) { }

        @Override
        public void setDataToView(List<CoursesProportion> objects, int position, Context context) {
            CoursesProportion coursesProportion = objects.get(position);
            if (position > 0 && coursesProportion.getYear()
                    .equals(objects.get(position - 1).getYear())) {//如果有上一个，且年份和上一条相同，省略年份
                dateTv.setText(coursesProportion.getMonth() + "月");
            } else {
                dateTv.setText(coursesProportion.getYear() + "年" + coursesProportion.getMonth() + "月");
            }
            proportionBar.setPercent(coursesProportion.getRatio());
            if (position == objects.size() - 1) {
                verticalLineFl.setVisibility(View.INVISIBLE);
            } else {
                verticalLineFl.setVisibility(View.VISIBLE);
            }
        }
    }
}

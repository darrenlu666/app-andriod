package com.codyy.erpsportal.commons.controllers.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.Map;


public class CollectivePrepareLessonsFilterFragment extends Fragment {

    /**
     * 年级
     */
    private static final int OPTION_CLASS_LEVEL = 0x001;

    /**
     * 学科
     */
    private static final int OPTION_SUBJECT = 0x002;

    private final static String TAG = "CollectivePrepareLessonsFilterFragment";
    /**
     * 右侧筛选项目内容
     */
    private ListView mOptionsLv;

    /**
     * 左侧帅选项目内容
     */
    private ListView mChoiceLv;
    /**
     * 网络请求
     */
    private RequestSender mSender;

    public static CollectivePrepareLessonsFilterFragment newInstance() {
        CollectivePrepareLessonsFilterFragment fragment = new CollectivePrepareLessonsFilterFragment();
        return fragment;
    }

    public CollectivePrepareLessonsFilterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSender = new RequestSender(getActivity());
        getGrade();
    }

    /**
     * 获取年级
     */
    private void getGrade() {
        httpConnect(URLConfig.GET_GRADE_UNLOGIN, null, OPTION_CLASS_LEVEL);
    }

    private int mLastOptionPos = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource_filter, container, false);
        mOptionsLv = (ListView) view.findViewById(R.id.first);
        mChoiceLv = (ListView) view.findViewById(R.id.second);
        return view;
    }

    /**
     * 网络请求
     *
     * @param url
     * @param data
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Cog.i(TAG, response.toString());
                switch (msg) {
                    case OPTION_CLASS_LEVEL:

                        break;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(Throwable error) {
                ToastUtil.showToast(getActivity(), getActivity().getResources().getString(R.string.net_error));
            }
        }));
    }

    class grade {
        /**
         * className : 二年级
         * classLevelId : 2df3f82ed1ce47b5819f955acaaab855
         */
        private String className;
        private String classLevelId;

        public void setClassName(String className) {
            this.className = className;
        }

        public void setClassLevelId(String classLevelId) {
            this.classLevelId = classLevelId;
        }

        public String getClassName() {
            return className;
        }

        public String getClassLevelId() {
            return classLevelId;
        }
    }

}

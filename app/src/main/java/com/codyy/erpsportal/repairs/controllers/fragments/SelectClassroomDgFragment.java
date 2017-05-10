package com.codyy.erpsportal.repairs.controllers.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.repairs.controllers.adapters.ClassroomsSelectingRvAdapter;
import com.codyy.erpsportal.repairs.controllers.adapters.ClassroomsSelectingRvAdapter.OnItemClickListener;
import com.codyy.erpsportal.repairs.models.entities.ClassroomSelectItem;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 选择报修教室
 * Created by gujiajia on 2017/4/14.
 */

public class SelectClassroomDgFragment extends DialogFragment {

    private final static String TAG = "SelectClassroomDgFragment";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.iv_close)
    ImageView mCloseIv;

    @Bind(R.id.tv_selected_name)
    TextView mSelectedNameTv;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    private UserInfo mUserInfo;

    private WebApi mWebApi;

    private ClassroomsSelectingRvAdapter mRvAdapter;

    private List<ClassroomSelectItem> mClassroomSelectItems;

    private ClassroomSelectItem mSelectedClassroom;

    /**
     * 选中监听器
     */
    private OnClassroomSelectedListener mListener;

    public static SelectClassroomDgFragment newInstance(UserInfo userInfo) {
        SelectClassroomDgFragment selectClassroomDgFragment = new SelectClassroomDgFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extra.USER_INFO, userInfo);
        selectClassroomDgFragment.setArguments(bundle);
        return selectClassroomDgFragment;
    }

    public void setClassroomSelectedListener(OnClassroomSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
        mWebApi = RsGenerator.create(WebApi.class);
        setStyle(STYLE_NO_FRAME, R.style.BottomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dg_select_classroom, container, false);
        ButterKnife.bind(this, view);
        mRvAdapter = new ClassroomsSelectingRvAdapter();
        mListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mListRv.setAdapter( mRvAdapter);
        mRvAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ClassroomSelectItem item, int position) {
                if (mListener != null) mListener.onClassroomSelected(item);
                dismiss();
            }
        });
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Map<String, String> params = new HashMap<>();
        params.put("schoolId", mUserInfo.getSchoolId());
        params.put("uuid", mUserInfo.getUuid());
        mWebApi.post4Json(URLConfig.GET_CLASSROOMS, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if ("success".equals(jsonObject.optString("result"))) {
                            Type type = new TypeToken<List<ClassroomSelectItem>>(){}.getType();
                            mClassroomSelectItems = new Gson().fromJson(
                                    jsonObject.optString("data"), type);
                            mRvAdapter.setClassroomSelectItems(mClassroomSelectItems);
                            if (mSelectedClassroom != null) {
                                int index = mRvAdapter.setSelectedItem( mSelectedClassroom);
                                if (index != -1)
                                    mListRv.scrollToPosition(index);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "accept error=", throwable);
                    }
                });
    }

    @OnClick(R.id.iv_close)
    public void onCloseClick() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setSelected(ClassroomSelectItem selectedClassroom) {
        mSelectedClassroom = selectedClassroom;
    }

    public interface OnClassroomSelectedListener {
        void onClassroomSelected(ClassroomSelectItem item);
    }
}

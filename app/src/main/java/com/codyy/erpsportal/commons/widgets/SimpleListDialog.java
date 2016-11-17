package com.codyy.erpsportal.commons.widgets;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.groups.controllers.viewholders.SingleTextViewHolder;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;

import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个列表顺序选择Dialog
 * --------------------------------
 * |    请选择 xxxx                |
 * --------------------------------
 * |     选项一                    |
 * --------------------------------
 * |     选项二                    |
 * --------------------------------
 * Created by poe on 16-2-29.
 */
public class SimpleListDialog extends DialogFragment {

    private static final String TAG = SimpleListDialog.class.getSimpleName();
    public static final String ARG_EXTRA_TITLE = "title";
    public static final String ARG_EXTRA_CONTENT = "content";
    private View mRootView;
    @Bind(R.id.tv_dialog_content)TextView mTextView;
    @Bind(R.id.recycler_view)RecyclerView mRecyclerView ;
    private OnItemClickListener mOnItemClickListener;
    private String mTitle = "";
    private BaseRecyclerAdapter<String,SingleTextViewHolder> mAdapter ;
    private List<String> mData;

    /**
     * 获取实例 ，不需要点击
     * @param title
     * @return
     */
    public static SimpleListDialog newInstance(String title , ArrayList<String> data ){
        SimpleListDialog dialog = new SimpleListDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_EXTRA_TITLE, title);
        bd.putStringArrayList(ARG_EXTRA_CONTENT, data);
        dialog.setArguments(bd);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            mTitle = getArguments().getString(ARG_EXTRA_TITLE);
            mData   =   getArguments().getStringArrayList(ARG_EXTRA_CONTENT);
        }
        //禁止点击外面取消
        this.setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //no title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(null == mRootView || mTextView == null){
            mRootView = inflater.inflate(R.layout.dialog_single_recycler_view, container, false);
            ButterKnife.bind(this,mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do something . data set .
        mTextView.setText(mTitle);
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));

        mAdapter    =  new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<SingleTextViewHolder>() {
            @Override
            public SingleTextViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new SingleTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_simple_text,parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClicked(View v, int position, String data) {
                if(null != mOnItemClickListener){
                    if(null != data && null != mData){
                        mOnItemClickListener.onClick(position);
                    }
                }
                dismiss();
            }
        });
        mAdapter.setData(mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showAllowStateLoss(FragmentManager fragmentManager , String TAG){
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction() ;
        ft.add(this ,TAG);
        ft.commitAllowingStateLoss();
    }


    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface  OnItemClickListener{
        /**
         * 点击事件
         * @param pos
         */
        void onClick(int pos);
    }
}

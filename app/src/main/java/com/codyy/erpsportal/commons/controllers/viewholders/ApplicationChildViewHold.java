package com.codyy.erpsportal.commons.controllers.viewholders;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.FunctionFragment;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.erpsportal.commons.models.Jumpable;
import com.codyy.erpsportal.commons.models.entities.AppInfo;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 应用-多孩子item
 * Created by poe on 16-6-6.
 */
public class ApplicationChildViewHold extends BaseRecyclerViewHolder<AppInfo>{

    public static final String TAG = "ApplicationChildViewHold";
    @Bind(R.id.lin_container)LinearLayout mContainerLinearLayout;
    private IChildItemClickListener mChildItemClickListener ;

    public ApplicationChildViewHold(View itemView,IChildItemClickListener childItemClickListener ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.mChildItemClickListener = childItemClickListener;
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_function_child_container;
    }
    
    @Override
    public void setData(int position, AppInfo data) {
        mCurrentPosition    =   position;
        mData   =   data ;
        //set new data struct ... !
        if(data != null && data.getChildGroups() !=null){
            refreshView(data.getChildGroups());
        }
    }

    private View.OnClickListener mCurrentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(null == v.getTag()) return;
            AppInfo app = (AppInfo) v.getTag();
            if(null != app ){
                if(app.getJumpable() != null){
                    AppConfig.jumpToActivity(app.getJumpable(), mContainerLinearLayout.getContext());
                }else{
                    if(null != mChildItemClickListener) mChildItemClickListener.onClick(v,app);
                }
            }
        }
    };

    /**
     * 刷新所有的ｖｉｅｗ
     *
     * @param childGroups
     */
    private void refreshView(List<AppInfo> childGroups) {
        Cog.d(TAG,"RefreshView(List<AppInfo> childGroups)...");
        int count = childGroups.size();
        float item_width = (EApplication.instance().getResources().getDisplayMetrics().widthPixels*1.0f -UIUtils.dip2px(EApplication.instance(),20))/ FunctionFragment.ITEM_COUNT_CHILD;
        //remove the old views
        mContainerLinearLayout.clearAnimation();
        mContainerLinearLayout.removeAllViews();
        mContainerLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //解决４．２．２中无法自己计算ＷｒａｐＣｏｎｔｅｎｔ的问你提.
        if(Build.VERSION.SDK_INT<19){
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(EApplication.instance().getResources().getDisplayMetrics().widthPixels, UIUtils.dip2px(EApplication.instance(),100)*((count - 1) / FunctionFragment.ITEM_COUNT_CHILD + 1));
            mContainerLinearLayout.setLayoutParams(param);
        }
        //add lineLayout，增加子项，每排四项。
        for (int i = 0; i < ((count - 1) / FunctionFragment.ITEM_COUNT_CHILD + 1); i++) {

            LinearLayout line = new LinearLayout(mContainerLinearLayout.getContext());
            line.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int dCount = FunctionFragment.ITEM_COUNT_CHILD;//every line child items count
            if (count - (i * FunctionFragment.ITEM_COUNT_CHILD) < FunctionFragment.ITEM_COUNT_CHILD) {
                dCount = count - FunctionFragment.ITEM_COUNT_CHILD * i;
            }

            LayoutInflater lin = LayoutInflater.from(mContainerLinearLayout.getContext());

            for (int j = 0; j < dCount; j++) {
                int index = FunctionFragment.ITEM_COUNT_CHILD * i + j;
                AppInfo app = childGroups.get(index);
                View child = lin.inflate(R.layout.item_frag_function_child, null);
                child.setTag(app);
                child.setId(index);
                child.setOnClickListener(mCurrentClickListener);
                FunctionChildViewHolder fv = new FunctionChildViewHolder(child);
                fv.setDataToView(app, mContainerLinearLayout.getContext());
                LinearLayout.LayoutParams paramsChild = new LinearLayout.LayoutParams((int)item_width, LinearLayout.LayoutParams.WRAP_CONTENT);
                if(dCount == FunctionFragment.ITEM_COUNT_CHILD){
                    paramsChild = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsChild.weight = 1.0f;
                }
                line.addView(child, paramsChild);
            }
            mContainerLinearLayout.addView(line, params);
        }
    }

    /**
     * 点击事件传递到Activity接口.
     */
    public interface IChildItemClickListener{
        /**
         * 孩子item点击事件.
         * @param app
         */
        void onClick(View v ,AppInfo app);
    }
}

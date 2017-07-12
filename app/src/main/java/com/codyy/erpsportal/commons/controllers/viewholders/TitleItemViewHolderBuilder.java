package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;

/**
 * build for {@link TitleItemViewHolder}
 * Created by poe on 16-6-1.
 */

public class TitleItemViewHolderBuilder {
    /**
     * 简单的数据提示 “很抱歉，暂无数据”
     */
    public static final int ITEM_TIPS_SIMPLE_TEXT = 0x01;
    /**
     * v5.3.0新样式 ！
     */
    private static final int ITEM_TIPS_WITH_EP_ICON = 0x02;


   private static TitleItemViewHolderBuilder instance ;

    public static TitleItemViewHolderBuilder getInstance(){
        if(null == instance){
            instance = new TitleItemViewHolderBuilder();
        }
        return instance ;
    }

    public BaseRecyclerViewHolder constructTitleItem(Context context , ViewGroup parent, int type ){
        BaseRecyclerViewHolder viewHolder = null;
        if(ITEM_TIPS_SIMPLE_TEXT == type){
            viewHolder = new TitleItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_title_bar,parent,false));
        }else if(ITEM_TIPS_WITH_EP_ICON == type){
            viewHolder = new TitleItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_title_bar_ep,parent,false));
        }
        return viewHolder;
    }
}

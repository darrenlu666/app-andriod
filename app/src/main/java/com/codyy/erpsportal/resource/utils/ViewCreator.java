package com.codyy.erpsportal.resource.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.widget.RadioButton;

import com.codyy.erpsportal.R;

/**
 * 创建View工具类
 * Created by gujiajia on 2016/12/19.
 */

public class ViewCreator {
    /**
     * 创建筛选项
     *
     * @return 筛选项RadioButton
     */
    public static RadioButton createRadioButton(Context context, int padding) {
        RadioButton radioButton = new RadioButton(context);
        radioButton.setButtonDrawable(new StateListDrawable());
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        radioButton.setTypeface(Typeface.DEFAULT);
        radioButton.setTextColor(ResourcesCompat.getColorStateList(context.getResources(), R.color.sl_rb_filter_item, null));
//        radioButton.setBackgroundDrawable(DrawableUtils.obtainAttrDrawable(context, R.attr.selectableItemBackground));
        ViewCompat.setBackground(radioButton, DrawableUtils.obtainAttrDrawable(context, R.attr.selectableItemBackground));
        radioButton.setPadding(padding, padding, padding, padding);
        return radioButton;
    }
}

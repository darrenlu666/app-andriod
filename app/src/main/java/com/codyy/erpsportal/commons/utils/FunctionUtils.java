package com.codyy.erpsportal.commons.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.codyy.erpsportal.R;

/**
 * Created by poe on 15-7-31.
 */
public class FunctionUtils {

    /**
     * 显示动画
     * @param view
     */
    public static void startEnterAnimation(Context mContext , LinearLayout view){
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_function_app);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(1.5f);

        if(null != view) {
//            view.startAnimation(animation);
            view.clearAnimation();
            view.setLayoutAnimation(controller);
        }
    }

    /**
     * 隐藏动画
     * @param view
     */
    public static void hideAnimation(Context mContext ,View view){
        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.anim_function_app);
        if(null != view){
            view.startAnimation(animation);
        }
    }

}

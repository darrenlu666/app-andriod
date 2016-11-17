package com.codyy.erpsportal.commons.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.BNLiveControlView;
import com.codyy.erpsportal.commons.widgets.BNMultipleLiveControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;


/**
 * 视频会议动画类
 * Created by poe on 15-9-11.
 */
public class UiOnlineMeetingAnimationUtils {

    private static  final String TAG = UiOnlineMeetingAnimationUtils.class.getSimpleName();

    /**
     * 全屏
     * @param index  0:主讲人全屏  1:发言人全屏
     * @param mainBV  主讲人视频
     * @param partBV  参会人视图
     * @param mainCV  主讲人控制器
     * @param partCV  参会者控制器
     * @param partnerLinearLayout 参会人+我的视频 容器视图
     * @param myVideoRlt    我的视频视图
     * @param partnerRlt   参会者视图
     */
    public static void ExpandVideoView(int index, BnVideoLayout2 mainBV, SurfaceView partBV , BNLiveControlView mainCV, BNMultipleLiveControlView partCV
                , LinearLayout partnerLinearLayout , RelativeLayout myVideoRlt , RelativeLayout partnerRlt){

        int screenWidth = EApplication.instance().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = EApplication.instance().getResources().getDisplayMetrics().heightPixels;
        Cog.i(TAG," sw:"+screenWidth +" sh:"+screenHeight);
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(1, 1);
        if(index == 0){
            //最小化1x1(px) 放在左上角
//            partBV.setLayoutParams(param2);
//            partCV.setLayoutParams(param2);
            partnerLinearLayout.setLayoutParams(param2);
            //主讲人全屏 ,参与者最小化
            mainBV.setLayoutParams(param1);
            mainCV.setLayoutParams(param1);
        }else{
            //最小化1x1(px)
            mainBV.setLayoutParams(param2);
            mainCV.setLayoutParams(param2);
            //参会者全屏,主讲人最小化
            /*partBV.setLayoutParams(param1);
            partCV.setLayoutParams(param1);*/
            partnerLinearLayout.setLayoutParams(param1);
            myVideoRlt.setLayoutParams(new LinearLayout.LayoutParams(1,1));
        }
    }

    /**
     * 视频全拼回到正常比例 .
     * @param mainBV
     * @param partBV
     * @param mainCV
     * @param partCV
     */
    public static void restoreVideoView(BnVideoLayout2 mainBV, SurfaceView partBV , BNLiveControlView mainCV, BNMultipleLiveControlView partCV
            , LinearLayout partnerLinearLayout , RelativeLayout myVideoRlt , RelativeLayout partnerRlt){
        //主讲
        set16vs9Height(mainBV , mainCV);
        //我的视频+发言人视频
        int height = UIUtils.dip2px(EApplication.instance(), 135);
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        param1.addRule(RelativeLayout.BELOW,R.id.bv_main_online_interact_video);
        param1.setMargins(0,1,0,0);
        partnerLinearLayout.setLayoutParams(param1);
        //我的视频
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params2.weight = 1;
        params2.setMargins(0,0,1,0);
        myVideoRlt.setLayoutParams(params2);
        //发言人的视频
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params3.weight = 1;
        params3.setMargins(1,0,0,0);
        partnerRlt.setLayoutParams(params3);
    }

    /**
     * 根据屏幕的宽度设置16:9的高度。
     * @param mainBV video  view
     * @param mainCV  control view
     * @return
     */
    public static void set16vs9Height(BnVideoLayout2 mainBV  , BNLiveControlView mainCV) {
        int screenWidth = EApplication.instance().getResources().getDisplayMetrics().widthPixels;
        int mainHeight  = screenWidth*9/16;
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mainHeight);
        mainBV.setLayoutParams(param);

        RelativeLayout.LayoutParams paramConrol = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, mainHeight);
        paramConrol.addRule(RelativeLayout.ALIGN_RIGHT,R.id.bv_main_online_interact_video);
        paramConrol.addRule(RelativeLayout.ALIGN_TOP,R.id.bv_main_online_interact_video);
        mainCV.setLayoutParams(paramConrol);
    }

    /**
     * arrow 移动到制定的位置
     * @param view
     * @param startX
     * @param toX
     */
    public static void moveArrowFlag(View view, int startX, int toX){
        Cog.d(TAG, "startX " + startX + " toX " + toX);
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        ValueAnimator animator = createMarginAnimator(view, startX, toX);
        animator.start();
    }


    public static void 	moveAnim(final View v,final int startX,final int toX, int startY, int toY, long during) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(during);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }
    /**
     *
     * @param view
     */
    public static void animateExpanding(final View view ,@NonNull final OnAnimationListener listener) {
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        ValueAnimator animator = createHeightAnimator(view, 0, view.getMeasuredHeight());
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                listener.onBegin();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                listener.onEnd();
            }
        });
        animator.start();
    }


    /**
     * 检测应用的弹进/弹出动画 结束监听
     */
    public interface OnAnimationListener{
        /**
         * 开始动画
         */
        void onBegin();
        /**
         * 结束动画 .
         */
        void onEnd();
    }
    /**
     * 闭合动画
     * @param view
     * @param arrowView
     */
    public static void animateCollapsing(final View view, final View arrowView ,@NonNull final OnAnimationListener listener) {
        int origHeight = view.getHeight();

        ValueAnimator animator = createHeightAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                listener.onBegin();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                listener.onEnd();
                view.setVisibility(View.GONE);
                if (null != arrowView) {
                    arrowView.clearAnimation();
                    arrowView.setVisibility(View.GONE);
                }
            }
        });
        animator.start();
    }

    public static ValueAnimator createHeightAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static ValueAnimator createMarginAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(value, 0, 0, dp2px(3));
//                params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.lin_container);
//                view.setLayoutParams(params);
                view.setTranslationX(value);
            }
        });
        return animator;
    }

    /**
     * dp to px cell .
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue){

        float scale = EApplication.instance().getResources().getDisplayMetrics().density;
        return  (int)(dpValue*scale+0.5);
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}

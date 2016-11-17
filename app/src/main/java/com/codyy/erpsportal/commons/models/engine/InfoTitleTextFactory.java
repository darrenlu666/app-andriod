package com.codyy.erpsportal.commons.models.engine;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.codyy.erpsportal.R;

import java.lang.ref.WeakReference;

/**
 * 资讯切换组件的子组件工厂
 * 自动切换的资讯内容TextView工厂
 * Created by gujiajia on 2016/10/12.
 */

public class InfoTitleTextFactory implements ViewSwitcher.ViewFactory {

    private WeakReference<Context> mContextRef;

    public InfoTitleTextFactory(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    public View makeView() {
        if (mContextRef == null) return null;
        Context context = mContextRef.get();
        if (context == null) return null;
        TextView infoTv = new TextView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        infoTv.setLayoutParams( layoutParams);
        infoTv.setGravity(Gravity.CENTER_VERTICAL);
        infoTv.setTextColor(0xff777777);
        infoTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.info_slide));
        infoTv.setSingleLine(true);
        infoTv.setEllipsize(TruncateAt.END);
        return infoTv;
    }
}

package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.codyy.erpsportal.R;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

public class AvatarView extends SimpleDraweeView {
    private boolean mGroup =false;

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        builder.setPlaceholderImage(getResources().getDrawable(R.drawable.ic_default_avatar));
        builder.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        GenericDraweeHierarchy hierarchy = builder.build();
        setHierarchy(hierarchy);
    }


    public void setGroup(boolean group) {
        if(mGroup == group)
            return;
        mGroup = group;
        if (group) {
            getHierarchy().setPlaceholderImage(getResources().getDrawable(R.drawable.ic_default_avatar_group));
        } else {
            getHierarchy().setPlaceholderImage(getResources().getDrawable(R.drawable.ic_default_avatar));
        }
    }

    public void setAvatarUrl(String url) {
        if(TextUtils.isEmpty(url)) return;
        setImageURI(Uri.parse(url));
    }
}

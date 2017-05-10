package com.codyy.erpsportal.commons.models.engine;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainBlogPost;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Linker;
import com.codyy.erpsportal.groups.controllers.activities.BlogPostDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;

/**
 * 首页博文项填入者
 * Created by gujiajia on 2016/8/19.
 */
@ItemLayoutId(R.layout.item_channel_blog_post)
public class BlogPostViewStuffer implements ViewStuffer<MainBlogPost>{

    private final static String TAG = "BlogPostViewStuffer";

    private WeakReference<Activity> mActivityRef;

    public BlogPostViewStuffer(Activity activity) {
        this.mActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void onStuffView(View view,final MainBlogPost item) {
        SimpleDraweeView iconDv = (SimpleDraweeView) view.findViewById(R.id.sdv_pic);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_name);
        TextView contentTv = (TextView) view.findViewById(R.id.et_desc);
        ImageFetcher.getInstance(view).fetchSmall(iconDv, item.getHeadPic());
        titleTv.setText( item.getBlogTitle());
        contentTv.setText( item.getBlogTextContent());
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = mActivityRef.get();
                if (activity == null) return;
                BlogPostDetailActivity.start(activity , item.getBlogId(),
                        BlogPostDetailActivity.FROM_TYPE_SHARE);
            }
        });
        iconDv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.d(TAG, "onBlogCreatorClick blogPost=", item);
                Linker.linkUserIcon((Activity) v.getContext(), item.getBaseUserId());
            }
        });
    }
}

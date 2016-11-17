package com.codyy.erpsportal.commons.controllers.viewholders;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.FunctionFragment;
import com.codyy.erpsportal.commons.models.entities.AppInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 应用-item
 * Created by poe on 16-6-6.
 */

public class ApplicationViewHold extends BaseRecyclerViewHolder<AppInfo> {
    /**
     * 一级：单独的item
     */
    public static final int ITEM_TYPE_SINGLE = 0x01;
    /**
     * 一级：单独的item 空位填充
     */
    public static final int ITEM_TYPE_SINGLE_EMPTY = 0x09;
    /**
     * 一级：拥有子选项的item
     */
    public static final int ITEM_TYPE_MULTI =  0x10;
    /**
     * 二级：子选项item的ｔｙｐｅ
     */
    public static final int ITEM_TYPE_CHILD = 0x110;

    @Bind(R.id.image)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.img_open_flag)ImageView mFlagImageView;

    public ApplicationViewHold(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_function_child;
    }

    @Override
    public void setData(int position, AppInfo data) {
        mCurrentPosition    =   position;
        mData   =   data ;
        if(getItemViewType()==ApplicationViewHold.ITEM_TYPE_SINGLE_EMPTY){
            mSimpleDraweeView.setVisibility(View.INVISIBLE);
            mTitleTextView.setVisibility(View.INVISIBLE);
            mFlagImageView.setVisibility(View.INVISIBLE);
        }else{
            
            mTitleTextView.setText(data.getAppName());
            if(FunctionFragment.sCurrentPosition == position && FunctionFragment.sCurrentPosition > -1){
                mFlagImageView.setVisibility(View.VISIBLE);
            }else{
                mFlagImageView.setVisibility(View.INVISIBLE);
            }

            // 失败后加载默认图片
            ControllerListener controllerListener = new ControllerListener<ImageInfo>() {
                @Override
                public void onSubmit(String id, Object callerContext) {

                }

                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                }

                @Override
                public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {

                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    mSimpleDraweeView.setImageResource(mData.getIcon());
                }

                @Override
                public void onRelease(String id) {

                }
            };
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setControllerListener(controllerListener)
                    .setUri(data.getHeadPic())
                    // other setters
                    .build();
            mSimpleDraweeView.setController(controller);
        }
    }
}

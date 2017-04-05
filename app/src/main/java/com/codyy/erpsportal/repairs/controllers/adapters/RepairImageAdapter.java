package com.codyy.erpsportal.repairs.controllers.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ContextUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.activities.media.MMSelectorActivity;
import com.codyy.erpsportal.exam.controllers.activities.media.image.MMImageBean;
import com.codyy.erpsportal.exam.controllers.activities.media.video.MMVideoAlbumFragment;
import com.codyy.erpsportal.homework.models.entities.student.ImageDetail;
import com.codyy.erpsportal.repairs.controllers.viewholders.AddImageVh;
import com.codyy.erpsportal.repairs.controllers.viewholders.RepairImageVh;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片适配器，后面有添加图片按钮
 * Created by gujiajia on 2017/3/30.
 */

public class RepairImageAdapter extends Adapter {

    private final static String TAG = "RepairImageAdapter";

    private final static int TYPE_ADD_IMAGE = 0;

    private final static int TYPE_IMAGE = 1;

    public static final int REQUEST_CODE_ADD_IMAGES = 1;

    private static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";

    private final SparseArray<ViewHolderCreator> mVhCreators = new SparseArray<>(2);

    private List<ImageDetail> mItems;

    private OnAddImageClickListener mAddImageClickListener = new OnAddImageClickListener() {
        @Override
        public void onAddImageClick(View view, int count) {
            Activity activity = ContextUtils.getActivity(view);
            Intent intent = new Intent(activity, MMSelectorActivity.class);
            intent.putExtra("EXTRA_TYPE", "IMAGE");
            intent.putExtra("EXTRA_SIZE", count);
            activity.startActivityForResult(intent, REQUEST_CODE_ADD_IMAGES);
            UIUtils.addEnterAnim(activity);
        }
    };

    /**
     * 最多显示多少张图片
     */
    private int mMaxCount = 9;

    public RepairImageAdapter(int maxCount) {
        mItems = new ArrayList<>();
        mMaxCount = maxCount;
        mVhCreators.put(TYPE_ADD_IMAGE, new EasyVhrCreator(AddImageVh.class));
        mVhCreators.put(TYPE_IMAGE, new EasyVhrCreator(RepairImageVh.class));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mVhCreators.get(viewType).createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof AddImageVh) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onAddImageClick");
                    mAddImageClickListener.onAddImageClick(v, mMaxCount - holder.getAdapterPosition());
                }
            });
        } else {
            ((RepairImageVh)holder).bindData(mItems.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        int itemCount = getItemCount();
        if (position == itemCount - 1) {//最后一项，判断是不是添加图片按钮
            if (mItems.size() >= itemCount) {//所有项都是图片,最后一项不是添加图片按钮
                return TYPE_IMAGE;
            } else {//图片未满，最后一项为添加图片按钮
                return TYPE_ADD_IMAGE;
            }
        } else {//非最后一项肯定是图片
            return TYPE_IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        if (mItems == null) return 1;
        if (mItems.size() >= mMaxCount) return mMaxCount;
        return mItems.size() + 1;
    }

    public void addImage(ImageDetail image) {
        mItems.add(image);
    }

    public void processResultData(Intent data) {
        List<MMImageBean> imageList = data.getParcelableArrayListExtra(EXTRA_DATA);
        String resourceType = data.getExtras().getString(MMVideoAlbumFragment.EXTRA_TYPE);
        for (MMImageBean imageBean: imageList) {
            ImageDetail imageDetail = new ImageDetail();
            imageDetail.setLoadInfo(ImageDetail.TYPE_LOADING);
            imageDetail.setPicUrl(imageBean.getPath());
            addImage(imageDetail);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加图片点击监听器
     */
    public interface OnAddImageClickListener{
        /**
         * 添加图片被点击
         * @param count 还可以加几张图
         */
        void onAddImageClick(View view, int count);
    }
}

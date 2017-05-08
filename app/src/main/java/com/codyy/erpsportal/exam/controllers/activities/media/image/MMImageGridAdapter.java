package com.codyy.erpsportal.exam.controllers.activities.media.image;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * @author eachann
 */
public class MMImageGridAdapter extends MMBaseRecyclerViewAdapter<MMImageBean> {
    private int mPosition = -1;

    public MMImageGridAdapter(List<MMImageBean> list, Context context) {
        super(list, context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.multimedia_item_image_grid, parent, false);
        return new NormalRecyclerViewHolder(view);
    }

    public void setPosition(int position) {
        notifyItemChanged(mPosition);
        this.mPosition = position;
        notifyItemChanged(mPosition);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        try {
            if (list.get(position).getPath() == null) {
                ((NormalRecyclerViewHolder) viewHolder).layerView.setVisibility(View.GONE);
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.setBackgroundColor(Color.GRAY);
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.setImageURI(Uri.parse("res://com.codyy.erpsportal/" + R.drawable.ic_exam_camera));
            } else {
                ((NormalRecyclerViewHolder) viewHolder).layerView.setVisibility(View.VISIBLE);
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.setController(getDraweeController(viewHolder, list.get(position).getThumbnails()));
                if (list.get(position).isSelected()) {
                    ((NormalRecyclerViewHolder) viewHolder).imgQueueMultiSelected.setImageResource(R.drawable.ic_exam_select_p);
                    ((NormalRecyclerViewHolder) viewHolder).layerView.setBackgroundResource(R.color.image_selected_color);
                } else {
                    ((NormalRecyclerViewHolder) viewHolder).imgQueueMultiSelected.setImageResource(R.drawable.ic_exam_select_n);
                    ((NormalRecyclerViewHolder) viewHolder).layerView.setBackgroundResource(R.color.transparent);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private DraweeController getDraweeController(RecyclerView.ViewHolder viewHolder, String path) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + path));
        imageRequestBuilder.setResizeOptions(new ResizeOptions(
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.getLayoutParams().width,
                ((NormalRecyclerViewHolder) viewHolder).imgQueue.getLayoutParams().height));
        ImageRequest imageRequest = imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true).build();
        return Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(((NormalRecyclerViewHolder) viewHolder).imgQueue.getController())
                .setAutoPlayAnimations(true)
                .build();
    }

    public static class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imgQueue;
        ImageView imgQueueMultiSelected;
        View layerView;

        public NormalRecyclerViewHolder(View view) {
            super(view);
            imgQueue = (SimpleDraweeView) view.findViewById(R.id.imgQueue);
            imgQueueMultiSelected = (ImageView) view.findViewById(R.id.cb_select_tag);
            layerView = view.findViewById(R.id.v_selected_frame);
        }
    }
}

package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.RecyclerView.RecyclerTabLayoutSimple;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;

/**
 * 应用-家长-适配器
 * @author poe 2016-2-23
 */
public class FunctionParentAdapter extends RecyclerTabLayoutSimple.Adapter<FunctionParentAdapter.ViewHolder> {

    private List<Student> mData;
    private RecyclerTabLayoutSimple mRecyclerTabLayoutSimple ;

    public FunctionParentAdapter(List<Student> datas,RecyclerTabLayoutSimple recyclerTabLayoutSimple, RecyclerTabLayoutSimple.OnItemClickListener clickListener) {
        super(clickListener);
        this.mData = datas ;
        this.mRecyclerTabLayoutSimple = recyclerTabLayoutSimple;
    }

    /**
     * update the data set .
     * @param icons icons
     */
    public void setData(List<Student> icons){
        this.mData =   icons   ;
        this.notifyDataSetChanged();
        if(null != mOnItemClickListener){
            mOnItemClickListener.OnClick(getCurrentIndicatorPosition());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_parent_child,null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setPadding(UIUtils.dip2px(EApplication.instance(),10),UIUtils.dip2px(EApplication.instance(),10),UIUtils.dip2px(EApplication.instance(),10),0);
        itemView.setLayoutParams(params);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mData.get(position));
        RelativeLayout.LayoutParams params ;
        if(null != mRecyclerTabLayoutSimple && mRecyclerTabLayoutSimple.getIndicatorPositoin() == position){
            params = new RelativeLayout.LayoutParams(UIUtils.dip2px(holder.mImageView.getContext(),64f),UIUtils.dip2px(holder.mImageView.getContext(),64f));
        }else{
            params = new RelativeLayout.LayoutParams(UIUtils.dip2px(holder.mImageView.getContext(),54f),UIUtils.dip2px(holder.mImageView.getContext(),54f));
        }

        holder.mImageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mContainer ;
        public SimpleDraweeView mImageView;
        public TextView mNameText;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer  =   itemView.findViewById(R.id.rlt_container);
            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mNameText    = (TextView) itemView.findViewById(R.id.tv_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnItemClickListener().OnClick(getAdapterPosition());
                }
            });
        }

        public void setData(Student speaker){
            mNameText.setText(speaker.getStudentName());
            ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mImageView,speaker.getStudentHeadPic());
        }
    }
}

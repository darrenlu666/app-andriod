package com.codyy.tpmp.filterlibrary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.tpmp.filterlibrary.R;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.LoadMoreViewHolder;
import java.util.List;

/**
 * Created by poe on 15-8-10.
 * 基本抽象的Recycler adapter ...
 */
public  class BaseRecyclerAdapter<T,VH extends BaseRecyclerViewHolder> extends RecyclerView.Adapter{
    private static final String TAG = "BaseRecyclerAdapter";
    public static final int TYPE_FOOTER = Integer.MIN_VALUE;

    private boolean mHasMoreData;//设置是否可以继续加载数据
    private boolean mRefreshing = false ;//控制加载"更多"是否显示...

    private List<T> mData;
    private ViewCreator<VH> mCreator;
    protected OnItemClickListener<T> mOnItemClickListener;
    protected OnLoadMoreClickListener mOnLoadMoreClickListener;
    protected int mSelectedPosition = 0 ;//默认选中的位置
    private static final long MIN_CLICK_TIME = 1*1000L;
    private long mLastClickTimeMillions = -1;

    public BaseRecyclerAdapter(ViewCreator<VH> mCreator) {
        this.mCreator = mCreator;
    }
    public void setData(List<T> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (mData != null) {
            mData.addAll(data);
        } else {
            setData( data);
        }

        this.notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    public void clearData() {
        if (mData != null)
            mData.clear();
    }

    public boolean isEnable() {
        return mEnable;
    }

    public void setEnable(boolean enable) {
        this.mEnable = enable;
    }

    public boolean isHasMoreData() {
        return mHasMoreData;
    }

    public void setHasMoreData(boolean hasMoreData) {
        this.mHasMoreData = hasMoreData;
        this.notifyDataSetChanged();
    }

    public boolean isRefreshing() {
        return mRefreshing;
    }

    public void setRefreshing(boolean mRefreshing) {
        this.mRefreshing = mRefreshing;
        this.notifyDataSetChanged();
    }

    public void setOnLoadMoreClickListener(OnLoadMoreClickListener listener){
        this.mOnLoadMoreClickListener = listener ;
    }

    private int getBasicItemCount() {
        return mData == null ?0 : mData.size();
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder<T> viewHolder = null ;
        if (viewType == TYPE_FOOTER) {//底部 加载view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_load_more,parent,false);
            viewHolder =  new LoadMoreViewHolder(view,mOnLoadMoreClickListener);
        } else if(null != mCreator){
            viewHolder =  mCreator.createViewHolder(parent,viewType);
            initItemClickListener(viewHolder);
        }else{
            //give an default view or throws exception
            throw new IllegalArgumentException("ViewCreator can not NULL , it must be implemented！！！");
        }

        return viewHolder;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
    }

    private void setChildListener(View view , View.OnClickListener clickListener) {
        if(view instanceof ViewGroup ){
//            Log.i(TAG , " itemView is an ViewGroup !");
            ViewGroup viewGroup = (ViewGroup) view;
//            Log.i(TAG,"item view count is : " + viewGroup.getChildCount() );
            if(viewGroup.getChildCount()==0) return;
            for(int i = 0 ; i < viewGroup.getChildCount() ; i++ ){
                final View v = viewGroup.getChildAt(i);
//                Log.i(TAG , " view [ "+i+" ] id :@"+v);
                if(v instanceof ViewGroup ){
                    setChildListener(v , clickListener);
                }else{
                    v.setOnClickListener(clickListener);
                }
            }
        }
    }

    /**
     * 是否可以点击 : default: true
     */
    private boolean mEnable = true;

    private void initItemClickListener(final BaseRecyclerViewHolder<T> viewHolder) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止多次点击.
                boolean isDoubleClick = false;
                long currentTime = System.currentTimeMillis();
                if(currentTime - mLastClickTimeMillions < MIN_CLICK_TIME){
                    isDoubleClick = true;
                    mLastClickTimeMillions = currentTime;
                }else{
                    mLastClickTimeMillions = currentTime;
                }
                if (mOnItemClickListener != null && mEnable && !isDoubleClick) {
                    try {
                        setSelectedPosition(viewHolder.getCurrentPosition());
                        mOnItemClickListener.onItemClicked(v, viewHolder.getCurrentPosition(), viewHolder.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //set the item click listener .
        viewHolder.itemView.setOnClickListener(clickListener);
        setChildListener(viewHolder.itemView , clickListener);
    }

    @Override
    public int getItemViewType(int position) {
        //过滤非法状态
        if(position < 0) return  position;
        if(null != mCreator){
            if (position == getBasicItemCount() && mHasMoreData) {
                return TYPE_FOOTER;
            }

            if(position>= getBasicItemCount()){
                return 0;
            }
            //USER INTERFACE
            try{
                return  mCreator.getItemViewType(position);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadMoreViewHolder){
            LoadMoreViewHolder lvh = (LoadMoreViewHolder)holder;
            if(mHasMoreData){
                lvh.show();
            }else{
                lvh.dismiss();
            }
            lvh.showMore(mRefreshing);
        }else if(holder instanceof BaseRecyclerViewHolder){
            BaseRecyclerViewHolder<T> bvh = (BaseRecyclerViewHolder<T>) holder;
            try {
                //设置数据
                bvh.setData(mData.get(position));
                //设置当前view处于recyclerView中的相对位置
                bvh.setCurrentPosition(position);
                //绘制View.
                bvh.setData(position , mData.get(position));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }else{
            throw new IllegalArgumentException("ViewHolder not extends BaseRecyclerViewHolder<T> ～！！！");
        }
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + (mHasMoreData ? 1 : 0);
    }

    public void setOnItemClickListener(OnItemClickListener<T> mOnClickListener) {
        this.mOnItemClickListener = mOnClickListener;
    }

    public interface  ViewCreator<VH>{
        /**public
         * create the viewHolder
         * @param parent
         * @param viewType
         * @return
         */

        VH createViewHolder(ViewGroup parent, int viewType);

        /**
         * return the view type .
         * @param position
         * @return
         */
        int getItemViewType(int position);

    }
    /** item click listener **/
    public interface OnItemClickListener<T>{
        /**
         * 单个item 点击事件
         * @param v 视图v .
         * @param position 子item的位置
         * @param data
         */
        void onItemClicked(View v, int position, T data) throws  Exception;
    }

    /**   more data click listener      */
    public interface OnLoadMoreClickListener{
        /**
         * 请求更多数据
         */
        void onMoreData();
    }
}

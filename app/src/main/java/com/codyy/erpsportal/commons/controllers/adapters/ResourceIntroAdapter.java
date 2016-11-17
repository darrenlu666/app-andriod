package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.EmptyViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.PagesViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleViewHolder;
import com.codyy.erpsportal.commons.widgets.TitleItemBar.OnMoreClickListener;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.resource.controllers.activities.MoreResourcesActivity;
import com.codyy.erpsportal.resource.controllers.viewholders.ResourceViewHolder;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 频道页优课资源数据适配器
 */
public class ResourceIntroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ResourceIntroAdapter";

    private static final int VIEW_TYPE_TITLE = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static final int VIEW_TYPE_TOP = 0x02;

    private static final int VIEW_TYPE_EMPTY = 0x03;

    private final List<GridItem> mItems;

    private final Context mContext;

    private GridLayoutManager mGridLayoutManager;

    public ResourceIntroAdapter(Context context, GridLayoutManager layoutManager) {
        mContext = context;
        mItems = new ArrayList<>();
        layoutManager.setSpanSizeLookup(new TitleSpanSizeLookup(mItems));
    }

    public void setItems(List<GridItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public boolean isTitleHeader(int position) {
        GridItem item = mItems.get(position);
        return item instanceof TitleItem;
    }

    public String itemToString(int position) {
        return mItems.get(position).toString();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_TITLE) {//标题栏
            return TitleViewHolder.create(parent.getContext());
        } else if (viewType == VIEW_TYPE_CONTENT){//资源
            view = inflateItemView(parent, R.layout.item_resource);
            return new ResourceViewHolder(view);
        } else if (viewType == VIEW_TYPE_TOP){//自动翻页幻灯片
            view = inflateItemView(parent, R.layout.item_res_intro_pages);
            return new PagesViewHolder(view);
        } else {
            view = inflateItemView(parent, R.layout.item_empty);
            return new EmptyViewHolder(view);
        }
    }

    /**
     * 获取要填充的子View
     * @param parent 父组件
     * @param layoutId 布局id
     * @return 单项
     */
    private View inflateItemView(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //根据不同ViewHolder，绑定不同数据
        if (holder instanceof ResourceViewHolder) {//资源
            final ResourceItem item = (ResourceItem)mItems.get(position);
            ((ResourceViewHolder)holder).bindItem( item.resource);
        } else if (holder instanceof TitleViewHolder){//小标题栏
            final TitleItem item = (TitleItem) mItems.get(position);
            TitleViewHolder titleViewHolder = (TitleViewHolder)holder;
            titleViewHolder.getTitleItemBar().setTitle(item.title);
            titleViewHolder.getTitleItemBar().setOnMoreClickListener(new OnMoreClickListener() {
                @Override
                public void onMoreClickListener(View view) {
//                    MoreResourceActivity.start(mContext, item.semesterId, item.title);
                    ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
                    AreaInfo areaInfo = AreaInfo.create(moduleConfig.getBaseAreaId(), moduleConfig.getSchoolId());
                    MoreResourcesActivity.start(mContext, areaInfo, item.semesterId, item.title);
                }
            });
        } else if (holder instanceof PagesViewHolder){//幻灯片区
            final TopItem item = (TopItem) mItems.get(position);
            ((PagesViewHolder)holder).bindView( item.resources);
        }
    }

    @Override
    public int getItemViewType(int position) {
        GridItem item = mItems.get(position);
        if (item instanceof TitleItem) {
            return VIEW_TYPE_TITLE;
        } else if (item instanceof ResourceItem) {
            return VIEW_TYPE_CONTENT;
        } else if (item instanceof TopItem){
            return VIEW_TYPE_TOP;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof PagesViewHolder) {
            PagesViewHolder pagesViewHolder = (PagesViewHolder)holder;
            pagesViewHolder.stopScrolling();
            mPagesViewHolders.remove(pagesViewHolder);
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof PagesViewHolder) {
            PagesViewHolder pagesViewHolder = (PagesViewHolder)holder;
            pagesViewHolder.startToScroll();
            mPagesViewHolders.add(pagesViewHolder);
        }
    }

    /**
     * 显示的幻灯片的ViewHolder
     */
    private List<PagesViewHolder> mPagesViewHolders = new ArrayList<>();

    public void onStart() {
        for(PagesViewHolder viewHolder: mPagesViewHolders){
            viewHolder.startToScroll();
        }
    }

    public void onStop() {
        for(PagesViewHolder viewHolder: mPagesViewHolders){
            viewHolder.stopScrolling();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            GridItem item = mItems.get(i);
            if (item.isTitle) {
                notifyItemChanged(i);
            }
        }
    }

    public void addItem(int index, GridItem item) {
        mItems.add(index, item);
    }

    public void addItem(GridItem item) {
        mItems.add(item);
    }

    public GridItem getItemAt(int index) {
        if (index >= mItems.size()||index < 0) return null;
        return mItems.get(index);
    }

    public GridItem removeItem(int index) {
        return mItems.remove(index);
    }

    /**
     * 从startIndex开始删除之后所有item
     * @param startIndex 开始位置
     */
    public void removeItems(int startIndex) {
        for (int i = mItems.size() - 1; i >= startIndex; i--) {
            mItems.remove(i);
        }
    }

    public void clearItems() {
        mItems.clear();
    }

    public static class GridItem {

        public boolean isTitle;

        public GridItem(boolean isTitle) {
            this.isTitle = isTitle;
        }
    }

    /**
     * 标题项
     */
    public static class TitleItem extends GridItem {

        public String title;

        /**
         * 年级id
         */
        public String semesterId;

        public TitleItem(String text, String semesterId) {
            super(true);
            this.title = text;
            this.semesterId = semesterId;
        }

        @Override
        public String toString() {
            return semesterId + ":" + title;
        }
    }

    /**
     * 资源项
     */
    public static class ResourceItem extends GridItem {
        public Resource resource;

        public ResourceItem(Resource resource) {
            super(false);
            this.resource = resource;
        }
    }

    /**
     * 幻灯片项
     */
    public static class TopItem extends GridItem {
        public List<Resource> resources;
        public TopItem(List<Resource> resources) {
            super(true);
            this.resources = resources;
        }

        public void setResources(List<Resource> resources) {
            this.resources = resources;
        }
    }

    /**
     * 提示“很抱歉，暂时没有相关内容”
     */
    public static class EmptyItem extends GridItem {

        public EmptyItem() {
            super(false);
        }
    }

    public static class TitleSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{

        private List<GridItem> mItems;

        public TitleSpanSizeLookup(List<GridItem> items) {
            mItems = items;
        }

        @Override
        public int getSpanSize(int position) {
            if (mItems.get(position) instanceof ResourceItem) {
                return 1;
            }
            return 2;
        }
    }
}

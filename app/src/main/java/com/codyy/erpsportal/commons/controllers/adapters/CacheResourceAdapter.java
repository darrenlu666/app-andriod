package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.services.HttpLargeDownload;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.TransferManager;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.utils.WeakHandler;
import com.codyy.erpsportal.commons.widgets.ProgressBarView;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.dao.DownloadDao;
import com.codyy.erpsportal.commons.models.entities.CacheItem;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by kmdai on 2015/4/10.
 */
public class CacheResourceAdapter extends BaseAdapter {
    private static final String TAG = "";
    private static final int MSG_UPDATE = 100;
    private List<CacheItem> caches;
    private WeakReference<Context> mAty ;
    private boolean isEdit = false;
    //时间更新不要太频繁 500ms一次就行
    private long mLastUpdate = 0;
    private final int TIME_DELAY = 500;
    private OnAllCheckedListener mAllCheckedListener;
    /**
     * 是否处于滚动模式
     */
    private boolean mScrolling = false;
    private CacheHandler mHandler = new CacheHandler(this);

    public CacheResourceAdapter(Context context, List<CacheItem> caches, OnAllCheckedListener checkListener) {
        mAty = new WeakReference<>(context);
        this.caches = caches;
        this.mAllCheckedListener = checkListener;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        this.notifyDataSetChanged();
    }

    private void tryUpdate(){
        if (!mScrolling) {
            update();
        }
    }

    public boolean isScrolling() {
        return mScrolling;
    }

    public void setScrolling(boolean scrolling) {
        this.mScrolling = scrolling;
    }

    //是否全选
    public void setCheckAll(boolean isCheck) {
        for (CacheItem crm : caches) {
            crm.setCheck(isCheck);
        }

        this.notifyDataSetChanged();
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return caches.size();
    }

    @Override
    public Object getItem(int position) {
        return caches.get(position);
    }

    public List<CacheItem> getCaches() {
        return caches;
    }

    public void setCaches(List<CacheItem> caches) {
        this.caches = caches;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mAty.get()).inflate(R.layout.item_cache_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set data
        viewHolder.setDataToView(position);

        RelativeLayout relativeLayout = (RelativeLayout) convertView;
        relativeLayout.startLayoutAnimation();

        return convertView;
    }

    class ViewHolder {

        private ImageView img;
        private TextView txtName;
        private TextView txtSize;
        private TextView txtSpeed;
        private CheckBox checkBox;
        private ProgressBarView progressBarView;
        private HttpLargeDownload.HttpDownloadListener listener;

        ViewHolder(View convertView) {
            checkBox = (CheckBox) convertView.findViewById(R.id.cache_item_check_btn);
            img = (ImageView) convertView.findViewById(R.id.cache_item_image);
            txtName = (TextView) convertView.findViewById(R.id.cache_item_text_name);
            txtSize = (TextView) convertView.findViewById(R.id.cache_item_text_size);
            txtSpeed = (TextView) convertView.findViewById(R.id.cache_item_text_speed);
            progressBarView = (ProgressBarView) convertView.findViewById(R.id.cache_item_progress);
            progressBarView.setMax(100);
            progressBarView.setClickable(false);
        }

        void setDataToView(final int position) {
            initListener(position);
            CacheItem cacheItem = caches.get(position);
            if (!TextUtils.isEmpty(cacheItem.getName())
                    &&!cacheItem.getName().equals(txtName.getText().toString())) {
                txtName.setText(cacheItem.getName());
            }
            long fileSize = cacheItem.getSize();
            if (fileSize != 0){
                Cog.d(TAG, "fileSize=",fileSize);
                String formatSize = Formatter.formatFileSize(txtSize.getContext(), fileSize);
                Cog.d(TAG, "formatSize=", formatSize);
                if(!formatSize.equals(txtSize.getText().toString())) {
                    txtSize.setText(formatSize);
                }
            }else{
                txtSize.setText("未知");
            }

            progressBarView.setProgress(cacheItem.getProgress());

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    caches.get(position).setCheck(!caches.get(position).isCheck());
                    //和activity外部的"全选"联动
                    if (null != mAllCheckedListener) {
                        boolean checkAll = true;
                        for (CacheItem crm : caches) {
                            if (!crm.isCheck()) {
                                checkAll = false;
                                break;
                            }
                        }
                        mAllCheckedListener.onCheckAll(checkAll);
                    }

                }
            });

            if (isEdit) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }

            if (caches.get(position).isCheck()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            //set image
            String icon = cacheItem.getThumbPath();//UiUtils.getSmallImage(data.getIcon());
            if(null != icon){
                Cog.i("res_down:", icon);
                Uri uri = Uri.parse(icon);
                String old = img.getTag() == null ? "" : img.getTag().toString();
                if (!old.equals(uri.toString())) {
                    img.setImageURI(uri);
                    img.setTag(uri);
                }
            }

            String resId = cacheItem.getId();
            String fileName = resId + cacheItem.getSuffix();

            if (FileDownloadService.hasCached(cacheItem.getBaseUserId(), fileName)) {
                cacheItem.setProgress(100);
                cacheItem.setSpeed("");
                txtSpeed.setText(cacheItem.getSpeed());
                //如果获取大小失败，下载完成后更新大小
                progressBarView.setProgress(cacheItem.getProgress());
                progressBarView.setPause(false);
                if("未知".equals(txtSize.getText().toString())){
                    int newLength = DownloadDao.instance(txtSize.getContext()).getFileSize(cacheItem.getBaseUserId(), cacheItem.getId());
                    cacheItem.setSize(newLength);
                    CacheDao cacheDao = new CacheDao(EApplication.instance());
                    cacheDao.update(cacheItem);
                    txtSize.setText(Formatter.formatFileSize(txtSize.getContext(), newLength));
                }
            } else {
                //get the speed and progress
                if (CacheItem.STATE_PAUSE.equals(cacheItem.getState())) {
                    txtSpeed.setText("继续");
                    //暂停下载
                    TransferManager.instance().pauseDownload(cacheItem.getId());
                    progressBarView.setPause(true);
                } else if (CacheItem.STATE_ERROR.equals(cacheItem.getState())){
                    txtSpeed.setText("无法连接");
                    //暂停下载
                    TransferManager.instance().pauseDownload(cacheItem.getId());
                    progressBarView.setPause(true);
                } else {
                    //继续下载
                    Context context = mAty.get();
                    if(null !=context ){
                        if(!VideoDownloadUtils.isConnected(context)){
                            ToastUtil.showToast(context, context.getString(R.string.net_error));
                        }else if(!NetworkUtils.isDownloadEnable(context)){
                            ToastUtil.showToast(context, context.getString(R.string.net_switch_close));
                        }else if (FileDownloadService.getOneLargerDownload(context, fileName, cacheItem.getDownloadUrl(), cacheItem.getBaseUserId(),resId)) {
                            //开启下载后 重置!
                            txtSpeed.setText(cacheItem.getSpeed() != null ? cacheItem.getSpeed() : "0kb/s");
                            progressBarView.setPause(false);
                            if(caches.size()>position){//防止全部删除后造成indexOut
                                TransferManager.instance().setHttpListener(caches.get(position).getId(), listener);
                            }
                        } else {
                            txtSpeed.setText(cacheItem.getSpeed() != null ? cacheItem.getSpeed() : "0kb/s");
                            TransferManager.instance().setHttpListener(cacheItem.getId(), listener);
                        }
                    }
                }
            }
            //如果已经下载完成了呢？是否应该显示一个个已完成，并把进度设为100%
            //获取对应的 HttpLargeDownload and set HttpListener;

        }

        private void initListener(final int position) {
            if (listener == null) {
                listener = new HttpLargeDownload.HttpDownloadListener() {
                    @Override
                    public void onComplete(HttpLargeDownload download, String url, String filename) {
                        if (!mScrolling) {
                            if (position < 0 || position >= caches.size()) return;
                            CacheItem cacheItem = caches.get(position);
                            cacheItem.setProgress(100);
                            cacheItem.setSpeed("");
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    }

                    @Override
                    public void onProgress(int progress, String speed) {
                        Cog.e("CacheAdapter", "progress:" + progress + " ::speed:" + speed);
                        if (position < 0 || position >= caches.size()) return;
                        CacheItem cacheItem = caches.get(position);
                        cacheItem.setProgress(progress);
                        cacheItem.setSpeed(speed);

                        if (!mScrolling) {
                            long now = System.currentTimeMillis();
                            if (now - mLastUpdate > TIME_DELAY) {
                                mLastUpdate = System.currentTimeMillis();
                                if (progress == 100) {
                                    TransferManager.instance().pauseDownload( cacheItem.getId());
                                }
                                mHandler.sendEmptyMessage(MSG_UPDATE);
                            }
                        }
                    }

                    @Override
                    public void onError(String url) {
                        Cog.d(TAG, "onError url=", url);
                        if (position < 0 || position >= caches.size()) return;
                        CacheItem cacheItem = caches.get(position);

                        cacheItem.setState(CacheItem.STATE_ERROR);
                        CacheDao cacheDao = new CacheDao(EApplication.instance());
                        cacheDao.update(cacheItem);
                    }
                };
            }
        }
    }

    /**
     * 监控选项的选择情况
     */
    public interface OnAllCheckedListener {
        /**
         * 是否全选了所有选项
         *
         * @param selectAll
         */
        void onCheckAll(boolean selectAll);
    }

    private static class CacheHandler extends WeakHandler<CacheResourceAdapter>{

        public CacheHandler(CacheResourceAdapter owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(null == getOwner()) return;
            switch (msg.what) {
                case MSG_UPDATE://update
                   getOwner().tryUpdate();
                   break;
            }
        }
    }
}


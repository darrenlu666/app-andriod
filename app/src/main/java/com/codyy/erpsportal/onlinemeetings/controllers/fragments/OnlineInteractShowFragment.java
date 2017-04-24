package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.Hit;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.artifex.mupdfdemo.MuPDFView;
import com.artifex.mupdfdemo.OutlineActivityData;
import com.artifex.mupdfdemo.ReaderView;
import com.artifex.mupdfdemo.SearchTaskResult;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.OnlineSimpleDocumentViewHolder;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.onlinemeetings.models.entities.DocControl;
import com.codyy.erpsportal.commons.models.entities.MeetingShow;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 视频会议-演示模式（new ）
 *
 * @Created by poe
 */
public class OnlineInteractShowFragment extends OnlineFragmentBase {
    private static final String TAG = "OnlineInteractShowFragment";
    /** 加载文档指令**/
    private static final int MSG_LOAD_DOCUMENT = 0x0110;
    /**
     * 判断是否来自服务器端的命令/滚动pdf文档
     */
    private boolean mIsFromServerPagerChange = false;
    /**
     * 是否来自服务器的关闭文档
     */
    private boolean mIsFromServerCloseTab = false;
    /**
     * 是否是来自服务器的 切换文档
     */
    private boolean mIsFromServerChooseTab = false;

    @Bind(R.id.text_doc_title)
    TextView mTitleTextView;
    @Bind(R.id.text_btn_more)
    ImageView mMoreImageView;
    @Bind(R.id.drawer_layout_online_show)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.recycler_view_online_show)
    RecyclerView mRecyclerView;//文档列表。
    @Bind(R.id.rlt_container)
    RelativeLayout mContainer;//容器
    @Bind(R.id.progress_bar_online_show)
    ProgressBar mProgressBar;//加载进度条

    private BaseRecyclerAdapter<MeetingShow, OnlineSimpleDocumentViewHolder> mAdapter;
    private List<MeetingShow> mList = new ArrayList<>();
    private MeetingShow mCurrentShow;
    private String mLastTitle;//上一次加载的文件title.
    public final static long MIN_PERIOD = 5 * 1000;//相同文档允许点击的最大时间间隔
    private long mLastClickTime = -1;
    private MuPDFCore mMuPDFCore;
    private MuPDFReaderView mDocView;
    private TextView mPagerIndexTextView;
    private String mFileName;
    private boolean mIsShowFromUser = false; //用户演示文档
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_interact_show;
    }

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        init();
        loadData();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Cog.e(TAG, "onViewCreated()~");
    }

    /**
     * 获取最新的演示详情
     */
    private void loadData() {
        Cog.e(TAG, "loadData()~~");
        mProgressBar.setVisibility(View.VISIBLE);
        UiOnlineMeetingUtils.loadMeetingData(getParentActivity(), mUserInfo.getUuid(), mMeetID, URLConfig.GET_ONLINE_MEETING_SHOW_DETAIL, new UiOnlineMeetingUtils.ICallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Cog.d(TAG, response.toString());
                List<MeetingShow> data = MeetingShow.parseList(response.optJSONArray("meet_show"));

                if (data != null) {
                    mList = data;
                    updateDataRole();
                }

                mAdapter.setData(mList);
                //load the current document .
                refreshCurrentContent();
            }

            @Override
            public void onFailure(JSONObject response) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNetError() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    //判断用户权限
    private void updateDataRole() {
        for (int i = 0; i < mList.size(); i++) {
            if (mMeetingBase.isWhiteBoardManager()) {
                mList.get(i).setShowDelete(true);
            } else {
                mList.get(i).setShowDelete(false);
            }
        }
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 刷新显示区域内容
     */
    private void refreshCurrentContent() {
        Cog.i(TAG, "refreshCurrentContent~");
        int index = 0;
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                //正在演示的文档 .
                if (mList.get(i).getShowModel().equals("2")) {
                    index = i;
                    break;
                }
            }
            syncLoadContent(mList.get(index), false);
        } else {
            //当前文档演示没有文档内容，则初次进入标志 置false
            OnlineMeetingActivity.mFirstEnter = false;
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 加载数据内容
     *
     * @param meetingShow
     * @param isNewOpen   是否是本地新添的演示文档
     */
    private void loadContent(MeetingShow meetingShow, boolean isNewOpen) {
        if (null == mTitleTextView) return;
        if(mProgressBar.getVisibility() == View.GONE) mProgressBar.setVisibility(View.VISIBLE);
        Cog.i(TAG, "loadContent:" + (meetingShow != null ? meetingShow.getShowTitle() : null) + " :isNewopen : " + isNewOpen);
        mCurrentShow = meetingShow;
        //图片和文档使用MUPdf
        if (UiOnlineMeetingUtils.hasLoadCompleted(meetingShow.getShowTitle())) {
            tryLoadPdf(meetingShow, isNewOpen);
        } else {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
            mTitleTextView.setText(meetingShow.getShowTitle());
            DownLoadPDFTask dt = new DownLoadPDFTask(isNewOpen);
            dt.execute(meetingShow);
        }
    }

    /**
     * 演示文档-发送命令给服务器 ...
     *
     * @param isNewOpen
     * @param pageCount
     */
    private void postDocToCoco(boolean isNewOpen, final int pageCount) {
        Cog.e(TAG, "postDocToCoco :: isFromServerCloseTab :" + mIsFromServerChooseTab + " >>isFromServerChooseTab : " + mIsFromServerChooseTab + " getTabIndex : " + getTabIndex() + "<<" + " coco:" + getCocoService());
        if (!mIsFromServerCloseTab && !mIsFromServerChooseTab && getCocoService() != null && getTabIndex() == 1) {
            Cog.e(TAG, "loadContent setDocument .");
            UiOnlineMeetingUtils.ICallback callback = new UiOnlineMeetingUtils.ICallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Cog.i(TAG, "");
                    //http服务器数据删除成功
                    String url = mCurrentShow.getShowResID() + "//," + (mCurrentShow.getShowCount() > 0 ? mCurrentShow.getShowCount() : pageCount);
                    try {
                        getCocoService().setDemonstrationDoc(mMeetID, "0", mUserInfo.getBaseUserId(), url, mCurrentShow.getShowResID(), mCurrentShow.getShowTitle());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject response) {

                }

                @Override
                public void onNetError() {
                }
            };

            if (!isNewOpen) {
                UiOnlineMeetingUtils.setDocument(getParentActivity(), mUserInfo.getUuid(), mMeetID, mCurrentShow.getShowResID(), null, URLConfig.SET_ONLINE_MEETING_SHOW_DOCUMENT, callback);
            } else {
                UiOnlineMeetingUtils.openDocument(getParentActivity(), mUserInfo.getUuid(), mMeetID, mCurrentShow.getShowResID(), callback);
            }
        }
        //tab文档切换一次后失效
        mIsFromServerChooseTab = false;
        mIsFromServerCloseTab = false;

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    private void tryLoadPdf(final MeetingShow meetingShow, boolean isNewOpen) {
        Cog.d(TAG, "tryLoadPdf...");
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        //bmp转换为jpg.
        String ResourceName = meetingShow.getShowTitle();
        if (!TextUtils.isEmpty(ResourceName) && ResourceName.endsWith(".bmp")) {
            ResourceName = ResourceName.substring(0, ResourceName.indexOf(".bmp")) + ".jpg";
        }
        mFileName = UiOnlineMeetingUtils.createDir() + "/" + ResourceName;
        loadPdf(meetingShow, isNewOpen);
    }

    private boolean mIsLoading = false;//是否在加载文档 ，default: false ;
    /**
     * 打开pdf （mupdf)
     * @param meetingShow
     */
    private void loadPdf(MeetingShow meetingShow, boolean isNewOpen) {
        if (null == mTitleTextView) return;
        //禁止点击显示文档列表了
        mIsLoading = true;
        try{
            mTitleTextView.setText(meetingShow.getShowTitle());
            cleanPdfCache();
            if (mMuPDFCore == null) {
                if (null != mFileName) {
                    Uri uri = Uri.parse(mFileName);
                    System.out.println("URI to open is: " + uri);
                    String path = Uri.decode(uri.getEncodedPath());
                    if (path == null) {
                        path = uri.toString();
                    }
                    mMuPDFCore = openFile(path);
                    SearchTaskResult.set(null);
                }
                if (mMuPDFCore != null && mMuPDFCore.countPages() == 0) {
                    mMuPDFCore = null;
                }
            }

            if (mMuPDFCore == null) {
                File file = new File(mFileName);
                if (file != null && file.exists()) {
                    file.delete();
                }
                file.deleteOnExit();
                if (getTabIndex() == 1) {
                    Snackbar.make(mRecyclerView, "无法打开文件!", Snackbar.LENGTH_SHORT).show();
                }

                //即使本地打开失败，也需要发送命令 ... 默认最大页码：｛＃showPageIndex()｝
                //计算页数 。
                if (!OnlineMeetingActivity.mFirstEnter) {
                    postDocToCoco(isNewOpen, meetingShow.getShowPageIndex());
                } else {
                    OnlineMeetingActivity.mFirstEnter = false;
                }
                //移除不必要的控件防止加载失败后的pdf文档通过点击事件操作不存在的温昂被带入沟里！
                mContainer.removeAllViews();
                return;
            }
            createUI(meetingShow, isNewOpen);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mIsLoading = false;
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void createUI(final MeetingShow meetingShow, boolean isNewOpen) {
        if (null == getParentActivity()) return;
        mCurrentShow = meetingShow;
        //空指针错误
        mDocView = new MuPDFReaderView(getParentActivity()) {
            @Override
            protected void onMoveToChild(int i) {
                if (mMuPDFCore == null) return;
                super.onMoveToChild(i);
                meetingShow.setShowPageIndex(i);
                if (null != mPagerIndexTextView) {
                    mPagerIndexTextView.setText(String.format("%d / %d", i + 1, mMuPDFCore.countPages()));
                }
                //非第一次过滤
                if (!OnlineMeetingActivity.mFirstEnter && getTabIndex() == 1 && getHostTabIndex() == 0) {
                    asyncServerDocPageIndex(i);
                } else {
                    OnlineMeetingActivity.mFirstEnter = false;
                }
            }

            @Override
            protected void onTapMainDocArea() {
            }

            @Override
            protected void onDocMotion() {
            }


            @Override
            protected void onHit(Hit item) {
            }
        };


        mDocView.setAdapter(new MuPDFPageAdapter(getParentActivity(), new FilePicker.FilePickerSupport() {
            @Override
            public void performPickFor(FilePicker picker) {
                //选择文件
            }
        }, mMuPDFCore));

//        int smax = Math.max(mMuPDFCore.countPages()-1,1);
        //加载控制按钮
        View controlView = LayoutInflater.from(getParentActivity()).inflate(R.layout.pdf_online_meeting_control_view, null);
        mPagerIndexTextView = (TextView) controlView.findViewById(R.id.text_page_index);

        mContainer.removeAllViews();
        mContainer.addView(mDocView);
        mContainer.addView(controlView);

        //set page index .
        Cog.e(TAG, "当前页码：" + meetingShow.getShowPageIndex() + "," + mFileName + " Fist enter ? " + OnlineMeetingActivity.mFirstEnter);
        //计算页数 。
        if (!OnlineMeetingActivity.mFirstEnter) {
            postDocToCoco(isNewOpen, mMuPDFCore.countPages());
        } else {
            OnlineMeetingActivity.mFirstEnter = false;
        }

        //set page index .
        if (meetingShow.getShowPageIndex() > 0) {
            mDocView.setDisplayedViewIndex(meetingShow.getShowPageIndex());
        } else {
            //设置默认页码
            if (null != mPagerIndexTextView) {
                mPagerIndexTextView.setText(String.format("%d / %d", 1, mMuPDFCore.countPages()));
            }
        }
    }

    /**
     * 设置演示文档进度页码
     *
     * @param i
     */
    private void asyncServerDocPageIndex(int i) {
        Cog.e(TAG, "asyncServerDocPageIndex :" + i);
        if (!mIsFromServerPagerChange && getCocoService() != null) {
            final String index = "" + i;
            UiOnlineMeetingUtils.setDocument(getParentActivity(), mUserInfo.getUuid(), mMeetID, mCurrentShow.getShowResID(), index, URLConfig.SET_ONLINE_MEETING_SHOW_INDEX, new UiOnlineMeetingUtils.ICallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Cog.e(TAG, "asyncServerDocPageIndex setDocument success, send coco ~");
                    try {
                        getCocoService().setChangeDoc(mMeetID, index, mCurrentShow.getShowResID() + "");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject response) {
                    Cog.e(TAG, "asyncServerDocPageIndex setDocument onFailure :" + response);
                }

                @Override
                public void onNetError() {
                    Cog.e(TAG, "asyncServerDocPageIndex setDocument onNetError ！");
                }
            });
        }

        //解锁  .
        mIsFromServerPagerChange = false;
    }

    private void init() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));

        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<OnlineSimpleDocumentViewHolder>() {
            @Override
            public OnlineSimpleDocumentViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new OnlineSimpleDocumentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_online_show_document, null));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<MeetingShow>() {
            @Override
            public void onItemClicked(View v, int position, MeetingShow data) {
                Cog.i(TAG, "clicked :: " + data.getShowTitle());
                if (v.getId() == R.id.img_item_delete) {
                    removeDocument(data);
                } else if (v.getId() == R.id.txt_item_title) {
                    clickDocument(data);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private MuPDFCore openFile(String path) {
        int lastSlashPos = path.lastIndexOf('/');
        String fileName = lastSlashPos == -1 ? path : path.substring(lastSlashPos + 1);
        System.out.println("Trying to open " + path);
        try {
            mMuPDFCore = new MuPDFCore(EApplication.instance(), path);
            // New file: drop the old outline data
            OutlineActivityData.set(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (java.lang.OutOfMemoryError e) {
            //  out of memory is not an Exception, so we catch it separately.
            e.printStackTrace();
            return null;
        }
        return mMuPDFCore;
    }

    //action-选择文档
    private void clickDocument(MeetingShow meetingShow) {
        Cog.i(TAG, "clickDocument :: " + meetingShow.getShowTitle());
        if (null != meetingShow) {
            //切换试图
            if (meetingShow.getShowTitle().equals(mLastTitle)) {
                long current = System.currentTimeMillis();
                if ((current - mLastClickTime) > MIN_PERIOD) {
                    syncLoadContent(meetingShow, false);
                    mLastClickTime = current;
                } else {
//                    ToastUtil.showToast(getParentActivity(), "频繁操作！");
                }
            } else {
                syncLoadContent(meetingShow, false);
            }

            mLastTitle = meetingShow.getShowTitle();
        }
    }

    //action-删除文档
    private void removeDocument(final MeetingShow doc) {
        if (null != getCocoService()) {
            Cog.e(TAG, "onDelete请求 setDocument .");
            //if http can delete then send coco delete message and update local data ...
            UiOnlineMeetingUtils.setDocument(getParentActivity(), mUserInfo.getUuid(), mMeetID, doc.getShowResID(), null, URLConfig.DELETE_ONLINE_MEETING_SHOW_DOCUMENT
                    , new UiOnlineMeetingUtils.ICallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            //http服务器数据删除成功
                            try {
                                getCocoService().setDelectDoc(mMeetID, doc.getShowResID());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            deleteDoc(doc.getShowResID());
                        }

                        @Override
                        public void onFailure(JSONObject response) {
                        }

                        @Override
                        public void onNetError() {
                        }
                    });

        }
    }

    @OnClick(R.id.text_btn_more)
    public void showOrHideList() {
        if(mIsLoading) return;
        if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    //明天使用volley代替http connection .
    private class DownLoadPDFTask extends AsyncTask<MeetingShow, Integer, MeetingShow> {

        private boolean isNewOpen = true;

        public DownLoadPDFTask(boolean isNewOpen) {
            this.isNewOpen = isNewOpen;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsLoading = true;
        }

        @Override
        protected MeetingShow doInBackground(MeetingShow... params) {
            MeetingShow meetingShow = params[0];
            InputStream is = null;
            OutputStream os = null;
            HttpURLConnection connection = null;
            String ResourceName = meetingShow.getShowTitle();
            //bmp转换为jpg.
            if (!TextUtils.isEmpty(ResourceName) && ResourceName.endsWith(".bmp")) {
                ResourceName = ResourceName.substring(0, ResourceName.indexOf(".bmp")) + ".jpg";
            }
            String directory = UiOnlineMeetingUtils.createDir() + "/" + ResourceName;

            try {
                String docPath = meetingShow.getShowDocPath();
                Cog.i(TAG, "down address : " + docPath);
                URL url = new URL(docPath);
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }

                    int fileLength = connection.getContentLength();
                    is = connection.getInputStream();
                    os = new FileOutputStream(directory);

                    //判断是否是bmp
                    if (ResourceName.endsWith(".bmp") || ResourceName.endsWith(".jpg")) {
                        Cog.d(TAG, "doInBackground: .bmp");
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        if (null != bitmap) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        }
                    } else {
                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = is.read(data)) != -1) {
                            if (isCancelled()) {
                                is.close();
                                return null;
                            }
                            total += count;
                            if (fileLength > 0)
                                publishProgress((int) total * 100 / fileLength);

                            os.write(data, 0, count);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return meetingShow;
        }

        @Override
        protected void onPostExecute(MeetingShow s) {
            super.onPostExecute(s);
            Cog.d(TAG, "文档加载完成");
            if (s != null && !isHidden() && !isDetached()) {
                tryLoadPdf(s, isNewOpen);
            } else {
                mProgressBar.setVisibility(View.GONE);
                Snackbar.make(mRecyclerView, "文档加载失败:" + (s != null ? s.getShowTitle() : "!"), Snackbar.LENGTH_SHORT).show();
                if (OnlineMeetingActivity.mFirstEnter) {
                    OnlineMeetingActivity.mFirstEnter = false;
                }
                cleanPdfCache();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private boolean isPause = false;

    @Override
    public void onPause() {
        super.onPause();
        Cog.d(TAG, "onPause ~ ");
        isPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Cog.d(TAG, "onResume ~ ");
        //恢复文档内容 .
        if (isPause) {
            isPause = false;
            if (mCurrentShow != null && UiOnlineMeetingUtils.DOC_TYPE_PDF.equals(UiOnlineMeetingUtils.CheckResourceType(mCurrentShow.getShowTitle()))) {
                if (!mIsShowFromUser) {//防止一次演示未完成被终止。
                    syncLoadContent(mCurrentShow, false);
                } else {
                    mIsShowFromUser = false;
                }
            }
        }
    }

    /**
     * 演示文档(用户手动打开)
     *
     * @throws RemoteException
     */
    public void onEventMainThread(MeetingShow meetingShow) throws RemoteException {
        if (null == meetingShow) return;
        changeDoc(meetingShow);
    }

    public void onEventMainThread(CoCoAction action) throws RemoteException {
        //发言人的变更/更新文档列表
        switch (action.getActionType()) {
            case PullXmlUtils.COMMAND_WHITE_BOARD_MARK: //授予-白板标注权限
                String result = action.getActionResult();
                mMeetingBase.setWhiteBoardManager(Boolean.valueOf(result) ? "1" : "0");
                updateDataRole();// TODO: 16-8-24 文档显示ＵＩ变化，可以演示，可以删除 ...
                break;
        }
    }

    /**
     * 接受文档管理发送的命令,执行文文档演示
     *
     * @param meetingShow
     */
    private void changeDoc(MeetingShow meetingShow) {
        Cog.i(TAG, "changeDoc ~" + meetingShow.getShowTitle());
        mIsShowFromUser = true;
        if (null == meetingShow) return;
        int pos = UiOnlineMeetingUtils.getResourceIndex(meetingShow.getShowResID(), mList);
        //1.1 新的文档
        if (pos < 0) {
            //add .
            mList.add(meetingShow);
            //turn to new doc .
            setDocSelected(mList.size() - 1, true);
        } else {
            //1.2 已经存在的文档
            setDocSelected(pos, false);
        }
    }

    /**
     * 选中一个文档 .
     *
     * @param index
     */
    public void setDocSelected(int index, boolean isNewOpen) {
        //如果是本地打开的，需要发送控制命令
        Cog.i(TAG, "setDocSelected" + index + " : isNewOpen" + isNewOpen);
        if (mList != null && index < mList.size()) {
            for (int i = 0; i < mList.size(); i++) {
                if (i == index) {
                    mList.get(i).setShowModel("2");
                } else {
                    mList.get(i).setShowModel("1");
                }
            }
        }

        //加载数据 .
        syncLoadContent(mList.get(index), isNewOpen);
    }

    private MeetingShow mTempMeetingShow = null ;//临时需要加载的文档缓存
    private boolean mTempShowTag = false;//是否新打开文档缓存副本
    private Runnable mLoadMeetingShowRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mLoadMeetingShowRunnable);
            if(null != mTempMeetingShow)
            loadContent(mTempMeetingShow,mTempShowTag);
        }
    };

    private void syncLoadContent(MeetingShow show , boolean isNewOpen){
        mHandler.removeCallbacks(mLoadMeetingShowRunnable);
        if(null == show) return;
        mTempMeetingShow = show;
        mTempShowTag = isNewOpen;
        mHandler.postDelayed(mLoadMeetingShowRunnable,500);
    }
    public void onEventMainThread(DocControl action) throws RemoteException {
        Cog.e(TAG, "DocControl: " + action.getActionType());

        if (action.getFrom().equals(mUserInfo.getBaseUserId())) {
            //如果命令是自己的
            return;
        }

        switch (action.getActionType()) {
            case PullXmlUtils.CHANGE_DOC_PAD://演示白板
                break;
            case PullXmlUtils.SHOW_DOC://演示文档
                mIsFromServerChooseTab = true;
                int index = UiOnlineMeetingUtils.getResourceIndex(action.getId(), mList);
                if (index > -1) {
                    syncLoadContent(mList.get(index), false);
                } else {
                    //刷新数据
                    loadData();
                }
                break;
            case PullXmlUtils.CHANGE_DOC:// 翻页
                //过滤掉自己发送的命令
                if (!action.getFrom().equals(mUserInfo.getBaseUserId())) {
                    mIsFromServerPagerChange = true;
                    int currentPage = Integer.parseInt(action.getCurrent());
                    String resID2 = action.getOwner().substring(action.getOwner().indexOf("_") + 1);

                    Cog.i(TAG, "pdf翻页 " + currentPage + " :resid :" + resID2);

                    //如果当前不是 最新的resID 同步过来
                    if (!resID2.equals(mCurrentShow.getShowResID())) {//演示别的文档
                        //turn page to the index .
                        for (int i = 0; i < mList.size(); i++) {
                            //正在演示的文档 .
                            if (mList.get(i).getShowResID().equals(resID2)) {
                                mList.get(i).setShowPageIndex(currentPage);
                                syncLoadContent(mList.get(i), false);
                                break;
                            }
                        }
                    } else {//演示同一个文档

                        if (mCurrentShow.getShowPageIndex() != currentPage) {
                            mCurrentShow.setShowPageIndex(currentPage);
                            if (null != mDocView) {
                                mDocView.setDisplayedViewIndex(currentPage);
                            }
                        }
                    }
                }
                break;
            case PullXmlUtils.ZOOM://缩放
                break;
            case PullXmlUtils.DELETE_DOC://关闭文档
                String key = action.getKey();
                if (key != null && key.contains("_")) {
                    mIsFromServerCloseTab = true;
                    String resID3 = key.substring(key.indexOf("_") + 1);
                    deleteDoc(resID3);
                }
                break;
        }
    }

    /**
     * 删除本地文件 .
     *
     * @param resID3
     */
    private void deleteDoc(String resID3) {
        Cog.i(TAG, " deleteDoc :" + resID3);
        int pos = UiOnlineMeetingUtils.getResourceIndex(resID3, mList);

        if (pos > -1) {
            UiOnlineMeetingUtils.remove(resID3, mList);
            if (mList.size() == 0) {
                mTitleTextView.setText(getString(R.string.no_show_document));
                //隐藏
                //释放加载的图片资源.
                cleanPdfCache();
                mContainer.removeAllViews();
            } else {
                if (mList.size() > pos) {
                    //总是加载后一个,如果没有后一个则加载前一个
                    syncLoadContent(mList.get(pos), false);
                } else {
                    syncLoadContent(mList.get(mList.size() - 1), false);
                }
            }

            if (null != mAdapter) mAdapter.setData(mList);
        } else {
            Cog.e(TAG, "invalidate position for close doc in " + pos);
        }
    }


    @Override
    public void onDestroy() {
        cleanPdfCache();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacks(mLoadMeetingShowRunnable);
        super.onDestroy();
    }

    /**
     * 销毁已经存在的pdf 文件
     */
    private void cleanPdfCache() {
        if (mMuPDFCore != null)
            mMuPDFCore.onDestroy();
        mMuPDFCore = null;
        if (mDocView != null) {
            mDocView.applyToChildren(new ReaderView.ViewMapper() {
                public void applyToView(View view) {
                    ((MuPDFView) view).releaseBitmaps();
                }
            });
            mDocView = null;
        }

    }
}

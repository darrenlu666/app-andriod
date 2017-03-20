package com.codyy.erpsportal.perlcourseprep.controllers.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.HttpVideoPlayerActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Courseware;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.tasks.DownloadTask;
import com.codyy.erpsportal.commons.models.tasks.DownloadTask.DownloadListener;
import com.codyy.erpsportal.commons.services.SaveLessonPlanRethinkService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Constants;
import com.codyy.erpsportal.commons.utils.FileOpener;
import com.codyy.erpsportal.commons.utils.PopupWindowUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.widgets.WrapLinearLayoutManager;
import com.codyy.erpsportal.perlcourseprep.models.entities.LessonPlanDetails;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人备课详情
 * Created by gujiajia on 2016/1/19.
 */
public class PersonalLesPrepContentActivity extends AppCompatActivity implements Callback {

    private final static String TAG = "PersonalLesPrepContentActivity";

    private final static String EXTRA_LESSON_PLAN_ID = "com.codyy.erpsportal.controllers.activities.lessonPlanId";

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    TextView mPromptTv;

    @Bind(R.id.btn_courseware)
    Button mCoursewareBtn;

    Button mDownloadBtn;

    Button mOpenBtn;

    ProgressBar mDownloadingPb;

    @Bind(R.id.fl_content)
    FrameLayout mContentFl;

    WebView mContentWv;

    LinearLayout mOfficeNotSupportLl;

    @Bind(R.id.view_stub)
    ViewStub mViewStub;

    private PopupWindow mPopupWindow;

    private RecyclerView mRecyclerView;

    private CoursewareAdapter mCoursewareAdapter;

    private String mLessonPlanId;

    private LessonPlanDetails mLessonPlanDetails;

    private UserInfo mUserInfo;

    private List<Courseware> mCoursewareList;

    private Object mRequestTag = new Object();

    private RequestSender mRequestSender;

    private Handler mHandler;

    private BroadcastReceiver mRethinkUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String lessonPlanId = intent.getStringExtra(SaveLessonPlanRethinkService.EXTRA_LESSON_PLAN_ID);
            String rethinkContent = intent.getStringExtra(SaveLessonPlanRethinkService.EXTRA_CONTENT);
            if (lessonPlanId.equals(mLessonPlanDetails.getLessonPlanId())) {
                mLessonPlanDetails.setRethink(rethinkContent);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_les_prep_content);
        ButterKnife.bind(this);
        mHandler = new Handler(this);
        initPopWindow();

        mLessonPlanId = getIntent().getStringExtra(EXTRA_LESSON_PLAN_ID);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        loadLessonPlan();
    }

    private void loadLessonPlan() {
        mRequestSender = new RequestSender(this);
        Map<String, String> params = new HashMap<>();
        params.put("lessonPlanId", mLessonPlanId);
        params.put("areaId", TextUtils.isEmpty(mUserInfo.getBaseAreaId()) ? "" : mUserInfo.getBaseAreaId());
        params.put("schoolId", TextUtils.isEmpty(mUserInfo.getSchoolId()) ? "" : mUserInfo.getSchoolId());
        params.put("uuid", mUserInfo.getUuid());
        mRequestSender.sendRequest(new RequestData(URLConfig.LESSON_PLAN_DETAILS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadLessonPlan response:", response);
                if ("success".equals(response.optString("result"))) {
                    mLessonPlanDetails = LessonPlanDetails.JSON_PARSER.parse(response.optJSONObject("preparationDetail"));
                    mCoursewareList = Courseware.JSON_PARSER.parseArray(response.optJSONArray("coursewareList"));
                    updateViews();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "onErrorResponse error:", error);
            }
        }, mRequestTag));

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter(SaveLessonPlanRethinkService.ACTION_UPDATE_RETHINK);
        localBroadcastManager.registerReceiver(mRethinkUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.unregisterReceiver(mRethinkUpdateReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private String mPath;

    private void updateViews() {
        if (mTitleBar == null) return;//view为空，说明已被回收，无需更新界面
        mTitleBar.setTitle(mLessonPlanDetails.getLessonPlanName());

        if (mLessonPlanDetails.isOfficeType()) {
//            mContentWv.setVisibility(View.GONE);
//            mOfficeNotSupportLl.setVisibility(View.VISIBLE);
            mViewStub.setLayoutResource(R.layout.ll_office_not_support);
            mOfficeNotSupportLl = (LinearLayout) mViewStub.inflate();
            mPromptTv = (TextView) mOfficeNotSupportLl.findViewById(R.id.tv_prompt);
            mDownloadBtn = (Button) mOfficeNotSupportLl.findViewById(R.id.btn_download);
            mOpenBtn = (Button) mOfficeNotSupportLl.findViewById(R.id.btn_open);
            mDownloadingPb = (ProgressBar) mOfficeNotSupportLl.findViewById(R.id.pb_downloading);
            mOpenBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOpenClick(v);
                }
            });
            mDownloadBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDownloadClick();
                }
            });
            if (getExternalCacheDir() != null) {
                mPath = getExternalCacheDir().getAbsolutePath() + Constants.FOLDED_LESSON_PLAN + mLessonPlanId;
                Cog.d(TAG, "updateViews mPath=", mPath);
                File file = new File(mPath);
                if (file.exists()) {
                    mOpenBtn.setVisibility(View.VISIBLE);
                    mDownloadBtn.setVisibility(View.GONE);
                } else {
                    mOpenBtn.setVisibility(View.GONE);
                    mDownloadBtn.setVisibility(View.VISIBLE);
                }
            } else {
                mOpenBtn.setVisibility(View.GONE);
                mDownloadBtn.setVisibility(View.GONE);
                mPromptTv.setText(R.string.office_not_support_and_no_sdcard);
            }
        } else {
//            mContentWv.setVisibility(View.VISIBLE);
//            mOfficeNotSupportLl.setVisibility(View.GONE);
            mViewStub.setLayoutResource(R.layout.wv_lesson_plan_content);
            mContentWv = (WebView) mViewStub.inflate();
            WebViewUtils.setContentToWebView(mContentWv, mLessonPlanDetails.getRichContent());
        }
        //如果有教案则显示教案按钮
        if (mCoursewareList != null && mCoursewareList.size() > 0) {
            mCoursewareBtn.setVisibility(View.VISIBLE);
            mCoursewareAdapter.setCoursewareList(mCoursewareList);
            mCoursewareAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop(mRequestTag);
        ButterKnife.unbind(this);
    }

    private void initPopWindow() {
        View contentView = getLayoutInflater().inflate(R.layout.pw_recyclerview, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mPopupWindow = new PopupWindow(contentView, 2 * displayMetrics.widthPixels / 3, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        mCoursewareAdapter = new CoursewareAdapter(this);
        mRecyclerView.setAdapter(mCoursewareAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(this));
    }

    @OnClick(R.id.btn_details)
    void onDetailsClick(View view) {
        if (mLessonPlanDetails == null) return;
        LessonPlanDetailsActivity.start(this, mLessonPlanDetails);
    }

    @OnClick(R.id.btn_rethink)
    void onRethinkClick(View view) {
        LessonPlanRethinkActivity.start(this, mUserInfo,mLessonPlanDetails);
    }

    @OnClick(R.id.btn_comments)
    void onCommentsClick(View view) {
        if (mLessonPlanDetails == null) return;
        LessonPlanCommentsActivity.start(this, mUserInfo, mLessonPlanId, mLessonPlanDetails.obtainRate());
    }

    @OnClick(R.id.btn_courseware)
    void onCoursewareClick(View view) {
        if (!mPopupWindow.isShowing()) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.END | Gravity.TOP, 0, mTitleBar.getBottom() + frame.top);
            PopupWindowUtils.dimBehind(mPopupWindow);
        } else {
            mPopupWindow.dismiss();
        }
    }

    private void onOpenClick(View view) {
        FileOpener.openFile(this, new File(mPath));
    }

    private void onDownloadClick() {
        if (getExternalCacheDir() != null) {
            downloadDocument();
        } else {
            Cog.e(TAG, "onDownloadClick no sdcard.");
        }
    }

    /**
     * 获取文档型教案下载地址
     * serverAddress+'/res/view/attachementDownload/' + RESOURCE_SERVER_VALIDATE_CODE + ".html?attachementName=" + id +"&showName="+name;
     *
     * @return
     */
    private String buildDownloadUrl() {
        StringBuilder sb = new StringBuilder(mLessonPlanDetails.getServerAddress());
        sb.append("/res/view/attachementDownload/")
                .append(mUserInfo.getValidateCode())
                .append(".html?attachementName=")
                .append(mLessonPlanDetails.getLessonPlanPath())
                .append("&showName=")
                .append(mLessonPlanDetails.getLessonPlanName());
        return sb.toString();
    }

    /**
     * 获取文档课件下载地址
     * serveraddress/res/view/download/{type}/{validateCode}/{resourceId}.html？showName=
     * type=doc、video（下载）
     *
     * @param courseware
     * @return
     */
    private String buildCoursewareDownloadUrl(Courseware courseware) {
        StringBuilder sb = new StringBuilder(courseware.getServerAddress());
        sb.append("/res/view/download/doc/")
                .append(mUserInfo.getValidateCode())
                .append('/')
                .append(courseware.getServerResourceId())
                .append(".html?")
                .append("showName=")
                .append(courseware.getName());
        return sb.toString();
    }

    /**
     * 下载文档教案
     */
    private void downloadDocument() {
        String resUrl = buildDownloadUrl();
        DownloadTask task = new DownloadTask(getApplicationContext(), new DownloadListener() {
            @Override
            public void onFinishDownload(String result) {
                if (result == null)
                    UIUtils.toast(PersonalLesPrepContentActivity.this, "下载失败", Toast.LENGTH_SHORT);
                else {
                    UIUtils.toast(PersonalLesPrepContentActivity.this, "下载完成", Toast.LENGTH_SHORT);
                }
                onFinishDownloading(result != null);
            }

            @Override
            public void onStartDownload(DownloadTask task) {
                onStartDownloading();
            }

            @Override
            public void onDownloadProgress(int progress) {
            }
        });
        Cog.d(TAG, "downloadDocument docUrl=", resUrl);
        task.execute(resUrl, getExternalCacheDir().getAbsolutePath() + Constants.FOLDED_LESSON_PLAN + mLessonPlanId);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        }
        return false;
    }

    /**
     * 课件被点击
     *
     * @param courseware 课件
     * @param position
     */
    private void onCoursewareItemClick(final Courseware courseware, int position) {
        Cog.d(TAG, "onCoursewareItemClick position=", position);
        if (Courseware.TYPE_VIDEO.equals(courseware.getType())) {
            openVideoCourseware(courseware);
            return;
        }
        if (courseware.isTransformFailed()) {
            ToastUtil.showToast(this, getString(R.string.disable_of_transform_failure), Toast.LENGTH_SHORT);
            return;
        }
        if (getExternalCacheDir() == null) {
            ToastUtil.showToast(this, "没有存储，无法缓存课件！", Toast.LENGTH_SHORT);
            return;
        }
        String fileName = courseware.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            Toast.makeText(this, "无法获取课件文件类型！", Toast.LENGTH_SHORT).show();
            return;
        }
        String suffix = fileName.substring(dotIndex);
        final String path = getExternalCacheDir().getAbsolutePath() + Constants.FOLDED_COURSEWARE + mLessonPlanId + suffix;
        if (courseware.getProgress() < 0) {
            String url = buildCoursewareDownloadUrl(courseware);

            Cog.d(TAG, "onCoursewareItemClick url=", url, ",path=", path);
            popConfirmCousewareOperationDialog(courseware, path, position);
        } else if (courseware.getProgress() == 100) {//下载完成
            popConfirmCousewareOperationDialog(courseware, path, position);
        } else if (courseware.getProgress() >= 0 && courseware.getProgress() < 100) {//正在下载中
            pauseOrResumeDownload(courseware, path, position);
        }
    }

    private void openVideoCourseware(Courseware courseware) {
        Cog.d(TAG, "+openVideoCourseware coursewareName=", courseware.getName());
        String videoUrl = courseware.getServerAddress() + "/res/view/viewVideo/"
                + mUserInfo.getValidateCode() + "/" + courseware.getServerResourceId()
                + "/normal.html";
        //serverAddress + '/res/view/viewVideo/' + RESOURCE_SERVER_VALIDATE_CODE + '/'+id+'/normal.html'
        Cog.d(TAG, "openVideoCourseware videoUrl=", videoUrl);
        HttpVideoPlayerActivity.start(PersonalLesPrepContentActivity.this, courseware.getName(), videoUrl);
    }

    private void pauseOrResumeDownload(Courseware data, String path, int position) {
        DownloadTask downloadTask = mTaskSparseArray.get(position);
        if (downloadTask != null) {//如果有任务，说明是暂停的
            downloadTask.cancel(true);
            mTaskSparseArray.remove(position);
        } else {
            downloadCourseware(data, path, position);
        }
    }

    private void popConfirmCousewareOperationDialog(final Courseware data, final String path, final int position) {
        File file = new File(path);
        OfficeNotSupportDialog officeNotSupportDialog = OfficeNotSupportDialog.newInstance(file.exists());
        officeNotSupportDialog.setListener(new OnDownloadOrOpenClickListener() {
            @Override
            public void onClick(boolean downloaded) {
                Cog.d(TAG, "onClick courseware=", data.getName());
                if (downloaded) {//已下载，开启
                    openCourseware(data, path);
                } else {
                    downloadCourseware(data, path, position);
                }
            }
        });
        officeNotSupportDialog.show(getSupportFragmentManager(), "officeNotSupportDialog");
    }

    /**
     * 开启课件
     *
     * @param data
     * @param path
     */
    private void openCourseware(Courseware data, String path) {
        FileOpener.openFile(this, new File(path));
    }

    private SparseArray<DownloadTask> mTaskSparseArray = new SparseArray<>();

    @Override
    protected void onStop() {
        super.onStop();
        if (mTaskSparseArray != null && mTaskSparseArray.size() > 0) {
            for (int i = 0; i < mTaskSparseArray.size(); i++) {
                mTaskSparseArray.removeAt(i);
            }
        }
    }

    /**
     * 下载课件
     *
     * @param courseware
     * @param path
     */
    private void downloadCourseware(final Courseware courseware, String path, final int position) {
        if (courseware.isTransformFailed()) {
            ToastUtil.showToast(this, "此文档因为转换失败无法下载！", Toast.LENGTH_SHORT);
            return;
        }
        String coursewareDownloadUrl = buildCoursewareDownloadUrl(courseware);
        DownloadTask task = new DownloadTask(getApplicationContext(), new DownloadListener() {
            private DownloadTask mDownloadTask;

            @Override
            public void onFinishDownload(String result) {
                mTaskSparseArray.remove(position);
                mDownloadTask = null;
                if (result == null)
                    UIUtils.toast(PersonalLesPrepContentActivity.this, "下载失败", Toast.LENGTH_SHORT);
                else {
                    courseware.setProgress(100);
                    mCoursewareAdapter.notifyDataSetChanged();
                    UIUtils.toast(PersonalLesPrepContentActivity.this, "下载完成", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onStartDownload(DownloadTask task) {
                mDownloadTask = task;
                mTaskSparseArray.put(position, task);
            }

            @Override
            public void onDownloadProgress(int progress) {
//                Cog.d(TAG, "onDownloadProgress progress=" + progress);
                courseware.setProgress(progress);
                mCoursewareAdapter.notifyDataSetChanged();
            }
        });
        Cog.d(TAG, "downloadCourseware docUrl=", coursewareDownloadUrl);
        task.execute(coursewareDownloadUrl, path);
    }

//    private boolean checkDocumentDownloaded(Courseware courseware) {
//
//    }

    /**
     * 下载文档完成
     *
     * @param successfully 下载成功了吗？
     */
    private void onFinishDownloading(boolean successfully) {
        mDownloadingPb.setVisibility(View.GONE);
        if (successfully) {
            mDownloadBtn.setVisibility(View.GONE);
            mOpenBtn.setVisibility(View.VISIBLE);
        } else {
            mDownloadBtn.setVisibility(View.VISIBLE);
            mOpenBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 开始下载
     */
    private void onStartDownloading() {
        mDownloadBtn.setVisibility(View.GONE);
        mOpenBtn.setVisibility(View.GONE);
        mDownloadingPb.setVisibility(View.VISIBLE);
    }

    public static void start(Context context, String id) {
        Intent intent = new Intent(context, PersonalLesPrepContentActivity.class);
        intent.putExtra(EXTRA_LESSON_PLAN_ID, id);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

    /**
     * 在组件上添加进入个人备课的点击事件
     *
     * @param view 要添加进入监听器的组件
     * @param id   个人备课id
     */
    public static void addEnterListenerOnView(View view, final String id) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                start(v.getContext(), id);
            }
        });
    }

    public static class CoursewareAdapter extends Adapter<CoursewareViewHolder> {

        private List<Courseware> mCoursewareList;

        private LayoutInflater mInflater;

        public CoursewareAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public CoursewareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_courseware, parent, false);
            return new CoursewareViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CoursewareViewHolder holder, int position) {
            holder.setDataToView(mCoursewareList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mCoursewareList == null ? 0 : mCoursewareList.size();
        }

        public void addItem(Courseware courseware) {
            mCoursewareList.add(courseware);
            notifyItemInserted(mCoursewareList.size() - 1);
        }

        public void setCoursewareList(List<Courseware> coursewareList) {
            mCoursewareList = coursewareList;
        }
    }

    public static class CoursewareViewHolder extends RecyclerViewHolder<Courseware> implements OnClickListener {

        private Context mContext;

        private ImageView mCoursewareTypeIv;

        private TextView mCoursewareNameTv;

        private ProgressBar mDownloadPb;

        private Courseware mCourseware;

        private int mPosition;

        public CoursewareViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void mapFromView(View view) {
            mContext = itemView.getContext();
            mCoursewareTypeIv = (ImageView) view.findViewById(R.id.iv_courseware_type);
            mCoursewareNameTv = (TextView) view.findViewById(R.id.tv_courseware_name);
            mDownloadPb = (ProgressBar) view.findViewById(R.id.pb_download);
        }

        @Override
        public void setDataToView(final Courseware courseware, final int position) {
            mCourseware = courseware;
            mPosition = position;
            mCoursewareNameTv.setText(courseware.getName());
            if (Courseware.TYPE_DOCUMENT.equals(courseware.getType())) {
                mCoursewareTypeIv.setImageResource(R.drawable.document_courseware);
            } else {
                mCoursewareTypeIv.setImageResource(R.drawable.media_courseware);
            }
            if (courseware.getProgress() < 0 || courseware.getProgress() >= 100) {
                mDownloadPb.setVisibility(View.GONE);
            } else {
                mDownloadPb.setVisibility(View.VISIBLE);
                mDownloadPb.setProgress(courseware.getProgress());
            }
//            itemView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((PersonalLesPrepContentActivity)v.getContext()).onCoursewareItemClick(courseware, position);
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            ((PersonalLesPrepContentActivity) v.getContext()).onCoursewareItemClick(mCourseware, mPosition);
        }
    }

    public static class OfficeNotSupportDialog extends DialogFragment {

        public final static String ARG_DOWNLOADED = "ARG_DOWNLOADED";

        private OnDownloadOrOpenClickListener mListener;

        private boolean mDownloaded;

        public static OfficeNotSupportDialog newInstance(boolean downloaded) {
            OfficeNotSupportDialog officeNotSupportDialog = new OfficeNotSupportDialog();
            Bundle bundle = new Bundle();
            bundle.putBoolean(ARG_DOWNLOADED, downloaded);
            officeNotSupportDialog.setArguments(bundle);
            return officeNotSupportDialog;
        }

        public OfficeNotSupportDialog() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mDownloaded = getArguments().getBoolean(ARG_DOWNLOADED);
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new Builder(getContext());
//            builder.setMessage("暂不支持在线浏览Office课件");
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_office_not_support, null);
            Button positiveBtn = (Button) view.findViewById(R.id.btn_confirm);
            Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
            String btnText;
            if (mDownloaded) {
                btnText = getString(R.string.view);
            } else {
                btnText = getString(R.string.download);
            }
            positiveBtn.setText(btnText);
            positiveBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(mDownloaded);
                    }
                    dismiss();
                }
            });
            cancelBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
//            builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (mListener != null) {
//                        mListener.onClick( mDownloaded);
//                    }
//                }
//            });
//
//            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setView(view);
            return builder.create();
        }

        public void setListener(OnDownloadOrOpenClickListener listener) {
            this.mListener = listener;
        }
    }

    interface OnDownloadOrOpenClickListener {
        void onClick(boolean downloaded);
    }
}

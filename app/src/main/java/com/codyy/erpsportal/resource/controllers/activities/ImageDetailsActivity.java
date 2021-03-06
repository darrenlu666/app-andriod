package com.codyy.erpsportal.resource.controllers.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.resource.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.resource.controllers.fragments.ResCommentsFragment;
import com.codyy.erpsportal.resource.controllers.fragments.ResourceDetailsFragment;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.erpsportal.resource.utils.CountIncreaser;
import com.codyy.erpsportal.resource.utils.FileUtils;
import com.codyy.erpsportal.resource.utils.ZoomOutAnimator;
import com.codyy.url.URLConfig;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 图片资源详情
 * Created by gujiajia on 2016/7/6
 */
public class ImageDetailsActivity extends FragmentActivity {

    private final static String TAG = "ImageDetailsActivity";

    private final static String EXTRA_USER_INFO = "extra_user_info";

    private final static String EXTRA_RESOURCE_ID = "extra_resource_id";

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(android.R.id.tabhost)
    TabHost mTabHost;

    @Bind(android.R.id.tabs)
    TabWidget mTabWidget;

    @Bind(R.id.dv_image)
    SimpleDraweeView mImageDv;

    @Bind(R.id.view_space)
    View mSpaceView;

    private Uri mImageUri;

    private TabsAdapter mTabsAdapter;

    private UserInfo mUserInfo;

    private String mResourceId;

    private String mClassId;

    private RequestSender mRequestSender;

    private ResourceDetails mResourceDetails;

    private Handler mHandler;

    private boolean mKeyboardOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        ButterKnife.bind(this);
        initAttributes();
        initViews(savedInstanceState);
        loadData();
    }

    private void initAttributes() {
        mHandler = new Handler();
        mRequestSender = new RequestSender(this);
        mUserInfo = getIntent().getParcelableExtra(EXTRA_USER_INFO);
        mResourceId = getIntent().getStringExtra(EXTRA_RESOURCE_ID);
        mClassId = getIntent().getStringExtra(Extra.CLASS_ID);
    }

    private void initViews(Bundle savedInstanceState) {
        mTabHost.setup();
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mTabHost, mViewPager);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt("tab"));
        }

        addResourceDetailsFragment();
        addResourceCommentsFragment();

        addKeyboardVisibleListener();
    }

    /**
     * 添加资源评论碎片页
     */
    private void addResourceCommentsFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ResCommentsFragment.ARG_USER_INFO, mUserInfo);
        bundle.putString(ResCommentsFragment.ARG_RESOURCE_ID, mResourceId);
        mTabsAdapter.addTab( mTabHost
                .newTabSpec("tab_comments")
                .setIndicator(makeTabIndicator(getString(R.string.comment)))
                , ResCommentsFragment.class, bundle);
    }

    /**
     * 添加资源详情碎片页
     */
    private void addResourceDetailsFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ResourceDetailsFragment.ARG_IS_MEDIA, true);//设置为显示视频详情，显示时长而非大小
        if (!TextUtils.isEmpty(mClassId)) {
            bundle.putBoolean(ResourceDetailsFragment.ARG_SHOW_SHARER, true);
        }
        mTabsAdapter.addTab( mTabHost
                .newTabSpec("tab_video_details")
                .setIndicator(makeTabIndicator("资源详情"))
                , ResourceDetailsFragment.class, bundle);
    }

    private ZoomOutAnimator mZoomOutAnimator;

    @OnClick(R.id.dv_image)
    public void onImageClick(final View view) {
        boolean keyboardVisible = mKeyboardOpen;
        Cog.d(TAG, "onImageClick keyboardVisible=", keyboardVisible);
        if (keyboardVisible) {
            closeVirtualKeyboard();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mResourceDetails != null) {
                    if (mZoomOutAnimator == null) {
                        mZoomOutAnimator = new ZoomOutAnimator();
                        mZoomOutAnimator.setAnimatorListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                String url = mResourceDetails.getThumbPath();
                                List<String> urlList = new ArrayList<>(1);
                                urlList.add(url);
                                PicturesActivity.start(ImageDetailsActivity.this, urlList, 0);
                            }
                        });
                    }
                    if (mZoomOutAnimator.isOver()) {
                        mZoomOutAnimator.zoomImageFromThumb(ImageDetailsActivity.this, view);
                        mSpaceView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, keyboardVisible ? 300: 0);
    }

    private void addKeyboardVisibleListener() {
        final View activityRootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);

                int rootViewHeight = activityRootView.getRootView().getHeight();
                int heightDiff = rootViewHeight - (r.bottom - r.top);
                Cog.d(TAG, "heightDiff=", heightDiff);
                if (heightDiff > rootViewHeight / 4 && !mKeyboardOpen) { // if more than 100 pixels, its probably a keyboard...
                    mKeyboardOpen = true;
                } else {
                    mKeyboardOpen = false;
                }
            }
        });
    }

    /**
     * 关闭虚拟键盘
     */
    private void closeVirtualKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mZoomOutAnimator != null) {
            mZoomOutAnimator.goBack(mImageDv, mTitleBar.getBottom());
        }
    }

    /**
     * 创建标签
     *
     * @param title 标题
     * @return 标签组件
     */
    private View makeTabIndicator(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView tabTitleTv = (TextView) view.findViewById(R.id.tab_title);
        tabTitleTv.setText(title);
        return view;
    }

    /**
     * 加载数据
     */
    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("resourceId", mResourceId);
        if (!TextUtils.isEmpty(mClassId)) {
            params.put("baseClassId", mClassId);
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.RESOURCE_DETAILS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if (mTitleBar == null) return;
                String result = response.optString("result");
                if ("success".equals(result)) {
                    JSONObject detailsJsonObject = response.optJSONObject("data");
                    mResourceDetails = ResourceDetails.parseJson(detailsJsonObject);
                    if (mResourceDetails != null) {
                        mTitleBar.setText(mResourceDetails.getResourceName());
                        String thumbUrl = mResourceDetails.getThumbPath();
                        if (TextUtils.isEmpty(thumbUrl)) {
                            mImageDv.setImageURI("");
                        } else {
                            mImageUri = Uri.parse(thumbUrl);
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setUri(mImageUri)
                                    .setAutoPlayAnimations(true)
                                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                                        @Override
                                        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                                            CountIncreaser.increaseViewCount(mRequestSender, mUserInfo.getUuid(), mResourceId);
                                        }
                                    })
                                    .build();
                            mImageDv.setController(controller);
                        }
                    }

                    ResourceDetailsFragment resourceDetailsFragment = (ResourceDetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag(UIUtils.obtainFragmentTag(mViewPager.getId(), mTabsAdapter.getItemId(0)));
                    resourceDetailsFragment.setResourceDetails(mResourceDetails);
                } else if ("error".equals(result)) {
                    String message = response.optString("message");
                    UIUtils.toast(ImageDetailsActivity.this, message, Toast.LENGTH_SHORT);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }));
    }

    @OnClick(R.id.btn_download)
    public void onDownloadClick() {
        if (mImageUri != null) {
            String downloadUrl = mResourceDetails.getDownloadUrl();
            int lastIndexOfDot = downloadUrl.lastIndexOf('.');
            final String suffix = downloadUrl.substring(lastIndexOfDot);
            if (FileDownloadService.hasCached(mUserInfo.getBaseUserId(), mResourceId + suffix)){
                ToastUtil.showToast(EApplication.instance(), R.string.image_cached_already);
                return;
            }
            //增加下载次数
            CountIncreaser.increaseDownloadCount(mRequestSender, mUserInfo.getUuid(), mResourceId);
            final String fileName = mResourceDetails.getId() + suffix;
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            boolean inMemoryCache = imagePipeline.isInBitmapMemoryCache(mImageUri);
            if (!inMemoryCache) {
                DataSource<Boolean> inDiskCacheSource = imagePipeline.isInDiskCache(mImageUri);
                DataSubscriber<Boolean> subscriber = new BaseDataSubscriber<Boolean>() {
                    @Override
                    protected void onNewResultImpl(DataSource<Boolean> dataSource) {
                        if (!dataSource.isFinished()) {
                            return;
                        }
                        Boolean isInCache = dataSource.getResult();
                        Cog.d(TAG, "onDownloadClick isInCache=", isInCache);
                        if (isInCache != null && isInCache) copeToCache(fileName);
                    }

                    @Override
                    protected void onFailureImpl(DataSource<Boolean> dataSource) {
                        Cog.d(TAG, "onFailureImpl dataSource=", dataSource);
                    }
                };
                inDiskCacheSource.subscribe(subscriber, Executors.newSingleThreadExecutor());
            } else {
                copeToCache(fileName);
            }
        } else {
            ToastUtil.showToast(this, R.string.fail_to_obtain_image_details);
        }
    }

    private void copeToCache(final String fileName) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                File imageFile = getCachedImageOnDisk(mImageUri);
                File cacheFile = FileDownloadService.obtainCachedFile(mUserInfo.getBaseUserId(), fileName);
                try {
                    FileUtils.copyFile(imageFile, cacheFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                VideoDownloadUtils.addImageCacheItem(mResourceDetails, mUserInfo.getBaseUserId());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(EApplication.instance(), R.string.cache_successfully);
                    }
                });
            }
        });
    }

    //return file or null
    public static File getCachedImageOnDisk(Uri loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory
                    .getInstance()
                    .getEncodedCacheKey(ImageRequest.fromUri(loadUri), null);
            if (ImagePipelineFactory.getInstance()
                    .getMainFileCache()
                    .hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance()
                        .getMainFileCache()
                        .getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance()
                    .getSmallImageFileCache()
                    .hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance()
                        .getSmallImageFileCache()
                        .getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt("tab", mTabHost.getCurrentTab());
        }
        super.onSaveInstanceState(outState);
    }

    public void onBackClick(View view) {
        DeviceUtils.hideSoftKeyboard(mViewPager);
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop();
        ButterKnife.unbind(this);
    }

    public interface PositionChangeListener {
        void onPositionListener(int position);
    }

    public static void start(Activity activity, UserInfo userInfo, String resourceId) {
        start(activity, userInfo, resourceId, null);
    }

    public static void start(Activity activity, UserInfo userInfo, String resourceId, String classId) {
        Intent intent = new Intent(activity, ImageDetailsActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_RESOURCE_ID, resourceId);
        if (classId != null) {
            intent.putExtra(Extra.CLASS_ID, classId);
        }
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}

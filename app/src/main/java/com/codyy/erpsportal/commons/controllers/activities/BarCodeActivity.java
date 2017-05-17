package com.codyy.erpsportal.commons.controllers.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclingPagerAdapter;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.personal.ShareApp;
import com.codyy.erpsportal.commons.models.personal.ShareParse;
import com.codyy.erpsportal.commons.utils.FileUtils;
import com.codyy.erpsportal.commons.utils.ImageUtils;
import com.codyy.erpsportal.commons.utils.QRCodeUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 分享二维码
 * Created by poe on 10/05/17.
 */

public class BarCodeActivity extends BaseHttpActivity {
    private static final String TAG = "BarCodeActivity";

    @Bind(R.id.header_lin)
    LinearLayout mHeaderLinearLayout;
    @Bind(R.id.save_tv)
    TextView mSaveTv;
    @Bind(R.id.share_tv)
    TextView mShareTv;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.app_tv)
    TextView mAppTv;
    @Bind(R.id.indicator)
    CirclePageIndicator mIndicator;
    private PictureAdapter mAdapter;
    private List<ShareApp> mData;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 0x220;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_bar_code_share;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_MOBILE_SHARE_BAR_CODE;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) throws Exception {
        HashMap<String, String> param = new HashMap<>();
        param.put("applicationId", String.valueOf(4));
        param.put("uuid", mUserInfo.getUuid());
        return param;
    }

    @Override
    public void init() {

        //get data .
        requestData(true);
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) throws Exception {
        if (null == response || mViewPager == null) return;
        ShareParse parse = new Gson().fromJson(response.toString(), ShareParse.class);
        if (null != parse) {
            List<ShareApp> apps = parse.getData();
            if (null != apps) {
                mData = apps;
                mAdapter = new PictureAdapter();
                mViewPager.setAdapter(mAdapter);
                //test viewPager .
                mIndicator.setViewPager(mViewPager);
            }
        }

        if (null != mData && mData.size() > 0) {
            mHeaderLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mHeaderLinearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(Throwable error) throws Exception {
        mHeaderLinearLayout.setVisibility(View.GONE);
    }

    @OnClick({R.id.save_tv, R.id.share_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_tv:
                if (Build.VERSION.SDK_INT >= 23) {
                    verifyStoragePermissions();
                } else {
                    savePicToSystem();
                }

                break;
            case R.id.share_tv:
                shareApp();
                break;
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(BarCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BarCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(mViewPager, "申请SD卡查看权限！", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(BarCodeActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(BarCodeActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            savePicToSystem();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (ActivityCompat.checkSelfPermission(BarCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //do nothing ．．．　ｏｒ
                    savePicToSystem();
                }
                break;
        }
    }

    /**
     * 保存图片到系统相册.
     */
    private void savePicToSystem() {
        Observable.fromCallable(new Callable<String>() {

            @Override
            public String call() throws Exception {
                String filePath = FileUtils.getBarCodePath(mViewPager.getCurrentItem(), BarCodeActivity.this);
                boolean result = ImageUtils.saveImageToGallery(BarCodeActivity.this.getApplicationContext(), BitmapFactory.decodeFile(filePath));
                return result ? filePath : "failed!";
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (TextUtils.isEmpty(s)) {
                            ToastUtil.showSnake("保存失败!", mViewPager);
                        } else {
                            ToastUtil.showSnake("保存成功!", mViewPager);
                        }
                    }
                });
    }

    UMShareAPI mShareAPI = UMShareAPI.get(this);
    private ShareAction mShareAction;

    /**
     * 分享app.
     */
    private void shareApp() {

        if (null == mShareAction) {
            mShareAction = new ShareAction(BarCodeActivity.this);
            //weixin
            if (mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN) && mShareAPI.isInstall(this, SHARE_MEDIA.QQ)) {
                mShareAction.setDisplayList(
                          SHARE_MEDIA.WEIXIN
                        , SHARE_MEDIA.WEIXIN_CIRCLE
                        , SHARE_MEDIA.WEIXIN_FAVORITE
                        , SHARE_MEDIA.QQ
                        , SHARE_MEDIA.QZONE
                );
            } else if (mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
                mShareAction.setDisplayList(
                        SHARE_MEDIA.WEIXIN
                        , SHARE_MEDIA.WEIXIN_CIRCLE
                        , SHARE_MEDIA.WEIXIN_FAVORITE
                        , SHARE_MEDIA.QZONE
                );
            } else if(mShareAPI.isInstall(this, SHARE_MEDIA.QQ)){
                mShareAction.setDisplayList(
                          SHARE_MEDIA.QQ
                        , SHARE_MEDIA.QZONE
                );
            }else{
                mShareAction.setDisplayList(
                         SHARE_MEDIA.QZONE
                );
            }

        }
        mShareAction
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        try{
                            ShareApp shareApp = mData.get(mViewPager.getCurrentItem());
                            String url = shareApp.getAppPhoneUrl();
                            if(mViewPager.getCurrentItem()>0){
                                url = shareApp.getDownload_url();
                            }
                            String title = shareApp.getApplicationName();
                            String content = "让人人都有获得优质教育资源的机会,点击下载"+shareApp.getAppOs()+"客户端";


                            if (SHARE_MEDIA.WEIXIN_CIRCLE == snsPlatform.mPlatform) {
                                String filePath = FileUtils.getBarCodePath(mViewPager.getCurrentItem(), BarCodeActivity.this);
                                File imageFile = new File(filePath);
                                UMImage imageLocal = new UMImage(BarCodeActivity.this, imageFile);
                                new ShareAction(BarCodeActivity.this)
                                        .withText(url)
                                        .withMedia(imageLocal)
                                        .setPlatform(share_media)
                                        .setCallback(mUmShareListener)
                                        .share();
                            } else {
                                UMWeb web = new UMWeb(url);
                                web.setTitle(title);
                                web.setDescription(content);
                                web.setThumb(new UMImage(BarCodeActivity.this, R.mipmap.ic_launcher));
                                new ShareAction(BarCodeActivity.this)
                                        .withText(url)
                                        .withMedia(web)
                                        .setPlatform(share_media)
                                        .setCallback(mUmShareListener)
                                        .share();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).open();
    }

    UMShareListener mUmShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            Toast.makeText(BarCodeActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(BarCodeActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(BarCodeActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    class PictureAdapter extends RecyclingPagerAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_bar_code, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.setData(position);
            return convertView;
        }

        @Override
        public int getCount() {
            return 2;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;

            public ViewHolder(View itemView) {
                if (null == itemView) return;
                imageView = (ImageView) itemView.findViewById(R.id.share_iv);
                textView = (TextView) itemView.findViewById(R.id.app_tv);
            }

            public void setData(final int position) {
                // TODO: 10/05/17 get iamge from api .
                if (position > 0) {
                    textView.setText("iOS");
                } else {
                    textView.setText("Android");
                }

                //生成二维码.
                final String filePath = FileUtils.getBarCodePath(position, BarCodeActivity.this);

                //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://10.1.10.235/Android/ECSP/2017-05-10_15-15-08/ErpsPortal-train-release.apk";
                        if (position > 0) {
                            url = "http://www.baidu.com";
                        }
                        if (null != mData && position < mData.size()) {
                            if (position == 0) {
                                url = mData.get(position).getAppPhoneUrl();
                            } else {
                                url = mData.get(position).getDownload_url();
                            }
                        }
                        File file = new File(filePath);
                        boolean success;
                        if (file != null && file.exists()) {
                            success = true;
                        } else {
                            success = QRCodeUtil.createQRImage(url.trim(), 800, 800,
                                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                                    filePath);
                        }

                        if (success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    /**
     * 启动二维码分享页面.
     *
     * @param act
     * @param userInfo
     */
    public static void start(Activity act, UserInfo userInfo) {
        Intent intent = new Intent(act, BarCodeActivity.class);
        intent.putExtra(Constants.USER_INFO, userInfo);

        act.startActivity(intent);
    }
}


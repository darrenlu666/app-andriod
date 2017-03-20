package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.services.uploadServices.AbstractUploadServiceReceiver;
import com.codyy.erpsportal.commons.services.uploadServices.UploadRequest;
import com.codyy.erpsportal.commons.services.uploadServices.UploadService;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmojiEditText;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.commons.widgets.NoScrollGridView;
import com.codyy.erpsportal.exam.controllers.activities.media.MMSelectorActivity;
import com.codyy.erpsportal.exam.controllers.activities.media.image.MMImageBean;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoImageFilpperDialog2;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoUpVideoDialogFragment;
import com.codyy.erpsportal.weibo.models.entities.WeiBoGroup;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;
import com.codyy.url.URLConfig;
import com.codyy.widgets.AlbumActivity;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class WeiBoNewActivity extends ToolbarActivity {
    /**
     * 插入图片
     */
    public static final int REQUEST_IMAGE = 0x001;
    /**
     * @联系人
     */
    public final static int REQUEST_AT = REQUEST_IMAGE + 1;
    /**
     * 插入视频
     */
    public final static int REQUEST_VIDEO = REQUEST_AT + 1;
    /**
     * 同步圈组
     */
    public final static int REQUEST_SYNC = REQUEST_VIDEO + 1;
    public final static String SEND_NEW_WEIBO_SUCCESS = "send_weibo_success";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.weibo_new_gridview)
    NoScrollGridView mGridView;
    @Bind(R.id.weibo_new_textview)
    EmojiEditText mETConunt;
    @Bind(R.id.weibo_new_rootview)
    LinearLayout mRootView;
    @Bind(R.id.weibo_new_emojiview)
    EmojiView mEmojiView;
    @Bind(R.id.weibo_new_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.weibo_new_video_relative)
    RelativeLayout mVideoRL;
    @Bind(R.id.weibo_new_grouptext)
    TextView mTextViewGroup;
    @Bind(R.id.weibo_new_personal_stub)
    ViewStub mStubPersonal;
    @Bind(R.id.weibo_new_group_stub)
    ViewStub mStubGroup;
    @Bind(R.id.weibo_listitem_video_simpledrawview)
    SimpleDraweeView mVideoBG;
    @Bind(R.id.weibo_listitem_video_play)
    ImageView mVideoPlayIV;
    @Bind(R.id.weibo_new_delete)
    ImageView mVideoDeleteIV;
    /**
     * 是否圈组成员可见
     */
    @Bind(R.id.weibo_new_isshow)
    CheckBox mCheckBox;
    private ArrayList<WeiBoSearchPeople> mAtPeople;
    private ArrayList<PhotoInfo> mImages;
    private ArrayList<WeiBoGroup> mWeiBoGroups;
    private int mImageW;
    private int mImageH;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    public static ArrayList<WeiBoSearchPeople> mMyFriends;
    private int mUpSize;
    private StringBuilder mPhotoName;
    private MMImageBean mMMImageBean;
    private StringBuilder mGroupIds;
    private String mGroupID;
    private int mType;
    private final int IMAGE_SIZE = 1024 * 1024 * 5;
    private String mSendUrl;
    private WeiBoUpVideoDialogFragment mWeiBoUpVideoDialogFragment;
    private Integer mHashTag = this.hashCode();
    private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {
            if (mWeiBoUpVideoDialogFragment != null) {
                mWeiBoUpVideoDialogFragment.upProgress(progress);
            }
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            if (!isFinishing()) {
                mWeiBoUpVideoDialogFragment.dismiss();
                Snackbar.make(mETConunt, "上传失败!", Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
            if (isFinishing()) {
                return;
            }
            if (mImages != null && mImages.size() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(serverResponseMessage);
                    if (jsonObject.optBoolean("result")) {
                        setUpSize();
                        setName(jsonObject.optString("message"));
                        mWeiBoUpVideoDialogFragment.setProgress(mUpSize + 1, mImages.size());
                    } else {
                        Snackbar.make(mETConunt, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).show();
                        mWeiBoUpVideoDialogFragment.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mImages.size() == mUpSize) {
                    mWeiBoUpVideoDialogFragment.dismiss();
                    Snackbar.make(mETConunt, "上传成功!", Snackbar.LENGTH_SHORT).show();
                    sendWeiBo(mETConunt.getText().toString(), mPhotoName.toString(), null);
                }
            } else if (mMMImageBean != null) {
                try {
                    JSONObject object = new JSONObject(serverResponseMessage);
                    if ("true".equals(object.optString("result"))) {
                        Snackbar.make(mETConunt, "上传成功!", Snackbar.LENGTH_SHORT).show();
                        sendWeiBo(mETConunt.getText().toString(), null, object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mWeiBoUpVideoDialogFragment.dismiss();
                    Snackbar.make(mETConunt, "上传失败!", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImages = new ArrayList<>();
        mImageW = UIUtils.dip2px(this, 100);
        mImageH = UIUtils.dip2px(this, 110);
        mGridView.setAdapter(mPhotoAdapter);
        mType = getIntent().getIntExtra(WeiBoActivity.TYPE, WeiBoActivity.TYPE_PERSONAL);
        mGroupID = getIntent().getStringExtra(WeiBoActivity.GROUP_ID);
        mWeiBoUpVideoDialogFragment = WeiBoUpVideoDialogFragment.newInstance();
        mWeiBoUpVideoDialogFragment.setOnCancel(new WeiBoUpVideoDialogFragment.OnCancel() {
            @Override
            public void cancel() {
                mPhotoName = new StringBuilder();
                UploadService.stopCurrentUpload();
            }
        });
        switch (mType) {
            case WeiBoActivity.TYPE_PERSONAL:
                mStubPersonal.inflate();
                mSendUrl = URLConfig.WEIBO_SEND_DYNAMIC;
                break;
            case WeiBoActivity.TYPE_GROUP:
            case WeiBoActivity.TYPE_GROUP_MANAGER:
                mSendUrl = URLConfig.WEIBO_SEND_DYNAMIC_GROUP;
                View view = mStubGroup.inflate();
                mCheckBox.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_wei_bo_new;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mTextView.setText("发微博");
        mAtPeople = new ArrayList<>();
        mPhotoName = new StringBuilder();
        mRequestSender = new RequestSender(this);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mEmojiView.setEditText(mETConunt, 150);
        mETConunt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiView.isShown()) {
                    hideKeyBoard(true, mEmojiView);
                }
            }
        });
        mETConunt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        mGroupIds = new StringBuilder("");
        mWeiBoGroups = new ArrayList<>();
        mTextViewGroup.setVisibility(View.GONE);
    }

    public static void start(Activity context, int requestcode) {
        Intent intent = new Intent(context, WeiBoNewActivity.class);
        context.startActivityForResult(intent, requestcode);
        UIUtils.addEnterAnim(context);
    }

    public static void start(FragmentActivity context, int requestcode) {
        Intent intent = new Intent(context, WeiBoNewActivity.class);
        context.startActivityForResult(intent, requestcode);
        UIUtils.addEnterAnim(context);
    }

    private synchronized void setUpSize() {
        mUpSize++;
    }

    private synchronized void setName(String name) {
        mPhotoName.append(name).append(",");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weibo_new, menu);
        TextView textView = (TextView) menu.findItem(R.id.weibo_new_send).getActionView();
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWeiBo();
            }
        });
        return true;
    }

    private void sendWeiBo(String content, String photoname, String mediaName) {

        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("weiBoContent", content);
        switch (mType) {
            case WeiBoActivity.TYPE_PERSONAL:
                parm.put("originalFlag", "Y");
                parm.put("groupIdList", mGroupIds.toString());
                StringBuilder name = new StringBuilder("");
                for (WeiBoSearchPeople weiBoSearchPeople : mAtPeople) {
                    name.append(weiBoSearchPeople.getRealName() + ",");
                }
                parm.put("realNameStr", name.toString());
                break;
            case WeiBoActivity.TYPE_GROUP:
            case WeiBoActivity.TYPE_GROUP_MANAGER:
                parm.put("groupId", mGroupID);
                if (mCheckBox.isChecked()) {
                    parm.put("onlyGroupViewFlag", "Y");
                } else {
                    parm.put("onlyGroupViewFlag", "N");
                }
                break;
        }
        if (photoname != null) {
            parm.put("imgInfo", mPhotoName.toString());
            parm.put("mediaType", "pic");
        }
        if (mediaName != null) {
            parm.put("mediaName", mediaName);
            parm.put("mediaType", "mp4");
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(mSendUrl, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                if ("success".equals(response.optString("result"))) {
                    JSONObject object = response.optJSONObject("miblogBlog");
                    WeiBoListInfo weiBoListInfo = WeiBoListInfo.getWeiBo(object, false);
                    JSONArray jsonArray = response.optJSONArray("imageList");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        ArrayList<WeiBoListInfo.ImageListEntity> imageListEntities = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            imageListEntities.add(WeiBoListInfo.ImageListEntity.getImage(jsonArray.optJSONObject(j)));
                        }
                        weiBoListInfo.setImageList(imageListEntities);
                    }
                    Intent intent = new Intent();
                    intent.putExtra(SEND_NEW_WEIBO_SUCCESS, weiBoListInfo);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (isFinishing()) {
                    return;
                }
                Snackbar.make(mRootView, "发送失败！", Snackbar.LENGTH_SHORT).show();
            }
        }, false, mHashTag));
    }

    private void sendWeiBo() {
        if (TextUtils.isEmpty(mETConunt.getText())) {
            Snackbar.make(mETConunt, "微博内容不能为空！", Snackbar.LENGTH_SHORT).show();
        } else if (mImages != null && mImages.size() > 0) {
            UploadService.stopCurrentUpload();
            mUpSize = 0;
            //SERVER_ADDRESS+"/res/"+RESOURCE_SERVER_AREA_CODE+"/imageUpload.do?validateCode="+RESOURCE_SERVER_VALIDATE_CODE
            String url = mUserInfo.getServerAddress() + "/res/" + mUserInfo.getAreaCode() + "/imageUpload.do?validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=5";
            mWeiBoUpVideoDialogFragment.setProgress(1, mImages.size());
            mWeiBoUpVideoDialogFragment.show(getSupportFragmentManager(), "mLoadingDialog");
            for (int i = 0; i < mImages.size(); i++) {
                onUploadButtonClick(mImages.get(i).getPath(), mImages.get(i).getName(), mImages.get(i).getName(), url);
            }
        } else if (mMMImageBean != null) {
            mUpSize = 0;
            UploadService.stopCurrentUpload();
            //SERVER_ADDRESSRESOURCE_SERVER_VALIDATE_CODE
//            mLoadingDialog.show(getSupportFragmentManager(), "mLoadingDialog");
            mWeiBoUpVideoDialogFragment.setProgress(1, 1);
            mWeiBoUpVideoDialogFragment.show(getSupportFragmentManager(), "mLoadingDialog");
            String url = mUserInfo.getServerAddress() + "/res/mix/" + mUserInfo.getAreaCode() + "/upload.do?printscreen=Y&printscreenType=auto&sourceType=miblog_blog&validateCode=" + mUserInfo.getValidateCode();
            onUploadButtonClick(mMMImageBean.getPath(), mMMImageBean.getPath(), mMMImageBean.getPath(), url);
        } else {
            sendWeiBo(mETConunt.getText().toString(), null, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onBack() {
        super.onBack();

    }

    private BaseAdapter mPhotoAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (mImages != null) {
                if (mImages.size() < 9) {
                    return mImages.size() + 1;
                } else {
                    return mImages.size();
                }
            }
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (mImages.size() > 0) {
                if (mImages.size() < 9 && position == getCount() - 1) {
                    ImageView imageView = new ImageView(WeiBoNewActivity.this);
                    imageView.setLayoutParams(new AbsListView.LayoutParams(mImageW, mImageH));
                    imageView.setImageResource(R.drawable.weibo_new_photo);
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPhoto(v);
                        }
                    });
                    return imageView;
                } else {
                    View view = getLayoutInflater().inflate(R.layout.weibo_new_photo, parent, false);
                    SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.weibo_new_simpledrawee);
                    ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(mImages.get(position).getContent());
                    imageRequestBuilder.setResizeOptions(new ResizeOptions(
                            mImageW,
                            mImageH));
                    simpleDraweeView.setImageURI(mImages.get(position).getContent());
                    simpleDraweeView.setLayoutParams(new RelativeLayout.LayoutParams(mImageW, mImageH));
                    ImageRequest imageRequest = imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true).build();
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(simpleDraweeView.getController())
                            .setAutoPlayAnimations(true)
                            .build();
                    simpleDraweeView.setController(draweeController);
                    simpleDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int index = mImages.get(position).getmPosition();
                            for (PhotoInfo info : mImages) {
                                if (info.getmPosition() > index) {
                                    info.setmPosition(info.getmPosition() - 1);
                                }
                            }
                            mImages.remove(position);
                            notifyDataSetChanged();
                            return true;
                        }
                    });
                    simpleDraweeView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WeiBoImageFilpperDialog2 dialog = WeiBoImageFilpperDialog2.newInstance(mImages, position);
                            dialog.show(getSupportFragmentManager(), "WeiBoImageFilpperDialog----");
                        }
                    });
                    return view;
                }
            } else {
                ImageView imageView = new ImageView(WeiBoNewActivity.this);
                imageView.setLayoutParams(new AbsListView.LayoutParams(mImageW, mImageH));
                imageView.setImageResource(R.drawable.weibo_new_photo);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openPhoto(v);
                    }
                });
                return imageView;
            }
        }
    };

    /**
     * 表情
     *
     * @param view
     */
    public void sendEmoji(View view) {
        if (mEmojiView.isShown()) {
            hideKeyBoard(true, mEmojiView);
        } else {
            showKeyBoard(this, mEmojiView, mETConunt);
        }
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    /**
     * @param view
     * @
     */
    public void sendAt(View view) {
        Intent intent = new Intent(this, WeiBoMyFriendActivity.class);
        intent.putExtra(WeiBoMyFriendActivity.TYPE_STYLE, WeiBoMyFriendActivity.TYPE_MY_FRIEND);
        startActivityForResult(intent, REQUEST_AT);
        UIUtils.addEnterAnim(this);
    }

    /**
     * 打开相册
     *
     * @param view
     */
    public void openPhoto(View view) {
        Intent intent = new Intent(this, AlbumActivity.class);
        if (mImages != null && mImages.size() > 0) {
            intent.putParcelableArrayListExtra(AlbumActivity.SET_SELECT_INFO, mImages);
        }
        intent.putExtra(AlbumActivity.IMAGE_MAX_SIZE, IMAGE_SIZE);
        intent.putExtra(AlbumActivity.SET_MAX_SELECT, 9);
        startActivityForResult(intent, REQUEST_IMAGE);
        UIUtils.addEnterAnim(this);
    }

    /**
     * 插入video
     *
     * @param view
     */
    public void openVideo(View view) {
        Intent intent = new Intent(this, MMSelectorActivity.class);
        intent.putExtra("EXTRA_TYPE", "VIDEO");
        intent.putExtra("EXTRA_SIZE", 50);//50M
        startActivityForResult(intent, REQUEST_VIDEO);
        UIUtils.addEnterAnim(this);
    }

    /**
     * 同步圈组
     *
     * @param view
     */
    public void synchronization(View view) {
        Intent intent = new Intent(this, WeiBoMyFriendActivity.class);
        intent.putParcelableArrayListExtra(WeiBoMyFriendActivity.RESULT_DATA_GROUP, mWeiBoGroups);
        intent.putExtra(WeiBoMyFriendActivity.TYPE_STYLE, WeiBoMyFriendActivity.TYPE_MY_GROUP);
        startActivityForResult(intent, REQUEST_SYNC);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mEmojiView.isShown()) {
            hideKeyBoard(false, mEmojiView);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_AT:
                if (data != null) {
                    WeiBoSearchPeople weiBoSearchPeople = data.getParcelableExtra(WeiBoMyFriendActivity.RESULT_DATA);
                    if (weiBoSearchPeople != null) {
                        String str = "@" + weiBoSearchPeople.getRealName() + " ";
                        if (str.length() > (150 - mETConunt.length())) {
                            Snackbar.make(mETConunt, "", Snackbar.LENGTH_SHORT).show();
                        } else {
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.weibo_at)), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mETConunt.getText().insert(mETConunt.getSelectionStart(), spannableStringBuilder);
                            mAtPeople.add(weiBoSearchPeople);
                        }
                    }
                }
                break;
            case REQUEST_IMAGE:
                if (resultCode == AlbumActivity.RESULT_SUCCESS) {
                    if (data != null) {
                        ArrayList<PhotoInfo> arrayList = data.getParcelableArrayListExtra(AlbumActivity.RESULT_SELECT_INFO);
                        mImages.clear();
                        mImages.addAll(arrayList);
                        mGridView.setVisibility(View.VISIBLE);
                        mVideoRL.setVisibility(View.GONE);
                        mPhotoAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_SYNC:
                if (resultCode == RESULT_OK) {
                    ArrayList<WeiBoGroup> weiBoGroups = data.getParcelableArrayListExtra(WeiBoMyFriendActivity.RESULT_DATA_GROUP);
                    if (weiBoGroups != null) {
                        if (weiBoGroups.size() > 0) {
                            mTextViewGroup.setVisibility(View.VISIBLE);
                            mWeiBoGroups.clear();
                            mWeiBoGroups.addAll(weiBoGroups);
                            mGroupIds = new StringBuilder("");
                            for (int i = 0; i < weiBoGroups.size(); i++) {
                                mGroupIds.append(weiBoGroups.get(i).getGroupId()).append(",");
                                if (i == 0) {
                                    mTextViewGroup.setText("同步到圈组： " + weiBoGroups.get(i).getGroupName());
                                    continue;
                                }
                                mTextViewGroup.setText(mTextViewGroup.getText() + " , " + weiBoGroups.get(i).getGroupName());
                            }
                        } else {
                            mTextViewGroup.setVisibility(View.GONE);
                            mWeiBoGroups.clear();
                        }
                    }
                }
                break;
            case REQUEST_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<MMImageBean> imageBeen = data.getParcelableArrayListExtra(ToolbarActivity.EXTRA_DATA);
                    if (imageBeen != null && imageBeen.size() > 0) {
                        mMMImageBean = imageBeen.get(0);
                        mImages.clear();
                        mPhotoAdapter.notifyDataSetChanged();
                        mGridView.setVisibility(View.GONE);
                        mVideoRL.setVisibility(View.VISIBLE);
                        mVideoDeleteIV.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMMImageBean = null;
                                mGridView.setVisibility(View.VISIBLE);
                                mVideoRL.setVisibility(View.GONE);
                            }
                        });
                        mVideoBG.setImageURI(Uri.parse("file://" + mMMImageBean.getPath()));
                        mVideoPlayIV.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WeiBoVideoActivity.start(WeiBoNewActivity.this, mMMImageBean.getPath());
                            }
                        });
                    }
                }
                break;
        }
    }

    private void onUploadButtonClick(String path, String parameterName, String fileName, String url) {
        final UploadRequest request = new UploadRequest(this, path, url);
        request.addFileToUpload(path, parameterName, fileName, "multipart/form-data");
        request.setNotificationConfig(R.mipmap.ic_launcher, getString(R.string.app_name), "上传", "成功", "错误", false);
        request.setCustomUserAgent("UploadServiceDemo/1.0");
        request.setNotificationClickIntent(null);
        request.setMaxRetries(2);
        try {
            UploadService.startUpload(request);
        } catch (Exception exc) {
            Toast.makeText(this, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        UploadService.stopCurrentUpload();
        if (mMyFriends != null) {
            mMyFriends.clear();
            mMyFriends = null;
        }
        super.onDestroy();
    }

    public void showKeyBoard(Activity activity, EmojiView emojiView, EditText editText) {
        int emotionHeight = InputUtils.getKeyboardHeight(activity);
        InputUtils.hideSoftInputFromWindow(activity, editText);
        emojiView.getLayoutParams().height = emotionHeight;
        emojiView.setVisibility(View.VISIBLE);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        在5.0有navigationbar的手机，高度高了一个statusBar
        int lockHeight = InputUtils.getAppContentHeight(activity);
//            lockHeight = lockHeight - statusBarHeight;
        lockContainerHeight(lockHeight);
//        mScrollView.scrollTo(0, 0);
    }

    private void lockContainerHeight(int paramInt) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        localLayoutParams.height = paramInt;
        localLayoutParams.weight = 0.0F;
    }

    public void hideKeyBoard(boolean showKeyBoard, EmojiView emojiView) {
        if (showKeyBoard) {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
            localLayoutParams.height = mEmojiView.getTop() - mToolbar.getHeight();
            localLayoutParams.weight = 0.0F;
            emojiView.setVisibility(View.GONE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            emojiView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    unlockContainerHeightDelayed();
                }
            }, 200L);
            InputUtils.showSoftInputFromWindow(this, mETConunt);
        } else {
            emojiView.setVisibility(View.GONE);
            InputUtils.hideSoftInputFromWindow(this, mETConunt);
            unlockContainerHeightDelayed();
        }
    }

    public void unlockContainerHeightDelayed() {
        ((LinearLayout.LayoutParams) mRootView.getLayoutParams()).weight = 1.0F;
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}

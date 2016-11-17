package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.services.uploadServices.AbstractUploadServiceReceiver;
import com.codyy.erpsportal.commons.services.uploadServices.UploadRequest;
import com.codyy.erpsportal.commons.services.uploadServices.UploadService;
import com.codyy.erpsportal.homework.implementclass.AudioRecorder;
import com.codyy.erpsportal.homework.widgets.PressBar;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoPrivateMsgAdapter;
import com.codyy.erpsportal.weibo.models.entities.WeiBoPrivateMessage;
import com.codyy.erpsportal.commons.widgets.EmojiEditText;
import com.codyy.erpsportal.commons.widgets.EmojiView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class WeiBoPrivateMessageActivity extends ToolbarActivity {
    private Integer mHashTag = this.hashCode();
    public static final String MSG_USERID = "user_id";
    public static final String MSG_NAME = "user_name";
    public static final int FIRST_LOADING = 0x001;
    public static final int LOAD_MORE = 0x002;
    /**
     * 发送私信
     */
    public static final int SEND_MESSAGE = 0x003;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.private_msg_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.edittext_send_private_msg)
    EmojiEditText mEditText;
    @Bind(R.id.private_rootview)
    LinearLayout mRootView;
    @Bind(R.id.inputviewlayout)
    LinearLayout mInputLayout;
    @Bind(R.id.private_emojiview)
    EmojiView mEmojiView;
    @Bind(R.id.pressbar)
    PressBar mPressBar;
    @Bind(R.id.send_image)
    ImageView mSendIV;
    private RequestSender mRequestSender;
    private String mTargetUserId;
    private String mUserName;
    private UserInfo mUserInfo;
    private final int mSize = 10;
    private WeiBoPrivateMsgAdapter mMsgAdapter;
    private ArrayList<WeiBoPrivateMessage> mMessages;

    private boolean mSendable;
    private boolean mRefreshing;
    /**
     * 本人头像
     */
    private String mSpeakPic;
    /**
     * 聊天对象头像
     */
    private String mTargetUserHeadPic;
    private LinearLayoutManager mManager;
    /**
     * 上传监听
     */
    private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {

        }

        @Override
        public void onError(String uploadId, Exception exception) {
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
            /**
             * {"result":true,"originalName":"record_audio.aac","code":0,"message":"9a9df294c50346e588f9e07d5e85c349","isVideo":false}
             */
            try {
                JSONObject object = new JSONObject(serverResponseMessage);
                Map<String, String> parm = new HashMap<>();
                parm.put("uuid", mUserInfo.getUuid());
                parm.put("targetUserId", mTargetUserId);
                parm.put("longStrCreateTime", String.valueOf(System.currentTimeMillis()));
                parm.put("audioPath", object.optString("message"));
                parm.put("audioDuration", String.valueOf(3000));
                httpConnect(URLConfig.SEND_MESSAGE, parm, SEND_MESSAGE + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(this);
        getMessage(-1);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_wei_bo_private_message;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mRefreshing = false;
        mSendable = true;
        mMessages = new ArrayList<>();
        mEmojiView.setEditText(mEditText, 150);
        mMsgAdapter = new WeiBoPrivateMsgAdapter(this, mMessages);
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mMsgAdapter);
        mTargetUserId = getIntent().getStringExtra(MSG_USERID);
        mUserName = getIntent().getStringExtra(MSG_NAME);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mTextView.setText(mUserName);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && scrollToTop()) {
                    if (!mRefreshing) {
                        if (mMessages.size() > 0) {
                            WeiBoPrivateMessage message = mMessages.get(mMessages.size() - 1);
                            if (message.isHasMoreMiBlogMessage()) {
                                mMsgAdapter.setRefreshType(WeiBoPrivateMsgAdapter.LOADING);
                                getMessage(message.getCreateTime());
                            } else {
                                mMsgAdapter.setRefreshType(WeiBoPrivateMsgAdapter.NO_MORE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEditText.hasFocus()) {
//                    mEmojiView.setVisibility(View.GONE);
                    hideKeyBoard(false, mEmojiView);
                    InputUtils.hideSoftInputFromWindow(WeiBoPrivateMessageActivity.this, mEditText);
                    mEditText.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            unlockContainerHeightDelayed();
                        }

                    }, 200L);
                    return true;
                }
                if (mEmojiView.isShown()) {
                    hideKeyBoard(false, mEmojiView);
                }
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mEmojiView.isShown()) {
                    hideKeyBoard(true, mEmojiView);
                }
            }
        });
//        mEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(s)) {
//                    mSendIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_send));
//                } else {
//                    mSendIV.setImageDrawable(getResources().getDrawable(R.drawable.weibo_image));
//                }
//            }
//        });
        mPressBar.setAudioRecorder(new AudioRecorder(this));
        mPressBar.setRecordListener(new PressBar.RecordListener() {
            @Override
            public void RecordEnd(String recordFilePath, int duration) {
                onUploadButtonClick(recordFilePath, "hahahahahah", "jajajajja", "");
            }
        });
    }

    /**
     * 发送私信
     *
     * @param view uuid={uuid}&targetUserId={targetUserId}&messageContent={messageContent}&createTime={createTime}
     */
    public void sendMessage(View view) {
        Editable editable = mEditText.getText();
        if (!TextUtils.isEmpty(editable) && mSendable) {
            mSendable = false;
            Map<String, String> parm = new HashMap<>();
            parm.put("uuid", mUserInfo.getUuid());
            parm.put("targetUserId", mTargetUserId);
            if (mMessages != null && mMessages.size() > 0) {
                parm.put("longStrCreateTime", String.valueOf(mMessages.get(0).getCreateTime()));
            }
            parm.put("messageContent", editable.toString());
            httpConnect(URLConfig.SEND_MESSAGE, parm, SEND_MESSAGE);
        } else {
            Snackbar.make(view, "消息不能为空!", Snackbar.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, AlbumActivity.class);
//            intent.putExtra(AlbumActivity.IMAGE_MAX_SIZE, 1024 * 1024 * 5);
//            intent.putExtra(AlbumActivity.SET_MAX_SELECT, 8);
//            startActivityForResult(intent, 2);
        }
    }

    public void sendEmoji(View view) {
        if (mEmojiView.getVisibility() == View.GONE) {
//            mEditText.setVisibility(View.VISIBLE);
//            mPressBar.setVisibility(View.GONE);
//            ((ImageView) findViewById(R.id.send_voice)).setImageDrawable(getResources().getDrawable(R.drawable.weibo_voice));
            showKeyBoard(this, mEmojiView, mEditText);
        } else {
            hideKeyBoard(true, mEmojiView);
        }
    }

    private boolean scrollToTop() {
        return mRecyclerView.canScrollVertically(1) && !mRecyclerView.canScrollVertically(-1);
    }

    private void getMessage(long date) {
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("targetUserId", mTargetUserId);
        parm.put("size", String.valueOf(mSize));
        if (date < 0) {
            parm.put("createTime", "");
            httpConnect(URLConfig.GET_MIBLOG_MESSAGELIST, parm, FIRST_LOADING);
        } else {
            parm.put("createTime", String.valueOf(date));
            httpConnect(URLConfig.GET_MIBLOG_MESSAGELIST, parm, LOAD_MORE);
        }
    }

    private void httpConnect(String url, Map<String, String> parm, final int msg) {
        mRefreshing = true;
        mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                mRefreshing = false;
                switch (msg) {
                    case FIRST_LOADING:
                        mMessages.clear();
                        if ("success".equals(response.optString("result"))) {
                            mSpeakPic = response.optString("speakerUserHeadPic");
                            if (TextUtils.isEmpty(mSpeakPic)) {
                                mSpeakPic = mUserInfo.getHeadPic();
                            }
                            mTargetUserHeadPic = response.optString("targetUserHeadPic");
                        }
                        mMessages.addAll(WeiBoPrivateMessage.getMessageList(response));
                        mMsgAdapter.notifyDataSetChanged();
                        break;
                    case LOAD_MORE:
                        int start = mMessages.size();
                        mMessages.addAll(WeiBoPrivateMessage.getMessageList(response));
                        mMsgAdapter.notifyItemRangeChanged(start - 1, mMessages.size() - start);
                        break;
                    case SEND_MESSAGE:
                        mSendable = true;
                        ArrayList<WeiBoPrivateMessage> message = WeiBoPrivateMessage.getSendMessage(response, mSpeakPic, mTargetUserHeadPic);
                        if (message != null) {
                            mMessages.addAll(0, message);
                            mMsgAdapter.notifyItemRangeInserted(0, message.size());
                            mRecyclerView.scrollToPosition(0);
                            mEditText.setText("");
                        } else {
                            Snackbar.make(mRecyclerView, "发送失败！", Snackbar.LENGTH_SHORT).show();
                            mEditText.setText("");
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefreshing = false;
                mSendable = true;
            }
        }, mHashTag));
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }

    public static void start(Context context, String userid, String name) {
        Intent intent = new Intent(context, WeiBoPrivateMessageActivity.class);
        intent.putExtra(MSG_USERID, userid);
        intent.putExtra(MSG_NAME, name);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weibo_new, menu);
        TextView textView = (TextView) menu.findItem(R.id.weibo_new_send).getActionView();
        textView.setText("刷新");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessage(-1);
                mManager.scrollToPosition(0);
            }
        });
        return true;
    }

    /**
     * 发送语音
     *
     * @param view
     */
    public void sendAudio(View view) {
        if (mEmojiView.getVisibility() == View.VISIBLE) {
            hideKeyBoard(false, mEmojiView);
        }
        if (mEditText.getVisibility() == View.VISIBLE) {
            InputUtils.hideSoftInputFromWindow(this, mEditText);
            mEditText.setVisibility(View.GONE);
            mPressBar.setVisibility(View.VISIBLE);
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.weibo_keyboard));
        } else {
            InputUtils.showSoftInputFromWindow(this, mEditText);
            mEditText.setVisibility(View.VISIBLE);
            mPressBar.setVisibility(View.GONE);
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.weibo_voice));
        }

    }

    public void inputMsg(View v) {

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
            InputUtils.showSoftInputFromWindow(this, mEditText);
        } else {
            emojiView.setVisibility(View.GONE);
            InputUtils.hideSoftInputFromWindow(this, mEditText);
            unlockContainerHeightDelayed();
        }
    }

    private void onUploadButtonClick(String path, String parameterName, String fileName, String url) {
        // TODO: 16-6-6 修改上传路径和url
        String path1 = Environment.getExternalStorageDirectory().getPath() + "/test_kmdai.aac";
        String url1 = mUserInfo.getServerAddress() + "/res/mix/" + mUserInfo.getAreaCode() + "/upload.do?printscreen=Y&printscreenType=auto&sourceType=miblog_message&validateCode=" + mUserInfo.getValidateCode();
        final UploadRequest request = new UploadRequest(this, path1, url1);
        request.addFileToUpload(path, path, "record_audio.aac", "multipart/form-data");
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

    public void unlockContainerHeightDelayed() {
        ((LinearLayout.LayoutParams) mRootView.getLayoutParams()).weight = 1.0F;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEmojiView.isShown()) {
                hideKeyBoard(false, mEmojiView);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}

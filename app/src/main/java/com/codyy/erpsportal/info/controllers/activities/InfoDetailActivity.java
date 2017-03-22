package com.codyy.erpsportal.info.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 资讯详情
 * Created by kmdai on 2015/8/18.
 */
public class InfoDetailActivity extends AppCompatActivity {

    private static final String TAG = "InfoDetailActivity";

    private static final String EXTRA_INFO_ID = "info_id";

    private static final String EXTRA_FROM = "from";

    /**
     * 从频道页进入
     */
    private static final int FROM_CHANNEL = 0;

    /**
     * 从应用页进入
     */
    private static final int FROM_FUNCTION = 1;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.wv_info_content)
    WebView mContentWv;

    private WebApi mWebApi;

    private String mInformation;

    /**
     * 记录从哪里进入资讯详情，以显示不同的配置的资讯分类标题
     */
    private int mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);
        ButterKnife.bind(this);
        WebSettings webSettings = mContentWv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mContentWv.addJavascriptInterface(this, "android");
        mInformation = getIntent().getStringExtra(EXTRA_INFO_ID);
        mFrom = getIntent().getIntExtra(EXTRA_FROM, FROM_FUNCTION);
        mWebApi = RsGenerator.create(WebApi.class);
        getInfo();
    }

    @JavascriptInterface
    public void toast(String str) {
        UIUtils.toast(this, str, Toast.LENGTH_SHORT);
    }

    /**
     * 获取新闻详情
     */
    private void getInfo() {
        HashMap<String, String> data = new HashMap<>();
        data.put("informationId", mInformation);
        Cog.d(TAG, "getInfo url=", URLConfig.INFORMATION_DETAIL, data);
        mWebApi.post4Json(URLConfig.INFORMATION_DETAIL, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "getInfo response=" + response);
                        if ("success".equals(response.optString("result"))) {
                            JSONObject object = response.optJSONObject("information");
                            if (object != null) {
                                if (Info.TYPE_NEWS.equals(object.optString("infoType"))) {
                                    if (mFrom == FROM_FUNCTION)
                                        mTitleBar.setTitle(Titles.sWorkspaceInfoNew);
                                    else
                                        mTitleBar.setTitle(Titles.sPagetitleIndexInfoNew);
                                } else if (Info.TYPE_NOTICE.equals(object.optString("infoType"))) {
                                    if (mFrom == FROM_FUNCTION)
                                        mTitleBar.setTitle(Titles.sWorkspaceNoticeAnnouncementNotice);
                                    else
                                        mTitleBar.setTitle(Titles.sPagetitleIndexInfoNotice);
                                } else {
                                    if (mFrom == FROM_FUNCTION)
                                        mTitleBar.setTitle(Titles.sWorkspaceNoticeAnnouncementAnnouncement);
                                    else
                                        mTitleBar.setTitle(Titles.sPagetitleIndexInfoAnnouncement);
                                }
                                String content = object.optString("richContent");
                                String url = URLConfig.GET_NEW_DELTAIL + object.optString("informationId") + ".html";
                                Cog.d(TAG, "information detail url=", url);
//                        if (TextUtils.isEmpty(content)) {
                                mContentWv.loadUrl(url);
//                        } else {
//                            WebViewUtils.setContentToWebView(mContentWv, content);
//                        }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                    }
                });
    }

    @IntDef(value={FROM_CHANNEL,FROM_FUNCTION})
    @Retention(RetentionPolicy.SOURCE)
    @interface infoFrom{};

    public static void start(Context context, String informationId,@infoFrom int from) {
        Intent intent = new Intent(context, InfoDetailActivity.class);
        intent.putExtra(EXTRA_INFO_ID, informationId);
        intent.putExtra(EXTRA_FROM, from);
        context.startActivity(intent);
    }

    /**
     * 从频道页进资讯详情
     * @param context 上下文
     * @param informationId 资讯id
     */
    public static void startFromChannel(Context context, String informationId) {
        start(context, informationId, FROM_CHANNEL);
    }

    /**
     * 从应用页进资讯详情
     * @param context 上下文
     * @param informationId 资讯id
     */
    public static void startFromFunction(Context context, String informationId) {
        start(context, informationId, FROM_FUNCTION);
    }
}

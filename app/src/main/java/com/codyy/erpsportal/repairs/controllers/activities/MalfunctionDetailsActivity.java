package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.HtmlUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.codyy.erpsportal.rethink.controllers.activities.SubjectMaterialPicturesActivity;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 常见问题详情
 */
public class MalfunctionDetailsActivity extends AppCompatActivity {

    private final static String TAG = "MalfunctionDetailsActivity";

    private final static String EXTRA_MALFUNCTION_ID = "com.codyy.erpsportal.EXTRA_MALFUNCTION_ID";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.wv_content)
    WebView mContentWv;

    private UserInfo mUserInfo;

    private String mMalfunctionId;

    private RequestSender mSender;

    private ImagesInteraction mImagesInteraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malfunction_details);
        ButterKnife.bind(this);
        mImagesInteraction = new ImagesInteraction(this);
        mContentWv.getSettings().setDefaultTextEncodingName("utf-8");
        mContentWv.getSettings().setJavaScriptEnabled(true);
        mContentWv.addJavascriptInterface(mImagesInteraction,"control");

        mSender = new RequestSender(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mMalfunctionId = getIntent().getStringExtra(EXTRA_MALFUNCTION_ID);

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("malGuideId", mMalfunctionId);
        mSender.sendRequest(new RequestData(URLConfig.GET_MALFUNCTION_DETAILS, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "onResponse response=", response);
                        JSONObject jsonObject = response.optJSONObject("data");
                        if (jsonObject != null) {
                            mTitleTv.setText(jsonObject.optString("summary"));
                            String contentHtml = jsonObject.optString("description");
                            mImagesInteraction.searchImageUrls( contentHtml);
                            contentHtml = contentHtml.replace("<pre>", "").replace("</pre>", "");
                            mImagesInteraction.setHtmlToWebView( contentHtml);
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.e(TAG, "onErrorResponse error=", error.getMessage());
                    }
                }
        ));
    }

    public class ImagesInteraction {

        private WeakReference<Activity> mActRef;

        private List<String> mImageList = new ArrayList<>();

        public ImagesInteraction(Activity activity) {
            mActRef = new WeakReference<>(activity);
        }

        public boolean hasImage() {
            return mImageList != null && mImageList.size() > 0;
        }

        public void searchImageUrls(String html) {
            mImageList = HtmlUtils.filterImages(html);
        }

        @JavascriptInterface
        public void showImage(int position ,String url) {
            Activity activity = mActRef.get();
            if (activity != null) {
                List<SubjectMaterialPicture> subjectMaterialPictures = new ArrayList<>();
                for (int i = 0; i < mImageList.size(); i++) {
                    SubjectMaterialPicture smp = new SubjectMaterialPicture();
                    smp.setId("" + i);
                    smp.setUrl(mImageList.get(i));
                    subjectMaterialPictures.add(smp);
                }
                SubjectMaterialPicturesActivity.start(activity, subjectMaterialPictures, position);
            }
        }

        public void setHtmlToWebView(String contentHtml) {
            if (hasImage()) {
                WebViewUtils.setContentToWebView(mContentWv, HtmlUtils.constructExecActionJs(contentHtml));
            } else {
                WebViewUtils.setContentToWebView(mContentWv, HtmlUtils.constructWordBreakJs(contentHtml));
            }
        }
    }

    public static void start(Context context, UserInfo userInfo, String malfunctionId) {
        Intent intent = new Intent(context, MalfunctionDetailsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_MALFUNCTION_ID, malfunctionId);
        context.startActivity(intent);
    }
}

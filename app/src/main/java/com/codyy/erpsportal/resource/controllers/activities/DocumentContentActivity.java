package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.Hit;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.artifex.mupdfdemo.MuPDFView;
import com.artifex.mupdfdemo.OutlineActivityData;
import com.artifex.mupdfdemo.ReaderView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Constants;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.resource.models.entities.Document;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 文档资源详情，用于显示文档内容
 * Created by gujiajia on 2016/7/6
 */
public class DocumentContentActivity extends FragmentActivity implements View.OnClickListener {

    private final static String TAG = "DocumentContentActivity";

    private final static String EXTRA_DOCUMENT_ID = "com.codyy.erpsportal.DOCUMENT_ID";

    private final static String EXTRA_DOCUMENT_NAME = "com.codyy.erpsportal.DOCUMENT_NAME";

    private TextView mNameTv;

    private TextView mPageNoTv;

    private RelativeLayout mPdfViewContainerRl;

    private UserInfo mUserInfo;

    private String mDocumentId;

    private String mDocumentName;

    private String mClassId;

    private ResourceDetails mResourceDetails;

    private MuPDFCore mPdfCore;

    private MuPDFReaderView mDocView;

    private ProgressBar mDownloadingPb;

    private String mCacheLoc;

    private RequestSender mSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_content);
        initAttributes();
        initViews();
        loadData();
    }

    private void initAttributes() {
        mSender = new RequestSender(this);
        if (getExternalCacheDir() != null) {
            mCacheLoc = getExternalCacheDir().getAbsolutePath();
        }
        if (getIntent() != null) {
            mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
            mDocumentId = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);
            mDocumentName = getIntent().getStringExtra(EXTRA_DOCUMENT_NAME);
            mClassId = getIntent().getStringExtra(Extra.CLASS_ID);
        }
    }

    private void initViews() {
        mNameTv = (TextView) findViewById(R.id.name);
        mPageNoTv = (TextView) findViewById(R.id.pageNoTv);
        View resourceDetailsBtn = findViewById(R.id.btn_resource_details);
        View commentBtn = findViewById(R.id.btn_comment);
        mDownloadingPb = (ProgressBar) findViewById(R.id.downloading);
        mPdfViewContainerRl = (RelativeLayout) findViewById(R.id.rl_pdf_view_container);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.progress_loading, null);
        mDownloadingPb.setIndeterminateDrawable(drawable);
        resourceDetailsBtn.setOnClickListener(this);
        commentBtn.setOnClickListener(this);
        if (!TextUtils.isEmpty(mDocumentName)) {
            mNameTv.setText(mDocumentName);
        }
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("resourceId", mDocumentId);
        if (!TextUtils.isEmpty(mClassId)) {
            params.put("baseClassId", mClassId);
        }
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.RESOURCE_DETAILS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadData:" + response);
                String result = response.optString("result");
                if ("success".equals(result)) {
                    JSONObject detailsJsonObject = response.optJSONObject("data");
                    mResourceDetails = ResourceDetails.parseJson(detailsJsonObject);
                    mNameTv.setText(mResourceDetails.getResourceName());
                    downloadPdf();
                } else if ("error".equals(result)) {
                    String message = response.optString("message");
                    UIUtils.toast(DocumentContentActivity.this, message, Toast.LENGTH_SHORT);
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

    private void downloadPdf() {
        String pdfUrl = mResourceDetails.getPlayUrl();
        DownloadTask task = new DownloadTask(getApplicationContext());
        Cog.d(TAG, "downloadPdf pdfUrl=" + pdfUrl);
        task.execute(pdfUrl, mResourceDetails.getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resource_details:
                if (mClassId == null) {
                    DocumentDetailsActivity.start(this, mResourceDetails);
                } else {
                    DocumentDetailsActivity.start(this, mResourceDetails);
                }
                break;
            case R.id.btn_comment:
                DocumentCommentActivity.start(this, mUserInfo, mResourceDetails.getId());
                break;
        }
    }

    public void onBackClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        private PowerManager.WakeLock mWakeLock;


        DownloadTask(Context context) {
            this.context = context;
        }

        /**
         * @param sUrl 第一位为url 第二位为resId
         * @return 返回下载好的pdf文件位置
         */
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String directory = createDir();
            if (directory == null) return null;
            String fileName = directory + "/" + sUrl[1];
            Cog.d(TAG, "cacheDir=" + directory);
            try {
                URL url = new URL(sUrl[0]);
                Cog.d(TAG, "fileName=" + fileName);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    return "Server returned HTTP " + connection.getResponseCode()
//                            + " " + connection.getResponseMessage();
                    return null;
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(fileName);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                Cog.e(TAG, e.toString());
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    Cog.e(TAG, ignored.toString());
                }
                if (connection != null)
                    connection.disconnect();
            }
            return fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mDownloadingPb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            Cog.d(TAG, "onPostExecute result=" + result);
            mWakeLock.release();
            if (result == null) {
                UIUtils.toast(context, "加载失败", Toast.LENGTH_SHORT);
            } else {
                UIUtils.toast(context, "加载完成", Toast.LENGTH_SHORT);
                displayPdf(result);
            }

            mDownloadingPb.setVisibility(View.GONE);
        }
    }

    /**
     * 加载显示pdf
     *
     * @param file pdf文件路径
     */
    private void displayPdf(String file) {
        loadPdf(file);
        mPageNoTv.setVisibility(View.VISIBLE);
    }

    private void loadPdf(String filePath) {
        cleanPdfCache();
        if (mPdfCore == null) {
            mPdfCore = openFile(filePath);
            if (mPdfCore == null || mPdfCore.countPages() == 0) {
                ToastUtil.showToast(EApplication.instance(), "无法打开文件!");
                return;
            } else {
                updatePageNumber(1);//默认在第一页
            }
        }
        createUI();
    }

    private void updatePageNumber(int pageNo) {
        mPageNoTv.setText(String.format(Locale.getDefault(), "%d / %d", pageNo, mPdfCore.countPages()));
    }

    private void createUI() {
        mDocView = new MuPDFReaderView(this) {
            @Override
            protected void onMoveToChild(int i) {
                if (mPdfCore == null)
                    return;
                updatePageNumber(i + 1);
                super.onMoveToChild(i);
            }

            @Override
            protected void onTapMainDocArea() { }

            @Override
            protected void onDocMotion() { }

            @Override
            protected void onHit(Hit item) {}
        };

        mDocView.setAdapter(new MuPDFPageAdapter(this, new FilePicker.FilePickerSupport() {
            @Override
            public void performPickFor(FilePicker picker) {
                //选择文件
            }
        }, mPdfCore));

        mDocView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mPdfViewContainerRl.addView(mDocView,0);
    }

    private MuPDFCore openFile(String path) {
//        mFileName = new String(lastSlashPos == -1
//                ? path
//                : path.substring(lastSlashPos + 1));
        System.out.println("Trying to open " + path);
        try {
            mPdfCore = new MuPDFCore(EApplication.instance(), path);
            // New file: drop the old outline data
            OutlineActivityData.set(null);
        } catch (Exception e) {
            Cog.e(TAG, e.getMessage());
            return null;
        } catch (java.lang.OutOfMemoryError e) {
            //  out of memory is not an Exception, so we catch it separately.
            Cog.e(TAG, e.getMessage());
            return null;
        }
        return mPdfCore;
    }

    /**
     * 创建PDF缓存目录
     *
     * @return 创建的PDF缓存目录路径
     */
    private String createDir() {
        if (TextUtils.isEmpty(mCacheLoc)) return null;
        File file = new File(mCacheLoc + Constants.FOLDER_DOC_CACHE);
        boolean result = false;
        if (file.exists()) {
            if (!file.isDirectory() ) {
                if (file.delete() && file.mkdirs()) {
                    result = true;
                }
            } else {
                result = true;
            }
        } else {
            result = file.mkdirs();
        }
        return result? file.getAbsolutePath(): null;
    }

    public static void start(Activity activity, UserInfo userInfo, Resource document) {
        Intent intent = new Intent(activity, DocumentContentActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_DOCUMENT_ID, document.getId());
        intent.putExtra(EXTRA_DOCUMENT_NAME, document.getTitle());
        activity.startActivity(intent);
    }

    public static void start(Activity activity, UserInfo userInfo, Document document) {
        start(activity, userInfo, document, null);
    }

    public static void start(Activity activity, UserInfo userInfo, Document document, String classId) {
        Intent intent = new Intent(activity, DocumentContentActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_DOCUMENT_ID, document.getId());
        intent.putExtra(EXTRA_DOCUMENT_NAME, document.getName());
        if (classId != null)
            intent.putExtra(Extra.CLASS_ID, classId);
        activity.startActivity(intent);
    }

    /**
     * 销毁已经存在的pdf 文件
     */
    private void cleanPdfCache() {
        if (mDocView != null) {
            mDocView.applyToChildren(new ReaderView.ViewMapper() {
                public void applyToView(View view) {
                    ((MuPDFView) view).releaseBitmaps();
                }
            });
        }
        if (mPdfCore != null)
            mPdfCore.onDestroy();
        mPdfCore = null;
    }
}

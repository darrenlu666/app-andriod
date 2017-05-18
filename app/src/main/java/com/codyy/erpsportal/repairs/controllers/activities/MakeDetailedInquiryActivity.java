package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter;
import com.codyy.erpsportal.repairs.models.engines.PhotosUploader;
import com.codyy.erpsportal.repairs.models.engines.PhotosUploader.UploadListener;
import com.codyy.erpsportal.repairs.models.entities.UploadingImage;
import com.codyy.erpsportal.repairs.widgets.ImageItemDecoration;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.RC_ADD_IMAGES;
import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.RC_PREVIEW;

/**
 * 追问
 */
public class MakeDetailedInquiryActivity extends AppCompatActivity {

    private static final String TAG = "MakeDetailedInquiryActivity";

    private static final int MAX_IMAGES = 10;

    private static final String EXTRA_SKEY = "com.codyy.erpsportal.EXTRA_SKEY";

    @Bind(R.id.btn_commit)
    Button mCommitBtn;

    @Bind(R.id.et_desc)
    EditText mDescEt;

    @Bind(R.id.rv_images)
    RecyclerView mImagesRv;

    private RepairImageAdapter mAdapter;

    private UserInfo mUserInfo;

    private String mRepairId;

    private String mSkey;

    private RequestSender mSender;

    private PhotosUploader mPhotosUploader;

    private LoadingDialog mLoadingDialog;

    private boolean mCommitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_detailed_inquiry);
        ButterKnife.bind(this);
        initAttributes();
        initComponents();
    }

    private void initAttributes() {
        mSender = new RequestSender(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mSkey = getIntent().getStringExtra(EXTRA_SKEY);
        mRepairId = getIntent().getStringExtra(Extra.ID);
    }

    private void initComponents() {
        mAdapter = new RepairImageAdapter(MAX_IMAGES);
        mImagesRv.setAdapter(mAdapter);
        mImagesRv.addItemDecoration(new ImageItemDecoration(UIUtils.dip2px(this, 5), 4));
        mImagesRv.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_ADD_IMAGES) {
                List<UploadingImage> newAddedImages = mAdapter.processAddImagesResultData(data);
                uploadImages(newAddedImages);
            } else if (requestCode == RC_PREVIEW) {
                mAdapter.processDeleteImagesResultData(data);
            }
        }
    }

    private void uploadImages(List<UploadingImage> newAddedImages) {
        final String uploadUrl = mUserInfo.getServerAddress() + "/res/" + mUserInfo.getAreaCode()
                + "/imageUpload.do?validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=5";
        mPhotosUploader = new PhotosUploader(uploadUrl, newAddedImages, new UploadListener() {
            @Override
            public void onStart() { }

            @Override
            public void onEachComplete(UploadingImage image) {
                int index = mAdapter.indexOfItem(image);
                if (index != -1) {
                    mAdapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onFinish() {
                mAdapter.notifyDataSetChanged();
            }
        });
        mPhotosUploader.start();
    }

    @OnClick(R.id.btn_commit)
    public void onCommitClick() {
        if (mCommitting) return;
        if (mPhotosUploader != null && mPhotosUploader.mIsUploading) {
            ToastUtil.showToast(this, "图片上传中，请稍等...");
            return;
        }
        Map<String, String> params = new HashMap<>();
        String desc = mDescEt.getText().toString().trim();
        if (TextUtils.isEmpty(desc)) {
            ToastUtil.showToast(this, "追问内容不可为空");
            return;
        }
        params.put("appendDescription", desc);
        params.put("answerName", mUserInfo.getRealName());
        params.put("appendType", "ASK");
        params.put("uuid", mUserInfo.getUuid());
        params.put("malDetailId", mRepairId);
        params.put("skey", mSkey);

        //添加上传的图片
        List<UploadingImage> images = mAdapter.getItems();
        if (images != null && images.size() > 0) {
            int availableCount = 0;//可用图片计数，去掉没id（没上传成功或正在上传）的
            StringBuilder sb = new StringBuilder();
            for (UploadingImage image: images) {
                if (!TextUtils.isEmpty( image.getId())) {
                    availableCount++;
                    sb.append(image.getId())
                            .append(',');
                }
            }
            if (availableCount > 0) {
                sb.deleteCharAt(sb.length() - 1);
                params.put("addimgs", sb.toString());
            }
        }

        mLoadingDialog = LoadingDialog.newInstance();
        mLoadingDialog.show(getSupportFragmentManager(), "sending");
        mCommitting = true;
        mSender.sendRequest(new RequestData(URLConfig.MAKE_DETAILED_INQUIRY, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "onCommitClick response=", response);
                        if ("success".equals( response.optString("result"))) {
                            setResult(RESULT_OK);
                            finish();
                        }
                        mLoadingDialog.dismiss();
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.d(TAG, "onErrorResponse error=", error);
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(MakeDetailedInquiryActivity.this, "出错了，请重试");
                        mCommitting = false;//失败了才能重试
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPhotosUploader != null) mPhotosUploader.stop();
        mAdapter.cancelAll();
    }

    public static void start(Fragment fragment, int rcMakeDetailedInquiry, UserInfo userInfo, String repairId, String skey) {
        Intent intent = new Intent(fragment.getActivity(), MakeDetailedInquiryActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_SKEY, skey);
        intent.putExtra(Extra.ID, repairId);
        fragment.startActivityForResult(intent,rcMakeDetailedInquiry);
        UIUtils.addEnterAnim(fragment.getActivity());
    }
}

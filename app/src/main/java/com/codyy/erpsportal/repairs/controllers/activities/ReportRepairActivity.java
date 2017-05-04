package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.Regexes;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectCategoriesDgFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectCategoriesDgFragment.OnCategorySelectedListener;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectClassroomDgFragment;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectClassroomDgFragment.OnClassroomSelectedListener;
import com.codyy.erpsportal.repairs.models.engines.PhotosUploader;
import com.codyy.erpsportal.repairs.models.engines.PhotosUploader.UploadListener;
import com.codyy.erpsportal.repairs.models.entities.CategoriesPageInfo;
import com.codyy.erpsportal.repairs.models.entities.ClassroomSelectItem;
import com.codyy.erpsportal.repairs.models.entities.MalfuncCategory;
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
import io.reactivex.disposables.CompositeDisposable;

import static com.codyy.erpsportal.R.id.ll_classroom;
import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.RC_ADD_IMAGES;
import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.RC_PREVIEW;

/**
 * 报修
 */
public class ReportRepairActivity extends AppCompatActivity {

    private static final String TAG = "ReportRepairActivity";

    private static final int RC_SCAN_CLASSROOM_SERIAL = 3;

    private static final int MAX_IMAGES = 10;

    @Bind(R.id.btn_commit)
    Button mCommitBtn;

    @Bind(R.id.tv_classroom)
    TextView mClassroomTv;

    @Bind(R.id.iv_scan_serial)
    ImageView mScanSerialIv;

    @Bind(ll_classroom)
    LinearLayout mClassroomLl;

    @Bind(R.id.tv_categories)
    TextView mCategoriesTv;

    @Bind(R.id.et_reporter)
    EditText mReporterEt;

    @Bind(R.id.et_phone)
    EditText mPhoneEt;

    @Bind(R.id.tv_lb_description)
    TextView mDescriptionLbTv;

    @Bind(R.id.et_desc)
    EditText mDescEt;

    /**
     * 附加图片们
     */
    @Bind(R.id.rv_images)
    RecyclerView mImagesRv;

    private UserInfo mUserInfo;

    private RepairImageAdapter mAdapter;

    private CompositeDisposable mDisposables;

    /**
     * 上次选择的故障类别
     */
    private List<CategoriesPageInfo> mSelectedCategories;

    /**
     * 已选择的教室
     */
    private ClassroomSelectItem mSelectedClassroom;

    private RequestSender mSender;

    private PhotosUploader mPhotosUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repair);
        ButterKnife.bind(this);
        initAttributes();
        initComponent();
    }

    /**
     * 初始化属性
     */
    private void initAttributes() {
        mSender = new RequestSender(this);
        mDisposables = new CompositeDisposable();
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
    }

    private void initComponent() {
        mAdapter = new RepairImageAdapter(MAX_IMAGES);
        mImagesRv.setAdapter(mAdapter);
        mImagesRv.addItemDecoration(new ImageItemDecoration(UIUtils.dip2px(this, 5), 4));
        mImagesRv.setLayoutManager(new GridLayoutManager(this, 4));
        if ( mUserInfo.isTeacher() || mUserInfo.isSchool()) {
            mReporterEt.setText( mUserInfo.getRealName());
            if (!TextUtils.isEmpty( mUserInfo.getContactPhone())) {
                mPhoneEt.setText( mUserInfo.getContactPhone());
            }
        }
        mReporterEt.clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_ADD_IMAGES) {
                List<UploadingImage> newAddedImages = mAdapter.processAddImagesResultData(data);
                uploadImages( newAddedImages);
            } else if (requestCode == RC_PREVIEW) {
                mAdapter.processDeleteImagesResultData(data);
            } else if (requestCode == RC_SCAN_CLASSROOM_SERIAL) {
                ClassroomSelectItem classroomSelectItem = data.getParcelableExtra(ScanSerialActivity.EXTRA_CLASSROOM);
                if (classroomSelectItem != null) {
                    classroomSelected(classroomSelectItem);
                }
            }
        }
    }

    /**
     * 图片上传
     * @param imageList 图片列表
     */
    private void uploadImages(List<UploadingImage> imageList) {
        final String uploadUrl = mUserInfo.getServerAddress() + "/res/" + mUserInfo.getAreaCode()
                + "/imageUpload.do?validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=5";

        mPhotosUploader = new PhotosUploader(uploadUrl, imageList, new UploadListener() {
            @Override
            public void onStart() {
//                mCommitBtn.setEnabled(false);
            }

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
//                mCommitBtn.setEnabled(true);
            }
        });
        mPhotosUploader.start();
    }

    @OnClick(R.id.ll_classroom)
    public void onClassroomClick() {
        SelectClassroomDgFragment fragment = SelectClassroomDgFragment.newInstance(mUserInfo);
        if (mSelectedClassroom != null) fragment.setSelected(mSelectedClassroom);
        fragment.setClassroomSelectedListener(new OnClassroomSelectedListener() {
            @Override
            public void onClassroomSelected(ClassroomSelectItem item) {
                classroomSelected(item);
            }
        });
        fragment.show(getSupportFragmentManager(), "selectClassroom");
    }

    /**
     * 选中某教室
     * @param classroom 某教室
     */
    private void classroomSelected(ClassroomSelectItem classroom) {
        mSelectedClassroom = classroom;
        mClassroomTv.setText(getString(R.string.classroom_role_format,
                mSelectedClassroom.getSkey(), mSelectedClassroom.getRoomName()));
    }

    @OnClick(R.id.ll_malfunction_categories)
    public void onMalfunctionCategoriesClick() {
        SelectCategoriesDgFragment fragment = SelectCategoriesDgFragment.newInstance(mUserInfo);
        fragment.setInitPageInfoList(mSelectedCategories);
        fragment.setOnCategorySelectedListener(new OnCategorySelectedListener() {
            @Override
            public void onCategorySelected(List<CategoriesPageInfo> pageInfos) {
                mSelectedCategories = pageInfos;
                StringBuilder categoriesSb = new StringBuilder();
                for (CategoriesPageInfo categoriesPageInfo: pageInfos) {
                    categoriesSb.append( categoriesPageInfo.getSelectedName())
                            .append('-');
                }
                categoriesSb.deleteCharAt( categoriesSb.length() - 1);
                mCategoriesTv.setText( categoriesSb.toString());
            }
        });
        fragment.show(getSupportFragmentManager(), "selectCategories");
    }

    @OnClick(R.id.iv_scan_serial)
    public void onScanSerialClick() {
        ScanSerialActivity.start(this, mUserInfo, RC_SCAN_CLASSROOM_SERIAL);
    }

    @OnClick(R.id.btn_commit)
    public void onCommitClick() {
        if (mPhotosUploader != null && mPhotosUploader.mIsUploading) {
            ToastUtil.showToast(this, "图片上传中，请稍等...");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        //设置参数：教室
        if (mSelectedClassroom == null) {
            ToastUtil.showToast(this, "请选择报修教室");
            return;
        }
        params.put("skey", mSelectedClassroom.getSkey());

        //设置参数：故障类别
        if (mSelectedCategories == null || mSelectedCategories.size() == 0) {
            ToastUtil.showToast(this, "请选择故障类别");
            return;
        }
        int i = 1;
        for (CategoriesPageInfo pageInfo: mSelectedCategories) {
            MalfuncCategory malfuncCategory = pageInfo.getSelectedCategory();
            params.put("malCatalogId" + i, malfuncCategory.getId());
            i++;
        }

        //设置参数：报修人
        String reportName = mReporterEt.getText().toString();
        if (TextUtils.isEmpty(reportName)) {
            ToastUtil.showToast(this, "请选择报修人");
            return;
        } else if (reportName.length() < 2 || reportName.length() > 20) {
            ToastUtil.showToast(this, "报修人名字长度需要是2到20个字");
            return;
        }
        params.put("reporter", reportName);

        //设置参数：联系电话
        String contactPhone = mPhoneEt.getText().toString();
        if (TextUtils.isEmpty(contactPhone) || !contactPhone.matches(Regexes.PHONE_REGEX)) {
            ToastUtil.showToast(this, "请输入正确的电话号码。");
            return;
        }
        params.put("reporterContact", contactPhone);

        //设置参数：故障描述
        String description = mDescEt.getText().toString();
        if (TextUtils.isEmpty(description)) {
            ToastUtil.showToast(this, "请输入故障描述。");
            return;
        }
        params.put("malDescription", description);

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

        mSender.sendRequest(new RequestData(URLConfig.REPORT_MAL, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onCommitClick response:", response);
                if ("success".equals(response.optString("result"))) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showToast(ReportRepairActivity.this, "报修失败");
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "onCommitClick error:", error);
                ToastUtil.showToast(ReportRepairActivity.this, "咦！出错了");
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
        mAdapter.cancelAll();
        if (mPhotosUploader != null) mPhotosUploader.stop();
        ButterKnife.unbind(this);
    }

    public static void start(Activity context, UserInfo userInfo, int rcReport) {
        Intent intent = new Intent(context, ReportRepairActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        context.startActivityForResult(intent, rcReport);
        UIUtils.addEnterAnim( context);
    }
}

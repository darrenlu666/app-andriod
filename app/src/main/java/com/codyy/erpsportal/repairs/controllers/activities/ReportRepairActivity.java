package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter;
import com.codyy.erpsportal.repairs.controllers.fragments.SelectCategoriesDgFragment;
import com.codyy.erpsportal.repairs.models.entities.UploadingImage;
import com.codyy.erpsportal.repairs.utils.UploadUtil;
import com.codyy.erpsportal.repairs.widgets.ImageItemDecoration;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.REQUEST_CODE_ADD_IMAGES;
import static com.codyy.erpsportal.repairs.controllers.adapters.RepairImageAdapter.REQUEST_PREVIEW;

/**
 * 报修
 */
public class ReportRepairActivity extends AppCompatActivity {

    private static final String TAG = "ReportRepairActivity";

    private static final int MAX_IMAGES = 10;

    @Bind(R.id.btn_commit)
    Button mCommitBtn;

    @Bind(R.id.tv_classroom)
    TextView mClassroomTv;

    @Bind(R.id.iv_scan_serial)
    ImageView mScanSerialIv;

    @Bind(R.id.ll_classroom)
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
        mDisposables = new CompositeDisposable();
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
    }

    private void initComponent() {
        mAdapter = new RepairImageAdapter(MAX_IMAGES);
        mImagesRv.setAdapter(mAdapter);
        mImagesRv.addItemDecoration(new ImageItemDecoration(UIUtils.dip2px(this, 5), 4));
        mImagesRv.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD_IMAGES) {
                List<UploadingImage> newAddedImages = mAdapter.processAddImagesResultData(data);
                uploadImages( newAddedImages);
            } else if (requestCode == REQUEST_PREVIEW) {
                mAdapter.processDeleteImagesResultData(data);
            }
        }
    }

    private void uploadImages(List<UploadingImage> imageList) {
        final String uploadUrl = mUserInfo.getServerAddress() + "/res/" + mUserInfo.getAreaCode()
                + "/imageUpload.do?validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=5";
        Observable.fromIterable(imageList)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<UploadingImage>() {
                    @Override
                    public void accept(UploadingImage uploadingImage) throws Exception {
                        Cog.d(TAG, "doOnNext accept imageDetail=", uploadingImage.getPath());
                        String result = UploadUtil.upload(uploadingImage, uploadUrl);
                        JSONObject jsonObject = new JSONObject(result);
                        uploadingImage.setId( jsonObject.optString("realname"));
                        uploadingImage.setName( jsonObject.optString("message"));
                        Cog.d(TAG, "doOnNext upload result=", result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Cog.d(TAG, "doOnSubscribe d=", disposable);
                        mDisposables.add(disposable);
                    }
                })
                .subscribe(new Consumer<UploadingImage>() {
                    @Override
                    public void accept(UploadingImage image) throws Exception {
                        Cog.d(TAG, "onNext value=", image.getPath());
                        image.setStatus(UploadingImage.STATUS_FINISHED);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "onError e=", throwable);
                        throwable.printStackTrace();
                    }
                });
    }

    @OnClick(R.id.ll_malfunction_categories)
    public void onMalfunctionCategoriesClick() {
        SelectCategoriesDgFragment fragment = new SelectCategoriesDgFragment();
        fragment.show(getSupportFragmentManager(), "selectCategories");
    }

    @OnClick(R.id.iv_scan_serial)
    public void onScanSerialClick() {
        ScanSerialActivity.start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
        mAdapter.cancelAll();
        ButterKnife.unbind(this);
    }

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, ReportRepairActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }
}

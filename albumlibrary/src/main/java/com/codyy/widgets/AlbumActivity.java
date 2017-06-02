package com.codyy.widgets;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.widgets.adapter.AlbumAdapter;
import com.codyy.widgets.imagepipeline.ImagePipelineConfigFactory;
import com.codyy.widgets.model.entities.AlbumBase;
import com.codyy.widgets.model.entities.PhotoInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlbumActivity extends AppCompatActivity implements AlbumAdapter.TakePhoto, OnClickListener {
    /**
     * 传入已选择的图片的信息
     */
    public static final String SET_SELECT_INFO = "set_select_info";
    /**
     * 返回已选择的图片的信息
     */
    public static final String RESULT_SELECT_INFO = "result_select_info";
    /**
     * 设置最大的选择照片数量
     */
    public static final String SET_MAX_SELECT = "set_max_select";
    /**
     *
     */
    public static final String IMAGE_MAX_SIZE = "image_max_size";
    /**
     * 请求权限
     */
    public static final int REQUEST_PERMISSION = 0x001;
    public static final int RESULT_SUCCESS = 0xa01;
    public static final int RESULT_ERROR = 0xa02;
    private RecyclerView mRecyclerView;
    private AlbumAdapter mAlbumAdapter;
    private String mPhotoName;
    /**
     * 图片路径基本目录
     */
    public static final String IMAGE_BASE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final int TAKE_PHOTO = 0x001;
    /**
     * 获取预览返回数据
     */
    public static final int GET_SELECT_INFO = 0x002;
    /**
     * 确定返回
     */
    public static final int GET_SELECT_OK = 0x003;
    /**
     * 预览图片数目
     */
    private TextView mPreviewSize;
    /**
     * 预览
     */
    private TextView mPreview;
    /**
     * 确定发送
     */
    private TextView mSend;
    private Toolbar mToolbar;
    private int mImageMaxSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlbumBase.initialize();
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        ArrayList<PhotoInfo> infos = intent.getParcelableArrayListExtra(SET_SELECT_INFO);
        AlbumBase.MAX_SELECT = intent.getIntExtra(SET_MAX_SELECT, -1);
        AlbumBase.MAX_SIZE = intent.getIntExtra(IMAGE_MAX_SIZE, Integer.MAX_VALUE);
        if (infos != null) {
            AlbumBase.selectInit(infos);
        }
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mPreviewSize = (TextView) findViewById(R.id.tv_preview_image);
        mPreview = (TextView) findViewById(R.id.textView_preview);
        mPreview.setOnClickListener(this);
        mSend = (TextView) findViewById(R.id.btn_ok);
        mSend.setOnClickListener(this);
        if (AlbumBase.SELECT_INFO == null || AlbumBase.SELECT_INFO.size() == 0) {
            mPreviewSize.setVisibility(View.GONE);
        }
        mToolbar.collapseActionView();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AlbumBase.PHOTO_INFO == null || AlbumBase.PHOTO_INFO.size() == 0) {
            mAlbumAdapter = new AlbumAdapter(AlbumActivity.this, ImagePipelineConfigFactory.getImagePipelineConfig(AlbumActivity.this));
            mAlbumAdapter.setmTakePhoto(this);
            mRecyclerView.setAdapter(mAlbumAdapter);
        }
        if (AlbumBase.PHOTO_INFO.size() == 0) {
            new LocalData().execute(0);
        }
    }

    @Override
    public void takePhoto() {
        int cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int fileGranted = ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE);

        if (cameraGranted == PackageManager.PERMISSION_GRANTED
                && fileGranted == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            StringBuilder permissions = new StringBuilder();
            if (cameraGranted != PackageManager.PERMISSION_GRANTED) {
                permissions.append(Manifest.permission.CAMERA).append("$");
            }
            if (fileGranted != PackageManager.PERMISSION_GRANTED) {
                permissions.append(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            ActivityCompat.requestPermissions(this, permissions.toString().split("$"),
                    REQUEST_PERMISSION);
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long createTime = System.currentTimeMillis();
        Date d1 = new Date(createTime);
        String t1 = format.format(d1);
        String cameraPath = AlbumActivity.IMAGE_BASE_PATH + "/Camera/";
        File file = new File(cameraPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mPhotoName = cameraPath + t1 + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + mPhotoName));
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION:
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(mPreview, "没有相机或文件写入权限！", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
                startCamera();
                break;
        }
    }

    @Override
    public void imageSelect() {
        int a = AlbumBase.SELECT_INFO.size();
        if (a == 0) {
            mPreviewSize.setVisibility(View.GONE);
        } else if (mPreviewSize.getVisibility() == View.GONE) {
            mPreviewSize.setVisibility(View.VISIBLE);
        }
        mPreviewSize.setText(String.valueOf(a));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(mPhotoName);
                    if (file.exists()) {
                        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        mediaScanIntent.setData(Uri.parse("file://" + mPhotoName));
                        sendBroadcast(mediaScanIntent);
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setType(PhotoInfo.TYPE_PHOTO);
                        photoInfo.setPath(mPhotoName);
                        photoInfo.setContent(Uri.fromFile(file));
                        photoInfo.setSize(file.length());
                        AlbumBase.PHOTO_INFO.add(0, photoInfo);
                        mAlbumAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case GET_SELECT_INFO:
                if (resultCode == GET_SELECT_OK) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_SELECT_INFO, AlbumBase.SELECT_INFO);
                    setResult(RESULT_SUCCESS, intent);
                    finish();
                    break;
                }
                imageSelect();
                mAlbumAdapter.dataInit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlbumBase.clear();
        if (mAlbumAdapter != null) {
            mAlbumAdapter.shutDown();
        }
        System.gc();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.textView_preview) {
            if (AlbumBase.SELECT_INFO.size() > 0) {
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra(PreviewActivity.IMAGE_TYPT, PreviewActivity.TYPE_PREVIEW);
                intent.putExtra(PreviewActivity.PAGE_INFO, 0);
                startActivityForResult(intent, GET_SELECT_INFO);
            } else {
                Snackbar.make(v, getResources().getString(R.string.no_select), Snackbar.LENGTH_SHORT).show();
            }

        } else if (i == R.id.btn_ok) {
            if (AlbumBase.SELECT_INFO.size() > 0) {
//                ArrayList<PhotoInfo> photoInfos = AlbumBase.SELECT_INFO;
                Intent intent = new Intent();
                intent.putExtra(RESULT_SELECT_INFO, AlbumBase.SELECT_INFO);
                setResult(RESULT_SUCCESS, intent);
                this.finish();
            } else {
                Snackbar.make(v, getResources().getString(R.string.no_select), Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    class LocalData extends AsyncTask<Integer, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Integer... params) {
            return AlbumBase.scanPhoto(AlbumActivity.this);
        }


        @Override
        protected void onPreExecute() {
            AlbumBase.dataInit();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag) {
                if (mAlbumAdapter != null) {
                    mAlbumAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}

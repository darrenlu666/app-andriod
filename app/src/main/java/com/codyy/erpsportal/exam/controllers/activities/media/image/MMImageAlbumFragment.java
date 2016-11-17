package com.codyy.erpsportal.exam.controllers.activities.media.image;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.SpacesItemDecoration;
import com.codyy.media.image.CaptureImageActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 图片可上传8张，每张最大5M，格式支持JPG，PNG，JPEG，BMP等格式
 *
 * @author eachann
 */
public class MMImageAlbumFragment extends Fragment implements Handler.Callback {

    private static final int REQUEST_CODE_PREVIEW = 1 << 3;
    private static final int REQUEST_CODE_CAPTURE = 1 << 4;
    private static int IMAGE_MAX_COUNT = 1 << 3;
    private static final int SPAN_COUNT = 4;
    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";
    public static final String EXTRA_TYPE = ToolbarActivity.class.getPackage() + ".EXTRA_TYPE";
    public static final String TYPE_IMAGE = "TYPE_IMAGE";
    private static final String ARG_SIZE = "ARG_SIZE";
    private final static int WHAT = 0;
    private ArrayList<MMImageBean> mImageList = new ArrayList<>();
    private TextView mTvPreview;
    private Button mConfirm;
    private MMImageGridAdapter mImageGridAdapter;
    private int mPreviewCount;
    private int mImageSize;
    private Handler mHandler;

    public static MMImageAlbumFragment newInstance(int size) {
        MMImageAlbumFragment fragment = new MMImageAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SIZE, size);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        if (getArguments() != null) {
            mImageSize = getArguments().getInt(ARG_SIZE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PREVIEW:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(EXTRA_DATA, data.getParcelableArrayListExtra(EXTRA_DATA));
                    intent.putExtra(EXTRA_TYPE, TYPE_IMAGE);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
                break;
            case REQUEST_CODE_CAPTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                    MMImageBean imageBean = new MMImageBean(data.getStringExtra("EXTRA_DATA"), true, null);
                    imageBeans.add(imageBean);
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                    intent.putExtra(EXTRA_TYPE, TYPE_IMAGE);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
                break;
        } // switch
    }

    private void setOnPreviewCountChangeListener() {
        if (mPreviewCount > 0) {
            mConfirm.setText(getString(R.string.exam_image_upload, mPreviewCount));
            mTvPreview.setEnabled(true);
            mConfirm.setEnabled(true);
        } else {
            mConfirm.setText(getString(R.string.exam_image_upload_n));
            mTvPreview.setEnabled(false);
            mConfirm.setEnabled(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_multimedia_recycler, container, false);
        RecyclerView mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mTvPreview = (TextView) mRootView.findViewById(R.id.tv_preview);
        mConfirm = (Button) mRootView.findViewById(R.id.btn_confirm);
        mConfirm.setText(getString(R.string.exam_image_upload_n));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mImageGridAdapter = new MMImageGridAdapter(new ArrayList<MMImageBean>(), getActivity());
        mRecyclerView.setAdapter(mImageGridAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(UIUtils.dip2px(getActivity(), 1f)));
        mImageGridAdapter.setOnInViewClickListener(R.id.v_selected_frame, new MMBaseRecyclerViewAdapter.onInternalClickListener<MMImageBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, MMImageBean values) {
                if (position != 0) {
                    RelativeLayout relativeLayout = (RelativeLayout) v;
                    if (relativeLayout.getChildAt(0) instanceof ImageView) {
                        if (values.isSeleted()) {
                            values.setSeleted(false);
                            --mPreviewCount;
                            relativeLayout.setBackgroundResource(R.color.transparent);
                            ((ImageView) relativeLayout.getChildAt(0)).setImageResource(R.drawable.ic_exam_select_n);
                        } else {
                            if (mPreviewCount == (IMAGE_MAX_COUNT - mImageSize)) {
                                Snackbar.make(v, getString(R.string.exam_image_max_count, (IMAGE_MAX_COUNT - mImageSize)), Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            values.setSeleted(true);
                            relativeLayout.setBackgroundResource(R.color.image_selected_color);
                            ((ImageView) relativeLayout.getChildAt(0)).setImageResource(R.drawable.ic_exam_select_p);
                            ++mPreviewCount;
                        }
                    }
                    setOnPreviewCountChangeListener();
                }

            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, MMImageBean values) {

            }
        });
        mImageGridAdapter.setOnInViewClickListener(R.id.imgQueue, new MMBaseRecyclerViewAdapter.onInternalClickListener<MMImageBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, MMImageBean values) {
                if (position == 0) {
                    startActivityForResult(new Intent(getActivity(), CaptureImageActivity.class), REQUEST_CODE_CAPTURE);
                }
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, MMImageBean values) {

            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                for (MMImageBean imageBean : mImageList) {
                    if (imageBean.isSeleted()) {
                        imageBeans.add(imageBean);
                    }
                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                intent.putExtra(EXTRA_TYPE, TYPE_IMAGE);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                for (MMImageBean imageBean : mImageList) {
                    if (imageBean.isSeleted()) {
                        imageBeans.add(imageBean);
                    }
                }
                Intent intent = new Intent(getActivity(), MMPreviewImageActivity.class);
                intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                startActivityForResult(intent, REQUEST_CODE_PREVIEW);
            }
        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mThread.isAlive()) {
                mThread.interrupt();
            }
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            ArrayList<MMImageBean> list = new ArrayList<>();
            ContentResolver cr = getContext().getContentResolver();
            // 扫描照片
            String str[] = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA};
            Cursor imageCursor = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, str,
                    MediaStore.Images.Media.SIZE + "<? and " + MediaStore.Images.Media.SIZE + ">0 and " + MediaStore.Images.Media.DISPLAY_NAME + " like '%.jpg'  or '%.jpeg' or '%.bmp' or '%.png'",
                    new String[]{"5242881"}, MediaStore.Images.Media.DATE_ADDED + " ASC");
            try {
                if (imageCursor != null && imageCursor.getCount() > 0) {

                    while (imageCursor.moveToNext()) {
                        MMImageBean bean;
                        Cursor thumbCursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Thumbnails.IMAGE_ID,
                                MediaStore.Images.Thumbnails.DATA}, MediaStore.Images.Thumbnails.IMAGE_ID + "=" + imageCursor.getString(0), null, null);
                        try {
                            if (thumbCursor != null && thumbCursor.getCount() > 0 && thumbCursor.moveToFirst()) {
                                do {
                                    bean = new MMImageBean(imageCursor.getString(2), false, thumbCursor.getString(1));
                                    list.add(bean);
                                } while (thumbCursor.moveToNext());
                            } else {
                                bean = new MMImageBean(imageCursor.getString(2), false, imageCursor.getString(2));
                                list.add(bean);
                            }
                        } finally {
                            if (thumbCursor != null)
                                thumbCursor.close();
                        }
                    }

                }
            } finally {
                if (imageCursor != null)
                    imageCursor.close();
            }

            // show newest photo at beginning of the list
            Collections.reverse(list);
            list.add(0, new MMImageBean(null, false, null));
            Message message = new Message();
            message.what = WHAT;
            message.obj = list;
            mHandler.sendMessage(message);
        }
    });

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT:
                try {
                    mImageList = (ArrayList<MMImageBean>) msg.obj;
                    mImageGridAdapter.setList(mImageList);
                } catch (Exception e) {
                    e.printStackTrace();
                    mImageGridAdapter.setList(mImageList = new ArrayList<>());
                }
                break;

            default:
                break;
        }
        return false;
    }
}

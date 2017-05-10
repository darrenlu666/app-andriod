package com.codyy.erpsportal.repairs.models.engines;

import android.os.Handler;

import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.repairs.models.entities.UploadingImage;
import com.codyy.erpsportal.repairs.utils.UploadUtil;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 图片上传器
 * Created by gujiajia on 2017/5/2.
 */

public class PhotosUploader {

    private final static String TAG = "PhotosUploader";

    public volatile boolean mIsUploading;

    private String mUploadUrl;

    private List<UploadingImage> mImageList;

    private UploadListener mListener;

    private Disposable mDisposable;

    private Handler mHandler;

    public PhotosUploader(String uploadUrl, List<UploadingImage> imageList, UploadListener listener) {
        this.mUploadUrl = uploadUrl;
        this.mImageList = imageList;
        this.mListener = listener;
        mHandler = new Handler();
    }

    public void start() {
        mDisposable = Observable.fromIterable(mImageList)
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mIsUploading = true;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) {
                                    mListener.onStart();
                                }
                            }
                        });
                    }
                })
                .doOnNext(new Consumer<UploadingImage>() {
                    @Override
                    public void accept(UploadingImage uploadingImage) throws Exception {
                        Cog.d(TAG, "doOnNext accept imageDetail=", uploadingImage.getPath());
                        String result = UploadUtil.doUpload(uploadingImage, mUploadUrl);
                        JSONObject jsonObject = new JSONObject(result);
                        uploadingImage.setId( jsonObject.optString("message"));
                        uploadingImage.setName( jsonObject.optString("realname"));
                        Cog.d(TAG, "doOnNext upload result=", result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Cog.d(TAG, "onDispose");
                        for (UploadingImage uploadingImage: mImageList) {
                            if (uploadingImage.getStatus() != UploadingImage.STATUS_FINISHED) {
                                uploadingImage.setStatus(UploadingImage.STATUS_CANCEL);
                            }
                        }
                    }
                })
                .subscribe(new Consumer<UploadingImage>() {
                    @Override
                    public void accept(UploadingImage image) throws Exception {
                        Cog.d(TAG, "onNext value=", image.getPath());
                        image.setStatus(UploadingImage.STATUS_FINISHED);
                        if (mListener != null) {
                            mListener.onEachComplete(image);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "onError e=", throwable);
                        throwable.printStackTrace();
                        for (UploadingImage uploadingImage: mImageList) {
                            if (uploadingImage.getStatus() != UploadingImage.STATUS_FINISHED) {
                                uploadingImage.setStatus(UploadingImage.STATUS_ERROR);
                            }
                        }
                        if (mListener != null) mListener.onFinish();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mIsUploading = false;
                        Cog.d(TAG, "onComplete");
                        if (mListener != null) {
                            mListener.onFinish();
                        }
                    }
                });
    }

    public void stop() {
        if (mDisposable != null) mDisposable.dispose();
    }

    public interface UploadListener {
        void onStart();
        void onEachComplete(UploadingImage image);
        void onFinish();
    }
}

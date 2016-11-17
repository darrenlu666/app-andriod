package com.codyy.erpsportal.weibo.widgets;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoVideoActivity;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoImageFilpperDialog;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-1-15.
 */
public class WeiBoMediaView extends android.support.v7.widget.GridLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    /***
     * 播放
     */
    public final static int TYPE_PLAY = 0x001;
    /**
     * 暂停
     */
    public final static int TYPE_PAUSE = TYPE_PLAY + 1;
    public static final int TYPE_AUDIO = 0x001;
    public static final int TYPE_VIDEO = 0x002;
    /**
     * 一张图片时的宽度 dp
     */
    private static final int ONE_IMAGE_WIDTH = 150;
    /**
     * 一张图片时的宽度 dp
     */
    private static final int THREE_IMAGE_WIDTH = 90;
    /**
     * 一张图片时的高度 dp
     */
    private static final int THREE_IMAGE_HEIGHT = 140;
    /**
     * 一张图片时的高度 dp
     */
    private static final int ONE_IMAGE_HEIGHT = 200;
    private ArrayList<WeiBoListInfo.ImageListEntity> mListEntities;
    private Context mContext;
    private int mPadding;
    private int mImageHigth;
    private onMediaPlay onMediaPlay;
    private int mAudioType;

    public WeiBoMediaView(Context context) {
        super(context);
        init(context, null);
    }

    public WeiBoMediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setOnMediaPlay(WeiBoMediaView.onMediaPlay onMediaPlay) {
        this.onMediaPlay = onMediaPlay;
    }

    public WeiBoMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mImageHigth = UIUtils.dip2px(mContext, 100);
        mPadding = UIUtils.dip2px(mContext, 5);
        setOrientation(GridLayout.HORIZONTAL);
    }

    public void setListEntities(ArrayList<WeiBoListInfo.ImageListEntity> listEntities) {
        this.mListEntities = listEntities;
        this.removeAllViews();
        if (mImageHigth != 0) {
            addImageView();
        }
    }

    /**
     *
     */
    private void addImageView() {
        if (mListEntities != null) {
            int size = mListEntities.size();
            if (size == 1) {
                setColumnCount(2);
                setRowCount(1);
                LayoutParams layout = new LayoutParams(GridLayout.spec(0), GridLayout.spec(0));
                layout.width = UIUtils.dip2px(mContext, ONE_IMAGE_WIDTH);
                layout.height = UIUtils.dip2px(mContext, ONE_IMAGE_HEIGHT);
                //指定该组件占满容器
                layout.setGravity(Gravity.FILL);
                final SimpleDraweeView imageView = getDraweeView(layout, mListEntities.get(0).getImage());
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageClick(imageView, 0, mListEntities);
//                        WeiBoImageActivity.start(mContext, 0, mListEntities);
                    }
                });
                imageView.setTag(getTagByPosition(0));
                this.addView(imageView, layout);
            } else if (size == 4) {
                setColumnCount(3);
                setRowCount(2);
                for (int i = 0; i < size; i++) {
                    LayoutParams layout = new LayoutParams(GridLayout.spec(i / 2), GridLayout.spec(i % 2));
                    layout.height = mImageHigth;
                    layout.width = mImageHigth;
                    //指定该组件占满容器
                    layout.setGravity(Gravity.FILL);
                    layout.setMargins(mPadding, mPadding, mPadding, mPadding);
                    final SimpleDraweeView imageView = getDraweeView(layout, mListEntities.get(i).getImage());
                    final int page = i;
                    imageView.setTag(getTagByPosition(i));
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageClick(imageView, page, mListEntities);
//                            WeiBoImageActivity.start(mContext, page, mListEntities);
                        }
                    });
                    this.addView(imageView, layout);
                }
            } else {
                setColumnCount(3);
                setRowCount(size / 3 + 1);
                for (int i = 0; i < size; i++) {
                    LayoutParams layout = new LayoutParams(GridLayout.spec(i / 3), GridLayout.spec(i % 3));
                    layout.width = mImageHigth;
                    layout.height = mImageHigth;
                    final int page = i;
                    final SimpleDraweeView imageView = getDraweeView(layout, mListEntities.get(i).getImage());
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageClick(imageView, page, mListEntities);
//                            WeiBoImageActivity.start(mContext, page, mListEntities);
                        }
                    });
                    imageView.setTag(getTagByPosition(i));
                    layout.setMargins(mPadding, mPadding, mPadding, mPadding);
                    this.addView(imageView, layout);
                }
            }
        }
    }

    private String getTagByPosition(int position) {
        return "imageview---" + position;
    }

    /**
     * 图片点击
     *
     * @param index
     * @param mListEntities
     */
    private void imageClick(final View view, int index, ArrayList<WeiBoListInfo.ImageListEntity> mListEntities) {
//        WeiBoImageActivity.start(mContext, index, mListEntities);
        WeiBoImageFilpperDialog dialog = WeiBoImageFilpperDialog.newInstance(mListEntities, index);
        dialog.setOnShowing(new WeiBoImageFilpperDialog.OnShowing() {
            @Override
            public View getStartView(int position) {
                return findViewWithTag(getTagByPosition(position));
            }

            @Override
            public View getEndView(int position) {
                return findViewWithTag(getTagByPosition(position));
            }
        });
        dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "");
    }

    private SimpleDraweeView getDraweeView(LayoutParams layout, String url) {
        SimpleDraweeView imageView = new SimpleDraweeView(mContext);
        imageView.setLayoutParams(layout);
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.placeholder_middle);
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(getSmall(url)));
        imageRequestBuilder.setResizeOptions(new ResizeOptions(
                imageView.getLayoutParams().width,
                imageView.getLayoutParams().height));
        ImageRequest imageRequest = imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true).build();
        PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setAutoPlayAnimations(true);
        if (ImageFetcher.getInstance(mContext).mShowImage) {
            draweeControllerBuilder.setImageRequest(imageRequest);
        }
        imageView.setController(draweeControllerBuilder.build());
        return imageView;
    }

    private String getSmall(String url) {

        StringBuilder stringBuilder = null;
        if (url != null) {
            stringBuilder = new StringBuilder(url);
            int a = stringBuilder.lastIndexOf(".");
            if (a > 0) {
                return stringBuilder.insert(a, ".small").toString();
            }
        }
        return url;
    }

    public void setAudio(final String url, int type, final int position) {
        mAudioType = type;
        removeAllViews();
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_weibo_listitem_audio, this, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.weibo_audio_play);
        if (type == TYPE_PLAY) {
            imageView.setImageResource(R.drawable.weibo_audio_play);
        } else {
            imageView.setImageResource(R.drawable.weibo_audio_pause);
        }
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMediaPlay != null && !TextUtils.isEmpty(url)) {
                    if (mAudioType == TYPE_PLAY) {
                        mAudioType = TYPE_PAUSE;
                        onMediaPlay.play(url, position);
                        ((ImageView) v).setImageResource(R.drawable.weibo_audio_pause);
                    } else {
                        mAudioType = TYPE_PLAY;
                        ((ImageView) v).setImageResource(R.drawable.weibo_audio_play);
                        onMediaPlay.pause(position);
                    }
                } else {
                    Snackbar.make(v, "播放地址错误！", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        setColumnCount(1);
        setRowCount(1);
        LayoutParams layout = new LayoutParams(GridLayout.spec(0), GridLayout.spec(0, (float) 1));
        rootView.setLayoutParams(layout);
        addView(rootView, layout);
    }

    public void setVideo(final String url, String thumb, boolean isLocal, final String statue) {
        removeAllViews();
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_weibo_listitem_video, this, false);
        if (thumb != null) {
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) rootView.findViewById(R.id.weibo_listitem_video_simpledrawview);
            if (isLocal) {
                simpleDraweeView.setImageURI(Uri.parse(thumb));
            } else {
                if (thumb.endsWith("/img_class.jpg")) {
                    simpleDraweeView.setImageURI(Uri.parse(thumb));
                } else {
                    simpleDraweeView.setImageURI(Uri.parse(getSmall(thumb)));
                }
            }
            ImageView play = (ImageView) rootView.findViewById(R.id.weibo_listitem_video_play);
            play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("TRANS_SUCCESS".equals(statue)) {
                        WeiBoVideoActivity.start(mContext, url);
                    } else if ("TRANS_FAILED".equals(statue)) {
                        ToastUtil.showToast(mContext, "视频转换失败!");
                    } else {
                        ToastUtil.showToast(mContext, "视频转换中，稍后刷新列表再试！", Toast.LENGTH_LONG);
                    }
                }
            });
        }
        setColumnCount(1);
        setRowCount(1);
        LayoutParams layout = new LayoutParams(GridLayout.spec(0), GridLayout.spec(0, (float) 1));
        rootView.setLayoutParams(layout);
        addView(rootView, layout);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onGlobalLayout() {
    }

    public interface onMediaPlay {
        void play(String url, int position);

        void pause(int position);
    }
}

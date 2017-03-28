package com.codyy.erpsportal.classroom.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.ClassRoomDetail;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播详细、视频播放页面
 * Created by ldh on 2016/08/17.
 * 多个视频播放、主辅课堂页面
 */
public class LiveVideoListPlayNewActivity extends FragmentActivity implements IFragmentMangerInterface{

    private String TAG = LiveVideoListPlayNewActivity.class.getSimpleName();

    private static final String EXTRA_USER_INFO = "user_info";

    private ViewPager mViewPager;

    /**
     * 上一个界面传来的课堂
     */
    private static ClassRoomDetail mClassRoomDetail;

    private List<ClassRoomInfo> mClassroomList = new ArrayList<>();

    private ClassroomsPagerAdapter mClassroomPagerAdapter;

    /**
     * 是否正在播放
     */
    public static boolean mIsPlayable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_bnvideo_viewpager);
        init();
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showPrompt();
    }

    public void init() {
        mIsPlayable = false;//init state
        mViewPager = (ViewPager) findViewById(R.id.live_class_viewpager);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Cog.d(TAG, "onPageScrolled position=" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Cog.d(TAG, "onPageSelected position=" + position);
                playCurrent();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        loadData();
    }

    private void updateViewPager() {
        if (null != mClassroomPagerAdapter && mClassroomList != null) {
            mViewPager.setAdapter(mClassroomPagerAdapter);
        } else {
            mClassroomPagerAdapter = new ClassroomsPagerAdapter(this);
            mViewPager.setAdapter(mClassroomPagerAdapter);
        }

        Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {
            @Override
            public void onNetError() {
            }

            @Override
            public void onContinue() {
                mIsPlayable = true;
                playCurrent();
            }
        });
    }

    private void playCurrent() {
        int current = mViewPager.getCurrentItem();
        BnVideoLayout2 videoLayout = mClassroomPagerAdapter.getBnVideoLayout(current);
        String playUrl = mClassroomList.get(current).getRooomUrl();
        if (!TextUtils.isEmpty(playUrl) && videoLayout != null) {
            playVideo(videoLayout, playUrl);
        }
    }

    private void playVideo(BnVideoLayout2 videoLayout, String url) {
        videoLayout.setUrl(url, BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
        videoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
    }

    /**
     * 显示提示
     */
    private void showPrompt() {
        SharedPreferences sharedPreferences = getSharedPreferences("hint", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("tourHint", true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("tourHint", false);
            editor.apply();
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            View view = getLayoutInflater().inflate(R.layout.navigation_layout, null);
            TextView promptTv = (TextView) view.findViewById(R.id.tv_prompt);
            promptTv.setText(R.string.slide_to_switch_video);
            ImageView imageView = (ImageView) view.findViewById(R.id.navigation_image);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.na_anim);
            imageView.startAnimation(animation);
            dialog.setContentView(view);
            dialog.show();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }

    private void loadData() {
        mClassroomList.clear();
        if (mClassRoomDetail != null) {
            ClassRoomInfo mainRoomInfo = new ClassRoomInfo(mClassRoomDetail.getSchoolName(), mClassRoomDetail.getMainUrl() + "/" + mClassRoomDetail.getStream());
            mClassroomList.add(mainRoomInfo);
            if (mClassRoomDetail.getReceiveInfoList() != null) {
                for (int i = 0; i < mClassRoomDetail.getReceiveInfoList().size(); i++) {
                    ClassRoomInfo receiveRoomInfo = new ClassRoomInfo(mClassRoomDetail.getReceiveInfoList().get(i).getReceiveName(), mClassRoomDetail.getMainUrl() + "/" + mClassRoomDetail.getReceiveInfoList().get(i).getStream());
                    mClassroomList.add(receiveRoomInfo);
                }
            }
        }
        updateViewPager();
    }

    public static void start(Activity activity, ClassRoomDetail classRoomDetail, UserInfo userInfo) {
        Intent intent = new Intent(activity, LiveVideoListPlayNewActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        mClassRoomDetail = classRoomDetail;
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        playCurrent();
    }

    @Override
    public FragmentManager getNewFragmentManager() {
        return getSupportFragmentManager();
    }

    /**
     * 教室实体类，存储教室名称和教室播放地址
     */
    private class ClassRoomInfo {
        private String roomName;
        private String rooomUrl;

        ClassRoomInfo(String name, String url) {
            this.roomName = name;
            this.rooomUrl = url;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRooomUrl() {
            return rooomUrl;
        }

        public void setRooomUrl(String rooomUrl) {
            this.rooomUrl = rooomUrl;
        }
    }


    private class ClassroomsPagerAdapter extends PagerAdapter {

        private LayoutInflater mLayoutInflater;

        private SparseArray<BnVideoLayout2> mBnVideoLayouts = new SparseArray<>();

        ClassroomsPagerAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mClassroomList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final String classroom = mClassroomList.get(position).getRooomUrl();
            View view = mLayoutInflater.inflate(R.layout.activity_live_video_play, container, false);
            TextView mTitleTv = (TextView) view.findViewById(R.id.txtTitleOfLiveVideoPlay);
            final BnVideoLayout2 videoLayout = (BnVideoLayout2) view.findViewById(R.id.bnVideoViewOfLiveVideoLayout);
            videoLayout.setVolume(100);
            videoLayout.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Cog.i(TAG, "surfaceCreated()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
                    //监测视频是否在fragment消失时被销毁?，如果没有则手动销毁一次
                    if (videoLayout != null && videoLayout.isPlaying()) {
                        Cog.i(TAG, "mVideoLayout is playing .............~~~~~~~~~~~~~~~");
                        videoLayout.stop();
                    }

                    //延迟2s执行视频恢复等待 stop销毁动作结束
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsPlayable) {
                                Cog.d(TAG, "startPlay mUrl=" + classroom);
                                if (TextUtils.isEmpty(classroom)) {
                                    Cog.e(TAG, "startPlay mUrl is NULL!");
                                    return;
                                }

                                videoLayout.setUrl(classroom, BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
                                videoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
                            }
                        }
                    }, 2 * 1000);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Cog.i(TAG, "surfaceDestroyed()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
                    videoLayout.stop();
                }
            });

            RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeHeadOfLiveVideoPlay);
            view.findViewById(R.id.imgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            String formatTitle = mClassroomList.get(position).getRoomName();//getString(R.string.title_live_class);
            mTitleTv.setText(String.format(formatTitle, ""));

            final AutoHideUtils autoHide = new AutoHideUtils(mRelativeLayout);
            videoLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    autoHide.showControl();
                    return false;
                }
            });
            autoHide.showControl();
            mBnVideoLayouts.append(position, videoLayout);
            container.addView(view);
            return view;
        }

        public BnVideoLayout2 getBnVideoLayout(int position) {
            return mBnVideoLayouts.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mBnVideoLayouts.delete(position);
        }
    }
}

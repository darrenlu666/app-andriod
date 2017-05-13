package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourPagerActivity.AppointmentInfo.ReceiveListBean;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourPagerActivity.ClassTourInfo.DetailBean;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourPagerActivity.ClassTourInfo.DetailBean.ReceiveTeacherListBean;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.TourClassroom;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.parsers.ClassTourClassroomNewParser;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播详细、视频播放页面
 * Created by caixingming on 2015/4/24.
 * <p>
 * 多个视频播放、主辅课堂页面
 */
public class ClassTourPagerActivity extends FragmentActivity implements IFragmentMangerInterface{

    private String TAG = "ClassTourPagerActivity";

    private static final String EXTRA_ID = "id";

    private static final String EXTRA_CLASSROOM = "video";

    private static final String EXTRA_USER_INFO = "user_info";

    private static final String EXTRA_TYPE = "type";

    private RelativeLayout mTitleBarRl;

    private ImageButton mReturnIb;

    private TextView mSchoolNameTv;

    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private Button mClassInfoBtn;
    private TextView mTvGrade, mTvSubject, mTvWeek, mTvCourseNum, mTvMainTeacher, mTvLbWeek;
    private LinearLayout mLlReceiverTeacher;
    private UserInfo mUserInfo;

    /**
     * 上一个界面传来的课堂
     */
    private TourClassroom mParentClassroom;

    private List<TourClassroom> mClassroomList = new ArrayList<>();

    private String mType;

    /**
     * 约课id
     */
    private String mId;// getClassWatchAll.do?scheduleDetailId = mId

    private ClassroomsPagerAdapter mClassroomPagerAdapter;

    private ClassTourClassroomNewParser mParser;

    public static boolean mIsPlayable = false;

    private RequestSender mRequestSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_class_tour_pager);
        initAttributes();
        initViews();
        loadData();
        loadClassTourInfo();
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showPrompt();
    }

    private void initAttributes() {
        mRequestSender = new RequestSender(ClassTourPagerActivity.this);
        mParser = new ClassTourClassroomNewParser();
        mIsPlayable = false;//init state
        mId = getIntent().getStringExtra(EXTRA_ID);
        mUserInfo = getIntent().getParcelableExtra(EXTRA_USER_INFO);
        mParentClassroom = getIntent().getParcelableExtra(EXTRA_CLASSROOM);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
    }

    private void initViews() {
        mTitleBarRl = (RelativeLayout) findViewById(R.id.rl_class_tour_title_bar);
        mReturnIb = (ImageButton) findViewById(R.id.ib_return);
        mSchoolNameTv = (TextView) findViewById(R.id.tv_school_name);
        mViewPager = (ViewPager) findViewById(R.id.live_class_viewpager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mClassInfoBtn = (Button) findViewById(R.id.btn_course_info);
        mTvGrade = (TextView) findViewById(R.id.tv_grade);
        mTvSubject = (TextView) findViewById(R.id.tv_subject);
        mTvWeek = (TextView) findViewById(R.id.tv_week);
        mTvCourseNum = (TextView) findViewById(R.id.tv_course_number);
        mTvMainTeacher = (TextView) findViewById(R.id.tv_main_teacher);
        mTvLbWeek = (TextView) findViewById(R.id.tv_lb_week);
        mLlReceiverTeacher = (LinearLayout) findViewById(R.id.ll_receiving_teachers);

        mReturnIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mClassInfoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Cog.d(TAG, "onPageScrolled position=" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Cog.d(TAG, "onPageSelected position=" + position);
                TourClassroom tourClassroom = mClassroomList.get(position);
                mSchoolNameTv.setText(tourClassroom.getSchoolName());
                playCurrent();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        TextView mainTeacherLbTv = (TextView) findViewById(R.id.lb_main_teacher);
        TextView receivingTeacherLbTv = (TextView) findViewById(R.id.tv_lb_receiving_teacher);
        mainTeacherLbTv.setText(getString(R.string.teacher_type_lb, Titles.sMasterTeacher));
        receivingTeacherLbTv.setText(getString(R.string.teacher_type_lb, Titles.sCoachTeacher));
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
            public void onNetError() {}

            @Override
            public void onContinue() {
                mIsPlayable = true;
                playCurrent();
            }
        });
    }

    private void playCurrent() {
        int current = mViewPager.getCurrentItem();
        final BnVideoLayout2 videoLayout = mClassroomPagerAdapter.getBnVideoLayout(current);
        final TourClassroom classroom = mClassroomList.get(current);
        if (!TextUtils.isEmpty(classroom.getVideoUrl())&& null != videoLayout) {
            playVideo(videoLayout, classroom.getVideoUrl());
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
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        String url;
        if (ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {//专递课堂
            url = URLConfig.SPECIAL_DELIVERY_CLASSROOM_VIDEOS;
        } else {//直录播课堂
            url = URLConfig.SCHOOL_NET_CLASSROOM_VIDEOS;
        }
        params.put("id", mId);
        mRequestSender.sendRequest(new RequestSender.RequestData(url, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    JSONArray jsonArray = response.optJSONArray("watchPath");
                    List<TourClassroom> classroomList = mParser.parseArray(jsonArray);
                    if (classroomList != null && classroomList.size() > 0) {
                        mClassroomList.clear();
                        mClassroomList.addAll(classroomList);
                        mSchoolNameTv.setText(mClassroomList.get(0).getSchoolName());
                        updateViewPager();
                    } else {
                        UIUtils.toast(ClassTourPagerActivity.this, "无法获取视频地址！", Toast.LENGTH_SHORT);
                        finish();
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
//                UiUtils.toast(LiveVideoListPlayActivity.this, R.string.net_error, Toast.LENGTH_SHORT);
                mClassroomList.clear();
                if (mParentClassroom != null) {
                    mClassroomList.add(mParentClassroom);
                    updateViewPager();
                }
//                finish();
            }
        }));
    }

    private void loadClassTourInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        String url;
        if (ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {//专递课堂
            url = URLConfig.SPECIAL_DELIVERY_CLASSROOM_CLASS_DETAIL;
        } else {//直录播课堂
            url = URLConfig.SCHOOL_NET_CLASSROOM_APPOINTMENT_INFO;
        }
        params.put("id", mId);

        mRequestSender.sendRequest(new RequestSender.RequestData(url, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if (ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {//专递课堂
                    try {
                        ClassTourInfo info = new Gson().fromJson(response.toString(), ClassTourInfo.class);
                        if (info != null && "success".equals(info.getResult())) {
                            DetailBean detailBean = info.getDetail();
                            if (detailBean != null) {
                                mTvGrade.setText(detailBean.getClasslevelName());
                                mTvSubject.setText(detailBean.getSubjectName());
                                mTvWeek.setText(TextUtils.isEmpty(detailBean.getWeekSeq()) ? "" : "第" + detailBean.getWeekSeq() + "周");
                                mTvCourseNum.setText(TextUtils.isEmpty(detailBean.getClassSeq()) ? "" : "第" + detailBean.getClassSeq() + "节");
                                mTvMainTeacher.setText(TextUtils.isEmpty(detailBean.getTeacherMobile()) ? detailBean.getTeacherName() : detailBean.getTeacherName() + "(" + detailBean.getTeacherMobile() + ")");
                                if (detailBean.getReceiveTeacherList() != null && detailBean.getReceiveTeacherList().size() > 0) {
                                    for (int i = 0; i < detailBean.getReceiveTeacherList().size(); i++) {
                                        TextView textView = new TextView(ClassTourPagerActivity.this);
                                        textView.setTextColor(ContextCompat.getColor(ClassTourPagerActivity.this, android.R.color.white));
//                                        textView.setBackgroundColor(Color.BLUE);
//                                        textView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                                        textView.setLinkTextColor(ContextCompat.getColor(ClassTourPagerActivity.this, android.R.color.white));
                                        ReceiveTeacherListBean receiveTeacherListBean = detailBean.getReceiveTeacherList().get(i);
                                        textView.setText(
                                                (TextUtils.isEmpty(receiveTeacherListBean.getTeacherName()) ? "未选择教师" : receiveTeacherListBean.getTeacherName())
                                                + (TextUtils.isEmpty(receiveTeacherListBean.getTeacherMobile()) ? "" : "("
                                                + receiveTeacherListBean.getTeacherMobile() + ")")
                                                + "\n" + receiveTeacherListBean.getSchoolName());
                                        if ( i != 0) {
                                            setMarginTop(textView);
                                        }
                                        mLlReceiverTeacher.addView(textView);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//直录播课堂
                    try {
                        AppointmentInfo info = new Gson().fromJson(response.toString(), AppointmentInfo.class);
                        if (info != null && "success".equals(info.getResult())) {
                            mTvGrade.setText(info.getClasslevelName());
                            mTvSubject.setText(info.getSubjectName());
                            mTvLbWeek.setVisibility(View.GONE);
                            mTvWeek.setVisibility(View.GONE);
                            mTvCourseNum.setText("第" + info.getClassSeq() + "节");
                            mTvMainTeacher.setText(TextUtils.isEmpty(info.getContact()) ? info.getSpeakUserName() : info.getSpeakUserName() + "(" + info.getContact() + ")");
                            if (info.getReceiveList() != null && info.getReceiveList().size() > 0) {
                                for (int i = 0; i < info.getReceiveList().size(); i++) {
                                    TextView textView = new TextView(ClassTourPagerActivity.this);
                                    textView.setTextColor(ContextCompat.getColor(ClassTourPagerActivity.this, android.R.color.white));
                                    textView.setLinkTextColor(ContextCompat.getColor(ClassTourPagerActivity.this, android.R.color.white));
                                    ReceiveListBean receiveListBean = info.getReceiveList().get(i);
                                    textView.setText(
                                            (TextUtils.isEmpty(receiveListBean.getHelpUserName())?"未选择教师" : receiveListBean.getHelpUserName())
                                            + (TextUtils.isEmpty(receiveListBean.getContact()) ? "" : "("
                                            + receiveListBean.getContact() + ")")
                                            + "\n" + receiveListBean.getSchoolName());
                                    if ( i != 0) {
                                        setMarginTop(textView);
                                    }
                                    mLlReceiverTeacher.addView(textView);
                                }
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse:" + error);
            }
        }));
    }

    private void setMarginTop(TextView textView) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.dp8);
        textView.setLayoutParams(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop();
    }

    public static void start(Activity activity, TourClassroom classroom, UserInfo userInfo, String type) {
        Intent intent = new Intent(activity, ClassTourPagerActivity.class);
        intent.putExtra(EXTRA_ID, classroom.getId());
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_CLASSROOM, classroom);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    public FragmentManager getNewFragmentManager() {
        return getSupportFragmentManager();
    }


    private class ClassroomsPagerAdapter extends PagerAdapter {

        private LayoutInflater mLayoutInflater;

        private SparseArray<BnVideoLayout2> mBnVideoLayouts = new SparseArray<>();

        public ClassroomsPagerAdapter(Context context) {
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
            final TourClassroom classroom = mClassroomList.get(position);
            View view = mLayoutInflater.inflate(R.layout.pager_class_tour, container, false);
//            TextView mTitleTv = (TextView) view.findViewById(R.id.txtTitleOfLiveVideoPlay);
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

//                    videoLayout.setUrl(classroom.getVideoUrl(),BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
//                    videoLayout.play(BnVideoView2.BN_PLAY_TYPE_1);

                    //延迟2s执行视频恢复等待 stop销毁动作结束
                  /*  new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {*/
                            if (mIsPlayable) {
                                Cog.d(TAG, "startPlay mUrl=" + classroom.getVideoUrl());
                                if (TextUtils.isEmpty(classroom.getVideoUrl())) {
                                    Cog.e(TAG, "startPlay mUrl is NULL!");
                                    return;
                                }

                                videoLayout.setUrl(classroom.getVideoUrl(), BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
                                videoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
                                videoLayout.setTimeOut(15);
                            }
                     /*   }
                    }, 2 * 1000);*/
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder) { }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Cog.i(TAG, "surfaceDestroyed()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
                    videoLayout.stop();
                }
            });

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

    class AppointmentInfo {

        /**
         * classSeq : 18542
         * classlevelName : 测试内容52rf
         * contact : 测试内容6vjk
         * receiveList : [{"contact":"测试内容7i56","helpUserName":"测试内容5udv","schoolName":"测试内容qn8g"}]
         * result : 测试内容ubw7
         * speakUserName : 测试内容cp5t
         * subjectName : 测试内容96a8
         */

        private String classSeq;
        private String classlevelName;
        private String contact;
        private String result;
        private String speakUserName;
        private String subjectName;
        /**
         * contact : 测试内容7i56
         * helpUserName : 测试内容5udv
         * schoolName : 测试内容qn8g
         */

        private List<ReceiveListBean> receiveList;

        public String getClassSeq() {
            return classSeq;
        }

        public void setClassSeq(String classSeq) {
            this.classSeq = classSeq;
        }

        public String getClasslevelName() {
            return classlevelName;
        }

        public void setClasslevelName(String classlevelName) {
            this.classlevelName = classlevelName;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getSpeakUserName() {
            return speakUserName;
        }

        public void setSpeakUserName(String speakUserName) {
            this.speakUserName = speakUserName;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public List<ReceiveListBean> getReceiveList() {
            return receiveList;
        }

        public void setReceiveList(List<ReceiveListBean> receiveList) {
            this.receiveList = receiveList;
        }

        public class ReceiveListBean {
            private String contact;
            private String helpUserName;
            private String schoolName;

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getHelpUserName() {
                return helpUserName;
            }

            public void setHelpUserName(String helpUserName) {
                this.helpUserName = helpUserName;
            }

            public String getSchoolName() {
                return schoolName;
            }

            public void setSchoolName(String schoolName) {
                this.schoolName = schoolName;
            }
        }
    }

    class ClassTourInfo {

        /**
         * result : success
         * detail : {"classSeq":"二","classlevelName":"一年级","receiveTeacherList":[{"schoolName":"别用我","teacherMobile":"","teacherName":""},{"schoolName":"别用我","teacherMobile":"","teacherName":""}],"subjectName":"数学","teacherMobile":"","teacherName":"t2&lt;input/&gt;","weekSeq":"12"}
         */

        private String result;
        /**
         * classSeq : 二
         * classlevelName : 一年级
         * receiveTeacherList : [{"schoolName":"别用我","teacherMobile":"","teacherName":""},{"schoolName":"别用我","teacherMobile":"","teacherName":""}]
         * subjectName : 数学
         * teacherMobile :
         * teacherName : t2&lt;input/&gt;
         * weekSeq : 12
         */

        private DetailBean detail;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public DetailBean getDetail() {
            return detail;
        }

        public void setDetail(DetailBean detail) {
            this.detail = detail;
        }

        public class DetailBean {
            private String classSeq;
            private String classlevelName;
            private String subjectName;
            private String teacherMobile;
            private String teacherName;
            private String weekSeq;
            /**
             * schoolName : 别用我
             * teacherMobile :
             * teacherName :
             */

            private List<ReceiveTeacherListBean> receiveTeacherList;

            public String getClassSeq() {
                return classSeq;
            }

            public void setClassSeq(String classSeq) {
                this.classSeq = classSeq;
            }

            public String getClasslevelName() {
                return classlevelName;
            }

            public void setClasslevelName(String classlevelName) {
                this.classlevelName = classlevelName;
            }

            public String getSubjectName() {
                return subjectName;
            }

            public void setSubjectName(String subjectName) {
                this.subjectName = subjectName;
            }

            public String getTeacherMobile() {
                return teacherMobile;
            }

            public void setTeacherMobile(String teacherMobile) {
                this.teacherMobile = teacherMobile;
            }

            public String getTeacherName() {
                return teacherName;
            }

            public void setTeacherName(String teacherName) {
                this.teacherName = teacherName;
            }

            public String getWeekSeq() {
                return weekSeq;
            }

            public void setWeekSeq(String weekSeq) {
                this.weekSeq = weekSeq;
            }

            public List<ReceiveTeacherListBean> getReceiveTeacherList() {
                return receiveTeacherList;
            }

            public void setReceiveTeacherList(List<ReceiveTeacherListBean> receiveTeacherList) {
                this.receiveTeacherList = receiveTeacherList;
            }

            public class ReceiveTeacherListBean {
                private String schoolName;
                private String teacherMobile;
                private String teacherName;

                public String getSchoolName() {
                    return schoolName;
                }

                public void setSchoolName(String schoolName) {
                    this.schoolName = schoolName;
                }

                public String getTeacherMobile() {
                    return teacherMobile;
                }

                public void setTeacherMobile(String teacherMobile) {
                    this.teacherMobile = teacherMobile;
                }

                public String getTeacherName() {
                    return teacherName;
                }

                public void setTeacherName(String teacherName) {
                    this.teacherName = teacherName;
                }
            }
        }
    }
}

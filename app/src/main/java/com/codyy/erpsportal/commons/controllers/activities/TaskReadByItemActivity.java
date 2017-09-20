package com.codyy.erpsportal.commons.controllers.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.homework.controllers.fragments.AddOverallCommentFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemIndexDialog;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.homework.widgets.AudioBar;
import com.codyy.erpsportal.homework.widgets.MyViewPager;
import com.codyy.erpsportal.homework.widgets.PressBar;
import com.codyy.erpsportal.homework.widgets.SlidingFloatScrollView;
import com.codyy.erpsportal.homework.widgets.WorkAnswerMediaPlayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;


/**
 * 按题批阅界面(抽象)
 * Created by ldh on 2016/2/4.
 */
public abstract class TaskReadByItemActivity extends ToolbarActivity implements SlidingFloatScrollView.OnScrollListener, PressBar.RecordListener {
    private static final String TAG = TaskReadByItemActivity.class.getSimpleName();
    @Bind(R.id.title_layout)
    protected Toolbar mToolbar;

    @Bind(R.id.viewpager_item_info)
    protected MyViewPager mItemInfoViewPager;//习题信息滑动部分
    protected TabsAdapter mTabsAdapter;
    protected RequestSender mRequestSender;
    private Map<String, String> mParams;

    protected int mCurrentItemIndex = 0;//当前题号mCurrentItemIndex
    private int mPreIndex = -1;//前一个浏览的题号
    protected List<ItemInfoClass> mItemInfoList;//题目信息
    private ArrayList<ItemInfoClass> mItemInfoNewList;//新的题目信息（添加标题）
    protected String mWorkId;//作业id
    protected String mCurrentStuId;//当前学生id
    protected String mCurrentWorkItemId;//当前题目id
    protected TaskReadDao mTaskReadDao = null;//批阅内容存储类
    /**
     * 底部语音录制按钮
     */
    /*protected PressBar mPressBar;*/

    /**
     * 音频条容器
     */
    private LinearLayout mAudioCommentLayout;
    /**
     * 自定义的滑动ScrollView，监听滑动距离大于一定数值时，隐藏题目信息
     */
    protected SlidingFloatScrollView mStuAnswerInfoSfsv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParams = new HashMap<>();
        addParams(mParams);
        mRequestSender = new RequestSender(this);
        onViewBound();
        initAudioCommentView();
    }

    private void initAudioCommentView() {
        mTaskReadDao = new TaskReadDao(this);
        mAudioCommentLayout = (LinearLayout) findViewById(R.id.ll_audio_comment_list);

/*        mPressBar = (PressBar) findViewById(R.id.pb_dialog);
        mPressBar.setAudioRecorder(new AudioRecorder(this));
        mPressBar.setRecordListener(this);*/
    }

    protected void addHistoryAudioInfo() {
        mAudioCommentLayout.removeAllViews();
        List<TaskReadDao.TaskItemReadAudioInfo> list = mTaskReadDao.queryCommentAudio(mCurrentStuId, mWorkId, mCurrentWorkItemId, 0);
        if (list == null || list.size() == 0) return;
        for (int i = 0; i < list.size(); i++) {
            addAudioBarInLayout(list.get(i).getAudioUrl(), true);
        }
    }

    @Override
    public void RecordEnd(String recordFilePath, int duration) {
        addAudioBarInLayout(recordFilePath, false);
        pullToDown();
    }

    private void addAudioBarInLayout(final String recordFilePath, final boolean isSave) {
        final AudioBar audioBar = new AudioBar(this);
        audioBar.setTag(recordFilePath);
        audioBar.setUrl(recordFilePath, WorkUtils.getUploadAudioUrl(UserInfoKeeper.obtainUserInfo()),true);
        audioBar.setProgressVisible(isSave ? false : true);
        audioBar.setOnAudioLongClickListener(new AudioBar.OnAudioListener() {
            @Override
            public void longClick(String tag) {
                deleteAudioCommentBar(tag);
            }

            @Override
            public void insertAudioInfo(String originalName, String messageId) {
                if (mTaskReadDao != null && !isSave) {
                    mTaskReadDao.insertCommentAudio(mCurrentStuId, mWorkId, mCurrentWorkItemId, recordFilePath, audioBar.getLength(), originalName, messageId, 0);
                }
            }
        });
        if (!isSave && mTaskReadDao.queryCommentAudio(mCurrentStuId, mWorkId, mCurrentWorkItemId, 0).size() >= AddOverallCommentFragment.MAX_AUDIO_COMMENT_COUNT) {
            Toast.makeText(this, "音频评论数量最多10条", Toast.LENGTH_SHORT).show();
            return;
        }
        mAudioCommentLayout.addView(audioBar);
    }

    /**
     * 删除语音评论条
     *
     * @param tag
     */
    private void deleteAudioCommentBar(String tag) {
        if (mAudioCommentLayout.findViewWithTag(tag) != null) {
            mAudioCommentLayout.removeView(mAudioCommentLayout.findViewWithTag(tag));
            //如果在播放，停止播放
            MediaPlayer mp = WorkAnswerMediaPlayer.newInstance();
            if (mp != null && mp.isPlaying()) {
                mp.stop();
            }
        }
        if (mTaskReadDao != null) {
            mTaskReadDao.deleteCommentAudio(mCurrentStuId, mWorkId, mCurrentWorkItemId, tag, 0);
        }
    }

    /**
     * 将界面滚动到底部
     */
    protected void pullToDown() {
        mStuAnswerInfoSfsv.postDelayed(new Runnable() {//将界面滑动到最下方
            @Override
            public void run() {
                mStuAnswerInfoSfsv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 200);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_work_progress, menu);
        final MenuItem menuItem = menu.findItem(R.id.task_read_progress_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextView textView = (TextView) linearLayout.findViewById(R.id.task_title);
        textView.setText(menuItem.toString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mItemInfoNewList = switchList((ArrayList<ItemInfoClass>) mItemInfoList, mCurrentItemIndex, mPreIndex, true);
                mPreIndex = mCurrentItemIndex;
                bundle.putParcelableArrayList(WorkItemIndexDialog.ARG_DATA, mItemInfoNewList);
                bundle.putBoolean(WorkItemIndexDialog.ARG_SHOW_FIRST_PAGE, false);
                final WorkItemIndexDialog dialog = new WorkItemIndexDialog();
                dialog.setArguments(bundle);
                dialog.setOnItemIndexClickListener(new WorkItemIndexDialog.onItemIndexClickListener() {
                    @Override
                    public void onBtnItemIndexClick(int itemIndex) {
                        mItemInfoViewPager.setCurrentItem(getPositionOfIndex(mItemInfoList, itemIndex + 1), true);
                        dialog.dismiss();
                    }
                });
                dialog.show(getSupportFragmentManager(), WorkItemIndexDialog.TAG);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private int getPositionOfIndex(List<ItemInfoClass> itemInfoNewList, int itemIndex) {
        int position = 0;
        for (int i = 0; i < itemInfoNewList.size(); i++) {
            if (itemInfoNewList.get(i).getWorkItemIndex() == itemIndex) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 重新封装题目信息，以便题目索引界面使用
     *
     * @param list
     * @param currentIndex
     * @param preIndex
     * @param isView
     * @return
     */
    @Nullable
    private ArrayList<ItemInfoClass> switchList(ArrayList<ItemInfoClass> list, int currentIndex, int preIndex, boolean isView) {
        ArrayList<ItemInfoClass> newList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ItemInfoClass itemInfoClass = list.get(i);
                if (i == 0) {
                    ItemInfoClass titleItemInfo = changeItemType(itemInfoClass.getWorkItemType());
                    newList.add(titleItemInfo);
                    newList.add(itemInfoClass);
                } else {
                    if (!itemInfoClass.getWorkItemType().equals(list.get(i - 1).getWorkItemType())) {
                        ItemInfoClass titleItemInfo = changeItemType(itemInfoClass.getWorkItemType());
                        newList.add(titleItemInfo);
                        newList.add(itemInfoClass);
                    } else
                        newList.add(itemInfoClass);
                }
                if (isView) {
                    if (currentIndex == i) {//找出被选中的题目，将其置红
                        itemInfoClass.setColor(getResources().getColor(R.color.work_item_index_color_selected));
                    }
                    if (preIndex == i && preIndex != currentIndex) {//找出前一个被选中的题目，将其置灰
                        itemInfoClass.setColor(getResources().getColor(R.color.work_item_index_color));
                    }
                }
            }
            return newList;
        }
        return null;
    }

    /**
     * 设置题目类型
     *
     * @param workItemType
     * @return
     */
    private ItemInfoClass changeItemType(String workItemType) {
        ItemInfoClass titleItemInfo = new ItemInfoClass();
        switch (workItemType) {
            case TaskFragment.TYPE_SINGLE_CHOICE:
                titleItemInfo.setWorkItemType("单选题");
                break;
            case TaskFragment.TYPE_MULTI_CHOICE:
                titleItemInfo.setWorkItemType("多选题");
                break;
            case TaskFragment.TYPE_JUDGEMENT:
                titleItemInfo.setWorkItemType("判断题");
                break;
            case TaskFragment.TYPE_FILL_IN_BLANK:
                titleItemInfo.setWorkItemType("填空题");
                break;
            case TaskFragment.TYPE_ASK_ANSWER:
                titleItemInfo.setWorkItemType("问答题");
                break;
            case TaskFragment.TYPE_COMPUTING:
                titleItemInfo.setWorkItemType("计算题");
                break;
            case TaskFragment.TYPE_TEXT:
                titleItemInfo.setWorkItemType("文本题");
                break;
            case TaskFragment.TYPE_FILE:
                titleItemInfo.setWorkItemType("附件题");
                break;
        }
        return titleItemInfo;
    }


    protected void onViewBound() {
        mRequestSender.sendRequest(new RequestSender.RequestData(getUrl(), mParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, response);
                mTabsAdapter = new TabsAdapter(TaskReadByItemActivity.this, getSupportFragmentManager(), mItemInfoViewPager);
                addFragments(response);
                mItemInfoViewPager.setAdapter(mTabsAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                //Cog.e(TAG, error.getMessage());
            }
        }));
    }

    /**
     * 调用addFragment方法，把Fragment加入
     */
    protected abstract void addFragments(JSONObject response);

    /**
     * 添加fragment
     *
     * @param title
     * @param clazz
     * @param bundle
     */
    protected void addFragment(String title, Class<? extends Fragment> clazz, Bundle bundle) {
        mTabsAdapter.addTab(title, clazz, bundle);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_read_by_item;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    /**
     * 添加请求参数
     *
     * @param param
     */
    protected void addParams(Map<String, String> param) {

    }

    protected void addParam(String key, String value) {
        mParams.put(key, value);
    }

    /**
     * 请求地址
     *
     * @return
     */
    protected abstract String getUrl();


}

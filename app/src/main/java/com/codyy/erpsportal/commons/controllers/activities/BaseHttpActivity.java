package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.common.EndlessRecyclerOnScrollListener;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import org.json.JSONObject;
import java.util.HashMap;
import butterknife.ButterKnife;

/**
 * 1.ButterKnife包裹基础类型
 * 2.对数据请求进行了过程回调封装，增加易读性
 * 3.增加返回动画
 * 4.增加ToolBar初始化
 * 5.增加Menu筛选功能{@link #setFilterListener(IFilterListener)}
 * Created by poe on 16-1-19.
 */
public abstract class BaseHttpActivity extends AppCompatActivity{
    private final static String TAG = "BaseHttpActivity";
    private static final float TRANSLATION_Y = -38;
    /**
     * 单页加载的数据
     */
    public static int sPageCount = 10;
    /**
     * 网络请求
     */
    protected RequestSender mSender;
    protected UserInfo mUserInfo ;
    private IFilterListener mFilterListener;
    private int mCurrentPageIndex = 1 ;//当前默认页面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(obtainLayoutId());
        ButterKnife.bind(this);
        preInit();
        init();
    }

    /**
     * 布局id
     * @return 当前布局
     */
    public abstract @LayoutRes int obtainLayoutId();

    /**
     * 获取数据的API.
     * @return api
     */
    public abstract String obtainAPI();

    /**
     * 参数拼接
     * @return 数据请求parmas
     */
    public abstract HashMap<String,String> getParam();

    /**
     * 初始化视图的初始化
     */
    public abstract  void init();

    /**
     * 数据请求返回结果
     * @param response 数据请求成功返回
     */
    public abstract void onSuccess(JSONObject response) throws Exception;

    /**
     * 数据请求错误
     * @param error 数据请求失败
     */
    public abstract void onFailure(VolleyError error)throws Exception;

    /**
     * 基本数据操作involved by {Activity#OnCrate(SavedInstance)}
     */
    public  void preInit(){
        mUserInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
        if(null == mUserInfo){
            mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        }
        mSender = new RequestSender(this);
    }

    /**
     * 请求数据
     */
    public void requestData(){
        requestData(obtainAPI(), getParam(), new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log(e);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                try {
                    onFailure(error);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.log(e);
                }
            }
        });
    }

    /**
     * 请求GET网络请求 .
     * @param url request url .
     * @param params request params .
     * @param requestListener request listener .
     */
    public void requestData(String url , HashMap<String ,String> params , final IRequest requestListener){

        if(!NetworkUtils.isConnected()){
            ToastUtil.showToast(getString(R.string.net_error));
            return;
        }

        mSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(null != requestListener){
                    try {
                        requestListener.onRequestSuccess(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.log(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(null != requestListener){
                    requestListener.onRequestFailure(error);
                }
                ToastUtil.showToast(getString(R.string.net_connect_error));
                LogUtils.log(error);
            }
        },TAG));
    }

    /**
     * !
     * 设置此项代表你需要筛选功能
     * @param mFilterListener set the filter listener .
     */
    public void setFilterListener(IFilterListener mFilterListener) {
        this.mFilterListener = mFilterListener;
        supportInvalidateOptionsMenu();
    }

    /**
     * 退出应用时调用
     */
    private void stopRequest(){
        Cog.i(TAG,"stopREquest() ~");
        if(null != mSender)
            mSender.stop(TAG);
    }

    protected void initToolbar(Toolbar toolbar) {
        if (toolbar == null)
            return;
        toolbar.setTitle("");
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn_bg);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                UIUtils.addExitTranAnim(BaseHttpActivity.this);
            }
        });
    }

    /**
     * 设置自动加载更多 默认不会自动加载更多...
     * @param recyclerView the recycler view which will auto load more .
     */
    protected void enableLoadMore(RecyclerView recyclerView){
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                Cog.d(TAG , "current page index :"+current_page);
                //更新下拉刷新...
                if(current_page != mCurrentPageIndex){
                    mCurrentPageIndex = current_page;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //加载view停留1s ，防止太快闪现!
                            SystemClock.sleep(500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    requestData();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 拟物动画
     * add by eachann
     *
     * @param isPlus is plus
     * @param view your view .
     */
    protected void setViewAnim(boolean isPlus, View... view) {
        float translationY = UIUtils.dip2px(this, isPlus ? -TRANSLATION_Y : TRANSLATION_Y);
        for (View v : view) {
            v.setAlpha(0.8f);
            v.setTranslationY(translationY);
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(300L)
                    .setDuration(200L)
                    .setInterpolator(new FastOutLinearInInterpolator())
                    .start();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            UIUtils.addExitTranAnim(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        stopRequest();
        Cog.i(TAG,"unbind() ~");
        ButterKnife.unbind(this);
        Cog.i(TAG,"stopREquest() ~");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(null == mFilterListener){
            menu.setGroupVisible(0,false);
            menu.getItem(0).setIcon(R.drawable.ic_filter);
        }else{
            mFilterListener.onPreFilterCreate(menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                if(null != mFilterListener){
                    mFilterListener.onFilterClick(item);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 网络请求回调
     */
    public interface IRequest{
        /**
         * success
         * @param response result data .
         */
        void onRequestSuccess(JSONObject response) throws Exception;

        /**
         * fail 失败
         * @param error error message .
         */
        void onRequestFailure(VolleyError error);
    }

    /**
     * 筛选listener，提供子类方便实现筛选功能
     */
    public interface IFilterListener{
        /**
         * 选中某个item
         * @param item the filter item .
         */
        void onFilterClick(MenuItem item);

        /**
         * do some UI change before filter view crated
         */
        void onPreFilterCreate(Menu menu);

    }
}

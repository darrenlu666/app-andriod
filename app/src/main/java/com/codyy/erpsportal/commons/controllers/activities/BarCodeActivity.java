package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclingPagerAdapter;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONObject;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 分享二维码
 * Created by poe on 10/05/17.
 */

public class BarCodeActivity extends BaseHttpActivity {
    private static final String TAG = "BarCodeActivity";

    @Bind(R.id.save_tv)
    TextView mSaveTv;
    @Bind(R.id.share_tv)
    TextView mShareTv;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.app_tv)
    TextView mAppTv;
    @Bind(R.id.indicator)
    CirclePageIndicator mIndicator;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_bar_code_share;
    }

    @Override
    public String obtainAPI() {
        return null;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) throws Exception {
        return null;
    }

    @Override
    public void init() {
        //test viewPager .
        PictureAdapter adapter = new PictureAdapter();
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) throws Exception {

    }

    @Override
    public void onFailure(Throwable error) throws Exception {

    }

    @OnClick({R.id.save_tv, R.id.share_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_tv:
                ToastUtil.showSnake("暂未实现",mViewPager);
                break;
            case R.id.share_tv:
                shareApp();
                break;
        }
    }

    /**
     * 分享app.
     */
    private void shareApp() {
        String url ="http://10.1.10.235/Android/ECSP/2017-05-10_15-15-08/ErpsPortal-train-release.apk";
        String title = "全国教育云服务平台\n";
        String content = "让人人都有获得优质教育资源的机会";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_TEXT, content+url);
        intent.setType("text/plain");
        startActivity(intent);
    }


    class PictureAdapter extends RecyclingPagerAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_bar_code, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.setData(position);
            return convertView;
        }

        @Override
        public int getCount() {
            return 2;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;

            public ViewHolder(View itemView) {
                if (null == itemView) return;
                imageView = (ImageView) itemView.findViewById(R.id.share_iv);
                textView = (TextView) itemView.findViewById(R.id.app_tv);
            }

            public void setData(int position) {
                // TODO: 10/05/17 get iamge from api .
                if(position > 0){
                    textView.setText("iOS");
                }else{
                    textView.setText("Android");
                }
            }
        }
    }

    /**
     * 启动二维码分享页面.
     *
     * @param act
     * @param userInfo
     */
    public static void start(Activity act, UserInfo userInfo) {
        Intent intent = new Intent(act, BarCodeActivity.class);
        intent.putExtra(Constants.USER_INFO, userInfo);

        act.startActivity(intent);
    }
}


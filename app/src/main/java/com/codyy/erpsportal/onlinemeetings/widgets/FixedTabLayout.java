package com.codyy.erpsportal.onlinemeetings.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;

/**
 * extends TabLayout
 * 1．自定义Tab的TextView解决标题栏文字会被AutoSize
 * 2. 自动画出tab之间的分割线Strip .
 * Created by poe on 12/14/16.
 */
public class FixedTabLayout extends TabLayout{
    private static final String TAG = "FixedTabLayout";
    public FixedTabLayout(Context context) {
        this(context,null);
    }

    public FixedTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FixedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //set listener .　selector deselector .
        addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                View customView = tab.getCustomView();
                if(null != customView && customView.findViewById(R.id.tv_tab_item)!=null){
                    ((TextView)customView.findViewById(R.id.tv_tab_item)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {
                View customView = tab.getCustomView();
                if(null != customView && customView.findViewById(R.id.tv_tab_item)!=null){
//                    ((TextView)customView.findViewById(R.id.tv_tab_item)).setTextColor(UiMainUtils.getColor(R.color.gray));
                    ((TextView)customView.findViewById(R.id.tv_tab_item)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        View tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_simple_text,null);
        TextView tv = (TextView) tabView.findViewById(R.id.tv_tab_item);
        tv.setText(tab.getText());
        super.addTab(tab.setCustomView(tabView));
        //如果当前tab的文字超过１５
    }

    private Canvas mCanvas ;
    @Override
    protected void onFinishInflate() {
        Cog.i(TAG,"onFinishInflate() ");
        super.onFinishInflate();
    }

    private void drawStrips() {
        int tabCount = getTabCount() ;
        if(tabCount > 1 && mCanvas != null ){//draw strip line .
            Paint paint = new Paint();
            int stripWidth = UIUtils.dip2px(EApplication.instance(),0.5f);
            paint.setStrokeWidth(stripWidth);
            paint.setColor(UiMainUtils.getColor(R.color.grey_divider));
            int sWidth = getResources().getDisplayMetrics().widthPixels ;
            int height = getHeight();
            int stripHeight = UIUtils.dip2px(EApplication.instance(),18);
            int padding = (height - stripHeight)/2 ;
            int stripNum = tabCount -1 ;
            for(int i = 0 ; i < stripNum ;i++){
                mCanvas.drawLine(sWidth*(i+1)/tabCount,padding,sWidth*(i+1)/tabCount,padding+stripHeight,paint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Cog.i(TAG,"onDraw() getTabCount: "+getTabCount());
        super.onDraw(canvas);
        mCanvas = canvas;
        drawStrips();
    }
}

package com.codyy.erpsportal.onlinemeetings.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;

/**
 * extends TabLayout
 * 1．自定义Tab的TextView解决标题栏文字会被AutoSize
 * 2. 自动画出tab之间的分割线Strip .
 * 3. app:isSingleLineText default : true  true: only use a single Text instead of the custom view  false : use the custom View .
 * 4. 通过#3中xml属性 setMode(MODE_FIXED) /MODE_SCROLLABLE 控制是否均分.
 * Created by poe on 12/14/16.
 * Modified by poe on 2017/03/14.
 */
public class FlexTabLayout extends TabLayout{
    private static final String TAG = "FixedTabLayout";
    private static final int DEFAULT_FADE_COLOR = R.color.grey_444;
    private boolean mIsSingleLine = true;
    private int mSelectColor = -1 ;

    public FlexTabLayout(Context context) {
        this(context,null);
    }

    public FlexTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlexTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //set listener .　selector deselector .
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlexTabLayout,defStyleAttr,0);
        mIsSingleLine = ta.getBoolean(ta.getIndex(R.styleable.FlexTabLayout_isSingleLineText),true);
        mSelectColor  = ta.getColor(R.styleable.FlexTabLayout_selectItemColor,0);
        ta.recycle();

        Cog.e(TAG,"select color : "+mSelectColor);
        //add listener for text selected color switch .
        addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                View customView = tab.getCustomView();
                if(null != customView && customView.findViewById(R.id.tv_tab_item)!=null){
                    ((TextView)customView.findViewById(R.id.tv_tab_item)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                if(null != customView && customView.findViewById(R.id.tab_item_title)!=null){
                    ((TextView)customView.findViewById(R.id.tab_item_title)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView)customView.findViewById(R.id.tab_item_title)).setTextColor(mSelectColor);
                }
                if(null != customView && customView.findViewById(R.id.tab_item_content)!=null){
                    ((TextView)customView.findViewById(R.id.tab_item_content)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView)customView.findViewById(R.id.tab_item_content)).setTextColor(mSelectColor);
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {
                View customView = tab.getCustomView();
                if(null != customView && customView.findViewById(R.id.tv_tab_item)!=null){
                    ((TextView)customView.findViewById(R.id.tv_tab_item)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                if(null != customView && customView.findViewById(R.id.tab_item_title)!=null){
                    ((TextView)customView.findViewById(R.id.tab_item_title)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    ((TextView)customView.findViewById(R.id.tab_item_title)).setTextColor(UiMainUtils.getColor(DEFAULT_FADE_COLOR));
                }
                if(null != customView && customView.findViewById(R.id.tab_item_content)!=null){
                    ((TextView)customView.findViewById(R.id.tab_item_content)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    ((TextView)customView.findViewById(R.id.tab_item_content)).setTextColor(UiMainUtils.getColor(DEFAULT_FADE_COLOR));
                }
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        if(mIsSingleLine) {
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_simple_text, null);
            TextView tv = (TextView) tabView.findViewById(R.id.tv_tab_item);
            tv.setText(tab.getText());
            super.addTab(tab.setCustomView(tabView));
        }else{
            super.addTab(tab);
        }
    }

    private Canvas mCanvas ;
    @Override
    protected void onFinishInflate() {
        Cog.i(TAG,"onFinishInflate() ");
        super.onFinishInflate();
    }

    /**
     * 20170314： 修复分割线在Mode_Scrollable模式下无法发准确画出的问题
     * method: customView.getLeft is the padding left or right
     * so : startX = paddingLeft + view_Width+paddingRight = customView.getLeft = customView.getRight .
     */
    private void drawStrips() {
        int tabCount = getTabCount() ;
        int startX = 0;
        if(tabCount > 1 && mCanvas != null ){//draw strip line .
            Paint paint = new Paint();
            int stripWidth = UIUtils.dip2px(EApplication.instance(),0.5f);
            paint.setStrokeWidth(stripWidth);
            paint.setColor(UiMainUtils.getColor(R.color.grey_divider));
            int height = getHeight();
            int stripHeight = UIUtils.dip2px(EApplication.instance(),18);
            int padding = (height - stripHeight)/2 ;
            int stripNum = tabCount -1 ;
            for(int i = 0 ; i < stripNum ;i++){
                View customView = getTabAt(i).getCustomView();
                startX += customView.getLeft()+customView.getRight();
                mCanvas.drawLine(startX,padding,startX,padding+stripHeight,paint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        drawStrips();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}

package com.codyy.erpsportal.commons.widgets.blog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UiMainUtils;

/**
 * 评论框的复杂发送Button .
 * Created by poe on 12/14/16.
 */
public class CommentButton extends LinearLayout{

    private TextView mTextView;
    private ImageView mIconImageView;
    private String mContent ="";
    private int mColor = UiMainUtils.getColor(R.color.gray);
    private Drawable mDrawableLeftRes = null;

    public CommentButton(Context context) {
        this(context,null);
    }

    public CommentButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommentButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.button_comment_with_icon, this, true);
        TypedArray ta =  getContext().obtainStyledAttributes(attrs,R.styleable.CommentButton);
        if(null !=  ta){
            mContent = ta.getString(R.styleable.CommentButton_cb_text);
            mColor   = ta.getColor(R.styleable.CommentButton_cb_textColor,UiMainUtils.getColor(R.color.gray));
            mDrawableLeftRes = ta.getDrawable(R.styleable.CommentButton_cb_drawableLeft);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIconImageView  = (ImageView) findViewById(R.id.icon_comment);
        mTextView       = (TextView) findViewById(R.id.text_comment);

        mIconImageView.setImageDrawable(mDrawableLeftRes);
        mTextView.setTextColor(mColor);
        mTextView.setText(mContent);
    }

    public void setText(String text){
        if(null != mTextView) mTextView.setText(text);
    }

    public void setTextColor(int color){
        if(null != mTextView) mTextView.setTextColor(color);
    }

    public void setDrawableLeft(int res){
        if(null != mIconImageView) mIconImageView.setImageResource(res);
    }

    public void showDrawLeft(boolean isVisible){
        if(null == mIconImageView) return;
        if( isVisible){
            mIconImageView.setVisibility(View.VISIBLE);
        }else{
            mIconImageView.setVisibility(View.GONE);
        }
    }
}

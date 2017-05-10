package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.InputUtils;

/**
 * Created by kmdai on 16-1-13.
 */
public class ClickTextView extends AppCompatTextView {
    private Context mContext;
    private OnClick mOnClick;

    public ClickTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ClickTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClickTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
    }

    public OnClick getmOnClick() {
        return mOnClick;
    }

    public void setmOnClick(OnClick mOnClick) {
        this.mOnClick = mOnClick;
    }

    public void setHtmlString(String text) {
        Spanned spanned = Html.fromHtml(text);
        String source = spanned.toString();
        SpannableStringBuilder spannableStringBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }
        URLSpan[] urlSpens = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);
        for (URLSpan urlSpan : urlSpens) {
            int start = spannableStringBuilder.getSpanStart(urlSpan);
            int end = spannableStringBuilder.getSpanEnd(urlSpan);
            String spanStr = source.substring(start, end);
            int endIndex = spanStr.indexOf("(");
            if (endIndex <= 0) {
                endIndex = end;
            } else {
                endIndex += start;
            }
            ClickSpan clickSpan = new ClickSpan(urlSpan.getURL(), spanStr);
            spannableStringBuilder.setSpan(clickSpan, start, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.removeSpan(urlSpan);
        }
        InputUtils.setEmojiSpan(getContext(), spannableStringBuilder, (int) getTextSize());
        setText(spannableStringBuilder);
        //要在settext之后设置，不然clickablespan没有点击效果，原因未知！
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setEmojiconString(String emojiconString) {
        if (emojiconString != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(emojiconString);
            InputUtils.setEmojiSpan(getContext(), spannableStringBuilder, (int) getTextSize());
            setText(spannableStringBuilder);
        } else {
            setText("");
        }
    }


    class ClickSpan extends ClickableSpan {
        String url = "";
        String cont;

        ClickSpan(String url, String cont) {
            this.cont = cont;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            if (url != null && mOnClick != null) {
                if (cont.startsWith("http://")) {
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(cont));
                    mContext.startActivity(it);
                } else {
                    mOnClick.OnClick(url);
                }
//                String id = url.substring(url.lastIndexOf('/'), url.lastIndexOf('.'));
//                Toast.makeText(mContext, url + "-" + cont, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(mContext.getResources().getColor(R.color.weibo_at));
            ds.setUnderlineText(false); //去掉下划线
        }
    }

    public interface OnClick {
        void OnClick(String url);
    }
}

package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 置顶博文
 * Created by poe on 16-3-7.
 */
public class TopTextViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {

    @Bind(R.id.text)TextView mTextView;

    public TopTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.text_line_item;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {
        mCurrentPosition    =   position;
        mData   =   data ;
        String content = "[置顶] "+ UIUtils.filterNull(data.getBaseTitle());
        Spannable spannable = new SpannableString(Html.fromHtml(UIUtils.filterCharacter(content)));
        spannable.setSpan(new StyleSpan(Typeface.NORMAL),0,4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#EE9A00")),0,4,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spannable.setSpan(new ForegroundColorSpan(Color.RED),0,4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mTextView.setText(spannable);
    }
}

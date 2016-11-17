package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Filter;

import com.codyy.erpsportal.commons.utils.InputUtils;


/**
 * 添加表情复制
 * Created by kmdai on 16-3-2.
 */
public class EmojiEditText extends EditText {
    public EmojiEditText(Context context) {
        super(context);
    }

    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public EmojiEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 添加表情复制
     *
     * @param id
     * @return
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
           InputFilter[] filter= getFilters();
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            CharSequence character = clip.getText();
            InputUtils.setEmojiSpanEdit(this, character.toString(), (int) getTextSize() + 5);
            return true;
        }
        return super.onTextContextMenuItem(id);
    }
}

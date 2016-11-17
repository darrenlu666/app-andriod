/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.text.Spannable;
import android.util.SparseIntArray;

import com.codyy.erpsportal.R;


/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public final class EmojiconHandler {
    private EmojiconHandler() {
    }

    private static final SparseIntArray sEmojisMap = new SparseIntArray(3);
    private static final SparseIntArray sSoftbanksMap = new SparseIntArray(471);

    static {
        // People
        sEmojisMap.put(0x1f604, R.drawable.smiley_0);
        sEmojisMap.put(0x1f603, R.drawable.smiley_1);
        sEmojisMap.put(0x1f600, R.drawable.smiley_2);
        sEmojisMap.put(0x1f60a, R.drawable.smiley_3);
        sEmojisMap.put(0x263a,  R.drawable.smiley_4);
        sEmojisMap.put(0x1f609, R.drawable.smiley_5);
        sEmojisMap.put(0x1f60d, R.drawable.smiley_6);
        sEmojisMap.put(0x1f618, R.drawable.smiley_7);
        sEmojisMap.put(0x1f61a, R.drawable.smiley_8);
        sEmojisMap.put(0x1f617, R.drawable.smiley_9);
        sEmojisMap.put(0x1f619, R.drawable.smiley_10);
        sEmojisMap.put(0x1f61c, R.drawable.smiley_11);
        sEmojisMap.put(0x1f61d, R.drawable.smiley_12);
        sEmojisMap.put(0x1f61b, R.drawable.smiley_13);
        sEmojisMap.put(0x1f633, R.drawable.smiley_14);
        sEmojisMap.put(0x1f601, R.drawable.smiley_15);
        sEmojisMap.put(0x1f614, R.drawable.smiley_16);
        sEmojisMap.put(0x1f60c, R.drawable.smiley_17);
        sEmojisMap.put(0x1f612, R.drawable.smiley_18);
        sEmojisMap.put(0x1f61e, R.drawable.smiley_19);
        sEmojisMap.put(0x1f623, R.drawable.smiley_20);
        sEmojisMap.put(0x1f622, R.drawable.smiley_21);
        sEmojisMap.put(0x1f602, R.drawable.smiley_22);
        sEmojisMap.put(0x1f62d, R.drawable.smiley_23);
        sEmojisMap.put(0x1f62a, R.drawable.smiley_24);
        sEmojisMap.put(0x1f625, R.drawable.smiley_25);
        sEmojisMap.put(0x1f630, R.drawable.smiley_26);
        sEmojisMap.put(0x1f605, R.drawable.smiley_27);
        sEmojisMap.put(0x1f613, R.drawable.smiley_28);
        sEmojisMap.put(0x1f629, R.drawable.smiley_29);
        sEmojisMap.put(0x1f62b, R.drawable.smiley_30);
        sEmojisMap.put(0x1f628, R.drawable.smiley_31);
        sEmojisMap.put(0x1f631, R.drawable.smiley_32);
        sEmojisMap.put(0x1f620, R.drawable.smiley_33);
        sEmojisMap.put(0x1f621, R.drawable.smiley_34);
        sEmojisMap.put(0x1f624, R.drawable.smiley_35);
        sEmojisMap.put(0x1f616, R.drawable.smiley_36);
        sEmojisMap.put(0x1f606, R.drawable.smiley_37);
        sEmojisMap.put(0x1f60b, R.drawable.smiley_38);
        sEmojisMap.put(0x1f637, R.drawable.smiley_39);
        sEmojisMap.put(0x1f60e, R.drawable.smiley_40);
        sEmojisMap.put(0x1f634, R.drawable.smiley_41);
        sEmojisMap.put(0x1f635, R.drawable.smiley_42);
        sEmojisMap.put(0x1f632, R.drawable.smiley_43);
        sEmojisMap.put(0x1f61f, R.drawable.smiley_44);
        sEmojisMap.put(0x1f615, R.drawable.smiley_27);


    }

    private static boolean isSoftBankEmoji(char c) {
        return ((c >> 12) == 0xe);
    }

    private static int getEmojiResource(Context context, int codePoint) {
        return sEmojisMap.get(codePoint);
    }

    private static int getSoftbankEmojiResource(char c) {
        return sSoftbanksMap.get(c);
    }
    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize) {
        addEmojis(context, text, emojiSize, 0, -1, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int index, int length) {
        addEmojis(context, text, emojiSize, index, length, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, boolean useSystemDefault) {
        addEmojis(context, text, emojiSize, 0, -1, useSystemDefault);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int index, int length, boolean useSystemDefault) {
        if (useSystemDefault) {
            return;
        }
        int textLength = text.length();
        int textLengthToProcessMax = textLength - index;
        int textLengthToProcess = length < 0 || length >= textLengthToProcessMax ? textLength : (length+index);
        // remove spans throughout all text
        EmojiconSpan[] oldSpans = text.getSpans(0, textLength, EmojiconSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            text.removeSpan(oldSpans[i]);
        }

        int skip;
        for (int i = index; i < textLengthToProcess; i += skip) {
            skip = 0;
            int icon = 0;
            char c = text.charAt(i);
            if (isSoftBankEmoji(c)) {
                icon = getSoftbankEmojiResource(c);
                skip = icon == 0 ? 0 : 1;
            }

            if (icon == 0) {
                int unicode = Character.codePointAt(text, i);
                skip = Character.charCount(unicode);

                if (unicode > 0xff) {
                    icon = getEmojiResource(context, unicode);
                }

                if (icon == 0 && i + skip < textLengthToProcess) {
                    int followUnicode = Character.codePointAt(text, i + skip);

                }
            }

            if (icon > 0) {
                text.setSpan(new EmojiconSpan(context, icon, emojiSize), i, i + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}

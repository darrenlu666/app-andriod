package com.codyy.erpsportal.onlinemeetings.utils;

import java.net.URLDecoder;

/**
 * Created by poe on 17-9-19.
 */

public class EmojiUtils {

    /**
     * 替换表情字符
     *
     * @param Msg
     * @return
     */
    public static String replaceMsg(String Msg) {
        String reciveMsg = Msg;
        if (containsAny(reciveMsg, "[") && containsAny(reciveMsg, "]")) {
            if (containsAny(reciveMsg, "[大哭]")) {
                reciveMsg = reciveMsg.replaceAll("\\[大哭\\]", URLDecoder.decode("%F0%9F%98%98"));
            }
            if (containsAny(reciveMsg, "[傲慢]")) {
                reciveMsg = reciveMsg.replaceAll("\\[傲慢\\]", URLDecoder.decode("%F0%9F%98%84"));
            }
            if (containsAny(reciveMsg, "[白眼]")) {
                reciveMsg = reciveMsg.replaceAll("\\[白眼\\]", URLDecoder.decode("%F0%9F%98%83"));
            }
            if (containsAny(reciveMsg, "[便便]")) {
                reciveMsg = reciveMsg.replaceAll("\\[便便\\]", URLDecoder.decode("%F0%9F%98%80"));
            }
            if (containsAny(reciveMsg, "[擦汗]")) {
                reciveMsg = reciveMsg.replaceAll("\\[擦汗\\]", URLDecoder.decode("%E2%98%BA"));
            }
            if (containsAny(reciveMsg, "[鄙视]")) {
                reciveMsg = reciveMsg.replaceAll("\\[鄙视\\]", URLDecoder.decode("%F0%9F%98%8A"));
            }
            if (containsAny(reciveMsg, "[菜刀]")) {
                reciveMsg = reciveMsg.replaceAll("\\[菜刀\\]", URLDecoder.decode("%F0%9F%98%89"));
            }
            if (containsAny(reciveMsg, "[呲牙]")) {
                reciveMsg = reciveMsg.replaceAll("\\[呲牙\\]", URLDecoder.decode("%F0%9F%98%8D"));
            }
            if (containsAny(reciveMsg, "[得意]")) {
                reciveMsg = reciveMsg.replaceAll("\\[得意\\]", URLDecoder.decode("%F0%9F%98%9A"));
            }
            if (containsAny(reciveMsg, "[发怒]")) {
                reciveMsg = reciveMsg.replaceAll("\\[发怒\\]", URLDecoder.decode("%F0%9F%98%97"));
            }
            if (containsAny(reciveMsg, "[尴尬]")) {
                reciveMsg = reciveMsg.replaceAll("\\[尴尬\\]", URLDecoder.decode("%F0%9F%98%99"));
            }
            if (containsAny(reciveMsg, "[傲慢]")) {
                reciveMsg = reciveMsg.replaceAll("\\[傲慢\\]", URLDecoder.decode("%F0%9F%98%98"));
            }
            if (containsAny(reciveMsg, "[害羞]")) {
                reciveMsg = reciveMsg.replaceAll("\\[害羞\\]", URLDecoder.decode("%F0%9F%98%9C"));
            }
            if (containsAny(reciveMsg, "[汗]")) {
                reciveMsg = reciveMsg.replaceAll("\\[汗\\]", URLDecoder.decode("%F0%9F%98%9D"));
            }
            if (containsAny(reciveMsg, "[憨笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[憨笑\\]", URLDecoder.decode("%F0%9F%98%9B"));
            }
            if (containsAny(reciveMsg, "[花]")) {
                reciveMsg = reciveMsg.replaceAll("\\[花\\]", URLDecoder.decode("%F0%9F%98%B3"));
            }
            if (containsAny(reciveMsg, "[惊恐]")) {
                reciveMsg = reciveMsg.replaceAll("\\[惊恐\\]", URLDecoder.decode("%F0%9F%98%81"));
            }
            if (containsAny(reciveMsg, "[惊讶]")) {
                reciveMsg = reciveMsg.replaceAll("\\[惊讶\\]", URLDecoder.decode("%F0%9F%98%94"));
            }
            if (containsAny(reciveMsg, "[可爱]")) {
                reciveMsg = reciveMsg.replaceAll("\\[可爱\\]", URLDecoder.decode("%F0%9F%98%8C"));
            }
            if (containsAny(reciveMsg, "[抠鼻]")) {
                reciveMsg = reciveMsg.replaceAll("\\[抠鼻\\]", URLDecoder.decode("%F0%9F%98%92"));
            }
            if (containsAny(reciveMsg, "[耍酷]")) {
                reciveMsg = reciveMsg.replaceAll("\\[耍酷\\]", URLDecoder.decode("%F0%9F%98%9E"));
            }
            if (containsAny(reciveMsg, "[酷]")) {
                reciveMsg = reciveMsg.replaceAll("\\[酷\\]", URLDecoder.decode("%F0%9F%98%9E"));
            }
            if (containsAny(reciveMsg, "[流泪]")) {
                reciveMsg = reciveMsg.replaceAll("\\[流泪\\]", URLDecoder.decode("%F0%9F%98%A3"));
            }
            if (containsAny(reciveMsg, "[傲慢]")) {
                reciveMsg = reciveMsg.replaceAll("\\[傲慢\\]", URLDecoder.decode("%F0%9F%98%84"));
            }
            if (containsAny(reciveMsg, "[难过]")) {
                reciveMsg = reciveMsg.replaceAll("\\[难过\\]", URLDecoder.decode("%F0%9F%98%A2"));
            }
            if (containsAny(reciveMsg, "[撇嘴]")) {
                reciveMsg = reciveMsg.replaceAll("\\[撇嘴\\]", URLDecoder.decode("%F0%9F%98%82"));
            }
            if (containsAny(reciveMsg, "[敲打]")) {
                reciveMsg = reciveMsg.replaceAll("\\[敲打\\]", URLDecoder.decode("%F0%9F%98%AD"));
            }
            if (containsAny(reciveMsg, "[亲亲]")) {
                reciveMsg = reciveMsg.replaceAll("\\[亲亲\\]", URLDecoder.decode("%F0%9F%98%AA"));
            }
            if (containsAny(reciveMsg, "[色]")) {
                reciveMsg = reciveMsg.replaceAll("\\[色\\]", URLDecoder.decode("%F0%9F%98%A5"));
            }
            if (containsAny(reciveMsg, "[胜利]")) {
                reciveMsg = reciveMsg.replaceAll("\\[胜利\\]", URLDecoder.decode("%F0%9F%98%B0"));
            }
            if (containsAny(reciveMsg, "[衰]")) {
                reciveMsg = reciveMsg.replaceAll("\\[衰\\]", URLDecoder.decode("%F0%9F%98%93"));
            }
            if (containsAny(reciveMsg, "[睡]")) {
                reciveMsg = reciveMsg.replaceAll("\\[睡\\]", URLDecoder.decode("%F0%9F%98%AB"));
            }
            if (containsAny(reciveMsg, "[微笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[微笑\\]", URLDecoder.decode("%F0%9F%98%A8"));
            }
            if (containsAny(reciveMsg, "[偷笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[偷笑\\]", URLDecoder.decode("%F0%9F%98%B1"));
            }
            if (containsAny(reciveMsg, "[吐]")) {
                reciveMsg = reciveMsg.replaceAll("\\[吐\\]", URLDecoder.decode("%F0%9F%98%A0"));
            }
            if (containsAny(reciveMsg, "[委屈]")) {
                reciveMsg = reciveMsg.replaceAll("\\[委屈\\]", URLDecoder.decode("%F0%9F%98%A1"));
            }
            if (containsAny(reciveMsg, "[微笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[微笑\\]", URLDecoder.decode("%F0%9F%98%A4"));
            }
            if (containsAny(reciveMsg, "[心]")) {
                reciveMsg = reciveMsg.replaceAll("\\[心\\]", URLDecoder.decode("%F0%9F%98%96"));
            }
            if (containsAny(reciveMsg, "[心裂]")) {
                reciveMsg = reciveMsg.replaceAll("\\[心裂\\]", URLDecoder.decode("%F0%9F%98%86"));
            }
            if (containsAny(reciveMsg, "[嘘]")) {
                reciveMsg = reciveMsg.replaceAll("\\[嘘\\]", URLDecoder.decode("%F0%9F%98%8B"));
            }
            if (containsAny(reciveMsg, "[阴险]")) {
                reciveMsg = reciveMsg.replaceAll("\\[阴险\\]", URLDecoder.decode("%F0%9F%98%B7"));
            }
            if (containsAny(reciveMsg, "[疑问]")) {
                reciveMsg = reciveMsg.replaceAll("\\[疑问\\]", URLDecoder.decode("%F0%9F%98%8E"));
            }
            if (containsAny(reciveMsg, "[再见]")) {
                reciveMsg = reciveMsg.replaceAll("\\[再见\\]", URLDecoder.decode("%F0%9F%98%B4"));
            }
            if (containsAny(reciveMsg, "[炸弹]")) {
                reciveMsg = reciveMsg.replaceAll("\\[炸弹\\]", URLDecoder.decode("%F0%9F%98%B5"));
            }
            if (containsAny(reciveMsg, "[抓狂]")) {
                reciveMsg = reciveMsg.replaceAll("\\[抓狂\\]", URLDecoder.decode("%F0%9F%98%B2"));
            }
            if (containsAny(reciveMsg, "[猪头]")) {
                reciveMsg = reciveMsg.replaceAll("\\[猪头\\]", URLDecoder.decode("%F0%9F%98%9F"));
            }
            if (containsAny(reciveMsg, "[示爱]")) {
                reciveMsg = reciveMsg.replaceAll("\\[示爱\\]", URLDecoder.decode("%F0%9F%98%95"));
            }
        }
        return reciveMsg;
    }


    /**
     * 判断字符串中是否包含某个特定字符
     *
     * @param str
     * @param searchChars
     * @return
     */
    public static boolean containsAny(String str, String searchChars) {
        if (str != null && searchChars != null) {
            return str.contains(searchChars);
        }
        return false;
    }
}



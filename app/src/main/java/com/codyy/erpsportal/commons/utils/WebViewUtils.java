package com.codyy.erpsportal.commons.utils;

import android.os.Build;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * webview工具类
 * Created by gujiajia on 2016/4/21.
 */
public class WebViewUtils {
    /**
     * 图片自适应WebView宽且字体不跟着缩放
     *
     * @param webView
     * @param html
     */
    public static void setContentToWebView(WebView webView, String html) {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        } else {
            webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        }
        webView.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        webView.loadData(getHtmlData(html), "text/html; charset=UTF-8", null);//这种写法可以正确解码
    }

    private static String getHtmlData(String bodyHTML) {
        bodyHTML = bodyHTML.replace("<p>", "<p style=\"word-break:break-all\">");//文字换行
        //                                        |这个问号必须要，否则如果<img>标签后还有有style属性的其它标签会匹配到后面去
        Pattern pattern = Pattern.compile("<img(.+?)style=\".*?\"");
        Matcher matcher = pattern.matcher(bodyHTML);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<img" + matcher.group(1) + "style=\"max-width: 100%; width:auto; height: auto;\"");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}

package com.codyy.erpsportal.commons.utils;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理html语言 or 富文本 语言 【增/减 js实现】
 * Created by poe on 16-5-30.
 */

public class HtmlUtils {

    /**
     * 获取html中的所有图片
     * @param compatText
     * @return
     */
    public static List<String>  filterImages(String compatText){
        List<String> uList = new ArrayList<>();
        if(!TextUtils.isEmpty(compatText)&&compatText.contains("<img")){
            //get img src
            Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
            Matcher m = p.matcher(compatText);
            String searchAttrib = "src";
            String regxpForTagAttrib = searchAttrib + "\\s*=\\s*[\"|']http://([^\"|']+)[\"|']";//"=[\"|']([^[\"|']]+)[\"|']";
            Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
            while(m.find()){
                Matcher matcherForAttrib = patternForAttrib.matcher(m.group());
                if (matcherForAttrib.find()) {
                    System.out.println("poe " +"http://" +matcherForAttrib.group(1));
                    uList.add("http://" +matcherForAttrib.group(1));
                }
            }
        }
        return  uList;
    }

    /**
     * 1.向富文本中插入执行函数sayHello
     * 2.修改后的文本插入<html></html> 组合为新的页面 .
     * 3.p.small {line-height: 150%} 控制行高150%
     * @param compatText
     */
    public static String constructExecActionJs(String compatText){
        StringBuffer sb = new StringBuffer();
        sb.append("<html> " +
                "<script type=\"text/javascript\"> " +
                "  function showImage(position , url) {\n" +
                "        window.control.showImage(position,url)\n" +
                "    }"+
                "</script>" +
                "<style type=\"text/css\">\n" +
                "p\n" +
                "{\n" +
                "word-wrap: break-word;\n" +
                "line-height: 150%;\n" +
                "}\n" +
                "</style>");
        //插入函数
        sb.append(insertExecActionJs(compatText));
        sb.append("</html>");
        return  sb.toString();
    }

    /**
     * 1. 修改后的文本插入<html></html> 组合为新的页面 .
     * 2. p.small {line-height: 150%} 控制行高150%
     * @param compatText
     */
    public static String constructWordBreakJs(String compatText){
        StringBuffer sb = new StringBuffer();
        sb.append("<html> " +
                "<style type=\"text/css\">\n" +
                "p\n" +
                "{\n" +
                "word-wrap: break-word;\n" +
                "line-height: 200%;\n" +
                "}\n" +
                "</style>");
        //插入函数
        sb.append(compatText);
        sb.append("</html>");
        return  sb.toString();
    }

    public static String insertExecActionJs(String compatText){
        String searchAttrib = "src";
        String regxpForTag ="<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        Pattern patternForTag = Pattern.compile(regxpForTag);
        String regxpForTagAttrib = searchAttrib + "\\s*=\\s*[\"|']http://([^\"|']+)[\"|']";//"=[\"|']([^[\"|']]+)[\"|']";
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
        Matcher matcherForTag = patternForTag.matcher(compatText);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        int pos = 0 ;
        while (result) {
            StringBuffer sbreplace = new StringBuffer();
            System.out.println(matcherForTag.group());
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group());

            if (matcherForAttrib.find()) {
                System.out.println("ll"+matcherForAttrib.group());
                matcherForAttrib.appendReplacement(sbreplace, searchAttrib+"=\"http://"
                        + matcherForAttrib.group(1) +"\""+ " onclick=\"showImage(" +
                        +pos+"," +
                        "'http://" +matcherForAttrib.group(1) +"'"
                        +")\"");
            }
            matcherForAttrib.appendTail(sbreplace);
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
            pos++;
        }
        matcherForTag.appendTail(sb);
        System.out.println("poe: "+sb.toString());
        return  sb.toString();
    }
}

package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by caixingming on 2015/4/24.
 *
 * 对应 课程的Index
 * 如：第一节，第二节...
 *
 * 暂定：最多 9节课目
 */
public class EmumIndex {


    private static String[] indexs = {"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四","十五","十六"};


    /**
     * 转化为大写的汉字
     * @param pos
     * @return
     */
    public static String getIndex(int pos){

        String result ="一";

        if(pos <16){
            if(pos>0){
                pos --;
            }else{
                pos = 0;
            }

            result  =   indexs[pos] ;
        }else{
            result=pos+"";
        }

        return result;
    }

}

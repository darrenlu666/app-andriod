package com.codyy.erpsportal.statistics.models.entities;


import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计分析项
 */
public class StatisticalItem {

    public enum TYPE {
        CLASS_COUNT,
        CLASS_PERCENT,
        CLASS_SCHOOL,
        ACTIVITY_ORG,
        ACTIVITY_SCHOOL,
        RESOURCE_ORG,
        RESOURCE_SCHOOL
    }

    private String title;

    private int count;

    private String content;

    private int max;

    private TYPE type;

    public StatisticalItem() {}

    public StatisticalItem(String title, int count) {
        this.title = title;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(JSONObject jsonObject, String key) {
        setTitle(StringUtils.replaceHtml(jsonObject.optString(key)));
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public static StatisticalItem parseJson(JSONObject jsonObject, TYPE type) {
        StatisticalItem item = new StatisticalItem();
        switch (type) {
            case CLASS_COUNT: {
                item.setTitle(jsonObject, "area");
                int count = jsonObject.optInt("scheduleCount");
                item.setCount(count);
                item.setContent(count + "");
                break;
            }
            case CLASS_PERCENT: {
                item.setTitle(jsonObject, "area");
                double percent = jsonObject.optDouble("schedulePercent");
                item.setCount((int) (percent * 1000));
                item.setContent(String.format("%.0f%%", percent * 100));
                break;
            }
            case CLASS_SCHOOL: {
                item.setTitle(jsonObject, "subjectName");
                int count = jsonObject.optInt("scheduleCount");
                item.setCount(count);
                item.setContent(count + "");
                break;
            }
            case ACTIVITY_ORG: {
                item.setTitle(jsonObject, "area");
                int count = jsonObject.optInt("evaluationCount");
                item.setCount(count);
                item.setContent(count + "");
                break;
            }
            case ACTIVITY_SCHOOL: {
                item.setTitle(jsonObject, "subjectName");
                int count = jsonObject.optInt("evaluationCount");
                item.setCount(count);
                item.setContent(count + "");
                break;
            }
            case RESOURCE_ORG: {
                item.setTitle(jsonObject, "area");
                int count = jsonObject.optInt("resourceCount");
                item.setCount(count);
                item.setContent(count + "");
                break;
            }
            case RESOURCE_SCHOOL: {
                item.setTitle(jsonObject, "subjectName");
                int count = jsonObject.optInt("resourceCount");
                item.setCount(count);
                item.setContent(count + "");
                break;
            }
        }
        return item;
    }

    public static List<StatisticalItem> parseJsonArray(JSONArray jsonArray, TYPE type) {
        List<StatisticalItem> items = new ArrayList<>();
        switch (type) {
            case CLASS_COUNT:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "area");
                    int count = jsonObject.optInt("scheduleCount");
                    item.setCount(count);
                    item.setContent(count + "");
                    item.setType(TYPE.CLASS_COUNT);
                    item.setMax(1000);
                    items.add(item);
                }
                break;
            case CLASS_PERCENT:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "area");
                    double percent = jsonObject.optDouble("schedulePercent");
                    item.setCount((int) (percent * 1000));
                    item.setContent(String.format("%.0f%%", percent * 100));
                    item.setType(TYPE.CLASS_PERCENT);
                    //统计项统计类型为学期开课比率比率条最大为1000
                    //item.setMax(1000);
                    items.add(item);
                }
                break;
            case CLASS_SCHOOL:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "subjectName");
                    int count = jsonObject.optInt("scheduleCount");
                    item.setCount(count);
                    item.setContent(count + "");
                    item.setType(TYPE.CLASS_SCHOOL);
                    items.add(item);
                }
                break;
            case ACTIVITY_ORG:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "area");
                    int count = jsonObject.optInt("evaluationCount");
                    item.setCount(count);
                    item.setContent(count + "");
                    item.setType(TYPE.ACTIVITY_ORG);
                    items.add(item);
                }
                break;
            case ACTIVITY_SCHOOL:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "subjectName");
                    int count = jsonObject.optInt("evaluationCount");
                    item.setCount(count);
                    item.setContent(count + "");
                    item.setType(TYPE.ACTIVITY_SCHOOL);
                    items.add(item);
                }
                break;
            case RESOURCE_ORG:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "area");
                    int count = jsonObject.optInt("resourceCount");
                    item.setCount(count);
                    item.setContent(count + "");
                    item.setType(TYPE.RESOURCE_ORG);
                    items.add(item);
                }
                break;
            case RESOURCE_SCHOOL:
                for (int i = 0; i< jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    StatisticalItem item = new StatisticalItem();
                    item.setTitle(jsonObject, "subjectName");
                    int count = jsonObject.optInt("resourceCount");
                    item.setCount(count);
                    item.setContent(count + "");
                    item.setType(TYPE.RESOURCE_SCHOOL);
                    items.add(item);
                }
                break;
        }
        return items;
    }

}

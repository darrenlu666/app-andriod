package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kmdai on 2015/4/24.
 */
public class StandardDetails {
    private String type;
    private int totalScore;
    private String itemName;
    private String itemId;
    private String itemChildId;
    private String content;
    private int score;
    private int showScore;
    private int myScore = 5;
    private int number;
    private boolean isContent = true;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isContent() {
        return isContent;
    }

    public void setIsContent(boolean isContent) {
        this.isContent = isContent;
    }

    public int getMyScore() {
        return myScore;
    }

    public void setMyScore(int myScore) {
        this.myScore = myScore;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemChildId() {
        return itemChildId;
    }

    public void setItemChildId(String itemChildId) {
        this.itemChildId = itemChildId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getShowScore() {
        return showScore;
    }

    public void setShowScore(int showScore) {
        this.showScore = showScore;
    }

    /**
     * @param object
     * @param standardDetailses
     */
    public static void getStandardDetails(JSONObject object, List<StandardDetails> standardDetailses) {
        if ("success".equals(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("standardDetails");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                JSONArray jsonArray1 = jsonObject.optJSONArray("itemDetails");
                if (jsonArray1.length() != 0) {
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject1 = jsonArray1.optJSONObject(j);
                        StandardDetails standardDetails = new StandardDetails();
                        standardDetails.setTotalScore(object.optInt("totalScore"));
                        standardDetails.setItemName(jsonObject.optString("itemName"));
                        standardDetails.setItemId(jsonObject.optString("itemId"));
                        standardDetails.setItemChildId(jsonObject1.optString("itemChildId"));
                        standardDetails.setContent(jsonObject1.optString("content"));
                        standardDetails.setScore(jsonObject1.optInt("score"));
                        standardDetails.setShowScore(jsonObject1.optInt("showScore"));
                        standardDetails.setNumber(j + 1);
                        standardDetails.setIsContent(true);
                        standardDetails.setType("itemChild");
                        standardDetailses.add(standardDetails);
                    }
                } else {
                    StandardDetails standardDetails = new StandardDetails();
                    standardDetails.setItemName(jsonObject.optString("itemName"));
                    standardDetails.setShowScore(jsonObject.optInt("showScore"));
                    standardDetails.setTotalScore(object.optInt("totalScore"));
                    standardDetails.setItemId(jsonObject.optString("itemId"));
                    standardDetails.setScore(jsonObject.optInt("score"));
                    standardDetails.setType("item");
                    standardDetails.setIsContent(false);
                    standardDetailses.add(standardDetails);
                }
            }
        }
    }
}

package com.codyy.erpsportal.homework.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 习题信息实体类
 * Created by ldh on 2016/1/6.
 */
public class ItemInfoClass implements Parcelable{

    /**
     * workItemIndex:1
     * workItemId : fdgafgdafadgfadfadssd
     * workItemType : SINGLE_CHOICE
     * blankCount : 5
     * workContent : asdgfadfasdfsfasdfasdf
     * resourceType：SINGLE_CHOICE
     * workItemResourceUrl : sdfasdfasdfadfadsfasdfas
     * workItemOptions : dfasdfdffasdfasdfasdf
     * trueAnswer : A
     * trueFillAnswer:["李白","晚上的月光/思乡之情/明月"]
     * itemAnalysis : 这道题的思路是。。。。。
     * itemAnalysisResourceId : sdfasdfasdfsdfafasf
     * knowledgePoint : 知识点2，知识点3
     * difficulty : EASY
     * attachmentList : [{"attachmentId":"dsgadgfad","attachmentName":"语文作业.doc"},{"attachmentId":"dsgadgfad","attachmentName":"语文作业.doc"}]
     */

    private int workItemIndex;
    private String workItemId;
    private String workItemType;
    private String resourceType;
    private int blankCount;
    private String workContent;
    private String workItemResourceUrl;
    private String workItemOptions;
    private String trueAnswer;
    private List<String> trueFillAnswer;
    private String itemAnalysis;
    private String itemAnalysisResourceId;
    private String knowledgePoint;
    private String difficulty;
    private int color;
    private String textQestion;

    public String getTextQestion() {
        return textQestion;
    }

    public void setTextQestion(String textQestion) {
        this.textQestion = textQestion;
    }

    public static Creator<ItemInfoClass> getCREATOR() {
        return CREATOR;
    }


    public ItemInfoClass(){

    }

    private List<AttachmentListEntity> attachmentList;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getWorkItemIndex() {
        return workItemIndex;
    }

    public void setWorkItemIndex(int workItemIndex) {
        this.workItemIndex = workItemIndex;
    }

    public void setWorkItemId(String workItemId) {
        this.workItemId = workItemId;
    }

    public void setWorkItemType(String workItemType) {
        this.workItemType = workItemType;
    }

    public void setBlankCount(int blankCount) {
        this.blankCount = blankCount;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public void setWorkItemResourceUrl(String workItemResourceUrl) {
        this.workItemResourceUrl = workItemResourceUrl;
    }

    public void setWorkItemOptions(String workItemOptions) {
        this.workItemOptions = workItemOptions;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    public void setItemAnalysis(String itemAnalysis) {
        this.itemAnalysis = itemAnalysis;
    }

    public void setItemAnalysisResourceId(String itemAnalysisResourceId) {
        this.itemAnalysisResourceId = itemAnalysisResourceId;
    }

    public void setKnowledgePoint(String knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setAttachmentList(List<AttachmentListEntity> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String getWorkItemId() {
        return workItemId;
    }

    public String getWorkItemType() {
        return workItemType;
    }

    public int getBlankCount() {
        return blankCount;
    }

    public String getWorkContent() {
        return workContent;
    }

    public String getWorkItemResourceUrl() {
        return workItemResourceUrl;
    }

    public String getWorkItemOptions() {
        return workItemOptions;
    }

    public String getTrueAnswer() {
        return trueAnswer;
    }

    public String getItemAnalysis() {
        return itemAnalysis;
    }

    public String getItemAnalysisResourceId() {
        return itemAnalysisResourceId;
    }

    public String getKnowledgePoint() {
        return knowledgePoint;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<AttachmentListEntity> getAttachmentList() {
        return attachmentList;
    }


    public List<String> getTrueFillAnswer() {
        return trueFillAnswer;
    }

    public void setTrueFillAnswer(List<String> trueFillAnswer) {
        this.trueFillAnswer = trueFillAnswer;
    }

   /* public static class AttachmentListEntity implements  Parcelable{
        private String attachmentId;
        private String attachmentName;

        public void setAttachmentId(String attachmentId) {
            this.attachmentId = attachmentId;
        }

        public void setAttachmentName(String attachmentName) {
            this.attachmentName = attachmentName;
        }

        public String getAttachmentId() {
            return attachmentId;
        }

        public String getAttachmentName() {
            return attachmentName;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.attachmentId);
            dest.writeString(this.attachmentName);
        }

        public static final Parcelable.Creator<AttachmentListEntity> CREATOR = new Parcelable.Creator<AttachmentListEntity>() {
            public AttachmentListEntity createFromParcel(Parcel source) {
                return new AttachmentListEntity(source);
            }

            public AttachmentListEntity[] newArray(int size) {
                return new AttachmentListEntity[size];
            }
        };

        protected AttachmentListEntity(Parcel in) {
            this.attachmentId = in.readString();
            this.attachmentName = in.readString();
        }

    }*/

    public static class AttachmentListEntity{
        private String attachmentId;
        private String attachmentName;
        private String attachmentSize;

        public String getAttachmentSize() {
            return attachmentSize;
        }

        public void setAttachmentSize(String attachmentSize) {
            this.attachmentSize = attachmentSize;
        }

        public void setAttachmentId(String attachmentId) {
            this.attachmentId = attachmentId;
        }

        public void setAttachmentName(String attachmentName) {
            this.attachmentName = attachmentName;
        }

        public String getAttachmentId() {
            return attachmentId;
        }

        public String getAttachmentName() {
            return attachmentName;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.difficulty);
        dest.writeString(this.itemAnalysis);
        dest.writeString(this.itemAnalysisResourceId);
        dest.writeString(this.knowledgePoint);
        dest.writeString(this.trueAnswer);
        dest.writeString(this.workContent);
        dest.writeString(this.workItemId);
        dest.writeInt(this.blankCount);
        dest.writeString(this.workItemOptions);
        dest.writeString(this.workItemResourceUrl);
        dest.writeString(this.workItemType);
        dest.writeString(this.resourceType);
        dest.writeInt(this.color);
        dest.writeInt(this.workItemIndex);
        dest.writeStringList(this.trueFillAnswer);
    }

    public static final Parcelable.Creator<ItemInfoClass> CREATOR = new Parcelable.Creator<ItemInfoClass>() {
        public ItemInfoClass createFromParcel(Parcel source) {
            return new ItemInfoClass(source);
        }

        public ItemInfoClass[] newArray(int size) {
            return new ItemInfoClass[size];
        }
    };

    protected ItemInfoClass(Parcel in) {
        this.difficulty = in.readString();
        this.itemAnalysis = in.readString();
        this.itemAnalysisResourceId = in.readString();
        this.knowledgePoint = in.readString();
        this.trueAnswer = in.readString();
        this.workContent = in.readString();
        this.workItemId = in.readString();
        this.blankCount = in.readInt();
        this.workItemOptions = in.readString();
        this.workItemResourceUrl = in.readString();
        this.workItemType = in.readString();
        this.resourceType = in.readString();
        this.color = in.readInt();
        this.workItemIndex = in.readInt();
        this.trueFillAnswer = in.createStringArrayList();
    }

    public static List<ItemInfoClass> parseResponse(JSONObject response){
        List<ItemInfoClass> list = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("list");
            for(int i = 0;i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ItemInfoClass itemInfoClass = new ItemInfoClass();
                    itemInfoClass.setWorkItemIndex(i + 1);
                    itemInfoClass.setWorkItemId(jsonObject.isNull("workItemId")?"":jsonObject.optString("workItemId"));
                    itemInfoClass.setWorkItemType(jsonObject.isNull("workItemType")?"":jsonObject.optString("workItemType"));
                    itemInfoClass.setResourceType(jsonObject.isNull("resourceType")?"":jsonObject.optString("resourceType"));
                    itemInfoClass.setWorkContent(jsonObject.isNull("workItemContent")?"":jsonObject.optString("workItemContent"));
                    itemInfoClass.setWorkItemResourceUrl(jsonObject.isNull("workItemResourceUrl")?"":jsonObject.optString("workItemResourceUrl"));
                    itemInfoClass.setWorkItemOptions(jsonObject.isNull("workItemOptions")?"":jsonObject.optString("workItemOptions"));
                    itemInfoClass.setTrueAnswer(jsonObject.isNull("trueAnswer")?"":jsonObject.optString("trueAnswer"));
                    JSONArray trueFillAnswerArray = jsonObject.optJSONArray("trueFillAnswer");
                    if(trueFillAnswerArray != null){
                        List<String> fillAnswerList = new ArrayList<>();
                        for (int t = 0 ; t < trueFillAnswerArray.length(); t ++){
                            fillAnswerList.add(trueFillAnswerArray.optString(t).toString());
                            itemInfoClass.setTrueFillAnswer(fillAnswerList);
                        }
                    }
                    itemInfoClass.setItemAnalysis(jsonObject.isNull("itemAnalysis")?"":jsonObject.optString("itemAnalysis"));
                    itemInfoClass.setItemAnalysisResourceId(jsonObject.isNull("itemAnalysisResourceUrl")?"":jsonObject.optString("itemAnalysisResourceUrl"));
                    itemInfoClass.setKnowledgePoint(jsonObject.isNull("knowledgePoint")?"":jsonObject.optString("knowledgePoint"));
                    itemInfoClass.setDifficulty(jsonObject.isNull("difficulty")?"":jsonObject.optString("difficulty"));
                    itemInfoClass.setTextQestion(jsonObject.isNull("textQestion")?"":jsonObject.optString("textQestion"));
                    List<AttachmentListEntity> attachmentListEntityList = new ArrayList<>();
                    JSONArray attachmentArray = jsonObject.optJSONArray("attachmentList");
                    if(attachmentArray != null){
                        for(int k = 0 ; k < attachmentArray.length(); k ++){
                            AttachmentListEntity attachmentListEntity = new AttachmentListEntity();
                            JSONObject attachmentObject = attachmentArray.optJSONObject(k);
                            attachmentListEntity.setAttachmentId(attachmentObject.isNull("attachmentId")?"":attachmentObject.optString("attachmentId"));
                            attachmentListEntity.setAttachmentName(attachmentObject.isNull("attachmentName")?"":attachmentObject.optString("attachmentName"));
                            attachmentListEntity.setAttachmentSize(attachmentObject.isNull("docSize")?"":attachmentObject.optString("docSize"));
                            attachmentListEntityList.add(attachmentListEntity);
                        }
                    }
                    itemInfoClass.setAttachmentList(attachmentListEntityList);
                    list.add(itemInfoClass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return list;
    }
}

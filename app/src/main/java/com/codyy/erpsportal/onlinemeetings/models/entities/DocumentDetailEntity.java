package com.codyy.erpsportal.onlinemeetings.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldh on 2015/8/20.
 */
public class DocumentDetailEntity {


    /**
     * docName : 519552078.png
     * uploadPerson : pp
     * meet_id : 681f2fee6216443588c171e5b13248b9
     * docId : eafcd563556142689de3123a05cf38d5
     */
    private String docName;
    private String uploadPerson;
    private String meet_id;
    private String docId;
    private String docPath;//下载路径

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public void setUploadPerson(String uploadPerson) {
        this.uploadPerson = uploadPerson;
    }

    public void setMeet_id(String meet_id) {
        this.meet_id = meet_id;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public String getUploadPerson() {
        return uploadPerson;
    }

    public String getMeet_id() {
        return meet_id;
    }

    public String getDocId() {
        return docId;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public static List<DocumentDetailEntity> parseArray(JSONArray jsonArray){
        List<DocumentDetailEntity> documentDetailEntityList = new ArrayList<>();

        for(int i = 0 ; i< jsonArray.length(); i ++){
            DocumentDetailEntity documentDetailEntity = new DocumentDetailEntity();

            JSONObject jsonObject = jsonArray.optJSONObject(i);
            documentDetailEntity.setDocId(jsonObject.optString("docId"));
            documentDetailEntity.setUploadPerson(jsonObject.optString("uploadPerson"));
            documentDetailEntity.setMeet_id(jsonObject.optString("meet_id"));
            documentDetailEntity.setDocName(jsonObject.optString("docName"));
            documentDetailEntity.setDocPath(jsonObject.optString("docPath"));

            documentDetailEntityList.add(documentDetailEntity);
        }
        return  documentDetailEntityList;
    }

}

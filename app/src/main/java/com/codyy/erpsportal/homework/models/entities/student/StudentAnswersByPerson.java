package com.codyy.erpsportal.homework.models.entities.student;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 按人批阅学生答案实体类
 * Created by ldh on 2016/2/2.
 */
public class StudentAnswersByPerson implements Parcelable {

    /**
     * result : success
     * nomalAnswerList : [{"workQuestionId":"fdgafgdafadgfadfadssd","myAnswer":"B","answerVideo":" Sleep Away.mp3","answerVideoResId":" 05dd0372535b4a79916ddef8a083c0aa","serverAddress":" http://10.5.225.36:8080/ResourceServer","comment":"回答的很好"},{"workQuestionId":"fdgafgdafadgfadfadssd","myAnswer":"B","answerVideo":" Sleep Away.mp3","answerVideoResId":"05dd0372535b4a79916ddef8a083c0aa","serverAddress":"http://10.5.225.36:8080/ResourceServer","comment":"回答的很好"}]
     * textAnswer : 文本题答案
     * textAnswerComment : 很好！
     * totalComment : 很好
     * docAnswerList : [{"docName":"学生回答附件 .docx","docSize":"355685"},{"docName":"学生回答附件 .docx","docSize":"355685"}]
     */

    private String result;
    private String textAnswer;
    private String textAnswerComment;
    private String docAnswerComment;
    private String totalComment;
    private String textReadOverFlag;
    private String docReadOverFlag;
    /**
     * workQuestionId : fdgafgdafadgfadfadssd
     * myAnswer : B
     * answerVideo :  Sleep Away.mp3
     * answerVideoResId :  05dd0372535b4a79916ddef8a083c0aa
     * serverAddress :  http://10.5.225.36:8080/ResourceServer
     * comment : 回答的很好
     */

    private List<NomalAnswerListEntity> nomalAnswerList;
    /**
     * docName : 学生回答附件 .docx
     * docSize : 355685
     */

    private List<DocAnswerListEntity> docAnswerList;

    public void setResult(String result) {
        this.result = result;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public void setTextAnswerComment(String textAnswerComment) {
        this.textAnswerComment = textAnswerComment;
    }

    public void setTotalComment(String totalComment) {
        this.totalComment = totalComment;
    }

    public void setNomalAnswerList(List<NomalAnswerListEntity> nomalAnswerList) {
        this.nomalAnswerList = nomalAnswerList;
    }

    public void setDocAnswerList(List<DocAnswerListEntity> docAnswerList) {
        this.docAnswerList = docAnswerList;
    }

    public String getResult() {
        return result;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public String getTextAnswerComment() {
        return textAnswerComment;
    }

    public String getTotalComment() {
        return totalComment;
    }

    public String getDocAnswerComment() {
        return docAnswerComment;
    }

    public void setDocAnswerComment(String docAnswerComment) {
        this.docAnswerComment = docAnswerComment;
    }

    public List<NomalAnswerListEntity> getNomalAnswerList() {
        return nomalAnswerList;
    }

    public List<DocAnswerListEntity> getDocAnswerList() {
        return docAnswerList;
    }

    public String getTextReadOverFlag() {
        return textReadOverFlag;
    }

    public void setTextReadOverFlag(String textReadOverFlag) {
        this.textReadOverFlag = textReadOverFlag;
    }

    public String getDocReadOverFlag() {
        return docReadOverFlag;
    }

    public void setDocReadOverFlag(String docReadOverFlag) {
        this.docReadOverFlag = docReadOverFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeString(this.textAnswer);
        dest.writeString(this.textAnswerComment);
        dest.writeString(this.totalComment);
        dest.writeList(this.docAnswerList);
        dest.writeList(this.nomalAnswerList);
        dest.writeString(this.docAnswerComment);
        dest.writeString(this.docReadOverFlag);
        dest.writeString(this.textReadOverFlag);
    }

    public static final Parcelable.Creator<StudentAnswersByPerson> CREATOR = new Parcelable.Creator<StudentAnswersByPerson>() {
        public StudentAnswersByPerson createFromParcel(Parcel source) {
            return new StudentAnswersByPerson(source);
        }

        public StudentAnswersByPerson[] newArray(int size) {
            return new StudentAnswersByPerson[size];
        }
    };

    protected StudentAnswersByPerson(Parcel in) {
        this.result = in.readString();
        this.textAnswer = in.readString();
        this.textAnswerComment = in.readString();
        this.totalComment = in.readString();
        this.docAnswerComment = in.readString();
        this.docReadOverFlag = in.readString();
        this.textReadOverFlag = in.readString();
    }

    public StudentAnswersByPerson(){

    }

    public static class NomalAnswerListEntity implements Parcelable {
        private String workQuestionId;
        private String myAnswer;
        private String answerVideo;
        private String answerVideoResId;
        private String serverAddress;
        private String comment;
        private String correctFlag;
        private String readOverFlag;

        public String getReadOverFlag() {
            return readOverFlag;
        }

        public void setReadOverFlag(String readOverFlag) {
            this.readOverFlag = readOverFlag;
        }

        public String getCorrectFlag() {
            return correctFlag;
        }

        public void setCorrectFlag(String correctFlag) {
            this.correctFlag = correctFlag;
        }

        public void setWorkQuestionId(String workQuestionId) {
            this.workQuestionId = workQuestionId;
        }

        public void setMyAnswer(String myAnswer) {
            this.myAnswer = myAnswer;
        }

        public void setAnswerVideo(String answerVideo) {
            this.answerVideo = answerVideo;
        }

        public void setAnswerVideoResId(String answerVideoResId) {
            this.answerVideoResId = answerVideoResId;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getWorkQuestionId() {
            return workQuestionId;
        }

        public String getMyAnswer() {
            return myAnswer;
        }

        public String getAnswerVideo() {
            return answerVideo;
        }

        public String getAnswerVideoResId() {
            return answerVideoResId;
        }

        public String getServerAddress() {
            return serverAddress;
        }

        public String getComment() {
            return comment;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.workQuestionId);
            dest.writeString(this.myAnswer);
            dest.writeString(this.answerVideo);
            dest.writeString(this.answerVideoResId);
            dest.writeString(this.comment);
            dest.writeString(this.serverAddress);
            dest.writeString(this.correctFlag);
            dest.writeString(this.readOverFlag);
        }

        public static final Parcelable.Creator<NomalAnswerListEntity> CREATOR = new Parcelable.Creator<NomalAnswerListEntity>() {
            public NomalAnswerListEntity createFromParcel(Parcel source) {
                return new NomalAnswerListEntity(source);
            }

            public NomalAnswerListEntity[] newArray(int size) {
                return new NomalAnswerListEntity[size];
            }
        };

        protected NomalAnswerListEntity(Parcel in) {
            this.workQuestionId = in.readString();
            this.myAnswer = in.readString();
            this.answerVideo = in.readString();
            this.answerVideoResId = in.readString();
            this.comment = in.readString();
            this.serverAddress = in.readString();
            this.correctFlag = in.readString();
            this.readOverFlag = in.readString();
        }

        protected NomalAnswerListEntity() {
        }
    }

    public static class DocAnswerListEntity implements Parcelable {
        private String docName;
        private String docSize;

        public void setDocName(String docName) {
            this.docName = docName;
        }

        public void setDocSize(String docSize) {
            this.docSize = docSize;
        }

        public String getDocName() {
            return docName;
        }

        public String getDocSize() {
            return docSize;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.docName);
            dest.writeString(this.docSize);
        }

        public static final Parcelable.Creator<DocAnswerListEntity> CREATOR = new Parcelable.Creator<DocAnswerListEntity>() {
            public DocAnswerListEntity createFromParcel(Parcel source) {
                return new DocAnswerListEntity(source);
            }

            public DocAnswerListEntity[] newArray(int size) {
                return new DocAnswerListEntity[size];
            }
        };

        protected DocAnswerListEntity(Parcel in) {
            this.docName = in.readString();
            this.docSize = in.readString();
        }

        protected DocAnswerListEntity() {
        }
    }

    public static StudentAnswersByPerson parseResponse(JSONObject response) {
        StudentAnswersByPerson studentAnswersByPerson = new StudentAnswersByPerson();
        if (response.optString("result").equals("success")) {
            studentAnswersByPerson.setResult(response.isNull("result") ? "" : response.optString("result"));
            studentAnswersByPerson.setTextAnswer(response.isNull("textAnswer") ? "" : response.optString("textAnswer"));
            studentAnswersByPerson.setTextAnswerComment(response.isNull("textAnswerComment") ? "" : response.optString("textAnswerComment"));
            studentAnswersByPerson.setTotalComment(response.isNull("totalComment") ? "" : response.optString("totalComment"));
            studentAnswersByPerson.setDocAnswerComment(response.isNull("docAnswerComment") ? "" : response.optString("docAnswerComment"));
            studentAnswersByPerson.setDocReadOverFlag(response.isNull("docReadOverFlag") ? "" : response.optString("docReadOverFlag"));
            studentAnswersByPerson.setTextReadOverFlag(response.isNull("textReadOverFlag") ? "" : response.optString("textReadOverFlag"));

            List<NomalAnswerListEntity> nomalAnswerList = new ArrayList<>();
            JSONArray nomalAnswerArray = response.optJSONArray("nomalAnswerList");
            for (int i = 0; i < nomalAnswerArray.length(); i++) {
                NomalAnswerListEntity nomalAnswerListEntity = new NomalAnswerListEntity();
                JSONObject normalAnswerObject = nomalAnswerArray.optJSONObject(i);
                if(normalAnswerObject != null){
                    nomalAnswerListEntity.setWorkQuestionId(normalAnswerObject.isNull("workQuestionId") ? "" : normalAnswerObject.optString("workQuestionId"));
                    nomalAnswerListEntity.setMyAnswer(normalAnswerObject.isNull("myAnswer") ? "" : normalAnswerObject.optString("myAnswer"));
                    nomalAnswerListEntity.setAnswerVideo(normalAnswerObject.isNull("answerVideo") ? "" : normalAnswerObject.optString("answerVideo"));
                    nomalAnswerListEntity.setAnswerVideoResId(normalAnswerObject.isNull("answerVideoResId") ? "" : normalAnswerObject.optString("answerVideoResId"));
                    nomalAnswerListEntity.setServerAddress(normalAnswerObject.isNull("serverAddress") ? "" : normalAnswerObject.optString("serverAddress"));
                    nomalAnswerListEntity.setComment(normalAnswerObject.isNull("comment") ? "" : normalAnswerObject.optString("comment"));
                    nomalAnswerListEntity.setCorrectFlag(normalAnswerObject.isNull("correctFlag") ? "" : normalAnswerObject.optString("correctFlag"));
                    nomalAnswerListEntity.setReadOverFlag(normalAnswerObject.isNull("readOverFlag") ? "" : normalAnswerObject.optString("readOverFlag"));
                    nomalAnswerList.add(nomalAnswerListEntity);
                }
            }
            studentAnswersByPerson.setNomalAnswerList(nomalAnswerList);

            List<DocAnswerListEntity> docAnswerEntityList = new ArrayList<>();
            JSONArray docAnswerArray = response.optJSONArray("docAnswerList");
            for (int i = 0; i < docAnswerArray.length(); i++) {
                DocAnswerListEntity docAnswerListEntity = new DocAnswerListEntity();
                JSONObject docAnswerObject = docAnswerArray.optJSONObject(i);
                docAnswerListEntity.setDocName(docAnswerObject.isNull("docName") ? "" : docAnswerObject.optString("docName"));
                docAnswerListEntity.setDocSize(docAnswerObject.isNull("docSize") ? "" : docAnswerObject.optString("docSize"));
                docAnswerEntityList.add(docAnswerListEntity);
            }
            studentAnswersByPerson.setDocAnswerList(docAnswerEntityList);
        }
        return studentAnswersByPerson;
    }
}

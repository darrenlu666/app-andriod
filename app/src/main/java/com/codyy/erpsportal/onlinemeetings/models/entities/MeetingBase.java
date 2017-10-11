package com.codyy.erpsportal.onlinemeetings.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.util.SparseArray;

import com.codyy.erpsportal.commons.models.entities.CocoEntity;
import com.codyy.erpsportal.commons.models.entities.LoopPatrol;
import com.codyy.erpsportal.commons.models.entities.SpeakerEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频会议基本信息
 * Created by poe on 15-8-14.
 */
public class MeetingBase implements Parcelable {

    /**
     * 视频模式(互动区)
     */
    public static final String BASE_MEET_MODEL_VIDEO = "0";

    /**
     * 演示模式(互动区)
     */
    public static final String BASE_MEET_MODEL_SHOW  = "1";

    public static final int BASE_MEET_ROLE_0 =   0;//主讲人
    public static final int BASE_MEET_ROLE_1 =   1;//发言人
    public static final int BASE_MEET_ROLE_2 =   2;//普通参会者
    public static final int BASE_MEET_ROLE_3 =   3;//观摩者
    public static final int BASE_MEET_ROLE_4 =   4;//来宾(仅显示) .

    public static final SparseArray<String> ROLES =   new SparseArray<>();
    static {
        ROLES.put(BASE_MEET_ROLE_0,"主持人");
        ROLES.put(BASE_MEET_ROLE_1,"发言人");
        ROLES.put(BASE_MEET_ROLE_2,"参会者");
        ROLES.put(BASE_MEET_ROLE_3,"观摩者");
        ROLES.put(BASE_MEET_ROLE_4,"来宾");
    }

    @IntDef(flag = true,value = {BASE_MEET_ROLE_0,BASE_MEET_ROLE_1,BASE_MEET_ROLE_2,BASE_MEET_ROLE_3,BASE_MEET_ROLE_4})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface MeetRole{}

    @StringDef(value = {BASE_MEET_MODEL_VIDEO,BASE_MEET_MODEL_SHOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface  MeetModel{}

    private String baseMeetID;//meeting id .
    private String baseTitle;// meeting title
    private String baseBeginTime;//meeting start time .
    private String baseModel;//会议当前的视频模式 0:视频模式 1： 演示模式
    private String baseMeetingPersist;//会议持续时间 ms (123123)
    private int baseRole;//我在会议中扮演的较色 //0:主讲人 1:发言者  2:参会者 3:观摩者
    private int baseSay;//0:正常状态 1:禁言(禁止聊天)
    private int baseNoDisturbing;//0:正常状态 1:免打扰 .
    private String  white_board;//是否拥有 "白板标注权限" 0:没有　１：拥有白板标注权限　
    private String baseChat = "1";//文本聊天功能　．　０　：　无法说话　１：可以说话
    private String token ;//token信息

    private DMSEntity baseDMS; //DMS info about video meeting services .
    private CocoEntity baseCoco;//coco services info for messages .
    private VideoShare baseVideoShare;//视频共享 0:正常状态 1:免打扰 .
    private DeskShare baseDeskShare;//桌面共享  0:正常状态 1:免打扰 .
    private LoopPatrol baseLoopPatrol;//轮巡     0:正常状态 1:免打扰

    private List<SpeakerEntity> baseSpeakers;//所有的发言者 .


    public MeetingBase() {
    }

    protected MeetingBase(Parcel in) {
        baseMeetID = in.readString();
        baseTitle = in.readString();
        baseBeginTime = in.readString();
        baseModel   =   in.readString();
        baseMeetingPersist = in.readString();
        baseRole    =   in.readInt();
        baseSay     =   in.readInt();
        baseNoDisturbing    =   in.readInt();
        white_board = in.readString();
        baseChat = in.readString();
        token = in.readString();

        //嵌套实体类读取
        baseDMS         =   in.readParcelable(DMSEntity.class.getClassLoader());
        baseCoco        =   in.readParcelable(CocoEntity.class.getClassLoader());
        baseVideoShare  =   in.readParcelable(VideoShare.class.getClassLoader());
        baseDeskShare   =   in.readParcelable(DeskShare.class.getClassLoader());
        baseLoopPatrol  =   in.readParcelable(LoopPatrol.class.getClassLoader());

        //读取 数据集合
        baseSpeakers = new ArrayList<>();
        in.readTypedList(baseSpeakers, SpeakerEntity.CREATOR);

    }
    /**
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(baseMeetID);
        dest.writeString(baseTitle);
        dest.writeString(baseBeginTime);
        dest.writeString(baseModel);
        dest.writeString(baseMeetingPersist);
        dest.writeInt(baseRole);
        dest.writeInt(baseSay);
        dest.writeInt(baseNoDisturbing);
        dest.writeString(white_board);
        dest.writeString(baseChat);
        dest.writeString(token);

        dest.writeParcelable(baseDMS, flags);
        dest.writeParcelable(baseCoco, flags);
        dest.writeParcelable(baseVideoShare, flags);
        dest.writeParcelable(baseDeskShare, flags);
        dest.writeParcelable(baseLoopPatrol, flags);

        //写入数据集合
        dest.writeTypedList(baseSpeakers);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseChat() {
        if(null == baseChat)
            baseChat = "";
        return baseChat;
    }

    public void setBaseChat(String baseChat) {
        this.baseChat = baseChat;
    }

    public boolean isWhiteBoardManager() {
        return "1".equals(this.white_board);
    }

    public void setWhiteBoardManager(String whiteBoardManager) {
        this.white_board =whiteBoardManager;
    }
    public String getBaseMeetID() {
        return baseMeetID;
    }

    public void setBaseMeetID(String baseMeetID) {
        this.baseMeetID = baseMeetID;
    }

    public String getBaseTitle() {
        return baseTitle;
    }

    public void setBaseTitle(String baseTitle) {
        this.baseTitle = baseTitle;
    }

    public String getBaseBeginTime() {
        return baseBeginTime;
    }

    public void setBaseBeginTime(String baseBeginTime) {
        this.baseBeginTime = baseBeginTime;
    }

    @MeetModel
    public String getBaseModel() {
        return baseModel;
    }

    public void setBaseModel(@MeetModel String baseModel) {
        this.baseModel = baseModel;
    }

    @MeetRole
    public int getBaseRole() {
        return baseRole;
    }

    public void setBaseRole(@MeetRole int baseRole) {
        this.baseRole = baseRole;
    }

    public String getBaseMeetingPersist() {
        return baseMeetingPersist;
    }

    public void setBaseMeetingPersist(String baseMeetingPersist) {
        this.baseMeetingPersist = baseMeetingPersist;
    }

    public int getBaseSay() {
        return baseSay;
    }

    public void setBaseSay(int baseSay) {
        this.baseSay = baseSay;
    }

    public int getBaseNoDisturbing() {
        return baseNoDisturbing;
    }

    /**
     * 0:关闭 1:开启
     * @param baseNoDisturbing
     */
    public void setBaseNoDisturbing(int baseNoDisturbing) {
        this.baseNoDisturbing = baseNoDisturbing;
    }

    public VideoShare getBaseVideoShare() {
        return baseVideoShare;
    }

    public void setBaseVideoShare(VideoShare baseVideoShare) {
        this.baseVideoShare = baseVideoShare;
    }

    public DeskShare getBaseDeskShare() {
        return baseDeskShare;
    }

    public void setBaseDeskShare(DeskShare baseDeskShare) {
        this.baseDeskShare = baseDeskShare;
    }

    public LoopPatrol getBaseLoopPatrol() {
        return baseLoopPatrol;
    }

    public void setBaseLoopPatrol(LoopPatrol baseLoopPatrol) {
        this.baseLoopPatrol = baseLoopPatrol;
    }


    public DMSEntity getBaseDMS() {
        return baseDMS;
    }

    public void setBaseDMS(DMSEntity baseDMS) {
        this.baseDMS = baseDMS;
    }

    public CocoEntity getBaseCoco() {
        return baseCoco;
    }

    public void setBaseCoco(CocoEntity baseCoco) {
        this.baseCoco = baseCoco;
    }

    public List<SpeakerEntity> getBaseSpeakers() {
        return baseSpeakers;
    }

    public void setBaseSpeakers(List<SpeakerEntity> baseSpeakers) {
        this.baseSpeakers = baseSpeakers;
    }


    public static MeetingBase parseJson(JSONObject response) {

        if (null == response) return null;

        MeetingBase mb = new MeetingBase();
        mb.setBaseMeetID(response.optString("meet_id"));
        mb.setBaseTitle(response.optString("title"));
        mb.setBaseBeginTime(response.optString("begin_time"));
        String model = response.optString("meet_modle");
        mb.setBaseModel("0".equals(model) ? BASE_MEET_MODEL_VIDEO : BASE_MEET_MODEL_SHOW);
        int role = response.optInt("role");
        int baseRole;
        switch (role){
            case BASE_MEET_ROLE_0:
                baseRole    =   BASE_MEET_ROLE_0;
                break;
            case BASE_MEET_ROLE_1:
                baseRole    =   BASE_MEET_ROLE_1;
                break;
            case BASE_MEET_ROLE_2:
                baseRole    =   BASE_MEET_ROLE_2;
                break;
            case BASE_MEET_ROLE_3:
                baseRole    =   BASE_MEET_ROLE_3;
                break;
            case BASE_MEET_ROLE_4:
                baseRole    =   BASE_MEET_ROLE_4;
                break;
            default:
                baseRole = BASE_MEET_ROLE_2;
                break;
        }
        mb.setBaseRole(baseRole);
        mb.setBaseMeetingPersist(response.optString("persist_time"));
        mb.setBaseSay(response.optInt("say_author"));
        mb.setBaseNoDisturbing(response.optInt("meet_no_disturbing"));
        //white_board ：　０　ｏｒ　１　白板标注权限　.
        mb.setWhiteBoardManager(response.optString("white_board"));
        //cocoa connect info .
        JSONObject cocoJson = response.optJSONObject("cocoa");
        mb.setBaseCoco(CocoEntity.parseJson(cocoJson));
        //ppMeet info
        JSONObject dmsJson = response.optJSONObject("ppmeet");
        mb.setBaseDMS(DMSEntity.parseJson(dmsJson));

        //speakers
        JSONArray speakerJsonArray = response.optJSONArray("speakers");
        mb.setBaseSpeakers(SpeakerEntity.parseList(speakerJsonArray));

        return mb;
    }

    @Override
    public int describeContents() {
        return 0;
    }




    public static final Parcelable.Creator<MeetingBase> CREATOR = new Parcelable.Creator<MeetingBase>() {

        @Override
        public MeetingBase createFromParcel(Parcel in) {

            MeetingBase part = new MeetingBase(in);

            return part;
        }

        @Override
        public MeetingBase[] newArray(int size) {

            return new MeetingBase[size];
        }
    };
}

package com.codyy.erpsportal.onlinemeetings.models.entities.coco;

/**
 * 记录视频会议相关命令
 * Created by poe on 17-9-21.
 */

public class MeetingCommand {

    /**
     * 翻页
     **/
    public final static String CHANGE_DOC = "changeDoc";
    /**
     * 切换到白板.
     */
    public final static String CHANGE_DOC_PAD = "ChangeDoc";
    /**
     * 文档操作：翻页，
     **/
    public final static String COMMAND_DO＿WRITE = "communication.doWrite";//操作文档
    /**
     * 缩放文档
     **/
    public final static String ZOOM = "zoom";
    /**
     * 演示文档
     **/
    public final static String SHOW_DOC = "ShowDoc";
    /**
     * 删除文档
     **/
    public final static String DELETE_DOC = "DeleteDoc";

    /**
     * 上传文档
     */
    public final static String WEB_ADD_DOCUMENT = "communication.addDocItem";
    /**
     * 刷新文档列表
     */
    public final static String COMMAND_REFRESH_DOC = "communication.showDocList";


    /**
     * web-参会者禁用白板-默认 ["writeControl", true/false]
     */
    public final static String WEB_WHITE_BOARD_MARK = "writeControl";

    /**
     * 主持人拒绝某人发言
     **/
    public final static String REFUSE_SPEAKER_BACK = "Codyy.Meet.SpeakerSet.refuseSpeakerBack";
    /**
     * 收到设置发言人消息
     **/
    public final static String INFO_AGREE_SPEAKER_BACK = "toBeSpeaker";

    /**
     * web端设置发言人命令
     */
    public final static String WEB_SET_SPEAKER= "setCurrentSpeakers";


    /**
     * 主持人设置某人发言-群发
     **/
    public final static String INFO_AGREE_SPEAKER_BACK＿ALL = "receiveSpeaker";
    /**
     * 主持人取消某人发言－单发
     **/
    public final static String INFO_CANCEL_SPEAKER = "cancelSpeaker";
    /**
     * 主持人取消某人发言－群发通知
     **/
    public final static String WEB_CANCEL_SPEAKER_ALL = "stopReceive";
    /**
     * web主持人-禁止某人的音频功能
     */
    public final static String WEB_STOP_AUDIO = "stopAudio";

    /**
     * web主持人-取消参会者禁言
     */
    public final static String WEB_PUBLISH_AUDIO = "publishAudio";

    /**
     * web主持人-设置参会者禁画面
     */
    public final static String WEB_STOP_VIDEO = "stopVideo";

    /**
     * web主持人-取消参会者禁画面
     */
    public final static String WEB_PUBLISH_VIDEO = "publishVideo";

    /**
     * 手机端－申请发言
     **/
    public final static String CMD_APPLY_SPEAKER = "applySpeak";
    /**
     * 手机－取消发言人
     **/
    public final static String CMD_CANCEL_SPEAKER = "applyCancelSpeaker";
    /**
     * 主持人－禁止发言（开启免打扰）
     **/
    public final static String WEB_NO_SPEAKER = "noSpeak";
    /**
     * 主持人－取消禁止发言（关闭免打扰）
     **/
    public final static String WEB_ALLOW_SPEAKER = "allowSpeak";

    /**
     * 主持人－禁止聊天（所有人不能发消息）
     **/
    public final static String WEB_CHAT_CONTROL = "chatControl";
    /**
     * 发起点名.
     */
    public final static String WEB_SIGN_START = "startSign";

    public final static String INFO_DESK_SHARE_OPEN = "receiveShareDesk";//开始桌面共享
    public final static String INFO_DESK_SHARE_CLOSE = "stopReceiveShareDesk";//结束桌面共享
    public final static String INFO_VIDEO_SHARE_OPEN = "videoTransfer";//开始视频共享
    public final static String INFO_VIDEO_SHARE_CLOSE = "stopReceiveShareVideo";//结束视频共享

    /**
     * 切换模式
     **/
    public final static String WEB_SWITCH_MODE = "turnMode";
    /**
     * web主持人-禁止参会者文本聊天["noChat", true/false]
     **/
    public final static String WEB_CHAT_IS_CLOSE_BACK = "noChat";

    public final static String UPLOADING_BACK = "uploadingBack";
    public final static String CANCEL_UPLOAD_SWFBACK = "cancelUploadSwfBack";
    public final static String COMPLATE_UPLOAD_FILE = "complateUploadFile";
    /**
     * 开启轮训
     */
    public final static String WEB_COMMAND_LOOP = "communication.broadcast";
    /**
     * （总）开启轮巡
     **/
    public final static String WEB_COMMAND_PUBLISH = "publish";
    /**
     * （总）关闭轮巡
     **/
    public final static String WEB_COMMAND_UN_PUBLISH = "unPublish";

    public final static String COMMON_RECEIVE_PLAY = "commonReceivePlay";
    public final static String LOCATION_RELOAD = "location.reload";

    /**
     * 会议结束.
     */
    public final static String INFO_END_MEET = "endMeeting";

    /**
     * 主持人踢人
     */
    public final static String WEB_ACTION_KICK_OUT = "kickOut";
}

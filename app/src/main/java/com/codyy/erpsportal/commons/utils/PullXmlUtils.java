package com.codyy.erpsportal.commons.utils;

import android.util.Log;
import android.util.Xml;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.onlinemeetings.models.entities.DocControl;
import com.codyy.erpsportal.commons.models.entities.LoginOut;
import com.codyy.erpsportal.commons.models.entities.SystemMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.greenrobot.event.EventBus;

/**
 * Created by eachann on 2015/6/30.
 * pull解析xml
 */
public class PullXmlUtils {
    private final static String TAG = "PullXmlUtils";

    private final static String UTF = "UTF-8";
    private final static String CALL = "call";
    private final static String ACT = "act";
    private final static String PARS = "pars";
    private final static String SAY = "say";
    private final static String DEF = "";
    private final static String FROM = "from";
    private final static String TO = "to";
    private final static String NICK = "send_nick";
    private final static String API = "api";
    private final static String TIME = "time";
    private final static String TYPE = "type";
    private final static String TEXT = "text";

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
    public final static String COMMAND_REFRESH_DOC= "communication.showDocList";

    private final static String GROUP = "group";
    private final static String ROOT = "root";
    /**
     * 群聊
     **/
    private final static String SEND_MSG_TO_ALL = "sendMsgToAll";
    /**
     * 单聊
     **/
    private final static String SEND_MSG_TO_ONE = "sendMsgToOne";
    /**
     * 设置白板标注权限 pars:"true"
     */
    public final static String COMMAND_WHITE_BOARD_MARK = "communication.writeControl";
    /**
     * 主持人拒绝某人发言
     **/
    public final static String REFUSE_SPEAKER_BACK = "Codyy.Meet.SpeakerSet.refuseSpeakerBack";
    /**
     * 主持人设置某人发言-单发
     **/
    public final static String AGREE_SPEAKER_BACK = "communication.toBeSpeaker";
    /**
     * 主持人设置某人发言-群发
     **/
    public final static String AGREE_SPEAKER_BACK＿ALL = "communication.receiveSpeaker";
    /**
     * 主持人取消某人发言－单发
     **/
    public final static String WEB_CANCEL_SPEAKER = "communication.cancelSpeaker";
    /**
     * 主持人取消某人发言－群发通知
     **/
    public final static String WEB_CANCEL_SPEAKER_ALL = "communication.stopReceive";
    /**
     * 禁止某人的音频功能
     */
    public final static String WEB_STOP_AUDIO = "communication.stopAudio";
    /**
     * 禁止某人的视频功能
     */
    public final static String WEB_STOP_VIDEO = "communication.stopVideo";
    /**
     * 手机端－申请发言
     **/
    public final static String APPLY_SPEAKER = "communication.applySpeak";
    /**
     * 手机－取消发言人
     **/
    public final static String CANCEL_SPEAKER = "communication.applyCancelSpeaker";
    /**
     * 主持人－禁止发言（开启免打扰）
     **/
    public final static String WEB_NO_SPEAKER = "communication.noSpeak";
    /**
     * 主持人－取消禁止发言（关闭免打扰）
     **/
    public final static String WEB_ALLOW_SPEAKER = "communication.allowSpeak";

    /**
     * 主持人－禁止聊天（所有人不能发消息）
     **/
    public final static String WEB_CHAT_CONTROL = "communication.chatControl";

    public final static String RD_CALL_PLAY = "communication.receiveShareDesk";//开始桌面共享
    public final static String RD_CALL_STOP = "communication.stopReceiveShareDesk";//结束桌面共享
    public final static String VS_CREATE_PLAYER = "communication.receiveShareVideo";//开始视频共享
    public final static String VS_CALL_STOP = "communication.stopReceiveShareVideo";//结束视频共享
    public final static String VS_CALL_START = "Codyy.Meet.VS.callStart";
    /**
     * 踢出会议
     **/
    public final static String KICK_MEET = "communication.kickOut";
    /**
     * 切换模式
     **/
    public final static String SWITCH_MODE = "communication.turnMode";
    /**
     * 禁止某人聊天(单人)
     **/
    public final static String CHAT_IS_CLOSE_BACK = "communication.noChat";

    public final static String UPLOADING_BACK = "uploadingBack";
    public final static String CANCEL_UPLOAD_SWFBACK = "cancelUploadSwfBack";
    public final static String COMPLATE_UPLOAD_FILE = "complateUploadFile";
    /**
     * 开启轮训
     */
    public final static String WEB_COMMAND_LOOP = "communication.broadcast";
    /** （总）开启轮巡**/
    public final static String WEB_COMMAND_PUBLISH = "communication.publish";
    /** （总）关闭轮巡**/
    public final static String WEB_COMMAND_UN_PUBLISH = "communication.unPublish";

    public final static String COMMON_RECEIVE_PLAY = "commonReceivePlay";
    public final static String LOCATION_RELOAD = "location.reload";

    public final static String END_MEET = "communication.endMeeting";
    /**
     * 登录成功
     */
    public static final String TYPE_LOAD_USER = "loadUser";
    /**
     * cipher验证失败
     */
    private static final String TYPE_VERIFY_COCO = "verifyCoco";
    /**
     * 群发
     */
    private static final String MEET = "meet";
    /**
     * 获取当前在线用户
     **/
    public static final String LOAD＿GROUP＿USER = "loadGroupUser";
    /**
     * 心跳包
     */
    private static final String TYPE_KEEP_ALIVE = "keepAlive";
    /**
     * 上线通知
     */
    public static final String TYPE_LOGIN = "login";
    /**
     * 下线
     */
    public static final String TYPE_LOGIN_OUT = "loginout";
    /**
     * 许可证
     */
    private static final String TYPE_LICENSE = "license";

    private static boolean isUnReceive = true;

    /**
     * 使用pull解析流
     *
     * @throws Exception
     */
    public static void parseXml(ByteArrayInputStream byteArrayInputStream) throws Exception {
        XmlPullParser parser = Xml.newPullParser();//得到Pull解析器
        if (byteArrayInputStream == null) {
            return;
        }
        parser.setInput(byteArrayInputStream, UTF);//设置下输入流的编码
        Cog.i(TAG, "--------->开始解析");
        int eventType = parser.getEventType();//得到第一个事件类型
        while (eventType != XmlPullParser.END_DOCUMENT) {//如果事件类型不是文档结束的话则不断处理事件
            switch (eventType) {
                case (XmlPullParser.START_DOCUMENT)://如果是文档开始事件
                    Cog.i(TAG, "parseXml:开始解析");
                    break;
                case (XmlPullParser.START_TAG)://如果遇到标签开始
                    String tagName = parser.getName();// 获得解析器当前元素的名称
                    if ("cross-domain-policy".equals(tagName)) {
                        Cog.i(TAG, "parseConnectXml:服务器连接成功");
                        EventBus.getDefault().post(new String(Constants.CONNECT_COCO));
                    }
                    if (ROOT.equals(tagName)) {
                        String type = parser.getAttributeValue(null, TYPE);
                        switch (type) {
                            case TYPE_LOAD_USER:
                                Cog.i(TAG, "登录成功");
                                switch (parser.getAttributeValue(null, "result")) {
                                    case Constants.COCO_LICENSE_AUTHENTICATION_SUCCESS:
                                        Cog.i(TAG, "License认证成功");

                                        EventBus.getDefault().post(new String(Constants.COCO_LICENSE_AUTHENTICATION_SUCCESS));
                                        break;
                                    default:
                                        break;
                                }
                                SystemMessage systemMessage = new SystemMessage();
                                systemMessage.setId(parser.getAttributeValue(DEF, FROM));
                                systemMessage.setNick(parser.getAttributeValue(DEF, NICK));
                                systemMessage.setTime(parser.getAttributeValue(DEF, TIME));
                                systemMessage.setMsg("登录成功了哦");
                                if (!containsAny(parser.getAttributeValue(DEF, FROM), "watcher")) {
                                    //观摩者不需要发出提示
                                    EventBus.getDefault().post(systemMessage);
                                }
                                EventBus.getDefault().post(new String(Constants.LOGIN_COCO_SUCCESS));
                                break;

                            case TYPE_VERIFY_COCO:
                                Cog.e(TAG, "cipher验证失败");
                                EventBus.getDefault().post(new String(Constants.COCO_CIPHER_FAILED));
                                break;
                            case TYPE_LICENSE:
                                Cog.i(TAG, "License认证");
                                switch (parser.getAttributeValue(null, "result")) {
                                    case Constants.COCO_LICENSE_AUTHENTICATION_FAILED:
                                        Cog.e(TAG, "License认证失败");
                                        EventBus.getDefault().post(new String(Constants.COCO_LICENSE_AUTHENTICATION_FAILED));
                                        break;
                                    case Constants.COCO_LICENSE_SERVICE_EXPIRATION:
                                        Cog.e(TAG, "License服务到期");
                                        EventBus.getDefault().post(new String(Constants.COCO_LICENSE_SERVICE_EXPIRATION));
                                        break;
                                    case Constants.COCO_LICENSE_OF_RANGE:
                                        Cog.e(TAG, "会议界面超过点数");
                                        EventBus.getDefault().post(new String(Constants.COCO_LICENSE_OF_RANGE));
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case TYPE_KEEP_ALIVE:
                                Cog.d(" 收到PullXmlUtilskeepAlive", "keepAlive");
                                break;
                            case TYPE_LOGIN:
                                Cog.d(TAG, "上线通知");
                                SystemMessage systemMessage2 = new SystemMessage();
                                systemMessage2.setId(parser.getAttributeValue(DEF, FROM));
                                systemMessage2.setNick(parser.getAttributeValue(DEF, NICK));
                                systemMessage2.setTime(System.currentTimeMillis() + "");
                                systemMessage2.setMsg("加入视频会议");

                                if (!containsAny(parser.getAttributeValue(DEF, FROM), "watcher")) {
                                    //观摩者不需要发出提示
                                    EventBus.getDefault().post(systemMessage2);
                                }

                                String[] onLineUserId = {parser.getAttributeValue(DEF, FROM)};
                                EventBus.getDefault().post(onLineUserId);

                                //进入会议通知 api = noticeOnline {用于视频会议主讲人进入检测}
                                CoCoAction action = new CoCoAction();
                                action.setActionType(TYPE_LOGIN);
                                action.setFrom(parser.getAttributeValue(DEF, FROM));
                                action.setTo(parser.getAttributeValue(DEF, TO));
                                action.setActionResult(parser.getAttributeValue(DEF, FROM));
                                action.setByOperationObject(parser.getAttributeValue(DEF, FROM));
                                action.setNickName(parser.getAttributeValue(DEF, "send_nick"));
                                EventBus.getDefault().post(action);

                                break;

                            case TYPE_LOGIN_OUT:

                                Cog.d(TAG, "TYPE_LOGIN_OUT = " + parser.getAttributeValue(DEF, FROM));
                                LoginOut loginOut = new LoginOut();
                                loginOut.setFrom(parser.getAttributeValue(DEF, FROM));
                                loginOut.setTo(parser.getAttributeValue(DEF, TO));
                                EventBus.getDefault().post(loginOut);

                                SystemMessage systemMessage3 = new SystemMessage();
                                systemMessage3.setId(parser.getAttributeValue(DEF, FROM));
                                systemMessage3.setNick(parser.getAttributeValue(DEF, NICK));
                                systemMessage3.setTime(System.currentTimeMillis() + "");
                                systemMessage3.setMsg("已退出视频会议");

                                if (!containsAny(parser.getAttributeValue(DEF, FROM), "watcher")) {
                                    //观摩者不需要发出提示
                                    EventBus.getDefault().post(systemMessage3);
                                }
                                break;
                            case LOAD＿GROUP＿USER:
                                Cog.d(TAG, "say = " + parser.getAttributeValue(DEF, SAY));
                                String[] onLineUserId1 = null;
                                onLineUserId1 = parser.getAttributeValue(DEF, SAY).split(",");
                                if (onLineUserId1.length > 0) {
                                    EventBus.getDefault().post(onLineUserId1);
                                }
                                break;
                            case MEET:
                                if (SEND_MSG_TO_ALL.equals(parser.getAttributeValue(DEF, API))) {//群聊消息
                                    String receiveMsg = URLDecoder.decode(parser.getAttributeValue(DEF, SAY));
                                    ChatMessage chatMessage = new ChatMessage();
                                    String receiveMsg1 = replaceMsg(receiveMsg);
                                    chatMessage.setMsg(receiveMsg1);
                                    chatMessage.setFrom(parser.getAttributeValue(DEF, FROM));
                                    chatMessage.setTo(parser.getAttributeValue(DEF, TO));
                                    chatMessage.setChatType(ChatMessage.GROUP_CHAT);
                                    chatMessage.setTime(parser.getAttributeValue(DEF, TIME));
                                    EventBus.getDefault().post(chatMessage);
                                } else if (WEB_NO_SPEAKER.equals(parser.getAttributeValue(DEF, CALL))) {//开启－免打扰（禁止发言）

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_NO_SPEAKER, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (WEB_ALLOW_SPEAKER.equals(parser.getAttributeValue(DEF, CALL))) {//关闭－免打扰（允许发言）

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_ALLOW_SPEAKER, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (AGREE_SPEAKER_BACK＿ALL.equals(parser.getAttributeValue(DEF, CALL))) {//主持人设置某人发言

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), AGREE_SPEAKER_BACK＿ALL, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (WEB_CANCEL_SPEAKER_ALL.equals(parser.getAttributeValue(DEF, CALL))) {//主持人取消某人发言

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_CANCEL_SPEAKER_ALL, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (CANCEL_SPEAKER.equals(parser.getAttributeValue(DEF, CALL))) {//支持人取消某人发言

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), CANCEL_SPEAKER, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (APPLY_SPEAKER.equals(parser.getAttributeValue(DEF, CALL))) {//某人申请发言

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), APPLY_SPEAKER, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (KICK_MEET.equals(parser.getAttributeValue(DEF, CALL))) {//主持人请出人员

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), KICK_MEET, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (SWITCH_MODE.equals(parser.getAttributeValue(DEF, CALL))) {//切模式

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), SWITCH_MODE, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (REFUSE_SPEAKER_BACK.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), REFUSE_SPEAKER_BACK, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                } else if (UPLOADING_BACK.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), UPLOADING_BACK, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (CANCEL_UPLOAD_SWFBACK.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), CANCEL_UPLOAD_SWFBACK, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (COMPLATE_UPLOAD_FILE.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), COMPLATE_UPLOAD_FILE, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (RD_CALL_PLAY.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), RD_CALL_PLAY, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (RD_CALL_STOP.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), RD_CALL_STOP, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (VS_CALL_START.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), VS_CALL_START, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (VS_CALL_STOP.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), VS_CALL_STOP, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (VS_CREATE_PLAYER.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), VS_CREATE_PLAYER, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } /*else if (LOCATION_RELOAD.equals(parser.getAttributeValue(DEF, CALL))) {
                                    isUnReceive = true;
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), LOCATION_RELOAD, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (isUnReceive && COMMON_RECEIVE_PLAY.equals(parser.getAttributeValue(DEF, CALL))) {
                                    isUnReceive = false;
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), COMMON_RECEIVE_PLAY, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                    //轮训
                                } else if (WEB_COMMAND_LOOP.equals(parser.getAttributeValue(DEF, CALL))) {
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_COMMAND_LOOP, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } */else if (END_MEET.equals(parser.getAttributeValue(DEF, CALL))) {
                                    Cog.d(TAG,"结束会议１～parseXml");
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), END_MEET, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                } else if (COMMAND_DO＿WRITE.equals(parser.getAttributeValue(DEF, CALL))) {
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), COMMAND_DO＿WRITE, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));

                                }else if (WEB_ADD_DOCUMENT.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_ADD_DOCUMENT, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }else if (COMMAND_REFRESH_DOC.equals(parser.getAttributeValue(DEF, CALL))) {

                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), COMMAND_REFRESH_DOC, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }else if (WEB_COMMAND_PUBLISH.equals(parser.getAttributeValue(DEF, CALL))) {//开启轮巡
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_COMMAND_PUBLISH, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }else if (WEB_COMMAND_UN_PUBLISH.equals(parser.getAttributeValue(DEF, CALL))) {//关闭轮巡
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_COMMAND_UN_PUBLISH, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }
                                break;
                            case TEXT:
                                if (SEND_MSG_TO_ONE.equals(parser.getAttributeValue(DEF, API))) {//单聊消息
                                    String receiveMsg = URLDecoder.decode(parser.getAttributeValue(DEF, SAY));
                                    ChatMessage chatMessage = new ChatMessage();
                                    String receiveMsg1 = replaceMsg(receiveMsg);
                                    chatMessage.setMsg(receiveMsg1);
                                    chatMessage.setFrom(parser.getAttributeValue(DEF, FROM));
                                    chatMessage.setTo(parser.getAttributeValue(DEF, TO));
                                    chatMessage.setTime(parser.getAttributeValue(DEF, TIME));
                                    chatMessage.setChatType(ChatMessage.SINGLE_CHAT);
                                    EventBus.getDefault().post(chatMessage);
                                } else if (CHAT_IS_CLOSE_BACK.equals(parser.getAttributeValue(DEF, CALL))) {//设置某人禁言
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), CHAT_IS_CLOSE_BACK, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                } else if (COMMAND_WHITE_BOARD_MARK.equals(parser.getAttributeValue(DEF, CALL))) {//白板标注权限
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), COMMAND_WHITE_BOARD_MARK, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                } else if (AGREE_SPEAKER_BACK.equals(parser.getAttributeValue(DEF, CALL))) {//主持人设置某人发言
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), AGREE_SPEAKER_BACK, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                } else if (WEB_CANCEL_SPEAKER.equals(parser.getAttributeValue(DEF, CALL))) {//主持人取消某人发言
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_CANCEL_SPEAKER, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                } else if (WEB_CHAT_CONTROL.equals(parser.getAttributeValue(DEF, CALL))) {//主持人禁止聊天
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_CHAT_CONTROL, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }else if (WEB_COMMAND_PUBLISH.equals(parser.getAttributeValue(DEF, CALL))) {//开启轮巡
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_COMMAND_PUBLISH, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }else if (WEB_COMMAND_UN_PUBLISH.equals(parser.getAttributeValue(DEF, CALL))) {//关闭轮巡
                                    parsParse(URLDecoder.decode(parser.getAttributeValue(DEF, PARS), UTF), WEB_COMMAND_UN_PUBLISH, parser.getAttributeValue(DEF, FROM), parser.getAttributeValue(DEF, TO), parser.getAttributeValue(DEF, NICK));
                                }
                                break;
                            case GROUP:
                               /* switch (parser.getAttributeValue(DEF, "act")) {
                                    default:
                                        break;
                                }*/
                                break;
                            default:
                                break;
                        }
                        Cog.i(TAG, "parseXml:消息类型=" + type);
                    }
                    break;
                case (XmlPullParser.END_TAG)://如果遇到标签结束
                    break;
            }
            eventType = parser.next();//进入下一个事件处理
        }
    }

    static ByteArrayInputStream byteArrayInputStream;

    public static void close() throws IOException {
        byteArrayInputStream.close();
    }

    private static void parsParse(String pars, String tag, String from, String to, String nickName) throws JSONException {

        CoCoAction coCoAction = new CoCoAction();//CoCo消息动作
        coCoAction.setFrom(from);//谁发的这条消息
        coCoAction.setTo(to);//这条消息是发给谁的
        coCoAction.setNickName(nickName);
        JSONArray jsonArray = null;
        if (!DEF.equals(pars)) {
            jsonArray = new JSONArray(pars);
        }
        switch (tag) {
            case WEB_NO_SPEAKER://禁止发言（开启免打扰）
                coCoAction.setActionType(WEB_NO_SPEAKER);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                Cog.d(TAG, "免打扰------" + jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(from, "开启免打扰", nickName);
                break;
            case WEB_ALLOW_SPEAKER://取消－禁止发言（关闭免打扰）
                coCoAction.setActionType(WEB_ALLOW_SPEAKER);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                Cog.d(TAG, "免打扰------" + jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(from, "关闭免打扰", nickName);
                break;
            case WEB_CANCEL_SPEAKER_ALL://web-取消某个发言人(群发)[["a2effa8bc38d44dfb6f28b68d83ee55c",""]]
                coCoAction.setActionType(WEB_CANCEL_SPEAKER_ALL);
                if(jsonArray.length()>2){
                    String cancelID  = jsonArray.optString(2);
                coCoAction.setByOperationObject(cancelID);
//                coCoAction.setActionResult(ja.get(1).toString());
                }
                EventBus.getDefault().post(coCoAction);
                break;
            case WEB_CANCEL_SPEAKER://web-取消某个发言人（单发） [["a2effa8bc38d44dfb6f28b68d83ee55c",""]]
                coCoAction.setActionType(WEB_CANCEL_SPEAKER);
//                JSONArray jarray = jsonArray.optJSONArray(0);
//                coCoAction.setByOperationObject(jarray.get(0).toString());
//                coCoAction.setActionResult(jarray.get(1).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case WEB_ADD_DOCUMENT://添加文档
                coCoAction.setActionType(WEB_ADD_DOCUMENT);
                EventBus.getDefault().post(coCoAction);
                break;
            case COMMAND_REFRESH_DOC://刷新列表（删除文档后执行）
                coCoAction.setActionType(COMMAND_REFRESH_DOC);
                EventBus.getDefault().post(coCoAction);
                break;
            case AGREE_SPEAKER_BACK://主持人设置某人发言
                coCoAction.setActionType(AGREE_SPEAKER_BACK);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                coCoAction.setActionResult(jsonArray.get(1).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case AGREE_SPEAKER_BACK＿ALL://主持人同意某人的发言申请
                coCoAction.setActionType(AGREE_SPEAKER_BACK＿ALL);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case CANCEL_SPEAKER://某人取消发言
                coCoAction.setActionType(CANCEL_SPEAKER);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                coCoAction.setActionResult(jsonArray.get(1).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case APPLY_SPEAKER://某人申请发言
                coCoAction.setActionType(APPLY_SPEAKER);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(jsonArray.get(0).toString(), "申请发言", nickName);
                break;
            case KICK_MEET://被请出对象
                Log.i(TAG,"eventBus post action : " + KICK_MEET+":"+jsonArray.get(0).toString());
                coCoAction.setActionType(KICK_MEET);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(jsonArray.get(0).toString(), "被请出会议", nickName);
                break;
            case SWITCH_MODE://**setActionResult = showMode 演示模式  setActionResult = videoMode 视频模式 */
                coCoAction.setActionType(SWITCH_MODE);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case CHAT_IS_CLOSE_BACK: /**  setActionResult = [true] 设置某人禁言 * setActionResult = [false] 取消某人禁言 */
                coCoAction.setActionType(CHAT_IS_CLOSE_BACK);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                if (jsonArray.get(0).toString().equals("true")) {
                    postSystemMsg(from, "被禁言", nickName);
                } else {
                    postSystemMsg(from, "被取消禁言", nickName);
                }
                break;
            case REFUSE_SPEAKER_BACK:/**setActionResult = ["3d43ac9f912e48299d10293213a75400"] 主持人拒绝某人申请发言 */
                coCoAction.setActionType(REFUSE_SPEAKER_BACK);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(jsonArray.get(0).toString(), "被主持人拒绝发言", nickName);
                break;
            case COMPLATE_UPLOAD_FILE: /** 上传文件结束 setByOperationObject 指的是哪个用户上传结束了*/
                coCoAction.setActionType(COMPLATE_UPLOAD_FILE);
                coCoAction.setByOperationObject(from);
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(from, "完成上传文件", nickName);
                break;
            case UPLOADING_BACK: /** ["6360e53f628d4d609fa84904034e7c54","yuwen004","Lighthouse.jpg"] */
                postSystemMsg(jsonArray.get(0).toString(), "正在上传" + jsonArray.get(2).toString(), nickName);
                break;
            case CANCEL_UPLOAD_SWFBACK:
                postSystemMsg(from, "取消上传文件", nickName);
                break;
            case RD_CALL_STOP:
                Cog.d("--------------xxd---------", "结束共享桌面");
                /**
                 *结束共享桌面
                 */
                coCoAction.setActionType(RD_CALL_STOP);
                EventBus.getDefault().post(coCoAction);
                break;
            case RD_CALL_PLAY:/**             *开始共享桌面             */
                Cog.d("--------------xxd---------", "开始共享桌面");
                coCoAction.setActionType(RD_CALL_PLAY);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case VS_CREATE_PLAYER:/**开始共享视频 setActionResult = "meeting_1d26f517c14d41e78ad741bae3a0dc48_64d9ab5e2a1f439c8e0c54f57af47e9c_vs" */
                Cog.d("--------------xxd---------", "开始共享视频");
                coCoAction.setActionType(VS_CREATE_PLAYER);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case VS_CALL_STOP:/**结束共享视频*/
                Cog.d("--------------xxd---------", "结束共享视频");
                coCoAction.setActionType(VS_CALL_STOP);
                EventBus.getDefault().post(coCoAction);
                break;
            case WEB_COMMAND_LOOP://轮训命令
                Cog.d("--------------xxd---------", "轮训广播："+jsonArray.get(0).toString());
               /* boolean isLooping = jsonArray.optBoolean(0);
                if(isLooping){
                    coCoAction.setActionType(COMMON_RECEIVE_PLAY);
                    EventBus.getDefault().post(coCoAction);
                }else{
                    coCoAction.setActionType(LOCATION_RELOAD);
                    EventBus.getDefault().post(coCoAction);
                }*/
                break;
            case WEB_COMMAND_PUBLISH:/**开始轮巡*/
                coCoAction.setActionType(WEB_COMMAND_PUBLISH);
                EventBus.getDefault().post(coCoAction);
                break;
            case WEB_COMMAND_UN_PUBLISH:/**结束轮巡*/
                coCoAction.setActionType(WEB_COMMAND_UN_PUBLISH);
                EventBus.getDefault().post(coCoAction);
                break;
            case END_MEET:/**会议结束*/
                Cog.d("END_MEET", "----------------post----END_MEET");
                coCoAction.setActionType(END_MEET);
                EventBus.getDefault().post(coCoAction);
                break;
            case COMMAND_WHITE_BOARD_MARK://白板标注权限
                Cog.d("COMMAND_WHITE_BOARD_MARK", "----------------post----SET White Board Mark Permission !");
                coCoAction.setActionType(COMMAND_WHITE_BOARD_MARK);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case WEB_CHAT_CONTROL://主持人－禁止聊天（所有人不能发消息）
                coCoAction.setActionType(WEB_CHAT_CONTROL);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case COMMAND_DO＿WRITE://文档操作　翻页／更换文档／ｚｏｏｍ
                /*{
                    "from": "f57ec71723ff4206b92f5f12301ee5b3",
                    "type": "group",
                    "key": "doc_deeadace9829449eaeb68df2d1375891",
                    "to": "b4a701e09ebb4a9a9370349551123f99",
                    "p2p": "1",
                    "act": "DeleteDoc",
                    "send_nick": "yangteano",
                    "o": "wp"
                }*/
                JSONObject docJson = (JSONObject) jsonArray.get(0);
                DocControl docControl = new DocControl();
                docControl.setFrom(docJson.optString(FROM));
                docControl.setTo(docJson.optString(TO));
                docControl.setActionType(docJson.optString(ACT));
                docControl.setFrom_null(docJson.optString("from_null"));
                switch (docJson.optString(ACT)) {
                    case SHOW_DOC:
                        //act = ShowDoc
                        docControl.setCurrent(docJson.optString("current"));
                        docControl.setUrl(docJson.optString("url"));
                        docControl.setId(docJson.optString("id"));
                        try {
                            docControl.setFilename(new String(docJson.optString("filename").getBytes(), UTF));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case CHANGE_DOC:
                        //act = changeDoc
                        docControl.setCurrent(docJson.optString("current"));
                        docControl.setUrl(docJson.optString("url"));
                        docControl.setId(docJson.optString("id"));
                        docControl.setOwner(docJson.optString("owner"));
                        break;
                    case ZOOM:
                        //act = zoom
                        docControl.setOwner(docJson.optString("owner"));
                        docControl.setIndex(docJson.optString("index"));
                        break;
                    case DELETE_DOC:
                        docControl.setKey(docJson.optString("key"));
                        break;
                    case CHANGE_DOC_PAD:
                        docControl.setOwner(docJson.optString("owner"));
                        break;
                }
                EventBus.getDefault().post(docControl);
                break;
            default:
                break;
        }
    }

    /**
     * 发送系统消息
     *
     * @param id
     * @param msg
     */
    public static void postSystemMsg(String id, String msg, String nick) {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setId(id);
        systemMessage.setNick(URLDecoder.decode(nick));
        systemMessage.setTime(System.currentTimeMillis() + "");
        systemMessage.setMsg(msg);
        EventBus.getDefault().post(systemMessage);
    }

    /**
     * 替换表情字符
     *
     * @param Msg
     * @return
     */
    public static String replaceMsg(String Msg) {
        String reciveMsg = Msg;
        if (containsAny(reciveMsg, "[") && containsAny(reciveMsg, "]")) {
            if (containsAny(reciveMsg, "[大哭]")) {
                reciveMsg = reciveMsg.replaceAll("\\[大哭\\]", URLDecoder.decode("%F0%9F%98%98"));
            }
            if (containsAny(reciveMsg, "[傲慢]")) {
                reciveMsg = reciveMsg.replaceAll("\\[傲慢\\]", URLDecoder.decode("%F0%9F%98%84"));
            }
            if (containsAny(reciveMsg, "[白眼]")) {
                reciveMsg = reciveMsg.replaceAll("\\[白眼\\]", URLDecoder.decode("%F0%9F%98%83"));
            }
            if (containsAny(reciveMsg, "[便便]")) {
                reciveMsg = reciveMsg.replaceAll("\\[便便\\]", URLDecoder.decode("%F0%9F%98%80"));
            }
            if (containsAny(reciveMsg, "[擦汗]")) {
                reciveMsg = reciveMsg.replaceAll("\\[擦汗\\]", URLDecoder.decode("%E2%98%BA"));
            }
            if (containsAny(reciveMsg, "[鄙视]")) {
                reciveMsg = reciveMsg.replaceAll("\\[鄙视\\]", URLDecoder.decode("%F0%9F%98%8A"));
            }
            if (containsAny(reciveMsg, "[菜刀]")) {
                reciveMsg = reciveMsg.replaceAll("\\[菜刀\\]", URLDecoder.decode("%F0%9F%98%89"));
            }
            if (containsAny(reciveMsg, "[呲牙]")) {
                reciveMsg = reciveMsg.replaceAll("\\[呲牙\\]", URLDecoder.decode("%F0%9F%98%8D"));
            }
            if (containsAny(reciveMsg, "[得意]")) {
                reciveMsg = reciveMsg.replaceAll("\\[得意\\]", URLDecoder.decode("%F0%9F%98%9A"));
            }
            if (containsAny(reciveMsg, "[发怒]")) {
                reciveMsg = reciveMsg.replaceAll("\\[发怒\\]", URLDecoder.decode("%F0%9F%98%97"));
            }
            if (containsAny(reciveMsg, "[尴尬]")) {
                reciveMsg = reciveMsg.replaceAll("\\[尴尬\\]", URLDecoder.decode("%F0%9F%98%99"));
            }
            if (containsAny(reciveMsg, "[傲慢]")) {
                reciveMsg = reciveMsg.replaceAll("\\[傲慢\\]", URLDecoder.decode("%F0%9F%98%98"));
            }
            if (containsAny(reciveMsg, "[害羞]")) {
                reciveMsg = reciveMsg.replaceAll("\\[害羞\\]", URLDecoder.decode("%F0%9F%98%9C"));
            }
            if (containsAny(reciveMsg, "[汗]")) {
                reciveMsg = reciveMsg.replaceAll("\\[汗\\]", URLDecoder.decode("%F0%9F%98%9D"));
            }
            if (containsAny(reciveMsg, "[憨笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[憨笑\\]", URLDecoder.decode("%F0%9F%98%9B"));
            }
            if (containsAny(reciveMsg, "[花]")) {
                reciveMsg = reciveMsg.replaceAll("\\[花\\]", URLDecoder.decode("%F0%9F%98%B3"));
            }
            if (containsAny(reciveMsg, "[惊恐]")) {
                reciveMsg = reciveMsg.replaceAll("\\[惊恐\\]", URLDecoder.decode("%F0%9F%98%81"));
            }
            if (containsAny(reciveMsg, "[惊讶]")) {
                reciveMsg = reciveMsg.replaceAll("\\[惊讶\\]", URLDecoder.decode("%F0%9F%98%94"));
            }
            if (containsAny(reciveMsg, "[可爱]")) {
                reciveMsg = reciveMsg.replaceAll("\\[可爱\\]", URLDecoder.decode("%F0%9F%98%8C"));
            }
            if (containsAny(reciveMsg, "[抠鼻]")) {
                reciveMsg = reciveMsg.replaceAll("\\[抠鼻\\]", URLDecoder.decode("%F0%9F%98%92"));
            }
            if (containsAny(reciveMsg, "[耍酷]")) {
                reciveMsg = reciveMsg.replaceAll("\\[耍酷\\]", URLDecoder.decode("%F0%9F%98%9E"));
            }
            if (containsAny(reciveMsg, "[酷]")) {
                reciveMsg = reciveMsg.replaceAll("\\[酷\\]", URLDecoder.decode("%F0%9F%98%9E"));
            }
            if (containsAny(reciveMsg, "[流泪]")) {
                reciveMsg = reciveMsg.replaceAll("\\[流泪\\]", URLDecoder.decode("%F0%9F%98%A3"));
            }
            if (containsAny(reciveMsg, "[傲慢]")) {
                reciveMsg = reciveMsg.replaceAll("\\[傲慢\\]", URLDecoder.decode("%F0%9F%98%84"));
            }
            if (containsAny(reciveMsg, "[难过]")) {
                reciveMsg = reciveMsg.replaceAll("\\[难过\\]", URLDecoder.decode("%F0%9F%98%A2"));
            }
            if (containsAny(reciveMsg, "[撇嘴]")) {
                reciveMsg = reciveMsg.replaceAll("\\[撇嘴\\]", URLDecoder.decode("%F0%9F%98%82"));
            }
            if (containsAny(reciveMsg, "[敲打]")) {
                reciveMsg = reciveMsg.replaceAll("\\[敲打\\]", URLDecoder.decode("%F0%9F%98%AD"));
            }
            if (containsAny(reciveMsg, "[亲亲]")) {
                reciveMsg = reciveMsg.replaceAll("\\[亲亲\\]", URLDecoder.decode("%F0%9F%98%AA"));
            }
            if (containsAny(reciveMsg, "[色]")) {
                reciveMsg = reciveMsg.replaceAll("\\[色\\]", URLDecoder.decode("%F0%9F%98%A5"));
            }
            if (containsAny(reciveMsg, "[胜利]")) {
                reciveMsg = reciveMsg.replaceAll("\\[胜利\\]", URLDecoder.decode("%F0%9F%98%B0"));
            }
            if (containsAny(reciveMsg, "[衰]")) {
                reciveMsg = reciveMsg.replaceAll("\\[衰\\]", URLDecoder.decode("%F0%9F%98%93"));
            }
            if (containsAny(reciveMsg, "[睡]")) {
                reciveMsg = reciveMsg.replaceAll("\\[睡\\]", URLDecoder.decode("%F0%9F%98%AB"));
            }
            if (containsAny(reciveMsg, "[微笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[微笑\\]", URLDecoder.decode("%F0%9F%98%A8"));
            }
            if (containsAny(reciveMsg, "[偷笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[偷笑\\]", URLDecoder.decode("%F0%9F%98%B1"));
            }
            if (containsAny(reciveMsg, "[吐]")) {
                reciveMsg = reciveMsg.replaceAll("\\[吐\\]", URLDecoder.decode("%F0%9F%98%A0"));
            }
            if (containsAny(reciveMsg, "[委屈]")) {
                reciveMsg = reciveMsg.replaceAll("\\[委屈\\]", URLDecoder.decode("%F0%9F%98%A1"));
            }
            if (containsAny(reciveMsg, "[微笑]")) {
                reciveMsg = reciveMsg.replaceAll("\\[微笑\\]", URLDecoder.decode("%F0%9F%98%A4"));
            }
            if (containsAny(reciveMsg, "[心]")) {
                reciveMsg = reciveMsg.replaceAll("\\[心\\]", URLDecoder.decode("%F0%9F%98%96"));
            }
            if (containsAny(reciveMsg, "[心裂]")) {
                reciveMsg = reciveMsg.replaceAll("\\[心裂\\]", URLDecoder.decode("%F0%9F%98%86"));
            }
            if (containsAny(reciveMsg, "[嘘]")) {
                reciveMsg = reciveMsg.replaceAll("\\[嘘\\]", URLDecoder.decode("%F0%9F%98%8B"));
            }
            if (containsAny(reciveMsg, "[阴险]")) {
                reciveMsg = reciveMsg.replaceAll("\\[阴险\\]", URLDecoder.decode("%F0%9F%98%B7"));
            }
            if (containsAny(reciveMsg, "[疑问]")) {
                reciveMsg = reciveMsg.replaceAll("\\[疑问\\]", URLDecoder.decode("%F0%9F%98%8E"));
            }
            if (containsAny(reciveMsg, "[再见]")) {
                reciveMsg = reciveMsg.replaceAll("\\[再见\\]", URLDecoder.decode("%F0%9F%98%B4"));
            }
            if (containsAny(reciveMsg, "[炸弹]")) {
                reciveMsg = reciveMsg.replaceAll("\\[炸弹\\]", URLDecoder.decode("%F0%9F%98%B5"));
            }
            if (containsAny(reciveMsg, "[抓狂]")) {
                reciveMsg = reciveMsg.replaceAll("\\[抓狂\\]", URLDecoder.decode("%F0%9F%98%B2"));
            }
            if (containsAny(reciveMsg, "[猪头]")) {
                reciveMsg = reciveMsg.replaceAll("\\[猪头\\]", URLDecoder.decode("%F0%9F%98%9F"));
            }
            if (containsAny(reciveMsg, "[示爱]")) {
                reciveMsg = reciveMsg.replaceAll("\\[示爱\\]", URLDecoder.decode("%F0%9F%98%95"));
            }
        }
        return reciveMsg;
    }

    /**
     * 判断字符串中是否包含某个特定字符
     *
     * @param str
     * @param searchChars
     * @return
     */
    public static boolean containsAny(String str, String searchChars) {
        if (str != null && searchChars != null) {
            return str.contains(searchChars);
        }
        return false;
    }
}

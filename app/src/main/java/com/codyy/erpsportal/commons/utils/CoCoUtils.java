package com.codyy.erpsportal.commons.utils;

import android.text.TextUtils;
import android.util.Log;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.commons.models.entities.SystemMessage;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.onlinemeetings.models.entities.DocControl;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.CoCoCommand;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.MeetingCommand;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.UserListEntity;
import com.codyy.erpsportal.onlinemeetings.utils.EmojiUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.greenrobot.event.EventBus;

/**
 * Created by poe on 2017/9/18.
 * coco3.0接受消息解析类.
 */
public class CoCoUtils {
    private final static String TAG = "CoCoUtils";

    /**
     * 聊天类型
     */
    private static final String ACTION_TYPE_CHAT = "text";
    /**
     * 视频会议私有控制命令
     */
    private static final String ACTION_TYPE_CONTROL = "control";

    private final static String UTF = "UTF-8";
    private final static String ACT = "act";
    private final static String DEF = "";
    private final static String FROM = "from";
    private final static String TO = "to";

    /**
     * 解析coco3.0返回的json信息.
     *
     * @throws Exception
     */
    public static void parseJson(String jsonString) throws Exception {
        if (jsonString == null) {
            return;
        }

        JSONObject response = new JSONObject(jsonString);

        if (null != response && !TextUtils.isEmpty(response.optString("command"))) {
            String command = response.optString("command");
            JSONObject object = response.optJSONObject("body");

            Cog.i(TAG, "command:消息类型=" + command);
            switch (command) {
                case CoCoCommand.COCO_ERROR://错误处理消息
                    Cog.e(TAG, "Coco error : code : " + object.optString("code") + " message: " + object.optString("message"));
                    break;
                case CoCoCommand.TYPE_ONLINE://上线-CoCo连接成功.
                    Cog.i(TAG, "登录成功");
                    if ("C200".equals(object.optString("code"))) {
                        Cog.i(TAG, "online成功");
                        EventBus.getDefault().post(new String(Constants.LOGIN_COCO_SUCCESS));
                    }
                    break;

                case CoCoCommand.ADD_GROUP_RESPONSE://我自己加入组成功的返回.
                    EventBus.getDefault().post(new String(Constants.ADD_GROUP_SUCCESS));
                    break;
                case CoCoCommand.ACTION_GROUP_RESPONSE://加入视频会议-通知上线./退出视频会议-通知离线.
                    String actionType = object.getString("action");
                    if ("add".equals(actionType)) {//上线
                        Cog.d(TAG, "上线通知");
                        notifyOnline(object);
                    } else if ("leave".equals(actionType)) {//离线
                        Cog.d(TAG, "离线通知");
                        notifyOffline(object);
                    }
                    break;
                case CoCoCommand.ACTION_KICK_USER://用户被踢
                    Cog.d(TAG, "COCO command =>> kickUser = userid: " + object.optString("userId"));
                    break;
                case CoCoCommand.ACTION_ALL_GROUP_USER://获取所有的用户列表.
                    Cog.d(TAG, "loadGroupUsers = ");
                    UserListEntity listEntity = new Gson().fromJson(object.toString(), UserListEntity.class);
                    if (null != listEntity && listEntity.getUserList().size() > 0) {
                        String[] onLineUserId1 = new String[listEntity.getUserList().size()];
                        for (int i = 0; i < listEntity.getUserList().size(); i++) {
                            onLineUserId1[i] = listEntity.getUserList().get(i).getUserId();
                        }
                        EventBus.getDefault().post(onLineUserId1);
                    }
                    break;
                case CoCoCommand.GROUP_CHAT://群聊 or 其他组内命令
                    String type = object.optString("type");
                    //群聊的控制命令
                    String content = object.optString("content");
                    Cog.i(TAG,"groupChat cmd: ===>> "+content);
                    String from = object.optString("from");
                    //判断控制类型
                    if (ACTION_TYPE_CHAT.equals(type)) {//群聊
                        doGroupChat(from,content);
                    }else if(ACTION_TYPE_CONTROL.equals(type)){//文档操作及会议命令.
                        //群发命令"content": ["receive", "", "S", 0, "", ["turnMode", "video"]],
                        //web端设置发言人命令
                        if(content!=null && content.contains("[")){
                            String cmd = obtainCMD(content);
                            switch (cmd){
                                case MeetingCommand.INFO_AGREE_SPEAKER_BACK://设置发言人(web)

                                    break;
                                case MeetingCommand.INFO_CANCEL_SPEAKER://取消发言人(web)

                                    break;
                                case MeetingCommand.WEB_CANCEL_SPEAKER_ALL://同意发言人取消发言申请(client)

                                    break;
                                case MeetingCommand.INFO_AGREE_SPEAKER_BACK＿ALL://同意客户端的发言人申请(client)

                                    break;
                                case MeetingCommand.WEB_SWITCH_MODE://视频/文档切换
                                    //获取细分的命令tag "video"/"show"
                                    String tag = obtainCMDValue(content);

                                    CoCoAction coCoAction = new CoCoAction();//CoCo消息动作
                                    coCoAction.setFrom(from);//谁发的这条消息
//                                    coCoAction.setNickName(f);
                                    if("video".equals(tag)){
                                        coCoAction.setActionType(MeetingCommand.WEB_SWITCH_MODE);
                                        coCoAction.setActionResult("video");
                                        EventBus.getDefault().post(coCoAction);
                                    }else if("show".equals(tag)){
                                        coCoAction.setActionType(MeetingCommand.WEB_SWITCH_MODE);
                                        coCoAction.setActionResult("show");
                                        EventBus.getDefault().post(coCoAction);
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case CoCoCommand.SINGLE_CHAT://单聊 or 其他组内命令.
                    String sendUserId = object.optString("sendUserId");
                    String receivedUserId = object.optString("receivedUserId");
                    JSONObject singleJson = object.optJSONObject("message");

                    /** text|png|audio当为png和audio时对内容进行base64 code编码 **/
                    String type2 = singleJson.optString("type");
                    String content2 = singleJson.optString("content");
                    Cog.i(TAG,"singleChat cmd: ===>> "+content2);
                    if (ACTION_TYPE_CHAT.equals(type2)) {
                        doSingleChat(sendUserId, receivedUserId, content2);
                    }else if(ACTION_TYPE_CONTROL.equals(type2)) {//文档操作及会议命令.
                        if(content2!=null && content2.contains("[")) {
                            String cmd = obtainCMD(content2);

                            switch (cmd){
                                case MeetingCommand.WEB_STOP_AUDIO://参会者禁言(web)

                                    break;
                                case MeetingCommand.WEB_PUBLISH_AUDIO://取消参会者禁言(web)

                                    break;
                                case MeetingCommand.WEB_STOP_VIDEO://设置参会者禁画面(web)

                                    break;
                                case MeetingCommand.WEB_PUBLISH_VIDEO://取消参会者禁画面

                                    break;
                                case MeetingCommand.WEB_CHAT_IS_CLOSE_BACK://禁止参会者文本聊天(web)

                                    String tag = obtainCMDValueNoQuote(content2);
                                    if("true".equals(tag)){//禁止参会者文本聊天

                                    }else if("false".equals(tag)){//取消禁止参会者文本聊天

                                    }
                                    break;
//                                    case
                            }
                        }

                    }
                    break;
                case CoCoCommand.ACTION_OFFLINE://重复登录被拒下线.
                    Cog.d(TAG, "ACTION_OFFLINE 您的id已在其他地方登录!");
                    break;
                case CoCoCommand.GROUP_ACTIVE_USER_COUNT://当前在线的活动用户数.
                    Cog.d(TAG, "GROUP_ACTIVE_USER_COUNT 9.组内活动用户数!onlineUsersCount = " + object.optString("activeUserCount"));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * "content": ["receive", "", "S", 0, "", ["turnMode", "video"]],
     * @param content
     * @return "turnMode"
     * @throws IndexOutOfBoundsException
     */
    private static String  obtainCMD(String content) throws IndexOutOfBoundsException{
        int i = 0;
        int pos = 0;
        while (i <2){
            pos = content.indexOf("[");
            if(pos>0){
                i++;
            }
        }

        int firstQuote = pos+1;

        //"cmd"前一个双引号的位置.
        int nextQuote = firstQuote+1;
        while (nextQuote<(content.length()-1)){
            if(content.charAt(nextQuote) == '\"'){
                ++nextQuote;
                break;
            }
            ++nextQuote;//向下移动一位
        }

        //获取命令.
        return content.substring(firstQuote,nextQuote).replaceAll("\"","");
    }

    /**
     * "content": ["receive", "", "S", 0, "", ["turnMode", "video"]],
     * @param content
     * @return "video"
     * @throws IndexOutOfBoundsException
     */
    private static String  obtainCMDValue(String content) throws IndexOutOfBoundsException{
        int i = 0;
        int pos = 0;
        while (i <2){
            pos = content.indexOf("[");
            if(pos>0){
                i++;
            }
        }

        int firstQuote = pos+1;

        //"cmd"前一个双引号的位置.
        int nextQuote = firstQuote+1;
        while (nextQuote<(content.length()-1)){
            if(content.charAt(nextQuote) == '\"'){
                //要到后面双引号的下一个位置.
                ++nextQuote;
                break;
            }
            ++nextQuote;//向下移动一位
        }

        int tagFirstQuote = nextQuote+1;
        int tagNextQuote = tagFirstQuote+1;
        while (tagFirstQuote<content.length()){
            if(content.charAt(tagNextQuote) == '\"'){
                ++tagNextQuote;
                break;
            }
            ++tagNextQuote;
        }

        //获取命令.
        return content.substring(tagFirstQuote,tagNextQuote).replaceAll("\"","");
    }

    /**
     * "content": ["receive", "af08f407f0db4eeebb46d61af88e1f17", "S", 0, "", ["noChat", true]],
     * @param content
     * @return true
     * @throws IndexOutOfBoundsException
     */
    private static String  obtainCMDValueNoQuote(String content) throws IndexOutOfBoundsException{
        int i = 0;
        int pos = 0;
        while (i <2){
            pos = content.indexOf("[");
            if(pos>0){
                i++;
            }
        }

        int firstQuote = pos+1;

        //"cmd"前一个双引号的位置.
        int nextQuote = firstQuote+1;
        while (nextQuote<(content.length()-1)){
            if(content.charAt(nextQuote) == '\"'){
                //要到后面双引号的下一个位置.
                ++nextQuote;
                break;
            }
            ++nextQuote;//向下移动一位
        }

        int tagFirstQuote = nextQuote+1;
        int tagNextQuote = tagFirstQuote+1;
        while (tagFirstQuote<content.length()){
            if(content.charAt(tagNextQuote) == ']'){
                break;
            }
            ++tagNextQuote;
        }

        //获取命令.
        return content.substring(tagFirstQuote,tagNextQuote).replaceAll("\"","");
    }

    private static void doSingleChat(String sendUserId, String receivedUserId, String content2) {
        String receiveSingleMsg = URLDecoder.decode(content2);
        ChatMessage singleMessage = new ChatMessage();
        singleMessage.setMsg(EmojiUtils.replaceMsg(receiveSingleMsg));
        singleMessage.setFrom(sendUserId);
        singleMessage.setTo(receivedUserId);
        singleMessage.setTime(String.valueOf(System.currentTimeMillis()));
        singleMessage.setChatType(ChatMessage.SINGLE_CHAT);
        EventBus.getDefault().post(singleMessage);
    }

    /**
     * 发送群聊信息.
     * @param from
     * @param content
     */
    private static void doGroupChat(String from, String content) {
        String receiveMsg = URLDecoder.decode(content);
        ChatMessage chatMessage = new ChatMessage();
        String receiveMsg1 = EmojiUtils.replaceMsg(receiveMsg);
        chatMessage.setMsg(receiveMsg1);
        chatMessage.setFrom(from);
//        chatMessage.setTo(groupId);
        chatMessage.setChatType(ChatMessage.GROUP_CHAT);
        chatMessage.setTime(String.valueOf(System.currentTimeMillis()));
        EventBus.getDefault().post(chatMessage);
    }

    /**
     * 通知下线.
     * @param object
     */
    private static void notifyOffline(JSONObject object) {

        SystemMessage systemMessage2 = new SystemMessage();
        systemMessage2.setId(object.optString("userId"));
        systemMessage2.setTime(System.currentTimeMillis() + "");
        systemMessage2.setMsg("下线通知");
        EventBus.getDefault().post(systemMessage2);

        //进入会议通知 api = noticeOnline {用于视频会议主讲人进入检测}
        CoCoAction action = new CoCoAction();
        action.setActionType(CoCoCommand.INFO_LEAVE_MEETING);
        action.setFrom(object.optString("groupId"));
        action.setTo(object.optString(""));
        action.setActionResult(object.optString("groupId"));
        action.setByOperationObject(object.optString("groupId"));
        EventBus.getDefault().post(action);

    }

    private static void notifyOnline(JSONObject object) {
        SystemMessage systemMessage2 = new SystemMessage();
        systemMessage2.setId(object.optString("userId"));
        systemMessage2.setTime(System.currentTimeMillis() + "");
        systemMessage2.setMsg("加入视频会议");

                        /*if (!EmojiUtils.containsAny(parser.getAttributeValue(DEF, FROM), "_visitor")) {
                            //观摩者不需要发出提示
                            EventBus.getDefault().post(systemMessage2);
                        }*/

        String[] onLineUserId = {object.optString("userId")};
        EventBus.getDefault().post(onLineUserId);

        //进入会议通知 api = noticeOnline {用于视频会议主讲人进入检测}
        CoCoAction action = new CoCoAction();
        action.setActionType(CoCoCommand.ADD_GROUP);
        action.setFrom(object.optString("groupId"));
        action.setTo(object.optString(""));
        action.setActionResult(object.optString("groupId"));
        action.setByOperationObject(object.optString("groupId"));
        EventBus.getDefault().post(action);
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
            case MeetingCommand.WEB_NO_SPEAKER://禁止发言（开启免打扰）
                coCoAction.setActionType(MeetingCommand.WEB_NO_SPEAKER);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                Cog.d(TAG, "免打扰------" + jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(from, "开启免打扰", nickName);
                break;
            case MeetingCommand.WEB_ALLOW_SPEAKER://取消－禁止发言（关闭免打扰）
                coCoAction.setActionType(MeetingCommand.WEB_ALLOW_SPEAKER);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                Cog.d(TAG, "免打扰------" + jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(from, "关闭免打扰", nickName);
                break;
            case MeetingCommand.WEB_CANCEL_SPEAKER_ALL://web-取消某个发言人(群发)[["a2effa8bc38d44dfb6f28b68d83ee55c",""]]
                coCoAction.setActionType(MeetingCommand.WEB_CANCEL_SPEAKER_ALL);
                if (jsonArray.length() > 2) {
                    String cancelID = jsonArray.optString(2);
                    coCoAction.setByOperationObject(cancelID);
//                coCoAction.setActionResult(ja.get(1).toString());
                }
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_CANCEL_SPEAKER://web-取消某个发言人（单发） [["a2effa8bc38d44dfb6f28b68d83ee55c",""]]
                coCoAction.setActionType(MeetingCommand.INFO_CANCEL_SPEAKER);
//                JSONArray jarray = jsonArray.optJSONArray(0);
//                coCoAction.setByOperationObject(jarray.get(0).toString());
//                coCoAction.setActionResult(jarray.get(1).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.WEB_ADD_DOCUMENT://添加文档
                coCoAction.setActionType(MeetingCommand.WEB_ADD_DOCUMENT);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.COMMAND_REFRESH_DOC://刷新列表（删除文档后执行）
                coCoAction.setActionType(MeetingCommand.COMMAND_REFRESH_DOC);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_AGREE_SPEAKER_BACK://主持人设置某人发言
                coCoAction.setActionType(MeetingCommand.INFO_AGREE_SPEAKER_BACK);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                coCoAction.setActionResult(jsonArray.get(1).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_AGREE_SPEAKER_BACK＿ALL://主持人同意某人的发言申请
                coCoAction.setActionType(MeetingCommand.INFO_AGREE_SPEAKER_BACK＿ALL);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.CMD_CANCEL_SPEAKER://某人取消发言
                coCoAction.setActionType(MeetingCommand.CMD_CANCEL_SPEAKER);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                coCoAction.setActionResult(jsonArray.get(1).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.CMD_APPLY_SPEAKER://某人申请发言
                coCoAction.setActionType(MeetingCommand.CMD_APPLY_SPEAKER);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(jsonArray.get(0).toString(), "申请发言", nickName);
                break;
            case MeetingCommand.WEB_ACTION_KICK_OUT://被请出对象
                Log.i(TAG, "eventBus post action : " + MeetingCommand.WEB_ACTION_KICK_OUT + ":" + jsonArray.get(0).toString());
                coCoAction.setActionType(MeetingCommand.WEB_ACTION_KICK_OUT);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(jsonArray.get(0).toString(), "被请出会议", nickName);
                break;
            case MeetingCommand.WEB_SWITCH_MODE://**setActionResult = showMode 演示模式  setActionResult = videoMode 视频模式 */
                coCoAction.setActionType(MeetingCommand.WEB_SWITCH_MODE);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.WEB_CHAT_IS_CLOSE_BACK: /**  setActionResult = [true] 设置某人禁言 * setActionResult = [false] 取消某人禁言 */
                coCoAction.setActionType(MeetingCommand.WEB_CHAT_IS_CLOSE_BACK);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                if (jsonArray.get(0).toString().equals("true")) {
                    postSystemMsg(from, "被禁言", nickName);
                } else {
                    postSystemMsg(from, "被取消禁言", nickName);
                }
                break;
            case MeetingCommand.REFUSE_SPEAKER_BACK:
                /**setActionResult = ["3d43ac9f912e48299d10293213a75400"] 主持人拒绝某人申请发言 */
                coCoAction.setActionType(MeetingCommand.REFUSE_SPEAKER_BACK);
                coCoAction.setByOperationObject(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(jsonArray.get(0).toString(), "被主持人拒绝发言", nickName);
                break;
            case MeetingCommand.COMPLATE_UPLOAD_FILE: /** 上传文件结束 setByOperationObject 指的是哪个用户上传结束了*/
                coCoAction.setActionType(MeetingCommand.COMPLATE_UPLOAD_FILE);
                coCoAction.setByOperationObject(from);
                EventBus.getDefault().post(coCoAction);
                postSystemMsg(from, "完成上传文件", nickName);
                break;
            case MeetingCommand.UPLOADING_BACK: /** ["6360e53f628d4d609fa84904034e7c54","yuwen004","Lighthouse.jpg"] */
                postSystemMsg(jsonArray.get(0).toString(), "正在上传" + jsonArray.get(2).toString(), nickName);
                break;
            case MeetingCommand.CANCEL_UPLOAD_SWFBACK:
                postSystemMsg(from, "取消上传文件", nickName);
                break;
            case MeetingCommand.INFO_DESK_SHARE_CLOSE:
                Cog.d("--------------xxd---------", "结束共享桌面");
                /**
                 *结束共享桌面
                 */
                coCoAction.setActionType(MeetingCommand.INFO_DESK_SHARE_CLOSE);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_DESK_SHARE_OPEN:/**             *开始共享桌面             */
                Cog.d("--------------xxd---------", "开始共享桌面");
                coCoAction.setActionType(MeetingCommand.INFO_DESK_SHARE_OPEN);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_VIDEO_SHARE_OPEN:/**开始共享视频 setActionResult = "meeting_1d26f517c14d41e78ad741bae3a0dc48_64d9ab5e2a1f439c8e0c54f57af47e9c_vs" */
                Cog.d("--------------xxd---------", "开始共享视频");
                coCoAction.setActionType(MeetingCommand.INFO_VIDEO_SHARE_OPEN);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_VIDEO_SHARE_CLOSE:/**结束共享视频*/
                Cog.d("--------------xxd---------", "结束共享视频");
                coCoAction.setActionType(MeetingCommand.INFO_VIDEO_SHARE_CLOSE);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.WEB_COMMAND_LOOP://轮训命令
                Cog.d("--------------xxd---------", "轮训广播：" + jsonArray.get(0).toString());
               /* boolean isLooping = jsonArray.optBoolean(0);
                if(isLooping){
                    coCoAction.setActionType(COMMON_RECEIVE_PLAY);
                    EventBus.getDefault().post(coCoAction);
                }else{
                    coCoAction.setActionType(LOCATION_RELOAD);
                    EventBus.getDefault().post(coCoAction);
                }*/
                break;
            case MeetingCommand.WEB_COMMAND_PUBLISH:/**开始轮巡*/
                coCoAction.setActionType(MeetingCommand.WEB_COMMAND_PUBLISH);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.WEB_COMMAND_UN_PUBLISH:/**结束轮巡*/
                coCoAction.setActionType(MeetingCommand.WEB_COMMAND_UN_PUBLISH);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.INFO_END_MEET:/**会议结束*/
                Cog.d("INFO_END_MEET", "----------------post----INFO_END_MEET");
                coCoAction.setActionType(MeetingCommand.INFO_END_MEET);
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.WEB_WHITE_BOARD_MARK://白板标注权限
                Cog.d("WEB_WHITE_BOARD_MARK", "----------------post----SET White Board Mark Permission !");
                coCoAction.setActionType(MeetingCommand.WEB_WHITE_BOARD_MARK);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.WEB_CHAT_CONTROL://主持人－禁止聊天（所有人不能发消息）
                coCoAction.setActionType(MeetingCommand.WEB_CHAT_CONTROL);
                coCoAction.setActionResult(jsonArray.get(0).toString());
                EventBus.getDefault().post(coCoAction);
                break;
            case MeetingCommand.COMMAND_DO＿WRITE://文档操作　翻页／更换文档／ｚｏｏｍ
                JSONObject docJson = (JSONObject) jsonArray.get(0);
                DocControl docControl = new DocControl();
                docControl.setFrom(docJson.optString(FROM));
                docControl.setTo(docJson.optString(TO));
                docControl.setActionType(docJson.optString(ACT));
                docControl.setFrom_null(docJson.optString("from_null"));
                switch (docJson.optString(ACT)) {
                    case MeetingCommand.SHOW_DOC:
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
                    case MeetingCommand.CHANGE_DOC:
                        //act = changeDoc
                        docControl.setCurrent(docJson.optString("current"));
                        docControl.setUrl(docJson.optString("url"));
                        docControl.setId(docJson.optString("id"));
                        docControl.setOwner(docJson.optString("owner"));
                        break;
                    case MeetingCommand.ZOOM:
                        //act = zoom
                        docControl.setOwner(docJson.optString("owner"));
                        docControl.setIndex(docJson.optString("index"));
                        break;
                    case MeetingCommand.DELETE_DOC:
                        docControl.setKey(docJson.optString("key"));
                        break;
                    case MeetingCommand.CHANGE_DOC_PAD:
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
}

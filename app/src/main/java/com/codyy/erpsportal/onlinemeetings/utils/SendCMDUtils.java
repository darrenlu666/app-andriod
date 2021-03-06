package com.codyy.erpsportal.onlinemeetings.utils;


import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.CoCoCommand;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.MeetingCommand;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * coco3.0 .
 * Created by poe.Cai on 2017/9/19.
 */
public class SendCMDUtils {
    private static final String TAG = "SendCMDUtils";

    /**
     * 基本参数
     */
    private static StringBuilder mStringBuilder = new StringBuilder("{");

    /**
     * 通知所有人上线->addGroup.(CoCo3.0)
     *
     * @param
     * @return
     * @author poe
     */
    public static String addGroup(MeetingConfig meetingConfig) {
        return appendBaseVal(CoCoCommand.ADD_GROUP,
                "\"groupId\":\"" +meetingConfig.getGid()+"\"," +
                    "\"userId\":\"" +meetingConfig.getUserId()+ "\""
                    ).toString();
    }

    /**
     * 登录coco
     * @return
     * @author poe
     */
    public static String login(MeetingConfig meetingConfig) {
        return "{" +
                "\"command\":\"" +CoCoCommand.TYPE_ONLINE+"\"," +
                "\"body\":{" +
                "\"userToken\":\"" +meetingConfig.getCipher()+"\"," +
                "\"clientType\":\"mobile\"," +
                "\"deviceName\":\"" + DeviceUtils.getDeviceName()+"\"" +
                "}" +
                "}";
    }

    /**
     * 获取所有的用户
     * @return
     * @author poe
     */
    public static String getGroupOnlineUser(MeetingConfig meetingConfig) {
        return appendBaseVal(CoCoCommand.ACTION_ALL_GROUP_USER,
                "\"groupId\":\"" +meetingConfig.getGid()+"\"")
               .toString();
    }

    /**
     * 发送群组聊天消息
     * @return
     * @author poe
     */
    public static String sendMsg(MeetingConfig meetingConfig, String msg) {
        return appendBaseVal(CoCoCommand.GROUP_CHAT,
                constructGroupChatStr(meetingConfig.getGid(),meetingConfig.getUserId(),msg))
                .toString();
    }


    /**
     * 发送单聊天消息
     *
     * @return
     * @author poe
     */
    public static String sendSignalMsg(MeetingConfig meetingConfig, String msg, String mToChatUserId) {
        return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructSingleChatStr(meetingConfig.getUserId(),mToChatUserId,msg))
                .toString();
    }

    /**
     * 设置免打扰
     *
     * @param id 设置免打扰用户的ID
     * @param b  当b= true 免打扰,b=false 取消免打扰;
     */
    public static String setDisturb(MeetingConfig meetingConfig, String id, boolean b) {
        return "";//appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callAll' ").append("call='toggleApplyBtn' ").append(" pars='").append(replacePoint(UrlEncode("[\"" + (b == true ? "on" : "off") + "\"]"))).append("'/>").toString();
    }

    /**
     * 主持人设置某人发言
     * @param id 设置发言人的ID
     * @param b  当b= true 发言,b=false 取消发言;
     */
    public static String setSpokesman(MeetingConfig meetingConfig, String id, boolean b) {
        return appendBaseVal(CoCoCommand.GROUP_CHAT,
                constructMeetingCMDSimple(MeetingCommand.WEB_SET_SPEAKER,
                            meetingConfig.getUserId(),
                            id)).toString();
    }

    /**
     * 移动端-申请发言
     * @param id 设置申请发言人的ID
     */
    public static String setProposerSpeak(MeetingConfig meetingConfig, String id,String to) {
        return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructMeetingCMD(
                        MeetingCommand.CMD_APPLY_SPEAKER,
                        id,
                        to,
                        meetingConfig.getuName()
                )).toString();
    }

    /**
     * 某人取消发言
     *
     * @param id 设置申请发言人的ID
     */
    public static String setCancelSpeak(MeetingConfig meetingConfig, String id,String to ) {
        return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructMeetingCMD(MeetingCommand.CMD_CANCEL_SPEAKER,
                        id,
                        to,
                        meetingConfig.getuName()
                )).toString();
    }

    /**
     * 主持人请出人员
     *
     * @param id 设置请出人员ID
     */
    public static String setAssignPeopleOut(MeetingConfig meetingConfig, String id) {

        return "";
    }

    /**
     * 切演示模式
     * @deprecated 移动端无切换权限.
     */
    public static String setDemonstrationMode(MeetingConfig meetingConfig) {
        /*return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructMeetingCMD(MeetingCommand.CMD_CANCEL_SPEAKER,
                        meetingConfig.getUserId(),
                        meetingConfig.getUserId(),
                        meetingConfig.getuName()
                )).toString();*/
        return "";
    }

    /**
     * 切视频模式
     * @deprecated 移动端无切换权限.
     */
    public static String setVideoMode(MeetingConfig meetingConfig) {
        /*return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructMeetingCMD(MeetingCommand.CMD_CANCEL_SPEAKER,
                        meetingConfig.getUserId(),
                        meetingConfig.getUserId(),
                        meetingConfig.getuName()
                )).toString();*/
        return "";
    }


    /**
     * 创建table 返回tableId演示文档用.
     * @param groupId
     * @param tableName
     * @return
     */
    public static String createTableJson(String groupId, String tableName) {

        String docs = "\"data\":{" +
                            "\"tabName\":\"" +tableName+"\"," +
                            "\"groupId\":\"" +groupId+"\"" +
                            "    }," +
                        "\"type\": \"" +MeetingCommand.WHITE_PAD_ADD+"\"," +
                        "\"userType\":\"all\"," +
                        "\"clientIds\":[]," +
                        "\"groupId\":\"" +groupId+"\"";

        return appendBaseVal(CoCoCommand.CONTROL_COMMAND,docs).toString();
    }

    /**
     * 演示文档
     * @param meetingConfig
     * @param current
     * @param url
     * @param id
     * @param filename
     * @return
     */
    public static String setDemonstrationDoc(MeetingConfig meetingConfig,String url,String current,String id,String filename,String tableId ) {

        String docs = "\"data\": {" +
                "\"to\": \"" +meetingConfig.getMid()+"\"," +
                "\"tabId\": \"" +tableId+"\"," +
                "\"tabName\": \"白板\"," +
                "\"o\": \"wp\"," +
                "\"from\": \"" +meetingConfig.getFrom()+"\"," +
                "\"type\": \"group\"," +
                "\"p2p\": \"1\"," +
                "\"url\": \"" +url+"\"," +
                "\"isDynamicPPT\": \"N\"," +
                "\"id\": \"" +id+"\"," +
                "\"groupId\": \"" +meetingConfig.getGid()+"\"," +
                "\"current\": \"" +current+"\"," +
                "\"filename\": \"" +filename+"\"," +
                "\"send_nick\": \"" +meetingConfig.getuName()+"\"," +
                "\"key\": \"doc_" +id+"\"," +
                "\"act\": \"" +MeetingCommand.SHOW_DOC+"\"" +
                "}," +
                "\"groupId\": \"" +meetingConfig.getGid()+"\"," +
                "\"type\": \"" +MeetingCommand.WHITE_PAD_ADD+"\"," +
                "\"userType\": \"all\"";

        return appendBaseVal(CoCoCommand.CONTROL_COMMAND,docs).toString();
    }

    /**
     * 文档翻页
     */
    public static String setChangeDoc(MeetingConfig meetingConfig ,String tableId,String current,String resId) {

        String docs = "\"data\": [" +
                        "{" +
                        "\"content\": {" +
                        "\"to\": \"" +meetingConfig.getMid()+"\"," +
                        "\"current\": \"" +current+"\"," +
                        "\"owner\": \"doc_" +resId+"\"," +
                        "\"o\": \"wp\"," +
                        "\"send_nick\": \"" +meetingConfig.getuName()+"\"," +
                        "\"from\": \"" +meetingConfig.getFrom()+"\"," +
                        "\"act\": \"" +MeetingCommand.WHITE_CHANGE_DOC+"\"," +
                        "\"type\": \"group\"," +
                        "\"p2p\": \"1\"" +
                        "}," +
                        "\"groupId\": \"" +meetingConfig.getGid()+"\"," +
                        "\"tabId\": \"" +tableId+"\"," +
                        "\"sequence\": 1," +
                        "\"type\": \"" +MeetingCommand.WHITE_CHANGE_DOC+"\"," +
                        "\"clientId\": \"" +meetingConfig.getClientId()+"\"" +
                        "}" +
                        "]," +
                        "\"groupId\":\"" +meetingConfig.getGid()+"\"," +
                        "\"tabId\":\"" +tableId+"\"," +
                        "\"sendChannelId\": \"" +meetingConfig.getClientId()+"\"";

        return appendBaseVal(MeetingCommand.WHITE_PAD_DOC,docs).toString();
    }

    /**
     * 文档缩放.
     * @param meetingConfig
     * @param to
     * @param index
     * @param from_null
     * @param owner
     * @return
     */
    public static String setDocZoom(MeetingConfig meetingConfig ,String to,String index,String from_null,String owner){
        return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructMeetingCMD(MeetingCommand.ZOOM,
                        meetingConfig.getUserId(),
                        meetingConfig.getUserId(),
                        meetingConfig.getuName()
                )).toString();
    }

    /**
     * 关闭演示
     * @param meetingConfig
     * @param tableId 文档所属的tableId
     * @param id
     * @return
     */
    public static String setDelectDoc(MeetingConfig meetingConfig ,String tableId,String id ) {
        String docs = "\"data\": {" +
                "\"to\": \"" +meetingConfig.getMid()+"\"," +
                "\"groupId\": \"" +meetingConfig.getGid()+"\"," +
                "\"tabId\": \"" +tableId+"\"," +
                "\"tabName\": \"白板\"," +
                "\"o\": \"wp\"," +
                "\"send_nick\": \"" +meetingConfig.getuName()+"\"," +
                "\"from\": \"" +meetingConfig.getFrom()+"\"," +
                "\"act\": \"" +MeetingCommand.DELETE_DOC+"\"," +
                "\"type\": \"group\"," +
                "\"p2p\": \"1\"," +
                "\"key\": \"doc_" +id+"\"" +
                "}," +
                "\"groupId\": \"" +meetingConfig.getGid()+"\"," +
                "\"type\": \"" +MeetingCommand.WHITE_PAD_REMOVE+"\"," +
                "\"userType\": \"all\"";

        return appendBaseVal(CoCoCommand.CONTROL_COMMAND,docs).toString();
    }

    /**
     * 设置某人禁言
     *
     * @param id 设置禁言人的ID
     * @param b  当b= true 禁言,b=false 取消禁言;
     */
    public static String setForbidSpeak(MeetingConfig meetingConfig, String id, boolean b) {
        /*return appendBaseVal(CoCoCommand.SINGLE_CHAT,
                constructMeetingCMD(MeetingCommand.WEB_STOP_AUDIO,
                        meetingConfig.getUserId(),
                        meetingConfig.getUserId(),
                        meetingConfig.getuName()
                )).toString();*/
        return "";
    }

    /**
     * 退出coco
     *
     * @return
     * @author eachann
     */
    public static String loginOut(RemoteDirectorConfig config) {
        return "";//appendBaseVal(CoCoCommand.INFO_END_MEET,"").toString();
    }

    /**
     * urlencode %2C 替换成 ,
     *
     * @param val
     * @return
     * @author eachann
     */
    private static String replacePoint(String val) {
        return val.replace("%2C", ",");
    }



    /**
     * 基本参数拼接
     * @param cmd  命令command
     * @param body    body 参数体
     * @return 基本参数信息
     * @author poe
     */
    public static StringBuilder appendBaseVal(String cmd, String body) {
        return new StringBuilder(mStringBuilder)
                .append("\"command\":").append("\""+cmd+"\",")
                .append("\"body\": {").append(body)
                .append("}")
                .append("}");
    }


    /**
     * 根据hashmap的值制造消息信息
     * @param hashMap
     * @return
     */
    public static String buildMsg(HashMap<String,String> hashMap){
        if(null == hashMap || hashMap.size() == 0) return  null;
        StringBuilder jpsb = new StringBuilder();

        //root
        jpsb.append("<root ");
        // content .

        Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            //key
            jpsb.append(entry.getKey());
            jpsb.append("=");
            jpsb.append("'");
            //value
            String value = entry.getValue();
            if(entry.getKey().equals("send_nick")|| entry.getKey().equals("filename")){
                value   =   URLEncoder.encode(value);
            }
            jpsb.append(value);
            jpsb.append("'");
            jpsb.append(" ");//空格
        }

        //end
        jpsb.append(" />") ;
        Cog.d(TAG ,"send"+ jpsb.toString());

        return  jpsb.toString();
    }


    /**
     * 构建web命令1.
     * @param cmd
     * @param sendUserId
     * @param toUserId
     * @param sendUserName
     * @return
     */
    private static String constructMeetingCMD(String cmd,String sendUserId ,String toUserId,String sendUserName){
        return "\"message\": {" +
                "\"content\":" +constructMeetingCMDContent(cmd,sendUserId,toUserId)+"," +
                "\"type\": \"control\"" +
                "}," +
                "\"sendUserName\": \"" +sendUserName+"\"," +
                "\"sendUserId\": \"" +sendUserId+"\"," +
                "\"receivedUserId\": \"" +toUserId+"\"," +
                "\"sendUserType\": \"all\"";
    }

    /**
     * 构建web命令2.
     * @param cmd
     * @param sendUserId
     * @param toUserId
     * @return
     */
    private static String constructMeetingCMDSimple(String cmd,String sendUserId ,String toUserId){
        return "\"content\":[\"receive\",\"\", \"S\",0, \"\"," +
                "[\"" +cmd+"\",\""+toUserId+"\","+
                "2,[\""+sendUserId+"\","+
                "\"\"," +
                "\"" +toUserId+"\"]]]," +
                "\"from\": \"" +sendUserId+"\"," +
                "\"type\": \"control\"";
    }

    /**
     * 构建web端的content.
     * @param cmd
     * @param sendUserId
     * @param toUserId
     * @return
     */
    private static String constructMeetingCMDContent(String cmd,String sendUserId ,String toUserId){

        return "[\"receive\"," +
                "\"" +toUserId+"\"," +
                "\"S\"," +
                "0," +
                "\"\"," +
                "[\"" +cmd+"\"," +
                "\"" +sendUserId+"\", " +
                "2, " +
                "[\"" +sendUserId+"\"," +
                "\"\"," +
                "\"" +toUserId+"\"]]]";
    }

    private static String constructGroupChatStr(String groupId,String from ,String content){
        return "\"groupId\":\""+groupId+"\"," +
                "\"message\":{" +
                "\"type\":\"text\"," +
                "\"from\": \"" +from+ "\","+
                "\"content\":\"" +content+"\"" +
                "}";
    }

    private static String constructSingleChatStr(String sendUserId,String recUserId, String content){
        return "\"sendUserId\":\""+sendUserId+"\"," +
                "\"receivedUserId\": \"" +recUserId+"\"," +
                "\"message\":{" +
                "\"type\":\"text\"," +
                "\"content\":\""+content+"\"" +
                "}";
    }
}

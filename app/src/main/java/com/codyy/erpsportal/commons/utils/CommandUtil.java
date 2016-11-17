package com.codyy.erpsportal.commons.utils;


import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kmdai on 2015/6/29.
 */
public class CommandUtil {

    private static final String TAG = CommandUtil.class.getSimpleName();
    private static int mAddSelf = 0;
    /**
     * 基本参数
     */
    private static StringBuilder mStringBuilder = new StringBuilder("<root ");

    /**
     * 通知所有人上线
     *
     * @param
     * @return
     * @author eachann
     */
    public static String noticeOnLine(MeetingConfig meetingConfig) {
        return "<root" +
                " send_nick='" + URLEncoder.encode(meetingConfig.getuName()) +
                "' api='noticeOnline' " +
                "type='noticeOnline' " +
                "from='" + meetingConfig.getFrom() +
                "' time='" + System.currentTimeMillis() +
                "' gid='" + meetingConfig.getGid() +
                "' enterpriseId='" + meetingConfig.getEnterpriseId() +
                "' serverType='" + meetingConfig.getServerType() +
                "' license='" + meetingConfig.getLicense() +
                "' cipher='" + meetingConfig.getCipher() +
                "'/>";
    }

    /**
     * 登录coco
     *
     * @return
     * @author eachann
     */
    public static String login(MeetingConfig meetingConfig) {
        return "<root" +
                 " send_nick='" + URLEncoder.encode(meetingConfig.getuName()) +
                "' api='login' type='login' from='"+ meetingConfig.getFrom()
                + "' to='" + meetingConfig.getGid()
                + "' time='" + System.currentTimeMillis()
                + "' gid='" + meetingConfig.getGid()
                + "' enterpriseId='" + meetingConfig.getEnterpriseId()
                + "' serverType='" + meetingConfig.getServerType()
                + "' license='" + meetingConfig.getLicense()
                + "' cipher= '" + meetingConfig.getCipher()
                + "'/>";
    }

    public static String keepLive(MeetingConfig meetingConfig) {
        return "<root from='" + meetingConfig.getFrom() + "' to='" +  meetingConfig.getFrom() + "' time='" + System.currentTimeMillis() + "' type='keepAlive' serverType='" + meetingConfig.getServerType() + "' enterpriseId='" + meetingConfig.getEnterpriseId() + "'/>";
    }


    /**
     * getGroupOnlineUser
     *
     * @return
     * @author eachann
     */
    public static String getGroupOnlineUser(MeetingConfig meetingConfig, String extraStr) {

        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr)
                .append("from_null='") .append(meetingConfig.getGid())
                .append("' type='")
                .append("getGroupUser")
                .append("' api='getGroupOnlineUser'").append("/>").toString();

    }


    /**
     * 发送群组聊天消息
     *
     * @return
     * @author eachann
     */
    public static String sendMsg(MeetingConfig meetingConfig, String msg, String extraStr) {

        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='sendMsgToAll' ").append("say= '").append(msg).append("'/>").toString();

    }
    /**
     * 发送单聊天消息
     *
     * @return
     * @author eachann
     */
    public static String sendSignalMsg(MeetingConfig meetingConfig, String msg, String extraStr,String mToChatUserId) {
        return appendBaseVal(meetingConfig.getFrom(), mToChatUserId, meetingConfig.getGroup()).append(extraStr).append("type=").append("'text' ").append("api='sendMsgToOne' ").append("say= '").append(msg).append("'/>").toString();
    }

    /**
     * 设置免打扰
     *
     * @param id 设置免打扰用户的ID
     * @param b  当b= true 免打扰,b=false 取消免打扰;
     */
    public static String setDisturb(MeetingConfig meetingConfig, String id, boolean b, String extraStr) {
        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callAll' ").append("call='toggleApplyBtn' ").append(" pars='").append(replacePoint(UrlEncode("[\"" + (b == true ? "on" : "off") + "\"]"))).append("'/>").toString();
    }

    /**
     * 主持人设置某人发言
     *
     * @param id 设置发言人的ID
     * @param b  当b= true 发言,b=false 取消发言;
     */
    public static String setSpokesman(MeetingConfig meetingConfig, String id, boolean b, String extraStr) {
        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callAll' ").append("call='communication.toBeSpeaker'").append(" pars='").append(replacePoint(UrlEncode("[\"" +id + "\",\"" + (b == true ? "actSetSpeaker" : "flag") + "\"]"))).append("'/>").toString();
    }


    /**
     * 某人申请发言
     *
     * @param id 设置申请发言人的ID
     */
    public static String setProposerSpeak(MeetingConfig meetingConfig, String id,String to , String extraStr) {
        return appendBaseVal(meetingConfig.getFrom(), to, meetingConfig.getGroup()).append(extraStr).append("type=").append("'text' ").append("api='callOne' ").append("call='communication.applySpeak'").append(" pars='").append(replacePoint(UrlEncode("[\"" + id + "\"]"))).append("'/>").toString();
    }

    /**
     * 某人取消发言
     *
     * @param id 设置申请发言人的ID
     */
    public static String setCancelSpeak(MeetingConfig meetingConfig, String id,String to , String extraStr) {
        return appendBaseVal(meetingConfig.getFrom(), to, meetingConfig.getGroup()).append(extraStr).append("type=").append("'text' ").append("api='callOne' ").append("call='communication.applyCancelSpeaker'").append(" pars='").append(replacePoint(UrlEncode("[\"" + id + "\"]"))).append("'/>").toString();
    }

    /**
     * 主持人请出人员
     *
     * @param id 设置请出人员ID
     */
    public static String setAssignPeopleOut(MeetingConfig meetingConfig, String id, String extraStr) {

        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callAll' ").append("call='kickMeet'").append(" pars='").append(replacePoint(UrlEncode("[\"" + id + "\"]"))).append("'/>").toString();
    }

    /**
     * 切演示模式
     */
    public static String setDemonstrationMode(MeetingConfig meetingConfig, String extraStr) {

        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callAll' ").append("call='switchMode'").append(" pars='").append(replacePoint(UrlEncode("[\"showMode\"]"))).append("'/>").toString();
    }

    /**
     * 切视频模式
     */
    public static String setVideoMode(MeetingConfig meetingConfig, String extraStr) {

        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callAll' ").append("call='switchMode'").append(" pars='").append(replacePoint(UrlEncode("[\"videoMode\"]"))).append("'/>").toString();

    }


    /**
     * 演示文档
     * @param meetingConfig
     * @param extraStr
     * @param to
     * @param current
     * @param from_null
     * @param url
     * @param id
     * @param filename
     * @return
     */
    public static String setDemonstrationDoc(MeetingConfig meetingConfig, String extraStr ,String to,String current,String from_null,String url,String id,String filename ) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("send_nick",URLEncoder.encode(meetingConfig.getuName()));
        hashMap.put("time",System.currentTimeMillis()+"");
        hashMap.put("enterpriseId",meetingConfig.getEnterpriseId());
        hashMap.put("serverType",meetingConfig.getServerType());
        hashMap.put("cipher",meetingConfig.getCipher());
        hashMap.put("from",meetingConfig.getFrom());
        hashMap.put("group", meetingConfig.getGroup());
        hashMap.put("to",meetingConfig.getGroup());
        hashMap.put("api","callAll");
        hashMap.put("call",PullXmlUtils.COMMAND_DO＿WRITE);
        hashMap.put("type", "meet");
        //extra start
        // TODO: 16-8-26 ｐａｒｓ＝　｛xxx｝
        StringBuilder parsBuild = new StringBuilder();
        parsBuild.append("[{");
        parsBuild.append("\"url\":\""+url+"\",");
        parsBuild.append("\"o\":\"wp\",");
        parsBuild.append("\"send_nick\":\""+URLEncoder.encode(meetingConfig.getuName())+"\",");
        parsBuild.append("\"isDynamicPPT\":\"N\",");
        parsBuild.append("\"id\":\""+id+"\",");
        parsBuild.append("\"to\":\""+meetingConfig.getMid()+"\",");
        parsBuild.append("\"filename\":\""+URLEncoder.encode(URLEncoder.encode(filename))+"\",");
        parsBuild.append("\"act\":\"ShowDoc\",");
        parsBuild.append("\"type\":\"group\",");
        parsBuild.append("\"p2p\":\"1\",");
        parsBuild.append("\"current\":\""+current+"\",");
        parsBuild.append("\"from\":\""+meetingConfig.getFrom()+"\"");
        parsBuild.append("}]");
        hashMap.put("pars",parsBuild.toString());
        return  buildMsg(hashMap);
    }

    /**
     * 文档翻页
     */
    public static String setChangeDoc(MeetingConfig meetingConfig, String extraStr ,String to,String current,String owner) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("time",System.currentTimeMillis()+"");
        hashMap.put("from",meetingConfig.getFrom());
        hashMap.put("to",meetingConfig.getGroup());
        hashMap.put("group", meetingConfig.getGroup());
        hashMap.put("send_nick",URLEncoder.encode(meetingConfig.getuName()));
        hashMap.put("serverType",meetingConfig.getServerType());
        hashMap.put("cipher",meetingConfig.getCipher());
        hashMap.put("enterpriseId",meetingConfig.getEnterpriseId());
        hashMap.put("api","callAll");
        hashMap.put("call",PullXmlUtils.COMMAND_DO＿WRITE);
        hashMap.put("type", "meet");

        // TODO: 16-8-26 ｐａｒｓ＝　｛xxx｝
        StringBuilder parsBuild = new StringBuilder();
        parsBuild.append("[{");
        parsBuild.append("\"owner\":\""+("doc_" + owner)+"\",");
        parsBuild.append("\"act\":\"changeDoc\",");
        parsBuild.append("\"o\":\"wp\",");
        parsBuild.append("\"p2p\":\"1\",");
        parsBuild.append("\"send_nick\":\""+URLEncoder.encode(meetingConfig.getuName())+"\",");
        parsBuild.append("\"current\":\""+current+"\",");
        parsBuild.append("\"to\":\""+meetingConfig.getMid()+"\",");
        parsBuild.append("\"type\":\"group\",");
        parsBuild.append("\"from\":\""+meetingConfig.getFrom()+"\"");
        parsBuild.append("}]");
        hashMap.put("pars",parsBuild.toString());
        return buildMsg(hashMap);
    }

    public static String setDocZoom(MeetingConfig meetingConfig, String extraStr ,String to,String index,String from_null,String owner){
        return appendBaseVal(meetingConfig.getFrom(), to, meetingConfig.getGroup()).append(extraStr).append("type=").append("'group' ").append("o='wp' ").append("act='zoom'").append(" p2p='1'").append(" index=").append("\'"+index+"\' ").append("owner=").append("\'"+owner+"\' ").append("from_null=").append("\'"+from_null).append("/>").toString();
    }

    public static String setDelectDoc(MeetingConfig meetingConfig, String extraStr ,String to,String id ) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("send_nick",URLEncoder.encode(meetingConfig.getuName()));
        hashMap.put("time",System.currentTimeMillis()+"");
        hashMap.put("enterpriseId",meetingConfig.getEnterpriseId());
        hashMap.put("serverType",meetingConfig.getServerType());
        hashMap.put("cipher",meetingConfig.getCipher());
        hashMap.put("from",meetingConfig.getFrom());
        hashMap.put("to",meetingConfig.getGroup());
        hashMap.put("group", meetingConfig.getGroup());
        hashMap.put("api","callAll");
        hashMap.put("call",PullXmlUtils.COMMAND_DO＿WRITE);
        hashMap.put("type", "meet");
        // TODO: 16-8-26 ｐａｒｓ＝　｛xxx｝
        StringBuilder parsBuild = new StringBuilder();
        parsBuild.append("[{");
        parsBuild.append("\"key\":\""+("doc_" + id)+"\",");
        parsBuild.append("\"act\":\"DeleteDoc\",");
        parsBuild.append("\"o\":\"wp\",");
        parsBuild.append("\"p2p\":\"1\",");
        parsBuild.append("\"send_nick\":\""+URLEncoder.encode(meetingConfig.getuName())+"\",");
        parsBuild.append("\"to\":\""+meetingConfig.getMid()+"\",");
        parsBuild.append("\"type\":\"group\",");
        parsBuild.append("\"from\":\""+meetingConfig.getFrom()+"\"");
        parsBuild.append("}]");
        hashMap.put("pars",parsBuild.toString());
        return buildMsg(hashMap);
    }


    /**
     * 设置某人禁言
     *
     * @param id 设置禁言人的ID
     * @param b  当b= true 禁言,b=false 取消禁言;
     */
    public static String setForbidSpeak(MeetingConfig meetingConfig, String id, boolean b, String extraStr) {
        return appendBaseVal(meetingConfig.getFrom(), meetingConfig.getGid(), meetingConfig.getGroup()).append(extraStr).append("type=").append("'meet' ").append("api='callOne' ").append("call='chatiscloseBack' ").append(" pars='").append(replacePoint(UrlEncode("[\"" + (b == true ? "true" : "false") + "\"]"))).append("'/>").toString();
    }


    /**
     * 退出coco
     *
     * @return
     * @author eachann
     */
    public static String loginOut(RemoteDirectorConfig config) {
        return "<root  type='loginout' from='" + config.getUid() + "' to='" + config.getMid() + "'  enterpriseId='" + config.getEnterpriseId() + "' serverType='" + config.getServerType() + "'/>";
    }


    /**
     * 特效切换
     *
     * @param command *
     *                '淡入淡出',  "4"
     *                '左上', : "0"
     *                '左下', : "2"
     *                '右上', : "1"
     *                '右下', : "3"
     *                不启用特效：-1
     * @param seq     自增序列
     * @return 返回需要发送的消息
     * @author eachann
     */
    public static String sceneStyle(String command, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"sceneStyle\",\"?\"]]".replace("?", command)))).append("'/>").toString();
    }

    /**
     * 导播模式
     *
     * @param mode auto 自动,manual 手动
     * @param seq  自增序列
     * @return 返回需要发送的字符串
     * @author eachann
     */
    public static String directorMode(String mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"directorMode\",\"" + mode + "\"]]"))).append("'/>").toString();
    }

    /**
     * 台标
     *
     * @param mode 1:启动台标;-1 ：不启动台标
     * @param seq  自增序列
     * @return 返回需要发送的信息
     * @author eachann
     */
    public static String setLogo(String mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"logo\",\"" + mode + "\"]]"))).append("'/>").toString();
    }

    /**
     * 设置字幕
     *
     * @param mode 1:启动字幕;-1 不启动字幕
     * @param seq  自增序列
     * @return 返回信息
     * @author eachann
     */
    public static String setSubTitle(String mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"subtitle\",\"" + mode + "\"]]"))).append("'/>").toString();
    }

    /**
     * 切换主画面
     *
     * @param postion 当前主画面位置
     * @param flag    当title=='vga' true,title!='vga' false;
     * @param seq     自增序列
     * @return
     * @author eachann
     */
    public static String changeVideoMain(String postion, boolean flag, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"videoMain\",\"" + postion + "\"," + flag + "]]"))).append("'/>").toString();
    }

    /**
     * 录制控制
     *
     * @param mode 0:开始/继续录制;1:暂停录制;2:停止录制
     * @param seq  自增序列
     * @return
     * @author eachann
     */
    public static String setRecordState(int mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"recordState\"," + mode + "]]"))).append("'/>").toString();
    }

    /**
     * 预置位
     *
     * @param flight         机位号
     * @param presetPosition 预置位，参数 0 1 2 3 4 5 6 7 8
     * @param seq            自增序列
     * @return
     * @author eachann
     */
    public static String setPresetPosition(int flight, int presetPosition, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"presetPosition\"," + flight + "," + presetPosition + "]]"))).append("'/>").toString();

    }

    /**
     * 控制按钮为按下和收起两种状态, 如果由变焦+ 直接变成 变焦-,需要调用Near down ，Near up, far down
     *
     * @param flight   机位索引
     * @param function near:变焦+,far:变焦-,in:变倍+,out:变倍-,up:上,down:下,left:左,right:右
     * @param action   up or down
     * @param seq      自增序列
     * @return
     * @author eachann
     */
    public static String setVideoMove(String flight, String function, String action, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"videoMove\",\"" + flight + "\",\"" + function + "\",\"" + action + "\"]]"))).append("'/>").toString();
    }

    /**
     * 上课、下课、静音、重启
     *
     * @param mode "startClass"上课 "finishClass"下课 "silentOn"静音，app需要自行禁止播放器声音 "silentOff" 取消静音，app需要自行恢复播放器声音 restart 重启
     * @param seq  自增序列
     */
    public static String setClassAndVoice(String mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"command\",\"" + mode + "\"]]"))).append("'/>").toString();
    }

    /**
     * 选中资源录制
     * @param mode
     * @param seq
     * @param config
     * @param extraStr
     * @return
     */

    public static String setVideoRecord(String mode,int seq, RemoteDirectorConfig config, String extraStr){
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"videoRecord\",\"" + mode + "\",true]]"))).append("'/>").toString();

    }

    /**
     * 选中资源录制
     * @param mode
     * @param seq
     * @param config
     * @param extraStr
     * @return
     */

    public static String setChangeRecordMode(String mode,int seq, RemoteDirectorConfig config, String extraStr){
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"changeRecordMode\",\"" + mode + "\",false]]"))).append("'/>").toString();

    }



    /**
     * 获得添加时间的mStringBuilder
     *
     * @return
     * @autor kmdai
     */
    private static StringBuilder getTime() {
        return new StringBuilder(mStringBuilder).append("time='").append(System.currentTimeMillis()).append("'");
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
     * 返回URL编码
     *
     * @param s
     * @return
     * @author kmdai
     */
    public static String UrlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * url编码转string
     *
     * @param s
     * @return
     * @author kmdai
     */
    public static String UrlDecoder(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 基本参数拼接
     *
     * @param from  ID
     * @param to    ID
     * @param group ID
     * @return 基本参数信息
     * @author kmdai
     */
    public static StringBuilder appendBaseVal(String from, String to, String group) {
        return getTime().append(" from='").append(from).append("'")
                .append(" to='").append(to).append("'")
                .append(" group='").append(group).append("' ");
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
}

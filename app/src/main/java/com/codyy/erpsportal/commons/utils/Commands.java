package com.codyy.erpsportal.commons.utils;


import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by kmdai on 2015/6/29.
 */
public class Commands {

    private final static String TAG = "Commands";

    /**
     * 基本参数
     */
    private static StringBuilder mStringBuilder = new StringBuilder("<root  call='remoteMain.get' type='text' api='callOne' ");//.append(Constants.EXTRA_STR) 动态获取

    /**
     * 通知所有人上线
     *
     * @param remoteDirectorConfig
     * @return
     * @author eachann
     */
    public static String noticeOnLine(RemoteDirectorConfig remoteDirectorConfig) {
        return "<root" +
                " send_nick='" + URLEncoder.encode(remoteDirectorConfig.getuName()) +
                "' api='noticeOnline' type='noticeOnline' from='" + remoteDirectorConfig.getUid() + "' gid='" + remoteDirectorConfig.getMid() + "' enterpriseId='" + remoteDirectorConfig.getEnterpriseId() + "' serverType='" + remoteDirectorConfig.getServerType() + "' license='" + remoteDirectorConfig.getLicense() + "' cipher='" + remoteDirectorConfig.getCipher() + "'/>";
    }

    /**
     * 登录coco
     * @return
     * @author eachann
     */
    public static String login(RemoteDirectorConfig config) {
        return "<root" +
                " send_nick='" + URLEncoder.encode(config.getuName()) +
                "' api='login' type='login' from='" + config.getUid()
                + "' to='" + config.getMid()
                + "'  gid='" + config.getMid()
                + "' enterpriseId='"
                + config.getEnterpriseId()
                + "' serverType='"  + config.getServerType()
                + "' license='" + config.getLicense()
                + "' cipher= '" + config.getCipher()
                + "'/>";
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
     * 画面模式
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
    public static String videoStitchMode(String command, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"videoStitchMode\",\"?\"]]".replace("?", command)))).append("'/>").toString();
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
     * 设置片头
     *
     * @param mode 1:启动字幕;-1 不启动字幕
     * @param seq  自增序列
     * @return 返回信息
     * @author eachann
     */
    public static String setVideoHead(String mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"movieHead\",\"" + mode + "\"]]"))).append("'/>").toString();
    }

    /**
     * 设置片尾
     *
     * @param mode 1:启动字幕;-1 不启动字幕
     * @param seq  自增序列
     * @return 返回信息
     * @author eachann
     */
    public static String setVideoEnd(String mode, int seq, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"movieEnd\",\"" + mode + "\"]]"))).append("'/>").toString();
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
     * 选中资源录制
     * @param mode
     * @param seq
     * @param config
     * @param extraStr
     * @return
     */

    public static String setVideoRecord(String mode,String flag,int seq, RemoteDirectorConfig config, String extraStr){
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"videoRecord\",\"" + mode +  "\","+flag+"]]"))).append("'/>").toString();

    }

    /**
     * 选中资源录制
     * @param mode
     * @param seq
     * @param config
     * @param extraStr
     * @return
     */

    public static String setChangeRecordMode(String mode,String flag, String recordArr, int seq, RemoteDirectorConfig config, String extraStr){
        String params = "[\"S\"," + seq + ",0,[\"changeRecordMode\",\"" + mode + "\"," + flag + "," + recordArr + "]]";
        Cog.d(TAG, "setChangeRecordMode params=" + params);
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode(params))).append("'/>").toString();
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

    public  static String setSubViewCenter(int seq,int index ,int x,int y,int width,int hight, RemoteDirectorConfig config, String extraStr) {
        return appendBaseVal(config.getUid(), config.getMainSpeak(), config.getMid()).append(extraStr).append(" pars='").append(replacePoint(UrlEncode("[\"S\"," + seq + ",0,[\"SetSubViewCenter\",\"" + index + "\""+","+x+","+y+","+width+","+hight+"]]"))).append("'/>").toString();
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

    public static void main(String args[]) throws JSONException {

        System.out.println(setClassAndVoice("startclass", 1, null, null));
    }


}

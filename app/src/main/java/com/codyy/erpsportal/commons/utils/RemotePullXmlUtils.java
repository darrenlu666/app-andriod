package com.codyy.erpsportal.commons.utils;

import android.util.Xml;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.models.entities.ChangeMainVideo;
import com.codyy.erpsportal.commons.models.entities.InitPageAll;
import com.codyy.erpsportal.commons.models.entities.InitPageLogo;
import com.codyy.erpsportal.commons.models.entities.InitPageSubTitle;
import com.codyy.erpsportal.commons.models.entities.InitPageVideoBar;
import com.codyy.erpsportal.commons.models.entities.InitPageVideoEnd;
import com.codyy.erpsportal.commons.models.entities.InitPageVideoHead;
import com.codyy.erpsportal.commons.models.entities.LoginOut;
import com.codyy.erpsportal.commons.models.entities.MapInfo;
import com.codyy.erpsportal.commons.models.entities.PicMode;
import com.codyy.erpsportal.commons.models.entities.RecordEvent;
import com.codyy.erpsportal.commons.models.entities.SetMode;
import com.codyy.erpsportal.commons.models.entities.VideoBarMapInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by eachann on 2015/6/30.
 * pull解析xml
 */
public class RemotePullXmlUtils {

    private final static String TAG = "RemotePullXmlUtils";
    /**
     * 登录成功
     */
    private static final String TYPE_LOAD_USER = "loadUser";
    /**
     * cipher验证失败
     */
    private static final String TYPE_VERIFY_COCO = "verifyCoco";
    /**
     * 心跳包
     */
    private static final String TYPE_KEEP_ALIVE = "keepAlive";
    /**
     * 上线通知
     */
    private static final String TYPE_LOGIN = "login";
    /**
     * 下线
     */
    private static final String TYPE_LOGIN_OUT = "loginout";
    /**
     * 许可证
     */
    private static final String TYPE_LICENSE = "license";
    /**
     * 课堂结束
     */
    private static final String TYPE_CLASS_OVER = "meet";

    private static final String TYPE_MESSAGE_CONTENT = "text";

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
        parser.setInput(byteArrayInputStream, "UTF-8");//设置下输入流的编码
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
                        EventBus.getDefault().post(Constants.CONNECT_COCO);
                    }
                    if ("root".equals(tagName)) {
                        String type = parser.getAttributeValue(null, "type");
                        Cog.d(TAG, "parseXml type=", type);
                        switch (type) {
                            case TYPE_LOAD_USER:
                                Cog.i(TAG, "登录成功");
                                switch (parser.getAttributeValue(null, "result")) {
                                    case Constants.COCO_LICENSE_AUTHENTICATION_SUCCESS:
                                        Cog.i(TAG, "License认证成功");
                                        EventBus.getDefault().post(Constants.COCO_LICENSE_AUTHENTICATION_SUCCESS);
                                        break;
                                    default:
                                        break;
                                }
                                EventBus.getDefault().post(Constants.LOGIN_COCO_SUCCESS);
                                break;
                            case TYPE_VERIFY_COCO:
                                Cog.e(TAG, "cipher验证失败");
                                EventBus.getDefault().post(Constants.COCO_CIPHER_FAILED);
                                break;
                            case TYPE_LICENSE:
                                Cog.i(TAG, "License认证");
                                switch (parser.getAttributeValue(null, "result")) {
                                    case Constants.COCO_LICENSE_AUTHENTICATION_FAILED:
                                        Cog.e(TAG, "License认证失败");
                                        EventBus.getDefault().post(Constants.COCO_LICENSE_AUTHENTICATION_FAILED);
                                        break;
                                    case Constants.COCO_LICENSE_SERVICE_EXPIRATION:
                                        Cog.e(TAG, "License服务到期");
                                        EventBus.getDefault().post(Constants.COCO_LICENSE_SERVICE_EXPIRATION);
                                        break;
                                    case Constants.COCO_LICENSE_OF_RANGE:
                                        Cog.e(TAG, "会议界面超过点数");
                                        EventBus.getDefault().post(Constants.COCO_LICENSE_OF_RANGE);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case TYPE_KEEP_ALIVE:
                                Cog.i(TAG, "keepAlive");
                                break;
                            case TYPE_LOGIN:
                                Cog.i(TAG, "上线通知");
                                break;
                            case TYPE_LOGIN_OUT:
                                LoginOut loginOut = new LoginOut();
                                loginOut.setFrom(parser.getAttributeValue("", "from"));
                                loginOut.setTo(parser.getAttributeValue("", "to"));
                                EventBus.getDefault().post(loginOut);
                                break;
                            case TYPE_CLASS_OVER:
                                if ("Cody.CRHasEnd".equals(parser.getAttributeValue("", "call"))) {
                                    EventBus.getDefault().post(Constants.CLASS_OVER);
                                }
                                break;
                            case TYPE_MESSAGE_CONTENT:
                                parsParse(URLDecoder.decode(parser.getAttributeValue("", "pars"), "UTF-8"));
                                break;
                            default:
                                break;
                        }
                        Cog.i(TAG, "parseXml type=", type, ",parsing done");
                    }
                    break;
                case (XmlPullParser.END_TAG)://如果遇到标签结束
                    break;
            }
            eventType = parser.next();//进入下一个事件处理
        }
    }

    /**
     * 解包
     *
     * @deprecated 包含空格时，字符串为null
     */
    @Deprecated
    public static String replaceR(String myString) {
        String newString = null;
        Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
        Matcher m = CRLF.matcher(myString);
        if (m.find()) {
            newString = m.replaceAll("");
        }
        return newString;
    }

    private static final String SET_MODE = "setMode";
    private static final String CHANGE_MAIN_VIDEO = "changeMainVideo";
    private static final String INIT_PAGE = "initPage";
    private static final String ALL = "all";
    private static final String VIDEO_BAR = "videoBar";
    private static final String SUB_TITLE = "subtitle";
    private static final String LOGO = "logo";
    private static final String HEAD_END = "headend";
    private static final String PIC_MODE = "picMode";

    private static void parsParse(String pars) {
        Cog.d(TAG, "parsParse pars=", pars);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(pars).getJSONArray(3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (jsonArray == null) return;
            /**
             * 课堂模式
             */
            if (SET_MODE.equals(jsonArray.get(0).toString())) {
                SetMode setMode = new SetMode();
                setMode.setMode(Integer.valueOf(jsonArray.get(1).toString()));
                Cog.d(TAG, "parsParse SET_MODE:" + setMode.toString());
                EventBus.getDefault().post(setMode);
            }

            /**
             * 当前主画面，自动切换
             */
            if (CHANGE_MAIN_VIDEO.equals(jsonArray.get(0).toString())) {
                ChangeMainVideo changeMainVideo = new ChangeMainVideo();
                changeMainVideo.setPos(jsonArray.get(1).toString());
                Cog.d(TAG, "parsParse 当前主画面，自动切换:" + changeMainVideo.toString());
                EventBus.getDefault().post(changeMainVideo);
            }

            /**
             * 其他配置信息
             */
            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && ALL.equals(jsonArray.get(1).toString())) {
                InitPageAll initPageAll = new InitPageAll();
                Cog.e(TAG, jsonArray.toString());
                initPageAll.setFreeRecordTime(jsonArray.getJSONObject(2).optInt("freeRecordTime"));
                initPageAll.setLessonMode(jsonArray.getJSONObject(2).optInt("lessonMode"));
                initPageAll.setRecordTime(jsonArray.getJSONObject(2).optInt("recordTime"));
                initPageAll.setRecordMode(jsonArray.getJSONObject(2).optInt("recordMode"));
                initPageAll.setFreeDiskSpace(jsonArray.getJSONObject(2).optInt("freeDiskSpace"));
                initPageAll.setRecordState(jsonArray.getJSONObject(2).optInt("recordState"));
                /*"silent": 1，静音， 0 未静音*/
                initPageAll.setSilent(jsonArray.getJSONObject(2).optInt("silent"));
                /* "inClass": 1，上课后， 0： 未上课*/
                initPageAll.setInClass(jsonArray.getJSONObject(2).optInt("inClass"));
                JSONObject jsonObject = jsonArray.getJSONObject(2).getJSONObject("sceneData");
                initPageAll.setSceneUseIndex(jsonObject.optInt("sceneUseIndex"));
                initPageAll.setSceneSelectIndex(jsonObject.optInt("sceneSelectIndex"));
                Cog.d(TAG, "parsParse 其他配置信息:" + initPageAll.toString());
                EventBus.getDefault().post(initPageAll);
            }

            /**
             * 机位信息
             */
            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && VIDEO_BAR.equals(jsonArray.get(1).toString())) {
                JSONObject jsonObject = jsonArray.getJSONObject(2).getJSONObject("videoBarData");
                InitPageVideoBar initPageVideoBar = new InitPageVideoBar();
                List<VideoBarMapInfo> map = new ArrayList<>();
                Iterator<String> iterator = jsonObject.getJSONObject("map").keys();
                JSONObject mapInfoJsonObj = jsonObject.getJSONObject("map");
                while (iterator.hasNext()) {
                    VideoBarMapInfo info = new VideoBarMapInfo();
                    String str = iterator.next();
                    info.setIndex(str);
                    info.setTitle(mapInfoJsonObj.getJSONObject(str).optString("title"));
                    info.setPizEnable(mapInfoJsonObj.getJSONObject(str).optBoolean("pizEnable"));
                    info.setVideoRecord(mapInfoJsonObj.getJSONObject(str).optBoolean("videoRecord"));
                    info.setIsReceiver(mapInfoJsonObj.getJSONObject(str).optBoolean("isReceiver"));
                    info.setPresetNum(mapInfoJsonObj.getJSONObject(str).optInt("presetNum"));
                    info.setVideoBitRate(mapInfoJsonObj.getJSONObject(str).optInt("videoBitrate"));
                    map.add(info);
                }
                initPageVideoBar.setMapInfos(map);
                initPageVideoBar.setDirectorMode(jsonObject.optString("directorMode"));
                initPageVideoBar.setVideoMain(jsonObject.optInt("videoMain"));
                Cog.d(TAG, "parsParse 机位信息:" + initPageVideoBar.toString());
                EventBus.getDefault().post(initPageVideoBar);
            }
            /**
             * 字幕
             */
            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && SUB_TITLE.equals(jsonArray.get(1).toString())) {
                JSONObject jsonObject = jsonArray.getJSONObject(2).getJSONObject("subtitleData");
                InitPageSubTitle initPageSubTitle = new InitPageSubTitle();
                List<MapInfo> map = new ArrayList<>();
                Iterator<String> iterator = jsonObject.getJSONObject("map").keys();
                JSONObject mapInfoJsonObj = jsonObject.getJSONObject("map");
                while (iterator.hasNext()) {
                    MapInfo info = new MapInfo();
                    String str = iterator.next();
                    info.setIndex(str);
                    info.setTitle(mapInfoJsonObj.getJSONObject(str).optString("title"));
                    map.add(info);
                }
                initPageSubTitle.setMapInfos(map);
                initPageSubTitle.setSubTitleFontColor(jsonObject.optString("subtitleFontColor"));
                initPageSubTitle.setSubTitlePosY(jsonObject.optString("subtitlePosY"));
                initPageSubTitle.setSubTitleScrollTimes(jsonObject.optInt("subtitleScrollTimes"));
                initPageSubTitle.setSubTitlePosX(jsonObject.optString("subtitlePosX"));
                initPageSubTitle.setSubTitleFontSize(jsonObject.optInt("subtitleFontSize"));
                initPageSubTitle.setSubTitleScrollMode(jsonObject.optInt("subtitleFontSize"));
                initPageSubTitle.setSubTitlePos(jsonObject.optString("subtitlePos"));
                initPageSubTitle.setSubTitleFontFamily(jsonObject.optInt("subtitleFontFamily"));
                initPageSubTitle.setSubTitleFontStyle(jsonObject.optInt("subtitleFontStyle"));
                initPageSubTitle.setSubTitleBackgroundColor(jsonObject.optInt("subtitleBackgroundColor"));
                initPageSubTitle.setSubTitleUseIndex(jsonObject.optInt("subtitleUseIndex"));
                Cog.d(TAG, "parsParse 字幕" + initPageSubTitle.toString());
                EventBus.getDefault().post(initPageSubTitle);
            }
            /**
             * 台标
             */
            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && LOGO.equals(jsonArray.get(1).toString())) {
                JSONObject jsonObject = jsonArray.getJSONObject(2).getJSONObject("logoData");
                InitPageLogo initPageLogo = new InitPageLogo();
                List<MapInfo> map = new ArrayList<>();
                Iterator<String> iterator = jsonObject.getJSONObject("map").keys();
                JSONObject mapInfoJsonObj = jsonObject.getJSONObject("map");
                while (iterator.hasNext()) {
                    MapInfo info = new MapInfo();
                    String str = iterator.next();
                    info.setIndex(str);
                    info.setTitle(mapInfoJsonObj.getJSONObject(str).optString("title"));
                    map.add(info);
                }
                initPageLogo.setMapInfo(map);
                initPageLogo.setLogoUseIndex(jsonObject.optInt("logoUseIndex"));
                initPageLogo.setLogoPos(jsonObject.optString("logoPos"));
                initPageLogo.setLogoPosX(jsonObject.optString("logoPosX"));
                initPageLogo.setLogoPosY(jsonObject.optInt("logoPosY"));
                Cog.d(TAG, "parsParse 台标:" + initPageLogo.toString());
                EventBus.getDefault().post(initPageLogo);
            }

            /**
             * 片头
             */
            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && HEAD_END.equals(jsonArray.get(1).toString())) {
                JSONObject json = jsonArray.getJSONObject(2);
                if (json.has("headData")) {
                    JSONObject jsonObject = jsonArray.getJSONObject(2).getJSONObject("headData");
                    if (jsonObject != null) {
                        Cog.d(TAG, "parsParse --------------- 片头:" + jsonObject.toString());
                        InitPageVideoHead initPageVideoHead = new InitPageVideoHead();
                        List<MapInfo> map = new ArrayList<>();
                        Iterator<String> iterator = jsonObject.getJSONObject("map").keys();
                        JSONObject mapInfoJsonObj = jsonObject.getJSONObject("map");
                        while (iterator.hasNext()) {
                            MapInfo info = new MapInfo();
                            String str = iterator.next();
                            info.setIndex(str);
                            info.setTitle(mapInfoJsonObj.getJSONObject(str).optString("title"));
                            map.add(info);
                        }
                        initPageVideoHead.setMapInfo(map);
                        initPageVideoHead.setLogoUseIndex(jsonObject.optInt("headUseIndex"));
                        EventBus.getDefault().post(initPageVideoHead);
                    }
                }
            }
            /**
             * 片尾
             */
            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && HEAD_END.equals(jsonArray.get(1).toString())) {
                JSONObject json = jsonArray.getJSONObject(2);
                if (json.has("endData")) {
                    JSONObject jsonObject = jsonArray.getJSONObject(2).getJSONObject("endData");
                    if (jsonObject != null) {
                        Cog.d(TAG, "parsParse  ----------------片尾:" + jsonObject.toString());
                        InitPageVideoEnd initPageVideoEnd = new InitPageVideoEnd();
                        List<MapInfo> map = new ArrayList<>();
                        Iterator<String> iterator = jsonObject.getJSONObject("map").keys();
                        JSONObject mapInfoJsonObj = jsonObject.getJSONObject("map");
                        while (iterator.hasNext()) {
                            MapInfo info = new MapInfo();
                            String str = iterator.next();
                            info.setIndex(str);
                            info.setTitle(mapInfoJsonObj.getJSONObject(str).optString("title"));
                            map.add(info);
                        }
                        initPageVideoEnd.setMapInfo(map);
                        initPageVideoEnd.setLogoUseIndex(jsonObject.optInt("endUseIndex"));
                        EventBus.getDefault().post(initPageVideoEnd);
                    }
                }
            }

            if (INIT_PAGE.equals(jsonArray.get(0).toString()) && PIC_MODE.equals(jsonArray.get(1).toString())) {
                JSONObject json = jsonArray.getJSONObject(2);
                PicMode picMode = new PicMode();
                picMode.setIndex(json.getJSONObject("picModeData").optInt("picModeUseIndex"));
                EventBus.getDefault().post(picMode);
            }

            if ("recordStateChange".equals(jsonArray.get(0).toString())) {
                JSONObject recordJo = jsonArray.getJSONObject(1);
                if (recordJo != null && recordJo.has("recordState")) {
                    RecordEvent recordEvent = new RecordEvent();
                    recordEvent.setState(recordJo.getInt("recordState"));
                    try {
                        recordEvent.setSecond(Integer.parseInt(recordJo.getString("recordTime")));
                    } catch (NumberFormatException e) {
                        recordEvent.setSecond(0);
                    }
                    EventBus.getDefault().post(recordEvent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

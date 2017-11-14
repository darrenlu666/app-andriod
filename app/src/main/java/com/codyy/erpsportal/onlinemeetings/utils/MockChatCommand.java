package com.codyy.erpsportal.onlinemeetings.utils;

import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.utils.CoCoUtils;

import java.util.ArrayList;

/**
 * 模拟web端发送命令.
 * Created by poe on 17-10-16.
 */

public class MockChatCommand {
    //切换到视频模式
    public static final String TURN_VIDEO ="{" +
            "  \"body\": {" +
            "    \"content\": [" +
            "      \"receive\"," +
            "      \"\"," +
            "      \"S\"," +
            "      0," +
            "      \"\"," +
            "      [" +
            "        \"turnMode\"," +
            "        \"show\"" +
            "      ]" +
            "    ]," +
            "    \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "    \"type\": \"control\"" +
            "  }," +
            "  \"command\": \"groupChat\"" +
            "}";
    //切换到文档演示
    public static final String TURN_SHOW ="{" +
            "  \"body\": {" +
            "    \"content\": [" +
            "      \"receive\"," +
            "      \"\"," +
            "      \"S\"," +
            "      0," +
            "      \"\"," +
            "      [" +
            "        \"turnMode\"," +
            "        \"video\"" +
            "      ]" +
            "    ]," +
            "    \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "    \"type\": \"control\"" +
            "  }," +
            "  \"command\": \"groupChat\"" +
            "}";
    //演示文档
    public static final String SHOW_DOC = "{" +
            "  \"body\": {" +
            "    \"data\": {" +
            "      \"to\": \"470f9709a23c4d989422d94da3576986\"," +
            "      \"tabId\": \"91c3ec3b8c7243bbbc4a02bdaef38b93\"," +
            "      \"tabName\": \"白板\"," +
            "      \"o\": \"wp\"," +
            "      \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "      \"type\": \"group\"," +
            "      \"p2p\": \"1\"," +
            "      \"url\": \"b6bb299ebc3141f4bc0a65525a0f93f2,22\"," +
            "      \"isDynamicPPT\": \"N\"," +
            "      \"id\": \"b6bb299ebc3141f4bc0a65525a0f93f2\"," +
            "      \"groupId\": \"4657047\"," +
            "      \"current\": \"0\"," +
            "      \"filename\": \"V5.3.0%E9%AA%8C%E6%94%B6-%E8%A7%86%E9%A2%91%E4%BC%9A%E8%AE%AE-Web%E7%AB%AF-52%E6%9D%A1-%E9%99%88%E6%B5%A9.docx\"," +
            "      \"send_nick\": \"teacher6\"," +
            "      \"act\": \"ShowDoc\"" +
            "    }," +
            "    \"groupId\": \"4657047\"," +
            "    \"type\": \"whitePadAddTab\"," +
            "    \"userType\": \"all\"" +
            "  }," +
            "  \"command\": \"control\"" +
            "}";
    //翻页
    public static final String CHANGE_PAGE_INDEX = "{\"body\":{\"groupId\":\"4657047\",\"tabId\":\"91c3ec3b8c7243bbbc4a02bdaef38b93\",\"data\":[{\"content\":{\"to\":\"470f9709a23c4d989422d94da3576986\",\"current\":\"2\",\"owner\":\"doc_b6bb299ebc3141f4bc0a65525a0f93f2\",\"o\":\"wp\",\"send_nick\":\"teacher6\",\"from\":\"af08f407f0db4eeebb46d61af88e1f17\",\"act\":\"changeDoc\",\"type\":\"group\",\"p2p\":\"1\"},\"sequence\":1,\"type\":\"changeDoc\",\"clientId\":\"123213213\"}],\"sendChannelId\":\"005056fffe9c2861-00003198-000007d6-712b74aef17236d3-f51295ac\"},\"command\":\"whitePad\"}";
    //关闭文档
    public static final String CLOSE_DOC = "{" +
            "  \"body\": {" +
            "    \"data\": {" +
            "      \"to\": \"470f9709a23c4d989422d94da3576986\"," +
            "      \"groupId\": \"4657047\"," +
            "      \"tabId\": \"91c3ec3b8c7243bbbc4a02bdaef38b93\"," +
            "      \"tabName\": \"白板\"," +
            "      \"o\": \"wp\"," +
            "      \"send_nick\": \"teacher6\"," +
            "      \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "      \"act\": \"DeleteDoc\"," +
            "      \"type\": \"group\"," +
            "      \"p2p\": \"1\"," +
            "      \"key\": \"doc_b6bb299ebc3141f4bc0a65525a0f93f2\"" +
            "    }," +
            "    \"groupId\": \"4657047\"," +
            "    \"type\": \"whitePadRemoveTab\"," +
            "    \"userType\": \"all\"" +
            "  }," +
            "    \"command\": \"control\"" +
            "}";
    // 刷新文档
    public static final String SHOW_DOC_LIST = "{" +
            "  \"body\": {" +
            "    \"content\": [" +
            "      \"receive\"," +
            "      \"\"," +
            "      \"S\"," +
            "      0," +
            "      \"\"," +
            "      [" +
            "        \"showDocList\"" +
            "      ]" +
            "    ]," +
            "    \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "    \"type\": \"control\"" +
            "  }," +
            "  \"command\": \"groupChat\"" +
            "}";
    // 添加文档
    public static final String ADD_DOC_ITEM = /*"{" +
            "\"body\": {" +
            "\"content\": [" +
            "      \"receive\"," +
            "      \"\"," +
            "      \"S\"," +
            "      0," +
            "      \"\"," +
            "      [" +
            "        \"addDocItem\"," +
            "        {" +
            "          \"createTime\": 1507874002423," +
            "          \"uploadUserId\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "          \"dynamicPptFlag\": \"N\"," +
            "          \"serverAddress\": \"http://10.5.52.11:8090/ResourceServer\"," +
            "          \"belongToType\": \"RELATED\"," +
            "          \"documentPath\": \"c8efe867197341aeb572ab97776a4e9e\"," +
            "          \"serverResourceId\": \"c8efe867197341aeb572ab97776a4e9e\"," +
            "          \"pageNum\": 1," +
            "          \"clsClassroomId\": \"3417d727c9df4ee6acb43e7fd5fd5204\"," +
            "          \"meetDocumentId\": \"4b5d7fdad3fb441686eea3403d15b4b8\"," +
            "          \"meetingId\": \"470f9709a23c4d989422d94da3576986\"," +
            "          \"userName\": \"teacher6\"," +
            "          \"documentName\": \"需求问题确认表.xls\"," +
            "          \"locked\": \"Y\"," +
            "          \"guestFlag\": \"N\"," +
            "          \"uploaderName\": \"null【null】\"" +
            "        }" +
            "      ]" +
            "    ]," +
            "    \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
            "    \"type\": \"control\"" +
            "  }," +
            "  \"command\": \"groupChat\"" +
            "}";;*/
                            "{" +
                                    "  \"body\": {" +
                                    "    \"content\": [" +
                                    "      \"receive\"," +
                                    "      \"\"," +
                                    "      \"S\"," +
                                    "      0," +
                                    "      \"\"," +
                                    "      [" +
                                    "        \"addDocItem\"," +
                                    "        {" +
                                    "          \"createTime\": 1507874040089," +
                                    "          \"uploadUserId\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
                                    "          \"dynamicPptFlag\": \"N\"," +
                                    "          \"serverAddress\": \"http://10.5.52.11:8090/ResourceServer\"," +
                                    "          \"belongToType\": \"RELATED\"," +
                                    "          \"documentPath\": \"57d1948d25fc482a8a57abd6aadcd454\"," +
                                    "          \"serverResourceId\": \"57d1948d25fc482a8a57abd6aadcd454\"," +
                                    "          \"pageNum\": 22," +
                                    "          \"clsClassroomId\": \"3417d727c9df4ee6acb43e7fd5fd5204\"," +
                                    "          \"meetDocumentId\": \"b6bb299ebc3141f4bc0a65525a0f93f2\"," +
                                    "          \"meetingId\": \"470f9709a23c4d989422d94da3576986\"," +
                                    "          \"userName\": \"teacher6\"," +
                                    "          \"documentName\": \"V5.3.0验收-视频会议-Web端-52条-陈浩.docx\"," +
                                    "          \"locked\": \"Y\"," +
                                    "          \"guestFlag\": \"N\"," +
                                    "          \"uploaderName\": \"null【null】\"" +
                                    "        }" +
                                    "      ]" +
                                    "    ]," +
                                    "    \"from\": \"af08f407f0db4eeebb46d61af88e1f17\"," +
                                    "    \"type\": \"control\"" +
                                    "  }," +
                                    "  \"command\": \"groupChat\"" +
                                    "}";

    public static final String SHARE_VIDEO ="{" +
            "\"body\":{" +
            "\"content\":[" +
            "\"receive\"," +
            "\"\"," +
            "S," +
            "0," +
            "\"\"," +
            "[" +
            "\"receiveShareVideo\"," +
            "\"434deef8c7a44a4687f0ee403ce15060\"" +
            "]" +
            "]," +
            "    \"from\": \"434deef8c7a44a4687f0ee403ce15060\"," +
            "    \"type\": \"control\"" +
            "  }," +
            "    \"command\" = \"groupChat\"" +
            "}";

    public static final String STOP_SHARE_VIDEO = "{" +
            "\"body\":{" +
            "\"content\":[" +
            "\"receive\"," +
            "\"\"," +
            "S," +
            "0," +
            "\"\"," +
            "[" +
            "\"stopReceiveShareVideo\"" +
            "]" +
            "]," +
            "    \"from\": \"434deef8c7a44a4687f0ee403ce15060\"," +
            "    \"type\": \"control\"" +
            "  }," +
            "    \"command\" = \"groupChat\"" +
            "}";

    public static final String CHAT_CONTROL_OPEN = "{\"body\":{\"content\":[\"receive\",\"\",\"S\",0,\"\",[\"chatControl\",true]],\"from\":\"434deef8c7a44a4687f0ee403ce15060\",\"type\":\"control\"},\"command\":\"groupChat\"}";
    public static final String CHAT_CONTROL_CLOSE = "{\"body\":{\"content\":[\"receive\",\"\",\"S\",0,\"\",[\"chatControl\",false]],\"from\":\"434deef8c7a44a4687f0ee403ce15060\",\"type\":\"control\"},\"command\":\"groupChat\"}";



    private static ArrayList<String> commands = new ArrayList<>();

    static {
        commands.clear();
        commands.add(TURN_VIDEO.replaceAll(" ",""));
        commands.add(TURN_SHOW.replaceAll(" ",""));
        commands.add(SHOW_DOC.replaceAll(" ",""));
        commands.add(CHANGE_PAGE_INDEX.replaceAll(" ",""));
        commands.add(CLOSE_DOC.replaceAll(" ",""));
        commands.add(SHOW_DOC_LIST.replaceAll(" ",""));
        commands.add(ADD_DOC_ITEM.replaceAll(" ",""));
    }


    /**
     * 测试演示模式切换
     */
    public static void testVideoShare(){
        new Thread(){
            @Override
            public void run() {
                commands.clear();
                commands.add(SHARE_VIDEO.replaceAll(" ",""));
                commands.add(STOP_SHARE_VIDEO.replaceAll(" ",""));

                for(String cmd : commands){
                    try {
                        CoCoUtils.parseJson(cmd,new MeetingConfig());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(2*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 测试文档操作.
     */
    public static void test(){
        commands.clear();
        commands.add(CHAT_CONTROL_OPEN.replaceAll(" ",""));
        commands.add(CHAT_CONTROL_CLOSE.replaceAll(" ",""));
        new Thread() {
            @Override
            public void run() {
                for(String cmd : commands){
                    try {
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        CoCoUtils.parseJson(cmd,new MeetingConfig());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 测试演示模式切换
     */
    public static void testTurnMode(){
        new Thread(){
            @Override
            public void run() {
                commands.clear();
                commands.add(TURN_VIDEO.replaceAll(" ",""));
                commands.add(TURN_SHOW.replaceAll(" ",""));

                for(String cmd : commands){
                    try {
                        CoCoUtils.parseJson(cmd,new MeetingConfig());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(2*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}

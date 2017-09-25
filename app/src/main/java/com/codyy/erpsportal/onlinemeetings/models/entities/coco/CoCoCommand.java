package com.codyy.erpsportal.onlinemeetings.models.entities.coco;

/**
 * Coco3.0 相关命令
 * Created by poe on 17-9-21.
 */

public class CoCoCommand {
    /** 上线通知 */
    public static final String TYPE_ONLINE = "online";
    /** 剔除用户组(踢出会议)**/
    public final static String KICK_GROUP = "kickUser";
    /** 加入组(加入视频会议所在的组)*/
    public final static String ADD_GROUP = "addGroup";
    /**加入组返回*/
    public final static String ADD_GROUP_RESPONSE = "addGroupResult";
    /**用户加入or退出组通知消息.*/
    public final static String ACTION_GROUP_RESPONSE = "userGroupAction";
    /**被踢出视频会议.*/
    public final static String ACTION_KICK_USER = "kickUser";//被踢消息
    /**
     * 用户重复登录被踢
     * 解释：当同时为mobile 时，重复登录会下线
     * 当一个登录是mobile 一个是client时不做限制
     */
    public final static String ACTION_OFFLINE = "offline";//被踢消息
    /**
     * 控制命令
     * 主要做一些控制操作，白板添加，白板切换，白板删除，自定义控制
     * command	String		是	消息命令
     * -body	String			消息体
     * type	String	body	是	控制命令类型   whitePadAddTab/whitePadRemoveTab/whitePadChangeTab其他自定义命令
     * userType	String	body	是	用户类型
     * clientIds	Array【String】	body	否	clientIds与groupId 有且只能填一个表示要控制哪些客户端
     * groupId	String	body	否	clientIds与groupId 有且只能填一个对组进行控制
     * groupId	String	body	否	clientIds与groupId 有且只能填一个对组进行控制
     */
    public final static String CONTROL_COMMAND = "control";
    /**群聊*/
    public final static String GROUP_CHAT = "groupChat";
    /**单聊*/
    public final static String SINGLE_CHAT = "singleChat";
    /**通知当前组内人数列表*/
    public static final String ACTION_ALL_GROUP_USER = "onlineUsers";
    /**获取用户活动数量*/
    public static final String GROUP_ACTIVE_USER_COUNT = "groupUserCount";
    /**错误类消息*/
    public static final String COCO_ERROR = "error";
    /**初始化白板.*/
    public static final String CMD_WHITE_PAD = "whitePad";
    /** 离开视频会议 **/
    public static final String INFO_LEAVE_MEETING = "leave";

    public static final String INFO_ADD_MEETING = "add";


    /**====================================上面都是coco定义的消息格式==================================*/
}

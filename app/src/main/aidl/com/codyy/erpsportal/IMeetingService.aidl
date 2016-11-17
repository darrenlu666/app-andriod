// IMeetingService.aidl
package com.codyy.erpsportal;

// Declare any non-default types here with import statements
import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
interface IMeetingService {

                    //登录coco服务器
                    void login();
                    //退出coco服务器
                    void loginOut();
                    //发送心跳
                    void keepAlive();
                    //上线消息
                    void noticeOnLine();
                    //退出系统
                    void exitSystem();
                    //连接coco
                    void connectCoCo();
                   //获取在线用户
                    void getGroupOnlineUser();
                    //发送群聊 COCO消息
                    void sendMsg(String msg);
                    //发送私聊 COCO消息
                    void sendSignalMsg(String msg, String id);
                    /**
                     *设置免打扰
                    * @param id 设置免打扰用户的ID
                    * @param b 当b= true 免打扰,b=false 取消免打扰;
                    */
                    void setDisturb(String id,boolean b);
                    /**
                     *主持人设置某人发言
                    * @param id 设置发言人的ID
                    * @param b 当b= true 发言,b=false 取消发言;
                    */
                    void setSpokesman(String id,boolean b);
                    /**
                     *某人申请发言
                    * @param id 设置申请发言人的ID
                    * @param b 当b= true 发言,b=false 取消发言;
                    */
                    void setProposerSpeak(String id,String to ,boolean b);
                    /**
                     *主持人请出人员
                    * @param id 设置请出人员ID
                    */
                    void setAssignPeopleOut(String id);
                    /**
                     *切演示模式
                    */
                    void setDemonstrationMode();
                    /**
                     *切视频模式
                    */
                    void setVideoMode();
                    /**
                     *设置某人禁言
                    * @param id 设置禁言人的ID
                    * @param b 当b= true 禁言,b=false 取消禁言;
                    */
                    void setForbidSpeak(String id,boolean b);
                    /**
                     *绑定参数并连接coco服务器
                    */
                     void bindConfig(in MeetingConfig config);
                    /**
                     *演示文档 （白板的COCO消息比较特殊）,发送文档演示前，先发一条切换演示模式的消息
                    */
                     void setDemonstrationDoc(String to,String current,String from_null,String url,String id,String fileName );
                    /**
                     *文档翻页
                    */
                     void setChangeDoc(String to,String current,String owner);
                    /**
                     *文档缩放
                    */
                     void setDocZoom(String to,String index,String from_null,String owner);
                    /** *取消发言人*/
                     void setCancelSpeak(String id);

                     void setDelectDoc(String to,String id);

}

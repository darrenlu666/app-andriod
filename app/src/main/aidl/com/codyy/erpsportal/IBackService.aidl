// IBackService.aidl
package com.codyy.erpsportal;

// Declare any non-default types here with import statements
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;

interface IBackService {
   /**
                * @author eachann
                * @param command * 特效切换
                *                '淡入淡出',  "4"
                *                '左上', : "0"
                *                '左下', : "2"
                *                '右上', : "1"
                *                '右下', : "3"
                *                不启用特效：-1
                * @param seq 自增序列
                */
               void sceneStyle(String command,int seq);
               void videoStitchMode(String command,int seq);
               /**
                * @author eachann
                * 导播模式
                * @param mode auto 自动,manual 手动
                * @param seq 自增序列
                */
               void directorMode(String mode,int seq);
               /**
                * @author eachann
                * 台标
                * @param mode 1:启动台标;-1 ：不启动台标
                * @param seq 自增序列
                */
               void setLogo(String mode,int seq);
               /**
                * @author eachann
                * 设置字幕
                * @param mode 1:启动字幕;-1 不启动字幕
                * @param seq 自增序列
                */
               void setSubTitle(String mode,int seq);
               void setVideoRecord(String mode,String flag,int seq);

               /**
                * 变换录制模式
                * @param recordArr 设置要录的画面
                */
               void setChangeRecordMode(String mode,String flag, String recordArr,int seq);
               void setVideoHead(String mode,int seq);
                void setVideoEnd(String mode,int seq);
               /**
                * @author eachann
                * 录制控制
                * @param mode 0:开始/继续录制;1:暂停录制;2:停止录制
                * @param seq 自增序列
                */
               void setRecordState( int mode,int seq);
                /**
                * @param flight         机位号
                * @param presetPosition 预置位，参数 0 1 2 3 4 5 6 7 8
                * @param seq            自增序列
                * @author eachann
                * 预置位
                */
                void setPresetPosition(int flight, int presetPosition, int seq) ;

                /**
                * @note 控制按钮为按下和收起两种状态, 如果由变焦+ 直接变成 变焦-,需要调用Near down ，Near up, far down
                * @param flight 机位索引
                * @param function near:变焦+,far:变焦-,in:变倍+,out:变倍-,up:上,down:下,left:左,right:右
                * @param action up or down
                * @param seq    自增序列
                * @author eachann
                */
                void setVideoMove(String flight, String function,String action, int seq);

                /**
                 * 切换主画面
                 * @param postion 当前主画面位置
                 * @param flag 当title=='vga' true,title!='vga' false;
                 * @param seq 自增序列
                 */
                 void changeVideoMain(String position,boolean flag,int seq) ;
                 /**
                 *登录coco服务器
                 */
                 void login();
                 /**
                 *退出coco服务器
                 */
                 void loginOut();
                 /**
                 *绑定参数并连接coco服务器
                 */
                 void bindConfig(in RemoteDirectorConfig config);
                 /**
                 *心跳包接口
                 */
                 void keepAlive();
                 /**
                 * noticeOnline
                 */
                 void noticeOnLine();
                 /**退出系统*/
                 void exitSystem();
                 /**
                 * @param mode "startClass"上课 "finishClass"下课 "silentOn"静音，app需要自行禁止播放器声音 "silentOff" 取消静音，app需要自行恢复播放器声音 restart 重启
                 * @param seq 自增序列
                 */
                 void setClassAndVoice(String mode,int seq);


            void setSubViewCenter(int seq,int index ,int x,int y,int width,int hight);



}

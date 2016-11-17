package com.codyy.erpsportal.commons.services;

import android.os.RemoteException;
import android.util.Log;

import com.codyy.erpsportal.IBackService;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;

/**
 * Created by yangxinwu on 2015/7/3.
 */
public class SendSocketCommand {


    private int mAddSelf = 0;
    private RemoteDirectorConfig mConfig;
    private IBackService mIBackService;

    public SendSocketCommand(IBackService mIBackService, RemoteDirectorConfig config) {
        this.mIBackService = mIBackService;
        this.mConfig = config;
    }

    /**
     * 发送切换模式的socket命令
     */
    public void sceneStyle(String command) {
        try {
            this.mIBackService.sceneStyle(command, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送切换画面模式的socket命令
     */
    public void videoStitchMode(String command) {
        try {
            this.mIBackService.videoStitchMode(command, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导播模式
     */
    public void directorMode(String mode) {
        try {
            this.mIBackService.directorMode(mode, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送开启或关闭台标的socket命令
     */
    public void setLogo(String command) {
        try {
            mIBackService.setLogo(command, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送开启或关闭字幕的socket命令
     */
    public void setSubTitle(String command) {
        try {
            this.mIBackService.setSubTitle(command, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送视频录制模式socket命令
     */
    public void setChangeRecordMode(String mode,String flag) {
        setChangeRecordMode( mode, flag, null);
    }

    /**
     * 发送视频录制模式socket命令
     */
    public void setChangeRecordMode(String mode,String flag, String recordArr) {
        try {
            this.mIBackService.setChangeRecordMode(mode, flag, recordArr, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送视频录制模式socket命令
     */
    public void setVideoRecord(String mode,String flag) {
        try {
            this.mIBackService.setVideoRecord(mode, flag, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送开启或关闭片头片尾的socket命令
     */
    public void setVideoHead(String command) {
        try {
            this.mIBackService.setVideoHead(command, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送开启或关闭片头片尾的socket命令
     */
    public void setVideoEnd(String command) {
        try {
            this.mIBackService.setVideoEnd(command, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送开启或关闭录制控制的socket命令
     */
    public void setRecordState(int mode) {
        try {
            this.mIBackService.setRecordState(mode, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送预置位的socket命令
     */
    public void setPresetPosition(int flight, int presetPosition) {
        try {
            this.mIBackService.setPresetPosition(flight, presetPosition, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制按钮为按下和收起两种状态, 如果由变焦+ 直接变成 变焦-,需要调用Near down ，Near up, far down
     */
    public void setVideoMove(String flight, String function, String action) {
        try {
            Log.v("flight","flight"+flight+"function"+function+"action"+action);
            this.mIBackService.setVideoMove(flight, function, action, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换主画面
     */
    public void changeVideoMain(String postion, boolean flag) {
        try {
            this.mIBackService.changeVideoMain(postion, flag, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 导播跟踪
     */
    public void  setSubViewCenter(int index ,int x,int y,int width,int hight){
        try {
            this.mIBackService.setSubViewCenter( ++mAddSelf,index,x,y,width,hight);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置上下课，重启，静音
     */
    public void setClassAndVoice(String mode) {
        try {
            this.mIBackService.setClassAndVoice(mode, ++mAddSelf);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 登录COCO
     */
    public void login() {
        try {
            this.mIBackService.login();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

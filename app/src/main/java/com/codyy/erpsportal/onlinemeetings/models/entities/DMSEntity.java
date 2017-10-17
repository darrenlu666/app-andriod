package com.codyy.erpsportal.onlinemeetings.models.entities;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * record the pp meet address info .
 * Created by poe on 15-8-14.
 */
public class DMSEntity implements Parcelable{
    private static final String TAG = DMSEntity.class.getSimpleName();
    private String dmsCode;
    private String dmsMainSpeakID;//main speaker uuid
    private String dmsAddress;//http://url.cn/dmc
    private String pmsAddress;//http://url.cn/pms
    private String dmsServerType;//0 :使用dmsAddress 1:使用pmsAddress

    private String directURL ;//直接使用的url 。

    public static List<ICallBack> mRegisters = new ArrayList<>();

    public DMSEntity() {
    }

    /**
     * 根据serverType 返回 服务器地址
     * 如果serverType ==0 需要dmc二次请求网址
     * @return
     */
    public void  getServer(Activity act , MeetingBase meetingBase,String areaId , ICallBack callBack){
        synchronized(EApplication.instance()){
            mRegisters.add(callBack);
            if(!TextUtils.isEmpty(directURL)){
                if(null != callBack && mRegisters.size()<= 1){
                    notifyDataUpdate();
                }
            }else{
                if(mRegisters.size()<= 1){//阻止多次调用此方法
                    getDirectURL(act , meetingBase , areaId);
                }
            }
        }
    }

    /**
     * 通知调用者 消息正在获取中
     * 增加同步锁，只有全部通知到了才会清楚
     */
    private  void notifyDataUpdate() {
        if(mRegisters.size()>0){
            for (ICallBack cb :mRegisters){
                if(cb != null){
                    cb.onSuccess(this.directURL);
                }
            }
        }
        mRegisters.clear();
    }

    private void getDirectURL(Activity act ,MeetingBase meetingBase ,String areaId) {
        Cog.i(TAG ,"开始获取DMS地址.....");
        if(this.dmsServerType!=null && dmsServerType.equals("0")){
            getMediaPlayAddress(act , meetingBase , areaId);
        }else{
            this.directURL = this.pmsAddress;
            notifyDataUpdate();
        }
    }

    public DMSEntity(String dmsCode, String dmsMainSpeakID, String dmsAddress) {
        this.dmsCode = dmsCode;
        this.dmsMainSpeakID = dmsMainSpeakID;
        this.dmsAddress = dmsAddress;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getDmsMainSpeakID() {
        return dmsMainSpeakID;
    }

    public void setDmsMainSpeakID(String dmsMainSpeakID) {
        this.dmsMainSpeakID = dmsMainSpeakID;
    }


    public void setDmsAddress(String dmsAddress) {
        this.dmsAddress = dmsAddress;
    }

    public void setPmsAddress(String pmsAddress) {
        this.pmsAddress = pmsAddress;
    }

    public void setDmsServerType(String dmsServerType) {
        this.dmsServerType = dmsServerType;
    }

    /**
     * 解析视频会议地址信息 .
     * @param dmsJson
     * @return
     */
    public static DMSEntity parseJson(JSONObject dmsJson) {

        if (null == dmsJson) return null;

        DMSEntity de = new DMSEntity();
        de.setDmsMainSpeakID(dmsJson.optString("main_speak"));
        de.setDmsCode(dmsJson.optString("code"));
        de.setDmsAddress(dmsJson.optString("dmc_address"));
        de.setPmsAddress(dmsJson.optString("pms_address"));
        de.setDmsServerType(dmsJson.optString("server_type"));

        return de;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dmsCode);
        dest.writeString(dmsMainSpeakID);
        dest.writeString(dmsAddress);
        dest.writeString(pmsAddress);
        dest.writeString(dmsServerType);
    }

    public static final Parcelable.Creator<DMSEntity> CREATOR = new Parcelable.Creator<DMSEntity>() {

        @Override
        public DMSEntity createFromParcel(Parcel in) {

            DMSEntity part = new DMSEntity();
            part.setDmsCode(in.readString());
            part.setDmsMainSpeakID(in.readString());
            part.setDmsAddress(in.readString());
            part.setPmsAddress(in.readString());
            part.setDmsServerType(in.readString());
            return part;
        }

        @Override
        public DMSEntity[] newArray(int size) {
            return new DMSEntity[size];
        }
    };


    /**
     * 返回GET拼接参数  ？mid=xxxx&uid=xxxx
     *
     * @return
     */
    public String getParams(Map<String, String> params) {
        if (null != params && params.size() > 0) {
            StringBuilder result = new StringBuilder("?");
            Object[] keys = params.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                if (i > 0) {
                    result.append("&");
                }
                result.append(keys[i]).append("=").append(params.get(keys[i]));
            }
            return result.toString();
        } else {
            return "";
        }
    }

    /**
     * 获取媒体服务器的实际地址 .
     */
    private void getMediaPlayAddress(final Activity act , MeetingBase meetingBase, String areaId) {

        Map<String, String> params = new HashMap<>();
        params.put("method", "play");
        params.put("protocal", "rtmp");
        params.put("group", meetingBase.getBaseMeetID());
        params.put("stream", UiOnlineMeetingUtils.getStream(meetingBase, meetingBase.getBaseDMS().getDmsMainSpeakID()));
        params.put("domain",areaId);
        //action 互动为1，观摩为2
        if(MeetingBase.BASE_MEET_ROLE_3 == meetingBase.getBaseRole()){//观摩
            params.put("action",String.valueOf(2));
        }else{//互动.
            params.put("action",String.valueOf(1));
        }

       /* RequestSender requestSender = new RequestSender(act.getApplicationContext());
        requestSender.sendGetRequest(new RequestSender.RequestData(dmsAddress, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onSuccess(response, act);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                onError(error);
            }
        }));*/
        String url = dmsAddress+ getParams(params);
        Cog.d(TAG, "sendRequest: AsyncHttpClient :" + url);
        AsyncHttpClient.getDefaultInstance().executeJSONObject(new AsyncHttpGet(url), new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception error, AsyncHttpResponse source, JSONObject result) {
                if (error != null) {
                    error.printStackTrace();
                    onError(error);
                    return;
                }
                onSuccess(result, act);
            }
        });
    }

    private void onError(Throwable error) {
        Cog.e(TAG, "onErrorResponse:" + error);
        ToastUtil.showToast("获取DMC失败!");
        mRegisters.clear();
        notifyDataUpdate();
    }

    private void onSuccess(JSONObject response, final Activity act) {
        Cog.d(TAG, "onResponse:" + response);
        //优先获取内网的dmsip,如果不存在内网ip则使用外网ip.
        JSONObject dms = response.optJSONObject("dms");
        if(dms == null) {
            ToastUtil.showToast(EApplication.instance(), "dms没有合适的服务器可用了,请稍后再试!");
            return;
        }
        final JSONObject internal = dms.optJSONArray("internal").optJSONObject(0);
        final JSONObject external = dms.optJSONArray("external").optJSONObject(0);

        //判断网络类型

        //1. 判断内网的服务器是否可用
        String socketUrl = internal.optString("socketUrl");
        Log.i("socket","test socket start !!!------------------"+socketUrl);

        AsyncHttpGet get = new AsyncHttpGet("http://"+socketUrl);
        get.setTimeout(500);

        AsyncHttpClient.getDefaultInstance().websocket(get, "http", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                Log.i("socket","test socket end !!!-----------------error: "+ex);
                if (ex != null) {
                    ex.printStackTrace();
                    directURL = "rtmp://"+external.optString("rtmpUrl")+"/dms";
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataUpdate();
                        }
                    });
                    return;
                }
                directURL = "rtmp://"+internal.optString("rtmpUrl")+"/dms";
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataUpdate();
                    }
                });

                String ping ="{method: \"ping\", data: \"1111111111111\"}";
                webSocket.send(ping);

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        System.out.println("I got a string: " + s);
                    }
                });
            }
        });
    }

    public interface ICallBack{
        /**
         * 成功获取服务器地址
         * @param serverURL
         */
        void onSuccess(String serverURL);

        /**
         * 网络请求错误
         * @param error
         */
        void onError(Throwable error);
    }
}

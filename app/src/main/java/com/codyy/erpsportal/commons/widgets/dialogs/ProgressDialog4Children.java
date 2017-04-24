package com.codyy.erpsportal.commons.widgets.dialogs;

import android.os.Bundle;
import android.text.TextUtils;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.SharedPreferenceUtil;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 获取用户的孩子信息并提供进度条.
 * Created by poe on 10/04/17.
 */

public class ProgressDialog4Children extends SimpleLoadingDialog {
    private static String TAG = "ProgressDialog4Children";
    /**
     * 获取实例 ，不需要点击
     * @return
     */
    public static ProgressDialog4Children newInstance(UserInfo userInfo, IResult iResult) {
        ProgressDialog4Children dialog = new ProgressDialog4Children();
        Bundle bd = new Bundle();
        bd.putParcelable(Constants.USER_INFO, userInfo);
        dialog.setArguments(bd);
        dialog.setResultInterface(iResult);
        return dialog;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_PARENT_CHILDREN;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
            data.put("userId",mUserInfo.getBaseUserId());
        }
        return data;
    }

    @Override
    public void onSuccess(JSONObject response) throws Exception {
        if(null == response ) return;
        this.dismissAllowingStateLoss();
        //do some thing .
        List<Student> mStudentList = Student.parseData(response);
        String studentId = "";
        if(null != mStudentList && mStudentList.size()>0){
            String currentStudentId  = EApplication.getPreferences().getString(ClassRoomContants.SHARE_PREFERENCE_STUDENT_ID,"");
            for(int i=0 ; i< mStudentList.size();i++){
                if(currentStudentId.equals(mStudentList.get(i).getStudentId())){
                    studentId = currentStudentId;
                }
            }
            //如果第一次获取孩子信息，默认选择第一个孩子
            if(TextUtils.isEmpty(studentId)){
                studentId = mStudentList.get(0).getStudentId();
                //保存到sharePreference中
                SharedPreferenceUtil.putString(ClassRoomContants.SHARE_PREFERENCE_STUDENT_ID,studentId);
            }
        }
        if(null != mResultInterface) mResultInterface.onSuccess(studentId);

    }

    @Override
    public void onFailure(Throwable error) throws Exception {
        if(null == error) return;
        this.dismissAllowingStateLoss();
        //do some thing .
        Cog.e(TAG,"request error: "+error.getMessage());
        if(null != mResultInterface) mResultInterface.onFailure(error);
    }

    @Override
    public void init() {
        requestData();
    }
}

package com.codyy.erpsportal.repairs.models.engines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.activities.AreaRepairsActivity;
import com.codyy.erpsportal.repairs.controllers.activities.SchoolRepairsActivity;

/**
 * 报修模块入口路由工具类
 * Created by gujiajia on 2017/4/10.
 */

public class RepairEntranceRoute {

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent();
        if (userInfo.isArea()) {
            intent.setClass(context, AreaRepairsActivity.class);
        } else {
            intent.setClass(context, SchoolRepairsActivity.class);
        }
        intent.putExtra(Extra.USER_INFO, userInfo);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }

}

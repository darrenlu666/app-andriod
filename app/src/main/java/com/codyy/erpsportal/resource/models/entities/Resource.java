package com.codyy.erpsportal.resource.models.entities;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.resource.controllers.activities.DocumentContentActivity;
import com.codyy.erpsportal.commons.controllers.activities.LoginActivity;
import com.codyy.erpsportal.resource.controllers.activities.ImageDetailsActivity;
import com.codyy.erpsportal.resource.controllers.activities.VideoDetailsActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.ToastUtil;

import org.json.JSONObject;

/**
 * 资源实体类
 * Created by gujiajia on 2015/4/14.
 */
public class Resource implements Parcelable {

    public final static String TYPE_DOCUMENT = "doc";

    public final static String TYPE_VIDEO = "video";

    public final static String TYPE_IMAGE = "image";

    private String id;

    private String title;

    private String iconUrl;

    private String type;

    private int viewCnt;

    public Resource() {}

    public Resource(String id, String title, String iconUrl) {
        this.id = id;
        this.title = title;
        this.iconUrl = iconUrl;
    }

    public Resource(String title, String iconUrl) {
        this.title = title;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(int viewCnt) {
        this.viewCnt = viewCnt;
    }

    /**
     * 解析器1
     */
    public static JsonParser<Resource> Parser1 = new JsonParser<Resource>() {
        @Override
        public Resource parse(JSONObject jsonObject) {
            Resource resource = new Resource();
            resource.setId(jsonObject.optString("id"));
            if (!jsonObject.isNull("thumb")) {
                resource.setIconUrl(jsonObject.optString("thumb").trim());
            }
            resource.setTitle(jsonObject.optString("resourceName"));
            resource.setType(jsonObject.optString("type"));
            return resource;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.iconUrl);
        dest.writeString(this.type);
    }

    protected Resource(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.iconUrl = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        public Resource createFromParcel(Parcel source) {
            return new Resource(source);
        }

        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

    /**
     * 添加点击后进入资源详情监听器
     * @param v 被添加监听的view
     * @param resource 资源
     */
    public static void addGotoResDetailsClickListener(View v, final Resource resource) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                if (userInfo == null) {
                    LoginActivity.startClearTask(activity);
                    return;
                }
                gotoResDetails(activity, userInfo, resource);
            }
        });
    }

    /**
     * 进入查看资源详情
     * @param activity
     * @param userInfo
     * @param resource
     */
    public static void gotoResDetails(Activity activity, UserInfo userInfo, Resource resource) {
        if (userInfo == null) {
            ToastUtil.showToast(activity, activity.getResources().getString(R.string.please_login_first));
            return;
        }
        if (Resource.TYPE_IMAGE.equals(resource.getType())) {
            ImageDetailsActivity.start(activity, userInfo, resource.getId());
        } else if (Resource.TYPE_DOCUMENT.equals(resource.getType())) {
            DocumentContentActivity.start(activity, userInfo, resource);
        } else {
            VideoDetailsActivity.start(activity, userInfo, resource.getId());
        }
    }

}

package com.codyy.tpmp.filterlibrary.entities;

import android.text.TextUtils;
import com.codyy.tpmp.filterlibrary.entities.interfaces.ParamBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据请求参数构建器.
 * Created by poe on 27/04/17.
 */

public class FilterParam implements ParamBuilder {
    /**
     * uuid
     */
    private String uuid;
    /**
     * 选中的区域id
     */
    private String areaId;
    /**
     * 选中的学段id
     */
    private String semesterId;

    /**
     * 是否需要更新
     */
    private boolean update = true;

    public FilterParam() {
    }

    public FilterParam(String uuid, String areaId, String semesterId) {
        this.uuid = uuid;
        this.areaId = areaId;
        this.semesterId = semesterId;
    }

    public FilterParam(String uuid, String areaId) {
        this.uuid = uuid;
        this.areaId = areaId;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String newId) {
        if (needUpdate(getUuid(), newId)) {
            this.uuid = newId;
            setUpdate(true);
        }
    }

    /**
     * 判断是否需要更新
     *
     * @param local  local variable
     * @param newStr new value
     * @return
     */
    private boolean needUpdate(String local, String newStr) {
        boolean result = false;
        if (TextUtils.isEmpty(newStr)) newStr = "";
        if (TextUtils.isEmpty(local)) {
            result = true;
        } else if (!local.equals(newStr)) {
            result = true;
        }
        return result;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        if (needUpdate(getAreaId(), areaId)) {
            this.areaId = areaId;
            setUpdate(true);
        }
    }

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        if (needUpdate(getSemesterId(), semesterId)) {
            this.semesterId = semesterId;
        }
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("areaId",areaId);
        params.put("uuid",uuid);
        /*Field[] fields = FilterParam.this.getClass().getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                String upName = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                Method m = this.getClass().getMethod("get" + upName);
                String value = (String) m.invoke(this); // 调用getter方法获取属性值

                if (!TextUtils.isEmpty(value)) {
                    params.put(name, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        return params;
    }
}

package com.codyy.erpsportal.repairs.utils;

import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.entities.AreaFilterItem;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.DirectSchoolsChoice;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsedListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 地区数据抓取器，用于地区筛选
 * Created by gujiajia on 2017/4/24.
 */

public class AreasFetcher {

    private final static String TAG = "AreasFetcher";

    private OnAreasFetchedListener mOnAreasFetchedListener;

    public void setOnAreasFetchedListener(OnAreasFetchedListener onAreasFetchedListener) {
        mOnAreasFetchedListener = onAreasFetchedListener;
    }

    /**
     * 请求下级地区
     * @param areaId 本级地区
     * @return 地区筛选项
     */
    public AreaFilterItem fetchAreaFilterItem(final String areaId) {
        final Map<String, String> params = new HashMap<>();
        final AreaFilterItem areaFilterItem = new AreaFilterItem();
        areaFilterItem.setUrl(URLConfig.GET_AREA);
        params.put("areaId", areaId);
        WebApi webApi = RsGenerator.create(WebApi.class);
        Cog.d(TAG, "fetchAreaFilterItem url=", URLConfig.GET_AREA, params);
        webApi.post4Json(areaFilterItem.getUrl(), params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "fetchAreaFilterItem response=", response);
                        if ("success".equals(response.optString("result"))) {
                            String levelName = response.optString("levelName");
                            areaFilterItem.setTypeName(levelName);
                            boolean hasDirect = "Y".equals(response.optString("hasDirect"));
                            JSONArray jsonArray = response.optJSONArray("areas");
                            if (jsonArray.length() == 0) {//当前是最下级区域，请求学校
                                if (mOnAreasFetchedListener != null) {
                                    mOnAreasFetchedListener.onNoAreaFetched(areaFilterItem);
                                }
                                return;
                            }
                            final List<Choice> choiceList = new ArrayList<>(1 + jsonArray.length() + (hasDirect ? 1 : 0));
                            choiceList.add(0, new Choice(Choice.ALL, "全部"));
                            AreaFilterItem.AREA_CHOICE_PARSER.parseArray(jsonArray, new OnParsedListener<Choice>() {
                                @Override
                                public void handleParsedObj(Choice obj) {
                                    choiceList.add(obj);
                                }
                            });
                            if (hasDirect) {
                                choiceList.add(new DirectSchoolsChoice(areaId, "直属校"));
                            }
                            areaFilterItem.setParamName("baseAreaId");
                            areaFilterItem.setChoices(choiceList);
                            if (mOnAreasFetchedListener != null) {
                                mOnAreasFetchedListener.onAreasFetched(areaFilterItem);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.d(TAG, "fetchAreaFilterItem error=", error);
                        error.printStackTrace();
                    }
                });
        return areaFilterItem;
    }

    /**
     * 地区获取监听器
     */
    public interface OnAreasFetchedListener {

        /**
         * 没有获取到下级地区
         * @param areaFilterItem 地区筛选项
         */
        void onNoAreaFetched(AreaFilterItem areaFilterItem);

        /**
         * 成功获取到下级地区
         * @param areaFilterItem 地区筛选项
         */
        void onAreasFetched(AreaFilterItem areaFilterItem);
    }
}

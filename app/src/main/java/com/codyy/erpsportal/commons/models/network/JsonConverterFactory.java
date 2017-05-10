package com.codyy.erpsportal.commons.models.network;

import com.codyy.erpsportal.commons.models.network.JsonResponseBodyConverters.JSONArrayResponseBodyConverter;
import com.codyy.erpsportal.commons.models.network.JsonResponseBodyConverters.JSONObjectResponseBodyConverter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * JSONObject
 * Created by gujiajia on 2016/11/10.
 */

public final class JsonConverterFactory extends Converter.Factory {

    public static JsonConverterFactory create() {
        return new JsonConverterFactory();
    }

    private JsonConverterFactory() { }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (type == JSONObject.class) {
            return JsonRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == JSONObject.class) {
            return JSONObjectResponseBodyConverter.INSTANCE;
        } else if (type == JSONArray.class) {
            return JSONArrayResponseBodyConverter.INSTANCE;
        }
        return null;
    }
}

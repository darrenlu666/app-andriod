package com.codyy.erpsportal.commons.models.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 *
 * Created by gujiajia on 2016/11/10.
 */

public class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

    static final JsonRequestBodyConverter<Object> INSTANCE = new JsonRequestBodyConverter<>();

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private JsonRequestBodyConverter() {}

    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, value.toString());
    }
}

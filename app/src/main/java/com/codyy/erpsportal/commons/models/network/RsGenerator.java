package com.codyy.erpsportal.commons.models.network;

import com.codyy.erpsportal.BuildConfig;
import com.codyy.url.URLConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit Service服务生成者
 * Created by gujiajia on 2016/11/10.
 */

public class RsGenerator {

    private static Retrofit sRetrofit;

    static {
        createNewRetrofit();
    }

    public static void createNewRetrofit() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {//debug模式下添加http请求日志
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }

        OkHttpClient okHttpClient = clientBuilder
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(1L, TimeUnit.MINUTES)
                .writeTimeout(1L, TimeUnit.MINUTES)
                .addInterceptor(new FakeInterceptor())
                .build();
        sRetrofit = new Retrofit.Builder()
                .baseUrl(URLConfig.BASE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static Retrofit getInstance() {
        return sRetrofit;
    }

    public static <S> S create(Class<S> serviceClass) {
        return sRetrofit.create(serviceClass);
    }
}

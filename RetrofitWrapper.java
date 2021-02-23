package com.sunmi.template.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunmi.template.http.api.ApiConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 网络接口服务包装类
 *
 * @author bps
 */
public class RetrofitWrapper {
    private static RetrofitWrapper instance;
    private Context mContext;


    private RetrofitWrapper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static RetrofitWrapper getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitWrapper(context);
        }
        return instance;
    }

    <T> T getNetService(Class<T> clazz) {
        String endpoint = ApiConstants.SERVER_ADDRESS;
        return getNetService(clazz, endpoint);
    }

    private <T> T getNetService(Class<T> clazz, String endPoint) {
        int cacheSize = 50 * 1024 * 1024;
        Cache cache = new Cache(mContext.getApplicationContext().getCacheDir(), cacheSize);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .serializeNulls()
                .setLenient()
                .create();
        Interceptor interceptor = chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("userAgent", "sunmi.com ")
                    .build();
            return chain.proceed(request);
        };


        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.i("retrofit", "log: " + message));

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .cache(cache)
                .build();
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(endPoint)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(clazz);
    }

}

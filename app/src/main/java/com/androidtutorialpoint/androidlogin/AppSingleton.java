package com.androidtutorialpoint.androidlogin;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class AppSingleton {
    private static AppSingleton mAppSingletonInstance;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient httpClient;
    private static Context mContext;
    private String token;

    private AppSingleton(Context context) {
        mContext = context;
        httpClient = getHttpClient();
    }

    public static synchronized AppSingleton getInstance(Context context) {
        if (mAppSingletonInstance == null) {
            mAppSingletonInstance = new AppSingleton(context);
        }
        return mAppSingletonInstance;
    }

    public OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.HOURS)
                    .writeTimeout(10, TimeUnit.HOURS)
                    .readTimeout(30, TimeUnit.HOURS)
                    .build();
        }
        return httpClient;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
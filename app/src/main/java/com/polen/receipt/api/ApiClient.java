package com.polen.receipt.api;


import android.support.annotation.StringRes;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polen.receipt.R;
import com.polen.receipt.MainApplication;

import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by kong on 7/10/15.
 */

public class ApiClient {

    private static final String API_URL = "http://192.168.0.135";

    private static Retrofit sRestAdapter;
    private static ApiService sService;

    /**
     * Returns the service used for API requests
     *
     * @return
     */
    public static ApiService getService() {
        if (sRestAdapter == null || sService == null) {
            sRestAdapter = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            sService = sRestAdapter.create(ApiService.class);
        }

        return sService;
    }

    public static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static OkHttpClient getClient() {
        MainApplication app = MainApplication.getInstance();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS);

        return builder.build();
    }

    private static GsonConverterFactory getConverter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//              .registerTypeAdapter(ImgurBaseObject.class, new ImgurSerializer())
                .create();

        return GsonConverterFactory.create(gson);
    }

    /**
     * Returns the string resource for the error thrown by Retrofit
     *
     * @param error The thrown error
     * @return
     */
    @StringRes
    public static int getErrorCode(Throwable error) {
        if (error instanceof UnknownHostException) {
            return R.string.error_network;
        }

        return R.string.error_generic;
    }

    /**
     * Returns the string resource for the HTTP status returned by the API
     *
     * @param httpStatus
     * @return
     */
    @StringRes
    public static int getErrorCode(int httpStatus) {
        switch (httpStatus) {
            case HttpURLConnection.HTTP_FORBIDDEN:
                return R.string.error_403;

            case HttpURLConnection.HTTP_UNAUTHORIZED:
                return R.string.error_401;

            default:
                return R.string.error_generic;
        }
    }
}
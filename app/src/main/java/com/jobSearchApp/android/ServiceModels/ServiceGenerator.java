package com.jobSearchApp.android.ServiceModels;


import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class ServiceGenerator {
    private static String baseUrl;

    private static LoginResponse loginResponse;

    public static void Initialize(String baseUrl){
        ServiceGenerator.baseUrl = baseUrl;
    }

    public static void setLoginResponse(LoginResponse loginResponse) {
        ServiceGenerator.loginResponse = loginResponse;
    }

    public  static <T> T createService(Class<T> serviceClass){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();// getting url of server

        return retrofit.create(serviceClass);
    }

    public static boolean isLoginResponseSet(){
        return loginResponse != null;
    }

    public  static <T> T createServiceWithAuth(Class<T> serviceClass){

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", loginResponse.token_type + " " +loginResponse.access_token)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();// getting url of server

        return retrofit.create(serviceClass);
    }
}

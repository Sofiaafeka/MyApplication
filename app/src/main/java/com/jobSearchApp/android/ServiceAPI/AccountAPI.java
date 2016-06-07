package com.jobSearchApp.android.ServiceAPI;

import com.jobSearchApp.android.ServiceModels.RegisterModel;
import com.jobSearchApp.android.ServiceModels.UserInfo;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface AccountAPI {

    @POST("/api/Account/Register")
    public Call<Void> register(@Body RegisterModel registerModel);

    @GET("/api/Account/UserInfo")
    public Call<UserInfo> getUserInfo();

    @POST("/api/Account/Logout")
    public Call<Void> logout();

    @POST("/api/Account/gcmtoken")
    public Call<Void> updateGcmToken(@Body String gcmToken);
}


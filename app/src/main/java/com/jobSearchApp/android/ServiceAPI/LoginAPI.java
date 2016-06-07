package com.jobSearchApp.android.ServiceAPI;


import com.jobSearchApp.android.ServiceModels.LoginResponse;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface LoginAPI {
    @FormUrlEncoded
    @POST("/token")
    public Call<LoginResponse> login(@Field("username")String userName, @Field("password")String password, @Field("grant_type")String grant_type);

}

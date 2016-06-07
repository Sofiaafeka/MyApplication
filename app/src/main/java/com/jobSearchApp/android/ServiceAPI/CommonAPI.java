package com.jobSearchApp.android.ServiceAPI;

import com.jobSearchApp.android.ServiceModels.AllCommonInfo;
import com.jobSearchApp.android.ServiceModels.Skill;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by סופי on 30/04/2016.
 */
public interface CommonAPI {
    @GET("api/common/skills")
    Call<List<Skill>> getSkills();

    @GET("api/common/JobPositions")
    Call<List<String>> getJobPositions();

    @GET("api/common/AllInfo")
    Call<AllCommonInfo> getCommonInfo();

}

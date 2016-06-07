package com.jobSearchApp.android.ServiceAPI;


import com.jobSearchApp.android.ServiceModels.ApplyJobInfo;
import com.jobSearchApp.android.ServiceModels.JobDetail;
import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceModels.Seeker;
import com.jobSearchApp.android.ServiceModels.TrainingRequest;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SeekerAPI {
    @GET("/api/seeker/jobs")
    Call<List<JobInfo>>getJobs();

    @GET("/api/seeker/job")
    Call<JobDetail>getJobDetail(@Query("jobId") int jobId);

    @GET("/api/seeker")
    Call<Seeker> getProfile();

    @POST("/api/seeker/edit")
    Call<Void> updateProfile(@Body Seeker seeker);

    @POST("/api/Seeker/applyJob")
    Call<Void> applyJob(@Body ApplyJobInfo applyJobInfo);

    @GET("/api/seeker/appliedJobs")
    Call<List<JobInfo>>getAppliedJobs();

    @POST("/api/Seeker/training")
    Call<Void> requestTraining(@Body TrainingRequest trainingRequest);

}


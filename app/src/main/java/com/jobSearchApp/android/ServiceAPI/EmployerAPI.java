package com.jobSearchApp.android.ServiceAPI;

import com.jobSearchApp.android.ServiceModels.Employer;
import com.jobSearchApp.android.ServiceModels.InterviewRequest;
import com.jobSearchApp.android.ServiceModels.JobDetail;
import com.jobSearchApp.android.ServiceModels.JobDetailExtended;
import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceModels.Seeker;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;


public interface EmployerAPI {
    @GET("/api/employer")
    Call<Employer> getProfile();

    @POST("/api/editemployer")
    Call<Void> updateProfile(@Body Employer employer);

    @POST("/api/Employer/job")
    Call<Void> addJob(@Body JobDetail job);

    @POST("/api/Employer/EditJob")
    Call<Void> updateJob(@Body JobDetail job);

    @GET("/api/Employer/jobs")
    Call<List<JobInfo>> getJobs();

    @GET("/api/Employer/job")
    Call<JobDetailExtended> getJobDetail(@Query("jobId") int jobId);

    @POST("/api/Employer/deleteJob")
    Call<Void> deleteJob(@Body int jobId);

    @GET("/api/Employer/seeker")
    Call<Seeker> getCandidate(@Query("seekerId") String seekerId, @Query("jobId") int jobId);

    @POST("api/Employer/interview")
    Call<Void> requestInterview(@Body InterviewRequest interviewRequest);
}

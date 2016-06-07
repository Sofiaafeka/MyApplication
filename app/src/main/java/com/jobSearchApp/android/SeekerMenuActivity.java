package com.jobSearchApp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jobSearchApp.android.ServiceAPI.AccountAPI;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SeekerMenuActivity extends AppCompatActivity {
    Button findJobBtn;
    Button desiredJobsBtn;
    Button createProfileBtn;
    Button logOutBtn;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findJobBtn = (Button)findViewById(R.id.find_job);
        desiredJobsBtn = (Button)findViewById(R.id.desired_jobs);
        createProfileBtn = (Button)findViewById(R.id.profiling_emp);
        logOutBtn =  (Button)findViewById(R.id.log_out);
    }

    protected void onStart(){
        super.onStart();

        findJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 intent = new Intent(getBaseContext(), SeekerJobsViewActivity.class);
                startActivity(intent);
            }
        });
        desiredJobsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 intent = new Intent(getBaseContext(), SeekerJobsApplied.class);
                startActivity(intent);
            }
        });
        createProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 intent = new Intent(getBaseContext(), EmployeeProfile.class);
                startActivity(intent);
            }
        });
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountAPI api = ServiceGenerator.createServiceWithAuth(AccountAPI.class);
                Call<Void> call = api.logout();
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });

                AppSharedData.clearLogin(getBaseContext());
                finish();
            }
        });
    }




}

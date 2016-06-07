package com.jobSearchApp.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jobSearchApp.android.ServiceAPI.AccountAPI;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EmployerMenuActivity extends AppCompatActivity {

    private Button showJobsBtn, addJobBtn, logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_menu);

        showJobsBtn =  (Button)findViewById(R.id.show_jobs);
        addJobBtn =  (Button)findViewById(R.id.add_job);
        logOutBtn =  (Button)findViewById(R.id.log_out);
    }

    @Override
    protected void onStart() {
        super.onStart();

        addJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), JobProfile.class);
                startActivity(intent);
            }
        });

        showJobsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EmployerJobsList.class);
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

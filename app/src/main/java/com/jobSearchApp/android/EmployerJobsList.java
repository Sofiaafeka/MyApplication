package com.jobSearchApp.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jobSearchApp.android.ServiceAPI.EmployerAPI;
import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EmployerJobsList extends AppCompatActivity {

    TextView jobNameTxt;
     LinearLayout layout;
    ImageView trash_icon ;
    //View horizontalRule;

    List<View> jobsViewlist = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs_list);

        layout = (LinearLayout) findViewById(R.id.scrollViewLayout);

        trash_icon = new ImageView(getBaseContext());
        trash_icon.setImageResource(R.drawable.trash_can_icon);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(150,100);
        trash_icon.setLayoutParams(layoutParams);

        loadJobsList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadJobsList(){
        for (View jobView : jobsViewlist) {
            layout.removeView(jobView);
        }
        jobsViewlist.clear();

        EmployerAPI employerAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);

        Call<List<JobInfo>> call = employerAPI.getJobs();
        call.enqueue(new Callback<List<JobInfo>>() {
            @Override
            public void onResponse(Response<List<JobInfo>> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    for (final JobInfo job : response.body()) {
                        jobNameTxt = new TextView(getBaseContext());
                        jobNameTxt.setText(job.Name);
                        jobNameTxt.setTextColor(Color.BLACK);
                        jobNameTxt.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                        jobNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        jobNameTxt.setPadding(15, 15, 15, 15);

                        // Listener for clicking on a job name
                        jobNameTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Open popup window

                                Intent intent = new Intent(getBaseContext(), JobProfile.class);
                                intent.putExtra("editJob", job.Id);// sending the job id to be edited
                                startActivity(intent);
                            }
                        });
                        /*seperator line view*/
                        View horizontalRule = new View(getBaseContext());
                        horizontalRule.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 3));
                        horizontalRule.setMinimumHeight(2);
                        horizontalRule.setBackgroundColor(Color.BLUE);

                        layout.addView(jobNameTxt);
                        layout.addView(horizontalRule);
                        jobsViewlist.add(jobNameTxt);
                        jobsViewlist.add(horizontalRule);
                        /* Create a new row to be added. */
                       /* tableRow = new TableRow(getBaseContext());
                        tableRow.setGravity(Gravity.RIGHT);
                        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                        //tableRow.addView(trash_icon);
                        tableRow.addView(jobNameTxt);
                        layout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        layout.addView(horizontalRule);*/
                    }
                }
                else
                    Toast.makeText(getBaseContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }




}

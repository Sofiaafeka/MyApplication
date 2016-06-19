package com.jobSearchApp.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jobSearchApp.android.ServiceAPI.EmployerAPI;
import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EmployerJobsList extends Activity {

    TextView jobNameTxt;
    LinearLayout layout;
    ImageView trashIcon;
    List<View> jobsViewlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs_list);

        layout = (LinearLayout) findViewById(R.id.scrollViewLayout);

        loadJobsList();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void loadJobsList() {
        for (View jobView : jobsViewlist) {
            layout.removeView(jobView);
        }
        jobsViewlist.clear();

        EmployerAPI employerAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);
        Call<List<JobInfo>> call = employerAPI.getJobs();
        call.enqueue(new Callback<List<JobInfo>>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Response<List<JobInfo>> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    for (final JobInfo job : response.body()) {
                        jobNameTxt = new TextView(getBaseContext());
                        trashIcon = new ImageView(getBaseContext());
                        RelativeLayout relativeLayout = new RelativeLayout(getBaseContext());

                        trashIcon.setImageResource(R.drawable.trash_can_icon);


                        relativeLayout.setLayoutParams(new
                                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT));
                        RelativeLayout.LayoutParams layoutParams_AlignLeft =
                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams_AlignLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        layoutParams_AlignLeft.addRule(RelativeLayout.CENTER_VERTICAL);

                        RelativeLayout.LayoutParams layoutParams_AlignRight =
                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams_AlignRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                        trashIcon.setLayoutParams(layoutParams_AlignLeft);
                        jobNameTxt.setLayoutParams(layoutParams_AlignRight);

                        jobNameTxt.setText(job.Name);
                        jobNameTxt.setTextColor(Color.BLACK);
                        jobNameTxt.setTextDirection(View.TEXT_DIRECTION_RTL);
                        trashIcon.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        jobNameTxt.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                        jobNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        jobNameTxt.setPadding(15, 15, 15, 15);

                        relativeLayout.addView(jobNameTxt);
                        relativeLayout.addView(trashIcon);
                        // Listener for clicking on a job name
                        jobNameTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Open popup window

                                Intent intent = new Intent(getBaseContext(), JobProfile.class);
                                // sending the job id to be edited
                                intent.putExtra("editJob", job.Id);
                                startActivity(intent);
                            }
                        });

                        trashIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //confirm with employer the deletion
                                AlertDialog.Builder dialog = new AlertDialog.Builder(EmployerJobsList.this)
                                        .setMessage("תבוצע מחיקת משרה כעת")
                                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setPositiveButton("המשך", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                // sending the job id to be deleted by server
                                                deleteJob(job.Id);
                                            }
                                        });
                                dialog.show();
                            }
                        });
                        /*seperator line view*/
                        View horizontalRule = new View(getBaseContext());
                        horizontalRule.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 3));
                        horizontalRule.setMinimumHeight(2);
                        horizontalRule.setBackgroundColor(Color.BLUE);

                        layout.addView(relativeLayout);
                        layout.addView(horizontalRule);
                        jobsViewlist.add(relativeLayout);
                        jobsViewlist.add(horizontalRule);
                    }
                } else
                    Toast.makeText(getBaseContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void deleteJob(int jobId) {
        EmployerAPI employerAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);
        Call<Void> call = employerAPI.deleteJob(jobId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Toast.makeText(getBaseContext(), "המשרה הוסרה בהצלחה!", Toast.LENGTH_LONG).show();
                    loadJobsList();
                } else
                    Toast.makeText(getBaseContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}

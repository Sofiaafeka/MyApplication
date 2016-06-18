package com.jobSearchApp.android;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jobSearchApp.android.Common.ServerHelper;
import com.jobSearchApp.android.ServiceAPI.SeekerAPI;
import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceModels.InterviewStatus;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SeekerJobsApplied extends AppCompatActivity {

    LinearLayout layout;
    TextView textView, txtInvitation, emptyJobListMeesage;
    JobDetailPopUpWindow popUp;
    List<View> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_jobs_applied);

        layout = (LinearLayout) findViewById(R.id.linear_layout_appliedJobs);
        emptyJobListMeesage = (TextView) findViewById(R.id.emptyJobListMessage);

        loadJobList();
    }

    public void onStart() {
        super.onStart();
    }

    private void loadJobList() {
        for (View view : viewList) {
            layout.removeView(view);
        }
        viewList.clear();

        SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);

        Call<List<JobInfo>> call = seekerAPI.getAppliedJobs();
        call.enqueue(new Callback<List<JobInfo>>() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Response<List<JobInfo>> response, Retrofit retrofit) {

                if (response.isSuccess()) {

                    if(response.body().size() == 0) {
                        emptyJobListMeesage.setVisibility(View.VISIBLE);
                        return;
                    }


                    for (final JobInfo job : response.body()) {
                        textView = new TextView(getBaseContext());
                        textView.setText(job.Name);
                        textView.setTextColor(Color.BLACK);
                        textView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        textView.setPadding(15, 15, 15, 15);
                        textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                        /* check if there is pending invitation to interview*/
                        if (job.InterviewInfo != null &&
                                job.InterviewInfo.Status != InterviewStatus.NONE.getValue())
                            if (job.InterviewInfo.Status == InterviewStatus.PENDING.getValue()) {
                                txtInvitation = new TextView(getBaseContext());
                                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
                                txtInvitation.setText("הזמנה לראיון בתאריך: " + df.format(ServerHelper.dateFromTicks(job.InterviewInfo.ScheduleDate)));
                                txtInvitation.setTextColor(Color.MAGENTA);
                                txtInvitation.setPadding(15,0,15,20);
                            }

                        // Listener for clicking on a job name
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Open popup window
                                showPopup(job.Id);


                            }
                        });
                        /* separator line for jobs list view*/
                        View horizontalRule = new View(getBaseContext());
                        horizontalRule.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, 2));

                        horizontalRule.setBackgroundColor(Color.BLUE);

                        layout.addView(textView);
                        viewList.add(textView);
                        if (txtInvitation != null) {
                            layout.addView(txtInvitation);
                            viewList.add(txtInvitation);
                            txtInvitation = null;
                        }
                        layout.addView(horizontalRule);
                        viewList.add(horizontalRule);
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

    // The method that displays the popup.
    private void showPopup(int jobId) {
        Point p = new Point();
        int[] location = new int[2];

        layout.getLocationOnScreen(location);
        p.set(location[0], location[1]);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int scrHeight = displaymetrics.heightPixels;
        int width = layout.getWidth() - 60;
        int height = scrHeight - p.y - 60;

        // Creating the PopupWindow
        popUp = new JobDetailPopUpWindow(this, JobDetailPopUpWindow.JobDetailPopupType.APPLYED_TO);
        popUp.setWidth(width);
        popUp.setHeight(height);
        popUp.setFocusable(true);

        // Some offset to align the popup .
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Displaying the popup at the specified location, + offsets.
        popUp.showPopup(Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y, jobId);
        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(popUp.getIsApplyRemoved())
                    loadJobList();
            }
        });
    }

}

package com.jobSearchApp.android;


import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.widget.RelativeLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobSearchApp.android.ServiceModels.JobInfo;
import com.jobSearchApp.android.ServiceAPI.SeekerAPI;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ShowJobsOnListFragment extends Fragment {

    ScrollView scrollView;
    TextView jobNameTextView, matchingLevel;//for job name text and matching level number
    /* for holding textview of job name
    /* and  matching level number on the same row*/
    LinearLayout layout;
    JobDetailPopUpWindow popUp;
    //List<View> viewList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.show_jobs_on_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollView = (ScrollView) view.findViewById(R.id.scrollview);
        layout = (LinearLayout) view.findViewById(R.id.scrollViewLayout);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadJobsList();
    }

    public void onStart() {
        super.onStart();
    }

    private void loadJobsList() {

        SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);

        Call<List<JobInfo>> call = seekerAPI.getJobs();
        call.enqueue(new Callback<List<JobInfo>>() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Response<List<JobInfo>> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    for (final JobInfo job : response.body()) {
                        jobNameTextView = new TextView(getContext());
                        matchingLevel = new TextView(getContext());
               /* layout parameters to show in one row the name and match number  */
                        RelativeLayout relativeLayout = new RelativeLayout(getContext());
                        relativeLayout.setLayoutParams(new
                                LayoutParams(LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT));
                        LayoutParams layoutParams_AlignLeft =
                                new LayoutParams(LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT);
                        layoutParams_AlignLeft.addRule(RelativeLayout.ALIGN_LEFT);
                        matchingLevel.setLayoutParams(layoutParams_AlignLeft);
                        LayoutParams layoutParams_AlignRight =
                                new LayoutParams(LayoutParams.MATCH_PARENT,
                                        LayoutParams.WRAP_CONTENT);
                        layoutParams_AlignRight.addRule(RelativeLayout.ALIGN_RIGHT);
                        matchingLevel.setLayoutParams(layoutParams_AlignRight);
                        jobNameTextView.setText(job.Name);
                        matchingLevel.setText(job.MatchLevel + "");
                        jobNameTextView.setTextColor(Color.BLACK);
                        matchingLevel.setTextColor(Color.BLACK);
                        jobNameTextView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                        matchingLevel.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                        jobNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        matchingLevel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        jobNameTextView.setPadding(15, 15, 15, 15);
                        matchingLevel.setPadding(15, 15, 15, 15);
                        jobNameTextView.setTextDirection(View.TEXT_DIRECTION_RTL);
                        matchingLevel.setTextDirection(View.TEXT_DIRECTION_LTR);
                        View horizontalRule = new View(getContext());
                        horizontalRule.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        horizontalRule.setMinimumHeight(2);
                        horizontalRule.setBackgroundColor(Color.BLUE);

                        // Listener for clicking on a job name
                        jobNameTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Open popup window
                                showPopup(ShowJobsOnListFragment.this, job.Id);

                            }
                        });
                        relativeLayout.addView(jobNameTextView, layoutParams_AlignRight);
                        relativeLayout.addView(matchingLevel, layoutParams_AlignLeft);
                        layout.addView(relativeLayout);
                        layout.addView(horizontalRule);
                        /*viewList.add(jobNameTextView);
                        viewList.add(matchingLevel);
                        viewList.add(horizontalRule);*/
                    }
                } else
                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // The method that displays the popup.
    private void showPopup(final Fragment fragment, final int jobId) {
        Point p = new Point();
        int[] location = new int[2];

        scrollView.getLocationOnScreen(location);
        p.set(location[0], location[1]);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int scrHeight = displaymetrics.heightPixels;
        int width = scrollView.getWidth() - 60;
        int height = scrHeight - p.y - 60;

        // Creating the PopupWindow
        popUp = new JobDetailPopUpWindow(fragment.getActivity(), JobDetailPopUpWindow.JobDetailPopupType.SEEK_JOB);
        popUp.setWidth(width);
        popUp.setHeight(height);
        popUp.setFocusable(true);

        // Some offset to align the popup .
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Displaying the popup at the specified location, + offsets.
        popUp.showPopup(Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y, jobId);
    }
}

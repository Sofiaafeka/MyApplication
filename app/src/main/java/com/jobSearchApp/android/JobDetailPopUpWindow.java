package com.jobSearchApp.android;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jobSearchApp.android.Common.ServerHelper;
import com.jobSearchApp.android.ServiceAPI.SeekerAPI;
import com.jobSearchApp.android.ServiceModels.ApplyJobInfo;
import com.jobSearchApp.android.ServiceModels.InterviewResponse;
import com.jobSearchApp.android.ServiceModels.JobDetail;
import com.jobSearchApp.android.ServiceModels.InterviewStatus;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;
import com.jobSearchApp.android.ServiceModels.SkillExperience;
import com.jobSearchApp.android.ServiceModels.TrainingRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class JobDetailPopUpWindow extends PopupWindow {

    private View layout;
    LinearLayout interviewInfoLayout;
    private Button applyToJobBtn;
    private Button requestTrainingBtn;
    private Button addToCalendarBtn;
    private Button cancelJobApply, rejectInterview;
    private TextView jobName, jobDescription, jobPosition, jobSkill1, jobSkill2, jobSkill3,
            jobYearsSkill1, jobYearsSkill2, jobYearsSkill3, jobAddress;
    private TextView interviewInfoTxt;
    private Activity activity;
    private JobDetailPopupType popupType;
    private int jobId;
    private JobDetail jobDetails;
    private boolean applyRemoved;

    public enum JobDetailPopupType {
        SEEK_JOB, APPLYED_TO
    }

    public JobDetailPopUpWindow(final Activity rootActivity, JobDetailPopupType popupType) {
        super(rootActivity);

        this.activity = rootActivity;
        this.popupType = popupType;

        // Inflate the job_details_window.xml
        LayoutInflater layoutInflater = (LayoutInflater) rootActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.job_details_window, null);

        setContentView(layout);

        /* Getting  reference to  buttons and views.*/
        applyToJobBtn = (Button) layout.findViewById(R.id.applyToJob);
        requestTrainingBtn = (Button) layout.findViewById(R.id.requestTraining);
        addToCalendarBtn = (Button) layout.findViewById(R.id.setInterviewOnCalendar);
        cancelJobApply = (Button) layout.findViewById(R.id.cancelJobApply);
        interviewInfoLayout = (LinearLayout) layout.findViewById(R.id.interviewInfoLayout);
        rejectInterview = (Button) layout.findViewById(R.id.rejectInterview);
        interviewInfoTxt = (TextView) layout.findViewById(R.id.interviewInfoTxt);

        jobName = (TextView) layout.findViewById(R.id.jobName);
        jobDescription = (TextView) layout.findViewById(R.id.jobDescription);
        jobPosition = (TextView) layout.findViewById(R.id.jobPosition);
        jobSkill1 = (TextView) layout.findViewById(R.id.jobSkill1);
        jobYearsSkill1 = (TextView) layout.findViewById(R.id.jobYearsSkill1);
        jobSkill2 = (TextView) layout.findViewById(R.id.jobSkill2);
        jobYearsSkill2 = (TextView) layout.findViewById(R.id.jobYearsSkill2);
        jobSkill3 = (TextView) layout.findViewById(R.id.jobSkill3);
        jobYearsSkill3 = (TextView) layout.findViewById(R.id.jobYearsSkill3);
        jobAddress = (TextView) layout.findViewById(R.id.jobAddress);

        switch (popupType) {
            case APPLYED_TO:
                applyToJobBtn.setVisibility(View.GONE);
                requestTrainingBtn.setVisibility(View.GONE);
                interviewInfoLayout.setVisibility(View.VISIBLE);
                cancelJobApply.setVisibility(View.VISIBLE);
                break;
            case SEEK_JOB:
                break;
        }

        /* Setting listeners to buttons */
        applyToJobBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                applyToJobBtn.setEnabled(false);
                requestTrainingBtn.setEnabled(false);

                ApplyJobInfo applyRequest = new ApplyJobInfo();
                applyRequest.JobId = jobId;
                applyRequest.SetApply = true;

                SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
                Call<Void> call = seekerAPI.applyJob(applyRequest);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {

                        if (response.isSuccess()) {
                            Toast.makeText(activity, "הגשת המועמדות נקלטה בהצלחה!", Toast.LENGTH_LONG).show();
                            dismiss();
                        } else
                            Toast.makeText(activity, response.message(), Toast.LENGTH_LONG).show();

                        applyToJobBtn.setEnabled(true);
                        requestTrainingBtn.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        requestTrainingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Set up the input
                final EditText input = new EditText(activity);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                        .setMessage("נא הכנס מספר פלאפון ופרטיך יישלחו לחברת ההכשרה.")
                        .setView(input)
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("המשך", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                applyToJobBtn.setEnabled(false);
                                requestTrainingBtn.setEnabled(false);

                                //send details to server
                                TrainingRequest trainingRequest = new TrainingRequest();
                                trainingRequest.JobId = jobId;
                                trainingRequest.PhoneNumber = input.getText().toString();
                                if (trainingRequest.PhoneNumber == null || trainingRequest.PhoneNumber.length() == 0) {
                                    Toast.makeText(activity, "הכנס מספר טלפון", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
                                Call<Void> call = seekerAPI.requestTraining(trainingRequest);

                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Response<Void> response, Retrofit retrofit) {

                                        if (response.isSuccess()) {

                                            Toast.makeText(activity, "הבקשה נשלחה! יצרו איתך קשר בהקדם.", Toast.LENGTH_LONG).show();
                                            dismiss();
                                        } else {
                                            Toast.makeText(activity, response.message(), Toast.LENGTH_LONG).show();
                                            requestTrainingBtn.setEnabled(true);
                                        }

                                        applyToJobBtn.setEnabled(true);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                dialog.show();

            }
        });

        addToCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                respondToInterview(true);
            }
        });

        rejectInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respondToInterview(false);
            }
        });

        cancelJobApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if already invited to an interview
                // and if is - delete from calendar

                //request  server delete candidate job apply
                SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
                ApplyJobInfo applyJobInfo = new ApplyJobInfo();
                applyJobInfo.SetApply = false;
                applyJobInfo.JobId = jobId;
                Call<Void> call = seekerAPI.applyJob(applyJobInfo);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            applyRemoved = true;
                            dismiss();

                        } else
                            Toast.makeText(activity.getBaseContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(activity.getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public boolean getIsApplyRemoved() {
        return applyRemoved;
    }




    /* Presenting to seeker the details of the selected job */

    private void setJobDetails(JobDetail jobDetails) {
        this.jobDetails = jobDetails;
        SkillExperience skillExperience;

        jobName.setText(jobDetails.Name);
        jobDescription.setText(jobDetails.Description);
        jobPosition.setText(jobDetails.JobPosition);
        /* Presenting to seeker the skills+experience years required of job */
        if (jobDetails.SkillExperiences.size() > 0) {
            skillExperience = jobDetails.SkillExperiences.get(0);
            jobSkill1.setText(skillExperience.Skill.Name);
            if (skillExperience.ExperienceRangeStr != null)
                jobYearsSkill1.setText(skillExperience.ExperienceRangeStr + " שנים");
        }
        if (jobDetails.SkillExperiences.size() > 1) {
            skillExperience = jobDetails.SkillExperiences.get(1);
            jobSkill2.setText(skillExperience.Skill.Name);
            if (skillExperience.ExperienceRangeStr != null)
                jobYearsSkill2.setText(skillExperience.ExperienceRangeStr + " שנים");

        }
        if (jobDetails.SkillExperiences.size() > 2) {
            skillExperience = jobDetails.SkillExperiences.get(2);
            jobSkill3.setText(skillExperience.Skill.Name);
            if (skillExperience.ExperienceRangeStr != null)
                jobYearsSkill3.setText(skillExperience.ExperienceRangeStr + " שנים");
        }
        /* Presenting to seeker the job address */
        if (jobDetails.Location != null)
            jobAddress.setText(jobDetails.Location.Address);

        if (jobDetails.InterviewInfo != null) {

            InterviewStatus status = InterviewStatus.fromInteger(jobDetails.InterviewInfo.Status);
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
            switch (status) {
                case PENDING:
                    interviewInfoTxt.setVisibility(View.VISIBLE);
                    interviewInfoTxt.setText("הזמנה לראיון בתאריך: " + df.format(ServerHelper.dateFromTicks(jobDetails.InterviewInfo.ScheduleDate)));
                    interviewInfoTxt.setTextColor(Color.MAGENTA);
                    interviewInfoTxt.setPadding(15, 0, 15, 20);
                    interviewInfoLayout.setVisibility(View.VISIBLE);
                    break;

                case ACCEPTED:
                    interviewInfoTxt.setVisibility(View.VISIBLE);
                    interviewInfoLayout.setVisibility(View.GONE);
                    interviewInfoTxt.setText("ראיון בתאריך:  " + df.format(ServerHelper.dateFromTicks(jobDetails.InterviewInfo.ScheduleDate)));
                    interviewInfoTxt.setTextColor(Color.GREEN);
                    interviewInfoTxt.setPadding(15, 0, 15, 20);
                    break;

                case REJECTED:
                    interviewInfoTxt.setVisibility(View.VISIBLE);
                    interviewInfoLayout.setVisibility(View.GONE);                    interviewInfoTxt.setText("נשלחה בקשה לקביעת ראיון חדש");
                    interviewInfoTxt.setTextColor(Color.RED);
                    interviewInfoTxt.setPadding(15, 0, 15, 20);
                    break;

                default:
                    interviewInfoLayout.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void respondToInterview(final boolean accept) {
        InterviewResponse interviewResponse = new InterviewResponse();
        interviewResponse.Accept = accept;
        interviewResponse.JobId = jobId;

        SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
        Call<Void> call = seekerAPI.RespondToInterview(interviewResponse);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (accept) {
                        Calendar beginTime = Calendar.getInstance();
                        beginTime.setTime(ServerHelper.dateFromTicks(jobDetails.InterviewInfo.ScheduleDate));
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTime(ServerHelper.dateFromTicks(jobDetails.InterviewInfo.ScheduleDate));
                        endTime.add(Calendar.HOUR, 1);
                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                                .putExtra(CalendarContract.Events.TITLE, "Interview")
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, jobAddress.getText().toString());


                        activity.startActivity(intent);
                        jobDetails.InterviewInfo.Status = InterviewStatus.ACCEPTED.getValue();
                    } else {// job interview rejected by seeker
                        Toast.makeText(activity.getBaseContext(), "דחייתך התקבלה, הודעה נשלחה למעסיק", Toast.LENGTH_LONG).show();
                        jobDetails.InterviewInfo.Status = InterviewStatus.REJECTED.getValue();

                    }

                    setJobDetails(jobDetails);
                } else {// response from server not success
                    Toast.makeText(activity.getBaseContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity.getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void showPopup(int gravity, int x, int y, final int jobId) {

        this.jobId = jobId;
        SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
        Call<JobDetail> call = seekerAPI.getJobDetail(jobId);
        Response<JobDetail> response = null;

        showLoad(true);
        call.enqueue(new Callback<JobDetail>() {
            @Override
            public void onResponse(Response<JobDetail> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    JobDetail jobDetail = response.body();
                    // setting job description
                    setJobDetails(jobDetail);
                } else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_LONG).show();
                }

                showLoad(false);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                showLoad(false);
            }
        });

        showAtLocation(layout, gravity, x, y);
    }

    private void showLoad(boolean b) {
        layout.setVisibility(b ? View.GONE : View.VISIBLE);
    }
}

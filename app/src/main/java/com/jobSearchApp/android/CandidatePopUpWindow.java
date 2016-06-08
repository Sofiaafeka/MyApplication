package com.jobSearchApp.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jobSearchApp.android.Common.ServerHelper;
import com.jobSearchApp.android.ServiceAPI.EmployerAPI;
import com.jobSearchApp.android.ServiceModels.InterviewRequest;
import com.jobSearchApp.android.ServiceModels.JobDetail;
import com.jobSearchApp.android.ServiceModels.Seeker;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CandidatePopUpWindow extends PopupWindow {
    private String candidateId;
    private int jobId;
    private Button interviewRqstBtn;
    Calendar calendar;

    private Activity activity;
    private View layout;
    private TextView fullName, yearBirth, candidateAddress, candidateDegree,
            fieldOfStudy, matriculation, expert1, expert2, expert3,
            expert1Years, expert2Years, expert3Years, desireSalary,
            availability, fullTime, mobile, candidateUrl;


    public CandidatePopUpWindow(Activity activity) {
        super(activity);

        this.activity = activity;

        // Inflate the candidate_details_window.xml
        LinearLayout viewGroup = (LinearLayout) activity.findViewById(R.id.rootLayout);
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.candidate_details_window, viewGroup);

        setContentView(layout);

        /* get reference to text view */
        fullName = (TextView) layout.findViewById(R.id.fullName);
        yearBirth = (TextView) layout.findViewById(R.id.yearBirth);
        candidateAddress = (TextView) layout.findViewById(R.id.candidate_address);
        candidateDegree = (TextView) layout.findViewById(R.id.candidate_degree);
        fieldOfStudy = (TextView) layout.findViewById(R.id.fieldOfStudy);
        matriculation = (TextView) layout.findViewById(R.id.matriculation);
        expert1 = (TextView) layout.findViewById(R.id.expert1);
        expert2 = (TextView) layout.findViewById(R.id.expert2);
        expert3 = (TextView) layout.findViewById(R.id.expert3);
        expert1Years = (TextView) layout.findViewById(R.id.years_exp1);
        expert2Years = (TextView) layout.findViewById(R.id.years_exp2);
        expert3Years = (TextView) layout.findViewById(R.id.years_exp3);
        desireSalary = (TextView) layout.findViewById(R.id.desire_salary);
        availability = (TextView) layout.findViewById(R.id.availability);
        fullTime = (TextView) layout.findViewById(R.id.full_time_pos);
        mobile = (TextView) layout.findViewById(R.id.mobile);
        candidateUrl = (TextView) layout.findViewById(R.id.candidate_url);
        interviewRqstBtn = (Button)layout.findViewById(R.id.interviewRqst_btn);
        interviewRqstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choostDateAndTimeInterview();
            }
        });

    }

    private void choostDateAndTimeInterview() {
        final View dialogView = View.inflate(activity, R.layout.interview_set_date_time, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();


        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                 calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                sendInterviewRequest(calendar.getTime(), alertDialog);
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
    public String getInterviewDate(){
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        return df.format(calendar.getTime());
    }

    private void sendInterviewRequest(Date date, final Dialog dialog) {
        InterviewRequest interviewRequest = new InterviewRequest();
        interviewRequest.JobId = jobId;
        interviewRequest.SeekerId = candidateId;
        interviewRequest.ScheduleDate = ServerHelper.ticksFromDate(date);

        EmployerAPI employerAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);

        employerAPI.requestInterview(interviewRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Toast.makeText(activity, "הבקשה נשלחה למועמד", Toast.LENGTH_LONG).show();

                    if(dialog!=null) {
                        dialog.dismiss();
                    }

                    CandidatePopUpWindow.this.dismiss();

                } else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void showPopup(int gravity, int x, int y, String candidateId, int jobId) {
        this.jobId=jobId;
        this.candidateId = candidateId;
        EmployerAPI employerAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);
        Call<Seeker> call = employerAPI.getCandidate(candidateId, jobId);
        Response<JobDetail> response = null;

        call.enqueue(new Callback<Seeker>() {
            @Override
            public void onResponse(Response<Seeker> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    Seeker candidate = response.body();

                    setCandidate(candidate);

                } else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        showAtLocation(layout, gravity, x, y);
    }

    /* Retrieve from server the details of the candidate for showing to employer in popup window */
    private void setCandidate(Seeker candidate) {
        fullName.setText(candidate.FirstName + " " + candidate.LastName);
        yearBirth.setText(Integer.toString(candidate.YearOfBirth));
        if(candidate.HomeLocation!= null)
            candidateAddress.setText(candidate.HomeLocation.Address);
        candidateDegree.setText(candidate.Degree);
        if(candidate.FieldOfStudy!= null)
        fieldOfStudy.setText(" בתחום " + candidate.FieldOfStudy);


        if (candidate.Matriculation)
            matriculation.setText(" מלאה");
        else
            matriculation.setText(" חלקית");

        if (candidate.SkillExperiences.size() > 0) {
            expert1.setText(candidate.SkillExperiences.get(0).Skill.Name + " ");

            expert1Years.setText("ניסיון: " + candidate.SkillExperiences.get(0).ExperienceRangeStr);
        }
        if (candidate.SkillExperiences.size() > 1) {
            expert2.setText(candidate.SkillExperiences.get(1).Skill.Name+ " ");
            expert2Years.setText("ניסיון: " + candidate.SkillExperiences.get(1).ExperienceRangeStr);
        }
        if (candidate.SkillExperiences.size() > 2) {
            expert3.setText(candidate.SkillExperiences.get(2).Skill.Name + " ");
            expert3Years.setText("ניסיון: " + candidate.SkillExperiences.get(2).ExperienceRangeStr);
        }
        desireSalary.setText(candidate.SalaryRanges);
        if (candidate.AvailableFrom != null){
            Date date = ServerHelper.dateFromTicks(candidate.AvailableFrom);
            DateFormat df = new SimpleDateFormat("dd/MM/yy");
            availability.setText("החל מתאריך:   " + df.format(date));
        }
        else
            availability.setText(" מיידית");
        if (candidate.FullTime)
            fullTime.setText(" מלאה");
        else
            fullTime.setText(" חלקית");
        if (candidate.IsMobile)
            mobile.setText(" כן");
        else
            mobile.setText(" לא");
        if (candidate.URL != null)
            candidateUrl.setText(candidate.URL);

    }
}

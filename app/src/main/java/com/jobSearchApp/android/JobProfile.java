package com.jobSearchApp.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.jobSearchApp.android.Common.ServerHelper;
import com.jobSearchApp.android.ServiceAPI.CommonAPI;
import com.jobSearchApp.android.ServiceAPI.EmployerAPI;
import com.jobSearchApp.android.ServiceModels.AllCommonInfo;
import com.jobSearchApp.android.ServiceModels.JobDetail;
import com.jobSearchApp.android.ServiceModels.JobDetailExtended;
import com.jobSearchApp.android.ServiceModels.LocationModel;
import com.jobSearchApp.android.ServiceModels.RegionModel;
import com.jobSearchApp.android.ServiceModels.SeekerInfo;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;
import com.jobSearchApp.android.ServiceModels.Skill;
import com.jobSearchApp.android.ServiceModels.SkillExperience;

import java.io.IOException;
import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class JobProfile extends AppCompatActivity {

    private static final String TAG = "Log_place";
    Spinner jobPosition, regionSpinner, subRegionSpinner,
            expertiseField1, expertiseField2, expertiseField3,
            expertYears1, expertYears2, expertYears3;

    View mProfileJobFormView, mProgressView;
    LinearLayout candidatesListLayout;
    TextView txtViewCandidates;
    TextView candidateNameTxt;
    EditText jobName, jobDescription;
    Button saveBtn;

    CandidatePopUpWindow popUp;

    private Skill emptySkill = new Skill(-1, "");
    private Skill[] skills_array;
    private RegionModel[] regions_array;
    private RegionModel[] subRegions_array;
    private String[] skillsYearsExp;
    private String[] jobPositions;


    int jobId = -1;

    private JobDetail jobDetail = new JobDetail();
    private PlaceAutocompleteFragment autocompleteFragmentJobAddress;

    private boolean getSubRegionFromSeekerRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("editJob")) {
            Bundle bundle = intent.getExtras();
            jobId = bundle.getInt("editJob");//job id for editing it

        }

        mProfileJobFormView = findViewById(R.id.mProfileJobFormView);
        mProgressView = findViewById(R.id.progress_animation);
        jobName = (EditText) findViewById(R.id.job_name);
        jobDescription = (EditText) findViewById(R.id.job_description);

        jobPosition = (Spinner) findViewById(R.id.job_position_spinner);
        regionSpinner = (Spinner) findViewById(R.id.regions_spinner);
        subRegionSpinner = (Spinner) findViewById(R.id.sub_regions_spinner);
        expertiseField1 = (Spinner) findViewById(R.id.expert1);
        expertiseField2 = (Spinner) findViewById(R.id.expert2);
        expertiseField3 = (Spinner) findViewById(R.id.expert3);
        expertYears1 = (Spinner) findViewById(R.id.expert1_years_exp);
        expertYears2 = (Spinner) findViewById(R.id.expert2_years_exp);
        expertYears3 = (Spinner) findViewById(R.id.expert3_years_exp);

        txtViewCandidates = (TextView) findViewById(R.id.txtViewCandidates);
        candidatesListLayout = (LinearLayout)findViewById(R.id.candidate_list);


        autocompleteFragmentJobAddress = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.job_preffered_address_autocomplete_fragment);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        
        if(!isJobEditMode()){
            txtViewCandidates.setVisibility(View.GONE);
            candidatesListLayout.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateJobToServer();
            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                RegionModel[] subRegions_array = new RegionModel[regions_array[position].SubRegions.size()];
                regions_array[position].SubRegions.toArray(subRegions_array);
                setSubRegions(subRegions_array);

                if (getSubRegionFromSeekerRequired) {
                    setSubRegionSpinnerByName(jobDetail.Location.Region);
                    getSubRegionFromSeekerRequired = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        autocompleteFragmentJobAddress.setFilter(typeFilter);
        autocompleteFragmentJobAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.i(TAG, "Place: " + place.getName());

                if (jobDetail.Location == null)
                    jobDetail.Location = new LocationModel();

                jobDetail.Location.Address = place.getAddress().toString();

                if (place.getLatLng() != null) {
                    jobDetail.Location.GeoLatitude = place.getLatLng().latitude;
                    jobDetail.Location.GeoLongitude = place.getLatLng().longitude;
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        GetInfoTask task = new GetInfoTask();
        task.execute((Void) null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean isJobEditMode() {
        return jobId != -1;
    }

    private void setSubRegionSpinnerByName(String regionName) {
        for (int i = 0; i < subRegions_array.length; i++) {
            if (subRegions_array[i].Name.equals(regionName)) {
                subRegionSpinner.setSelection(i);
                break;
            }
        }
    }

    private void addOrUpdateJobToServer() {
        EmployerAPI empAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);

        updateJobDetailsFromView();

        Call<Void> call = null;

        if(isJobEditMode())
            call = empAPI.updateJob(jobDetail);
        else
            call =  empAPI.addJob(jobDetail);

        showProgress(true);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {

                    String message = isJobEditMode() ? "המשרה עודכנה בהצלחה!" : "המשרה הוספה בהצלחה!";
                    Toast.makeText(getBaseContext(),message , Toast.LENGTH_LONG).show();
                    finish();

                }
                showProgress(false);
            }

            @Override
            public void onFailure(Throwable t) {
                showProgress(false);
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateJobDetailsFromView() {

        jobDetail.Name = jobName.getText().toString();
        jobDetail.Description = jobDescription.getText().toString();
        jobDetail.JobPosition = jobPosition.getSelectedItem().toString();

        jobDetail.SkillExperiences.clear();
        if ((Skill) expertiseField1.getSelectedItem() != emptySkill) {
            SkillExperience skillExperience = new SkillExperience();
            skillExperience.Skill = (Skill) expertiseField1.getSelectedItem();
            skillExperience.ExperienceRangeStr = (String) expertYears1.getSelectedItem();
            jobDetail.SkillExperiences.add(skillExperience);
        }
        if ((Skill) expertiseField2.getSelectedItem() != emptySkill) {
            SkillExperience skillExperience = new SkillExperience();
            skillExperience.Skill = (Skill) expertiseField2.getSelectedItem();
            skillExperience.ExperienceRangeStr = (String) expertYears2.getSelectedItem();
            jobDetail.SkillExperiences.add(skillExperience);
        }
        if ((Skill) expertiseField3.getSelectedItem() != emptySkill) {
            SkillExperience skillExperience = new SkillExperience();
            skillExperience.Skill = (Skill) expertiseField3.getSelectedItem();
            skillExperience.ExperienceRangeStr = (String) expertYears3.getSelectedItem();
            jobDetail.SkillExperiences.add(skillExperience);
        }

        if (jobDetail.Location == null)
            jobDetail.Location = new LocationModel();

        jobDetail.Location.Region = ((RegionModel) subRegionSpinner.getSelectedItem()).Name;
    }

    // The method that displays the popup.
    private void showPopupCandidate(String candidateId) {
        Point p = new Point();

        // Creating the PopupWindow
        popUp = new CandidatePopUpWindow(this);

        popUp.setFocusable(true);

        // Some offset to align the popup .
        int OFFSET_X = 60;
        int OFFSET_Y = 150;

        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        popUp.setWidth(screenWidth - 120);
        popUp.setHeight(screenHeight - 200);
        p.x = 0;
        p.y = 0;

        // Displaying the popup at the specified location, + offsets.
        popUp.showPopup(Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y, candidateId, jobId);
        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String candidateInterviewDate =  popUp.getInterviewDate();;
                if(candidateInterviewDate != null) {
                    String candidateName = candidateNameTxt.getText().toString();
                    candidateNameTxt.setText(candidateName + "" +
                            "הוזמן לראיון בתאריך: "   + candidateInterviewDate);
                }

            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private class GetInfoTask extends AsyncTask<Void, Void, Boolean> {
        public String errorMessage;
        private AllCommonInfo commonInfo;
        private JobDetailExtended jobDetail;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                CommonAPI commonApi = ServiceGenerator.createServiceWithAuth(CommonAPI.class);
                Call<AllCommonInfo> callcommonInfo = commonApi.getCommonInfo();

                Response<AllCommonInfo> response = null;

                response = callcommonInfo.execute();

                if (response.isSuccess()) {

                    commonInfo = response.body();
                    commonInfo.Skills.add(0, JobProfile.this.emptySkill);
                } else {
                    errorMessage = response.message();
                    return false;
                }


                if (isJobEditMode()) {
                    EmployerAPI employerAPI = ServiceGenerator.createServiceWithAuth(EmployerAPI.class);
                    Response<JobDetailExtended> jobResponse = employerAPI.getJobDetail(jobId).execute();

                    if (jobResponse.isSuccess()) {
                        jobDetail = jobResponse.body();
                    } else {
                        errorMessage = response.message();
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success) {

                Skill[] skills_array = new Skill[commonInfo.Skills.size()];
                commonInfo.Skills.toArray(skills_array);

                String[] skillYearsExp_array = new String[commonInfo.YearsOfExperience.size()];
                commonInfo.YearsOfExperience.toArray(skillYearsExp_array);

                String[] jobPostions_array = new String[commonInfo.JobPositions.size()];
                commonInfo.JobPositions.toArray(jobPostions_array);

                RegionModel[] regions_array = new RegionModel[commonInfo.Regions.size()];
                commonInfo.Regions.toArray(regions_array);

                JobProfile.this.setSkills(skills_array);
                JobProfile.this.setSkillsYearsExp(skillYearsExp_array);
                JobProfile.this.setJobPositions(jobPostions_array);
                JobProfile.this.setRegions(regions_array);

                if (isJobEditMode()) {// There is jobId to edit a job
                    JobProfile.this.setJobDetail(jobDetail);
                }
            } else {
                Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();

                JobProfile.this.finish();
            }

            JobProfile.this.showProgress(false);

        }

        @Override
        protected void onPreExecute() {
            JobProfile.this.showProgress(true);
            super.onPreExecute();
        }
    }

    private void setJobDetail(JobDetailExtended jobDetail) {
        this.jobDetail = jobDetail;

        jobName.setText(jobDetail.Name);
        jobDescription.setText(jobDetail.Description);

        ArrayAdapter<CharSequence> adapter =(ArrayAdapter<CharSequence>) jobPosition.getAdapter();
        int pos = adapter.getPosition(jobDetail.JobPosition);

        jobPosition.setSelection(pos);

        if (jobDetail.SkillExperiences.size() > 0) {
            SkillExperience skillExperience = jobDetail.SkillExperiences.get(0);
            expertiseField1.setSelection(((ArrayAdapter<Skill>) expertiseField1.getAdapter()).getPosition(skillExperience.Skill));
            expertYears1.setSelection(((ArrayAdapter<CharSequence>) expertYears1.getAdapter()).getPosition(skillExperience.ExperienceRangeStr));
        }
        if (jobDetail.SkillExperiences.size() > 1) {
            SkillExperience skillExperience = jobDetail.SkillExperiences.get(1);
            expertiseField2.setSelection(((ArrayAdapter<Skill>) expertiseField2.getAdapter()).getPosition(skillExperience.Skill));
            expertYears2.setSelection(((ArrayAdapter<CharSequence>) expertYears2.getAdapter()).getPosition(skillExperience.ExperienceRangeStr));
        }
        if (jobDetail.SkillExperiences.size() > 2) {
            SkillExperience skillExperience = jobDetail.SkillExperiences.get(2);
            expertiseField3.setSelection(((ArrayAdapter<Skill>) expertiseField3.getAdapter()).getPosition(skillExperience.Skill));
            expertYears3.setSelection(((ArrayAdapter<CharSequence>) expertYears3.getAdapter()).getPosition(skillExperience.ExperienceRangeStr));
        }

        if (jobDetail.Location != null) {

            autocompleteFragmentJobAddress.setText(jobDetail.Location.Address);

            for (int i = 0; i < regions_array.length; i++) {
                if (regions_array[i].Name.equals(jobDetail.Location.Region)) {
                    regionSpinner.setSelection(((ArrayAdapter<RegionModel>) regionSpinner.getAdapter()).getPosition(regions_array[i]));
                    break;
                }

                for (int j = 0; j < regions_array[i].SubRegions.size(); j++) {
                    if (regions_array[i].SubRegions.get(j).Name.equals(jobDetail.Location.Region)) {
                        regionSpinner.setSelection(((ArrayAdapter<RegionModel>) regionSpinner.getAdapter()).getPosition(regions_array[i]));
                        getSubRegionFromSeekerRequired = true;

                        break;
                    }
                }
            }
        }

     /* Showing the list of candidates in edit mode */
       if(isJobEditMode()){
           for(final SeekerInfo candidate: jobDetail.Candidates) {
               candidateNameTxt = new TextView(getBaseContext());
               candidateNameTxt.setText(candidate.FullName);
               candidateNameTxt.setTextColor(Color.BLACK);
               candidateNameTxt.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
               candidateNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
               candidateNameTxt.setPadding(15, 15, 15, 15);

               candidateNameTxt.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       showPopupCandidate(candidate.Id);
                   }
               });


               candidatesListLayout.addView(candidateNameTxt);
           }
       }
    }

    public void setSkills(Skill[] skills) {
        this.skills_array = skills;


        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, skills_array);
        expertiseField1.setAdapter(adapter);
        expertiseField2.setAdapter(adapter);
        expertiseField3.setAdapter(adapter);
    }

    public void setRegions(RegionModel[] regions) {
        this.regions_array = regions;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, regions_array);
        regionSpinner.setAdapter(adapter);
    }


    public void setSubRegions(RegionModel[] regions) {
        this.subRegions_array = regions;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, subRegions_array);
        subRegionSpinner.setAdapter(adapter);
    }

    public void setSkillsYearsExp(String[] skillsYearsExp) {
        this.skillsYearsExp = skillsYearsExp;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, skillsYearsExp);
        expertYears1.setAdapter(adapter);
        expertYears2.setAdapter(adapter);
        expertYears3.setAdapter(adapter);
    }

    public void setJobPositions(String[] jobPositions) {
        this.jobPositions = jobPositions;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, jobPositions);
        jobPosition.setAdapter(adapter);
    }

    /**
     * Shows the progress UI .
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProfileJobFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileJobFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileJobFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProfileJobFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

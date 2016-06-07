package com.jobSearchApp.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.jobSearchApp.android.Common.ServerHelper;
import com.jobSearchApp.android.ServiceAPI.CommonAPI;
import com.jobSearchApp.android.ServiceModels.AllCommonInfo;
import com.jobSearchApp.android.ServiceModels.LocationModel;
import com.jobSearchApp.android.ServiceModels.RegionModel;
import com.jobSearchApp.android.ServiceModels.Seeker;
import com.jobSearchApp.android.ServiceAPI.SeekerAPI;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;
import com.jobSearchApp.android.ServiceModels.Skill;
import com.jobSearchApp.android.ServiceModels.SkillExperience;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EmployeeProfile extends AppCompatActivity {
    private static final String TAG = "Log_place";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText fnameText, lnameText, yearBirth, fieldOfStudy, url;
    private Skill[] skills_array;
    private String[] salaryRanges;
    private String[] skillsYearsExp;

    private Skill emptySkill = new Skill(-1, "");
    private RegionModel emptySubRegion = new RegionModel("בחר תת-אזור");
    private Button saveBtn;
    private Spinner s1, s2, s3;
    private Spinner salarySpin;
    private Spinner s1_yearsExperience;
    private Spinner s2_yearsExperience;
    private Spinner s3_yearsExperience;
    private Spinner jobPositionSpinner;
    private Spinner degreeTypeSpinner;
    private Spinner regionSpinner;
    private Spinner subRegionSpinner;
    private Seeker seeker;
    RadioButton fullMatriculation, partialMatriculation;
    RadioButton immediateAvailable, noImmediateAvailable;
    RadioButton fullTimePos, partialTimePos;
    RadioButton mobile, notMobile;
    private View mProgressView;
    private View mProfileFormView;
    private PlaceAutocompleteFragment autocompleteFragmentSeeker;
    private RegionModel[] regions_array;
    private RegionModel[] subRegions_array;
    private String[] jobPositions;

    private boolean getSubRegionFromSeekerRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        fnameText = (EditText) findViewById(R.id.firstName);
        lnameText = (EditText) findViewById(R.id.lastName);
        yearBirth = (EditText) findViewById(R.id.yearBirth);
        fieldOfStudy = (EditText) findViewById(R.id.fieldOfStudy);
        url = (EditText) findViewById(R.id.url);

        s1 = (Spinner) findViewById(R.id.expert1);
        s2 = (Spinner) findViewById(R.id.expert2);
        s3 = (Spinner) findViewById(R.id.expert3);
        s1_yearsExperience = (Spinner) findViewById(R.id.expert1_years_exp);
        s2_yearsExperience = (Spinner) findViewById(R.id.expert2_years_exp);
        s3_yearsExperience = (Spinner) findViewById(R.id.expert3_years_exp);

        degreeTypeSpinner = (Spinner) findViewById(R.id.degree_type);
        jobPositionSpinner = (Spinner) findViewById(R.id.job_position_spinner);
        regionSpinner = (Spinner) findViewById(R.id.regions_spinner);
        subRegionSpinner = (Spinner) findViewById(R.id.sub_regions_spinner);
        salarySpin = (Spinner) findViewById(R.id.salarySpin);

        fullMatriculation = (RadioButton) findViewById(R.id.full_matriculation);
        partialMatriculation = (RadioButton) findViewById(R.id.partial_matriculation);
        immediateAvailable = (RadioButton) findViewById(R.id.immediate_available);
        noImmediateAvailable = (RadioButton) findViewById(R.id.no_immediate_available);
        fullTimePos = (RadioButton) findViewById(R.id.full_time_pos);
        partialTimePos = (RadioButton) findViewById(R.id.partial_time_pos);
        mobile = (RadioButton) findViewById(R.id.mobile);
        notMobile = (RadioButton) findViewById(R.id.no_mobile);

        saveBtn = (Button) findViewById(R.id.saveBtn);

        mProgressView = findViewById(R.id.progress_animation);//for progress bar
        mProfileFormView = findViewById(R.id.mProfileFormView);

        noImmediateAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Open calendar dialog*/
                final View dialogView = View.inflate(EmployeeProfile.this, R.layout.interview_set_date_time, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(EmployeeProfile.this).create();
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                timePicker.setVisibility(View.GONE);
/* Set date and after clicking the button, dismiss the calendar dialog*/
                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);


                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth());

                        setAvailableFrom(calendar.getTime());

                        alertDialog.dismiss();
                    }});
                alertDialog.setView(dialogView);
                alertDialog.show();

            }
        });
        autocompleteFragmentSeeker = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.seeker_place_autocomplete_fragment);


        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                regions_array[position].SubRegions.add(0, emptySubRegion);

                RegionModel[] subRegions_array = new RegionModel[regions_array[position].SubRegions.size()];
                regions_array[position].SubRegions.toArray(subRegions_array);
                setSubRegions(subRegions_array);

                regions_array[position].SubRegions.remove(0);

                if(getSubRegionFromSeekerRequired){

                    setSubRegionSpinnerByName(seeker.SeekArea.Region);
                    getSubRegionFromSeekerRequired=false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setAvailableFrom(Date time) {
        seeker.AvailableFrom = ServerHelper.ticksFromDate(time);
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        noImmediateAvailable.setText("זמינות חלקית, מתאריך:   " + df.format(time));
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*get saved details of user from server*/
        SeekerInfoTask task = new SeekerInfoTask();
        task.execute((Void) null);

        /*get input from user and save it on server */
        saveBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           if (!isValidFields())
                                               return;
                                           getDetailsFromSeeker();
                                           updateSeekerDetailsOnServer();
                                       }
                                   }
        );

        autocompleteFragmentSeeker.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.i(TAG, "Place: " + place.getAddress());
                if (seeker.HomeLocation == null)
                    seeker.HomeLocation = new LocationModel();

                seeker.HomeLocation.Address = place.getAddress().toString();

                if (place.getLatLng() != null) {
                    seeker.HomeLocation.GeoLatitude = place.getLatLng().latitude;
                    seeker.HomeLocation.GeoLongitude = place.getLatLng().longitude;
                }
            }

            @Override
            public void onError(Status status) {

                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }


    private boolean isValidFields() {
        try {
            int yearOfBirth = Integer.parseInt(yearBirth.getText().toString());
            int maxYear = Calendar.getInstance().get(Calendar.YEAR) - 10;
            int minYear = 1900;
            if (yearOfBirth < minYear)
                throw new Exception("Year of birth must be minimum " + minYear);
            if (yearOfBirth > maxYear)
                throw new Exception("Year of birth must be maximum " + maxYear);

        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void setSubRegionSpinnerByName(String regionName) {
        for (int i = 0; i < subRegions_array.length; i++) {
            if (subRegions_array[i].Name.equals(regionName)) {
                subRegionSpinner.setSelection(i);
                break;
            }
        }
    }

    private void setRegionSpinnerByName(String regionName) {
        for (int i = 0; i < regions_array.length; i++) {
            if (regions_array[i].Name.equals(regionName)) {
                regionSpinner.setSelection(i);
                break;
            }
        }
    }

    public void setSkills(Skill[] skills) {
        this.skills_array = skills;


        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, skills_array);
        s1.setAdapter(adapter);
        s2.setAdapter(adapter);
        s3.setAdapter(adapter);
    }

    public void setSkillsYearsExp(String[] skillsYearsExp) {
        this.skillsYearsExp = skillsYearsExp;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, skillsYearsExp);
        s1_yearsExperience.setAdapter(adapter);
        s2_yearsExperience.setAdapter(adapter);
        s3_yearsExperience.setAdapter(adapter);
    }

    public void setSalaryRanges(String[] salaryRanges) {
        this.salaryRanges = salaryRanges;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, salaryRanges);
        adapter.createFromResource(
                this, R.array.salaries, R.layout.spinner_layout);

        salarySpin.setAdapter(adapter);
    }

    public void setSeeker(Seeker seeker) {
        this.seeker = seeker;

        fnameText.setText(seeker.FirstName);
        lnameText.setText(seeker.LastName);
        fieldOfStudy.setText(seeker.FieldOfStudy);
        url.setText(seeker.URL);

        if(seeker.Degree != null)
            degreeTypeSpinner.setSelection(((ArrayAdapter)degreeTypeSpinner.getAdapter()).getPosition(seeker.Degree));
        seeker.FieldOfStudy = fieldOfStudy.getText().toString();

        if (seeker.SkillExperiences.size() > 0) {
            s1.setSelection(GetSkillPositionById(seeker.SkillExperiences.get(0).Skill.Id));
            s1_yearsExperience.setSelection(GetSkillYearsExpById(seeker.SkillExperiences.get(0).ExperienceRangeStr));
        }
        if (seeker.SkillExperiences.size() > 1) {
            s2.setSelection(GetSkillPositionById(seeker.SkillExperiences.get(1).Skill.Id));
            s2_yearsExperience.setSelection(GetSkillYearsExpById(seeker.SkillExperiences.get(1).ExperienceRangeStr));
        }
        if (seeker.SkillExperiences.size() > 2) {
            s3.setSelection(GetSkillPositionById(seeker.SkillExperiences.get(2).Skill.Id));
            s3_yearsExperience.setSelection(GetSkillYearsExpById(seeker.SkillExperiences.get(2).ExperienceRangeStr));
        }
        if (seeker.YearOfBirth > 0)
            yearBirth.setText(Integer.toString(seeker.YearOfBirth));

        if (seeker.SalaryRanges != null)
            salarySpin.setSelection(GetSalaryRangesPositionById(seeker.SalaryRanges));

        if(seeker.DesiredPosition != null)
            jobPositionSpinner.setSelection(GetJobPositionById(seeker.DesiredPosition));
        if (seeker.HomeLocation != null)
            autocompleteFragmentSeeker.setText(seeker.HomeLocation.Address);

        if (seeker.SeekArea != null) {
            for (int i = 0; i < regions_array.length; i++) {
                if (regions_array[i].Name.equals(seeker.SeekArea.Region)) {
                    setRegionSpinnerByName(regions_array[i].Name);
                    break;
                }

                for (int j = 0; j < regions_array[i].SubRegions.size(); j++) {
                    if (regions_array[i].SubRegions.get(j).Name.equals(seeker.SeekArea.Region)) {
                        setRegionSpinnerByName(regions_array[i].Name);
                        getSubRegionFromSeekerRequired = true;
                        //setSubRegionSpinnerByName(regions_array[i].SubRegions.get(j).Name);
                        break;
                    }
                }
            }
        }
        // autocompleteFragmentJob.setText(seeker.SeekArea.Region);

        if (seeker.Matriculation)
            fullMatriculation.setChecked(true);
        else
            partialMatriculation.setChecked(true);
        if (seeker.AvailableFrom == null)
            immediateAvailable.setChecked(true);
        else {
            noImmediateAvailable.setChecked(true);
           setAvailableFrom(ServerHelper.dateFromTicks(seeker.AvailableFrom));
        }

        if (seeker.FullTime)
            fullTimePos.setChecked(true);
        else
            partialTimePos.setChecked(true);
        if (seeker.IsMobile)
            mobile.setChecked(true);
        else
            notMobile.setChecked(true);
    }


    private int GetSkillPositionById(int id) {
        for (int i = 0; i < skills_array.length; i++) {
            if (skills_array[i].Id == id)
                return i;
        }
        return 0;
    }

    private int GetSkillYearsExpById(String skillYearExp) {
        for (int i = 0; i < skillsYearsExp.length; i++) {
            if (skillsYearsExp[i].equals(skillYearExp))
                return i;
        }
        return 0;
    }

    private int GetJobPositionById(String desiredPosition) {
        for(int i = 0; i < jobPositions.length; i++)
            if(jobPositions[i].equals(desiredPosition))
                return i;
        return 0;
    }

    private int GetSalaryRangesPositionById(String salary) {
        for (int i = 0; i < salaryRanges.length; i++) {
            if (salaryRanges[i].equals(salary))
                return i;
        }
        return 0;
    }

    public void getDetailsFromSeeker() {
        seeker.FirstName = fnameText.getText().toString();
        seeker.LastName = lnameText.getText().toString();
        seeker.YearOfBirth = Integer.parseInt(yearBirth.getText().toString());
        seeker.URL = url.getText().toString().length() == 0 ? null : url.getText().toString();

        seeker.Degree = degreeTypeSpinner.getSelectedItem().toString();
        seeker.FieldOfStudy = fieldOfStudy.getText().toString();

        seeker.SkillExperiences.clear();
        if ((Skill) s1.getSelectedItem() != emptySkill) {
            SkillExperience skillExperience = new SkillExperience();
            skillExperience.Skill = (Skill) s1.getSelectedItem();
            skillExperience.ExperienceRangeStr = (String) s1_yearsExperience.getSelectedItem();
            seeker.SkillExperiences.add(skillExperience);
        }
        if ((Skill) s2.getSelectedItem() != emptySkill) {
            SkillExperience skillExperience = new SkillExperience();
            skillExperience.Skill = (Skill) s2.getSelectedItem();
            skillExperience.ExperienceRangeStr = (String) s2_yearsExperience.getSelectedItem();
            seeker.SkillExperiences.add(skillExperience);
        }
        if ((Skill) s3.getSelectedItem() != emptySkill) {
            SkillExperience skillExperience = new SkillExperience();
            skillExperience.Skill = (Skill) s3.getSelectedItem();
            skillExperience.ExperienceRangeStr = (String) s3_yearsExperience.getSelectedItem();
            seeker.SkillExperiences.add(skillExperience);
        }

        if(seeker.SeekArea==null)
            seeker.SeekArea= new LocationModel();

        if ((RegionModel) subRegionSpinner.getSelectedItem() != emptySubRegion) {
            seeker.SeekArea.Region = ((RegionModel) subRegionSpinner.getSelectedItem()).Name;
        } else {
            seeker.SeekArea.Region = ((RegionModel) regionSpinner.getSelectedItem()).Name;
        }
        seeker.DesiredPosition = jobPositionSpinner.getSelectedItem().toString();
        seeker.SalaryRanges = (String) salarySpin.getSelectedItem();

        seeker.Matriculation = fullMatriculation.isChecked();
        //TODO: change the date according to seeker's input
        seeker.AvailableFrom = immediateAvailable.isChecked() ? null : ServerHelper.ticksFromDate(Calendar.getInstance().getTime());
        seeker.FullTime = fullTimePos.isChecked();
        seeker.IsMobile = mobile.isChecked();
    }

    private void updateSeekerDetailsOnServer() {

        SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
        Call<Void> call = seekerAPI.updateProfile(seeker);

        showProgress(true);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess())
                    finish();
                else
                    Toast.makeText(getBaseContext(), response.message(), Toast.LENGTH_LONG).show();

                showProgress(false);
            }

            @Override
            public void onFailure(Throwable t) {
                showProgress(false);
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

            mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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

    public void setJobPositions(String[] jobPositions) {
        this.jobPositions = jobPositions;

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, jobPositions);
        jobPositionSpinner.setAdapter(adapter);
    }

    private class SeekerInfoTask extends AsyncTask<Void, Void, Boolean> {

        public Seeker seeker;
        public AllCommonInfo commonInfo;
        public String errorMessage;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                CommonAPI commonApi = ServiceGenerator.createServiceWithAuth(CommonAPI.class);
                Call<AllCommonInfo> callcommonInfo = commonApi.getCommonInfo();

                Response<AllCommonInfo> response = callcommonInfo.execute();

                if (response.isSuccess()) {
                    commonInfo = response.body();
                } else {
                    errorMessage = response.message();
                    return false;
                }
                commonInfo.Skills.add(0, emptySkill);

                SeekerAPI seekerAPI = ServiceGenerator.createServiceWithAuth(SeekerAPI.class);
                Call<Seeker> callGetSeeker = seekerAPI.getProfile();
                Response<Seeker> seekerResponse = callGetSeeker.execute();

                if (seekerResponse.isSuccess()) {
                    seeker = seekerResponse.body();

                } else {
                    errorMessage = seekerResponse.message();
                    return false;
                }

            } catch (IOException e) {
                errorMessage = e.getMessage();
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            EmployeeProfile.this.showProgress(true);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

                Skill[] skills_array = new Skill[commonInfo.Skills.size()];
                commonInfo.Skills.toArray(skills_array);

                String[] jobPostions_array = new String[commonInfo.JobPositions.size()];
                commonInfo.JobPositions.toArray(jobPostions_array);

                String[] salaryRanges_array = new String[commonInfo.SalaryRanges.size()];
                commonInfo.SalaryRanges.toArray(salaryRanges_array);

                String[] skillYearsExp_array = new String[commonInfo.YearsOfExperience.size()];
                commonInfo.YearsOfExperience.toArray(skillYearsExp_array);

                RegionModel[] regions_array = new RegionModel[commonInfo.Regions.size()];
                commonInfo.Regions.toArray(regions_array);


                EmployeeProfile.this.setSkills(skills_array);
                EmployeeProfile.this.setJobPositions(jobPostions_array);
                EmployeeProfile.this.setSalaryRanges(salaryRanges_array);
                EmployeeProfile.this.setSkillsYearsExp(skillYearsExp_array);
                EmployeeProfile.this.setRegions(regions_array);
                EmployeeProfile.this.setSeeker(seeker);
            } else {
                Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();

                EmployeeProfile.this.finish();
            }

            EmployeeProfile.this.showProgress(false);
        }
    }


}
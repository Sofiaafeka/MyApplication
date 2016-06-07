package com.jobSearchApp.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.jobSearchApp.android.ServiceAPI.AccountAPI;
import com.jobSearchApp.android.ServiceModels.JsaUserType;
import com.jobSearchApp.android.ServiceModels.RegisterModel;
import com.jobSearchApp.android.ServiceModels.ServiceGenerator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegistrationActivity extends AppCompatActivity {
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button regBtn;
    RadioButton radioSeeker, radioEmployer;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mProgressView = findViewById(R.id.progress_animation);
        mRegisterFormView = findViewById(R.id.register_form);

        radioSeeker = (RadioButton) findViewById(R.id.radio_seeker);
        radioEmployer = (RadioButton) findViewById(R.id.radio_emp);
        mEmailView = (EditText) findViewById(R.id.reg_email);
        mPasswordView = (EditText) findViewById(R.id.reg_password);
        regBtn = (Button) findViewById(R.id.btnRegister);

        /* register user on server*/
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = null, password = null;
                email = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                RegisterModel newUser = new RegisterModel();
                if (!email.isEmpty() && !password.isEmpty()) {
                    newUser.Email = email;
                    newUser.Password = password;
                    newUser.ConfirmPassword = password;
                    if (radioSeeker.isChecked()) {// if seeker- create seeker newUser
                        newUser.AccountType = JsaUserType.SEEKER.getValue();


                    } else if (radioEmployer.isChecked()) {// if employer- create employer newUser
                        newUser.AccountType = JsaUserType.EMPLOYER.getValue();
                    }
                    //send details of new user to server
                    showProgress(true);
                    AccountAPI api = ServiceGenerator.createService(AccountAPI.class);
                    Call<Void> call = api.register(newUser);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Response response, Retrofit retrofit) {
                            showProgress(false);

                            if (response.isSuccess()) {
                                finish();
                                Toast.makeText(getBaseContext(), "Registered successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showProgress(false);
                            Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

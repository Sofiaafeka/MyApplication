package com.jobSearchApp.android.ServiceModels;

import android.content.Context;
import android.util.Log;

import com.jobSearchApp.android.AppSharedData;
import com.jobSearchApp.android.R;
import com.jobSearchApp.android.ServiceAPI.AccountAPI;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class GcmTokenUpdater {

    public static void UpdateTokenOnServer(final Context context) {
        ServiceGenerator.Initialize(context.getResources().getString(R.string.service_url));

        String gcm_token = AppSharedData.getSavedRegistrationToken(context);
        if (gcm_token != null && ServiceGenerator.isLoginResponseSet()) {

            // Call Service update token on server
            AccountAPI accountAPI = ServiceGenerator.createServiceWithAuth(AccountAPI.class);
            accountAPI.updateGcmToken(gcm_token).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess())
                        AppSharedData.clearRegistrationToken(context);
                    else
                        Log.d("Server response", response.message());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Failure", "server response failure: " + t.getMessage());
                }
            });
        }
    }
}

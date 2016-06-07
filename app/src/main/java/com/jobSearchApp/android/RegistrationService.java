package com.jobSearchApp.android;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jobSearchApp.android.ServiceModels.GcmTokenUpdater;

import java.io.IOException;

public class RegistrationService extends IntentService {
    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Registration Token","Getting instance ID");
        /*Instance ID API to generate or fetch the registration token*/
        InstanceID myID = InstanceID.getInstance(this);
        try {
            String registrationToken = myID.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null        );
            Log.d("Registration Token", registrationToken);//contents of the registration token for debugging purposes

            AppSharedData.setRegistrationToken(getBaseContext(), registrationToken);

            GcmTokenUpdater.UpdateTokenOnServer(getBaseContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.jobSearchApp.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jobSearchApp.android.ServiceModels.LoginResponse;

/**
 * Created by סופי on 04/05/2016.
 */
public class AppSharedData {

    private static final String ACC_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type"; // for user type
    private static final String GCM_REG_TOKEN = "gcm_reg_token"; // for gcm

    public static LoginResponse getSavedLoginResponse(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String acc_token = settings.getString(ACC_TOKEN, null);//if not exists token -return null by default
        String token_type = settings.getString(TOKEN_TYPE, null);

        if (acc_token != null) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.access_token = acc_token;
            loginResponse.token_type = token_type;
            return loginResponse;
        }

        return null;
    }

    public static void setLoginResponse(Context context, LoginResponse loginResponse){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ACC_TOKEN, loginResponse.access_token);
        editor.putString(TOKEN_TYPE, loginResponse.token_type);
        editor.commit();// saves the token
    }
/*Saving registration token of push notifications*/
    public static void setRegistrationToken(Context context, String registrationToken){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(GCM_REG_TOKEN, registrationToken);
        editor.commit();// saves the token
    }

    public static String getSavedRegistrationToken(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String reg_token = settings.getString(GCM_REG_TOKEN, null);//if not exists token -return null by default

        return reg_token;
    }

    public static void clearRegistrationToken(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(GCM_REG_TOKEN);
        editor.commit();// saves the token
    }

    public static void clearLogin(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(ACC_TOKEN);
        editor.remove(TOKEN_TYPE);
        editor.commit();// saves the token
    }
}

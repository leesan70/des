package com.sblee.des.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by minsoo on 2017. 11. 7..
 */

public class UserPref {

    private SharedPreferences pref;

    public UserPref(Context context){
        pref = context.getSharedPreferences("DES", Context.MODE_PRIVATE);
    }

    public void setUserData(String facebookId, String facebookToken) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("facebook_id", facebookId);
        editor.putString("facebook_token", facebookToken);
        editor.putBoolean("logged_in", true);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean("logged_in", false);
    }

    public String getFacebookId() {
        return pref.getString("facebook_id", "");
    }

    public String getGender() {
        return pref.getString("gender", "");
    }

    public void setGender(String gender){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gender", gender);
        editor.apply();
    }
}

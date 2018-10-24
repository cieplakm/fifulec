package com.mmc.fifulec.utils;

import android.content.SharedPreferences;

import javax.inject.Inject;

import com.mmc.fifulec.di.AppScope;

@AppScope
public class Preferences {

    private static final String NICK_KEY = "com.mmc.fifulec.NICK";
    private static final String NOTIFICATION_ACTIVE = "com.mmc.fifulec.NOTIFICATIONS";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    public Preferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.sharedPreferencesEditor = sharedPreferences.edit();
    }

    public void putPassword(String pass){

    }

    public void putNick(String nick){
        sharedPreferencesEditor.putString(NICK_KEY, nick).apply();
    }

    public String getNick(){
        return sharedPreferences.getString(NICK_KEY, null);
    }

    public void putNotificationActive(boolean isOn) {
        sharedPreferencesEditor.putBoolean(NOTIFICATION_ACTIVE, isOn).apply();
    }

    public boolean isNotificationActive(){
        return sharedPreferences.getBoolean(NOTIFICATION_ACTIVE, false);
    }
}

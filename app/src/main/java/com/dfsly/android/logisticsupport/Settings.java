package com.dfsly.android.logisticsupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

public class Settings {
    private static SharedPreferences sSettingsPre;
    private static Context sContext;
    private static final String TAG = Settings.class.getSimpleName();


    public static void initialize(Context context) {
        sContext = context.getApplicationContext();
        sSettingsPre = PreferenceManager.getDefaultSharedPreferences(sContext);
    }

//    public static String getStringArray(String key,String defValue){
//        return sSettingsPre.getString(key,defValue);
//    }
    public static void putBoolean(String key, Boolean value) {
        sSettingsPre.edit().putBoolean(key, value).apply();
    }
    public static void putInt(String key, int value) {
        sSettingsPre.edit().putInt(key, value).apply();
    }
    public static int getInt(String key, int defValue) {
        try {
            return sSettingsPre.getInt(key, defValue);
        } catch (ClassCastException e) {
            Log.d(TAG, "Get ClassCastException when get " + key + " value", e);
            return defValue;
        }
    }

    public static Boolean getBoolean(String key, Boolean defValue) {
        try {
            return sSettingsPre.getBoolean(key, defValue);
        } catch (ClassCastException e) {
            Log.d(TAG, "Get ClassCastException when get " + key + " value", e);
            return defValue;
        }
    }

    public static Map<String,?> getAll(){
        return sSettingsPre.getAll();
    }
}

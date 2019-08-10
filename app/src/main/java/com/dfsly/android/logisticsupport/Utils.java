package com.dfsly.android.logisticsupport;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Calendar;

public class Utils {
    public static String getTextTime(int h, int m){
        String timeText;
        if(h<10){
            timeText = "0"+h;
        }else {
            timeText = h+"";
        }
        if(m<10){
            timeText = timeText+":0"+m;
        }else {
            timeText = timeText+":"+m;
        }
        return timeText;
    }


    public static long getMillis(int h,int m){
        return (h * 60 + m) * 60000;
    }

    public static boolean isServiceStart(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.dfsly.android.logisticsupport.LogisticService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getVersionName(Context context){
        try{
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        }catch (PackageManager.NameNotFoundException e){
            return "unknown";
        }
    }

    static void saveCurrentDate(){
        long l = System.currentTimeMillis();
        Settings.putLoog("saveTime",l);
    }

    static void saveCurrentTime(int index){
        long l = System.currentTimeMillis();
        Settings.putLoog("saveTime"+index,l);
    }

    static long getSubtract(){
        long l = System.currentTimeMillis()-Settings.getLong("saveTime",(long)0);
        if(l>24*60*60*1000){
            return -1;
        }else{
            return l;
        }
    }

    static long SubtractSystemTime(int index){
        long l = System.currentTimeMillis()-Settings.getLong("saveTime",(long)0);
        if(l>24*60*60*1000){
            return -1;
        }else{
            return l;
        }
    }

}

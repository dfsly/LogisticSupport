package com.dfsly.android.logisticsupport;

import android.app.ActivityManager;
import android.content.Context;

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
}

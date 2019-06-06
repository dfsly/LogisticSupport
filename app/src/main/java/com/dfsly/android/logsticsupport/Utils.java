package com.dfsly.android.logsticsupport;

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
}

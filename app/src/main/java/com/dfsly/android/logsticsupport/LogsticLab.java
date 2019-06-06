package com.dfsly.android.logsticsupport;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class LogsticLab {
    private static LogsticLab sLogsticLab;
    private List<Logstic> mLogstics;
    private LogsticLab(Context context) {
        mLogstics = new ItemsFromJson().getJsonData(context);
    }

    public static LogsticLab get(Context context) {
        if (sLogsticLab == null) {
            sLogsticLab = new LogsticLab(context);
        }
        return sLogsticLab;
    }

    public List<Logstic> getLogstics() {
        return mLogstics;
    }

}

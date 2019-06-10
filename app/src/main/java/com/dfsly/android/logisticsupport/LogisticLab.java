package com.dfsly.android.logisticsupport;

import android.content.Context;

import java.util.List;

public class LogisticLab {
    private static LogisticLab sLogisticLab;
    private List<Logistic> mLogistics;
    private LogisticLab(Context context) {
        mLogistics = new ItemsFromJson().getJsonData(context);
    }

    public static LogisticLab get(Context context) {
        if (sLogisticLab == null) {
            sLogisticLab = new LogisticLab(context);
        }
        return sLogisticLab;
    }

    public List<Logistic> getLogistics() {
        return mLogistics;
    }

}

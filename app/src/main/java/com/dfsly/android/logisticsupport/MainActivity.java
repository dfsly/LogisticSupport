package com.dfsly.android.logisticsupport;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView ivStarOval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        com.dfsly.android.logisticsupport.Settings.initialize(this);
        recyclerView = findViewById(R.id.logistic_data_recycler_view);
        ivStarOval = findViewById(R.id.oval_start);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LogisticLab logisticLab = LogisticLab.get(this);
        List<Logistic> logistics = logisticLab.getLogistics();
        recyclerView.setAdapter(new MyAdapter(this, logistics));
    }

    public void starOval(View view) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.dfsly.android.logisticsupport.LogisticService".equals(service.service.getClassName())) {
                stopLogisticService();
                return;
            }
        }
        startFloatingService();
    }

    public void startFloatingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }else {
                startLogisticService();
            }
        } else {
            startLogisticService();
        }
    }

    private void startLogisticService() {
        startService(LogisticService.newIntent(this));
        ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_blue));
    }

    private void stopLogisticService() {
        stopService(LogisticService.newIntent(this));
        ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_button));
    }

}

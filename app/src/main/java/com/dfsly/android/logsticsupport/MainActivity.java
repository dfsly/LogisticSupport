package com.dfsly.android.logsticsupport;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
        com.dfsly.android.logsticsupport.Settings.initialize(this);
        recyclerView = findViewById(R.id.logstic_data_recycler_view);
        ivStarOval = findViewById(R.id.oval_start);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LogsticLab logsticLab = LogsticLab.get(this);
        List<Logstic> logstics = logsticLab.getLogstics();
        recyclerView.setAdapter(new MyAdapter(this, logstics));

//        startService(new Intent(this,RocketService.class));

        TextView textView1 = new TextView(this);
    }

    public void starOval(View view) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.dfsly.android.logsticsupport.LogsticService".equals(service.service.getClassName())) {
                stopService(new Intent(this, LogsticService.class));
                ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_button));
                return;
            }
        }
        startFloatingService();
    }

    public void startRocket(View view) {
        startFloatingService();
//        startService(new Intent(this, RocketService.class));
//        finish();
    }

    public void stopService(View view) {
        stopService(new Intent(this, LogsticService.class));

    }

    public void startFloatingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }else {
                startService(new Intent(this, LogsticService.class));
                ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_blue));
            }
        } else {
            startService(new Intent(this, LogsticService.class));
            ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_blue));
        }
    }

//    private void init(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragmentById = fragmentManager.findFragmentById();
//    }
}

package com.dfsly.android.logisticsupport;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ImageView ivStarOval;
    DrawerLayout drawerLayout;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        com.dfsly.android.logisticsupport.Settings.initialize(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.logistic_data_recycler_view);
        ivStarOval = findViewById(R.id.oval_start);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LogisticLab logisticLab = LogisticLab.get(this);
        List<Logistic> logistics = logisticLab.getLogistics();
        myAdapter = new MyAdapter(this, logistics);
        recyclerView.setAdapter(myAdapter);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

//        //activity意外销毁重建时设置悬浮球颜色
//        if (savedInstanceState != null) {
//            if (savedInstanceState.getBoolean("ovalSwitch")) {
//                ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_blue));
//                ovalSwitch = true;
//            }
//        }

        //服务如果已经存在，绑定它，并设置悬浮球颜色
        if(Utils.isServiceStart(this)){
            bindService(LogisticService.newIntent(this),connection,BIND_AUTO_CREATE);
            ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_blue));
//            ovalSwitch = true;
        }
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("ovalSwitch", ovalSwitch);
//    }

    public void starOval(View view) {
        if(Utils.isServiceStart(this)){
            stopLogisticService();
        }else{
            startFloatingService();
        }
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if ("com.dfsly.android.logisticsupport.LogisticService".equals(service.service.getClassName())) {
//                stopLogisticService();
//                return;
//            }
//        }
//        startFloatingService();
    }

//    private boolean isServiceStart(){
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if ("com.dfsly.android.logisticsupport.LogisticService".equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    public void startFloatingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                startLogisticService();
            }
        } else {
            startLogisticService();
        }
    }
    LogisticService.LogisticServiceBinder logisticServiceBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logisticServiceBinder = (LogisticService.LogisticServiceBinder)service;
            myAdapter.setBinder(logisticServiceBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logisticServiceBinder=null;
        }
    };
//    boolean ovalSwitch = false;
    private void startLogisticService() {
        startService(LogisticService.newIntent(this));
        bindService(LogisticService.newIntent(this),connection,BIND_AUTO_CREATE);
        ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_blue));
//        ovalSwitch = true;
    }

    private void stopLogisticService() {
        unbindService(connection);
        stopService(LogisticService.newIntent(this));
        ivStarOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_button));
//        ovalSwitch = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            startActivity(SettingsActivity.newIntent(this));
        }
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}

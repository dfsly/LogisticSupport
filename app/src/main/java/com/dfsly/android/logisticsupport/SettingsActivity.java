package com.dfsly.android.logisticsupport;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    SwitchCompat checkBoxSwitchTopToast;
    SwitchCompat switchExplore;
//    EditText editTextDelayTopToast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //顶部通知的开关
        checkBoxSwitchTopToast = findViewById(R.id.switch_top_toast);
        switchExplore = findViewById(R.id.switch_explore);
        View NPView = View.inflate(this,R.layout.dialog_number_picker,null);
        final NumberPicker secondPicker = NPView.findViewById(R.id.second_picker);
        secondPicker.setMaxValue(20);
        secondPicker.setMinValue(1);
        secondPicker.setValue(5);
         AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        final AlertDialog alertDialog =
                builder.setTitle("顶部通知持续时间 单位/s")
                .setView(NPView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = secondPicker.getValue();
                        Settings.putInt("delay_time",i);
//                                System.out.println("输出数字"+i);
                        if(logisticServiceBinder!=null){
                            logisticServiceBinder.setToastDelay(i);
                        }
                        Toast.makeText(SettingsActivity.this,"设置成功："+i+"秒",Toast.LENGTH_SHORT).show();
                    }
                }).create();
        findViewById(R.id.but_save_delay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondPicker.setValue(Settings.getInt("delay_time",5));
                alertDialog.show();
            }
        });

        //开关
        checkBoxSwitchTopToast.setChecked(Settings.getBoolean("switch_toast",true));
        checkBoxSwitchTopToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkBoxSwitchTopToast.isChecked();
                Settings.putBoolean("switch_toast",isChecked);
                //更新服务
                if(logisticServiceBinder!=null){
                    logisticServiceBinder.setToastSwitch(isChecked);
                }
            }
        });
        switchExplore.setChecked(Settings.getBoolean("switch_explore",true));
        switchExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = switchExplore.isChecked();
                Settings.putBoolean("switch_explore",isChecked);
                //更新服务
//                if(logisticServiceBinder!=null){
//                    logisticServiceBinder.setToastSwitch(isChecked);
//                }
            }
        });

        findViewById(R.id.settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SeekBar changeOval = findViewById(R.id.change_oval);
        changeOval.setProgress(Settings.getInt("ovalSize",32)-32);
        changeOval.setOnSeekBarChangeListener(new MySeekBarChangeListener());
        //绑定服务
        bindServices();
    }

    class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
//            Toast.makeText(SettingsActivity.this,"seekBar.getProgress()="+seekBar.getProgress(),Toast.LENGTH_SHORT).show();
            if(logisticServiceBinder!=null){
                int sizeDip =seekBar.getProgress()+32;
                logisticServiceBinder.refreshOval(Utils.dip2px(SettingsActivity.this,sizeDip));
                //存本地
                Settings.putInt("ovalSize",sizeDip);
//                System.out.println("96"+Utils.dip2px(SettingsActivity.this,50));
            }
        }
    }



    public static Intent newIntent(Context context){
        return new Intent(context,SettingsActivity.class);
    }

    private void bindServices(){
        if(Utils.isServiceStart(this)){
            bindService(LogisticService.newIntent(this),connection,BIND_AUTO_CREATE);
        }
    }

    LogisticService.LogisticServiceBinder logisticServiceBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logisticServiceBinder = (LogisticService.LogisticServiceBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logisticServiceBinder=null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Utils.isServiceStart(this)){
            unbindService(connection);
        }
    }
}

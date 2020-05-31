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
    SwitchCompat switchExplore, switchVibrate, switchMedia;

    private static final int NOTICE = 1;
    private static final int VIBRATE = 2;
    private int dialogFlag;

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
        switchVibrate = findViewById(R.id.switch_vibrate);
        switchMedia = findViewById(R.id.switch_media);
        View NPView = View.inflate(this, R.layout.dialog_number_picker, null);
        final NumberPicker secondPicker = NPView.findViewById(R.id.second_picker);
        secondPicker.setMaxValue(20);
        secondPicker.setMinValue(1);
        secondPicker.setValue(5);
        final AlertDialog alertDialog = new AlertDialog
                .Builder(SettingsActivity.this)
                .setTitle("持续时间(秒)")
                .setView(NPView)
                .setPositiveButton("确定", (dialog, which) -> {
                    int i = secondPicker.getValue();
                    switch (dialogFlag) {
                        case NOTICE:
                            Settings.putInt("delay_time", i);

                            break;
                        case VIBRATE:
                            Settings.putInt("vibrate_duration", i);
                            break;
                    }
                    if (logisticServiceBinder != null) {
                        logisticServiceBinder.resetSwitch();
                    }
                    Toast.makeText(SettingsActivity.this, "设置成功：" + i + "秒", Toast.LENGTH_SHORT).show();
                }).create();
        findViewById(R.id.but_save_delay).setOnClickListener(v -> {
            dialogFlag = NOTICE;
            secondPicker.setValue(Settings.getInt("delay_time", 5));
            alertDialog.show();
        });

        findViewById(R.id.btn_vibrate_duration).setOnClickListener(v -> {
            dialogFlag = VIBRATE;
            secondPicker.setValue(Settings.getInt("vibrate_duration", 2));
            alertDialog.show();
        });

        //开关
        checkBoxSwitchTopToast.setChecked(Settings.getBoolean("switch_toast", true));
        checkBoxSwitchTopToast.setOnClickListener(v -> {
            boolean isChecked = checkBoxSwitchTopToast.isChecked();
            Settings.putBoolean("switch_toast", isChecked);
            //更新服务
            if (logisticServiceBinder != null) {
                logisticServiceBinder.resetSwitch();
            }
        });
        switchExplore.setChecked(Settings.getBoolean("switch_explore", true));
        switchExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = switchExplore.isChecked();
                Settings.putBoolean("switch_explore", isChecked);
                if (logisticServiceBinder != null) {
                    logisticServiceBinder.resetSwitch();
                }
                //FIXME 悬浮球开启状态下，移除或添加探索相关控件
            }
        });

        findViewById(R.id.settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SeekBar changeOval = findViewById(R.id.change_oval);
        changeOval.setProgress(Settings.getInt("ovalSize", 32) - 32);
        changeOval.setOnSeekBarChangeListener(new MySeekBarChangeListener());

        switchVibrate.setChecked(Settings.getBoolean("switch_vibrate", true));
        switchVibrate.setOnClickListener(v -> {
            Settings.putBoolean("switch_vibrate", switchVibrate.isChecked());
            if (logisticServiceBinder != null) {
                logisticServiceBinder.resetSwitch();
            }
        });
        switchMedia.setChecked(Settings.getBoolean("switch_media", true));
        switchMedia.setOnClickListener(v -> {
            Settings.putBoolean("switch_media", switchMedia.isChecked());
            if (logisticServiceBinder != null) {
                logisticServiceBinder.resetSwitch();
            }
        });
        //绑定服务
        bindServices();
    }

    class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
//            Toast.makeText(SettingsActivity.this,"seekBar.getProgress()="+seekBar.getProgress(),Toast.LENGTH_SHORT).show();
            if (logisticServiceBinder != null) {
                int sizeDip = seekBar.getProgress() + 32;
                logisticServiceBinder.refreshOval(Utils.dip2px(SettingsActivity.this, sizeDip));
                //存本地
                Settings.putInt("ovalSize", sizeDip);
//                System.out.println("96"+Utils.dip2px(SettingsActivity.this,50));
            }
        }
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    private void bindServices() {
        if (Utils.isServiceStart(this)) {
            bindService(LogisticService.newIntent(this), connection, BIND_AUTO_CREATE);
        }
    }

    LogisticService.LogisticServiceBinder logisticServiceBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logisticServiceBinder = (LogisticService.LogisticServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logisticServiceBinder = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Utils.isServiceStart(this)) {
            unbindService(connection);
        }
    }

    private void secondPickerDialog() {

    }
}

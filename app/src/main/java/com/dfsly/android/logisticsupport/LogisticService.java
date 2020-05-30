package com.dfsly.android.logisticsupport;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;


public class LogisticService extends Service implements View.OnTouchListener {
    TimePicker timePicker;

    Boolean isShowToast;
    Boolean isShowExplore;
    int delayTime;
    int[] keys = new int[5];
    //悬浮球
    ImageView imageOval;
    int led_red = 1;
    int led_gre = 2;
    int led_whi = 0;
    int[] led_state = {led_whi, led_whi, led_whi, led_whi,led_whi};
    //倒计时视图
    CardView[] cardViews = new CardView[5];
    TextView[] editTimes = new TextView[5];
    TextView[] restarts = new TextView[5];
    TextView[] nameDownTimeLayout = new TextView[5];
    //修改时间视图
    LinearLayout cdWindowEditTime;
    WindowManager.LayoutParams editTimeLayoutParams;
    TextView nameEditTimeLayout;
    //    EditText hourEditTimeLayout;
//    EditText minuteEditTimeLayout;
    //toast
    View[] llWindowToasts = new View[5];
    WindowManager.LayoutParams toastLayoutParams;

    WindowManager windowManager;
    Button mButtonShrink;
    MyCountDownTimer[] myCountDownTimers = new MyCountDownTimer[5];

    LinearLayout llWindowDownTime;
    View ivWindowOval;
    //后勤列表
    private LinearLayout llWindowSelectLogistic;
    private LinearLayout llWindowSelectExplore;
    LinearLayout llListLogistic;

    WindowManager.LayoutParams defaultLayoutParams;
    LinearLayout llWindowChild;

    List<Logistic> logistics;
    TextView childNo;
    TextView childTime;
    View layoutChild;
    //用于记录所点击的当前界面的位置
    int index;
    int winHeight;
    int winWidth;

    public static Intent newIntent(Context context) {
        return new Intent(context, LogisticService.class);
    }

    class LogisticServiceBinder extends Binder {
        public void refreshLogisticList() {
            logistics = LogisticLab.get(getApplicationContext()).getLogistics();
//            windowManager.removeView(llWindowSelectLogistic);
            llListLogistic.removeAllViews();
            addItem(llListLogistic);
            windowManager.updateViewLayout(llWindowSelectLogistic, defaultLayoutParams);
//            windowManager.addView(llWindowSelectLogistic,defaultLayoutParams);
        }

        public void refreshOval(int size) {
            //设置悬浮球大小
            ViewGroup.LayoutParams p = imageOval.getLayoutParams();
            p.height=size;
            p.width=size;
            imageOval.setLayoutParams(p);
            windowManager.updateViewLayout(ivWindowOval, defaultLayoutParams);
        }

        public void setToastSwitch(boolean b) {
            isShowToast = b;
        }

        public void setToastDelay(int sec) {
            delayTime = sec * 1000;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LogisticServiceBinder();
//        return null;
    }

    void refreshOvalColor() {
        imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_button));
        for (int i = 0; i < 5; i++) {
            if (led_state[i] == led_red) {
                imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
                break;
            } else if (led_state[i] == led_gre) {
                imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_green));
            }
        }
    }

    private void setOvalSize(ImageView oval){
        ViewGroup.LayoutParams p = oval.getLayoutParams();
        int size = Utils.dip2px(this,Settings.getInt("ovalSize",32));
        p.height=size;
        p.width=size;
        oval.setLayoutParams(p);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isShowToast = Settings.getBoolean("switch_toast", true);
        isShowExplore = Settings.getBoolean("switch_explore", true);
        delayTime = Settings.getInt("delay_time", 5) * 1000;
        logistics = LogisticLab.get(getApplicationContext()).getLogistics();

        llWindowDownTime = (LinearLayout) View.inflate(this, R.layout.service_logistic, null);
        ivWindowOval = View.inflate(this, R.layout.window_oval, null);
        imageOval = ivWindowOval.findViewById(R.id.image_oval);
        //设置悬浮球大小
        setOvalSize(imageOval);

        ViewGroup.LayoutParams p = imageOval.getLayoutParams();
        System.out.println("宽"+p.width+"高"+p.height);

        ivWindowOval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivWindowOval.setVisibility(View.GONE);
                llWindowDownTime.setVisibility(View.VISIBLE);
            }
        });
        mButtonShrink = llWindowDownTime.findViewById(R.id.shrink);
        mButtonShrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivWindowOval.setVisibility(View.VISIBLE);
                llWindowDownTime.setVisibility(View.GONE);
                refreshOvalColor();
            }
        });
        llWindowDownTime.findViewById(R.id.down_time_layout_alpha).getBackground().setAlpha(50);
        cardViews[0] = llWindowDownTime.findViewById(R.id.logistic1);
        cardViews[1] = llWindowDownTime.findViewById(R.id.logistic2);
        cardViews[2] = llWindowDownTime.findViewById(R.id.logistic3);
        cardViews[3] = llWindowDownTime.findViewById(R.id.logistic4);
        cardViews[4] = llWindowDownTime.findViewById(R.id.explore);
        //判断探索功能是否开启
        if(!isShowExplore){
            cardViews[4].setVisibility(View.GONE);
        }
        //设置编辑时间按钮的点击监听
        for (int i = 0; i < 5; i++) {
            editTimes[i] = cardViews[i].findViewById(R.id.edit_time);
            editTimes[i].setOnClickListener(new StartETLClcikListener(i));
            restarts[i] = cardViews[i].findViewById(R.id.restart);
            restarts[i].setOnClickListener(new restartClickListener(i));
        }

        //跳转到后勤选择界面
        for (int i = 0; i < 4; i++) {
            cardViews[i].setOnClickListener(new StartSelectLogisticLayoutClickListener(i));
            nameDownTimeLayout[i] = cardViews[i].findViewById(R.id.id_logistic);
//            nameDownTimeLayout[i].setText("后勤" + (i + 1) + ":");
        }
        //跳转到探索选择界面
        cardViews[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index=4;
                llWindowDownTime.setVisibility(View.GONE);
                llWindowSelectExplore.setVisibility(View.VISIBLE);
            }
        });
        nameDownTimeLayout[4] = cardViews[4].findViewById(R.id.id_logistic);
        nameDownTimeLayout[4].setText("探索");

        windowManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        winHeight = windowManager.getDefaultDisplay().getHeight();
        winWidth = windowManager.getDefaultDisplay().getWidth();
        initDefaultLayoutParams();
        initEditTimeLayoutParams();

        initSelectLayout();
        initSelectExploreLayout();

        initEditTimeLayout();
        initToastLayoutParams(getResources().getConfiguration().orientation);
        llWindowDownTime.setVisibility(View.GONE);
        cdWindowEditTime.setVisibility(View.GONE);
        windowManager.addView(ivWindowOval, defaultLayoutParams);
        windowManager.addView(llWindowDownTime, defaultLayoutParams);
        windowManager.addView(llWindowSelectLogistic, defaultLayoutParams);
        windowManager.addView(cdWindowEditTime, editTimeLayoutParams);
        ivWindowOval.setOnTouchListener(this);

        //恢复意外中断的倒计时
        long l = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            //获取先前启动定时器时系统的时间
            long saveTime = Settings.getLong("saveTime" + i, (long) -1);
            //获取先前启动定时器时输入的时间
            long logisticTime = Settings.getLong("cdt"+i,(long)-1);

//            System.out.println("fuuusaveTime" + saveTime);
            if (saveTime != -1&&logisticTime!=-1) {
                long t = l - saveTime;
//                System.out.println("fuuulogisticTime" + logisticTime);
                long restartTime = logisticTime - t;
                if (restartTime <= 0) {
                    restartTime = 1;
                }
                //获取先前启动定时器时所选后勤在logistics中的下标
                //对keys的赋值需要在启动定时器之前
                keys[i] = Settings.getInt("cdn" + i, 0);

                myCountDownTimers[i] = new MyCountDownTimer(restartTime, 1000, i);
                myCountDownTimers[i].start();
                cardViews[i].setCardBackgroundColor(getResources().getColor(R.color.light_green_600));
                led_state[i] = led_gre;

//                System.out.println("fuuulogisticNo" +  Settings.getInt("cdn" + i, 0));
                if(i<=3){
                    Logistic logistic = logistics.get(keys[i]);
//                    TextView tv = cardViews[i].findViewById(R.id.id_logistic);
//                    tv.setText(logistic.getNo());
                    nameDownTimeLayout[i].setTextSize(16);
                    nameDownTimeLayout[i].setText(logistic.getNo());
                }
            }
        }
        refreshOvalColor();
    }

    int starY;
    int starX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                starY = (int) event.getRawY();
                starX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                int endY = (int) event.getRawY();
                int dy = endY - starY;
                int endX = (int) event.getRawX();
                int dx = endX - starX;

                //更新view位置
                defaultLayoutParams.y += dy;
                if (defaultLayoutParams.y > winHeight - ivWindowOval.getHeight())
                    defaultLayoutParams.y = winHeight - ivWindowOval.getHeight();
                if (defaultLayoutParams.y < 0) defaultLayoutParams.y = 0;

                defaultLayoutParams.x -= dx;
                if (defaultLayoutParams.x > winWidth - ivWindowOval.getWidth())
                    defaultLayoutParams.x = winWidth - ivWindowOval.getWidth();
                if (defaultLayoutParams.x < 0) defaultLayoutParams.x = 0;

                windowManager.updateViewLayout(ivWindowOval, defaultLayoutParams);

                starY = (int) event.getRawY();
                starX = (int) event.getRawX();

                break;
            case MotionEvent.ACTION_UP:
                if (defaultLayoutParams.x > (winWidth / 2 - ivWindowOval.getWidth() / 2)) {
                    defaultLayoutParams.x = winWidth - ivWindowOval.getWidth();
                } else {
                    defaultLayoutParams.x = 0;
                }
                windowManager.updateViewLayout(ivWindowOval, defaultLayoutParams);
                windowManager.updateViewLayout(llWindowDownTime, defaultLayoutParams);
                windowManager.updateViewLayout(llWindowSelectLogistic, defaultLayoutParams);
                break;
        }
        return false;
    }

    class StartETLClcikListener implements View.OnClickListener {
        int i;

        public StartETLClcikListener(int i) {
            this.i = i;
        }

        @Override
        public void onClick(View v) {
            index = i;
            if(index<=3) nameEditTimeLayout.setText("后勤  "+nameDownTimeLayout[i].getText());
            else if(index==4)nameEditTimeLayout.setText(nameDownTimeLayout[i].getText());
            //拿到倒计时当前的时间，并赋值给timePicker
            TextView textView = cardViews[i].findViewById(R.id.time_logistic);
            String s = textView.getText().toString();
            String[] arr = s.split(":", 3);
            int h = Integer.parseInt(arr[0]);
            int m = Integer.parseInt(arr[1]);
            gotoEditTimeLFromDownTimeL(h, m);
        }
    }

    class restartClickListener implements View.OnClickListener {
        int i;

        public restartClickListener(int i) {
            this.i = i;
        }

        @Override
        public void onClick(View v) {
            myCountDownTimers[i].cancel();
            long millis=0;
            if(i<=3){
                Logistic l = logistics.get(keys[i]);
                millis = Utils.getMillis(l.getH(), l.getM());
            }else if(i==4){
                millis = Utils.getMillis(keys[i]-1000,0);
            }
            myCountDownTimers[i] = new MyCountDownTimer(millis, 1000, i);
            myCountDownTimers[i].start();
            cardViews[i].setCardBackgroundColor(getResources().getColor(R.color.light_green_600));
            led_state[i] = led_gre;
            restarts[i].setVisibility(View.GONE);
        }
    }

    class StartSelectLogisticLayoutClickListener implements View.OnClickListener {
        int i;

        public StartSelectLogisticLayoutClickListener(int i) {
            this.i = i;
        }

        @Override
        public void onClick(View v) {
            index = i;
            showSaveLogisticList();
        }
    }

    private void initEditTimeLayout() {
        cdWindowEditTime = (LinearLayout) View.inflate(this, R.layout.window_edit_time, null);
        timePicker = cdWindowEditTime.findViewById(R.id.dialog_time_picker);
        timePicker.setIs24HourView(true);
        nameEditTimeLayout = cdWindowEditTime.findViewById(R.id.edit_logistic_name);

//        hourEditTimeLayout = cdWindowEditTime.findViewById(R.id.hour_edit_time_layout);
//        minuteEditTimeLayout = cdWindowEditTime.findViewById(R.id.minute_edit_time_layout);

        cdWindowEditTime.findViewById(R.id.ed_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDownTimeLFromEditTimeL();
            }
        });
        cdWindowEditTime.findViewById(R.id.cv_no_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拦截cardView的点击事件
                return;
            }
        });
        cdWindowEditTime.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h, m;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    h = timePicker.getHour();
                    m = timePicker.getMinute();
                } else {
                    h = timePicker.getCurrentHour();
                    m = timePicker.getCurrentMinute();
                }
                myCountDownTimers[index].cancel();
                myCountDownTimers[index] = new MyCountDownTimer(Utils.getMillis(h, m), 1000, index);
                myCountDownTimers[index].start();
                gotoDownTimeLFromEditTimeL();
//                String text = hourEditTimeLayout.getText().toString();
//                if (text.equals("")) {
//                    h = 0;
//                } else {
//                    h = Integer.parseInt(text);
//                }
//                text = minuteEditTimeLayout.getText().toString();
//                if (text.equals("")) {
//                    m = 0;
//                } else {
//                    m = Integer.parseInt(text);
//                }
//                myCountDownTimers[index].cancel();
//                myCountDownTimers[index] = new MyCountDownTimer(Utils.getMillis(h, m), 1000, index);
//                myCountDownTimers[index].start();
//                gotoDownTimeLFromEditTimeL();
            }
        });
    }

    private void gotoDownTimeLFromEditTimeL() {
        cdWindowEditTime.setVisibility(View.GONE);
        llWindowDownTime.setVisibility(View.VISIBLE);
    }

    private void gotoEditTimeLFromDownTimeL(int h, int m) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(h);
            timePicker.setMinute(m);
        } else {
            timePicker.setCurrentHour(h);
            timePicker.setCurrentMinute(m);
        }
        cdWindowEditTime.setVisibility(View.VISIBLE);
        llWindowDownTime.setVisibility(View.GONE);
    }

    private void initLayoutParams(WindowManager.LayoutParams layoutParams) {
        //透明背景
//        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
//        else {
        //MiUI 使用TYPE_TOAST会闪退
//            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        }
    }

    private void initEditTimeLayoutParams() {
        editTimeLayoutParams = new WindowManager.LayoutParams();
        initLayoutParams(editTimeLayoutParams);
        editTimeLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        editTimeLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        editTimeLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //获取焦点，返回键无效
        editTimeLayoutParams.gravity = Gravity.CENTER;
        editTimeLayoutParams.format = PixelFormat.TRANSLUCENT;
//        editTimeLayoutParams.alpha = 0.9f;
    }

    private void initDefaultLayoutParams() {
        defaultLayoutParams = new WindowManager.LayoutParams();
        initLayoutParams(defaultLayoutParams);
        defaultLayoutParams.format = PixelFormat.TRANSLUCENT;
        defaultLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        defaultLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        defaultLayoutParams.x = 5;
        defaultLayoutParams.y = (int) Math.floor(0.5 * winHeight);
    }

    private void initSelectLayout() {
        //添加布局
        llWindowSelectLogistic = (LinearLayout) View.inflate(this, R.layout.select_logistic, null);
        //拿到容器布局
//        llWindowSelectLogistic = llWindowSelectView.findViewById(R.id.ll_select_logistic);
        llWindowSelectLogistic.findViewById(R.id.select_list_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownTimeList();
            }
        });
        llWindowSelectLogistic.findViewById(R.id.sv_list_logistic).getBackground().setAlpha(50);
        llListLogistic = llWindowSelectLogistic.findViewById(R.id.ll_list_logistic);
        addItem(llListLogistic);
        //初始化完成后设置为不可见
        llWindowSelectLogistic.setVisibility(View.GONE);
    }

    private void initSelectExploreLayout() {
        //添加布局
        llWindowSelectExplore = (LinearLayout) View.inflate(this, R.layout.select_explore, null);
        //拿到容器布局
        llWindowSelectExplore.findViewById(R.id.select_list_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWindowSelectExplore.setVisibility(View.GONE);
                llWindowDownTime.setVisibility(View.VISIBLE);
            }
        });
        llWindowSelectExplore.findViewById(R.id.sv_list_logistic).getBackground().setAlpha(50);
        (llWindowSelectExplore.findViewById(R.id.explore_1)).setOnClickListener(new CheckItemOnClick(1003));
        (llWindowSelectExplore.findViewById(R.id.explore_2)).setOnClickListener(new CheckItemOnClick(1005));
        (llWindowSelectExplore.findViewById(R.id.explore_3)).setOnClickListener(new CheckItemOnClick(1008));
        (llWindowSelectExplore.findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消探索
                llWindowDownTime.setVisibility(View.VISIBLE);
                llWindowSelectExplore.setVisibility(View.GONE);
                restarts[index].setVisibility(View.GONE);
                editTimes[index].setVisibility(View.GONE);
                cardViews[index].findViewById(R.id.placeholder).setVisibility(View.VISIBLE);
                if (myCountDownTimers[index] != null) {
                    myCountDownTimers[index].cancel();
                }
                nameDownTimeLayout[index].setText("探索");
                TextView tv = (cardViews[index].findViewById(R.id.time_logistic));
                tv.setText("00:00:00");
                Settings.putInt("cdn" + index, -1);
                Settings.putLoog("cdt" + index, -1);
                cardViews[index].setCardBackgroundColor(getResources().getColor(R.color.grey_300));
                led_state[index] = led_whi;
            }
        });

        //初始化完成后设置为不可见
        llWindowSelectExplore.setVisibility(View.GONE);
        windowManager.addView(llWindowSelectExplore, defaultLayoutParams);
    }

    public void addItem(LinearLayout llListLogistic) {
        Logistic logistic;
        boolean remindAdd = true;
        //取消后勤
        layoutChild = LayoutInflater.from(this).inflate(R.layout.cancel_logistic, llWindowSelectLogistic, false);
        (layoutChild.findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWindowDownTime.setVisibility(View.VISIBLE);
                llWindowSelectLogistic.setVisibility(View.GONE);
                restarts[index].setVisibility(View.GONE);
                editTimes[index].setVisibility(View.GONE);
                cardViews[index].findViewById(R.id.placeholder).setVisibility(View.VISIBLE);
                if (myCountDownTimers[index] != null) {
                    myCountDownTimers[index].cancel();
                }
                nameDownTimeLayout[index].setText("后勤");
                nameDownTimeLayout[index].setTextSize(15);
               TextView tv = (cardViews[index].findViewById(R.id.time_logistic));
               tv.setText("00:00:00");
                Settings.putInt("cdn" + index, -1);
                Settings.putLoog("cdt" + index, -1);
                cardViews[index].setCardBackgroundColor(getResources().getColor(R.color.grey_300));
                led_state[index] = led_whi;
            }
        });
        llListLogistic.addView(layoutChild);

        //遍历全部后勤，将用户在主界面选择过的后勤添加到悬浮球后勤选择界面
        for (int i = 0; i < logistics.size(); i++) {
            logistic = logistics.get(i);
            if (logistic.isSave) {
                //返回加载的layout
                layoutChild = LayoutInflater.from(this).inflate(R.layout.select_logist_item, llListLogistic, false);

                //需要提供parent的layoutParams来约束select_logist_item生成的位置
//                layoutChild = View.inflate(this, R.layout.select_logist_item, null);
                llWindowChild = layoutChild.findViewById(R.id.window_child);
                childNo = layoutChild.findViewById(R.id.tv_no_child);
                childTime = layoutChild.findViewById(R.id.tv_time_child);

                childNo.setText(logistic.getNo());
                childTime.setText(Utils.getTextTime(logistic.getH(), logistic.getM()));

                //为容器布局添加子view
                llWindowChild.setOnClickListener(new CheckItemOnClick(logistic.getId()));
                llListLogistic.addView(layoutChild);
                remindAdd = false;
            }
        }

        if (remindAdd) {
            layoutChild = LayoutInflater.from(this).inflate(R.layout.window_remind, llWindowSelectLogistic, false);
            llListLogistic.addView(layoutChild);
//            Toast.makeText(this,"选择后勤后请重启悬浮球",Toast.LENGTH_LONG).show();
        }
    }

    class CheckItemOnClick implements View.OnClickListener {

        int key;

        public CheckItemOnClick(int key) {
            this.key = key;
        }

        @Override
        public void onClick(View v) {
            //点击列表后即时保存该位置对应的后勤/探索编号
            keys[index] = key;
            llWindowDownTime.setVisibility(View.VISIBLE);
            long millis=0;
            if(key<1000){//后勤
                llWindowSelectLogistic.setVisibility(View.GONE);
                Logistic l = logistics.get(key);
                millis = Utils.getMillis(l.getH(), l.getM());
//                TextView tv = cardViews[index].findViewById(R.id.id_logistic);
//                tv.setText(l.getNo());
                nameDownTimeLayout[index].setText(l.getNo());
                nameDownTimeLayout[index].setTextSize(16);
            }else if(key>1000&&key<2000){//探索
                llWindowSelectExplore.setVisibility(View.GONE);
                if(key==1003){
                    millis=Utils.getMillis(2, 0);
                }else if(key==1005){
                    millis=Utils.getMillis(5, 0);
                }else if(key==1008){
                    millis=Utils.getMillis(8, 0);
                }
            }
            if (myCountDownTimers[index] != null) {
                myCountDownTimers[index].cancel();
            }
            myCountDownTimers[index] = new MyCountDownTimer(millis, 1000, index);
            myCountDownTimers[index].start();
            cardViews[index].setCardBackgroundColor(getResources().getColor(R.color.light_green_600));
            led_state[index] = led_gre;

        }
    }


    private void showSaveLogisticList() {
        llWindowDownTime.setVisibility(View.GONE);
        llWindowSelectLogistic.setVisibility(View.VISIBLE);
    }

    private void showDownTimeList() {
        llWindowSelectLogistic.setVisibility(View.GONE);
        llWindowDownTime.setVisibility(View.VISIBLE);
    }


    public class MyCountDownTimer extends CountDownTimer {
        CardView cv;
        TextView tv_time;
        int ledIndex;
        long sSum;
        long h;
        long m;
        long s;
        long millis;

        public MyCountDownTimer(long millisInFuture, long countDownInterval, int index) {
            super(millisInFuture, countDownInterval);
            this.ledIndex = index;
            this.cv = cardViews[ledIndex];
            this.tv_time = cv.findViewById(R.id.time_logistic);
            restarts[ledIndex].setVisibility(View.GONE);
            editTimes[ledIndex].setVisibility(View.VISIBLE);

            cv.findViewById(R.id.placeholder).setVisibility(View.GONE);
            //启动倒计时的同时保存当前系统时间(在列表中对应的位置)
            Utils.saveCurrentTime(ledIndex);
            Settings.putInt("cdn" + ledIndex, keys[ledIndex]);
            Settings.putLoog("cdt" + ledIndex, millisInFuture);
//            System.out.println("fuuuindex" + keys[ledIndex]);
//            System.out.println("fuuusaveindex" + Settings.getInt("cdn"+ledIndex,0));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            millis = millisUntilFinished;
            sSum = millisUntilFinished / 1000;
            h = sSum / 3600;
            m = sSum % 3600 / 60;
            s = sSum % 3600 % 60;
            String sTime;
            if (h < 10) {
                sTime = "0" + h + ":";
            } else {
                sTime = h + ":";
            }
            if (m < 10) {
                sTime = sTime + "0" + m + ":";
            } else {
                sTime = sTime + m + ":";
            }
            if (s < 10) {
                sTime = sTime + "0" + s;
            } else {
                sTime = sTime + s;
            }
//            tv_time.setText(h + ":" + m + ":" + s);
            tv_time.setText(sTime);
        }

        @Override
        public void onFinish() {
            if(ledIndex<=3){
                tv_time.setText("后勤归来！");
            }else if(ledIndex==4){
                tv_time.setText("探索归来！");
            }
            cv.setCardBackgroundColor(getResources().getColor(R.color.red_500));
            imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
            led_state[ledIndex] = led_red;
            editTimes[ledIndex].setVisibility(View.GONE);
            restarts[ledIndex].setVisibility(View.VISIBLE);
            if (isShowToast) {
                //创建并弹出toast窗口通知
//                TextView tv = cardViews[ledIndex].findViewById(R.id.id_logistic);
//                showCustomToast(tv.getText().toString(), ledIndex);
                showCustomToast(nameDownTimeLayout[ledIndex].getText().toString(), ledIndex);
            }
        }
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < 5; i++) {
            if (myCountDownTimers[i] != null) {
                myCountDownTimers[i].cancel();
            }
        }
        windowManager.removeView(llWindowSelectLogistic);
        windowManager.removeView(llWindowDownTime);
        windowManager.removeView(ivWindowOval);
        windowManager.removeView(cdWindowEditTime);
        windowManager.removeView(llWindowSelectExplore);
        for (int i = 0; i < 5; i++) {
            if (llWindowToasts[i] != null) {
                windowManager.removeView(llWindowToasts[i]);
            }
        }
        //获取当前系统时间
        Utils.saveCurrentDate();

        super.onDestroy();
    }

    private final static int ANIM_CLOSE = 10;

    public void showCustomToast(String logisticName, int index) {
        //至少需要60s，同一个位置的toast才会被重新赋值
        llWindowToasts[index] = getToastLayout(logisticName,index);
        windowManager.addView(llWindowToasts[index], toastLayoutParams);
        setHeadToastViewAnim(index);
        mHandler.sendEmptyMessageDelayed(index, delayTime);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what >= 0 && msg.what <= 4) {
                animDismiss(msg.what);
            }
        }
    };

    private void animDismiss(final int index) {
//        System.out.println("index:"+index);
        if (llWindowToasts[index] == null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(llWindowToasts[index], "translationY", 0, -700);
        animator.setDuration(1000);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismiss(index);
            }
        });
    }

    private void dismiss(int index) {
        if (null != llWindowToasts[index]) {
            windowManager.removeView(llWindowToasts[index]);
            llWindowToasts[index] = null;
        }
    }

    private void setHeadToastViewAnim(int index) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(llWindowToasts[index], "translationY", -700, 0);
        animator.setDuration(1000);
        animator.start();
    }

    //
    public void initToastLayoutParams(int orientation) {
        toastLayoutParams = new WindowManager.LayoutParams();
        toastLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        toastLayoutParams.format = PixelFormat.TRANSLUCENT;
        toastLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        toastLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toastLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            toastLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            toastLayoutParams.y = getStatusBarHeight(getApplicationContext());
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toastLayoutParams.y = 0;
        }
        toastLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        toastLayoutParams.x = 0;
//        toastLayoutParams.format = -3;  // 会影响Toast中的布局消失的时候父控件和子控件消失的时机不一致，比如设置为-1之后就会不同步
//        wm_params.alpha = 1f;

    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        System.out.println("result:" + result);
        return result;
    }

    private View getToastLayout(String logisticName,int index) {
        View llWindowToast = View.inflate(this, R.layout.window_toast, null);
        TextView tv = llWindowToast.findViewById(R.id.content_window_toast);
        if(index<=3){
            tv.setText("后勤支援 " + logisticName + " 行动结束！");
        }else if(index==4){
            tv.setText("探索归来！");
        }
        return llWindowToast;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //屏幕方向改变时重新生成toast的layoutParams
        initToastLayoutParams(newConfig.orientation);
//        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            System.out.println("orientation:"+"竖屏");
//        }else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//            System.out.println("orientation:"+"横屏");
//        }
    }
}

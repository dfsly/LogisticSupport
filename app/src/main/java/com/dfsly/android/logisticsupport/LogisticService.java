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
    int delayTime;
    int[] keys = new int[4];
    //悬浮球
    ImageView imageOval;
    int led_red = 1;
    int led_gre = 2;
    int led_whi = 0;
    int[] led_state = {led_whi, led_whi, led_whi, led_whi};
    //倒计时视图
    CardView[] cardViews = new CardView[4];
    TextView[] editTimes = new TextView[4];
    TextView[] restarts = new TextView[4];
    TextView[] nameDownTimeLayout = new TextView[4];
    //修改时间视图
    LinearLayout cdWindowEditTime;
    WindowManager.LayoutParams editTimeLayoutParams;
    TextView nameEditTimeLayout;
    //    EditText hourEditTimeLayout;
//    EditText minuteEditTimeLayout;
    //toast
    View[] llWindowToasts = new View[4];
    WindowManager.LayoutParams toastLayoutParams;

    WindowManager windowManager;
    Button mButtonShrink;
    MyCountDownTimer[] myCountDownTimers = new MyCountDownTimer[4];

    LinearLayout llWindowDownTime;
    View ivWindowOval;
    //后勤列表
    private LinearLayout llWindowSelectLogistic;
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
        for (int i = 0; i < 4; i++) {
            if (led_state[i] == led_red) {
                imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
                break;
            } else if (led_state[i] == led_gre) {
                imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_green));
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isShowToast = Settings.getBoolean("switch_toast", true);
        delayTime = Settings.getInt("delay_time", 5) * 1000;
        logistics = LogisticLab.get(getApplicationContext()).getLogistics();

        llWindowDownTime = (LinearLayout) View.inflate(this, R.layout.service_logistic, null);
        ivWindowOval = View.inflate(this, R.layout.window_oval, null);
        imageOval = ivWindowOval.findViewById(R.id.image_oval);
        //设置悬浮球大小
//        ViewGroup.LayoutParams p = imageOval.getLayoutParams();
//        p.height=300;
//        p.width=300;
//        imageOval.setLayoutParams(p);

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
        //设置编辑时间按钮的点击监听
        for (int i = 0; i < 4; i++) {
            editTimes[i] = cardViews[i].findViewById(R.id.edit_time);
            editTimes[i].setOnClickListener(new StartETLClcikListener(i));
            restarts[i] = cardViews[i].findViewById(R.id.restart);
            restarts[i].setOnClickListener(new restartClickListener(i));
        }

        for (int i = 0; i < 4; i++) {
            cardViews[i].setOnClickListener(new StartSelectLogisticLayoutClickListener(i));
            nameDownTimeLayout[i] = cardViews[i].findViewById(R.id.id_logistic);
            nameDownTimeLayout[i].setText("后勤" + (i + 1) + ":");
        }

        windowManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        winHeight = windowManager.getDefaultDisplay().getHeight();
        winWidth = windowManager.getDefaultDisplay().getWidth();
        initDefaultLayoutParams();
        initEditTimeLayoutParams();

        initSelectLayout();

        initEditTimeLayout();
        initToastLayoutParams(getResources().getConfiguration().orientation);
        llWindowDownTime.setVisibility(View.GONE);
        cdWindowEditTime.setVisibility(View.GONE);
        windowManager.addView(ivWindowOval, defaultLayoutParams);
        windowManager.addView(llWindowDownTime, defaultLayoutParams);
        windowManager.addView(llWindowSelectLogistic, defaultLayoutParams);
        windowManager.addView(cdWindowEditTime, editTimeLayoutParams);
        ivWindowOval.setOnTouchListener(this);
//        initTestDialog();

        //恢复意外中断的倒计时
//        long subtractTime = Utils.getSubtract();
//        for(int i=0;i<4;i++){
//            //倒计时过程中服务被停止，则millis有不为-1的值，其他情况均为-1
//            long millis = Settings.getLong("cdt"+i,(long)-1);
//            if(millis!=-1){
//                long restartTime;
//                if(subtractTime==-1||subtractTime>millis){
//                    restartTime=1;
//                }else{
//                    restartTime = millis-subtractTime;
//                }
//                System.out.println("myCountDownTimers save"+i+millis);
//                myCountDownTimers[i] = new MyCountDownTimer(restartTime,1000,i);
//                myCountDownTimers[i].start();
//                cardViews[i].setCardBackgroundColor(getResources().getColor(R.color.light_green_600));
//                led_state[i] = led_gre;
//                TextView tv = cardViews[i].findViewById(R.id.id_logistic);
//                keys[i]=Settings.getInt("cdn"+i,0);
//                Logistic l = logistics.get(keys[i]);
//                tv.setText(l.getNo());
//            }
//        }
//        refreshOvalColor();
        //恢复意外中断的倒计时
        long l = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            //获取先前启动定时器时系统的时间
            long saveTime = Settings.getLong("saveTime" + i, (long) -1);
            //获取先前启动定时器时输入的时间
            long logisticTime = Settings.getLong("cdt"+i,(long)-1);

//            System.out.println("fuuusaveTime" + saveTime);
            if (saveTime != -1||logisticTime!=-1) {
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

                Logistic logistic = logistics.get(keys[i]);
                TextView tv = cardViews[i].findViewById(R.id.id_logistic);
                tv.setText(logistic.getNo());
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
            nameEditTimeLayout.setText(nameDownTimeLayout[i].getText());
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
            Logistic l = logistics.get(keys[i]);
            long millis = Utils.getMillis(l.getH(), l.getM());
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

    public void addItem(LinearLayout llListLogistic) {
        Logistic logistic;
        boolean remindAdd = true;
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
            //点击列表后即时保存该位置对应的后勤编号
            keys[index] = key;
            llWindowDownTime.setVisibility(View.VISIBLE);
            llWindowSelectLogistic.setVisibility(View.GONE);
            Logistic l = logistics.get(key);
            long millis = Utils.getMillis(l.getH(), l.getM());
            if (myCountDownTimers[index] != null) {
                myCountDownTimers[index].cancel();
            }
            TextView tv = cardViews[index].findViewById(R.id.id_logistic);
            tv.setText(l.getNo());
            tv.setTextSize(17);
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
//            System.out.println("fuuuindex" + keys[ledIndex]);
//            System.out.println("fuuusaveindex" + Settings.getInt("cdn"+ledIndex,0));
            Settings.putLoog("cdt" + ledIndex, millisInFuture);
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
            tv_time.setText("后勤归来！");
            cv.setCardBackgroundColor(getResources().getColor(R.color.red_500));
            imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
            led_state[ledIndex] = led_red;
            editTimes[ledIndex].setVisibility(View.GONE);
            restarts[ledIndex].setVisibility(View.VISIBLE);
            if (isShowToast) {
                //创建并弹出toast窗口通知
                TextView tv = cardViews[ledIndex].findViewById(R.id.id_logistic);
                showCustomToast(tv.getText().toString(), ledIndex);
            }
        }
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < 4; i++) {
            if (myCountDownTimers[i] != null) {
//                Settings.putInt("cdn"+i,keys[i]);
//                if(myCountDownTimers[i].getCurrentMillis()==0){
//                    Settings.putLoog("cdt"+i,-1);
//                }else {
//                    Settings.putLoog("cdt"+i,myCountDownTimers[i].getCurrentMillis());
//                }
//                System.out.println("myCountDownTimers"+i+":"+ myCountDownTimers[i].getCurrentMillis());
                myCountDownTimers[i].cancel();
            }
        }
        windowManager.removeView(llWindowSelectLogistic);
        windowManager.removeView(llWindowDownTime);
        windowManager.removeView(ivWindowOval);
        windowManager.removeView(cdWindowEditTime);
        for (int i = 0; i < 4; i++) {
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
        llWindowToasts[index] = getToastLayout(logisticName);
        windowManager.addView(llWindowToasts[index], toastLayoutParams);
        setHeadToastViewAnim(index);
        mHandler.sendEmptyMessageDelayed(index, delayTime);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what >= 0 && msg.what <= 3) {
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

    private View getToastLayout(String logisticName) {
        View llWindowToast = View.inflate(this, R.layout.window_toast, null);
        TextView tv = llWindowToast.findViewById(R.id.content_window_toast);
        tv.setText("后勤支援" + logisticName + "行动结束！");
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

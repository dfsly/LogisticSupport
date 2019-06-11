package com.dfsly.android.logisticsupport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;


import java.util.List;


public class LogisticService extends Service implements View.OnTouchListener {
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
    EditText hourEditTimeLayout;
    EditText minuteEditTimeLayout;

    WindowManager windowManager;
    Button mButtonShrink;
    MyCountDownTimer[] myCountDownTimers = new MyCountDownTimer[4];

    LinearLayout llWindowDownTime;
    View ivWindowOval;
    private LinearLayout llWindowSelectLogistic;
    WindowManager.LayoutParams layoutParams;
    LinearLayout llWindowChild;

    List<Logistic> logistics;
    TextView childNo;
    TextView childTime;
    View layoutChild;
    //用于记录所点击的当前界面的位置
    int index;
    int winHeight;

    public static Intent newIntent(Context context) {
        return new Intent(context,LogisticService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logistics = LogisticLab.get(this).getLogistics();
        llWindowDownTime = (LinearLayout) View.inflate(this, R.layout.service_logistic, null);
        ivWindowOval = View.inflate(this, R.layout.window_oval, null);
        imageOval = ivWindowOval.findViewById(R.id.image_oval);
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

                for (int i = 0; i < 4; i++) {
                    if (led_state[i] == led_red) {
                        imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
                        break;
                    } else if (led_state[i] == led_gre) {
                        imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_green));
                    }
                }
            }
        });
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
        initLayoutParams();
        initEditTimeLayoutParams();

        initSelectLayout();

        initEditTimeLayout();
        llWindowDownTime.setVisibility(View.GONE);
        cdWindowEditTime.setVisibility(View.GONE);
        windowManager.addView(ivWindowOval, layoutParams);
        windowManager.addView(llWindowDownTime, layoutParams);
        windowManager.addView(llWindowSelectLogistic, layoutParams);
        windowManager.addView(cdWindowEditTime, editTimeLayoutParams);
        ivWindowOval.setOnTouchListener(this);
    }

    int starY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                starY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endY = (int) event.getRawY();
                int dy = endY - starY;

                //更新view位置
                layoutParams.y += dy;
                if (layoutParams.y > winHeight - ivWindowOval.getHeight())
                    layoutParams.y = winHeight - ivWindowOval.getHeight();
                if (layoutParams.y < 0) layoutParams.y = 0;

                windowManager.updateViewLayout(ivWindowOval, layoutParams);

                starY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                windowManager.updateViewLayout(llWindowDownTime, layoutParams);
                windowManager.updateViewLayout(llWindowSelectLogistic, layoutParams);
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
            gotoEditTimeLFromDownTimeL();
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
        nameEditTimeLayout = cdWindowEditTime.findViewById(R.id.edit_logistic_name);
        hourEditTimeLayout = cdWindowEditTime.findViewById(R.id.hour_edit_time_layout);
        minuteEditTimeLayout = cdWindowEditTime.findViewById(R.id.minute_edit_time_layout);

        cdWindowEditTime.findViewById(R.id.ed_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDownTimeLFromEditTimeL();
            }
        });
        cdWindowEditTime.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h, m;
                String text = hourEditTimeLayout.getText().toString();
                if (text.equals("")) {
                    h = 0;
                } else {
                    h = Integer.parseInt(text);
                }
                text = minuteEditTimeLayout.getText().toString();
                if (text.equals("")) {
                    m = 0;
                } else {
                    m = Integer.parseInt(text);
                }
                myCountDownTimers[index].cancel();
                myCountDownTimers[index] = new MyCountDownTimer(Utils.getMillis(h, m), 1000, index);
                myCountDownTimers[index].start();
                gotoDownTimeLFromEditTimeL();
            }
        });
    }

    private void gotoDownTimeLFromEditTimeL() {
        cdWindowEditTime.setVisibility(View.GONE);
        llWindowDownTime.setVisibility(View.VISIBLE);
    }

    private void gotoEditTimeLFromDownTimeL() {
        cdWindowEditTime.setVisibility(View.VISIBLE);
        llWindowDownTime.setVisibility(View.GONE);
    }

    private void initLayoutParams() {
//        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        //透明背景
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        if(Build.VERSION.SDK_INT >=26){
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else
        if (Build.VERSION.SDK_INT > 24) {
                        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        layoutParams.x = 5;
//        System.out.println("winHeight" + (int) Math.floor(0.5 * winHeight));
        layoutParams.y = (int) Math.floor(0.5 * winHeight);

    }

    private void initEditTimeLayoutParams() {
        editTimeLayoutParams = new WindowManager.LayoutParams();
        //透明背景
        editTimeLayoutParams.format = PixelFormat.TRANSLUCENT;
        editTimeLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        editTimeLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        editTimeLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //获取焦点，返回键无效
        if(Build.VERSION.SDK_INT >=26){
            editTimeLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else
        if (Build.VERSION.SDK_INT > 24) {
            editTimeLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            editTimeLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        editTimeLayoutParams.gravity = Gravity.CENTER;
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
        Logistic logistic;
        for (int i = 0; i < logistics.size(); i++) {
            logistic = logistics.get(i);
            if (logistic.isSave) {
                //返回加载的layout
                layoutChild = LayoutInflater.from(this).inflate(R.layout.select_logist_item, llWindowSelectLogistic, false);

                //需要提供parent的layoutParams来约束select_logist_item生成的位置
//                layoutChild = View.inflate(this, R.layout.select_logist_item, null);
                llWindowChild = layoutChild.findViewById(R.id.window_child);
                childNo = layoutChild.findViewById(R.id.tv_no_child);
                childTime = layoutChild.findViewById(R.id.tv_time_child);

                childNo.setText(logistic.getNo());
                childTime.setText(Utils.getTextTime(logistic.getH(), logistic.getM()));

                //为容器布局添加子view
                llWindowChild.setOnClickListener(new CheckItemOnClick(logistic.getId()));
                llWindowSelectLogistic.addView(layoutChild);
            }
        }

        //初始化完成后设置为不可见
        llWindowSelectLogistic.setVisibility(View.GONE);
    }

    class CheckItemOnClick implements View.OnClickListener {

        int key;

        public CheckItemOnClick(int key) {
            this.key = key;
        }

        @Override
        public void onClick(View v) {
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
            keys[index] = key;
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

        public MyCountDownTimer(long millisInFuture, long countDownInterval, int index) {
            super(millisInFuture, countDownInterval);
            this.ledIndex = index;
            this.cv = cardViews[ledIndex];
            this.tv_time = cv.findViewById(R.id.time_logistic);
            restarts[ledIndex].setVisibility(View.GONE);
            editTimes[ledIndex].setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long sSum = millisUntilFinished / 1000;
            long h = sSum / 3600;
            long m = sSum % 3600 / 60;
            long s = sSum % 3600 % 60;
            tv_time.setText(h + ":" + m + ":" + s);
        }

        @Override
        public void onFinish() {
            tv_time.setText("后勤归来！");
            cv.setCardBackgroundColor(getResources().getColor(R.color.red_500));
            imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
            led_state[ledIndex] = led_red;
            editTimes[ledIndex].setVisibility(View.GONE);
            restarts[ledIndex].setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < 4; i++) {
            if (myCountDownTimers[i] != null) {
                myCountDownTimers[i].cancel();
            }
        }
        windowManager.removeView(llWindowSelectLogistic);
        windowManager.removeView(llWindowDownTime);
        windowManager.removeView(ivWindowOval);
        windowManager.removeView(cdWindowEditTime);
        super.onDestroy();
    }
}

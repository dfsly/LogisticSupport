package com.dfsly.android.logsticsupport;

import android.animation.ObjectAnimator;
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
import android.widget.Toast;

import androidx.cardview.widget.CardView;


import java.util.List;


public class LogsticService extends Service implements View.OnClickListener {
    //toast
    LinearLayout linearLayout;
    View headToastView;
    WindowManager.LayoutParams toastParams;
    //悬浮球
    ImageView imageOval;
    int led_red = 1;
    int led_gre = 2;
    int led_whi = 0;
    int[] led_state = {0, 0, 0, 0};
    int OVAL_COLOR = led_whi;
    //倒计时页面的cardView
    CardView[] cardViews = new CardView[4];
//    CardView logstic2;
//    CardView logstic3;
//    CardView logstic4;

    WindowManager windowManager;
    Button mButtonShrink;
    MyCountDownTimer[] myCountDownTimers = new MyCountDownTimer[4];
    //    MyCountDownTimer myCountDownTimer2;
//    MyCountDownTimer myCountDownTimer3;
//    MyCountDownTimer myCountDownTimer4;
//    TextView mTimeLogstic1;
//    TextView mTimeLogstic2;
//    TextView mTimeLogstic3;
//    TextView mTimeLogstic4;
    LinearLayout llWindowDownTime;
    View ivWindowOval;
    private LinearLayout llWindowSelectLogstic;
    WindowManager.LayoutParams layoutParams;
    LinearLayout llWindowChild;

    List<Logstic> logstics;
    TextView childNo;
    TextView childTime;
    View layoutChild;
    int index;
    int winHeight;
//    int winWidth;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logstics = LogsticLab.get(this).getLogstics();
        llWindowDownTime = (LinearLayout) View.inflate(this, R.layout.service_logstic, null);
        ivWindowOval = View.inflate(this, R.layout.window_oval, null);
        imageOval = ivWindowOval.findViewById(R.id.image_oval);
        ivWindowOval.setOnClickListener(this);
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
        cardViews[0] = llWindowDownTime.findViewById(R.id.logstic1);
        cardViews[1] = llWindowDownTime.findViewById(R.id.logstic2);
        cardViews[2] = llWindowDownTime.findViewById(R.id.logstic3);
        cardViews[3] = llWindowDownTime.findViewById(R.id.logstic4);
        TextView tv;
        for (int i = 0; i < 4; i++) {
            cardViews[i].setOnClickListener(this);
            tv = cardViews[i].findViewById(R.id.id_logstic);
            tv.setText("后勤"+(i+1)+":");
        }
//        logstic2 = llWindowDownTime.findViewById(R.id.logstic2);
//        logstic2.setOnClickListener(this);
//        logstic3 = llWindowDownTime.findViewById(R.id.logstic3);
//        logstic4 = llWindowDownTime.findViewById(R.id.logstic4);

//        mTimeLogstic1 = logstic1.findViewById(R.id.time_logstic);
//        mTimeLogstic2 = logstic2.findViewById(R.id.time_logstic);
//        mTimeLogstic3 = logstic3.findViewById(R.id.time_logstic);
//        mTimeLogstic4 = logstic4.findViewById(R.id.time_logstic);

        //为对应后勤设置倒计时
//        myCountDownTimer = new MyCountDownTimer(120000, 1000, logstic1,0);
//        myCountDownTimer.start();
//        myCountDownTimer2 = new MyCountDownTimer(60000, 1000, logstic2,1);
//        myCountDownTimer2.start();
//        myCountDownTimer3 = new MyCountDownTimer(30000, 1000, logstic3,2);
//        myCountDownTimer3.start();
//        myCountDownTimer4 = new MyCountDownTimer(10000, 1000, logstic4,3);
//        myCountDownTimer4.start();

        windowManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        winHeight = windowManager.getDefaultDisplay().getHeight();
        initWindowLayout();
        //        winWidth = windowManager.getDefaultDisplay().getWidth();

        //初始化后勤列表布局
        initSelectLayout();
        llWindowDownTime.setVisibility(View.GONE);
        windowManager.addView(ivWindowOval, layoutParams);
        windowManager.addView(llWindowDownTime, layoutParams);
        windowManager.addView(llWindowSelectLogstic, layoutParams);
//        initHeadToastView();

        ivWindowOval.setOnTouchListener(new View.OnTouchListener() {

            private int starY;
//            private int starX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

//                        int dx = endX - starX;
                        int dy = endY - starY;

                        //更新view位置
//                        layoutParams.x += dx;
                        layoutParams.y += dy;
                        //按住view并将其拖到边界后，event.getRawX会一直增大
//                        if (layoutParams.x > winWidth - ivWindowOval.getWidth())
//                            layoutParams.x = winWidth - ivWindowOval.getWidth();
//                        if (layoutParams.x < 0) layoutParams.x = 0;
                        if (layoutParams.y > winHeight - ivWindowOval.getHeight())
                            layoutParams.y = winHeight - ivWindowOval.getHeight();
                        if (layoutParams.y < 0) layoutParams.y = 0;

                        windowManager.updateViewLayout(ivWindowOval, layoutParams);

//                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        windowManager.updateViewLayout(llWindowDownTime, layoutParams);
                        windowManager.updateViewLayout(llWindowSelectLogstic, layoutParams);
                        break;
                }
                return false;
            }
        });

    }

    private void initWindowLayout() {


//        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        //透明背景
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.type =1999;
        //>26,TYPE_APPLICATION_OVERLAY
        if (Build.VERSION.SDK_INT > 24) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        layoutParams.x = 5;
        System.out.println("winHeight" + (int) Math.floor(0.5 * winHeight));
        layoutParams.y = (int) Math.floor(0.5 * winHeight);

    }

//    private void setHeadToastViewAnim() {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(linearLayout, "translationY", -700, 0);
//        //ANIM_DURATION=600
//        animator.setDuration(600);
//        animator.start();
//    }

//    public void initHeadToastView() {
//        //准备Window要添加的View
//        linearLayout = new LinearLayout(this);
//        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        linearLayout.setLayoutParams(layoutParams);
//        headToastView = View.inflate(this, R.layout.header_tosat, null);
//        linearLayout.addView(headToastView);
//        initToastParams();
//        windowManager.addView(linearLayout, toastParams);
//        setHeadToastViewAnim();
//    }

//    private void initToastParams() {
//        toastParams = new WindowManager.LayoutParams();
//        //透明背景
//        toastParams.format = PixelFormat.TRANSLUCENT;
//        toastParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        toastParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        toastParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//        //>26,TYPE_APPLICATION_OVERLAY
//        if (Build.VERSION.SDK_INT > 24) {
//            toastParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        } else {
//            toastParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        }
//        toastParams.gravity = Gravity.LEFT | Gravity.TOP;
//        toastParams.x = 5;
//        toastParams.y = 0;
//    }

    private void initSelectLayout() {

        //添加布局
        llWindowSelectLogstic = (LinearLayout) View.inflate(this, R.layout.select_logstic, null);
        //拿到容器布局
//        llWindowSelectLogstic = llWindowSelectView.findViewById(R.id.ll_select_logstic);
        llWindowSelectLogstic.findViewById(R.id.select_list_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownTimeList();
            }
        });
        Logstic logstic;
        for (int i = 0; i < logstics.size(); i++) {
            logstic = logstics.get(i);
            if (logstic.isSave) {
                //返回加载的layout
                layoutChild = LayoutInflater.from(this).inflate(R.layout.select_logist_item,llWindowSelectLogstic,false);

                //需要提供parent的layoutParams来约束select_logist_item生成的位置
//                layoutChild = View.inflate(this, R.layout.select_logist_item, null);
                llWindowChild = layoutChild.findViewById(R.id.window_child);
                childNo = layoutChild.findViewById(R.id.tv_no_child);
                childTime = layoutChild.findViewById(R.id.tv_time_child);

                childNo.setText(logstic.getNo());
                childTime.setText(Utils.getTextTime(logstic.getH(), logstic.getM()));

                //为容器布局添加子view
                llWindowChild.setOnClickListener(new CheckItemOnClick(logstic.getId()));
                llWindowSelectLogstic.addView(layoutChild);
            }
        }

        //初始化完成后设置为不可见
        llWindowSelectLogstic.setVisibility(View.GONE);


    }

    class CheckItemOnClick implements View.OnClickListener {

        int key;

        public CheckItemOnClick(int key) {
            this.key = key;
        }

        @Override
        public void onClick(View v) {
            llWindowDownTime.setVisibility(View.VISIBLE);
            llWindowSelectLogstic.setVisibility(View.GONE);
            Logstic l = logstics.get(key);
            long millis = (l.getH() * 60 + l.getM()) * 60000;

            if (myCountDownTimers[index] != null) {
                myCountDownTimers[index].cancel();
            }
            TextView tv = cardViews[index].findViewById(R.id.id_logstic);
            tv.setText(l.getNo());
//            tv.setTextColor(getResources().getColor(R.color.grey_975));
            tv.setTextSize(17);
            myCountDownTimers[index] = new MyCountDownTimer(millis, 1000);
            myCountDownTimers[index].start();
            cardViews[index].setCardBackgroundColor(getResources().getColor(R.color.light_green_600));


//            if(flag==1){
////                starDownTime(ItemDownTimeCardView cardView,);
//                if(myCountDownTimer!=null){
//                    myCountDownTimer.cancel();
//                }
//                TextView tv = logstic1.findViewById(R.id.id_logstic);
//                tv.setText(l.getNo());
//                myCountDownTimer = new MyCountDownTimer(millis,1000,logstic1,0);
//                myCountDownTimer.start();
//                logstic1.setCardBackgroundColor(getResources().getColor(R.color.light_green_600));
//
//            }else if(flag==2){
//                if(myCountDownTimer2!=null){
//                    myCountDownTimer2.cancel();
//                }
//                myCountDownTimer2 = new MyCountDownTimer(millis,1000,logstic2,1);
//                myCountDownTimer2.start();
//                logstic2.setCardBackgroundColor(getResources().getColor(R.color.light_green_600));
//            }
            led_state[index] = led_gre;
        }
    }

    @Override
    public void onClick(View v) {

//        windowManager.removeView(v);0
        switch (v.getId()) {
            case R.id.but_oval:
                ivWindowOval.setVisibility(View.GONE);
                llWindowDownTime.setVisibility(View.VISIBLE);
                break;
            case R.id.logstic1:
                index = 0;
                showSaveLogsticList();

                break;
            case R.id.logstic2:
                index = 1;
                showSaveLogsticList();

                break;
            case R.id.logstic3:
                index = 2;
                showSaveLogsticList();

                break;
            case R.id.logstic4:
                index = 3;
                showSaveLogsticList();

                break;
        }
    }

    private void showSaveLogsticList() {
        llWindowDownTime.setVisibility(View.GONE);
        llWindowSelectLogstic.setVisibility(View.VISIBLE);
    }

    private void showDownTimeList() {
        llWindowSelectLogstic.setVisibility(View.GONE);
        llWindowDownTime.setVisibility(View.VISIBLE);
    }



    public class MyCountDownTimer extends CountDownTimer {
        CardView cv;
        TextView tv_time;
        //        TextView tv_no;
        int ledIndex;
//        TextView edit_time;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.cv = cardViews[index];
            this.tv_time = cv.findViewById(R.id.time_logstic);
//            this.edit_time = cv.findViewById(R.id.edit_time);
//            edit_time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    windowManag
//                }
//            });
//            this.tv_no=cv.findViewById(R.id.id_logstic);
            this.ledIndex = index;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long sSum = millisUntilFinished / 1000;
            long h = sSum / 3600;
            long m = sSum % 3600 / 60;
            long s = sSum % 3600 % 60;
//            mTimeLogstic1.setText(millisUntilFinished/1000+"s");
            tv_time.setText(h + ":" + m + ":" + s);
        }

        @Override
        public void onFinish() {
            //为window添加通知view，显示完毕后移除
            tv_time.setText("后勤归来！");
            cv.setCardBackgroundColor(getResources().getColor(R.color.red_500));
            imageOval.setImageDrawable(getResources().getDrawable(R.drawable.oval_red));
            led_state[ledIndex] = led_red;
        }
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < 4; i++) {
            if (myCountDownTimers[i] != null) {
                myCountDownTimers[i].cancel();
            }
        }
        windowManager.removeView(llWindowSelectLogstic);
        windowManager.removeView(llWindowDownTime);
        windowManager.removeView(ivWindowOval);
        super.onDestroy();
    }
}

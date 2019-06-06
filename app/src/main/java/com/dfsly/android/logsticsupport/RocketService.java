package com.dfsly.android.logsticsupport;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class RocketService extends Service {

    private WindowManager.LayoutParams params;
    private WindowManager mWM;
    private View view;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final int winWidth = mWM.getDefaultDisplay().getWidth();
        final int winHeight = mWM.getDefaultDisplay().getHeight();
        params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        System.out.println("SDK_INT="+Build.VERSION.SDK_INT+"VERSION_CODES.O="+Build.VERSION_CODES.O);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置重心和偏移量
        params.gravity = Gravity.START + Gravity.TOP;

        view = View.inflate(this, R.layout.rocket, null);
        ImageView iv_rocket = view.findViewById(R.id.iv_rocket);
        iv_rocket.setBackgroundResource(R.drawable.anim_rocket);
        AnimationDrawable anim = (AnimationDrawable) iv_rocket.getBackground();
        anim.start();

        mWM.addView(view, params);

        view.setOnTouchListener(new View.OnTouchListener() {

            private int starY;
            private int starX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - starX;
                        int dy = endY - starY;

                        //更新view位置
                        params.x += dx;
                        params.y += dy;
                        //按住view并将其拖到边界后，event.getRawX会一直增大
                        if (params.x > winWidth - view.getWidth())
                            params.x = winWidth - view.getWidth();
                        if (params.x < 0) params.x = 0;
                        if (params.y > winHeight - view.getHeight())
                            params.y = winHeight - view.getHeight();
                        if (params.y < 0) params.y = 0;

                        mWM.updateViewLayout(view, params);

                        //错误：event.getRawX的是点击处离屏幕左边的距离
//                        starX = params.x; starX过小，dx过大，params.x过大
//                        starY = params.y;
                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("h=" + winHeight + ";w=" + winWidth + ";v_h=" + view.getHeight() + ";v_w=" + view.getWidth());

                        if (params.x > 300 && params.x < 780 && params.y > winHeight - 600) {
                            System.out.println("火箭发射");
                            sendRocket();
                        }

                        break;
                }
                return false;
            }
        });
    }

    private void sendRocket() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                int pos = 1920;
                int y;
                for (int i = 0; i <= 10; i++) {
                    y = pos - i * 192;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.arg1 = y;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();

    }



        @SuppressLint("HandlerLeak")
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int y = msg.arg1;
                params.y = y;
                mWM.updateViewLayout(view, params);
            }
        };

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mWM != null && view != null) {
                mWM.removeView(view);
                view = null;
            }
        }
    }

package com.ctyfl.sleepdemo.sleepDemoV1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;
import com.ctyfl.sleepdemo.config.SleepConfig;
/**
 * @Author ctyFL
 * @Date 2020-02-19 16:15
 * @Version 1.0
 */
public class SleepBaseActivity extends Activity {

    public Context context;
    public CountDownTimer countDownTimer; //定时器1：一定时间内开启休眠广告
    public CountDownTimer quitTimer;//定时器2：一定时间内退出账号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                //按下时取消定时
                cancelAllTimer();
                System.out.println("ACTION_DOWN---取消定时");
                break;

            case MotionEvent.ACTION_UP :
                //抬起时启动定时
                //startAD();
                new AdTherad().start();
                new QuitThread().start();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startAD() {
        long adTime = SleepConfig.getInstance().getAdTimeByDayAndNight();
        if(countDownTimer == null) {
            countDownTimer = new CountDownTimer(adTime, 1000l) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    //定时完成后的操作
                    //跳转到广告页面
                    Intent intent = new Intent(SleepBaseActivity.this, ADActivity.class);
                    startActivity(intent);
                }
            };
            countDownTimer.start();
            System.out.println("开始定时");
        }
    }

    public void quitListener(){
        long noOpsTime = SleepConfig.getInstance().getNoOpsQuitTime();
        if(quitTimer == null) {
            quitTimer = new CountDownTimer(noOpsTime, 1000l) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    //退出
                    Toast.makeText(context, "退出账号", Toast.LENGTH_LONG).show();
                }
            };
            quitTimer.start();
        }
    }

    public class AdTherad extends Thread {
        public AdTherad() {
            super();
        }
        @Override
        public void run() {
            Looper.prepare();
            super.run();
            startAD();
            Looper.loop();
        }
    }

    public class QuitThread extends Thread {
        public QuitThread() {
            super();
        }
        @Override
        public void run() {
            Looper.prepare();
            super.run();
            quitListener();
            Looper.loop();
        }
    }

    public void cancelAllTimer() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if(quitTimer != null) {
            quitTimer.cancel();
            quitTimer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new QuitThread().start();
        new AdTherad().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当activity不在前台时停止定时
        cancelAllTimer();
        System.out.println("onPause---取消定时");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时停止定时
        cancelAllTimer();
        System.out.println("onDestroy---取消定时");
    }

}

package com.ctyfl.gsyvideodemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

public class AdPlayerActivity extends AppCompatActivity {

    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";


    AdPlayVideo videoPlayer;

    OrientationUtils orientationUtils;

    private boolean isTransition;

    private Transition transition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad_player);
        ButterKnife.bind(this);
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);
        init();
    }

    private void init() {
        videoPlayer = findViewById(R.id.video_adplayer);
        //String url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        //String url = "http://192.168.1.9:9000/bpm/123.mp4";
        String url = "http://192.168.10.153:9000/bpm/AioPutRecordServlet?mode=playvideo&pathname=/202005/1590634304671/birthday.mp4";
//
//        videoPlayer.setUp(url, true, "");

        List<GSYVideoModel> urls = new ArrayList<>();
        //urls.add(new GSYVideoModel("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4", ""));
        //urls.add(new GSYVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4", ""));
        //urls.add(new GSYVideoModel("https://res.exexm.com/cw_145225549855002", ""));
        //urls.add(new GSYVideoModel("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4", ""));
        urls.add(new GSYVideoModel(url, ""));
        videoPlayer.setUp(urls, true, 0);

        //设置全屏裁剪
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);

        //过渡动画
        initTransition();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                //按下时取消定时
                finish();
                //videoPlayer.playNext();
                break;

            case MotionEvent.ACTION_UP :
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }, 500);
        }
    }


    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener(){
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }
}

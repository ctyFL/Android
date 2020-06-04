package com.ctyfl.slideshowactivity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyImageView myImageView;
    private List<String> imgList;
    private long changeImgTime = 3 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        prepareImg();
        showImg();
    }

    private void showImg() {
        new Thread() {
            @Override
            public void run() {
                try {
                    myImageView = (MyImageView) findViewById(R.id.myImageView);
                    for(int i=0; i<imgList.size(); i++) {
                        myImageView.setImageURL(imgList.get(i));
                        Thread.sleep(changeImgTime);
                        if(i == (imgList.size() - 1)) {
                            i = -1;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void prepareImg() {
        imgList = new ArrayList<String>();
        imgList.add("图片链接.....");
        imgList.add("图片链接.....");
        imgList.add("图片链接.....");
        imgList.add("图片链接.....");
        imgList.add("图片链接.....");
    }

}

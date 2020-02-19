package com.ctyfl.sleepdemo.sleepDemoV1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ctyfl.sleepdemo.R;
/**
 * @Author ctyFL
 * @Date 2020-02-19 17:35
 * @Version 1.0
 */
public class ADActivity extends AppCompatActivity {

    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        initView();
        setButtonClickEnent();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
    }

    private void setButtonClickEnent() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

package com.ctyfl.gsyvideodemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

public class MainActivity extends Activity {

    private Button btn_goplay;
    private Button btn_goad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_goplay = (Button) findViewById(R.id.btn_goplay);
        btn_goad = (Button) findViewById(R.id.btn_goad);
        setOnClickEvent();
    }

    private void setOnClickEvent() {
        btn_goplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimplePlayerActivity.class);
                startActivity(intent);
            }
        });
        btn_goad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdPlayerActivity.class);
                intent.putExtra("TRANSITION", true);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair pair = new Pair<>(findViewById(R.id.btn_goad), "IMG_TRANSITION");
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            MainActivity.this, pair);
                    ActivityCompat.startActivity(MainActivity.this, intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }
        });
    }
}

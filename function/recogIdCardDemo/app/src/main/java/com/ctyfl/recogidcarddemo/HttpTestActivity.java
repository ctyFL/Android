package com.ctyfl.recogidcarddemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileInputStream;

public class HttpTestActivity extends Activity implements View.OnClickListener {

    private TextView text_filepath;
    private TextView text_rootPath;
    private ImageView img_localImg;

    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
    private String servletName = "http://192.168.10.153:9100/AriesFL/ReceiveFileServlet";
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_test);
        filePath = rootPath + "/zp.bmp";
        initView();
    }

    private void initView() {
        text_filepath = (TextView) findViewById(R.id.text_path);
        text_rootPath = (TextView) findViewById(R.id.text_rootPath);
        img_localImg = (ImageView) findViewById(R.id.img_localImg);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_post_uploadFile).setOnClickListener(this);
        findViewById(R.id.btn_getRootPath).setOnClickListener(this);
        findViewById(R.id.btn_getFilePath).setOnClickListener(this);
        findViewById(R.id.btn_getLocalImg).setOnClickListener(this);
    }

    private void setLocalImg() {
        if(!"".equals(filePath)) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Bitmap bmp = BitmapFactory.decodeStream(fis);
                img_localImg.setImageBitmap(bmp);
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back :
                finish();
                break;
            case R.id.btn_get :
               UploadUtil.HttpGetThread(null, servletName);
                break;
            case R.id.btn_post :
                UploadUtil.HttpPostThread(null, servletName);
                break;
            case R.id.btn_post_uploadFile :
                UploadUtil.postUploadFileThread(filePath, "330727199604070014", servletName);
                break;
            case R.id.btn_getRootPath :
                text_rootPath.setText(rootPath);
                break;
            case R.id.btn_getFilePath :
                text_filepath.setText(filePath);
                break;
            case R.id.btn_getLocalImg :
                setLocalImg();
                break;
            default:
                break;
        }
    }

}

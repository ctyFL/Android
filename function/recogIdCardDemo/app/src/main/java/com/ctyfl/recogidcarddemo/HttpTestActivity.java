package com.ctyfl.recogidcarddemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;

public class HttpTestActivity extends Activity implements View.OnClickListener {

    private TextView text_filepath;
    private TextView text_rootPath;
//    private Button btn_back;
//    private Button btn_httpGet;
//    private Button btn_httpPost;
//    private Button btn_postUpload;
//    private Button btn_getRootPath
    private ImageView img_localImg;

    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
    private String servletName ="http://192.168.10.153:9100/AriesFL/ReceiveFileServlet";
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
//        btn_back = (Button) findViewById(R.id.btn_back);
//        btn_httpGet = (Button) findViewById(R.id.btn_get);
//        btn_httpPost = (Button) findViewById(R.id.btn_post);
//        btn_postUpload = (Button) findViewById(R.id.btn_post_uploadFile);
        img_localImg = (ImageView) findViewById(R.id.img_localImg);
    }

    private void setLocalImg() {
        if(!"".equals(filePath)) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Bitmap bmp = BitmapFactory.decodeStream(fis);
                img_localImg.setImageBitmap(bmp);
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
                Toast.makeText(HttpTestActivity.this, UploadUtil.HttpGet(null, servletName), Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_post :
                Toast.makeText(HttpTestActivity.this, UploadUtil.HttpPost(null, servletName), Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_post_uploadFile :
                Toast.makeText(HttpTestActivity.this, UploadUtil.uploadPost(filePath, servletName), Toast.LENGTH_LONG).show();
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

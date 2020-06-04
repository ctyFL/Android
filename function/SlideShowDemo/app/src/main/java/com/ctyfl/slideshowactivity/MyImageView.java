package com.ctyfl.slideshowactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author ctyFL
 * @Date 2020-06-02 14:20
 * @Version 1.0
 */

@SuppressLint("AppCompatCustomView")
public class MyImageView extends ImageView {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCESS :
                    Bitmap bitmap = (Bitmap) msg.obj;
                    setImageBitmap(bitmap);
                    break;
                case NETWORK_ERROR :
                    Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR :
                    Toast.makeText(getContext(), "服务器发送错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageURL(final String path) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    int code = conn.getResponseCode();
                    if(code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        //获得原图
                        //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //获得压缩后的图（若图片超出屏幕大小则压缩）
                        Bitmap bitmap = getCompressBitmap(inputStream);
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        inputStream.close();
                    }else {
                        handler.sendEmptyMessage(SERVER_ERROR);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        }.start();
    }

    /**
     * 获取图片实际的宽度
     * @return
     */
    public int getRealImageWidth() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        //如果ImageView设置了宽度就可以获取实际宽度
        int width = getWidth();
        if (width <= 0) {
            //如果ImageView没有设置宽度，就获取父级容器的宽度
            width = layoutParams.width;
        }
        if (width <= 0) {
            //获取ImageView宽度的最大值
            width = getMaxWidth();
        }
        if (width <= 0) {
            //获取屏幕的宽度
            width = displayMetrics.widthPixels;
        }
        return width;
    }

    /**
     * 获取图片实际的高度
     * @return
     */
    public int getRealImageHeight() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        //如果ImageView设置了高度就可以获取实在宽度
        int height = getHeight();
        if (height <= 0) {
            //如果ImageView没有设置高度，就获取父级容器的高度
            height = layoutParams.height;
        }
        if (height <= 0) {
            //获取ImageView高度的最大值
            height = getMaxHeight();
        }
        if (height <= 0) {
            //获取ImageView高度的最大值
            height = displayMetrics.heightPixels;
        }
        return height;
    }

    /**
     * 计算压缩比率
     * @param options
     * @return
     */
    public int getInSampleSize(BitmapFactory.Options options) {
        int inSampleSize = 1;
        int realWith = getRealImageWidth();
        int realHeight = getRealImageHeight();

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        //获取比率最大的那个
        if (outWidth > realWith || outHeight > realHeight) {
            int withRadio = Math.round(outWidth / realWith);
            int heightRadio = Math.round(outHeight / realHeight);
            inSampleSize = withRadio > heightRadio ? withRadio : heightRadio;
        }
        return inSampleSize;
    }

    /**
     * 返回压缩后的图片
     * @param input
     * @return
     */
    public Bitmap getCompressBitmap(InputStream input) {
        //因为InputStream要使用两次，但是使用一次就无效了，所以需要复制两个
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //复制新的输入流
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

        //只是获取网络图片的大小，并没有真正获取图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        //获取图片并进行压缩
        options.inSampleSize = getInSampleSize(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is2, null, options);
    }

}

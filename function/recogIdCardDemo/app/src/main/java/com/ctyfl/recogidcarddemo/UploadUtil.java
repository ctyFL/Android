package com.ctyfl.recogidcarddemo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * @Author ctyFL
 * @Date 2020-04-21 16:23
 * @Version 1.0
 */
public class UploadUtil {

    private static final String TAG = "UploadUtil";
    private static final int TIME_OUT = 10 * 1000;
    private static final String BOUNDARY = UUID.randomUUID().toString();//边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String NEXT_LINE = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data";
    private static final String POST = "POST";

    public static String uploadFile(File file, String servletUrl) {
        String result = "";
        try {
            URL url = new URL(servletUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(POST);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if(file != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

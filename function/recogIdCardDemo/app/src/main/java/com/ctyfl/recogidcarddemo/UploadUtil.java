package com.ctyfl.recogidcarddemo;

import android.util.Log;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    //https://www.jb51.net/article/80659.htm
    private static final String TAG = "UploadUtil";
    private static final int TIME_OUT = 10 * 1000;
    private static final String BOUNDARY = UUID.randomUUID().toString();//边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String NEXT_LINE = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data";
    private static final String POST = "POST";

    public static String uploadFile(File file, String outPutFileName, String servletUrl) {
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
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(NEXT_LINE);
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getName() + "\"" + NEXT_LINE);
                sb.append("Content-Type: application/octet-stream; charset=UTF-8" + NEXT_LINE);
                sb.append(NEXT_LINE);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(NEXT_LINE.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + NEXT_LINE).getBytes();
                dos.write(end_data);
                dos.flush();

                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if(res == 200) {
                    Log.e(TAG, "requestSuccess");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result:" + result);
                }else {
                    Log.e(TAG, "result error");
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

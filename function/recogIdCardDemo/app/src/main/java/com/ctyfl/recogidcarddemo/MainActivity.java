package com.ctyfl.recogidcarddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.huashi.otg.sdk.HSIDCardInfo;
import com.huashi.otg.sdk.HandlerMsg;
import com.huashi.otg.sdk.HsOtgApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {

    private String filepath = "";
    private Button btn_autoRead;
    private Button btn_httpTest;
    private TextView text_info;
    private ImageView img_photo;
    private boolean startRead = false;
    private HsOtgApi hsOtgApi;
    private ProgressDialog connectProgressDialog;
    private ProgressDialog readProgressDialog;
    private long startTime;

    private static final int TIMEOUT = 1000 * 7;
    String servletName = "http://192.168.10.153:9100/AriesFL/ReceiveFileServlet";
    SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //第一次授权时候的判断是利用handler判断，授权过后就不用这个判断了
            if(msg.what ==HandlerMsg.CONNECT_SUCCESS) {
                //连接成功
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == HandlerMsg.CONNECT_ERROR) {
               //连接失败
                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                connectApi();
            }
            if(msg.what == HandlerMsg.READ_ERROR) {
                //卡认证失败
               if(isTimeout(System.currentTimeMillis(), startTime)) {
                   stopReadAndDialogDismiss();
                   text_info.setText("已超时，请重新识别");
               }
            }
            if(msg.what == HandlerMsg.READ_SUCCESS) {
                //读卡成功
                Toast.makeText(MainActivity.this, "读卡成功", Toast.LENGTH_LONG).show();
                stopReadAndDialogDismiss();
                getIdInfo(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
        setContentView(R.layout.activity_main);
        initProcessDialog();
        initView();
        setOnClickEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstInitApi();
    }

    private void initView() {
        btn_autoRead = (Button) findViewById(R.id.btn_autoRead);
        text_info = (TextView) findViewById(R.id.text_info);
        img_photo = (ImageView) findViewById(R.id.img_photo);
        btn_httpTest = (Button) findViewById(R.id.btn_httpTest);
        cleanData();
    }

    private void firstInitApi() {
        copy(MainActivity.this, "base.dat", "base.dat", filepath);
        copy(MainActivity.this, "license.lic", "license.lic", filepath);
        hsOtgApi = new HsOtgApi(handler, MainActivity.this);
        hsOtgApi.init();
    }

    private boolean connectApi() {
        if(connectProgressDialog != null && !connectProgressDialog.isShowing()) {
            connectProgressDialog.show();
        }
        int ret = hsOtgApi.init();
        if(ret == 1) {
            // 连接成功
            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            connDialogDismiss();
            return true;
        }
        connDialogDismiss();
        return false;
    }

    private void initProcessDialog() {
        connectProgressDialog = new ProgressDialog(MainActivity.this);
        connectProgressDialog.setMessage("正在连接...");
        connectProgressDialog.setCancelable(false);

        readProgressDialog = new ProgressDialog(MainActivity.this);
        readProgressDialog.setMessage("请将身份证靠近扫描区域...");
        readProgressDialog.setCancelable(false);
    }

    private void setOnClickEvent() {
        btn_autoRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readProgressDialog != null && !readProgressDialog.isShowing()) {
                    readProgressDialog.show();
                }

                if(connectApi()){
                    startRead = true;
                    cleanData();
                    startTime = System.currentTimeMillis();
                    new Thread(new ReadIdCardThread()).start();
                }
            }
        });
        btn_httpTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HttpTestActivity.class);
                startActivity(intent);
            }
        });
    }

    public class ReadIdCardThread extends Thread {
        public ReadIdCardThread() {
            super();
        }
        @Override
        public void run() {
            super.run();
            HSIDCardInfo ici;
            Message msg;
            while (startRead) {
                if (hsOtgApi.Authenticate(200, 200) != 1) {
                    msg = Message.obtain();
                    msg.what = HandlerMsg.READ_ERROR;
                    handler.sendMessage(msg);
                } else {
                    ici = new HSIDCardInfo();
                    if (hsOtgApi.ReadCard(ici, 200, 1300) == 1) {
                        msg = Message.obtain();
                        msg.obj = ici;
                        msg.what = HandlerMsg.READ_SUCCESS;
                        handler.sendMessage(msg);
                    }
                }
                SystemClock.sleep(300);
            }
        }
    }

    private void getIdInfo(Message msg) {
        HSIDCardInfo ic = (HSIDCardInfo) msg.obj;
        byte[] fp = new byte[1024];
        fp = ic.getFpDate();
        String m_FristPFInfo = "";
        String m_SecondPFInfo = "";

        if(fp[4] == (byte)0x01) {
            m_FristPFInfo = String.format("指纹  信息：第一枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[5]), fp[6]);
        }else {
            m_FristPFInfo = "身份证无指纹 \n";
        }
        if(fp[512 + 4] == (byte)0x01) {
            m_SecondPFInfo = String.format("指纹  信息：第二枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[512 + 5]),
                    fp[512 + 6]);
        }else {
            m_SecondPFInfo = "身份证无指纹 \n";
        }
        if(ic.getcertType() == " ") {
            text_info.setText("证件类型：身份证\n" + "姓名："
                    + ic.getPeopleName() + "\n" + "性别：" + ic.getSex()
                    + "\n" + "民族：" + ic.getPeople() + "\n" + "出生日期："
                    + df.format(ic.getBirthDay()) + "\n" + "地址："
                    + ic.getAddr() + "\n" + "身份号码：" + ic.getIDCard()
                    + "\n" + "签发机关：" + ic.getDepartment() + "\n"
                    + "有效期限：" + ic.getStrartDate() + "-"
                    + ic.getEndDate() + "\n" + m_FristPFInfo + "\n"
                    + m_SecondPFInfo);
        }else {
            if(ic.getcertType() == "J") {
                text_info.setText("证件类型：港澳台居住证（J）\n"
                        + "姓名：" + ic.getPeopleName() + "\n" + "性别："
                        + ic.getSex() + "\n"
                        + "签发次数：" + ic.getissuesNum() + "\n"
                        + "通行证号码：" + ic.getPassCheckID() + "\n"
                        + "出生日期：" + df.format(ic.getBirthDay())
                        + "\n" + "地址：" + ic.getAddr() + "\n" + "身份号码："
                        + ic.getIDCard() + "\n" + "签发机关："
                        + ic.getDepartment() + "\n" + "有效期限："
                        + ic.getStrartDate() + "-" + ic.getEndDate() + "\n"
                        + m_FristPFInfo + "\n" + m_SecondPFInfo);
            }else {
                if(ic.getcertType() == "I") {
                    text_info.setText("证件类型：外国人永久居留证（I）\n"
                            + "英文名称：" + ic.getPeopleName() + "\n"
                            + "中文名称：" + ic.getstrChineseName() + "\n"
                            + "性别：" + ic.getSex() + "\n"
                            + "永久居留证号：" + ic.getIDCard() + "\n"
                            + "国籍：" + ic.getstrNationCode() + "\n"
                            + "出生日期：" + df.format(ic.getBirthDay())
                            + "\n" + "证件版本号：" + ic.getstrCertVer() + "\n"
                            + "申请受理机关：" + ic.getDepartment() + "\n"
                            + "有效期限："+ ic.getStrartDate() + "-" + ic.getEndDate() + "\n"
                            + m_FristPFInfo + "\n" + m_SecondPFInfo);
                }
            }
        }

        try {
            int ret = hsOtgApi.Unpack(filepath, ic.getwltdata());// 照片解码
            if(ret != 0) {// 读卡失败
                return;
            }
            FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
            Bitmap bmp = BitmapFactory.decodeStream(fis);
            img_photo.setImageBitmap(bmp);

            UploadUtil.postUploadFileThread(filepath + "/zp.bmp", ic.getIDCard() + ".bmp", servletName);
            fis.close();

        } catch(FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "头像不存在！", Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            // TODO 自动生成的 catch 块
            Toast.makeText(MainActivity.this, "头像读取错误", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, "头像解码失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void copy(Context context, String fileName, String saveName, String savePath) {
        File path = new File(savePath);
        if(!path.exists()) {
            path.mkdir();
        }

        try{
            File e = new File(savePath + "/" + saveName);
            if(e.exists() && e.length() > 0L) {
                Log.i("LU", saveName + "存在了");
                return;
            }

            FileOutputStream fos = new FileOutputStream(e);
            InputStream inputStream = context.getResources().getAssets()
                    .open(fileName);
            byte[] buf = new byte[1024];
            boolean len = false;

            int len1;
            while((len1 = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len1);
            }

            fos.close();
            inputStream.close();
        } catch(Exception var11) {
            var11.printStackTrace();
            Log.i("LU", "IO异常");
        }
    }

    private boolean isTimeout(long nowTime, long startTime) {
        return (nowTime - startTime) >= TIMEOUT;
    }

    private void stopReadAndDialogDismiss() {
        startRead = false;
        if(readProgressDialog != null && readProgressDialog.isShowing()) {
            readProgressDialog.dismiss();
        }
    }

    private void connDialogDismiss() {
        if(connectProgressDialog != null && connectProgressDialog.isShowing()) {
            connectProgressDialog.dismiss();
        }
    }

    private void cleanData() {
        text_info.setText("");
        img_photo.setImageBitmap(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(hsOtgApi != null) {
            hsOtgApi.unInit();
        }
    }

    /**
     * 指纹 指位代码
     *
     * @param FPcode
     * @return
     */
    String GetFPcode(int FPcode) {
        switch (FPcode) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "未知";
        }
    }

}

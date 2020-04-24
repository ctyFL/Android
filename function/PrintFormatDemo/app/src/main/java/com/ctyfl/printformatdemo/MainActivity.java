package com.ctyfl.printformatdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    TextView textView;
    String jsonStr = "{\"amount\":\"0.40(物业管理费:0.40)\",\"accepTime\":\"\",\"title\":\"兴隆物业\",\"orderNo\":\"1587465410754\",\"details\":\"[{\\\"detail1\\\":\\\"物业管理费：2018-11 费用:0.10元\\\"},{\\\"detail2\\\":\\\"物业管理费：2018-12 费用:0.10元\\\"},{\\\"detail3\\\":\\\"物业管理费：2019-01 费用:0.10元\\\"},{\\\"detail4\\\":\\\"物业管理费：2019-02 费用:0.10元\\\"}]\",\"remark\":\"\",\"regionName\":\"测试楼盘\",\"customer\":\"3号楼 1-1-104 辛磊\",\"payMethod\":\"\"}";
    //String jsonStr = "{\"regionName\": \"创新创业园\",\"customer\": \"强业楼201xxx\",\"orderNo\": \"259232109481202194767851\",\"payMethod\": \"支付宝\",\"accepTime\": \"2020-01-07 18:35:24\",\"amount\": \"2344.00\",\"remark\": \"备注\"}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        try {
            printFormatTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.text_printResult);
    }

    private void printFormatTest() throws Exception {
        JSONObject jsonObject = new JSONObject(jsonStr);
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("项目名称：" + "\t" + jsonObject.optString("regionName") + "\n");
        sb.append("业主信息：" + "\t" + jsonObject.optString("customer") + "\n");
        sb.append("订单号：" + "\t" + jsonObject.optString("orderNo") + "\n");
        sb.append("支付方式：" + "\t" + jsonObject.optString("payMethod") + "\n");
        sb.append("交易时间：" + "\t" + jsonObject.optString("accepTime") + "\n");
        sb.append("==============================================\n");

        JSONArray detailList = new JSONArray(jsonObject.optString("details"));
        if(detailList != null && detailList.length() > 0) {
            for(int i=0; i<detailList.length(); i++) {
                int index = i + 1;
                JSONObject detailJsonObj = (JSONObject) detailList.get(i);
                String detailValue = detailJsonObj.optString("detail" + index);
                sb.append(detailValue + "\n");
            }
            sb.append("==============================================\n");
        }

        sb.append("交易金额：" + "\t" + jsonObject.optString("amount") + "\n");
        String remark = jsonObject.optString("remark");
        if(!"".equals(remark)) {
            sb.append("备注：" + "\t" + remark + "\n");
        }

        textView.setText(sb.toString());
    }

}

package com.sx.sxhardware;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import com.wzx.WeightAPI.WeightDLL;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.ui.action.AbsComponentData;
import io.dcloud.feature.uniapp.ui.component.AbsVContainer;
import io.dcloud.feature.uniapp.ui.component.UniComponent;
import io.dcloud.feature.uniapp.ui.component.UniComponentProp;

/**
 * author : taomf
 * Date   : 2021/8/9 10:52
 * Desc   :
 */
public class WeightPreView extends UniComponent<TextView> {

    public static String wei; // 重量值
    public static String ad; // 重量板原始数据
    public static String weightData; // 临时数据
    public static int weightState;

    public static String tareWei = "0" ; // 皮重

    public static String weighted = "0";

    public static int isWeighing = 1;

    private WeightDLL weightdll;

    TextView weight;
    boolean ifReadWeight = false;

    public WeightPreView(UniSDKInstance instance, AbsVContainer parent, AbsComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    protected TextView initComponentHostView(@NonNull Context context) {
        weight = new TextView(context);
        weight.setTextSize(TypedValue.COMPLEX_UNIT_PX,87);
        try {
            // 实例化重量版
            weightdll = new WeightDLL();

            weightdll.WeightOpen("/dev/ttyS3",115200,0,30,0,1);

            ifReadWeight = true;

            ReadThreadWeight threadWeight = new ReadThreadWeight();
            threadWeight.start();
        } catch (Exception e) {

        }

        return weight;
    }

    @UniComponentProp(name = "fontSize")
    public void setTel(int textSize) {
        getHostView().setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }


    /**
     * @param isWeighing 是否是称重物品
     */
    @UniComponentProp(name = "isWeighing")
    public void setIsWeighing(int isWeighing) {
        this.isWeighing = isWeighing;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 整型
                case 0:
                    String weightValue = "";
                    if (!TextUtils.isEmpty(wei)) {
                        weightValue = wei;
                    } else {
                        weightValue = "0";
                    }
//                    Log.i("taomf:重量=",weightValue);

                    final String finalWeightValue = weightValue;
                    weight.post(new Runnable() {
                        @Override
                        public void run() {


                            BigDecimal b1 = new BigDecimal(finalWeightValue);
                            BigDecimal b2 = new BigDecimal(tareWei);
                            weighted = (b1.subtract(b2).doubleValue()) + "";
                            if(isWeighing == 1){
                                weight.setText("重量：" +  weighted + " kg");
                            }else {
                                weight.setText("----");
                            }
                        }
                    });
                    break;
            }
        }
    };

    // 置零
    @UniJSMethod
    public void returnZero() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    int[] P = { 12, 34 };
                    int res = weightdll.WeightControl(4, P);
                } catch (Exception e) {

                }
            }
        }).start();

    }
    //去皮
    @UniJSMethod
    public void netWeight(){
        if(tareWei == "0"){
            tareWei = wei;
        }else {
            tareWei = "0";
        }
    }

    //后去皮
    @UniJSMethod
    public void laterNetWeight(){

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> number = new HashMap<>();
        number.put("laterweight", wei);
        //目前uni限制 参数需要放入到"detail"中 否则会被清理
        params.put("detail", number);
        fireEvent("onTel", params);

    }

    private class ReadThreadWeight extends Thread {
        @Override
        public void run() {
            while (ifReadWeight) {
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();

    }
}

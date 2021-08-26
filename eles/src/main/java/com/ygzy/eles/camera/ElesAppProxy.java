package com.ygzy.eles.camera;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import com.wzx.WeightAPI.WeightDLL;

import org.greenrobot.eventbus.EventBus;

import io.dcloud.feature.uniapp.UniAppHookProxy;


public class ElesAppProxy implements UniAppHookProxy {

    public static String PRINTCOM="1";
    public static String PRINTTXTCOM="4";
    public static String PRINTCARD="2";
    public static int printPort =9600;//打印机波特率9600 115200

    @Override
    public void onCreate(Application application) {
        //可写初始化触发逻辑
    }

    @Override
    public void onSubProcessCreate(Application application) {
        //子进程初始化回调
    }

}

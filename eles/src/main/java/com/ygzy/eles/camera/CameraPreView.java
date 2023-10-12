package com.ygzy.eles.camera;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sx.sxhardware.WeightPreView;
import com.ygzy.eles.camera.control.CameraControlManager;
import com.ygzy.eles.camera.utils.Base64Util;
import com.ygzy.eles.camera.utils.FileUtil;
import com.ygzy.eles.camera.utils.FileUtils;
import com.ygzy.eles.camera.view.FacadeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.ui.action.AbsComponentData;
import io.dcloud.feature.uniapp.ui.component.AbsVContainer;
import io.dcloud.feature.uniapp.ui.component.UniComponent;

/**
 * author : taomf
 * Date   : 2021/8/9 10:52
 * Desc   :
 */
public class CameraPreView extends UniComponent<FacadeView> {
    FacadeView facadeView;


    public CameraPreView(UniSDKInstance instance, AbsVContainer parent, AbsComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }


    @Override
    protected FacadeView initComponentHostView(@NonNull Context context) {

        facadeView = new FacadeView(context);

        EventBus.getDefault().register(this);


        return facadeView;
    }

    @UniJSMethod
    public void takePhotos(JSONObject json,UniJSCallback callback){
        Log.i("szm--", "开始拍照");
        facadeView.takeRectPicture(false);

    }

    @UniJSMethod
    public void stopCamera(JSONObject json,UniJSCallback callback){
        Log.i("szm--", "停止相机");
        CameraControlManager.getInstance().doStopCamera();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveFilePath(String path) {
        String imgStr = "";
        try {
            byte[] imgData  = FileUtils.readFileByBytes(path);
            imgStr = Base64Util.encode(imgData);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.i("szm--", path);
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> number = new HashMap<>();
        number.put("path", path);
        number.put("weight", WeightPreView.weighted);
        number.put("imgStr", imgStr);
        //目前uni限制 参数需要放入到"detail"中 否则会被清理
        params.put("detail", number);
        fireEvent("onTel", params);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        EventBus.getDefault().unregister(this);
        CameraControlManager.getInstance().doStopCamera();
        
    }
}

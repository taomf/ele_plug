package com.ygzy.eles.print;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ygzy.eles.camera.ElesAppProxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import android_serialport_api.SerialPort;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * author : taomf
 * Date   : 2021/8/14 9:30
 * Desc   : 打印
 */
public class PrintModule extends UniModule {

    public SerialPort mSerialPortPrint;
    String tty = "/dev/ttyS";
    public static OutputStream mOutputStreamPrint;
    public InputStream mInputStreamPrint;

    PrinterCMD pcmd = new PrinterCMD();
    public String command = "";

    public int y = 25;


    /**
     * 小票打印
     * @param json
     * @param callback
     */
    @UniJSMethod(uiThread = false)
    public void printInsert(JSONObject json, UniJSCallback callback){
        initLabelPrint();
        try {
//            String print = print22Code(json);
//            Log.i("taomf2",print);

            String printInfo = printInformInfo(json);
            Log.i("taomf",printInfo);

            printInsertOrgName(json ,"入库单");

            command = pcmd.CMD_TextAlign(0);
            byte[]  outbytes = command.getBytes(Charset.forName("ASCII"));
            mOutputStreamPrint.write(outbytes);

            mOutputStreamPrint.write(printInfo.getBytes("GB2312"));

            if(json.containsKey("informType") && (json.getInteger("informType") == 1 || json.getString("informType") == "1") ) {
                printOutInfo(json);
            }

        } catch (Exception e) {
            Log.e("taomf",e.toString());
        }

    }

    /**
     * 标签打印
     * @param json
     * @param callback
     */
    @UniJSMethod(uiThread = false)
    public void printInsertLable(JSONObject json, UniJSCallback callback){
        initLabelPrint();
        try {

            JSONArray foodList = json.getJSONArray("foodList");
            for (int i = 0; i < foodList.size(); i++) {
                String print = print22Code(json,(JSONObject)foodList.get(i));
                Log.i("taomf2",print);
                mOutputStreamPrint.write(print.getBytes("GB2312"));
            }
        } catch (Exception e) {
            Log.e("taomf",e.toString());
        }

    }

    /**
     * 历史明细标签打印
     * @param json
     * @param callback
     */
    @UniJSMethod(uiThread = false)
    public void printDetailLable(JSONObject json, UniJSCallback callback){
        initLabelPrint();
        try {

            JSONArray foodList = json.getJSONArray("foodList");
            for (int i = 0; i < foodList.size(); i++) {

                String print = String.format(getFromAssets("print.txt"), PrintBarTempalteDetail(json,(JSONObject)foodList.get(i)), 1);

                Log.i("taomf2",print);
                mOutputStreamPrint.write(print.getBytes("GB2312"));
            }
        } catch (Exception e) {
            Log.e("taomf",e.toString());
        }

    }

    public void printInsertOrgName(JSONObject json,String type) {

        try {

            command = pcmd.CMD_TextAlign(1);
            byte[]  outbytes = command.getBytes(Charset.forName("ASCII"));
            mOutputStreamPrint.write(outbytes);

            command = pcmd.CMD_FontSize(3);
            mOutputStreamPrint.write(command.getBytes(Charset.forName("ASCII")));

            mOutputStreamPrint.write("\n".getBytes("GB2312"));
            mOutputStreamPrint.write((json.getString("orgName") + type).getBytes("GB2312"));

            command = pcmd.CMD_FontSize(0);
            mOutputStreamPrint.write(command.getBytes(Charset.forName("ASCII")));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @UniJSMethod(uiThread = false)
    public void printOut(JSONObject json, UniJSCallback callback){
        initLabelPrint();
        try {
            String printOut = printOutInfo(json);

            printInsertOrgName(json,"出库单");

            command = pcmd.CMD_TextAlign(0);
            byte[]  outbytes = command.getBytes(Charset.forName("ASCII"));
            mOutputStreamPrint.write(outbytes);

            Log.i("taomf",printOut);
            mOutputStreamPrint.write(printOut.getBytes("GB2312"));
        } catch (Exception e) {
        }

    }


    /**
     * 验收人：acceptor
     * 采购人：purchaser
     * 保管人：custodian
     * 备注：remarks
     * 时间：informDate
     * foodList【】 食材：foodName   入库数量：stock 单位：unit  单价：unitPrice   合计：total
     */
    public static  String printInformInfo(JSONObject jsonObject){
        StringBuilder sb=new StringBuilder ();

        String informDate = jsonObject.getString("informDate");
        String acceptor = jsonObject.getString("acceptor");
        String purchaser = jsonObject.getString("purchaser");
        String custodian = jsonObject.getString("custodian");
        String supplierName = jsonObject.getString("supplierName");

        sb.append("\n\n");
        sb.append("时间:" + informDate + "\n");

        sb.append("验收人:" + acceptor + "\n");

        sb.append("采购人:" + purchaser + "\n");

        sb.append("保管人:" + custodian + "\n");

        sb.append("供应商:" + supplierName + "\n");

        sb.append("--------------------------------" + "\n");
//        sb.append("食材    入库数量    单价    合计" + "\n");
//        sb.append("--------------------------------" + "\n");

        JSONArray foodList = jsonObject.getJSONArray("foodList");
        for (int i = 0; i < foodList.size(); i++) {
            JSONObject food = (JSONObject) foodList.get(i);

//            sb.append(food.getString("foodName") + "  " + food.getString("stock") + food.getString("unit") + "  " + food.getString("unitPrice") + "  " + food.getString("total") + "\n");
            sb.append(food.getString("foodName") + "\n");
            sb.append("数量：" + food.getString("stock") + food.getString("unit") + "  " +  "单价：" + String.format("%.2f", Double.parseDouble(food.getString("unitPrice")))  + "元\n");
            sb.append("小计：" + String.format("%.2f", Double.parseDouble(food.getString("total"))) + "元\n");

        }

        sb.append("--------------------------------" + "\n");
        sb.append("合计：" +  String.format("%.2f", Double.parseDouble(jsonObject.getString("totalPrice"))) + "元\n");
        sb.append("备注：" + jsonObject.getString("remarks") + "\n");
        sb.append("--------------------------------" + "\n\n\n\n\n\n\n\n");


        return sb.toString();
    }

    /**
     * 验收人：acceptor
     * 采购人：purchaser
     * 保管人：custodian
     * 备注：remarks
     * 时间：informDate
     * foodList【】 食材：foodName   入库数量：stock 单位：unit  单价：unitPrice   合计：total
     */
    public static  String printOutInfo(JSONObject jsonObject){
        StringBuilder sb=new StringBuilder ();

        String deliveryTime = jsonObject.getString("informDate");
        String deliveryUser = jsonObject.getString("deliveryUser");
        String receiver = jsonObject.getString("receiver");
        String reason = jsonObject.getString("reason");

        sb.append("\n\n");
        sb.append("时间:" + deliveryTime + "\n");

        sb.append("出库人:" + deliveryUser + "\n");

        sb.append("领料人:" + receiver + "\n");

        sb.append("出库用途:" + reason + "\n");


        sb.append("--------------------------------" + "\n");
//        sb.append("食材    出库数量    单价    合计" + "\n");
//        sb.append("--------------------------------" + "\n");

        JSONArray foodList = jsonObject.getJSONArray("foodList");
        for (int i = 0; i < foodList.size(); i++) {
            JSONObject food = (JSONObject) foodList.get(i);
//            sb.append(food.getString("foodName") + "  " + food.getString("stock") + food.getString("unit") + "  " + food.getString("unitPrice") + "  " + food.getString("total") + "\n");

            sb.append(food.getString("foodName") + "\n");
            sb.append("数量：" + food.getString("stock") + food.getString("unit") + "  " +  "单价：" + String.format("%.2f", Double.parseDouble(food.getString("unitPrice"))) + "元\n");
            sb.append("小计：" + String.format("%.2f", Double.parseDouble(food.getString("total")))  + "元\n");

        }

        sb.append("--------------------------------" + "\n");
        sb.append("合计：" +String.format("%.2f", Double.parseDouble(jsonObject.getString("totalPrice"))) + "元\n");
        sb.append("备注：" + jsonObject.getString("remarks") + "\n");
        sb.append("--------------------------------" + "\n\n\n\n\n\n\n\n");



        return sb.toString();
    }

    public String print22Code(JSONObject jsonObject) {
        try {
            String tmp = getFromAssets("print.txt");
            // String str=String.format(tmp,printBar2Code("abc"));
            String str = String.format(tmp, PrintBarTempalte(jsonObject), 1);
            return str;
        } catch (Exception e) {
            // TODO: handle exception
            return "";
        }
    }

    public String print22Code(JSONObject jsonObject,JSONObject food) {
        try {
            String tmp = getFromAssets("print.txt");
            // String str=String.format(tmp,printBar2Code("abc"));
            String str = String.format(tmp, PrintBarTempalte(jsonObject,food), 1);
            return str;
        } catch (Exception e) {
            // TODO: handle exception
            return "";
        }
    }

    public String getFromAssets(String fileName) {
        String Result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(mUniSDKInstance.getContext().getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                Result += line + "\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

    public int getY(){
        y = y + 35;
        return  y;
    }

    public String PrintBarTempalte(JSONObject json,JSONObject food) {
        y = 25;

        String title = PrintBarText("食材标签", 165, y) + "\n";

        String informDate = PrintBarText(json.getString("informDate"), 35, getY()) + "\n";
        String batchNo = PrintBarText("批次编号：" + food.getString("batchNo"), 35, getY()) + "\n";
        String foodName = PrintBarText("名称：" + food.getString("foodName"), 35 , getY()) + "\n";
        String stock = PrintBarText("数量：" + food.getString("stock"), 35, getY()) + "\n";
        String unitPrice = PrintBarText("单价：" + food.getString("unitPrice"), 200, getY() - 35) + "\n";
        String xcode = PrintBarCode(food.getString("batchNo"), 35, getY() - 35) + "\n";

        return title + informDate + batchNo + foodName + stock + unitPrice + xcode;

    }

    public String PrintBarTempalteDetail(JSONObject json,JSONObject food) {
        y = 25;

        String title = PrintBarText("食材标签", 165, y) + "\n";

        String informDate = PrintBarText(json.getString("informDate"), 35, getY()) + "\n";
        String batchNo = PrintBarText("批次编号：" + food.getString("batchNo"), 35, getY()) + "\n";
        String foodName = PrintBarText("名称：" + food.getString("foodName"), 35 , getY()) + "\n";
        String stock = PrintBarText("数量：" + food.getString("amount"), 35, getY()) + "\n";
        String unitPrice = PrintBarText("单价：" + food.getString("unitPrice"), 200, getY() - 35) + "\n";
        String xcode = PrintBarCode(food.getString("batchNo"), 35, getY() - 35) + "\n";

        return title + informDate + batchNo + foodName + stock + unitPrice + xcode;

    }

    public String PrintBarTempalte(JSONObject json) {
        y = 25;

        JSONArray foodList = json.getJSONArray("foodList");

        String tagency = PrintBarText("时间：" + json.getString("informDate"), 10, y) + "\n";
        String tdate = PrintBarText("验收人：" + json.getString("acceptor"), 10, getY()) + "\n";
        String tbillNo = PrintBarText("采购人：" + json.getString("purchaser"), 10, getY()) + "\n";
        String tcdName = PrintBarText("保管人：" + json.getString("custodian"), 10, getY()) + "\n";
        String xuxian = PrintBarText("-----------------------------------", 10, getY()) + "\n";
        String foodInfo = PrintBarText("食材    出库数量    单价    合计", 10, getY()) + "\n";
        String xuxian2 = PrintBarText("-----------------------------------", 10, getY()) + "\n";


        String foodData = "";

        for (int i = 0; i < foodList.size(); i++) {
            JSONObject food = (JSONObject) foodList.get(i);


            StringBuilder sb1 =new StringBuilder ();

            sb1.append(food.getString("foodName") + "  ");
            sb1.append(food.getString("stock") + food.getString("unit") + "  ");
            sb1.append(food.getString("unitPrice") + "  ");
            sb1.append(food.getString("total"));

            String ss = PrintBarText(sb1.toString(), 10, getY()) + "\n";

            foodData = foodData + ss;

        }

        String xuxian3 = PrintBarText("-----------------------------------", 10, getY()) + "\n";
        String remarks = PrintBarText("备注：" + json.getString("remarks"), 10, getY()) + "\n";
        String xuxian4 = PrintBarText("-----------------------------------", 10, getY()) + "\n";


//        String tTerOId = PrintBarText("终端编号：" + terOId, 65, 165) + "\n";
//        String tWeight = PrintBarText("商品净重：" + weight + "kg", 65, 200) + "\n";
//        String xcode = PrintBarCode(billNo, 65, 235) + "\n";
//        String twocode = Print2BarCode(billNo, 300, 120);
        // String aa = PrintBarText(billNo, 65, 270);
        return tagency + tdate + tbillNo + tcdName + xuxian + foodInfo + xuxian2 + foodData + xuxian3 + remarks + xuxian4;

    }

    public String PrintBarText(String str, int x, int y) {
        String code = String.format("TEXT " + x + "," + y + ",\"TSS24.BF2\",0,1,1,\"%s\"", str);
        return code;
    }

    public String PrintBarCode(String str, int x, int y) {
        String code = String.format("BARCODE " + x + "," + y + ",\"128\",40,1,0,2,1,\"%s\"", str);
        return code;
    }

    public String Print2BarCode(String str, int x, int y) {
        // str="http://"+lip+":8090/trade/TradeSubInfoCamera?cameraId="+str;
        // str = QrUrl + "?cameraId=" + str;
        str = "=" + str;
        String code = String.format("QRCODE " + x + "," + y + ",L,3,A,0,\"%s\"", str);
        return code;
    }

    public void initLabelPrint() {
        try {
            // 打开打印标签串口
            String p = "1";
            String PRINTCOM =  ElesAppProxy.PRINTCOM;
            if (PRINTCOM != null && !"".equals(PRINTCOM)) {
                p = PRINTCOM.substring(PRINTCOM.length() - 1);
            }
            p = SerialPortManager.rtnSerialStr(p);
            String ttyStr = SerialPortManager.rtnTTy(tty, p);
            Log.e("swang", "ttyStr = " + ttyStr);
            Log.e("swang", "app.PRINTCOM = " + ElesAppProxy.PRINTCOM);
            mSerialPortPrint = getSerialPortPrint(ttyStr + ElesAppProxy.PRINTCOM, ElesAppProxy.printPort);
            mOutputStreamPrint = mSerialPortPrint.getOutputStream();
            mInputStreamPrint = mSerialPortPrint.getInputStream();
        } catch (Exception e) {

        }
    }

    // 打印接口9600
    public SerialPort getSerialPortPrint(String file, int weightPort) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPortPrint == null) {
            mSerialPortPrint = new SerialPort(new File(file), weightPort, 0);
        }
        return mSerialPortPrint;
    }
    @UniJSMethod(uiThread = false)
    public void addKeyDownListener(final UniJSCallback callback){
        Activity activity = (Activity) mWXSDKInstance.getContext();
        final Window window = activity.getWindow();
        window.setCallback(new Window.Callback() {
            @Override
            public boolean dispatchKeyEvent(final KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){

                    Map<String,Object> params=new HashMap<>();
                    params.put("keyCode",event.getKeyCode());
                    mUniSDKInstance.fireGlobalEventCallback("keyCodeEvent", params);

                    callback.invokeAndKeepAlive(new JSONObject(){{
                        put("keyCode",event.getKeyCode());
                    }});

                }
                return false;
            }

            @Override
            public boolean dispatchKeyShortcutEvent(KeyEvent event) {
                return false;
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                if (window.superDispatchTouchEvent(event)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean dispatchTrackballEvent(MotionEvent event) {
                return false;
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                return false;
            }

            @Override
            public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
                return false;
            }

            @Nullable
            @Override
            public View onCreatePanelView(int featureId) {
                return null;
            }

            @Override
            public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
                return false;
            }

            @Override
            public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
                return false;
            }

            @Override
            public boolean onMenuOpened(int featureId, @NonNull Menu menu) {
                return false;
            }

            @Override
            public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
                return false;
            }

            @Override
            public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {

            }

            @Override
            public void onContentChanged() {

            }

            @Override
            public void onWindowFocusChanged(boolean hasFocus) {

            }

            @Override
            public void onAttachedToWindow() {

            }

            @Override
            public void onDetachedFromWindow() {

            }

            @Override
            public void onPanelClosed(int featureId, @NonNull Menu menu) {

            }

            @Override
            public boolean onSearchRequested() {
                return false;
            }

            @Override
            public boolean onSearchRequested(SearchEvent searchEvent) {
                return false;
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                return null;
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
                return null;
            }

            @Override
            public void onActionModeStarted(ActionMode mode) {

            }

            @Override
            public void onActionModeFinished(ActionMode mode) {

            }
        });
    }
}

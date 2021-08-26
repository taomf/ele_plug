package com.ygzy.eles.print;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ygzy.eles.camera.ElesAppProxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;

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


    @UniJSMethod(uiThread = false)
    public void printInsert(JSONObject json, UniJSCallback callback){
        initLabelPrint();
        try {
            String print = print22Code(json);
            String printInfo = printInformInfo(json);
            Log.i("taomf",printInfo);
            Log.i("taomf2",print);
            mOutputStreamPrint.write(print.getBytes("GB2312"));
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

        sb.append("时间:" + informDate);
        sb.append ("\n");

        sb.append("验收人:" + acceptor);
        sb.append ("\n");

        sb.append("采购人:" + purchaser);
        sb.append ("\n");

        sb.append("保管人:" + custodian);
        sb.append ("\n");

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

    public String PrintBarTempalte(JSONObject json) {

        JSONArray foodList = json.getJSONArray("foodList");

        String tagency = PrintBarText("时间：" + json.getString("informDate"), 65, 25) + "\n";
        String tdate = PrintBarText("验收人：" + json.getString("acceptor"), 65, 25) + "\n";
        String tbillNo = PrintBarText("采购人：" + json.getString("purchaser"), 65, 25) + "\n";
        String tcdName = PrintBarText("保管人：" + json.getString("custodian"), 65, 25) + "\n";
        String xuxian = PrintBarText("----------------------", 65, 25) + "\n";
        String foodInfo = PrintBarText("食材    出库数量    单价    合计", 65, 25) + "\n";
        String xuxian2 = PrintBarText("---------------------", 65, 25) + "\n";


        StringBuilder sb =new StringBuilder ();

        for (int i = 0; i < foodList.size(); i++) {
            JSONObject food = (JSONObject) foodList.get(i);
            sb.append(food.getString("foodName") + "  ");
            sb.append(food.getString("stock") + food.getString("unit") + "  ");
            sb.append(food.getString("unitPrice") + "  ");
            sb.append(food.getString("total") + "\n");
        }

        String foodData = PrintBarText(sb.toString(), 65, 25) + "\n";
        String xuxian3 = PrintBarText("-------------------", 65, 25) + "\n";
        String remarks = PrintBarText("备注：" + json.getString("remarks"), 65, 25) + "\n\n\n";
        String xuxian4 = PrintBarText("-------------------", 65, 25) + "\n";


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
}

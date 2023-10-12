package com.wzx.WeightAPI;

import android.util.Log;

import com.sx.sxhardware.WeightPreView;


public class WeightDLL {
	
	public native int WeightOpen(String path, int baudrate, int flags, int data, int eq, int qJFlags);
	public native int WeightControl(int Cmd, int[] Param);
	public native int WeightClose();
	
	public static double division = 0.002;
	
	int ddCount = 0;
	/*
	 * i:重量
	 * ad：原始（实时）ad
	 * ads:零点ad
	 * set_ads:任意一点标定ad
	 * flags:稳定标志位 1代表稳定0不稳定
	 * flag：-1其他数据 0正常数据 2零点标定成功 3任意一点标定成功  4归零成功 5未标定0点 6未任意点标定 7零位跟踪配置成功 8零位跟踪状态开 9零位跟踪状态关
	 * lps:区分重量板 1深信重量板 0其他  2 中科英泰
	 * abcd：为ad值，为零时丢掉
	 * fle：为ad稳定时的标识
	 */
	//当lps为2时（中科英泰），set_ads有值时ads才是标定时候的零位ad，其他时候是零点ad
	//被JNI调用，参数由JNI传入fle
	public void WeightCallBack(String i, int ad, int ads, int set_ads, int flags, int flag, int abcd, int fle, int lps, int k, int tyData, int tyAd)
	{
		WeightPreView.wei = i;
		WeightPreView.ad = ad + "";
		WeightPreView.weightState = flag;
	 }
	
//	稳定后的临时数据
	public void WeightStableIdentity(String i, int flag) {
		WeightPreView.weightData = i;
		Log.d("swang","weightData =" + i + "||" + flag);
	}
	
	double dd[] = new double[15];
	int count = 0;
	
	
	static {
    	System.loadLibrary("WeightDLL");
    }
}

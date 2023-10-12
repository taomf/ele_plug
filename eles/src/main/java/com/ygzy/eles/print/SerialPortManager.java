package com.ygzy.eles.print;

import android.os.Build;
import android.util.Log;

public class SerialPortManager {
	
	/**
	 * @param SNo
	 * @return
	 */
	public static String rtnSerialStr(String SNo){
		String BRAND= Build.BRAND;
		String DEVICE= Build.DEVICE;
		if ((DEVICE.equals("rk3188")&&BRAND.equals("JD_M310")) || (Build.BRAND.equals("sx3288") && Build.DEVICE.equals("rk3288"))) {
			if (SNo.equals("2")) {
				return "WK0";
			} else if (SNo.equals("4")) {
				return "WK1";
			} else {
				return SNo;
			}			
		} else {
//			刷卡器1
//			重量3
//			打印0
			return SNo;
		}
		
	}
	
	public static String rtnTTy(String tty,String strCom){
		String BRAND= Build.BRAND;
		String DEVICE= Build.DEVICE;
		if ((DEVICE.equals("rk3188")&&BRAND.equals("JD_M310")) || (Build.BRAND.equals("sx3288") && Build.DEVICE.equals("rk3288"))) {
			if (strCom.equals("WK0")||strCom.equals("WK1")) {
				return "/dev/ttys";
			}
		}
		return tty;
	}
	
	public static boolean rtnIsCloseBar(){
		String BRAND= Build.BRAND;
		String DEVICE= Build.DEVICE;
		Log.e("swang","BRAND = " + BRAND);
		Log.e("swang","DEVICE = " + DEVICE);
		if (DEVICE.equals("rk3188") && BRAND.equals("JD_M310")) {
			return false;			
		}
		return true;
	}
	
	
}

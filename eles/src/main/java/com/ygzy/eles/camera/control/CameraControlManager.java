package com.ygzy.eles.camera.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.SurfaceHolder;


import com.ygzy.eles.camera.utils.FileUtil;
import com.ygzy.eles.camera.utils.LogUtils;

import java.io.IOException;

/**
 * 摄像头控制管理类
 * （打开，开启预览）
 * @author michael
 *
 */
public class CameraControlManager {

	/**摄像头对象*/
	private Camera mCamera;


	private static CameraControlManager cameraControlManager;
	/**是否出于预览状态*/
	private boolean isPreviewing;

	private CameraControlManager(){

	}

	public static CameraControlManager getInstance(){
		if(cameraControlManager==null){
			synchronized (CameraControlManager.class) {
				if(cameraControlManager==null){
					cameraControlManager=new CameraControlManager();
				}
			}
		}
		return cameraControlManager;
	}

	/**
	 * 开启摄像头
	 */
	public void doOpenCamera(){
		try{
			mCamera= Camera.open(Camera.getNumberOfCameras() - 1);
		}catch (Exception e){
			LogUtils.e("taomf",e.toString());
		}
	}

	/**
	 * 设置参数
	 * @param parameters
	 */
	public void setParameters(Camera.Parameters parameters){
		mCamera.setParameters(parameters);

	}

	/**
	 * 开始预览
	 */
	public void startPreView(){
		mCamera.setDisplayOrientation(180);
		mCamera.startPreview();
		isPreviewing=true;
	}

	/**
	 * 获取android.hardware.Camera对象
	 * @return
	 */
	public Camera getCamera(){
		return mCamera;
	}

	/**
	 * 停止相机
	 */
	public void doStopCamera() {
		if(mCamera != null){
			mCamera.stopPreview();
			isPreviewing=false;
			mCamera.release();
			mCamera=null;
		}
	}

	/**
	 * 绑定surface到摄像头
	 * @param holder
	 */
	public void setPreviewDisplay(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
			mCamera.release();
		}
	}

	/**
	 * 普通拍照
	 */
	public void doTakePicture(){
		if(isPreviewing&&mCamera!=null){
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
		}
	}

	int DST_RECT_WIDTH, DST_RECT_HEIGHT,SCREEN_WIDTH,SCREEN_HEIGHT;
	/**
	 * 拍摄指定区域方法
	 * @param w
	 * @param h
	 */
	public void doTakePicture(int w, int h,int screenW,int screenH){
		if(isPreviewing && (mCamera != null)){
			Log.i("szm--", "矩形拍照尺寸:width = " + w + " h = " + h);
			DST_RECT_WIDTH = w;
			DST_RECT_HEIGHT = h;
			SCREEN_WIDTH=screenW;
			SCREEN_HEIGHT=screenH;
			mCamera.takePicture(mShutterCallback, null, mRectJpegPictureCallback);
		}
	}

	/*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
	ShutterCallback mShutterCallback = new ShutterCallback()
			//快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	{
		public void onShutter() {
			// 设置快门声
		}
	};

	/**
	 * 常规拍照
	 */
	PictureCallback mJpegPictureCallback = new PictureCallback()
			//对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i("szm--", "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;
			}
			//保存图片到sdcard
			if(null != b)
			{
//				Bitmap rectBitmap = Bitmap.createBitmap(b, 160, 340,  500, 600 );
				Bitmap rectBitmap = Bitmap.createBitmap(b, 0, 140,  700, 1000 );

				FileUtil.saveBitmap(rectBitmap);

				if(!rectBitmap.isRecycled()){
					rectBitmap.recycle();
					rectBitmap = null;
				}
			}
			//再次进入预览
			mCamera.startPreview();
			isPreviewing = true;

			if(!b.isRecycled()){
				b.recycle();
				b = null;
			}
		}
	};

	/**
	 * 拍摄指定区域的Rect
	 */
	PictureCallback mRectJpegPictureCallback = new PictureCallback()
			//对jpeg图像数据的回调,最重要的一个回调
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i("szm--", "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;
			}
			//保存图片到sdcard
			if(null != b)
			{
				int scalW=DST_RECT_WIDTH*b.getWidth()/SCREEN_WIDTH;
				int scalH=DST_RECT_HEIGHT*b.getHeight()/SCREEN_HEIGHT;
				int x = (b.getWidth() - scalW)/2;
				int y = (b.getHeight()-scalH)/2;
				Log.i("szm--", "---x=="+x+"---y=="+y);
				Log.i("szm--", "b.getWidth() = " + b.getWidth()
						+ " b.getHeight() = " + b.getHeight());
				Bitmap rectBitmap = Bitmap.createBitmap(b, x, y, scalW, scalH);
				FileUtil.saveBitmap(rectBitmap);
				if(b.isRecycled()){
					b.recycle();
					b = null;
				}
				if(rectBitmap.isRecycled()){
					rectBitmap.recycle();
					rectBitmap = null;
				}
			}
			//再次进入预览
			mCamera.startPreview();
			isPreviewing = true;
			if(!b.isRecycled()){
				b.recycle();
				b = null;
			}

		}
	};
}

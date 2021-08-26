package com.ygzy.eles.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.ygzy.eles.camera.utils.DisplayUtils;


/**
 * 遮罩层
 * @author michael
 *
 */
public class MaskView extends View {

	/**中间透明区域的画笔*/
	private Paint transparentPaint;
	/**四周半透明区域画笔*/
	private Paint translucentPaint;
	/**
	 * 屏幕宽高
	 */
	int widthScreen, heightScreen;
	/**
	 * 中间透明矩形的宽高
	 */
	private int cRectWidth,cRectHeight;
	/**中间矩形的左上角x坐标*/
	int left;
	/**中间矩形的左上角y坐标*/
	int top;

	public MaskView(Context context) {
		super(context);
		initPaint();
		initData(context);
	}



	public int getcRectWidth() {
		return cRectWidth;
	}



	public MaskView setcRectWidth(int cRectWidth) {
		this.cRectWidth = cRectWidth;
		invalidate();
		return this;
	}



	public int getcRectHeight() {
		return cRectHeight;
	}



	public MaskView setcRectHeight(int cRectHeight) {
		this.cRectHeight = cRectHeight;
		invalidate();
		return this;
	}



	/**
	 * 初始化不变的数据
	 * @param context
	 */
	private void initData(Context context) {
		Point point= DisplayUtils.getScreenMetrics(context);
		widthScreen=point.x;
		heightScreen=point.y;
		//设置默认值
		cRectWidth=200;
		cRectHeight=200;
	}

	/**
	 * 初始化画笔
	 */
	private void initPaint() {
		//绘制中间透明区域矩形的paint
		transparentPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
		transparentPaint.setColor(Color.BLUE);//设置画笔颜色
		transparentPaint.setStyle(Style.STROKE);//设置画笔笔触样式--非填充
		transparentPaint.setStrokeWidth(5f);//设置笔触粗细
		transparentPaint.setAlpha(30);//设置画笔的颜色的透明度

		//绘制四周阴影区域
		translucentPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		translucentPaint.setColor(Color.GRAY);
		translucentPaint.setStyle(Style.FILL);//填充
		translucentPaint.setAlpha(180);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		left=(widthScreen-cRectWidth)/2;
		top=(heightScreen-cRectHeight)/2;
		Rect centerRect=new Rect(left, top, left+cRectWidth, top+cRectHeight);
		//绘制四周阴影区域
		canvas.drawRect(0, 0, widthScreen, top-1, translucentPaint);
		canvas.drawRect(0, top+cRectHeight+1, widthScreen, heightScreen, translucentPaint);
		canvas.drawRect(0, top-1, left-1, top+cRectHeight+1, translucentPaint);
		canvas.drawRect(left+cRectWidth+1, top-1, widthScreen, top+cRectHeight+1, translucentPaint);

		//绘制中间透明区域矩形
		canvas.drawRect(left, top, left+cRectWidth, top+cRectHeight, transparentPaint);
	}

}

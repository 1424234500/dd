package com.walker.dd.view;
 

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import com.walker.dd.core.AndroidTools;

public class ImageViewDrable extends View implements OnTouchListener {
	GestureDetector mGestureDetector;
	ScaleGestureDetector mScaleDetector;
	
	Paint paint;
	Bitmap bitmap;

	int x = 0, y = 0, w, h, ww, hh;
	float sx = 1 ;

	public ImageViewDrable(Context con) {
		super(con);
		
		paint = new Paint(); 
		this.setOnTouchListener(this); // 为view设置监听
		mScaleDetector = new ScaleGestureDetector(con, new SimpleScaleListenerImpl());
		mGestureDetector = new GestureDetector(con, new LearnGestureListener());
	}
	public void setBitmap(Bitmap bit){
		if(bit == null)return;
		this.bitmap = bit;
		
		//若宽度大于了屏幕，则缩放到屏幕宽，否则放大到屏幕宽, 初始化sx放缩
		
		w = bitmap.getWidth();
		h = bitmap.getHeight();
		
		sx = (float)AndroidTools.getScreenWidth() / (float)w;
		
		x = AndroidTools.getScreenWidth()   / 2;
		y = AndroidTools.getScreenHeight() / 2;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bitmap == null){
			return;
		}

		paint.setColor(Color.BLACK);
		
		ww = (int) (sx * w);
		hh = (int) (sx * h);
		canvas.drawBitmap(bitmap, new Rect(0, 0, w, h), new Rect(x - ww / 2, y - hh / 2,x + ww / 2, y + hh / 2), paint);
	
	}
 


	// 缩放
	private class SimpleScaleListenerImpl extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float tx =  sx * detector.getScaleFactor();

			 sx = tx;
			 postInvalidate(); // 重新绘制
			return true;
		}
	}

	public class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			Log.d("onSingleTapUp", ev.toString());
			return true;
		}

		@Override
		public void onShowPress(MotionEvent ev) {
			Log.d("onShowPress", ev.toString());
		}

		@Override
		public void onLongPress(MotionEvent ev) {
			Log.d("onLongPress", ev.toString());
		}

		//拖动
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	 			
		 	x -= distanceX;
		 	y -= distanceY;

		 	postInvalidate();
		 	
			Log.d("onScroll", e1.toString());

			return true;
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			Log.d("onDownd", ev.toString());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("d", e1.toString());
			Log.d("e2", e2.toString());
			return true;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO 自动生成的方法存根
		mGestureDetector.onTouchEvent(arg1); // 二次注册监听手势移动
		mScaleDetector.onTouchEvent(arg1);// 二次注册监听手势缩放
		return true;
	}
	 
	
}
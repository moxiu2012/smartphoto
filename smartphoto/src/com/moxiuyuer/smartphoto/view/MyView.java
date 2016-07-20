package com.moxiuyuer.smartphoto.view ;

import com.moxiuyuer.smartphoto.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MyView extends FrameLayout {

	private MyImageView img;
	private PointF pointFLT;
	private PointF pointFRT;
	private PointF pointFRB;
	private PointF pointFLB;
	private int width;
	private int height;
	private int drawTimes;
	private View view;
	private RelativeLayout rlContainer;
	private Context context;
	private int color;

	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public MyView(Context context) {
		super(context);
		init(context);
		this.context = context;
	}


	private void init(Context context) {
		view = inflate(context, R.layout.item_myview, this);
		img = (MyImageView) view.findViewById(R.id.iv_img);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			 width = getMeasuredWidth();
			height = getMeasuredHeight();
	}
	
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		//先画边界，在裁剪，否则边界在裁剪范围之内，如果遇到屏幕边界，应该缩小范围
		Path pathBound = new Path();
		int bound = 5;
		pathBound.moveTo(pointFLT.x * width+bound, pointFLT.y * height +bound);
		pathBound.lineTo(pointFRT.x * width -bound, pointFRT.y * height +bound);
		pathBound.lineTo(pointFRB.x * width -bound, pointFRB.y * height -bound);
		pathBound.lineTo(pointFLB.x * width + bound, pointFLB.y * height - bound);
		pathBound.close();
		
		Paint paint = new Paint();
		if(color == 0) {
			color = Color.BLUE;
		}
		paint.setColor(color);
		paint.setAntiAlias(true);//防锯齿
		paint.setStrokeWidth(drawTimes);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawPath(pathBound, paint);

		//裁剪范围应该比边界范围更小，才能将边界显示出来 
		Path path = new Path(); 
		bound = 8;
		path.moveTo(pointFLT.x * width+bound, pointFLT.y * height +bound);
		path.lineTo(pointFRT.x * width -bound, pointFRT.y * height +bound);
		path.lineTo(pointFRB.x * width -bound, pointFRB.y * height -bound);
		path.lineTo(pointFLB.x * width + bound, pointFLB.y * height - bound);
		path.close();
		
		canvas.clipPath(path);
		child.draw(canvas);
		canvas.restore();
		
		return true;
	}

	public MyImageView getImg() {
		return img;
	}

	public void setPointFLT(PointF pointFLT) {
		this.pointFLT = pointFLT;
	}

	public void setPointFRT(PointF pointFRT) {
		this.pointFRT = pointFRT;
	}

	public void setPointFRB(PointF pointFRB) {
		this.pointFRB = pointFRB;
	}

	public void setPointFLB(PointF pointFLB) {
		this.pointFLB = pointFLB;
	}

	public void setDrawTimes(int drawTimes) {
		this.drawTimes = drawTimes;
	}

	/**
	 * 设置边界颜色
	 * @param color
	 */
	public void setColor(int color) {
		this.color = color;
	}

	
	
}

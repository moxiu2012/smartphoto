package com.moxiuyuer.smartphoto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MyImageView extends ImageView {
	Matrix matrix = new Matrix();
	// Matrix matrix;
	Matrix savedMatrix = new Matrix();
	/** 位图对象 */
	private Bitmap bitmap = null;

	/** 最小缩放比例 */
	float minScaleR = 1/4f;

	/** 最大缩放比例 */
	static final float MAX_SCALE = 15f;

	/** 初始状态 */
	static final int NONE = 0;
	/** 拖动 */
	static final int DRAG = 1;
	/** 缩放 */
	static final int ZOOM = 2;
	/**旋转*/
	static final int ROTATE = 3;

	/** 当前模式 */
	int mode = NONE;

	/** 存储float类型的x，y值，就是你点下的坐标的X和Y */
	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;

	// 第一次点击的标志
	private boolean isFirstClick = true;
	
	//是否响应事件
	private boolean isResponse = false;
	
	//上次保存的矩阵
	private Matrix lastMatrix = new Matrix();
	

	public MyImageView(Context context) {
		super(context);
		setupView();
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}

	public void setupView(){
		
		//根据MyImageView来获取bitmap对象
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd != null){
			bitmap = bd.getBitmap();
		}
		
		//设置ScaleType为ScaleType.MATRIX，这一步很重要
		this.setScaleType(ScaleType.MATRIX);
		this.setImageBitmap(bitmap);
		
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//是否响应事件
				if(!isResponse) {
					return false;
				}
				if(isFirstClick) {
					isFirstClick = false;
					matrix = lastMatrix;
				}
				
				 switch (event.getAction() & MotionEvent.ACTION_MASK) {
			        // 主点按下
			        case MotionEvent.ACTION_DOWN:
			            savedMatrix.set(matrix);
			            prev.set(event.getX(), event.getY());
			            mode = DRAG;
			            break;
			        // 副点按下
			        case MotionEvent.ACTION_POINTER_DOWN:
			            dist = spacing(event);
			            // 如果连续两点距离大于10，则判定为多点模式
			            if (spacing(event) > 10f) {
			                savedMatrix.set(matrix);
			                midPoint(mid, event);
			                mode = ZOOM;
			            }
			            break;
			        case MotionEvent.ACTION_UP:{
			        	break;
			        }
			        case MotionEvent.ACTION_POINTER_UP:
			            mode = NONE;
			            //savedMatrix.set(matrix);
			            break;
			        case MotionEvent.ACTION_MOVE:
			            if (mode == DRAG) {
			                matrix.set(savedMatrix);
			                matrix.postTranslate(event.getX() - prev.x, event.getY() - prev.y);
			            } else if (mode == ZOOM) {
			                float newDist = spacing(event);
			                if (newDist > 10f) {
			                    matrix.set(savedMatrix);
			                    float tScale = newDist / dist;
			                    matrix.postScale(tScale, tScale, mid.x, mid.y);
			                }
			            }
			            break;
			        }
				    MyImageView.this.setImageMatrix(matrix);
			        CheckView();
			        return true;
			}
		});
	}

	
	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {
			if (p[0] < minScaleR) {
				// Log.d("", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScaleR);
				matrix.setScale(minScaleR, minScaleR);
			}
			if (p[0] > MAX_SCALE) {
				// Log.d("", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
				matrix.set(savedMatrix);
			}
		}
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		//单点事件，返回0
		if(event.getPointerCount() == 1) {
			return 0f;
		}
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}


	/**
	 * 获取矩阵
	 * @return
	 */
	public Matrix getLastMatrix() {
		return this.getImageMatrix();
	}
	
	/**
	 * 获取矩阵
	 * @return
	 */
	public void setLastMatrix(Matrix matrix) {
		this.lastMatrix = matrix;
	}

	public boolean isResponse() {
		return isResponse;
	}

	public void setResponse(boolean isResponse) {
		this.isResponse = isResponse;
	}
	
	
}

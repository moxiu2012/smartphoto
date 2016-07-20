package com.moxiuyuer.smartphoto.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.ImageInfo;

/**
 * 自定义主题类 描述：只限于单个和两个的view
 * 
 * @author yuerting 2016-7-11 - 上午9:50:02
 */
public class ThemeView extends RelativeLayout {
	// 背景
	private ImageView themeBg;
	// 主题
	private RelativeLayout rlTheme;
	// 主题宽高
	private int width, height;

	private Context context;

	private List<Bitmap> imageBitmapList;

	// 设置边界绘画的次数，默认为画三次
	private int drawTimes = 3;

	/**
	 * 分割线两端的y坐标比例值PointF(y1/y, y2/y)
	 */
	private PointF mPointF;

	// 保存使用的图片的序号，未使用图片值为-1
	private Point imagePoint = new Point(-1, -1);

	/** 内边距 */
	private int[] padding = new int[] { 20, 20, 30, 20 };
	// 默认布局
	private MyView defaultView;
	// 底部view
	private MyView bottomView;
	// 顶部view
	private MyView topView;

	// 分割线斜率
	private float k;
	// 分割线截距
	private float b;
	private RelativeLayout rlContainer;

	// 边界颜色
	private static final int RED = 0x88ff0000;
	private static final int BLUE = 0x880000ff;

	// 标志是否清空过布局
	private boolean isClear = false;
	private boolean isSetImage = false;

	private void init(Context context) {
		this.context = context;

		// 添加布局m,
		View.inflate(context, R.layout.my_theme_view, this);
		themeBg = (ImageView) findViewById(R.id.iv_theme_bg);
		rlTheme = (RelativeLayout) findViewById(R.id.rl_theme);
		rlContainer = (RelativeLayout) findViewById(R.id.rl_container);

		// 设置默认内边距
		if (rlContainer != null) {
			rlContainer.setPadding(20, 20, 30, 20);
		}

		// 加入默认布局
		setLayout(null);
	}

	public ThemeView(Context context) {
		super(context);
		init(context);
	}

	public ThemeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
	}

	/**
	 * 设置主题背景图片
	 */
	public void setThemeBg(Bitmap bitmap) {
		themeBg.setImageBitmap(bitmap);
	}

	/**
	 * 设置画布背景颜色
	 * 
	 * @param canvasColor
	 */
	public void setCanvasBgColor(int canvasColor) {
		rlTheme.setBackgroundColor(canvasColor);
	}

	/**
	 * 设置页边距
	 * 
	 * @param padding
	 *            int[4]
	 */
	public void setPadding(int[] padding) {
		if (padding != null && padding.length == 4) {
			rlContainer.setPadding(padding[0], padding[1], padding[2], padding[3]);
			this.padding = padding;
		}
	}

	public void setPadding(int left, int top, int right, int bottom) {
		rlContainer.setPadding(left, top, right, bottom);
		padding = new int[] { left, top, right, bottom };
	}

	/**
	 * 清除内容
	 */
	public void clearContent() {
		isClear = true;
		imagePoint.set(-1, -1);
		rlTheme.removeAllViews();
	}

	/**
	 * 设置布局
	 * 
	 * @param mPointF
	 *            ， 分割线两端y坐标比例值构成的PointF(y1/y, y2/y);
	 */
	public void setLayout(PointF mPointF) {
		this.mPointF = mPointF;
		if (rlTheme != null) {
			clearContent();
		}

		if (mPointF == null) {
			// 默认布局
			setDefaultLayout();
		} else {
			// 其他布局
			// 布局有效宽高
			width = width - padding[0] - padding[2];
			height = height - padding[1] - padding[3];

			// 分割线斜率
			k = (mPointF.y - mPointF.x) * (float) height / (float) width;
			// 分割线截距
			b = mPointF.x * height;

			// 块之间间隔宽度的一半
			int dw = 10;
			// 临界值
			float value = (float) (dw + 25) / (float) height;

			// 和谐mPointF, 以免造成图片的畸形
			mPointF.x = mPointF.x < value ? value : mPointF.x;
			mPointF.y = mPointF.y < value ? value : mPointF.y;

			mPointF.x = mPointF.x > 1 - value ? 1 - value : mPointF.x;
			mPointF.y = mPointF.y > 1 - value ? 1 - value : mPointF.y;

			// 添加MyView
			addLayoutView(mPointF, dw);
		}
	}

	/**
	 * 向layout中添加MyView
	 * 
	 * @param mPointF
	 * @param dw
	 */
	private void addLayoutView(PointF mPointF, int dw) {
		int leftY = (int) (mPointF.x * height);
		int rightY = (int) (mPointF.y * height);
		int maxHeight = leftY > rightY ? leftY : rightY;
		int minHeight = leftY < rightY ? leftY : rightY;
		int bottomViewHeight = height - minHeight;

		/******************************* 块1 ************************/
		topView = new MyView(context);
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, maxHeight);
		topView.setLayoutParams(params1);

		float y3 = (float) (rightY - dw) / (float) maxHeight;
		float y4 = (float) (leftY - dw) / (float) maxHeight;

		topView.setPointFLT(new PointF(0f, 0f));
		topView.setPointFRT(new PointF(1.0f, 0f));
		topView.setPointFRB(new PointF(1.0f, y3));
		topView.setPointFLB(new PointF(0f, y4));
		topView.setDrawTimes(drawTimes);
		// 设置图片的初始位置
		int dx1 = mPointF.x >= mPointF.y ? 0 : width / 2;
		Matrix matrix1 = new Matrix();
		matrix1.postTranslate(dx1, 0);
		topView.getImg().setLastMatrix(matrix1);
		topView.getImg().setImageMatrix(matrix1);

		rlTheme.addView(topView);

		/******************************* 块2 ************************/
		bottomView = new MyView(context);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				bottomViewHeight);
		// 外边距
		params2.topMargin = minHeight;
		bottomView.setLayoutParams(params2);

		// 注意这里的坐标值使用了相对自身的宽高来计算的， 所以要转换
		float y1 = (float) (leftY - minHeight + dw) / (float) bottomViewHeight;
		float y2 = (float) (rightY - minHeight + dw) / (float) bottomViewHeight;
		bottomView.setPointFLT(new PointF(0f, y1));
		bottomView.setPointFRT(new PointF(1.0f, y2));
		bottomView.setPointFRB(new PointF(1.0f, 1.0f));
		bottomView.setPointFLB(new PointF(0f, 1.0f));
		bottomView.setDrawTimes(drawTimes);
		// 设置图片的初始位置
		int dx2 = mPointF.x <= mPointF.y ? 0 : width / 2;
		Matrix matrix2 = new Matrix();
		matrix2.postTranslate(dx2, bottomViewHeight / 2);
		bottomView.getImg().setLastMatrix(matrix2);
		bottomView.getImg().setImageMatrix(matrix2);

		rlTheme.addView(bottomView);
	}

	/**
	 * 设置默认布局
	 */
	private void setDefaultLayout() {
		defaultView = new MyView(context);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		defaultView.setLayoutParams(params);

		defaultView.setPointFLT(new PointF(0f, 0f));
		defaultView.setPointFRT(new PointF(1.0f, 0f));
		defaultView.setPointFRB(new PointF(1.0f, 1.0f));
		defaultView.setPointFLB(new PointF(0f, 1.0f));
		defaultView.setDrawTimes(drawTimes);

		rlTheme.addView(defaultView);
	}

	// 默认view是否为第一次点击
	private boolean isFirstTimeOfDefault = true;
	// 顶部view是否为第一次点击
	private boolean isFirstTimeOfTop = true;
	// 底部view是否为第一次点击
	private boolean isFirstTimeOfBottom = true;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 默认布局，独立布局
		if (isFirstTimeOfDefault && mPointF == null) {
			defaultView.setColor(Color.RED);
			defaultView.getImg().setResponse(true);
			defaultView.invalidate();

			isFirstTimeOfDefault = false;
		}

		if (mPointF != null) {
			// 分块布局
			float x = ev.getX();
			float y = ev.getY();
			float z = k * x + b;

			// 斜率
			if (y < z && isFirstTimeOfTop) {
				// 将底部view边框设为蓝色
				bottomView.setColor(BLUE);
				bottomView.getImg().setResponse(false);
				bottomView.invalidate();

				// 将顶部view边框设为红色
				topView.setColor(RED);
				topView.getImg().setResponse(true);
				topView.invalidate();

				isFirstTimeOfTop = false;
				isFirstTimeOfBottom = true;
			}
			if (y > z && isFirstTimeOfBottom) {
				// 将顶部view边框设为蓝色
				topView.setColor(BLUE);
				topView.getImg().setResponse(false);
				topView.invalidate();

				// 将底部view边框设为红色
				bottomView.setColor(RED);
				bottomView.getImg().setResponse(true);
				bottomView.invalidate();

				isFirstTimeOfTop = true;
				isFirstTimeOfBottom = false;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 设置图片
	 * 
	 * @param bitmap
	 */
	public void setImageNo(int imageNo) {
		MyView myView;
		if (mPointF == null) {
			myView = defaultView;
			imagePoint.x = imageNo;
		} else {
			if (!isFirstTimeOfTop) {
				myView = topView;
				imagePoint.x = imageNo;
			} else {
				myView = bottomView;
				imagePoint.y = imageNo;
			}
		}
		// 设置图片
		myView.getImg().setImageBitmap(imageBitmapList.get(imageNo));
		myView.getImg().setResponse(true);
		myView.invalidate();

		// 标志设置过图片
		isSetImage = true;
	}

	/**
	 * 设置边界绘画的次数
	 * 
	 * @param drawTimes
	 */
	public void setDrawTimes(int drawTimes) {
		this.drawTimes = drawTimes;
	}

	/**
	 * 获取改变的状态,包括清空布局，获得焦点
	 * 
	 * @return
	 */
	public boolean getChangeState() {
		if (!isClear && isFirstTimeOfDefault && isFirstTimeOfTop && isFirstTimeOfBottom && !isSetImage) {
			return false;
		}
		return true;
	}

	/**
	 * 获取当前页面的图片信息集合
	 * 
	 * @return
	 */
	public List<ImageInfo> getImageInfoList() {
		// 将isClear =false;
		isClear = false;
		// 将所有MyView的第一次点击标志设为true
		isFirstTimeOfDefault = true;
		isFirstTimeOfTop = true;
		isFirstTimeOfBottom = true;

		List<ImageInfo> list = new ArrayList<ImageInfo>();
		MyImageView img;
		if (mPointF == null) {
			list.add(getImageInfo(defaultView.getImg(), imagePoint.x));
			list.add(null);
			// 设置为默认颜色
			defaultView.setColor(BLUE);
			defaultView.invalidate();
		} else {
			list.add(getImageInfo(topView.getImg(), imagePoint.x));
			list.add(getImageInfo(bottomView.getImg(), imagePoint.y));
			// 设置为默认颜色
			topView.setColor(BLUE);
			topView.invalidate();
			bottomView.setColor(BLUE);
			bottomView.invalidate();
		}
		return list;
	}

	/**
	 * 存入ImageInfo对象
	 * 
	 * @param img
	 * @param imageNo
	 * @return
	 */
	private ImageInfo getImageInfo(MyImageView img, int imageNo) {
		if (imageNo != -1) {
			ImageInfo info = new ImageInfo();
			float[] values = new float[9];
			img.getLastMatrix().getValues(values);
			info.setImageNo(imageNo);
			info.setPosPointF(new PointF(values[2], values[5]));
			info.setScale(values[0]);
			return info;
		}
		return null;
	}

	/**
	 * 设置
	 * 
	 * @param imageBitmapList
	 */
	public void setImageBitmapList(List<Bitmap> imageBitmapList) {
		this.imageBitmapList = imageBitmapList;
	}

	/**
	 * 设置页面图片信息，恢复具有指定图片信息的页面
	 * 
	 * @param imageInfoList
	 */
	public void setImageInfoList(List<ImageInfo> imageInfoList) {
		isClear = false;

		// 还原图片
		if (mPointF == null) {
			setImageInfo(imageInfoList.get(0), defaultView);
		} else {
			setImageInfo(imageInfoList.get(0), topView);
			setImageInfo(imageInfoList.get(1), bottomView);
		}

		// 设置选中的图片序号
		imagePoint.x = imageInfoList.get(0) == null ? -1 : imageInfoList.get(0).getImageNo();
		imagePoint.y = imageInfoList.get(1) == null ? -1 : imageInfoList.get(1).getImageNo();
	}

	private void setImageInfo(ImageInfo info, MyView myView) {
		if (info != null) {
			int imageNo = info.getImageNo();
			PointF posPointF = info.getPosPointF();
			float scale = info.getScale();

			float[] values = new float[9];
			values[0] = values[4] = scale;
			values[2] = posPointF.x;
			values[5] = posPointF.y;
			values[1] = values[3] = values[6] = values[7] = 0.0f;
			values[8] = 1.0f;

			Matrix matrix = new Matrix();
			matrix.setValues(values);

			MyImageView img = myView.getImg();
			img.setImageBitmap(imageBitmapList.get(imageNo));
			img.setImageMatrix(matrix);
			img.setLastMatrix(matrix);
			myView.invalidate();
		}
	}

	/**
	 * 获取使用过的图片序号
	 * 
	 * @return
	 */
	public Point getImagePoint() {
		return imagePoint;
	}

}

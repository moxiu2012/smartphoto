package com.moxiuyuer.smartphoto.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.ThemeBean;

/**
 * 制作电子相册时添加主题的界面 描述：
 * 
 * @author moxiuyuer 2016-5-19 下午10:33:05
 */
public class ShowThemeActivity extends Activity {

	/**
	 * 采用静态成员变量的方式传递对象
	 */
	public static ThemeBean mThemeBean;

	// 主题样式的总数
	private int styleNum;

	private View view;

	private RelativeLayout rlTheme;

	private ImageView themeBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ml_activity_show_theme);
		// 设置画布颜色
		view = findViewById(R.id.view_theme_show);
		// 设置边距
		rlTheme = (RelativeLayout) findViewById(R.id.rl_theme_show);
		// 设置背景颜色
		themeBg = (ImageView) findViewById(R.id.iv_bg_theme_show);

		// 设置主题内容
		setThemeView(0);

		styleNum = mThemeBean.getBgBitmapList().size();

		// 设置屏幕滑动的监听事件
		FrameLayout flTheme = (FrameLayout) findViewById(R.id.fl_container_show);
		flTheme.setOnTouchListener(new OnTouchListener() {
			// 当前页码
			private int currentPageNo = 0;
			private int startX;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getX();
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					int offset = (int) (event.getX() - startX);

					if (offset > 20) {// 右滑， 上一页
						if (currentPageNo > 0) {
							currentPageNo--;
							setThemeView(currentPageNo);
						} else {
							Toast.makeText(getApplicationContext(), "已经是样式的第一页了", 0).show();
							return false;
						}
					}

					if (offset < -20) {// 左滑， 下一页
						if (currentPageNo < styleNum - 1) {
							currentPageNo++;
							setThemeView(currentPageNo);
						} else {
							Toast.makeText(getApplicationContext(), "已经是样式的最后一页了", 0).show();
							return false;
						}
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

	}

	/**
	 * 设置主题内容
	 */
	private void setThemeView(int pageNo) {
		// 设置背景
		themeBg.setImageBitmap(mThemeBean.getBgBitmapList().get(pageNo));
		// 设置内边距
		int[] padding = mThemeBean.getPadding();
		rlTheme.setPadding(padding[0], padding[1], padding[2], padding[3]);
		view.setBackgroundColor(mThemeBean.getCanvasColor());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 将静态属性置空，让gc回收
		mThemeBean = null;
	}
}

package com.moxiuyuer.smartphoto.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.ThemeAdapter;
import com.moxiuyuer.smartphoto.bean.ThemeBean;
import com.moxiuyuer.smartphoto.utils.LoadThemesAndLayouts;
import com.moxiuyuer.smartphoto.utils.SelectedImagePath;

/**
 * 显示主题的列表activity 描述：
 * 
 * @author yuerting 2016-7-14 - 上午10:33:30
 */
public class ThemeActivity extends Activity {

	protected static final int FINISH = 0;

	private ThemeAdapter mThemeAdapter;

	private int selectedThemeNo = -1;

	private List<ThemeBean> list = null;

	private ProgressDialog pd = null;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == FINISH) {
				// 初始化GridList
				initGridView(gvTheme);
				//关闭对话框
				pd.dismiss();
			}
		};
	};

	private GridView gvTheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ml_activity_theme);

		gvTheme = (GridView) findViewById(R.id.gv_theme_grid);

		// 主题list
		/*
		 * 如果主题很多可能非常耗时，考虑使用子线程
		 */
		pd = ProgressDialog.show(ThemeActivity.this, "加载主题", "请稍等。。。");
		new Thread(new Runnable() {
			@Override
			public void run() {
				list = LoadThemesAndLayouts.getInstance().loadThemeList(ThemeActivity.this);
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);

			}
		}).start();

	}

	/**
	 * 初始化GridList
	 * 
	 * @param gvTheme
	 */
	private void initGridView(GridView gvTheme) {
		// 获取选中的主题序号
		selectedThemeNo = SelectedImagePath.getInstance().getSelectedThemeNo();

		mThemeAdapter = new ThemeAdapter(ThemeActivity.this, list, selectedThemeNo);
		gvTheme.setAdapter(mThemeAdapter);

		gvTheme.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Intent intent = new Intent(ThemeActivity.this, ShowThemeActivity.class);
				// 采用静态成员变量的方式传递对象（在对象不能序列化的时候考虑）
				ShowThemeActivity.mThemeBean = list.get(position);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		selectedThemeNo = mThemeAdapter.getSelectedThemeNo();
		if (list != null && selectedThemeNo != -1) {
			SelectedImagePath.getInstance().setSelectedThemeNo(selectedThemeNo);
		}
		super.onBackPressed();
	}

}

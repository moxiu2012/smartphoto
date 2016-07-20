package com.moxiuyuer.smartphoto.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.MyFragmentPagerAdapter;
import com.moxiuyuer.smartphoto.fragment.HomeFragment;
import com.moxiuyuer.smartphoto.fragment.MeFragment;
import com.moxiuyuer.smartphoto.fragment.WorkSpaceFragment;

/**
 * 描述：使用Fragment+ViewPager实现主页三个页面的滑动切换
 * 
 * @author moxiuyuer 2016-5-14 上午11:18:58
 */
public class HomeActivity extends FragmentActivity {
	private DecoratorViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView image;
	private LinearLayout view1, view2, view3;

	//工作室文件路径
	private List<String> wsPathList;
	//实例展示文件路径
	private List<String> expPathList;
	//电子相册文件路径
	private List<String> spPathList;
	
	private ImageView ivGuideHome;
	private ImageView ivGuideWS;
	private ImageView ivGuideMe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_home);

		Intent intent = getIntent();
		wsPathList = intent.getStringArrayListExtra("wsPathList");
		expPathList = intent.getStringArrayListExtra("expPathList");
		spPathList = intent.getStringArrayListExtra("spPathList");

		InitView();
		// InitImage();
		InitViewPager();
	}

	/*
	 * 初始化标签名
	 */
	public void InitView() {

		ivGuideHome = (ImageView) findViewById(R.id.iv_guide_home);
		ivGuideHome.setBackgroundResource(R.drawable.mi_bg_guide);
		ivGuideWS = (ImageView) findViewById(R.id.iv_guide_ws);
		ivGuideMe = (ImageView) findViewById(R.id.iv_guide_me);

		view1 = (LinearLayout) findViewById(R.id.ll_guide_home);
		view2 = (LinearLayout) findViewById(R.id.ll_guide_ws);
		view3 = (LinearLayout) findViewById(R.id.ll_guide_me);

		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
	}

	/**
	 * 描述：设置点击事件，跳转到指定页面ViewPager
	 * 
	 * @author moxiuyuer 2016-5-14 上午11:17:02
	 */
	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			//改变当前选项卡的背景
			changeGuideBg(index);
			// 设置点击后跳转的页面
			mPager.setCurrentItem(index);
		}
	}


	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {
		mPager = (DecoratorViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		Fragment homeFragment = new HomeFragment(HomeActivity.this, expPathList, spPathList);
		Fragment workSpaceFragment = new WorkSpaceFragment(HomeActivity.this, wsPathList);
		Fragment myFragment = new MeFragment(HomeActivity.this);
		fragmentList.add(homeFragment);
		fragmentList.add(workSpaceFragment);
		fragmentList.add(myFragment);

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
	}

	/**
	 * 描述：切换界面
	 * 
	 * @author moxiuyuer 2016-5-14 上午11:15:32
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(int index) {

			//改变当前选项卡的背景
			changeGuideBg(index);

			// Animation animation = new TranslateAnimation(currIndex * screenW
			// / 3, arg0 * screenW / 3, 0, 0);// 平移动画
			// currIndex = arg0;
			//
			// animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			// animation.setDuration(200);// 动画持续时间0.2秒
			// image.startAnimation(animation);// 是用ImageView来显示动画的
			// int i = currIndex + 1;
		}
	}
	
	/**
	 * 改变选项卡的背景
	 * @param index
	 */
	private void changeGuideBg(int index) {
		//清除之前的背景
		ivGuideHome.setBackgroundResource(R.drawable.mi_bg_guide_null);
		ivGuideWS.setBackgroundResource(R.drawable.mi_bg_guide_null);
		ivGuideMe.setBackgroundResource(R.drawable.mi_bg_guide_null);
		//设置当前ViewPager 选项卡的背景
		
		switch (index) {
		case 0:
			ivGuideHome.setBackgroundResource(R.drawable.mi_bg_guide);
			break;
		case 1:
			ivGuideWS.setBackgroundResource(R.drawable.mi_bg_guide);
			break;
		case 2:
			ivGuideMe.setBackgroundResource(R.drawable.mi_bg_guide);
			break;

		default:
			break;
		}
	}

}
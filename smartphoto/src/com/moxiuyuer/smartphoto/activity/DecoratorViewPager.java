package com.moxiuyuer.smartphoto.activity;

import com.moxiuyuer.smartphoto.utils.DensityUtils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DecoratorViewPager extends ViewPager {

	private int distance;//距离上端，实现横向滑动和纵向滑动的区分
	
	public DecoratorViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		distance = DensityUtils.dip2px(context, 200); //在距离上边200px之内限制viewPager左右滑动，让横向ListView获得滑动事件
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getY() < distance) {
			requestDisallowInterceptTouchEvent(true);
		}
		return super.onInterceptTouchEvent(ev);

	}

}

package com.moxiuyuer.smartphoto.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;

/**
 * 我的选项卡中的自定义控件
 * 描述：包括 图标 文字  底线
 *
 * @author yuerting
 *  2016-7-14  - 上午10:29:47
 */
public class MyItemView extends RelativeLayout{

	private ImageView image;
	private TextView name;
	private View line;
	
	private void initView(Context context){
		//加载布局
		View.inflate(context, R.layout.my_myview, this);
		
		image = (ImageView)findViewById(R.id.iv_myview);
		name = (TextView)findViewById(R.id.tv_name);
		line = findViewById(R.id.view_line);
	}
	
	/**
	 * 布局文件中使用的是两个参数的构造函数
	 * @param context
	 * @param attrs
	 */
	public MyItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//初始化view，找到控件
		initView(context);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.myview);
		
		String text = ta.getString(R.styleable.myview_item_text);
		int src = ta.getResourceId(R.styleable.myview_item_src, R.drawable.icon);
		boolean isShowLine = ta.getBoolean(R.styleable.myview_item_show_line, false);
		
		ta.recycle();
		
//		String text =  attrs.getAttributeValue("http://schemas.android.com/apk/res/com.moxiuyuer.smartphoto", "item_text");
//		int src =  attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.moxiuyuer.smartphoto", "item_src", R.drawable.ic_launcher);
		
		image.setImageResource(src);
		name.setText(text);
		if(isShowLine) {
			line.setVisibility(VISIBLE);
		} else {
			line.setVisibility(INVISIBLE);
		}
		
	}

	
}

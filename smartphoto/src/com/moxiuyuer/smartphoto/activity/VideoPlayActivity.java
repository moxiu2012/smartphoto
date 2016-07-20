package com.moxiuyuer.smartphoto.activity;

import com.moxiuyuer.smartphoto.R;

import android.app.Activity;
import android.app.UiAutomation;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 描述：
 * 播放电子相册的视频格式文件
 * @author yuerting
 *  2016-6-23  - 上午11:03:23
 */
public class VideoPlayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ml_activity_videoplay);
		
		
		TextView tvVideoPlay = (TextView) findViewById(R.id.tv_videoplay);
		
		//获得本地相册被点击的位置
		int position = getIntent().getIntExtra("position", -1);
		System.out.println("position=" + position);
		if(position != -1) {
			tvVideoPlay.setText("本地相册视频播放---待完成");
		}
	}
	
}

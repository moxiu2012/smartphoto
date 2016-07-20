package com.moxiuyuer.smartphoto.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.utils.GlobalParams;
import com.moxiuyuer.smartphoto.utils.OriginalImageLoader;
import com.moxiuyuer.smartphoto.utils.OriginalImageLoader.NativeImageCallBack;
import com.moxiuyuer.smartphoto.view.MyImageView;
import com.moxiuyuer.smartphoto.view.MyScaleImageView;

/**
 * 显示单个图片的activity
 * 描述：
 * @author moxiuyuer
 * 2016-5-23 上午10:52:39
 */
public class ShowItemImageActivity extends Activity {

	private MyScaleImageView mItemImage;
	private Point mPoint = new Point(GlobalParams.windowPoint.x/2, GlobalParams.windowPoint.y/2);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_show_item_image);
		
		mItemImage = (MyScaleImageView) findViewById(R.id.iv_item_image);
		//获得图片的路径
		String path = getIntent().getStringExtra("path");
		
		//由于是单例模式，使用NativeImageLoader类加载图片，
		//是之前加载的图片的缓存（之前缩放过），所以不是原图
//			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, new NativeImageCallBack() {
		//利用OriginalImageLoader类加载本地原图
		Bitmap bitmap = OriginalImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
		
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				mItemImage.setImageBitmap(bitmap);
			}
		});
		
		if(bitmap != null){
			mItemImage.setImageBitmap(bitmap);
		}else{
			mItemImage.setImageResource(R.drawable.mi_friends_sends_pictures_no);
		}
	}
	
	@Override
	protected void onDestroy() {
		
//		//回收bitmap;
//		if(mItemImage != null && mItemImage.getDrawable() != null) {
//			mItemImage.setImageBitmap(null);
//			Bitmap oldBitmap = ((BitmapDrawable)mItemImage.getDrawable()).getBitmap();
//			if(oldBitmap != null) {
//				oldBitmap.recycle();
//				oldBitmap = null;
//			}
//		}
		
		super.onDestroy();
	}
}

package com.moxiuyuer.smartphoto.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.GlobalParams;
import com.moxiuyuer.smartphoto.utils.ScanFileUtils;
import com.moxiuyuer.smartphoto.utils.SerializableUtils;

/**
 * 应用入口界面， 描述：主要作用是联网检查更新，查找本地播放文件
 * 
 * @author yuerting 2016-6-23 - 上午11:07:49
 */
public class SplashActivity extends Activity {

	protected static final int FINISH = 0;

	private boolean isConnectNet = false;

	// 保存工作室文件路径
	private List<String> wsPathList;
	// 保存实例展示文件路径
	private List<String> expPathList;
	// 保存电子相册文件路径
	private List<String> spPathList;
	// app封面
	private Bitmap bm;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == FINISH) {
				Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
				intent.putStringArrayListExtra("wsPathList", (ArrayList<String>) wsPathList);
				intent.putStringArrayListExtra("expPathList", (ArrayList<String>) expPathList);
				intent.putStringArrayListExtra("spPathList", (ArrayList<String>) spPathList);
				startActivity(intent);

				bm.recycle();
				bm = null;
				System.gc();
				// 关闭界面
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_main);

		// 获取窗口参数
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		GlobalParams.windowPoint = new Point(metrics.widthPixels, metrics.heightPixels);

		ImageView ivSplashBg = (ImageView) findViewById(R.id.mi_splash_bg);
		
		 BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.mi_splash_bg);
		 bm = BitmapUtils.scaleBitmapByLess(bd.getBitmap(), 480, 800);
		ivSplashBg.setImageBitmap(bm);
		
		// 设置透明度变化的动画
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1500);
		aa.setFillAfter(true);
		ivSplashBg.setAnimation(aa);

		// 开启子线程 联网检查更新 扫描本地播放文件
		new Thread() {
			public void run() {

				long startTime = SystemClock.currentThreadTimeMillis();

				String basePath = Environment.getExternalStorageDirectory().toString() + "/smartphoto";
				File file = new File(basePath);
				if (!file.exists()) {
					file.mkdirs();
				}

				// 扫描本地工作室文件
				String wsPath = basePath + "/workspace";
				wsPathList = ScanFileUtils.findFileWithPath(wsPath, "uer");

				// 扫描实例展示文件
				String expPath = basePath + "/example";
				File expFile = new File(expPath);
				// 如果不存在，则创建，由于开启应用之后都不再改变这个文件夹，所以现在创建，
				// ws 和 sp 需要再次写入，不再这里创建，防止开启后被删除, 统一在写入的时候判断和创建
				if (!expFile.exists()) {
					expFile.mkdirs();
				}
				//拷贝文件
				copyFile("photonote.exp", expFile, "摄影笔记.exp");
					
				expPathList = ScanFileUtils.findFileWithPath(expPath, "exp");

				// 以前扫描保存的路径文件
				File pathFile = new File(basePath + "/pathfile.temp");
				if (pathFile.exists()) {
					Object object = SerializableUtils.file2Object(pathFile);
					if (object != null) {
						spPathList = (ArrayList<String>) object;
					}
				}

				if (spPathList == null) {
					spPathList = new ArrayList<String>();
				}

				// 扫描本地电子相册文件
				String spPath = basePath + "/smartphotofile";
				List<String> pathList = ScanFileUtils.findFileWithPath(spPath, "sp");

				if (pathList != null && pathList.size() > 0) {
					for (String path : pathList) {
						if (!spPathList.contains(path)) {
							spPathList.add(path);
						}
					}
				}

				// 联网操作
				if (isConnectNet) {
				}

				long duringTime = SystemClock.currentThreadTimeMillis() - startTime;
				if (duringTime < 2000) {
					try {
						sleep(2000 - duringTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);

			};
		}.start();

	}

	/**
	 * 将assets中的文件拷贝到本地
	 */
	private void copyFile(String fileName, File descFile, String newName) {

		File file = new File(descFile, fileName);
		if (!file.exists()) {
			try {
				// 拷贝数据库
				InputStream is = getAssets().open(fileName);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] b = new byte[1024];// 缓存
				int len = 0;
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);// 写入
				}
				is.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//重命名
		if (file.exists() && newName != null) {
			file.renameTo(new File(descFile, newName));
		}
	}

}

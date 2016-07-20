package com.moxiuyuer.smartphoto.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.PlaySPAdapter;
import com.moxiuyuer.smartphoto.bean.SmartPhotoFile;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.GlobalParams;
import com.moxiuyuer.smartphoto.utils.MusicUtils;
import com.moxiuyuer.smartphoto.utils.PlayMusic;
import com.moxiuyuer.smartphoto.utils.SerializableUtils;
import com.moxiuyuer.smartphoto.view.PageWidget;

/**
 * 播放电子相册的activity 描述：
 * 
 * @author moxiuyuer 2016-5-19 下午10:33:05
 */
public class PlayActivity extends Activity {

	protected static final int TIMEUP = 1;

	private List<Bitmap> bitmapList = new ArrayList<Bitmap>();

	private static final int FINISH = 0;

	private static ExecutorService mThreadPool = Executors.newFixedThreadPool(1);

	// 显示加载文件的progressBar
	private ProgressDialog mProgressDialog = null;

	private PlaySPAdapter adapter;

	private PageWidget pwPlay;

	private ImageView playBg;

	private FrameLayout flPlay;

	private boolean isAutoPlay = false;

	private boolean isQuit = false;

	private File musicFile;

	private PlayMusic player;

	// 是否播放音乐，控制循环的结束, 退出时置为false
	private boolean isPlayMusic = true;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == FINISH) {
				// 初始化PageWidget
				initListView();

				// 播放音乐
				if (musicFile != null && musicFile.exists()) {
					// 播放音乐
					playMusic();
				}

				// 进度条隐藏
				mProgressDialog.dismiss();

				// 隐藏背景
				playBg.setVisibility(View.GONE);

				// 是否执行自动播放
				if (isAutoPlay) {
					autoTouch(pwPlay);
				}
			} else if (msg.what == TIMEUP) {

				int width = GlobalParams.windowPoint.x;
				int height = GlobalParams.windowPoint.y;
				
				long uptimeMillis = SystemClock.uptimeMillis();
				MotionEvent ev = MotionEvent.obtain(uptimeMillis, uptimeMillis, 
						MotionEvent.ACTION_DOWN, (int)(height*0.8), (int)(width*0.8), 0);
				flPlay.dispatchTouchEvent(ev);
				
//				for (int i = 0; i <= 10; i++) {
//					MotionEvent ev1 = MotionEvent.obtain(uptimeMillis, SystemClock.uptimeMillis(),
//							MotionEvent.ACTION_MOVE, 1500 - 10 * i, 800 - 3 * i, 0);
//					flPlay.dispatchTouchEvent(ev1);
////					pwPlay.autoTouch(ev1);
//				}

				
				MotionEvent ev2 = MotionEvent.obtain(uptimeMillis, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
						(int)(height*0.7), (int)(width*0.7), 0);
				flPlay.dispatchTouchEvent(ev2);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ml_activity_play);

		flPlay = (FrameLayout) findViewById(R.id.fl_play);

		// 获取播放文件
		Intent intent = getIntent();
		String spPath = intent.getStringExtra("path");
		isAutoPlay = intent.getBooleanExtra("isAutoPlay", false);

		// 初始化数据
		initData(spPath);

	}

	/**
	 * 播放音乐
	 */
	protected void playMusic() {
		// 循环播放音乐
		player = new PlayMusic();
		player.playMusic(getApplicationContext(), musicFile.getAbsolutePath());
	}

	/**
	 * 初始化PageWidget
	 */
	private void initListView() {
		pwPlay = (PageWidget) findViewById(R.id.pw_play);
		adapter = new PlaySPAdapter(PlayActivity.this, bitmapList);
		pwPlay.setAdapter(adapter);
	}

	private void autoTouch(final PageWidget pwPlay) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isQuit && pwPlay.getCurrentPosition() != bitmapList.size() / 2 - 1) {
					// System.out.println("==========" + pwPlay.getCurrentPosition());
					try {
						Thread.sleep(2800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = Message.obtain();
					msg.what = TIMEUP;
					handler.sendMessage(msg);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "自动播放已结束", 0).show();
					}
				});
			}
		}).start();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
	
	
	/**
	 * 初始化数据，获取电子相册播放图片
	 * 
	 * @param spPath
	 */
	private void initData(final String spPath) {

		// playActivity 背景
		playBg = (ImageView) findViewById(R.id.iv_play_bg);
		playBg.setVisibility(View.VISIBLE);

		if (spPath != null) {
			// 加载数据时，显示提示框
			mProgressDialog = ProgressDialog.show(PlayActivity.this, "加载中", "请稍等！", true);
			// 没有并发情况，并且需要立刻执行，使用Thread比较合适
			mThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					// 反序列化
					File spFile = new File(spPath);
					List<Object> objs = SerializableUtils.file2Objects(spFile);
					SmartPhotoFile mSmartPhotoFile = (SmartPhotoFile) objs.get(1);
					if (mSmartPhotoFile != null) {
						for (byte[] data : mSmartPhotoFile.getImageByteList()) {
							// 生成bitmap属于耗时操作, 需要对图片进行边界压缩
							// Bitmap bitmap = BitmapUtils.byte2Bitmap(data);
							Bitmap bitmap = BitmapUtils.byte2BitmapWithScale(data, 600, 800, true);

							if (bitmap.getWidth() > 600 || bitmap.getHeight() > 800) {
								bitmap = BitmapUtils.scaleBitmapByLess(bitmap, 600, 800);
							}
							bitmapList.add(bitmap);
						}
					}

					if (objs.size() >= 3) {// 不包含音乐文件，则不用解析
						// 获取音乐文件, 找不到本地音乐就反序列化
						String musicPath = spPath.substring(0, spPath.lastIndexOf("/") + 1) + "music";
						String musicName = spPath.substring(spPath.lastIndexOf("/") + 1, spPath.lastIndexOf("."))
								+ ".mp3";

						// 判断文件夹是否存在
						File dirFile = new File(musicPath);
						musicFile = new File(musicPath, musicName);
						if (!dirFile.exists()) {
							dirFile.mkdirs();
							// 创建文件
							// 输出音乐文件
							outputMusic(objs);
						} else {// 文件夹存在，判断文件是否存在，并比较修改时间
							if (!musicFile.exists()) {
								// 输出音乐文件
								outputMusic(objs);
							} else {
								if (musicFile.lastModified() != spFile.lastModified()) {// 修改时间不同
									// 输出音乐文件
									outputMusic(objs);
								}
							}
						}

						musicFile = new File(musicPath, musicName);
						if (!musicFile.exists()) {
							outputMusic(objs);
						}
					}

					// 通知主线程
					Message msg = Message.obtain();
					msg.what = FINISH;
					handler.sendMessage(msg);
				}

				private void outputMusic(List<Object> objs) {
					Object object = objs.get(2);
					if (object != null) {
						// 输出音乐文件
						byte[] musicByte = (byte[]) object;
						MusicUtils.byte2MusicFile(musicByte, musicFile);
					}
				}
			});
		} else {
			// 文件不存在
			Toast.makeText(PlayActivity.this, "文件不存在", 0).show();
		}
	}

	@Override
	public void onBackPressed() {
		// 标志退出，结束线程循环，
		if (player != null) {
			player.stopPlay();
		}
		isPlayMusic = false;

		isQuit = true;
		super.onBackPressed();
	}

}

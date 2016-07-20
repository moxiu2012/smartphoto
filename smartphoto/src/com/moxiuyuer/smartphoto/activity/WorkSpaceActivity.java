package com.moxiuyuer.smartphoto.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.NewHListViewAdapter;
import com.moxiuyuer.smartphoto.bean.ImageInfo;
import com.moxiuyuer.smartphoto.bean.ThemeBean;
import com.moxiuyuer.smartphoto.bean.WorkSpaceFile;
import com.moxiuyuer.smartphoto.bean.WorkSpaceInfo;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.BitmapUtils.CallBackImageByte;
import com.moxiuyuer.smartphoto.utils.DensityUtils;
import com.moxiuyuer.smartphoto.utils.GlobalParams;
import com.moxiuyuer.smartphoto.utils.HorizontialListView;
import com.moxiuyuer.smartphoto.utils.LoadThemesAndLayouts;
import com.moxiuyuer.smartphoto.utils.SelectedImagePath;
import com.moxiuyuer.smartphoto.utils.SerializableUtils;
import com.moxiuyuer.smartphoto.view.ThemeView;

/**
 * 制作电子相册的界面 描述：
 * 
 * @author moxiuyuer 2016-5-19 下午10:33:05
 */
public class WorkSpaceActivity extends Activity implements OnClickListener {

	private HorizontialListView mHlvLayout;
	private HorizontialListView mHlvImage;
	private LinearLayout llws;

	// 横向ListView布局的适配器
	private NewHListViewAdapter layoutAdapter;
	// 横向ListView图片的适配器
	private NewHListViewAdapter imageAdapter;

	// 主题
	private ThemeView themeView;
	// 主题
	private ThemeBean mThemeBean;
	// 获取主题背景图片列表
	private List<Bitmap> bgBitmapList;
	// 使用的主题序号
	private int themeNo = 0;

	// 当前页数
	private int pageNo = 0;
	// 显示当前页码
	private TextView tvPageNo;

	// 保存工作室文件信息
	private WorkSpaceInfo mWorkSpaceInfo;
	// 保存工作室文件
	private WorkSpaceFile mWorkSpaceFile;
	// 工作室文件路径
	private String workSpacePath;

	// 保存当前编辑完成的界面为图片
	private Map<Integer, byte[]> finishViewByteMap = new HashMap<Integer, byte[]>();
	// byte[] 方式保存的图片list
	private List<byte[]> imageByteList = new ArrayList<byte[]>();
	// 图片的位图集合
	private List<Bitmap> imageBitmapList = new ArrayList<Bitmap>();
	// 图片的路径
	private List<String> imagePathList;
	// 保存已使用过得所有图片集合，map不能序列化， hashMap才可序列化
	private HashMap<Integer, List<ImageInfo>> imageInfoListMap = new HashMap<Integer, List<ImageInfo>>();
	// // 保存已使用的图片位置
	private List<Integer> usedImageNoList = new ArrayList<Integer>();
	// 新添加的图片bitmap
	private List<Bitmap> addImageBtimapList;

	// 保存已使用的布局位置
	private List<Integer> layoutNoList = new ArrayList<Integer>();
	// 保存当前选择的布局位置
	private List<Integer> currentLayout = new ArrayList<Integer>();
	// 设置布局的参数
	private List<PointF> layoutParamsList = new ArrayList<PointF>();
	// 布局缩略图
	private List<Bitmap> layoutBitmapList = new ArrayList<Bitmap>();

	// 初始化图片完成的标志
	private int INIT_FINISH = 0;
	// 加载图片完成的标志
	private int LOAD_FINISH = 1;
	// 保存工作室完成的标志
	private int SAVE_FINISH = 2;

	// 显示加载图片的progressBar
	private ProgressDialog mProgressDialog = null;

	// Handle, 图片加载完成，更新界面
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SAVE_FINISH) {
				// 进度条隐藏
				mProgressDialog.dismiss();
				// 关闭界面并传递数据
				finishWithIntentGC();
				return;
			}
			// 设置主题界面数据
			themeView.setImageBitmapList(imageBitmapList);
			// 初始化布局
			initLayout();
			// 初始化图片,保存缩略图和压缩后的图片
			initImage();
			if (msg.what == INIT_FINISH) {
				// 设置主题背景图片
				changeThemeBg(0);
			} else if (msg.what == LOAD_FINISH) {
				// 标识已使用的图片和当前布局
				layoutNotifyDataSetChanged(currentLayout);
				imageNotifyDataSetChanged(usedImageNoList);
				// 加载上次编辑的页面
				historyPage(pageNo);
			}
			// 显示当前页面
			tvPageNo.setText("" + (pageNo+1));

			// 进度条隐藏
			mProgressDialog.dismiss();
		}

	};
	private Button btnPre;
	private Button btnNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_workspace);

		// 获取传入的工作室文件的路径，通过反序列化获取数据
		Intent intent = getIntent();
		workSpacePath = intent.getStringExtra("workSpacePath");
		// 初始化控件
		initView();
		// 初始化布局参数和缩略图
		loadLayouts();

		// 加载数据时，显示提示框
		mProgressDialog = ProgressDialog.show(WorkSpaceActivity.this, "加载图片", "请稍等！", true);

		if (workSpacePath != null) {
			// 加载数据
			loadData(workSpacePath);
		} else {
			// 初始化数据
			initData();
		}
	}

	/**
	 * 加载布局参数和缩略图
	 */
	private void loadLayouts() {
		layoutParamsList = LoadThemesAndLayouts.getInstance().getLayoutParams();
		layoutBitmapList = LoadThemesAndLayouts.getInstance().loadLayoutBitmap(WorkSpaceActivity.this);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mHlvLayout = (HorizontialListView) findViewById(R.id.list_view_h_layout);
		mHlvImage = (HorizontialListView) findViewById(R.id.list_view_h_image);
		themeView = (ThemeView) findViewById(R.id.theme_view);
		tvPageNo = (TextView) findViewById(R.id.tv_pageNo);

		btnPre = (Button) findViewById(R.id.btn_pre);
		btnNext = (Button) findViewById(R.id.btn_next);
		btnPre.setOnClickListener(this);
		btnNext.setOnClickListener(this);

		// 屏幕左右滑动事件
		// llws = (LinearLayout) findViewById(R.id.ll_ws);
		// llws.setOnTouchListener(new FingerTouchListener());
	}

	// 加载数据，开启线程，反序列化获得保存的数据
	private void loadData(String workSpacePath) {
		final String path = workSpacePath;
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Object> objectList = SerializableUtils.file2Objects(new File(path));
				// 工作室文件的信息
				mWorkSpaceInfo = (WorkSpaceInfo) objectList.get(0);
				// 工作室文件
				mWorkSpaceFile = (WorkSpaceFile) objectList.get(1);
				// 获取上次最后编辑的页码
				pageNo = mWorkSpaceInfo.getPageNum() - 1;

				// 获取图片bimap集合
				imageByteList = mWorkSpaceFile.getImageByteList();
				for (byte[] data : imageByteList) {
					// 进行品质的压缩 options.inPreferredConfig = Bitmap.Config.RGB_565;
					imageBitmapList.add(BitmapUtils.byte2BitmapWithScale(data, 480, 800, true));
				}

				// 获取已使用的图片集合
				imageInfoListMap = mWorkSpaceFile.getImageInfoListMap();
				// 获取已使用的图片的序号集合
				usedImageNoList = mWorkSpaceFile.getUsedImageNoList();

				// 获取已使用的布局集合
				layoutNoList = mWorkSpaceFile.getLayoutNoList();
				// 最后使用的布局
				currentLayout.add(layoutNoList.get(pageNo));

				// 获取主题， 加载主题比较的时可能会很耗时间，所以放在子线程中加载
				themeNo = mWorkSpaceFile.getThemeNo();
				initTheme();

				// 数据加载完成后，通知主线程
				Message msg = Message.obtain();
				msg.what = LOAD_FINISH;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 初始化数据,将选择的图片转化成bitmap，保存至list中
	 */
	private void initData() {
		// 获取被选中的图片路径列表
		imagePathList = SelectedImagePath.getInstance().getImagePathList();
		if (imagePathList != null && imagePathList.size() > 0) {
			
			if(imagePathList.size() >80) {
				Toast.makeText(WorkSpaceActivity.this, "图片太多，只加载前80张，\n请分两次制作，一起导出吧", 1).show();
				imagePathList = imagePathList.subList(0, 80);
			}
			
			// 这里不具备并发情况，需要立刻执行，可以使用thread 或者固定单个线程 newFixedThreadPool(1)
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 根据路径加载图片
					Point mPoint = new Point(480, 800);
					for (String path : imagePathList) {
						Bitmap mBitmap = null;
						try {
							mBitmap = BitmapUtils.decodeThumbBitmapForFile(path, mPoint == null ? 0 : mPoint.x,
									mPoint == null ? 0 : mPoint.y, new CallBackImageByte() {
										@Override
										public void onImageByte(byte[] imageByte) {
											// 输出没有设置Bitmap.Config 参数的imageByte
											imageByteList.add(imageByte);
										}
									});

							// 压缩图片
							compressBitmap(mBitmap, mPoint.x, mPoint.y);

						} catch (Exception e) {
							e.printStackTrace();
							// 在主线程中弹出toast
							final String errorMsg = "图片" + path.substring(path.lastIndexOf("/")) + "加载失败";
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(WorkSpaceActivity.this, errorMsg, 0).show();
								}
							});
						}
						// 将加载好的bitmap存入list中
						imageBitmapList.add(mBitmap);
					}

					// 初始化主题
					themeNo = SelectedImagePath.getInstance().getSelectedThemeNo();
					initTheme();

					// 由于第一次进入， 布局的列表没有值，应该赋初始值，以后将在点击上下页时赋值
					layoutNoList.add(0);

					// 通知主线程,加载图片完毕
					Message msg = Message.obtain();
					msg.what = INIT_FINISH;
					handler.sendMessage(msg);
				}
			}).start();
		} else {
			// 没有图片,弹窗提示， 退出
			mProgressDialog.dismiss();
			btnNext.setEnabled(false);
			btnPre.setEnabled(false);
		}
	}

	/**
	 * 压缩bitmap
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 */
	private void compressBitmap(Bitmap bitmap, int width, int height) {
		// 如果压缩后的bitmap 大小还超过480x800则进一步压缩
		if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
			bitmap = BitmapUtils.scaleBitmapByLess(bitmap, width, height);
		}
	}

	/**
	 * 初始化主题
	 */
	private void initTheme() {
		mThemeBean = LoadThemesAndLayouts.getInstance().loadThemeList(WorkSpaceActivity.this).get(themeNo);
		// 获取主题背景图片列表
		bgBitmapList = mThemeBean.getBgBitmapList();

		// 必须在主线程中更新主线程界面
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				themeView.setPadding(mThemeBean.getPadding());
				themeView.setCanvasBgColor(mThemeBean.getCanvasColor());
			}
		});
	}

	/**
	 * 更换主题背景图片
	 */
	private void changeThemeBg(int currentPageNo) {
		// 获得主题的第一种背景
		Bitmap bitmap = bgBitmapList.get(currentPageNo % bgBitmapList.size());
		themeView.setThemeBg(bitmap);
	}

	/**
	 * 初始化布局，
	 */
	private void initLayout() {
		layoutAdapter = new NewHListViewAdapter(WorkSpaceActivity.this, layoutBitmapList, mHlvLayout);
		mHlvLayout.setAdapter(layoutAdapter);
		// 设置布局的点击事件
		mHlvLayout.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// // 更换当前页面布局
				changeLayout(position);
			}
		});
	}

	/**
	 * 改变布局， 更换布局需要清除当前选择的图片
	 * 
	 * @param position
	 */
	private void changeLayout(int position) {
		// 清除旧布局，并且清除themeView 中保存的数据
		clear();

		// 清除当前页面选择的图片
		if (usedImageNoList.size() / 2 != pageNo) {
			usedImageNoList.set(pageNo * 2, -1);
			usedImageNoList.set(pageNo * 2 + 1, -1);
			imageNotifyDataSetChanged(usedImageNoList);
		}

		// 将重新选择的布局位置保存在list中
		if (layoutNoList.size() == pageNo) {
			layoutNoList.add(position);
		} else {
			layoutNoList.set(pageNo, position);
		}
		// 更新当前页面的布局，
		updateUI(position);
	}

	/**
	 * 清除旧布局
	 */
	private void clear() {
		themeView.clearContent();
	}

	/**
	 * 设置布局，初始化布局
	 */
	private void setLayout() {
		// 更新当前页面的布局，默认使用上一个
		int lastLayout = 0;
		if (pageNo - 1 >= 0) {
			lastLayout = layoutNoList.get(pageNo - 1);
		}
		// 设置默认保存的当前页面布局为上一个布局
		layoutNoList.add(pageNo, lastLayout);

		updateUI(lastLayout);
	}

	/**
	 * 更新当前页面，包括布局和中间显示部分
	 * 
	 * @param position
	 */
	private void updateUI(int position) {

		// 页面中布局只显示当前页面使用的一种
		currentLayout.clear();
		currentLayout.add(position);
		// 更新ListView
		layoutNotifyDataSetChanged(currentLayout);

		// 当前页面的布局
		themeView.setLayout(layoutParamsList.get(position));

	}

	/**
	 * 初始化图片
	 */
	private void initImage() {
		imageAdapter = new NewHListViewAdapter(WorkSpaceActivity.this, imageBitmapList, mHlvImage);
		mHlvImage.setAdapter(imageAdapter);
		/**
		 * 设置图片的点击事件
		 */
		mHlvImage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				themeView.setImageNo(position);
			}
		});
	}

	/**
	 * 通知layoutListView 数据更新
	 * 
	 * @param currentLayout
	 */
	private void layoutNotifyDataSetChanged(final List<Integer> currentLayout) {
		layoutAdapter.setItemList(currentLayout);
		layoutAdapter.notifyDataSetChanged();
	}

	/**
	 * 通知layoutListView 数据更新
	 * 
	 * @param imagePositionList
	 */
	private void imageNotifyDataSetChanged(List<Integer> imagePositionList) {
		imageAdapter.setItemList(imagePositionList);
		imageAdapter.notifyDataSetChanged();
	}

	/**
	 * 恢复历史界面
	 */
	private void historyPage(int currentPageNo) {
		// 更换主题背景
		changeThemeBg(currentPageNo);

		// 取出上次选中的布局的位置
		int layoutPosition = layoutNoList.get(currentPageNo);
		// 设置布局
		updateUI(layoutPosition);

		// 恢复页面图片和布局
		List<ImageInfo> imageInfoList = imageInfoListMap.get(currentPageNo);
		themeView.setImageInfoList(imageInfoList);
	}

	/**
	 * 按钮控件点击事件
	 */
	@Override
	public void onClick(View view) {
		// 如果页面改变了， 就保存页面信息
		saveViewInfo();

		switch (view.getId()) {
		// 查看上一页
		case R.id.btn_pre:
			prePage();
			break;
		// 制作下一页
		case R.id.btn_next:
			nextPage();
			break;
		default:
			break;
		}

		// 设置当前页码
		tvPageNo.setText("" + (pageNo + 1));
	}

	private void saveViewInfo() {
		if (themeView.getChangeState()) {
			// 保存图片信息
			imageInfoListMap.put(pageNo, themeView.getImageInfoList());

			// 保存使用过得图片序号，并更新图像ListView
			Point mPoint = themeView.getImagePoint();
			if (usedImageNoList.size() / 2 == pageNo) {
				usedImageNoList.add(mPoint.x);
				usedImageNoList.add(mPoint.y);
			} else {
				usedImageNoList.set(pageNo * 2, mPoint.x);
				usedImageNoList.set(pageNo * 2 + 1, mPoint.y);
			}
			imageNotifyDataSetChanged(usedImageNoList);

			// 保存当前已编辑好的页面为
			saveFinishView2ImageByte();
		}
	}

	/**
	 * 下一页
	 */
	private void nextPage() {
		// 判断当前页面是否有编辑，没有编辑则拒绝进入下一页, 状态改变并且有图片
		if (themeView.getImageInfoList().get(0) != null || themeView.getImageInfoList().get(1) != null) {
			// 清除当前界面
			clear();
			// 当前页码自增
			pageNo++;
			if (pageNo >= layoutNoList.size()) {
				// 更换主题背景
				changeThemeBg(pageNo);
				// 设置布局
				setLayout();
			} else {
				// 恢复以往页面显示
				historyPage(pageNo);
			}
		} else {
			Toast.makeText(getApplicationContext(), "请编辑当前页面", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 上一页
	 */
	private void prePage() {
		if (pageNo > 0) {
			if (themeView.getImageInfoList().get(0) == null) {
				Toast.makeText(getApplicationContext(), "请编辑当前页面", Toast.LENGTH_SHORT).show();
			}
			// 清除当前界面
			clear();
			// 当前页码
			pageNo--;
			// 恢复以往页面显示
			historyPage(pageNo);
		} else {
			Toast.makeText(getApplicationContext(), "已经是第一页了", 0).show();
		}
	}

	// /**
	// * 手指滑动事件监听类 描述：
	// *
	// * @author yuerting 2016-7-3 - 上午10:54:03
	// */
	// private class FingerTouchListener implements OnTouchListener {
	// private int topLine = DensityUtils.dip2px(WorkSpaceActivity.this, 100);
	// private int bottomLine = GlobalParams.windowPoint.y - topLine;
	// private int startX;
	//
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	//
	// // 在屏幕指定范围内响应左右滑动事件
	// if (event.getY() > topLine && event.getY() < bottomLine) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:// 手指按下
	// startX = (int) event.getX();
	// break;
	// case MotionEvent.ACTION_MOVE:// 手指移动
	// break;
	// case MotionEvent.ACTION_UP:// 手指抬起
	// int offsetX = (int) (event.getX() - startX);
	// if (offsetX > 20) { // 往右滑动,
	// // 上一页
	// prePage();
	// } else if (offsetX < -20) { // 往左滑动，
	// // 下一页
	// nextPage();
	// }
	// break;
	// default:
	// break;
	// }
	// return true;// 被消费了返回true,否则返回false
	// } else {
	// return false;
	// }
	// }
	// }

	/**
	 * 保存编辑完成的页面为图片
	 */
	private void saveFinishView2ImageByte() {
		// 将view保存为bitmap
		Bitmap saveBitmap = BitmapUtils.view2Bitmap(themeView);
		// 将bitmap转成byte[] 存进Map中
		finishViewByteMap.put(pageNo, BitmapUtils.Bitmap2ByteJPEG(saveBitmap, 100));
		// 必须释放资源
		saveBitmap.recycle();
		saveBitmap = null;
	}

	/**
	 * 保存工作室
	 */
	private void saveWorkSpace() {
		// 保存工作室文件信息
		saveWorkSpaceInfo();
		// 保存工作室文件
		saveWorkSpaceFile();

		// 序列化到本地文件；
		String path = getExternalCacheDir().getPath();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory() + "/smartphoto/workspace";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// 工作室路径
		workSpacePath = path + "/" + mWorkSpaceInfo.getName() + ".uer";
		File saveFile = new File(workSpacePath);

		List<Object> objectList = new ArrayList<Object>();
		objectList.add(mWorkSpaceInfo);
		objectList.add(mWorkSpaceFile);
		SerializableUtils.objects2File(objectList, saveFile);
	}

	/**
	 * 保存WorkSpaceFile
	 * 
	 * @return
	 */
	private void saveWorkSpaceFile() {
		// 保存工作室文件bean
		if (mWorkSpaceFile == null) {
			// 新建工作室， 需要创建WorkSpaceFile这个类，并保留选择的主题序号，否则直接更新改变项
			mWorkSpaceFile = new WorkSpaceFile();
			mWorkSpaceFile.setThemeNo(themeNo);
		}
		mWorkSpaceFile.setUsedImageNoList(usedImageNoList);
		mWorkSpaceFile.setLayoutNoList(layoutNoList);
		mWorkSpaceFile.setImageInfoListMap(imageInfoListMap);
		// 保存编辑的页面
		saveImageByte2File();

		if (mWorkSpaceFile.getImageByteList() == null) {
			mWorkSpaceFile.setImageByteList(imageByteList);
		}
	}

	/**
	 * 保存编辑完成的themeView为图片
	 */
	private void saveImageByte2File() {
		List<byte[]> finishEditList = mWorkSpaceFile.getFinishEditList();
		if (finishEditList == null) {
			finishEditList = new ArrayList<byte[]>();
		}

		Set<Integer> keySet = finishViewByteMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>();
		for (Iterator<Integer> iter = keySet.iterator(); iter.hasNext();) {
			int key = iter.next();
			keyList.add(key);
		}
		// 将编辑的页面的
		Collections.sort(keyList);
		int lastPageNum = finishEditList.size();
		for (int key : keyList) {
			if (key >= lastPageNum) {
				// 从未保存的页面
				finishEditList.add(finishViewByteMap.get(key));
			} else {
				// 保存过得页面
				finishEditList.set(key, finishViewByteMap.get(key));
			}
		}
		mWorkSpaceFile.setFinishEditList(finishEditList);
	}

	/**
	 * 保存WorkSpaceInfo
	 * 
	 * @param date
	 * @return
	 */
	private void saveWorkSpaceInfo() {
		// 保存工作室信息
		if (mWorkSpaceInfo == null) {
			// 新建文件，设置默认文件名和描述, 否则保留原来的
			mWorkSpaceInfo = new WorkSpaceInfo();
			mWorkSpaceInfo.setName("love-" + new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()));
			mWorkSpaceInfo.setDesc("hello smartphoto");
		}
		mWorkSpaceInfo.setPageNum(layoutNoList.size());
		mWorkSpaceInfo.setModifyTime(new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()));

		// 取出工作室编辑的第一页的图片位置，如果还没编辑第一页，则使用第一个位置的图片
		int firstImagePosition = usedImageNoList.get(0) == -1 ? 0 : usedImageNoList.get(0);
		Bitmap bitmap = BitmapUtils.centerSquareScaleBitmap(imageBitmapList.get(firstImagePosition), 100, 100);
		mWorkSpaceInfo.setCoverImage(BitmapUtils.Bitmap2BytePNG(bitmap));
		bitmap.recycle();
		bitmap = null;
	}

	/**
	 * 显示对话框
	 */
	public void showDialog() {
		AlertDialog.Builder builder = new Builder(WorkSpaceActivity.this);
		builder.setTitle("对话框标题");
		builder.setMessage("对话框消息");
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 清除旧工作室
				if (workSpacePath != null) {
					new File(workSpacePath).delete();
				}
				// 保存工作室时，显示提示框
				mProgressDialog = ProgressDialog.show(WorkSpaceActivity.this, "正在保存数据", "请稍等！", true);

				// 保存当前已编辑好的页面为
				saveViewInfo();

				// 保存工作室, 需要序列化文件，比较耗时
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 保存工作室
						saveWorkSpace();
						
						Message msg = Message.obtain();
						msg.what = SAVE_FINISH;
						handler.sendMessage(msg);
					}
				}).start();
			}
		});
		builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 不保存，则将workSpacePath = null;
				workSpacePath = null;
				// 关闭界面并传递数据
				finishWithIntentGC();
			}
		});
		builder.show();// 与上面的表示等价
	}

	@Override
	public void onBackPressed() {
		if (imagePathList != null && imagePathList.size() > 0) {
			// 清空SelectedImagePath中保存的图片路径列表，主题默认使用上次的，所以不清除
			SelectedImagePath.getInstance().getImagePathList().clear();
		}
		if (imageBitmapList != null && imageBitmapList.size() > 0) {
			// 提示保存
			showDialog();
		} else {
			//直接退出
			finishWithIntent();
		}
	}

	/**
	 * 回传数据并关闭界面 释放bitmap
	 */
	private void finishWithIntentGC() {
		//回收list列表中的bitmap，应该回收每一个bitmap
		if (imageBitmapList != null && imageBitmapList.size() > 0) {
			for (Bitmap bm : imageBitmapList) {
				bm.recycle();
				bm = null;
			}
			imageBitmapList.clear();
			imageBitmapList = null;
		}

		if (bgBitmapList != null && bgBitmapList.size() > 0) {
			for (Bitmap bitmap : bgBitmapList) {
				bitmap.recycle();
				bitmap = null;
			}
			bgBitmapList.clear();
			bgBitmapList = null;
		}

		// 回传数据并关闭界面 返回新增的工作室路径
		finishWithIntent();
		
		// 通知回收内存
		System.gc();
	}

	/**
	 * 回传数据并关闭界面
	 */
	private void finishWithIntent() {
		Intent data = new Intent();
		data.putExtra("workSpacePath", workSpacePath);
		setResult(0, data);
		// 关闭界面
		finish();
	}

}

package com.moxiuyuer.smartphoto.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Environment;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.bean.LayoutBean;
import com.moxiuyuer.smartphoto.bean.ThemeBean;
import com.moxiuyuer.smartphoto.bean.ThemeFileBean;

/**
 * 加载主题和布局的工具类 描述：使用单例模式加载主题和布局
 * 
 * @author yuerting 2016-6-25 - 下午2:01:44
 */
public class LoadThemesAndLayouts {

	private static LoadThemesAndLayouts instance = new LoadThemesAndLayouts();

	private File dirFile;

	private ArrayList<Bitmap> layoutBitmapList;

	private ArrayList<PointF> layoutParamsList;

	private ArrayList<Bitmap> localLayoutBitmapList;

	private ArrayList<PointF> loaclLayoutParamsList;

	private LoadThemesAndLayouts() {
		String path = Environment.getExternalStorageDirectory() + "/smartphoto/themeandlayout";
		dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	};

	public static LoadThemesAndLayouts getInstance() {
		return instance;
	}

	/**
	 * 加载主题列表
	 * 
	 * @param context
	 * @return
	 */
	public List<ThemeBean> loadThemeList(Context context) {

		List<ThemeBean> list = new ArrayList<ThemeBean>();

		/**
		 * 加载默认主题
		 */
		// 加载梦幻主题
		list.addAll(loadTheme(context, "dream", 3, 0xffffff));
		// 加载风景主题
		list.addAll(loadTheme(context, "scene", 4, 0xffffff));
		// 加载星空主题
		list.addAll(loadTheme(context, "star", 3, 0xffffff));

		/**
		 * 加载下载的本地主题
		 */
		List<ThemeBean> loadLocalTheme = loadLocalTheme(context);
		if (loadLocalTheme != null && loadLocalTheme.size() > 0) {
			list.addAll(loadLocalTheme);
		}

		return list;
	}

	/**
	 * 加载各类主题，默认主题
	 * 
	 * @param name
	 * @param num
	 *            样式数量 即背景图片数量
	 * @return list 主题类列表，规定有3种不同的边距和背景画布
	 */
	private List<ThemeBean> loadTheme(Context context, String name, int num, int color) {

		String bgImageName = "bg_theme_" + name + "_";
		// 加载主题背景图片
		List<Bitmap> bitmapList = new ArrayList<Bitmap>();
		for (int i = 0; i < num; i++) {
			try {
				Bitmap bgBitmap = BitmapFactory.decodeStream(context.getAssets().open(bgImageName + i + ".png"));
				bitmapList.add(bgBitmap);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("==========加载主题背景图片失败");
			}
		}

		int d = DensityUtils.dip2px(context, 3);
		int padLR = DensityUtils.dip2px(context, 5);
		int padTB = DensityUtils.dip2px(context, 8);

		// 封装成ThemeBean, 三种不同的边距和画布
		List<ThemeBean> list = new ArrayList<ThemeBean>();
		ThemeBean mThemeBean;
		for (int i = 0; i < 4; i++) {
			mThemeBean = new ThemeBean();
			mThemeBean.setThemeName(name + "_" + i);
			mThemeBean.setBgBitmapList(bitmapList);
			int di = d * i;
			int[] padding = { padLR + di, padTB + di, padLR + di, padTB + di };
			mThemeBean.setPadding(padding);

			int canvasColor = 0xaa000000 + 0x11000000 * i + color;
			mThemeBean.setCanvasColor(canvasColor);

			list.add(mThemeBean);
		}

		return list;
	}

	/**
	 * 加载本地主题
	 */
	private List<ThemeBean> loadLocalTheme(Context context) {
		List<String> themeFileList = ScanFileUtils.findFileWithPath(dirFile.getAbsolutePath(), "theme");
		if (themeFileList == null || themeFileList.size() == 0) {
			return null;
		}
		// 所以本地主题文件包含的主题类
		List<ThemeBean> list = new ArrayList<ThemeBean>();

		for (String path : themeFileList) {
			Object obj = SerializableUtils.file2Object(new File(path));
			// 单个本地主题文件包含的主题类
			List<ThemeBean> themeBeanList = null;
			if (obj != null) {
				try {
					List<ThemeFileBean> themeFileBeanList = (List<ThemeFileBean>) obj;
					if (themeFileBeanList != null && themeFileBeanList.size() > 0) {
						themeBeanList = new ArrayList<ThemeBean>();
						ThemeBean themeBean;
						for (ThemeFileBean themeFileBean : themeFileBeanList) {
							themeBean = new ThemeBean();
							List<byte[]> bgByteList = themeFileBean.getBgByteList();
							List<Bitmap> bgBitmapList = new ArrayList<Bitmap>();
							for (byte[] bs : bgByteList) {
								Bitmap bitmap = BitmapUtils.byte2Bitmap(bs);
								bgBitmapList.add(bitmap);
							}

							themeBean.setBgBitmapList(bgBitmapList);
							themeBean.setCanvasColor(themeFileBean.getCanvasColor());
							themeBean.setPadding(themeFileBean.getPadding());
							themeBean.setThemeName(themeFileBean.getThemeName());
							themeBeanList.add(themeBean);
						}
					}
				} catch (Exception e) {
					Toast.makeText(context, "主题文件错误", 0).show();
				}
			}
			if (themeBeanList != null && themeBeanList.size() > 0) {
				list.addAll(themeBeanList);
			}
		}
		return list;
	}

	public List<Bitmap> loadLayoutBitmap(Context context) {
		/**
		 * 加载默认布局文件缩略图
		 */
		String name;
		Bitmap bitmap;
		List<Bitmap> bmList = new ArrayList<Bitmap>();
		for (int i = 0; i < 12; i++) {
			name = "mi_layout_" + i + ".png";
			try {
				bitmap = BitmapFactory.decodeStream(context.getAssets().open(name));
				bmList.add(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 加载下载的本地布局文件缩略图
		 */
		loadLocalLayout();

		if (localLayoutBitmapList != null && localLayoutBitmapList.size() > 0) {
			bmList.addAll(localLayoutBitmapList);
		}

		return bmList;
	}

	/**
	 * 加载下载的本地布局文件缩略图
	 */
	private void loadLocalLayout() {
		List<String> layoutFileList = ScanFileUtils.findFileWithPath(dirFile.getAbsolutePath(), "layout");

		if (layoutFileList != null && layoutFileList.size() > 0) {
			localLayoutBitmapList = new ArrayList<Bitmap>();
			loaclLayoutParamsList = new ArrayList<PointF>();
			// 遍历每一个本地布局文件
			List<Bitmap> bitmapList = null;
			List<PointF> paramsList = null;
			for (String layoutPath : layoutFileList) {
				Object obj = SerializableUtils.file2Object(new File(layoutPath));
				if (obj != null) {
					bitmapList = new ArrayList<Bitmap>();
					paramsList = new ArrayList<PointF>();
					List<LayoutBean> layoutBeanList = new ArrayList<LayoutBean>();
					// 每个布局布局文件的布局
					layoutBeanList = (List<LayoutBean>) obj;
					for (LayoutBean layoutBean : layoutBeanList) {
						Bitmap bitmap = BitmapUtils.byte2Bitmap(layoutBean.getCoverImage());
						bitmapList.add(bitmap);
						if (layoutBean.getLeftY() == -1 || layoutBean.getRightY() == -1) {
							paramsList.add(null);
						} else {
							paramsList.add(new PointF(layoutBean.getLeftY(), layoutBean.getRightY()));
						}
					}
				}

				if (bitmapList != null && bitmapList.size() > 0 && paramsList != null && paramsList.size() > 0) {
					localLayoutBitmapList.addAll(bitmapList);
					loaclLayoutParamsList.addAll(paramsList);
				}
			}
		}
	}

	public List<PointF> getLayoutParams() {
		List<PointF> pointList = new ArrayList<PointF>();

		/**
		 * 加载默认布局参数
		 */
		// 对应mi_layout_0
		pointList.add(null);
		// 对应mi_layout_1
		pointList.add(new PointF(0.5f, 0.5f));
		// 对应mi_layout_2
		pointList.add(new PointF(0.4f, 0.4f));
		// 对应mi_layout_3
		pointList.add(new PointF(0.6f, 0.6f));
		// 对应mi_layout_4
		pointList.add(new PointF(0.6f, 0.4f));
		// 对应mi_layout_5
		pointList.add(new PointF(0.4f, 0.6f));
		// 对应mi_layout_6
		pointList.add(new PointF(1.0f, 0f));
		// 对应mi_layout_7
		pointList.add(new PointF(0f, 1.0f));
		// 对应mi_layout_8
		pointList.add(new PointF(0.6f, 0.f));
		// 对应mi_layout_9
		pointList.add(new PointF(0f, 0.6f));
		// 对应mi_layout_10
		pointList.add(new PointF(1.0f, 0.4f));
		// 对应mi_layout_11
		pointList.add(new PointF(0.4f, 1.0f));

		/**
		 * 加载本地布局参数
		 */
		if (loaclLayoutParamsList != null && loaclLayoutParamsList.size() > 0) {
			pointList.addAll(loaclLayoutParamsList);
		}

		return pointList;
	}

	
	
	/************************************************************************************/
	
	public void outputThemeFile(List<ThemeBean> themeBeanList) {

		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File file = new File(dirFile, "theme_1.theme");

		List<ThemeFileBean> themeFileBeanList = new ArrayList<ThemeFileBean>();
		for (ThemeBean themeBean : themeBeanList) {
			ThemeFileBean mThemeFileBean = new ThemeFileBean();

			List<Bitmap> bgBitmapList = themeBean.getBgBitmapList();
			List<byte[]> byteList = new ArrayList<byte[]>();
			for (Bitmap bitmap : bgBitmapList) {
				byteList.add(BitmapUtils.Bitmap2ByteJPEG(bitmap, 100));
			}

			mThemeFileBean.setCanvasColor(themeBean.getCanvasColor());
			mThemeFileBean.setPadding(themeBean.getPadding());
			mThemeFileBean.setThemeName(themeBean.getThemeName());
			mThemeFileBean.setBgByteList(byteList);

			themeFileBeanList.add(mThemeFileBean);
		}

		// 序列化
		SerializableUtils.object2File(themeFileBeanList, file);
	}

	public void outputLayoutFile(List<Bitmap> bitmapList, List<PointF> pointFList) {

		List<LayoutBean> layoutList = new ArrayList<LayoutBean>();
		for (int i = 0; i < bitmapList.size(); i++) {
			LayoutBean mLayoutBean = new LayoutBean();

			byte[] byteLayout = BitmapUtils.Bitmap2ByteJPEG(bitmapList.get(i), 100);
			PointF pointF = pointFList.get(i);
			if (pointF != null) {
				mLayoutBean.setLeftY(pointF.x);
				mLayoutBean.setRightY(pointF.y);
			} else {
				mLayoutBean.setLeftY(-1);
				mLayoutBean.setRightY(-1);
			}
			mLayoutBean.setCoverImage(byteLayout);

			layoutList.add(mLayoutBean);
		}

		// 序列化
		SerializableUtils.object2File(layoutList, new File(dirFile, "layout_1.layout"));

	}

}

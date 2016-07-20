package com.moxiuyuer.smartphoto.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.view.View;

/**
 * 图形的缩略图 描述：根据给定的长宽裁剪图片
 * 
 * @author moxiuyuer 2016-5-22 下午8:52:08
 */
public class BitmapUtils {

	/**
	 * 
	 * @param bitmap
	 *            图片的位图
	 * @param width
	 *            需要裁剪的宽度
	 * @param height
	 *            需要裁剪的高度
	 * @return
	 */
	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int width, int height) {
		if (null == bitmap || width <= 0 || height <= 0) {
			return null;
		}

		Bitmap result = null;

		// 最大程度符合给定区域缩放图片
		Bitmap scaledBitmap = scaleBitmapByMore(bitmap, width, height);
		try {
			// 从图中截取正中间的正方形部分。
			int xTopLeft = (scaledBitmap.getWidth() - width) / 2;
			int yTopLeft = (scaledBitmap.getHeight() - height) / 2;

			// 判断裁剪范围是否超过被裁减的原图，否则会报错
			width = (xTopLeft + width) >= scaledBitmap.getWidth() ? (scaledBitmap.getWidth() - 2 * xTopLeft) : width;
			height = (yTopLeft + height) >= scaledBitmap.getHeight() ? (scaledBitmap.getHeight() - 2 * yTopLeft)
					: height;

			// 剪切图片，矩形左上角开始，width和height的边长
			result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, width, height);
			scaledBitmap.recycle();
			scaledBitmap = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 一边接近，一边超过 根据给定的区域，将位图按照最大限度的方式显示，超出给定范围，以float的比例精度缩放
	 * 
	 * @param bitmap
	 *            位图
	 * @param width
	 *            区域宽度
	 * @param height
	 *            区域高度
	 * @return
	 */
	public static Bitmap scaleBitmapByMore(Bitmap bitmap, int width, int height) {
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();
		Bitmap resultBitmap = null;

		// 换成float进行运算才能得到更精确的值
		float scaleWidth = (float) ((float) widthOrg / (float) width);
		float scaleHeight = (float) ((float) heightOrg / (float) height);
		float scale = scaleWidth <= scaleHeight ? scaleWidth : scaleHeight;

		try {
			// 先获得一个小的bitmap，一条边接近，而另一边超过
			resultBitmap = Bitmap.createScaledBitmap(bitmap, (int) ((float) widthOrg / scale),
					(int) ((float) heightOrg / scale), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultBitmap;
	}

	/**
	 * 一边接近，一边不足 根据给定的区域，将位图按照最小限度的方式显示，小于给定区域，以float的比例精度缩放
	 * 
	 * @param bitmap
	 *            位图
	 * @param width
	 *            区域宽度
	 * @param height
	 *            区域高度
	 * @return
	 */
	public static Bitmap scaleBitmapByLess(Bitmap bitmap, int width, int height) {
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();
		Bitmap resultBitmap = null;

		// 换成float进行运算才能得到更精确的值
		float scaleWidth = (float) ((float) widthOrg / (float) width);
		float scaleHeight = (float) ((float) heightOrg / (float) height);
		float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

		try {
			// 先获得一个小的bitmap，一条边接近，而另一边不足
			resultBitmap = Bitmap.createScaledBitmap(bitmap, (int) ((float) widthOrg / scale),
					(int) ((float) heightOrg / scale), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultBitmap;
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图,以float的比例精度缩放
	 * 
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @param setConfig 设置Bitmap.Config = RGB_565
	 * @return
	 */
	public static Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight, CallBackImageByte mCallBack) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		// 设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		
		byte[] imageByte = Bitmap2ByteJPEG(bitmap, 100);
		mCallBack.onImageByte(imageByte);
		Bitmap copy = bitmap.copy(Bitmap.Config.RGB_565, true);
		bitmap.recycle();
		bitmap = null;
		
		return copy;
	}

	public static Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		// 设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		long middle = System.currentTimeMillis();
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}
	
	
	
	
	
	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * 
	 * @param options
	 * @param width
	 * @param height
	 */
	private static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
		int inSampleSize = 1;
		if (viewWidth == 0 || viewWidth == 0) {
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		// 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if (bitmapWidth > viewWidth || bitmapHeight > viewHeight) {
			// int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			// int heightScale = Math.round((float) bitmapHeight / (float) viewHeight);

			int widthScale = (int) ((float) bitmapWidth / (float) viewWidth);
			int heightScale = (int) ((float) bitmapHeight / (float) viewHeight);
			// 为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale >= heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	/**
	 * byte[]转化成bitmap
	 * 
	 * @param data
	 * @param setConfig 是否设置Bitmap.Config.RGB_565
	 * @return
	 */
	public static Bitmap byte2Bitmap(byte[] data, boolean setConfig) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		if (setConfig) {
			options.inPreferredConfig = Bitmap.Config.RGB_565;
		}
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/**
	 * byte[]转化成bitmap
	 * @param data
	 * @return
	 */
	public static Bitmap byte2Bitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	/**
	 * byte[]转化成bitmap, 并且根据指定大小缩放
	 * 
	 * @param data
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap byte2BitmapWithScale(byte[] data, int viewWidth, int viewHeight, boolean setConfig) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		// 设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		// 设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		if (setConfig) {
			options.inPreferredConfig = Bitmap.Config.RGB_565;
		}
		options.inPurgeable = true;
		options.inInputShareable = true;

		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/**
	 * bitmap 转化成byte[]
	 * 
	 * @param bitmap
	 * @param size
	 *            压缩后的大小最大值
	 * @return
	 */
	public static byte[] Bitmap2ByteJPEG(Bitmap bitmap, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bitmap.compress(CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > size) {
			baos.reset();
			options -= 10;
			bitmap.compress(CompressFormat.JPEG, options, baos);
		}
		return baos.toByteArray();
	}

	/**
	 * 压缩成PNG
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] Bitmap2BytePNG(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, baos);
		return baos.toByteArray();
	}

	/**
	 * 压缩成JPEG
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] Bitmap2ByteJPEG(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 将view转化为bitmap
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap view2Bitmap(View view) {

		view.clearFocus();
		view.setPressed(false);

		// 能画缓存就返回false
		boolean willNotCache = view.willNotCacheDrawing();
		view.setWillNotCacheDrawing(false);

		int color = view.getDrawingCacheBackgroundColor();
		view.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			view.destroyDrawingCache();
		}

		view.buildDrawingCache();

		Bitmap cacheBitmap = view.getDrawingCache();
		if (cacheBitmap == null) {
			System.out.println("----------转成bitmap失败");
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		cacheBitmap.recycle();
		cacheBitmap = null;
		Bitmap resultBitmap = null;
		if (bitmap.getWidth() > 480 || bitmap.getHeight() > 800) {
			resultBitmap = BitmapUtils.scaleBitmapByLess(bitmap, 480, 800);
			bitmap.recycle();
			bitmap = null;
		} else {
			resultBitmap = bitmap;
		}
		//
		// Restore the view
		view.destroyDrawingCache();
		view.setWillNotCacheDrawing(willNotCache);
		view.setDrawingCacheBackgroundColor(color);

		return resultBitmap;
	}

	/**
	 * 加载图片时，回调没有设置Bitmap.Config.RGB_565的图片的byte[]格式
	 * 描述：
	 *
	 * @author yuerting
	 *  2016-7-15  - 下午5:16:58
	 */
	public interface CallBackImageByte{
		public void onImageByte(byte[] imageByte);
	}

}

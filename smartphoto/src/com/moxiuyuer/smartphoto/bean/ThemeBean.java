package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.moxiuyuer.smartphoto.view.ThemeView;

/**
 * 封装主题的实体类 描述：主题类是由封面和一个自定义布局的控件的list
 * 
 * @author moxiuyuer 2016-5-25 上午10:51:35
 */
public class ThemeBean {

	// 主题名称
	private String themeName;

	// 背景图片
	 private List<Bitmap> bgBitmapList;
	
	private List<byte[]> bgByteList;

	private int canvasColor;

	// 设置边距
	private int[] padding;

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public Bitmap getCoverBitmap() {
		if (bgBitmapList != null && bgBitmapList.size() > 0) {
			return bgBitmapList.get(0);
		}
		return null;
	}

	public List<Bitmap> getBgBitmapList() {
		return bgBitmapList;
	}

	public void setBgBitmapList(List<Bitmap> bgBitmapList) {
		this.bgBitmapList = bgBitmapList;
	}

	public int[] getPadding() {
		return padding;
	}

	public void setPadding(int[] padding) {
		this.padding = padding;
	}

	public int getCanvasColor() {
		return canvasColor;
	}

	public void setCanvasColor(int canvasColor) {
		this.canvasColor = canvasColor;
	}

	public List<byte[]> getBgByteList() {
		return bgByteList;
	}

	public void setBgByteList(List<byte[]> bgByteList) {
		this.bgByteList = bgByteList;
	}

	
	
}

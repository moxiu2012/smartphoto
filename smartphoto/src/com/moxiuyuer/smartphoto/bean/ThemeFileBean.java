package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;

/**
 * 用来序列化的主题类
 * 描述：
 *
 * @author yuerting
 *  2016-7-18  - 上午11:13:04
 */
public class ThemeFileBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8320627648422674524L;

	// 主题名称
	private String themeName;

	//背景图片byte[]
	private List<byte[]> bgByteList;

	//画布的颜色
	private int canvasColor;

	// 设置边距
	private int[] padding;

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public List<byte[]> getBgByteList() {
		return bgByteList;
	}

	public void setBgByteList(List<byte[]> bgByteList) {
		this.bgByteList = bgByteList;
	}

	public int getCanvasColor() {
		return canvasColor;
	}

	public void setCanvasColor(int canvasColor) {
		this.canvasColor = canvasColor;
	}

	public int[] getPadding() {
		return padding;
	}

	public void setPadding(int[] padding) {
		this.padding = padding;
	}
	
	
}

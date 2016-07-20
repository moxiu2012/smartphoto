package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;

/**
 * 封装布局文件的bean
 * 描述：保存为文件使用
 *
 * @author yuerting
 *  2016-7-18  - 上午10:22:17
 */
public class LayoutBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8184025971272570659L;

	//展示的缩略图
	private byte[] coverImage;
	
	//布局参数分割线左边y坐标值
	private float leftY;
	
	//布局参数分割线右边y坐标值
	private float rightY;

	public byte[] getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(byte[] coverImage) {
		this.coverImage = coverImage;
	}

	public float getLeftY() {
		return leftY;
	}

	public void setLeftY(float leftY) {
		this.leftY = leftY;
	}

	public float getRightY() {
		return rightY;
	}

	public void setRightY(float rightY) {
		this.rightY = rightY;
	}
	
}

package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;

import android.graphics.PointF;

/**
 * 图片信息类 描述：封装图片信息，图片序号，图片位置，图片缩放率
 * 
 * @author yuerting 2016-7-11 - 上午11:46:51
 */
public class ImageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 33205631776352223L;

	// 图片序号
	private int imageNo = -1;

	/**
	 * float[] values = new float[9]; values[0] values[4] 为缩放率 values[2] values[5] 为位置坐标 values[1, 3, 6, 7] = 0.0,
	 * values[8] = 1.0
	 */

	// 图片位置,无法序列化
	// private PointF posPointF;
	private float posX;
	private float posY;

	// 图片缩放率
	private float scale;

	public int getImageNo() {
		return imageNo;
	}

	public void setImageNo(int imageNo) {
		this.imageNo = imageNo;
	}

	public PointF getPosPointF() {
		PointF posPointF = new PointF(posX, posY);
		return posPointF;
	}

	public void setPosPointF(PointF posPointF) {
		this.posX = posPointF.x;
		this.posY = posPointF.y;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

}

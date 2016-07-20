package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;

/**
 * 电子相册保存播放文件头信息
 * 描述： 保存第一张图片，标题，描述等信息
 *
 * @author yuerting
 *  2016-6-30  - 下午12:45:39
 */
public class SmartPhotoInfo implements Serializable, Comparable<SmartPhotoInfo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4182876687468141497L;
	//标题
	private String name;
	//描述
	private String desc;
	//文件路径
	private String path;
	//最后修改时间
	private long lastModifyTime;
	
	/**
	 * 保存byte[] 格式的第一张图片
	 */
	private byte[] imageByte;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	public byte[] getImageByte() {
		return imageByte;
	}

	public void setImageByte(byte[] imageByte) {
		this.imageByte = imageByte;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	
	/**
	 * 覆盖比较方法，大的排在前
	 */
	@Override
	public int compareTo(SmartPhotoInfo info) {
		if(lastModifyTime > info.getLastModifyTime()) {
			return -1;
		} 
		if(lastModifyTime < info.getLastModifyTime()){
			return 1;
		}
		if(lastModifyTime == info.getLastModifyTime()) {
			return 0;
		}
		return 0;
	}
	
}

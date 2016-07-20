package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;
import java.util.List;


/**
 * 电子相册文件的内容类
 * 描述：
 *
 * @author yuerting
 *  2016-7-2  - 下午11:02:21
 */
public class SmartPhotoFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3058954521105483534L;
	private List<byte[]> imageByteList;

	public List<byte[]> getImageByteList() {
		return imageByteList;
	}

	public void setImageByteList(List<byte[]> imageByteList) {
		this.imageByteList = imageByteList;
	}
	
}

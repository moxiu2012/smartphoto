package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Point;

/**
 * 保存工作室文件 描述：
 * 
 * @author yuerting 2016-7-2 - 上午8:08:36
 */
public class WorkSpaceFile implements Serializable {

	// 保存图片
	private List<byte[]> imageByteList;

	// 保存已编辑页面的图片
	private List<byte[]> finishEditList;

	// 保存文字
	private List<String> wordList;

	// 保存图片信息集合,由于map不能序列化，但是hashMap可以
	private HashMap<Integer, List<ImageInfo>> imageInfoListMap;

	// 保存布局序号集合
	private List<Integer> layoutNoList;

	// 保存主题序号
	private int ThemeNo;

	// 保存使用过得图片的序号
	private List<Integer> usedImageNoList;

	public List<byte[]> getImageByteList() {
		return imageByteList;
	}

	public void setImageByteList(List<byte[]> imageByteList) {
		this.imageByteList = imageByteList;
	}

	public List<byte[]> getFinishEditList() {
		return finishEditList;
	}

	public void setFinishEditList(List<byte[]> finishEditList) {
		this.finishEditList = finishEditList;
	}

	public List<String> getWordList() {
		return wordList;
	}

	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}

	public List<Integer> getLayoutNoList() {
		return layoutNoList;
	}

	public void setLayoutNoList(List<Integer> layoutNoList) {
		this.layoutNoList = layoutNoList;
	}

	public int getThemeNo() {
		return ThemeNo;
	}

	public void setThemeNo(int themeNo) {
		ThemeNo = themeNo;
	}

	public List<Integer> getUsedImageNoList() {
		return usedImageNoList;
	}

	public void setUsedImageNoList(List<Integer> usedImageNoList) {
		this.usedImageNoList = usedImageNoList;
	}

	public HashMap<Integer, List<ImageInfo>> getImageInfoListMap() {
		return imageInfoListMap;
	}

	public void setImageInfoListMap(HashMap<Integer, List<ImageInfo>> imageInfoListMap) {
		this.imageInfoListMap = imageInfoListMap;
	}

}

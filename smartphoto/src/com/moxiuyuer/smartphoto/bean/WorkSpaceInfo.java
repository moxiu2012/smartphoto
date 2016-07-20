package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;

public class WorkSpaceInfo implements Comparable<WorkSpaceInfo>, Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6025935272229492641L;

	//工作室文件名称
	private String name;
	//描述
	private String desc;
	//封面
	private byte[] coverImage;
	//已编辑的页数
	private int pageNum;
	//最后修改时间
	private String modifyTime;
	//工作室路径，当文件被扫描时，存入， 为保证文件和路径排序一致
	private String filePath;

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

	public byte[] getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(byte[] coverImage) {
		this.coverImage = coverImage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String date) {
		this.modifyTime = date;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	
	
	
	
	/**
	 * 覆盖比较方法，大的排在前
	 */
	@Override
	public int compareTo(WorkSpaceInfo info) {
		return modifyTime.compareTo(info.getModifyTime());
	}
	
}

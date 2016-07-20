package com.moxiuyuer.smartphoto.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ����ģʽ���汻ѡ�е�ͼƬ��·��
 * ������
 * @author moxiuyuer
 * 2016-5-24 ����6:15:20
 */
public class SelectedImagePath {

	//��ű�ѡ�е�ͼƬ��·��?
	private  List<String>  imagePathList;
	
	//选中的主题序号
	private int selectedThemeNo = 0;
	
	private static SelectedImagePath instance = new SelectedImagePath();
	
	private SelectedImagePath() {
		 imagePathList = new ArrayList<String>();//ֻ��ʼ��һ��
	}
	
	public static SelectedImagePath getInstance() {
		return instance;
	}
	

	
	public List<String> getImagePathList() {
		return imagePathList;
	}
	
	public void setImagePathList(List<String> imagePathList) {
		this.imagePathList = imagePathList;
	}

	
	//获取选中的主题序号
	public int getSelectedThemeNo() {
		return selectedThemeNo;
	}

	//存入选中的主题序号
	public void setSelectedThemeNo(int selectedThemeNo) {
		this.selectedThemeNo = selectedThemeNo;
	}

	/**
	 * ���ѡ��ͼƬ��·��?
	 * @param path
	 */
	public void addImagePath(String path) {
		//���˵��Ѿ����ڵ�·��
		if(imagePathList != null && path != null&&!imagePathList.contains(path)) {
			imagePathList.add(path);
		}
	}
	
	/**
	 * ���ѡ��ͼƬ��·������?
	 * @param pathList
	 */
	public void addImagePathList(List<String> pathList) {
		if(imagePathList != null && pathList != null) {
			for (String path : pathList) {
				addImagePath(path);
			}
		}
	}
	
	
}

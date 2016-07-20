package com.moxiuyuer.smartphoto.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ScanFileUtils {

	/**
	 * 在指定的文件夹中查找指定类型的文件
	 * 
	 * @param path
	 *            文件夹路径
	 * @param tag
	 *            后缀 如doc txt
	 * @return
	 */
	public static List<String> findFileWithPath(String path, final String tag) {
		File file = new File(path);
		List<String> pathList = new ArrayList<String>();

		if (file.exists()) {
			if (file.isFile()) {
				// 是文件则原路径返回
				pathList.add(path);
			} else {
				// 是文件夹，则进一步扫描
				List<String> childFile = recursiveFindFile(file, tag);
				pathList.addAll(childFile);
			}
		}
		return pathList;
	}

	/**
	 * 递归查找文件
	 */
	private static List<String> recursiveFindFile(File file, String tag) {
		List<String> pathList = new ArrayList<String>();
		// 查找文件夹下的文件和子文件夹
		File[] files = file.listFiles();
		if (files == null) {
			return pathList;
		}
		for (File childFile : files) {
			if (childFile.isFile()) {// 是文件
				// 截取后缀
				String name = childFile.getName();
				String extension = name.substring(name.lastIndexOf(".") + 1);
				if (tag.equals(extension)) {
					pathList.add(childFile.getAbsolutePath());
				}
				
//				if (childFile.getAbsolutePath().endsWith("." + tag)) {
//					pathList.add(childFile.getAbsolutePath());
//				}
			} else {// 是文件夹
				List<String> childList = recursiveFindFile(childFile, tag);
				if (childList != null && childList.size() > 0) {
					pathList.addAll(childList);
				}
			}
		}
		return pathList;
	}
}

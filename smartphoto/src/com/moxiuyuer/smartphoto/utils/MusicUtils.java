package com.moxiuyuer.smartphoto.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 操作音乐文件的工具类 描述：
 * 
 * @author yuerting 2016-7-17 - 下午8:56:05
 */
public class MusicUtils {

	/**
	 * 将音乐文件裁剪后保存为byte[]
	 * 
	 * @param srcPath
	 *            音乐文件路径
	 * @param startCutTime
	 *            开始裁剪事件
	 * @param stopCutTime
	 *            结束裁剪事件
	 * @param duration
	 *            文件总时长
	 * @return
	 */
	public static byte[] musicFile2Byte(String srcPath, int startCutTime, int stopCutTime, int duration) {
		byte[] resultByte = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			File file = new File(srcPath);
			long length = file.length();
			int startLength = (int) ((float) startCutTime / (float) duration * length);
			int stopLength = (int) ((float) stopCutTime / (float) duration * length);

			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[10*1024];
			int len;
			int count = 0;
			while ((len = bis.read(buffer)) != -1 && count <= stopLength) {
				count += len;
				if (count >= startLength) {
					baos.write(buffer, 0, len);
				}
			}
			// 转为数组
			resultByte = baos.toByteArray();

			baos.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				bis.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultByte;
	}

	/**
	 * 将数组转成文件保存
	 * @param musicByte 音乐文件数组
	 * @param desPath 生成的文件路径
	 */
	public static void byte2MusicFile(byte[] musicByte, File desFile) {
		try {
			FileOutputStream fos = new FileOutputStream(desFile);
			fos.write(musicByte, 0, musicByte.length);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

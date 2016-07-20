package com.moxiuyuer.smartphoto.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class SerializableUtils {

	
	/**
	 * 将一个对象序列化到本地文件
	 * 
	 * @param objectList
	 * @param file
	 */
	public static void object2File(Object obj, File file) {
		ObjectOutputStream oos = null;
		try {
			// 序列化对象到本地
			oos = new ObjectOutputStream(new FileOutputStream(file, false));
			oos.writeObject(obj);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将多个对象序列化到本地文件 已追加方式写入
	 * 
	 * @param objectList
	 * @param file
	 */
	public static void objects2File(List<Object> objectList, File file) {
		ObjectOutputStream oos = null;
		try {
			// 序列化对象到本地
			oos = new ObjectOutputStream(new FileOutputStream(file, true));
			for (Object object : objectList) {
				oos.writeObject(object);
			}
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将文件反序列化成对象
	 * 
	 * @param file
	 * @return objectList 对象集合
	 */
	public static Object file2Object(File file) {

		ObjectInputStream ois = null;
		Object object = null;

		try {
			// 反序列化对象
			ois = new ObjectInputStream(new FileInputStream(file));
			object = ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (ois != null)
					ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return object;
	}


	/**
	 * 将包含多个对象的文件反序列化成对象集合
	 * 
	 * @param file
	 * @return objectList 对象集合
	 */
	public static List<Object> file2Objects(File file) {

		ObjectInputStream ois = null;
		Object object = null;
		List<Object> objectList = new ArrayList<Object>();

		try {
			// 反序列化对象
			ois = new ObjectInputStream(new FileInputStream(file));
			while (true) {
				object = ois.readObject();
				objectList.add(object);
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("====文件终止=====");
//			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				if (ois != null)
					ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return objectList;
	}

	/**
	 * 将包含多个对象的文件反序列化第一个对象
	 * 
	 * @param file
	 * @return object 第一个对象
	 */
	public static Object file2FirstObject(File file) {
		
		if(file == null) {
			return null;
		}
		
		ObjectInputStream ois = null;
		Object object = null;
		try {
			// 反序列化对象
			ois = new ObjectInputStream(new FileInputStream(file));

			while (true) {
				object = ois.readObject();
				return object;
			}

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

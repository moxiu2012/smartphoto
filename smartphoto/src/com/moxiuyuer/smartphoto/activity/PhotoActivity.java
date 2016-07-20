package com.moxiuyuer.smartphoto.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.GroupImageAdapter;
import com.moxiuyuer.smartphoto.bean.ImageBean;
import com.moxiuyuer.smartphoto.utils.SelectedImagePath;

/**
 * 以列表方式显示图片文件夹的activity
 * 描述：
 *
 * @author yuerting
 *  2016-7-14  - 上午10:38:02
 */
public class PhotoActivity extends Activity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();
	
	private Map<Integer,List<String>> tempMap = new HashMap<Integer,List<String>>();

	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private GroupImageAdapter adapter;
	private GridView mGroupGridView;

	private int flag;// 标识grid位置

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				// 关闭进度条
				mProgressDialog.dismiss();

				adapter = new GroupImageAdapter(PhotoActivity.this,
						list = subGroupOfImage(mGruopMap), mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_photo);

		mGroupGridView = (GridView) findViewById(R.id.main_grid);

		getImages();

		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<String> childList = mGruopMap.get(list.get(position).getFolderName());

				Intent mIntent = new Intent(PhotoActivity.this, ShowImageActivity.class);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
				startActivityForResult(mIntent, 0);

				flag = position;// 标记位置
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			List<String> list = data.getStringArrayListExtra("selectedPathList");

			if(tempMap.containsKey(flag)) {
				
				List<String> tempList = tempMap.get(flag);
				// 清除上次选择的图片
				SelectedImagePath.getInstance().getImagePathList().removeAll(tempList);
				
				tempMap.remove(flag);
			} 
			
			//加入临时存储的map
			tempMap.put(flag, list);
			// 添加这次选择的图片
			SelectedImagePath.getInstance().addImagePathList(list);
//			//不能clear,否则tempMap.get(flag)中没有值
//			list.clear();
			
			Toast.makeText(this, "选中  " + SelectedImagePath.getInstance().getImagePathList().size() + " item", 0).show();
		}
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PhotoActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?", new String[] {
								"image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();

					// 根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> childList = new ArrayList<String>();
						childList.add(path);
						mGruopMap.put(parentName, childList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}

				mCursor.close();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}

	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
		if (mGruopMap.size() == 0) {
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();

		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();

			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片

			list.add(mImageBean);
		}

		return list;

	}
	
	
	@Override
	public void onBackPressed() {
//		//获取传输过来的数据，判断点击的来源，
//		//如果来自HomeFragment， 则返回时，返回到WorkSpaceFragment
//		fromWhere = getIntent().getStringExtra("fromWhere");
//		if("HomeFragment".equals(fromWhere)) {
//			Intent intent = new Intent(context, cls)
//			
//			
//		}
		
		super.onBackPressed();
	}

}

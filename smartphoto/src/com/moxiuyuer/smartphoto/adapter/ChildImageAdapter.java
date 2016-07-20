package com.moxiuyuer.smartphoto.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.NativeImageLoader;
import com.moxiuyuer.smartphoto.utils.NativeImageLoader.NativeImageCallBack;
import com.moxiuyuer.smartphoto.view.MyImageView;
import com.moxiuyuer.smartphoto.view.MyScaleImageView;

/**
 * 图片缩略图展示的adapter 
 * 描述：checkbox实现多选
 * @author moxiuyuer 2016-6-4 下午6:02:42
 */
public class ChildImageAdapter extends BaseAdapter {
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, Boolean> mSelectMap;
	private GridView mGridView;
	private List<String> list;
	private List<String> selectedList = null;
	protected LayoutInflater mInflater;
	private TextView tvSelectedNum;

	/**
	 * 
	 * @param context
	 * @param list
	 * @param mGridView
	 * @param mSelectMap
	 *            图片选中状态
	 * @param tvSelectedNum
	 */
	public ChildImageAdapter(Context context, List<String> list, GridView mGridView,
			HashMap<Integer, Boolean> mSelectMap, TextView tvSelectedNum) {
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
		// // 获取之前选择的图片的list
		// selectedList = SelectedImagePath.getInstance().getImagePathList();

		this.mSelectMap = mSelectMap;
		this.tvSelectedNum = tvSelectedNum;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_grid_child, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyScaleImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.mi_friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);

		/*************************************/
		/**
		 * 必须先设置监听，再设置状态，否则会出现滚动时状态改变问题
		 */

		// // 设置checkbox的状态
		// viewHolder.mCheckBox.setChecked(mSelectMap.get(position));
		// // 改变选择的数量
		// setSelectNum();

		/*************************************/

		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				mSelectMap.put(position, isChecked);
				mSelectMap.size();
				// 改变选择的数量
				setSelectNum();
			}
		});

		// 设置checkbox的状态
		viewHolder.mCheckBox.setChecked(mSelectMap.get(position));
		// 改变选择的数量
		setSelectNum();

		// 利用NativeImageLoader类加载本地图片
//		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, GlobalParams.windowPoint,
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, new Point(150, 150),
				new NativeImageCallBack() {

					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
						if (bitmap != null && mImageView != null) {
							bitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, 100, 100);
							mImageView.setImageBitmap(bitmap);
						}
					}
				});

		if (bitmap != null) {
			bitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, 100, 100);
			viewHolder.mImageView.setImageBitmap(bitmap);
		} else {
			viewHolder.mImageView.setImageResource(R.drawable.mi_friends_sends_pictures_no);
		}

		return convertView;
	}

	/**
	 * 设置选择的数量
	 */
	protected void setSelectNum() {
		if (getSelectItems().size() != 0) {
			tvSelectedNum.setText("选中" + getSelectItems().size() + "张");
		} else {
			tvSelectedNum.setText("");
		}
	}

	/**
	 * 获取选中的Item的position
	 * 
	 * @return
	 */
	public List<Integer> getSelectItems() {
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				list.add(entry.getKey());
			}
		}
		return list;
	}
	

	public void setmSelectMap(HashMap<Integer, Boolean> mSelectMap) {
		this.mSelectMap = mSelectMap;
	}



	class ViewHolder {
		public MyScaleImageView mImageView;
		public CheckBox mCheckBox;
	}
}

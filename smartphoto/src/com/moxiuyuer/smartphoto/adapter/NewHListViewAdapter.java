package com.moxiuyuer.smartphoto.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.HorizontialListView;

/**
 * 新建相册里面的横向ListView的adaper 描述：显示布局方式和图片
 * 
 * @author moxiuyuer 2016-6-7 下午9:18:59
 */
public class NewHListViewAdapter extends MyBaseAdapter {

	// 图片的bitmap列表
	private List<Bitmap> bitmapList;
	private HorizontialListView mListView;
	// 选中的项目集合
	private List<Integer> itemList = new ArrayList<Integer>();
	
	public List<Integer> getItemList() {
		return itemList;
	}

	public void setItemList(List<Integer> itemList) {
		this.itemList = itemList;
	}


	public NewHListViewAdapter(Context context, List<Bitmap> bitmapList,
			HorizontialListView mlistView) {
		super(context);
		this.bitmapList = bitmapList;
		this.mListView = mlistView;
	}

	@Override
	public int getCount() {
		return bitmapList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int p = position;
		ViewHolder holder;
		Bitmap mBitmap = bitmapList.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_hlv_new, null);

			holder = new ViewHolder();
			holder.mImageView = (ImageView) convertView
					.findViewById(R.id.iv_new_listview_h);
			holder.mRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_new);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.mImageView.setImageResource(R.drawable.mi_img_no);
		}
		holder.mRelativeLayout.setTag(position);

		//设置选中item的背景颜色
		if(itemList.contains(position)) {
			holder.mRelativeLayout.setBackgroundColor(Color.RED);
		} else {
			holder.mRelativeLayout.setBackgroundColor(Color.GREEN);
		}
		
		if (mBitmap != null) {
			Bitmap bitmap = BitmapUtils.centerSquareScaleBitmap(mBitmap, 100, 100);
//			mBitmap.recycle();
//			mBitmap = null;
			holder.mImageView.setImageBitmap(bitmap);
		}
		
		return convertView;
	}

	/**
	 * view的容器
	 * 
	 * @author yuerting
	 * 
	 */
	class ViewHolder {
		public ImageView mImageView;
		public RelativeLayout mRelativeLayout;
	}

}
